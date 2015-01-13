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
 * Nonmonotone line search method.
 * 
 * <p>
 * This calls implements nonmonotone line search method of Birgin,
 * Martinez, and Raydan (2000).</p>
 * 
 * <ol>
 * <li>Ernesto G. Birgin, José Mario Martínez, and Marcos Raydan. "<i>Nonmonotone
 *     spectral projected gradient methods on convex sets</i>." SIAM J. Optim.,
 *     <b>10</b>, pp. 1196–1211, (2000). [doi: 10.1137/S1052623497330963]</li>
 * <li>Ernesto G Birgin, José Mario Martínez, and Marcos Raydan. "<i>Algorithm
 *     813: SPG — software for convex-constrained optimization</i>." ACM
 *     Transactions on Mathematical Software (TOMS), <b>27</b>, pp. 340–349
 *     (2001). [doi: 10.1145/502800]</li>
 * </ul>
 * 
 * @author Éric Thiébaut.
 */
public class NonmonotoneLineSearch extends LineSearch {
    /** Lower steplength bound to trigger bisection. */
    private double sigma1 = 0.1;

    /** Upper steplength relative bound to trigger bisection. */
    private double sigma2 = 0.9;

    /** Parameter for the function reduction criterion. */
    private double ftol = 1e-4;

    /** Function values for M last accepted steps. */
    private double[] fsav;

    /** Maximum function value for the past M steps. */
    private double fmax = Double.NEGATIVE_INFINITY;

    /** Number of previous steps to remember. */
    private final int m;

    /** Number of steps since starting. */
    private int mp;

    /**
     * Get the parameter for the Armijo-like condition.
     */
    public double getTolerance() {
        return ftol;
    }

    /**
     * Set the parameter for the Armijo-like condition.
     *
     * <p>
     * The line search is assumed to have converged when the following
     * sufficient decrease (Armijo) condition holds:
     * <pre>
     *    f(x0 + alpha*d) <= fmax + alpha*ftol*<d|g(x0)>
     * </pre>
     * where <i>fmax</i> is the highest function value since the last <i>m</i>
     * steps, <i>alpha</i> is the step length and <i>ftol</i> is the parameter
     * of the Armijo condition.</p>
     *
     * @param ftol - The parameter value.  Must be in the range (0,1)
     *                 otherwise an {@link IllegalArgumentException} is
     *                 thrown.
     */
    public void setTolerance(double ftol) {
        if (Double.isNaN(ftol) || ftol <= 0.0 || ftol >= 1.0) {
            throw new IllegalArgumentException();
        }
        this.ftol = ftol;
    }

    /**
     * Get lower steplength bound to trigger bisection.
     */
    public double getLowerBound() {
        return sigma1;
    }

    /**
     * Get upper steplength relative bound to trigger bisection.
     */
    public double getUpperBound() {
        return sigma2;
    }

    /**
     * Set bounds to trigger bisection.
     * 
     * @param lower  The lower steplength bound to trigger bisection.
     * @param upper  The upper steplength relative bound to trigger bisection.
     */
    public void setBounds(double lower, double upper) {
        if (Double.isNaN(lower) || Double.isNaN(upper) || lower <= 0.0
                || lower >= upper || upper >= 1.0) {
            throw new IllegalArgumentException();
        }
        sigma1 = lower;
        sigma2 = upper;
    }

    /**
     * Constructor with default parameters.
     */
    public NonmonotoneLineSearch() {
        this(10);
    }

    /**
     * Constructor with given number of steps to memorize.
     * 
     * @param m  The number of previous steps to memorize.
     */
    public NonmonotoneLineSearch(int m) {
        if (m < 1) {
            m = 1;
        }
        this.m = m;
        this.fsav = new double[m];
        reset();
    }

    private void reset() {
        mp = 0;
        for (int k = 0; k < m; ++k) {
            fsav[k] = Double.NEGATIVE_INFINITY;
        }
    }

    @Override
    protected LineSearchStatus startHook() {
        /* Save function value. */
        fsav[mp%m] = finit;
        ++mp;

        /* Get the worst function value among the N last steps. */
        int n = Math.min(mp,  mp);
        fmax = fsav[0];
        for (int k = 1; k < n; ++k) {
            if (fmax < fsav[k]) {
                fmax = fsav[k];
            }
        }
        return LineSearchStatus.SEARCH;
    }

    @Override
    public LineSearchStatus iterateHook(double f, double g) {
        /* Check whether Armijo-like condition satisfied. */
        if (f <= fmax + stp*ftol*ginit) {
            /* Convergence criterion satisfied. */
            return LineSearchStatus.CONVERGENCE;
        }

        /* Check whether step is already at the lower bound. */
        if (stp <= stpmin) {
            stp = stpmin;
            return LineSearchStatus.WARNING_STP_EQ_STPMIN;
        }

        /* Attempt to use safeguarded quadratic interpolation to find a better step.
           The optimal steplength estimated by quadratic interpolation is q/r and
           r > 0 must hold for the quadratic approximation to be strictly convex. */
        double q = -ginit*stp*stp;
        double r = (f - finit - stp*ginit)*2.0;
        if (r > 0.0 && sigma1*r <= q && q <= sigma2*r*stp) {
            /* Quadratic approximation is strictly convex and its minimum is within
                 the bounds.  Take the quadratic interpolation step. */
            stp = q/r;
        } else {
            /* Take the bisection step. */
            stp = (stp + stpmin)/2.0;
        }

        /* Safeguard the step. */
        stp = Math.max(stp, stpmin);
        return (stp > 0.0 ? LineSearchStatus.SEARCH : LineSearchStatus.WARNING_STP_EQ_STPMIN);
    }

    public static void main(String[] args) {
        NonmonotoneLineSearch lineSearch = new NonmonotoneLineSearch(10);
        double alpha = 12.0;
        double f0 = 0.0;
        double g0 = -1.0;
        double h0 = 5.0;
        LineSearchStatus state = lineSearch.start(f0, g0, alpha, 0.0, 1e20*alpha);
        System.out.println("state = " + state);
        System.out.println("finished = " + lineSearch.finished());
        for (int k = 1; k <= 6; ++k) {
            alpha = lineSearch.getStep();
            double f1 = f0 + alpha*(g0 + 0.5*h0*alpha);
            double g1 = g0 + h0*alpha;
            state = lineSearch.iterate(alpha, f1, g1);
            System.out.println("alpha[" + k + "] = " + alpha + ";" + " f[" + k
                    + "] = " + f1 + ";" + " g[" + k + "] = " + g1 + ";"
                    + " state[" + k + "] = " + state + ";" + " finished[" + k
                    + "] = " + lineSearch.finished() + ";");
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
