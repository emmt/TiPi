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

/**
 * A view is a mean to access an array as if it is a multidimensional array
 * with flexible strides and offset.
 * 
 * <p>
 * In a nutshell, a <i>views</i> wraps a Java mono-dimensional array into
 * an object, the <i>view</i> which provide methods to access the array as
 * if it is multi-dimensional.  Moreover, the ordering of the dimension, their
 * directions (forward or backward), the position of the first element and the
 * stepping along each dimension are almost arbitrary.  The only limit is to not
 * index the original array outside its limits.
 * <p>
 * To create a <i>view</i>, you must provide the array {@code data} which stores
 * the values, the dimensions {@code n1}, {@code n2}, ... of the view, the offset
 * {@code s0} of the first element of the view and the strides  {@code s1},
 * {@code s2}, ... along each dimension.  The <i>offset</i> is the index {@code s0}
 * in the wrapped array which correspond to the element at coordinates
 * {@code (0,0,...)} in the view.  The <i>strides</i> are equal to the number of
 * positions to skip in the wrapped array when the view index along the corresponding
 * dimension is incremented by 1.  It is even possible to have zero strides (to indicate
 * that the view does not depend on a particular dimensions) or negative strides to
 * move backward.
 * <p>
 * There are many flavors of <i>views</i>, for instance {@link FloatView3D} implements
 * efficient means to access to array of {@code float}'s as if they are 3 dimensional
 * arrays, similarly {@link ShortView4D} is to access arrays of {@code float}'s as if
 * they are 4 dimensional arrays.
 * <p>
 * As an example, we consider below a 3x4x5 <i>view</i> on an array of {@code float}'s
 * which is accessed in column-major order and filled with some computed values:
 * <pre>
 *     int n1 = 4, n2 = 5, n3 = 6;
 *     float[] arr = new float[n1*n2*n3];
 *     FloatView3D view = new FloatView3D(arr, n1,n2,n3, 0, 1,n1,n1*n2);
 *     for (int i3 = 0; i3 < n3; ++i3) {
 *         for (int i2 = 0; i2 < n2; ++i2) {
 *             for (int i1 = 0; i1 < n1; ++i1) {
 *                view.set(i1,i2,i3, Math.sqrt(i1*i1 + i2*i2 + i3*i3));
 *             }
 *         }
 *     }
 * </pre>
 * 
 * @author Éric Thiébaut.
 */
public interface View {
    /** The ordering of the dimensions is neither {@link #COLUMN_MAJOR}
     *  nor {@link #ROW_MAJOR}.
     */
    static final int NONSPECIFIC_ORDER = 0;

    /**
     * The ordering of the dimension is column-major, that is the leftmost (first)
     * indices vary faster when stepping through consecutive memory locations.
     */
    static final int COLUMN_MAJOR = 1;

    /**
     * The ordering of the dimension is row-major, that is the rightmost (last)
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
     * Query the rank (that is the number of dimensions) of the view.
     * @return A positive integer.
     */
    public int getRank();

    /**
     * Query the shape (that is the list of dimensions) of the view.
     */
    public int[] getShape();
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
