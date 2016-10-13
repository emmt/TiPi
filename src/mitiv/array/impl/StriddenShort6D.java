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

import mitiv.array.Short1D;
import mitiv.array.Short5D;
import mitiv.array.Short6D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.ShortFunction;
import mitiv.base.mapping.ShortScanner;
import mitiv.random.ShortGenerator;
import mitiv.base.indexing.CompiledRange;


/**
 * Stridden implementation of 6-dimensional arrays of short's.
 *
 * @author Éric Thiébaut.
 */
public class StriddenShort6D extends Short6D {
    final int order;
    final short[] data;
    final int offset;
    final int stride1;
    final int stride2;
    final int stride3;
    final int stride4;
    final int stride5;
    final int stride6;
    final boolean flat;

    public StriddenShort6D(short[] arr, int offset, int[] stride, int[] dims) {
        super(dims);
        if (stride.length != 6) {
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
        this.order = Short6D.checkViewStrides(data.length, offset, stride1, stride2, stride3, stride4, stride5, stride6, dim1, dim2, dim3, dim4, dim5, dim6);
        this.flat = (offset == 0 && stride1 == 1 && stride2 == dim1 && stride3 == dim2*stride2 && stride4 == dim3*stride3 && stride5 == dim4*stride4 && stride6 == dim5*stride5);
    }

    public StriddenShort6D(short[] arr, int offset, int stride1, int stride2, int stride3, int stride4, int stride5, int stride6, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        super(dim1, dim2, dim3, dim4, dim5, dim6);
        this.data = arr;
        this.offset = offset;
        this.stride1 = stride1;
        this.stride2 = stride2;
        this.stride3 = stride3;
        this.stride4 = stride4;
        this.stride5 = stride5;
        this.stride6 = stride6;
        this.order = Short6D.checkViewStrides(data.length, offset, stride1, stride2, stride3, stride4, stride5, stride6, dim1, dim2, dim3, dim4, dim5, dim6);
        this.flat = (offset == 0 && stride1 == 1 && stride2 == dim1 && stride3 == dim2*stride2 && stride4 == dim3*stride3 && stride5 == dim4*stride4 && stride6 == dim5*stride5);
    }

    @Override
    public void checkSanity() {
        Short6D.checkViewStrides(data.length, offset, stride1, stride2, stride3, stride4, stride5, stride6, dim1, dim2, dim3, dim4, dim5, dim6);
    }

    final int index(int i1, int i2, int i3, int i4, int i5, int i6) {
        return offset + stride6*i6 + stride5*i5 + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1;
    }

    @Override
    public final short get(int i1, int i2, int i3, int i4, int i5, int i6) {
        return data[offset + stride6*i6 + stride5*i5 + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int i5, int i6, short value) {
        data[offset + stride6*i6 + stride5*i5 + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(short value) {
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
                                    data[j6] = value;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i6 = 0; i6 < dim6; ++i6) {
                int j6 = stride6*i6 + offset;
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

    @Override
    public void fill(ShortGenerator generator) {
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
                                    data[j6] = generator.nextShort();
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i6 = 0; i6 < dim6; ++i6) {
                int j6 = stride6*i6 + offset;
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
                                    data[j1] = generator.nextShort();
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
                                    data[j6] += value;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i6 = 0; i6 < dim6; ++i6) {
                int j6 = stride6*i6 + offset;
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

    @Override
    public void decrement(short value) {
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
                                    data[j6] -= value;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i6 = 0; i6 < dim6; ++i6) {
                int j6 = stride6*i6 + offset;
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

    @Override
    public void scale(short value) {
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
                                    data[j6] *= value;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i6 = 0; i6 < dim6; ++i6) {
                int j6 = stride6*i6 + offset;
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

    @Override
    public void map(ShortFunction function) {
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
                                    data[j6] = function.apply(data[j6]);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i6 = 0; i6 < dim6; ++i6) {
                int j6 = stride6*i6 + offset;
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

    @Override
    public void scan(ShortScanner scanner)  {
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
                                    if (initialized) {
                                        scanner.update(data[j6]);
                                    } else {
                                        scanner.initialize(data[j6]);
                                        initialized = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i6 = 0; i6 < dim6; ++i6) {
                int j6 = stride6*i6 + offset;
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

    @Override
    public final boolean isFlat() {
        return flat;
    }

    @Override
    public short[] flatten(boolean forceCopy) {
        if (! forceCopy && flat) {
            return data;
        }
        short[] out = new short[number];
        if (flat) {
            System.arraycopy(data, 0, out, 0, number);
        } else {
            int j = -1;
            for (int i6 = 0; i6 < dim6; ++i6) {
                int j6 = stride6*i6 + offset;
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
        return out;
    }

    @Override
    public short[] getData() {
        return (flat ? data : null);
    }

    @Override
    public Short5D slice(int idx) {
        return new StriddenShort5D(data,
               offset + stride6*idx, // offset
               stride1, stride2, stride3, stride4, stride5, // strides
               dim1, dim2, dim3, dim4, dim5); // dimensions
    }

    @Override
    public Short5D slice(int idx, int dim) {
        int sliceOffset;
        int sliceStride1, sliceStride2, sliceStride3, sliceStride4, sliceStride5;
        int sliceDim1, sliceDim2, sliceDim3, sliceDim4, sliceDim5;
        if (dim < 0) {
            /* A negative index is taken with respect to the end. */
            dim += 6;
        }
        if (dim == 0) {
            /* Slice along 1st dimension. */
            sliceOffset = offset + stride1*idx;
            sliceStride1 = stride2;
            sliceStride2 = stride3;
            sliceStride3 = stride4;
            sliceStride4 = stride5;
            sliceStride5 = stride6;
            sliceDim1 = dim2;
            sliceDim2 = dim3;
            sliceDim3 = dim4;
            sliceDim4 = dim5;
            sliceDim5 = dim6;
        } else if (dim == 1) {
            /* Slice along 2nd dimension. */
            sliceOffset = offset + stride2*idx;
            sliceStride1 = stride1;
            sliceStride2 = stride3;
            sliceStride3 = stride4;
            sliceStride4 = stride5;
            sliceStride5 = stride6;
            sliceDim1 = dim1;
            sliceDim2 = dim3;
            sliceDim3 = dim4;
            sliceDim4 = dim5;
            sliceDim5 = dim6;
        } else if (dim == 2) {
            /* Slice along 3rd dimension. */
            sliceOffset = offset + stride3*idx;
            sliceStride1 = stride1;
            sliceStride2 = stride2;
            sliceStride3 = stride4;
            sliceStride4 = stride5;
            sliceStride5 = stride6;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim4;
            sliceDim4 = dim5;
            sliceDim5 = dim6;
        } else if (dim == 3) {
            /* Slice along 4th dimension. */
            sliceOffset = offset + stride4*idx;
            sliceStride1 = stride1;
            sliceStride2 = stride2;
            sliceStride3 = stride3;
            sliceStride4 = stride5;
            sliceStride5 = stride6;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
            sliceDim4 = dim5;
            sliceDim5 = dim6;
        } else if (dim == 4) {
            /* Slice along 5th dimension. */
            sliceOffset = offset + stride5*idx;
            sliceStride1 = stride1;
            sliceStride2 = stride2;
            sliceStride3 = stride3;
            sliceStride4 = stride4;
            sliceStride5 = stride6;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
            sliceDim4 = dim4;
            sliceDim5 = dim6;
        } else if (dim == 5) {
            /* Slice along 6th dimension. */
            sliceOffset = offset + stride6*idx;
            sliceStride1 = stride1;
            sliceStride2 = stride2;
            sliceStride3 = stride3;
            sliceStride4 = stride4;
            sliceStride5 = stride5;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
            sliceDim4 = dim4;
            sliceDim5 = dim5;
        } else {
            throw new IndexOutOfBoundsException("Dimension index out of bounds.");
        }
        return new StriddenShort5D(data, sliceOffset,
                sliceStride1, sliceStride2, sliceStride3, sliceStride4, sliceStride5,
                sliceDim1, sliceDim2, sliceDim3, sliceDim4, sliceDim5);
    }

    @Override
    public Short6D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5, Range rng6) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, offset, stride1);
        CompiledRange cr2 = new CompiledRange(rng2, dim2, 0, stride2);
        CompiledRange cr3 = new CompiledRange(rng3, dim3, 0, stride3);
        CompiledRange cr4 = new CompiledRange(rng4, dim4, 0, stride4);
        CompiledRange cr5 = new CompiledRange(rng5, dim5, 0, stride5);
        CompiledRange cr6 = new CompiledRange(rng6, dim6, 0, stride6);
        if (cr1.doesNothing() && cr2.doesNothing() && cr3.doesNothing() && cr4.doesNothing() && cr5.doesNothing() && cr6.doesNothing()) {
            return this;
        }
        return new StriddenShort6D(this.data,
                cr1.getOffset() + cr2.getOffset() + cr3.getOffset() + cr4.getOffset() + cr5.getOffset() + cr6.getOffset(),
                cr1.getStride(), cr2.getStride(), cr3.getStride(), cr4.getStride(), cr5.getStride(), cr6.getStride(),
                cr1.getNumber(), cr2.getNumber(), cr3.getNumber(), cr4.getNumber(), cr5.getNumber(), cr6.getNumber());
    }

    @Override
    public Short6D view(int[] sel1, int[] sel2, int[] sel3, int[] sel4, int[] sel5, int[] sel6) {
        int[] idx1 = Helper.select(offset, stride1, dim1, sel1);
        int[] idx2 = Helper.select(0, stride2, dim2, sel2);
        int[] idx3 = Helper.select(0, stride3, dim3, sel3);
        int[] idx4 = Helper.select(0, stride4, dim4, sel4);
        int[] idx5 = Helper.select(0, stride5, dim5, sel5);
        int[] idx6 = Helper.select(0, stride6, dim6, sel6);
        return new SelectedShort6D(this.data, idx1, idx2, idx3, idx4, idx5, idx6);
    }

    @Override
    public Short1D as1D() {
        // FIXME: may already be contiguous
        return new FlatShort1D(flatten(), number);
    }

}
