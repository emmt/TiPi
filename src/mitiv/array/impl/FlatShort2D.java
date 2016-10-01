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

import mitiv.array.Short1D;
import mitiv.array.Short2D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.ShortFunction;
import mitiv.base.mapping.ShortScanner;
import mitiv.random.ShortGenerator;
import mitiv.base.Shape;
import mitiv.base.indexing.CompiledRange;
import mitiv.exception.NonConformableArrayException;


/**
 * Flat implementation of 2-dimensional arrays of short's.
 *
 * @author Éric Thiébaut.
 */
public class FlatShort2D extends Short2D {
    static final int order = COLUMN_MAJOR;
    final short[] data;

    public FlatShort2D(int dim1, int dim2) {
        super(dim1, dim2);
        data = new short[number];
    }

    public FlatShort2D(int[] dims) {
        super(dims);
        data = new short[number];
    }

    public FlatShort2D(Shape shape) {
        super(shape);
        data = new short[number];
    }

    public FlatShort2D(short[] arr, int dim1, int dim2) {
        super(dim1, dim2);
        checkSize(arr);
        data = arr;
    }

    public FlatShort2D(short[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
    }

    public FlatShort2D(short[] arr, Shape shape) {
        super(shape);
        checkSize(arr);
        data = arr;
    }

    @Override
    public void checkSanity() {
        if (data == null) {
           throw new NonConformableArrayException("Wrapped array is null.");
        }
        if (data.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    private void checkSize(short[] arr) {
        if (arr == null || arr.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    final int index(int i1, int i2) {
        return dim1*i2 + i1;
    }

    @Override
    public final short get(int i1, int i2) {
        return data[dim1*i2 + i1];
    }

    @Override
    public final void set(int i1, int i2, short value) {
        data[dim1*i2 + i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(short value) {
         for (int j = 0; j < number; ++j) {
            data[j] = value;
         }
    }

    @Override
    public void fill(ShortGenerator generator) {
        for (int j = 0; j < number; ++j) {
            data[j] = generator.nextShort();
        }
    }

    @Override
    public void increment(short value) {
        for (int j = 0; j < number; ++j) {
            data[j] += value;
        }
    }

    @Override
    public void decrement(short value) {
        for (int j = 0; j < number; ++j) {
            data[j] -= value;
        }
    }

    @Override
    public void scale(short value) {
        for (int j = 0; j < number; ++j) {
            data[j] *= value;
        }
    }

    @Override
    public void map(ShortFunction function) {
        for (int j = 0; j < number; ++j) {
            data[j] = function.apply(data[j]);
        }
    }

    @Override
    public void scan(ShortScanner scanner)  {
        scanner.initialize(data[0]);
        for (int j = 1; j < number; ++j) {
            scanner.update(data[j]);
        }
    }

    @Override
    public final boolean isFlat() {
        return true;
    }

    @Override
    public short[] flatten(boolean forceCopy) {
        if (forceCopy) {
            short[] result = new short[number];
            System.arraycopy(data, 0, result, 0, number);
            return result;
        } else {
            return data;
        }
    }

    @Override
    public Short1D slice(int idx) {
        idx = Helper.fixIndex(idx, dim2);
        if (idx == 0) {
            return new FlatShort1D(data, dim1);
        } else {
            return new StriddenShort1D(data,
                    dim1*idx, // offset
                    1, // strides
                    dim1); // dimensions
        }
    }

    @Override
    public Short1D slice(int idx, int dim) {
        int sliceOffset;
        int sliceStride1;
        int sliceDim1;
        dim = Helper.fixSliceIndex(dim, 2);
        if (dim == 0) {
            /* Slice along 1st dimension. */
            sliceOffset = Helper.fixIndex(idx, dim1);
            sliceStride1 = dim1;
            sliceDim1 = dim2;
        } else {
            /* Slice along 2nd dimension. */
            sliceOffset = dim1*Helper.fixIndex(idx, dim2);
            sliceStride1 = 1;
            sliceDim1 = dim1;
        }
        return new StriddenShort1D(data, sliceOffset,
                sliceStride1,
                sliceDim1);
    }

    @Override
    public Short2D view(Range rng1, Range rng2) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, 0, 1);
        CompiledRange cr2 = new CompiledRange(rng2, dim2, 0, dim1);
        if (cr1.doesNothing() && cr2.doesNothing()) {
            return this;
        }
        return new StriddenShort2D(this.data,
                cr1.getOffset() + cr2.getOffset(),
                cr1.getStride(), cr2.getStride(),
                cr1.getNumber(), cr2.getNumber());
    }

    @Override
    public Short2D view(int[] sel1, int[] sel2) {
        int[] idx1 = Helper.select(0, 1, dim1, sel1);
        int[] idx2 = Helper.select(0, dim1, dim2, sel2);
        return new SelectedShort2D(this.data, idx1, idx2);
    }

    @Override
    public Short1D as1D() {
        return new FlatShort1D(data, number);
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
