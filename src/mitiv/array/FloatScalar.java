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

import mitiv.array.impl.FlatFloat1D;
import mitiv.array.impl.StriddenFloat1D;
import mitiv.base.Traits;
import mitiv.base.mapping.FloatFunction;
import mitiv.base.mapping.FloatScanner;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.NonConformableArrayException;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.random.FloatGenerator;

/**
 * Selected implementation of 0-dimensional float arrays (i.e. scalars).
 * <p>
 * This specific kind of scalar values can be used to <i>view</i> a single
 * element of another multi-dimensional array.
 * </p>
 * @author Éric Thiébaut.
 */
public class FloatScalar extends Scalar implements FloatArray {
    final float[] data;
    final int offset;
    final boolean flat;

    /**
     * Create a new FloatScalar object.
     */
    public FloatScalar() {
        super();
        this.data = new float[1];
        this.offset = 0;
        this.flat = true;
    }

    /**
     * Create a new FloatScalar object of same type.
     */
    @Override
    public FloatScalar create() {
        return new FloatScalar();
    }

    /**
     * Create a FloatScalar pointing a slot in a Java array.
     *
     * @param arr - The array.
     * @param idx - The index of the scalar element in the array.
     */
    public FloatScalar(float[] arr, int idx) {
        super();
        this.data = arr;
        this.offset = idx;
        this.flat = (idx == 0 && arr.length == 1);
        checkSanity();
    }

    /**
     * Wrap a slot in a Java array into a new FloatScalar object.
     */
    public static FloatScalar wrap(float[] arr, int idx) {
        return new FloatScalar(arr, idx);
    }

    /**
     * Create a FloatScalar with an initial value.
     *
     * @param value - The initial value of the scalar.
     */
    public FloatScalar(float value) {
        super();
        data = new float[]{value};
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

    public final float get() {
        return data[offset];
    }

    public final void set(float value) {
        data[offset] = value;
    }

    @Override
    public final void fill(float value) {
        data[offset] = value;
    }

    @Override
    public final void fill(FloatGenerator generator) {
        data[offset] = generator.nextFloat();
    }

    @Override
    public final void increment(float value) {
        data[offset] += value;
    }

    @Override
    public final void decrement(float value) {
        data[offset] -= value;
    }

    @Override
    public final void scale(float value) {
        data[offset] *= value;
    }

    @Override
    public final void map(FloatFunction function) {
        data[offset] = function.apply(data[offset]);
    }

    @Override
    public final void scan(FloatScanner scanner)  {
        scanner.initialize(data[offset]);
    }

    @Override
    public final boolean isFlat() {
        return flat;
    }

    @Override
    public final float[] flatten() {
        return flatten(false);
    }

    @Override
    public final float[] flatten(boolean forceCopy) {
        if (! forceCopy && flat) {
            return data;
        }
        return new float[]{data[offset]};
    }

    @Override
    public float min() {
        return data[offset];
    }

    @Override
    public float max() {
        return data[offset];
    }

    @Override
    public float[] getMinAndMax() {
        float[] result = new float[2];
        getMinAndMax(result);
        return result;
    }

    @Override
    public void getMinAndMax(float[] mm) {
        float value = data[offset];
        mm[0] = value;
        mm[1] = value;
    }

    @Override
    public float sum() {
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
        return this;
    }

    @Override
    public final DoubleArray toDouble() {
        return new DoubleScalar((double)data[offset]);
    }

    @Override
    public final Float1D as1D() {
        if (offset == 0) {
            return new FlatFloat1D(data, 1);
        } else {
            return new StriddenFloat1D(data, offset, 0, 1);
        }
    }

    @Override
    public final void assign(ShapedArray src) {
        if (! shape.equals(src.getShape())) {
            throw new NonConformableArrayException();
        }
        switch (src.getType()) {
        case Traits.BYTE:
            data[offset] = (float)((ByteScalar)src).get();
            break;
        case Traits.SHORT:
            data[offset] = (float)((ShortScalar)src).get();
            break;
        case Traits.INT:
            data[offset] = (float)((IntScalar)src).get();
            break;
        case Traits.LONG:
            data[offset] = (float)((LongScalar)src).get();
            break;
        case Traits.FLOAT:
            data[offset] = (float)((FloatScalar)src).get();
            break;
        case Traits.DOUBLE:
            data[offset] = (float)((DoubleScalar)src).get();
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
        data[offset] = (float)src.get(0);
    }

    @Override
    public final FloatScalar copy() {
        return new FloatScalar(data[offset]);
    }
}
