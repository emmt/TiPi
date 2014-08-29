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
 * Define abstract class for multi-dimensional arrays of rank 6.
 *
 * @author Éric Thiébaut.
 */
public abstract class Array6D implements Shaped {
    static protected final int rank = 6;
    protected final int dim1;
    protected final int dim2;
    protected final int dim3;
    protected final int dim4;
    protected final int dim5;
    protected final int dim6;
    protected final int number;
    protected final int[] shape;

    /*
     * The following constructors make this class non instantiable, but still
     * let others inherit from this class.
     */

    protected Array6D(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        if (dim1 < 1 || dim2 < 1 || dim3 < 1 || dim4 < 1 || dim5 < 1 || dim6 < 1) {
            throw new IllegalArgumentException("Bad dimension(s) for 6D array");
        }
        this.dim1 = dim1;
        this.dim2 = dim2;
        this.dim3 = dim3;
        this.dim4 = dim4;
        this.dim5 = dim5;
        this.dim6 = dim6;
        this.number = dim1*dim2*dim3*dim4*dim5*dim6;
        this.shape = new int[]{dim1,dim2,dim3,dim4,dim5,dim6};
    }

    protected Array6D(int[] shape, boolean cloneShape) {
        if (shape == null || shape.length != rank ||
                (dim1 = shape[0]) < 1 ||
                (dim2 = shape[1]) < 1 ||
                (dim3 = shape[2]) < 1 ||
                (dim4 = shape[3]) < 1 ||
                (dim5 = shape[4]) < 1 ||
                (dim6 = shape[5]) < 1) {
            throw new IllegalArgumentException("Bad shape for 6D array");
        }
        this.number = dim1*dim2*dim3*dim4*dim5*dim6;
        if (cloneShape) {
            this.shape = new int[]{dim1,dim2,dim3,dim4,dim5,dim6};
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
        return new int[]{dim1,dim2,dim3,dim4,dim5,dim6};
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
        return number;
    }

    @Override
    public final int getDimension(int k) {
        return (k < rank ? shape[k] : 1);
    }

    /**
     * Check the parameters of a 6D view with strides and get ordering.
     * @param number  - The number of elements in the wrapped array.
     * @param dim1    - The 1st dimension of the 6D view.
     * @param dim2    - The 2nd dimension of the 6D view.
     * @param dim3    - The 3rd dimension of the 6D view.
     * @param dim4    - The 4th dimension of the 6D view.
     * @param dim5    - The 5th dimension of the 6D view.
     * @param dim6    - The 6th dimension of the 6D view.
     * @param offset  - The offset of element (0,0,0,0,0,0) of the 6D view.
     * @param stride1 - The stride along the 1st dimension.
     * @param stride2 - The stride along the 2nd dimension.
     * @param stride3 - The stride along the 3rd dimension.
     * @param stride4 - The stride along the 4th dimension.
     * @param stride5 - The stride along the 5th dimension.
     * @param stride6 - The stride along the 6th dimension.
     * @return The ordering: {@link Shaped#COLUMN_MAJOR},
     *         {@link Shaped#ROW_MAJOR}, or {@link Shaped#NONSPECIFIC_ORDER}.
     */
    protected static int checkViewStrides(int number, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6,
            int offset, int stride1, int stride2, int stride3, int stride4, int stride5, int stride6) {
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
        if (imin < 0 || imax >= number) {
            throw new IndexOutOfBoundsException("6D view is not within available space");
        }
        int s1 = Math.abs(stride1);
        int s2 = Math.abs(stride2);
        int s3 = Math.abs(stride3);
        int s4 = Math.abs(stride4);
        int s5 = Math.abs(stride5);
        int s6 = Math.abs(stride6);
        if (s1 <= s2 && s2 <= s3 && s3 <= s4 && s4 <= s5 && s5 <= s6) {
            return COLUMN_MAJOR;
        } else if (s1 >= s2 && s2 >= s3 && s3 >= s4 && s4 >= s5 && s5 >= s6) {
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
