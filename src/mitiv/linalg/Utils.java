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

package mitiv.linalg;

import java.nio.ByteOrder;

public class Utils {
    /**
     * Makes this class non instantiable, but still let's others inherit from
     * it.
     */
    protected Utils() {
        throw new RuntimeException("Non instantiable");
    }

    public static final int TYPE_OPAQUE = 0;
    public static final int TYPE_CHAR = 1;
    public static final int TYPE_SHORT = 2;
    public static final int TYPE_INT = 3;
    public static final int TYPE_LONG = 4;
    public static final int TYPE_FLOAT = 5;
    public static final int TYPE_DOUBLE = 6;
    
    public static final int BIG_ENDIAN    = 4321;
    public static final int LITTLE_ENDIAN = 1234;
    public static final int UNKNOWN_BYTE_ORDER = -1;

    private static int getNativeByteOrder() {
        ByteOrder order = ByteOrder.nativeOrder();
        if (order == ByteOrder.BIG_ENDIAN) {
            return BIG_ENDIAN;
        } else if (order == ByteOrder.LITTLE_ENDIAN) {
            return LITTLE_ENDIAN;
        } else {
            return UNKNOWN_BYTE_ORDER;
        }
    }
    public static final int NATIVE_BYTE_ORDER = getNativeByteOrder();

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

    /**
     * FLT_EPSILON is the minimum positive single precision floating
     * point number such that 1.0F + FLT_EPSILON != 1.0F. 
     */
    public static final float FLT_EPSILON = machineEpsilonFloat();

    private static float machineEpsilonFloat() {
        float value = 1.0f;
        while ((1.0F + (value / 2.0F)) != 1.0F) {
            value /= 2.0F;
        }
        if (value <= 0.0F) {
            throw new RuntimeException("Failed to compute FLT_EPSILON.");
        }
        return value;
    }

    /**
     * DBL_EPSILON is the minimum positive double precision floating
     * point number such that 1.0 + DBL_EPSILON != 1.0. 
     */
    public static final double DBL_EPSILON = machineEpsilonDouble();

    private static double machineEpsilonDouble() {
        double value = 1.0;
        while ((1.0 + (value / 2.0)) != 1.0) {
            value /= 2.0;
        }
        if (value <= 0.0) {
            throw new RuntimeException("Failed to compute DBL_EPSILON.");
        }
        return value;
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
