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
 * Compiled ranges.
 * 
 * @author Éric Thiébaut.
 */
public class CompiledRange {
    /* The members of a range are publicly accessible. */
    public final int offset;
    public final int stride;
    public final int number;
    public final boolean nothing;

    public CompiledRange(Range rng, int length) {
        int first = fixIndex(rng.first, length);
        int last = fixIndex(rng.last, length);
        int step = rng.step;
        this.offset = first;
        this.stride = step;
        this.number = count(first, last, step);
        this.nothing = (first == 0 && number == length && (step == 1 || length == 1));
    }

    public CompiledRange(Range rng, int length, int offset, int stride) {
        int first = fixIndex(rng.first, length);
        int last = fixIndex(rng.last, length);
        int step = rng.step;
        this.offset = offset + first*stride;
        this.stride = step*stride;
        this.number = count(first, last, step);
        this.nothing = (first == 0 && number == length && (step == 1 || length == 1));
    }

    public static final int count(int first, int last, int step) {
        if ((step > 0 && first <= last) || (step < 0 && first >= last)) {
            return (last - first)/step + 1;
        } else {
            return 0;
        }
    }

    /** Get the first position of the compiled range. */
    final public int getOffset() {
        return offset;
    }

    /** Get the stepping of the compiled range. */
    final public int getStride() {
        return stride;
    }

    /** Get the number of positions in a compiled range. */
    final public int getNumber() {
        return number;
    }

    /** Check whether a range has no effects. */
    final public boolean doesNothing() {
        return nothing;
    }

    /**
     * Get a range index accounting for the rules for negative values.
     * @param index  - The index.
     * @param length - The number of elements along the dimension
     *                 of interest.
     * @return The corrected index.
     */
    final public static int fixIndex(int index, int length) {
        return (index >= 0 ? index : length - index);
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
