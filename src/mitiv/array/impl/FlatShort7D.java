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
import mitiv.array.Short6D;
import mitiv.array.Short7D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.ShortFunction;
import mitiv.base.mapping.ShortScanner;
import mitiv.random.ShortGenerator;
import mitiv.array.ArrayUtils;
import mitiv.base.Shape;
import mitiv.base.indexing.CompiledRange;
import mitiv.exception.NonConformableArrayException;
import mitiv.exception.IllegalRangeException;


/**
 * Flat implementation of 7-dimensional arrays of short's.
 *
 * @author Éric Thiébaut.
 */
public class FlatShort7D extends Short7D {
    static final int order = COLUMN_MAJOR;
    final short[] data;
    final int dim1dim2;
    final int dim1dim2dim3;
    final int dim1dim2dim3dim4;
    final int dim1dim2dim3dim4dim5;
    final int dim1dim2dim3dim4dim5dim6;

    public FlatShort7D(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7) {
        super(dim1, dim2, dim3, dim4, dim5, dim6, dim7);
        data = new short[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
    }

    public FlatShort7D(int[] dims) {
        super(dims);
        data = new short[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
    }

    public FlatShort7D(Shape shape) {
        super(shape);
        data = new short[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
    }

    public FlatShort7D(short[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7) {
        super(dim1, dim2, dim3, dim4, dim5, dim6, dim7);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
    }

    public FlatShort7D(short[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
    }

    public FlatShort7D(short[] arr, Shape shape) {
        super(shape);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
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

    final int index(int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
        return dim1dim2dim3dim4dim5dim6*i7 + dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1;
    }

    @Override
    public final short get(int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
        return data[dim1dim2dim3dim4dim5dim6*i7 + dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int i5, int i6, int i7, short value) {
        data[dim1dim2dim3dim4dim5dim6*i7 + dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1] = value;
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
    public Short6D slice(int idx) {
        if (idx == 0) {
            return new FlatShort6D(data, dim1, dim2, dim3, dim4, dim5, dim6);
        } else {
            return new StriddenShort6D(data,
                    dim1dim2dim3dim4dim5dim6*idx, // offset
                    1, dim1, dim1dim2, dim1dim2dim3, dim1dim2dim3dim4, dim1dim2dim3dim4dim5, // strides
                    dim1, dim2, dim3, dim4, dim5, dim6); // dimensions
        }
    }

    @Override
    public Short6D slice(int idx, int dim) {
        if (dim < 0) {
            /* A negative index is taken with respect to the end. */
            dim += 7;
        }
        switch (dim) {
        case 0:
            return new StriddenShort6D(data,
                    idx, // offset
                    dim1, dim1dim2, dim1dim2dim3, dim1dim2dim3dim4, dim1dim2dim3dim4dim5, dim1dim2dim3dim4dim5dim6, // strides
                    dim2, dim3, dim4, dim5, dim6, dim7); // dimensions
        case 1:
            return new StriddenShort6D(data,
                    dim1*idx, // offset
                    1, dim1dim2, dim1dim2dim3, dim1dim2dim3dim4, dim1dim2dim3dim4dim5, dim1dim2dim3dim4dim5dim6, // strides
                    dim1, dim3, dim4, dim5, dim6, dim7); // dimensions
        case 2:
            return new StriddenShort6D(data,
                    dim1dim2*idx, // offset
                    1, dim1, dim1dim2dim3, dim1dim2dim3dim4, dim1dim2dim3dim4dim5, dim1dim2dim3dim4dim5dim6, // strides
                    dim1, dim2, dim4, dim5, dim6, dim7); // dimensions
        case 3:
            return new StriddenShort6D(data,
                    dim1dim2dim3*idx, // offset
                    1, dim1, dim1dim2, dim1dim2dim3dim4, dim1dim2dim3dim4dim5, dim1dim2dim3dim4dim5dim6, // strides
                    dim1, dim2, dim3, dim5, dim6, dim7); // dimensions
        case 4:
            return new StriddenShort6D(data,
                    dim1dim2dim3dim4*idx, // offset
                    1, dim1, dim1dim2, dim1dim2dim3, dim1dim2dim3dim4dim5, dim1dim2dim3dim4dim5dim6, // strides
                    dim1, dim2, dim3, dim4, dim6, dim7); // dimensions
        case 5:
            return new StriddenShort6D(data,
                    dim1dim2dim3dim4dim5*idx, // offset
                    1, dim1, dim1dim2, dim1dim2dim3, dim1dim2dim3dim4, dim1dim2dim3dim4dim5dim6, // strides
                    dim1, dim2, dim3, dim4, dim5, dim7); // dimensions
        case 6:
            return new StriddenShort6D(data,
                    dim1dim2dim3dim4dim5dim6*idx, // offset
                    1, dim1, dim1dim2, dim1dim2dim3, dim1dim2dim3dim4, dim1dim2dim3dim4dim5, // strides
                    dim1, dim2, dim3, dim4, dim5, dim6); // dimensions
        }
        throw new IndexOutOfBoundsException("Dimension index out of bounds.");
    }

    @Override
    public Short7D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5, Range rng6, Range rng7) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, 0, 1);
        CompiledRange cr2 = new CompiledRange(rng2, dim2, 0, dim1);
        CompiledRange cr3 = new CompiledRange(rng3, dim3, 0, dim1dim2);
        CompiledRange cr4 = new CompiledRange(rng4, dim4, 0, dim1dim2dim3);
        CompiledRange cr5 = new CompiledRange(rng5, dim5, 0, dim1dim2dim3dim4);
        CompiledRange cr6 = new CompiledRange(rng6, dim6, 0, dim1dim2dim3dim4dim5);
        CompiledRange cr7 = new CompiledRange(rng7, dim7, 0, dim1dim2dim3dim4dim5dim6);
        if (cr1.doesNothing() && cr2.doesNothing() && cr3.doesNothing() && cr4.doesNothing() && cr5.doesNothing() && cr6.doesNothing() && cr7.doesNothing()) {
            return this;
        }
        if (cr1.getNumber() == 0 || cr2.getNumber() == 0 || cr3.getNumber() == 0 || cr4.getNumber() == 0 || cr5.getNumber() == 0 || cr6.getNumber() == 0 || cr7.getNumber() == 0) {
            throw new IllegalRangeException("Empty range.");
        }
        return new StriddenShort7D(this.data,
                cr1.getOffset() + cr2.getOffset() + cr3.getOffset() + cr4.getOffset() + cr5.getOffset() + cr6.getOffset() + cr7.getOffset(),
                cr1.getStride(), cr2.getStride(), cr3.getStride(), cr4.getStride(), cr5.getStride(), cr6.getStride(), cr7.getStride(),
                cr1.getNumber(), cr2.getNumber(), cr3.getNumber(), cr4.getNumber(), cr5.getNumber(), cr6.getNumber(), cr7.getNumber());
    }

    @Override
    public Short7D view(int[] sel1, int[] sel2, int[] sel3, int[] sel4, int[] sel5, int[] sel6, int[] sel7) {
        int[] idx1 = ArrayUtils.select(0, 1, dim1, sel1);
        int[] idx2 = ArrayUtils.select(0, dim1, dim2, sel2);
        int[] idx3 = ArrayUtils.select(0, dim1dim2, dim3, sel3);
        int[] idx4 = ArrayUtils.select(0, dim1dim2dim3, dim4, sel4);
        int[] idx5 = ArrayUtils.select(0, dim1dim2dim3dim4, dim5, sel5);
        int[] idx6 = ArrayUtils.select(0, dim1dim2dim3dim4dim5, dim6, sel6);
        int[] idx7 = ArrayUtils.select(0, dim1dim2dim3dim4dim5dim6, dim7, sel7);
        return new SelectedShort7D(this.data, idx1, idx2, idx3, idx4, idx5, idx6, idx7);
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
