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
import mitiv.exception.NotImplementedException;

public class DiagonalOperator extends LinearEndomorphism {

    protected Vector diag;

    /**
     * Create a new diagonal operator.
     *
     * This protected method is intended for inherited classes which
     * override the methods and do not need to store the elements of the
     * diagonal.
     *
     * @param space - The vector space on which operates the operator.
     */
    protected DiagonalOperator(VectorSpace space) {
        super(space);
        this.diag = null;
    }

    /**
     * Create a new diagonal operator.
     *
     * @param diag - A vector whose elements are the diagonal of the
     *               operator and whose vector space is the vector
     *               space on which operates the operator.
     */
    public DiagonalOperator(Vector diag) {
        super(diag.space);
        this.diag = diag;
    }

    /**
     * Get the elements of a diagonal operator .
     *
     * @return A vector whose elements are the diagonal of the
     *         operator and whose vector space is the vector
     *         space on which operates the operator.
     */
    public Vector getDiagonal() {
        return diag;
    }

    /**
     * Set the elements of a diagonal operator .
     *
     * @param diag - A vector whose elements are the diagonal of the
     *               operator and whose vector space is the vector
     *               space on which operate the operator.
     *
     * @throws IncorrectSpaceException {@code diag} must belongs to
     * the vector space on which operates the operator.
     */
    public void setDiagonal(Vector diag) {
        if (diag == null || ! diag.belongsTo(space)) {
            throw new IncorrectSpaceException();
        }
        this.diag = diag;
    }

    @Override
    protected void _apply(Vector dst, Vector src, int job)
            throws IncorrectSpaceException, NotImplementedException {
        if (job != DIRECT && job != ADJOINT) {
            throw new NotImplementedException();
        }
        space._multiply(dst, src, diag);
    }

    @Override
    protected void _apply(Vector vec, int job)
            throws IncorrectSpaceException, NotImplementedException {
        if (job != DIRECT && job != ADJOINT) {
            throw new NotImplementedException();
        }
        space._multiply(vec, vec, diag);
    }

}
