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
import mitiv.array.Byte3D;
import mitiv.array.Byte4D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.ByteFunction;
import mitiv.base.mapping.ByteScanner;
import mitiv.random.ByteGenerator;
import mitiv.base.Shape;
import mitiv.base.indexing.CompiledRange;
import mitiv.exception.NonConformableArrayException;


/**
 * Flat implementation of 4-dimensional arrays of byte's.
 *
 * @author Éric Thiébaut.
 */
public class FlatByte4D extends Byte4D {
    static final int order = COLUMN_MAJOR;
    final byte[] data;
    final int dim1dim2;
    final int dim1dim2dim3;

    public FlatByte4D(int dim1, int dim2, int dim3, int dim4) {
        super(dim1, dim2, dim3, dim4);
        data = new byte[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
    }

    public FlatByte4D(int[] dims) {
        super(dims);
        data = new byte[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
    }

    public FlatByte4D(Shape shape) {
        super(shape);
        data = new byte[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
    }

    public FlatByte4D(byte[] arr, int dim1, int dim2, int dim3, int dim4) {
        super(dim1, dim2, dim3, dim4);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
    }

    public FlatByte4D(byte[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
    }

    public FlatByte4D(byte[] arr, Shape shape) {
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

    private void checkSize(byte[] arr) {
        if (arr == null || arr.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    final int index(int i1, int i2, int i3, int i4) {
        return dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1;
    }

    @Override
    public final byte get(int i1, int i2, int i3, int i4) {
        return data[dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, byte value) {
        data[dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1] = value;
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
    public final boolean isFlat() {
        return true;
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
    public byte[] getData() {
        return data;
    }

    @Override
    public Byte3D slice(int idx) {
        idx = Helper.fixIndex(idx, dim4);
        if (idx == 0) {
            return new FlatByte3D(data, dim1, dim2, dim3);
        } else {
            return new StriddenByte3D(data,
                    dim1dim2dim3*idx, // offset
                    1, dim1, dim1dim2, // strides
                    dim1, dim2, dim3); // dimensions
        }
    }

    @Override
    public Byte3D slice(int idx, int dim) {
        int sliceOffset;
        int sliceStride1, sliceStride2, sliceStride3;
        int sliceDim1, sliceDim2, sliceDim3;
        dim = Helper.fixSliceIndex(dim, 4);
        if (dim == 0) {
            /* Slice along 1st dimension. */
            sliceOffset = Helper.fixIndex(idx, dim1);
            sliceStride1 = dim1;
            sliceStride2 = dim1dim2;
            sliceStride3 = dim1dim2dim3;
            sliceDim1 = dim2;
            sliceDim2 = dim3;
            sliceDim3 = dim4;
        } else if (dim == 1) {
            /* Slice along 2nd dimension. */
            sliceOffset = dim1*Helper.fixIndex(idx, dim2);
            sliceStride1 = 1;
            sliceStride2 = dim1dim2;
            sliceStride3 = dim1dim2dim3;
            sliceDim1 = dim1;
            sliceDim2 = dim3;
            sliceDim3 = dim4;
        } else if (dim == 2) {
            /* Slice along 3rd dimension. */
            sliceOffset = dim1dim2*Helper.fixIndex(idx, dim3);
            sliceStride1 = 1;
            sliceStride2 = dim1;
            sliceStride3 = dim1dim2dim3;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim4;
        } else {
            /* Slice along 4th dimension. */
            sliceOffset = dim1dim2dim3*Helper.fixIndex(idx, dim4);
            sliceStride1 = 1;
            sliceStride2 = dim1;
            sliceStride3 = dim1dim2;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
        }
        return new StriddenByte3D(data, sliceOffset,
                sliceStride1, sliceStride2, sliceStride3,
                sliceDim1, sliceDim2, sliceDim3);
    }

    @Override
    public Byte4D view(Range rng1, Range rng2, Range rng3, Range rng4) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, 0, 1);
        CompiledRange cr2 = new CompiledRange(rng2, dim2, 0, dim1);
        CompiledRange cr3 = new CompiledRange(rng3, dim3, 0, dim1dim2);
        CompiledRange cr4 = new CompiledRange(rng4, dim4, 0, dim1dim2dim3);
        if (cr1.doesNothing() && cr2.doesNothing() && cr3.doesNothing() && cr4.doesNothing()) {
            return this;
        }
        return new StriddenByte4D(this.data,
                cr1.getOffset() + cr2.getOffset() + cr3.getOffset() + cr4.getOffset(),
                cr1.getStride(), cr2.getStride(), cr3.getStride(), cr4.getStride(),
                cr1.getNumber(), cr2.getNumber(), cr3.getNumber(), cr4.getNumber());
    }

    @Override
    public Byte4D view(int[] sel1, int[] sel2, int[] sel3, int[] sel4) {
        int[] idx1 = Helper.select(0, 1, dim1, sel1);
        int[] idx2 = Helper.select(0, dim1, dim2, sel2);
        int[] idx3 = Helper.select(0, dim1dim2, dim3, sel3);
        int[] idx4 = Helper.select(0, dim1dim2dim3, dim4, sel4);
        return new SelectedByte4D(this.data, idx1, idx2, idx3, idx4);
    }

    @Override
    public Byte1D as1D() {
        return new FlatByte1D(data, number);
    }

}
