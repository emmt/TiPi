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

package mitiv.array.impl;

import mitiv.array.Float1D;
import mitiv.array.Float4D;
import mitiv.array.Float5D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.FloatFunction;
import mitiv.base.mapping.FloatScanner;
import mitiv.random.FloatGenerator;
import mitiv.array.ArrayUtils;

/**
 * Selected implementation of 5-dimensional arrays of float's.
 *
 * This specific kind of arrays/views are accessed via indirection tables (one
 * for each dimension).
 *
 * @author Éric Thiébaut.
 */
public class SelectedFloat5D extends Float5D {
    static final int order = NONSPECIFIC_ORDER;
    final float[] data;
    final int[] idx1;
    final int[] idx2;
    final int[] idx3;
    final int[] idx4;
    final int[] idx5;

    /**
     * Create a new instance of a view via lists of selected indices.
     *
     * <p>
     * All lists of selected indices must be immutable as, for efficiency,
     * a simple reference is kept.
     * </p>
     */
    public SelectedFloat5D(float[] arr, int[] idx1, int[] idx2, int[] idx3, int[] idx4, int[] idx5) {
        super(idx1.length, idx2.length, idx3.length, idx4.length, idx5.length);
        this.data = arr;
        this.idx1 = idx1;
        this.idx2 = idx2;
        this.idx3 = idx3;
        this.idx4 = idx4;
        this.idx5 = idx5;
    }

    @Override
    public final void checkSanity() {
        int offsetMin = 0, offsetMax = 0, indexMin, indexMax;
         indexMin = indexMax = idx1[0];
        for (int i1 = 1; i1 < dim1; ++i1) {
            int index = idx1[i1];
            if (index < indexMin) indexMin = index;
            if (index > indexMax) indexMax = index;
        }
        offsetMin += indexMin;
        offsetMax += indexMax;
         indexMin = indexMax = idx2[0];
        for (int i2 = 1; i2 < dim2; ++i2) {
            int index = idx2[i2];
            if (index < indexMin) indexMin = index;
            if (index > indexMax) indexMax = index;
        }
        offsetMin += indexMin;
        offsetMax += indexMax;
         indexMin = indexMax = idx3[0];
        for (int i3 = 1; i3 < dim3; ++i3) {
            int index = idx3[i3];
            if (index < indexMin) indexMin = index;
            if (index > indexMax) indexMax = index;
        }
        offsetMin += indexMin;
        offsetMax += indexMax;
         indexMin = indexMax = idx4[0];
        for (int i4 = 1; i4 < dim4; ++i4) {
            int index = idx4[i4];
            if (index < indexMin) indexMin = index;
            if (index > indexMax) indexMax = index;
        }
        offsetMin += indexMin;
        offsetMax += indexMax;
         indexMin = indexMax = idx5[0];
        for (int i5 = 1; i5 < dim5; ++i5) {
            int index = idx5[i5];
            if (index < indexMin) indexMin = index;
            if (index > indexMax) indexMax = index;
        }
        offsetMin += indexMin;
        offsetMax += indexMax;
        if (offsetMin < 0 || offsetMax >= data.length) {
            throw new IndexOutOfBoundsException("Selected indices are out of bounds.");
        }
    }

    final int index(int i1, int i2, int i3, int i4, int i5) {
        return idx5[i5] + idx4[i4] + idx3[i3] + idx2[i2] + idx1[i1];
    }

