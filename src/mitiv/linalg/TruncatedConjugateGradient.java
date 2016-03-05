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

package mitiv.linalg;

import mitiv.exception.IncorrectSpaceException;

/**
 * Implement trust region conjugate gradient algorithm of Steihaug.
 *
 * References: Trond Steihaug, "The conjugate gradient method and trust regions
 * in large scale optimization," SIAM J. Numer. Anal., vol. 20, pp. 626-637
 * (1983).
 *
 * @author eric
 *
 */
public class TruncatedConjugateGradient {
    public static final int SUCCESS = 0;
    public static final int FAILURE = -1;

    public static final int IN_PROGRESS = 0; /* Algorithm is running. */
    public static final int CONVERGED = 1; /*
     * Algorithm has converged within
     * tolerances.
     */
    public static final int TRUNCATED = 2;
    public static final int TOO_MANY_ITERATIONS = 3; /* Too many iterations. */
    public static final int A_IS_NOT_POSITIVE_DEFINITE = 4; /*
     * RHS matrix A is
     * not positive
     * definite.
     */
    public static final int P_IS_NOT_POSITIVE_DEFINITE = 5; /*
     * Preconditioner P
     * is not positive
     * definite.
     */
    public static final int BUG = 7;

    public static final int FOREVER = -1;

    public static final double DEFAULT_ABSOLUTE_TOLERANCE = 0.0;
    public static final double DEFAULT_RELATIVE_TOLERANCE = 1E-6;
    public static final int DEFAULT_MAXIMUM_ITERATIONS = FOREVER;

    /* Instance variables. */
    protected double atol = DEFAULT_ABSOLUTE_TOLERANCE;
    protected double rtol = DEFAULT_RELATIVE_TOLERANCE;
    protected int maxiter = DEFAULT_MAXIMUM_ITERATIONS;
    protected LinearOperator A = null;
    protected Vector b = null;
    protected double delta = 0.0;

    public TruncatedConjugateGradient() {

    }

    public void setDefaults() {
        this.atol = DEFAULT_ABSOLUTE_TOLERANCE;
        this.rtol = DEFAULT_RELATIVE_TOLERANCE;
        this.maxiter = DEFAULT_MAXIMUM_ITERATIONS;
    }

    public void releaseMemory() {
        this.b = null;
    }

    public void setPreconditioner(LinearOperator P) {
        /* FIXME: */
    }

    public void setRightHandSideVector(Vector b) {
        /* FIXME: */
    }

    public void setLeftHandSideOperator(LinearOperator A) {
        /* FIXME: */
    }

    public double getAbsoluteTolerance() {
        return this.atol;
    }

    public void setAbsoluteTolerance(double atol) {
        this.atol = Math.max(0.0, atol);
    }

    public double getRelativeTolerance() {
        return this.rtol;
    }

    public void setRelativeTolerance(double rtol) {
        this.rtol = Math.max(0.0, rtol);
    }

    public int getMaximumIterations() {
        return this.maxiter;
    }

    public void setMaximumIterations(int maxiter) {
        this.maxiter = (maxiter >= 0 ? maxiter : FOREVER);
    }

    /**
     * Compute the roots of a 2nd degree polynomial.
     *
     * Solve the quadratic equation:
     *
     * a*x^2 + b*x + c = 0
     *
     * for real x. The number n of distinct real roots is returned and the roots
     * if any are stored into x. If there is no roots, the contents of x is left
     * unchanged; otherwise (n = 1 or 2) the two values of x are set with the
     * roots in ascending order, i.e. such that x[0] <= x[1].
     * @param x
     *            2-element output array to store the result
     * @param a
     *            2nd degree coefficient
     * @param b
     *            1st degree coefficient
     * @param c
     *            0th degree coefficient
     *
     * @return The number n of distinct real roots. If n >= 1, then x[0] <=
     *         x[1]; otherwise (n = 0) and x contents is left unchanged.
     */
    public static int solveQuadratic(double x[], double a, double b, double c) {
        if (a != 0.0) {
            double p = a + a;
            double q = c + c;
            double r = b * b - p * q;
            if (r > 0.0) {
                if (b >= 0.0) {
                    r = -b - Math.sqrt(r);
                } else {
                    r = Math.sqrt(r) - b;
                }
                double xa = q / r;
                double xb = r / p;
                if (xa < xb) {
                    x[0] = xa;
                    x[1] = xb;
                } else {
                    x[0] = xb;
                    x[1] = xa;
                }
                return 2;
            } else if (r == 0.0) {
                x[0] = x[1] = -b / p;
                return 1;
            }
        } else if (b != 0.0) {
            x[0] = x[1] = -c / b;
            return 1;
        }
        return 0;
    }

