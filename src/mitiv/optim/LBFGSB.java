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

package mitiv.optim;

import mitiv.linalg.LinearOperator;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

/**
 * Multivariate non-linear optimization with simple bound constraints by
 * L-BFGS-B/VMLM-B method.
 * 
 * <p>There are some differences compared to {@link LBFGS}, the unconstrained
 * version of the algorithm:
 * <ol>
 * <li>The initial variables must be feasible.  This is easily achieved by
 *     applying the projector on the initial variables.</li>
 * <li>The gradients computed by the caller are projected.  This means
 *     that they are not left unchanged.</li>
 * <li>The line search procedure should only implement a sufficient decrease
 *     test (<i>e.g.</i> first Wolfe condition).</li>
 * </ol>
 * </p>
 * 
 * @author Éric Thiébaut.
 *
 */
public class LBFGSB implements ReverseCommunicationOptimizer {

    public static int NO_PROBLEM = 0;
    public static int BAD_H0 = 1; /* H0 is not positive definite */
    public static int LNSRCH_WARNING = 2; /* warning in line search */
    public static int LNSRCH_ERROR = 3; /* error in line search */

    protected LBFGSOperator H = null;
    protected LineSearch lnsrch;
    protected OptimTask task = null;
    protected int reason = NO_PROBLEM;

    /* Number of function (and gradient) evaluations since start. */
    protected int evaluations = 0;

    /* Number of iterations since start. */
    protected int iterations = 0;

    /* Number of restarts. */
    protected int restarts = 0;

    protected double epsilon = 0.01;
    protected double tiny = 1e-3;
    protected double pnorm; // the norm of P
    protected double grtol;  /* Relative threshold for the norm or the gradient (relative
    to GTEST the norm of the initial gradient) for convergence. */
    protected double gatol;  /* Absolute threshold for the norm or the gradient for
    convergence. */

    /** Norm or the initial projected gradient. */
    protected double gtest;

    protected double stpmin = 1e-20;
    protected double stpmax = 1e6;

    /* To save space, the variable and gradient at the start of a line
     * search are references to the (s,y) pair of vectors of the LBFGS
     * operator just after the mark.
     */
    private boolean saveMemory = true;

    /** Variables at the start of the line search. */
    protected Vector x0 = null;

    /** Function value at X0. */
    protected  double f0 = 0.0;

    /** Projected gradient at X0. */
    protected Vector g0 = null;

    /**
     * The (anti-)search direction.
     * 
     * An iterate is computed as: x1 = x0 - alpha*p
     * with alpha > 0.
     */
    protected Vector p = null;

    /** The current step length. */
    protected double alpha;

    /** Directional derivative at X0. */
    protected  double dg0 = 0.0;

    /** Euclidean norm of the projected gradient at the last accepted step. */
    protected double g1norm = 0.0;

    /** Euclidean norm of the projected gradient at X0. */
    protected double g0norm = 0.0;


    protected final BoundProjector projector;

    public LBFGSB(VectorSpace vsp, BoundProjector bp, int m, LineSearch ls) {
        H = new LBFGSOperator(vsp, m);
        projector = bp;
        lnsrch = ls;
    }

    public LBFGSB(LinearOperator H0, BoundProjector bp, int m, LineSearch ls) {
        H = new LBFGSOperator(H0, m);
        projector = bp;
        lnsrch = ls;
    }

    @Override
    public OptimTask getTask() {
        return task;
    }

    @Override
    public int getIterations() {
        return iterations;
    }

    @Override
    public int getEvaluations() {
        return evaluations;
    }

    @Override
    public int getRestarts() {
        return restarts;
    }

    @Override
    public String getMessage(int reason) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getReason() {
        return reason;
    }

    @Override
    public OptimTask start() {
        evaluations = 0;
        iterations = 0;
        restarts = 0;
        return begin();
    }

    @Override
    public OptimTask restart() {
        ++restarts;
        return begin();
    }

    private OptimTask begin() {
        H.reset();
        task = OptimTask.COMPUTE_FG;
        return task;
    }

