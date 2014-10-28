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

import mitiv.array.impl.FlatByte5D;
import mitiv.array.impl.StriddenByte5D;
import mitiv.base.Shape;
import mitiv.base.Shaped;
import mitiv.base.Traits;
import mitiv.base.mapping.ByteFunction;
import mitiv.base.mapping.ByteScanner;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.NonConformableArrayException;
import mitiv.base.indexing.Range;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.random.ByteGenerator;


/**
 * Define class for comprehensive 5-dimensional arrays of byte's.
 *
 * @author Éric Thiébaut.
 */
public abstract class Byte5D extends Array5D implements ByteArray {

    protected Byte5D(int dim1, int dim2, int dim3, int dim4, int dim5) {
        super(dim1,dim2,dim3,dim4,dim5);
    }

    protected Byte5D(int[] dims) {
        super(dims);
    }

    protected Byte5D(Shape shape) {
        super(shape);
    }

    @Override
    public final int getType() {
        return type;
    }

    /**
     * Query the value stored at a given position.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param i5 - The index along the 5th dimension.
     * @return The value stored at position {@code (i1,i2,i3,i4,i5)}.
     */
    public abstract byte get(int i1, int i2, int i3, int i4, int i5);

    /**
     * Set the value at a given position.
     * @param i1    - The index along the 1st dimension.
     * @param i2    - The index along the 2nd dimension.
     * @param i3    - The index along the 3rd dimension.
     * @param i4    - The index along the 4th dimension.
     * @param i5    - The index along the 5th dimension.
     * @param value - The value to store at position {@code (i1,i2,i3,i4,i5)}.
     */
    public abstract void set(int i1, int i2, int i3, int i4, int i5, byte value);

    /*=======================================================================*/
    /* Provide default (non-optimized, except for the loop ordering)
     * implementation of methods that can be coded solely with the "set"
     * and "get" methods. */

