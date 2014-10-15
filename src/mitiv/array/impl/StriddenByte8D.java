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

import mitiv.array.Byte1D;
import mitiv.array.Byte7D;
import mitiv.array.Byte8D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.ByteFunction;
import mitiv.base.mapping.ByteScanner;
import mitiv.random.ByteGenerator;

/**
 * Stridden implementation of 8-dimensional arrays of byte's.
 *
 * @author Éric Thiébaut.
 */
public class StriddenByte8D extends Byte8D {
    final int order;
    final byte[] data;
    final int offset;
    final int stride1;
    final int stride2;
    final int stride3;
    final int stride4;
    final int stride5;
    final int stride6;
    final int stride7;
    final int stride8;

    public StriddenByte8D(byte[] arr, int offset, int[] stride, int[] shape) {
        super(shape, true);
        if (stride.length != rank) {
            throw new IllegalArgumentException("There must be as many strides as the rank.");
        }
        this.data = arr;
        this.offset = offset;
        stride1 = stride[0];
        stride2 = stride[1];
        stride3 = stride[2];
        stride4 = stride[3];
        stride5 = stride[4];
        stride6 = stride[5];
        stride7 = stride[6];
        stride8 = stride[7];
        this.order = Byte8D.checkViewStrides(data.length, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, offset, stride1, stride2, stride3, stride4, stride5, stride6, stride7, stride8);
    }

    public StriddenByte8D(byte[] arr, int offset, int stride1, int dim1, int stride2, int dim2, int stride3, int dim3, int stride4, int dim4, int stride5, int dim5, int stride6, int dim6, int stride7, int dim7, int stride8, int dim8) {
        super(dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8);
        this.data = arr;
        this.offset = offset;
        this.stride1 = stride1;
        this.stride2 = stride2;
        this.stride3 = stride3;
        this.stride4 = stride4;
        this.stride5 = stride5;
        this.stride6 = stride6;
        this.stride7 = stride7;
        this.stride8 = stride8;
        this.order = Byte8D.checkViewStrides(data.length, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, offset, stride1, stride2, stride3, stride4, stride5, stride6, stride7, stride8);
    }

    @Override
    public void checkSanity() {
        Byte8D.checkViewStrides(data.length, dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, offset, stride1, stride2, stride3, stride4, stride5, stride6, stride7, stride8);
    }

    private boolean isFlat() {
        return (offset == 0 && stride1 == 1 && stride2 == dim1 && stride3 == dim2*stride2 && stride4 == dim3*stride3 && stride5 == dim4*stride4 && stride6 == dim5*stride5 && stride7 == dim6*stride6 && stride8 == dim7*stride7);
    }

    final int index(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        return offset + stride8*i8 + stride7*i7 + stride6*i6 + stride5*i5 + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1;
    }

