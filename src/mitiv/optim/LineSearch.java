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
 * <pre>
 *     min f(x0 + alpha*p)
 * </pre>
 * where the minimization is carried out with respect to the step length
 * {@code alpha > 0}, {@code x0} are the current variables, {@code p} is
 * the search direction and {@code f(x)} is a multivariate function.  The
 * search direction {@code p} must be a descent direction, that is there
 * exists {@code epsilon > 0} such that for any {@code alpha} such that
 * {@code 0 < alpha < epsilon}:
 * <pre>
 *     f(x0 + alpha*p) < f(x0)
 * </pre>
 * </p>
 *
 * <p>
 * Line search is used in numerical optimization of smooth multivariate
 * functions.  The other part of the optimization consists in choosing
 * good search directions.  In this context, it is important to choose an
 * efficient step without too many function evaluations.  To that end,
 * alpha is usually chosen so as to satisfy the so-called Wolfe
 * conditions:
 * <pre>
 *     f(x0 + alpha*p) - f(x0) <= sigma1*alpha*p'.g(x0)           (1)
 *     p'.g(x0 + alpha*p) >= sigma2*p'.g(x0)                      (2)
 * </pre>
 * where {@code g(x)} is the gradient of {@code f(x)} with respect to
 * {@code x} and {@code p'.g} denotes the inner product between {@code p}
 * and {@code g} while {@code sigma1} and {@code sigma2} are constants
 * such that:
 * <pre>
 *     0 < sigma1 < sigma2 < 1.
 * </pre>
 * The first Wolfe condition (1) ensures a sufficient decrease of the function
 * and avoids making too long steps while the second Wolfe condition (2)
 * ensures a sufficient reduction of the gradient along the search direction
 * thus avoiding making too short steps.  The first inequality (1) is
 * also called the Armijo condition.  Sometimes, the strong Wolfe condition:
 * <pre>
 *     |p'.g(x0 + alpha*p)| <= sigma2*|p'.g(x0)|                  (3)
 * </pre>
 * is used in place of (2).  The conjunction of (1) and (2) --- or (1) and (3) ---
 * with {@code 0 < sigma1 < sigma2 < 1} warrants global convergence.
 * </p>
 *
 * <p>
 * Alternative conditions are the Goldstein conditions:
 * <pre>
 *     (1 - sigma)*alpha*p'.g(x0) <= f(x0 + alpha*p) - f(x0) <= sigma*alpha*p'.g(x0)
 * </pre>
 * with {@code 0 < sigma < 1/2}.
 * </p>
 *
 * <p>
 * Equivalently, defining:
 * <pre>
 *    phi(alpha) = f(x0 + alpha*p) - f(x0)
 * </pre>
 * the problem amounts to:
 * <pre>
 *    min phi(alpha)
 * </pre>
 * For a smooth function {@code f(x)}, the derivative of {@code phi(alpha)} is:
 * <pre>
 *    phi'(alpha) = p'.g(x0 + alpha*p)
 * </pre>
 * so the Wolfe conditions write:
 * <pre>
 *    phi(alpha) <= sigma1*phi'(0)*alpha
 *    phi'(alpha) >= sigma2*phi'(0)
 *    |phi(alpha)| <= sigma2*|phi'(0)|
 * </pre>
 * </p>
 * 
 * <h2>Using line search<h2>
 * 
 * <ol type="a">
 *   <li>choose search direction {@code p} at {@code x0};</li>
 *   <li>compute {@code f0 = f(x0)} and {@code g0 = p'.g(x0)};</li>
 *   <li>perform line search:
 *     <pre> 
 *     SomeLineSearchClass lineSearch = new SomeLineSearchClass();
 *     double alpha = 1.0; // first step to try
 *     lineSearch.start(f0, g0, alpha, 0.0, 1E2*alpha);
 *     while (! lineSearch.finished()) {
 *        alpha = lineSearch.getStep();
 *        x1 = x0 + alpha*p;
 *        f1 = f(x1);
 *        g1 = p'.g(x1);
 *        lineSearch.next(alpha, f1, g1);
 *     }
 *     </pre>
 *     or:
 *     <pre>
 *     SomeLineSearchClass lineSearch = new SomeLineSearchClass();
 *     double alpha = 1.0; // first step to try
 *     int status = lineSearch.start(f0, g0, alpha, 0.0, 1E2*alpha);
 *     while (status == LineSearch.SEARCH) {
 *        alpha = lineSearch.getStep();
 *        x1 = x0 + alpha*p;
 *        f1 = f(x1);
 *        g1 = p'.g(x1);
 *        status = lineSearch.next(alpha, f1, g1);
 *     }
 *     </pre>
 *   </li>
 * </ol>
 *
 * Beware that you can have an infinite loop if you do not check for the
 * finish condition.
 *
 *
 * <h2>Rationale</h2>
 * 
 * The line search routines only work on scalars, they do not store the
 * "<i>vectors</i>" {@code x0}, {@code p}, {@code g(x)}, <i>etc</i>.
 *
 * When starting a line search, the initial parameters {@code x0}, function value
 * {@code f0 = f(x0)}, directional derivative {@code g0 = g(x0)'.p}, search direction
 * {@code p} and first step size must be known.
 *
 * @author &Eacute;ric Thi&eacute;baut <a href="mailto:eric.thiebaut@univ-lyon1.fr">eric.thiebaut@univ-lyon1.fr</a>
 *
 */
public abstract class LineSearch {

    public static final int ERROR_ILLEGAL_ADDRESS                    = -12; // FIXME: unused
    public static final int ERROR_CORRUPTED_WORKSPACE                = -11; // FIXME: unused
    public static final int ERROR_BAD_WORKSPACE                      = -10; // FIXME: unused
    public static final int ERROR_STP_CHANGED                        =  -9;
    public static final int ERROR_STP_OUTSIDE_BRACKET                =  -8;
    public static final int ERROR_NOT_A_DESCENT                      =  -7;
    public static final int ERROR_STPMIN_GT_STPMAX                   =  -6;
    public static final int ERROR_STPMIN_LT_ZERO                     =  -5;
    public static final int ERROR_STP_LT_STPMIN                      =  -4;
    public static final int ERROR_STP_GT_STPMAX                      =  -3;
    public static final int ERROR_INITIAL_DERIVATIVE_GE_ZERO         =  -2;
    public static final int ERROR_NOT_STARTED                        =  -1;
    public static final int SEARCH                                   =   0;
    public static final int CONVERGENCE                              =   1;
    public static final int WARNING_ROUNDING_ERRORS_PREVENT_PROGRESS =   2;
    public static final int WARNING_XTOL_TEST_SATISFIED              =   3;
    public static final int WARNING_STP_EQ_STPMAX                    =   4;
    public static final int WARNING_STP_EQ_STPMIN                    =   5;

    protected double stp = 0.0;    /* current step length */
    protected double stpmin = 0.0; /* lower bound for the step */
    protected double stpmax = 0.0; /* upper bound for the step */
    protected double finit = 0.0;  /* function value at the start of the search */
    protected double ginit = 0.0;  /* directional derivative value at the start of the search */

    protected int status = ERROR_NOT_STARTED;

    protected LineSearch() {
    }

    /**
     * Start a new line search.
     *
     * @param f0         Function value at {@code x0}, the start of the line search.
     * @param g0         Directional derivative at {@code x0}, must be strictly negative.
     * @param nextStep   Guess for the next step length (must be strictly greater
     *                   than 0 and this value will be returned by {@link #getStep}
     *                   for the first iteration of the line search after clipping
     *                   into the step bounds).
     * @param stepMin    The lower bound for the step length (must be greater or
     *                   equal 0).
     * @param stepMax    The upper bound for the step length (must be strictly greater
     *                   than {@code stepMin}).
     * 
     * @return The state of the line search.
     */
    public int start(double f0, double g0, double nextStep, double stepMin, double stepMax)
    {
        if (stepMin < 0.0) {
            status = ERROR_STPMIN_LT_ZERO;
        } else if (stepMin > stepMax) {
            status = ERROR_STPMIN_GT_STPMAX;
        } else if (nextStep < stepMin) {
            status = ERROR_STP_LT_STPMIN;
        } else if (nextStep > stepMax) {
            status = ERROR_STP_GT_STPMAX;
        } else if (g0 >= 0.0) {
            status = ERROR_INITIAL_DERIVATIVE_GE_ZERO;
        } else {
            this.stp = nextStep;
            this.stpmin = stepMin;
            this.stpmax = stepMax;
            this.finit = f0;
            this.ginit = g0;
            status = startHook();
        }
        return status;
    }

    /**
     * Perform a line search iteration.
     * 
     * This method is used to submit the function value (and its directional derivative)
     * at the new position to try.  Upon return, this method indicates whether the line
     * search has converged.  Otherwise, it computes a new step to try.
     * 
     * @param s1   The value of the step (same as the value returned by {@link #getStep}). 
     * @param f1   The value of the function at {@code x1 = x0 + s1*p} where {@code x0}
     *             are the variables at the start of the line search and {@code p} is the
     *             search direction.
     * @param g1   The directional derivative at {@code x1}, that is {@code p'.g(x1)} the
     *             inner product between the search direction and the function gradient at
     *             {@code x1}.
     *
     * @return The new status of the line search instance.
     */
    public int iterate(double s1, double f1, double g1)
    {
      if (status == SEARCH) {
        if (s1 != stp) {
          status = ERROR_STP_CHANGED;
        } else {
          status = iterateHook(s1, f1, g1);
          if (stp >= stpmax) {
            if (s1 >= stpmax) {
              status = WARNING_STP_EQ_STPMAX;
            }
            stp = stpmax;
          } else if (stp <= stpmin) {
            if (s1 <= stpmin) {
              status = WARNING_STP_EQ_STPMIN;
            }
            stp = stpmin;
          }
        }
      } else {
        status = ERROR_NOT_STARTED;
      }
      return status;
    }

    /**
     * Protected method to set internals at the start of a line search.
     * 
     * This protected method is called by the {@link #start} method to set attributes of the
     * line search instance after starting a new search.  It can be overwritten as needed,
     * the default method does nothing but return {@code LineSearch.SEARCH}.
     * 
     * @return The new status of the line search instance, in principle {@code LineSearch.SEARCH}.
     */
    protected int startHook()
    {
        return SEARCH;
    }

    /**
     * Protected abstract method to iterate during a line search.
     * 
     * This protected method is called by the {@link #iterate} method to check whether line
     * search has converged and, otherwise, to compute the next step to try (stored
     * as attribute {@code stp}).  The provided arguments have been checked.  Upon return,
     * the caller method, {@link #iterate}, takes care of safeguarding the step.
     * 
     * @param s1   The value of the step (same as the value returned by {@link #getStep}). 
     * @param f1   The value of the function at {@code x1 = x0 + s1*p} where {@code x0}
     *             are the variables at the start of the line search and {@code p} is the
     *             search direction.
     * @param g1   The directional derivative at {@code x1}, that is {@code p'.g(x1)} the
     *             inner product between the search direction and the function gradient at
     *             {@code x1}.
     * 
     * @return The new status of the line search instance.
     */
    protected abstract int iterateHook(double s1, double f1, double g1);

    /**
     * Get the current step length.
     * 
     * This method should be called to query the value of the step to try
     * during a line search.
     * 
     * @return The value of the step length.
     */
    public double getStep()
    {
        return stp;
    }

    /**
     * Get the current line search status.
     * @return The line search status.
     */
    public final int getStatus()
    {
        return status;
    }

    /**
     * Get a literal description of a line search status.
     * @param code  A line search status (e.g. as returned by {@link #getStatus}).
     * @return A string describing the line search status.
     */
    public final String getMessage(int code)
    {
        switch(code) {
        case ERROR_ILLEGAL_ADDRESS:
            return "Illegal address";
        case ERROR_CORRUPTED_WORKSPACE:
            return "Corrupted workspace";
        case ERROR_BAD_WORKSPACE:
            return "Bad workspace";
        case ERROR_STP_CHANGED:
            return "Step changed";
        case ERROR_STP_OUTSIDE_BRACKET:
            return "Step outside bracket";
        case ERROR_NOT_A_DESCENT:
            return "Not a descent direction";
        case ERROR_STPMIN_GT_STPMAX:
            return "Upper step bound smaller than lower bound";
        case ERROR_STPMIN_LT_ZERO:
            return "Lower step bound less than zero";
        case ERROR_STP_LT_STPMIN:
            return "Step below lower bound";
        case ERROR_STP_GT_STPMAX:
            return "Step above upper bound";
        case ERROR_INITIAL_DERIVATIVE_GE_ZERO:
            return "Initial directional derivative greater or equal zero";
        case ERROR_NOT_STARTED:
            return "Linesearch not started";
        case SEARCH:
            return "Linesearch in progress";
        case CONVERGENCE:
            return "Linesearch has converged";
        case WARNING_ROUNDING_ERRORS_PREVENT_PROGRESS:
            return "Rounding errors prevent progress";
        case WARNING_XTOL_TEST_SATISFIED:
            return "Search interval smaller than tolerance";
        case WARNING_STP_EQ_STPMAX:
            return "Step at upper bound";
        case WARNING_STP_EQ_STPMIN:
            return "Step at lower bound";
        }
        return "Unknown linesearch status";
    }

    /**
     * Get a literal description of the current line search status.
     * @return A string describing the line search status.
     */
    public final String getMessage()
    {
        return getMessage(status);
    }

    public final boolean hasErrors()
    {
        return (status < 0);
    }

    public final boolean hasWarnings()
    {
        return (status > CONVERGENCE);
    }

    public final boolean converged()
    {
        return (status == CONVERGENCE);
    }

    public final boolean finished()
    {
        return (status != SEARCH);
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
