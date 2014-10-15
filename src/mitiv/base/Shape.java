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
 * A Shape object is unmodifiable and used to store the dimensions of a shaped
 * object in an efficient way.
 *
 * @author Éric Thiébaut
 */
public class Shape {
    private final int rank;
    private final int number;
    private final int[] shape;

    /** The shape of any scalar object. */
    private final static Shape scalarShape = new Shape(new int[]{}, true) {
        @Override
        public int shape(int k) {
            return 1;
        }
    };

    /** Factory for shape of scalar objects. */
    public static Shape make() {
        return scalarShape;
    }

    public static Shape make(int[] shape) {
        return new Shape(shape, false);
    }

    /**
     * Make a shape given the dimensions.
     * @param shape - The list of dimensions.
     * @param share - The contents of the list of dimensions will never change.
     * @return A Shape object.
     */
    public static Shape make(int[] shape, boolean share) {
        return new Shape(shape, share);
    }

    /**
     * Make a 1-D shape
     * @param dim1 - The 1st dimension.
     * @return A Shape object.
     */
    public static Shape make(int dim1) {
        return new Shape(new int[]{dim1}, true);
    }
    /**
     * Make a 2-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @return A Shape object.
     */
    public static Shape make(int dim1, int dim2) {
        return new Shape(new int[]{dim1, dim2}, true);
    }
    /**
     * Make a 3-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @return A Shape object.
     */
    public static Shape make(int dim1, int dim2, int dim3) {
        return new Shape(new int[]{dim1, dim2, dim3}, true);
    }
    /**
     * Make a 4-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @return A Shape object.
     */
    public static Shape make(int dim1, int dim2, int dim3, int dim4) {
        return new Shape(new int[]{dim1, dim2, dim3, dim4}, true);
    }
    /**
     * Make a 5-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @param dim5 - The 5th dimension.
     * @return A Shape object.
     */
    public static Shape make(int dim1, int dim2, int dim3, int dim4, int dim5) {
        return new Shape(new int[]{dim1, dim2, dim3, dim4, dim5}, true);
    }
    /**
     * Make a 6-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @param dim5 - The 5th dimension.
     * @param dim6 - The 6th dimension.
     * @return A Shape object.
     */
    public static Shape make(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        return new Shape(new int[]{dim1, dim2, dim3, dim4, dim5, dim6}, true);
    }
    /**
     * Make a 7-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @param dim5 - The 5th dimension.
     * @param dim6 - The 6th dimension.
     * @param dim7 - The 7th dimension.
     * @return A Shape object.
     */
    public static Shape make(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7) {
        return new Shape(new int[]{dim1, dim2, dim3, dim4, dim5, dim6, dim7}, true);
    }
    /**
     * Make a 8-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @param dim5 - The 5th dimension.
     * @param dim6 - The 6th dimension.
     * @param dim7 - The 7th dimension.
     * @param dim8 - The 8th dimension.
     * @return A Shape object.
     */
    public static Shape make(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        return new Shape(new int[]{dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8}, true);
    }
    /**
     * Make a 9-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @param dim5 - The 5th dimension.
     * @param dim6 - The 6th dimension.
     * @param dim7 - The 7th dimension.
     * @param dim8 - The 8th dimension.
     * @param dim9 - The 9th dimension.
     * @return A Shape object.
     */
    public static Shape make(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        return new Shape(new int[]{dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9}, true);
    }

    /**
     * Build a Shape object given a list of dimensions.
     * @param shape - The list of dimensions.
     * @param share - The contents of the list of dimensions will never change.
     */
    protected Shape(int[] shape, boolean share) {
        rank = shape.length;
        int number = 1;
        if (share) {
            this.shape = shape;
            for (int k = 0; k < rank; ++k) {
                int dim = shape[k];
                if (dim < 1) {
                    throw new IllegalArgumentException("Invalid dimension(s).");
                }
                number *= dim;
            }
        } else {
            this.shape = new int[rank];
            for (int k = 0; k < rank; ++k) {
                int dim = shape[k];
                if (dim < 1) {
                    throw new IllegalArgumentException("Invalid dimension(s).");
                }
                number *= dim;
                this.shape[k] = dim;
            }
        }
        this.number = number;
    }

    /**
     * Get the number of dimensions.
     * @return The number of dimensions.
     */
    public int rank() {
        return rank;
    }

    /**
     * Get the number of elements of an object with that shape.
     * @return The number of elements of an object with that shape.
     */
    public int number() {
        return number;
    }

    /**
     * Get the length of a given dimension.
     * @param k - The index of the dimension.
     * @return The length of the {@code (k+1)}-th dimension; {@code 1} if
     *         {@code k}&nbsp;&ge;&nbsp;{@link #rank()}.
     */
    public int shape(int k) {
        return (k < rank ? shape[k] : 1);
    }

    /**
     * Check whether another shape is the same as this one.
     * @param other - Another shape.
     * @return A boolean result.
     */
    public boolean equals(Shape other) {
        if (other != this && other.shape != this.shape) {
            if (this.rank != other.rank) {
                return false;
            }
            for (int k = 0; k < rank; ++k) {
                if (other.shape[k] != this.shape[k]) {
                    return false;
                }
            }
        }
        return true;
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
