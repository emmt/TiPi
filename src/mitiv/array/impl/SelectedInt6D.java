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

import mitiv.array.Int1D;
import mitiv.array.Int5D;
import mitiv.array.Int6D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.IntFunction;
import mitiv.base.mapping.IntScanner;
import mitiv.random.IntGenerator;


/**
 * Selected implementation of 6-dimensional arrays of int's.
 *
 * This specific kind of arrays/views are accessed via indirection tables (one
 * for each dimension).
 *
 * @author Éric Thiébaut.
 */
public class SelectedInt6D extends Int6D {
    static final int order = NONSPECIFIC_ORDER;
    final int[] data;
    final int[] idx1;
    final int[] idx2;
    final int[] idx3;
    final int[] idx4;
    final int[] idx5;
    final int[] idx6;

    /**
     * Create a new instance of a view via lists of selected indices.
     *
     * <p>
     * All lists of selected indices must be immutable as, for efficiency,
     * a simple reference is kept.
     * </p>
     */
    public SelectedInt6D(int[] arr, int[] idx1, int[] idx2, int[] idx3, int[] idx4, int[] idx5, int[] idx6) {
        super(idx1.length, idx2.length, idx3.length, idx4.length, idx5.length, idx6.length);
        this.data = arr;
        this.idx1 = idx1;
        this.idx2 = idx2;
        this.idx3 = idx3;
        this.idx4 = idx4;
        this.idx5 = idx5;
        this.idx6 = idx6;
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
        indexMin = indexMax = idx6[0];
        for (int i6 = 1; i6 < dim6; ++i6) {
            int index = idx6[i6];
            if (index < indexMin) indexMin = index;
            if (index > indexMax) indexMax = index;
        }
        offsetMin += indexMin;
        offsetMax += indexMax;
        if (offsetMin < 0 || offsetMax >= data.length) {
            throw new IndexOutOfBoundsException("Selected indices are out of bounds.");
        }
    }

    final int index(int i1, int i2, int i3, int i4, int i5, int i6) {
        return idx6[i6] + idx5[i5] + idx4[i4] + idx3[i3] + idx2[i2] + idx1[i1];
    }

