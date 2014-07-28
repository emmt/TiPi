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


import mitiv.base.mapping.IntegerFunction;
import mitiv.base.mapping.IntegerScanner;
import mitiv.random.IntegerGenerator;

/**
 * Define the global operations which can be applied to an array with a
 * specific type.
 *
 * @author Éric Thiébaut.
 */
public interface IntegerArray extends ShapedArray {

    static public final int type = INT;

    /**
     * Set all the values of the array of int's.
     * @param value - The value to set.
     */
    public abstract void set(int value);

    /**
     * Set the values of the array of int's with a generator.
     * @param generator - The generator to use.
     */
    public abstract void set(IntegerGenerator generator);

    /**
     * Increment all the values of the array of int's.
     * @param value - The increment.
     */
    public abstract void incr(int value);


    /**
     * Decrement all the values of the array of int's.
     * @param value - The decrement.
     */
    public abstract void decr(int value);

    /**
     * Multiply all the values of the array of int's.
     * @param value - The multiplier.
     */
    public abstract void mult(int value);

    /**
     * Map all the values of the array of int's by a function.
     * @param func - The function to apply.
     */
    public abstract void map(IntegerFunction func);

    /**
     * Scan the values of the array of int's.
     * @param scanner - The scanner to use.
     */
    public abstract void scan(IntegerScanner scanner);

    /**
     * Flatten the array of int's in a simple array.
     * <p>
     * The contents of a (multi-dimensional) IntegerArray can be stored in many
     * different forms.  This storage details are hidden to the end-user in
     * favor of a unified and comprehensive interface.  This method returns
     * the contents of the IntegerArray object as a simple flat array.  If the
     * IntegerArray object is multi-dimensional, the storage of the returned
     * result is column-major order.
     * @param forceCopy - Set true to force a copy of the internal data
     *                    even though it can already be in a flat form.
     * @return A simple array of int's with the contents of
     *         the IntegerArray.
     */
    public abstract int[] flatten(boolean forceCopy);

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
