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
 * VMLMB method.
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
public class VMLMB extends ReverseCommunicationOptimizerWithLineSearch {

    /** LBFGS approximation of the inverse Hessian */
    private LBFGSOperator H = null;

    /** Relative threshold for the sufficient descent condition. */
    private double delta = 5e-2;

    /** Small relative size for the initial step or after a restart. */
    private double epsilon = 0.0;

    /**
     * Relative threshold for the norm or the gradient (relative to the norm
     * of the initial gradient) for convergence.
     */
    protected double grtol;

    /**
     * Absolute threshold for the norm or the gradient for convergence.
     */
    protected double gatol;

    /** Norm or the initial gradient. */
    protected double ginit;

    /** The norm of the search direction. */
    protected double pnorm;

    /** Lower relative step bound. */
    protected double stpmin = 1e-20;

    /** Upper relative step bound. */
    protected double stpmax = 1e+20;

    /**
     * Attempt to save some memory?
     *
     * <p>
     * To save space, the variable and gradient at the start of a line search
     * may be references to the `(s,y)` pair of vectors of the LBFGS operator
     * just after the mark.
     * </p>
     */
    private boolean saveMemory = true;

    /** Variables at the start of the line search. */
    protected Vector x0 = null;

    /** Function value at X0. */
    protected  double f0 = 0.0;

    /** Gradient at X0. */
    protected Vector g0 = null;

    protected Vector tmp = null;

    /**
     * The (anti-)search direction.
     *
     * <p>
     * An iterate is computed as: x = x0 - alpha*p with alpha > 0.
     * </p>
     */
    protected Vector p = null;

    /** The current step length. */
    protected double alpha;

    /** Directional derivative at X0. */
    protected  double dg0 = 0.0;

    /** Euclidean norm of the gradient at the last accepted step. */
    protected double gnorm = 0.0;

    /** Euclidean norm of the gradient at X0. */
    protected  double g0norm = 0.0;

    /** Projector to use to impose constraints. */
    protected final BoundProjector projector;

    public VMLMB(VectorSpace vsp, BoundProjector bp, int m, LineSearch ls) {
        this(new LBFGSOperator(vsp, m), bp, ls);
    }

    public VMLMB(LinearOperator H0, BoundProjector bp, int m, LineSearch ls) {
        this(new LBFGSOperator(H0, m), bp, ls);
    }

    private VMLMB(LBFGSOperator H, BoundProjector bp, LineSearch ls) {
        this.H = H;
        this.projector = bp;
        this.p = H.getOutputSpace().create();
        if (! this.saveMemory) {
            this.x0 = H.getOutputSpace().create();
            this.g0 = H.getInputSpace().create();
        }
        this.lnsrch = ls;
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
        return success(OptimTask.COMPUTE_FG);
    }

