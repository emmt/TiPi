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

/**
 * Base class for line search routines.
 *
 * <h2>Introduction and description</h2>
 *
 * <p>
 * Line search aims at approximately solving the problem:
 *
 * <pre>
 *     min f(x0 + alpha*d)
 * </pre>
 *
 * where the minimization is carried out with respect to the step length
 * <i>alpha</i>&nbsp;&gt;&nbsp;0, <i>x0</i> are the current variables, <i>d</i>
 * is the search direction and <i>f</i>(<i>x</i>) is a multivariate function.
 * The search direction <i>d</i> must be a descent direction, that is there
 * exists <i>epsilon</i> &gt; 0 such that for any <i>alpha</i> such that
 * 0&nbsp;&lt;&nbsp;<i>alpha</i>&nbsp;&lt;&nbsp;<i>epsilon</i>, the following
 * strict inequality holds:
 *
 * <pre>
 * f(x0 + alpha * d) < f(x0)
 * </pre>
 * </p>
 *
 * <p>
 * Line search is used in numerical optimization of smooth multivariate
 * functions. The other part of the optimization consists in choosing good
 * search directions. In this context, it is important to choose an efficient
 * step without too many function evaluations. To that end, <i>alpha</i> is
 * usually chosen so as to satisfy the so-called Wolfe conditions:
 *
 * <pre>
 *     f(x0 + alpha*d) - f(x0) <= sigma1*alpha*d'.g(x0)           (1)
 *     d'.g(x0 + alpha*d) >= sigma2*d'.g(x0)                      (2)
 * </pre>
 *
 * where <i>g</i>(<i>x</i>) is the gradient of <i>f</i>(<i>x</i>) with respect
 * to <i>x</i> and <i>d</i><sup>t</sup>.<i>g</i> denotes the inner product
 * between <i>d</i> and <i>g</i> while <i>sigma</i><sub>1</sub> and <i>sigma</i>
 * <sub>2</sub> are constants such that:
 *
 * <pre>
 * 0 < sigma1 < sigma2 < 1.
 * </pre>
 *
 * The first Wolfe condition (1) ensures a sufficient decrease of the function
 * and avoids making too long steps while the second Wolfe condition (2) ensures
 * a sufficient reduction of the gradient along the search direction thus
 * avoiding making too short steps. The first inequality (1) is also called the
 * Armijo condition. Sometimes, the strong Wolfe condition:
 *
 * <pre>
 *     |d'.g(x0 + alpha*d)| <= sigma2*|d'.g(x0)|                  (3)
 * </pre>
 *
 * is used in place of (2). The conjunction of (1) and (2) &#8212; or (1) and
 * (3) &#8212; with 0&nbsp;&lt;&nbsp;<i>sigma</i><sub>1</sub>&nbsp;&lt;&nbsp;
 * <i>sigma</i><sub>2</sub>&nbsp;&lt;&nbsp;1 warrants global convergence.
 * </p>
 *
 * <p>
 * Alternative conditions are the Goldstein conditions:
 *
 * <pre>
 *     (1 - sigma)*alpha*d'.g(x0) <= f(x0 + alpha*d) - f(x0) <= sigma*alpha*d'.g(x0)
 * </pre>
 *
 * with 0&nbsp;&lt;&nbsp;<i>sigma</i>&nbsp;&lt;&nbsp;1/2.
 * </p>
 *
 * <p>
 * Equivalently, defining:
 *
 * <pre>
 * phi(alpha) = f(x0 + alpha * d) - f(x0)
 * </pre>
 *
 * the problem amounts to:
 *
 * <pre>
 *    min phi(alpha)
 * </pre>
 *
 * with respect to <i>alpha</i>. For a smooth function <i>f</i>(<i>x</i>), the
 * derivative of <i>phi</i>(<i>alpha</i>) is:
 *
 * <pre>
 *    phi'(alpha) = d'.g(x0 + alpha*d)
 * </pre>
 *
 * and the Wolfe conditions write:
 *
 * <pre>
 *    phi(alpha) <= sigma1*phi'(0)*alpha
 *    phi'(alpha) >= sigma2*phi'(0)
 *    |phi'(alpha)| <= sigma2*|phi'(0)|
 * </pre>
 * </p>
 *
 * <p>
 * Rounding errors may prevent using the value of the function <i>f</i>(<i>x</i>
 * ), in this case, the "<i>approximate Wolfe conditions</i>" [2] may be used
 * instead of the Wolfe conditions:
 *
 * <pre>
 *   sigma2*d'.g(x0) <= d'.g(x0 + alpha*d) <= (2*sigma1 - 1)*d'.g(x0)
 * </pre>
 *
 * The first inequality is just the second Wolfe condition while the second
 * inequality is the first Wolfe condition when <i>f</i>(<i>x</i>) is quadratic
 * (and <i>alpha</i>&nbsp;&gt;&nbsp;0). Since <i>g</i>(<i>x0</i>)<sup>t</sup>.
 * <i>d</i>&nbsp;&lt;&nbsp;0, then 0&nbsp;&lt;&nbsp;<i>sigma</i><sub>1</sub>
 * &nbsp;&lt;&nbsp;min(<i>sigma</i><sub>2</sub>,1/2).
 *
 * <h2>Using line search</h2>
 *
 * <ol type="a">
 * <li>choose search direction <i>d</i> at <i>x0</i>;</li>
 * <li>compute <i>f0</i>&nbsp;=&nbsp;<i>f</i>(<i>x0</i>) and <i>g0</i>
 * &nbsp;=&nbsp;<i>g</i>(<i>x0</i>)<sup>t</sup>.<i>d</i>;</li>
 * <li>perform line search:
 *
 * <pre>
 *     SomeLineSearchClass lineSearch = new SomeLineSearchClass();
 *     double alpha = 1.0; // first step to try
 *     lineSearch.start(f0, g0, alpha, 0.0, 1E2*alpha);
 *     while (! lineSearch.finished()) {
 *        alpha = lineSearch.getStep();
 *        x1 = x0 + alpha*d;
 *        f1 = f(x1);
 *        g1 = d'.g(x1);
 *        lineSearch.next(alpha, f1, g1);
 *     }
 * </pre>
 *
 * or:
 *
 * <pre>
 *     SomeLineSearchClass lineSearch = new SomeLineSearchClass();
 *     double alpha = 1.0; // first step to try
 *     LineSearchTask task = lineSearch.start(f0, g0, alpha, 0.0, 1E2*alpha);
 *     while (task == LineSearch.SEARCH) {
 *        alpha = lineSearch.getStep();
 *        x1 = x0 + alpha*d;
 *        f1 = f(x1);
 *        g1 = d'.g(x1);
 *        task = lineSearch.next(alpha, f1, g1);
 *     }
 * </pre>
 *
 * </li>
 * </ol>
 *
 * <p>
 * Beware that you can have an infinite loop if you do not check for the finish
 * condition.
 * </p>
 *
 *
 * <h2>Rationale</h2>
 *
 * <p>
 * The line search routines only work on scalars, they do not store the
 * "<i>vectors</i>" <i>x0</i>, <i>d</i>, <i>g</i>(<i>x</i>), <i>etc</i>.
 * </p>
 *
 * <p>
 * When starting a line search, the initial parameters <i>x0</i>, function value
 * <i>f0</i>&nbsp;=&nbsp;<i>f</i>(<i>x0</i>), directional derivative <i>g0</i>
 * &nbsp;=&nbsp;<i>g</i>(<i>x0</i>)<sup>t</sup>.<i>d</i>, search direction
 * <i>d</i> and first step size must be known.
 * </p>
 *
 * <h2>References</h2>
 * <dl>
 * <dt>[1]</dt>
 * <dd>Nocedal, J. &amp; Wright, S.J., "<i>Numerical Optimization</i>", Springer
 * Verlag, (2006).</dd>
 * <dt>[2]</dt>
 * <dd>Hager, W.W. &amp; Zhang, H., "<i>A New Conjugate Gradient Method with
 * Guaranteed Descent and an Efficient Line Search</i>", SIAM, J. Optim.
 * <b>16</b>, 170-192 (2005).</dd>
 * </dl>
 *
 * @author &Eacute;ric Thi&eacute;baut
 *         <a href="mailto:eric.thiebaut@univ-lyon1.fr">eric.thiebaut@univ-lyon1
 *         .fr</a>
 *
 */
