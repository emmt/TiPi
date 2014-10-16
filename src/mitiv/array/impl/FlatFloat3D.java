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
import mitiv.array.Float2D;
import mitiv.array.Float3D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.FloatFunction;
import mitiv.base.mapping.FloatScanner;
import mitiv.random.FloatGenerator;
import mitiv.base.Shape;
import mitiv.exception.NonConformableArrayException;


/**
 * Flat implementation of 3-dimensional arrays of float's.
 *
 * @author Éric Thiébaut.
 */
public class FlatFloat3D extends Float3D {
    static final int order = COLUMN_MAJOR;
    final float[] data;
    final int dim1dim2;

    public FlatFloat3D(int dim1, int dim2, int dim3) {
        super(dim1, dim2, dim3);
        data = new float[number];
        dim1dim2 = dim1*dim2;
    }

    public FlatFloat3D(int[] dims) {
        super(dims);
        data = new float[number];
        dim1dim2 = dim1*dim2;
    }

    public FlatFloat3D(Shape shape) {
        super(shape);
        data = new float[number];
        dim1dim2 = dim1*dim2;
    }

    public FlatFloat3D(float[] arr, int dim1, int dim2, int dim3) {
        super(dim1, dim2, dim3);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
    }

    public FlatFloat3D(float[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
    }

    public FlatFloat3D(float[] arr, Shape shape) {
        super(shape);
        checkSize(arr);
        data = arr;
        dim1dim2 = dim1*dim2;
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

    final int index(int i1, int i2, int i3) {
        return dim1dim2*i3 + dim1*i2 + i1;
    }

    @Override
    public final float get(int i1, int i2, int i3) {
        return data[dim1dim2*i3 + dim1*i2 + i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, float value) {
        data[dim1dim2*i3 + dim1*i2 + i1] = value;
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
            float[] out = new float[number];
            System.arraycopy(data, 0, out, 0, number);
            return out;
        } else {
            return data;
        }
    }
    @Override
    public Float2D slice(int idx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Float2D slice(int idx, int dim) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Float3D view(Range rng1, Range rng2, Range rng3) {
        // TODO Auto-generated method stub
        return null;
    }

   @Override
    public Float3D view(int[] idx1, int[] idx2, int[] idx3) {
        // TODO Auto-generated method stub
        return null;
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
