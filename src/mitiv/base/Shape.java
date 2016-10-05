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

//
package mitiv.base;


/**
 * Shape object to store dimesnions list.
 * <p>
 * A Shape object is unmodifiable and is used to store the dimensions of a
 * shaped object in an efficient way.  To preserve its properties, Shape
 * objects can only be built by factories (static methods).
 * </p>
 * @author Éric Thiébaut
 */
public class Shape {
    /* The following (long) constants are used to check for integer
       overflows. */
    private final static long LONG_MAX = Long.MAX_VALUE;
    private final static long INT_MAX = Integer.MAX_VALUE;

    private final long number;
    private final int rank;
    private final int[] dims;

    /** The shape of any scalar object. */
    private final static Shape scalarShape = new Shape(new int[]{}, true);

    /**
     * Get the shape of scalar objects.
     * @return The shape of a scalar object.
     */
    @Deprecated
    public static Shape make() {
        return scalarShape;
    }

    /**
     * Make a shape given the dimensions.
     * @param dims - The list of dimensions ({@code null} is the same as
     *               an array of length equals to {@code 0} and yields the
     *               shape of a scalar object).
     * @return A new shape built from the given dimensions.
     */
    @Deprecated
    public static Shape make(int[] dims) {
        if (dims == null || dims.length == 0) {
            return scalarShape;
        } else {
            return new Shape(dims, false);
        }
    }

    /**
     * Make a shape given the dimensions.
     * @param dims - The list of dimensions ({@code null} is the same as
     *               an array of length equals to {@code 0} and yields the
     *               shape of a scalar object).
     * @return A new shape built from the given dimensions.
     */
    @Deprecated
    public static Shape make(long[] dims) {
        if (dims == null || dims.length == 0) {
            return scalarShape;
        } else {
            return new Shape(dims);
        }
    }

    /**
     * Make a 1-D shape
     * @param dim1 - The 1st dimension.
     * @return A new 1-dimensional shape built from the given dimensions.
     */
    @Deprecated
    public static Shape make(int dim1) {
        return new Shape(new int[]{dim1}, true);
    }

    /**
     * Make a 2-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @return A new 2-dimensional shape built from the given dimensions.
     */
    @Deprecated
    public static Shape make(int dim1, int dim2) {
        return new Shape(new int[]{dim1, dim2}, true);
    }

    /**
     * Make a 3-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @return A new 3-dimensional shape built from the given dimensions.
     */
    @Deprecated
    public static Shape make(int dim1, int dim2, int dim3) {
        return new Shape(new int[]{dim1, dim2, dim3}, true);
    }

    /**
     * Make a 4-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @return A new 4-dimensional shape built from the given dimensions.
     */
    @Deprecated
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
     * @return A new 5-dimensional shape built from the given dimensions.
     */
    @Deprecated
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
     * @return A new 6-dimensional shape built from the given dimensions.
     */
    @Deprecated
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
     * @return A new 7-dimensional shape built from the given dimensions.
     */
    @Deprecated
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
     * @return A new 8-dimensional shape built from the given dimensions.
     */
    @Deprecated
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
     * @return A new 9-dimensional shape built from the given dimensions.
     */
    @Deprecated
    public static Shape make(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        return new Shape(new int[]{dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9}, true);
    }

    /**
     * Get the number of dimensions.
     * @return The number of dimensions.
     */
    public final int rank() {
        return rank;
    }

    /**
     * Get the number of elements of an object with that shape.
     * @return The number of elements of an object with that shape.
     */
    public final long number() {
        return number;
    }

    /**
     * Get the length of a given dimension.
     * @param k - The index of the dimension.
     * @return The length of the {@code (k+1)}-th dimension.
     */
    public final int dimension(int k) {
        return dims[k];
    }

    /**
     * Copy the dimension list.
     * @return A copy of the dimension list.
     */
    public final int[] copyDimensions() {
        if (rank == 0) {
            return dims;
        } else {
            int[] copy = new int[rank];
            for (int k = 0; k < rank; ++k) {
                copy[k] = dims[k];
            }
            return copy;
        }
    }

