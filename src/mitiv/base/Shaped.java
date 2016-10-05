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

package mitiv.base;

/**
 * A Shaped object stores rectangular multi-dimensional arrays of elements.
 *
 * <p>
 * Here "rectangular" means that the lengths of each dimension are the same everywhere.
 * </p><p>
 * For a multi-dimensional object (i.e. with a rank > 1), the preferred storage is column-major
 * order, that is the first (resp. the last) dimension is the minor (resp. the major) dimension.
 * </p><p>
 * Like in Yorick extra trailing dimensions can be assumed to be equal to 1.
 * </p>
 * @author Éric Thiébaut.
 */
public interface Shaped {
    /**
     * The ordering of the dimensions is neither {@link #COLUMN_MAJOR}
     * nor {@link #ROW_MAJOR}.
     */
    static final int NONSPECIFIC_ORDER = 0;

    /**
     * The ordering of the dimensions is column-major, that is the leftmost (first)
     * indices vary faster when stepping through consecutive memory locations.
     */
    static final int COLUMN_MAJOR = 1;

    /**
     * The ordering of the dimensions is row-major, that is the rightmost (last)
     * indices vary faster when stepping through consecutive memory locations.
     */
    static final int ROW_MAJOR = 2;

    /**
     * Query the ordering of the dimensions in the view.
     *
     * This is useful for ordering the loops through the elements of a view.
     * In case of a tie, the method shall privilege {@link #COLUMN_MAJOR} which
     * is the preferred default in this library.
     * @return One of {@link #COLUMN_MAJOR}, {@link #ROW_MAJOR}, or
     *         {@link #NONSPECIFIC_ORDER}.
     */
    public int getOrder();

    /**
     * Query the rank (that is the number of dimensions) of the shaped object.
     * @return A positive integer.
     */
    public int getRank();

    /**
     * Query the number of elements (that is the product of its dimensions) of the shaped object.
     * @return A positive integer.
     */
    public int getNumber();

    /**
     * Query the length of a given dimension of the shaped object.
     * @param k - The index of the dimension of interest (starting at {@code 0} for
     *            the minor dimension and ending at {@code getRank()-1} for the major
     *            dimension).
     * @return The length of the {@code k}-th dimension.  If {@code k} is greater or
     *         equal the rank of the shaped object, {@code 1} is returned.  If {@code k}
     *         is less than zero, the exception {@link IndexOutOfBoundsException} is thrown.
     */
    public int getDimension(int k);

    /**
     * Get the shape of the shaped object.
     *
     * @return The shape of the object.
     */
    public Shape getShape();

}
