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

package mitiv.optim;

import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

public abstract class SimpleBoxedSet extends BoxedSet {

    protected SimpleBoxedSet(VectorSpace space) {
        super(space);
    }

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

