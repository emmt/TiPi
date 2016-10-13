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
import mitiv.linalg.shaped.ShapedVector;

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
     * Check whether a shaped array is stored as a flat Java array.
     *
     * @return True if the results of {@code this.flatten(false)} and
     *         {@code this.flatten()} are guaranteed to yield a direct
     *         reference (not a copy) to the contents of the array.
     */
    public abstract boolean isFlat();

    /**
     * Flatten the elements of a shaped array in a simple generic array.
     * <p>
     * The contents of a shaped array can be stored in many different forms.
     * This storage details are hidden to the end-user in favor of a unified
     * and comprehensive interface.  This method returns the contents of a
     * shaped array as a simple <i>flat</i> array, <i>i.e.</i> successive
     * elements are contiguous and the first element has {@code 0}-offset.
     * If the shaped array is multi-dimensional, the storage of the returned
     * result is column-major order.
     * </p>
     * @param forceCopy - Set true to force a copy of the internal data even
     *                    though it can already be in a flat form.  Otherwise
     *                    and if the shaped array is in flat form, see {@link
     *                    #isFlat()}, the data storage of the array is directly
     *                    returned (not a copy).
     *
     * @return An object which can be recast into a {@code type[]}
     *         array with {@code type} the generic Java type corresponding
     *         to the type of the elements of the shaped array: {@code byte},
     *         {@code short}, {@code int}, {@code long}, {@code float} or
     *         {@code double}.
     */
    public abstract Object flatten(boolean forceCopy);

    /**
     * Flatten the elements of a shaped array in a simple generic array.
     * <p>
     * This method behaves as if argument {@code forceCopy} was set to false
     * in {@link #flatten(boolean)}.  Depending on the storage layout, the
     * returned array may or may not share the same storage as the
     * ${className} array.  Call {@code flatten(true)} to make sure that the
     * two storage areas are independent.
     * </p>
     * @return An object (see {@link #flatten(boolean)} for more explanations).
     */
    public abstract Object flatten();

    /**
     * Get a direct access to the elements of a shaped array.
     * <p>
     * Calling this method should be equivalent to:
     * <pre>
     * (this.isFlat() ? this.flatten() : null)
     * </pre>
     * </p>
     *
     * @return An object which can be {@code null} if direct access is not
     *         possible (or allowed).  If non-{@code null}, the returned value
     *         can be recast into a {@code type[]} array with {@code type} the
     *         generic Java type corresponding to the type of the elements of
     *         the shaped array: {@code byte}, {@code short}, {@code int},
     *         {@code long}, {@code float} or {@code double}.
     *
     * @see {@link #flatten()} and {@link #isFlat()}.
     */
    public abstract Object getData();

    /**
     * Convert array elements to type {@code byte}.
     * @return A {@link ByteArray} object which may be the object itself
     *         if it is already a ByteArray.
     */
    public abstract ByteArray toByte();

    /**
     * Convert array elements to type {@code short}.
     * @return A {@link ShortArray} object which may be the object itself
     *         if it is already a ShortArray.
     */
    public abstract ShortArray toShort();

    /**
     * Convert array elements to type {@code int}.
     * @return A {@link IntArray} object which may be the object itself
     *         if it is already an IntArray.
     */
    public abstract IntArray toInt();

    /**
     * Convert array elements to type {@code long}.
     * @return A {@link LongArray} object which may be the object itself
     *         if it is already a LongArray.
     */
    public abstract LongArray toLong();

    /**
     * Convert array elements to type {@code float}.
     * @return A {@link FloatArray} object which may be the object itself
     *         if it is already a FloatArray.
     */
    public abstract FloatArray toFloat();

    /**
     * Convert array elements to type {@code double}.
     * @return A {@link DoubleArray} object which may be the object itself
     *         if it is already a DoubleArray.
     */
    public abstract DoubleArray toDouble();

    /**
     * Create a new array with same element type and shape.
     * <p>
     * This method yields a new shaped array which has the same element type
     * and shape as the object but whose contents is not initialized.
     * </p>
     * @return A new shaped array.
     */
    public abstract ShapedArray create();

    /**
     * Copy the contents of the object as a new array.
     * <p>
     * This method yields a new shaped array which has the same shape, type
     * and values as the object but whose contents is independent from that
     * of the object.  If the object is a <i>view</i>, then this method yields
     * a compact array in a <i>flat</i> form.
     * </p>
     * @return A flat shaped array.
     */
    public abstract ShapedArray copy();

    /**
     * Assign the values of the object from those of another shaped array.
     * <p>
     * The shape of the source and of the destination must match, type
     * conversion (to the type of the elements of the destination) is
     * automatically done if needed.
     * </p>
     * @param src - The source object.
     */
    public abstract void assign(ShapedArray src);

    /**
     * Assign the values of the object from those of a shaped vector.
     * <p>
     * This operation may be slow.
     * </p>
     * @param src - The source object.
     * @see {@link #assign(ShapedArray)} for a discussion of the rules
     * that apply.
     */
    public abstract void assign(ShapedVector src);

    /**
     * Get a view of the object as a 1D array.
     *<p>
     * The result is a 1D <i>view</i> of its parents, this means that
     * they share the same contents.
     * </p>
     * @return A 1D view of the object.
     */
    public abstract Array1D as1D();

    /**
     * Perform some sanity tests.
     *
     * <p>
     * For performance reasons, not all errors are checked by TiPi code.
     * This means that arguments may be simply trusted for being correct.
     * This method can be used for debugging and tracking incorrect
     * parameters/arguments.  It throws runtime exception(s) if some
     * errors or inconsistencies are discovered.
     * </p>
     */
    public abstract void checkSanity();

}
