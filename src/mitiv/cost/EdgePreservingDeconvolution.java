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

package mitiv.cost;

import mitiv.array.ArrayFactory;
import mitiv.array.ArrayUtils;
import mitiv.array.ByteArray;
import mitiv.array.DoubleArray;
import mitiv.array.FloatArray;
import mitiv.array.IntArray;
import mitiv.array.LongArray;
import mitiv.array.ShapedArray;
import mitiv.array.ShortArray;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.deconv.Convolution;
import mitiv.deconv.WeightedConvolutionCost;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.FloatShapedVectorSpace;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;
import mitiv.optim.OptimTask;
import mitiv.utils.FFTUtils;


public class EdgePreservingDeconvolution extends SmoothInverseProblem {

    /** Indicate whether internal parameters should be recomputed. */
    private boolean updatePending = true;

    /** Force single precision. */
    private boolean single;

    /** The data space. */
    private ShapedVectorSpace dataSpace = null;

    /** The object space. */
    private ShapedVectorSpace objectSpace = null;

    /** The current solution. */
    private Vector x = null;

    /** Input data. */
    private ShapedArray data = null;
    private boolean writableData = false;

    /** Optional statistical weights. */
    private ShapedArray weights = null;
    private boolean writableWeights = false;

    /** Standard deviation of detector readout noise (in counts/pixel). */
    private double sigma = Double.NaN;

    /** Detector gain in counts per analog to digital unit (in counts/ADU). */
    private double gamma = Double.NaN;

    /** Optional bad data mask. */
    private ShapedArray bads = null;

    /** Storage for the weighted data. */
    private WeightedData weightedData = null;

    /** The point spread function (PSF). */
    private ShapedArray psf = null;

    /** Auto-normalize the PSF? */
    private boolean normalizePSF = false;

    /** The result.  If non-null at the start, it is assumed to be the starting solution. */
    private ShapedArray object = null;

    /** Optional dimensions of the object. */
    private Shape objectShape = null;

    /** Filling value for initial object. */
    private double padValue = Double.NaN;

    /** Edge-preserving threshold. */
    private double epsilon = 1.0;

    /** Regularization scale along the dimensions. */
    private double[] scale = {1.0};

    /** Use new code? */
    private boolean useNewCode = false;

    private void forceRestart() {
        weightedData = null;
        updatePending = true;
    }

    public boolean getUseNewCode() {
        return useNewCode;
    }

    public void setUseNewCode(boolean value) {
        if (useNewCode != value) {
            useNewCode = value;
            forceRestart();
        }
    }

    public boolean getForceSinglePrecision() {
        return single;
    }

    public void setForceSinglePrecision(boolean value) {
        if (single != value) {
            single = value;
            forceRestart();
        }
    }

    public ShapedArray getData() {
        return data;
    }

    public void setData(ShapedArray arr, boolean writable) {
        if (data != arr) {
            data = arr;
            writableData = writable;
            forceRestart();
        }
    }

    public void setData(ShapedArray arr) {
        setData(arr, false);
    }

    public ShapedArray getWeights() {
        return weights;
    }

    public void setWeights(ShapedArray arr, boolean writable) {
        if (weights != arr) {
            weights = arr;
            writableWeights = writable;
            forceRestart();
        }
    }

    public void setWeights(ShapedArray arr) {
        setWeights(arr, false);
    }

    public ShapedArray getBads() {
        return bads;
    }

    public void setBads(ShapedArray arr) {
        if (bads != arr) {
            bads = arr;
            forceRestart();
        }
    }

    /**
     * Get the PSF.
     *
     * @return The point spread function.
     */
    public ShapedArray getPSF() {
        return psf;
    }

    /**
     * Set the PSF.
     *
     * @param arr
     *        The point spread function.
     */
    public void setPSF(ShapedArray arr) {
        setPSF(arr, false);
    }

