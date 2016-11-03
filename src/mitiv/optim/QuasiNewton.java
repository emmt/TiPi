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
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

/**
 * Abstract super class for quasi-Newton methods.
 *
 * @author Éric Thiébaut.
 *
 */
abstract class QuasiNewton extends ReverseCommunicationOptimizerWithLineSearch {

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

    /** Relative threshold for the sufficient descent condition. */
    protected double epsilon = 0.0;

    /** Small relative size for the initial step or after a restart. */
    protected double delta = 1e-3;

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

    public QuasiNewton(VectorSpace space, LineSearch lnsrch) {
        super(space, lnsrch);
    }


    //    /** Build the new step to try as: x = x0 - alpha*p. */
    //    private OptimTask nextStep(Vector x) {
    //        alpha = lnsrch.getStep();
    //        x.combine(1.0, x0, -alpha, p);
    //        return success(OptimTask.COMPUTE_FG);
    //    }

    /**
     * Get the threshold to accept a descent direction.
     *
     * @return The threshold to accept a descent direction.
     */
    public double getSufficientDescentThreshold() {
        return epsilon;
    }

    /**
     * Set the threshold to accept a descent direction.
     *
     * <p> The L-BFGS recursion is used to compute a search direction
     * <code>d</code> which is accepted if it is a sufficient direction, that is
     * if: </p>
     *
     * <pre>
     * ⟨d,∇f(x)⟩ ≤ -ϵ⋅‖d‖⋅‖∇f(x)‖   if ϵ > 0     (1)
     * ⟨d,∇f(x)⟩ < 0                if ϵ = 0     (2)
     * </pre>
     *
     * <p> where <code>∇f(x)</code> is the current gradient and <code>ϵ</code>
     * is the sufficient descent threshold. If the search direction is rejected,
     * the steepest direction is used instead: <code>d = -∇f(x)</code>.</p>
     *
     * <p> As shown by Zoutendijk, if condition (1) holds then global
     * convergence is guaranteed. See Nocedal & Wright, "Numerical
     * Optimization", section 3.2, p. 44 (1999). We added condition (2) which,
     * thanks to the limited size of the smallest non-zero floating point value,
     * should be sufficient. </p>
     *
     * @param value
     *        The threshold to accept a descent direction, must be in [0,1).
     */
    public void setSufficientDescentThreshold(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)
                || value < 0.0 || value >= 1.0) {
            throw new IllegalArgumentException("Bad value for the sufficient descent threshold");
        }
        epsilon = value;
    }

    /**
     * Check the sufficient descent condition.
     *
     * @param dtg
     *        The scalar product <code>⟨d,∇f(x)⟩</code> with <code>d</code> the
     *        search direction and <code>∇f(x)</code> the gradient.
     *
     * @param gnorm
     *        The Euclidean norm of the gradient <code>‖∇f(x)‖</code>.
     *
     * @param d
     *        The search direction. It is only used if its Euclidean norm is
     *        required (thus it can also be the anti-search direction).
     *
     * @return A boolean value.
     *
     * @see #setSufficientDescentThreshold
     */
    boolean checkSufficientDescent(double dtg, double gnorm, Vector d) {
        if (epsilon > 0) {
            double r = epsilon*gnorm*d.norm2();
            if (r > 0) {
                return dtg <= -r;
            }
        }
        return dtg < 0;
    }

    /**
     * Check the sufficient descent condition.
     *
     * @param dtg
     *        The scalar product <code>⟨d,∇f(x)⟩</code> with <code>d</code> the
     *        search direction and <code>∇f(x)</code> the gradient.
     *
     * @param gnorm
     *        The Euclidean norm of the gradient <code>‖∇f(x)‖</code>.
     *
     * @param dnorm
     *        The Euclidean norm of the search direction.
     *
     * @return A boolean value.
     *
     * @see #setSufficientDescentThreshold
     */
    boolean checkSufficientDescent(double dtg, double gnorm, double dnorm) {
        if (epsilon > 0) {
            double r = epsilon*gnorm*dnorm;
            if (r > 0) {
                return dtg <= -r;
            }
        }
        return dtg < 0;
    }

    /**
     * Get the relative size for a small step.
     *
     * @return The relative size of a small step.
     */
    public double getRelativeSmallStep() {
        return delta;
    }

    /**
     * Set the relative size for a small step.
     *
     * @param value
     *        The relative size of a small step, must be strictly in (0,1).
     */
    public void setRelativeSmallStep(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)
                || value <= 0.0 || value >= 1.0) {
            throw new IllegalArgumentException("Bad value for relative small step size");
        }
        delta = value;
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
     *    max(0, gatol, grtol*g0nrm)
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
