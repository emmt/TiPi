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

import mitiv.exception.IllegalLinearOperationException;
import mitiv.exception.IncorrectSpaceException;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

/**
 * Implement limited memory quasi-Newton approximation of the inverse Hessian.
 * 
 * The approximation of the inverse of Hessian is based on BFGS
 * (Broyden, Fletcher, Goldfarb & Shanno) updates using the 2-loop recursive
 * algorithm of Strang (described by Nocedal, 1980) combined with a
 * preconditioner (initial approximation of the inverse Hessian) or
 * automatic scalings (along the ideas of Gilbert & Lemaréchal (1989; and
 * Shanno).
 * 
 * @author Éric Thiébaut.
 *
 */
public class LBFGSOperator extends LinearOperator {

    /* There are at most M saved pairs, MP is the actual number of saved pairs
     * and MARK is the index of the oldest one.  Therefore:
     *    (MARK + MP)%M  is the newest saved pair (if MP > 0)
     */
    protected Vector[] s;    // to store variable changes (steps)
    protected Vector[] y;    // to store gradient changes
    protected final int m;   // maximum number of saved pairs
    protected int mp;        // actual number of saved pairs
    protected int mark;      // index of oldest saved pair
    protected double rho[];
    protected double gamma;  // scaling parameter for H0
    protected double alpha[];
    protected LinearOperator H0; // crude approximation of inverse
    // Hessian (preconditioner)
    protected InverseHessianApproximation rule;
    private Vector tmp; // temporary vector for the apply() method

    /**
     * Create a new rank-one linear operator.
     * 
     * A rank-one operator is A = u.v' thus:
     * 
     * A.x = (v'.x) u = <v|x> u
     * 
     * @param u
     *            the left vector
     * @param v
     *            the right vector
     */
    public LBFGSOperator(VectorSpace space, int m) {
        super(space);
        this.m = m;
        this.H0 = null;
        rule = InverseHessianApproximation.BY_STY_OVER_YTY;
        allocateWorkspace();
    }

    /**
     * Create a limited memory BFGS operator with a preconditioner.
     * 
     * The LBFGS operator just stores a reference to the preconditioner
     * {@code H0} and does not assume that the preconditioner is a constant
     * operator.  It is therefore possible for the caller to adjust the
     * preconditioner at every iteration.  The LBFGS operator will have
     * the same input and output spaces as the preconditioner and can be
     * thought as a refined version of {@code H0}.
     * 
     * @param H0 - The preconditioner.
     * @param m - The number of previous updates to memorize.
     */
    public LBFGSOperator(LinearOperator H0, int m) {
        super(H0.getInputSpace(), H0.getOutputSpace());
        this.m = m;
        this.H0 = H0;
        rule = InverseHessianApproximation.NONE;
        allocateWorkspace();
    }

    private void allocateWorkspace() {
        tmp = (H0 == null ? null : inputSpace.create());
        s = new Vector[m];
        y = new Vector[m];
        for (int i = 0; i < m; ++i) {
            s[i] = outputSpace.create();
            y[i] = inputSpace.create();
        }
        alpha = new double[m];
        rho = new double[m];
        reset();
    }

    /**
     * Reset the operator.
     * 
     * Forget all memorized pairs.
     */
    public void reset() {
        mp = 0;
        mark = -1;
        gamma = 1.0;
    }

    /**
     * Set the scaling of the initial approximation of the inverse Hessian.
     * 
     * The best scaling strategy is probably {@link #InverseHessianApproximation.BY_SY_OVER_YY}
     * when no preconditioner {@code H0} is provided.
     *
     * @param id - The strategy to use.
     */
    public void setScaling(InverseHessianApproximation value) {
        rule = value;
    }

    /**
     * Get the current scaling strategy.
     * @return The identifier of the current strategy.
     */
    public InverseHessianApproximation getScaling() {
        return rule;
    }

    /**
     * Set the scaling parameter.
     * 
     * This automatically set the scaling strategy to {@link #USER_SCALING}.
     * @param value - The value of gamma.  Must be strictly positive.
     */
    public void setScale(double value) {
        gamma = value;
        rule = InverseHessianApproximation.BY_USER;
    }

    /**
     * Get the scaling parameter.
     * @return The current value of {@code gamma}, the scaling parameter of
     * the initial approximation of the inverse Hessian (that is the
     * preconditioner {@code H0}, or the identity if no preconditioner is
     * given).
     */
    public double getScale() {
        return gamma;
    }


