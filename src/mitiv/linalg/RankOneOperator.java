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

public class RankOneOperator extends LinearOperator {

    protected Vector u;
    protected Vector v;

    /**
     * Create a new rank-one linear operator.
     *
     * <p> A rank-one operator is {@code A = u.v'} thus: </p>
     * <pre>
     * A.x = (v'.x) u
     * </pre>
     *
     * @param u   The left vector.
     * @param v   The right vector.
     */
    public RankOneOperator(Vector u, Vector v) {
        super(v.getSpace(), u.getSpace());
        this.u = u;
        this.v = v;
    }

    public RankOneOperator(Vector u, Vector v, boolean clone)
            throws IncorrectSpaceException {
        super(v.getSpace(), u.getSpace());
        if (clone) {
            this.u = u.clone();
            this.v = v.clone();
        } else {
            this.u = u;
            this.v = v;
        }
    }

    @Override
    protected void _apply(Vector dst, Vector src, int job) {
        if (job == DIRECT) {
            dst.scale(this.v.dot(src), this.u);
        } else if (job == ADJOINT) {
            dst.scale(this.u.dot(src), this.v);
        } else {
            throw new NotImplementedException();
        }
    }

    /**
     * Get the left vector of a rank-one linear operator.
     * @return The left vector of the rank-one linear operator.
     */
    public Vector getLeftVector() {
        return u;
    }

    /**
     * Set the left vector of a rank-one linear operator.
     * @param u  the new left vector.
     * @throws IncorrectSpaceException
     */
    public void setLeftVector(Vector u) throws IncorrectSpaceException {
        if (u.getSpace() != outputSpace) {
            throw new IncorrectSpaceException();
        }
        this.u = u;
    }

    /**
     * Get the right vector of a rank-one linear operator.
     * @return The right vector of the rank-one linear operator.
     */
    public Vector getRightVector() {
        return v;
    }

    /**
     * Set the right vector of a rank-one linear operator.
     * @param v  the new right vector.
     * @throws IncorrectSpaceException
     */
    public void setRightVector(Vector v) throws IncorrectSpaceException {
        if (v.getSpace() != inputSpace) {
            throw new IncorrectSpaceException();
        }
        this.v = v;
    }

}