    /**
     * Set the PSF.
     *
     * @param arr
     *        The point spread function.
     *
     * @param normalize
     *        Normalize the PSF? If true, all PSF values are divided by the sum
     *        of the PSF values; otherwise, the PSF is used as it is.
     */
    public void setPSF(ShapedArray arr, boolean normalize) {
        if (psf != arr) {
            psf = arr;
            normalizePSF = normalize;
            forceRestart();
        }
    }

    public double getEdgeThreshold() {
        return epsilon;
    }

    public void setEdgeThreshold(double value) {
        if (nonfinite(value) || value <= 0.0) {
            error("Edge threshold must be strictly positive");
        }
        if (epsilon != value) {
            epsilon = value;
            forceRestart();
        }
    }

    /**
     * Set regularization scale along the dimensions.
     *
     * @param delta
     *        The regularization scale along the dimensions of the solution. If
     *        a single value is given, the same scale for all dimensions will
     *        be used. Otherwise there should be as many values as the number
     *        of dimensions.
     *
     * @see HyperbolicTotalVariation
     */
    public void setScale(double... delta) {
        scale = delta;
    }

    /**
     * Get the regularization scale along the dimensions.
     *
     * @return The regularization scale for all dimensions of the solution.
     */
    public double[] getScale() {
        return scale;
    }

    public ShapedArray getSolution() {
        return object;
    }

    @Override
    public ShapedVector getBestSolution() {
        return (ShapedVector)super.getBestSolution();
    }

    public void setInitialSolution(ShapedArray arr) {
        if (object != arr) {
            object = arr;
            forceRestart();
            resetIteration();
        }
    }

    public Shape getObjectShape() {
        return objectShape;
    }

    public void setObjectShape(Shape shape) {
        if ((shape == null) != (objectShape == null) ||
                (shape != null && objectShape != null && ! shape.equals(objectShape))) {
            objectShape = shape;
            forceRestart();
        }
    }

    /**
     * Get the value for padding the initial solution.
     *
     * @return The padding value.
     */
    public double getFillValue() {
        return padValue;
    }

    /**
     * Set the value for padding the initial solution.
     *
     * <p> This value is only taken into account for a start. If it is NaN, a
     * value is automatically computed from the data (and the PSF if any). </p>
     *
     * @param fillValue
     *        The padding value.
     */
    public void setFillValue(double fillValue) {
        this.padValue = fillValue;
    }

    public void setObjectShape(int[] dims) {
        setObjectShape(new Shape(dims));
    }

    /**
     * Create an edge preserving deconvolution instance.
     *
     * The caller shall create a new instance, then set attributes (not
     * necessarily all) using public setters, then create the solver and run
     * it.  The validity of attributes is checked when the solver is created.
     * <p>
     * Typical usage:
     * <pre>
     * invpb = new EdgePreservingDeconvolution();
     * invpb.setData(data);
     * invpb.setPSF(psf);
     * invbp.setForceSinglePrecision(true);
     * invpb.start();
     * while (! invpb.stop()) {
     *    invpb.iterate();
     *    show(invpb.getObject());
     * }
     * </pre>
     * Restarting with a different initial solution (automatically restart):
     * <pre>
     * invpb.setObject(obj);
     * invpb.start();
     * </pre>
     * Changing the PSF:
     * <pre>
     * invpb.setPSF(psf);
     * invpb.start();
     * </pre>
     */
    public EdgePreservingDeconvolution() {
    }

