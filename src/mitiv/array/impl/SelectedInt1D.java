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

import mitiv.array.Int1D;
import mitiv.array.IntScalar;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.IntFunction;
import mitiv.base.mapping.IntScanner;
import mitiv.random.IntGenerator;


/**
 * Selected implementation of 1-dimensional arrays of int's.
 *
 * This specific kind of arrays/views are accessed via indirection tables (one
 * for each dimension).
 *
 * @author Éric Thiébaut.
 */
public class SelectedInt1D extends Int1D {
    static final int order = NONSPECIFIC_ORDER;
    final int[] data;
    final int[] idx1;

    /**
     * Create a new instance of a view via lists of selected indices.
     *
     * <p>
     * All lists of selected indices must be immutable as, for efficiency,
     * a simple reference is kept.
     * </p>
     */
    public SelectedInt1D(int[] arr, int[] idx1) {
        super(idx1.length);
        this.data = arr;
        this.idx1 = idx1;
    }

    @Override
    public final void checkSanity() {
        int offsetMin = 0, offsetMax = 0, indexMin, indexMax;
        indexMin = indexMax = idx1[0];
        for (int i1 = 1; i1 < dim1; ++i1) {
            int index = idx1[i1];
            if (index < indexMin) indexMin = index;
            if (index > indexMax) indexMax = index;
        }
        offsetMin += indexMin;
        offsetMax += indexMax;
        if (offsetMin < 0 || offsetMax >= data.length) {
            throw new IndexOutOfBoundsException("Selected indices are out of bounds.");
        }
    }

    final int index(int i1) {
        return idx1[i1];
    }

    @Override
    public final int get(int i1) {
        return data[idx1[i1]];
    }

    @Override
    public final void set(int i1, int value) {
        data[idx1[i1]] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(int value) {
        for (int i1 = 0; i1 < dim1; ++i1) {
            int j1 = idx1[i1];
            data[j1] = value;
        }
    }

    @Override
    public void fill(IntGenerator generator) {
        for (int i1 = 0; i1 < dim1; ++i1) {
            int j1 = idx1[i1];
            data[j1] = generator.nextInt();
        }
    }

    @Override
    public void increment(int value) {
        for (int i1 = 0; i1 < dim1; ++i1) {
            int j1 = idx1[i1];
            data[j1] += value;
        }
    }

    @Override
    public void decrement(int value) {
        for (int i1 = 0; i1 < dim1; ++i1) {
            int j1 = idx1[i1];
            data[j1] -= value;
        }
    }

    @Override
    public void scale(int value) {
        for (int i1 = 0; i1 < dim1; ++i1) {
            int j1 = idx1[i1];
            data[j1] *= value;
        }
    }

    @Override
    public void map(IntFunction function) {
        for (int i1 = 0; i1 < dim1; ++i1) {
            int j1 = idx1[i1];
            data[j1] = function.apply(data[j1]);
        }
    }

    @Override
    public void scan(IntScanner scanner)  {
        boolean initialized = false;
        for (int i1 = 0; i1 < dim1; ++i1) {
            int j1 = idx1[i1];
            if (initialized) {
                scanner.update(data[j1]);
            } else {
                scanner.initialize(data[j1]);
                initialized = true;
            }
        }
    }

    @Override
    public final boolean isFlat() {
        return false;
    }

    @Override
    public int[] flatten(boolean forceCopy) {
        int[] out = new int[number];
        int j = -1;
        for (int i1 = 0; i1 < dim1; ++i1) {
            int j1 = idx1[i1];
            out[++j] = data[j1];
        }
        return out;
    }

    @Override
    public IntScalar slice(int idx) {
        return new IntScalar(data, idx1[Helper.fixIndex(idx, dim1)]);
    }

    @Override
    public IntScalar slice(int idx, int dim) {
        Helper.fixSliceIndex(dim, 1); // throws an exception if dim != 0
        return new IntScalar(data, Helper.fixIndex(idx, dim1));
    }

    @Override
    public Int1D view(Range rng1) {
        int[] viewIndex1 = Helper.select(idx1, rng1);
        if (viewIndex1 == idx1) {
            return this;
        } else {
            return new SelectedInt1D(data, viewIndex1);
        }
    }

    @Override
    public Int1D view(int[] sel1) {
        int[] viewIndex1 = Helper.select(idx1, sel1);
        if (viewIndex1 == idx1) {
            return this;
        } else {
            return new SelectedInt1D(data, viewIndex1);
        }
    }

    @Override
    public Int1D as1D() {
        return this;
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
