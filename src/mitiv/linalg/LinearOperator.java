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

import mitiv.base.mapping.DifferentiableMapping;
import mitiv.exception.IllegalLinearOperationException;
import mitiv.exception.IncorrectSpaceException;
import mitiv.exception.NotImplementedException;

public abstract class LinearOperator extends DifferentiableMapping {
    /** Job value to apply the linear operator. */
    public static int DIRECT = 0;

    /** Job value to apply the adjoint of the linear operator. */
    public static int ADJOINT = 1;

    /** Job value to apply the inverse of the linear operator. */
    public static int INVERSE = 2;

    /** Job value to apply the inverse of the adjoint of the linear operator. */
    public static int INVERSE_ADJOINT = (ADJOINT|INVERSE);

    /** Job value to apply the inverse of the adjoint of the linear operator. */
    public static int ADJOINT_INVERSE = (ADJOINT|INVERSE);

    /**
     * Create a new linear operator which operates in the same vector space
     * (endomorphism).
     *
     * @param vsp
     *        The vector space.
     */
    public LinearOperator(VectorSpace vsp) {
        super(vsp);
    }

    /**
     * Create a new linear operator.
     *
     * <p> This method is protected as it may not be suitable for all linear
     * operators. </p>
     *
     * @param inp
     *        The input vector space.
     * @param out
     *        The output vector space.
     */
    protected LinearOperator(VectorSpace inp, VectorSpace out) {
        super(inp, out);
    }

    /**
     * Apply a given operation implemented by a linear operator.
     *
     * @param dst
     *        The destination vector.
     * @param src
     *        The source vector.
     * @param job
     *        The type of operation to perform ({@link #DIRECT},
     *        {@link #ADJOINT}, {@link INVERSE} or {@link INVERSE_ADJOINT}).
     *
     * @throws IncorrectSpaceException
     *         If {@code job} is {@link #DIRECT}, or {@link INVERSE_ADJOINT}
     *         (resp. {@link #ADJOINT} or {@link INVERSE}), {@code src} (resp.
     *         {@code dst}) must belongs to the input vector space of the
     *         operator and {@code dst} (resp. {@code src}) must belongs to the
     *         output vector space of the operator.
     *
     * @throws IllegalLinearOperationException
     *         The value of {@code job} is incorrect.
     *
     * @throws NotImplementedException
     *         The operation {@code job} is not implemented or not possible.
     */
    public void apply(Vector dst, Vector src, int job)
            throws IncorrectSpaceException, IllegalLinearOperationException,
            NotImplementedException {
        if (job == DIRECT || job == (INVERSE|ADJOINT)) {
            if (! outputSpace.owns(dst)) {
                throw new IncorrectSpaceException("Destination does not belong to the output space");
            }
            if (! inputSpace.owns(src)) {
                throw new IncorrectSpaceException("Source does not belong to the input space");
            }
        } else if (job == ADJOINT || job == INVERSE) {
            if (! inputSpace.owns(dst)) {
                throw new IncorrectSpaceException("Destination does not belong to the input space");
            }
            if (! outputSpace.owns(src)) {
                throw new IncorrectSpaceException("Source does not belong to the output space");
            }
        } else {
            throw new IllegalLinearOperationException();
        }
        this._apply(dst, src, job);
    }

    /**
     * Implement the operations performed by a linear operator out-of-place.
     *
     * <p> This protected method is called by the {@link #apply} method after
     * checking of the arguments. The operation is performed out-of-place which
     * means that source and destination vectors are guaranteed to be
     * different. </p>
     *
     * @param dst
     *        The destination vector.
     * @param src
     *        The source vector.
     * @param job
     *        The type of operation to perform.
     *
     * @throws NotImplementedException
     *         Operation is not supported.
     */
    protected abstract void _apply(Vector dst, Vector src, int job)
            throws NotImplementedException;

    @Override
    protected void _apply(Vector dst, Vector src) {
        _apply(dst, src, DIRECT);
    }

    @Override
    protected void _applyJacobian(Vector y, Vector x, Vector v) {
        _apply(y, v, ADJOINT);
    }

    /**
     * Check consistency of the arguments of a linear problem.
     *
     * <p>Check that <b>A</b>.<b><i>x</i></b>&nbsp;=&nbsp;<b><i>b</i></b> makes
     * sense and, optionally, also check that <b>A</b> is an endomorphism
     * (<i>i.e.</i> its input and output spaces are the same).</p>
     *
     * @param A
     *        The <i>left-hand-side</i> (LHS) matrix of the problem.
     * @param b
     *        The <i>right-hand-side</i> (RHS) vector of the problem (must
     *        belongs to output space of <b>A</b>).
     * @param x
     *        A vector to store the solution (must belongs to the input space of
     *        <b>A</b>).
     * @param endomorphism
     *        Assert that output and input spaces are the same?
     *
     * @throws IncorrectSpaceException
     *         Not all vectors belong to the correct vector spaces.
     */
    public static void checkLinearProblem(LinearOperator A, Vector b, Vector x,
            boolean endomorphism) throws IncorrectSpaceException {
        if (!x.belongsTo(A.getInputSpace()) || !b.belongsTo(A.getOutputSpace())) {
            throw new IncorrectSpaceException();
        }
        if (endomorphism && A.getInputSpace() != A.getOutputSpace()) {
            throw new IllegalArgumentException("LHS linear operator is not an endomorphism");
        }
    }

    /**
     * Check the adjoint of the operator.
     *
     * @param x
     *        A vector of the input space.
     * @param y
     *        A vector of the output space.
     *
     * @return The relative difference between {@code <A.x|y>} and
     *         {@code <x|A'.y>}.
     */
    public double checkAdjoint(Vector x, Vector y) {
        final Vector Ax = outputSpace.create();
        this.apply(Ax, x);
        final Vector Aty = inputSpace.create();
        this.apply(Aty, y, ADJOINT);
        final double a = y.dot(Ax);
        final double b = x.dot(Aty);
        if (a == b) {
            return 0.0;
        } else {
            return Math.abs(a - b)/Math.max(Math.abs(a),  Math.abs(b));
        }
    }

}
