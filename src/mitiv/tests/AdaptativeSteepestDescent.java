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

package mitiv.tests;

public class AdaptativeSteepestDescent {
    protected double sigma1 = 0.1;
    protected double sigma2 = 0.9;
    protected double stepGain = 2.0;
    protected boolean favorStepGrowth = true;
   
    
    /**
     * Set the line search slope tolerances according to Goldstein rule.
     * 
     * Given a lower tolerance slope, 0 < sigma < 0.5, set the
     * line search bound to be: sigma and 1 - sigma.
     *  
     * @param sigma   The value of the lower tolerance slope, must
     *                be numerically strictly between 0 and 1/2.  The value
     */
    public void setTolerances(double sigma) {
        setTolerances(sigma, 1.0 - sigma);
    }
    
    /**
     * Set the line search slope tolerances.
     * 
     * The line search is assumed to be successful whenever:
     * 
     *    f(x0) + sigma2*alpha*p'.g(x0) <= f(x0 + alpha*p) <= f(x0) + sigma1*alpha*p'.g(x0)
     *    
     * where x0 is the position at the start of the line search, f(x) is
     * the cost function, g(x) is its gradient alpha > 0 is the step length
     * and p is the search direction.  As p must be a descent direction at
     * the starting point x0, then p'.g(x0) < 0 must hold and the above
     * inequalities amounts to:
     *  
     *    sigma1 <= (f(x0 + alpha*p) - f(x0)) / (alpha*p'.g(x0)) <= sigma2
     * 
     * (in fact, it is sufficient to insure that alpha*p'.g(x0) be strictly
     * negative).  Therefore sigma1 and sigma2 can be seen as bounds for
     * an acceptable mean slope between x0 and x0 + alpha*p.
     * 
     * @param sigma1  The lower tolerance slope. 
     * @param sigma2  The upper tolerance slope.
     */
    public void setTolerances(double sigma1, double sigma2) {
        if (sigma1 <= 0 || sigma1 >= sigma2 || sigma2 >= 1.0) {
            throw new IllegalArgumentException();
        }
        this.sigma1 = sigma1;
        this.sigma2 = sigma2;
    }
    
    public void setTolerances(double[] sigma) {
        if (sigma == null || sigma.length != 2) {
            throw new IllegalArgumentException();            
        }
        setTolerances(sigma[0], sigma[1]);
    }

    /**
     * Get the line search step gain.
     * @return The value of the step gain.
     */
    public double getStepGain() {
        return stepGain;
    }

    /**
     * Set the line search step gain.
     * 
     * Whenever the step length must be reduced by the line search, it is
     * multiplied by the step gain; conversely, whenever the step length
     * must be reduced, it is divided by the step gain.
     *
     * @param value   The value of the gain (must be strictly greater than 1).
     */
    public void setStepGain(double value) {
        if (value <= 1.0) {
            throw new IllegalArgumentException();            
        }
       stepGain = value;
    }
    
    /**
     * Check whether step growth is favored.
     * @return A boolean value.
     */
    public boolean isFavorStepGrowth() {
        return favorStepGrowth;
    }
    
    /**
     * Set strategy for next step length.
     * 
     * In order to avoid stagnation with a small step, it may be advantageous to
     * favor, in the next line search, a step length which is larger than the
     * previous one.  In this case, the first value of the step length for the
     * next line search is taken to be the length of the previous accepted step
     * times the step gain. Otherwise, the next line search will first try the
     * previous accepted step length.
     *  
     * @param favorStepGrowth  Do we start the next step with a larger step length?
     */
    public void setFavorStepGrowth(boolean favorStepGrowth) {
        this.favorStepGrowth = favorStepGrowth;
    }
    