    private void update() {

        /* Check input parameters for the likelihood term. */
        if (data == null) {
            error("No data specified");
        }
        final int rank =  data.getRank();
        final Shape dataShape = data.getShape();
        if (weights != null && ! weights.getShape().equals(dataShape)) {
            error("Weights and data must have the same dimensions");
        }
        if (bads != null && ! bads.getShape().equals(dataShape)) {
            error("Mask of invalid data must have the same dimensions as the data");
        }
        if (psf != null && psf.getRank() != rank) {
            error("PSF and data must have the same number of dimensions");
        }
        if (object != null && object.getRank() != rank) {
            error("Object and data must have the same number of dimensions");
        }
        if (objectShape != null && objectShape.rank() != rank) {
            error("Given object shape must the same number of dimensions as the data");
        }
        if (debug) {
            System.out.format("mu: %.2g, epsilon: %.2g\n",
                    getRegularizationLevel(), getEdgeThreshold());
        }

        /* Determine the floating-point type for all vectors. */
        int type;
        if (single) {
            type = Traits.FLOAT;
        } else if (data.getType() == Traits.DOUBLE ||
                (psf != null && psf.getType() == Traits.DOUBLE) ||
                (weights != null && weights.getType() == Traits.DOUBLE) ||
                (object != null && object.getType() == Traits.DOUBLE)) {
            type = Traits.DOUBLE;
        } else {
            type = Traits.FLOAT;
        }

        /* Determine the dimensions of the object. */
        if (psf == null) {
            objectShape = dataShape; // FIXME: for denoising, object has same size as data
            // if (objectShape != null) {
            //     /* Check whether given dimensions are large enough. */
            //     for (int k = 0; k < rank; ++k) {
            //         if (objectShape.dimension(k) < dataShape.dimension(k)) {
            // error("Given object dimensions must be at least those of the data");
            //         }
            //     }
            // } else {
            //     /* Solution will have the same dimensions as the data. */
            //     objectShape = dataShape;
            // }
        } else {
            final Shape psfShape = psf.getShape();
            if (objectShape != null) {
                /* Check whether given dimensions are large enough. */
                for (int k = 0; k < rank; ++k) {
                    if (objectShape.dimension(k) < dataShape.dimension(k)) {
                        error("Given object dimensions must be at least those of the data");
                    }
                    if (psfShape != null && objectShape.dimension(k) < psfShape.dimension(k)) {
                        error("Given object dimensions must be at least those of the PSF");
                    }
                }
            } else {
                /* Determine suitable object dimensions from input arrays. */
                final int[] objectDims = new int[rank];
                for (int k = 0; k < rank; ++k) {
                    int dim = dataShape.dimension(k) + psfShape.dimension(k) - 1;
                    if (object != null) {
                        dim = Math.max(dim, object.getDimension(k));
                    }
                    objectDims[k] = FFTUtils.bestDimension(dim);
                }
                objectShape = new Shape(objectDims);
            }
        }

        /* Build vector spaces. */
        if (type == Traits.FLOAT) {
            if (dataSpace==null)
                dataSpace = new FloatShapedVectorSpace(dataShape);
            if (objectSpace==null)
                objectSpace = new FloatShapedVectorSpace(objectShape);
        } else {
            if (dataSpace==null)
                dataSpace = new DoubleShapedVectorSpace(dataShape);
            if (objectSpace==null)
                objectSpace = new DoubleShapedVectorSpace(objectShape);
        }

        /* Build likelihood term. */
        if (psf == null) {
            weightedData = new WeightedData(dataSpace);
            setWeightsAndData(weightedData);
            setLikelihood(weightedData);
        } else if (useNewCode) {
            weightedData = new WeightedData(dataSpace);
            setWeightsAndData(weightedData);
            Convolution directModel = Convolution.build(objectSpace, dataSpace);
            directModel.setPSF(psf, normalizePSF);
            setLikelihood(new DifferentiableGaussianLikelihood(weightedData, directModel));
        } else {
            WeightedConvolutionCost fdata = WeightedConvolutionCost.build(objectSpace, dataSpace);
            setWeightsAndData(fdata);
            fdata.setPSF(psf, normalizePSF);
            setLikelihood(fdata);
            weightedData = fdata;
        }

        /* Initial solution. */
        if (object == null) {
            /* Create a flat object with the same value everywhere. */
            double val = computePadValue();
            object = ArrayFactory.create(type, objectShape);
            if (single) {
                ((FloatArray)object).fill((float)val);
            } else {
                ((DoubleArray)object).fill(val);
            }
            if (debug) {
                System.err.format("Create initial array with value %g\n", val);
            }
        } else {
            /* Crop/pad the given object to the proper dimensions. */
            double val = 0;
            for (int k = 0; k < rank; ++k) {
                if (objectShape.dimension(k) > object.getDimension(k)) {
                    val = computePadValue();
                    break;
                }
            }
            if (debug) {
                System.err.format("Pad initial array with value %g\n", val);
            }
            object = ArrayUtils.extract(object, objectShape, val);
        }

        /* Build the regularization cost function. */
        HyperbolicTotalVariation fprior = new HyperbolicTotalVariation(objectSpace, epsilon);
        if (scale.length == 1) {
            fprior.setScale(scale[0]);
        } else {
            fprior.setScale(scale);
        }
        setRegularization(fprior);

        /* Make sure the vector of variables share its contents with the
           object shaped array. */
        boolean wrap = (object.getType() != type || ! object.isFlat());
        x = objectSpace.create(object, false);
        if (wrap) {
            if (type == Traits.FLOAT) {
                object = ArrayFactory.wrap(((FloatShapedVector)x).getData(), objectShape);
            } else {
                object = ArrayFactory.wrap(((DoubleShapedVector)x).getData(), objectShape);
            }
        }

        updatePending = false;
    }

