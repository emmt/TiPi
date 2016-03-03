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
 * The approximation of the inverse of Hessian is based on BFGS (Broyden,
 * Fletcher, Goldfarb & Shanno) updates using the 2-loop recursive algorithm of
 * Strang (described by Nocedal, 1980) combined with a preconditioner (initial
 * approximation of the inverse Hessian) or automatic scalings (along the ideas
 * of Gilbert & Lemaréchal (1989); and Shanno).
 *
 * @author Éric Thiébaut.
 *
 */
public class LBFGSOperator extends LinearOperator {

    /* There are at most M saved pairs, MP is the actual number of saved pairs
     * and MARK is the index of the oldest one.  Therefore:
     *    (MARK + MP)%M  is the newest saved pair (if MP > 0)
     */
    protected Vector[] s; // to store variable changes (steps)
    protected Vector[] y; // to store gradient changes
    protected final int m; // maximum number of saved pairs
    protected int mp; // actual number of saved pairs
    protected int mark; // number of successful updates
    protected double rho[];
    protected double gamma; // scaling parameter for H0
    protected double beta[];
    protected LinearOperator H0; // crude approximation of inverse Hessian
    // (preconditioner)
    protected InverseHessianApproximation rule;
    private Vector tmp; // temporary vector for the apply() method

    /**
     * Create a limited memory BFGS operator without preconditioner.
     *
     * @param space
     *            - The vector space to operate on.
     * @param m
     *            - The number of previous updates to memorize.
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
     * operator. It is therefore possible for the caller to adjust the
     * preconditioner at every iteration. The LBFGS operator will have the same
     * input and output spaces as the preconditioner and can be thought as a
     * refined version of {@code H0}.
     *
     * @param H0
     *            - The preconditioner.
     * @param m
     *            - The number of previous updates to memorize.
     */
    public LBFGSOperator(LinearOperator H0, int m) {
        super(H0.getInputSpace());
        if (! H0.isEndomorphism()) {
            throw new IncorrectSpaceException("Preconditioner must be an endomorphism");
        }
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
        beta = new double[m];
        rho = new double[m];
        mp = 0;
        mark = 0;
        gamma = 1.0;
    }

    /**
     * Reset the operator.
     *
     * Forget all memorized pairs.
     */
    public void reset() {
        mp = 0;
    }

    /**
     * Set the scaling of the initial approximation of the inverse Hessian.
     *
     * The best scaling strategy is probably
     * {@link #InverseHessianApproximation.BY_SY_OVER_YY} when no preconditioner {@code H0} is
     * provided.
     *
     * @param id
     *            - The strategy to use.
     */
    public void setScaling(InverseHessianApproximation value) {
        rule = value;
    }

    /**
     * Get the current scaling strategy.
     *
     * @return The identifier of the current strategy.
     */
    public InverseHessianApproximation getScaling() {
        return rule;
    }

    /**
     * Set the scaling parameter.
     *
     * This automatically set the scaling strategy to {@link #USER_SCALING}.
     *
     * @param value
     *            - The value of gamma. Must be strictly positive.
     */
    public void setScale(double value) {
        if (value <= 0.0) {
            throw new IllegalArgumentException(
                    "scale factor must be strictly positive");
        }
        gamma = value;
        rule = InverseHessianApproximation.BY_USER;
    }

    /**
     * Get the scaling parameter.
     *
     * @return The current value of {@code gamma}, the scaling parameter of the
     *         initial approximation of the inverse Hessian (that is the
     *         preconditioner {@code H0}, or the identity if no preconditioner
     *         is given).
     */
    public double getScale() {
        return gamma;
    }

