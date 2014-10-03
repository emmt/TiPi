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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import mitiv.array.ArrayFactory;
import mitiv.array.ArrayUtils;
import mitiv.array.DoubleArray;
import mitiv.array.ScalingOptions;
import mitiv.array.ShapedArray;
import mitiv.cost.CompositeDifferentiableCostFunction;
import mitiv.cost.HyperbolicTotalVariation;
import mitiv.cost.QuadraticCost;
import mitiv.deconv.ConvolutionOperator;
import mitiv.invpb.ReconstructionJob;
import mitiv.invpb.ReconstructionViewer;
import mitiv.invpb.SimpleViewer;
import mitiv.io.DataFormat;
import mitiv.io.MdaFormat;
import mitiv.linalg.ArrayOps;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.RealComplexFFT;
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

    @Argument
    private List<String> arguments;

    private DoubleArray data = null;
    private DoubleArray psf = null;
    private DoubleArray result = null;
    private double fcost = 0.0;
    private DoubleShapedVector gcost = null;

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

    public ReconstructionViewer getViewer() {
        return viewer;
    }
    public void setViewer(ReconstructionViewer rv) {
        viewer = rv;
    }

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
    public void setLowerBound(double value) {
        lowerBound = value;
    }
    public void setUpperBound(double value) {
        upperBound = value;
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
        job.data = readImage(inputName, ArrayUtils.GRAY, "input image");;
        job.psf = readImage(psfName, ArrayUtils.GRAY, "PSF");

        job.deconvolve();
        try {
            writeImage(job.result, job.outName);
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

    public void deconvolve() {

        // Check input data and get dimensions.
        if (data == null) {
            fatal("Input data not specified.");
        }
        int[] shape = data.cloneShape();
        int rank = data.getRank();

        // Check the PSF.
        if (psf == null) {
            fatal("PSF not specified.");
        }
        if (psf.getRank() != rank) {
            fatal("PSF must have same rank as data.");
        }
        for (int k = 0; k < rank; ++k) {
            if (psf.getDimension(k) != shape[k]) {
                fatal("The dimensions of the PSF must match those of the input image.");
            }
        }

        // Initialize a vector space and populate it with workspace vectors.
        DoubleShapedVectorSpace space = new DoubleShapedVectorSpace(shape);
        RealComplexFFT FFT = new RealComplexFFT(space);
        LinearOperator W = null; // new LinearOperator(space);
        double[] tmp = psf.flatten();
        DoubleShapedVector x = space.create();
        DoubleShapedVector y = space.wrap(data.flatten());
        DoubleShapedVector h = space.wrap(tmp);
        double psf_sum = 0.0;
        for (int j = 0; j < tmp.length; ++j) {
            psf_sum += tmp[j];
        }
        if (psf_sum != 1.0) {
            if (psf_sum != 0.0) {
                space.axpby(0.0, x, 1.0/psf_sum, y, x);
            } else {
                x.fill(0.0);
            }
        }
        ConvolutionOperator H = new ConvolutionOperator(FFT, h);
        result = ArrayFactory.wrap(x.getData(), x.cloneShape(), false);
        if (debug) {
            System.out.println("Vector space initialization complete.");
        }

        // Build the cost functions
        QuadraticCost fdata = new QuadraticCost(H, y, W);
        HyperbolicTotalVariation fprior = new HyperbolicTotalVariation(space, epsilon);
        CompositeDifferentiableCostFunction cost = new CompositeDifferentiableCostFunction(1.0, fdata, mu, fprior);
        fcost = 0.0;
        gcost = space.create();
        if (debug) {
            System.out.println("Cost function initialization complete.");
        }

        // Initialize the non linear conjugate gradient
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
                lbfgs = new LBFGS(space, limitedMemorySize, lineSearch);
                lbfgs.setAbsoluteTolerance(gatol);
                lbfgs.setRelativeTolerance(grtol);
                minimizer = lbfgs;
            } else {
                int method = NonLinearConjugateGradient.DEFAULT_METHOD;
                nlcg = new NonLinearConjugateGradient(space, method, lineSearch);
                nlcg.setAbsoluteTolerance(gatol);
                nlcg.setRelativeTolerance(grtol);
                minimizer = nlcg;
            }
        } else {
            /* Some bounds have been specified. */
            lineSearch = new ArmijoLineSearch(0.5, 0.1);
            if (bounded == 1) {
                /* Only a lower bound has been specified. */
                projector = new SimpleLowerBound(space, lowerBound);
            } else if (bounded == 2) {
                /* Only an upper bound has been specified. */
                projector = new SimpleUpperBound(space, upperBound);
            } else {
                /* Both a lower and an upper bounds have been specified. */
                projector = new SimpleBounds(space, lowerBound, upperBound);
            }
            int m = (limitedMemorySize > 1 ? limitedMemorySize : 5);
            lbfgsb = new LBFGSB(space, projector, m, lineSearch);
            lbfgsb.setAbsoluteTolerance(gatol);
            lbfgsb.setRelativeTolerance(grtol);
            minimizer = lbfgsb;
            projector.apply(x, x);

        }
        if (debug) {
            System.out.println("Optimization method initialization complete.");
        }

        // Launch the non linear conjugate gradient
        OptimTask task = minimizer.start();
        while (true) {
            if (task == OptimTask.COMPUTE_FG) {
                fcost = cost.computeCostAndGradient(0.1, x, gcost, true);
            } else if (task == OptimTask.NEW_X || task == OptimTask.FINAL_X) {
                if (viewer != null) {
                    viewer.display(this);
                }
                //if (verbose) {
                //    double threshold;
                //    if (minimizer == lbfgs) {
                //        threshold = lbfgs.getGradientThreshold();
                //    } else if (minimizer == lbfgsb) {
                //        threshold = lbfgsb.getGradientThreshold();
                //    } else if (minimizer == nlcg) {
                //        threshold = nlcg.getGradientThreshold();
                //    } else {
                //        threshold = 0.0;
                //    }
                //    System.out.format("iter: %4d    eval: %4d    fx = %21.15g    |gx| = %8.2g (%.2g)\n",
                //            minimizer.getIterations(), minimizer.getEvaluations(),
                //            fcost, gcost.norm2(), threshold);
                //}
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
            task = minimizer.iterate(x, fcost, gcost);
        }
        if (verbose) {
            System.out.format("min(x) = %g\n", ArrayOps.getMin(x.getData()));
            System.out.format("max(x) = %g\n", ArrayOps.getMax(x.getData()));
        }
    }


    /* ************************ FONCTIONS UTILES   ************************* */

    /**
     * FONCTIONS UTILES : writeImage()
     * @param img
     * @param fileName
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void writeImage(ShapedArray img, String fileName)
            throws FileNotFoundException, IOException {
        writeImage(img, fileName, new ScalingOptions());
    }

    public static void writeImage(ShapedArray img, String fileName,
            ScalingOptions opts) throws FileNotFoundException, IOException {
        int format = DataFormat.guessFormat(fileName);
        String formatName = null;
        switch (format) {
        //case DataFormat.FMT_PNM:
        case DataFormat.FMT_JPEG:
        case DataFormat.FMT_PNG:
        case DataFormat.FMT_GIF:
        case DataFormat.FMT_BMP:
        case DataFormat.FMT_WBMP:
            //case DataFormat.FMT_TIFF:
            //case DataFormat.FMT_FITS:
            formatName = DataFormat.getFormatName(format);
            break;
        case DataFormat.FMT_MDA:
            MdaFormat.save(img, fileName);
            return;
        default:
            formatName = null;
        }
        if (formatName == null) {
            fatal("Unknown/unsupported format name.");
        }
        int depth, width, height, rank = img.getRank();
        if (rank == 2) {
            depth = 1;
            width = img.getDimension(0);
            height = img.getDimension(1);
        } else if (rank == 3) {
            depth = img.getDimension(0);
            width = img.getDimension(1);
            height = img.getDimension(2);
        } else {
            depth = 0;
            width = 0;
            height = 0;
            fatal("Expecting 2D array as image.");
        }
        double[] data = img.toDouble().flatten();
        BufferedImage buf = ArrayUtils.doubleAsBuffered(data, depth, width, height, opts);
        ImageIO.write(buf, formatName, new File(fileName));
    }

    private static DoubleArray readImage(String fileName, int colorModel, String description) {
        DoubleArray img = null;
        int format = DataFormat.guessFormat(fileName);
        try {
            if (format == DataFormat.FMT_MDA) {
                img = MdaFormat.load(fileName).toDouble();
            } else {
                img = ArrayUtils.imageAsDouble(ImageIO.read(new File(fileName)), colorModel);
            }
        } catch (Exception e) {
            fatal("Error while reading " + description + "(" + e.getMessage() +").");
        }
        return img;
    }

    /* Below are all methods required for a RecosntructionJob. */

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