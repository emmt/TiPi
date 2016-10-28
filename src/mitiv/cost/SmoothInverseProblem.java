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

import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;
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

/**
 * Implement iterative solver for simple smooth inverse problem.
 *
 * <p>A <i>smooth</i> inverse problem is associated with a differentiable cost
 * function with the following form:</p>
 *
 * <pre>
 * f(x) = fdata(x) + µ * fprior(x)
 * </pre>
 *
 * <p>where {@code x} are the variables, {@code fdata(x)} is the likelihood cost
 * function, {@code fprior(x)} is the regularization and {@code µ >= 0} is the
 * regularization level. </p>
 *
 * @author Éric.
 *
 */
public class SmoothInverseProblem extends IterativeDifferentiableSolver {

    /** Debug mode. */
    protected boolean debug = false;

    /** Indicate whether internal parameters should be recomputed. */
    private boolean restart = true;

    /** Data cost function. */
    private DifferentiableCostFunction fdata = null;

    /** Regularization level. */
    private double mu = 1.0;

    /** Regularization.  */
    private DifferentiableCostFunction fprior = null;

    /**
     * Number of memorized steps for the LBFGS method, nonlinear
     * conjugate gradient is used if this value is less than one.
     */
    private int limitedMemorySize = 5;

    /** Lower bound for the variables. */
    private double lowerBound = Double.NEGATIVE_INFINITY;

    /** Upper bound for the variables. */
    private double upperBound = Double.POSITIVE_INFINITY;

    /** Projector to apply the bound constraints. */
    private BoundProjector projector = null;

    private LineSearch lineSearch = null;

    /** Absolute gradient tolerance for the convergence. */
    private double gatol = 0.0;

    /** Relative gradient tolerance for the convergence. */
    private double grtol = 1e-3;

    public double getAbsoluteTolerance() {
        return gatol;
    }

    public void setAbsoluteTolerance(double value) {
        if (nonfinite(value) || value < 0.0) {
            error("Absolute tolerance for convergence must be nonnegative");
        }
        gatol = value;
    }

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
     * Get the number of previous steps to memorize.
     *
     * @return The number of previous steps to memorize.
     */
    public int getLimitedMemorySize() {
        return limitedMemorySize;
    }

    /**
     * Set the number of previous steps to memorize.
     *
     * @param value
     *        The number of previous steps to memorize. If strictly positive,
     *        this is the number of memorized steps for the LBFGS method;
     *        nonlinear conjugate gradient is used if this value is less than
     *        one.
     */
    public void setLimitedMemorySize(int value) {
        if (value < 0) {
            error("Limited memory size be nonnegative");
        }
        if (limitedMemorySize != value) {
            limitedMemorySize = value;
            restart = true;
        }
    }

    /**
     * Get the lower bound on the variables.
     *
     * @return The value of the lower bound, {@link Double#NEGATIVE_INFINITY}
     *         if none.
     */
    public double getLowerBound() {
        return lowerBound;
    }

    /**
     * Set the value of the lower bound for the variables.
     *
     * @param value
     *        The lower bound for all the variables.
     */
    public void setLowerBound(double value) {
        if (Double.isNaN(value) || value == Double.POSITIVE_INFINITY) {
            error("Invalid value for the lower bound");
        }
        if (lowerBound != value) {
            lowerBound = value;
            restart = true;
        }
    }

    /**
     * Get the upper bound on the variables.
     *
     * @return The value of the upper bound, {@link Double#POSITIVE_INFINITY}
     *         if none.
     */
    public double getUpperBound() {
        return upperBound;
    }

    /**
     * Set the value of the upper bound for the variables.
     *
     * @param value
     *        The upper bound for all the variables.
     */
    public void setUpperBound(double value) {
        if (Double.isNaN(value) || value == Double.NEGATIVE_INFINITY) {
            error("Invalid value for the upper bound");
        }
        if (upperBound != value) {
            upperBound = value;
            restart = true;
        }
    }

    /**
     * Get the likelihood.
     *
     * @return The data cost function, {@code null} if none has been specified
     *         yet.
     */
    public DifferentiableCostFunction getLikelihood() {
        return fdata;
    }

    /**
     * Set the likelihood.
     *
     * @param f The data cost function, can be {@code null} to unspecify it.
     */
    public void setLikelihood(DifferentiableCostFunction f) {
        if (fdata != f) {
            fdata = f;
            restart = true;
        }
    }

    /**
     * Get the regularization level.
     *
     * @return The value of the regularization level.
     */
    public double getRegularizationLevel() {
        return mu;
    }

    /**
     * Set the regularization level.
     *
     * @param value
     *        The regularization level (must be nonnegative).
     */
    public void setRegularizationLevel(double value) {
        if (nonfinite(value) || value < 0.0) {
            error("Regularization level must be nonnegative");
        }
        if (mu != value) {
            mu = value;
            restart = true;
        }
    }

