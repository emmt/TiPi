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

import mitiv.array.impl.FlatByte1D;
import mitiv.array.impl.StriddenByte1D;
import mitiv.base.Traits;
import mitiv.base.mapping.ByteFunction;
import mitiv.base.mapping.ByteScanner;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.NonConformableArrayException;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.random.ByteGenerator;

/**
 * Selected implementation of 0-dimensional byte arrays (i.e. scalars).
 * <p>
 * This specific kind of scalar values can be used to <i>view</i> a single
 * element of another multi-dimensional array.
 * </p>
 * @author Éric Thiébaut.
 */
public class ByteScalar extends Scalar implements ByteArray {
    final byte[] data;
    final int offset;
    final boolean flat;

    /**
     * Create a new ByteScalar object.
     */
    public ByteScalar() {
        super();
        this.data = new byte[1];
        this.offset = 0;
        this.flat = true;
    }

    /**
     * Create a new ByteScalar object of same type.
     */
    @Override
    public ByteScalar create() {
        return new ByteScalar();
    }

    /**
     * Create a ByteScalar pointing a slot in a Java array.
     *
     * @param arr - The array.
     * @param idx - The index of the scalar element in the array.
     */
    public ByteScalar(byte[] arr, int idx) {
        super();
        this.data = arr;
        this.offset = idx;
        this.flat = (idx == 0 && arr.length == 1);
        checkSanity();
    }

    /**
     * Wrap a slot in a Java array into a new ByteScalar object.
     */
    public static ByteScalar wrap(byte[] arr, int idx) {
        return new ByteScalar(arr, idx);
    }

    /**
     * Create a ByteScalar with an initial value.
     *
     * @param value - The initial value of the scalar.
     */
    public ByteScalar(byte value) {
        super();
        data = new byte[]{value};
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

    public final byte get() {
        return data[offset];
    }

    public final void set(byte value) {
        data[offset] = value;
    }

    @Override
    public final void fill(byte value) {
        data[offset] = value;
    }

    @Override
    public final void fill(ByteGenerator generator) {
        data[offset] = generator.nextByte();
    }

    @Override
    public final void increment(byte value) {
        data[offset] += value;
    }

    @Override
    public final void decrement(byte value) {
        data[offset] -= value;
    }

    @Override
    public final void scale(byte value) {
        data[offset] *= value;
    }

    @Override
    public final void map(ByteFunction function) {
        data[offset] = function.apply(data[offset]);
    }

    @Override
    public final void scan(ByteScanner scanner)  {
        scanner.initialize(data[offset]);
    }

    @Override
    public final boolean isFlat() {
        return flat;
    }

    @Override
    public final byte[] flatten() {
        return flatten(false);
    }

    @Override
    public final byte[] flatten(boolean forceCopy) {
        if (! forceCopy && flat) {
            return data;
        }
        return new byte[]{data[offset]};
    }

    @Override
    public final byte[] getData() {
        return (flat ? data : null);
    }

    @Override
    public int min() {
        return (int)(data[offset] & 0xFF);
    }

    @Override
    public int max() {
        return (int)(data[offset] & 0xFF);
    }

    @Override
    public int[] getMinAndMax() {
        int[] result = new int[2];
        getMinAndMax(result);
        return result;
    }

    @Override
    public void getMinAndMax(int[] mm) {
        int value = (int)(data[offset] & 0xFF);
        mm[0] = value;
        mm[1] = value;
    }

    @Override
    public int sum() {
        return (int)(data[offset] & 0xFF);
    }

    @Override
    public double average() {
        return (int)(data[offset] & 0xFF);
    }

    @Override
    public final ByteArray toByte() {
        return this;
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
        return new DoubleScalar((double)data[offset]);
    }

    @Override
    public final Byte1D as1D() {
        if (offset == 0) {
            return new FlatByte1D(data, 1);
        } else {
            return new StriddenByte1D(data, offset, 0, 1);
        }
    }

    @Override
    public final void assign(ShapedArray src) {
        if (! shape.equals(src.getShape())) {
            throw new NonConformableArrayException();
        }
        switch (src.getType()) {
        case Traits.BYTE:
            data[offset] = (byte)((ByteScalar)src).get();
            break;
        case Traits.SHORT:
            data[offset] = (byte)((ShortScalar)src).get();
            break;
        case Traits.INT:
            data[offset] = (byte)((IntScalar)src).get();
            break;
        case Traits.LONG:
            data[offset] = (byte)((LongScalar)src).get();
            break;
        case Traits.FLOAT:
            data[offset] = (byte)((FloatScalar)src).get();
            break;
        case Traits.DOUBLE:
            data[offset] = (byte)((DoubleScalar)src).get();
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
        data[offset] = (byte)src.get(0);
    }

    @Override
    public final ByteScalar copy() {
        return new ByteScalar(data[offset]);
    }
}