    /**
     * Get slot index of a saved pair of variables and gradient differences.
     *
     * The offset {@code k} must be greater or equal {@code 1 - mp};
     * {@code slot(0)} is the last saved pair (newest one), {@code slot(-1)}
     * is the previous pair, ..., {@code slot(1 - mp)} is the oldest saved
     * pair.   Offset can also be positive, for instance, {@code slot(1)} is
     * the index of the slot just after the last saved one, that is the next
     * position of the mark.
     * 
     * @param k - The offset with respect to the last saved pair.
     * @return The index of {@code k}-th slot relative to the last saved pair.
     * 
     */
    protected int slot(int k) {
        return (m + mark + k)%m;
    }

    protected Vector s(int k) {
        return s[slot(k)];
    }

    protected Vector y(int k) {
        return y[slot(k)];
    }

    @Override
    protected void privApply(Vector src, Vector dst, int job) {
        if (job != DIRECT && job != ADJOINT) {
            throw new IllegalLinearOperationException();
        }

        /* First loop of the recursion (from the newest saved pair to the
         * oldest one). */
        final int newest = 0;
        final int oldest = 1 - mp;
        Vector r = (H0 == null ? dst : tmp);
        r.copyFrom(src);
        for (int k = newest; k >= oldest; --k) {
            int j = slot(k);
            if (rho[j] > 0.0) {
                alpha[j] = rho[j]*r.dot(s[j]);
                r.axpby(1.0, r, -alpha[j], y[j]);
            } else {
                alpha[j] = 0.0;
            }
        }

        /* Apply approximation of inverse Hessian.  (Note that in any
         * case Q and DST are the same vector.) */
        Vector q;
        if (H0 != null) {
            q = dst;
            H0.apply(r, q);
        } else {
            q = r;
        }
        if (rule != InverseHessianApproximation.NONE && gamma != 1.0) {
            q.scale(gamma);
        }

        /* Second loop of the recursion (from the oldest saved pair to the
         * newest one). */
        for (int k = oldest; k <= newest; ++k) {
            int j = slot(k);
            if (rho[j] > 0.0) {
                double beta = rho[j]*q.dot(y[j]);
                q.axpby(1.0, q, alpha[j] - beta, s[j]);
            }
        }
    }

    /*
     * Start of iteration k:
     *    if any previous storage, compute and store:
     *        s(k) = x(k) - x(k-1)
     *        y(k) = g(k) - g(k-1)
     *    compute search direction:
     *        p = H(-g(k));
     *    to save storage, x(k) and g(k) can be stored in the
     *    oldest slot for s and y
     */

    /**
     * Update LBFGS operator with a new pair of variables and gradient
     * differences.
     * @param x1 - The new variables.
     * @param x0 - The previous variables.
     * @param g1 - The gradient at {@code x1}.
     * @param g0 - The gradient at {@code x0}.
     * @throws IncorrectSpaceException
     */
    public void update(Vector x1, Vector x0, Vector g1, Vector g0)
            throws IncorrectSpaceException {
        /* Store the variables and gradient differences in the slot
         * just after the mark (which is the index of the last saved
         * pair or -1 if none).
         */
        int j = slot(1);
        s[j].axpby(1.0, x1, -1.0, x0);
        y[j].axpby(1.0, g1, -1.0, g0);

        /* Compute RHO[j] and GAMMA.  If the update formula for GAMMA does
         * not yield a strictly positive value, the strategy is to keep the
         * previous value. */
        double sty = s[j].dot(y[j]);
        if (sty > 0.0) {
            rho[j] = 1.0/sty;
            if (rule == InverseHessianApproximation.BY_STY_OVER_YTY
                    || (rule == InverseHessianApproximation.BY_INITIAL_STY_OVER_YTY && (mp == 0 || gamma == 1.0))) {
                double yty = y[j].dot(y[j]);
                if (yty > 0.0) {
                    gamma = sty/yty;
                }
            } else if (rule == InverseHessianApproximation.BY_STS_OVER_STY
                    || (rule == InverseHessianApproximation.BY_INITIAL_STS_OVER_STY && (mp == 0 || gamma == 1.0))) {
                double ss = s[j].dot(s[j]);
                if (ss > 0.0) {
                    gamma = ss/sty;
                }
            }
        } else {
            /* This pair will be skipped. */
            rho[j] = 0.0;
        }

        /* Update the mark and the number of saved pairs. */
        mark = j;
        mp = Math.min(mp + 1, m);
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
