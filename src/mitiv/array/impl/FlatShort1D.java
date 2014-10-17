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
import mitiv.array.ShortScalar;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.ShortFunction;
import mitiv.base.mapping.ShortScanner;
import mitiv.random.ShortGenerator;
import mitiv.base.Shape;
import mitiv.base.indexing.CompiledRange;
import mitiv.exception.NonConformableArrayException;
import mitiv.exception.IllegalRangeException;


/**
 * Flat implementation of 1-dimensional arrays of short's.
 *
 * @author Éric Thiébaut.
 */
public class FlatShort1D extends Short1D {
    static final int order = COLUMN_MAJOR;
    final short[] data;

    public FlatShort1D(int dim1) {
        super(dim1);
        data = new short[number];
    }

    public FlatShort1D(int[] dims) {
        super(dims);
        data = new short[number];
    }

    public FlatShort1D(Shape shape) {
        super(shape);
        data = new short[number];
    }

    public FlatShort1D(short[] arr, int dim1) {
        super(dim1);
        checkSize(arr);
        data = arr;
    }

    public FlatShort1D(short[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
    }

    public FlatShort1D(short[] arr, Shape shape) {
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

    final int index(int i1) {
        return i1;
    }

    @Override
    public final short get(int i1) {
        return data[i1];
    }

    @Override
    public final void set(int i1, short value) {
        data[i1] = value;
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
    public ShortScalar slice(int idx) {
        idx = Helper.fixIndex(idx, dim1);
        return new ShortScalar(data, idx);
    }

    @Override
    public ShortScalar slice(int idx, int dim) {
        Helper.fixSliceIndex(dim, 1);
        return new ShortScalar(data, Helper.fixIndex(idx, dim1));
    }

    @Override
    public Short1D view(Range rng1) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, 0, 1);
        if (cr1.doesNothing()) {
            return this;
        }
        if (cr1.getNumber() == 0) {
            throw new IllegalRangeException("Empty range.");
        }
        return new StriddenShort1D(this.data,
                cr1.getOffset(),
                cr1.getStride(),
                cr1.getNumber());
    }

    @Override
    public Short1D view(int[] sel1) {
        int[] idx1 = Helper.select(0, 1, dim1, sel1);
        return new SelectedShort1D(this.data, idx1);
    }

    @Override
    public Short1D as1D() {
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
