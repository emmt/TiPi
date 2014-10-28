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

import mitiv.array.impl.FlatShort8D;
import mitiv.array.impl.StriddenShort8D;
import mitiv.base.Shape;
import mitiv.base.Shaped;
import mitiv.base.Traits;
import mitiv.base.mapping.ShortFunction;
import mitiv.base.mapping.ShortScanner;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.NonConformableArrayException;
import mitiv.base.indexing.Range;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.random.ShortGenerator;


/**
 * Define class for comprehensive 8-dimensional arrays of short's.
 *
 * @author Éric Thiébaut.
 */
public abstract class Short8D extends Array8D implements ShortArray {

    protected Short8D(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        super(dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8);
    }

    protected Short8D(int[] dims) {
        super(dims);
    }

    protected Short8D(Shape shape) {
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
     * @param i6 - The index along the 6th dimension.
     * @param i7 - The index along the 7th dimension.
     * @param i8 - The index along the 8th dimension.
     * @return The value stored at position {@code (i1,i2,i3,i4,i5,i6,i7,i8)}.
     */
    public abstract short get(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    /**
     * Set the value at a given position.
     * @param i1    - The index along the 1st dimension.
     * @param i2    - The index along the 2nd dimension.
     * @param i3    - The index along the 3rd dimension.
     * @param i4    - The index along the 4th dimension.
     * @param i5    - The index along the 5th dimension.
     * @param i6    - The index along the 6th dimension.
     * @param i7    - The index along the 7th dimension.
     * @param i8    - The index along the 8th dimension.
     * @param value - The value to store at position {@code (i1,i2,i3,i4,i5,i6,i7,i8)}.
     */
    public abstract void set(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, short value);

    /*=======================================================================*/
    /* Provide default (non-optimized, except for the loop ordering)
     * implementation of methods that can be coded solely with the "set"
     * and "get" methods. */

    @Override
    public void fill(short value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void increment(short value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, (short)(get(i1,i2,i3,i4,i5,i6,i7,i8) + value));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, (short)(get(i1,i2,i3,i4,i5,i6,i7,i8) + value));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void decrement(short value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, (short)(get(i1,i2,i3,i4,i5,i6,i7,i8) - value));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, (short)(get(i1,i2,i3,i4,i5,i6,i7,i8) - value));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void scale(short value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, (short)(get(i1,i2,i3,i4,i5,i6,i7,i8) * value));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, (short)(get(i1,i2,i3,i4,i5,i6,i7,i8) * value));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void map(ShortFunction function) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, function.apply(get(i1,i2,i3,i4,i5,i6,i7,i8)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, function.apply(get(i1,i2,i3,i4,i5,i6,i7,i8)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void fill(ShortGenerator generator) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, generator.nextShort());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, generator.nextShort());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void scan(ShortScanner scanner)  {
        boolean initialized = false;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            if (initialized) {
                                                scanner.update(get(i1,i2,i3,i4,i5,i6,i7,i8));
                                            } else {
                                                scanner.initialize(get(i1,i2,i3,i4,i5,i6,i7,i8));
                                                initialized = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            if (initialized) {
                                                scanner.update(get(i1,i2,i3,i4,i5,i6,i7,i8));
                                            } else {
                                                scanner.initialize(get(i1,i2,i3,i4,i5,i6,i7,i8));
                                                initialized = true;
                                            }
                                        }
                                    }
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
     * @see devel.eric.array.base.ShortArray#flatten(boolean)
     */
    @Override
    public short[] flatten(boolean forceCopy) {
        /* Copy the elements in column-major order. */
        short[] out = new short[number];
        int i = -1;
        for (int i8 = 0; i8 < dim8; ++i8) {
            for (int i7 = 0; i7 < dim7; ++i7) {
                for (int i6 = 0; i6 < dim6; ++i6) {
                    for (int i5 = 0; i5 < dim5; ++i5) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i3 = 0; i3 < dim3; ++i3) {
                                for (int i2 = 0; i2 < dim2; ++i2) {
                                    for (int i1 = 0; i1 < dim1; ++i1) {
                                        out[++i] = get(i1,i2,i3,i4,i5,i6,i7,i8);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return out;
    }

    @Override
    public short[] flatten() {
        return flatten(false);
    }

    @Override
    public short min() {
        short minValue = get(0,0,0,0,0,0,0,0);
        boolean skip = true;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            if (skip) {
                                                skip = false;
                                            } else {
                                                short value = get(i1,i2,i3,i4,i5,i6,i7,i8);
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
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            if (skip) {
                                                skip = false;
                                            } else {
                                                short value = get(i1,i2,i3,i4,i5,i6,i7,i8);
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
                }
            }
        }
        return minValue;
    }

    @Override
    public short max() {
        short maxValue = get(0,0,0,0,0,0,0,0);
        boolean skip = true;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            if (skip) {
                                                skip = false;
                                            } else {
                                                short value = get(i1,i2,i3,i4,i5,i6,i7,i8);
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
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            if (skip) {
                                                skip = false;
                                            } else {
                                                short value = get(i1,i2,i3,i4,i5,i6,i7,i8);
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
                }
            }
        }
        return maxValue;
    }

    @Override
    public short[] getMinAndMax() {
        short[] result = new short[2];
        getMinAndMax(result);
        return result;
    }

    @Override
    public void getMinAndMax(short[] mm) {
        short minValue = get(0,0,0,0,0,0,0,0);
        short maxValue = minValue;
        boolean skip = true;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            if (skip) {
                                                skip = false;
                                            } else {
                                                short value = get(i1,i2,i3,i4,i5,i6,i7,i8);
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
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            if (skip) {
                                                skip = false;
                                            } else {
                                                short value = get(i1,i2,i3,i4,i5,i6,i7,i8);
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
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            totalValue += get(i1,i2,i3,i4,i5,i6,i7,i8);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            totalValue += get(i1,i2,i3,i4,i5,i6,i7,i8);
                                        }
                                    }
                                }
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
     * Convert instance into a Byte8D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Byte8D whose values has been converted into byte's
     *         from those of {@code this}.
     */
    @Override
    public Byte8D toByte() {
        byte[] out = new byte[number];
        int i = -1;
        for (int i8 = 0; i8 < dim8; ++i8) {
            for (int i7 = 0; i7 < dim7; ++i7) {
                for (int i6 = 0; i6 < dim6; ++i6) {
                    for (int i5 = 0; i5 < dim5; ++i5) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i3 = 0; i3 < dim3; ++i3) {
                                for (int i2 = 0; i2 < dim2; ++i2) {
                                    for (int i1 = 0; i1 < dim1; ++i1) {
                                        out[++i] = (byte)get(i1,i2,i3,i4,i5,i6,i7,i8);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Byte8D.wrap(out, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
    }
    /**
     * Convert instance into a Short8D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Short8D whose values has been converted into short's
     *         from those of {@code this}.
     */
    @Override
    public Short8D toShort() {
        return this;
    }
    /**
     * Convert instance into an Int8D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return An Int8D whose values has been converted into int's
     *         from those of {@code this}.
     */
    @Override
    public Int8D toInt() {
        int[] out = new int[number];
        int i = -1;
        for (int i8 = 0; i8 < dim8; ++i8) {
            for (int i7 = 0; i7 < dim7; ++i7) {
                for (int i6 = 0; i6 < dim6; ++i6) {
                    for (int i5 = 0; i5 < dim5; ++i5) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i3 = 0; i3 < dim3; ++i3) {
                                for (int i2 = 0; i2 < dim2; ++i2) {
                                    for (int i1 = 0; i1 < dim1; ++i1) {
                                        out[++i] = (int)get(i1,i2,i3,i4,i5,i6,i7,i8);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Int8D.wrap(out, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
    }
    /**
     * Convert instance into a Long8D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Long8D whose values has been converted into long's
     *         from those of {@code this}.
     */
    @Override
    public Long8D toLong() {
        long[] out = new long[number];
        int i = -1;
        for (int i8 = 0; i8 < dim8; ++i8) {
            for (int i7 = 0; i7 < dim7; ++i7) {
                for (int i6 = 0; i6 < dim6; ++i6) {
                    for (int i5 = 0; i5 < dim5; ++i5) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i3 = 0; i3 < dim3; ++i3) {
                                for (int i2 = 0; i2 < dim2; ++i2) {
                                    for (int i1 = 0; i1 < dim1; ++i1) {
                                        out[++i] = (long)get(i1,i2,i3,i4,i5,i6,i7,i8);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Long8D.wrap(out, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
    }
    /**
     * Convert instance into a Float8D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Float8D whose values has been converted into float's
     *         from those of {@code this}.
     */
    @Override
    public Float8D toFloat() {
        float[] out = new float[number];
        int i = -1;
        for (int i8 = 0; i8 < dim8; ++i8) {
            for (int i7 = 0; i7 < dim7; ++i7) {
                for (int i6 = 0; i6 < dim6; ++i6) {
                    for (int i5 = 0; i5 < dim5; ++i5) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i3 = 0; i3 < dim3; ++i3) {
                                for (int i2 = 0; i2 < dim2; ++i2) {
                                    for (int i1 = 0; i1 < dim1; ++i1) {
                                        out[++i] = (float)get(i1,i2,i3,i4,i5,i6,i7,i8);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Float8D.wrap(out, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
    }
    /**
     * Convert instance into a Double8D.
     * <p>
     * The operation is lazy, in the sense that {@code this} is returned if it
     * is already of the requested type.
     *
     * @return A Double8D whose values has been converted into double's
     *         from those of {@code this}.
     */
    @Override
    public Double8D toDouble() {
        double[] out = new double[number];
        int i = -1;
        for (int i8 = 0; i8 < dim8; ++i8) {
            for (int i7 = 0; i7 < dim7; ++i7) {
                for (int i6 = 0; i6 < dim6; ++i6) {
                    for (int i5 = 0; i5 < dim5; ++i5) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i3 = 0; i3 < dim3; ++i3) {
                                for (int i2 = 0; i2 < dim2; ++i2) {
                                    for (int i1 = 0; i1 < dim1; ++i1) {
                                        out[++i] = (double)get(i1,i2,i3,i4,i5,i6,i7,i8);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Double8D.wrap(out, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
    }

    @Override
    public Short8D copy() {
        return new FlatShort8D(flatten(true), shape);
    }

    @Override
    public void assign(ShapedArray arr) {
        if (! getShape().equals(arr.getShape())) {
            throw new NonConformableArrayException("Source and destination must have the same shape.");
        }
        Short8D src;
        if (arr.getType() == Traits.SHORT) {
            src = (Short8D)arr;
        } else {
            src = (Short8D)arr.toShort();
        }
        // FIXME: do assignation and conversion at the same time
        if (getOrder() == ROW_MAJOR && src.getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                for (int i2 = 0; i2 < dim2; ++i2) {
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, src.get(i1,i2,i3,i4,i5,i6,i7,i8));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, src.get(i1,i2,i3,i4,i5,i6,i7,i8));
                                        }
                                    }
                                }
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
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, (short)src.get(++i));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (vec.getType() == Traits.FLOAT) {
            FloatShapedVector src = (FloatShapedVector)vec;
            for (int i8 = 0; i8 < dim8; ++i8) {
                for (int i7 = 0; i7 < dim7; ++i7) {
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            set(i1,i2,i3,i4,i5,i6,i7,i8, (short)src.get(++i));
                                        }
                                    }
                                }
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
    public Short8D create() {
        return new FlatShort8D(getShape());
    }

    /**
     * Create a 8D array of short's with given dimensions.
     * <p>
     * This method creates a 8D array of short's with zero offset, contiguous
     * elements and column-major order.  All dimensions must at least 1.
     * @param dim1 - The 1st dimension of the 8D array.
     * @param dim2 - The 2nd dimension of the 8D array.
     * @param dim3 - The 3rd dimension of the 8D array.
     * @param dim4 - The 4th dimension of the 8D array.
     * @param dim5 - The 5th dimension of the 8D array.
     * @param dim6 - The 6th dimension of the 8D array.
     * @param dim7 - The 7th dimension of the 8D array.
     * @param dim8 - The 8th dimension of the 8D array.
     * @return A new 8D array of short's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Short8D create(int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        return new FlatShort8D(dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8);
    }

    /**
     * Create a 8D array of short's with given shape.
     * <p>
     * This method creates a 8D array of short's with zero offset, contiguous
     * elements and column-major order.
     * @param dims - The list of dimensions of the 8D array (all dimensions
     *               must at least 1).  This argument is not referenced by
     *               the returned object and its contents can be modified
     *               after calling this method.
     * @return A new 8D array of short's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Short8D create(int[] dims) {
        return new FlatShort8D(dims);
    }

    /**
     * Create a 8D array of short's with given shape.
     * <p>
     * This method creates a 8D array of short's with zero offset, contiguous
     * elements and column-major order.
     * @param shape      - The shape of the 8D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 8D array of short's.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Short8D create(Shape shape) {
        return new FlatShort8D(shape);
    }

    /**
     * Wrap an existing array in a 8D array of short's with given dimensions.
     * <p>
     * The returned 8D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8) = data[i1 + dim1*(i2 + dim2*(i3 + dim3*(i4 + dim4*(i5 + dim5*(i6 + dim6*(i7 + dim7*i8))))))]</pre>
     * with {@code arr} the returned 8D array.
     * @param data - The data to wrap in the 8D array.
     * @param dim1 - The 1st dimension of the 8D array.
     * @param dim2 - The 2nd dimension of the 8D array.
     * @param dim3 - The 3rd dimension of the 8D array.
     * @param dim4 - The 4th dimension of the 8D array.
     * @param dim5 - The 5th dimension of the 8D array.
     * @param dim6 - The 6th dimension of the 8D array.
     * @param dim7 - The 7th dimension of the 8D array.
     * @param dim8 - The 8th dimension of the 8D array.
     * @return A 8D array sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Short8D wrap(short[] data, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        return new FlatShort8D(data, dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8);
    }

    /**
     * Wrap an existing array in a 8D array of short's with given shape.
     * <p>
     * The returned 8D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8) = data[i1 + shape[0]*(i2 + shape[1]*(i3 + shape[2]*(i4 + shape[3]*(i5 + shape[4]*(i6 + shape[5]*(i7 + shape[6]*i8))))))]</pre>
     * with {@code arr} the returned 8D array.
     * @param data - The data to wrap in the 8D array.
     * @param dims - The list of dimensions of the 8D array.  This argument is
     *                not referenced by the returned object and its contents
     *                can be modified after the call to this method.
     * @return A new 8D array of short's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Short8D wrap(short[] data, int[] dims) {
        return new FlatShort8D(data, dims);
    }

    /**
     * Wrap an existing array in a 8D array of short's with given shape.
     * <p>
     * The returned 8D array have zero offset, contiguous elements and
     * column-major storage order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8) = data[i1 + shape[0]*(i2 + shape[1]*(i3 + shape[2]*(i4 + shape[3]*(i5 + shape[4]*(i6 + shape[5]*(i7 + shape[6]*i8))))))]</pre>
     * with {@code arr} the returned 8D array.
     * @param data       - The data to wrap in the 8D array.
     * @param shape      - The shape of the 8D array.
     * @param cloneShape - If true, the <b>shape</b> argument is duplicated;
     *                     otherwise, the returned object will reference
     *                     <b>shape</b> whose contents <b><i>must not be
     *                     modified</i></b> while the returned object is in
     *                     use.
     * @return A new 8D array of short's sharing the elements of <b>data</b>.
     * @see {@link Shaped#COLUMN_MAJOR}
     */
    public static Short8D wrap(short[] data, Shape shape) {
        return new FlatShort8D(data, shape);
    }

    /**
     * Wrap an existing array in a 8D array of short's with given dimensions,
     * strides and offset.
     * <p>
     * This creates a 8D array of dimensions {{@code dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8}}
     * sharing (part of) the contents of {@code data} in arbitrary storage
     * order.  More specifically:
     * <pre>arr.get(i1,i2,i3,i4,i5,i6,i7,i8) = data[offset + stride1*i1 + stride2*i2 + stride3*i3 + stride4*i4 + stride5*i5 + stride6*i6 + stride7*i7 + stride8*i8]</pre>
     * with {@code arr} the returned 8D array.
     * @param data    - The array to wrap in the 8D array.
     * @param offset  - The offset in {@code data} of element (0,0,0,0,0,0,0,0) of
     *                  the 8D array.
     * @param stride1 - The stride along the 1st dimension.
     * @param stride2 - The stride along the 2nd dimension.
     * @param stride3 - The stride along the 3rd dimension.
     * @param stride4 - The stride along the 4th dimension.
     * @param stride5 - The stride along the 5th dimension.
     * @param stride6 - The stride along the 6th dimension.
     * @param stride7 - The stride along the 7th dimension.
     * @param stride8 - The stride along the 8th dimension.
     * @param dim1    - The 1st dimension of the 8D array.
     * @param dim2    - The 2nd dimension of the 8D array.
     * @param dim3    - The 3rd dimension of the 8D array.
     * @param dim4    - The 4th dimension of the 8D array.
     * @param dim5    - The 5th dimension of the 8D array.
     * @param dim6    - The 6th dimension of the 8D array.
     * @param dim7    - The 7th dimension of the 8D array.
     * @param dim8    - The 8th dimension of the 8D array.
     * @return A 8D array sharing the elements of <b>data</b>.
     */
    public static Short8D wrap(short[] data,
            int offset, int stride1, int stride2, int stride3, int stride4, int stride5, int stride6, int stride7, int stride8, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        return new StriddenShort8D(data, offset, stride1,stride2,stride3,stride4,stride5,stride6,stride7,stride8, dim1,dim2,dim3,dim4,dim5,dim6,dim7,dim8);
    }

    /**
     * Get a slice of the array.
     *
     * @param idx - The index of the slice along the last dimension of
     *              the array.  The same indexing rules as for
     *              {@link mitiv.base.indexing.Range} apply for negative
     *              index: 0 for the first, 1 for the second, -1 for the
     *              last, -2 for penultimate, <i>etc.</i>
     * @return A Short7D view on the given slice of the array.
     */
    public abstract Short7D slice(int idx);

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
     * @return A Short7D view on the given slice of the array.
     */
    public abstract Short7D slice(int idx, int dim);

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
     * @param rng6 - The range of indices to select along 6th dimension
     *               (or {@code null} to select all.
     * @param rng7 - The range of indices to select along 7th dimension
     *               (or {@code null} to select all.
     * @param rng8 - The range of indices to select along 8th dimension
     *               (or {@code null} to select all.
     *
     * @return A Short8D view for the given ranges of the array.
     */
    public abstract Short8D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5, Range rng6, Range rng7, Range rng8);

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
     * @param idx6 - The list of indices to select along 6th dimension
     *               (or {@code null} to select all.
     * @param idx7 - The list of indices to select along 7th dimension
     *               (or {@code null} to select all.
     * @param idx8 - The list of indices to select along 8th dimension
     *               (or {@code null} to select all.
     *
     * @return A Short8D view for the given index selections of the
     *         array.
     */
    public abstract Short8D view(int[] idx1, int[] idx2, int[] idx3, int[] idx4, int[] idx5, int[] idx6, int[] idx7, int[] idx8);

    /**
     * Get a view of the array as a 1D array.
     *
     * @return A 1D view of the array.
     */
    @Override
    public abstract Short1D as1D();

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
