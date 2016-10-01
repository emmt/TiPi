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

import mitiv.array.impl.FlatShort1D;
import mitiv.array.impl.StriddenShort1D;
import mitiv.base.Traits;
import mitiv.base.mapping.ShortFunction;
import mitiv.base.mapping.ShortScanner;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.NonConformableArrayException;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.random.ShortGenerator;

/**
 * Selected implementation of 0-dimensional short arrays (i.e. scalars).
 * <p>
 * This specific kind of scalar values can be used to <i>view</i> a single
 * element of another multi-dimensional array.
 * </p>
 * @author Éric Thiébaut.
 */
public class ShortScalar extends Scalar implements ShortArray {
    final short[] data;
    final int offset;

    /**
     * Create a new ShortScalar object.
     */
    public ShortScalar() {
        super();
        this.data = new short[1];
        this.offset = 0;
    }

    /**
     * Create a new ShortScalar object of same type.
     */
    @Override
    public ShortScalar create() {
        return new ShortScalar();
    }

    /**
     * Create a ShortScalar pointing a slot in a Java array.
     *
     * @param arr - The array.
     * @param idx - The index of the scalar element in the array.
     */
    public ShortScalar(short[] arr, int idx) {
        super();
        this.data = arr;
        this.offset = idx;
        checkSanity();
    }

    /**
     * Wrap a slot in a Java array into a new ShortScalar object.
     */
    public static ShortScalar wrap(short[] arr, int idx) {
        return new ShortScalar(arr, idx);
    }

    /**
     * Create a ShortScalar with an initial value.
     *
     * @param value - The initial value of the scalar.
     */
    public ShortScalar(short value) {
        super();
        data = new short[]{value};
        offset = 0;
    }

    @Override
    public final void checkSanity() {
        if (offset < 0 || offset >= data.length) {
            throw new IndexOutOfBoundsException("Scalar offset is out of bounds.");
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

    public final short get() {
        return data[offset];
    }

    public final void set(short value) {
        data[offset] = value;
    }

    @Override
    public final void fill(short value) {
        data[offset] = value;
    }

    @Override
    public final void fill(ShortGenerator generator) {
        data[offset] = generator.nextShort();
    }

    @Override
    public final void increment(short value) {
        data[offset] += value;
    }

    @Override
    public final void decrement(short value) {
        data[offset] -= value;
    }

    @Override
    public final void scale(short value) {
        data[offset] *= value;
    }

    @Override
    public final void map(ShortFunction function) {
        data[offset] = function.apply(data[offset]);
    }

    @Override
    public final void scan(ShortScanner scanner)  {
        scanner.initialize(data[offset]);
    }

    @Override
    public final boolean isFlat() {
        return true;
    }

    @Override
    public final short[] flatten() {
        return flatten(false);
    }

    @Override
    public final short[] flatten(boolean forceCopy) {
        if (! forceCopy && offset == 0 && data.length == 1) {
            return data;
        }
        return new short[]{data[offset]};
    }

    @Override
    public short min() {
        return data[offset];
    }

    @Override
    public short max() {
        return data[offset];
    }

    @Override
    public short[] getMinAndMax() {
        short[] result = new short[2];
        getMinAndMax(result);
        return result;
    }

    @Override
    public void getMinAndMax(short[] mm) {
        short value = data[offset];
        mm[0] = value;
        mm[1] = value;
    }

    @Override
    public int sum() {
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
        return this;
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
    public final Short1D as1D() {
        if (offset == 0) {
            return new FlatShort1D(data, 1);
        } else {
            return new StriddenShort1D(data, offset, 0, 1);
        }
    }

    @Override
    public final void assign(ShapedArray src) {
        if (! shape.equals(src.getShape())) {
            throw new NonConformableArrayException();
        }
        switch (src.getType()) {
        case Traits.BYTE:
            data[offset] = (short)((ByteScalar)src).get();
            break;
        case Traits.SHORT:
            data[offset] = (short)((ShortScalar)src).get();
            break;
        case Traits.INT:
            data[offset] = (short)((IntScalar)src).get();
            break;
        case Traits.LONG:
            data[offset] = (short)((LongScalar)src).get();
            break;
        case Traits.FLOAT:
            data[offset] = (short)((FloatScalar)src).get();
            break;
        case Traits.DOUBLE:
            data[offset] = (short)((DoubleScalar)src).get();
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
        data[offset] = (short)src.get(0);
    }

    @Override
    public final ShortScalar copy() {
        return new ShortScalar(data[offset]);
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
