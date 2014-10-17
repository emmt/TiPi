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
import mitiv.array.Int3D;
import mitiv.array.Int4D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.IntFunction;
import mitiv.base.mapping.IntScanner;
import mitiv.random.IntGenerator;
import mitiv.array.ArrayUtils;
import mitiv.base.Shape;
import mitiv.base.indexing.CompiledRange;
import mitiv.exception.NonConformableArrayException;
import mitiv.exception.IllegalRangeException;


/**
 * Flat implementation of 4-dimensional arrays of int's.
 *
 * @author Éric Thiébaut.
 */
public class FlatInt4D extends Int4D {
    static final int order = COLUMN_MAJOR;
    final int[] data;
    final int dim1dim2;
    final int dim1dim2dim3;

    public FlatInt4D(int dim1, int dim2, int dim3, int dim4) {
        super(dim1, dim2, dim3, dim4);
        data = new int[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
    }

    public FlatInt4D(int[] dims) {
        super(dims);
        data = new int[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
    }

    public FlatInt4D(Shape shape) {
        super(shape);
        data = new int[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
    }

    public FlatInt4D(int[] arr, int dim1, int dim2, int dim3, int dim4) {
        super(dim1, dim2, dim3, dim4);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
    }

    public FlatInt4D(int[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
    }

    public FlatInt4D(int[] arr, Shape shape) {
        super(shape);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
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

    private void checkSize(int[] arr) {
        if (arr == null || arr.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    final int index(int i1, int i2, int i3, int i4) {
        return dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1;
    }

    @Override
    public final int get(int i1, int i2, int i3, int i4) {
        return data[dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int value) {
        data[dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(int value) {
         for (int j = 0; j < number; ++j) {
            data[j] = value;
         }
    }

    @Override
    public void fill(IntGenerator generator) {
        for (int j = 0; j < number; ++j) {
            data[j] = generator.nextInt();
        }
    }

    @Override
    public void increment(int value) {
        for (int j = 0; j < number; ++j) {
            data[j] += value;
        }
    }

    @Override
    public void decrement(int value) {
        for (int j = 0; j < number; ++j) {
            data[j] -= value;
        }
    }

    @Override
    public void scale(int value) {
        for (int j = 0; j < number; ++j) {
            data[j] *= value;
        }
    }

    @Override
    public void map(IntFunction function) {
        for (int j = 0; j < number; ++j) {
            data[j] = function.apply(data[j]);
        }
    }

    @Override
    public void scan(IntScanner scanner)  {
        scanner.initialize(data[0]);
        for (int j = 1; j < number; ++j) {
            scanner.update(data[j]);
        }
    }

    @Override
    public int[] flatten(boolean forceCopy) {
        if (forceCopy) {
            int[] result = new int[number];
            System.arraycopy(data, 0, result, 0, number);
            return result;
        } else {
            return data;
        }
    }

    @Override
    public Int3D slice(int idx) {
        if (idx == 0) {
            return new FlatInt3D(data, dim1, dim2, dim3);
        } else {
            return new StriddenInt3D(data,
                    dim1dim2dim3*idx, // offset
                    1, dim1, dim1dim2, // strides
                    dim1, dim2, dim3); // dimensions
        }
    }

    @Override
    public Int3D slice(int idx, int dim) {
        if (dim < 0) {
            /* A negative index is taken with respect to the end. */
            dim += 4;
        }
        switch (dim) {
        case 0:
            return new StriddenInt3D(data,
                    idx, // offset
                    dim1, dim1dim2, dim1dim2dim3, // strides
                    dim2, dim3, dim4); // dimensions
        case 1:
            return new StriddenInt3D(data,
                    dim1*idx, // offset
                    1, dim1dim2, dim1dim2dim3, // strides
                    dim1, dim3, dim4); // dimensions
        case 2:
            return new StriddenInt3D(data,
                    dim1dim2*idx, // offset
                    1, dim1, dim1dim2dim3, // strides
                    dim1, dim2, dim4); // dimensions
        case 3:
            return new StriddenInt3D(data,
                    dim1dim2dim3*idx, // offset
                    1, dim1, dim1dim2, // strides
                    dim1, dim2, dim3); // dimensions
        }
        throw new IndexOutOfBoundsException("Dimension index out of bounds.");
    }

    @Override
    public Int4D view(Range rng1, Range rng2, Range rng3, Range rng4) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, 0, 1);
        CompiledRange cr2 = new CompiledRange(rng2, dim2, 0, dim1);
        CompiledRange cr3 = new CompiledRange(rng3, dim3, 0, dim1dim2);
        CompiledRange cr4 = new CompiledRange(rng4, dim4, 0, dim1dim2dim3);
        if (cr1.doesNothing() && cr2.doesNothing() && cr3.doesNothing() && cr4.doesNothing()) {
            return this;
        }
        if (cr1.getNumber() == 0 || cr2.getNumber() == 0 || cr3.getNumber() == 0 || cr4.getNumber() == 0) {
            throw new IllegalRangeException("Empty range.");
        }
        return new StriddenInt4D(this.data,
                cr1.getOffset() + cr2.getOffset() + cr3.getOffset() + cr4.getOffset(),
                cr1.getStride(), cr2.getStride(), cr3.getStride(), cr4.getStride(),
                cr1.getNumber(), cr2.getNumber(), cr3.getNumber(), cr4.getNumber());
    }

    @Override
    public Int4D view(int[] sel1, int[] sel2, int[] sel3, int[] sel4) {
        int[] idx1 = ArrayUtils.select(0, 1, dim1, sel1);
        int[] idx2 = ArrayUtils.select(0, dim1, dim2, sel2);
        int[] idx3 = ArrayUtils.select(0, dim1dim2, dim3, sel3);
        int[] idx4 = ArrayUtils.select(0, dim1dim2dim3, dim4, sel4);
        return new SelectedInt4D(this.data, idx1, idx2, idx3, idx4);
    }

    @Override
    public Int1D as1D() {
        return new FlatInt1D(data, number);
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