public abstract class LineSearch {

    protected double stp = 0.0; /* current step length */
    protected double stpmin = 0.0; /* lower bound for the step */
    protected double stpmax = 0.0; /* upper bound for the step */
    protected double finit = 0.0; /* function value at the start of the search */
    protected double ginit = 0.0; /* directional derivative value at the start of the search */

    /* Force derived classes to use success/failure/warning methods. */
    private LineSearchTask task;
    private OptimStatus status;

    protected LineSearch() {
        /* Set initial task and status. */
        failure(OptimStatus.NOT_STARTED);
    }

    /**
     * Start a new line search.
     *
     * @param f0
     *            Function value at {@code x0}, the start of the line search.
     * @param g0
     *            Directional derivative at {@code x0}, must be strictly
     *            negative.
     * @param nextStep
     *            Guess for the next step length (must be strictly greater than
     *            0 and this value will be returned by {@link #getStep} for the
     *            first iteration of the line search after clipping into the
     *            step bounds).
     * @param minStep
     *            The lower bound for the step length (must be greater or equal
     *            0).
     * @param maxStep
     *            The upper bound for the step length (must be strictly greater
     *            than {@code stepMin}).
     *
     * @return The state of the line search.
     */
    public LineSearchTask start(double f0, double g0, double nextStep,
            double minStep, double maxStep) {
        if (minStep < 0.0) {
            failure(OptimStatus.STPMIN_LT_ZERO);
        } else if (minStep > maxStep) {
            failure(OptimStatus.STPMIN_GT_STPMAX);
        } else if (nextStep < minStep) {
            failure(OptimStatus.STEP_LT_STPMIN);
        } else if (nextStep > maxStep) {
            failure(OptimStatus.STEP_GT_STPMAX);
        } else if (g0 >= 0.0) {
            failure(OptimStatus.NOT_A_DESCENT);
        } else {
            this.stp = nextStep;
            this.stpmin = minStep;
            this.stpmax = maxStep;
            this.finit = f0;
            this.ginit = g0;
            startHook();
        }
        return task;
    }

