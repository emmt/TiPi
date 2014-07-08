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

import mitiv.exception.IllegalLinearOperationException;
import mitiv.exception.IncorrectSpaceException;

public abstract class LinearOperator {
    protected VectorSpace inputSpace;
    protected VectorSpace outputSpace;


    public static int DIRECT = 0;
    public static int ADJOINT = 1;
    public static int INVERSE = 2;
    public static int INVERSE_ADJOINT = (ADJOINT|INVERSE);
    public static int ADJOINT_INVERSE = (ADJOINT|INVERSE);

    /**
     * Create a new LinearOperator which operates in the same vector space (endomorphism).
     * 
     * @param vsp
     *            the vector space
     */
    public LinearOperator(VectorSpace vsp) {
        inputSpace = vsp;
        outputSpace = vsp;
    }

    /**
     * Create a new LinearOperator.
     * 
     * This method is protected as it may not be suitable for all linear
     * operators.
     * 
     * @param inp
     *            input vector space;
     * @param out
     *            output vector space;
     */
    protected LinearOperator(VectorSpace inp, VectorSpace out) {
        inputSpace = inp;
        outputSpace = out;
    }

    /**
     * Get the input space of a linear operator.
     * 
     * @return The input space of the linear operator.
     */
    public VectorSpace getInputSpace() {
        return inputSpace;
    }

    /**
     * Get the output space of a linear operator.
     * 
     * @return The output space of the linear operator.
     */
    public VectorSpace getOutputSpace() {
        return outputSpace;
    }

    /**
     * Check whether a linear operator is an endomorphism.
     * @return true is the input and output spaces of the linear operator are the same; false otherwise.
     */
    public boolean isEndomorphism() {
        return (outputSpace == inputSpace);
    }

    /**
     * Apply a linear operator (or its adjoint) to a vector.
     * 
     * This protected method is called by the "apply" method after checking of the
     * arguments.
     * @param src        the source vector
     * @param dst        the destination vector
     * @param job        the type of operation to apply (DIRECT, ADJOINT, etc.)
     * @throws IncorrectSpaceException If adjoint is false (resp. tour), src (resp. dst) must belongs to the input vector space
     * of the operator and dst (resp. src) must belongs to the output vector space of the operator.
     */
    protected abstract void privApply(final Vector src, Vector dst, int job)
            throws IncorrectSpaceException;

    /**
     * Apply a linear operator to a vector.
     *
     * @param src        the source vector
     * @param dst        the destination vector
     * @throws IncorrectSpaceException
     */
    public void apply(final Vector src, Vector dst)
            throws IncorrectSpaceException {
        apply(src, dst, DIRECT);
    }

    /**
     * Apply linear operator with checking.
     * @param src      The source vector.
     * @param dst      The destination vector.
     * @param job      The type of operation to perform (DIRECT, ADJOINT, etc.)
     * @throws IncorrectSpaceException
     */
    public void apply(final Vector src, Vector dst, int job)
            throws IncorrectSpaceException {
        if (job == DIRECT || job == (INVERSE|ADJOINT)) {
            if (! src.belongsTo(inputSpace) || ! dst.belongsTo(outputSpace)) {
                throw new IncorrectSpaceException();
            }
        } else if (job == ADJOINT || job == INVERSE) {
            if (! src.belongsTo(outputSpace) || ! dst.belongsTo(inputSpace)) {
                throw new IncorrectSpaceException();
            }
        } else {
            throw new IllegalLinearOperationException();
        }
        this.privApply(src, dst, job);
    }

    /**
     * Check consistency of the arguments of a linear problem.
     * 
     * Check that A.x = b makes sense and.  Optionally also check that A is an endomorphism (i.e. its input and
     * output spaces are the same).
     * 
     * @param A
     *            the LHS matrix of the problem;
     * @param b
     *            the RHS vector of the problem (must belongs to output space of
     *            A);
     * @param x
     *            a vector to store the solution (must belongs to the input
     *            space of A);
     * @param endomorphism
     *            output and input spaces must be the same?
     * @throws IncorrectSpaceException
     */
    public static void checkLinearProblem(LinearOperator A, Vector b, Vector x,
            boolean endomorphism) throws IncorrectSpaceException {
        if (!x.belongsTo(A.getInputSpace()) || !b.belongsTo(A.getOutputSpace())) {
            throw new IncorrectSpaceException();
        }
        if (endomorphism && A.getInputSpace() != A.getOutputSpace()) {
            /* FIXME: use another exception for that. */
            throw new IncorrectSpaceException();
        }
    }

    /**
     * Check the adjoint of the operator.
     * @param x  - A vector of the input space.
     * @param y  - A vector of the output space.
     * @return The relative difference between {@code <A.x|y>} and {@code <x|A'.y>}.
     */
    public double checkAdjoint(Vector x, Vector y) {
        Vector Ax = outputSpace.create();
        apply(x, Ax);
        Vector Aty = inputSpace.create();
        apply(y, Aty, ADJOINT);
        double a = outputSpace.dot(y,  Ax);
        double b = inputSpace.dot(Aty,  x);
        if (a == b) {
            return 0.0;
        } else {
            return Math.abs(a - b)/Math.max(Math.abs(a),  Math.abs(b));
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
