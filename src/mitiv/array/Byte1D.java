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
import mitiv.base.mapping.ByteFunction;
import mitiv.base.mapping.ByteScanner;
import mitiv.random.ByteGenerator;


/**
 * Define class for comprehensive 1-dimensional arrays of byte's.
 *
 * @author Éric Thiébaut.
 */
public abstract class Byte1D extends Array1D implements ByteArray {

    protected Byte1D(int dim1) {
        super(dim1);
    }

    protected Byte1D(int[] shape, boolean cloneShape) {
        super(shape, cloneShape);
    }

    @Override
    public final int getType() {
        return type;
    }

    /**
     * Query the value stored at a given position.
     * @param i1 - The index along the 1st dimension.
     * @return The value stored at position {@code (i1)}.
     */
    public abstract byte get(int i1);

    /**
     * Set the value at a given position.
     * @param i1 - The index along the 1st dimension.
     * @param value - The value to store at position {@code (i1)}.
     */
    public abstract void set(int i1, byte value);

    /*=======================================================================*/
    /* Provide default (non-optimized, except for the loop ordering)
     * implementation of methods that can be coded solely with the "set"
     * and "get" methods. */

    @Override
    public void set(byte value) {
        for (int i1 = 0; i1 < dim1; ++i1) {
            set(i1, value);
        }
    }

    @Override
    public void incr(byte value) {
        for (int i1 = 0; i1 < dim1; ++i1) {
            set(i1, (byte)(get(i1) + value));
        }
    }

    @Override
    public void decr(byte value) {
        for (int i1 = 0; i1 < dim1; ++i1) {
            set(i1, (byte)(get(i1) - value));
        }
    }

    @Override
    public void mult(byte value) {
        for (int i1 = 0; i1 < dim1; ++i1) {
            set(i1, (byte)(get(i1) * value));
        }
    }

    @Override
    public void map(ByteFunction function) {
        for (int i1 = 0; i1 < dim1; ++i1) {
            set(i1, function.apply(get(i1)));
        }
    }

    @Override
    public void set(ByteGenerator generator) {
        for (int i1 = 0; i1 < dim1; ++i1) {
            set(i1, generator.nextByte());
        }
    }

    @Override
    public void scan(ByteScanner scanner)  {
        scanner.initialize(get(0));
        for (int i1 = 1; i1 < dim1; ++i1) {
            scanner.update(get(i1));
        }
    }

    /* Note that the following default implementation of the "flatten" method
     * is always returning a copy of the contents whatever the value of the
     * "forceCopy" argument.
     * @see devel.eric.array.base.ByteArray#flatten(boolean)
     */
    @Override
    public byte[] flatten(boolean forceCopy) {
        /* Copy the elements in column-major order. */
        byte[] out = new byte[dim1];
        for (int i1 = 0; i1 < dim1; ++i1) {
            out[i1] = get(i1);
        }
        return out;
    }

