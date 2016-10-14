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

import mitiv.exception.IllegalRangeException;

/**
 * Manage ranges along array dimensions.
 *
 * <p>
 * Symbolically, ranges are specified as [<i>start</i>:<i>stop</i>:<i>step</i>]
 * or just as [<i>start</i>:<i>stop</i>] (the step is optional). As in Java,
 * indexes of a range start at {@code 0} (also {@link Range#FIRST}) but may be
 * negative. Starting/ending indexes of a range are interpreted as offsets
 * relative to the length of the dimension of interest if they are strictly
 * negative.  That is {@code -1} (also {@link Range#LAST}) is the last element
 * along a dimension, {@code -2} is the penultimate element, {@code -3} is the
 * antepenultimate element, <i>etc.</i>  This rule is however only applied once:
 * assuming <i>n</i> is the length of the dimension of interest and <i>j</i> is
 * the first or last index, then <i>j</i> (if <i>j</i>&nbsp;&ge;&nbsp;{@code 0})
 * or <i>j</i>&nbsp;+&nbsp;<i>n</i> (if <i>j</i>&nbsp;&lt;&nbsp;{@code 0}) must
 * be greater or equal 0 and strictly less than <i>n</i>.
 * </p>
 * <p>
 * The convention is that the element corresponding to the first index of a
 * range is always considered (unless the range is empty as explained below)
 * while the last one may not be reached. In pseudo-code, the rules are:
 * <pre>
 *     if (step > 0) {
 *         for (int index = first; index <= last; index += step) {
 *             ...
 *         }
 *     } else if (step < 0) {
 *         for (int index = first; index >= last; index += step) {
 *             ...
 *         }
 *     } else {
 *         // step = 0 is illegal
 *         throw new IllegalRangeException();
 *     }
 * </pre>
 * Note that a range is <i>empty</i> if
 * {@code first}&nbsp;&gt;&nbsp;{@code last} and
 * {@code step}&nbsp;&gt;&nbsp;{@code 0} or if
 * {@code first}&nbsp;&lt;&nbsp;{@code last} and
 * {@code step}&nbsp;&lt;&nbsp;{@code 0} (after applying the rules for negative
 * indices).
 * </p>
 *
 * @author Éric Thiébaut.
 */
public class Range {
    /* The members of a range are publicly accessible. */
    /* If strictly negative, 'first' and 'last' are relative to the end of the available space.
     */
    private int first;
    private int last;
    private int step;

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
     * @throws IllegalRangeException
     */
    public Range(int[] r) {
        switch (r == null ? 0 : r.length) {
        case 0:
            first = FIRST;
            last = LAST;
            step = 1;
            break;
        case 2:
            first = r[0];
            last = r[1];
            step = 1;
            break;
        case 3:
            first = r[0];
            last = r[1];
            setStep(r[2]);
            break;
        default:
            throw new IllegalRangeException();
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
        this.step = 1;
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
        setStep(step);
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
        return fixIndex(first, length);
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
        return fixIndex(last, length);
    }

    /** Set the last position of the range. */
    final public void setLast(int last) {
        this.last = last;
    }

    /** Get the stepping of the range. */
    final public int getStep() {
        return step;
    }

    /** Set the stepping of the range. */
    final public void setStep(int step) {
        if (step == 0) {
            throw new IllegalRangeException("Illegal 0 step.");
        }
        this.step = step;
    }

    /**
     * Get a range index accounting for the rules for negative values.
     * @param index  - The index.
     * @param length - The number of elements along the dimension
     *                 of interest.
     * @return The corrected index.
     */
    final public static int fixIndex(int index, int length) {
        return (index >= 0 ? index : length + index);
    }

    /**
     * Check whether a range takes all elements in order.
     *
     * @param rng    - The range (can be {@code null} to mean <i>all</i>).
     * @param length - The length of the dimension of interest.
     * @return A boolean result.
     */
    final public static boolean doesNothing(Range rng, int length) {
        return (rng == null || rng.doesNothing(length));
    }

    final public boolean doesNothing(int length) {
        return (step == 1 && fixIndex(first, length) == 0
                && fixIndex(last, length) == length);
    }

    /**
     * Build a list of indices from a range.
     * @param rng    - The range (can be {@code null} to mean <i>all</i>).
     * @param length - The length of the dimension of interest.
     * @return A list of indices, can have zero length if the range is empty.
     */
    final public static int[] asIndexList(Range rng, int length) {
        int first, last, step, number;
        if (rng == null) {
            first = 0;
            step = 1;
            number = length;
        } else {
            first = fixIndex(rng.first, length);
            last = fixIndex(rng.last, length);
            step = rng.step;
            number = 0;
            if (step > 0) {
                if (first <= last) {
                    number = (last - first)/step + 1;
                }
            } else if (step < 0) {
                if (first >= last) {
                    number = (first - last)/(-step) + 1;
                }
            }
        }
        int[] idx = new int[number];
        for (int j = 0; j < number; ++j) {
            idx[j] = first + j*step;
        }
        return idx;
    }

    /**
     * Build a list of indices from the range.
     * @param length - The length of the dimension of interest.
     * @return A list of indices, can have zero length if the range is empty.
     */
    final public int[] asIndexList(int length) {
        int first = fixIndex(this.first, length);
        int last = fixIndex(this.last, length);
        int number = 0;
        if (step > 0) {
            if (first <= last) {
                number = (last - first)/step + 1;
            }
        } else if (step < 0) {
            if (first >= last) {
                number = (first - last)/(-step) + 1;
            }
        }
        int[] idx = new int[number];
        for (int j = 0; j < number; ++j) {
            idx[j] = first + j*step;
        }
        return idx;
    }
}
