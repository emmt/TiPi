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
import mitiv.array.ByteScalar;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.ByteFunction;
import mitiv.base.mapping.ByteScanner;
import mitiv.random.ByteGenerator;
import mitiv.base.indexing.CompiledRange;


/**
 * Stridden implementation of 1-dimensional arrays of byte's.
 *
 * @author Éric Thiébaut.
 */
public class StriddenByte1D extends Byte1D {
    final int order;
    final byte[] data;
    final int offset;
    final int stride1;

    public StriddenByte1D(byte[] arr, int offset, int[] stride, int[] dims) {
        super(dims);
        if (stride.length != 1) {
            throw new IllegalArgumentException("There must be as many strides as the rank.");
        }
        this.data = arr;
        this.offset = offset;
        stride1 = stride[0];
        this.order = Byte1D.checkViewStrides(data.length, offset, stride1, dim1);
    }

    public StriddenByte1D(byte[] arr, int offset, int stride1, int dim1) {
        super(dim1);
        this.data = arr;
        this.offset = offset;
        this.stride1 = stride1;
        this.order = Byte1D.checkViewStrides(data.length, offset, stride1, dim1);
    }

    @Override
    public void checkSanity() {
        Byte1D.checkViewStrides(data.length, offset, stride1, dim1);
    }

    final int index(int i1) {
        return offset + stride1*i1;
    }

    @Override
    public final byte get(int i1) {
        return data[offset + stride1*i1];
    }

    @Override
    public final void set(int i1, byte value) {
        data[offset + stride1*i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(byte value) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] = value;
            }
    }

    @Override
    public void fill(ByteGenerator generator) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] = generator.nextByte();
            }
    }

    @Override
    public void increment(byte value) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] += value;
            }
    }

    @Override
    public void decrement(byte value) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] -= value;
            }
    }

    @Override
    public void scale(byte value) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] *= value;
            }
    }

    @Override
    public void map(ByteFunction function) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] = function.apply(data[j1]);
            }
    }

    @Override
    public void scan(ByteScanner scanner)  {
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
    public final boolean isFlat() {
        return (offset == 0 && stride1 == 1);
    }

    @Override
    public byte[] flatten(boolean forceCopy) {
        if (! forceCopy && isFlat()) {
            return data;
        }
        byte[] out = new byte[number];
        int j = -1;
        for (int i1 = 0; i1 < dim1; ++i1) {
            int j1 = stride1*i1 + offset;
            out[++j] = data[j1];
        }
        return out;
    }

    @Override
    public ByteScalar slice(int idx) {
        return new ByteScalar(data, offset + stride1*idx);
    }

    @Override
    public ByteScalar slice(int idx, int dim) {
        int sliceOffset;
        if (dim < 0) {
            /* A negative index is taken with respect to the end. */
            dim += 1;
        }
        if (dim == 0) {
            /* Slice along 1st dimension. */
            sliceOffset = offset + stride1*idx;
        } else {
            throw new IndexOutOfBoundsException("Dimension index out of bounds.");
        }
        return new ByteScalar(data, sliceOffset);
    }

    @Override
    public Byte1D view(Range rng1) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, offset, stride1);
        if (cr1.doesNothing()) {
            return this;
        }
        return new StriddenByte1D(this.data,
                cr1.getOffset(),
                cr1.getStride(),
                cr1.getNumber());
    }

    @Override
    public Byte1D view(int[] sel1) {
        int[] idx1 = Helper.select(offset, stride1, dim1, sel1);
        return new SelectedByte1D(this.data, idx1);
    }

    @Override
    public Byte1D as1D() {
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
