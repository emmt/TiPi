/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014-2016 the MiTiV project, http://mitiv.univ-lyon1.fr/
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

package mitiv.utils;

import java.lang.reflect.Array;
import java.util.Locale;

import mitiv.array.ArrayFactory;
import mitiv.array.ShapedArray;
import mitiv.base.ArrayDescriptor;
import mitiv.base.Traits;
import mitiv.exception.IllegalTypeException;
import mitiv.linalg.shaped.ShapedVector;

/**
 * This class provides static method for exploiting Java reflection to
 * build shaped arrays, shaped vectors, etc.
 *
 * @author Éric.
 *
 */
public class ArrayReflection {

    /**
     * Get the "deep" component type of an object.
     *
     * <p>
     * This static method travels all the levels (including none if argument is
     * not an array) of the argument and returns the class of the deepest
     * component.
     *
     * @param o
     *            - The object (must not be null).
     * @return The component type at the deepest level of the argument.
     */
    public static Class<?> deepComponentType(Object o) {
        Class<?> c = o.getClass();
        while (c.isArray()) {
            c = c.getComponentType();
        }
        return c;
    }

    /**
     * Get the depth of an object.
     *
     * @param o
     *            - The object (must not be null).
     * @return The number of dimensions of an array, 0 otherwise.
     */
    public static int getDepth(Object o) {
        int depth = 0;
        Class<?> c = o.getClass();
        while (c.isArray()) {
            c = c.getComponentType();
            ++depth;
        }
        return depth;
    }

    /**
     * Count the total number of components of an object.
     *
     * @param obj
     *            - The object.
     * @return 0 for a null object, 1 for a non-array object, the total number
     *         of components for an array.
     */
    public static long countElements(Object obj) {
        if (obj == null) {
            return 0;
        }
        Class<?> sub = obj.getClass().getComponentType();
        if (sub == null) {
            // non-array counts for 1
            return 1;
        }
        int len = Array.getLength(obj);
        if (sub.isArray()) {
            // Object is array of array, recursively count the number of components.
            long count = 0;
            for (int i = 0; i < len; ++i) {
                count += countElements(Array.get(obj, i));
            }
            return count;
        } else {
            // Object is a flat (1-D) array.
            return len;
        }
    }

    /**
     * Recursively copy the contents of a multi-dimensional array into a flat
     * array.
     *
     * @param dst
     *            - The destination, a flat array.
     * @param off
     *            - The index of the first element to write in the destination.
     * @param src
     *            - The source (ignored if non-array or null).
     * @return The updated offset, that is the index of the next element to write
     *         in the destination.
     */
    public static int recursiveCopy(Object dst, int off, Object src) {
        if (src != null) {
            Class<?> sub = src.getClass().getComponentType();
            if (sub == null) {
                // Source object is not an array, copy a single element.
                Array.set(dst, off++, src);
            } else {
                // Source object is an array.
                int len = Array.getLength(src);
                if (sub.isArray()) {
                    // Source object is an array of array.
                    for (int i = 0; i < len; ++i) {
                        off = recursiveCopy(dst, off, Array.get(src, i));
                    }
                } else {
                    // Source object is a flat array.
                    System.arraycopy(src, 0, dst, off, len);
                    off += len;
                }
            }
        }
        return off;
    }

    /**
     * Get all the components of an object as a flat array.
     * @param obj - The source object.
     * @return A flat array which may be the object itself if it is already a flat array. FIXME:
     */
    public static Object flatten(Object obj) {
        Object arr = null;
        long count = countElements(obj);
        int length = (int)count;
        if (length != count) {
            throw new IndexOutOfBoundsException("Too many components to store in a flat array");
        }
        arr = Array.newInstance(deepComponentType(obj), length);
        recursiveCopy(arr, 0, obj);
        return arr;
    }

    /* We have to treat the primitive types specifically. */

    /**
     * Make a single element array with a {@code boolean} value.
     *
     * @param value
     *            - The value of the element of the returned array.
     * @return A single element array of type {@code boolean[]}.
     */
    public static Object flatten(boolean value) {
        return new boolean[]{value};
    }

    /**
     * Make a single element array with a {@code char} value.
     *
     * @param value
     *            - The value of the element of the returned array.
     * @return A single element array of type {@code char[]}.
     */
    public static Object flatten(char value) {
        return new char[]{value};
    }

