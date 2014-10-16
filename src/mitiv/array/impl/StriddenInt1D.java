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
 * Stridden implementation of 1-dimensional arrays of int's.
 *
 * @author Éric Thiébaut.
 */
public class StriddenInt1D extends Int1D {
    final int order;
    final int[] data;
    final int offset;
    final int stride1;

    public StriddenInt1D(int[] arr, int offset, int[] stride, int[] dims) {
        super(dims);
        if (stride.length != 1) {
            throw new IllegalArgumentException("There must be as many strides as the rank.");
        }
        this.data = arr;
        this.offset = offset;
        stride1 = stride[0];
        this.order = Int1D.checkViewStrides(data.length, dim1, offset, stride1);
    }

    public StriddenInt1D(int[] arr, int offset, int stride1, int dim1) {
        super(dim1);
        this.data = arr;
        this.offset = offset;
        this.stride1 = stride1;
        this.order = Int1D.checkViewStrides(data.length, dim1, offset, stride1);
    }

    @Override
    public void checkSanity() {
        Int1D.checkViewStrides(data.length, dim1, offset, stride1);
    }

    private boolean isFlat() {
        return (offset == 0 && stride1 == 1);
    }

    final int index(int i1) {
        return offset + stride1*i1;
    }

    @Override
    public final int get(int i1) {
        return data[offset + stride1*i1];
    }

    @Override
    public final void set(int i1, int value) {
        data[offset + stride1*i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(int value) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] = value;
            }
    }

    @Override
    public void fill(IntGenerator generator) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] = generator.nextInt();
            }
    }

    @Override
    public void increment(int value) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] += value;
            }
    }

    @Override
    public void decrement(int value) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] -= value;
            }
    }

    @Override
    public void scale(int value) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] *= value;
            }
    }

    @Override
    public void map(IntFunction function) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] = function.apply(data[j1]);
            }
    }

    @Override
    public void scan(IntScanner scanner)  {
        boolean initialized = false;
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                if (initialized) {
                    scanner.update(data[j1]);
                } else {
                    scanner.initialize(data[j1]);
                    initialized = true;
                }
            }
    }

    @Override
    public int[] flatten(boolean forceCopy) {
        if (! forceCopy && isFlat()) {
            return data;
        }
        int[] out = new int[number];
        int j = -1;
        for (int i1 = 0; i1 < dim1; ++i1) {
            int j1 = stride1*i1 + offset;
            out[++j] = data[j1];
        }
        return out;
    }
    @Override
    public IntScalar slice(int idx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IntScalar slice(int idx, int dim) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Int1D view(Range rng1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Int1D view(int[] idx1) {
        // TODO Auto-generated method stub
        return null;
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
