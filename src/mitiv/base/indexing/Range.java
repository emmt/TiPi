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

package mitiv.base.indexing;

/**
 * Manage ranges along array dimensions.
 * 
 * <p>
 * Symbolically, ranges are specified as [<i>start</i>:<i>stop</i>:<i>step</i>]
 * or just as [<i>start</i>:<i>stop</i>] (the step is optional).
 * As in Java, indexes of a range start at {@code 0} (also
 * {@link Range#FIRST}) but may be negative.
 * Starting/ending indexes of a range are interpreted as offsets relative
 * to the length of the dimension of interest if they are strictly
 * negative.  That is {@code -1} (also {@link Range#LAST}) is the last element
 * along a dimension, {@code -2} is the penultimate element, etc.  This
 * rule is however only applied once: if <i>j</i> is a start/stop index,
 * then <i>j</i> (if <i>j</i>&nbsp;&ge;&nbsp;{@code 0}) or
 * <i>j</i>&nbsp;+&nbsp;<i>n</i> (if <i>j</i>&nbsp;&lt;&nbsp;{@code 0}
 * with <i>n</i> the length of the dimension) must be greater or equal 0
 * and strictly less than <i>n</i>, the length of the dimension of
 * interest.
 * </p><p>
 * The convention is that the element corresponding to the first index of
 * a range is always considered while the last one may not be reached.
 * In pseudo-code, the rules are:
 * <pre>
 *     if (step > 0) {
 *         for (int index = first; index <= last; index += step) {
 *             ...
 *         }
 *     } else if (step < 0) {
 *         for (int index = first; index >= last; index += step) {
 *             ...
 *         }
 *     }</pre>
 * The step may have the special value {@code 0} to use a step equals to
 * &plusmn;1 depending on the ordering of the first and last element of
 * the range after taking into account the length of the dimension of
 * interest.  Otherwise, the sign of the step must be in agreement with
 * the ordering of the first and last element of the range.
 * </p>
 *
 * @author Éric Thiébaut.
 */
public class Range {
    /* If strictly negative, 'first' and 'last' are relative to the end of the available space.
     * If 'step' is zero, it means that it is automatically determined from the endpoints of the range.
     */
    protected int first;
    protected int last;
    protected int step;

    /** First element along a dimension. */
    final public static int FIRST = 0;

    /** Last element along a dimension. */
    final public static int LAST = -1;

    final public static Range ALL = new Range(FIRST, LAST, 1);
    final public static Range REVERSE = new Range(LAST, FIRST, -1);
    final public static Range ODD = new Range(0, LAST, 1);
    final public static Range EVEN = new Range(1, LAST, 1);

    /** Create a full range. */
    public Range() {
        this.first = FIRST;
        this.last = LAST;
        this.step = 1;
    }

    /**
     * Make a range from an array of 0, 2 or 3 integers.
     * @param r - If {@code null} or of length 0, create a full range; if
     *            length is 2 (or 3), create a range starting at {@code r[0]}, ending
     *            at {@code r[1]} and with step 1 (or {@code r[2]}).
     */
    public Range(int[] r) {
        switch (r == null ? 0 : r.length) {
        case 0:
            this.first = FIRST;
            this.last = LAST;
            this.step = 1;
            break;
        case 2:
            this.first = r[0];
            this.last = r[1];
            this.step = 0;
            break;
        case 3:
            this.first = r[0];
            this.last = r[1];
            this.step = r[2];
            break;
        default:
            throw new IllegalArgumentException("Invalid range settings.");
        }
    }

    /**
     * Create a range.
     * 
     * @param first - Starting index of the range.
     * @param last  - Ending index of the range.
     */
    public Range(int first, int last) {
        this.first = first;
        this.last = last;
        this.step = 0;
    }

    /**
     * Create a range with a first and last index and a step length.
     * @param first - Starting index of the range.
     * @param last  - Ending index of the range.
     * @param step  - Stepping.
     */
    public Range(int first, int last, int step) {
        this.first = first;
        this.last = last;
        this.step = step;
    }

    /**
     * Engrave the range given the length of the dimension of interest.
     * 
     * <p>
     * This method must be used to apply the conventions for range
     * settings (in particular negative values to indicate indexing
     * from the end) and validate them.
     * </p>
     * @param length - The number of elements along the dimension
     *                 of interest.
     * @return A checked range.
     */
    final public EngravedRange engrave(int length) {
        return new EngravedRange(this, length);
    }

    /** Get the first position of the range. */
    final public int getFirst() {
        return first;
    }

    /**
     * Get the first position of the range taking into account the length
     * of the dimension of interest.
     * @param length - The number of elements along the dimension
     *                 of interest.
     */
    final public int getFirst(int length) {
        return (first >= 0 ? first : length - first);
    }

    /** Set the first position of the range. */
    final public void setFirst(int first) {
        this.first = first;
    }

    /** Get the last position of the range. */
    final public int getLast() {
        return last;
    }

    /**
     * Get the last position of the range taking into account the length
     * of the dimension of interest.
     * @param length - The number of elements along the dimension
     *                 of interest.
     */
    final public int getLast(int length) {
        return (last >= 0 ? last : length - last);
    }

    /** Set the last position of the range. */
    final public void setLast(int last) {
        this.last = last;
    }

    /** Get the stepping of the range. */
    final public int getStep() {
        return step;
    }

    /**
     * Get the stepping of the range taking into account the length
     * of the dimension of interest.
     * @param length - The number of elements along the dimension
     *                 of interest.
     */
    final public int getStep(int length) {
        if (step != 0) {
            return step;
        } else {
            return (getFirst(length) <= getLast(length) ? +1 : -1);
        }
    }

    /** Set the stepping of the range. */
    final public void setStep(int step) {
        this.step = step;
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
