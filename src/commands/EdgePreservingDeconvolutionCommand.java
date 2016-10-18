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

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.invpb.EdgePreservingDeconvolution;
import mitiv.io.ColorModel;
import mitiv.io.DataFormat;
import mitiv.optim.OptimTask;
import mitiv.utils.FFTUtils;


public class EdgePreservingDeconvolutionCommand {
    private PrintStream stream = System.out;

    @Option(name = "--output", aliases = {"-o"}, usage = "Name of output image.", metaVar = "OUTPUT")
    private String outName = "output.mda";

    @Option(name = "--init", aliases = {"-i"}, usage = "Name of initial image.", metaVar = "INIT")
    private String initName = null;

    @Option(name = "--weight", aliases = {"-w"}, usage = "Name of file with weights.", metaVar = "WEIGHT")
    private String weightName = null;

    //@Option(name = "--eta", aliases = {"-e"}, usage = "Mean data error.", metaVar = "ETA")
    //private double eta = 1.0;

    @Option(name = "--mu", aliases = {"-m"}, usage = "Regularization level.", metaVar = "MU")
    private double mu = 10.0;

    @Option(name = "--epsilon", aliases = {"-t"}, usage = "Edge threshold.", metaVar = "EPSILON")
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

    @Option(name = "--single", aliases = {"-s"}, usage = "Force single precision.")
    private boolean single = false;

    @Option(name = "--newcode", usage = "Try to use new code.")
    private boolean newCode = false;

    @Option(name = "--help", aliases = {"-h", "-?"}, usage = "Display help.")
    private boolean help;

    @Option(name = "--verbose", aliases = {"-v"}, usage = "Verbose mode.")
    private boolean verbose = false;

    @Option(name = "--debug", aliases = {"-d"}, usage = "Debug mode.")
    private boolean debug = false;

    @Option(name = "--maxiter", aliases = {"-l"}, usage = "Maximum number of iterations, -1 for no limits.")
    private int maxiter = 200;

    @Option(name = "--maxeval", aliases = {"-L"}, usage = "Maximum number of evaluations, -1 for no limits.")
    private int maxeval = -1;

    @Option(name = "--pad", usage = "Padding method (auto|none).", metaVar = "VALUE")
    private String paddingMethod = "auto";

    @Argument
    private List<String> arguments;

    public static ShapedArray loadData(String name, boolean single) {
        ShapedArray arr = DataFormat.load(name);
        ColorModel colorModel = ColorModel.guessColorModel(arr);
        if (colorModel == ColorModel.NONE) {
            return (single ? arr.toFloat() :  arr.toDouble());
        } else {
            return (single
                    ? ColorModel.filterImageAsFloat(arr, ColorModel.GRAY)
                            : ColorModel.filterImageAsDouble(arr, ColorModel.GRAY));
        }
    }

    public static void main(String[] args) {

        // Switch to "US" locale to avoid problems with number formats.
        Locale.setDefault(Locale.US);

        // Parse options.
        EdgePreservingDeconvolutionCommand job = new EdgePreservingDeconvolutionCommand();
        CmdLineParser parser = new CmdLineParser(job);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.format("Error: %s\n", e.getMessage());
            parser.getProperties().withUsageWidth(80);
            parser.printUsage(System.err);
            System.exit(1);
        }
        if (job.help) {
            PrintStream stream = System.out;
            stream.println("Usage: deconv [OPTIONS] INPUT_IMAGE PSF");
            stream.println("Options:");
            parser.getProperties().withUsageWidth(80);
            parser.printUsage(stream);
            System.exit(0);
        }

        // Deal with remaining arguments.
        int size = (job.arguments == null ? 0 : job.arguments.size());
        if (size != 2) {
            System.err.format("Too %s arguments.\n", (size < 2 ? "few" : "many"));
            System.exit(1);
        }
        String inputName = job.arguments.get(0);
        String psfName = job.arguments.get(1);

        EdgePreservingDeconvolution solver = new EdgePreservingDeconvolution();

