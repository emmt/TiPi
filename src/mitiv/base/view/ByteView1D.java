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

package mitiv.base.view;

import mitiv.base.mapping.ByteFunction;
import mitiv.base.mapping.ByteScanner;
import mitiv.random.ByteGenerator;

/**
 * This class implements 1D views of arrays of byte's.
 * 
 * @see {@link View} for a general introduction to the concepts of <i>views</i>.
 * 
 * @author Éric Thiébaut.
 *
 */
public class ByteView1D extends View1D implements ByteView {
    private final byte[] data;

    /**
     * Create a 1D view of an array of byte's with zero offset, contiguous
     * elements and {@link #COLUMN_MAJOR} order.
     * @param data - The array to wrap in the view.
     */
    public ByteView1D(byte[] data) {
        super(data.length, data.length, 0, 1);
        this.data = data;
    }

    /**
     * Create a 1D view of an array of byte's with zero offset, contiguous
     * elements and {@link #COLUMN_MAJOR} order.
     * @param data - The array to wrap in the view.
     * @param n1   - The 1st dimension of the view.
     */
    public ByteView1D(byte[] data, int n1) {
        super(data.length, n1, 0, 1);
        this.data = data;
    }

    /**
     * Create a 1D view of an array of byte's.
     * @param data - The array to wrap in the view.
     * @param n1   - The 1st dimension of the view.
     * @param s0   - The offset of element (0,0,0).
     * @param s1   - The stride along the 1st dimension.
     */
    public ByteView1D(byte[] data, int n1, int s0, int s1) {
        super(data.length, n1, s0, s1);
        this.data = data;
    }

    /**
     * Query the array wrapped by the view.
     * @return The array wrapped by the view.
     */
    public final byte[] getData() {
        return data;
    }

    /**
     * Query the value stored at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @return The value stored at position {@code (i1,i2)} of the view.
     */
    public final byte get(int i1) {
        return data[index(i1)];
    }

    /**
     * Set the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param value - The value to store at position {@code (i1,i2,i3)} of the view.
     */
    public final void set(int i1, byte value) {
        data[index(i1)] = value;
    }

    /**
     * Set all the values of the view.
     * @param value - The value to set.
     */
    @Override
    public final void set(byte value) {
        for (int i1 = 0; i1 < n1; ++i1) {
            data[index(i1)] = value;
        }
    }

    /**
     * Set the values of the view with a generator.
     * @param generator - The generator to use.
     */
    @Override
    public final void set(ByteGenerator generator) {
        for (int i1 = 0; i1 < n1; ++i1) {
            data[index(i1)] = generator.nextByte();
        }
    }

    /**
     * Increment the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param value - The value to add to the value stored at position
     *                {@code (i1,i2)} of the view.
     */
    public final void incr(int i1, byte value) {
        data[index(i1)] += value;
    }

    /**
     * Increment all the values of the view.
     * @param value - The increment.
     */
    @Override
    public final void incr(byte value) {
        for (int i1 = 0; i1 < n1; ++i1) {
            data[index(i1)] += value;
        }
    }

    /**
     * Decrement the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param value - The value to subtract to the value stored at position
     *                {@code (i1)} of the view.
     */
    public final void decr(int i1, byte value) {
        data[index(i1)] -= value;
    }

    /**
     * Decrement all the values of the view.
     * @param value - The decrement.
     */
    @Override
    public final void decr(byte value) {
        for (int i1 = 0; i1 < n1; ++i1) {
            data[index(i1)] -= value;
        }
    }

    /**
     * Multiply the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param value - The value by which to scale the value at position
     *                {@code (i1)} of the view.
     */
    public final void mult(int i1, byte value) {
        data[index(i1)] *= value;
    }

    /**
     * Multiply all the values of the view.
     * @param value - The multiplier.
     */
    @Override
    public final void mult(byte value) {
        for (int i1 = 0; i1 < n1; ++i1) {
            data[index(i1)] *= value;
        }
    }

    /**
     * Map the value at a given position of the view by a function.
     * @param i1 - The index along the 1st dimension.
     * @param func - The function to apply.
     */
    public final void map(int i1, ByteFunction func) {
        int k = index(i1);
        data[k] = func.apply(data[k]);
    }

    /**
     * Map all the values of the view by a function.
     * @param func - The function to apply.
     */
    @Override
    public final void map(ByteFunction func) {
        for (int i1 = 0; i1 < n1; ++i1) {
            int k = index(i1);
            data[k] = func.apply(data[k]);
        }
    }

    /**
     * Scan the values of the view.
     * @param scanner - The scanner to use.
     */
    @Override
    public final void scan(ByteScanner scanner) {
        scanner.initialize(get(0));
        for (int i1 = 1; i1 < n1; ++i1) {
            scanner.update(get(i1));
        }
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
