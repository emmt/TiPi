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
import mitiv.base.Shape;
import mitiv.base.Shaped;
import mitiv.base.indexing.Range;


/**
 * Define abstract class for multi-dimensional arrays of rank 9.
 *
 * @author Éric Thiébaut.
 */
public abstract class Array9D implements ShapedArray {
    protected final Shape shape;
    protected final int number;
    protected final int dim1;
    protected final int dim2;
    protected final int dim3;
    protected final int dim4;
    protected final int dim5;
    protected final int dim6;
    protected final int dim7;
    protected final int dim8;
    protected final int dim9;

    /*
     * The following constructors make this class non instantiable, but still
     * let others inherit from this class.
     */
    protected Array9D(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        shape = Shape.make(dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
        if (shape.number() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Total number of elements is too large.");
        }
        number = (int)shape.number();
        this.dim1 = dim1;
        this.dim2 = dim2;
        this.dim3 = dim3;
        this.dim4 = dim4;
        this.dim5 = dim5;
        this.dim6 = dim6;
        this.dim7 = dim7;
        this.dim8 = dim8;
        this.dim9 = dim9;
    }

    protected Array9D(int[] dims) {
        this(Shape.make(dims));
    }

    protected Array9D(Shape shape) {
        if (shape.rank() != 9) {
            throw new IllegalArgumentException("Bad number of dimensions for 9-D array.");
        }
        if (shape.number() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Total number of elements is too large.");
        }
        this.number = (int)shape.number();
        this.shape = shape;
        this.dim1 = shape.dimension(0);
        this.dim2 = shape.dimension(1);
        this.dim3 = shape.dimension(2);
        this.dim4 = shape.dimension(3);
        this.dim5 = shape.dimension(4);
        this.dim6 = shape.dimension(5);
        this.dim7 = shape.dimension(6);
        this.dim8 = shape.dimension(7);
        this.dim9 = shape.dimension(8);
    }

    @Override
    public final int getRank() {
        return 9;
    }

    @Override
    public final Shape getShape() {
        return shape;
    }

    @Override
    public final int getNumber() {
        return number;
    }

    @Override
    public final int getDimension(int k) {
        return shape.dimension(k);
    }

    @Override
    public abstract Array9D copy();

    /**
     * Get a slice of the array.
     *
     * @param idx - The index of the slice along the last dimension of
     *              the array.  The same indexing rules as for
     *              {@link mitiv.base.indexing.Range} apply for negative
     *              index: 0 for the first, 1 for the second, -1 for the
     *              last, -2 for penultimate, <i>etc.</i>
     * @return A Array8D view on the given slice of the array.
     */
    public abstract Array8D slice(int idx);

    /**
     * Get a slice of the array.
     *
     * @param idx - The index of the slice along the last dimension of
     *              the array.
     * @param dim - The dimension to slice.  For these two arguments,
     *              the same indexing rules as for
     *              {@link mitiv.base.indexing.Range} apply for negative
     *              index: 0 for the first, 1 for the second, -1 for the
     *              last, -2 for penultimate, <i>etc.</i>
     *
     * @return A Array8D view on the given slice of the array.
     */
    public abstract Array8D slice(int idx, int dim);

    /**
     * Get a view of the array for given ranges of indices.
     *
     * @param rng1 - The range of indices to select along 1st dimension
     *               (or {@code null} to select all.
     * @param rng2 - The range of indices to select along 2nd dimension
     *               (or {@code null} to select all.
     * @param rng3 - The range of indices to select along 3rd dimension
     *               (or {@code null} to select all.
     * @param rng4 - The range of indices to select along 4th dimension
     *               (or {@code null} to select all.
     * @param rng5 - The range of indices to select along 5th dimension
     *               (or {@code null} to select all.
     * @param rng6 - The range of indices to select along 6th dimension
     *               (or {@code null} to select all.
     * @param rng7 - The range of indices to select along 7th dimension
     *               (or {@code null} to select all.
     * @param rng8 - The range of indices to select along 8th dimension
     *               (or {@code null} to select all.
     * @param rng9 - The range of indices to select along 9th dimension
     *               (or {@code null} to select all.
     *
     * @return A Array9D view for the given ranges of the array.
     */
    public abstract Array9D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5, Range rng6, Range rng7, Range rng8, Range rng9);

    /**
     * Get a view of the array for given ranges of indices.
     *
     * @param idx1 - The list of indices to select along 1st dimension
     *               (or {@code null} to select all.
     * @param idx2 - The list of indices to select along 2nd dimension
     *               (or {@code null} to select all.
     * @param idx3 - The list of indices to select along 3rd dimension
     *               (or {@code null} to select all.
     * @param idx4 - The list of indices to select along 4th dimension
     *               (or {@code null} to select all.
     * @param idx5 - The list of indices to select along 5th dimension
     *               (or {@code null} to select all.
     * @param idx6 - The list of indices to select along 6th dimension
     *               (or {@code null} to select all.
     * @param idx7 - The list of indices to select along 7th dimension
     *               (or {@code null} to select all.
     * @param idx8 - The list of indices to select along 8th dimension
     *               (or {@code null} to select all.
     * @param idx9 - The list of indices to select along 9th dimension
     *               (or {@code null} to select all.
     *
     * @return A Array9D view for the given index selections of the
     *         array.
     */
    public abstract Array9D view(int[] idx1, int[] idx2, int[] idx3, int[] idx4, int[] idx5, int[] idx6, int[] idx7, int[] idx8, int[] idx9);

