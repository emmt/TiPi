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

public class LinearConjugateGradient {
    /* Codes returned by solve() method. */

    /** Algorithm is running. */
    public static final int IN_PROGRESS = 0;

    /** Algorithm has converged within tolerances. */
    public static final int CONVERGED = 1;

    /** Too many iterations. */
    public static final int TOO_MANY_ITERATIONS = 2;

    /** RHS matrix A is not positive definite. */
    public static final int A_IS_NOT_POSITIVE_DEFINITE = 3;

    /** Preconditioner P is not positive definite. */
    public static final int P_IS_NOT_POSITIVE_DEFINITE = 4;

    /** Parameter ATOL is less than zero. */
    public static final int BAD_ATOL = 5;

    /** Parameter RTOL is less than zero or greater or equal one. */
    public static final int BAD_RTOL = 6;

    /** Congratulations you have found a bug! */
    public static final int BUG = 7;

    public static final double DEFAULT_ATOL = 0.0;
    public static final double DEFAULT_RTOL = 1e-5;
    private double atol = DEFAULT_ATOL;
    private double rtol = DEFAULT_RTOL;

    private LinearOperator A; /* LHS matrix */
    private LinearOperator P; /* preconditioner (or null) */
    private Vector b; /* RHS vector */
    private Vector p; /* search direction */
    private Vector q; /* q = A.p */
    private Vector r; /* residuals */
    private Vector z; /* preconditioned residuals: z = P.r */



    public LinearConjugateGradient(LinearOperator A, Vector b) {
        this(A, b, null);
    }

    public LinearConjugateGradient(LinearOperator A, Vector b, LinearOperator P) {
        /* Check that A.x = b makes sense. */
        if (! b.belongsTo(A.getOutputSpace())) {
            throw new IncorrectSpaceException();
        }
        if (A.getInputSpace() != A.getOutputSpace()) {
            throw new IllegalArgumentException("LHS matrix must be an endomorphism.");
        }
        if (P != null) {
            /* Check preconditioner P. */
            if (P.getInputSpace() != A.getOutputSpace() ||
                    P.getOutputSpace() != A.getInputSpace()) {
                throw new IncorrectSpaceException();
            }
        }
        this.A = A;
        this.b = b;
        this.P = P;
    }

    public double getAtol() {
        return atol;
    }

    public void setAtol(double atol) {
        this.atol = Math.max(0.0, atol);
    }

    public double getRtol() {
        return rtol;
    }

    public void setRtol(double rtol) {
        this.rtol = Math.max(0.0, rtol);
    }

    public int solve(Vector x, int maxiter, boolean reset) {

        /* Check that A.x = b makes sense. */
        if (! x.belongsTo(A.getInputSpace())) {
            throw new IncorrectSpaceException();
        }
        VectorSpace vsp = b.getSpace();
        if (p == null) {
            p = vsp.create();
        }
        if (q == null) {
            q = vsp.create();
        }
        if (r == null) {
            r = vsp.create();
        }
        if (z == null) {
            /* For the unpreconditioned version of the linear conjugate
             * gradient, the vector z is always the same as the residuals r. */
            z = (P == null ? r : vsp.create());
        }
        /*
         * Initial solution x and initial residuals r (FIXME: slight
         * optimization possible if x is known to be zero).
         */
        if (reset) {
            /* x = 0 and r = b */
            x.zero();
            r.copy(b);
        } else {
            /* r = b - A.x */
            A.apply(r, x);
            r.combine(1.0, b, -1.0, r);
        }
        if (P != null) {
            P.apply(z, r);
        }

        /* Compute convergence threshold: EPSILON = max(0, ATOL, RTOL*RHO)) */
        double rho = z.dot(r);
        double rho_prev = 0.0;
        double epsilon = Math.max(0.0, Math.max(atol, rtol * rho));
        int iter = 0;
        for (;;) {
            /* Check for convergence. */
            if (rho <= epsilon) {
                if (rho < 0.0) {
                    /* RHO must be greater or equal zero. */
                    if (P != null) {
                        return P_IS_NOT_POSITIVE_DEFINITE;
                    } else {
                        return BUG;
                    }
                }
                return CONVERGED;
            }
            if (maxiter >= 0 && iter >= maxiter) {
                return TOO_MANY_ITERATIONS;
            }
            /* Compute new search direction: p = z + beta*p */
            if (iter == 0) {
                p.copy(p);
            } else {
                double beta = rho / rho_prev;
                p.combine(1.0, z, beta, p);
            }
            /* Compute optimal step length and update unknown x and residuals r. */
            A.apply(q, p);
            double gamma = p.dot(q);
            if (gamma <= 0.0) {
                return A_IS_NOT_POSITIVE_DEFINITE;
            }
            double alpha = rho/gamma;
            x.add(+alpha, p);
            r.add(-alpha, q);
            if (P != null) {
                P.apply(z, r);
            }
            rho_prev = rho;
            rho = z.dot(r);
            ++iter;
        }
    }


    public static int solve(LinearOperator A, Vector b, Vector x,
            double atol, double rtol, int maxiter, boolean reset) {
        return solve(A, b, x, null, atol, rtol, maxiter, reset);
    }

    public static int solve(LinearOperator A, Vector b, Vector x,
            LinearOperator P, double atol, double rtol, int maxiter,
            boolean reset) {
        if (atol < 0.0) {
            return BAD_ATOL;
        }
        if (rtol < 0.0) {
            return BAD_RTOL;
        }
        LinearConjugateGradient cg = new LinearConjugateGradient(A, b, P);
        return cg.solve(x, maxiter, reset);
    }
}
