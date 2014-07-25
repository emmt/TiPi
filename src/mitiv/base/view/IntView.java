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

import mitiv.base.mapping.IntegerFunction;
import mitiv.base.mapping.IntegerScanner;
import mitiv.random.IntegerGenerator;

/**
 * Define the global operations which can be applied to a view.
 * @author Éric Thiébaut.
 *
 */
public interface IntView {

    /**
     * Set all the values of the view.
     * @param value - The value to set.
     */
    public abstract void set(int value);

    /**
     * Set the values of the view with a generator.
     * @param generator - The generator to use.
     */
    public abstract void set(IntegerGenerator generator);

    /**
     * Increment all the values of the view.
     * @param value - The increment.
     */
    public abstract void incr(int value);


    /**
     * Decrement all the values of the view.
     * @param value - The decrement.
     */
    public abstract void decr(int value);

    /**
     * Multiply all the values of the view.
     * @param value - The multiplier.
     */
    public abstract void mult(int value);

    /**
     * Map all the values of the view by a function.
     * @param func - The function to apply.
     */
    public abstract void map(IntegerFunction func);

    /**
     * Scan the values of the view.
     * @param scanner - The scanner to use.
     */
    public abstract void scan(IntegerScanner scanner);

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
