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

import mitiv.array.Long1D;
import mitiv.array.Long8D;
import mitiv.array.Long9D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.LongFunction;
import mitiv.base.mapping.LongScanner;
import mitiv.random.LongGenerator;
import mitiv.base.Shape;
import mitiv.exception.NonConformableArrayException;


/**
 * Flat implementation of 9-dimensional arrays of long's.
 *
 * @author Éric Thiébaut.
 */
public class FlatLong9D extends Long9D {
    static final int order = COLUMN_MAJOR;
    final long[] data;
    final int dim1dim2;
    final int dim1dim2dim3;
    final int dim1dim2dim3dim4;
    final int dim1dim2dim3dim4dim5;
    final int dim1dim2dim3dim4dim5dim6;
    final int dim1dim2dim3dim4dim5dim6dim7;
    final int dim1dim2dim3dim4dim5dim6dim7dim8;

    public FlatLong9D(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        super(dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
        data = new long[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
        dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
        dim1dim2dim3dim4dim5dim6dim7dim8 = dim1dim2dim3dim4dim5dim6dim7*dim8;
    }

    public FlatLong9D(int[] dims) {
        super(dims);
        data = new long[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
        dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
        dim1dim2dim3dim4dim5dim6dim7dim8 = dim1dim2dim3dim4dim5dim6dim7*dim8;
    }

    public FlatLong9D(Shape shape) {
        super(shape);
        data = new long[number];
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
        dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
        dim1dim2dim3dim4dim5dim6dim7dim8 = dim1dim2dim3dim4dim5dim6dim7*dim8;
    }

    public FlatLong9D(long[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        super(dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
        dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
        dim1dim2dim3dim4dim5dim6dim7dim8 = dim1dim2dim3dim4dim5dim6dim7*dim8;
    }

    public FlatLong9D(long[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
        dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
        dim1dim2dim3dim4dim5dim6dim7dim8 = dim1dim2dim3dim4dim5dim6dim7*dim8;
    }

    public FlatLong9D(long[] arr, Shape shape) {
        super(shape);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
        dim1dim2dim3 = dim1dim2*dim3;
        dim1dim2dim3dim4 = dim1dim2dim3*dim4;
        dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
        dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
        dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
        dim1dim2dim3dim4dim5dim6dim7dim8 = dim1dim2dim3dim4dim5dim6dim7*dim8;
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

    private void checkSize(long[] arr) {
        if (arr == null || arr.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    final int index(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        return dim1dim2dim3dim4dim5dim6dim7dim8*i9 + dim1dim2dim3dim4dim5dim6dim7*i8 + dim1dim2dim3dim4dim5dim6*i7 + dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1;
    }

    @Override
    public final long get(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        return data[dim1dim2dim3dim4dim5dim6dim7dim8*i9 + dim1dim2dim3dim4dim5dim6dim7*i8 + dim1dim2dim3dim4dim5dim6*i7 + dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, long value) {
        data[dim1dim2dim3dim4dim5dim6dim7dim8*i9 + dim1dim2dim3dim4dim5dim6dim7*i8 + dim1dim2dim3dim4dim5dim6*i7 + dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(long value) {
         for (int j = 0; j < number; ++j) {
            data[j] = value;
         }
    }

    @Override
    public void fill(LongGenerator generator) {
        for (int j = 0; j < number; ++j) {
            data[j] = generator.nextLong();
        }
    }

    @Override
    public void increment(long value) {
        for (int j = 0; j < number; ++j) {
            data[j] += value;
        }
    }

    @Override
    public void decrement(long value) {
        for (int j = 0; j < number; ++j) {
            data[j] -= value;
        }
    }

    @Override
    public void scale(long value) {
        for (int j = 0; j < number; ++j) {
            data[j] *= value;
        }
    }

    @Override
    public void map(LongFunction function) {
        for (int j = 0; j < number; ++j) {
            data[j] = function.apply(data[j]);
        }
    }

    @Override
    public void scan(LongScanner scanner)  {
        scanner.initialize(data[0]);
        for (int j = 1; j < number; ++j) {
            scanner.update(data[j]);
        }
    }

    @Override
    public long[] flatten(boolean forceCopy) {
        if (forceCopy) {
            long[] out = new long[number];
            System.arraycopy(data, 0, out, 0, number);
            return out;
        } else {
            return data;
        }
    }
    @Override
    public Long8D slice(int idx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long8D slice(int idx, int dim) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long9D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5, Range rng6, Range rng7, Range rng8, Range rng9) {
        // TODO Auto-generated method stub
        return null;
    }

   @Override
    public Long9D view(int[] idx1, int[] idx2, int[] idx3, int[] idx4, int[] idx5, int[] idx6, int[] idx7, int[] idx8, int[] idx9) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long1D as1D() {
        return new FlatLong1D(data, number);
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
