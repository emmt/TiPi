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
 * Simple backtracking line search method.
 *
 * <p>
 * This simple strategy for terminating a line search is well suited for Newton
 * methods but is less appropriate for quasi-Newton nor conjugate gradient
 * methods.</p>
 *
 * @author Éric Thiébaut.
 */
public class ArmijoLineSearch extends LineSearch {
    /** Best step length so far. */
    private double bestStep = 0.0;

    /** Best function value so far. */
    private double bestFunc = 0.0;

    /** Bypass next check? */
    private boolean bypass = false;

    /**
     * Parameter for the Armijo condition.
     */
    protected double sigma = 0.05;

    /**
     * Step length gain.
     */
    protected double rho = 0.5;

    /**
     * Set the step gain.
     *
     * Until the sufficient decrease (Armijo) condition holds, the step
     * length is reduced by multiplying it by the gain.
     *
     * @param value - The new gain.  Must be in the range (0,1) otherwise
     *                an {@link IllegalArgumentException} is thrown.
     */
    public void setGain(double value) {
        if (Double.isNaN(value) || value <= 0.0 || value >= 1.0) {
            throw new IllegalArgumentException();
        }
        rho = value;
    }

    /**
     * Get the step gain.
     */
    public double getGain() {
        return rho;
    }

    /**
     * Set the parameter for the Armijo condition.
     *
     * <p>
     * The line search is assumed to have converged when the following
     * sufficient decrease (Armijo) condition holds:
     * <pre>
     *    phi(alpha) <= phi(0) + alpha*sigma*phi'(0)
     * </pre>
     * where <i>alpha</i> is the step length and <i>sigma</i> is the parameter
     * of the Armijo condition.</p>
     *
     * @param value - The parameter value.  Must be in the range (0,1)
     *                 otherwise an {@link IllegalArgumentException} is
     *                 thrown.
     */
    public void setTolerance(double value) {
        if (Double.isNaN(value) || value <= 0.0 || value >= 1.0) {
            throw new IllegalArgumentException();
        }
        sigma = value;
    }

    /**
     * Get the parameter for the 1st Wolfe (Armijo) condition..
     */
    public double getTolerance() {
        return sigma;
    }

    public static void main(String[] args) {
        ArmijoLineSearch lineSearch = new ArmijoLineSearch(0.5, 0.001);
        double alpha = 12.0;
        double f0 = 0.0;
        double g0 = -1.0;
        double h0 = 5.0;
        LineSearchTask task = lineSearch.start(f0, g0, alpha, 0.0, 1e20*alpha);
        System.out.println("state = " + task);
        System.out.println("finished = " + lineSearch.finished());
        for (int k = 1; k <= 6; ++k) {
            alpha = lineSearch.getStep();
            double f1 = f0 + alpha*(g0 + 0.5*h0*alpha);
            double g1 = g0 + h0*alpha;
            task = lineSearch.iterate(alpha, f1, g1);
            System.out.println("alpha[" + k + "] = " + alpha + ";" + " f[" + k
                    + "] = " + f1 + ";" + " g[" + k + "] = " + g1 + ";"
                    + " state[" + k + "] = " + task + ";" + " finished[" + k
                    + "] = " + lineSearch.finished() + ";");
        }
    }

    /**
     * Constructor with default parameters.
     */
    public ArmijoLineSearch() {
    }

    /**
     * Constructor with given parameters.
     *
     * @param rho   - The gain for reducing the step length.
     * @param sigma - The parameter of the sufficient decrease condition.
     */
    public ArmijoLineSearch(double rho, double sigma) {
        setGain(rho);
        setTolerance(sigma);
    }

    @Override
    public boolean useDerivative()
    {
        return true;
    }

    @Override
    protected void startHook() {
        bestFunc = finit;
        bestStep = 0.0;
        bypass = false;
        success(LineSearchTask.SEARCH);
    }

    // FIXME: Bypassing the test means that we spend one more
    //         function (and gradient) evaluation than really
    //         necessary.
    @Override
    public void iterateHook(double f, double g) {
        if (bypass || f - finit <= stp*sigma*ginit) {
            success(LineSearchTask.CONVERGENCE);
        } else {
            if (f < bestFunc) {
                /* Register best function so far and reduce the step. */
                bestStep = stp;
                bestFunc = f;
                stp = rho*stp;
            } else if (bestStep > stp) {
                /* A better function has been obtained with a larger step.
                 * Revert to this step and manage to accept it by bypassing
                 * the convergence test. */
                bypass = true;
                stp = bestStep;
            } else {
                /* Reduce the length of the step. */
                stp = rho*stp;
            }
            success(LineSearchTask.SEARCH);
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
