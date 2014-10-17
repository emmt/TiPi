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

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import mitiv.base.Shaped;
import mitiv.base.indexing.Range;
import mitiv.linalg.shaped.DoubleShapedVector;

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
    /* SELECTION */

    private static void badStepDirection() {
        throw new IllegalArgumentException("Bad step direction.");
    }

    private static void badEndPoints() {
        throw new IndexOutOfBoundsException("Range endpoints outside bounds.");
    }

    /**
     * Select indices from a stridden dimension.
     * @param offset - The offset for the dimension of interest.
     * @param stride - The stepping along the dimension of interest.
     * @param dim    - The number of elements along the dimension of interest.
     * @param sel    - The indices to select.
     * @return A sub-list of indices: {@code offset + stride*sel[j]}.
     * @throws IndexOutOfBoundsException if any of {@code sel[j]} is less than
     *         zero or greater or equal {@code dim}.
     */
    public static int[] select(int offset, int stride, int dim, int[] sel) {
        int length = sel.length;
        int[] result = new int[length];
        for (int j = 0; j < length; ++j) {
            int index = sel[j];
            if (index < 0 || index >= dim) {
                throw new IndexOutOfBoundsException("Selection index outside bounds.");
            }
            result[j] = offset + stride*index;
        }
        return result;
    }

    /**
     * Select a sub-list of values by range.
     * @param src - The initial list of values.
     * @param rng - The range to select.
     * @return A sub-list of values from {@code src}.
     */
    public static int[] select(int[] src, Range rng) {
        if (rng == null) {
            return src;
        }
        int length = src.length;
        int first = rng.getFirst(length);
        int last = rng.getLast(length);
        int step = rng.step;
        if (first <= last) {
            if (step < 0) {
                badStepDirection();
            }
            if (step == 0) {
                step = 1;
            }
            if (first == 0 && last == length - 1 && step == 1) {
                return src;
            }
            if (first < 0 || last >= length) {
                badEndPoints();
            }
            length = (last - first + 1)/step;
        } else {
            if (step > 0) {
                badStepDirection();
            }
            if (step == 0) {
                step = -1;
            }
            if (last < 0 || first >= length) {
                badEndPoints();
            }
            length = (first - last + 1)/(-step);
        }
        int[] idx = new int[length];
        for (int j = 0, k = first; j < length; ++j, k += step) {
            idx[j] = src[k];
        }
        return idx;
    }

    /**
     * Select a sub-list of values by indices.
     * @param src - The initial list of values.
     * @param sel - The list of indices to select.
     * @return A sub-list of values from {@code src}.
     */
    public static int[] select(int[] src, int[] sel) {
        if (sel == null) {
            return src;
        }
        int length = sel.length;
        int[] idx = new int[length];
        for (int j = 0; j < length; ++j) {
            idx[j] = src[sel[j]];
        }
        return idx;
    }


    /*=======================================================================*/
    /* COLORS */

    /**
     * Convert red, green and blue levels in a gray level.
     * @param red   - The red level.
     * @param green - The green level.
     * @param blue  - The blue level.
     * @return The gray level.
     */
    public static double colorToGrey(double red, double green, double blue) {
        return 0.2126*red + 0.7152*green + 0.0722*blue;
    }

    /**
     * Convert red, green and blue levels in a gray level.
     * @param red   - The red level.
     * @param green - The green level.
     * @param blue  - The blue level.
     * @return The gray level.
     */
    public static float colorToGrey(float red, float green, float blue) {
        return 0.2126F*red + 0.7152F*green + 0.0722F*blue;
    }

    /**
     * Convert red, green and blue levels in a gray level.
     * @param red   - The red level.
     * @param green - The green level.
     * @param blue  - The blue level.
     * @return The gray level.
     */
    public static int colorToGrey(int red, int green, int blue) {
        return Math.round(0.2126F*red + 0.7152F*green + 0.0722F*blue);
    }

    /* COLOR MODELS
     * Below is how to decode an ARGB pixel value:
     * <pre>
     *   int argb = image.getRGB(i,j);
     *   int red   = (argb)&0xFF;
     *   int green = (argb>>8)&0xFF;
     *   int blue  = (argb>>16)&0xFF;
     *   int alpha = (argb>>24)&0xFF;
     * </pre>
     */

    //private static int[] grayTable = makeGrayTable();
    //private static int[] makeGrayTable() {
    //    int[] color = new int[256];
    //    int OPAQUE = 255;
    //    for (int gray = 0; gray < 256; ++gray) {
    //        color[gray] = (OPAQUE<<24)|(gray<<16)|(gray<<8)|gray;
    //    }
    //    return color;
    //}

    /*=======================================================================*/
    /* COLOR MODELS */

    public final static int RED   = 0;
    public final static int GREEN = 1;
    public final static int BLUE  = 2;
    public final static int ALPHA = 3;
    public final static int GRAY  = 4;
    public final static int RGB   = 5;
    public final static int RGBA  = 6;

    /*=======================================================================*/
    /* FUNCTIONS FOR DOUBLE TYPE */

    /**
     * Convert a BufferedImage into a DoubleArray.
     * @param image      - The input BufferedImage.
     * @param colorModel - The color model for the result.
     * @return A DoubleArray object with shape {width,height} for a
     * grayscale image, with shape {depth,width,height} for a RGB or RGBA
     * image (depth = 3 or 4 respectively).
     */
    public static DoubleArray imageAsDouble(BufferedImage image, int colorModel) {
        final double OPAQUE = 255.0;
        int height = image.getHeight();
        int width = image.getWidth();
        int depth;
        switch (colorModel) {
        case RED:
        case GREEN:
        case BLUE:
        case ALPHA:
        case GRAY:
            depth = 1;
            break;
        case RGB:
            depth = 3;
            break;
        case RGBA:
            depth = 4;
            break;
        default:
            throw new IllegalArgumentException("Unknown color model");
        }
        WritableRaster raster = image.getRaster();
        final int nbands = raster.getNumBands();
        double[] out = new double[depth*width*height];
        int[] pixval = new int[nbands];
        if (nbands == 1) {
            /* Assume input is greyscale image. */
            if (colorModel == GRAY || colorModel == RED || colorModel == GREEN || colorModel == BLUE) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = pixval[0];
                    }
                }
            } else if (colorModel == ALPHA) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        out[x + y*width] = OPAQUE;
                    }
                }
            } else if (colorModel == RGB) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 3*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        double level = pixval[0];
                        out[k]   = level;
                        out[k+1] = level;
                        out[k+2] = level;
                    }
                }
            } else {
                /* Output must be RGBA. */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 4*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        double level = pixval[0];
                        out[k]   = level;
                        out[k+1] = level;
                        out[k+2] = level;
                        out[k+3] = OPAQUE;
                    }
                }
            }
        } else if (nbands == 3) {
            /* Assume input is RGB image. */
            if (colorModel == GRAY) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = colorToGrey((double)pixval[0], (double)pixval[1], (double)pixval[2]);
                    }
                }
            } else if (colorModel == RGB) {
                /* Flatten the RGB image. */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 3*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        out[k]   = pixval[0];
                        out[k+1] = pixval[1];
                        out[k+2] = pixval[2];
                    }
                }
            } else if (colorModel == RGBA) {
                /* Flatten the RGBA image. */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 4*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        out[k]   = pixval[0];
                        out[k+1] = pixval[1];
                        out[k+2] = pixval[2];
                        out[k+3] = OPAQUE;
                    }
                }
            } else if (colorModel == ALPHA) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        out[x + y*width] = OPAQUE;
                    }
                }
            } else {
                int band;
                if (colorModel == RED) {
                    band = 0;
                } else if (colorModel == GREEN) {
                    band = 1;
                } else /* Output must be BLUE channel. */ {
                    band = 2;
                }
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = pixval[band];
                    }
                }
            }
        } else if (nbands == 4) {
            /* Assume input is RGBA image. */
            if (colorModel == GRAY) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = colorToGrey((double)pixval[0], (double)pixval[1], (double)pixval[2]);
                    }
                }
            } else if (colorModel == RGB) {
                /* Flatten the RGB image (ignoring the ALPHA channel). */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 3*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        out[k]   = pixval[0];
                        out[k+1] = pixval[1];
                        out[k+2] = pixval[2];
                    }
                }
            } else if (colorModel == RGBA) {
                /* Flatten the RGBA image. */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 4*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        out[k]   = pixval[0];
                        out[k+1] = pixval[1];
                        out[k+2] = pixval[2];
                        out[k+3] = pixval[3];
                    }
                }
            } else {
                int band;
                if (colorModel == RED) {
                    band = 0;
                } else if (colorModel == GREEN) {
                    band = 1;
                } else if (colorModel == BLUE) {
                    band = 2;
                } else /* Output must be ALPHA channel. */ {
                    band = 3;
                }
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = pixval[band];
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Unknown pixel format");
        }
        if (depth == 1) {
            return Double2D.wrap(out, width, height);
        } else {
            return Double3D.wrap(out, depth, width, height);
        }
    }

    /**
     * Convert a BufferedImage into a DoubleArray.
     * @param image      - The input BufferedImage.
     * @return A DoubleArray object with shape {width,height} for a
     * grayscale image, with shape {depth,width,height} for a RGB or RGBA
     * image (depth = 3 or 4 respectively).
     */
    public static DoubleArray imageAsDouble(BufferedImage image) {
        switch (image.getRaster().getNumBands()) {
        case 1:
            return imageAsDouble(image, GRAY);
        case 3:
            return imageAsDouble(image, RGB);
        case 4:
            return imageAsDouble(image, RGBA);
        default:
            throw new IllegalArgumentException("Unknown pixel format");
        }
    }

    private static int[] getImageDimensions(Shaped img) {
        int rank = img.getRank();
        if (rank == 2) {
            return new int[]{1, img.getDimension(0), img.getDimension(1)};
        } else if (rank == 3) {
            int depth = img.getDimension(0);
            int width = img.getDimension(1);
            int height = img.getDimension(2);
            if (depth == 3 || depth == 4) {
                return new int[]{depth, width, height};
            }
        }
        throw new IllegalArgumentException("Conversion to image is only allowed for WIDTHxHEIGHT, 3xWIDTHxHEIGHT or 4xWIDTHxHEIGHT arrays.");
    }

    public static void writeImage(DoubleArray img, String fileName) {
        BufferedImage buf = doubleAsBuffered(img);
        writeImage(buf, fileName);
    }

    public static void writeImage(DoubleArray img, String fileName, ScalingOptions scaleOpts) {
        BufferedImage buf = doubleAsBuffered(img, scaleOpts);
        writeImage(buf, fileName);
    }

    public static void writeImage(DoubleShapedVector img, String fileName) {
        BufferedImage buf = doubleAsBuffered(img);
        writeImage(buf, fileName);
    }

    public static void writeImage(DoubleShapedVector img, String fileName, ScalingOptions scaleOpts) {
        BufferedImage buf = doubleAsBuffered(img, scaleOpts);
        writeImage(buf, fileName);
    }

    // FIXME: deal with NaN and INFINITE
    public static void writeImage(double[] arr, int depth, int width, int height, String fileName) {
        BufferedImage buf = doubleAsBuffered(arr, depth, width, height);
        writeImage(buf, fileName);
    }

    public static void writeImage(double[] arr, int depth, int width, int height, String fileName,
            ScalingOptions scaleOpts) {
        BufferedImage buf = doubleAsBuffered(arr, depth, width, height, scaleOpts);
        writeImage(buf, fileName);
    }

    private static int checkDimensions(double[] arr, int depth, int width, int height) {
        int number;
        if ((depth != 1 && depth != 3 && depth != 4)) {
            throw new IllegalArgumentException("Only gray, RGB or RGBA images supported.");
        }
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Bad image size.");
        }
        number = depth*width*height;
        if (arr == null || number > arr.length) {
            throw new IllegalArgumentException("More pixel values than array elements.");
        }
        return number;
    }

    /*=======================================================================*/
    /* FUNCTIONS TO BUFFERED */

    public static BufferedImage doubleAsBuffered(DoubleArray img) {
        int[] dims = getImageDimensions(img);
        return doubleAsBuffered(img.flatten(), dims[0], dims[1], dims[2]);
    }

    public static BufferedImage doubleAsBuffered(DoubleArray img, ScalingOptions scaleOpts) {
        int[] dims = getImageDimensions(img);
        return doubleAsBuffered(img.flatten(), dims[0], dims[1], dims[2], scaleOpts);
    }

    public static BufferedImage doubleAsBuffered(DoubleShapedVector img) {
        int[] dims = getImageDimensions(img);
        return doubleAsBuffered(img.getData(), dims[0], dims[1], dims[2]);
    }

    public static BufferedImage doubleAsBuffered(DoubleShapedVector img, ScalingOptions scaleOpts) {
        int[] dims = getImageDimensions(img);
        return doubleAsBuffered(img.getData(), dims[0], dims[1], dims[2], scaleOpts);
    }

    public static BufferedImage doubleAsBuffered(double[] arr, int depth, int width, int height) {
        return doubleAsBuffered(arr, depth, width, height, new ScalingOptions());
    }

    public static BufferedImage doubleAsBuffered(double[] arr, int depth, int width, int height,
            ScalingOptions scaleOpts) {
        int number = checkDimensions(arr, depth, width, height);
        double[] sf = scaleOpts.getScaling(arr, 0, number, 0, 255);
        double scale = sf[0];
        double bias = sf[1];
        double factor = 1.0/scale;
        BufferedImage buf;
        if (depth == 1) {
            buf = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            WritableRaster raster = buf.getRaster();
            int[] rgb = new int[3];
            for (int y = 0; y < height; ++y){
                for (int x = 0; x < width; ++x){
                    int level = Math.min(255, Math.max(0, (int)Math.round((arr[x + y*width] - bias)*factor)));
                    rgb[0] = level;
                    rgb[1] = level;
                    rgb[2] = level;
                    raster.setPixel(x, y, rgb);
                }
            }
        } else if (depth == 3) {
            buf = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            WritableRaster raster = buf.getRaster();
            int[] rgb = new int[3];
            for (int y = 0; y < height; ++y){
                for (int x = 0; x < width; ++x){
                    int i = (x + y*width)*3;
                    rgb[0] = Math.min(255, Math.max(0, (int)Math.round((arr[i]     - bias)*factor)));
                    rgb[1] = Math.min(255, Math.max(0, (int)Math.round((arr[i + 1] - bias)*factor)));
                    rgb[2] = Math.min(255, Math.max(0, (int)Math.round((arr[i + 2] - bias)*factor)));
                    raster.setPixel(x, y, rgb);
                }
            }
        } else if (depth == 4) {
            buf = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            WritableRaster raster = buf.getRaster();
            int[] argb = new int[4];
            for (int y = 0; y < height; ++y){
                for (int x = 0; x < width; ++x){
                    int i = (x + y*width)*4;
                    argb[0] = Math.min(255, Math.max(0, (int)Math.round((arr[i]     - bias)*factor)));
                    argb[1] = Math.min(255, Math.max(0, (int)Math.round((arr[i + 1] - bias)*factor)));
                    argb[2] = Math.min(255, Math.max(0, (int)Math.round((arr[i + 2] - bias)*factor)));
                    argb[3] = Math.min(255, Math.max(0, (int)Math.round((arr[i + 3] - bias)*factor)));
                    raster.setPixel(x, y, argb);
                }
            }
        } else {
            throw new IllegalArgumentException("Unexpected image depth.");
        }
        return buf;
    }

    private static void writeImage(BufferedImage buf, String fileName){
        String type = "png"; // FIXME: guess from extension
        try {
            ImageIO.write(buf, type, new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*=======================================================================*/
    /* FUNCTIONS FOR FLOAT TYPE */

    // FIXME: to do
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
