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
import mitiv.base.mapping.IntegerFunction;
import mitiv.base.mapping.IntegerScanner;
import mitiv.random.IntegerGenerator;


/**
 * Define class for comprehensive 2-dimensional arrays of int's.
 *
 * @author Éric Thiébaut.
 */
public abstract class Integer2D extends Array2D implements IntegerArray {

    protected Integer2D(int dim1, int dim2) {
        super(dim1,dim2);
    }

    protected Integer2D(int[] shape, boolean cloneShape) {
        super(shape, cloneShape);
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
    public abstract int get(int i1, int i2);

    /**
     * Set the value at a given position.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param value - The value to store at position {@code (i1,i2)}.
     */
    public abstract void set(int i1, int i2, int value);

    /*=======================================================================*/
    /* Provide default (non-optimized, except for the loop ordering)
     * implementation of methods that can be coded solely with the "set"
     * and "get" methods. */

    @Override
    public void set(int value) {
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
    public void incr(int value) {
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
    public void decr(int value) {
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
    public void mult(int value) {
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
    public void map(IntegerFunction function) {
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
    public void set(IntegerGenerator generator) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    set(i1,i2, generator.nextInteger());
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                for (int i1 = 0; i1 < dim1; ++i1) {
                    set(i1,i2, generator.nextInteger());
                }
            }
        }
    }

    @Override
    public void scan(IntegerScanner scanner)  {
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
     * @see devel.eric.array.base.IntegerArray#flatten(boolean)
     */
    @Override
    public int[] flatten(boolean forceCopy) {
        /* Copy the elements in column-major order. */
        int[] out = new int[number];
        int i = -1;
        for (int i2 = 0; i2 < dim2; ++i2) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                out[++i] = get(i1,i2);
            }
        }
        return out;
    }

    @Override
    public int[] flatten() {
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
    private static final Integer2D factory = new Integer2D(1,1) {
        @Override
        public final int get(int i1, int i2) {
            return 0;
        }
        @Override
        public final void set(int i1, int i2, int value) {
        }
        @Override
        public final int getOrder() {
            return COLUMN_MAJOR;
        }
        @Override
        public int[] flatten(boolean forceCopy) {
            return null;
        }
    };

    /*=======================================================================*/
    /* FLAT LAYOUT */

    /**
     * Create a 2D array of int's with given dimensions.
     * <p>
     * This method creates a 2D array of int's with zero offset, contiguous
     * elements and column-major order.  All dimensions must at least 1.
     * @param dim1 - The 1st dimension of the 2D array.
     * @param dim2 - The 2nd dimension of the 2D array.
     * @return A new 2D array of int's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Integer2D create(int dim1, int dim2) {
        return factory.new Flat(dim1,dim2);
    }

    /**
     * Create a 2D array of int's with given shape.
     * <p>
     * This method creates a 2D array of int's with zero offset, contiguous
     * elements and column-major order.
     * @param shape - The list of dimensions of the 2D array (all dimensions
     *                must at least 1).  This argument is not referenced by
     *                the returned object and its contents can be modified
     *                after calling this method.
     * @return A new 2D array of int's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Integer2D create(int[] shape) {
        return factory.new Flat(shape, true);
    }

    /**
     * Create a 2D array of int's with given shape.
     * <p>
     * This method creates a 2D array of int's with zero offset, contiguous
     * elements and column-major order.
     * @param shape      - The list of dimensions of the 2D array (all
     *                     dimensions must at least 1).
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 2D array of int's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Integer2D create(int[] shape, boolean cloneShape) {
        return factory.new Flat(shape, cloneShape);
    }

    /**
     * Wrap an existing array in a 2D array of int's with given dimensions.
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
    public static Integer2D wrap(int[] data, int dim1, int dim2) {
        return factory.new Flat(data, dim1,dim2);
    }

    /**
     * Wrap an existing array in a 2D array of int's with given shape.
     * <p>
     * The returned 2D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2) = data[i1 + shape[0]*i2]</pre>
     * with {@code arr} the returned 2D array.
     * @param data - The data to wrap in the 2D array.
     * @param shape - The list of dimensions of the 2D array.  This argument is
     *                not referenced by the returned object and its contents
     *                can be modified after the call to this method.
     * @return A new 2D array of int's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Integer2D wrap(int[] data, int[] shape) {
        return factory.new Flat(data, shape, true);
    }

    /**
     * Wrap an existing array in a 2D array of int's with given shape.
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
     * @return A new 2D array of int's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Integer2D wrap(int[] data, int[] shape, boolean cloneShape) {
        return factory.new Flat(data, shape, cloneShape);
    }

    /*
     * The following inner class is defined to handle the specific case of a
     * 2D array stored in a "flat" (1D) Java array in column-major order.
     * To instantiate such an inner class, an instance of the outer class must
     * be available (this is the purpose of the static "factory" instance).
     */
    private class Flat extends Integer2D {
        private static final int order = COLUMN_MAJOR;
        private final int[] data;

        Flat(int dim1, int dim2) {
            super(dim1,dim2);
            data = new int[number];
        }

        Flat(int[] shape, boolean cloneShape) {
            super(shape, cloneShape);
            data = new int[number];
        }

        Flat(int[] arr, int dim1, int dim2) {
            super(dim1,dim2);
            data = arr;
        }

        Flat(int[] arr, int[] shape, boolean cloneShape) {
            super(shape, cloneShape);
            data = arr;
        }

        @Override
        public final int get(int i1, int i2) {
            return data[dim1*i2 + i1];
        }

        @Override
        public final void set(int i1, int i2, int value) {
            data[dim1*i2 + i1] = value;
        }

        @Override
        public final int getOrder() {
            return order;
        }

        @Override
        public int[] flatten(boolean forceCopy) {
            if (! forceCopy) {
                return data;
            }
            int number = getNumber();
            int[] out = new int[number];
            System.arraycopy(data, 0, out, 0, number);
            return out;
        }
    }

    /*=======================================================================*/
    /* STRIDED LAYOUT */

    /**
     * Wrap an existing array in a 2D array of int's with given dimensions,
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
    public static Integer2D wrap(int[] data, int dim1, int dim2,
            int offset, int stride1, int stride2) {
        return factory.new Strided(data, dim1,dim2, offset, stride1,stride2);
    }

    /*
     * The following inner class is defined to handle the specific case of a
     * 2D array stored in a "flat" (1D) Java array with offset and strides.
     * To instantiate such an inner class, an instance of the outer class must
     * be available (this is the purpose of the static "factory" instance).
     */
    private class Strided extends Integer2D {
        private final int[] data;
        private final int order;
        private final int offset;
        private final int stride1;
        private final int stride2;

        Strided(int[] arr, int dim1, int dim2, int offset, int stride1, int stride2) {
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
        public final int get(int i1, int i2) {
            return data[index(i1,i2)];
        }

        @Override
        public final void set(int i1, int i2, int value) {
            data[index(i1,i2)] = value;
        }

        @Override
        public final int getOrder() {
            return order;
        }

        @Override
        public int[] flatten(boolean forceCopy) {
            boolean flat = (stride1 == 1 && stride2 == dim1);
            if (flat && ! forceCopy && offset == 0) {
                return data;
            }
            int[] out;
            int number = getNumber();
            out = new int[number];
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
     * Wrap an existing 2D array of int's in a Integer2D array.
     * <p>
     * More specifically:
     * <pre>arr.get(i1,i2) = data[i2][i1]</pre>
     * with {@code arr} the returned 2D array.
     * @param data    - The array to wrap in the 2D array.
     * @return A 2D array sharing the elements of <b>data</b>.
     */
    public static Integer2D wrap(int[][] data) {
        return factory.new Multi2(data);
    }

    /*
     * The following inner class is defined to handle the specific case of a
     * 2D array stored in a 2D Java array.  To instantiate such an inner class,
     * an instance of the outer class must be available (this is the purpose
     * of the static "factory" instance).
     */
    class Multi2 extends Integer2D {
        private static final int order = COLUMN_MAJOR;
        private final int[][] data;

        protected Multi2(int[][] arr) {
            super(arr[0].length, arr.length);
            data = arr;
        }
        @Override
        public int getOrder() {
            return order;
        }
        @Override
        public final int get(int i1, int i2) {
            return data[i2][i1];
        }
        @Override
        public final void set(int i1, int i2, int value) {
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
