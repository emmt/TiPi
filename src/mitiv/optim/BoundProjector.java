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
     * Project a direction to the null-space of the linearized constraints.
     *
     * @param x      - The variables (in principle they should be feasible).
     * @param d      - The direction to be projected.
     * @param ascent - If true `d` is an ascent direction, otherwise
     *                 `d` is a descent direction.  Typically, one set
     *                 `ascent` true if `d` is the gradient.
     * @param dp     - The projected direction (can be the same as {@code d}).
     */
    public void projectDirection(Vector x, Vector d, boolean ascent,
            Vector dp) {
        if (! x.belongsTo(space) || ! d.belongsTo(space) ||
                ! dp.belongsTo(space)) {
            throw new IncorrectSpaceException();
        }
        _projectDirection(x, d, ascent, dp, null);
    }

    /**
     * Project a direction to the null-space of the linearized constraints.
     *
     * @param x      - The variables (in principle they should be feasible).
     * @param d      - The direction to be projected.
     * @param ascent - If true {@code d} is an ascent direction, otherwise
     *                 {@code d} is a descent direction.  Typically, one set
     *                 {@code ascent} true if {@code d} is the gradient.
     * @param dp     - The projected direction (can be the same as {@code d}).
     * @param bnd    - An array of two values used to store step bounds, the
     *                 first value is the ``<i>free path length</i>'' (the
     *                 largest step length which can be taken along the
     *                 projected direction without crossing any bounds), the
     *                 second value is the smallest step length above which
     *                 the variables will remain unchanged because of bounds.
     *                 If {@code ascent} is false, then the line search seeks
     *                 for improved variables as: {@code x + alpha*d};
     *                 otherwise, the line search seeks for improved variables
     *                 as: {@code x - alpha*d}.  In both cases {@code alpha}
     *                 is the nonnegative step length to which the computed
     *                 bounds apply.
     */
    public void projectDirection(Vector x, Vector d, boolean ascent,
            Vector dp, double[] bnd) {
        if (! x.belongsTo(space) || ! d.belongsTo(space) ||
                ! dp.belongsTo(space)) {
            throw new IncorrectSpaceException();
        }
        if (bnd != null && bnd.length != 2) {
            throw new IllegalArgumentException("Bad array size for step bounds");
        }
        _projectDirection(x, d, ascent, dp, bnd);
    }

    /**
     * Protected method to project a direction to the null-space of the
     * linearized constraints.
     *
     * <p>
     * This abstract method must be overridden by instantiable sub-classes,
     * it is guaranteed to be called with checked arguments.  The input and
     * output vectors can be the same.
     * </p>
     * @param x      - The variables (in principle they should be feasible).
     * @param d      - The input direction to be projected.
     * @param ascent - If true `d` is an ascent direction, otherwise
     *                 `d` is a descent direction.
     * @param dp     - The projected direction.
     * @param bnd    - Bounds for the step length.
     */
    protected abstract void _projectDirection(Vector x, Vector d,
            boolean ascent, Vector dp, double[] bnd);

    /** Convert a double to a float taking car of maximum allowed absolute values. */
    public final float convertToFloat(double value) {
        if (value < -Float.MAX_VALUE) {
            return Float.NEGATIVE_INFINITY;
        } else if (value < -Float.MAX_VALUE) {
            return Float.POSITIVE_INFINITY;
        } else {
            return (float)value;
        }
    }

    /**
     * Check whether a lower bound is valid (throw an IllegalArgumentException otherwise).
     * @param lowerBound - The lower bound.
     * @param single     - Bound will be used in single precision.
     */
    public final void checkLowerBound(double lowerBound, boolean single) {
        if (Double.isNaN(lowerBound)
                || (Double.isInfinite(lowerBound) && lowerBound != Double.NEGATIVE_INFINITY)
                || (single && convertToFloat(lowerBound) == Float.POSITIVE_INFINITY)) {
            throw new IllegalArgumentException("Invalid lower bound value");
        }
    }

    /**
     * Check whether an upper bound is valid (throw an IllegalArgumentException otherwise).
     * @param upperBound - The upper bound.
     * @param single     - Bound will be used in single precision.
     */
    public final void checkUpperBound(double upperBound, boolean single) {
        if (Double.isNaN(upperBound)
                || (Double.isInfinite(upperBound) && upperBound != Double.POSITIVE_INFINITY)
                || (single && convertToFloat(upperBound) == Float.NEGATIVE_INFINITY)) {
            throw new IllegalArgumentException("Invalid upper bound value");
        }
    }

    /**
     * Check whether upper and lower bounds are valid (throw an IllegalArgumentException otherwise).
     * @param lowerBound - The lower bound.
     * @param upperBound - The upper bound.
     * @param single     - Bounds will be used in single precision.
     */
    public final void checkBounds(double lowerBound, double upperBound, boolean single) {
        checkLowerBound(lowerBound, single);
        checkUpperBound(upperBound, single);
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException("Lower bound must be less or equal upper bound");
        }
    }

    /** Get the minimum of two single precision floating point values. */
    public static final double min(double a, double b) {
        return (a <= b ? a : b);
    }

    /** Get the maximum of two single precision doubleing point values. */
    public static final double max(double a, double b) {
        return (a >= b ? a : b);
    }

    /** Force a variable in an interval. */
    public static final double clamp(double x, double xmin, double xmax) {
        if (x <= xmin) return xmin;
        if (x >= xmax) return xmax;
        return x;
    }

    /** Get the minimum of two single precision floating point values. */
    public static final float min(float a, float b) {
        return (a <= b ? a : b);
    }

    /** Get the maximum of two single precision floating point values. */
    public static final float max(float a, float b) {
        return (a >= b ? a : b);
    }

    /** Force a variable in an interval. */
    public static final float clamp(float x, float xmin, float xmax) {
        if (x <= xmin) return xmin;
        if (x >= xmax) return xmax;
        return x;
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