    /**
     * Get a view of the array as a 1D array.
     *
     * @return A 1D view of the array.
     */
    public abstract Array1D as1D();

    /**
     * Check the parameters of a 9D view with strides and get ordering.
     * @param number  - The number of elements in the wrapped array.
     * @param dim1    - The 1st dimension of the 9D view.
     * @param dim2    - The 2nd dimension of the 9D view.
     * @param dim3    - The 3rd dimension of the 9D view.
     * @param dim4    - The 4th dimension of the 9D view.
     * @param dim5    - The 5th dimension of the 9D view.
     * @param dim6    - The 6th dimension of the 9D view.
     * @param dim7    - The 7th dimension of the 9D view.
     * @param dim8    - The 8th dimension of the 9D view.
     * @param dim9    - The 9th dimension of the 9D view.
     * @param offset  - The offset of element (0,0,0,0,0,0,0,0,0) of the 9D view.
     * @param stride1 - The stride along the 1st dimension.
     * @param stride2 - The stride along the 2nd dimension.
     * @param stride3 - The stride along the 3rd dimension.
     * @param stride4 - The stride along the 4th dimension.
     * @param stride5 - The stride along the 5th dimension.
     * @param stride6 - The stride along the 6th dimension.
     * @param stride7 - The stride along the 7th dimension.
     * @param stride8 - The stride along the 8th dimension.
     * @param stride9 - The stride along the 9th dimension.
     * @return The ordering: {@link Shaped#COLUMN_MAJOR},
     *         {@link Shaped#ROW_MAJOR}, or {@link Shaped#NONSPECIFIC_ORDER}.
     * @throws IndexOutOfBoundsException
     */
    public static int checkViewStrides(int number, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9,
            int offset, int stride1, int stride2, int stride3, int stride4, int stride5, int stride6, int stride7, int stride8, int stride9) {
        int imin, imax, itmp;
        itmp = (dim1 - 1)*stride1;
        if (itmp >= 0) {
            imin = offset;
            imax = offset + itmp;
        } else {
            imin = offset + itmp;
            imax = offset;
        }
        itmp = (dim2 - 1)*stride2;
        if (itmp >= 0) {
            imax += itmp;
        } else {
            imin += itmp;
        }
        itmp = (dim3 - 1)*stride3;
        if (itmp >= 0) {
            imax += itmp;
        } else {
            imin += itmp;
        }
        itmp = (dim4 - 1)*stride4;
        if (itmp >= 0) {
            imax += itmp;
        } else {
            imin += itmp;
        }
        itmp = (dim5 - 1)*stride5;
        if (itmp >= 0) {
            imax += itmp;
        } else {
            imin += itmp;
        }
        itmp = (dim6 - 1)*stride6;
        if (itmp >= 0) {
            imax += itmp;
        } else {
            imin += itmp;
        }
        itmp = (dim7 - 1)*stride7;
        if (itmp >= 0) {
            imax += itmp;
        } else {
            imin += itmp;
        }
        itmp = (dim8 - 1)*stride8;
        if (itmp >= 0) {
            imax += itmp;
        } else {
            imin += itmp;
        }
        itmp = (dim9 - 1)*stride9;
        if (itmp >= 0) {
            imax += itmp;
        } else {
            imin += itmp;
        }
        if (imin < 0 || imax >= number) {
            throw new IndexOutOfBoundsException("9D view is not within available space");
        }
        int s1 = Math.abs(stride1);
        int s2 = Math.abs(stride2);
        int s3 = Math.abs(stride3);
        int s4 = Math.abs(stride4);
        int s5 = Math.abs(stride5);
        int s6 = Math.abs(stride6);
        int s7 = Math.abs(stride7);
        int s8 = Math.abs(stride8);
        int s9 = Math.abs(stride9);
        if (s1 <= s2 && s2 <= s3 && s3 <= s4 && s4 <= s5 && s5 <= s6 && s6 <= s7 && s7 <= s8 && s8 <= s9) {
            return COLUMN_MAJOR;
        } else if (s1 >= s2 && s2 >= s3 && s3 >= s4 && s4 >= s5 && s5 >= s6 && s6 >= s7 && s7 >= s8 && s8 >= s9) {
            return ROW_MAJOR;
        } else {
            return NONSPECIFIC_ORDER;
        }
    }

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
