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
import mitiv.base.mapping.ShortFunction;
import mitiv.base.mapping.ShortScanner;
import mitiv.random.ShortGenerator;

/**
 * Define the global operations which can be applied to an array with a
 * specific type.
 *
 * @author Éric Thiébaut.
 */
public interface ShortArray extends ShapedArray {

    static public final int type = Traits.SHORT;

    /**
     * Set all the values of the array of short's.
     * @param value - The value to set.
     */
    public abstract void fill(short value);

    /**
     * Set the values of the array of short's with a generator.
     * @param generator - The generator to use.
     */
    public abstract void fill(ShortGenerator generator);

    /**
     * Increment all the values of the array of short's.
     * @param value - The increment.
     */
    public abstract void increment(short value);


    /**
     * Decrement all the values of the array of short's.
     * @param value - The decrement.
     */
    public abstract void decrement(short value);

    /**
     * Multiply all the values of the array of short's.
     * @param value - The multiplier.
     */
    public abstract void scale(short value);

    /**
     * Map all the values of the array of short's by a function.
     * @param func - The function to apply.
     */
    public abstract void map(ShortFunction func);

    /**
     * Scan the values of the array of short's.
     * @param scanner - The scanner to use.
     */
    public abstract void scan(ShortScanner scanner);

    /**
     * Flatten the shaped array in a simple generic array.
     * <p>
     * The contents of a (multi-dimensional) ShortArray can be stored in
     * many different forms.  This storage details are hidden to the end-user
     * in favor of a unified and comprehensive interface.  This method returns
     * the contents of the ShortArray object as a simple <i>flat</i> array,
     * <i>i.e.</i> successive elements are contiguous and the first element
     * has {@code 0}-offset.  If the ShortArray object is multi-dimensional,
     * the storage of the returned result is column-major order.
     * </p>
     * @param forceCopy - Set true to force a copy of the internal data
     *                    even though it can already be in a flat form.
     *                    Otherwise and if the shaped array is in flat form,
     *                    see {@link #isFlat()}, the data storage of the
     *                    array is directly returned (not a copy).
     *
     * @return A simple generic {@code short[]} array with the contents of
     *         the ShortArray array.
     *
     * @see {@link mitiv.array.ShapedArray#flatten(boolean)} for a general
     *      description, {@link mitiv.base.Shaped#COLUMN_MAJOR} for
     *      explanations about storage order.
     */
    public abstract short[] flatten(boolean forceCopy);

    /**
     * Flatten the shaped array in a simple generic array avoiding
     * copies if possible.
     * <p>
     * This method behaves as if argument {@code forceCopy} was set to false
     * in {@link #flatten(boolean)}.  Depending on the storage layout, the
     * returned array may or may not share the same storage as the
     * ShortArray array.  Call {@code flatten(true)} to make sure that the
     * two storage areas are independent.
     * </p>
     * @return A simple generic {@code short[]} array with the contents of
     *         the ShortArray array.
     *
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public abstract short[] flatten();

    @Override
    public abstract ShortArray copy();

    /**
     * Get the minimal value of all the elements.
     */
    public abstract short min();

    /**
     * Get the maximal value of all the elements.
     */
    public abstract short max();

    /**
     * Get the minimal and maximal values of all the elements.
     */
    public abstract short[] getMinAndMax();

    /**
     * Get the minimal and maximal values of all the elements.
     */
    public abstract void getMinAndMax(short[] mm);

    /**
     * Get the sum of values of all elements.
     */
    public abstract int sum();

    /**
     * Get the average value of all elements.
     */
    public abstract double average();
}
