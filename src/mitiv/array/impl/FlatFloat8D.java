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

import mitiv.array.Float1D;
import mitiv.array.Float7D;
import mitiv.array.Float8D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.FloatFunction;
import mitiv.base.mapping.FloatScanner;
import mitiv.random.FloatGenerator;
import mitiv.array.ArrayUtils;
import mitiv.base.Shape;
import mitiv.base.indexing.CompiledRange;
import mitiv.exception.NonConformableArrayException;
import mitiv.exception.IllegalRangeException;


/**
 * Flat implementation of 8-dimensional arrays of float's.
 *
 * @author Éric Thiébaut.
 */
public class FlatFloat8D extends Float8D {
    static final int order = COLUMN_MAJOR;
    final float[] data;
    final int dim1dim2;
    final int dim1dim2dim3;
    final int dim1dim2dim3dim4;
    final int dim1dim2dim3dim4dim5;
    final int dim1dim2dim3dim4dim5dim6;
    final int dim1dim2dim3dim4dim5dim6dim7;

    public FlatFloat8D(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        super(dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
        data = new float[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
        dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
    }

    public FlatFloat8D(int[] dims) {
        super(dims);
        data = new float[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
        dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
    }

    public FlatFloat8D(Shape shape) {
        super(shape);
        data = new float[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
        dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
    }

    public FlatFloat8D(float[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        super(dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
        dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
    }

    public FlatFloat8D(float[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
        dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
    }

    public FlatFloat8D(float[] arr, Shape shape) {
        super(shape);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
        dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
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

    private void checkSize(float[] arr) {
        if (arr == null || arr.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    final int index(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        return dim1dim2dim3dim4dim5dim6dim7*i8 + dim1dim2dim3dim4dim5dim6*i7 + dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1;
    }

    @Override
    public final float get(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        return data[dim1dim2dim3dim4dim5dim6dim7*i8 + dim1dim2dim3dim4dim5dim6*i7 + dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float value) {
        data[dim1dim2dim3dim4dim5dim6dim7*i8 + dim1dim2dim3dim4dim5dim6*i7 + dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(float value) {
         for (int j = 0; j < number; ++j) {
            data[j] = value;
         }
    }

    @Override
    public void fill(FloatGenerator generator) {
        for (int j = 0; j < number; ++j) {
            data[j] = generator.nextFloat();
        }
    }

    @Override
    public void increment(float value) {
        for (int j = 0; j < number; ++j) {
            data[j] += value;
        }
    }

    @Override
    public void decrement(float value) {
        for (int j = 0; j < number; ++j) {
            data[j] -= value;
        }
    }

    @Override
    public void scale(float value) {
        for (int j = 0; j < number; ++j) {
            data[j] *= value;
        }
    }

    @Override
    public void map(FloatFunction function) {
        for (int j = 0; j < number; ++j) {
            data[j] = function.apply(data[j]);
        }
    }

    @Override
    public void scan(FloatScanner scanner)  {
        scanner.initialize(data[0]);
        for (int j = 1; j < number; ++j) {
            scanner.update(data[j]);
        }
    }

    @Override
    public float[] flatten(boolean forceCopy) {
        if (forceCopy) {
            float[] result = new float[number];
            System.arraycopy(data, 0, result, 0, number);
            return result;
        } else {
            return data;
        }
    }

    @Override
    public Float7D slice(int idx) {
        if (idx == 0) {
            return new FlatFloat7D(data, dim1, dim2, dim3, dim4, dim5, dim6, dim7);
        } else {
            return new StriddenFloat7D(data,
                    dim1dim2dim3dim4dim5dim6dim7*idx, // offset
                    1, dim1, dim1dim2, dim1dim2dim3, dim1dim2dim3dim4, dim1dim2dim3dim4dim5, dim1dim2dim3dim4dim5dim6, // strides
                    dim1, dim2, dim3, dim4, dim5, dim6, dim7); // dimensions
        }
    }

    @Override
    public Float7D slice(int idx, int dim) {
        int sliceOffset;
        int sliceStride1, sliceStride2, sliceStride3, sliceStride4, sliceStride5, sliceStride6, sliceStride7;
        int sliceDim1, sliceDim2, sliceDim3, sliceDim4, sliceDim5, sliceDim6, sliceDim7;
        if (dim < 0) {
            /* A negative index is taken with respect to the end. */
            dim += 8;
        }
        if (dim == 0) {
            /* Slice along 1st dimension. */
            sliceOffset = idx;
            sliceStride1 = dim1;
            sliceStride2 = dim1dim2;
            sliceStride3 = dim1dim2dim3;
            sliceStride4 = dim1dim2dim3dim4;
            sliceStride5 = dim1dim2dim3dim4dim5;
            sliceStride6 = dim1dim2dim3dim4dim5dim6;
            sliceStride7 = dim1dim2dim3dim4dim5dim6dim7;
            sliceDim1 = dim2;
            sliceDim2 = dim3;
            sliceDim3 = dim4;
            sliceDim4 = dim5;
            sliceDim5 = dim6;
            sliceDim6 = dim7;
            sliceDim7 = dim8;
        } else if (dim == 1) {
            /* Slice along 2nd dimension. */
            sliceOffset = dim1*idx;
            sliceStride1 = 1;
            sliceStride2 = dim1dim2;
            sliceStride3 = dim1dim2dim3;
            sliceStride4 = dim1dim2dim3dim4;
            sliceStride5 = dim1dim2dim3dim4dim5;
            sliceStride6 = dim1dim2dim3dim4dim5dim6;
            sliceStride7 = dim1dim2dim3dim4dim5dim6dim7;
            sliceDim1 = dim1;
            sliceDim2 = dim3;
            sliceDim3 = dim4;
            sliceDim4 = dim5;
            sliceDim5 = dim6;
            sliceDim6 = dim7;
            sliceDim7 = dim8;
        } else if (dim == 2) {
            /* Slice along 3rd dimension. */
            sliceOffset = dim1dim2*idx;
            sliceStride1 = 1;
            sliceStride2 = dim1;
            sliceStride3 = dim1dim2dim3;
            sliceStride4 = dim1dim2dim3dim4;
            sliceStride5 = dim1dim2dim3dim4dim5;
            sliceStride6 = dim1dim2dim3dim4dim5dim6;
            sliceStride7 = dim1dim2dim3dim4dim5dim6dim7;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim4;
            sliceDim4 = dim5;
            sliceDim5 = dim6;
            sliceDim6 = dim7;
            sliceDim7 = dim8;
        } else if (dim == 3) {
            /* Slice along 4th dimension. */
            sliceOffset = dim1dim2dim3*idx;
            sliceStride1 = 1;
            sliceStride2 = dim1;
            sliceStride3 = dim1dim2;
            sliceStride4 = dim1dim2dim3dim4;
            sliceStride5 = dim1dim2dim3dim4dim5;
            sliceStride6 = dim1dim2dim3dim4dim5dim6;
            sliceStride7 = dim1dim2dim3dim4dim5dim6dim7;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
            sliceDim4 = dim5;
            sliceDim5 = dim6;
            sliceDim6 = dim7;
            sliceDim7 = dim8;
        } else if (dim == 4) {
            /* Slice along 5th dimension. */
            sliceOffset = dim1dim2dim3dim4*idx;
            sliceStride1 = 1;
            sliceStride2 = dim1;
            sliceStride3 = dim1dim2;
            sliceStride4 = dim1dim2dim3;
            sliceStride5 = dim1dim2dim3dim4dim5;
            sliceStride6 = dim1dim2dim3dim4dim5dim6;
            sliceStride7 = dim1dim2dim3dim4dim5dim6dim7;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
            sliceDim4 = dim4;
            sliceDim5 = dim6;
            sliceDim6 = dim7;
            sliceDim7 = dim8;
        } else if (dim == 5) {
            /* Slice along 6th dimension. */
            sliceOffset = dim1dim2dim3dim4dim5*idx;
            sliceStride1 = 1;
            sliceStride2 = dim1;
            sliceStride3 = dim1dim2;
            sliceStride4 = dim1dim2dim3;
            sliceStride5 = dim1dim2dim3dim4;
            sliceStride6 = dim1dim2dim3dim4dim5dim6;
            sliceStride7 = dim1dim2dim3dim4dim5dim6dim7;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
            sliceDim4 = dim4;
            sliceDim5 = dim5;
            sliceDim6 = dim7;
            sliceDim7 = dim8;
        } else if (dim == 6) {
            /* Slice along 7th dimension. */
            sliceOffset = dim1dim2dim3dim4dim5dim6*idx;
            sliceStride1 = 1;
            sliceStride2 = dim1;
            sliceStride3 = dim1dim2;
            sliceStride4 = dim1dim2dim3;
            sliceStride5 = dim1dim2dim3dim4;
            sliceStride6 = dim1dim2dim3dim4dim5;
            sliceStride7 = dim1dim2dim3dim4dim5dim6dim7;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
            sliceDim4 = dim4;
            sliceDim5 = dim5;
            sliceDim6 = dim6;
            sliceDim7 = dim8;
        } else if (dim == 7) {
            /* Slice along 8th dimension. */
            sliceOffset = dim1dim2dim3dim4dim5dim6dim7*idx;
            sliceStride1 = 1;
            sliceStride2 = dim1;
            sliceStride3 = dim1dim2;
            sliceStride4 = dim1dim2dim3;
            sliceStride5 = dim1dim2dim3dim4;
            sliceStride6 = dim1dim2dim3dim4dim5;
            sliceStride7 = dim1dim2dim3dim4dim5dim6;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
            sliceDim4 = dim4;
            sliceDim5 = dim5;
            sliceDim6 = dim6;
            sliceDim7 = dim7;
        } else {
            throw new IndexOutOfBoundsException("Dimension index out of bounds.");
        }
        return new StriddenFloat7D(data, sliceOffset,
                sliceStride1, sliceStride2, sliceStride3, sliceStride4, sliceStride5, sliceStride6, sliceStride7,
                sliceDim1, sliceDim2, sliceDim3, sliceDim4, sliceDim5, sliceDim6, sliceDim7);
    }

    @Override
    public Float8D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5, Range rng6, Range rng7, Range rng8) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, 0, 1);
        CompiledRange cr2 = new CompiledRange(rng2, dim2, 0, dim1);
        CompiledRange cr3 = new CompiledRange(rng3, dim3, 0, dim1dim2);
        CompiledRange cr4 = new CompiledRange(rng4, dim4, 0, dim1dim2dim3);
        CompiledRange cr5 = new CompiledRange(rng5, dim5, 0, dim1dim2dim3dim4);
        CompiledRange cr6 = new CompiledRange(rng6, dim6, 0, dim1dim2dim3dim4dim5);
        CompiledRange cr7 = new CompiledRange(rng7, dim7, 0, dim1dim2dim3dim4dim5dim6);
        CompiledRange cr8 = new CompiledRange(rng8, dim8, 0, dim1dim2dim3dim4dim5dim6dim7);
        if (cr1.doesNothing() && cr2.doesNothing() && cr3.doesNothing() && cr4.doesNothing() && cr5.doesNothing() && cr6.doesNothing() && cr7.doesNothing() && cr8.doesNothing()) {
            return this;
        }
        if (cr1.getNumber() == 0 || cr2.getNumber() == 0 || cr3.getNumber() == 0 || cr4.getNumber() == 0 || cr5.getNumber() == 0 || cr6.getNumber() == 0 || cr7.getNumber() == 0 || cr8.getNumber() == 0) {
            throw new IllegalRangeException("Empty range.");
        }
        return new StriddenFloat8D(this.data,
                cr1.getOffset() + cr2.getOffset() + cr3.getOffset() + cr4.getOffset() + cr5.getOffset() + cr6.getOffset() + cr7.getOffset() + cr8.getOffset(),
                cr1.getStride(), cr2.getStride(), cr3.getStride(), cr4.getStride(), cr5.getStride(), cr6.getStride(), cr7.getStride(), cr8.getStride(),
                cr1.getNumber(), cr2.getNumber(), cr3.getNumber(), cr4.getNumber(), cr5.getNumber(), cr6.getNumber(), cr7.getNumber(), cr8.getNumber());
    }

    @Override
    public Float8D view(int[] sel1, int[] sel2, int[] sel3, int[] sel4, int[] sel5, int[] sel6, int[] sel7, int[] sel8) {
        int[] idx1 = ArrayUtils.select(0, 1, dim1, sel1);
        int[] idx2 = ArrayUtils.select(0, dim1, dim2, sel2);
        int[] idx3 = ArrayUtils.select(0, dim1dim2, dim3, sel3);
        int[] idx4 = ArrayUtils.select(0, dim1dim2dim3, dim4, sel4);
        int[] idx5 = ArrayUtils.select(0, dim1dim2dim3dim4, dim5, sel5);
        int[] idx6 = ArrayUtils.select(0, dim1dim2dim3dim4dim5, dim6, sel6);
        int[] idx7 = ArrayUtils.select(0, dim1dim2dim3dim4dim5dim6, dim7, sel7);
        int[] idx8 = ArrayUtils.select(0, dim1dim2dim3dim4dim5dim6dim7, dim8, sel8);
        return new SelectedFloat8D(this.data, idx1, idx2, idx3, idx4, idx5, idx6, idx7, idx8);
    }

    @Override
    public Float1D as1D() {
        return new FlatFloat1D(data, number);
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