    /**
     * Get the regularization.
     *
     * @return The regularization cost function, {@code null} if none has been
     *         specified yet.
     */
    public DifferentiableCostFunction getRegularization() {
        return fprior;
    }

    /**
     * Set the regularization.
     *
     * @param f
     *        The regularization cost function, can be {@code null} to unspecify
     *        it.
     */
    public void setRegularization(DifferentiableCostFunction f) {
        if (fprior != f) {
            fprior = f;
            restart = true;
        }
    }

    public boolean getDebug() {
        return debug;
    }

    public void setDebug(boolean value) {
        debug = value;
    }

    /**
     * Create an iterative solver for simple smooth inverse problem.
     */
    public SmoothInverseProblem() {
    }

    @Override
    public OptimTask start(Vector x) {
        return start(x, false);
    }

    @Override
    public OptimTask start(Vector x, boolean reset) {
        /* Make sure everything is correctly initialized. */
        if (restart) {
            setup(x);
        }
        return super.start(x, reset);
    }

    @Override
    public OptimTask iterate(Vector x) {
        if (restart) {
            return start(x);
        } else {
            return super.iterate(x);
        }
    }

    /**
     * Release as much resources as possible.
     */
    public void releaseResources() {
        setOptimizer(null);
        restart = true;
    }

    /** Initialize all resources. */
    private void setup(Vector x) {

        /*
         * Assemble the cost function. This must be the first stage because it
         * conditions the vector space of the variables.
         */
        if (fdata == null) {
            error("No data cost specified");
        }
        if (mu == 0.0) {
            setCostFunction(fdata);
        } else {
            if (fprior == null) {
                error("No regularization specified");
            }
            setCostFunction(new CompositeDifferentiableCostFunction(1.0, fdata, mu, fprior));
        }

        /* Determine the constraints and choose an optimizer to solve the problem. */
        if (lowerBound > upperBound) {
            error("Incompatible bounds");
        }
        int bounded = 0;
        if (lowerBound != Double.NEGATIVE_INFINITY) {
            bounded |= 1;
        }
        if (upperBound != Double.POSITIVE_INFINITY) {
            bounded |= 2;
        }
        lineSearch = null;
        projector = null;
        VectorSpace space = getSpace(); // the vector space of the variables
        if (bounded == 0) {
            /* No bounds have been specified. */
            lineSearch = new MoreThuenteLineSearch(LBFGS.SFTOL, LBFGS.SGTOL, LBFGS.SXTOL);
            if (limitedMemorySize > 0) {
                LBFGS lbfgs = new LBFGS(space, limitedMemorySize, lineSearch);
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
                        NonLinearConjugateGradient(space, method, lineSearch);
                nlcg.setAbsoluteTolerance(gatol);
                nlcg.setRelativeTolerance(grtol);
                setOptimizer(nlcg);
                if (debug) {
                    System.out.format("Using non-linear conjugate gradients.\n");
                }
            }
        } else {
            /* Some bounds have been specified. */
            if (! (space instanceof ShapedVectorSpace)) {
                error("Bounds are only implented for shaped vectors");
            }
            if (bounded == 1) {
                /* Only a lower bound has been specified. */
                projector = new SimpleLowerBound((ShapedVectorSpace)space, lowerBound);
            } else if (bounded == 2) {
                /* Only an upper bound has been specified. */
                projector = new SimpleUpperBound((ShapedVectorSpace)space, upperBound);
            } else {
                /* Both a lower and an upper bounds have been specified. */
                projector = new SimpleBounds((ShapedVectorSpace)space, lowerBound, upperBound);
            }
            final int m = (limitedMemorySize > 1 ? limitedMemorySize : 5); // FIXME:
            //lineSearch = new ArmijoLineSearch(0.5, 1e-4);
            //VMLMB vmlmb = new VMLMB(objectSpace, projector, m, lineSearch);
            //vmlmb.setAbsoluteTolerance(gatol);
            //vmlmb.setRelativeTolerance(grtol);
            //optimizer = vmlmb;
            final BLMVM blmvm = new BLMVM(space, projector, m);
            blmvm.setAbsoluteTolerance(gatol);
            blmvm.setRelativeTolerance(grtol);
            setOptimizer(blmvm);
            projector.projectVariables(x, x);
            if (debug) {
                System.out.format("Using BLMVM with %d memorized steps.\n",
                        limitedMemorySize);
            }
        }

        restart = false;
    }

    private static void error(String reason) {
        throw new IllegalArgumentException(reason);
    }

    private boolean nonfinite(double value) {
        return Double.isInfinite(value) || Double.isNaN(value);
    }

}
