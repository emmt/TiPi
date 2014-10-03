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

import mitiv.exception.IncorrectSpaceException;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

/**
 * Abstract class for projection on a convex set.
 *
 * <p>
 * A ConvexSetProjector can be used to implement convex constraints.
 * Although it operates on a vector space, a ConvexSetProjector is not
 * a linear operator.  It is an idempotent endomorphism and can be
 * applied <i>in-place</i> that is with the same input and output vector.</p>
 *
 * @author Éric Thiébaut.
 */
public abstract class ConvexSetProjector {
    final protected VectorSpace space;

    protected ConvexSetProjector(VectorSpace vsp) {
        space = vsp;
    }

    /** Get the vector space to which operate the projector. */
    public VectorSpace getSpace() {
        return space;
    }

    /** Get the vector space to which operate the projector. */
    public VectorSpace getInputSpace() {
        return space;
    }

    /** Get the vector space to which operate the projector. */
    public VectorSpace getOutputSpace() {
        return space;
    }

    /**
     * Apply the projector.
     * 
     * <p>
     * Given input variables <i>x</i>, the projection produces feasible
     * output variables <i>xp</i> that are within the bounds.  The input
     * and output variables can be stored in the same vector (<i>i.e.</i>
     * the method can be applied <i>in-place</i>).
     * </p>
     * @param x  - The source.
     * @param xp - The destination.
     * @throws IncorrectSpaceException if its arguments do not belong to the
     *         correct vector space.
     */
    public void apply(Vector x, Vector xp) {
        if (x == null || ! x.belongsTo(space) ||
                xp == null || ! xp.belongsTo(space)) {
            throw new IncorrectSpaceException();
        }
        _apply(x, xp);
    }

    /**
     * Apply the projector (in-place version).
     * 
     * @param x  - On entry, the unconstrained variables; on exit, the
     *             projected variables.
     * @throws IncorrectSpaceException if its arguments do not belong to the
     *         correct vector space.
     */
    public void apply(Vector x) {
        if (x == null || ! x.belongsTo(space)) {
            throw new IncorrectSpaceException();
        }
        _apply(x, x);
    }

    /**
     * Protected method to apply the projector to the variables.
     * 
     * <p>
     * This abstract method must be overridden by instantiable sub-classes,
     * it is guaranteed to be called with checked arguments.  The input and
     * output vectors can be the same.
     * </p>
     * @param x  - The source.
     * @param xp - The destination.
     */
    protected abstract void _apply(Vector x, Vector xp);

    /** Get the minimum of two single precision floating point values. */
    protected static final float min(float a, float b) {
        return (a <= b ? a : b);
    }

    /** Get the maximum of two single precision floating point values. */
    protected static final float max(float a, float b) {
        return (a >= b ? a : b);
    }

    /** Get the minimum of two double precision floating point values. */
    protected static final double min(double a, double b) {
        return (a <= b ? a : b);
    }

    /** Get the maximum of two double precision floating point values. */
    protected static final double max(double a, double b) {
        return (a >= b ? a : b);
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