        try {
            // Read the blurred data and the PSF.
            solver.setForceSinglePrecision(job.single);
            solver.setData(loadData(inputName, job.single));
            solver.setPSF(loadData(psfName, job.single));
            if (job.weightName != null) {
                solver.setWeight(loadData(job.weightName, job.single));
            }

            // Compute dimensions of result.
            Shape dataShape = solver.getData().getShape();
            Shape psfShape = solver.getPSF().getShape();
            int rank = dataShape.rank();
            int[] objDims = new int[rank];
            if (job.paddingMethod.equals("auto")) {
                for (int k = 0; k < rank; ++k) {
                    int dataDim = dataShape.dimension(k);
                    int psfDim = psfShape.dimension(k);
                    objDims[k] = FFTUtils.bestDimension(dataDim + psfDim - 1);
                }
            } else if (job.paddingMethod.equals("none")) {
                for (int k = 0; k < rank; ++k) {
                    int dataDim = dataShape.dimension(k);
                    int psfDim = psfShape.dimension(k);
                    objDims[k] = FFTUtils.bestDimension(Math.max(dataDim, psfDim));
                }
            } else {
                throw new IllegalArgumentException("Unknown padding strategy");
            }
            solver.setObjectShape(objDims);

            // Result and initial solution.
            if (job.initName != null) {
                solver.setInitialSolution(loadData(job.initName, job.single));
            }

            solver.setAbsoluteTolerance(job.gatol);
            solver.setRelativeTolerance(job.grtol);
            solver.setLowerBound(job.lowerBound);
            solver.setUpperBound(job.upperBound);
            solver.setLimitedMemorySize(job.limitedMemorySize);
            solver.setRegularizationLevel(job.mu);
            solver.setEdgeThreshold(job.epsilon);
            solver.setMaximumIterations(job.maxiter);
            solver.setMaximumEvaluations(job.maxeval);
            solver.setDebug(job.debug);
            solver.setSaveBest(true);
            solver.setUseNewCode(job.newCode);

            OptimTask task = solver.start();
            while (true) {
                if (task == OptimTask.ERROR) {
                    fatal(solver.getReason());
                }
                if (task == OptimTask.WARNING) {
                    warn(solver.getReason());
                    break;
                }
                if (job.verbose && (task == OptimTask.NEW_X || task == OptimTask.FINAL_X)) {
                    double elapsed = solver.getElapsedTime();
                    int evaluations = solver.getEvaluations();
                    int iterations = solver.getIterations();
                    solver.getRestarts();
                    job.stream.format("iter: %4d    eval: %4d    time: %7.3f s.    fx = %22.16e    |gx| = %8.2e\n",
                            iterations, evaluations,
                            elapsed, solver.getCost(),
                            solver.getGradient().norm2());
                    if (task == OptimTask.FINAL_X) {
                        job.stream.format("Total time in cost function: %.3f s (%.3f ms/eval.)\n",
                                elapsed, (evaluations > 0 ? 1e3*elapsed/evaluations : 0.0));
                    }
                    // if (fdata instanceof WeightedConvolutionCost) {
                    //     WeightedConvolutionCost f = fdata;
                    //     elapsed = f.getElapsedTimeInFFT();
                    //     System.out.format("Total time in FFT: %.3f s (%.3f ms/eval.)\n",
                    //         elapsed, (evaluations > 0 ? 1e3*elapsed/evaluations : 0.0));
                    //     elapsed = f.getElapsedTime() - elapsed;
                    //     System.out.format("Total time in other parts of the convolution operator: %.3f s (%.3f ms/eval.)\n",
                    //         elapsed, (evaluations > 0 ? 1e3*elapsed/evaluations : 0.0));
                    // }
                }
                if (task == OptimTask.FINAL_X) {
                    break;
                }
                task = solver.iterate();
            }
        } catch (RuntimeException e) {
            fatal(e.getMessage());
        }
        try {
            DataFormat.save(solver.getBestSolution(), job.outName);
        } catch (final IOException e) {
            if (job.debug) {
                e.printStackTrace();
            }
            fatal("Failed to write output image");
        }
        if (job.verbose){
            System.out.println("Done!");
        }
        System.exit(0);
    }

    private static void fatal(String mesg) {
        System.err.format("Error: %s.\n", mesg);
        System.exit(1);
    }

    private static void warn(String mesg) {
        System.err.format("Warning: %s.\n", mesg);
    }
}
