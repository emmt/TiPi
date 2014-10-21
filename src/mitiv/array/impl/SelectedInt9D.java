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
import mitiv.array.Int8D;
import mitiv.array.Int9D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.IntFunction;
import mitiv.base.mapping.IntScanner;
import mitiv.random.IntGenerator;


/**
 * Selected implementation of 9-dimensional arrays of int's.
 *
 * This specific kind of arrays/views are accessed via indirection tables (one
 * for each dimension).
 *
 * @author Éric Thiébaut.
 */
public class SelectedInt9D extends Int9D {
    static final int order = NONSPECIFIC_ORDER;
    final int[] data;
    final int[] idx1;
    final int[] idx2;
    final int[] idx3;
    final int[] idx4;
    final int[] idx5;
    final int[] idx6;
    final int[] idx7;
    final int[] idx8;
    final int[] idx9;

    /**
     * Create a new instance of a view via lists of selected indices.
     *
     * <p>
     * All lists of selected indices must be immutable as, for efficiency,
     * a simple reference is kept.
     * </p>
     */
    public SelectedInt9D(int[] arr, int[] idx1, int[] idx2, int[] idx3, int[] idx4, int[] idx5, int[] idx6, int[] idx7, int[] idx8, int[] idx9) {
        super(idx1.length, idx2.length, idx3.length, idx4.length, idx5.length, idx6.length, idx7.length, idx8.length, idx9.length);
        this.data = arr;
        this.idx1 = idx1;
        this.idx2 = idx2;
        this.idx3 = idx3;
        this.idx4 = idx4;
        this.idx5 = idx5;
        this.idx6 = idx6;
        this.idx7 = idx7;
        this.idx8 = idx8;
        this.idx9 = idx9;
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
        indexMin = indexMax = idx7[0];
        for (int i7 = 1; i7 < dim7; ++i7) {
            int index = idx7[i7];
            if (index < indexMin) indexMin = index;
            if (index > indexMax) indexMax = index;
        }
        offsetMin += indexMin;
        offsetMax += indexMax;
        indexMin = indexMax = idx8[0];
        for (int i8 = 1; i8 < dim8; ++i8) {
            int index = idx8[i8];
            if (index < indexMin) indexMin = index;
            if (index > indexMax) indexMax = index;
        }
        offsetMin += indexMin;
        offsetMax += indexMax;
        indexMin = indexMax = idx9[0];
        for (int i9 = 1; i9 < dim9; ++i9) {
            int index = idx9[i9];
            if (index < indexMin) indexMin = index;
            if (index > indexMax) indexMax = index;
        }
        offsetMin += indexMin;
        offsetMax += indexMax;
        if (offsetMin < 0 || offsetMax >= data.length) {
            throw new IndexOutOfBoundsException("Selected indices are out of bounds.");
        }
    }

    final int index(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        return idx9[i9] + idx8[i8] + idx7[i7] + idx6[i6] + idx5[i5] + idx4[i4] + idx3[i3] + idx2[i2] + idx1[i1];
    }

    @Override
    public final int get(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        return data[idx9[i9] + idx8[i8] + idx7[i7] + idx6[i6] + idx5[i5] + idx4[i4] + idx3[i3] + idx2[i2] + idx1[i1]];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int value) {
        data[idx9[i9] + idx8[i8] + idx7[i7] + idx6[i6] + idx5[i5] + idx4[i4] + idx3[i3] + idx2[i2] + idx1[i1]] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(int value) {
        for (int i9 = 0; i9 < dim9; ++i9) {
            int j9 = idx9[i9];
            for (int i8 = 0; i8 < dim8; ++i8) {
                int j8 = idx8[i8] + j9;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = idx7[i7] + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = idx6[i6] + j7;
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
            }
        }
    }

    @Override
    public void fill(IntGenerator generator) {
        for (int i9 = 0; i9 < dim9; ++i9) {
            int j9 = idx9[i9];
            for (int i8 = 0; i8 < dim8; ++i8) {
                int j8 = idx8[i8] + j9;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = idx7[i7] + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = idx6[i6] + j7;
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
            }
        }
    }

    @Override
    public void increment(int value) {
        for (int i9 = 0; i9 < dim9; ++i9) {
            int j9 = idx9[i9];
            for (int i8 = 0; i8 < dim8; ++i8) {
                int j8 = idx8[i8] + j9;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = idx7[i7] + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = idx6[i6] + j7;
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
            }
        }
    }

    @Override
    public void decrement(int value) {
        for (int i9 = 0; i9 < dim9; ++i9) {
            int j9 = idx9[i9];
            for (int i8 = 0; i8 < dim8; ++i8) {
                int j8 = idx8[i8] + j9;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = idx7[i7] + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = idx6[i6] + j7;
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
            }
        }
    }

    @Override
    public void scale(int value) {
        for (int i9 = 0; i9 < dim9; ++i9) {
            int j9 = idx9[i9];
            for (int i8 = 0; i8 < dim8; ++i8) {
                int j8 = idx8[i8] + j9;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = idx7[i7] + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = idx6[i6] + j7;
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
            }
        }
    }

    @Override
    public void map(IntFunction function) {
        for (int i9 = 0; i9 < dim9; ++i9) {
            int j9 = idx9[i9];
            for (int i8 = 0; i8 < dim8; ++i8) {
                int j8 = idx8[i8] + j9;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = idx7[i7] + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = idx6[i6] + j7;
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
            }
        }
    }

    @Override
    public void scan(IntScanner scanner)  {
        boolean initialized = false;
        for (int i9 = 0; i9 < dim9; ++i9) {
            int j9 = idx9[i9];
            for (int i8 = 0; i8 < dim8; ++i8) {
                int j8 = idx8[i8] + j9;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = idx7[i7] + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = idx6[i6] + j7;
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
            }
        }
    }

    @Override
    public int[] flatten(boolean forceCopy) {
        int[] out = new int[number];
        int j = -1;
        for (int i9 = 0; i9 < dim9; ++i9) {
            int j9 = idx9[i9];
            for (int i8 = 0; i8 < dim8; ++i8) {
                int j8 = idx8[i8] + j9;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = idx7[i7] + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = idx6[i6] + j7;
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
                }
            }
        }
        return out;
    }

