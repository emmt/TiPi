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


/**
 * Define abstract class for multi-dimensional arrays of rank 1.
 *
 * @author Éric Thiébaut.
 */
public abstract class Array1D implements ShapedArray {
    static protected final int rank = 1;
    protected final int dim1;
    protected final int[] shape;

    /*
     * The following constructors make this class non instantiable, but still
     * let others inherit from this class.
     */

    protected Array1D(int dim1) {
        if (dim1 < 1) {
            throw new IllegalArgumentException("Bad dimension(s) for 1D array");
        }
        this.dim1 = dim1;
        this.shape = new int[]{dim1};
    }

    protected Array1D(int[] shape) {
        this(shape, true);
    }

    protected Array1D(int[] shape, boolean cloneShape) {
        if (shape == null || shape.length != rank ||
                (dim1 = shape[0]) < 1) {
            throw new IllegalArgumentException("Bad shape for 1D array");
        }
        if (cloneShape) {
            this.shape = new int[]{dim1};
        } else {
            this.shape = shape;
        }
    }

    @Override
    public final int getRank() {
        return rank;
    }

    @Override
    public final int[] cloneShape() {
        return new int[]{dim1};
    }

    /**
     * Get the shape (that is the list of dimensions) of the shaped object.
     * <p>
     * The result returned by this method must be considered as
     * <b><i>read-only</i></b>.  This is why the visibility of this method is
     * limited to the package. Use {@link #cloneShape} to get a copy of the
     * dimension list.
     *
     * @return A list of dimensions.
     */
    int[] getShape() {
        return shape;
    }

    @Override
    public final int getNumber() {
        return dim1;
    }

    @Override
    public final int getDimension(int k) {
        return (k < rank ? shape[k] : 1);
    }

    /**
     * Check the parameters of a 1D view with strides and get ordering.
     * @param number  - The number of elements in the wrapped array.
     * @param dim1    - The 1st dimension of the 1D view.
     * @param offset  - The offset of element (0) of the 1D view.
     * @param stride1 - The stride along the 1st dimension.
     * @return The ordering: {@link Shaped#COLUMN_MAJOR},
     *         {@link Shaped#ROW_MAJOR}, or {@link Shaped#NONSPECIFIC_ORDER}.
     */
    protected static int checkViewStrides(int number, int dim1,
            int offset, int stride1) {
        int imin, imax, itmp;
        itmp = (dim1 - 1)*stride1;
        if (itmp >= 0) {
            imin = offset;
            imax = offset + itmp;
        } else {
            imin = offset + itmp;
            imax = offset;
        }
        if (imin < 0 || imax >= number) {
            throw new IndexOutOfBoundsException("1D view is not within available space");
        }
        return COLUMN_MAJOR;
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