    public OptimTask start() {
        return start(false);
    }

    public OptimTask start(boolean reset) {
        /* Make sure everything is correctly initialized. */
        if (updatePending) {
            update();
        }
        return super.start(x, reset);
    }

    public OptimTask iterate() {
        if (updatePending) {
            return start();
        } else {
            return super.iterate(x);
        }
    }

    private static void error(String reason) {
        throw new IllegalArgumentException(reason);
    }

    private boolean nonfinite(double value) {
        return Double.isInfinite(value) || Double.isNaN(value);
    }

    public double getDetectorNoise() {
        return sigma;
    }

    public void setDetectorNoise(double sigma) {
        this.sigma = sigma;
    }

    public double getDetectorGain() {
        return gamma;
    }

    public void setDetectorGain(double gamma) {
        this.gamma = gamma;
    }

    private void setWeightsAndData(WeightedData weightedData) {
        /* Set the data. */
        weightedData.setData(data, writableData);

        /* Set the weights. */
        if (weights != null) {
            if (! isnan(sigma) || ! isnan(gamma)) {
                System.err.println("Warning: noise model parameters are ignored when weights are specified.");
            }
            weightedData.setWeights(weights, writableWeights);
        } else {
            double alpha;
            double beta;
            if (isnan(sigma)) {
                if (! isnan(gamma)) {
                    System.err.println("Warning: linear noise model parameter is ignored if affine noise model parameter is not specified");
                }
                alpha = 0;
                beta = 1;
            } else if (isnan(gamma)) {
                alpha = 0;
                beta = abs2(sigma);
            } else {
                alpha = 1/gamma;
                beta = abs2(sigma/gamma);
            }
            System.err.format("alpha = %g, beta = %g\n", alpha, beta);
            weightedData.computeWeightsFromData(alpha, beta);
        }

        /* Deal with bad pixels. */
        if (bads != null) {
            weightedData.markBadData(bads);
        }
    }

    private double computePadValue() {
        double val;
        if (isnan(padValue)) {
            val = weightedData.getWeightedMean();
            if (psf != null && ! normalizePSF) {
                val /= sum(psf);
            }
        } else {
            val = padValue;
        }
        return val;
    }

    private static double sum(ShapedArray arr) {
        double sum = 0.0;
        if (arr != null) {
            switch(arr.getType()) {

                case Traits.BYTE:
                    sum = ((ByteArray)arr).sum();
                    break;

                case Traits.SHORT:
                    sum = ((ShortArray)arr).sum();
                    break;

                case Traits.INT:
                    sum = ((IntArray)arr).sum();
                    break;

                case Traits.LONG:
                    sum = ((LongArray)arr).sum();
                    break;

                case Traits.FLOAT:
                    sum = ((FloatArray)arr).sum();
                    break;

                case Traits.DOUBLE:
                    sum = ((DoubleArray)arr).sum();
                    break;
            }
        }
        return sum;
    }

    private final static boolean isnan(double x) {
        return Double.isNaN(x);
    }

    private final static double abs2(double x) {
        return x*x;
    }

}