    /**
     * Compute the gradient convergence threshold given the norm of the initial gradient.
     *
     * A convergence criterion is to stop the optimization routine as soon as
     * the norm of the gradient of the cost function is below a threshold, say epsilon:
     * 
     *    if (norm(g) <= epsilon) then convergence;
     *
     * This routine compute the threshold as:
     * 
     *    epsilon = max(atol, rtol*norm(g0))           (for a type 1 norm)
     *    
     * or as:
     * 
     *    epsilon = max(atol, rtol*sqrt(norm(g0)))^2   (for a type 2 norm)
     * 
     * where norm(g0) is the norm of the initial gradient (or some typical value of the
     * gradient norm) and atol and rtol are absolute and relative threshold levels.
     * 
     * @param atol      The absolute threshold; should be >= 0.
     * @param rtol      The relative threshold; should be in [0,1].
     * @param gnrm      The norm of the initial gradient.
     * @param normType  The type of the norm: 2 for squared Euclidean norm, 1 for
     *                  other norms.
     * @return The threshold below which the optimization method will be
     *         considered to have converged.
     */
    public static double gradientThreshold(double atol, double rtol, double gnrm,
            int normType) {
        if (normType == 2) {
            double r = Math.max(0.0, Math.max(atol, rtol*Math.sqrt(gnrm)));
            return r*r;
        } else {
            return Math.max(0.0, Math.max(atol, rtol*gnrm));
        }
    }
    
    /**
     * Testing routine.
     * @param args
     */
    public static void main(String[] args) {
        int prob = 2;
        int n = 6;
        double[] x0 = new double[n];
        double[] x = new double[n];
        double[] g = new double[n];
        AdaptativeSteepestDescent t = new AdaptativeSteepestDescent();
        MinPack1Tests.umipt(x0, prob, 1.0);
        t.test(x0,x,g,prob,1.0,0.0,1e-5);
    }
    void test(double[] x0, double[] x, double[] g, int prob, double alpha, double atol, double rtol) {
        double f, f0, f1, f2, rho, epsilon = 0.0;
        if (alpha <= 0.0) {
            alpha = 1.0;
        }
        double alphaNext = alpha;
        int n = x0.length;
        int nf = 0, ng = 0;
        f0 = MinPack1Tests.umobj(x0, prob);
        ++nf;
        System.out.println("f0 = " + f0);
        while (true) {
            /* Compute the gradient at x0 and its squared norm. */
            MinPack1Tests.umgrd(x0, g, prob);
            ++ng;
            rho = 0.0;
            for (int i = 0; i < n; ++i) {
                rho += g[i]*g[i];
            }
            if (ng == 1) {
                epsilon = gradientThreshold(atol, rtol, rho, 2);
            }
            if (rho <= epsilon) {
                /* The solution has been found. */
                System.out.println("after " + nf + " function and " + ng
                        + " gradient calls, final f = " + f0
                        + ", rho = " + rho
                        + ", epsilon = " + epsilon);
                return;
            } else {
                System.out.println("after " + nf + " function and " + ng
                        + " gradient calls, best f = " + f0
                        + ", rho = " + rho
                        + ", epsilon = " + epsilon);
            }

            /* Line search loop. */
            double alphaMin = 0.0;
            double alphaMax = Double.MAX_VALUE;
            alpha = alphaNext;
            while (true) {
                /* Try a new step. */
                for (int i = 0; i < n; ++i) {
                    x[i] = x0[i] - alpha*g[i];
                }
                f = MinPack1Tests.umobj(x, prob);
                ++nf;

                /* Compute the function bounds for the line search.
                 * As 0 < sigma1 < sigma2 < 1 and alpha > 0 then:
                 * f0 > f1 > f2
                 */
                f1 = f0 - sigma1*rho*alpha;
                f2 = f0 - sigma2*rho*alpha;
                if (f > f1) {
                    /* The cost is too high, reduce the step. */
                    alphaMax = alpha;
                    alpha /= stepGain;
                    if (alpha <= alphaMin) {
                        alpha = (alphaMin + alphaMax)/2.0;
                    }
                } else if (f < f2) {
                    /* The reduction is insufficient, augment the step. */
                    alphaMin = alpha;
                    alpha *= stepGain;
                    if (alpha >= alphaMax) {
                        alpha = (alphaMin + alphaMax)/2.0;
                    }
                } else {
                    /* An acceptable step has been found.  X becomes the new initial point. */
                    double[] tmp;
                    tmp = x0;
                    x0 = x;
                    x = tmp;
                    f0 = f;
                    alphaNext = (favorStepGrowth ? alpha*stepGain : alpha);
                    break;
                }
            }
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
