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
import mitiv.optim.BLMVM;
import mitiv.optim.BoundProjector;
import mitiv.optim.IterativeDifferentiableSolver;
import mitiv.optim.LBFGS;
import mitiv.optim.LineSearch;
import mitiv.optim.MoreThuenteLineSearch;
import mitiv.optim.NonLinearConjugateGradient;
import mitiv.optim.OptimTask;
import mitiv.optim.SimpleBounds;
import mitiv.optim.SimpleLowerBound;
import mitiv.optim.SimpleUpperBound;
import mitiv.utils.FFTUtils;


public class EdgePreservingDeconvolution extends IterativeDifferentiableSolver {

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
    private ShapedArray weight = null;

    public ShapedArray getWeight() {
        return weight;
    }

    public void setWeights(ShapedArray arr) {
        if (weight != arr) {
            weight = arr;
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

    /** Regularization level. */
    private double mu = 10.0;

    public double getRegularizationLevel() {
        return mu;
    }

    public void setRegularizationLevel(double value) {
        if (nonfinite(value) || value < 0.0) {
            error("Regularization level must be nonnegative");
        }
        if (mu != value) {
            mu = value;
            updatePending = true;
        }
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

    /** Absolute gradient tolerance for the convergence. */
    private double gatol = 0.0;

    public double getAbsoluteTolerance() {
        return gatol;
    }

    public void setAbsoluteTolerance(double value) {
        if (nonfinite(value) || value < 0.0) {
            error("Absolute tolerance for convergence must be nonnegative");
        }
        gatol = value;
    }

    /** Relative gradient tolerance for the convergence. */
    private double grtol = 1e-3;

    public double getRelativeTolerance() {
        return grtol;
    }

    public void setRelativeTolerance(double value) {
        if (nonfinite(value) || value < 0.0) {
            error("Relative tolerance for convergence must be nonnegative");
        }
        grtol = value;
    }

    /**
     * Number of memorized steps for the LBFGS method, nonlinear
     * conjugate gradient is used if this value is less than one.
     */
    private int limitedMemorySize = 0;

    public int getLimitedMemorySize() {
        return limitedMemorySize;
    }

    public void setLimitedMemorySize(int value) {
        if (value < 0) {
            error("Limited memory size be nonnegative");
        }
        if (limitedMemorySize != value) {
            limitedMemorySize = value;
            updatePending = true;
        }
    }

    /** Lower bound for the variables. */
    private double lowerBound = Double.NEGATIVE_INFINITY;

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double value) {
        if (Double.isNaN(value) || value == Double.POSITIVE_INFINITY) {
            error("Invalid value for the lower bound");
        }
        if (lowerBound != value) {
            lowerBound = value;
            updatePending = true;
        }
    }

    /** Upper bound for the variables. */
    private double upperBound = Double.POSITIVE_INFINITY;

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double value) {
        if (Double.isNaN(value) || value == Double.NEGATIVE_INFINITY) {
            error("Invalid value for the upper bound");
        }
        if (upperBound != value) {
            upperBound = value;
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

    /** Debug mode. */
    public boolean debug = false;

    public boolean getDebug() {
        return debug;
    }

    public void setDebug(boolean value) {
        debug = value;
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
    private DifferentiableCostFunction fdata = null;
    private HyperbolicTotalVariation fprior = null;
    private BoundProjector projector = null;
    private int bounded = 0;
    private LineSearch lineSearch = null;
    private Vector x = null; // current solution

    private void update() {

        /* Interdependent attributes have not been checked.  Perform these checks now. */
        if (lowerBound > upperBound) {
            error("Incompatible bounds");
        }
        if (data == null) {
            error("No data specified");
        }
        final int rank =  data.getRank();
        final Shape dataShape = data.getShape();
        if (weight != null &&! weight.getShape().equals(dataShape)) {
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
            System.out.format("mu: %.2g, epsilon: %.2g\n", mu, epsilon);
        }

        /* Determine the floating-point type for all vectors. */
        int type;
        if (single) {
            type = Traits.FLOAT;
        } else if (data.getType() == Traits.DOUBLE || psf.getType() == Traits.DOUBLE ||
                (weight != null && weight.getType() == Traits.DOUBLE) ||
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
            if (weight != null) {
                weightedData.setWeights(dataSpace.create(weight), true);
            }

            /* Build the direct model. */
            Convolution directModel = Convolution.build(objectSpace, dataSpace);
            directModel.setPSF(psf);
            fdata = new DifferentiableGaussianLikelihood(weightedData, directModel);
        } else {
            WeightedConvolutionCost cost = WeightedConvolutionCost.build(objectSpace, dataSpace);
            cost.setPSF(psf);
            cost.setWeightsAndData(weight, data);
            fdata = cost;
        }

        /* Build the regularization cost function. */
        fprior = new HyperbolicTotalVariation(objectSpace, epsilon);

        /* Build the total cost function. */
        setCostFunction(new CompositeDifferentiableCostFunction(1.0, fdata, mu, fprior));

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

        /* Choose an optimizer to solve the problem. */
        lineSearch = null;
        projector = null;
        bounded = 0;
        if (lowerBound != Double.NEGATIVE_INFINITY) {
            bounded |= 1;
        }
        if (upperBound != Double.POSITIVE_INFINITY) {
            bounded |= 2;
        }
        if (bounded == 0) {
            /* No bounds have been specified. */
            lineSearch = new MoreThuenteLineSearch(LBFGS.SFTOL, LBFGS.SGTOL, LBFGS.SXTOL);
            if (limitedMemorySize > 0) {
                LBFGS lbfgs = new LBFGS(objectSpace, limitedMemorySize, lineSearch);
                lbfgs.setAbsoluteTolerance(gatol);
                lbfgs.setRelativeTolerance(grtol);
                setOptimizer(lbfgs);
                if (debug) {
                    System.out.format("Using L-BFGS with %d memorized steps.\n",
                            limitedMemorySize);
                }
            } else {
                lineSearch = new MoreThuenteLineSearch(NonLinearConjugateGradient.SFTOL,
                        NonLinearConjugateGradient.SGTOL,
                        NonLinearConjugateGradient.SXTOL);
                int method = NonLinearConjugateGradient.DEFAULT_METHOD;
                NonLinearConjugateGradient nlcg = new
                        NonLinearConjugateGradient(objectSpace, method, lineSearch);
                nlcg.setAbsoluteTolerance(gatol);
                nlcg.setRelativeTolerance(grtol);
                setOptimizer(nlcg);
                if (debug) {
                    System.out.format("Using non-linear conjugate gradients.\n");
                }
            }
        } else {
            /* Some bounds have been specified. */
            if (bounded == 1) {
                /* Only a lower bound has been specified. */
                projector = new SimpleLowerBound(objectSpace, lowerBound);
            } else if (bounded == 2) {
                /* Only an upper bound has been specified. */
                projector = new SimpleUpperBound(objectSpace, upperBound);
            } else {
                /* Both a lower and an upper bounds have been specified. */
                projector = new SimpleBounds(objectSpace, lowerBound, upperBound);
            }
            final int m = (limitedMemorySize > 1 ? limitedMemorySize : 5); // FIXME:
            //lineSearch = new ArmijoLineSearch(0.5, 1e-4);
            //VMLMB vmlmb = new VMLMB(objectSpace, projector, m, lineSearch);
            //vmlmb.setAbsoluteTolerance(gatol);
            //vmlmb.setRelativeTolerance(grtol);
            //optimizer = vmlmb;
            final BLMVM blmvm = new BLMVM(objectSpace, projector, m);
            blmvm.setAbsoluteTolerance(gatol);
            blmvm.setRelativeTolerance(grtol);
            setOptimizer(blmvm);
            projector.projectVariables(x, x);
            if (debug) {
                System.out.format("Using BLMVM with %d memorized steps.\n",
                        limitedMemorySize);
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

}