    /**
     * Perform a line search iteration.
     *
     * This method is used to submit the function value (and its directional
     * derivative) at the new position to try. Upon return, this method
     * indicates whether the line search has converged. Otherwise, it computes a
     * new step to try.
     *
     * @param alpha
     *            The value of the step (same as the value returned by
     *            {@link #getStep}).
     * @param f
     *            The value of the function at {@code x = x0 + alpha*p} where
     *            {@code x0} are the variables at the start of the line search
     *            and {@code p} is the search direction.
     * @param df
     *            The directional derivative at {@code x}, that is
     *            {@code p'.g(x)} the inner product between the search direction
     *            and the function gradient at {@code x}.
     *
     * @return The new status of the line search instance.
     */
    public LineSearchTask iterate(double alpha, double f, double df) {
        if (task == LineSearchTask.SEARCH) {
            if (alpha != stp) {
                failure(OptimStatus.STEP_CHANGED);
            } else {
                iterateHook(f, df);
                if (status == OptimStatus.SUCCESS) {
                    if (stp >= stpmax) {
                        if (stp >= stpmax) {
                            warning(OptimStatus.STEP_EQ_STPMAX);
                        }
                        stp = stpmax;
                    } else if (stp <= stpmin) {
                        if (stp <= stpmin) {
                            warning(OptimStatus.STEP_EQ_STPMIN);
                        }
                        stp = stpmin;
                    }
                }
            }
        } else {
            failure(OptimStatus.NOT_STARTED);
        }
        return task;
    }

    /**
     * Protected method to set internals at the start of a line search.
     *
     * This protected method is called by the {@link #start} method to set
     * attributes of the line search instance after starting a new search. It
     * can be overwritten as needed, the default method does nothing but set the
     * next task to be {@code LineSearch.SEARCH}.
     *
     * Derived line search classes which override this method should call @{link
     * success} or @{link failure} to set the next task on success or to set the
     * reason of the error on failure.
     */
    protected void startHook() {
        success(LineSearchTask.SEARCH);
    }