    @Override
    public final float get(int i1, int i2, int i3, int i4, int i5) {
        return data[idx5[i5] + idx4[i4] + idx3[i3] + idx2[i2] + idx1[i1]];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int i5, float value) {
        data[idx5[i5] + idx4[i4] + idx3[i3] + idx2[i2] + idx1[i1]] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(float value) {
        for (int i5 = 0; i5 < dim5; ++i5) {
            int j5 = idx5[i5];
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = idx4[i4] + j5;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = idx3[i3] + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = idx2[i2] + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = idx1[i1] + j2;
                            data[j1] = value;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void fill(FloatGenerator generator) {
        for (int i5 = 0; i5 < dim5; ++i5) {
            int j5 = idx5[i5];
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = idx4[i4] + j5;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = idx3[i3] + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = idx2[i2] + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = idx1[i1] + j2;
                            data[j1] = generator.nextFloat();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void increment(float value) {
        for (int i5 = 0; i5 < dim5; ++i5) {
            int j5 = idx5[i5];
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = idx4[i4] + j5;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = idx3[i3] + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = idx2[i2] + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = idx1[i1] + j2;
                            data[j1] += value;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void decrement(float value) {
        for (int i5 = 0; i5 < dim5; ++i5) {
            int j5 = idx5[i5];
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = idx4[i4] + j5;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = idx3[i3] + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = idx2[i2] + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = idx1[i1] + j2;
                            data[j1] -= value;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void scale(float value) {
        for (int i5 = 0; i5 < dim5; ++i5) {
            int j5 = idx5[i5];
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = idx4[i4] + j5;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = idx3[i3] + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = idx2[i2] + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = idx1[i1] + j2;
                            data[j1] *= value;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void map(FloatFunction function) {
        for (int i5 = 0; i5 < dim5; ++i5) {
            int j5 = idx5[i5];
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = idx4[i4] + j5;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = idx3[i3] + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = idx2[i2] + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = idx1[i1] + j2;
                            data[j1] = function.apply(data[j1]);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void scan(FloatScanner scanner)  {
        boolean initialized = false;
        for (int i5 = 0; i5 < dim5; ++i5) {
            int j5 = idx5[i5];
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = idx4[i4] + j5;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = idx3[i3] + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = idx2[i2] + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = idx1[i1] + j2;
                            if (initialized) {
                                scanner.update(data[j1]);
                            } else {
                                scanner.initialize(data[j1]);
                                initialized = true;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public float[] flatten(boolean forceCopy) {
        float[] out = new float[number];
        int j = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            int j5 = idx5[i5];
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = idx4[i4] + j5;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = idx3[i3] + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = idx2[i2] + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = idx1[i1] + j2;
                            out[++j] = data[j1];
                        }
                    }
                }
            }
        }
        return out;
    }

    @Override
    public Float4D slice(int idx) {
        int[] idx1 = this.idx1;
        int offset = idx5[idx];
        if (offset != 0) {
            /* Add the offset to the first indirection table. */
            int length = idx1.length;
            int[] tmp = new int[length];
            for (int i = 0; i < length; ++i) {
                tmp[i] = idx1[i] + offset;
            }
            idx1 = tmp;
        }
        return new SelectedFloat4D(this.data, idx1, idx2, idx3, idx4);
    }

    @Override
    public Float4D slice(int idx, int dim) {
        if (dim < 0) {
            /* A negative index is taken with respect to the end. */
            dim += 5;
        }
        if (dim != 0) {
            throw new IndexOutOfBoundsException("Dimension index out of bounds.");
        }
        int[] idx1;
        int[] idx2;
        int[] idx3;
        int[] idx4;
        int offset;
        switch (dim) {
        case 0:
            offset = this.idx1[idx];
            idx1 = this.idx2;
            idx2 = this.idx3;
            idx3 = this.idx4;
            idx4 = this.idx5;
            break;
        case 1:
            idx1 = this.idx1;
            offset = this.idx2[idx];
            idx2 = this.idx3;
            idx3 = this.idx4;
            idx4 = this.idx5;
            break;
        case 2:
            idx1 = this.idx1;
            idx2 = this.idx2;
            offset = this.idx3[idx];
            idx3 = this.idx4;
            idx4 = this.idx5;
            break;
        case 3:
            idx1 = this.idx1;
            idx2 = this.idx2;
            idx3 = this.idx3;
            offset = this.idx4[idx];
            idx4 = this.idx5;
            break;
        case 4:
            idx1 = this.idx1;
            idx2 = this.idx2;
            idx3 = this.idx3;
            idx4 = this.idx4;
            offset = this.idx5[idx];
            break;
        default:
            throw new IndexOutOfBoundsException("Dimension index out of bounds.");
        }
        if (offset != 0) {
            /* Add the offset to the first indirection table. */
            int length = idx1.length;
            int[] tmp = new int[length];
            for (int i = 0; i < length; ++i) {
                tmp[i] = offset + idx1[i];
            }
            idx1 = tmp;
        }
        return new SelectedFloat4D(this.data, idx1, idx2, idx3, idx4);
    }

    @Override
    public Float5D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5) {
        int[] idx1 = ArrayUtils.select(this.idx1, rng1);
        int[] idx2 = ArrayUtils.select(this.idx2, rng2);
        int[] idx3 = ArrayUtils.select(this.idx3, rng3);
        int[] idx4 = ArrayUtils.select(this.idx4, rng4);
        int[] idx5 = ArrayUtils.select(this.idx5, rng5);
        if (idx1 == this.idx1 && idx2 == this.idx2 && idx3 == this.idx3 && idx4 == this.idx4 && idx5 == this.idx5) {
            return this;
        } else {
            return new SelectedFloat5D(this.data, idx1, idx2, idx3, idx4, idx5);
        }
    }

    @Override
    public Float5D view(int[] sel1, int[] sel2, int[] sel3, int[] sel4, int[] sel5) {
        int[] idx1 = ArrayUtils.select(this.idx1, sel1);
        int[] idx2 = ArrayUtils.select(this.idx2, sel2);
        int[] idx3 = ArrayUtils.select(this.idx3, sel3);
        int[] idx4 = ArrayUtils.select(this.idx4, sel4);
        int[] idx5 = ArrayUtils.select(this.idx5, sel5);
        if (idx1 == this.idx1 && idx2 == this.idx2 && idx3 == this.idx3 && idx4 == this.idx4 && idx5 == this.idx5) {
            return this;
        } else {
            return new SelectedFloat5D(this.data, idx1, idx2, idx3, idx4, idx5);
        }
    }

    @Override
    public Float1D as1D() {
        int[] idx = new int[number];
        int j = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            int j5 = idx5[i5];
            for (int i4 = 0; i4 < dim4; ++i4) {
                int j4 = idx4[i4] + j5;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    int j3 = idx3[i3] + j4;
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        int j2 = idx2[i2] + j3;
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            int j1 = idx1[i1] + j2;
                            idx[++j] = j1;
                        }
                    }
                }
            }
        }
        return new SelectedFloat1D(data, idx);
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