    @Override
    public OptimTask iterate(Vector x, double f, Vector g) {

        switch (getTask()) {

        case COMPUTE_FG:

            /* Caller has computed the function value and the gradient at the
             * current point. */
            if (projector != null) {
                projector.projectDirection(x, g, true, g);
            }
            ++evaluations;
            if (evaluations > 1) {
                /* A line search is in progress.  Compute directional
                 * derivative and check whether line search has converged. */
                LineSearchTask lnsrchTask = lnsrch.iterate(alpha, f, -p.dot(g));
                if (lnsrchTask == LineSearchTask.SEARCH) {
                    return nextStep(x);
                } else if (lnsrchTask != LineSearchTask.CONVERGENCE) {
                    OptimStatus lnsrchStatus = lnsrch.getStatus();
                    if (lnsrchTask != LineSearchTask.WARNING ||
                            lnsrchStatus != OptimStatus.ROUNDING_ERRORS_PREVENT_PROGRESS) {
                        return failure(lnsrchStatus);
                    }
                }
                ++iterations;
            }

            /* The current step is acceptable. Check for global convergence. */
            gnorm = g.norm2();
            if (evaluations == 1) {
                ginit = gnorm;
            }
            double gtest = getGradientThreshold();
            return success(gnorm <= gtest ? OptimTask.FINAL_X : OptimTask.NEW_X);

        case NEW_X:

            if (evaluations > 1) {
                /* Update the LBFGS matrix. */
                H.update(x, x0, g, g0);
            }

        case FINAL_X:

            /* Compute a search direction, possibly after updating the LBFGS
             * matrix.  We take care of checking whether D = -P is a
             * sufficient descent direction.  As shown by Zoutendijk, this is
             * true if: cos(theta) = -(D/|D|)'.(G/|G|) >= DELTA > 0
             * where G is the gradient. */
            while (true) {
                H.apply(g, p);
                pnorm = p.norm2(); // FIXME: in some cases, can be just GNORM*GAMMA
                double pg = p.dot(g);
                if (pg >= delta*pnorm*gnorm) {
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
                    return failure(OptimStatus.BAD_PRECONDITIONER);
                }
                /* Restart the LBFGS recursion and loop to use H0 to compute
                 * an initial search direction. */
                H.reset();
                ++restarts;
            }

            /* Save current variables X0, gradient G0 and function value F0. */
            if (saveMemory) {
                /* Use the slot just after the mark to store X0 and G0. */
                x0 = H.s(0);
                g0 = H.y(0);
                if (H.mp == H.m) {
                    --H.mp;
                }
            }
            x0.copyFrom(x);
            g0.copyFrom(g);
            g0norm = gnorm;
            f0 = f;

            /* Estimate the length of the first step, start the line search
             * and take the first step along the search direction. */
            if (H.mp >= 1 || H.rule == LBFGSOperator.NO_SCALING) {
                alpha = 1.0;
            } else {
                alpha = initialStep(x0, p);
            }
            double amin, amax;
            x.combine(1.0, x0, -alpha, p);
            if (projector == null) {
                /* Unconstrained optimization. */
                amin = stpmin*alpha;
                amax = stpmax*alpha;
            } else {
                /* Project the first guess and set line search bounds to only
                 * do backtracking. */
                if (tmp == null) {
                    tmp = x.getOwner().create();
                }
                //for (;;) {
                //    tmp.combine(1.0, x0, -1.0, x);
                //    projector.projectVariables(x, x);
                //    dg0 = -tmp.dot(g0);
                //    if (dg0 < 0.0) break; // FIXME: tolerance?
                //    if (dg0 == 0.0) {
                //        return schedule(OptimTask.FINAL_X);
                //    }
                //    alpha *= 0.5;
                //    x.combine(1.0, x0, -alpha, p);
                //}
                /* Compute a anti-search direction P.  We take care of checking
                 * whether D = -P is a sufficient descent direction.  As shown by
                 * Zoutendijk, this is true if: -(D/|D|)'.(G/|G|) >= DELTA > 0
                 * where G is the gradient.  Below, R = DELTA*|D|*|G|.
                 * See Nocedal & Wright, "Numerical Optimization", section 3.2,
                 * p. 44 (1999). */
                while (true) {
                    projector.projectVariables(x, x);
                    tmp.combine(1.0, x0, -1.0, x);
                    dg0 = -tmp.dot(g0);
                    if (dg0 < 0.0) break; // FIXME: tolerance?
                    if (dg0 == 0.0 && H.mp < 1) {
                        return success(OptimTask.FINAL_X);
                    }
                    if (H.mp >= 1) {
                        /* Restart the LBFGS recursion and loop to use H0 to compute
                         * an initial search direction. */
                        H.reset();
                        ++restarts;
                        H.apply(g, p);
                        pnorm = p.norm2();
                        // FIXME: check p.g0
                        alpha = initialStep(x0, p);
                    } else {
                        alpha *= 0.5;
                    }
                    x.combine(1.0, x0, -alpha, p);
                }
                p.copyFrom(tmp);
                alpha = 1.0;
                amin = stpmin;
                amax = 1.0;
            }
            LineSearchTask lnsrchTask = lnsrch.start(f0, dg0, alpha, amin, amax);
            if (lnsrchTask != LineSearchTask.SEARCH) {
                return failure(lnsrch.getStatus());
            }
            return success(OptimTask.COMPUTE_FG);

        default:

            /* There must be something wrong. */
            return getTask();

        }
    }

    protected double initialStep(Vector x, Vector d) {
        double dnorm = d.norm2();
        if (0.0 < epsilon && epsilon < 1.0) {
            double xnorm = x.norm2();
            if (xnorm > 0.0) {
                return (xnorm/dnorm)*epsilon;
            }
        }
        return 1.0/dnorm;
    }

    /** Build the new step to try as: x = Proj(x0 - alpha*p). */
    private OptimTask nextStep(Vector x) {
        alpha = lnsrch.getStep();
        x.combine(1.0, x0, -alpha, p);
        if (projector != null) {
            projector.projectVariables(x, x);
        }
        return success(OptimTask.COMPUTE_FG);
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
        return max(0.0, gatol, grtol*ginit);
    }

    private static final double max(double a1, double a2, double a3) {
        if (a3 >= a2) {
            return (a3 >= a1 ? a3 : a1);
        } else {
            return (a2 >= a1 ? a2 : a1);
        }
    }
}
