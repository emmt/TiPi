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
import mitiv.base.mapping.DoubleFunction;
import mitiv.base.mapping.DoubleScanner;
import mitiv.random.DoubleGenerator;


/**
 * Define class for comprehensive 2-dimensional arrays of double's.
 *
 * @author Éric Thiébaut.
 */
public abstract class Double2D extends Array2D implements DoubleArray {

    protected Double2D(int dim1, int dim2) {
        super(dim1,dim2);
    }

    protected Double2D(int[] shape, boolean cloneShape) {
        super(shape, cloneShape);
    }

    protected Double2D(int[] shape) {
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
     * @return The value stored at position {@code (i1,i2)}.
     */
    public abstract double get(int i1, int i2);

    /**
     * Set the value at a given position.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param value - The value to store at position {@code (i1,i2)}.
     */
    public abstract void set(int i1, int i2, double value);

    /*=======================================================================*/
    /* Provide default (non-optimized, except for the loop ordering)
     * implementation of methods that can be coded solely with the "set"
     * and "get" methods. */

    @Override
    public void set(double value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, value);
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, value);
                }
            }
        }
    }

    @Override
    public void incr(double value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, get(i1,i2) + value);
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, get(i1,i2) + value);
                }
            }
        }
    }

    @Override
    public void decr(double value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, get(i1,i2) - value);
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, get(i1,i2) - value);
                }
            }
        }
    }

    @Override
    public void mult(double value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, get(i1,i2) * value);
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, get(i1,i2) * value);
                }
            }
        }
    }

    @Override
    public void map(DoubleFunction function) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, function.apply(get(i1,i2)));
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, function.apply(get(i1,i2)));
                }
            }
        }
    }

    @Override
    public void set(DoubleGenerator generator) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, generator.nextDouble());
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, generator.nextDouble());
                }
            }
        }
    }

    @Override
    public void scan(DoubleScanner scanner)  {
        boolean skip = true;
        scanner.initialize(get(0,0));
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    if (skip) skip = false; else scanner.update(get(i1,i2));
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    if (skip) skip = false; else scanner.update(get(i1,i2));
                }
            }
        }
    }

    /* Note that the following default implementation of the "flatten" method
     * is always returning a copy of the contents whatever the value of the
     * "forceCopy" argument.
     * @see devel.eric.array.base.DoubleArray#flatten(boolean)
     */
    @Override
    public double[] flatten(boolean forceCopy) {
        /* Copy the elements in column-major order. */
        double[] out = new double[number];
        int i = -1;
        for (int i2 = 0; i2 < dim2; ++i2) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                out[++i] = get(i1,i2);
            }
        }
        return out;
    }

    @Override
    public double[] flatten() {
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
    private static final Double2D factory = new Double2D(1,1) {
        @Override
        public final double get(int i1, int i2) {
            return 0.0;
        }
        @Override
        public final void set(int i1, int i2, double value) {
        }
        @Override
        public final int getOrder() {
            return COLUMN_MAJOR;
        }
        @Override
        public double[] flatten(boolean forceCopy) {
            return null;
        }
    };

    /*=======================================================================*/
    /* FLAT LAYOUT */

    /**
     * Create a 2D array of double's with given dimensions.
     * <p>
     * This method creates a 2D array of double's with zero offset, contiguous
     * elements and column-major order.  All dimensions must at least 1.
     * @param dim1 - The 1st dimension of the 2D array.
     * @param dim2 - The 2nd dimension of the 2D array.
     * @return A new 2D array of double's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Double2D create(int dim1, int dim2) {
        return factory.new Flat(dim1,dim2);
    }

    /**
     * Create a 2D array of double's with given shape.
     * <p>
     * This method creates a 2D array of double's with zero offset, contiguous
     * elements and column-major order.
     * @param shape - The list of dimensions of the 2D array (all dimensions
     *                must at least 1).  This argument is not referenced by
     *                the returned object and its contents can be modified
     *                after calling this method.
     * @return A new 2D array of double's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Double2D create(int[] shape) {
        return factory.new Flat(shape, true);
    }

    /**
     * Create a 2D array of double's with given shape.
     * <p>
     * This method creates a 2D array of double's with zero offset, contiguous
     * elements and column-major order.
     * @param shape      - The list of dimensions of the 2D array (all
     *                     dimensions must at least 1).
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 2D array of double's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Double2D create(int[] shape, boolean cloneShape) {
        return factory.new Flat(shape, cloneShape);
    }

    /**
     * Wrap an existing array in a 2D array of double's with given dimensions.
     * <p>
     * The returned 2D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2) = data[i1 + dim1*i2]</pre>
     * with {@code arr} the returned 2D array.
     * @param data - The data to wrap in the 2D array.
     * @param dim1 - The 1st dimension of the 2D array.
     * @param dim2 - The 2nd dimension of the 2D array.
     * @return A 2D array sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Double2D wrap(double[] data, int dim1, int dim2) {
        return factory.new Flat(data, dim1,dim2);
    }

    /**
     * Wrap an existing array in a 2D array of double's with given shape.
     * <p>
     * The returned 2D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2) = data[i1 + shape[0]*i2]</pre>
     * with {@code arr} the returned 2D array.
     * @param data - The data to wrap in the 2D array.
     * @param shape - The list of dimensions of the 2D array.  This argument is
     *                not referenced by the returned object and its contents
     *                can be modified after the call to this method.
     * @return A new 2D array of double's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Double2D wrap(double[] data, int[] shape) {
        return factory.new Flat(data, shape, true);
    }

    /**
     * Wrap an existing array in a 2D array of double's with given shape.
     * <p>
     * The returned 2D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2) = data[i1 + shape[0]*i2]</pre>
     * with {@code arr} the returned 2D array.
     * @param data       - The data to wrap in the 2D array.
     * @param shape      - The list of dimensions of the 2D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 2D array of double's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Double2D wrap(double[] data, int[] shape, boolean cloneShape) {
        return factory.new Flat(data, shape, cloneShape);
    }

    /*
     * The following inner class is defined to handle the specific case of a
     * 2D array stored in a "flat" (1D) Java array in column-major order.
     * To instantiate such an inner class, an instance of the outer class must
     * be available (this is the purpose of the static "factory" instance).
     */
    private class Flat extends Double2D {
        private static final int order = COLUMN_MAJOR;
        private final double[] data;

        Flat(int dim1, int dim2) {
            super(dim1,dim2);
            data = new double[number];
        }

        Flat(int[] shape, boolean cloneShape) {
            super(shape, cloneShape);
            data = new double[number];
        }

        Flat(double[] arr, int dim1, int dim2) {
            super(dim1,dim2);
            data = arr;
        }

        Flat(double[] arr, int[] shape, boolean cloneShape) {
            super(shape, cloneShape);
            data = arr;
        }

        @Override
        public final double get(int i1, int i2) {
            return data[dim1*i2 + i1];
        }

        @Override
        public final void set(int i1, int i2, double value) {
            data[dim1*i2 + i1] = value;
        }

        @Override
        public final int getOrder() {
            return order;
        }

        @Override
        public double[] flatten(boolean forceCopy) {
            if (! forceCopy) {
                return data;
            }
            int number = getNumber();
            double[] out = new double[number];
            System.arraycopy(data, 0, out, 0, number);
            return out;
        }
    }

    /*=======================================================================*/
    /* STRIDED LAYOUT */

    /**
     * Wrap an existing array in a 2D array of double's with given dimensions,
     * strides and offset.
     * <p>
     * This creates a 2D array of dimensions {{@code dim1,dim2}}
     * sharing (part of) the contents of {@code data} in arbitrary storage
     * order.  More specifically:
     * <pre>arr.get(i1,i2) = data[offset + stride1*i1 + stride2*i2]</pre>
     * with {@code arr} the returned 2D array.
     * @param data    - The array to wrap in the 2D array.
     * @param dim1    - The 1st dimension of the 2D array.
     * @param dim2    - The 2nd dimension of the 2D array.
     * @param offset  - The offset in {@code data} of element (0,0) of
     *                  the 2D array.
     * @param stride1 - The stride along the 1st dimension.
     * @param stride2 - The stride along the 2nd dimension.
     * @return A 2D array sharing the elements of <b>data</b>.
     */
    public static Double2D wrap(double[] data, int dim1, int dim2,
            int offset, int stride1, int stride2) {
        return factory.new Strided(data, dim1,dim2, offset, stride1,stride2);
    }

    /*
     * The following inner class is defined to handle the specific case of a
     * 2D array stored in a "flat" (1D) Java array with offset and strides.
     * To instantiate such an inner class, an instance of the outer class must
     * be available (this is the purpose of the static "factory" instance).
     */
    private class Strided extends Double2D {
        private final double[] data;
        private final int order;
        private final int offset;
        private final int stride1;
        private final int stride2;

        Strided(double[] arr, int dim1, int dim2, int offset, int stride1, int stride2) {
            super(dim1,dim2);
            this.data = arr;
            this.offset = offset;
            this.stride1 = stride1;
            this.stride2 = stride2;
            this.order = checkViewStrides(arr.length, dim1,dim2, offset, stride1,stride2);
        }

        private final int index(int i1, int i2) {
            return offset + stride2*i2 + stride1*i1;
        }

        @Override
        public final double get(int i1, int i2) {
            return data[index(i1,i2)];
        }

        @Override
        public final void set(int i1, int i2, double value) {
            data[index(i1,i2)] = value;
        }

        @Override
        public final int getOrder() {
            return order;
        }

        @Override
        public double[] flatten(boolean forceCopy) {
            boolean flat = (stride1 == 1 && stride2 == dim1);
            if (flat && ! forceCopy && offset == 0) {
                return data;
            }
            double[] out;
            int number = getNumber();
            out = new double[number];
            if (flat) {
                System.arraycopy(data, offset, out, 0, number);
            } else {
                /* Must access the output in column-major order. */
                int i = -1;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i1 = 0; i1 < dim1; ++i1) {
                        out[++i] = get(i1,i2);
                    }
                }
            }
            return out;
        }
    }

    /*=======================================================================*/
    /* MULTIDIMENSIONAL (2D) LAYOUT */

    /**
     * Wrap an existing 2D array of double's in a Double2D array.
     * <p>
     * More specifically:
     * <pre>arr.get(i1,i2) = data[i2][i1]</pre>
     * with {@code arr} the returned 2D array.
     * @param data    - The array to wrap in the 2D array.
     * @return A 2D array sharing the elements of <b>data</b>.
     */
    public static Double2D wrap(double[][] data) {
        return factory.new Multi2(data);
    }

    /*
     * The following inner class is defined to handle the specific case of a
     * 2D array stored in a 2D Java array.  To instantiate such an inner class,
     * an instance of the outer class must be available (this is the purpose
     * of the static "factory" instance).
     */
    class Multi2 extends Double2D {
        private static final int order = COLUMN_MAJOR;
        private final double[][] data;

        protected Multi2(double[][] arr) {
            super(arr[0].length, arr.length);
            data = arr;
        }
        @Override
        public int getOrder() {
            return order;
        }
        @Override
        public final double get(int i1, int i2) {
            return data[i2][i1];
        }
        @Override
        public final void set(int i1, int i2, double value) {
            data[i2][i1] = value;
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
