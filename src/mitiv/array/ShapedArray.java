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

import mitiv.base.Shaped;
import mitiv.base.Typed;


/**
 * A ShapedArray is a shaped object with a primitive type.
 * <p>
 * A ShapedArray stores rectangular multi-dimensional arrays of elements of
 * the same data type.  Compared to a {@link #ShapedVector}, the elements
 * of a ShapedArray reside in conventional memory and may be stored in arbitrary
 * order and in a non-contiguous way.
 * 
 * @author Éric Thiébaut.
 */
public interface ShapedArray extends Shaped, Typed {
    /**
     * Convert array elements to type {@code byte}.
     * @return A {@link ByteArray} object which may be the object itself
     *         if it is already a ByteArray.
     */
    public ByteArray toByte();

    /**
     * Convert array elements to type {@code short}.
     * @return A {@link ShortArray} object which may be the object itself
     *         if it is already a ShortArray.
     */
    public ShortArray toShort();

    /**
     * Convert array elements to type {@code int}.
     * @return A {@link IntArray} object which may be the object itself
     *         if it is already an IntArray.
     */
    public IntArray toInt();

    /**
     * Convert array elements to type {@code long}.
     * @return A {@link LongArray} object which may be the object itself
     *         if it is already a LongArray.
     */
    public LongArray toLong();

    /**
     * Convert array elements to type {@code float}.
     * @return A {@link FloatArray} object which may be the object itself
     *         if it is already a FloatArray.
     */
    public FloatArray toFloat();

    /**
     * Convert array elements to type {@code double}.
     * @return A {@link DoubleArray} object which may be the object itself
     *         if it is already a DoubleArray.
     */
    public DoubleArray toDouble();
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
