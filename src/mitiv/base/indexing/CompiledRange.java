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
 * Compiled ranges.
 *
 * <p>
 * A CompiledRange instance is an immutable object which stores all the needed
 * information to walk through the indices specified by a {@link Range} object.
 * It is necessary to distinguish a {@link Range} object and the corresponding
 * {@link CompliedRange} object because the former may have relative end-points
 * while the latter give absolute information and thus requires to known all
 * details about the length of the considered dimension but also the offset of
 * the first element along this dimension and the spacing of successive
 * elements. Walking through the elements selected by a range is typically done
 * by:
 *
 * <pre>
 * Range rng = ...;
 * CompiledRange crng = new CompiledRange(rng, length, offset, stride);
 * for (int i = 0; i < crng.getNumber(); ++i) {
 *     int j = crng.getOffset() + i*crng.getStride();
 *     a[j] = ...;
 * }
 * </pre>
 * </p>
 *
 * @author Éric Thiébaut.
 */
public class CompiledRange {
    /* The members of a range are publicly accessible but immutable. */

    /** The index of the first element. */
    public final int offset;

    /** The spacing of successive elements. */
    public final int stride;

    /** The number of elements (cannot be zero). */
    public final int number;

    /** Is the range unspecified (which means that all positions along the
     *  corresponding dimension have to be considered)? */
    public final boolean nothing;

    /**
     * Compile a range for simple indexing.
     * <p>
     * This method compiles a range along a dimension where elements have no
     * offset and are contiguous.
     * </p>
     *
     * @param rng
     *            - The range to compile.
     * @param length
     *            - The length of the dimension.
     *
     * @throws IllegalRangeException
     *             The range is empty or badly specified.
     * @throws IndexOutOfBoundsException
     *             The end-points of the range are out of bounds.
     */
    public CompiledRange(Range rng, int length) {
        if (rng == null) {
            this.offset = 0;
            this.stride = 1;
            this.number = length;
            this.nothing = true;
        } else {
            int first = rng.getFirst(length);
            int last = rng.getLast(length);
            int step = rng.getStep();
            this.number = count(first, last, step, length);
            this.offset = first;
            this.stride = step;
            this.nothing = (first == 0 && number == length && (step == 1 || length == 1));
        }
    }

    /**
     * Compile a range for general indexing.
     * <p>
     * This method compiles a range along a dimension where elements have
     * specific offset and spacing.
     * </p>
     *
     * @param rng
     *            - The range to compile.
     * @param length
     *            - The length of the dimension.
     * @param offset
     *            - The offset of the first element along the dimension.
     * @param stride
     *            - The spacing of elements along the dimension.
     *
     * @throws IllegalRangeException
     *             The range is empty or badly specified.
     * @throws IndexOutOfBoundsException
     *             The end-points of the range are out of bounds.
     */
    public CompiledRange(Range rng, int length, int offset, int stride) {
        if (rng == null) {
            this.offset = offset;
            this.stride = stride;
            this.number = length;
            this.nothing = true;
        } else {
            int first = rng.getFirst(length);
            int last = rng.getLast(length);
            int step = rng.getStep();
            this.number = count(first, last, step, length);
            this.offset = offset + first*stride;
            this.stride = step*stride;
            this.nothing = (first == 0 && number == length && (step == 1 || length == 1));
        }
    }

    private static final int count(int first, int last, int step, int length) {
        if (first <= last) {
            if (0 <= first && last < length) {
                if (step > 0) {
                    return (last - first)/step + 1;
                } else if (step < 0) {
                    emptyRange();
                } else {
                    illegalStep();
                }
            }
        } else {
            if (0 <= last && first < length) {
                if (step < 0) {
                    return (last - first)/step + 1;
                } else if (step > 0) {
                    emptyRange();
                } else {
                    illegalStep();
                }
            }
        }
        throw new IndexOutOfBoundsException("Range is outside bounds");
    }

    private final static void emptyRange() {
        throw new IllegalRangeException("Empty range");
    }

    private final static void illegalStep() {
        throw new IllegalRangeException("Illegal 0 step");
    }

    /** Get the index of the first element in the compiled range. */
    final public int getOffset() {
        return offset;
    }

    /** Get the spacing of successive elements in the compiled range. */
    final public int getStride() {
        return stride;
    }

    /** Get the number of elements in a compiled range. */
    final public int getNumber() {
        return number;
    }

    /** Check whether a range has no effects. */
    final public boolean doesNothing() {
        return nothing;
    }

}
