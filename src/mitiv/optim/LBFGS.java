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

import mitiv.base.Traits;
import mitiv.linalg.LinearEndomorphism;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

/**
 * Multivariate non-linear optimization by L-BFGS/VMLM method.
 *
 * <p>
 * LBFGS implements a limited memory quasi-Newton method for unconstrained
 * optimization with Broyden-Fletcher-Goldfarb-Shanno (BFGS) updates using
 * Strang's two-loop recursive formula (Nocedal, 1980).
 * </p>
 *
 * <p>
 * Combined with a Moré & Thuente (1984) line search, the implemented method is
 * similar to VMLM (Nocedal, 1980) or L-BFGS (Liu & Nocedal, 1989) algorithms.
 * </p>
 *
 * <h3>References</h3>
 *  <ul>
 *  <li>Nocedal, J. "<i>Updating Quasi-Newton Matrices with Limited Storage</i>,"
 *      Mathematics of Computation <b>35</b>, pp.&nbsp;773-782 (1980).</li>
 *  <li>Moré, J. J. & Thuente, D. J. "<i>Line search algorithms with guaranteed
 *      sufficient decrease</i>," TOMS, ACM Press <b>20</b>, pp.&nbsp;286-307
 *      (1994).</li>
 *  <li>Liu, D. C. & Nocedal, J. "<i>On the limited memory BFGS method for
 *      large scale optimization</i>," Mathematical programming <b>45</b>,
 *      pp.&nbsp;503-528 (1989).</li>
 *  </ul>

 * @author Éric Thiébaut.
 *
 */
public class LBFGS extends ReverseCommunicationOptimizerWithLineSearch {

    /**
     * Default value for {@code ftol} parameter in More & Thuente line
     * search.
     */
    static public final double SFTOL = 1.0e-4;

    /**
     * Default value for {@code gtol} parameter in More & Thuente line
     * search.
     */
    static public final double SGTOL = 0.9;

    /**
     * Default value for {@code xtol} parameter in More & Thuente line
     * search.
     */
    static public final double SXTOL = Traits.DBL_EPSILON;

    /** LBFGS approximation of the inverse Hessian */
    protected LBFGSOperator H = null;

    /** Relative threshold for the sufficient descent condition. */
    protected double delta = 0.01;

    /** Small relative size for the initial step or after a restart. */
    protected double epsilon = 1e-3;

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

    /** Lower relative step bound. */
    protected double stpmin = 1e-20;

    /** Upper relative step bound. */
    protected double stpmax = 1e+20;

    /**
     * Attempt to save some memory?
     *
     * <p> To save space, the variable and gradient at the start of a line
     * search may be references to the (s,y) pair of vectors of the LBFGS
     * operator just after the mark.  </p>
     */
    private final boolean saveMemory = true;

    /** Variables at the start of the line search. */
    protected Vector x0 = null;

    /** Function value at X0. */
    protected  double f0 = 0.0;

    /** Gradient at X0. */
    protected Vector g0 = null;

    /**
     * The (anti-)search direction.
     *
     * <p> An iterate is computed as: {@code x = x0 - alpha*p} with
     * {@code alpha > 0}. </p>
     */
    protected Vector p = null;

    /** The current step length. */
    protected double alpha;

    /** Directional derivative at X0. */
    protected  double dg0 = 0.0;

    /** Euclidean norm of the gradient at the last accepted step. */
    protected double gnorm = 0.0;

    public LBFGS(VectorSpace space, int m) {
        this(new LBFGSOperator(space, m),
                new MoreThuenteLineSearch(SFTOL, SGTOL, SXTOL));
    }

    public LBFGS(VectorSpace space, int m, LineSearch lnsrch) {
        this(new LBFGSOperator(space, m), lnsrch);
    }

    public LBFGS(LinearEndomorphism H0, int m, LineSearch lnsrch) {
        this(new LBFGSOperator(H0, m), lnsrch);
    }

