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
import mitiv.array.Double4D;
import mitiv.array.Double5D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.DoubleFunction;
import mitiv.base.mapping.DoubleScanner;
import mitiv.random.DoubleGenerator;
import mitiv.base.Shape;
import mitiv.base.indexing.CompiledRange;
import mitiv.exception.NonConformableArrayException;


/**
 * Flat implementation of 5-dimensional arrays of double's.
 *
 * @author Éric Thiébaut.
 */
public class FlatDouble5D extends Double5D {
    static final int order = COLUMN_MAJOR;
    final double[] data;
    final int dim1dim2;
    final int dim1dim2dim3;
    final int dim1dim2dim3dim4;

    public FlatDouble5D(int dim1, int dim2, int dim3, int dim4, int dim5) {
        super(dim1, dim2, dim3, dim4, dim5);
        data = new double[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
    }

    public FlatDouble5D(int[] dims) {
        super(dims);
        data = new double[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
    }

    public FlatDouble5D(Shape shape) {
        super(shape);
        data = new double[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
    }

    public FlatDouble5D(double[] arr, int dim1, int dim2, int dim3, int dim4, int dim5) {
        super(dim1, dim2, dim3, dim4, dim5);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
    }

    public FlatDouble5D(double[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
    }

    public FlatDouble5D(double[] arr, Shape shape) {
        super(shape);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
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

    private void checkSize(double[] arr) {
        if (arr == null || arr.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    final int index(int i1, int i2, int i3, int i4, int i5) {
        return dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1;
    }

    @Override
    public final double get(int i1, int i2, int i3, int i4, int i5) {
        return data[dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int i5, double value) {
        data[dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(double value) {
         for (int j = 0; j < number; ++j) {
            data[j] = value;
         }
    }

    @Override
    public void fill(DoubleGenerator generator) {
        for (int j = 0; j < number; ++j) {
            data[j] = generator.nextDouble();
        }
    }

    @Override
    public void increment(double value) {
        for (int j = 0; j < number; ++j) {
            data[j] += value;
        }
    }

    @Override
    public void decrement(double value) {
        for (int j = 0; j < number; ++j) {
            data[j] -= value;
        }
    }

    @Override
    public void scale(double value) {
        for (int j = 0; j < number; ++j) {
            data[j] *= value;
        }
    }

    @Override
    public void map(DoubleFunction function) {
        for (int j = 0; j < number; ++j) {
            data[j] = function.apply(data[j]);
        }
    }

    @Override
    public void scan(DoubleScanner scanner)  {
        scanner.initialize(data[0]);
        for (int j = 1; j < number; ++j) {
            scanner.update(data[j]);
        }
    }

    @Override
    public double[] flatten(boolean forceCopy) {
        if (forceCopy) {
            double[] result = new double[number];
            System.arraycopy(data, 0, result, 0, number);
            return result;
        } else {
            return data;
        }
    }

    @Override
    public Double4D slice(int idx) {
        idx = Helper.fixIndex(idx, dim5);
        if (idx == 0) {
            return new FlatDouble4D(data, dim1, dim2, dim3, dim4);
        } else {
            return new StriddenDouble4D(data,
                    dim1dim2dim3dim4*idx, // offset
                    1, dim1, dim1dim2, dim1dim2dim3, // strides
                    dim1, dim2, dim3, dim4); // dimensions
        }
    }

    @Override
    public Double4D slice(int idx, int dim) {
        int sliceOffset;
        int sliceStride1, sliceStride2, sliceStride3, sliceStride4;
        int sliceDim1, sliceDim2, sliceDim3, sliceDim4;
        dim = Helper.fixSliceIndex(dim, 5);
        if (dim == 0) {
            /* Slice along 1st dimension. */
            sliceOffset = Helper.fixIndex(idx, dim1);
            sliceStride1 = dim1;
            sliceStride2 = dim1dim2;
            sliceStride3 = dim1dim2dim3;
            sliceStride4 = dim1dim2dim3dim4;
            sliceDim1 = dim2;
            sliceDim2 = dim3;
            sliceDim3 = dim4;
            sliceDim4 = dim5;
        } else if (dim == 1) {
            /* Slice along 2nd dimension. */
            sliceOffset = dim1*Helper.fixIndex(idx, dim2);
            sliceStride1 = 1;
            sliceStride2 = dim1dim2;
            sliceStride3 = dim1dim2dim3;
            sliceStride4 = dim1dim2dim3dim4;
            sliceDim1 = dim1;
            sliceDim2 = dim3;
            sliceDim3 = dim4;
            sliceDim4 = dim5;
        } else if (dim == 2) {
            /* Slice along 3rd dimension. */
            sliceOffset = dim1dim2*Helper.fixIndex(idx, dim3);
            sliceStride1 = 1;
            sliceStride2 = dim1;
            sliceStride3 = dim1dim2dim3;
            sliceStride4 = dim1dim2dim3dim4;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim4;
            sliceDim4 = dim5;
        } else if (dim == 3) {
            /* Slice along 4th dimension. */
            sliceOffset = dim1dim2dim3*Helper.fixIndex(idx, dim4);
            sliceStride1 = 1;
            sliceStride2 = dim1;
            sliceStride3 = dim1dim2;
            sliceStride4 = dim1dim2dim3dim4;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
            sliceDim4 = dim5;
        } else {
            /* Slice along 5th dimension. */
            sliceOffset = dim1dim2dim3dim4*Helper.fixIndex(idx, dim5);
            sliceStride1 = 1;
            sliceStride2 = dim1;
            sliceStride3 = dim1dim2;
            sliceStride4 = dim1dim2dim3;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
            sliceDim4 = dim4;
        }
        return new StriddenDouble4D(data, sliceOffset,
                sliceStride1, sliceStride2, sliceStride3, sliceStride4,
                sliceDim1, sliceDim2, sliceDim3, sliceDim4);
    }

    @Override
    public Double5D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, 0, 1);
        CompiledRange cr2 = new CompiledRange(rng2, dim2, 0, dim1);
        CompiledRange cr3 = new CompiledRange(rng3, dim3, 0, dim1dim2);
        CompiledRange cr4 = new CompiledRange(rng4, dim4, 0, dim1dim2dim3);
        CompiledRange cr5 = new CompiledRange(rng5, dim5, 0, dim1dim2dim3dim4);
        if (cr1.doesNothing() && cr2.doesNothing() && cr3.doesNothing() && cr4.doesNothing() && cr5.doesNothing()) {
            return this;
        }
        return new StriddenDouble5D(this.data,
                cr1.getOffset() + cr2.getOffset() + cr3.getOffset() + cr4.getOffset() + cr5.getOffset(),
                cr1.getStride(), cr2.getStride(), cr3.getStride(), cr4.getStride(), cr5.getStride(),
                cr1.getNumber(), cr2.getNumber(), cr3.getNumber(), cr4.getNumber(), cr5.getNumber());
    }

    @Override
    public Double5D view(int[] sel1, int[] sel2, int[] sel3, int[] sel4, int[] sel5) {
        int[] idx1 = Helper.select(0, 1, dim1, sel1);
        int[] idx2 = Helper.select(0, dim1, dim2, sel2);
        int[] idx3 = Helper.select(0, dim1dim2, dim3, sel3);
        int[] idx4 = Helper.select(0, dim1dim2dim3, dim4, sel4);
        int[] idx5 = Helper.select(0, dim1dim2dim3dim4, dim5, sel5);
        return new SelectedDouble5D(this.data, idx1, idx2, idx3, idx4, idx5);
    }

    @Override
    public Double1D as1D() {
        return new FlatDouble1D(data, number);
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
