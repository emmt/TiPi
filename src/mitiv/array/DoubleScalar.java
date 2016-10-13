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

import mitiv.array.impl.FlatDouble1D;
import mitiv.array.impl.StriddenDouble1D;
import mitiv.base.Traits;
import mitiv.base.mapping.DoubleFunction;
import mitiv.base.mapping.DoubleScanner;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.NonConformableArrayException;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.random.DoubleGenerator;

/**
 * Selected implementation of 0-dimensional double arrays (i.e. scalars).
 * <p>
 * This specific kind of scalar values can be used to <i>view</i> a single
 * element of another multi-dimensional array.
 * </p>
 * @author Éric Thiébaut.
 */
public class DoubleScalar extends Scalar implements DoubleArray {
    final double[] data;
    final int offset;
    final boolean flat;

    /**
     * Create a new DoubleScalar object.
     */
    public DoubleScalar() {
        super();
        this.data = new double[1];
        this.offset = 0;
        this.flat = true;
    }

    /**
     * Create a new DoubleScalar object of same type.
     */
    @Override
    public DoubleScalar create() {
        return new DoubleScalar();
    }

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
        this.flat = (idx == 0 && arr.length == 1);
        checkSanity();
    }

    /**
     * Wrap a slot in a Java array into a new DoubleScalar object.
     */
    public static DoubleScalar wrap(double[] arr, int idx) {
        return new DoubleScalar(arr, idx);
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
        flat = true;
    }

    @Override
    public final void checkSanity() {
        if (offset < 0 || offset >= data.length) {
            throw new IndexOutOfBoundsException("Scalar offset is out of bounds");
        }
    }

    @Override
    public final int getType() {
        return type;
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
    public final void fill(double value) {
        data[offset] = value;
    }

    @Override
    public final void fill(DoubleGenerator generator) {
        data[offset] = generator.nextDouble();
    }

    @Override
    public final void increment(double value) {
        data[offset] += value;
    }

    @Override
    public final void decrement(double value) {
        data[offset] -= value;
    }

    @Override
    public final void scale(double value) {
        data[offset] *= value;
    }

    @Override
    public final void map(DoubleFunction function) {
        data[offset] = function.apply(data[offset]);
    }

    @Override
    public final void scan(DoubleScanner scanner)  {
        scanner.initialize(data[offset]);
    }

    @Override
    public final boolean isFlat() {
        return flat;
    }

    @Override
    public final double[] flatten() {
        return flatten(false);
    }

    @Override
    public final double[] flatten(boolean forceCopy) {
        if (! forceCopy && flat) {
            return data;
        }
        return new double[]{data[offset]};
    }

    @Override
    public double min() {
        return data[offset];
    }

    @Override
    public double max() {
        return data[offset];
    }

    @Override
    public double[] getMinAndMax() {
        double[] result = new double[2];
        getMinAndMax(result);
        return result;
    }

    @Override
    public void getMinAndMax(double[] mm) {
        double value = data[offset];
        mm[0] = value;
        mm[1] = value;
    }

    @Override
    public double sum() {
        return data[offset];
    }

    @Override
    public double average() {
        return data[offset];
    }

    @Override
    public final ByteArray toByte() {
        return new ByteScalar((byte)data[offset]);
    }

    @Override
    public final ShortArray toShort() {
        return new ShortScalar((short)data[offset]);
    }

    @Override
    public final IntArray toInt() {
        return new IntScalar((int)data[offset]);
    }

    @Override
    public final LongArray toLong() {
        return new LongScalar((long)data[offset]);
    }

    @Override
    public final FloatArray toFloat() {
        return new FloatScalar((float)data[offset]);
    }

    @Override
    public final DoubleArray toDouble() {
        return this;
    }

    @Override
    public final Double1D as1D() {
        if (offset == 0) {
            return new FlatDouble1D(data, 1);
        } else {
            return new StriddenDouble1D(data, offset, 0, 1);
        }
    }

    @Override
    public final void assign(ShapedArray src) {
        if (! shape.equals(src.getShape())) {
            throw new NonConformableArrayException();
        }
        switch (src.getType()) {
        case Traits.BYTE:
            data[offset] = (double)((ByteScalar)src).get();
            break;
        case Traits.SHORT:
            data[offset] = (double)((ShortScalar)src).get();
            break;
        case Traits.INT:
            data[offset] = (double)((IntScalar)src).get();
            break;
        case Traits.LONG:
            data[offset] = (double)((LongScalar)src).get();
            break;
        case Traits.FLOAT:
            data[offset] = (double)((FloatScalar)src).get();
            break;
        case Traits.DOUBLE:
            data[offset] = (double)((DoubleScalar)src).get();
            break;
        default:
            throw new IllegalTypeException();
        }
    }

    @Override
    public final void assign(ShapedVector src) {
        if (! shape.equals(src.getShape())) {
            throw new NonConformableArrayException();
        }
        data[offset] = (double)src.get(0);
    }

    @Override
    public final DoubleScalar copy() {
        return new DoubleScalar(data[offset]);
    }
}