    /**
     * Make a single element array with a {@code byte} value.
     *
     * @param value
     *            - The value of the element of the returned array.
     * @return A single element array of type {@code byte[]}.
     */
    public static Object flatten(byte value) {
        return new byte[]{value};
    }

    /**
     * Make a single element array with a {@code short} value.
     *
     * @param value
     *            - The value of the element of the returned array.
     * @return A single element array of type {@code short[]}.
     */
    public static Object flatten(short value) {
        return new short[]{value};
    }

    /**
     * Make a single element array with an {@code int} value.
     *
     * @param value
     *            - The value of the element of the returned array.
     * @return A single element array of type {@code int[]}.
     */
    public static Object flatten(int value) {
        return new int[]{value};
    }

    /**
     * Make a single element array with a {@code long} value.
     *
     * @param value
     *            - The value of the element of the returned array.
     * @return A single element array of type {@code long[]}.
     */
    public static Object flatten(long value) {
        return new long[]{value};
    }

    /**
     * Make a single element array with a {@code float} value.
     *
     * @param value
     *            - The value of the element of the returned array.
     * @return A single element array of type {@code float[]}.
     */
    public static Object flatten(float value) {
        return new float[]{value};
    }

    /**
     * Make a single element array with a {@code double} value.
     *
     * @param value
     *            - The value of the element of the returned array.
     * @return A single element array of type {@code double[]}.
     */
    public static Object flatten(double value) {
        return new double[]{value};
    }

    /**
     * Check an array is rectangular (all components recursively have
     * the same dimensions, throws an exception otherwise.
     */
    static private void checkLengths(Object arr, int[] dims, int k) {
        if (k >= 1) {
            int n = dims[k];
            int p = dims[k - 1];
            for (int i = 0; i < n; ++i) {
                Object sub = Array.get(arr, i);
                if (sub == null) {
                    emptyDimension();
                }
                if (Array.getLength(sub) != p) {
                    nonRectangular();
                }
                if (k >= 2) {
                    checkLengths(sub, dims, k - 1);
                }
            }
        }
    }

    /**
     * Attempt to make an array descriptor from any object.
     *
     * <p>
     * This methods retrieves all information of the argument type and
     * dimensions and make sure it is suitable for being interpreted as a shaped
     * array or vector. The argument must be a scalar or an array (with any
     * number of dimensions) of a numerical primitive type. If it is a
     * multi-dimensional array it must be rectangular.
     *
     * @param obj
     *            - The input object.
     * @return An array descriptor.
     * @throws IllegalTypeException
     *             The type of the components of the argument is not a numerical
     *             primitive type.
     * @throws IllegalArgumentException
     *             The argument is not a rectangular array or a scalar of
     *             primitive type.
     */
    public static ArrayDescriptor makeArrayDescriptor(Object obj) {
        Class<?> c = obj.getClass();
        int rank = 0;
        int[] dims = null;
        while (c.isArray()) {
            c = c.getComponentType();
            ++rank;
        }
        if (rank == 0) {
            dims = null;
        } else {
            /* Fetch all dimensions. */
            dims = new int[rank];
            int k = rank;
            Object arr = obj;
            while (true) {
                dims[--k] = Array.getLength(arr);
                if (k <= 0) {
                    break;
                }
                arr = Array.get(arr, 0);
                if (arr == null) {
                    emptyDimension();
                }
            }
            /* Check whether the multi-dimensional array is rectangular. */
            checkLengths(obj, dims, rank - 1);

        }
        int type;
        if (c.equals(byte.class)) {
            type = Traits.BYTE;
        } else if (c.equals(short.class)) {
            type = Traits.SHORT;
        } else if (c.equals(int.class)) {
            type = Traits.INT;
        } else if (c.equals(long.class)) {
            type = Traits.LONG;
        } else if (c.equals(float.class)) {
            type = Traits.FLOAT;
        } else if (c.equals(double.class)) {
            type = Traits.DOUBLE;
        } else {
            throw new IllegalTypeException("Only numerical primitive types are supported");
        }
        return new ArrayDescriptor(type, dims);
    }

