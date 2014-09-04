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
import mitiv.base.mapping.FloatFunction;
import mitiv.base.mapping.FloatScanner;
import mitiv.random.FloatGenerator;


/**
 * Define class for comprehensive 8-dimensional arrays of float's.
 *
 * @author Éric Thiébaut.
 */
public abstract class Float8D extends Array8D implements FloatArray {

    protected Float8D(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        super(dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8);
    }

    protected Float8D(int[] shape, boolean cloneShape) {
        super(shape, cloneShape);
    }

    protected Float8D(int[] shape) {
        super(shape, true);
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
     * @param i6 - The index along the 6th dimension.
     * @param i7 - The index along the 7th dimension.
     * @param i8 - The index along the 8th dimension.
     * @return The value stored at position {@code (i1,i2,i3,i4,i5,i6,i7,i8)}.
     */
    public abstract float get(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    /**
     * Set the value at a given position.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param i5 - The index along the 5th dimension.
     * @param i6 - The index along the 6th dimension.
     * @param i7 - The index along the 7th dimension.
     * @param i8 - The index along the 8th dimension.
     * @param value - The value to store at position {@code (i1,i2,i3,i4,i5,i6,i7,i8)}.
     */
    public abstract void set(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float value);

    /*=======================================================================*/
    /* Provide default (non-optimized, except for the loop ordering)
     * implementation of methods that can be coded solely with the "set"
     * and "get" methods. */

    @Override
    public void set(float value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void incr(float value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, get(i1,i2,i3,i4,i5,i6,i7,i8) + value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, get(i1,i2,i3,i4,i5,i6,i7,i8) + value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void decr(float value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, get(i1,i2,i3,i4,i5,i6,i7,i8) - value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, get(i1,i2,i3,i4,i5,i6,i7,i8) - value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mult(float value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, get(i1,i2,i3,i4,i5,i6,i7,i8) * value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, get(i1,i2,i3,i4,i5,i6,i7,i8) * value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void map(FloatFunction function) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, function.apply(get(i1,i2,i3,i4,i5,i6,i7,i8)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, function.apply(get(i1,i2,i3,i4,i5,i6,i7,i8)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void set(FloatGenerator generator) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, generator.nextFloat());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, generator.nextFloat());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void scan(FloatScanner scanner)  {
        boolean skip = true;
        scanner.initialize(get(0,0,0,0,0,0,0,0));
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            if (skip) skip = false; else scanner.update(get(i1,i2,i3,i4,i5,i6,i7,i8));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            if (skip) skip = false; else scanner.update(get(i1,i2,i3,i4,i5,i6,i7,i8));
                                        }
                                    }
                                }
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
     * @see devel.eric.array.base.FloatArray#flatten(boolean)
     */
    @Override
    public float[] flatten(boolean forceCopy) {
        /* Copy the elements in column-major order. */
        float[] out = new float[number];
        int i = -1;
        for (int i8 = 0; i8 < dim8; ++i8) {
            for (int i7 = 0; i7 < dim7; ++i7) {
                for (int i6 = 0; i6 < dim6; ++i6) {
                    for (int i5 = 0; i5 < dim5; ++i5) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i3 = 0; i3 < dim3; ++i3) {
                                for (int i2 = 0; i2 < dim2; ++i2) {
                                    for (int i1 = 0; i1 < dim1; ++i1) {
                                        out[++i] = get(i1,i2,i3,i4,i5,i6,i7,i8);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return out;
    }

    @Override
    public float[] flatten() {
        return flatten(false);
    }

    /*=======================================================================*/
    /* FACTORY */

    /* Inner class instances can only be created from an instance of the outer
     * class.  For this, we need a static instance of the outer class (to
     * spare the creation of this instance each time a new instance of the
     * inner class is needed).  The outer class is however "abstract" and we
     * must provide a minimal set of methods to make it instantiable.
     */
    private static final Float8D factory = new Float8D(1,1,1,1,1,1,1,1) {
        @Override
        public final float get(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            return 0.0F;
        }
        @Override
        public final void set(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float value) {
        }
        @Override
        public final int getOrder() {
            return COLUMN_MAJOR;
        }
        @Override
        public float[] flatten(boolean forceCopy) {
            return null;
        }
    };

    /*=======================================================================*/
    /* FLAT LAYOUT */

    /**
     * Create a 8D array of float's with given dimensions.
     * <p>
     * This method creates a 8D array of float's with zero offset, contiguous
     * elements and column-major order.  All dimensions must at least 1.
     * @param dim1 - The 1st dimension of the 8D array.
     * @param dim2 - The 2nd dimension of the 8D array.
     * @param dim3 - The 3rd dimension of the 8D array.
     * @param dim4 - The 4th dimension of the 8D array.
     * @param dim5 - The 5th dimension of the 8D array.
     * @param dim6 - The 6th dimension of the 8D array.
     * @param dim7 - The 7th dimension of the 8D array.
     * @param dim8 - The 8th dimension of the 8D array.
     * @return A new 8D array of float's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Float8D create(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        return factory.new Flat(dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8);
    }

    /**
     * Create a 8D array of float's with given shape.
     * <p>
     * This method creates a 8D array of float's with zero offset, contiguous
     * elements and column-major order.
     * @param shape - The list of dimensions of the 8D array (all dimensions
     *                must at least 1).  This argument is not referenced by
     *                the returned object and its contents can be modified
     *                after calling this method.
     * @return A new 8D array of float's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Float8D create(int[] shape) {
        return factory.new Flat(shape, true);
    }

    /**
     * Create a 8D array of float's with given shape.
     * <p>
     * This method creates a 8D array of float's with zero offset, contiguous
     * elements and column-major order.
     * @param shape      - The list of dimensions of the 8D array (all
     *                     dimensions must at least 1).
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 8D array of float's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Float8D create(int[] shape, boolean cloneShape) {
        return factory.new Flat(shape, cloneShape);
    }

    /**
     * Wrap an existing array in a 8D array of float's with given dimensions.
     * <p>
     * The returned 8D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8) = data[i1 + dim1*(i2 + dim2*(i3 + dim3*(i4 + dim4*(i5 + dim5*(i6 + dim6*(i7 + dim7*i8))))))]</pre>
     * with {@code arr} the returned 8D array.
     * @param data - The data to wrap in the 8D array.
     * @param dim1 - The 1st dimension of the 8D array.
     * @param dim2 - The 2nd dimension of the 8D array.
     * @param dim3 - The 3rd dimension of the 8D array.
     * @param dim4 - The 4th dimension of the 8D array.
     * @param dim5 - The 5th dimension of the 8D array.
     * @param dim6 - The 6th dimension of the 8D array.
     * @param dim7 - The 7th dimension of the 8D array.
     * @param dim8 - The 8th dimension of the 8D array.
     * @return A 8D array sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Float8D wrap(float[] data, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        return factory.new Flat(data, dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8);
    }

    /**
     * Wrap an existing array in a 8D array of float's with given shape.
     * <p>
     * The returned 8D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8) = data[i1 + shape[0]*(i2 + shape[1]*(i3 + shape[2]*(i4 + shape[3]*(i5 + shape[4]*(i6 + shape[5]*(i7 + shape[6]*i8))))))]</pre>
     * with {@code arr} the returned 8D array.
     * @param data - The data to wrap in the 8D array.
     * @param shape - The list of dimensions of the 8D array.  This argument is
     *                not referenced by the returned object and its contents
     *                can be modified after the call to this method.
     * @return A new 8D array of float's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Float8D wrap(float[] data, int[] shape) {
        return factory.new Flat(data, shape, true);
    }

    /**
     * Wrap an existing array in a 8D array of float's with given shape.
     * <p>
     * The returned 8D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8) = data[i1 + shape[0]*(i2 + shape[1]*(i3 + shape[2]*(i4 + shape[3]*(i5 + shape[4]*(i6 + shape[5]*(i7 + shape[6]*i8))))))]</pre>
     * with {@code arr} the returned 8D array.
     * @param data       - The data to wrap in the 8D array.
     * @param shape      - The list of dimensions of the 8D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 8D array of float's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Float8D wrap(float[] data, int[] shape, boolean cloneShape) {
        return factory.new Flat(data, shape, cloneShape);
    }

    /*
     * The following inner class is defined to handle the specific case of a
     * 8D array stored in a "flat" (1D) Java array in column-major order.
     * To instantiate such an inner class, an instance of the outer class must
     * be available (this is the purpose of the static "factory" instance).
     */
    private class Flat extends Float8D {
        private static final int order = COLUMN_MAJOR;
        private final float[] data;
        private final int dim1dim2;
        private final int dim1dim2dim3;
        private final int dim1dim2dim3dim4;
        private final int dim1dim2dim3dim4dim5;
        private final int dim1dim2dim3dim4dim5dim6;
        private final int dim1dim2dim3dim4dim5dim6dim7;

        Flat(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
            super(dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8);
            data = new float[number];
            dim1dim2 = dim1*dim2;
            dim1dim2dim3 = dim1dim2*dim3;
            dim1dim2dim3dim4 = dim1dim2dim3*dim4;
            dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
            dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
            dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
        }

        Flat(int[] shape, boolean cloneShape) {
            super(shape, cloneShape);
            data = new float[number];
            dim1dim2 = dim1*dim2;
            dim1dim2dim3 = dim1dim2*dim3;
            dim1dim2dim3dim4 = dim1dim2dim3*dim4;
            dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
            dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
            dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
        }

        Flat(float[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
            super(dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8);
            data = arr;
            dim1dim2 = dim1*dim2;
            dim1dim2dim3 = dim1dim2*dim3;
            dim1dim2dim3dim4 = dim1dim2dim3*dim4;
            dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
            dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
            dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
        }

        Flat(float[] arr, int[] shape, boolean cloneShape) {
            super(shape, cloneShape);
            data = arr;
            dim1dim2 = dim1*dim2;
            dim1dim2dim3 = dim1dim2*dim3;
            dim1dim2dim3dim4 = dim1dim2dim3*dim4;
            dim1dim2dim3dim4dim5 = dim1dim2dim3dim4*dim5;
            dim1dim2dim3dim4dim5dim6 = dim1dim2dim3dim4dim5*dim6;
            dim1dim2dim3dim4dim5dim6dim7 = dim1dim2dim3dim4dim5dim6*dim7;
        }

        @Override
        public final float get(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            return data[dim1dim2dim3dim4dim5dim6dim7*i8 + dim1dim2dim3dim4dim5dim6*i7 + dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1];
        }

        @Override
        public final void set(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float value) {
            data[dim1dim2dim3dim4dim5dim6dim7*i8 + dim1dim2dim3dim4dim5dim6*i7 + dim1dim2dim3dim4dim5*i6 + dim1dim2dim3dim4*i5 + dim1dim2dim3*i4 + dim1dim2*i3 + dim1*i2 + i1] = value;
        }

        @Override
        public final int getOrder() {
            return order;
        }

        @Override
        public float[] flatten(boolean forceCopy) {
            if (! forceCopy) {
                return data;
            }
            int number = getNumber();
            float[] out = new float[number];
            System.arraycopy(data, 0, out, 0, number);
            return out;
        }
    }

    /*=======================================================================*/
    /* STRIDED LAYOUT */

    /**
     * Wrap an existing array in a 8D array of float's with given dimensions,
     * strides and offset.
     * <p>
     * This creates a 8D array of dimensions {{@code dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8}}
     * sharing (part of) the contents of {@code data} in arbitrary storage
     * order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8) = data[offset + stride1*i1 + stride2*i2 + stride3*i3 + stride4*i4 + stride5*i5 + stride6*i6 + stride7*i7 + stride8*i8]</pre>
     * with {@code arr} the returned 8D array.
     * @param data    - The array to wrap in the 8D array.
     * @param dim1    - The 1st dimension of the 8D array.
     * @param dim2    - The 2nd dimension of the 8D array.
     * @param dim3    - The 3rd dimension of the 8D array.
     * @param dim4    - The 4th dimension of the 8D array.
     * @param dim5    - The 5th dimension of the 8D array.
     * @param dim6    - The 6th dimension of the 8D array.
     * @param dim7    - The 7th dimension of the 8D array.
     * @param dim8    - The 8th dimension of the 8D array.
     * @param offset  - The offset in {@code data} of element (0,0,0,0,0,0,0,0) of
     *                  the 8D array.
     * @param stride1 - The stride along the 1st dimension.
     * @param stride2 - The stride along the 2nd dimension.
     * @param stride3 - The stride along the 3rd dimension.
     * @param stride4 - The stride along the 4th dimension.
     * @param stride5 - The stride along the 5th dimension.
     * @param stride6 - The stride along the 6th dimension.
     * @param stride7 - The stride along the 7th dimension.
     * @param stride8 - The stride along the 8th dimension.
     * @return A 8D array sharing the elements of <b>data</b>.
     */
    public static Float8D wrap(float[] data, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8,
            int offset, int stride1, int stride2, int stride3, int stride4, int stride5, int stride6, int stride7, int stride8) {
        return factory.new Strided(data, dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8, offset, stride1,stride2,stride3,stride4,stride5,stride6,stride7,stride8);
    }

    /*
     * The following inner class is defined to handle the specific case of a
     * 8D array stored in a "flat" (1D) Java array with offset and strides.
     * To instantiate such an inner class, an instance of the outer class must
     * be available (this is the purpose of the static "factory" instance).
     */
    private class Strided extends Float8D {
        private final float[] data;
        private final int order;
        private final int offset;
        private final int stride1;
        private final int stride2;
        private final int stride3;
        private final int stride4;
        private final int stride5;
        private final int stride6;
        private final int stride7;
        private final int stride8;

        Strided(float[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int offset, int stride1, int stride2, int stride3, int stride4, int stride5, int stride6, int stride7, int stride8) {
            super(dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8);
            this.data = arr;
            this.offset = offset;
            this.stride1 = stride1;
            this.stride2 = stride2;
            this.stride3 = stride3;
            this.stride4 = stride4;
            this.stride5 = stride5;
            this.stride6 = stride6;
            this.stride7 = stride7;
            this.stride8 = stride8;
            this.order = checkViewStrides(arr.length, dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8, offset, stride1,stride2,stride3,stride4,stride5,stride6,stride7,stride8);
        }

        private final int index(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            return offset + stride8*i8 + stride7*i7 + stride6*i6 + stride5*i5 + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1;
        }

        @Override
        public final float get(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            return data[index(i1,i2,i3,i4,i5,i6,i7,i8)];
        }

        @Override
        public final void set(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float value) {
            data[index(i1,i2,i3,i4,i5,i6,i7,i8)] = value;
        }

        @Override
        public final int getOrder() {
            return order;
        }

        @Override
        public float[] flatten(boolean forceCopy) {
            boolean flat = (stride1 == 1 && stride2 == dim1 && stride3 == stride2*dim2 && stride4 == stride3*dim3 && stride5 == stride4*dim4 && stride6 == stride5*dim5 && stride7 == stride6*dim6 && stride8 == stride7*dim7);
            if (flat && ! forceCopy && offset == 0) {
                return data;
            }
            float[] out;
            int number = getNumber();
            out = new float[number];
            if (flat) {
                System.arraycopy(data, offset, out, 0, number);
            } else {
                /* Must access the output in column-major order. */
                int i = -1;
                for (int i8 = 0; i8 < dim8; ++i8) {
                    for (int i7 = 0; i7 < dim7; ++i7) {
                        for (int i6 = 0; i6 < dim6; ++i6) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i4 = 0; i4 < dim4; ++i4) {
                                    for (int i3 = 0; i3 < dim3; ++i3) {
                                        for (int i2 = 0; i2 < dim2; ++i2) {
                                            for (int i1 = 0; i1 < dim1; ++i1) {
                                                out[++i] = get(i1,i2,i3,i4,i5,i6,i7,i8);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return out;
        }
    }

    /*=======================================================================*/
    /* MULTIDIMENSIONAL (8D) LAYOUT */

    /**
     * Wrap an existing 8D array of float's in a Float8D array.
     * <p>
     * More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8) = data[i8][i7][i6][i5][i4][i3][i2][i1]</pre>
     * with {@code arr} the returned 8D array.
     * @param data    - The array to wrap in the 8D array.
     * @return A 8D array sharing the elements of <b>data</b>.
     */
    public static Float8D wrap(float[][][][][][][][] data) {
        return factory.new Multi8(data);
    }

    /*
     * The following inner class is defined to handle the specific case of a
     * 8D array stored in a 8D Java array.  To instantiate such an inner class,
     * an instance of the outer class must be available (this is the purpose
     * of the static "factory" instance).
     */
    class Multi8 extends Float8D {
        private static final int order = COLUMN_MAJOR;
        private final float[][][][][][][][] data;

        protected Multi8(float[][][][][][][][] arr) {
            super(arr[0][0][0][0][0][0][0].length, arr[0][0][0][0][0][0].length, arr[0][0][0][0][0].length, arr[0][0][0][0].length, arr[0][0][0].length, arr[0][0].length, arr[0].length, arr.length);
            data = arr;
        }
        @Override
        public int getOrder() {
            return order;
        }
        @Override
        public final float get(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            return data[i8][i7][i6][i5][i4][i3][i2][i1];
        }
        @Override
        public final void set(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float value) {
            data[i8][i7][i6][i5][i4][i3][i2][i1] = value;
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
