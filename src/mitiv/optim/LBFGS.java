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
 * Multivariate non-linear optimization by L-BFGS/VMLM method.
 *
 * <p>
 * LBFGS implements a limited memory quasi-Newton method for unconstrained
 * optimization with Broyden-Fletcher-Goldfarb-Shanno (BGGS) updates using
 * Strang two-loop recursive formula  (Nocedal, 1980).
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
public class LBFGS implements ReverseCommunicationOptimizer {

    public static int NO_PROBLEMS = 0;
    public static int BAD_PRECONDITIONER = 1; /* B0 is not positive definite */
    public static int LNSRCH_WARNING = 2; /* warning in line search */
    public static int LNSRCH_ERROR = 3; /* error in line search */

    protected LBFGSOperator H = null; /* LBFGS approximation of the inverse Hessian */
    protected LineSearch lnsrch;
    protected OptimTask task = null;
    protected int reason = NO_PROBLEMS;

    /* Number of function (and gradient) evaluations since start. */
    protected int evaluations = 0;

    /* Number of iterations since start. */
    protected int iterations = 0;

    /* Number of restarts. */
    protected int restarts = 0;

    protected double delta = 0.01;
    protected double epsilon = 1e-3;
    protected double pnorm; // the norm of P
    protected double grtol;  /* Relative threshold for the norm or the gradient (relative
    to GTEST the norm of the initial gradient) for convergence. */
    protected double gatol;  /* Absolute threshold for the norm or the gradient for
    convergence. */
    protected double ginit;  /* Norm or the initial gradient. */

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

    /** Gradient at X0. */
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

    /** Euclidean norm of the gradient at the last accepted step. */
    protected double g1norm = 0.0;

    /** Euclidean norm of the gradient at X0. */
    protected  double g0norm = 0.0;


    public LBFGS(VectorSpace vsp, int m, LineSearch ls) {
        this(new LBFGSOperator(vsp, m), ls);
    }

    public LBFGS(LinearOperator H0, int m, LineSearch ls) {
        this(new LBFGSOperator(H0, m), ls);
    }

    private LBFGS(LBFGSOperator H, LineSearch ls) {
        this.H = H;
        this.p = H.getOutputSpace().create();
        if (! this.saveMemory) {
            this.x0 = H.getOutputSpace().create();
            this.g0 = H.getInputSpace().create();
        }
        this.lnsrch = ls;
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
        double gtest, pg1;
        int status;

        switch (task) {
        case COMPUTE_FG:

            /* Caller has computed the function value and the gradient at the
             * current point. */
            ++evaluations;
            if (evaluations > 1) {
                /* A line search is in progress.  Compute directional
                 * derivative and check whether line search has converged. */
                pg1 = p.dot(g1);
                status = lnsrch.iterate(alpha, f1, -pg1);
                if (status == LineSearch.SEARCH) {
                    return nextStep(x1);
                }
                if (status != LineSearch.CONVERGENCE &&
                        status != LineSearch.WARNING_ROUNDING_ERRORS_PREVENT_PROGRESS) {
                    return lineSearchFailure();
                }
                ++iterations;
            }

            /* The current step is acceptable. Check for global convergence. */
            g1norm = g1.norm2();
            if (evaluations == 1) {
                ginit = g1norm;
            }
            gtest = getGradientThreshold();
            return optimizerSuccess(g1norm <= gtest ? OptimTask.FINAL_X : OptimTask.NEW_X);

        case NEW_X:

            if (evaluations > 1) {
                /* Update the LBFGS matrix. */
                H.update(x1, x0, g1, g0);
            }

        case FINAL_X:

            /* Compute a search direction, possibly after updating the LBFGS
             * matrix.  We take care of checking whether D = -P is a
             * sufficient descent direction.  As shown by Zoutendijk, this is
             * true if: cos(theta) = -(D/|D|)'.(G/|G|) >= EPSILON > 0
             * where G is the gradient. */
            while (true) {
                H.apply(g1, p);
                pnorm = p.norm2(); // FIXME: in some cases, can be just GNORM*GAMMA
                pg1 = p.dot(g1);
                if (pg1 >= delta*pnorm*g1norm) {
                    /* Accept P (respectively D = -P) as a sufficient ascent
                     * (respectively descent) direction and set the directional
                     * derivative. */
                    dg0 = -pg1;
                    break;
                }
                if (H.mp < 1) {
                    /* Initial iteration or recursion has just been
                     * restarted.  This means that the initial inverse
                     * Hessian approximation is not positive definite. */
                    return optimizerFailure(BAD_PRECONDITIONER);
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
            if (H.mp >= 1 || H.rule == InverseHessianApproximation.BY_USER) {
                alpha = 1.0;
            } else if (0.0 < epsilon && epsilon < 1.0) {
                double x1norm = x1.norm2();
                if (x1norm > 0.0) {
                    alpha = (x1norm/g1norm)*epsilon;
                } else {
                    alpha = 1.0/g1norm;
                }
            } else {
                alpha = 1.0/g1norm;
            }
            status = lnsrch.start(f0, dg0, alpha, stpmin*alpha, stpmax*alpha);
            if (status != LineSearch.SEARCH) {
                return lineSearchFailure();
            }
            return nextStep(x1);

        default:

            /* There must be something wrong. */
            return task;
        }

    }

    /** Build the new step to try as: x1 = x0 - alpha*p. */
    private OptimTask nextStep(Vector x1) {
        alpha = lnsrch.getStep();
        x1.axpby(1.0, x0, -alpha, p);
        return optimizerSuccess(OptimTask.COMPUTE_FG);
    }

    private OptimTask lineSearchFailure() {
        if (lnsrch.hasWarnings()) {
            reason = LNSRCH_WARNING;
            task = OptimTask.WARNING;
        } else {
            reason = LNSRCH_ERROR;
            task = OptimTask.ERROR;
        }
        return task;
    }

    private OptimTask optimizerSuccess(OptimTask task) {
        this.reason = NO_PROBLEMS;
        this.task = task;
        return task;
    }

    private OptimTask optimizerFailure(int reason)
    {
        this.reason = reason;
        this.task = OptimTask.ERROR;
        return this.task;
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
