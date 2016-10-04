/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014-2016 the MiTiV project, http://mitiv.univ-lyon1.fr/
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

package mitiv.base.mapping;

import mitiv.exception.IncorrectSpaceException;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

/**
 * This abstract class implements differentiable mappings between two vector spaces.
 *
 * @author Éric Thiébaut
 */
public abstract class DifferentiableMapping extends Mapping {

    protected DifferentiableMapping(VectorSpace inp, VectorSpace out) {
        super(inp, out);
    }

    protected DifferentiableMapping(VectorSpace space) {
        super(space);

    }

    /**
     * Apply the Jacobian of a differentiable mapping to a vector.
     *
     * <p>
     * A mapping {@code m(x)} is a function from an input vector space, say {@code E}, to an output
     * vector space, say {@code F}.  The Jacobian {@code J(x)} of the mapping {@code m(x)} collects
     * all the partial derivatives of the mapping.  Applying the Jacobian to a vector {@code v}
     * amounts to computing:
     * <pre>
     *        ____
     *        \     d m_j(x)
     * y_i =   )    -------- v_j
     *        /___    d x_i
     *          j
     * </pre>
     * Depending on the conventions, the above equation may be considered as multiplying {@code v} by
     * the adjoint of the Jacobian.
     *
     * @param y - The vector to store the result: {@code y = Jac(x).v}
     * @param x - The point at which is evaluated the mapping.
     * @param v - The vector to which apply the Jacobian.
     */
    public final void applyJacobian(Vector y, Vector x, Vector v) {
        if (! inputSpace.owns(y)) {
            throw new IncorrectSpaceException("Destination does not belong to the input space");
        }
        if (! inputSpace.owns(x)) {
            throw new IncorrectSpaceException("Variables does not belong to the input space");
        }
        if (! outputSpace.owns(v)) {
            throw new IncorrectSpaceException("Source does not belong to the output space");
        }
        _applyJacobian(y, x, v);
    }

    /** Private method implementing the Jacobian. */
    protected abstract void _applyJacobian(Vector y, Vector x, Vector v);

}

