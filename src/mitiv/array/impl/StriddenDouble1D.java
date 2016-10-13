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

import mitiv.array.Double1D;
import mitiv.array.DoubleScalar;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.DoubleFunction;
import mitiv.base.mapping.DoubleScanner;
import mitiv.random.DoubleGenerator;
import mitiv.base.indexing.CompiledRange;


/**
 * Stridden implementation of 1-dimensional arrays of double's.
 *
 * @author Éric Thiébaut.
 */
public class StriddenDouble1D extends Double1D {
    final int order;
    final double[] data;
    final int offset;
    final int stride1;
    final boolean flat;

    public StriddenDouble1D(double[] arr, int offset, int[] stride, int[] dims) {
        super(dims);
        if (stride.length != 1) {
            throw new IllegalArgumentException("There must be as many strides as the rank.");
        }
        this.data = arr;
        this.offset = offset;
        stride1 = stride[0];
        this.order = Double1D.checkViewStrides(data.length, offset, stride1, dim1);
        this.flat = (offset == 0 && stride1 == 1);
    }

    public StriddenDouble1D(double[] arr, int offset, int stride1, int dim1) {
        super(dim1);
        this.data = arr;
        this.offset = offset;
        this.stride1 = stride1;
        this.order = Double1D.checkViewStrides(data.length, offset, stride1, dim1);
        this.flat = (offset == 0 && stride1 == 1);
    }

    @Override
    public void checkSanity() {
        Double1D.checkViewStrides(data.length, offset, stride1, dim1);
    }

    final int index(int i1) {
        return offset + stride1*i1;
    }

    @Override
    public final double get(int i1) {
        return data[offset + stride1*i1];
    }

    @Override
    public final void set(int i1, double value) {
        data[offset + stride1*i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(double value) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] = value;
            }
    }

    @Override
    public void fill(DoubleGenerator generator) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] = generator.nextDouble();
            }
    }

    @Override
    public void increment(double value) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] += value;
            }
    }

    @Override
    public void decrement(double value) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] -= value;
            }
    }

    @Override
    public void scale(double value) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] *= value;
            }
    }

    @Override
    public void map(DoubleFunction function) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                data[j1] = function.apply(data[j1]);
            }
    }

    @Override
    public void scan(DoubleScanner scanner)  {
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
        return flat;
    }

    @Override
    public double[] flatten(boolean forceCopy) {
        if (! forceCopy && flat) {
            return data;
        }
        double[] out = new double[number];
        int j = -1;
        for (int i1 = 0; i1 < dim1; ++i1) {
            int j1 = stride1*i1 + offset;
            out[++j] = data[j1];
        }
        return out;
    }

    @Override
    public DoubleScalar slice(int idx) {
        return new DoubleScalar(data, offset + stride1*idx);
    }

    @Override
    public DoubleScalar slice(int idx, int dim) {
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
        return new DoubleScalar(data, sliceOffset);
    }

    @Override
    public Double1D view(Range rng1) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, offset, stride1);
        if (cr1.doesNothing()) {
            return this;
        }
        return new StriddenDouble1D(this.data,
                cr1.getOffset(),
                cr1.getStride(),
                cr1.getNumber());
    }

    @Override
    public Double1D view(int[] sel1) {
        int[] idx1 = Helper.select(offset, stride1, dim1, sel1);
        return new SelectedDouble1D(this.data, idx1);
    }

    @Override
    public Double1D as1D() {
        return this;
    }

}
