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
import mitiv.array.Float2D;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.FloatFunction;
import mitiv.base.mapping.FloatScanner;
import mitiv.random.FloatGenerator;

/**
 * Stridden implementation of 2-dimensional arrays of float's.
 *
 * @author Éric Thiébaut.
 */
public class StriddenFloat2D extends Float2D {
    final int order;
    final float[] data;
    final int offset;
    final int stride1;
    final int stride2;

    public StriddenFloat2D(float[] arr, int offset, int[] stride, int[] dims) {
        super(dims);
        if (stride.length != 2) {
            throw new IllegalArgumentException("There must be as many strides as the rank.");
        }
        this.data = arr;
        this.offset = offset;
        stride1 = stride[0];
        stride2 = stride[1];
        this.order = Float2D.checkViewStrides(data.length, dim1, dim2, offset, stride1, stride2);
    }

    public StriddenFloat2D(float[] arr, int offset, int stride1, int dim1, int stride2, int dim2) {
        super(dim1, dim2);
        this.data = arr;
        this.offset = offset;
        this.stride1 = stride1;
        this.stride2 = stride2;
        this.order = Float2D.checkViewStrides(data.length, dim1, dim2, offset, stride1, stride2);
    }

    @Override
    public void checkSanity() {
        Float2D.checkViewStrides(data.length, dim1, dim2, offset, stride1, stride2);
    }

    private boolean isFlat() {
        return (offset == 0 && stride1 == 1 && stride2 == dim1);
    }

    final int index(int i1, int i2) {
        return offset + stride2*i2 + stride1*i1;
    }

    @Override
    public final float get(int i1, int i2) {
        return data[offset + stride2*i2 + stride1*i1];
    }

    @Override
    public final void set(int i1, int i2, float value) {
        data[offset + stride2*i2 + stride1*i1] = value;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public void fill(float value) {
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
    public void fill(FloatGenerator generator) {
        if (getOrder() == ROW_MAJOR) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                int j1 = stride1*i1 + offset;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = stride2*i2 + j1;
                    data[j2] = generator.nextFloat();
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < dim2; ++i2) {
                int j2 = stride2*i2 + offset;
                for (int i1 = 0; i1 < dim1; ++i1) {
                    int j1 = stride1*i1 + j2;
                    data[j1] = generator.nextFloat();
                }
            }
        }
    }

    @Override
    public void increment(float value) {
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
    public void decrement(float value) {
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
    public void scale(float value) {
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
    public void map(FloatFunction function) {
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
    public void scan(FloatScanner scanner)  {
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
    public float[] flatten(boolean forceCopy) {
        if (! forceCopy && isFlat()) {
            return data;
        }
        float[] out = new float[number];
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
    public Float1D slice(int idx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Float1D slice(int idx, int dim) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Float2D view(Range rng1, Range rng2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Float2D view(int[] idx1, int[] idx2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Float1D as1D() {
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
