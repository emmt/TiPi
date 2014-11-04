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
     * A rank-one operator is A = u.v' thus:
     * 
     * A.x = (v'.x) u = <v|x> u
     * 
     * @param u
     *            the left vector
     * @param v
     *            the right vector
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
            this.u = outputSpace.clone(u);
            this.v = inputSpace.clone(v);
        } else {
            this.u = u;
            this.v = v;
        }
    }

    protected void privApply(final Vector src, Vector dst, int job) {
        if (job == DIRECT) {
            outputSpace.axpby(inputSpace.dot(this.v, src), this.u, 0.0, dst);
        } else if (job == ADJOINT) {
            inputSpace.axpby(outputSpace.dot(this.u, src), this.v, 0.0, dst);
        } else {
            throw new NotImplementedException();
        }
    }

    /**
     * Get the left vector of a rank-one linear operator.
     * @return The left vector of the rank-one linear operator.
     */
    public Vector getU() {
        return u;
    }

    /* FIXME: provide means to clone the left/right vectors. */
    
    /**
     * Set the left vector of a rank-one linear operator.
     * @param u  the new left vector.
     * @throws IncorrectSpaceException
     */
    public void setU(Vector u) throws IncorrectSpaceException {
        if (u.getSpace() != outputSpace) {
            throw new IncorrectSpaceException();
        }
        this.u = u;
    }

    /**
     * Get the right vector of a rank-one linear operator.
     * @return The right vector of the rank-one linear operator.
     */
    public Vector getV() {
        return v;
    }

    /**
     * Set the right vector of a rank-one linear operator.
     * @param v  the new right vector.
     * @throws IncorrectSpaceException
     */
    public void setV(Vector v) throws IncorrectSpaceException {
        if (v.getSpace() != inputSpace) {
            throw new IncorrectSpaceException();
        }
        this.v = v;
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