    @Override
    public OptimTask iterate(Vector x1, double f1, Vector g1) {

        if (task == OptimTask.COMPUTE_FG) {

            ++evaluations;
            //System.err.format("eval=%d, |x1|=%g, f1=%.15g, |g1|=%g",
            //        evaluations, x1.norm2(), f1, g1.norm2());
            projector.projectGradient(x1, g1, g1);
            //System.err.format(", |g1p|=%g\n", g1.norm2());
            if (evaluations > 1) {
                /* A line search is in progress.  Compute directional
                 * derivative and check whether line search has converged. */
                int status = lnsrch.iterate(alpha, f1, -p.dot(g1));
                if (status == LineSearch.SEARCH) {
                    return nextStep(x1);
                }
                if (status != LineSearch.CONVERGENCE &&
                        status != LineSearch.WARNING_ROUNDING_ERRORS_PREVENT_PROGRESS) {
                    return lineSearchFailure(status);
                }
                ++iterations;
            }

            /* The current step is acceptable. Check for global convergence. */
            g1norm = g1.norm2();
            if (evaluations == 1) {
                gtest = g1norm;
            }
            reason = NO_PROBLEM;
            task = (g1norm <= getGradientThreshold() ? OptimTask.FINAL_X : OptimTask.NEW_X);

        } else if (task == OptimTask.NEW_X || task == OptimTask.FINAL_X) {

            /* Compute a search direction, possibly after updating the LBFGS
             * matrix.  We take care of checking whether D = -P is a
             * sufficient descent direction.  As shown by Zoutendijk, this is
             * true if:
             *      cos(theta) = (D/|D|)'.(G/|G|) >= EPSILON > 0
             * where G is the gradient. */
            if (evaluations == 1) {
                if (p == null) {
                    p = H.getOutputSpace().create();
                }
                if (! saveMemory && x0 == null) {
                    x0 = H.getOutputSpace().create();
                }
                if (! saveMemory && g0 == null) {
                    g0 = H.getInputSpace().create();
                }
            } else {
                H.update(x1, x0, g1, g0);
            }
            while (true) {
                H.apply(g1, p);
                projector.projectGradient(x1, p, p);
                pnorm = p.norm2(); // FIXME: in some cases, can be just GNORM*GAMMA
                double pg = p.dot(g1);
                if (pg >= epsilon*pnorm*g1norm) {
                    /* Accept P (respectively D = -P) as a sufficient ascent
                     * (respectively descent) direction and set the directional
                     * derivative. */
                    dg0 = -pg;
                    break;
                }
                if (H.mp < 1) {
                    /* Initial iteration or recursion has just been
                     * restarted.  This means that the initial inverse
                     * Hessian approximation is not positive definite. */
                    reason = BAD_H0;
                    task = OptimTask.ERROR;
                    return task;
                }
                /* Restart the LBFGS recursion and loop to use H0 to compute
                 * an initial search direction. */
                H.reset();
                ++restarts;
            }

            /* Save current variables X0, gradient G0 and function value F0. */
            if (saveMemory) {
                /* Use the slot just after the mark to store X0 and G0. */
                x0 = H.s(1);
                g0 = H.y(1);
                H.mp = Math.min(H.mp,  H.m - 1);
            }
            x0.copyFrom(x1);
            g0.copyFrom(g1);
            g0norm = g1norm;
            f0 = f1;

            /* Estimate the length of the first step, start the line search
             * and take the first step along the search direction. */
            if (H.mp >= 1 || H.scaling == LBFGSOperator.USER_SCALING) {
                alpha = 1.0;
            } else if (0.0 < tiny && tiny < 1.0) {
                double xnorm = x1.norm2();
                if (xnorm > 0.0) {
                    alpha = (xnorm/g1norm)*tiny;
                } else {
                    alpha = 1.0/g1norm;
                }
            } else {
                alpha = 1.0/g1norm;
            }
            int status = lnsrch.start(f0, dg0, alpha, stpmin*alpha, stpmax*alpha);
            if (status != LineSearch.SEARCH) {
                return lineSearchFailure(status);
            }
            return nextStep(x1);
        }

        return task;
    }

    /** Build the new step to try as: x1 = x0 - alpha*p. */
    private OptimTask nextStep(Vector x1) {
        alpha = lnsrch.getStep();
        x1.axpby(1.0, x0, -alpha, p);
        projector.apply(x1, x1);
        reason = NO_PROBLEM;
        task = OptimTask.COMPUTE_FG;
        return task;
    }

    private OptimTask lineSearchFailure(int status) {
        if (lnsrch.hasWarnings()) {
            reason = LNSRCH_WARNING;
            task = OptimTask.WARNING;
        } else {
            reason = LNSRCH_ERROR;
            task = OptimTask.ERROR;
        }
        return task;
    }

    /**
     * Set the absolute tolerance for the convergence criterion.
     * @param gatol - Absolute tolerance for the convergence criterion.
     * @see {@link #setRelativeTolerance}, {@link #getAbsoluteTolerance},
     *      {@link #getGradientThreshold}.
     */
    public void setAbsoluteTolerance(double gatol) {
        this.gatol = gatol;
    }

    /**
     * Set the relative tolerance for the convergence criterion.
     * @param grtol - Relative tolerance for the convergence criterion.
     * @see {@link #setAbsoluteTolerance}, {@link #getRelativeTolerance},
     *      {@link #getGradientThreshold}.
     */
    public void setRelativeTolerance(double grtol) {
        this.grtol = grtol;
    }

    /**
     * Query the absolute tolerance for the convergence criterion.
     * @see {@link #setAbsoluteTolerance}, {@link #getRelativeTolerance},
     *      {@link #getGradientThreshold}.
     */
    public double getAbsoluteTolerance() {
        return gatol;
    }

    /**
     * Query the relative tolerance for the convergence criterion.
     * @see {@link #setRelativeTolerance}, {@link #getAbsoluteTolerance},
     *      {@link #getGradientThreshold}.
     */
    public double getRelativeTolerance() {
        return grtol;
    }

    /**
     * Query the gradient threshold for the convergence criterion.
     * 
     * The convergence of the optimization method is achieved when the
     * Euclidean norm of the gradient at a new iterate is less or equal
     * the threshold:
     * <pre>
     *    max(0.0, gatol, grtol*gtest)
     * </pre>
     * where {@code gtest} is the norm of the initial gradient, {@code gatol}
     * {@code grtol} are the absolute and relative tolerances for the
     * convergence criterion.
     * @return The gradient threshold.
     * @see {@link #setAbsoluteTolerance}, {@link #setRelativeTolerance},
     *      {@link #getAbsoluteTolerance}, {@link #getRelativeTolerance}.
     */
    public double getGradientThreshold() {
        return max(0.0, gatol, grtol*gtest);
    }

    private static final double max(double a1, double a2, double a3) {
        if (a3 >= a2) {
            return (a3 >= a1 ? a3 : a1);
        } else {
            return (a2 >= a1 ? a2 : a1);
        }
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
