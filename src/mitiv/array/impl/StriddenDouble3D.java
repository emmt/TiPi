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
import mitiv.array.Double2D;
import mitiv.array.Double3D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.DoubleFunction;
import mitiv.base.mapping.DoubleScanner;
import mitiv.random.DoubleGenerator;

/**
 * Stridden implementation of 3-dimensional arrays of double's.
 *
 * @author Éric Thiébaut.
 */
public class StriddenDouble3D extends Double3D {
    final int order;
    final double[] data;
    final int offset;
    final int stride1;
    final int stride2;
    final int stride3;

    public StriddenDouble3D(double[] arr, int offset, int[] stride, int[] dims) {
        super(dims);
        if (stride.length != 3) {
            throw new IllegalArgumentException("There must be as many strides as the rank.");
        }
        this.data = arr;
        this.offset = offset;
        stride1 = stride[0];
        stride2 = stride[1];
        stride3 = stride[2];
        this.order = Double3D.checkViewStrides(data.length, dim1, dim2, dim3, offset, stride1, stride2, stride3);
    }

    public StriddenDouble3D(double[] arr, int offset, int stride1, int dim1, int stride2, int dim2, int stride3, int dim3) {
        super(dim1, dim2, dim3);
        this.data = arr;
        this.offset = offset;
        this.stride1 = stride1;
        this.stride2 = stride2;
        this.stride3 = stride3;
        this.order = Double3D.checkViewStrides(data.length, dim1, dim2, dim3, offset, stride1, stride2, stride3);
    }

    @Override
    public void checkSanity() {
        Double3D.checkViewStrides(data.length, dim1, dim2, dim3, offset, stride1, stride2, stride3);
    }

    private boolean isFlat() {
        return (offset == 0 && stride1 == 1 && stride2 == dim1 && stride3 == dim2*stride2);
    }

    final int index(int i1, int i2, int i3) {
        return offset + stride3*i3 + stride2*i2 + stride1*i1;
    }

    @Override
    public final double get(int i1, int i2, int i3) {
        return data[offset + stride3*i3 + stride2*i2 + stride1*i1];
    }

    @Override
    public final void set(int i1, int i2, int i3, double value) {
        data[offset + stride3*i3 + stride2*i2 + stride1*i1] = value;
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
                        data[j3] = value;
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i3 = 0; i3 < dim3; ++i3) {
                int j3 = stride3*i3 + offset;
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

    @Override
    public void fill(DoubleGenerator generator) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        data[j3] = generator.nextDouble();
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i3 = 0; i3 < dim3; ++i3) {
                int j3 = stride3*i3 + offset;
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

    @Override
    public void increment(double value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        data[j3] += value;
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i3 = 0; i3 < dim3; ++i3) {
                int j3 = stride3*i3 + offset;
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

    @Override
    public void decrement(double value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        data[j3] -= value;
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i3 = 0; i3 < dim3; ++i3) {
                int j3 = stride3*i3 + offset;
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

    @Override
    public void scale(double value) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        data[j3] *= value;
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i3 = 0; i3 < dim3; ++i3) {
                int j3 = stride3*i3 + offset;
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

    @Override
    public void map(DoubleFunction function) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    for (int i3 = 0; i3 < dim3; ++i3) {
                        int j3 = stride3*i3 + j2;
                        data[j3] = function.apply(data[j3]);
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i3 = 0; i3 < dim3; ++i3) {
                int j3 = stride3*i3 + offset;
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
                        if (initialized) {
                            scanner.update(data[j3]);
                        } else {
                            scanner.initialize(data[j3]);
                            initialized = true;
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i3 = 0; i3 < dim3; ++i3) {
                int j3 = stride3*i3 + offset;
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

    @Override
    public double[] flatten(boolean forceCopy) {
        if (! forceCopy && isFlat()) {
            return data;
        }
        double[] out = new double[number];
        int j = -1;
        for (int i3 = 0; i3 < dim3; ++i3) {
            int j3 = stride3*i3 + offset;
            for (int i2 = 0; i2 < dim2; ++i2) {
                int j2 = stride2*i2 + j3;
                for (int i1 = 0; i1 < dim1; ++i1) {
                    int j1 = stride1*i1 + j2;
                    out[++j] = data[j1];
                }
            }
        }
        return out;
    }
    @Override
    public Double2D slice(int idx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double2D slice(int idx, int dim) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double3D view(Range rng1, Range rng2, Range rng3) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double3D view(int[] idx1, int[] idx2, int[] idx3) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double1D as1D() {
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
