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

package mitiv.base.mapping;

/**
 * A ByteScanner is designed for scanning a collection of values (of type
 * byte).
 *
 * <p>
 * A scanner has two mandatory methods: the {@link #initialize} method is
 * called with the first value of the collection to (re)start the scan and the
 * {@link #update} method is called repeatedly for all other values of the
 * collection.
 * </p>
 * <p>
 * A typical use of a scanner is to compute the moments of the values of a
 * collection or to find the minimal and maximal values of a collection.
 * </p>
 *
 * @author Éric Thiébaut.
 */
public interface ByteScanner {
    /**
     * Initialize the scanner with the first value of the collection.
     * @param arg - The first value of the collection.
     */
    public abstract void initialize(byte arg);

    /**
     * Update the scanner with an additional value of the collection.
     * @param arg  - An additional value of the collection.
     */
    public abstract void update(byte arg);
}

