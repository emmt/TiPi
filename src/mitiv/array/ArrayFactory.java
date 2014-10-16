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
import mitiv.base.Shaped;
import mitiv.base.Traits;


/**
 * Conversion functions.
 *
 * @author Éric Thiébaut & Jonathan Léger.
 */
public abstract class ArrayFactory implements Shaped {
    /**
     * This class is not instantiable.
     */
    protected ArrayFactory() {}

    /**
     * Convert a ShapedArray into a ByteArray.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     *
     * @param src - The source array.
     * @return A ByteArray whose values has been converted into byte's
     *         from those of {@code src}.
     */
    public ByteArray toByte(ShapedArray src) {
        int srcType = src.getType();
        if (src.getType() == Traits.BYTE) {
            return (ByteArray)src;
        }
        int number = src.getNumber();
        byte[] out = new byte[number];
        switch (srcType) {
        case Traits.SHORT:
            {
                short[] inp = ((ShortArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (byte)inp[j];
                }
            }
            break;
        case Traits.INT:
            {
                int[] inp = ((IntArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (byte)inp[j];
                }
            }
            break;
        case Traits.LONG:
            {
                long[] inp = ((LongArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (byte)inp[j];
                }
            }
            break;
        case Traits.FLOAT:
            {
                float[] inp = ((FloatArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (byte)inp[j];
                }
            }
            break;
        case Traits.DOUBLE:
            {
                double[] inp = ((DoubleArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (byte)inp[j];
                }
            }
            break;
        default:
            throw new IllegalArgumentException("unexpected type (BUG)");
        }
        return wrap(out, src.getShape());
    }

    /**
     * Convert a ShapedArray into a ShortArray.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     *
     * @param src - The source array.
     * @return A ShortArray whose values has been converted into short's
     *         from those of {@code src}.
     */
    public ShortArray toShort(ShapedArray src) {
        int srcType = src.getType();
        if (src.getType() == Traits.SHORT) {
            return (ShortArray)src;
        }
        int number = src.getNumber();
        short[] out = new short[number];
        switch (srcType) {
        case Traits.BYTE:
            {
                byte[] inp = ((ByteArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (short)inp[j];
                }
            }
            break;
        case Traits.INT:
            {
                int[] inp = ((IntArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (short)inp[j];
                }
            }
            break;
        case Traits.LONG:
            {
                long[] inp = ((LongArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (short)inp[j];
                }
            }
            break;
        case Traits.FLOAT:
            {
                float[] inp = ((FloatArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (short)inp[j];
                }
            }
            break;
        case Traits.DOUBLE:
            {
                double[] inp = ((DoubleArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (short)inp[j];
                }
            }
            break;
        default:
            throw new IllegalArgumentException("unexpected type (BUG)");
        }
        return wrap(out, src.getShape());
    }

    /**
     * Convert a ShapedArray into an IntArray.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     *
     * @param src - The source array.
     * @return An IntArray whose values has been converted into int's
     *         from those of {@code src}.
     */
    public IntArray toInt(ShapedArray src) {
        int srcType = src.getType();
        if (src.getType() == Traits.INT) {
            return (IntArray)src;
        }
        int number = src.getNumber();
        int[] out = new int[number];
        switch (srcType) {
        case Traits.BYTE:
            {
                byte[] inp = ((ByteArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (int)inp[j];
                }
            }
            break;
        case Traits.SHORT:
            {
                short[] inp = ((ShortArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (int)inp[j];
                }
            }
            break;
        case Traits.LONG:
            {
                long[] inp = ((LongArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (int)inp[j];
                }
            }
            break;
        case Traits.FLOAT:
            {
                float[] inp = ((FloatArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (int)inp[j];
                }
            }
            break;
        case Traits.DOUBLE:
            {
                double[] inp = ((DoubleArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (int)inp[j];
                }
            }
            break;
        default:
            throw new IllegalArgumentException("unexpected type (BUG)");
        }
        return wrap(out, src.getShape());
    }

    /**
     * Convert a ShapedArray into a LongArray.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     *
     * @param src - The source array.
     * @return A LongArray whose values has been converted into long's
     *         from those of {@code src}.
     */
    public LongArray toLong(ShapedArray src) {
        int srcType = src.getType();
        if (src.getType() == Traits.LONG) {
            return (LongArray)src;
        }
        int number = src.getNumber();
        long[] out = new long[number];
        switch (srcType) {
        case Traits.BYTE:
            {
                byte[] inp = ((ByteArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (long)inp[j];
                }
            }
            break;
        case Traits.SHORT:
            {
                short[] inp = ((ShortArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (long)inp[j];
                }
            }
            break;
        case Traits.INT:
            {
                int[] inp = ((IntArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (long)inp[j];
                }
            }
            break;
        case Traits.FLOAT:
            {
                float[] inp = ((FloatArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (long)inp[j];
                }
            }
            break;
        case Traits.DOUBLE:
            {
                double[] inp = ((DoubleArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (long)inp[j];
                }
            }
            break;
        default:
            throw new IllegalArgumentException("unexpected type (BUG)");
        }
        return wrap(out, src.getShape());
    }

    /**
     * Convert a ShapedArray into a FloatArray.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     *
     * @param src - The source array.
     * @return A FloatArray whose values has been converted into float's
     *         from those of {@code src}.
     */
    public FloatArray toFloat(ShapedArray src) {
        int srcType = src.getType();
        if (src.getType() == Traits.FLOAT) {
            return (FloatArray)src;
        }
        int number = src.getNumber();
        float[] out = new float[number];
        switch (srcType) {
        case Traits.BYTE:
            {
                byte[] inp = ((ByteArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (float)inp[j];
                }
            }
            break;
        case Traits.SHORT:
            {
                short[] inp = ((ShortArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (float)inp[j];
                }
            }
            break;
        case Traits.INT:
            {
                int[] inp = ((IntArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (float)inp[j];
                }
            }
            break;
        case Traits.LONG:
            {
                long[] inp = ((LongArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (float)inp[j];
                }
            }
            break;
        case Traits.DOUBLE:
            {
                double[] inp = ((DoubleArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (float)inp[j];
                }
            }
            break;
        default:
            throw new IllegalArgumentException("unexpected type (BUG)");
        }
        return wrap(out, src.getShape());
    }

    /**
     * Convert a ShapedArray into a DoubleArray.
     * <p>
     * The operation is lazy, in the sense that {@code src} is returned if it
     * is already of the requested type.
     *
     * @param src - The source array.
     * @return A DoubleArray whose values has been converted into double's
     *         from those of {@code src}.
     */
    public DoubleArray toDouble(ShapedArray src) {
        int srcType = src.getType();
        if (src.getType() == Traits.DOUBLE) {
            return (DoubleArray)src;
        }
        int number = src.getNumber();
        double[] out = new double[number];
        switch (srcType) {
        case Traits.BYTE:
            {
                byte[] inp = ((ByteArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (double)inp[j];
                }
            }
            break;
        case Traits.SHORT:
            {
                short[] inp = ((ShortArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (double)inp[j];
                }
            }
            break;
        case Traits.INT:
            {
                int[] inp = ((IntArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (double)inp[j];
                }
            }
            break;
        case Traits.LONG:
            {
                long[] inp = ((LongArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (double)inp[j];
                }
            }
            break;
        case Traits.FLOAT:
            {
                float[] inp = ((FloatArray)src).flatten(false);
                for (int j = 0; j < number; ++j) {
                    out[j] = (double)inp[j];
                }
            }
            break;
        default:
            throw new IllegalArgumentException("unexpected type (BUG)");
        }
        return wrap(out, src.getShape());
    }


    /**
     * Wrap an array of byte values into a ByteArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ByteArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Byte1D}, {@link #Byte2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 1D array.
     * @return A ByteArray wrapped around the source array {@code arr}.
      */
    public static Byte1D wrap(byte[] arr, int dim1) {
        return Byte1D.wrap(arr, dim1);
    }

    /**
     * Wrap an array of byte values into a ByteArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ByteArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Byte1D}, {@link #Byte2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 2D array.
     * @param dim2 - The 2nd dimension of the 2D array.
     * @return A ByteArray wrapped around the source array {@code arr}.
      */
    public static Byte2D wrap(byte[] arr, int dim1, int dim2) {
        return Byte2D.wrap(arr, dim1, dim2);
    }

    /**
     * Wrap an array of byte values into a ByteArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ByteArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Byte1D}, {@link #Byte2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 3D array.
     * @param dim2 - The 2nd dimension of the 3D array.
     * @param dim3 - The 3rd dimension of the 3D array.
     * @return A ByteArray wrapped around the source array {@code arr}.
      */
    public static Byte3D wrap(byte[] arr, int dim1, int dim2, int dim3) {
        return Byte3D.wrap(arr, dim1, dim2, dim3);
    }

    /**
     * Wrap an array of byte values into a ByteArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ByteArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Byte1D}, {@link #Byte2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 4D array.
     * @param dim2 - The 2nd dimension of the 4D array.
     * @param dim3 - The 3rd dimension of the 4D array.
     * @param dim4 - The 4th dimension of the 4D array.
     * @return A ByteArray wrapped around the source array {@code arr}.
      */
    public static Byte4D wrap(byte[] arr, int dim1, int dim2, int dim3, int dim4) {
        return Byte4D.wrap(arr, dim1, dim2, dim3, dim4);
    }

    /**
     * Wrap an array of byte values into a ByteArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ByteArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Byte1D}, {@link #Byte2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 5D array.
     * @param dim2 - The 2nd dimension of the 5D array.
     * @param dim3 - The 3rd dimension of the 5D array.
     * @param dim4 - The 4th dimension of the 5D array.
     * @param dim5 - The 5th dimension of the 5D array.
     * @return A ByteArray wrapped around the source array {@code arr}.
      */
    public static Byte5D wrap(byte[] arr, int dim1, int dim2, int dim3, int dim4, int dim5) {
        return Byte5D.wrap(arr, dim1, dim2, dim3, dim4, dim5);
    }

    /**
     * Wrap an array of byte values into a ByteArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ByteArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Byte1D}, {@link #Byte2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 6D array.
     * @param dim2 - The 2nd dimension of the 6D array.
     * @param dim3 - The 3rd dimension of the 6D array.
     * @param dim4 - The 4th dimension of the 6D array.
     * @param dim5 - The 5th dimension of the 6D array.
     * @param dim6 - The 6th dimension of the 6D array.
     * @return A ByteArray wrapped around the source array {@code arr}.
      */
    public static Byte6D wrap(byte[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        return Byte6D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6);
    }

    /**
     * Wrap an array of byte values into a ByteArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ByteArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Byte1D}, {@link #Byte2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 7D array.
     * @param dim2 - The 2nd dimension of the 7D array.
     * @param dim3 - The 3rd dimension of the 7D array.
     * @param dim4 - The 4th dimension of the 7D array.
     * @param dim5 - The 5th dimension of the 7D array.
     * @param dim6 - The 6th dimension of the 7D array.
     * @param dim7 - The 7th dimension of the 7D array.
     * @return A ByteArray wrapped around the source array {@code arr}.
      */
    public static Byte7D wrap(byte[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7) {
        return Byte7D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7);
    }

    /**
     * Wrap an array of byte values into a ByteArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ByteArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Byte1D}, {@link #Byte2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 8D array.
     * @param dim2 - The 2nd dimension of the 8D array.
     * @param dim3 - The 3rd dimension of the 8D array.
     * @param dim4 - The 4th dimension of the 8D array.
     * @param dim5 - The 5th dimension of the 8D array.
     * @param dim6 - The 6th dimension of the 8D array.
     * @param dim7 - The 7th dimension of the 8D array.
     * @param dim8 - The 8th dimension of the 8D array.
     * @return A ByteArray wrapped around the source array {@code arr}.
      */
    public static Byte8D wrap(byte[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        return Byte8D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
    }

    /**
     * Wrap an array of byte values into a ByteArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ByteArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Byte1D}, {@link #Byte2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 9D array.
     * @param dim2 - The 2nd dimension of the 9D array.
     * @param dim3 - The 3rd dimension of the 9D array.
     * @param dim4 - The 4th dimension of the 9D array.
     * @param dim5 - The 5th dimension of the 9D array.
     * @param dim6 - The 6th dimension of the 9D array.
     * @param dim7 - The 7th dimension of the 9D array.
     * @param dim8 - The 8th dimension of the 9D array.
     * @param dim9 - The 9th dimension of the 9D array.
     * @return A ByteArray wrapped around the source array {@code arr}.
      */
    public static Byte9D wrap(byte[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        return Byte9D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
    }

    /**
     * Wrap a ByteArray object around a simple array of byte's.
     * <p>
     * Note that the storage order is assumed to be {@link Shaped#COLUMN_MAJOR}
     * and that the result can be safely casted into a {@link #Byte1D},
     * {@link #Byte2D}, ... according to the number of dimensions in
     * {@code shape}.
     *
     * @param arr  - The input array.
     * @param dims - The dimension list of the result.
     *
     * @return An instance of ByteArray sharing its data with the input array
     *         {@code arr}.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public static ByteArray wrap(byte[] arr, int[] dims) {
        return wrap(arr, Shape.make(dims));
    }

    /**
     * Wrap a ByteArray object around a simple array of byte's.
     * <p>
     * Note that the storage order is assumed to be {@link Shaped#COLUMN_MAJOR}
     * and that the result can be safely casted into a {@link #Byte1D},
     * {@link #Byte2D}, ... according to the number of dimensions in
     * {@code shape}.
     *
     * @param data   - The input array.
     * @param shape  - The shape of the result.
     * @param cloneShape - Indicate whether the {@code shape} parameter must be
     *                     cloned.
     *
     * @return An instance of ByteArray sharing its data with the input array
     *         {@code data}.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public static ByteArray wrap(byte[] data, Shape shape) {
        switch (shape.rank()) {
        case 1:
            return Byte1D.wrap(data, shape);
        case 2:
            return Byte2D.wrap(data, shape);
        case 3:
            return Byte3D.wrap(data, shape);
        case 4:
            return Byte4D.wrap(data, shape);
        case 5:
            return Byte5D.wrap(data, shape);
        case 6:
            return Byte6D.wrap(data, shape);
        case 7:
            return Byte7D.wrap(data, shape);
        case 8:
            return Byte8D.wrap(data, shape);
        case 9:
            return Byte9D.wrap(data, shape);
        default:
            throw new IllegalArgumentException("Invalid shape.");
        }
    }

    /**
     * Wrap an array of short values into a ShortArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ShortArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Short1D}, {@link #Short2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 1D array.
     * @return A ShortArray wrapped around the source array {@code arr}.
      */
    public static Short1D wrap(short[] arr, int dim1) {
        return Short1D.wrap(arr, dim1);
    }

    /**
     * Wrap an array of short values into a ShortArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ShortArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Short1D}, {@link #Short2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 2D array.
     * @param dim2 - The 2nd dimension of the 2D array.
     * @return A ShortArray wrapped around the source array {@code arr}.
      */
    public static Short2D wrap(short[] arr, int dim1, int dim2) {
        return Short2D.wrap(arr, dim1, dim2);
    }

    /**
     * Wrap an array of short values into a ShortArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ShortArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Short1D}, {@link #Short2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 3D array.
     * @param dim2 - The 2nd dimension of the 3D array.
     * @param dim3 - The 3rd dimension of the 3D array.
     * @return A ShortArray wrapped around the source array {@code arr}.
      */
    public static Short3D wrap(short[] arr, int dim1, int dim2, int dim3) {
        return Short3D.wrap(arr, dim1, dim2, dim3);
    }

    /**
     * Wrap an array of short values into a ShortArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ShortArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Short1D}, {@link #Short2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 4D array.
     * @param dim2 - The 2nd dimension of the 4D array.
     * @param dim3 - The 3rd dimension of the 4D array.
     * @param dim4 - The 4th dimension of the 4D array.
     * @return A ShortArray wrapped around the source array {@code arr}.
      */
    public static Short4D wrap(short[] arr, int dim1, int dim2, int dim3, int dim4) {
        return Short4D.wrap(arr, dim1, dim2, dim3, dim4);
    }

    /**
     * Wrap an array of short values into a ShortArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ShortArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Short1D}, {@link #Short2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 5D array.
     * @param dim2 - The 2nd dimension of the 5D array.
     * @param dim3 - The 3rd dimension of the 5D array.
     * @param dim4 - The 4th dimension of the 5D array.
     * @param dim5 - The 5th dimension of the 5D array.
     * @return A ShortArray wrapped around the source array {@code arr}.
      */
    public static Short5D wrap(short[] arr, int dim1, int dim2, int dim3, int dim4, int dim5) {
        return Short5D.wrap(arr, dim1, dim2, dim3, dim4, dim5);
    }

    /**
     * Wrap an array of short values into a ShortArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ShortArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Short1D}, {@link #Short2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 6D array.
     * @param dim2 - The 2nd dimension of the 6D array.
     * @param dim3 - The 3rd dimension of the 6D array.
     * @param dim4 - The 4th dimension of the 6D array.
     * @param dim5 - The 5th dimension of the 6D array.
     * @param dim6 - The 6th dimension of the 6D array.
     * @return A ShortArray wrapped around the source array {@code arr}.
      */
    public static Short6D wrap(short[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        return Short6D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6);
    }

    /**
     * Wrap an array of short values into a ShortArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ShortArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Short1D}, {@link #Short2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 7D array.
     * @param dim2 - The 2nd dimension of the 7D array.
     * @param dim3 - The 3rd dimension of the 7D array.
     * @param dim4 - The 4th dimension of the 7D array.
     * @param dim5 - The 5th dimension of the 7D array.
     * @param dim6 - The 6th dimension of the 7D array.
     * @param dim7 - The 7th dimension of the 7D array.
     * @return A ShortArray wrapped around the source array {@code arr}.
      */
    public static Short7D wrap(short[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7) {
        return Short7D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7);
    }

    /**
     * Wrap an array of short values into a ShortArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ShortArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Short1D}, {@link #Short2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 8D array.
     * @param dim2 - The 2nd dimension of the 8D array.
     * @param dim3 - The 3rd dimension of the 8D array.
     * @param dim4 - The 4th dimension of the 8D array.
     * @param dim5 - The 5th dimension of the 8D array.
     * @param dim6 - The 6th dimension of the 8D array.
     * @param dim7 - The 7th dimension of the 8D array.
     * @param dim8 - The 8th dimension of the 8D array.
     * @return A ShortArray wrapped around the source array {@code arr}.
      */
    public static Short8D wrap(short[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        return Short8D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
    }

    /**
     * Wrap an array of short values into a ShortArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned ShortArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Short1D}, {@link #Short2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 9D array.
     * @param dim2 - The 2nd dimension of the 9D array.
     * @param dim3 - The 3rd dimension of the 9D array.
     * @param dim4 - The 4th dimension of the 9D array.
     * @param dim5 - The 5th dimension of the 9D array.
     * @param dim6 - The 6th dimension of the 9D array.
     * @param dim7 - The 7th dimension of the 9D array.
     * @param dim8 - The 8th dimension of the 9D array.
     * @param dim9 - The 9th dimension of the 9D array.
     * @return A ShortArray wrapped around the source array {@code arr}.
      */
    public static Short9D wrap(short[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        return Short9D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
    }

    /**
     * Wrap a ShortArray object around a simple array of short's.
     * <p>
     * Note that the storage order is assumed to be {@link Shaped#COLUMN_MAJOR}
     * and that the result can be safely casted into a {@link #Short1D},
     * {@link #Short2D}, ... according to the number of dimensions in
     * {@code shape}.
     *
     * @param arr  - The input array.
     * @param dims - The dimension list of the result.
     *
     * @return An instance of ShortArray sharing its data with the input array
     *         {@code arr}.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public static ShortArray wrap(short[] arr, int[] dims) {
        return wrap(arr, Shape.make(dims));
    }

    /**
     * Wrap a ShortArray object around a simple array of short's.
     * <p>
     * Note that the storage order is assumed to be {@link Shaped#COLUMN_MAJOR}
     * and that the result can be safely casted into a {@link #Short1D},
     * {@link #Short2D}, ... according to the number of dimensions in
     * {@code shape}.
     *
     * @param data   - The input array.
     * @param shape  - The shape of the result.
     * @param cloneShape - Indicate whether the {@code shape} parameter must be
     *                     cloned.
     *
     * @return An instance of ShortArray sharing its data with the input array
     *         {@code data}.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public static ShortArray wrap(short[] data, Shape shape) {
        switch (shape.rank()) {
        case 1:
            return Short1D.wrap(data, shape);
        case 2:
            return Short2D.wrap(data, shape);
        case 3:
            return Short3D.wrap(data, shape);
        case 4:
            return Short4D.wrap(data, shape);
        case 5:
            return Short5D.wrap(data, shape);
        case 6:
            return Short6D.wrap(data, shape);
        case 7:
            return Short7D.wrap(data, shape);
        case 8:
            return Short8D.wrap(data, shape);
        case 9:
            return Short9D.wrap(data, shape);
        default:
            throw new IllegalArgumentException("Invalid shape.");
        }
    }

    /**
     * Wrap an array of int values into an IntArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned IntArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Int1D}, {@link #Int2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 1D array.
     * @return A IntArray wrapped around the source array {@code arr}.
      */
    public static Int1D wrap(int[] arr, int dim1) {
        return Int1D.wrap(arr, dim1);
    }

    /**
     * Wrap an array of int values into an IntArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned IntArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Int1D}, {@link #Int2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 2D array.
     * @param dim2 - The 2nd dimension of the 2D array.
     * @return A IntArray wrapped around the source array {@code arr}.
      */
    public static Int2D wrap(int[] arr, int dim1, int dim2) {
        return Int2D.wrap(arr, dim1, dim2);
    }

    /**
     * Wrap an array of int values into an IntArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned IntArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Int1D}, {@link #Int2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 3D array.
     * @param dim2 - The 2nd dimension of the 3D array.
     * @param dim3 - The 3rd dimension of the 3D array.
     * @return A IntArray wrapped around the source array {@code arr}.
      */
    public static Int3D wrap(int[] arr, int dim1, int dim2, int dim3) {
        return Int3D.wrap(arr, dim1, dim2, dim3);
    }

    /**
     * Wrap an array of int values into an IntArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned IntArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Int1D}, {@link #Int2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 4D array.
     * @param dim2 - The 2nd dimension of the 4D array.
     * @param dim3 - The 3rd dimension of the 4D array.
     * @param dim4 - The 4th dimension of the 4D array.
     * @return A IntArray wrapped around the source array {@code arr}.
      */
    public static Int4D wrap(int[] arr, int dim1, int dim2, int dim3, int dim4) {
        return Int4D.wrap(arr, dim1, dim2, dim3, dim4);
    }

    /**
     * Wrap an array of int values into an IntArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned IntArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Int1D}, {@link #Int2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 5D array.
     * @param dim2 - The 2nd dimension of the 5D array.
     * @param dim3 - The 3rd dimension of the 5D array.
     * @param dim4 - The 4th dimension of the 5D array.
     * @param dim5 - The 5th dimension of the 5D array.
     * @return A IntArray wrapped around the source array {@code arr}.
      */
    public static Int5D wrap(int[] arr, int dim1, int dim2, int dim3, int dim4, int dim5) {
        return Int5D.wrap(arr, dim1, dim2, dim3, dim4, dim5);
    }

    /**
     * Wrap an array of int values into an IntArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned IntArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Int1D}, {@link #Int2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 6D array.
     * @param dim2 - The 2nd dimension of the 6D array.
     * @param dim3 - The 3rd dimension of the 6D array.
     * @param dim4 - The 4th dimension of the 6D array.
     * @param dim5 - The 5th dimension of the 6D array.
     * @param dim6 - The 6th dimension of the 6D array.
     * @return A IntArray wrapped around the source array {@code arr}.
      */
    public static Int6D wrap(int[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        return Int6D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6);
    }

    /**
     * Wrap an array of int values into an IntArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned IntArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Int1D}, {@link #Int2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 7D array.
     * @param dim2 - The 2nd dimension of the 7D array.
     * @param dim3 - The 3rd dimension of the 7D array.
     * @param dim4 - The 4th dimension of the 7D array.
     * @param dim5 - The 5th dimension of the 7D array.
     * @param dim6 - The 6th dimension of the 7D array.
     * @param dim7 - The 7th dimension of the 7D array.
     * @return A IntArray wrapped around the source array {@code arr}.
      */
    public static Int7D wrap(int[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7) {
        return Int7D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7);
    }

    /**
     * Wrap an array of int values into an IntArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned IntArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Int1D}, {@link #Int2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 8D array.
     * @param dim2 - The 2nd dimension of the 8D array.
     * @param dim3 - The 3rd dimension of the 8D array.
     * @param dim4 - The 4th dimension of the 8D array.
     * @param dim5 - The 5th dimension of the 8D array.
     * @param dim6 - The 6th dimension of the 8D array.
     * @param dim7 - The 7th dimension of the 8D array.
     * @param dim8 - The 8th dimension of the 8D array.
     * @return A IntArray wrapped around the source array {@code arr}.
      */
    public static Int8D wrap(int[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        return Int8D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
    }

    /**
     * Wrap an array of int values into an IntArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned IntArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Int1D}, {@link #Int2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 9D array.
     * @param dim2 - The 2nd dimension of the 9D array.
     * @param dim3 - The 3rd dimension of the 9D array.
     * @param dim4 - The 4th dimension of the 9D array.
     * @param dim5 - The 5th dimension of the 9D array.
     * @param dim6 - The 6th dimension of the 9D array.
     * @param dim7 - The 7th dimension of the 9D array.
     * @param dim8 - The 8th dimension of the 9D array.
     * @param dim9 - The 9th dimension of the 9D array.
     * @return A IntArray wrapped around the source array {@code arr}.
      */
    public static Int9D wrap(int[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        return Int9D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
    }

    /**
     * Wrap an IntArray object around a simple array of int's.
     * <p>
     * Note that the storage order is assumed to be {@link Shaped#COLUMN_MAJOR}
     * and that the result can be safely casted into a {@link #Int1D},
     * {@link #Int2D}, ... according to the number of dimensions in
     * {@code shape}.
     *
     * @param arr  - The input array.
     * @param dims - The dimension list of the result.
     *
     * @return An instance of IntArray sharing its data with the input array
     *         {@code arr}.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public static IntArray wrap(int[] arr, int[] dims) {
        return wrap(arr, Shape.make(dims));
    }

    /**
     * Wrap an IntArray object around a simple array of int's.
     * <p>
     * Note that the storage order is assumed to be {@link Shaped#COLUMN_MAJOR}
     * and that the result can be safely casted into a {@link #Int1D},
     * {@link #Int2D}, ... according to the number of dimensions in
     * {@code shape}.
     *
     * @param data   - The input array.
     * @param shape  - The shape of the result.
     * @param cloneShape - Indicate whether the {@code shape} parameter must be
     *                     cloned.
     *
     * @return An instance of IntArray sharing its data with the input array
     *         {@code data}.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public static IntArray wrap(int[] data, Shape shape) {
        switch (shape.rank()) {
        case 1:
            return Int1D.wrap(data, shape);
        case 2:
            return Int2D.wrap(data, shape);
        case 3:
            return Int3D.wrap(data, shape);
        case 4:
            return Int4D.wrap(data, shape);
        case 5:
            return Int5D.wrap(data, shape);
        case 6:
            return Int6D.wrap(data, shape);
        case 7:
            return Int7D.wrap(data, shape);
        case 8:
            return Int8D.wrap(data, shape);
        case 9:
            return Int9D.wrap(data, shape);
        default:
            throw new IllegalArgumentException("Invalid shape.");
        }
    }

    /**
     * Wrap an array of long values into a LongArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned LongArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Long1D}, {@link #Long2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 1D array.
     * @return A LongArray wrapped around the source array {@code arr}.
      */
    public static Long1D wrap(long[] arr, int dim1) {
        return Long1D.wrap(arr, dim1);
    }

    /**
     * Wrap an array of long values into a LongArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned LongArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Long1D}, {@link #Long2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 2D array.
     * @param dim2 - The 2nd dimension of the 2D array.
     * @return A LongArray wrapped around the source array {@code arr}.
      */
    public static Long2D wrap(long[] arr, int dim1, int dim2) {
        return Long2D.wrap(arr, dim1, dim2);
    }

    /**
     * Wrap an array of long values into a LongArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned LongArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Long1D}, {@link #Long2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 3D array.
     * @param dim2 - The 2nd dimension of the 3D array.
     * @param dim3 - The 3rd dimension of the 3D array.
     * @return A LongArray wrapped around the source array {@code arr}.
      */
    public static Long3D wrap(long[] arr, int dim1, int dim2, int dim3) {
        return Long3D.wrap(arr, dim1, dim2, dim3);
    }

    /**
     * Wrap an array of long values into a LongArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned LongArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Long1D}, {@link #Long2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 4D array.
     * @param dim2 - The 2nd dimension of the 4D array.
     * @param dim3 - The 3rd dimension of the 4D array.
     * @param dim4 - The 4th dimension of the 4D array.
     * @return A LongArray wrapped around the source array {@code arr}.
      */
    public static Long4D wrap(long[] arr, int dim1, int dim2, int dim3, int dim4) {
        return Long4D.wrap(arr, dim1, dim2, dim3, dim4);
    }

    /**
     * Wrap an array of long values into a LongArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned LongArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Long1D}, {@link #Long2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 5D array.
     * @param dim2 - The 2nd dimension of the 5D array.
     * @param dim3 - The 3rd dimension of the 5D array.
     * @param dim4 - The 4th dimension of the 5D array.
     * @param dim5 - The 5th dimension of the 5D array.
     * @return A LongArray wrapped around the source array {@code arr}.
      */
    public static Long5D wrap(long[] arr, int dim1, int dim2, int dim3, int dim4, int dim5) {
        return Long5D.wrap(arr, dim1, dim2, dim3, dim4, dim5);
    }

    /**
     * Wrap an array of long values into a LongArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned LongArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Long1D}, {@link #Long2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 6D array.
     * @param dim2 - The 2nd dimension of the 6D array.
     * @param dim3 - The 3rd dimension of the 6D array.
     * @param dim4 - The 4th dimension of the 6D array.
     * @param dim5 - The 5th dimension of the 6D array.
     * @param dim6 - The 6th dimension of the 6D array.
     * @return A LongArray wrapped around the source array {@code arr}.
      */
    public static Long6D wrap(long[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        return Long6D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6);
    }

    /**
     * Wrap an array of long values into a LongArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned LongArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Long1D}, {@link #Long2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 7D array.
     * @param dim2 - The 2nd dimension of the 7D array.
     * @param dim3 - The 3rd dimension of the 7D array.
     * @param dim4 - The 4th dimension of the 7D array.
     * @param dim5 - The 5th dimension of the 7D array.
     * @param dim6 - The 6th dimension of the 7D array.
     * @param dim7 - The 7th dimension of the 7D array.
     * @return A LongArray wrapped around the source array {@code arr}.
      */
    public static Long7D wrap(long[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7) {
        return Long7D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7);
    }

    /**
     * Wrap an array of long values into a LongArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned LongArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Long1D}, {@link #Long2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 8D array.
     * @param dim2 - The 2nd dimension of the 8D array.
     * @param dim3 - The 3rd dimension of the 8D array.
     * @param dim4 - The 4th dimension of the 8D array.
     * @param dim5 - The 5th dimension of the 8D array.
     * @param dim6 - The 6th dimension of the 8D array.
     * @param dim7 - The 7th dimension of the 8D array.
     * @param dim8 - The 8th dimension of the 8D array.
     * @return A LongArray wrapped around the source array {@code arr}.
      */
    public static Long8D wrap(long[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        return Long8D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
    }

    /**
     * Wrap an array of long values into a LongArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned LongArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Long1D}, {@link #Long2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 9D array.
     * @param dim2 - The 2nd dimension of the 9D array.
     * @param dim3 - The 3rd dimension of the 9D array.
     * @param dim4 - The 4th dimension of the 9D array.
     * @param dim5 - The 5th dimension of the 9D array.
     * @param dim6 - The 6th dimension of the 9D array.
     * @param dim7 - The 7th dimension of the 9D array.
     * @param dim8 - The 8th dimension of the 9D array.
     * @param dim9 - The 9th dimension of the 9D array.
     * @return A LongArray wrapped around the source array {@code arr}.
      */
    public static Long9D wrap(long[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        return Long9D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
    }

    /**
     * Wrap a LongArray object around a simple array of long's.
     * <p>
     * Note that the storage order is assumed to be {@link Shaped#COLUMN_MAJOR}
     * and that the result can be safely casted into a {@link #Long1D},
     * {@link #Long2D}, ... according to the number of dimensions in
     * {@code shape}.
     *
     * @param arr  - The input array.
     * @param dims - The dimension list of the result.
     *
     * @return An instance of LongArray sharing its data with the input array
     *         {@code arr}.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public static LongArray wrap(long[] arr, int[] dims) {
        return wrap(arr, Shape.make(dims));
    }

    /**
     * Wrap a LongArray object around a simple array of long's.
     * <p>
     * Note that the storage order is assumed to be {@link Shaped#COLUMN_MAJOR}
     * and that the result can be safely casted into a {@link #Long1D},
     * {@link #Long2D}, ... according to the number of dimensions in
     * {@code shape}.
     *
     * @param data   - The input array.
     * @param shape  - The shape of the result.
     * @param cloneShape - Indicate whether the {@code shape} parameter must be
     *                     cloned.
     *
     * @return An instance of LongArray sharing its data with the input array
     *         {@code data}.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public static LongArray wrap(long[] data, Shape shape) {
        switch (shape.rank()) {
        case 1:
            return Long1D.wrap(data, shape);
        case 2:
            return Long2D.wrap(data, shape);
        case 3:
            return Long3D.wrap(data, shape);
        case 4:
            return Long4D.wrap(data, shape);
        case 5:
            return Long5D.wrap(data, shape);
        case 6:
            return Long6D.wrap(data, shape);
        case 7:
            return Long7D.wrap(data, shape);
        case 8:
            return Long8D.wrap(data, shape);
        case 9:
            return Long9D.wrap(data, shape);
        default:
            throw new IllegalArgumentException("Invalid shape.");
        }
    }

    /**
     * Wrap an array of float values into a FloatArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned FloatArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Float1D}, {@link #Float2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 1D array.
     * @return A FloatArray wrapped around the source array {@code arr}.
      */
    public static Float1D wrap(float[] arr, int dim1) {
        return Float1D.wrap(arr, dim1);
    }

    /**
     * Wrap an array of float values into a FloatArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned FloatArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Float1D}, {@link #Float2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 2D array.
     * @param dim2 - The 2nd dimension of the 2D array.
     * @return A FloatArray wrapped around the source array {@code arr}.
      */
    public static Float2D wrap(float[] arr, int dim1, int dim2) {
        return Float2D.wrap(arr, dim1, dim2);
    }

    /**
     * Wrap an array of float values into a FloatArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned FloatArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Float1D}, {@link #Float2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 3D array.
     * @param dim2 - The 2nd dimension of the 3D array.
     * @param dim3 - The 3rd dimension of the 3D array.
     * @return A FloatArray wrapped around the source array {@code arr}.
      */
    public static Float3D wrap(float[] arr, int dim1, int dim2, int dim3) {
        return Float3D.wrap(arr, dim1, dim2, dim3);
    }

    /**
     * Wrap an array of float values into a FloatArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned FloatArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Float1D}, {@link #Float2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 4D array.
     * @param dim2 - The 2nd dimension of the 4D array.
     * @param dim3 - The 3rd dimension of the 4D array.
     * @param dim4 - The 4th dimension of the 4D array.
     * @return A FloatArray wrapped around the source array {@code arr}.
      */
    public static Float4D wrap(float[] arr, int dim1, int dim2, int dim3, int dim4) {
        return Float4D.wrap(arr, dim1, dim2, dim3, dim4);
    }

    /**
     * Wrap an array of float values into a FloatArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned FloatArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Float1D}, {@link #Float2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 5D array.
     * @param dim2 - The 2nd dimension of the 5D array.
     * @param dim3 - The 3rd dimension of the 5D array.
     * @param dim4 - The 4th dimension of the 5D array.
     * @param dim5 - The 5th dimension of the 5D array.
     * @return A FloatArray wrapped around the source array {@code arr}.
      */
    public static Float5D wrap(float[] arr, int dim1, int dim2, int dim3, int dim4, int dim5) {
        return Float5D.wrap(arr, dim1, dim2, dim3, dim4, dim5);
    }

    /**
     * Wrap an array of float values into a FloatArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned FloatArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Float1D}, {@link #Float2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 6D array.
     * @param dim2 - The 2nd dimension of the 6D array.
     * @param dim3 - The 3rd dimension of the 6D array.
     * @param dim4 - The 4th dimension of the 6D array.
     * @param dim5 - The 5th dimension of the 6D array.
     * @param dim6 - The 6th dimension of the 6D array.
     * @return A FloatArray wrapped around the source array {@code arr}.
      */
    public static Float6D wrap(float[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        return Float6D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6);
    }

    /**
     * Wrap an array of float values into a FloatArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned FloatArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Float1D}, {@link #Float2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 7D array.
     * @param dim2 - The 2nd dimension of the 7D array.
     * @param dim3 - The 3rd dimension of the 7D array.
     * @param dim4 - The 4th dimension of the 7D array.
     * @param dim5 - The 5th dimension of the 7D array.
     * @param dim6 - The 6th dimension of the 7D array.
     * @param dim7 - The 7th dimension of the 7D array.
     * @return A FloatArray wrapped around the source array {@code arr}.
      */
    public static Float7D wrap(float[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7) {
        return Float7D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7);
    }

    /**
     * Wrap an array of float values into a FloatArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned FloatArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Float1D}, {@link #Float2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 8D array.
     * @param dim2 - The 2nd dimension of the 8D array.
     * @param dim3 - The 3rd dimension of the 8D array.
     * @param dim4 - The 4th dimension of the 8D array.
     * @param dim5 - The 5th dimension of the 8D array.
     * @param dim6 - The 6th dimension of the 8D array.
     * @param dim7 - The 7th dimension of the 8D array.
     * @param dim8 - The 8th dimension of the 8D array.
     * @return A FloatArray wrapped around the source array {@code arr}.
      */
    public static Float8D wrap(float[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        return Float8D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
    }

    /**
     * Wrap an array of float values into a FloatArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned FloatArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Float1D}, {@link #Float2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 9D array.
     * @param dim2 - The 2nd dimension of the 9D array.
     * @param dim3 - The 3rd dimension of the 9D array.
     * @param dim4 - The 4th dimension of the 9D array.
     * @param dim5 - The 5th dimension of the 9D array.
     * @param dim6 - The 6th dimension of the 9D array.
     * @param dim7 - The 7th dimension of the 9D array.
     * @param dim8 - The 8th dimension of the 9D array.
     * @param dim9 - The 9th dimension of the 9D array.
     * @return A FloatArray wrapped around the source array {@code arr}.
      */
    public static Float9D wrap(float[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        return Float9D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
    }

    /**
     * Wrap a FloatArray object around a simple array of float's.
     * <p>
     * Note that the storage order is assumed to be {@link Shaped#COLUMN_MAJOR}
     * and that the result can be safely casted into a {@link #Float1D},
     * {@link #Float2D}, ... according to the number of dimensions in
     * {@code shape}.
     *
     * @param arr  - The input array.
     * @param dims - The dimension list of the result.
     *
     * @return An instance of FloatArray sharing its data with the input array
     *         {@code arr}.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public static FloatArray wrap(float[] arr, int[] dims) {
        return wrap(arr, Shape.make(dims));
    }

    /**
     * Wrap a FloatArray object around a simple array of float's.
     * <p>
     * Note that the storage order is assumed to be {@link Shaped#COLUMN_MAJOR}
     * and that the result can be safely casted into a {@link #Float1D},
     * {@link #Float2D}, ... according to the number of dimensions in
     * {@code shape}.
     *
     * @param data   - The input array.
     * @param shape  - The shape of the result.
     * @param cloneShape - Indicate whether the {@code shape} parameter must be
     *                     cloned.
     *
     * @return An instance of FloatArray sharing its data with the input array
     *         {@code data}.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public static FloatArray wrap(float[] data, Shape shape) {
        switch (shape.rank()) {
        case 1:
            return Float1D.wrap(data, shape);
        case 2:
            return Float2D.wrap(data, shape);
        case 3:
            return Float3D.wrap(data, shape);
        case 4:
            return Float4D.wrap(data, shape);
        case 5:
            return Float5D.wrap(data, shape);
        case 6:
            return Float6D.wrap(data, shape);
        case 7:
            return Float7D.wrap(data, shape);
        case 8:
            return Float8D.wrap(data, shape);
        case 9:
            return Float9D.wrap(data, shape);
        default:
            throw new IllegalArgumentException("Invalid shape.");
        }
    }

    /**
     * Wrap an array of double values into a DoubleArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned DoubleArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Double1D}, {@link #Double2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 1D array.
     * @return A DoubleArray wrapped around the source array {@code arr}.
      */
    public static Double1D wrap(double[] arr, int dim1) {
        return Double1D.wrap(arr, dim1);
    }

    /**
     * Wrap an array of double values into a DoubleArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned DoubleArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Double1D}, {@link #Double2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 2D array.
     * @param dim2 - The 2nd dimension of the 2D array.
     * @return A DoubleArray wrapped around the source array {@code arr}.
      */
    public static Double2D wrap(double[] arr, int dim1, int dim2) {
        return Double2D.wrap(arr, dim1, dim2);
    }

    /**
     * Wrap an array of double values into a DoubleArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned DoubleArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Double1D}, {@link #Double2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 3D array.
     * @param dim2 - The 2nd dimension of the 3D array.
     * @param dim3 - The 3rd dimension of the 3D array.
     * @return A DoubleArray wrapped around the source array {@code arr}.
      */
    public static Double3D wrap(double[] arr, int dim1, int dim2, int dim3) {
        return Double3D.wrap(arr, dim1, dim2, dim3);
    }

    /**
     * Wrap an array of double values into a DoubleArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned DoubleArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Double1D}, {@link #Double2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 4D array.
     * @param dim2 - The 2nd dimension of the 4D array.
     * @param dim3 - The 3rd dimension of the 4D array.
     * @param dim4 - The 4th dimension of the 4D array.
     * @return A DoubleArray wrapped around the source array {@code arr}.
      */
    public static Double4D wrap(double[] arr, int dim1, int dim2, int dim3, int dim4) {
        return Double4D.wrap(arr, dim1, dim2, dim3, dim4);
    }

    /**
     * Wrap an array of double values into a DoubleArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned DoubleArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Double1D}, {@link #Double2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 5D array.
     * @param dim2 - The 2nd dimension of the 5D array.
     * @param dim3 - The 3rd dimension of the 5D array.
     * @param dim4 - The 4th dimension of the 5D array.
     * @param dim5 - The 5th dimension of the 5D array.
     * @return A DoubleArray wrapped around the source array {@code arr}.
      */
    public static Double5D wrap(double[] arr, int dim1, int dim2, int dim3, int dim4, int dim5) {
        return Double5D.wrap(arr, dim1, dim2, dim3, dim4, dim5);
    }

    /**
     * Wrap an array of double values into a DoubleArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned DoubleArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Double1D}, {@link #Double2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 6D array.
     * @param dim2 - The 2nd dimension of the 6D array.
     * @param dim3 - The 3rd dimension of the 6D array.
     * @param dim4 - The 4th dimension of the 6D array.
     * @param dim5 - The 5th dimension of the 6D array.
     * @param dim6 - The 6th dimension of the 6D array.
     * @return A DoubleArray wrapped around the source array {@code arr}.
      */
    public static Double6D wrap(double[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        return Double6D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6);
    }

    /**
     * Wrap an array of double values into a DoubleArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned DoubleArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Double1D}, {@link #Double2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 7D array.
     * @param dim2 - The 2nd dimension of the 7D array.
     * @param dim3 - The 3rd dimension of the 7D array.
     * @param dim4 - The 4th dimension of the 7D array.
     * @param dim5 - The 5th dimension of the 7D array.
     * @param dim6 - The 6th dimension of the 7D array.
     * @param dim7 - The 7th dimension of the 7D array.
     * @return A DoubleArray wrapped around the source array {@code arr}.
      */
    public static Double7D wrap(double[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7) {
        return Double7D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7);
    }

    /**
     * Wrap an array of double values into a DoubleArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned DoubleArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Double1D}, {@link #Double2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 8D array.
     * @param dim2 - The 2nd dimension of the 8D array.
     * @param dim3 - The 3rd dimension of the 8D array.
     * @param dim4 - The 4th dimension of the 8D array.
     * @param dim5 - The 5th dimension of the 8D array.
     * @param dim6 - The 6th dimension of the 8D array.
     * @param dim7 - The 7th dimension of the 8D array.
     * @param dim8 - The 8th dimension of the 8D array.
     * @return A DoubleArray wrapped around the source array {@code arr}.
      */
    public static Double8D wrap(double[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        return Double8D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
    }

    /**
     * Wrap an array of double values into a DoubleArray.
     * <p>
     * Notes: (i) The source array {@code arr} and the returned DoubleArray
     * share the same contents. (ii) The storage order is assumed to be
     * {@link Shaped#COLUMN_MAJOR}. (iii) The result can be safely casted into a
     * {@link #Double1D}, {@link #Double2D}, ... according to the number of
     * dimensions in {@code shape}.
     *
     * @param arr  - The source array.
     * @param dim1 - The 1st dimension of the 9D array.
     * @param dim2 - The 2nd dimension of the 9D array.
     * @param dim3 - The 3rd dimension of the 9D array.
     * @param dim4 - The 4th dimension of the 9D array.
     * @param dim5 - The 5th dimension of the 9D array.
     * @param dim6 - The 6th dimension of the 9D array.
     * @param dim7 - The 7th dimension of the 9D array.
     * @param dim8 - The 8th dimension of the 9D array.
     * @param dim9 - The 9th dimension of the 9D array.
     * @return A DoubleArray wrapped around the source array {@code arr}.
      */
    public static Double9D wrap(double[] arr, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        return Double9D.wrap(arr, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9);
    }

    /**
     * Wrap a DoubleArray object around a simple array of double's.
     * <p>
     * Note that the storage order is assumed to be {@link Shaped#COLUMN_MAJOR}
     * and that the result can be safely casted into a {@link #Double1D},
     * {@link #Double2D}, ... according to the number of dimensions in
     * {@code shape}.
     *
     * @param arr  - The input array.
     * @param dims - The dimension list of the result.
     *
     * @return An instance of DoubleArray sharing its data with the input array
     *         {@code arr}.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public static DoubleArray wrap(double[] arr, int[] dims) {
        return wrap(arr, Shape.make(dims));
    }

    /**
     * Wrap a DoubleArray object around a simple array of double's.
     * <p>
     * Note that the storage order is assumed to be {@link Shaped#COLUMN_MAJOR}
     * and that the result can be safely casted into a {@link #Double1D},
     * {@link #Double2D}, ... according to the number of dimensions in
     * {@code shape}.
     *
     * @param data   - The input array.
     * @param shape  - The shape of the result.
     * @param cloneShape - Indicate whether the {@code shape} parameter must be
     *                     cloned.
     *
     * @return An instance of DoubleArray sharing its data with the input array
     *         {@code data}.
     * @see {@link #flatten(boolean)}, {@link Shaped#COLUMN_MAJOR}.
     */
    public static DoubleArray wrap(double[] data, Shape shape) {
        switch (shape.rank()) {
        case 1:
            return Double1D.wrap(data, shape);
        case 2:
            return Double2D.wrap(data, shape);
        case 3:
            return Double3D.wrap(data, shape);
        case 4:
            return Double4D.wrap(data, shape);
        case 5:
            return Double5D.wrap(data, shape);
        case 6:
            return Double6D.wrap(data, shape);
        case 7:
            return Double7D.wrap(data, shape);
        case 8:
            return Double8D.wrap(data, shape);
        case 9:
            return Double9D.wrap(data, shape);
        default:
            throw new IllegalArgumentException("Invalid shape.");
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