    /**
     * Check whether another shape is the same as this one.
     * @param other - Another shape.
     * @return A boolean result.
     */
    public final boolean equals(Shape other) {
        if (other != this && other.dims != this.dims) {
            if (this.rank != other.rank) {
                return false;
            }
            for (int k = 0; k < rank; ++k) {
                if (other.dims[k] != this.dims[k]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Build a Shape object given a list of dimensions.
     * <p>
     * This constructor of the class is private to prevent any
     * uncontrolled construction with shared dimensions and to
     * force all scalars the share the same shape.
     * <p>
     * @param dims  - The list of dimensions.
     * @param share - The caller guarantees that the contents of the list of
     *                dimensions will never change.
     */
    private Shape(int[] dims, boolean share) {
        long number = 1L;
        rank = dims.length;
        if (share) {
            this.dims = dims;
            for (int k = 0; k < rank; ++k) {
                final int dim = dims[k];
                if (dim < 1) {
                    dimensionTooSmall();
                }
                if (dim > LONG_MAX/number) {
                    numberOverflow();
                }
                number *= dim;
            }
        } else {
            this.dims = new int[rank];
            for (int k = 0; k < rank; ++k) {
                final int dim = dims[k];
                if (dim < 1) {
                    dimensionTooSmall();
                }
                if (dim > LONG_MAX/number) {
                    numberOverflow();
                }
                number *= dim;
                this.dims[k] = dim;
            }
        }
        this.number = number;
    }

    /**
     * Build a Shape object given a list of dimensions.
     *
     * <p>
     * The input dimensions are copied into a private array and are
     * never shared with the returned shape instance.
     *
     * @param dims - The list of dimensions.
     */
    public Shape(int[] dims) {
        this(dims, false);
    }

    /**
     * Build a Shape object given a list of dimensions.
     *
     * <p>
     * The input dimensions are copied into a private array and are
     * never shared with the returned shape instance.
     *
     * @param dims - The list of dimensions.
     */
    public Shape(long[] dims) {
        long number = 1L;
        rank = dims.length;
        this.dims = new int[rank];
        for (int k = 0; k < rank; ++k) {
            final long dim = dims[k];
            if (dim < 1L) {
                dimensionTooSmall();
            }
            if (dim > INT_MAX) {
                dimensionTooLarge();
            }
            if (dim > LONG_MAX/number) {
                numberOverflow();
            }
            number *= dim;
            this.dims[k] = (int)dim;
        }
        this.number = number;
    }

    /**
     * Create a 1-D shape
     * @param dim1 - The 1st dimension.
     * @return A new 1-dimensional shape built from the given dimensions.
     */
    public Shape(int dim1) {
        this(new int[]{dim1}, true);
    }

    /**
     * Create a 2-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @return A new 2-dimensional shape built from the given dimensions.
     */
    public Shape(int dim1, int dim2) {
        this(new int[]{dim1, dim2}, true);
    }

    /**
     * Create a 3-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @return A new 3-dimensional shape built from the given dimensions.
     */
    public Shape(int dim1, int dim2, int dim3) {
        this(new int[]{dim1, dim2, dim3}, true);
    }

    /**
     * Create a 4-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @return A new 4-dimensional shape built from the given dimensions.
     */
    public Shape(int dim1, int dim2, int dim3, int dim4) {
        this(new int[]{dim1, dim2, dim3, dim4}, true);
    }

    /**
     * Create a 5-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @param dim5 - The 5th dimension.
     * @return A new 5-dimensional shape built from the given dimensions.
     */
    public Shape(int dim1, int dim2, int dim3, int dim4, int dim5) {
        this(new int[]{dim1, dim2, dim3, dim4, dim5}, true);
    }

    /**
     * Create a 6-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @param dim5 - The 5th dimension.
     * @param dim6 - The 6th dimension.
     * @return A new 6-dimensional shape built from the given dimensions.
     */
    public Shape(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        this(new int[]{dim1, dim2, dim3, dim4, dim5, dim6}, true);
    }

    /**
     * Create a 7-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @param dim5 - The 5th dimension.
     * @param dim6 - The 6th dimension.
     * @param dim7 - The 7th dimension.
     * @return A new 7-dimensional shape built from the given dimensions.
     */
    public Shape(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7) {
        this(new int[]{dim1, dim2, dim3, dim4, dim5, dim6, dim7}, true);
    }

    /**
     * Create a 8-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @param dim5 - The 5th dimension.
     * @param dim6 - The 6th dimension.
     * @param dim7 - The 7th dimension.
     * @param dim8 - The 8th dimension.
     * @return A new 8-dimensional shape built from the given dimensions.
     */
    public Shape(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        this(new int[]{dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8}, true);
    }

    /**
     * Create a 9-D shape
     * @param dim1 - The 1st dimension.
     * @param dim2 - The 2nd dimension.
     * @param dim3 - The 3rd dimension.
     * @param dim4 - The 4th dimension.
     * @param dim5 - The 5th dimension.
     * @param dim6 - The 6th dimension.
     * @param dim7 - The 7th dimension.
     * @param dim8 - The 8th dimension.
     * @param dim9 - The 9th dimension.
     * @return A new 9-dimensional shape built from the given dimensions.
     */
    public Shape(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        this(new int[]{dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9}, true);
    }

    /**
     * Query the string representation of a shape.
     */
    @Override
    public String toString() {
        String str = "[";
        for (int k = 0; k < rank; ++k) {
            str += String.format((k > 0 ? ",%d" : "%d"), dims[k]);
        }
        return str + "]";
    }

    private static void dimensionTooSmall() {
        throw new IllegalArgumentException("Dimensions must be at least 1.");
    }

    private static void dimensionTooLarge() {
        throw new IllegalArgumentException("Dimensions must be at most Integer.MAX_VALUE.");
    }

    private static void numberOverflow() {
        throw new IllegalArgumentException("Total number of elements is too large.");
    }

}
