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

import mitiv.linalg.LinearEndomorphism;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

/**
 * Multivariate non-linear optimization with simple bound constraints by
 * BLMVM method.
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
public class BLMVM extends ReverseCommunicationOptimizer {

    /** LBFGS approximation of the inverse Hessian */
    protected LBFGSOperator H = null;

    /** Relative threshold for the sufficient descent condition. */
    protected double delta = 0.01;

    /** Small relative size for the initial step or after a restart. */
    protected double epsilon = 1e-3;

    /**
     * Relative threshold for the norm or the projected gradient (relative to
     * the norm of the initial projected gradient) for global convergence.
     */
    protected double grtol;

    /**
     * Absolute threshold for the norm or the projected gradient for
     * global convergence.
     */
    protected double gatol;

    /**
     * The Euclidean norm or the initial projected gradient.
     */
    protected double pginit;

    /** The norm of the search direction. */
    protected double pnorm;

    /** Lower relative step bound. */
    protected double stpmin = 1e-20;

    /** Upper relative step bound. */
    protected double stpmax = 1e+20;

    /**
     * Parameter to control the convergence of the line search.
     */
    protected double sftol = 1e-2; // FIXME:

    protected double[] bnd = new double[2];

    /**
     * Attempt to save some memory?
     *
     * <p>
     * To save space, the variable and gradient at the start of a line search
     * may be references to the (s,y) pair of vectors of the LBFGS operator
     * just after the mark.
     * </p>
     */
    private final boolean saveMemory = true;

    /** Variables at the start of the line search. */
    protected Vector x0 = null;

    /** Function value at X0. */
    protected  double f0 = 0.0;

    /** Gradient at X0. */
    protected Vector g0 = null;

    /** The projected gradient at X0. */
    protected Vector pg0 = null;

    /**
     * The last projected gradient.
     */
    protected Vector pg = null;

    /**
     * The Euclidean norm of the last projected gradient.
     */
    protected double pgnorm = 0.0;

    /**
     * A temporary vector.
     */
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

    /** Projector to use to impose constraints. */
    protected final BoundProjector projector;

    public BLMVM(VectorSpace vsp, BoundProjector bp, int m) {
        this(new LBFGSOperator(vsp, m), bp);
    }

    public BLMVM(LinearEndomorphism H0, BoundProjector bp, int m) {
        this(new LBFGSOperator(H0, m), bp);
    }

    private BLMVM(LBFGSOperator H, BoundProjector bp) {
        super(H.getSpace());
        this.H = H;
        if (bp == null) {
            throw new IllegalArgumentException("Illegal null projector");
        }
        this.projector = bp;
        this.p = H.getSpace().create();
        if (! this.saveMemory) {
            this.x0 = H.getSpace().create();
            this.g0 = H.getSpace().create();
        }
        this.pg0 = H.getSpace().create();
        this.pg = H.getSpace().create();
        this.tmp = H.getSpace().create();
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
             * current point.   Project the gradient and check for global
             * convergence. */
            ++evaluations;
            projector.projectDirection(x, g, true, pg);
            pgnorm = pg.norm2();
            if (evaluations == 1) {
                pginit = pgnorm;
            }
            if (pgnorm <= max(0.0, gatol, grtol*pginit)) {
                /* Global convergence. */
                return success(OptimTask.FINAL_X);
            }
            if (evaluations == 1) {
                /* This is the first evaluation.  Return to caller with the
                 * initial solution. */
                return success(OptimTask.NEW_X);
            }
            /* A line search is in progress.  Compute directional
             * derivative and check whether line search has converged. */
            tmp.combine(1.0, x, -1.0, x0);
            if (f <= f0 + sftol*tmp.dot(g0)) {
                /* Line search has converged. */
                ++iterations;
                return success(OptimTask.NEW_X);
            }
            /* Reduce the step length and try a new point. */
            alpha /= 2.0; // FIXME: check alpha not too small and that s is significant
            return nextStep(x);

        case NEW_X:

            if (iterations >= 1) {
                /* Update the LBFGS matrix. */
                H.update(x, x0, pg, pg0);
            }

        case FINAL_X:

            /* Compute (anti-)search direction.  If it not a descent, reset
             * the Hessian approximation to take the projected steepest
             * descent. */
            while (true) {
                H.apply(p, g);
                projector.projectDirection(x, p, true, tmp, bnd);
                if (tmp.dot(g) > 0.0) {
                    /* Sufficient descent condition holds.  Estimate the
                     * length of the first step and break to proceed with
                     * first iterate along the new direction. */
                    alpha = min(initialStep(x, p.norm2()), bnd[1]);
                    break;
                }
                if (H.mp < 1) {
                    /* Initial iteration or recursion has just been
                     * restarted.  This means that the initial inverse
                     * Hessian approximation is not positive definite. */
                    return failure(OptimStatus.BAD_PRECONDITIONER);
                }
                /* Restart the LBFGS recursion and loop to use H0 for
                 * computing an initial search direction. */
                H.reset();
                ++restarts;
            }

            /* Save current variables X0, gradient G0, projected gradient PG0
             * and function value F0. */
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
            pg0.copy(pg);
            f0 = f;

            /* Take the first step along the search direction. */
            return nextStep(x);

        default:

            /* There must be something wrong. */
            return getTask();

        }
    }

    protected OptimTask nextStep(Vector x) {
        x.combine(1.0, x0, -alpha, p);
        projector.projectVariables(x);
        return success(OptimTask.COMPUTE_FG);
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
     * Retrieve the projected gradient for the last set of variables.
     * @return The projected gradient for the last set of variables
     *         tried by the method; {@code null} is returned if no
     *         evaluations have been performed.  The returned vector
     *         must be considered as <b>read-only</b>.
     */
    public Vector getProjectedGradient() {
        return (evaluations >= 1 ? pg : null);
    }

    /**
     * Retrieve the Euclidean norm of the projected gradient for the last set
     * of variables.
     *
     * @return The Euclidean norm of the projected gradient for the last set
     *         of variables tried by the method; {@code -1.0} is returned if
     *         no evaluations have been performed.
     */
    public double getProjectedGradientNorm2() {
        return (evaluations >= 1 ? pgnorm : -1.0);
    }

    private static final double max(double a1, double a2, double a3) {
        if (a2 < a3) {
            a2 = a3;
        }
        return (a1 >= a2 ? a1 : a2);
    }
    private static final double min(double a1, double a2) {
        return (a1 <= a2 ? a1 : a2);
    }

}
