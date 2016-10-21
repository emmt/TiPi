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
import mitiv.array.ShapedArray;
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

    /** Use new code? */
    private boolean useNewCode = false;

    public boolean getUseNewCode() {
        return useNewCode;
    }

    public void setUseNewCode(boolean value) {
        if (useNewCode != value) {
            useNewCode = value;
            updatePending = true;
        }
    }

    /** Indicate whether internal parameters should be recomputed. */
    private boolean updatePending = true;

    /** Input data. */
    private ShapedArray data = null;

    public ShapedArray getData() {
        return data;
    }

    public void setData(ShapedArray arr) {
        if (data != arr) {
            data = arr;
            updatePending = true;
        }
    }

    /** Optional statistical weights. */
    private ShapedArray weights = null;

    public ShapedArray getWeights() {
        return weights;
    }

    public void setWeights(ShapedArray arr) {
        if (weights != arr) {
            weights = arr;
            updatePending = true;
        }
    }

    /** Point spread function. */
    private ShapedArray psf = null;

    public ShapedArray getPSF() {
        return psf;
    }

    public void setPSF(ShapedArray arr) {
        if (psf != arr) {
            psf = arr;
            updatePending = true;
        }
    }

    /** The result.  If non-null at the start, it is assumed to be the starting solution. */
    private ShapedArray object = null;

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
            updatePending = true;
        }
    }

    /** Optional dimensions of the object. */
    private Shape objectShape = null;

    public Shape getObjectShape() {
        return objectShape;
    }

    public void setObjectShape(Shape shape) {
        if ((shape == null) != (objectShape == null) ||
                (shape != null && objectShape != null && ! shape.equals(objectShape))) {
            objectShape = shape;
            updatePending = true;
        }
    }

    public void setObjectShape(int[] dims) {
        setObjectShape(new Shape(dims));
    }

    /** Edge-preserving threshold. */
    private double epsilon = 1.0;

    public double getEdgeThreshold() {
        return epsilon;
    }

    public void setEdgeThreshold(double value) {
        if (nonfinite(value) || value <= 0.0) {
            error("Edge threshold must be strictly positive");
        }
        if (epsilon != value) {
            epsilon = value;
            updatePending = true;
        }
    }

    /** Force single precision. */
    private boolean single;

    public boolean getForceSinglePrecision() {
        return single;
    }

    public void setForceSinglePrecision(boolean value) {
        if (single != value) {
            single = value;
            updatePending = true;
        }
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

    private ShapedVectorSpace dataSpace = null;
    private ShapedVectorSpace objectSpace = null;
    private Vector x = null; // current solution

    private void update() {

        if (data == null) {
            error("No data specified");
        }
        final int rank =  data.getRank();
        final Shape dataShape = data.getShape();
        if (weights != null &&! weights.getShape().equals(dataShape)) {
            error("Weights and data must have the same dimensions");
        }
        if (psf == null) {
            error("No PSF specified");
        }
        if (psf.getRank() != rank) {
            error("PSF and data must have the same number of dimensions");
        }
        final Shape psfShape = psf.getShape();
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
        } else if (data.getType() == Traits.DOUBLE || psf.getType() == Traits.DOUBLE ||
                (weights != null && weights.getType() == Traits.DOUBLE) ||
                (object != null && object.getType() == Traits.DOUBLE)) {
            type = Traits.DOUBLE;
        } else {
            type = Traits.FLOAT;
        }

        /* Determine the dimensions of the object and crop/pad the initial
           object (if specified) to the correct dimensions. */
        if (objectShape != null) {
            /* Check whether given dimensions are suitable. */
            for (int k = 0; k < rank; ++k) {
                if (objectShape.dimension(k) < dataShape.dimension(k)) {
                    error("Given object dimensions must be at least those of the data");
                }
                if (objectShape.dimension(k) < psfShape.dimension(k)) {
                    error("Given object dimensions must be at least those of the PSF");
                }
            }
            if (object != null) {
                boolean crop = false;
                boolean pad = false;
                final int[] cropDims = new int[rank];
                for (int k = 0; k < rank; ++k) {
                    if (objectShape.dimension(k) < object.getDimension(k)) {
                        crop = true;
                    }
                    if (objectShape.dimension(k) > object.getDimension(k)) {
                        pad = true;
                    }
                    cropDims[k] = Math.max(objectShape.dimension(k), object.getDimension(k));
                }
                if (crop) {
                    object = ArrayUtils.crop(object, new Shape(cropDims));
                }
                if (pad) {
                    object = ArrayUtils.pad(object, objectShape);
                }
            }
        } else {
            /* Guess object dimensions from input arrays. */
            final int[] objectDims = new int[rank];
            if (object == null) {
                for (int k = 0; k < rank; ++k) {
                    final int minDim = dataShape.dimension(k) + psfShape.dimension(k) - 1;
                    objectDims[k] = FFTUtils.bestDimension(minDim);
                }
                objectShape = new Shape(objectDims);
            } else {
                for (int k = 0; k < rank; ++k) {
                    final int minDim = Math.max(dataShape.dimension(k) + psfShape.dimension(k) - 1,
                            object.getDimension(k));
                    objectDims[k] = FFTUtils.bestDimension(minDim);
                }
                objectShape = new Shape(objectDims);
                object = ArrayUtils.pad(object, objectShape);
            }
        }

        /* Build vector spaces. */
        if (type == Traits.FLOAT) {
            dataSpace = new FloatShapedVectorSpace(dataShape);
            objectSpace = new FloatShapedVectorSpace(objectShape);
        } else {
            dataSpace = new DoubleShapedVectorSpace(dataShape);
            objectSpace = new DoubleShapedVectorSpace(objectShape);
        }

        /* Build the likelihood cost function. */
        if (useNewCode) {
            /* Build weighted data instance. */
            WeightedData  weightedData = new WeightedData(dataSpace.create(data), true);
            if (weights != null) {
                weightedData.setWeights(dataSpace.create(weights), true);
            }

            /* Build the direct model. */
            Convolution directModel = Convolution.build(objectSpace, dataSpace);
            directModel.setPSF(psf);
            setLikelihood(new DifferentiableGaussianLikelihood(weightedData, directModel));
        } else {
            WeightedConvolutionCost cost = WeightedConvolutionCost.build(objectSpace, dataSpace);
            cost.setPSF(psf);
            cost.setData(data);
            cost.setWeights(weights);
            setLikelihood(cost);
        }

        /* Build the regularization cost function. */
        HyperbolicTotalVariation fprior = new HyperbolicTotalVariation(objectSpace, epsilon);
        if (scale.length == 1) {
            fprior.setScale(scale[0]);
        } else {
            fprior.setScale(scale);
        }
        System.err.println("WARNING scale disabled");
        setRegularization(fprior);

        /* Make sure the vector of variables share its contents with the
           object shaped array. */
        boolean wrap;
        if (object != null) {
            wrap = (object.getType() != type || ! object.isFlat());
            x = objectSpace.create(object, false);
        } else {
            wrap = true;
            x = objectSpace.create(0.0);
        }
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

    private double[] scale = {1.0};

    /**
     * Set regularization scale along the dimensions.
     *
     * @param delta
     *        The regularization scale along the dimensions of the solution. If a single value is
     *        given, the same scale for all dimensions will be used. Otherwise
     *        there should be as many values as the number of dimensions.
     *
     * @see {@link HyperbolicTotalVariation}.
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

}