    @Override
    public Int8D slice(int idx) {
        int[] sliceIndex1;
        int sliceOffset = idx9[Helper.fixIndex(idx, dim9)];
        if (sliceOffset == 0) {
            sliceIndex1 = idx1;
        } else {
            /* Add the offset to the first indirection table. */
            sliceIndex1 = new int[dim1];
            for (int i = 0; i < dim1; ++i) {
                sliceIndex1[i] = idx1[i] + sliceOffset;
            }
        }
        return new SelectedInt8D(data, sliceIndex1, idx2, idx3, idx4, idx5, idx6, idx7, idx8);
    }

    @Override
    public Int8D slice(int idx, int dim) {
        int sliceOffset;
        int[] sliceIndex1;
        int[] sliceIndex2;
        int[] sliceIndex3;
        int[] sliceIndex4;
        int[] sliceIndex5;
        int[] sliceIndex6;
        int[] sliceIndex7;
        int[] sliceIndex8;
        dim = Helper.fixSliceIndex(dim, 9);
        if (dim == 0) {
            /* Slice along 1st dimension. */
            sliceOffset = idx1[Helper.fixIndex(idx, dim1)];
            sliceIndex1 = idx2;
            sliceIndex2 = idx3;
            sliceIndex3 = idx4;
            sliceIndex4 = idx5;
            sliceIndex5 = idx6;
            sliceIndex6 = idx7;
            sliceIndex7 = idx8;
            sliceIndex8 = idx9;
        } else if (dim == 1) {
            /* Slice along 2nd dimension. */
            sliceIndex1 = idx1;
            sliceOffset = idx2[Helper.fixIndex(idx, dim2)];
            sliceIndex2 = idx3;
            sliceIndex3 = idx4;
            sliceIndex4 = idx5;
            sliceIndex5 = idx6;
            sliceIndex6 = idx7;
            sliceIndex7 = idx8;
            sliceIndex8 = idx9;
        } else if (dim == 2) {
            /* Slice along 3rd dimension. */
            sliceIndex1 = idx1;
            sliceIndex2 = idx2;
            sliceOffset = idx3[Helper.fixIndex(idx, dim3)];
            sliceIndex3 = idx4;
            sliceIndex4 = idx5;
            sliceIndex5 = idx6;
            sliceIndex6 = idx7;
            sliceIndex7 = idx8;
            sliceIndex8 = idx9;
        } else if (dim == 3) {
            /* Slice along 4th dimension. */
            sliceIndex1 = idx1;
            sliceIndex2 = idx2;
            sliceIndex3 = idx3;
            sliceOffset = idx4[Helper.fixIndex(idx, dim4)];
            sliceIndex4 = idx5;
            sliceIndex5 = idx6;
            sliceIndex6 = idx7;
            sliceIndex7 = idx8;
            sliceIndex8 = idx9;
        } else if (dim == 4) {
            /* Slice along 5th dimension. */
            sliceIndex1 = idx1;
            sliceIndex2 = idx2;
            sliceIndex3 = idx3;
            sliceIndex4 = idx4;
            sliceOffset = idx5[Helper.fixIndex(idx, dim5)];
            sliceIndex5 = idx6;
            sliceIndex6 = idx7;
            sliceIndex7 = idx8;
            sliceIndex8 = idx9;
        } else if (dim == 5) {
            /* Slice along 6th dimension. */
            sliceIndex1 = idx1;
            sliceIndex2 = idx2;
            sliceIndex3 = idx3;
            sliceIndex4 = idx4;
            sliceIndex5 = idx5;
            sliceOffset = idx6[Helper.fixIndex(idx, dim6)];
            sliceIndex6 = idx7;
            sliceIndex7 = idx8;
            sliceIndex8 = idx9;
        } else if (dim == 6) {
            /* Slice along 7th dimension. */
            sliceIndex1 = idx1;
            sliceIndex2 = idx2;
            sliceIndex3 = idx3;
            sliceIndex4 = idx4;
            sliceIndex5 = idx5;
            sliceIndex6 = idx6;
            sliceOffset = idx7[Helper.fixIndex(idx, dim7)];
            sliceIndex7 = idx8;
            sliceIndex8 = idx9;
        } else if (dim == 7) {
            /* Slice along 8th dimension. */
            sliceIndex1 = idx1;
            sliceIndex2 = idx2;
            sliceIndex3 = idx3;
            sliceIndex4 = idx4;
            sliceIndex5 = idx5;
            sliceIndex6 = idx6;
            sliceIndex7 = idx7;
            sliceOffset = idx8[Helper.fixIndex(idx, dim8)];
            sliceIndex8 = idx9;
        } else {
            /* Slice along 9th dimension. */
            sliceIndex1 = idx1;
            sliceIndex2 = idx2;
            sliceIndex3 = idx3;
            sliceIndex4 = idx4;
            sliceIndex5 = idx5;
            sliceIndex6 = idx6;
            sliceIndex7 = idx7;
            sliceIndex8 = idx8;
            sliceOffset = idx9[Helper.fixIndex(idx, dim9)];
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
        return new SelectedInt8D(data, sliceIndex1, sliceIndex2, sliceIndex3, sliceIndex4, sliceIndex5, sliceIndex6, sliceIndex7, sliceIndex8);
    }

    @Override
    public Int9D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5, Range rng6, Range rng7, Range rng8, Range rng9) {
        int[] viewIndex1 = Helper.select(idx1, rng1);
        int[] viewIndex2 = Helper.select(idx2, rng2);
        int[] viewIndex3 = Helper.select(idx3, rng3);
        int[] viewIndex4 = Helper.select(idx4, rng4);
        int[] viewIndex5 = Helper.select(idx5, rng5);
        int[] viewIndex6 = Helper.select(idx6, rng6);
        int[] viewIndex7 = Helper.select(idx7, rng7);
        int[] viewIndex8 = Helper.select(idx8, rng8);
        int[] viewIndex9 = Helper.select(idx9, rng9);
        if (viewIndex1 == idx1 && viewIndex2 == idx2 && viewIndex3 == idx3 && viewIndex4 == idx4 && viewIndex5 == idx5 && viewIndex6 == idx6 && viewIndex7 == idx7 && viewIndex8 == idx8 && viewIndex9 == idx9) {
            return this;
        } else {
            return new SelectedInt9D(data, viewIndex1, viewIndex2, viewIndex3, viewIndex4, viewIndex5, viewIndex6, viewIndex7, viewIndex8, viewIndex9);
        }
    }

