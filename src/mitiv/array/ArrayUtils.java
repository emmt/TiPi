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

import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.base.indexing.Range;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.NonConformableArrayException;

public class ArrayUtils {

    /*=======================================================================*/
    /* CONVERSION */

    /**
     * Convert an array of {@code byte}'s into an array of {@code byte}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to byte's
     *         from those of {@code src}.
     */
    public static byte[] toByte(byte[] src) {
        return src;
    }

    /**
     * Convert an array of {@code short}'s into an array of {@code byte}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to byte's
     *         from those of {@code src}.
     */
    public static byte[] toByte(short[] src) {
        int number = src.length;
        byte[] dst = new byte[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (byte)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code int}'s into an array of {@code byte}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to byte's
     *         from those of {@code src}.
     */
    public static byte[] toByte(int[] src) {
        int number = src.length;
        byte[] dst = new byte[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (byte)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code long}'s into an array of {@code byte}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to byte's
     *         from those of {@code src}.
     */
    public static byte[] toByte(long[] src) {
        int number = src.length;
        byte[] dst = new byte[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (byte)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code float}'s into an array of {@code byte}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to byte's
     *         from those of {@code src}.
     */
    public static byte[] toByte(float[] src) {
        int number = src.length;
        byte[] dst = new byte[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (byte)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code double}'s into an array of {@code byte}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to byte's
     *         from those of {@code src}.
     */
    public static byte[] toByte(double[] src) {
        int number = src.length;
        byte[] dst = new byte[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (byte)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code byte}'s into an array of {@code short}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to short's
     *         from those of {@code src}.
     */
    public static short[] toShort(byte[] src) {
        int number = src.length;
        short[] dst = new short[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (short)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code short}'s into an array of {@code short}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to short's
     *         from those of {@code src}.
     */
    public static short[] toShort(short[] src) {
        return src;
    }

    /**
     * Convert an array of {@code int}'s into an array of {@code short}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to short's
     *         from those of {@code src}.
     */
    public static short[] toShort(int[] src) {
        int number = src.length;
        short[] dst = new short[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (short)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code long}'s into an array of {@code short}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to short's
     *         from those of {@code src}.
     */
    public static short[] toShort(long[] src) {
        int number = src.length;
        short[] dst = new short[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (short)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code float}'s into an array of {@code short}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to short's
     *         from those of {@code src}.
     */
    public static short[] toShort(float[] src) {
        int number = src.length;
        short[] dst = new short[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (short)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code double}'s into an array of {@code short}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to short's
     *         from those of {@code src}.
     */
    public static short[] toShort(double[] src) {
        int number = src.length;
        short[] dst = new short[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (short)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code byte}'s into an array of {@code int}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to int's
     *         from those of {@code src}.
     */
    public static int[] toInt(byte[] src) {
        int number = src.length;
        int[] dst = new int[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (int)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code short}'s into an array of {@code int}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to int's
     *         from those of {@code src}.
     */
    public static int[] toInt(short[] src) {
        int number = src.length;
        int[] dst = new int[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (int)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code int}'s into an array of {@code int}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to int's
     *         from those of {@code src}.
     */
    public static int[] toInt(int[] src) {
        return src;
    }

    /**
     * Convert an array of {@code long}'s into an array of {@code int}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to int's
     *         from those of {@code src}.
     */
    public static int[] toInt(long[] src) {
        int number = src.length;
        int[] dst = new int[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (int)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code float}'s into an array of {@code int}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to int's
     *         from those of {@code src}.
     */
    public static int[] toInt(float[] src) {
        int number = src.length;
        int[] dst = new int[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (int)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code double}'s into an array of {@code int}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to int's
     *         from those of {@code src}.
     */
    public static int[] toInt(double[] src) {
        int number = src.length;
        int[] dst = new int[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (int)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code byte}'s into an array of {@code long}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to long's
     *         from those of {@code src}.
     */
    public static long[] toLong(byte[] src) {
        int number = src.length;
        long[] dst = new long[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (long)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code short}'s into an array of {@code long}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to long's
     *         from those of {@code src}.
     */
    public static long[] toLong(short[] src) {
        int number = src.length;
        long[] dst = new long[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (long)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code int}'s into an array of {@code long}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to long's
     *         from those of {@code src}.
     */
    public static long[] toLong(int[] src) {
        int number = src.length;
        long[] dst = new long[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (long)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code long}'s into an array of {@code long}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to long's
     *         from those of {@code src}.
     */
    public static long[] toLong(long[] src) {
        return src;
    }

    /**
     * Convert an array of {@code float}'s into an array of {@code long}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to long's
     *         from those of {@code src}.
     */
    public static long[] toLong(float[] src) {
        int number = src.length;
        long[] dst = new long[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (long)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code double}'s into an array of {@code long}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to long's
     *         from those of {@code src}.
     */
    public static long[] toLong(double[] src) {
        int number = src.length;
        long[] dst = new long[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (long)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code byte}'s into an array of {@code float}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to float's
     *         from those of {@code src}.
     */
    public static float[] toFloat(byte[] src) {
        int number = src.length;
        float[] dst = new float[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (float)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code short}'s into an array of {@code float}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to float's
     *         from those of {@code src}.
     */
    public static float[] toFloat(short[] src) {
        int number = src.length;
        float[] dst = new float[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (float)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code int}'s into an array of {@code float}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to float's
     *         from those of {@code src}.
     */
    public static float[] toFloat(int[] src) {
        int number = src.length;
        float[] dst = new float[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (float)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code long}'s into an array of {@code float}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to float's
     *         from those of {@code src}.
     */
    public static float[] toFloat(long[] src) {
        int number = src.length;
        float[] dst = new float[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (float)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code float}'s into an array of {@code float}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to float's
     *         from those of {@code src}.
     */
    public static float[] toFloat(float[] src) {
        return src;
    }

    /**
     * Convert an array of {@code double}'s into an array of {@code float}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to float's
     *         from those of {@code src}.
     */
    public static float[] toFloat(double[] src) {
        int number = src.length;
        float[] dst = new float[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (float)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code byte}'s into an array of {@code double}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to double's
     *         from those of {@code src}.
     */
    public static double[] toDouble(byte[] src) {
        int number = src.length;
        double[] dst = new double[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (double)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code short}'s into an array of {@code double}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to double's
     *         from those of {@code src}.
     */
    public static double[] toDouble(short[] src) {
        int number = src.length;
        double[] dst = new double[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (double)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code int}'s into an array of {@code double}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to double's
     *         from those of {@code src}.
     */
    public static double[] toDouble(int[] src) {
        int number = src.length;
        double[] dst = new double[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (double)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code long}'s into an array of {@code double}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to double's
     *         from those of {@code src}.
     */
    public static double[] toDouble(long[] src) {
        int number = src.length;
        double[] dst = new double[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (double)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code float}'s into an array of {@code double}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to double's
     *         from those of {@code src}.
     */
    public static double[] toDouble(float[] src) {
        int number = src.length;
        double[] dst = new double[number];
        for (int j = 0; j < number; ++j) {
            dst[j] = (double)src[j];
        }
        return dst;
    }

    /**
     * Convert an array of {@code double}'s into an array of {@code double}'.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     * </p>
     * @param src - The source array.
     * @return An array whose values has been converted to double's
     *         from those of {@code src}.
     */
    public static double[] toDouble(double[] src) {
        return src;
    }


    /*=======================================================================*/
    /* PAD/CROP AN ARRAY */

    /**
     * Zero-pad a shaped array.
     * <p>
     * Zero-padding consists in adding zeros around an array to build a larger
     * array.
     * </p><p>
     * There must be as many dimensions in the result as in the input array
     * and all dimensions must be greater or equal the corresponding dimension
     * in the input array.  The operation is lazy: if no padding is needed
     * (that is, if the shapes are the same), the input array is returned.
     * Otherwise, the contents of the input array is pasted into a larger
     * output array approximately at the geometric center of this former
     * array.  More specifically, the number of zeros at the beginning of a
     * given dimension is equal to:
     * <pre>
     * (outDim/2) - (inpDim/2)
     * </pre>
     * assuming integer arithmetic and where {@code outDim} and {@code inpDim}
     * are the respective lengths of the given dimension in the output and
     * input arrays.
     * </p>
     * @param array - The input array.
     * @param shape - The shape of the result.
     * @return A shaped array of the given shape.
     */
    public static ShapedArray pad(ShapedArray array, Shape shape) {
        return pad(array, shape, null, 0.0);
    }

    /**
     * Pad a shaped array with a specific value.
     * <p>
     * This function behaves as {@link #pad(ShapedArray,Shape)} except that
     * the padding elements are set to the given value.
     * </p>
     * @param array - The input array.
     * @param shape - The shape of the result.
     * @param value - The value of the padding elements (it is silently
     *                converted to the element type of the output array which
     *                is the same as that of the input array).
     *
     * @return A shaped array of the given shape.
     */
    public static ShapedArray pad(ShapedArray array, Shape shape,
            Double value) {
        return pad(array, shape, null, value);
    }

    /**
     * Zero-pad a shaped array with given offsets.
     * <p>
     * This function behaves as {@link #pad(ShapedArray,Shape)} except that
     * specific offsets are given.
     * </p>
     * @param array  - The input array.
     * @param shape  - The shape of the result.
     * @param offset - The offsets along each dimenions of the input array
     *                 relative to the result.  If {@code null}, the contents
     *                 of the array is approximately at the geometric center
     *                 of the result.
     *
     * @return A shaped array of the given shape.
     */
    public static ShapedArray pad(ShapedArray array, Shape shape,
            int[] offset) {
        return pad(array, shape, offset, 0.0);
    }

    /**
     * Pad a shaped array with a given value and given offsets.
     * <p>
     * Padding consists in adding elements with a given value around an array
     * to build a larger array.
     * </p><p>
     * There must be as many dimensions in the result as in the input array
     * and all dimensions must be greater or equal the corresponding dimension
     * in the input array.  The operation is lazy: if no padding is needed
     * (that is, if the shapes are the same), the input array is returned.
     * Otherwise, the contents of the input array is pasted into a larger
     * output array approximately at the geometric center of this former
     * array.  More specifically, the number of elements added at the
     * beginning of a given dimension is equal to:
     * <pre>
     * (outDim/2) - (inpDim/2)
     * </pre>
     * assuming integer arithmetic and where {@code outDim} and {@code inpDim}
     * are the respective lengths of the given dimension in the output and
     * input arrays.
     * </p>
     * @param array  - The input array.
     * @param shape  - The shape of the result.
     * @param offset - The offsets along each dimension of the input array
     *                 relative to the result.  If {@code null}, the contents
     *                 of the array is approximately at the geometric center
     *                 of the result.
     * @param value  - The value of the padding elements (it is silently
     *                 converted to the element type of the output array which
     *                 is the same as that of the input array).
     *
     * @return A shaped array of the given shape.
     */
    public static ShapedArray pad(ShapedArray array,
            Shape shape, int[] offset, double value) {

        /* Get bounds of the region of interest. */
        Range[] range = getROI(shape, array.getShape(), offset);
        if (range == null) {
            /* Nothing has to be done. */
            return array;
        }

        /* Create the output array and fill it with the padding value. */
        int rank = range.length;
        int type = array.getType();
        ShapedArray result = ArrayFactory.create(type, shape);
        switch (type) {
        case Traits.BYTE:
            ((ByteArray)result).fill((byte)value);
            break;
        case Traits.SHORT:
            ((ShortArray)result).fill((short)value);
            break;
        case Traits.INT:
            ((IntArray)result).fill((int)value);
            break;
        case Traits.LONG:
            ((LongArray)result).fill((long)value);
            break;
        case Traits.FLOAT:
            ((FloatArray)result).fill((float)value);
            break;
        case Traits.DOUBLE:
            ((DoubleArray)result).fill((double)value);
            break;
        default:
            throw new IllegalTypeException();
        }

        /* Copy input into output. */
        ShapedArray roi;
        switch (rank) {
        case 1:
            roi = ((Array1D)result).view(range[0]);
            break;
        case 2:
            roi = ((Array2D)result).view(range[0], range[1]);
            break;
        case 3:
            roi = ((Array3D)result).view(range[0], range[1], range[2]);
            break;
        case 4:
            roi = ((Array4D)result).view(range[0], range[1], range[2], range[3]);
            break;
        case 5:
            roi = ((Array5D)result).view(range[0], range[1], range[2], range[3], range[4]);
            break;
        case 6:
            roi = ((Array6D)result).view(range[0], range[1], range[2], range[3], range[4], range[5]);
            break;
        case 7:
            roi = ((Array7D)result).view(range[0], range[1], range[2], range[3], range[4], range[5], range[6]);
            break;
        case 8:
            roi = ((Array8D)result).view(range[0], range[1], range[2], range[3], range[4], range[5], range[6], range[7]);
            break;
        case 9:
            roi = ((Array9D)result).view(range[0], range[1], range[2], range[3], range[4], range[5], range[6], range[7], range[8]);
            break;
        default:
            throw new IllegalArgumentException("Unsupported rank.");
        }
        roi.assign(array);
        return result;
    }

    /**
     * Crop the central part of a shaped array.
     * <p>
     * This function returns the central region of the input array.  More
     * specifically, the first element taken along a given dimension is at
     * offset:
     * <pre>
     * (inpDim/2) - (outDim/2)
     * </pre>
     * assuming integer arithmetic and where {@code outDim} and {@code inpDim}
     * are the respective lengths of the considerd dimension in the output and
     * input arrays.
     * </p><p>
     * The operation is lazzy in the sense that it returns either the input
     * array (if the cropped region is the same as the input array) or a view
     * inside the input array.
     * </p>
     * @param array  - The input array.
     * @param shape  - The shape of the result.
     *
     * @return A shaped array of the given shape.
     */
    public static ShapedArray crop(ShapedArray array, Shape shape) {
        return crop(array, shape, null);
    }

    /**
     * Crop a specific part of a shaped array.
     * <p>
     * Padding consists in adding elements with a given value around an array
     * to build a larger array.
     * </p><p>
     * The operation is lazzy in the sense that it returns either the input
     * array (if the cropped region is the same as the input array) or a view
     * inside the input array.
     * </p>
     * @param array  - The input array.
     * @param shape  - The shape of the result.
     * @param offset - The offsets along each dimensions of the cropped region
     *                 relative to the input array.  If {@code null}, the
     *                 cropped region corresponds to approximately the central
     *                 part of the input array.
     *
     * @return A shaped array of the given shape.
     */
    public static ShapedArray crop(ShapedArray array, Shape shape,
            int[] offset) {
        /* Get bounds of the region of interest. */
        Range[] range = getROI(array.getShape(), shape, offset);
        if (range == null) {
            /* Nothing has to be done. */
            return array;
        }

        /* Return a view to the ROI. */
        switch (range.length) {
        case 1:
            return ((Array1D)array).view(range[0]);
        case 2:
            return ((Array2D)array).view(range[0], range[1]);
        case 3:
            return ((Array3D)array).view(range[0], range[1], range[2]);
        case 4:
            return ((Array4D)array).view(range[0], range[1], range[2], range[3]);
        case 5:
            return ((Array5D)array).view(range[0], range[1], range[2], range[3], range[4]);
        case 6:
            return ((Array6D)array).view(range[0], range[1], range[2], range[3], range[4], range[5]);
        case 7:
            return ((Array7D)array).view(range[0], range[1], range[2], range[3], range[4], range[5], range[6]);
        case 8:
            return ((Array8D)array).view(range[0], range[1], range[2], range[3], range[4], range[5], range[6], range[7]);
        case 9:
            return ((Array9D)array).view(range[0], range[1], range[2], range[3], range[4], range[5], range[6], range[7], range[8]);
        default:
            throw new IllegalArgumentException("Unsupported rank.");
        }
    }


    private static Range[] getROI(Shape large, Shape small, int[] offset) {
        int rank = large.rank();
        if (small.rank() != rank) {
            throw new NonConformableArrayException("Not same rank.");
        }
        Boolean nothing = true;
        Boolean outOfBounds = false;
        if (offset == null) {
            for (int k = 0; k < rank; ++k) {
                int largeDim = large.dimension(k);
                int smallDim = small.dimension(k);
                if (smallDim > largeDim) {
                    outOfBounds = true;
                    break;
                }
                if (smallDim != largeDim) {
                    nothing = false;
                }
            }
        } else {
            if (offset.length != rank) {
                throw new NonConformableArrayException("Bad number of offsets.");
            }
            for (int k = 0; k < rank; ++k) {
                int largeDim = large.dimension(k);
                int smallDim = small.dimension(k);
                if (offset[k] < 0 || smallDim + offset[k] > largeDim) {
                    outOfBounds = true;
                    break;
                }
                if (smallDim != largeDim) {
                    nothing = false;
                }
            }
        }
        if (outOfBounds) {
            throw new ArrayIndexOutOfBoundsException("Out of bounds region of interest.");
        }
        if (nothing) {
            return null;
        }
        Range[] range = new Range[rank];
        if (offset == null) {
            for (int k = 0; k < rank; ++k) {
                int largeDim = large.dimension(k);
                int smallDim = small.dimension(k);
                int first = (largeDim/2) - (smallDim/2);
                int last = first + smallDim - 1;
                range[k] = new Range(first, last, 1);
            }
        } else {
            for (int k = 0; k < rank; ++k) {
                int smallDim = small.dimension(k);
                int first = offset[k];
                int last = first + smallDim - 1;
                range[k] = new Range(first, last, 1);
            }
        }
        return range;
    }


    /*=======================================================================*/
    /* ROLLING OF DIMENSIONS */

    /**
     * Roll the dimensions of a shaped array.
     * <p>
     * This is the same as {@link #roll(ShapedArray, int[])} with offsets
     * equal to half the lenght of each dimensions.
     * </p>
     * @param arr - The input array.
     * @return A view with the contents of the input array but rolled along
     *         the dimensions of the input array by the given offsets.  Note
     *         that the result shares its contents with the input array.
     */
    public static ShapedArray roll(ShapedArray arr) {
        Shape shape = arr.getShape();
        int rank = shape.rank();
        int[] off = new int[rank];
        boolean nothing = true;
        for (int k = 0; k < rank; ++k) {
            int dim = shape.dimension(k);
            off[k] = -(dim/2);
            if (dim != 1) {
                nothing = false;
            }
        }
        if (nothing) {
            return arr;
        }
        return roll(arr, off);
    }

    /**
     * Roll the dimensions of a shaped array with given offsets.
     * <p>
     * This static method rolls the contents of the input array along its
     * dimensions.  For a mono-dimensional array of length {@code dim}, this
     * is equivalent to something like:
     * <pre>
     * dst[j] = src[(j - off)%dim]
     * </pre>
     * where {@code src} is the input array, {@code dst} is the result,
     * {@code off} is the offset and assuming that the modulo operator
     * returns a result wrapped in the range [0:{@code dim}-1].
     * </p><p>
     * The operation is lazy: if no rolling is needed (that is, if the shapes
     * are the same), the input array is returned.
     * </p>
     * @param arr - The input array.
     * @param off - The offsets to apply along each dimensions.
     * @return A shaped array with the contents of the input array but rolled
     *         along the dimensions of the input array by the given offsets.
     *         Note that the result shares its contents with the input array.
     */
    public static ShapedArray roll(ShapedArray arr, int off[]) {
        Shape shape = arr.getShape();
        int rank = shape.rank();
        if (off.length != rank) {
            throw new IllegalArgumentException("Range mismatch.");
        }
        boolean nothing = true;
        int[][] sel = new int[rank][];
        for (int k = 0; k < rank; ++k) {
            int dim = shape.dimension(k);
            int offset;
            if (dim == 1) {
                offset = 0;
            } else {
                offset = (dim + (off[k]%dim))%dim;
            }
            if (offset != 0) {
                int[] index = new int[dim];
                for (int j = 0; j < dim; ++j) {
                    index[j] = (j + offset)%dim;
                }
                sel[k] = index;
                nothing = false;
            }
        }
        if (nothing) {
            return arr;
        }
        switch (rank) {
        case 1:
            return ((Array1D)arr).view(sel[0]);
        case 2:
            return ((Array2D)arr).view(sel[0], sel[1]);
        case 3:
            return ((Array3D)arr).view(sel[0], sel[1], sel[2]);
        case 4:
            return ((Array4D)arr).view(sel[0], sel[1], sel[2], sel[3]);
        case 5:
            return ((Array5D)arr).view(sel[0], sel[1], sel[2], sel[3], sel[4]);
        case 6:
            return ((Array6D)arr).view(sel[0], sel[1], sel[2], sel[3], sel[4], sel[5]);
        case 7:
            return ((Array7D)arr).view(sel[0], sel[1], sel[2], sel[3], sel[4], sel[5], sel[6]);
        case 8:
            return ((Array8D)arr).view(sel[0], sel[1], sel[2], sel[3], sel[4], sel[5], sel[6], sel[7]);
        case 9:
            return ((Array9D)arr).view(sel[0], sel[1], sel[2], sel[3], sel[4], sel[5], sel[6], sel[7], sel[8]);
        default:
            throw new IllegalArgumentException("Unsupported rank.");
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
