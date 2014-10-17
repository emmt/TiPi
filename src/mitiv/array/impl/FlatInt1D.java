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
import mitiv.array.ArrayUtils;
import mitiv.base.Shape;
import mitiv.base.indexing.CompiledRange;
import mitiv.exception.NonConformableArrayException;
import mitiv.exception.IllegalRangeException;


/**
 * Flat implementation of 1-dimensional arrays of int's.
 *
 * @author Éric Thiébaut.
 */
public class FlatInt1D extends Int1D {
    static final int order = COLUMN_MAJOR;
    final int[] data;

    public FlatInt1D(int dim1) {
        super(dim1);
        data = new int[number];
    }

    public FlatInt1D(int[] dims) {
        super(dims);
        data = new int[number];
    }

    public FlatInt1D(Shape shape) {
        super(shape);
        data = new int[number];
    }

    public FlatInt1D(int[] arr, int dim1) {
        super(dim1);
        checkSize(arr);
        data = arr;
    }

    public FlatInt1D(int[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
    }

    public FlatInt1D(int[] arr, Shape shape) {
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

    private void checkSize(int[] arr) {
        if (arr == null || arr.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    final int index(int i1) {
        return i1;
    }

    @Override
    public final int get(int i1) {
        return data[i1];
    }

    @Override
    public final void set(int i1, int value) {
        data[i1] = value;
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
    public IntScalar slice(int idx) {
        return new IntScalar(data, idx);
    }

    @Override
    public IntScalar slice(int idx, int dim) {
        if (dim < 0) {
            /* A negative index is taken with respect to the end. */
            dim += 1;
        }
        if (dim == 0) {
            return new IntScalar(data, idx);
        }
        throw new IndexOutOfBoundsException("Dimension index out of bounds.");
    }

    @Override
    public Int1D view(Range rng1) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, 0, 1);
        if (cr1.doesNothing()) {
            return this;
        }
        if (cr1.getNumber() == 0) {
            throw new IllegalRangeException("Empty range.");
        }
        return new StriddenInt1D(this.data,
                cr1.getOffset(),
                cr1.getStride(),
                cr1.getNumber());
    }

    @Override
    public Int1D view(int[] sel1) {
        int[] idx1 = ArrayUtils.select(0, 1, dim1, sel1);
        return new SelectedInt1D(this.data, idx1);
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