    @Override
    public final byte get(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        return data[offset + stride8*i8 + stride7*i7 + stride6*i6 + stride5*i5 + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, byte value) {
        data[offset + stride8*i8 + stride7*i7 + stride6*i6 + stride5*i5 + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(byte value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                int j5 = stride5*i5 + j4;
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    int j6 = stride6*i6 + j5;
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        int j7 = stride7*i7 + j6;
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            int j8 = stride8*i8 + j7;
                                            data[j8] = value;
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
                int j8 = stride8*i8 + offset;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = stride7*i7 + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = stride6*i6 + j7;
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            int j5 = stride5*i5 + j6;
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                int j4 = stride4*i4 + j5;
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    int j3 = stride3*i3 + j4;
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        int j2 = stride2*i2 + j3;
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            int j1 = stride1*i1 + j2;
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
    public void fill(ByteGenerator generator) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                int j5 = stride5*i5 + j4;
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    int j6 = stride6*i6 + j5;
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        int j7 = stride7*i7 + j6;
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            int j8 = stride8*i8 + j7;
                                            data[j8] = generator.nextByte();
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
                int j8 = stride8*i8 + offset;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = stride7*i7 + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = stride6*i6 + j7;
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            int j5 = stride5*i5 + j6;
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                int j4 = stride4*i4 + j5;
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    int j3 = stride3*i3 + j4;
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        int j2 = stride2*i2 + j3;
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            int j1 = stride1*i1 + j2;
                                            data[j1] = generator.nextByte();
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
    public void increment(byte value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                int j5 = stride5*i5 + j4;
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    int j6 = stride6*i6 + j5;
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        int j7 = stride7*i7 + j6;
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            int j8 = stride8*i8 + j7;
                                            data[j8] += value;
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
                int j8 = stride8*i8 + offset;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = stride7*i7 + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = stride6*i6 + j7;
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            int j5 = stride5*i5 + j6;
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                int j4 = stride4*i4 + j5;
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    int j3 = stride3*i3 + j4;
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        int j2 = stride2*i2 + j3;
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            int j1 = stride1*i1 + j2;
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
    public void decrement(byte value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                int j5 = stride5*i5 + j4;
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    int j6 = stride6*i6 + j5;
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        int j7 = stride7*i7 + j6;
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            int j8 = stride8*i8 + j7;
                                            data[j8] -= value;
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
                int j8 = stride8*i8 + offset;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = stride7*i7 + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = stride6*i6 + j7;
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            int j5 = stride5*i5 + j6;
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                int j4 = stride4*i4 + j5;
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    int j3 = stride3*i3 + j4;
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        int j2 = stride2*i2 + j3;
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            int j1 = stride1*i1 + j2;
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
    public void scale(byte value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                int j5 = stride5*i5 + j4;
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    int j6 = stride6*i6 + j5;
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        int j7 = stride7*i7 + j6;
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            int j8 = stride8*i8 + j7;
                                            data[j8] *= value;
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
                int j8 = stride8*i8 + offset;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = stride7*i7 + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = stride6*i6 + j7;
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            int j5 = stride5*i5 + j6;
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                int j4 = stride4*i4 + j5;
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    int j3 = stride3*i3 + j4;
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        int j2 = stride2*i2 + j3;
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            int j1 = stride1*i1 + j2;
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
    public void map(ByteFunction function) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                int j5 = stride5*i5 + j4;
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    int j6 = stride6*i6 + j5;
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        int j7 = stride7*i7 + j6;
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            int j8 = stride8*i8 + j7;
                                            data[j8] = function.apply(data[j8]);
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
                int j8 = stride8*i8 + offset;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = stride7*i7 + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = stride6*i6 + j7;
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            int j5 = stride5*i5 + j6;
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                int j4 = stride4*i4 + j5;
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    int j3 = stride3*i3 + j4;
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        int j2 = stride2*i2 + j3;
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            int j1 = stride1*i1 + j2;
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
    public void scan(ByteScanner scanner)  {
        boolean initialized = false;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j3;
                            for (int i5 = 0; i5 < dim5; ++i5) {
                                int j5 = stride5*i5 + j4;
                                for (int i6 = 0; i6 < dim6; ++i6) {
                                    int j6 = stride6*i6 + j5;
                                    for (int i7 = 0; i7 < dim7; ++i7) {
                                        int j7 = stride7*i7 + j6;
                                        for (int i8 = 0; i8 < dim8; ++i8) {
                                            int j8 = stride8*i8 + j7;
                                            if (initialized) {
                                                scanner.update(data[j8]);
                                            } else {
                                                scanner.initialize(data[j8]);
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
                int j8 = stride8*i8 + offset;
                for (int i7 = 0; i7 < dim7; ++i7) {
                    int j7 = stride7*i7 + j8;
                    for (int i6 = 0; i6 < dim6; ++i6) {
                        int j6 = stride6*i6 + j7;
                        for (int i5 = 0; i5 < dim5; ++i5) {
                            int j5 = stride5*i5 + j6;
                            for (int i4 = 0; i4 < dim4; ++i4) {
                                int j4 = stride4*i4 + j5;
                                for (int i3 = 0; i3 < dim3; ++i3) {
                                    int j3 = stride3*i3 + j4;
                                    for (int i2 = 0; i2 < dim2; ++i2) {
                                        int j2 = stride2*i2 + j3;
                                        for (int i1 = 0; i1 < dim1; ++i1) {
                                            int j1 = stride1*i1 + j2;
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
    public byte[] flatten(boolean forceCopy) {
        if (! forceCopy && isFlat()) {
            return data;
        }
        byte[] out = new byte[number];
        int j = -1;
        for (int i8 = 0; i8 < dim8; ++i8) {
            int j8 = stride8*i8 + offset;
            for (int i7 = 0; i7 < dim7; ++i7) {
                int j7 = stride7*i7 + j8;
                for (int i6 = 0; i6 < dim6; ++i6) {
                    int j6 = stride6*i6 + j7;
                    for (int i5 = 0; i5 < dim5; ++i5) {
                        int j5 = stride5*i5 + j6;
                        for (int i4 = 0; i4 < dim4; ++i4) {
                            int j4 = stride4*i4 + j5;
                            for (int i3 = 0; i3 < dim3; ++i3) {
                                int j3 = stride3*i3 + j4;
                                for (int i2 = 0; i2 < dim2; ++i2) {
                                    int j2 = stride2*i2 + j3;
                                    for (int i1 = 0; i1 < dim1; ++i1) {
                                        int j1 = stride1*i1 + j2;
                                        out[++j] = data[j1];
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
    public Byte7D slice(int idx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Byte7D slice(int idx, int dim) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Byte8D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5, Range rng6, Range rng7, Range rng8) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Byte8D view(int[] idx1, int[] idx2, int[] idx3, int[] idx4, int[] idx5, int[] idx6, int[] idx7, int[] idx8) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Byte1D as1D() {
        // TODO Auto-generated method stub
        return null;
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
