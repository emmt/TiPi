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
import mitiv.base.Shape;
import mitiv.base.indexing.CompiledRange;
import mitiv.exception.NonConformableArrayException;


/**
 * Flat implementation of 1-dimensional arrays of double's.
 *
 * @author Éric Thiébaut.
 */
public class FlatDouble1D extends Double1D {
    static final int order = COLUMN_MAJOR;
    final double[] data;

    public FlatDouble1D(int dim1) {
        super(dim1);
        data = new double[number];
    }

    public FlatDouble1D(int[] dims) {
        super(dims);
        data = new double[number];
    }

    public FlatDouble1D(Shape shape) {
        super(shape);
        data = new double[number];
    }

    public FlatDouble1D(double[] arr, int dim1) {
        super(dim1);
        checkSize(arr);
        data = arr;
    }

    public FlatDouble1D(double[] arr, int[] dims) {
        super(dims);
        checkSize(arr);
        data = arr;
    }

    public FlatDouble1D(double[] arr, Shape shape) {
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

    private void checkSize(double[] arr) {
        if (arr == null || arr.length < number) {
            throw new NonConformableArrayException("Wrapped array is too small.");
        }
    }

    final int index(int i1) {
        return i1;
    }

    @Override
    public final double get(int i1) {
        return data[i1];
    }

    @Override
    public final void set(int i1, double value) {
        data[i1] = value;
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
    public final boolean isFlat() {
        return true;
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
    public double[] getData() {
        return data;
    }

    @Override
    public DoubleScalar slice(int idx) {
        idx = Helper.fixIndex(idx, dim1);
        return new DoubleScalar(data, idx);
    }

    @Override
    public DoubleScalar slice(int idx, int dim) {
        Helper.fixSliceIndex(dim, 1); // throws an exception if dim != 0
        return new DoubleScalar(data, Helper.fixIndex(idx, dim1));
    }

    @Override
    public Double1D view(Range rng1) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, 0, 1);
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
        int[] idx1 = Helper.select(0, 1, dim1, sel1);
        return new SelectedDouble1D(this.data, idx1);
    }

    @Override
    public Double1D as1D() {
        return this;
    }

}
