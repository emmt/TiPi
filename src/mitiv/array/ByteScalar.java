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

import mitiv.base.mapping.ByteFunction;
import mitiv.base.mapping.ByteScanner;
import mitiv.random.ByteGenerator;

/**
 * Selected implementation of 0-dimensional arrays of byte's.
 *
 * This specific kind of arrays/views are accessed via indirection tables (one
 * for each dimension).
 *
 * @author Éric Thiébaut.
 */
public class ByteScalar extends Scalar implements ByteArray {
    final byte[] data;
    final int offset;

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
        checkSanity();
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

    public final byte get() {
        return data[offset];
    }

    public final void set(byte value) {
        data[offset] = value;
    }

    @Override
    public void fill(byte value) {
        data[offset] = value;
    }

    @Override
    public void fill(ByteGenerator generator) {
        data[offset] = generator.nextByte();
    }

    @Override
    public void increment(byte value) {
        data[offset] += value;
    }

    @Override
    public void decrement(byte value) {
        data[offset] -= value;
    }

    @Override
    public void scale(byte value) {
        data[offset] *= value;
    }

    @Override
    public void map(ByteFunction function) {
        data[offset] = function.apply(data[offset]);
    }

    @Override
    public void scan(ByteScanner scanner)  {
        scanner.initialize(data[offset]);
    }

    @Override
    public byte[] flatten() {
        return flatten(false);
    }

    @Override
    public byte[] flatten(boolean forceCopy) {
        if (! forceCopy && offset == 0 && data.length == number) {
            return data;
        }
        byte[] out = new byte[number];
        out[0] = data[offset];
        return out;
    }

    @Override
    public ByteArray toByte() {
        return this;
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
        return new DoubleScalar((double)data[offset]);
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public Byte1D as1D() {
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
