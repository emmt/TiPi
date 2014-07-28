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
 * This interface collects some information about Java primitives.
 *
 * char is as always a problem and is considered as a non-numeric primitive type.
 */
public interface Traits {

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

    /** Is this a numeric class */
    static final boolean[] isNumeric = new boolean[]{
        true, true, true, true, true, true, false, false
    };

    /** Full names */
    static final String[] names = new String[]{
        "byte", "short", "int", "long", "float", "double", "char", "boolean"
    };

    /** Sizes */
    static final int[] sizes = new int[]{
        1, 2, 4, 8, 4, 8, 2, 1
    };

    /** Index of first element of above arrays referring to a numeric type. */
    static final int FIRST_NUMERIC = 0;

    /** Index of last element of above arrays referring to a numeric type. */
    static final int LAST_NUMERIC = 5;

    /** Unknown type. */
    static final int VOID = -1;

    /** Index for the byte type. */
    static final int BYTE = 0;

    /** Index for the short type. */
    static final int SHORT = 1;

    /** Index for the int type. */
    static final int INT = 2;

    /** Index for the long type. */
    static final int LONG = 3;

    /** Index for the float type. */
    static final int FLOAT = 4;

    /** Index for the double type. */
    static final int DOUBLE = 5;

    /** Index for the char type. */
    static final int CHAR = 6;

    /** Index for the boolean type. */
    static final int BOOLEAN = 7;

    /** Non-primitive object. */
    static final int OBJECT = 8;


    public static final int BIG_ENDIAN    = 4321;
    public static final int LITTLE_ENDIAN = 1234;
    public static final int UNKNOWN_BYTE_ORDER = -1;

    public static final int NATIVE_BYTE_ORDER = BaseUtils.getNativeByteOrder();

    /**
     * FLT_EPSILON is the minimum positive single precision floating
     * point number such that 1.0F + FLT_EPSILON != 1.0F.
     */
    public static final float FLT_EPSILON = BaseUtils.machineEpsilonFloat();

    /**
     * DBL_EPSILON is the minimum positive double precision floating
     * point number such that 1.0 + DBL_EPSILON != 1.0.
     */
    public static final double DBL_EPSILON = BaseUtils.machineEpsilonDouble();

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