    /**
     * Protected abstract method to iterate during a line search.
     *
     * This protected method is called by the {@link #iterate} method to check
     * whether line search has converged and, otherwise, to compute the next
     * step to try (stored as attribute {@code stp}). The provided arguments
     * have been checked. Upon return, the caller method, {@link #iterate},
     * takes care of safeguarding the step.
     *
     * This method in derived line search classes must call @{link success}
     * or @{link failure} to set the next task on success or to set the reason
     * of the error on failure.
     *
     * @param f
     *            The value of the function at {@code x = x0 + stp*p} where
     *            {@code x0} are the variables at the start of the line search
     *            and {@code p} is the search direction.
     * @param df
     *            The directional derivative at {@code x}, that is
     *            {@code p'.g(x)} the inner product between the search direction
     *            and the function gradient at {@code x}.
     */
    protected abstract void iterateHook(double f, double df);

    /**
     * Get the current step length.
     *
     * This method should be called to query the value of the step to try during
     * a line search.
     *
     * @return The value of the step length.
     */
    public double getStep() {
        return stp;
    }

    /**
     * Get the current line search status.
     *
     * @return The line search status.
     */
    public final LineSearchTask getTask() {
        return task;
    }

    /**
     * Get the current line search status.
     *
     * @return The line search status.
     */
    public final OptimStatus getStatus() {
        return status;
    }

    /**
     * Set internal state to indicate a failure.
     *
     * @param status
     *            - The new line search status (must not be
     *            {@link OptimStatus.SUCCESS}).
     * @return The new line search task which has been set to
     *         {@link LineSearchTask.ERROR}.
     */
    protected final LineSearchTask failure(OptimStatus status) {
        this.status = status;
        this.task = LineSearchTask.ERROR;
        return this.task;
    }

    /**
     * Set internal state to indicate a non-fatal abnormal termination.
     *
     * @param task
     *            - The new line search status (must not be
     *            {@link OptimStatus.SUCCESS}).
     * @return The next line search task.
     */
    protected final LineSearchTask warning(OptimStatus status) {
        this.status = status;
        this.task = LineSearchTask.WARNING;
        return this.task;
    }

    /**
     * Set next line search task.
     *
     * @param task
     *            - The next line search task (must not be
     *            {@link LineSearchTask.ERROR}).
     * @return The next line search task.
     */
    protected final LineSearchTask success(LineSearchTask task) {
        this.status = OptimStatus.SUCCESS;
        this.task = task;
        return this.task;
    }

    /**
     * Get a literal description of the current line search status.
     *
     * @return A string describing the line search status.
     */
    public final String getMessage() {
        return (status == OptimStatus.SUCCESS ?
                task.toString() : status.toString());
    }

    /**
     * Check whether line search was stopped upon an error.
     */
    public final boolean hasErrors() {
        return task.isError();
    }

    /**
     * Check whether line search was stopped with a warning.
     */
    public final boolean hasWarnings() {
        return task.isWarning();
    }

    /**
     * Check whether line search has converged.
     */
    public final boolean converged() {
        return (task == LineSearchTask.CONVERGENCE);
    }

    /**
     * Check whether line search is finished (either because of convergence or
     * because of an error).
     */
    public final boolean finished() {
        return (task != LineSearchTask.SEARCH);
    }

    /**
     * Check line search convergence conditions.
     *
     * @param alpha
     *            - The current step length.
     * @param f
     *            - The current function value.
     * @param g
     *            - The current directional directional derivative.
     * @param finit
     *            - The function value at the start of the line search.
     * @param ginit
     *            - The directional derivative at the start of the line search
     *            (must be strictly negative).
     * @param ftol
     *            - The function tolerance parameter.
     * @param gtol
     *            - The derivative tolerance.
     *
     * @return 0 if the first Wolfe condition does not hold; 1 if the first
     *         condition holds but not the second one; 2 if the first and second
     *         weak Wolfe condition hold; 3 if the strong conditions hold.
     */
    static int checkWolfeConditions(double alpha, double f, double g,
            double finit, double ginit, double ftol, double gtol) {
        /* Check for first Wolfe condition. */
        if (f - finit > ftol * ginit * alpha) {
            return 0;
        }
        /* Check for second Wolfe conditions. */
        double gtest = gtol * ginit;
        if (g < gtest) {
            /* Only the first Wolfe condition is satisfied. */
            return 1;
        }
        if (Math.abs(g) > -gtest) {
            /* Only the weak second Wolfe condition is satisfied. */
            return 2;
        } else {
            return 3;
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
