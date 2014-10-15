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

package mitiv.array;

import mitiv.base.mapping.DoubleFunction;
import mitiv.base.mapping.DoubleScanner;
import mitiv.random.DoubleGenerator;

/**
 * Selected implementation of 0-dimensional arrays of double's.
 *
 * This specific kind of arrays/views are accessed via indirection tables (one
 * for each dimension).
 *
 * @author Éric Thiébaut.
 */
public class DoubleScalar extends Scalar implements DoubleArray {
    final double[] data;
    final int offset;

    /**
     * Create a DoubleScalar pointing a slot in a Java array.
     *
     * @param arr - The array.
     * @param idx - The index of the scalar element in the array.
     */
    public DoubleScalar(double[] arr, int idx) {
        super();
        this.data = arr;
        this.offset = idx;
        checkSanity();
    }

    /**
     * Create a DoubleScalar with an initial value.
     *
     * @param value - The initial value of the scalar.
     */
    public DoubleScalar(double value) {
        super();
        data = new double[]{value};
        offset = 0;
    }

    @Override
    public final void checkSanity() {
        if (offset < 0 || offset >= data.length) {
            throw new IndexOutOfBoundsException("Scalar offset is out of bounds.");
        }
    }

    @Override
    public final int getOrder() {
        return COLUMN_MAJOR;
    }

    final int index() {
        return offset;
    }

    public final double get() {
        return data[offset];
    }

    public final void set(double value) {
        data[offset] = value;
    }

    @Override
    public void fill(double value) {
        data[offset] = value;
    }

    @Override
    public void fill(DoubleGenerator generator) {
        data[offset] = generator.nextDouble();
    }

    @Override
    public void increment(double value) {
        data[offset] += value;
    }

    @Override
    public void decrement(double value) {
        data[offset] -= value;
    }

    @Override
    public void scale(double value) {
        data[offset] *= value;
    }

    @Override
    public void map(DoubleFunction function) {
        data[offset] = function.apply(data[offset]);
    }

    @Override
    public void scan(DoubleScanner scanner)  {
        scanner.initialize(data[offset]);
    }

    @Override
    public double[] flatten() {
        return flatten(false);
    }

    @Override
    public double[] flatten(boolean forceCopy) {
        if (! forceCopy && offset == 0 && data.length == number) {
            return data;
        }
        double[] out = new double[number];
        out[0] = data[offset];
        return out;
    }

    @Override
    public ByteArray toByte() {
        return new ByteScalar((byte)data[offset]);
    }

    @Override
    public ShortArray toShort() {
        return new ShortScalar((short)data[offset]);
    }

    @Override
    public IntArray toInt() {
        return new IntScalar((int)data[offset]);
    }

    @Override
    public LongArray toLong() {
        return new LongScalar((long)data[offset]);
    }

    @Override
    public FloatArray toFloat() {
        return new FloatScalar((float)data[offset]);
    }

    @Override
    public DoubleArray toDouble() {
        return this;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public Double1D as1D() {
        // TODO Auto-generated method stub
        // FOXME: return a stridden 1D array
        return null;
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
