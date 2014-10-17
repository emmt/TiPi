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

import mitiv.array.impl.FlatInt5D;
import mitiv.array.impl.StriddenInt5D;
import mitiv.base.Shape;
import mitiv.base.Shaped;
import mitiv.base.mapping.IntFunction;
import mitiv.base.mapping.IntScanner;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.random.IntGenerator;


/**
 * Define class for comprehensive 5-dimensional arrays of int's.
 *
 * @author Éric Thiébaut.
 */
public abstract class Int5D extends Array5D implements IntArray {

    protected Int5D(int dim1, int dim2, int dim3, int dim4, int dim5) {
        super(dim1,dim2,dim3,dim4,dim5);
    }

    protected Int5D(int[] dims) {
        super(dims);
    }

    protected Int5D(Shape shape) {
        super(shape);
    }

    @Override
    public final int getType() {
        return type;
    }

    /**
     * Query the value stored at a given position.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param i5 - The index along the 5th dimension.
     * @return The value stored at position {@code (i1,i2,i3,i4,i5)}.
     */
    public abstract int get(int i1, int i2, int i3, int i4, int i5);

    /**
     * Set the value at a given position.
     * @param i1    - The index along the 1st dimension.
     * @param i2    - The index along the 2nd dimension.
     * @param i3    - The index along the 3rd dimension.
     * @param i4    - The index along the 4th dimension.
     * @param i5    - The index along the 5th dimension.
     * @param value - The value to store at position {@code (i1,i2,i3,i4,i5)}.
     */
    public abstract void set(int i1, int i2, int i3, int i4, int i5, int value);

    /*=======================================================================*/
    /* Provide default (non-optimized, except for the loop ordering)
     * implementation of methods that can be coded solely with the "set"
     * and "get" methods. */

