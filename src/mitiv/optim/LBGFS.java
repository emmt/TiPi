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
 * Multivariate non-linear optimization by LBGFS/VMLM method.
 * 
 * @author Éric Thiébaut.
 *
 */
public class LBGFS implements ReverseCommunicationOptimizer {

    public static int NO_PROBLEM = 0;
    public static int BAD_H0 = 1; /* H0 is not positive definite */
    public static int LNSRCH_WARNING = 2; /* warning in line search */
    public static int LNSRCH_ERROR = 3; /* error in line search */

    LBGFSOperator H = null;
    LineSearch lnsrch;
    OptimTask task = null;
    int reason = NO_PROBLEM;

    int evaluations = 0;
    int iterations = 0;
    int restarts = 0;
    boolean starting = true;
    private double alpha; // current step length
    private double epsilon = 0.01;
    protected double tiny = 1e-3;
    private Vector p = null; // the (anti-)search direction
    private double pnorm; // the norm of P
    private double grtol;  /* Relative threshold for the norm or the gradient (relative
    to GTEST the norm of the initial gradient) for convergence. */
    private double gatol;  /* Absolute threshold for the norm or the gradient for
    convergence. */
    private double gtest;  /* Norm or the initial gradient. */

    private double stpmin = 1e-20;
    private double stpmax = 1e6;

    /* To save space, the variable and gradient at the start of a line
     * search are references to the (s,y) pair of vectors of the LBFGS
     * operator just after the mark.
     */
    private Vector x0 = null;
    private Vector g0 = null;

    private double g0norm, f0, dg0;

    public LBGFS(VectorSpace vsp, int m, LineSearch ls) {
        H = new LBGFSOperator(vsp, m);
        lnsrch = ls;
    }

    public LBGFS(LinearOperator H0, int m, LineSearch ls) {
        H = new LBGFSOperator(H0, m);
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
        // TODO Auto-generated method stub
        return 0;
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
        starting = true;
        task = OptimTask.COMPUTE_FG;
        return task;
    }

    @Override
    public OptimTask iterate(Vector x1, double f1, Vector g1) {
        double g1norm = 0.0;
        double pg1 = 0.0;
        if (task == OptimTask.COMPUTE_FG) {
            boolean checkConvergence;
            ++evaluations;
            if (starting) {
                checkConvergence = true;
            } else {
                /* Compute directional derivative and check whether line
                 * search has converged. */
                pg1 = p.dot(g1);
                int status = lnsrch.iterate(alpha, f1, -pg1);
                if (status == LineSearch.SEARCH) {
                    alpha = lnsrch.getStep();
                    checkConvergence = false;
                } else if (status == LineSearch.CONVERGENCE ||
                        status == LineSearch.WARNING_ROUNDING_ERRORS_PREVENT_PROGRESS) {
                    ++iterations;
                    checkConvergence = true;
                } else {
                    return lineSearchFailure(status);
                }
            }
            if (checkConvergence) {
                /* Check for global convergence. */
                g1norm = g1.norm2();
                if (iterations == 0) {
                    gtest = g1norm;
                }
                if (g1norm <= getGradientThreshold()) {
                    task = OptimTask.FINAL_X;
                } else {
                    task = OptimTask.NEW_X;
                }
                reason = NO_PROBLEM;
                return task;
            }
        } else if (task == OptimTask.NEW_X || task == OptimTask.FINAL_X) {
            /* Compute a search direction after updating the LBGFS matrix. */
            if (! starting) {
                H.update(x1, x0, g1, g0);
            }
            if (p == null) {
                p = H.getOutputSpace().create();
            }
            H.apply(g1, p);

            /* Check whether D = -P is a sufficient descent direction.
             * As shown by Zoutendijk, this is true if:
             *      cos(theta) = (D/|D|)'.(G1/|G1|) >= EPSILON > 0
             */
            while (true) {
                pnorm = p.norm2(); // FIXME: in some cases, can be just GNORM*GAMMA
                pg1 = p.dot(g1);
                if (pg1 >= epsilon*pnorm*g1norm) {
                    /* Accept P (resp. D = -P) as a sufficient ascent (resp.
                     * descent) direction. */
                    break;
                } else if (starting) {
                    /* The initial inverse Hessian approximation is
                     * not positive definite. */
                    task = OptimTask.ERROR;
                    reason = BAD_H0;
                    return task;
                } else {
                    /* Restart the LBFGS recursion and use H0 to compute
                     * a search direction. */
                    H.reset();
                    H.apply(g1, p);
                    ++restarts;
                    starting = true;
                }
            }

            /* Use the slot just after the mark to store the previous variables
               and gradient. */
            x0 = H.s(1);
            g0 = H.y(1);

            /* Store current position as X0, f0, etc. */
            x0.copyFrom(x1);
            g0.copyFrom(g1);
            g0norm = g1norm;
            f0 = f1;

            /* Estimate the length of the first step and start the
             * line search. */
            if (starting) {
                if (0.0 < tiny && tiny < 1.0) {
                    double xnorm = x1.norm2();
                    if (xnorm > 0.0) {
                        alpha = (xnorm/g1norm)*tiny;
                    } else {
                        alpha = 1.0/g1norm;
                    }
                } else {
                    alpha = 1.0/g1norm;
                }
            } else {
                alpha = 1.0;
            }
            dg0 = -pg1; // because D = -P and G0 = G1
            int status = lnsrch.start(f0, dg0, alpha,
                    stpmin*alpha, stpmax*alpha);
            if (status != LineSearch.SEARCH) {
                return lineSearchFailure(status);
            }
        } else {
            return task;
        }

        /* Build a new step to try. */
        x1.axpby(1.0, x0, -alpha, p);
        starting = false;
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