    //public ShapedVector makeShapedVector(Object obj, ShapedVectorSpace space) {
    //    ShapedVector vec = null;
    //    return vec;
    //}

    public ShapedArray makeShapedArray(ShapedArray arr) {
        return arr;
    }

    public ShapedArray makeShapedArray(ShapedVector vec) {
        return vec.asShapedArray();
    }

    public ShapedArray makeShapedArray(Object obj) {
        ArrayDescriptor descr = makeArrayDescriptor(obj);
        Object data = flatten(obj); // FIXME: it may not be needed to make a copy
        switch (descr.getType()) {
        case Traits.BYTE:
            return ArrayFactory.wrap((byte[])data, descr.getShape());
        case Traits.SHORT:
            return ArrayFactory.wrap((short[])data, descr.getShape());
        case Traits.INT:
            return ArrayFactory.wrap((int[])data, descr.getShape());
        case Traits.LONG:
            return ArrayFactory.wrap((long[])data, descr.getShape());
        case Traits.FLOAT:
            return ArrayFactory.wrap((float[])data, descr.getShape());
        case Traits.DOUBLE:
            return ArrayFactory.wrap((double[])data, descr.getShape());
        default:
            throw new IllegalTypeException("Only numerical primitive types are supported");
        }
    }

    private static void emptyDimension() {
        throw new IllegalArgumentException("Arrays with empty dimension(s) are not supported");
    }

    private static void nonRectangular() {
        throw new IllegalArgumentException("Only rectangular arrays are supported");
    }

    public static void main(String[] args) {
        // Switch to "US" locale to avoid problems with number formats.
        Locale.setDefault(Locale.US);
        //new Timer();
        int n1 = 7;
        int n2 = 8;
        int n3 = 9;
        int[][][] arr = new int[n3][n2][];
        for (int i3 = 0; i3 < n3; ++i3) {
            for (int i2 = 0; i2 < n2; ++i2) {
                arr[i3][i2] = new int[n1];
            }
        }
        for (int i3 = 0, i = 0; i3 < n3; ++i3) {
            for (int i2 = 0; i2 < n2; ++i2) {
                for (int i1 = 0; i1 < n1; ++i1) {
                    arr[i3][i2][i1] = i++;
                }
            }
        }
        //arr[n3-1][n2-1] = new int[n1+1]; // make it non-rectangular
        String name = arr.getClass().getName();

        //System.out.println(Void.TYPE);
        System.out.println("isArray: " + (arr.getClass().isArray() ? "true" : "false"));
        System.out.format("rank: %d\n", Array.getLength(arr));
        System.out.println(deepComponentType(arr));
        System.out.println(name);
        System.out.println(name.length());
        ArrayDescriptor tmp = makeArrayDescriptor(arr);
        System.out.format("type = %s, rank = %d, dims = {",
                Traits.nameOf(tmp.getType()), tmp.getRank());
        for (int k = 0; k < tmp.getRank(); ++k) {
            System.out.format((k == 0 ? "%d" : ",%d"), tmp.getDimension(k));
        }
        System.out.format("}\n");
        int[] a = (int[])flatten(arr);
        int nerrs = 0;
        for (int i3 = 0, i = 0; i3 < n3; ++i3) {
            for (int i2 = 0; i2 < n2; ++i2) {
                for (int i1 = 0; i1 < n1; ++i1) {
                    if (arr[i3][i2][i1] != a[i++]) {
                        ++nerrs;
                    }
                }
            }
        }
        System.out.format("# of errors: %d\n", nerrs);
        float[] x = (float[])Array.newInstance(float.class, 1);
        x[0] = (float)3.9;
        System.out.format("x[0]: %g\n", x[0]);

        x = (float[])flatten(new float[]{5});
        System.out.format("x[0]: %g\n", x[0]);

        long value = 11;
        Object o = flatten(value);
        System.out.format("# of components: %d\n", Array.getLength(o));
        System.out.format("is array: %b\n", o.getClass().isArray());
        System.out.format("type of components: %s\n", o.getClass().getComponentType().toString());
        long[] b;
        //b = (long[])flatten(new long[]{value});
        b = (long[])flatten(value);
        System.out.format("# of components: %d\n", b.length);
        System.out.format("b[0]: %d\n", b[0]);

    }

}