    @Override
    public void fill(int value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, value);
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, value);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void increment(int value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, get(i1,i2,i3,i4,i5) + value);
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, get(i1,i2,i3,i4,i5) + value);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void decrement(int value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, get(i1,i2,i3,i4,i5) - value);
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, get(i1,i2,i3,i4,i5) - value);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void scale(int value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, get(i1,i2,i3,i4,i5) * value);
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, get(i1,i2,i3,i4,i5) * value);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void map(IntFunction function) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, function.apply(get(i1,i2,i3,i4,i5)));
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, function.apply(get(i1,i2,i3,i4,i5)));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void fill(IntGenerator generator) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, generator.nextInt());
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, generator.nextInt());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void scan(IntScanner scanner)  {
        boolean skip = true;
        scanner.initialize(get(0,0,0,0,0));
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                if (skip) skip = false; else scanner.update(get(i1,i2,i3,i4,i5));
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                if (skip) skip = false; else scanner.update(get(i1,i2,i3,i4,i5));
                            }
                        }
                    }
                }
            }
        }
    }

    /* Note that the following default implementation of the "flatten" method
     * is always returning a copy of the contents whatever the value of the
     * "forceCopy" argument.
     * @see devel.eric.array.base.IntArray#flatten(boolean)
     */
    @Override
    public int[] flatten(boolean forceCopy) {
        /* Copy the elements in column-major order. */
        int[] out = new int[number];
        int i = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            for (int i4 = 0; i4 < dim4; ++i4) {
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            out[++i] = get(i1,i2,i3,i4,i5);
                        }
                    }
                }
            }
        }
        return out;
    }

    @Override
    public int[] flatten() {
        return flatten(false);
    }

    /**
     * Convert instance into a Byte5D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Byte5D whose values has been converted into byte's
     *         from those of {@code this}.
     */
    @Override
    public Byte5D toByte() {
        byte[] out = new byte[number];
        int i = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            for (int i4 = 0; i4 < dim4; ++i4) {
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            out[++i] = (byte)get(i1,i2,i3,i4,i5);
                        }
                    }
                }
            }
        }
        return Byte5D.wrap(out, dim1, dim2, dim3, dim4, dim5);
    }
    /**
     * Convert instance into a Short5D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Short5D whose values has been converted into short's
     *         from those of {@code this}.
     */
    @Override
    public Short5D toShort() {
        short[] out = new short[number];
        int i = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            for (int i4 = 0; i4 < dim4; ++i4) {
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            out[++i] = (short)get(i1,i2,i3,i4,i5);
                        }
                    }
                }
            }
        }
        return Short5D.wrap(out, dim1, dim2, dim3, dim4, dim5);
    }
    /**
     * Convert instance into an Int5D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return An Int5D whose values has been converted into int's
     *         from those of {@code this}.
     */
    @Override
    public Int5D toInt() {
        return this;
    }
    /**
     * Convert instance into a Long5D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Long5D whose values has been converted into long's
     *         from those of {@code this}.
     */
    @Override
    public Long5D toLong() {
        long[] out = new long[number];
        int i = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            for (int i4 = 0; i4 < dim4; ++i4) {
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            out[++i] = (long)get(i1,i2,i3,i4,i5);
                        }
                    }
                }
            }
        }
        return Long5D.wrap(out, dim1, dim2, dim3, dim4, dim5);
    }
    /**
     * Convert instance into a Float5D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Float5D whose values has been converted into float's
     *         from those of {@code this}.
     */
    @Override
    public Float5D toFloat() {
        float[] out = new float[number];
        int i = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            for (int i4 = 0; i4 < dim4; ++i4) {
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            out[++i] = (float)get(i1,i2,i3,i4,i5);
                        }
                    }
                }
            }
        }
        return Float5D.wrap(out, dim1, dim2, dim3, dim4, dim5);
    }
    /**
     * Convert instance into a Double5D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Double5D whose values has been converted into double's
     *         from those of {@code this}.
     */
    @Override
    public Double5D toDouble() {
        double[] out = new double[number];
        int i = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            for (int i4 = 0; i4 < dim4; ++i4) {
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            out[++i] = (double)get(i1,i2,i3,i4,i5);
                        }
                    }
                }
            }
        }
        return Double5D.wrap(out, dim1, dim2, dim3, dim4, dim5);
    }

    @Override
    public Int5D copy() {
        // TODO
        return null;
    }

    @Override
    public void assign(ShapedArray arr) {
        // TODO
    }

    @Override
    public void assign(ShapedVector vec) {
        // TODO
    }


    /*=======================================================================*/
    /* ARRAY FACTORIES */

    /**
     * Create a 5D array of int's with given dimensions.
     * <p>
     * This method creates a 5D array of int's with zero offset, contiguous
     * elements and column-major order.  All dimensions must at least 1.
     * @param dim1 - The 1st dimension of the 5D array.
     * @param dim2 - The 2nd dimension of the 5D array.
     * @param dim3 - The 3rd dimension of the 5D array.
     * @param dim4 - The 4th dimension of the 5D array.
     * @param dim5 - The 5th dimension of the 5D array.
     * @return A new 5D array of int's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Int5D create(int dim1, int dim2, int dim3, int dim4, int dim5) {
        return new FlatInt5D(dim1,dim2,dim3,dim4,dim5);
    }

    /**
     * Create a 5D array of int's with given shape.
     * <p>
     * This method creates a 5D array of int's with zero offset, contiguous
     * elements and column-major order.
     * @param dims - The list of dimensions of the 5D array (all dimensions
     *               must at least 1).  This argument is not referenced by
     *               the returned object and its contents can be modified
     *               after calling this method.
     * @return A new 5D array of int's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Int5D create(int[] dims) {
        return new FlatInt5D(dims);
    }

    /**
     * Create a 5D array of int's with given shape.
     * <p>
     * This method creates a 5D array of int's with zero offset, contiguous
     * elements and column-major order.
     * @param shape      - The shape of the 5D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 5D array of int's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Int5D create(Shape shape) {
        return new FlatInt5D(shape);
    }

    /**
     * Wrap an existing array in a 5D array of int's with given dimensions.
     * <p>
     * The returned 5D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5) = data[i1 + dim1*(i2 + dim2*(i3 + dim3*(i4 + dim4*i5)))]</pre>
     * with {@code arr} the returned 5D array.
     * @param data - The data to wrap in the 5D array.
     * @param dim1 - The 1st dimension of the 5D array.
     * @param dim2 - The 2nd dimension of the 5D array.
     * @param dim3 - The 3rd dimension of the 5D array.
     * @param dim4 - The 4th dimension of the 5D array.
     * @param dim5 - The 5th dimension of the 5D array.
     * @return A 5D array sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Int5D wrap(int[] data, int dim1, int dim2, int dim3, int dim4, int dim5) {
        return new FlatInt5D(data, dim1,dim2,dim3,dim4,dim5);
    }

    /**
     * Wrap an existing array in a 5D array of int's with given shape.
     * <p>
     * The returned 5D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5) = data[i1 + shape[0]*(i2 + shape[1]*(i3 + shape[2]*(i4 + shape[3]*i5)))]</pre>
     * with {@code arr} the returned 5D array.
     * @param data - The data to wrap in the 5D array.
     * @param dims - The list of dimensions of the 5D array.  This argument is
     *                not referenced by the returned object and its contents
     *                can be modified after the call to this method.
     * @return A new 5D array of int's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Int5D wrap(int[] data, int[] dims) {
        return new FlatInt5D(data, dims);
    }

    /**
     * Wrap an existing array in a 5D array of int's with given shape.
     * <p>
     * The returned 5D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5) = data[i1 + shape[0]*(i2 + shape[1]*(i3 + shape[2]*(i4 + shape[3]*i5)))]</pre>
     * with {@code arr} the returned 5D array.
     * @param data       - The data to wrap in the 5D array.
     * @param shape      - The shape of the 5D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 5D array of int's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Int5D wrap(int[] data, Shape shape) {
        return new FlatInt5D(data, shape);
    }

    /**
     * Wrap an existing array in a 5D array of int's with given dimensions,
     * strides and offset.
     * <p>
     * This creates a 5D array of dimensions {{@code dim1,dim2,dim3,dim4,dim5}}
     * sharing (part of) the contents of {@code data} in arbitrary storage
     * order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5) = data[offset + stride1*i1 + stride2*i2 + stride3*i3 + stride4*i4 + stride5*i5]</pre>
     * with {@code arr} the returned 5D array.
     * @param data    - The array to wrap in the 5D array.
     * @param dim1    - The 1st dimension of the 5D array.
     * @param dim2    - The 2nd dimension of the 5D array.
     * @param dim3    - The 3rd dimension of the 5D array.
     * @param dim4    - The 4th dimension of the 5D array.
     * @param dim5    - The 5th dimension of the 5D array.
     * @param offset  - The offset in {@code data} of element (0,0,0,0,0) of
     *                  the 5D array.
     * @param stride1 - The stride along the 1st dimension.
     * @param stride2 - The stride along the 2nd dimension.
     * @param stride3 - The stride along the 3rd dimension.
     * @param stride4 - The stride along the 4th dimension.
     * @param stride5 - The stride along the 5th dimension.
     * @return A 5D array sharing the elements of <b>data</b>.
     */
    public static Int5D wrap(int[] data, int dim1, int dim2, int dim3, int dim4, int dim5,
            int offset, int stride1, int stride2, int stride3, int stride4, int stride5) {
        return new StriddenInt5D(data, dim1,dim2,dim3,dim4,dim5, offset, stride1,stride2,stride3,stride4,stride5);
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
