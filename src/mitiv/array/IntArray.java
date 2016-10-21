// *WARNING* This file has been automatically generated by TPP do not edit directly.
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
import mitiv.base.Traits;
import mitiv.base.mapping.IntFunction;
import mitiv.base.mapping.IntScanner;
import mitiv.random.IntGenerator;

/**
 * Define the global operations which can be applied to an array with a
 * specific type.
 *
 * @author Éric Thiébaut.
 */
public interface IntArray extends ShapedArray {

    static public final int type = Traits.INT;

    /**
     * Set all the values of the array of int's.
     * @param value   The value to set.
     */
    public abstract void fill(int value);

    /**
     * Set the values of the array of int's with a generator.
     * @param generator   The generator to use.
     */
    public abstract void fill(IntGenerator generator);

    /**
     * Increment all the values of the array of int's.
     * @param value   The increment.
     */
    public abstract void increment(int value);


    /**
     * Decrement all the values of the array of int's.
     * @param value   The decrement.
     */
    public abstract void decrement(int value);

    /**
     * Multiply all the values of the array of int's.
     * @param value   The multiplier.
     */
    public abstract void scale(int value);

    /**
     * Map all the values of the array of int's by a function.
     * @param func   The function to apply.
     */
    public abstract void map(IntFunction func);

    /**
     * Scan the values of the array of int's.
     * @param scanner   The scanner to use.
     */
    public abstract void scan(IntScanner scanner);

    /**
     * Flatten the shaped array in a simple generic array.
     *
     * <p>The contents of a (multi-dimensional) IntArray can be stored in
     * many different forms.  This storage details are hidden to the end-user
     * in favor of a unified and comprehensive interface.  This method returns
     * the contents of the IntArray object as a simple <i>flat</i> array,
     * <i>i.e.</i> successive elements are contiguous and the first element has
     * {@code 0}-offset.  If the IntArray object is multi-dimensional, the
     * storage of the returned result is column-major order.  </p>
     *
     * @param forceCopy
     *        Set true to force a copy of the internal data even though it can
     *        already be in a flat form.  Otherwise and if the shaped array is
     *        in flat form, see {@link #isFlat()}, the data storage of the
     *        array is directly returned (not a copy).
     *
     * @return A simple generic {@code int[]} array with the contents of
     *         the IntArray array.
     *
     * @see {@link mitiv.array.ShapedArray#flatten(boolean)} for a general
     *      description, {@link mitiv.base.Shaped#COLUMN_MAJOR} for
     *      explanations about storage order.
     */
    @Override
    public abstract int[] flatten(boolean forceCopy);

    /**
     * Flatten the shaped array in a simple generic array avoiding
     * copies if possible.
     *
     * <p>This method behaves as if argument {@code forceCopy} was set to
     * false in {@link #flatten(boolean)}.  Depending on the storage layout,
     * the returned array may or may not share the same storage as the
     * IntArray array.  Call {@code flatten(true)} to make sure that the
     * two storage areas are independent.</p>
     *
     * @return A simple generic {@code int[]} array with the contents of
     *         the IntArray array.
     *
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    @Override
    public abstract int[] flatten();

    @Override
    public abstract int[] getData();

    @Override
    public abstract IntArray copy();

    /**
     * Get the minimal value of all the elements.
     *
     * @return The minimum value of {@code this}.
     */
    public abstract int min();

    /**
     * Get the maximal value of all the elements.
     *
     * @return The maximum value of {@code this}.
     */
    public abstract int max();

    /**
     * Get the minimal and maximal values of all the elements.
     *
     * @return A 2-element array with the minimum and maximum values of {@code
     * this}.
     */
    public abstract int[] getMinAndMax();

    /**
     * Get the minimal and maximal values of all the elements.
     *
     * @param mm    A 2-element array to store the minimum and maximum values
     *              of{@code this}.
     */
    public abstract void getMinAndMax(int[] mm);

    /**
     * Get the sum of values of all elements.
     *
     * @return The sum of the values of {@code this}.
     */
    public abstract int sum();

    /**
     * Get the average value of all elements.
     *
     * @return The average of the values of {@code this}.
     */
    public abstract double average();
}
