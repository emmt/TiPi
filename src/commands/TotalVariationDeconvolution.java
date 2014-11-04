/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014 the MiTiV project, http://mitiv.univ-lyon1.fr/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package commands;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

import mitiv.array.ArrayFactory;
import mitiv.array.DoubleArray;
import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.cost.CompositeDifferentiableCostFunction;
import mitiv.cost.HyperbolicTotalVariation;
import mitiv.cost.QuadraticCost;
import mitiv.deconv.ConvolutionOperator;
import mitiv.deconv.WeightedConvolutionOperator;
import mitiv.exception.IncorrectSpaceException;
import mitiv.invpb.ReconstructionJob;
import mitiv.invpb.ReconstructionSynchronizer;
import mitiv.invpb.ReconstructionViewer;
import mitiv.invpb.SimpleViewer;
import mitiv.io.ColorModel;
import mitiv.io.DataFormat;
import mitiv.linalg.ArrayOps;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.RealComplexFFT;
import mitiv.linalg.shaped.ShapedLinearOperator;
import mitiv.optim.ArmijoLineSearch;
import mitiv.optim.BoundProjector;
import mitiv.optim.LBFGS;
import mitiv.optim.LBFGSB;
import mitiv.optim.LineSearch;
import mitiv.optim.MoreThuenteLineSearch;
import mitiv.optim.NonLinearConjugateGradient;
import mitiv.optim.OptimTask;
import mitiv.optim.ReverseCommunicationOptimizer;
import mitiv.optim.SimpleBounds;
import mitiv.optim.SimpleLowerBound;
import mitiv.optim.SimpleUpperBound;
import mitiv.utils.FFTUtils;
import mitiv.utils.Timer;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class TotalVariationDeconvolution implements ReconstructionJob {

    @Option(name = "--output", aliases = {"-o"}, usage = "Name of output image.", metaVar = "OUTPUT")
    private String outName = "output.mda";

    @Option(name = "--eta", aliases = {"-e"}, usage = "Mean data error.", metaVar = "ETA")
    private double eta = 1.0;

    @Option(name = "--mu", aliases = {"-m"}, usage = "Regularization level.", metaVar = "MU")
    private double mu = 10.0;

    @Option(name = "--epsilon", aliases = {"-t"}, usage = "Threshold level.", metaVar = "EPSILON")
    private double epsilon = 1.0;

    @Option(name = "--gatol", usage = "Absolute gradient tolerance for the convergence.", metaVar = "GATOL")
    private double gatol = 0.0;

    @Option(name = "--grtol", usage = "Relative gradient tolerance for the convergence.", metaVar = "GRTOL")
    private double grtol = 1e-3;

    @Option(name = "--lbfgs", usage = "Use LBFGS method with M saved steps.", metaVar = "M")
    private int limitedMemorySize = 0;

    @Option(name = "--xmin", usage = "Lower bound for the variables.", metaVar = "VALUE")
    private double lowerBound = Double.NEGATIVE_INFINITY;

    @Option(name = "--xmax", usage = "Upper bound for the variables.", metaVar = "VALUE")
    private double upperBound = Double.POSITIVE_INFINITY;

    @Option(name = "--help", aliases = {"-h", "-?"}, usage = "Display help.")
    private boolean help;

    @Option(name = "--verbose", aliases = {"-v"}, usage = "Verbose mode.")
    private boolean verbose = false;

    @Option(name = "--debug", aliases = {"-d"}, usage = "Debug mode.")
    private boolean debug = false;

    @Option(name = "--maxiter", aliases = {"-l"}, usage = "Maximum number of iterations, -1 for no limits.")
    private int maxiter = 200;

    @Option(name = "--pad", usage = "Padding method (auto|none).", metaVar = "VALUE")
    private String paddingMethod = "auto";

    @Option(name = "--old", usage = "Use old convolution operator.")
    private boolean old;

    @Argument
    private List<String> arguments;

    private DoubleArray data = null;
    private DoubleArray psf = null;
    private DoubleArray result = null;
    private double fcost = 0.0;
    private DoubleShapedVector gcost = null;
    private Timer timer = new Timer();

    public DoubleArray getData() {
        return data;
    }
    public void setData(DoubleArray data) {
        this.data = data;
    }
    public DoubleArray getPsf() {
        return psf;
    }
    public void setPsf(DoubleArray psf) {
        this.psf = psf;
    }
    @Override
    public DoubleArray getResult() {
        /* Nothing else to do because the actual result is in a vector
         * which shares the contents of the ShapedArray.  Otherwise,
         * some kind of synchronization is needed. */
        return result;
    }

    public void setResult(DoubleArray result) {
        this.result = result;
    }

    private ReverseCommunicationOptimizer minimizer = null;
    private ReconstructionViewer viewer = null;
    private ReconstructionSynchronizer synchronizer = null;
    private double[] synchronizedParameters = {0.0, 0.0};
    private boolean[] change = {false, false};
    private String[] synchronizedParameterNames = {"Regularization Level", "Relaxation Threshold"};

    private double[] weights = null;

    public ReconstructionViewer getViewer() {
        return viewer;
    }
    public void setViewer(ReconstructionViewer rv) {
        viewer = rv;
    }
    public ReconstructionSynchronizer getSynchronizer() {
        return synchronizer;
    }
    public void createSynchronizer() {
        if (synchronizer == null) {
            synchronizedParameters[0] = mu;
            synchronizedParameters[1] = epsilon;
            synchronizer = new ReconstructionSynchronizer(synchronizedParameters);
        }
    }
    public void deleteSynchronizer() {
        synchronizer = null;
    }
    // FIXME: names should be part of the synchronizer...
    public String getSynchronizedParameterName(int i) {
        return synchronizedParameterNames[i];
    }

    private boolean run = true;

    public TotalVariationDeconvolution() {
    }

    public void setVerboseMode(boolean value) {
        verbose = value;
    }
    public void setDebugMode(boolean value) {
        debug = value;
    }
    public void setOutputName(String name) {
        outName = name;
    }
    public void setMaximumIterations(int value) {
        maxiter = value;
    }
    public void setLimitedMemorySize(int value) {
        limitedMemorySize = value;
    }
    public void setTargetError(double value) {
        eta = value;
    }
    public void setRegularizationWeight(double value) {
        mu = value;
    }
    public void setRegularizationThreshold(double value) {
        epsilon = value;
    }
    public void setAbsoluteTolerance(double value) {
        gatol = value;
    }
    public void setRelativeTolerance(double value) {
        grtol = value;
    }
    @Override
    public double getRelativeTolerance() {
        return grtol;
    }
    public void setLowerBound(double value) {
        lowerBound = value;
    }
    public void setUpperBound(double value) {
        upperBound = value;
    }
    public void stop() {
        run = false;
    }
    public void setWeight(double[] W){
        this.weights = W;
    }

    public static DoubleArray loadData(String name) {
        ShapedArray arr = DataFormat.load(name);
        ColorModel colorModel = ColorModel.guessColorModel(arr);
        if (colorModel == ColorModel.NONE) {
            // FIXME: what about bytes?
            return arr.toDouble();
        } else {
            return ColorModel.filterImageAsDouble(arr, ColorModel.GRAY);
        }
    }

    //private static double parseDouble(String option, String arg) {
    //    try {
    //        return Double.parseDouble(arg);
    //    } catch (Exception e) {
    //        System.err.format("Invalid real value for option %s (%s).\n", option, arg);
    //        System.exit(1);
    //        return 0.0; // dummy result to avoid warnings
    //    }
    //}

    public static void main(String[] args) {
        // Switch to "US" locale to avoid problems with number formats.
        Locale.setDefault(Locale.US);

        // Parse options.
        TotalVariationDeconvolution job = new TotalVariationDeconvolution();
        CmdLineParser parser = new CmdLineParser(job);
        try {
            parser.parseArgument(args);
            if (job.mu < 0.0) {
                System.err.format("Regularization level MU must be strictly positive.\n");
                System.exit(1);
            }
            if (job.epsilon <= 0.0) {
                System.err.format("Threshold level EPSILON must be strictly positive.\n");
                System.exit(1);
            }
            if (job.help) {
                PrintStream stream = System.out;
                stream.println("Usage: tvdec [OPTIONS] INPUT_IMAGE PSF");
                stream.println("Options:");
                parser.setUsageWidth(80);
                parser.printUsage(stream);
                System.exit(0);
            }

        } catch (CmdLineException e) {
            System.err.format("Error: %s\n", e.getMessage());
            parser.setUsageWidth(80);
            parser.printUsage(System.err);
        }

        // Deal with remaining arguments.
        int size = (job.arguments == null ? 0 : job.arguments.size());
        if (size != 2) {
            System.err.format("Too %s arguments.\n", (size < 2 ? "few" : "many"));
            System.exit(1);
        }
        String inputName = job.arguments.get(0);
        String psfName = job.arguments.get(1);

        if (job.verbose) {
            job.setViewer(new SimpleViewer());
        }

        if (job.debug){
            System.out.format("mu: %.2g, threshold: %.2g, output: %s\n",
                    job.mu, job.epsilon, job.outName);
        }

        // Read the blurred image and the PSF.
        job.data = loadData(inputName);
        job.psf = loadData(psfName);

        job.deconvolve(job.paddingMethod);
        try {
            DataFormat.save(job.result, job.outName);
        } catch (IOException e) {
            if (job.debug) {
                e.printStackTrace();
            }
            System.err.format("Failed to write output image.\n");
            System.exit(1);
        }
        if (job.verbose){
            System.out.println("Done!");
        }
        System.exit(0);
    }

    private static void fatal(String reason) {
        throw new IllegalArgumentException(reason);
    }

    public void deconvolve(String padding) {
        Shape dataShape = data.getShape();
        Shape psfShape = psf.getShape();
        if (old) {
            deconvolve(dataShape);
        } else {
            int rank =  data.getRank();
            int[] dims = new int[rank];
            if (padding.equals("auto")) {
                for (int k = 0; k < rank; ++k) {
                    int dataDim = dataShape.dimension(k);
                    int psfDim = psfShape.dimension(k);
                    int resultDim = FFTUtils.bestDimension(dataDim + psfDim - 1);
                    dims[k] = resultDim;
                }
            } else if (padding.equals("none")) {
                for (int k = 0; k < rank; ++k) {
                    int dataDim = dataShape.dimension(k);
                    int psfDim = psfShape.dimension(k);
                    int resultDim = FFTUtils.bestDimension(Math.max(dataDim, psfDim));
                    dims[k] = resultDim;
                }
            } else {
                fatal("Unknown padding strategy.");
            }
            deconvolve(Shape.make(dims));
        }
    }

    public void deconvolve(Shape resultShape) {

        timer.start();

        // Check input data and get dimensions.
        if (data == null) {
            fatal("Input data not specified.");
        }
        Shape dataShape = data.getShape();
        int rank = data.getRank();

        // Check the PSF.
        if (psf == null) {
            fatal("PSF not specified.");
        }
        if (psf.getRank() != rank) {
            fatal("PSF must have same rank as data.");
        }
        Shape psfShape = psf.getShape();
        if (old) {
            for (int k = 0; k < rank; ++k) {
                if (psf.getDimension(k) != dataShape.dimension(k)) {
                    fatal("The dimensions of the PSF must match those of the data.");
                }
            }
        }
        if (result != null) {
            /* We try to keep the previous result, at least its dimensions
             * must match. */
            for (int k = 0; k < rank; ++k) {
                if (result.getDimension(k) != data.getDimension(k)) {
                    result = null;
                    break;
                }
            }
        }

        // Check the shape of the result.
        for (int k = 0; k < rank; ++k) {
            if (old) {
                if (resultShape.dimension(k) != dataShape.dimension(k)) {
                    fatal("The dimensions of the result must be equal to those of the data.");
                }
            } else {
                if (resultShape.dimension(k) < dataShape.dimension(k)) {
                    fatal("The dimensions of the result must be at least those of the data.");
                }
                if (resultShape.dimension(k) < psfShape.dimension(k)) {
                    fatal("The dimensions of the result must be at least those of the PSF.");
                }
            }
        }

        // Initialize an input and output vector spaces and populate them with
        // workspace vectors.

        DoubleShapedVectorSpace dataSpace = new DoubleShapedVectorSpace(dataShape);
        DoubleShapedVectorSpace resultSpace = (old ? dataSpace : new DoubleShapedVectorSpace(resultShape));
        LinearOperator W = null;
        DoubleShapedVector y = dataSpace.create(data);
        DoubleShapedVector x = null;
        if (result != null) {
            x = resultSpace.create(result);
        } else if (old) {
            double psf_sum = psf.sum();
            x = resultSpace.create();
            if (psf_sum != 1.0) {
                if (psf_sum != 0.0) {
                    x.axpby(0.0, x, 1.0/psf_sum, y);
                } else {
                    x.fill(0.0);
                }
            }
        } else {
            x = resultSpace.create(0.0);
        }
        result = ArrayFactory.wrap(x.getData(), resultShape);

        // Build convolution operator.
        ShapedLinearOperator H = null;
        if (old) {
            RealComplexFFT FFT = new RealComplexFFT(resultSpace);
            if (weights != null) {
                // FIXME: for now the weights are stored as a simple Java vector.
                if (weights.length != data.getNumber()) {
                    throw new IllegalArgumentException("Error weights and input data size don't match");
                }
                W = new LinearOperator(resultSpace) {
                    @Override
                    protected void privApply(Vector src, Vector dst, int job)
                            throws IncorrectSpaceException {
                        double[] inp = ((DoubleShapedVector)src).getData();
                        double[] out = ((DoubleShapedVector)dst).getData();
                        int number = src.getNumber();
                        for (int i = 0; i < number; ++i) {
                            out[i] = inp[i]*weights[i];
                        }
                    }
                };
            }
            DoubleShapedVector h = resultSpace.create(psf);
            H = new ConvolutionOperator(FFT, h);
        } else {
            // FIXME: add a method for that
            WeightedConvolutionOperator A = WeightedConvolutionOperator.build(resultSpace, dataSpace);
            A.setPSF(psf);
            H = A;
        }
        if (debug) {
            System.out.println("Vector space initialization complete.");
        }

        // Build the cost functions
        QuadraticCost fdata = new QuadraticCost(H, y, W);
        HyperbolicTotalVariation fprior = new HyperbolicTotalVariation(resultSpace, epsilon);
        CompositeDifferentiableCostFunction cost = new CompositeDifferentiableCostFunction(1.0, fdata, mu, fprior);
        fcost = 0.0;
        gcost = resultSpace.create();
        timer.stop();
        if (debug) {
            System.out.format("Cost function initialization completed in %.3f sec.\n",
                    timer.getElapsedTime());
        }
        timer.reset();

        // Initialize the non linear conjugate gradient
        timer.start();
        LineSearch lineSearch = null;
        LBFGS lbfgs = null;
        LBFGSB lbfgsb = null;
        NonLinearConjugateGradient nlcg = null;
        BoundProjector projector = null;
        int bounded = 0;
        if (lowerBound != Double.NEGATIVE_INFINITY) {
            bounded |= 1;
        }
        if (upperBound != Double.POSITIVE_INFINITY) {
            bounded |= 2;
        }
        if (bounded == 0) {
            /* No bounds have been specified. */
            lineSearch = new MoreThuenteLineSearch(0.05, 0.1, 1E-17);
            if (limitedMemorySize > 0) {
                lbfgs = new LBFGS(resultSpace, limitedMemorySize, lineSearch);
                lbfgs.setAbsoluteTolerance(gatol);
                lbfgs.setRelativeTolerance(grtol);
                minimizer = lbfgs;
            } else {
                int method = NonLinearConjugateGradient.DEFAULT_METHOD;
                nlcg = new NonLinearConjugateGradient(resultSpace, method, lineSearch);
                nlcg.setAbsoluteTolerance(gatol);
                nlcg.setRelativeTolerance(grtol);
                minimizer = nlcg;
            }
        } else {
            /* Some bounds have been specified. */
            lineSearch = new ArmijoLineSearch(0.5, 0.1);
            if (bounded == 1) {
                /* Only a lower bound has been specified. */
                projector = new SimpleLowerBound(resultSpace, lowerBound);
            } else if (bounded == 2) {
                /* Only an upper bound has been specified. */
                projector = new SimpleUpperBound(resultSpace, upperBound);
            } else {
                /* Both a lower and an upper bounds have been specified. */
                projector = new SimpleBounds(resultSpace, lowerBound, upperBound);
            }
            int m = (limitedMemorySize > 1 ? limitedMemorySize : 5);
            lbfgsb = new LBFGSB(resultSpace, projector, m, lineSearch);
            lbfgsb.setAbsoluteTolerance(gatol);
            lbfgsb.setRelativeTolerance(grtol);
            minimizer = lbfgsb;
            projector.apply(x, x);

        }
        timer.stop();
        if (debug) {
            System.out.format("Optimization method initialization complete in %.3f sec.\n",
                    timer.getElapsedTime());
        }
        timer.reset();


        // Launch the non linear conjugate gradient
        OptimTask task = minimizer.start();
        while (run) {
            if (task == OptimTask.COMPUTE_FG) {
                timer.resume();
                fcost = cost.computeCostAndGradient(1.0, x, gcost, true);
                timer.stop();
            } else if (task == OptimTask.NEW_X || task == OptimTask.FINAL_X) {
                if (viewer != null) {
                    // FIXME: must get values back from the result vector
                    viewer.display(this);
                }
                boolean stop = (task == OptimTask.FINAL_X);
                if (! stop && maxiter >= 0 && minimizer.getIterations() >= maxiter) {
                    System.err.format("Warning: too many iterations (%d).\n", maxiter);
                    stop = true;
                }
                if (stop) {
                    break;
                }
            } else {
                System.err.println("error/warning: " + task);
                break;
            }
            if (synchronizer != null) {
                /* FIXME: check the values, suspend/resume, restart the algorithm, etc. */
                if (synchronizer.getTask() == ReconstructionSynchronizer.STOP) {
                    break;
                }
                synchronizedParameters[0] = mu;
                synchronizedParameters[1] = epsilon;
                if (synchronizer.updateParameters(synchronizedParameters, change)) {
                    if (change[0]) {
                        mu = synchronizedParameters[0];
                    }
                    if (change[1]) {
                        epsilon = synchronizedParameters[1];
                    }
                    // FIXME: restart!!!
                }
            }
            task = minimizer.iterate(x, fcost, gcost);
        }
        if (verbose) {
            timer.stop();
            double elapsed = timer.getElapsedTime();
            int nevals = getEvaluations();
            System.out.format("Total time in cost function: %.3f s (%.3f ms/eval.)\n",
                    elapsed, (nevals > 0 ? 1e3*elapsed/nevals : 0.0));
            if (H instanceof WeightedConvolutionOperator) {
                WeightedConvolutionOperator A = (WeightedConvolutionOperator)H;
                elapsed = A.getElapsedTimeInFFT();
                System.out.format("Total time in FFT: %.3f s (%.3f ms/eval.)\n",
                        elapsed, (nevals > 0 ? 1e3*elapsed/nevals : 0.0));
                elapsed = A.getElapsedTime() - elapsed;
                System.out.format("Total time in other parts of the convolution operator: %.3f s (%.3f ms/eval.)\n",
                        elapsed, (nevals > 0 ? 1e3*elapsed/nevals : 0.0));
            }
            System.out.format("min(x) = %g\n", ArrayOps.getMin(x.getData()));
            System.out.format("max(x) = %g\n", ArrayOps.getMax(x.getData()));
        }
    }


    /* Below are all methods required for a ReconstructionJob. */

    @Override
    public int getIterations() {
        return (minimizer == null ? 0 : minimizer.getIterations());
    }

    @Override
    public int getEvaluations() {
        return (minimizer == null ? 0 : minimizer.getEvaluations());
    }

    @Override
    public double getCost() {
        return fcost;
    }

    @Override
    public double getGradientNorm2() {
        return (gcost == null ? 0.0 : gcost.norm2());
    }

    @Override
    public double getGradientNorm1() {
        return (gcost == null ? 0.0 : gcost.norm1());
    }

    @Override
    public double getGradientNormInf() {
        return (gcost == null ? 0.0 : gcost.normInf());
    }

}


/*
 * Local Variables:
 * mode: Java
 * tab-width: 8
 * indent-tabs-mode: nil
 * c-basic-offset: 4
 * fill-column: 78
 * coding: utf-8
 * ispell-local-dictionary: "american"
 * End:
 */