    @Override
    public byte[] flatten() {
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
    private static final Byte1D factory = new Byte1D(1) {
        @Override
        public final byte get(int i1) {
            return 0;
        }
        @Override
        public final void set(int i1, byte value) {
        }
        @Override
        public final int getOrder() {
            return COLUMN_MAJOR;
        }
        @Override
        public byte[] flatten(boolean forceCopy) {
            return null;
        }
    };

    /*=======================================================================*/
    /* FLAT LAYOUT */

    /**
     * Create a 1D array of byte's with given dimensions.
     * <p>
     * This method creates a 1D array of byte's with zero offset, contiguous
     * elements and column-major order.  All dimensions must at least 1.
     * @param dim1 - The 1st dimension of the 1D array.
     * @return A new 1D array of byte's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Byte1D create(int dim1) {
        return factory.new Flat(dim1);
    }

    /**
     * Create a 1D array of byte's with given shape.
     * <p>
     * This method creates a 1D array of byte's with zero offset, contiguous
     * elements and column-major order.
     * @param shape - The list of dimensions of the 1D array (all dimensions
     *                must at least 1).  This argument is not referenced by
     *                the returned object and its contents can be modified
     *                after calling this method.
     * @return A new 1D array of byte's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Byte1D create(int[] shape) {
        return factory.new Flat(shape, true);
    }

    /**
     * Create a 1D array of byte's with given shape.
     * <p>
     * This method creates a 1D array of byte's with zero offset, contiguous
     * elements and column-major order.
     * @param shape      - The list of dimensions of the 1D array (all
     *                     dimensions must at least 1).
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 1D array of byte's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Byte1D create(int[] shape, boolean cloneShape) {
        return factory.new Flat(shape, cloneShape);
    }

    /**
     * Wrap an existing array in a 1D array of byte's with given dimensions.
     * <p>
     * The returned 1D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1) = data[i1]</pre>
     * with {@code arr} the returned 1D array.
     * @param data - The data to wrap in the 1D array.
     * @param dim1 - The 1st dimension of the 1D array.
     * @return A 1D array sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Byte1D wrap(byte[] data, int dim1) {
        return factory.new Flat(data, dim1);
    }

    /**
     * Wrap an existing array in a 1D array of byte's with given shape.
     * <p>
     * The returned 1D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1) = data[i1]</pre>
     * with {@code arr} the returned 1D array.
     * @param data - The data to wrap in the 1D array.
     * @param shape - The list of dimensions of the 1D array.  This argument is
     *                not referenced by the returned object and its contents
     *                can be modified after the call to this method.
     * @return A new 1D array of byte's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Byte1D wrap(byte[] data, int[] shape) {
        return factory.new Flat(data, shape, true);
    }

    /**
     * Wrap an existing array in a 1D array of byte's with given shape.
     * <p>
     * The returned 1D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1) = data[i1]</pre>
     * with {@code arr} the returned 1D array.
     * @param data       - The data to wrap in the 1D array.
     * @param shape      - The list of dimensions of the 1D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 1D array of byte's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Byte1D wrap(byte[] data, int[] shape, boolean cloneShape) {
        return factory.new Flat(data, shape, cloneShape);
    }

    /*
     * The following inner class is defined to handle the specific case of a
     * 1D array stored in a "flat" (1D) Java array in column-major order.
     * To instantiate such an inner class, an instance of the outer class must
     * be available (this is the purpose of the static "factory" instance).
     */
    private class Flat extends Byte1D {
        private static final int order = COLUMN_MAJOR;
        private final byte[] data;

        Flat(int dim1) {
            super(dim1);
            data = new byte[dim1];
        }

        Flat(int[] shape, boolean cloneShape) {
            super(shape, cloneShape);
            data = new byte[dim1];
        }

        Flat(byte[] arr, int dim1) {
            super(dim1);
            data = arr;
        }

        Flat(byte[] arr, int[] shape, boolean cloneShape) {
            super(shape, cloneShape);
            data = arr;
        }

        @Override
        public final byte get(int i1) {
            return data[i1];
        }

        @Override
        public final void set(int i1, byte value) {
            data[i1] = value;
        }

        @Override
        public final int getOrder() {
            return order;
        }

        @Override
        public byte[] flatten(boolean forceCopy) {
            if (! forceCopy) {
                return data;
            }
            int number = getNumber();
            byte[] out = new byte[dim1];
            System.arraycopy(data, 0, out, 0, number);
            return out;
        }
    }

    /*=======================================================================*/
    /* STRIDED LAYOUT */

    /**
     * Wrap an existing array in a 1D array of byte's with given dimensions,
     * strides and offset.
     * <p>
     * This creates a 1D array of dimensions {{@code dim1}}
     * sharing (part of) the contents of {@code data} in arbitrary storage
     * order.  More specifically:
     * <pre>arr.get(i1) = data[offset + stride1*i1]</pre>
     * with {@code arr} the returned 1D array.
     * @param data    - The array to wrap in the 1D array.
     * @param dim1    - The 1st dimension of the 1D array.
     * @param offset  - The offset in {@code data} of element (0) of
     *                  the 1D array.
     * @param stride1 - The stride along the 1st dimension.
     * @return A 1D array sharing the elements of <b>data</b>.
     */
    public static Byte1D wrap(byte[] data, int dim1,
            int offset, int stride1) {
        return factory.new Strided(data, dim1, offset, stride1);
    }

    /*
     * The following inner class is defined to handle the specific case of a
     * 1D array stored in a "flat" (1D) Java array with offset and strides.
     * To instantiate such an inner class, an instance of the outer class must
     * be available (this is the purpose of the static "factory" instance).
     */
    private class Strided extends Byte1D {
        private final byte[] data;
        private final int order;
        private final int offset;
        private final int stride1;

        Strided(byte[] arr, int dim1, int offset, int stride1) {
            super(dim1);
            this.data = arr;
            this.offset = offset;
            this.stride1 = stride1;
            this.order = checkViewStrides(arr.length, dim1, offset, stride1);
        }

        private final int index(int i1) {
            return offset + stride1*i1;
        }

        @Override
        public final byte get(int i1) {
            return data[index(i1)];
        }

        @Override
        public final void set(int i1, byte value) {
            data[index(i1)] = value;
        }

        @Override
        public final int getOrder() {
            return order;
        }

        @Override
        public byte[] flatten(boolean forceCopy) {
            boolean flat = (stride1 == 1);
            if (flat && ! forceCopy && offset == 0) {
                return data;
            }
            byte[] out;
            int number = getNumber();
            out = new byte[dim1];
            if (flat) {
                System.arraycopy(data, offset, out, 0, number);
            } else {
                /* Must access the output in column-major order. */
                int i = -1;
                for (int i1 = 0; i1 < dim1; ++i1) {
                    out[++i] = get(i1);
                }
            }
            return out;
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