    @Override
    public final int get(int i1, int i2, int i3, int i4, int i5, int i6) {
        return data[idx6[i6] + idx5[i5] + idx4[i4] + idx3[i3] + idx2[i2] + idx1[i1]];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int i5, int i6, int value) {
        data[idx6[i6] + idx5[i5] + idx4[i4] + idx3[i3] + idx2[i2] + idx1[i1]] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(int value) {
        for (int i6 = 0; i6 < dim6; ++i6) {
            int j6 = idx6[i6];
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = idx5[i5] + j6;
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
    }

    @Override
    public void fill(IntGenerator generator) {
        for (int i6 = 0; i6 < dim6; ++i6) {
            int j6 = idx6[i6];
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = idx5[i5] + j6;
                for (int i4 = 0; i4 < dim4; ++i4) {
                    int j4 = idx4[i4] + j5;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = idx3[i3] + j4;
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            int j2 = idx2[i2] + j3;
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                int j1 = idx1[i1] + j2;
                                data[j1] = generator.nextInt();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void increment(int value) {
        for (int i6 = 0; i6 < dim6; ++i6) {
            int j6 = idx6[i6];
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = idx5[i5] + j6;
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
    }

    @Override
    public void decrement(int value) {
        for (int i6 = 0; i6 < dim6; ++i6) {
            int j6 = idx6[i6];
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = idx5[i5] + j6;
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
    }

    @Override
    public void scale(int value) {
        for (int i6 = 0; i6 < dim6; ++i6) {
            int j6 = idx6[i6];
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = idx5[i5] + j6;
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
    }

    @Override
    public void map(IntFunction function) {
        for (int i6 = 0; i6 < dim6; ++i6) {
            int j6 = idx6[i6];
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = idx5[i5] + j6;
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
    }

    @Override
    public void scan(IntScanner scanner)  {
        boolean initialized = false;
        for (int i6 = 0; i6 < dim6; ++i6) {
            int j6 = idx6[i6];
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = idx5[i5] + j6;
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
    }

    @Override
    public final boolean isFlat() {
        return false;
    }

    @Override
    public int[] flatten(boolean forceCopy) {
        int[] out = new int[number];
        int j = -1;
        for (int i6 = 0; i6 < dim6; ++i6) {
            int j6 = idx6[i6];
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = idx5[i5] + j6;
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
        }
        return out;
    }

    @Override
    public Int5D slice(int idx) {
        int[] sliceIndex1;
        int sliceOffset = idx6[Helper.fixIndex(idx, dim6)];
        if (sliceOffset == 0) {
            sliceIndex1 = idx1;
        } else {
            /* Add the offset to the first indirection table. */
            sliceIndex1 = new int[dim1];
            for (int i = 0; i < dim1; ++i) {
                sliceIndex1[i] = idx1[i] + sliceOffset;
            }
        }
        return new SelectedInt5D(data, sliceIndex1, idx2, idx3, idx4, idx5);
    }

    @Override
    public Int5D slice(int idx, int dim) {
        int sliceOffset;
        int[] sliceIndex1;
        int[] sliceIndex2;
        int[] sliceIndex3;
        int[] sliceIndex4;
        int[] sliceIndex5;
        dim = Helper.fixSliceIndex(dim, 6);
        if (dim == 0) {
            /* Slice along 1st dimension. */
            sliceOffset = idx1[Helper.fixIndex(idx, dim1)];
            sliceIndex1 = idx2;
            sliceIndex2 = idx3;
            sliceIndex3 = idx4;
            sliceIndex4 = idx5;
            sliceIndex5 = idx6;
        } else if (dim == 1) {
            /* Slice along 2nd dimension. */
            sliceIndex1 = idx1;
            sliceOffset = idx2[Helper.fixIndex(idx, dim2)];
            sliceIndex2 = idx3;
            sliceIndex3 = idx4;
            sliceIndex4 = idx5;
            sliceIndex5 = idx6;
        } else if (dim == 2) {
            /* Slice along 3rd dimension. */
            sliceIndex1 = idx1;
            sliceIndex2 = idx2;
            sliceOffset = idx3[Helper.fixIndex(idx, dim3)];
            sliceIndex3 = idx4;
            sliceIndex4 = idx5;
            sliceIndex5 = idx6;
        } else if (dim == 3) {
            /* Slice along 4th dimension. */
            sliceIndex1 = idx1;
            sliceIndex2 = idx2;
            sliceIndex3 = idx3;
            sliceOffset = idx4[Helper.fixIndex(idx, dim4)];
            sliceIndex4 = idx5;
            sliceIndex5 = idx6;
        } else if (dim == 4) {
            /* Slice along 5th dimension. */
            sliceIndex1 = idx1;
            sliceIndex2 = idx2;
            sliceIndex3 = idx3;
            sliceIndex4 = idx4;
            sliceOffset = idx5[Helper.fixIndex(idx, dim5)];
            sliceIndex5 = idx6;
        } else {
            /* Slice along 6th dimension. */
            sliceIndex1 = idx1;
            sliceIndex2 = idx2;
            sliceIndex3 = idx3;
            sliceIndex4 = idx4;
            sliceIndex5 = idx5;
            sliceOffset = idx6[Helper.fixIndex(idx, dim6)];
        }
        if (sliceOffset != 0) {
            /* Add the offset to the first indirection table. */
            int length = sliceIndex1.length;
            int[] tempIndex = new int[length];
            for (int i = 0; i < length; ++i) {
                tempIndex[i] = sliceOffset + sliceIndex1[i];
            }
            sliceIndex1 = tempIndex;
        }
        return new SelectedInt5D(data, sliceIndex1, sliceIndex2, sliceIndex3, sliceIndex4, sliceIndex5);
    }

    @Override
    public Int6D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5, Range rng6) {
        int[] viewIndex1 = Helper.select(idx1, rng1);
        int[] viewIndex2 = Helper.select(idx2, rng2);
        int[] viewIndex3 = Helper.select(idx3, rng3);
        int[] viewIndex4 = Helper.select(idx4, rng4);
        int[] viewIndex5 = Helper.select(idx5, rng5);
        int[] viewIndex6 = Helper.select(idx6, rng6);
        if (viewIndex1 == idx1 && viewIndex2 == idx2 && viewIndex3 == idx3 && viewIndex4 == idx4 && viewIndex5 == idx5 && viewIndex6 == idx6) {
            return this;
        } else {
            return new SelectedInt6D(data, viewIndex1, viewIndex2, viewIndex3, viewIndex4, viewIndex5, viewIndex6);
        }
    }

    @Override
    public Int6D view(int[] sel1, int[] sel2, int[] sel3, int[] sel4, int[] sel5, int[] sel6) {
        int[] viewIndex1 = Helper.select(idx1, sel1);
        int[] viewIndex2 = Helper.select(idx2, sel2);
        int[] viewIndex3 = Helper.select(idx3, sel3);
        int[] viewIndex4 = Helper.select(idx4, sel4);
        int[] viewIndex5 = Helper.select(idx5, sel5);
        int[] viewIndex6 = Helper.select(idx6, sel6);
        if (viewIndex1 == idx1 && viewIndex2 == idx2 && viewIndex3 == idx3 && viewIndex4 == idx4 && viewIndex5 == idx5 && viewIndex6 == idx6) {
            return this;
        } else {
            return new SelectedInt6D(data, viewIndex1, viewIndex2, viewIndex3, viewIndex4, viewIndex5, viewIndex6);
        }
    }

    @Override
    public Int1D as1D() {
        int[] idx = new int[number];
        int j = -1;
        for (int i6 = 0; i6 < dim6; ++i6) {
            int j6 = idx6[i6];
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = idx5[i5] + j6;
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
        }
        return new SelectedInt1D(data, idx);
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