    @Override
    public void fill(byte value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, value);
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, value);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void increment(byte value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, (byte)(get(i1,i2,i3,i4,i5) + value));
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, (byte)(get(i1,i2,i3,i4,i5) + value));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void decrement(byte value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, (byte)(get(i1,i2,i3,i4,i5) - value));
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, (byte)(get(i1,i2,i3,i4,i5) - value));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void scale(byte value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, (byte)(get(i1,i2,i3,i4,i5) * value));
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, (byte)(get(i1,i2,i3,i4,i5) * value));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void map(ByteFunction function) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, function.apply(get(i1,i2,i3,i4,i5)));
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, function.apply(get(i1,i2,i3,i4,i5)));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void fill(ByteGenerator generator) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, generator.nextByte());
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, generator.nextByte());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void scan(ByteScanner scanner)  {
        boolean initialized = false;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                if (initialized) {
                                    scanner.update(get(i1,i2,i3,i4,i5));
                                } else {
                                    scanner.initialize(get(i1,i2,i3,i4,i5));
                                    initialized = true;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                if (initialized) {
                                    scanner.update(get(i1,i2,i3,i4,i5));
                                } else {
                                    scanner.initialize(get(i1,i2,i3,i4,i5));
                                    initialized = true;
                                }
                            }
                        }
                    }
                }
            }
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
        byte[] out = new byte[number];
        int i = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            for (int i4 = 0; i4 < dim4; ++i4) {
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            out[++i] = get(i1,i2,i3,i4,i5);
                        }
                    }
                }
            }
        }
        return out;
    }

    @Override
    public byte[] flatten() {
        return flatten(false);
    }

    @Override
    public int min() {
        int minValue = (int)(get(0,0,0,0,0) & 0xFF);
        boolean skip = true;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                if (skip) {
                                    skip = false;
                                } else {
                                    int value = (int)(get(i1,i2,i3,i4,i5) & 0xFF);
                                    if (value < minValue) {
                                        minValue = value;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                if (skip) {
                                    skip = false;
                                } else {
                                    int value = (int)(get(i1,i2,i3,i4,i5) & 0xFF);
                                    if (value < minValue) {
                                        minValue = value;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return minValue;
    }

    @Override
    public int max() {
        int maxValue = (int)(get(0,0,0,0,0) & 0xFF);
        boolean skip = true;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                if (skip) {
                                    skip = false;
                                } else {
                                    int value = (int)(get(i1,i2,i3,i4,i5) & 0xFF);
                                    if (value > maxValue) {
                                        maxValue = value;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                if (skip) {
                                    skip = false;
                                } else {
                                    int value = (int)(get(i1,i2,i3,i4,i5) & 0xFF);
                                    if (value > maxValue) {
                                        maxValue = value;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return maxValue;
    }

    @Override
    public int[] getMinAndMax() {
        int[] result = new int[2];
        getMinAndMax(result);
        return result;
    }

    @Override
    public void getMinAndMax(int[] mm) {
        int minValue = (int)(get(0,0,0,0,0) & 0xFF);
        int maxValue = minValue;
        boolean skip = true;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                if (skip) {
                                    skip = false;
                                } else {
                                    int value = (int)(get(i1,i2,i3,i4,i5) & 0xFF);
                                    if (value < minValue) {
                                        minValue = value;
                                    }
                                    if (value > maxValue) {
                                        maxValue = value;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                if (skip) {
                                    skip = false;
                                } else {
                                    int value = (int)(get(i1,i2,i3,i4,i5) & 0xFF);
                                    if (value < minValue) {
                                        minValue = value;
                                    }
                                    if (value > maxValue) {
                                        maxValue = value;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        mm[0] = minValue;
        mm[1] = maxValue;
    }

    @Override
    public int sum() {
        int totalValue = 0;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                totalValue += (int)(get(i1,i2,i3,i4,i5) & 0xFF);
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                totalValue += (int)(get(i1,i2,i3,i4,i5) & 0xFF);
                            }
                        }
                    }
                }
            }
        }
        return totalValue;
    }

    @Override
    public double average() {
        return (double)sum()/(double)number;
    }

    /**
     * Convert instance into a Byte5D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Byte5D whose values has been converted into byte's
     *         from those of {@code this}.
     */
    @Override
    public Byte5D toByte() {
        return this;
    }
    /**
     * Convert instance into a Short5D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Short5D whose values has been converted into short's
     *         from those of {@code this}.
     */
    @Override
    public Short5D toShort() {
        short[] out = new short[number];
        int i = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            for (int i4 = 0; i4 < dim4; ++i4) {
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            out[++i] = (short)get(i1,i2,i3,i4,i5);
                        }
                    }
                }
            }
        }
        return Short5D.wrap(out, dim1, dim2, dim3, dim4, dim5);
    }
    /**
     * Convert instance into an Int5D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return An Int5D whose values has been converted into int's
     *         from those of {@code this}.
     */
    @Override
    public Int5D toInt() {
        int[] out = new int[number];
        int i = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            for (int i4 = 0; i4 < dim4; ++i4) {
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            out[++i] = (int)get(i1,i2,i3,i4,i5);
                        }
                    }
                }
            }
        }
        return Int5D.wrap(out, dim1, dim2, dim3, dim4, dim5);
    }
    /**
     * Convert instance into a Long5D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Long5D whose values has been converted into long's
     *         from those of {@code this}.
     */
    @Override
    public Long5D toLong() {
        long[] out = new long[number];
        int i = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            for (int i4 = 0; i4 < dim4; ++i4) {
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            out[++i] = (long)get(i1,i2,i3,i4,i5);
                        }
                    }
                }
            }
        }
        return Long5D.wrap(out, dim1, dim2, dim3, dim4, dim5);
    }
    /**
     * Convert instance into a Float5D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Float5D whose values has been converted into float's
     *         from those of {@code this}.
     */
    @Override
    public Float5D toFloat() {
        float[] out = new float[number];
        int i = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            for (int i4 = 0; i4 < dim4; ++i4) {
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            out[++i] = (float)get(i1,i2,i3,i4,i5);
                        }
                    }
                }
            }
        }
        return Float5D.wrap(out, dim1, dim2, dim3, dim4, dim5);
    }
    /**
     * Convert instance into a Double5D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Double5D whose values has been converted into double's
     *         from those of {@code this}.
     */
    @Override
    public Double5D toDouble() {
        double[] out = new double[number];
        int i = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            for (int i4 = 0; i4 < dim4; ++i4) {
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            out[++i] = (double)get(i1,i2,i3,i4,i5);
                        }
                    }
                }
            }
        }
        return Double5D.wrap(out, dim1, dim2, dim3, dim4, dim5);
    }

    @Override
    public Byte5D copy() {
        return new FlatByte5D(flatten(true), shape);
    }

    @Override
    public void assign(ShapedArray arr) {
        if (! getShape().equals(arr.getShape())) {
            throw new NonConformableArrayException("Source and destination must have the same shape.");
        }
        Byte5D src;
        if (arr.getType() == Traits.BYTE) {
            src = (Byte5D)arr;
        } else {
            src = (Byte5D)arr.toByte();
        }
        // FIXME: do assignation and conversion at the same time
        if (getOrder() == ROW_MAJOR && src.getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                set(i1,i2,i3,i4,i5, src.get(i1,i2,i3,i4,i5));
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, src.get(i1,i2,i3,i4,i5));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void assign(ShapedVector vec) {
        if (! getShape().equals(vec.getShape())) {
            throw new NonConformableArrayException("Source and destination must have the same shape.");
        }
        // FIXME: much too slow and may be skipped if data are identical (and array is flat)
        int i = -1;
        if (vec.getType() == Traits.DOUBLE) {
            DoubleShapedVector src = (DoubleShapedVector)vec;
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, (byte)src.get(++i));
                            }
                        }
                    }
                }
            }
        } else if (vec.getType() == Traits.FLOAT) {
            FloatShapedVector src = (FloatShapedVector)vec;
            for (int i5 = 0; i5 < dim5; ++i5) {
                for (int i4 = 0; i4 < dim4; ++i4) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                set(i1,i2,i3,i4,i5, (byte)src.get(++i));
                            }
                        }
                    }
                }
            }
        } else {
            throw new IllegalTypeException();
        }
    }


    /*=======================================================================*/
    /* ARRAY FACTORIES */

    @Override
    public Byte5D create() {
        return new FlatByte5D(getShape());
    }

    /**
     * Create a 5D array of byte's with given dimensions.
     * <p>
     * This method creates a 5D array of byte's with zero offset, contiguous
     * elements and column-major order.  All dimensions must at least 1.
     * @param dim1 - The 1st dimension of the 5D array.
     * @param dim2 - The 2nd dimension of the 5D array.
     * @param dim3 - The 3rd dimension of the 5D array.
     * @param dim4 - The 4th dimension of the 5D array.
     * @param dim5 - The 5th dimension of the 5D array.
     * @return A new 5D array of byte's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Byte5D create(int dim1, int dim2, int dim3, int dim4, int dim5) {
        return new FlatByte5D(dim1,dim2,dim3,dim4,dim5);
    }

    /**
     * Create a 5D array of byte's with given shape.
     * <p>
     * This method creates a 5D array of byte's with zero offset, contiguous
     * elements and column-major order.
     * @param dims - The list of dimensions of the 5D array (all dimensions
     *               must at least 1).  This argument is not referenced by
     *               the returned object and its contents can be modified
     *               after calling this method.
     * @return A new 5D array of byte's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Byte5D create(int[] dims) {
        return new FlatByte5D(dims);
    }

    /**
     * Create a 5D array of byte's with given shape.
     * <p>
     * This method creates a 5D array of byte's with zero offset, contiguous
     * elements and column-major order.
     * @param shape      - The shape of the 5D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 5D array of byte's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Byte5D create(Shape shape) {
        return new FlatByte5D(shape);
    }

    /**
     * Wrap an existing array in a 5D array of byte's with given dimensions.
     * <p>
     * The returned 5D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5) = data[i1 + dim1*(i2 + dim2*(i3 + dim3*(i4 + dim4*i5)))]</pre>
     * with {@code arr} the returned 5D array.
     * @param data - The data to wrap in the 5D array.
     * @param dim1 - The 1st dimension of the 5D array.
     * @param dim2 - The 2nd dimension of the 5D array.
     * @param dim3 - The 3rd dimension of the 5D array.
     * @param dim4 - The 4th dimension of the 5D array.
     * @param dim5 - The 5th dimension of the 5D array.
     * @return A 5D array sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Byte5D wrap(byte[] data, int dim1, int dim2, int dim3, int dim4, int dim5) {
        return new FlatByte5D(data, dim1,dim2,dim3,dim4,dim5);
    }

    /**
     * Wrap an existing array in a 5D array of byte's with given shape.
     * <p>
     * The returned 5D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5) = data[i1 + shape[0]*(i2 + shape[1]*(i3 + shape[2]*(i4 + shape[3]*i5)))]</pre>
     * with {@code arr} the returned 5D array.
     * @param data - The data to wrap in the 5D array.
     * @param dims - The list of dimensions of the 5D array.  This argument is
     *                not referenced by the returned object and its contents
     *                can be modified after the call to this method.
     * @return A new 5D array of byte's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Byte5D wrap(byte[] data, int[] dims) {
        return new FlatByte5D(data, dims);
    }

    /**
     * Wrap an existing array in a 5D array of byte's with given shape.
     * <p>
     * The returned 5D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5) = data[i1 + shape[0]*(i2 + shape[1]*(i3 + shape[2]*(i4 + shape[3]*i5)))]</pre>
     * with {@code arr} the returned 5D array.
     * @param data       - The data to wrap in the 5D array.
     * @param shape      - The shape of the 5D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 5D array of byte's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Byte5D wrap(byte[] data, Shape shape) {
        return new FlatByte5D(data, shape);
    }

    /**
     * Wrap an existing array in a 5D array of byte's with given dimensions,
     * strides and offset.
     * <p>
     * This creates a 5D array of dimensions {{@code dim1,dim2,dim3,dim4,dim5}}
     * sharing (part of) the contents of {@code data} in arbitrary storage
     * order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5) = data[offset + stride1*i1 + stride2*i2 + stride3*i3 + stride4*i4 + stride5*i5]</pre>
     * with {@code arr} the returned 5D array.
     * @param data    - The array to wrap in the 5D array.
     * @param offset  - The offset in {@code data} of element (0,0,0,0,0) of
     *                  the 5D array.
     * @param stride1 - The stride along the 1st dimension.
     * @param stride2 - The stride along the 2nd dimension.
     * @param stride3 - The stride along the 3rd dimension.
     * @param stride4 - The stride along the 4th dimension.
     * @param stride5 - The stride along the 5th dimension.
     * @param dim1    - The 1st dimension of the 5D array.
     * @param dim2    - The 2nd dimension of the 5D array.
     * @param dim3    - The 3rd dimension of the 5D array.
     * @param dim4    - The 4th dimension of the 5D array.
     * @param dim5    - The 5th dimension of the 5D array.
     * @return A 5D array sharing the elements of <b>data</b>.
     */
    public static Byte5D wrap(byte[] data,
            int offset, int stride1, int stride2, int stride3, int stride4, int stride5, int dim1, int dim2, int dim3, int dim4, int dim5) {
        return new StriddenByte5D(data, offset, stride1,stride2,stride3,stride4,stride5, dim1,dim2,dim3,dim4,dim5);
    }

    /**
     * Get a slice of the array.
     *
     * @param idx - The index of the slice along the last dimension of
     *              the array.  The same indexing rules as for
     *              {@link mitiv.base.indexing.Range} apply for negative
     *              index: 0 for the first, 1 for the second, -1 for the
     *              last, -2 for penultimate, <i>etc.</i>
     * @return A Byte4D view on the given slice of the array.
     */
    public abstract Byte4D slice(int idx);

    /**
     * Get a slice of the array.
     *
     * @param idx - The index of the slice along the last dimension of
     *              the array.
     * @param dim - The dimension to slice.  For these two arguments,
     *              the same indexing rules as for
     *              {@link mitiv.base.indexing.Range} apply for negative
     *              index: 0 for the first, 1 for the second, -1 for the
     *              last, -2 for penultimate, <i>etc.</i>
     *
     * @return A Byte4D view on the given slice of the array.
     */
    public abstract Byte4D slice(int idx, int dim);

    /**
     * Get a view of the array for given ranges of indices.
     *
     * @param rng1 - The range of indices to select along 1st dimension
     *               (or {@code null} to select all.
     * @param rng2 - The range of indices to select along 2nd dimension
     *               (or {@code null} to select all.
     * @param rng3 - The range of indices to select along 3rd dimension
     *               (or {@code null} to select all.
     * @param rng4 - The range of indices to select along 4th dimension
     *               (or {@code null} to select all.
     * @param rng5 - The range of indices to select along 5th dimension
     *               (or {@code null} to select all.
     *
     * @return A Byte5D view for the given ranges of the array.
     */
    public abstract Byte5D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5);

    /**
     * Get a view of the array for given ranges of indices.
     *
     * @param idx1 - The list of indices to select along 1st dimension
     *               (or {@code null} to select all.
     * @param idx2 - The list of indices to select along 2nd dimension
     *               (or {@code null} to select all.
     * @param idx3 - The list of indices to select along 3rd dimension
     *               (or {@code null} to select all.
     * @param idx4 - The list of indices to select along 4th dimension
     *               (or {@code null} to select all.
     * @param idx5 - The list of indices to select along 5th dimension
     *               (or {@code null} to select all.
     *
     * @return A Byte5D view for the given index selections of the
     *         array.
     */
    public abstract Byte5D view(int[] idx1, int[] idx2, int[] idx3, int[] idx4, int[] idx5);

    /**
     * Get a view of the array as a 1D array.
     *
     * @return A 1D view of the array.
     */
    @Override
    public abstract Byte1D as1D();

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
