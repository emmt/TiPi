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

import mitiv.array.impl.FlatInt1D;
import mitiv.array.impl.StriddenInt1D;
import mitiv.base.Traits;
import mitiv.base.mapping.IntFunction;
import mitiv.base.mapping.IntScanner;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.NonConformableArrayException;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.random.IntGenerator;

/**
 * Selected implementation of 0-dimensional int arrays (i.e. scalars).
 * <p>
 * This specific kind of scalar values can be used to <i>view</i> a single
 * element of another multi-dimensional array.
 * </p>
 * @author Éric Thiébaut.
 */
public class IntScalar extends Scalar implements IntArray {
    final int[] data;
    final int offset;
    final boolean flat;

    /**
     * Create a new IntScalar object.
     */
    public IntScalar() {
        super();
        this.data = new int[1];
        this.offset = 0;
        this.flat = true;
    }

    /**
     * Create a new IntScalar object of same type.
     */
    @Override
    public IntScalar create() {
        return new IntScalar();
    }

    /**
     * Create a IntScalar pointing a slot in a Java array.
     *
     * @param arr - The array.
     * @param idx - The index of the scalar element in the array.
     */
    public IntScalar(int[] arr, int idx) {
        super();
        this.data = arr;
        this.offset = idx;
        this.flat = (idx == 0 && arr.length == 1);
        checkSanity();
    }

    /**
     * Wrap a slot in a Java array into a new IntScalar object.
     */
    public static IntScalar wrap(int[] arr, int idx) {
        return new IntScalar(arr, idx);
    }

    /**
     * Create a IntScalar with an initial value.
     *
     * @param value - The initial value of the scalar.
     */
    public IntScalar(int value) {
        super();
        data = new int[]{value};
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

    public final int get() {
        return data[offset];
    }

    public final void set(int value) {
        data[offset] = value;
    }

    @Override
    public final void fill(int value) {
        data[offset] = value;
    }

    @Override
    public final void fill(IntGenerator generator) {
        data[offset] = generator.nextInt();
    }

    @Override
    public final void increment(int value) {
        data[offset] += value;
    }

    @Override
    public final void decrement(int value) {
        data[offset] -= value;
    }

    @Override
    public final void scale(int value) {
        data[offset] *= value;
    }

    @Override
    public final void map(IntFunction function) {
        data[offset] = function.apply(data[offset]);
    }

    @Override
    public final void scan(IntScanner scanner)  {
        scanner.initialize(data[offset]);
    }

    @Override
    public final boolean isFlat() {
        return flat;
    }

    @Override
    public final int[] flatten() {
        return flatten(false);
    }

    @Override
    public final int[] flatten(boolean forceCopy) {
        if (! forceCopy && flat) {
            return data;
        }
        return new int[]{data[offset]};
    }

    @Override
    public final int[] getData() {
        return (flat ? data : null);
    }

    @Override
    public int min() {
        return data[offset];
    }

    @Override
    public int max() {
        return data[offset];
    }

    @Override
    public int[] getMinAndMax() {
        int[] result = new int[2];
        getMinAndMax(result);
        return result;
    }

    @Override
    public void getMinAndMax(int[] mm) {
        int value = data[offset];
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
        return new ShortScalar((short)data[offset]);
    }

    @Override
    public final IntArray toInt() {
        return this;
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
    public final Int1D as1D() {
        if (offset == 0) {
            return new FlatInt1D(data, 1);
        } else {
            return new StriddenInt1D(data, offset, 0, 1);
        }
    }

    @Override
    public final void assign(ShapedArray src) {
        if (! shape.equals(src.getShape())) {
            throw new NonConformableArrayException();
        }
        switch (src.getType()) {
        case Traits.BYTE:
            data[offset] = (int)((ByteScalar)src).get();
            break;
        case Traits.SHORT:
            data[offset] = (int)((ShortScalar)src).get();
            break;
        case Traits.INT:
            data[offset] = (int)((IntScalar)src).get();
            break;
        case Traits.LONG:
            data[offset] = (int)((LongScalar)src).get();
            break;
        case Traits.FLOAT:
            data[offset] = (int)((FloatScalar)src).get();
            break;
        case Traits.DOUBLE:
            data[offset] = (int)((DoubleScalar)src).get();
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
        data[offset] = (int)src.get(0);
    }

    @Override
    public final IntScalar copy() {
        return new IntScalar(data[offset]);
    }
}
