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

import mitiv.array.Double1D;
import mitiv.array.Double4D;
import mitiv.array.Double5D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.DoubleFunction;
import mitiv.base.mapping.DoubleScanner;
import mitiv.random.DoubleGenerator;
import mitiv.base.indexing.CompiledRange;
import mitiv.exception.IllegalRangeException;

/**
 * Stridden implementation of 5-dimensional arrays of double's.
 *
 * @author Éric Thiébaut.
 */
public class StriddenDouble5D extends Double5D {
    final int order;
    final double[] data;
    final int offset;
    final int stride1;
    final int stride2;
    final int stride3;
    final int stride4;
    final int stride5;

    public StriddenDouble5D(double[] arr, int offset, int[] stride, int[] dims) {
        super(dims);
        if (stride.length != 5) {
            throw new IllegalArgumentException("There must be as many strides as the rank.");
        }
        this.data = arr;
        this.offset = offset;
        stride1 = stride[0];
        stride2 = stride[1];
        stride3 = stride[2];
        stride4 = stride[3];
        stride5 = stride[4];
        this.order = Double5D.checkViewStrides(data.length, offset, stride1, stride2, stride3, stride4, stride5, dim1, dim2, dim3, dim4, dim5);
    }

    public StriddenDouble5D(double[] arr, int offset, int stride1, int stride2, int stride3, int stride4, int stride5, int dim1, int dim2, int dim3, int dim4, int dim5) {
        super(dim1, dim2, dim3, dim4, dim5);
        this.data = arr;
        this.offset = offset;
        this.stride1 = stride1;
        this.stride2 = stride2;
        this.stride3 = stride3;
        this.stride4 = stride4;
        this.stride5 = stride5;
        this.order = Double5D.checkViewStrides(data.length, offset, stride1, stride2, stride3, stride4, stride5, dim1, dim2, dim3, dim4, dim5);
    }

    @Override
    public void checkSanity() {
        Double5D.checkViewStrides(data.length, offset, stride1, stride2, stride3, stride4, stride5, dim1, dim2, dim3, dim4, dim5);
    }

    private boolean isFlat() {
        return (offset == 0 && stride1 == 1 && stride2 == dim1 && stride3 == dim2*stride2 && stride4 == dim3*stride3 && stride5 == dim4*stride4);
    }

    final int index(int i1, int i2, int i3, int i4, int i5) {
        return offset + stride5*i5 + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1;
    }

