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
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.FloatShapedVectorSpace;


/**
 * Implementation of boxed set for MiTiV vectors.
 *
 * @author Éric Thiébaut.
 */
public abstract class BoxedSet {

    protected VectorSpace space;

    /** Orientation of an ascending direction. */
    static final int ASCENT = -1;

    /** Orientation of a descending direction. */
    static final int DESCENT = +1;

    protected BoxedSet(VectorSpace space)
    {
        this.space = space;
    }

    /**
     * Create a new boxed set.
     *
     * The static method creates a new boxed set for variables of a given
     * vector space.  The returned boxed set is unbounded, you may use {@code
     * setLowerBound} and {@code setUpperBound} to set the bounds.
     *
     * @param space - The variable space.
     *
     * @return A new boxed set.
     */
    public static BoxedSet create(VectorSpace space)
    {
        if (space instanceof FloatShapedVectorSpace) {
            return new FloatBoxedSet((FloatShapedVectorSpace)space);
        } else if (space instanceof DoubleShapedVectorSpace) {
            return new DoubleBoxedSet((DoubleShapedVectorSpace)space);
        } else {
            throw new IncorrectSpaceException();
        }
    }

    /**
     * Project variables into the boxed set.
     * @param xp - The output projected variables.
     * @param x  - The input variables.
     */
    public abstract void projectVariables(Vector xp, Vector x);

    /**
     * Project a direction.
     *
     * This method projects the direction {@code d} so that:
     * <pre>
     * x + orient*alpha*d
     * </pre>
     * yields a feasible position for {@code alpha > 0} sufficiently small.
     *
     * @param dp - The resulting projected direction.
     * @param x  - The current variables.
     * @param d  - The direction to project.
     * @param orient - The orientation of the direction {@code d}.  Strictly
     *              positive if {@code d} is a descent direction, strictly
     *              negative if {@code d} is an ascent direction.  For
     *              convenience, the constants {@link #DESCENT} and {@link
     *              #ASCENT} can be used to specify the orientation.
     */
    public abstract void projectDirection(Vector dp, Vector x,
            Vector d, int orient);

    /**
     * Find the non-binding constraints.
     *
     * @param w - The resulting mask whose elements are set to 1 (resp. 0) if
     *              the corresponding variables are free (resp. binded).
     * @param x - The current variables.
     * @param d - The search direction.
     * @param orient - The orientation of the search direction (see {@link
     *              #projectDirection}).
     */
    public abstract void findFreeVariables(Vector w, Vector x,
            Vector d, int orient);

    /**
     * Find the limits of the step size.
     *
     * <p> Along the search direction the new variables are computed as:
     * <pre>
     * proj(x +/- alpha*d)
     * </pre>
     * where {@code proj} is the projection onto the feasible set, {@code
     * alpha} is the step length and +/- is a plus for a descent direction and
     * a minus otherwise.  This method computes 3 specific step lengths: {@code
     * smin1} which is the step length for the first bound reached by the free
     * variables along the search direction; {@code smin2} which is the step
     * length for the first bound reached by the free variables along the
     * search direction and with a non-zero step length; {@code smax} which is
     * the step length for the last bound reached by the free variables along
     * the search direction.  </p>
     *
     * <p> In other words, for any {@code 0 <= alpha <= smin1}, no free
     * variables may exceed a bound, while, for any {@code alpha >= smax}, the
     * projected variables are the same as those obtained with {@code alpha =
     * smax}.  If {@code d} has been properly projected (e.g. by {@link
     * #projectDirection}), then {@code smin1 = smin2} otherwise {@code 0 <=
     * smin1 <= smin2} and {@code smin2 > 0}. </p>
     *
     * @param x - The current variables.
     * @param d - The search direction.
     * @param orient - The orientation of the search direction (see
     *                  {@link #projectDirection}).
     *
     * @return A 3-element array of values: {@code {smin1,smin2,smax}}.
     */
    public abstract double[] findStepLimits(Vector x, Vector d, int orient);

    /**
     * Set the lower bound of the variables.
     *
     * This method sets the lower bound of the variables which is the same for
     * all variables.  If the bound value is set to {@link
     * Double#NEGATIVE_INFINITY}, the variables will be considered as being
     * unbounded below.
     *
     * @param val - The new lower bound.
     */
    public abstract void setLowerBound(double val);

    /**
     * Set the lower bound of the variables.
     *
     * This method sets the component-wise lower bounds of the variables.  If
     * the bound value is {@code null}, the variables will be considered as
     * being unbounded below.  Any element of {@code vec} equal to {@link
     * Double#NEGATIVE_INFINITY} or {@link Float#NEGATIVE_INFINITY} will be
     * considered as being unbounded below.
     *
     * @param vec - A vector with the new lower bounds.
     */
    public abstract void setLowerBound(Vector vec);

    /**
     * Set the upper bound of the variables.
     *
     * This method sets the upper bound of the variables which is the same for
     * all variables.  If the bound value is set to {@link
     * Double#POSITIVE_INFINITY}, the variables will be considered as being
     * unbounded above.
     *
     * @param val - The new upper bound.
     */
    public abstract void setUpperBound(double val);

    /**
     * Set the upper bound of the variables.
     *
     * This method sets the component-wise upper bounds of the variables.  If
     * the bound value is {@code null}, the variables will be considered as
     * being unbounded above.  Any element of {@code vec} equal to {@link
     * Double#POSITIVE_INFINITY} or {@link Float#POSITIVE_INFINITY} will be
     * considered as being unbounded above.
     *
     * @param vec - A vector with the new upper bounds.
     */
    public abstract void setUpperBound(Vector vec);

    /**
     * Unset the lower bound(s).
     *
     * The variables will be considered as being unbounded below.
     */
    public void unsetLowerBound()
    {
        setLowerBound(Double.NEGATIVE_INFINITY);
    }

    /**
     * Unset the upper bound(s).
     *
     * The variables will be considered as being unbounded above.
     */
    public void unsetUpperBound()
    {
        setLowerBound(Double.POSITIVE_INFINITY);
    }

    /**
     * Unset the bound.
     *
     * The variables will be considered as being unbounded.
     */
    public void unsetBounds()
    {
        setLowerBound(Double.NEGATIVE_INFINITY);
        setLowerBound(Double.POSITIVE_INFINITY);
    }

}
