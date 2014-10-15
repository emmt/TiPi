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
import mitiv.base.mapping.LongFunction;
import mitiv.base.mapping.LongScanner;
import mitiv.random.LongGenerator;

/**
 * Define the global operations which can be applied to an array with a
 * specific type.
 *
 * @author Éric Thiébaut.
 */
public interface LongArray extends ShapedArray {

    static public final int type = LONG;

    /**
     * Set all the values of the array of long's.
     * @param value - The value to set.
     */
    public abstract void fill(long value);

    /**
     * Set the values of the array of long's with a generator.
     * @param generator - The generator to use.
     */
    public abstract void fill(LongGenerator generator);

    /**
     * Increment all the values of the array of long's.
     * @param value - The increment.
     */
    public abstract void increment(long value);


    /**
     * Decrement all the values of the array of long's.
     * @param value - The decrement.
     */
    public abstract void decrement(long value);

    /**
     * Multiply all the values of the array of long's.
     * @param value - The multiplier.
     */
    public abstract void scale(long value);

    /**
     * Map all the values of the array of long's by a function.
     * @param func - The function to apply.
     */
    public abstract void map(LongFunction func);

    /**
     * Scan the values of the array of long's.
     * @param scanner - The scanner to use.
     */
    public abstract void scan(LongScanner scanner);

    /**
     * Flatten the array of long's in a simple array.
     * <p>
     * The contents of a (multi-dimensional) LongArray can be stored in
     * many different forms.  This storage details are hidden to the end-user
     * in favor of a unified and comprehensive interface.  This method returns
     * the contents of the LongArray object as a simple <i>flat</i> array,
     * <i>i.e.</i> successive elements are contiguous and the first element
     * has {@code 0}-offset.  If the LongArray object is multi-dimensional,
     * the storage of the returned result is column-major order.
     * </p>
     * @param forceCopy - Set true to force a copy of the internal data
     *                    even though it can already be in a flat form.
     * @return A simple array of long's with the contents of
     *         the LongArray.
     */
    public abstract long[] flatten(boolean forceCopy);

    /**
     * Flatten the contents of long's in a simple array.
     * <p>
     * The contents of a (multi-dimensional) LongArray can be stored in
     * many different forms.  This storage details are hidden to the end-user
     * in favor of a unified and comprehensive interface.  This method returns
     * the contents of the LongArray object as a simple <i>flat</i> array,
     * <i>i.e.</i> successive elements are contiguous and the first element
     * has {@code 0}-offset.  If the LongArray object is multi-dimensional,
     * the storage of the returned result is column-major order.
     * </p><p>
     * Depending on the storage layout, the returned array may or may not
     * share the same storage as the LongArray array.  Call {@code
     * flatten(true)} to make sure that the two storage areas are independent.
     * </p>
     * @return A simple array of longs with the contents of
     *         the LongArray array.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public abstract long[] flatten();

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