    @Override
    public final double get(int i1, int i2, int i3, int i4, int i5) {
        return data[offset + stride5*i5 + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, int i4, int i5, double value) {
        data[offset + stride5*i5 + stride4*i4 + stride3*i3 + stride2*i2 + stride1*i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(double value) {
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
                                data[j5] = value;
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = stride5*i5 + offset;
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

    @Override
    public void fill(DoubleGenerator generator) {
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
                                data[j5] = generator.nextDouble();
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = stride5*i5 + offset;
                for (int i4 = 0; i4 < dim4; ++i4) {
                    int j4 = stride4*i4 + j5;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j4;
                        for (int i2 = 0; i2 < dim2; ++i2) {
                            int j2 = stride2*i2 + j3;
                            for (int i1 = 0; i1 < dim1; ++i1) {
                                int j1 = stride1*i1 + j2;
                                data[j1] = generator.nextDouble();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void increment(double value) {
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
                                data[j5] += value;
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = stride5*i5 + offset;
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

    @Override
    public void decrement(double value) {
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
                                data[j5] -= value;
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = stride5*i5 + offset;
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

    @Override
    public void scale(double value) {
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
                                data[j5] *= value;
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = stride5*i5 + offset;
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

    @Override
    public void map(DoubleFunction function) {
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
                                data[j5] = function.apply(data[j5]);
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i5 = 0; i5 < dim5; ++i5) {
                int j5 = stride5*i5 + offset;
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

    @Override
    public void scan(DoubleScanner scanner)  {
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
                                if (initialized) {
                                    scanner.update(data[j5]);
                                } else {
                                    scanner.initialize(data[j5]);
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
                int j5 = stride5*i5 + offset;
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

    @Override
    public double[] flatten(boolean forceCopy) {
        if (! forceCopy && isFlat()) {
            return data;
        }
        double[] out = new double[number];
        int j = -1;
        for (int i5 = 0; i5 < dim5; ++i5) {
            int j5 = stride5*i5 + offset;
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
        return out;
    }

    @Override
    public Double4D slice(int idx) {
        return new StriddenDouble4D(data,
               offset + stride5*idx, // offset
               stride1, stride2, stride3, stride4, // strides
               dim1, dim2, dim3, dim4); // dimensions
    }

    @Override
    public Double4D slice(int idx, int dim) {
        int sliceOffset;
        int sliceStride1, sliceStride2, sliceStride3, sliceStride4;
        int sliceDim1, sliceDim2, sliceDim3, sliceDim4;
        if (dim < 0) {
            /* A negative index is taken with respect to the end. */
            dim += 5;
        }
        if (dim == 0) {
            /* Slice along 1st dimension. */
            sliceOffset = offset + stride1*idx;
            sliceStride1 = stride2;
            sliceStride2 = stride3;
            sliceStride3 = stride4;
            sliceStride4 = stride5;
            sliceDim1 = dim2;
            sliceDim2 = dim3;
            sliceDim3 = dim4;
            sliceDim4 = dim5;
        } else if (dim == 1) {
            /* Slice along 2nd dimension. */
            sliceOffset = offset + stride2*idx;
            sliceStride1 = stride1;
            sliceStride2 = stride3;
            sliceStride3 = stride4;
            sliceStride4 = stride5;
            sliceDim1 = dim1;
            sliceDim2 = dim3;
            sliceDim3 = dim4;
            sliceDim4 = dim5;
        } else if (dim == 2) {
            /* Slice along 3rd dimension. */
            sliceOffset = offset + stride3*idx;
            sliceStride1 = stride1;
            sliceStride2 = stride2;
            sliceStride3 = stride4;
            sliceStride4 = stride5;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim4;
            sliceDim4 = dim5;
        } else if (dim == 3) {
            /* Slice along 4th dimension. */
            sliceOffset = offset + stride4*idx;
            sliceStride1 = stride1;
            sliceStride2 = stride2;
            sliceStride3 = stride3;
            sliceStride4 = stride5;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
            sliceDim4 = dim5;
        } else if (dim == 4) {
            /* Slice along 5th dimension. */
            sliceOffset = offset + stride5*idx;
            sliceStride1 = stride1;
            sliceStride2 = stride2;
            sliceStride3 = stride3;
            sliceStride4 = stride4;
            sliceDim1 = dim1;
            sliceDim2 = dim2;
            sliceDim3 = dim3;
            sliceDim4 = dim4;
        } else {
            throw new IndexOutOfBoundsException("Dimension index out of bounds.");
        }
        return new StriddenDouble4D(data, sliceOffset,
                sliceStride1, sliceStride2, sliceStride3, sliceStride4,
                sliceDim1, sliceDim2, sliceDim3, sliceDim4);
    }

    @Override
    public Double5D view(Range rng1, Range rng2, Range rng3, Range rng4, Range rng5) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, offset, stride1);
        CompiledRange cr2 = new CompiledRange(rng2, dim2, 0, stride2);
        CompiledRange cr3 = new CompiledRange(rng3, dim3, 0, stride3);
        CompiledRange cr4 = new CompiledRange(rng4, dim4, 0, stride4);
        CompiledRange cr5 = new CompiledRange(rng5, dim5, 0, stride5);
        if (cr1.doesNothing() && cr2.doesNothing() && cr3.doesNothing() && cr4.doesNothing() && cr5.doesNothing()) {
            return this;
        }
        if (cr1.getNumber() == 0 || cr2.getNumber() == 0 || cr3.getNumber() == 0 || cr4.getNumber() == 0 || cr5.getNumber() == 0) {
            throw new IllegalRangeException("Empty range.");
        }
        return new StriddenDouble5D(this.data,
                cr1.getOffset() + cr2.getOffset() + cr3.getOffset() + cr4.getOffset() + cr5.getOffset(),
                cr1.getStride(), cr2.getStride(), cr3.getStride(), cr4.getStride(), cr5.getStride(),
                cr1.getNumber(), cr2.getNumber(), cr3.getNumber(), cr4.getNumber(), cr5.getNumber());
    }

    @Override
    public Double5D view(int[] sel1, int[] sel2, int[] sel3, int[] sel4, int[] sel5) {
        int[] idx1 = Helper.select(offset, stride1, dim1, sel1);
        int[] idx2 = Helper.select(0, stride2, dim2, sel2);
        int[] idx3 = Helper.select(0, stride3, dim3, sel3);
        int[] idx4 = Helper.select(0, stride4, dim4, sel4);
        int[] idx5 = Helper.select(0, stride5, dim5, sel5);
        return new SelectedDouble5D(this.data, idx1, idx2, idx3, idx4, idx5);
    }

    @Override
    public Double1D as1D() {
        // FIXME: may already be contiguous
        return new FlatDouble1D(flatten(), number);
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