    @Override
    public Int9D view(int[] sel1, int[] sel2, int[] sel3, int[] sel4, int[] sel5, int[] sel6, int[] sel7, int[] sel8, int[] sel9) {
        int[] viewIndex1 = Helper.select(idx1, sel1);
        int[] viewIndex2 = Helper.select(idx2, sel2);
        int[] viewIndex3 = Helper.select(idx3, sel3);
        int[] viewIndex4 = Helper.select(idx4, sel4);
        int[] viewIndex5 = Helper.select(idx5, sel5);
        int[] viewIndex6 = Helper.select(idx6, sel6);
        int[] viewIndex7 = Helper.select(idx7, sel7);
        int[] viewIndex8 = Helper.select(idx8, sel8);
        int[] viewIndex9 = Helper.select(idx9, sel9);
        if (viewIndex1 == idx1 && viewIndex2 == idx2 && viewIndex3 == idx3 && viewIndex4 == idx4 && viewIndex5 == idx5 && viewIndex6 == idx6 && viewIndex7 == idx7 && viewIndex8 == idx8 && viewIndex9 == idx9) {
            return this;
        } else {
            return new SelectedInt9D(data, viewIndex1, viewIndex2, viewIndex3, viewIndex4, viewIndex5, viewIndex6, viewIndex7, viewIndex8, viewIndex9);
        }
    }

    @Override
    public Int1D as1D() {
        int[] idx = new int[number];
        int j = -1;
        for (int i9 = 0; i9 < dim9; ++i9) {
            int j9 = idx9[i9];
            for (int i8 = 0; i8 < dim8; ++i8) {
                int j8 = idx8[i8] + j9;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = idx7[i7] + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = idx6[i6] + j7;
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
