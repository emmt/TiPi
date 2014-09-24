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
import java.util.Random;

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
import mitiv.exception.DataFormatException;
import mitiv.exception.RecoverableFormatException;
import mitiv.io.DataFormat;
import mitiv.io.MdaFormat;
import mitiv.linalg.ArrayOps;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.RealComplexFFT;
import mitiv.optim.MoreThuenteLineSearch;
import mitiv.optim.NonLinearConjugateGradient;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class TotalVariationDeconvolution {

    @Option(name = "-o", aliases = {"--output"}, usage = "Name of output image.", metaVar = "OUTPUT")
    private final String outName = "output.mda";

    @Option(name = "-m", aliases = {"--mu"}, usage = "Regularization level.", metaVar = "MU")
    private final double mu = 10.0;

    @Option(name = "-t", aliases = {"--epsilon"}, usage = "Threshold level.", metaVar = "EPSILON")
    private final double epsilon = 1.0;

    @Option(name = "-h", aliases = {"-?", "--help"}, usage = "Display help.")
    private boolean help;

    @Option(name = "-v", aliases = {"--verbose"}, usage = "Verbose mode.")
    private final boolean verbose = false;

    @Option(name = "-d", aliases = {"--debug"}, usage = "Debug mode.")
    private final boolean debug = false;

    @Argument
    private List<String> arguments;

    private int maxIter = -1;

    DoubleArray data = null;
    DoubleArray psf = null;
    DoubleArray result = null;

    public TotalVariationDeconvolution() {
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
            if (job.mu <= 0.0) {
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
        job.maxIter = 200;

        String inputName;
        String psfName;

        // Deal with remaining arguments.
        int size = (job.arguments == null ? 0 : job.arguments.size());
        if (size != 2) {
            System.err.format("Too %s arguments.\n", (size < 2 ? "few" : "many"));
            System.exit(1);
        }
        inputName = job.arguments.get(0);
        psfName = job.arguments.get(1);

        System.out.format("mu: %.2g, threshold: %.2g, output: %s\n",
                job.mu, job.epsilon, job.outName);

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
        System.out.println("Done!");
        System.exit(0);
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
        } catch (IOException | DataFormatException | RecoverableFormatException e) {
            fatal("Error while reading " + description + "(" + e.getMessage() +").");
        }
        return img;
    }

    private static void fatal(String reason) {
        throw new IllegalArgumentException(reason);
    }

    public void deconvolve() {

        // Check input data and get dimensions.
        if (data == null) {
            fatal("Input data not specified.");
        }
        if (data.getRank() != 2) {
            fatal("Expecting 2D array as input image.");
        }
        int width = data.getDimension(0);
        int height = data.getDimension(1);

        // Check the PSF.
        if (psf == null) {
            fatal("PSF not specified.");
        }
        if (psf.getRank() != 2) {
            fatal("Expecting 2D array as PSF.");
        }
        if (psf.getDimension(0) != width || psf.getDimension(1) != height) {
            fatal("The dimensions of the PSF must match those of the input image.");
        }

        // Initialize a vector space and populate it with workspace vectors.
        int[] shape = {width, height};
        DoubleShapedVectorSpace space = new DoubleShapedVectorSpace(shape);
        RealComplexFFT FFT = new RealComplexFFT(space);
        LinearOperator W = null; // new LinearOperator(space);
        DoubleShapedVector y = space.wrap(data.flatten());
        DoubleShapedVector x = y.clone();
        DoubleShapedVector h = space.wrap(psf.flatten());
        ConvolutionOperator H = new ConvolutionOperator(FFT, h);
        result = ArrayFactory.wrap(x.getData(), x.cloneShape(), false);
        System.out.println("Vector space initialization complete.");

        // Build the cost functions
        QuadraticCost fdata = new QuadraticCost(H, y, W);
        HyperbolicTotalVariation fprior = new HyperbolicTotalVariation(space, epsilon);
        CompositeDifferentiableCostFunction cost = new CompositeDifferentiableCostFunction(1.0, fdata, mu, fprior);
        double fcost = 0.0;
        DoubleShapedVector gcost = space.create();
        System.out.println("Cost function initialization complete.");

        // Initialize the non linear conjugate gradient
        MoreThuenteLineSearch lineSearch = new MoreThuenteLineSearch(0.05, 0.1, 1E-17);
        int iter = -1;
        int eval = 0;
        int method = NonLinearConjugateGradient.DEFAULT_METHOD;
        NonLinearConjugateGradient minimizer = new NonLinearConjugateGradient(space, method, lineSearch);
        System.out.println("Non linear conjugate gradient initialization complete.");

        // Launch the non linear conjugate gradient
        int task = minimizer.start();
        while (true) {
            if (task == NonLinearConjugateGradient.TASK_COMPUTE_FG) {
                fcost = cost.computeCostAndGradient(0.1, x, gcost, true);
                ++eval;
            } else if (task == NonLinearConjugateGradient.TASK_NEW_X ||
                    task == NonLinearConjugateGradient.TASK_FINAL_X) {
                ++iter;
                System.out.format("iter %4d, eval %4d: |xn| = %.2g    fn = %.15g   |gn| = %.2g\n",
                        iter, eval, space.norm2(x), fcost, space.norm2(gcost));
                if (task == NonLinearConjugateGradient.TASK_FINAL_X ||
                        maxIter >= 0 && iter >= maxIter) {
                    System.out.println("in " + iter + " iterations, " + eval +" function calls and gradient calls");
                    break;
                }
            } else {
                System.err.println("error/warning: " + task);
                break;
            }
            task = minimizer.iterate(x, fcost, gcost);
        }
        System.out.println("Max : " + ArrayOps.getMax(x.getData()));
        System.out.println("Min : " + ArrayOps.getMin(x.getData()));
    }


    /* ************************ FONCTIONS UTILES   ************************* */

    /**
     * FONCTIONS UTILES : randInt()
     * @param min
     * @param max
     * @param taille
     * @return
     */
    public static int[] randInt(int min, int max, int taille) {
        int tabInd[] = new int[taille];
        for (int i = 0; i < taille; i++){
            Random rand = new Random();
            int randomNum = rand.nextInt((max - min) + 1) + min;
            tabInd[i] = randomNum;
        }
        return tabInd;
    }

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
        case DataFormat.FMT_PNM:
            formatName = "PNM";
            break;
        case DataFormat.FMT_JPEG:
            formatName = "JPEG";
            break;
        case DataFormat.FMT_PNG:
            formatName = "PNG";
            break;
        case DataFormat.FMT_TIFF:
            formatName = "TIFF";
            break;
        case DataFormat.FMT_GIF:
            formatName = "GIF";
            break;
        case DataFormat.FMT_MDA:
            MdaFormat.save(img, fileName);
            return;
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