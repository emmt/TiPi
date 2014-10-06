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
 * An engraved range is a constant range with settings validated upon creation.
 * @author Éric
 */
public class EngravedRange {

    /** The first index of the range. */
    public final int first;

    /** The last index of the range. */
    public final int last;

    /** The stepping. */
    public final int step;

    /**
     * Build a constant (final) range with checked arguments.
     * 
     * @param first  - First index to take (must be reachable).
     * @param last   - Last possible index (may be unreached but must be reachable).
     * @param step   - Step length (automatically set to +/-1 if zero).
     * @param length - The number of elements along the dimension of interest.
     */
    public EngravedRange(int first, int last, int step, int length) {
        if (first <= last) {
            if (first < 0 || last >= length) {
                outOfBounds();
            }
            if (step == 0) {
                step = 1;
            } else if (step < 0) {
                invalidStep();
            }
        } else {
            if (first >= length || last < 0) {
                outOfBounds();
            }
            if (step == 0) {
                step = -1;
            } else if (step > 0) {
                invalidStep();
            }
        }
        this.first = first;
        this.last = last;
        this.step = step;
    }

    /**
     * Build a constant (final) range with checked arguments from a range.
     * 
     * @param range  - The range.
     * @param length - The number of elements along the dimension of interest.
     */
    public EngravedRange(Range range, int length) {
        first = (range.first >= 0 ? range.first : length + range.first);
        last = (range.last >= 0 ? range.last : length + range.last);
        if (first <= last) {
            if (first < 0 || last >= length) {
                outOfBounds();
            }
            step = (range.step == 0 ? +1 : range.step);
            if (step < 0) {
                invalidStep();
            }
        } else {
            if (first >= length || last < 0) {
                outOfBounds();
            }
            step = (range.step == 0 ? -1 : range.step);
            if (step > 0) {
                invalidStep();
            }
        }
    }

    private void outOfBounds() {
        throw new IndexOutOfBoundsException("Out of bounds range parameters.");
    }

    private void invalidStep() {
        throw new IllegalArgumentException("Range step has wrong direction.");
    }

    /** Get the first index of the range. */
    final public int getFirst() {
        return first;
    }

    /** Get the last index of the range. */
    final public int getLast() {
        return last;
    }

    /** Get the stepping of the range. */
    final public int getStep() {
        return step;
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
