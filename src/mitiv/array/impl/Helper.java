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

package mitiv.array.impl;

import mitiv.base.indexing.Range;

public class Helper {
    static int fixSliceIndex(int slice, int rank) {
        if (slice < 0) {
            slice += rank;
        }
        if (slice < 0 || slice >= rank) {
            throw new IndexOutOfBoundsException("Slice index out of bounds.");
        }
        return slice;
    }

    static int fixIndex(int index, int dim) {
        if (index < 0) {
            index += dim;
        }
        if (index < 0 || index >= dim) {
            throw new IndexOutOfBoundsException("Index out of bounds.");
        }
        return index;
    }


    /*=======================================================================*/
    /* SELECTION */

    private static void badStepDirection() {
        throw new IllegalArgumentException("Bad step direction.");
    }

    private static void badEndPoints() {
        throw new IndexOutOfBoundsException("Range endpoints outside bounds.");
    }

    /**
     * Select indices from a stridden dimension.
     * @param offset - The offset for the dimension of interest.
     * @param stride - The stepping along the dimension of interest.
     * @param dim    - The number of elements along the dimension of interest.
     * @param sel    - The indices to select.
     * @return A sub-list of indices: {@code offset + stride*sel[j]}.
     * @throws IndexOutOfBoundsException if any of {@code sel[j]} is less than
     *         zero or greater or equal {@code dim}.
     */
    public static int[] select(int offset, int stride, int dim, int[] sel) {
        int length = sel.length;
        int[] result = new int[length];
        for (int j = 0; j < length; ++j) {
            int index = sel[j];
            if (index < 0 || index >= dim) {
                throw new IndexOutOfBoundsException("Selection index outside bounds.");
            }
            result[j] = offset + stride*index;
        }
        return result;
    }

    /**
     * Select a sub-list of values by range.
     * @param src - The initial list of values.
     * @param rng - The range to select.
     * @return A sub-list of values from {@code src}.
     */
    public static int[] select(int[] src, Range rng) {
        if (rng == null) {
            return src;
        }
        int length = src.length;
        int first = rng.getFirst(length);
        int last = rng.getLast(length);
        int step = rng.step;
        if (first <= last) {
            if (step < 0) {
                badStepDirection();
            }
            if (step == 0) {
                step = 1;
            }
            if (first == 0 && last == length - 1 && step == 1) {
                return src;
            }
            if (first < 0 || last >= length) {
                badEndPoints();
            }
            length = (last - first + 1)/step;
        } else {
            if (step > 0) {
                badStepDirection();
            }
            if (step == 0) {
                step = -1;
            }
            if (last < 0 || first >= length) {
                badEndPoints();
            }
            length = (first - last + 1)/(-step);
        }
        int[] idx = new int[length];
        for (int j = 0, k = first; j < length; ++j, k += step) {
            idx[j] = src[k];
        }
        return idx;
    }

    /**
     * Select a sub-list of values by indices.
     * @param src - The initial list of values.
     * @param sel - The list of indices to select.
     * @return A sub-list of values from {@code src}.
     */
    public static int[] select(int[] src, int[] sel) {
        if (sel == null) {
            return src;
        }
        int length = sel.length;
        int[] idx = new int[length];
        for (int j = 0; j < length; ++j) {
            idx[j] = src[sel[j]];
        }
        return idx;
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