    private LBFGS(LBFGSOperator H, LineSearch lnsrch) {
        super(H.getSpace(), lnsrch);
        this.H = H;
        this.p = H.getSpace().create();
        if (! this.saveMemory) {
            this.x0 = H.getSpace().create();
            this.g0 = H.getSpace().create();
        }
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
            ++evaluations;
            if (evaluations > 1) {
                /* A line search is in progress.  Compute directional
                 * derivative and check whether line search has converged. */
                final LineSearchTask lnsrchTask = lnsrch.iterate(f, -p.dot(g));
                if (lnsrchTask == LineSearchTask.SEARCH) {
                    return nextStep(x);
                } else if (lnsrchTask != LineSearchTask.CONVERGENCE) {
                    final OptimStatus lnsrchStatus = lnsrch.getStatus();
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
            final double gtest = getGradientThreshold(ginit);
            return success(gnorm <= gtest ? OptimTask.FINAL_X : OptimTask.NEW_X);

        case NEW_X:
        case FINAL_X:

            if (iterations >= 1) {
                /* Update the LBFGS matrix. */
                H.update(x, x0, g, g0);
            }

            /* Compute a anti-search direction P.  We take care of checking
             * whether D = -P is a sufficient descent direction.  As shown by
             * Zoutendijk, this is true if: -(D/|D|)'.(G/|G|) >= DELTA > 0
             * where G is the gradient.  Below, R = DELTA*|D|*|G|.
             * See Nocedal & Wright, "Numerical Optimization", section 3.2,
             * p. 44 (1999). */
            while (true) {
                H.apply(p, g);
                dg0 = -p.dot(g);
                double r = (delta > 0.0 ? delta*gnorm*p.norm2() : 0.0);
                if (r > 0.0 ? (dg0 <= -r) : (dg0 < 0.0)) {
                    /* Sufficient descent condition holds.  Estimate the
                     * length of the first step and break to proceed with
                     * first iterate along the new direction. */
                    alpha = initialStep(x, gnorm);
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
            x0.copy(x);
            g0.copy(g);
            f0 = f;

            /* Start the line search. */
            LineSearchTask lnsrchTask = lnsrch.start(f0, dg0, alpha, stpmin*alpha, stpmax*alpha);
            if (lnsrchTask != LineSearchTask.SEARCH) {
                return failure(lnsrch.getStatus());
            }

            /* Take the first step along the search direction. */
            return nextStep(x);

        default:

            /* There must be something wrong. */
            return getTask();

        }

    }

    protected double initialStep(Vector x, double dnorm) {
        if (H.mp >= 1 || H.rule == LBFGSOperator.NO_SCALING) {
            return 1.0;
        }
        if (0.0 < epsilon && epsilon < 1.0) {
            final double xnorm = x.norm2();
            if (xnorm > 0.0) {
                return (xnorm/dnorm)*epsilon;
            }
        }
        return 1.0/dnorm;
    }

    /** Build the new step to try as: x = x0 - alpha*p. */
    private OptimTask nextStep(Vector x) {
        alpha = lnsrch.getStep();
        x.combine(1.0, x0, -alpha, p);
        return success(OptimTask.COMPUTE_FG);
    }

    /**
     * Set the absolute tolerance for the convergence criterion.
     *
     * @param gatol
     *        Absolute tolerance for the convergence criterion.
     *
     * @see #setRelativeTolerance(double)
     * @see #getAbsoluteTolerance()
     * @see #getGradientThreshold(double)
     */
    public void setAbsoluteTolerance(double gatol) {
        this.gatol = gatol;
    }

    /**
     * Set the relative tolerance for the convergence criterion.
     *
     * @param grtol
     *        Relative tolerance for the convergence criterion.
     *
     * @see #setAbsoluteTolerance(double)
     * @see #getRelativeTolerance()
     * @see #getGradientThreshold(double)
     */
    public void setRelativeTolerance(double grtol) {
        this.grtol = grtol;
    }

    /**
     * Query the absolute tolerance for the convergence criterion.
     *
     * @see #setAbsoluteTolerance(double)
     * @see #getRelativeTolerance()
     * @see #getGradientThreshold(double)
     */
    public double getAbsoluteTolerance() {
        return gatol;
    }

    /**
     * Query the relative tolerance for the convergence criterion.
     *
     * @see #setRelativeTolerance(double)
     * @see #getAbsoluteTolerance()
     * @see #getGradientThreshold(double)
     */
    public double getRelativeTolerance() {
        return grtol;
    }

    /**
     * Query the gradient threshold for the convergence criterion.
     *
     * <p> The convergence of the optimization method is achieved when the
     * Euclidean norm of the gradient at a new iterate is less or equal the
     * threshold: </p>
     *
     * <pre>
     *    max(0.0, gatol, grtol*g0nrm)
     * </pre>
     *
     * <p> where {@code gtest} is the norm of the initial gradient, {@code
     * gatol} {@code grtol} are the absolute and relative tolerances for the
     * convergence criterion. </p>
     *
     * @param g0nrm
     *        The norm of the initial gradient.
     *
     * @return The gradient threshold.
     *
     * @see #setAbsoluteTolerance(double)
     * @see #setRelativeTolerance(double)
     * @see #getAbsoluteTolerance()
     * @see #getRelativeTolerance()
     */
    public double getGradientThreshold(double g0nrm) {
        return max(0.0, gatol, grtol*g0nrm);
    }

    private static final double max(double a1, double a2, double a3) {
        if (a3 >= a2) {
            return (a3 >= a1 ? a3 : a1);
        } else {
            return (a2 >= a1 ? a2 : a1);
        }
    }

}
