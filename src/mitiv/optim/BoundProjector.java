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
 * Abstract class for bound projection.
 *
 * <p>
 * A BoundProjector can be used to implement bound constraints.  Compared
 * to a {@link ConvexSetProjector}, it has also the ability to project the
 * gradient, that is to generate (the opposite) of a feasible steepest
 * descent direction.</p>
 *
 * @author Éric Thiébaut.
 */
public abstract class BoundProjector extends ConvexSetProjector {

    protected BoundProjector(VectorSpace vsp) {
        super(vsp);
    }

    /**
     * Project the gradient of bounded variables (in-place version).
     * 
     * <p>
     * For general convex set, the projection of the gradient can only be done
     * out-of-place.  For bound constraints, the projection of the gradient
     * can be done in-place to save memory and that's why this method is
     * provided.
     * </p>
     * 
     * @param x  - The variables (must be <i>feasible</i>, that is within
     *             the bounds).
     * @param g  - On entry, the gradient or the ascent direction at
     *             <b>x</b>; on exit, the projected gradient or feasible
     *             ascent direction.
     * @see {@link #projectGradient(Vector, Vector, Vector)}.
     * @throws IncorrectSpaceException if its arguments do not belong to the
     *         correct vector space.
     */
    public void projectGradient(Vector x, Vector g) {
        if (! x.belongsTo(space) || ! g.belongsTo(space)) {
            throw new IncorrectSpaceException();
        }
        _projectDirection(x, g, g, g);
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
