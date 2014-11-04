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

import mitiv.array.Long1D;
import mitiv.array.Long2D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.LongFunction;
import mitiv.base.mapping.LongScanner;
import mitiv.random.LongGenerator;
import mitiv.base.indexing.CompiledRange;


/**
 * Stridden implementation of 2-dimensional arrays of long's.
 *
 * @author Éric Thiébaut.
 */
public class StriddenLong2D extends Long2D {
    final int order;
    final long[] data;
    final int offset;
    final int stride1;
    final int stride2;

    public StriddenLong2D(long[] arr, int offset, int[] stride, int[] dims) {
        super(dims);
        if (stride.length != 2) {
            throw new IllegalArgumentException("There must be as many strides as the rank.");
        }
        this.data = arr;
        this.offset = offset;
        stride1 = stride[0];
        stride2 = stride[1];
        this.order = Long2D.checkViewStrides(data.length, offset, stride1, stride2, dim1, dim2);
    }

    public StriddenLong2D(long[] arr, int offset, int stride1, int stride2, int dim1, int dim2) {
        super(dim1, dim2);
        this.data = arr;
        this.offset = offset;
        this.stride1 = stride1;
        this.stride2 = stride2;
        this.order = Long2D.checkViewStrides(data.length, offset, stride1, stride2, dim1, dim2);
    }

    @Override
    public void checkSanity() {
        Long2D.checkViewStrides(data.length, offset, stride1, stride2, dim1, dim2);
    }

    private boolean isFlat() {
        return (offset == 0 && stride1 == 1 && stride2 == dim1);
    }

    final int index(int i1, int i2) {
        return offset + stride2*i2 + stride1*i1;
    }

    @Override
    public final long get(int i1, int i2) {
        return data[offset + stride2*i2 + stride1*i1];
    }

    @Override
    public final void set(int i1, int i2, long value) {
        data[offset + stride2*i2 + stride1*i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(long value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    data[j2] = value;
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                int j2 = stride2*i2 + offset;
                for (int i1 = 0; i1 < dim1; ++i1) {
                    int j1 = stride1*i1 + j2;
                    data[j1] = value;
                }
            }
        }
    }

    @Override
    public void fill(LongGenerator generator) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    data[j2] = generator.nextLong();
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                int j2 = stride2*i2 + offset;
                for (int i1 = 0; i1 < dim1; ++i1) {
                    int j1 = stride1*i1 + j2;
                    data[j1] = generator.nextLong();
                }
            }
        }
    }

    @Override
    public void increment(long value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    data[j2] += value;
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                int j2 = stride2*i2 + offset;
                for (int i1 = 0; i1 < dim1; ++i1) {
                    int j1 = stride1*i1 + j2;
                    data[j1] += value;
                }
            }
        }
    }

    @Override
    public void decrement(long value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    data[j2] -= value;
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                int j2 = stride2*i2 + offset;
                for (int i1 = 0; i1 < dim1; ++i1) {
                    int j1 = stride1*i1 + j2;
                    data[j1] -= value;
                }
            }
        }
    }

    @Override
    public void scale(long value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    data[j2] *= value;
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                int j2 = stride2*i2 + offset;
                for (int i1 = 0; i1 < dim1; ++i1) {
                    int j1 = stride1*i1 + j2;
                    data[j1] *= value;
                }
            }
        }
    }

    @Override
    public void map(LongFunction function) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    data[j2] = function.apply(data[j2]);
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                int j2 = stride2*i2 + offset;
                for (int i1 = 0; i1 < dim1; ++i1) {
                    int j1 = stride1*i1 + j2;
                    data[j1] = function.apply(data[j1]);
                }
            }
        }
    }

    @Override
    public void scan(LongScanner scanner)  {
        boolean initialized = false;
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    if (initialized) {
                        scanner.update(data[j2]);
                    } else {
                        scanner.initialize(data[j2]);
                        initialized = true;
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                int j2 = stride2*i2 + offset;
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

    @Override
    public long[] flatten(boolean forceCopy) {
        if (! forceCopy && isFlat()) {
            return data;
        }
        long[] out = new long[number];
        int j = -1;
        for (int i2 = 0; i2 < dim2; ++i2) {
            int j2 = stride2*i2 + offset;
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + j2;
                out[++j] = data[j1];
            }
        }
        return out;
    }

    @Override
    public Long1D slice(int idx) {
        return new StriddenLong1D(data,
               offset + stride2*idx, // offset
               stride1, // strides
               dim1); // dimensions
    }

    @Override
    public Long1D slice(int idx, int dim) {
        int sliceOffset;
        int sliceStride1;
        int sliceDim1;
        if (dim < 0) {
            /* A negative index is taken with respect to the end. */
            dim += 2;
        }
        if (dim == 0) {
            /* Slice along 1st dimension. */
            sliceOffset = offset + stride1*idx;
            sliceStride1 = stride2;
            sliceDim1 = dim2;
        } else if (dim == 1) {
            /* Slice along 2nd dimension. */
            sliceOffset = offset + stride2*idx;
            sliceStride1 = stride1;
            sliceDim1 = dim1;
        } else {
            throw new IndexOutOfBoundsException("Dimension index out of bounds.");
        }
        return new StriddenLong1D(data, sliceOffset,
                sliceStride1,
                sliceDim1);
    }

    @Override
    public Long2D view(Range rng1, Range rng2) {
        CompiledRange cr1 = new CompiledRange(rng1, dim1, offset, stride1);
        CompiledRange cr2 = new CompiledRange(rng2, dim2, 0, stride2);
        if (cr1.doesNothing() && cr2.doesNothing()) {
            return this;
        }
        return new StriddenLong2D(this.data,
                cr1.getOffset() + cr2.getOffset(),
                cr1.getStride(), cr2.getStride(),
                cr1.getNumber(), cr2.getNumber());
    }

    @Override
    public Long2D view(int[] sel1, int[] sel2) {
        int[] idx1 = Helper.select(offset, stride1, dim1, sel1);
        int[] idx2 = Helper.select(0, stride2, dim2, sel2);
        return new SelectedLong2D(this.data, idx1, idx2);
    }

    @Override
    public Long1D as1D() {
        // FIXME: may already be contiguous
        return new FlatLong1D(flatten(), number);
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