    /**
     * Get slot index of a saved pair of variables and gradient differences.
     *
     * The offset {@code k} must be between {@code 0} and {@code mp}
     * (inclusive); {@code slot(0)} is the index of the slot just after the last
     * saved one, that is the one which will be used for the next update;
     * {@code slot(1)} is the last saved pair (newest one), {@code slot(2)} is
     * the previous pair, ..., {@code slot(mp)} is the oldest saved pair.
     *
     * In principle, {@code k} in the range {@code 1} to {@code mp} (inclusive)
     * is used to apply the operator; while {@code k = 0} is only used to update
     * the operator.
     *
     * @param k
     *            - The offset.
     * @return The index of {@code k}-th slot relative to the current iteration.
     *
     */
    protected int slot(int k) {
        if (k < 0 || k > mp) {
            throw new IndexOutOfBoundsException(
                    "BFGS slot index is out of bounds");
        }
        return (mark - k) % m;
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

        /* Initialize work vectors. */
        Vector tmp = null;
        if (H0 == null) {
            /* No preconditioner, no needs for a scratch vector. */
            tmp = dst;
        } else {
            /* With a preconditioner, a scratch vector is needed. */
            if (this.tmp == null) {
                this.tmp = inputSpace.create();
            }
            tmp = this.tmp;
        }

        /* First loop of the recursion (from the newest saved pair to the
         * oldest one). */
        tmp.copyFrom(src);
        for (int k = 1; k <= mp; ++k) {
            int j = slot(k);
            if (rho[j] > 0.0) {
                beta[j] = rho[j] * tmp.dot(s[j]);
                tmp.combine(1.0, tmp, -beta[j], y[j]);
            } else {
                beta[j] = 0.0;
            }
        }

        /* Apply approximation of inverse Hessian. */
        if (H0 != null) {
            H0.apply(tmp, dst);
        }
        if (gamma != 1.0) {
            dst.scale(gamma);
        }

        /* Second loop of the recursion (from the oldest saved pair to the
         * newest one). */
        for (int k = mp; k >= 1; --k) {
            int j = slot(k);
            if (rho[j] > 0.0) {
                double phi = rho[j] * dst.dot(y[j]);
                dst.combine(1.0, dst, beta[j] - phi, s[j]);
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
     *
     * @param x1
     *            - The new variables.
     * @param x0
     *            - The previous variables.
     * @param g1
     *            - The gradient at {@code x1}.
     * @param g0
     *            - The gradient at {@code x0}.
     * @throws IncorrectSpaceException
     */
    public void update(Vector x1, Vector x0, Vector g1, Vector g0)
            throws IncorrectSpaceException {
        /* Store the variables and gradient differences in the slot
         * just after the mark (which is the index of the last saved
         * pair or -1 if none).
         */
        int j = slot(0);
        s[j].combine(1.0, x1, -1.0, x0);
        y[j].combine(1.0, g1, -1.0, g0);

        /* Compute RHO[j] and GAMMA.  If the update formula for GAMMA does
         * not yield a strictly positive value, the strategy is to keep the
         * previous value. */
        double sty = s[j].dot(y[j]);
        if (sty <= 0.0) {
            /* This pair will be skipped.  This may however indicate a problem,
             * see Nocedal & Wright "Numerical Optimization", section 8.1,
             * p. 201 (1999). */
            rho[j] = 0.0;
        } else {
            /* Compute RHO[j] and GAMMA. */
            rho[j] = 1.0/sty;
            if (rule == InverseHessianApproximation.BY_STY_OVER_YTY
                    || (rule == InverseHessianApproximation.BY_INITIAL_STY_OVER_YTY && mark == 0)) {
                double ynorm = y[j].norm2();
                gamma = (sty/ynorm)/ynorm;
            } else if (rule == InverseHessianApproximation.BY_STS_OVER_STY
                    || (rule == InverseHessianApproximation.BY_INITIAL_STS_OVER_STY && mark == 0)) {
                double snorm = s[j].norm2();
                gamma = (snorm/sty)*snorm;
            }
            /* Update the mark and the number of saved pairs. */
            ++mark;
            if (mp < m) {
                ++mp;
            }
        }
    }

}
