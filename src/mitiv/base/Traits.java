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

import java.nio.ByteOrder;

/**
 * This interface collects some information about Java primitives.
 */
public class Traits {

    /**
     * Makes this class non instantiable, but still let's others inherit from
     * it.
     */
    protected Traits() {
        throw new RuntimeException("Non instantiable");
    }

    /** Suffixes used for the classnames for primitive arrays. */
    static final char[] suffixes = new char[]{
            'B', 'S', 'I', 'J', 'F', 'D', 'C', 'Z'
    };

    /** Classes of the primitives.
     * These should be in widening order for the numeric types.
     */
    @SuppressWarnings("rawtypes")
    static final Class[] classes = new Class[]{
            byte.class, short.class, int.class,
            long.class, float.class, double.class,
            char.class, boolean.class
    };

    /**
     * Check whether a type identifier corresponds to numerical type.
     *
     * <p>
     * {@code char} is as always a problem and is considered as a non-numeric primitive type.
     *
     * @param type - Numerical type identifier.
     *
     * @return A boolean value.
     */
    static final public boolean isNumeric(int type) {
        return (BYTE <= type && type <= DOUBLE);
    }

    /**
     * Get name of type.
     *
     * @param type - Numerical type identifier.
     *
     * @return A type name ("" for an unknown type).
     */
    public static final String nameOf(int type) {
        if (BYTE <= type && type <= BOOLEAN) {
            return names[type];
        } else if (type == VOID) {
            return "void";
        } else if (type == OBJECT) {
            return "Object";
        } else {
            return "";
        }
    }

    private static final String[] names = new String[]{
            "byte", "short", "int", "long", "float", "double", "char", "boolean"
    };

    /**
     * Get size of type.
     *
     * @param type - Numerical type identifier.
     *
     * @return A number of bytes (zero for an unknown type).
     */
    public static final int sizeOf(int type) {
        if (BYTE <= type && type <= BOOLEAN) {
            return sizes[type];
        } else {
            return 0;
        }
    }

    private static final int[] sizes = new int[]{
            1, 2, 4, 8, 4, 8, 2, 1
    };

    /** Index of first element of above arrays referring to a numeric type. */
    public static final int FIRST_NUMERIC = 0;

    /** Index of last element of above arrays referring to a numeric type. */
    public static final int LAST_NUMERIC = 5;

    /** Unknown type. */
    public static final int VOID = -1;

    /** Index for the byte type. */
    public static final int BYTE = 0;

    /** Index for the short type. */
    public static final int SHORT = 1;

    /** Index for the int type. */
    public static final int INT = 2;

    /** Index for the long type. */
    public static final int LONG = 3;

    /** Index for the float type. */
    public static final int FLOAT = 4;

    /** Index for the double type. */
    public static final int DOUBLE = 5;

    /** Index for the char type. */
    public static final int CHAR = 6;

    /** Index for the boolean type. */
    public static final int BOOLEAN = 7;

    /** Non-primitive object. */
    public static final int OBJECT = 8;


    public static final int BIG_ENDIAN    = 0x04030201;
    public static final int LITTLE_ENDIAN = 0x01020304;
    public static final int UNKNOWN_BYTE_ORDER = -1;

    public static final int NATIVE_BYTE_ORDER = getNativeByteOrder();

    /**
     * FLT_EPSILON is the minimum positive single precision floating
     * point number such that 1.0F + FLT_EPSILON != 1.0F.
     */
    public static final float FLT_EPSILON = machineEpsilonFloat();

    /**
     * DBL_EPSILON is the minimum positive double precision floating
     * point number such that 1.0 + DBL_EPSILON != 1.0.
     */
    public static final double DBL_EPSILON = machineEpsilonDouble();

    private final static int getNativeByteOrder() {
        ByteOrder order = ByteOrder.nativeOrder();
        if (order == ByteOrder.BIG_ENDIAN) {
            return BIG_ENDIAN;
        } else if (order == ByteOrder.LITTLE_ENDIAN) {
            return LITTLE_ENDIAN;
        } else {
            return UNKNOWN_BYTE_ORDER;
        }
    }

    private final static float machineEpsilonFloat() {
        float value = 1.0f;
        while ((1.0F + (value / 2.0F)) != 1.0F) {
            value /= 2.0F;
        }
        if (value <= 0.0F) {
            throw new RuntimeException("Failed to compute FLT_EPSILON");
        }
        return value;
    }

    final static double machineEpsilonDouble() {
        double value = 1.0;
        while ((1.0 + (value / 2.0)) != 1.0) {
            value /= 2.0;
        }
        if (value <= 0.0) {
            throw new RuntimeException("Failed to compute DBL_EPSILON");
        }
        return value;
    }

    public static void main(String[] args) {
        System.out.println("FLT_EPSILON = " + FLT_EPSILON);
        System.out.println("DBL_EPSILON = " + DBL_EPSILON);
        String name;
        if (NATIVE_BYTE_ORDER == BIG_ENDIAN) {
            name = "BIG_ENDIAN";
        } else if (NATIVE_BYTE_ORDER == LITTLE_ENDIAN) {
            name = "LITTLE_ENDIAN";
        } else {
            name = "UNKNOWN_BYTE_ORDER";
        }
        System.out.println("This machine byte order: " + name);
        System.out.flush();
    }

}
