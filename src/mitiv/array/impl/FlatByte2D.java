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

import mitiv.array.Byte1D;
import mitiv.array.Byte2D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.ByteFunction;
import mitiv.base.mapping.ByteScanner;
import mitiv.random.ByteGenerator;
import mitiv.array.ArrayUtils;
import mitiv.base.Shape;
import mitiv.base.indexing.CompiledRange;
import mitiv.exception.NonConformableArrayException;
import mitiv.exception.IllegalRangeException;


/**
 * Flat implementation of 2-dimensional arrays of byte's.
 *
 * @author Éric Thiébaut.
 */
public class FlatByte2D extends Byte2D {
    static final int order = COLUMN_MAJOR;
    final byte[] data;

    public FlatByte2D(int dim1, int dim2) {
        super(dim1, dim2);
        data = new byte[number];
    }

    public FlatByte2D(int[] dims) {
        super(dims);
        data = new byte[number];
    }

    public FlatByte2D(Shape shape) {
        super(shape);
        data = new byte[number];
    }

    public FlatByte2D(byte[] arr, int dim1, int dim2) {
        super(dim1, dim2);
        checkSize(arr);
        data = arr;
    }

    public FlatByte2D(byte[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
    }

    public FlatByte2D(byte[] arr, Shape shape) {
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

    private void checkSize(byte[] arr) {
        if (arr == null || arr.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    final int index(int i1, int i2) {
        return dim1*i2 + i1;
    }

    @Override
    public final byte get(int i1, int i2) {
        return data[dim1*i2 + i1];
    }

    @Override
    public final void set(int i1, int i2, byte value) {
        data[dim1*i2 + i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(byte value) {
         for (int j = 0; j < number; ++j) {
            data[j] = value;
         }
    }

    @Override
    public void fill(ByteGenerator generator) {
        for (int j = 0; j < number; ++j) {
            data[j] = generator.nextByte();
        }
    }

    @Override
    public void increment(byte value) {
        for (int j = 0; j < number; ++j) {
            data[j] += value;
        }
    }

    @Override
    public void decrement(byte value) {
        for (int j = 0; j < number; ++j) {
            data[j] -= value;
        }
    }

    @Override
    public void scale(byte value) {
        for (int j = 0; j < number; ++j) {
            data[j] *= value;
        }
    }

    @Override
    public void map(ByteFunction function) {
        for (int j = 0; j < number; ++j) {
            data[j] = function.apply(data[j]);
        }
    }

    @Override
    public void scan(ByteScanner scanner)  {
        scanner.initialize(data[0]);
        for (int j = 1; j < number; ++j) {
            scanner.update(data[j]);
        }
    }

    @Override
    public byte[] flatten(boolean forceCopy) {
        if (forceCopy) {
            byte[] result = new byte[number];
            System.arraycopy(data, 0, result, 0, number);
            return result;
        } else {
            return data;
        }
    }

    @Override
    public Byte1D slice(int idx) {
        if (idx == 0) {
            return new FlatByte1D(data, dim1);
        } else {
            return new StriddenByte1D(data,
                    dim1*idx, // offset
                    1, // strides
                    dim1); // dimensions
        }
    }

    @Override
    public Byte1D slice(int idx, int dim) {
        if (dim < 0) {
            /* A negative index is taken with respect to the end. */
            dim += 2;
        }
        switch (dim) {
        case 0:
            return new StriddenByte1D(data,
                    idx, // offset
                    dim1, // strides
                    dim2); // dimensions
        case 1:
            return new StriddenByte1D(data,
                    dim1*idx, // offset
                    1, // strides
                    dim1); // dimensions
        }
        throw new IndexOutOfBoundsException("Dimension index out of bounds.");
    }

    @Override
    public Byte2D view(Range rng1, Range rng2) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, 0, 1);
        CompiledRange cr2 = new CompiledRange(rng2, dim2, 0, dim1);
        if (cr1.doesNothing() && cr2.doesNothing()) {
            return this;
        }
        if (cr1.getNumber() == 0 || cr2.getNumber() == 0) {
            throw new IllegalRangeException("Empty range.");
        }
        return new StriddenByte2D(this.data,
                cr1.getOffset() + cr2.getOffset(),
                cr1.getStride(), cr2.getStride(),
                cr1.getNumber(), cr2.getNumber());
    }

    @Override
    public Byte2D view(int[] sel1, int[] sel2) {
        int[] idx1 = ArrayUtils.select(0, 1, dim1, sel1);
        int[] idx2 = ArrayUtils.select(0, dim1, dim2, sel2);
        return new SelectedByte2D(this.data, idx1, idx2);
    }

    @Override
    public Byte1D as1D() {
        return new FlatByte1D(data, number);
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