    /**
     * Adjust vector length along a given direction.
     *
     * Replace x by x + alpha*p so that ||x - alpha*p|| = delta.
     *
     * @param x
     *            the vector to adjust
     * @param p
     *            the direction
     * @param delta
     *            the Euclidean norm of the result
     * @param xnrm
     *            the Euclidean norm of x
     * @throws IncorrectSpaceException
     */
    private static int adjustStep(Vector x, final Vector p, double delta,
            double xnrm) throws IncorrectSpaceException {
        if (delta < 0.0) {
            return FAILURE;
        }
        if (xnrm == delta) {
            return SUCCESS;
        }
        /*
         * ||x + alpha*p|| = delta <==> a*t^2 + b*t + c = 0
         *
         * with:
         *
         * a = ||p||^2
         *
         * b = 2*<x|p>
         *
         * c = ||x||^2 - delta^2 = (||x|| - delta)*(||x|| - delta)
         */
        double a = p.dot(p);
        double b = 2.0*p.dot(x);
        double c = (xnrm + delta)*(xnrm - delta);
        double[] t = new double[2];
        double alpha = 0.0;
        if (solveQuadratic(t, a, b, c) >= 1) {
            if (xnrm > delta) {
                /* Compute a backward step. */
                alpha = Math.min(0.0, t[0]);
            } else {
                /* Compute a forward step. */
                alpha = Math.max(0.0, t[1]);
            }
        }
        if (alpha == 0.0) {
            return FAILURE;
        }
        x.combine(1.0, x, alpha, p);
        return SUCCESS;
    }

    public int truncatedConjugateGradient(LinearOperator A, Vector b, Vector x,
            double delta, int maxiter)
                    throws IncorrectSpaceException {
        return truncatedConjugateGradient(A, b, x, null, delta, maxiter);
    }

    /**
     * Compute a trust-region step by means of preconditioned truncated linear
     * conjugate gradient.
     *
     *
     * @param A
     *            The LHS operator.
     * @param b
     *            The RHS vector.
     * @param x
     *            A vector to store the solution.
     * @param P
     *            A preconditioner.
     * @param delta
     *            The maximum allowed length of x.
     * @param maxiter
     *            The maximum number of iterations.
     * @return A code indicating the reason of stopping the algorithm.
     * @throws IncorrectSpaceException
     */
    public int truncatedConjugateGradient(LinearOperator A, Vector b, Vector x,
            LinearOperator P, double delta, int maxiter)
                    throws IncorrectSpaceException {
        /*
         * Check that A.x = b makes sense and whether A input and output spaces
         * are the same.
         */
        LinearOperator.checkLinearProblem(A, b, x, true);
        VectorSpace vsp = b.getSpace();
        Vector r = vsp.create(); /* residuals */
        Vector p = vsp.create(); /* search direction */
        Vector q = vsp.create(); /* q = A.p */
        Vector z = null; /* preconditioned residuals: z = P.r */

        /*
         * Initial solution x = 0 and initial residuals r = b.
         */
        vsp.zero(x);
        double xnrm = 0.0;
        r.copyFrom(b);
        if (P != null) {
            /* Check preconditioner P and allocate vector for Z. */
            if (P.getInputSpace() != vsp || P.getOutputSpace() != vsp) {
                throw new IncorrectSpaceException();
            }
            z = vsp.create();
            P.apply(r, z);
        } else {
            /*
             * Unpreconditioned version of the linear conjugate gradient. Vector
             * z is always the same as the residuals r.
             */
            z = r;
        }
        double rho = z.dot(r);
        double rho_prev = 0.0;
        double epsilon = 0.0;
        int iter = 0;
        for (;;) {
            /* Check step length and then check for convergence. */
            if (rho <= epsilon) {
                return CONVERGED;
            }
            if (maxiter >= 0 && iter >= maxiter) {
                return TOO_MANY_ITERATIONS;
            }
            /* Compute new search direction: p = z + beta*p */
            if (iter == 0) {
                p.copyFrom(z);
            } else {
                double beta = rho / rho_prev;
                p.combine(1.0, z, beta, p);
            }
            /* Compute optimal step length and update unknown x and residuals r. */
            A.apply(p, q);
            double gamma = p.dot(q);
            if (gamma <= 0.0) {
                /*
                 * Non positivedefiniteness of A. Apply a truncated forward
                 * step.
                 */
                adjustStep(x, p, delta, xnrm);
                return A_IS_NOT_POSITIVE_DEFINITE;
            }
            double alpha = rho/gamma;
            x.combine(1.0, x, +alpha, p);
            xnrm = Math.sqrt(x.dot(x));
            if (xnrm >= delta) {
                /* Apply a truncated backward step. */
                adjustStep(x, p, delta, xnrm);
                return TRUNCATED;
            }
            r.combine(1.0, r, -alpha, q);
            if (P != null) {
                P.apply(r, z);
            }
            rho_prev = rho;
            rho = z.dot(r);
            ++iter;
        }
    }
}
