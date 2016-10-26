/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014-2016 the MiTiV project, http://mitiv.univ-lyon1.fr/
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

package mitiv.linalg.shaped;

import mitiv.base.Traits;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.NotImplementedException;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.Vector;

public class DenseOperator extends LinearOperator {
    private final Object data;
    private final boolean single;

    /**
     * Create a dense operator given its coefficients as a shaped vector.
     *
     * @param vec
     *        The vector of coefficients. The {@code n} leading dimensions of
     *        {@code vec} corresponds to the output space of the operator; while
     *        the remaining trailing dimensions correspond to the input space.
     *
     * @param n
     *        The number of dimensions of the the output space of the operator.
     */
    public DenseOperator(ShapedVector vec, int n) {
        super(splitSpace(vec, n, true), splitSpace(vec, n, false));
        if (vec.getType() == Traits.FLOAT) {
            single = true;
            data = ((FloatShapedVector)vec).getData();
        } else {
            single = false;
            data = ((DoubleShapedVector)vec).getData();
        }
    }

    @Override
    protected void _apply(Vector dst, Vector src, int job) throws NotImplementedException {
        if (job != DIRECT && job != ADJOINT) {
            throw new NotImplementedException("Only direct and adjoint operators are implemented");
        }
        final int nrows = getOutputSpace().getNumber();
        final int ncols = getInputSpace().getNumber();
        if (single) {
            float[] a = (float[])data;
            float[] x = ((FloatShapedVector)src).getData();
            float[] y = ((FloatShapedVector)dst).getData();
            if (job == DIRECT) {
                /* Apply direct operator. */
                for (int i = 0; i < nrows; ++i) {
                    y[i] = 0;
                }
                for (int j = 0; j < ncols; ++j) {
                    float xj = x[j];
                    int off = nrows*j;
                    for (int i = 0; i < nrows; ++i) {
                        y[i] += a[i + off]*xj;
                    }
                }
            } else {
                /* Apply adjoint operator. */
                for (int j = 0; j < ncols; ++j) {
                    float sum = 0;
                    int off = nrows*j;
                    for (int i = 0; i < nrows; ++i) {
                        sum += a[i + off]*x[i];
                    }
                    y[j] = sum;
                }
            }
        } else {
            double[] a = (double[])data;
            double[] x = ((DoubleShapedVector)src).getData();
            double[] y = ((DoubleShapedVector)dst).getData();
            if (job == DIRECT) {
                /* Apply direct operator. */
                for (int i = 0; i < nrows; ++i) {
                    y[i] = 0;
                }
                for (int j = 0; j < ncols; ++j) {
                    double xj = x[j];
                    int off = nrows*j;
                    for (int i = 0; i < nrows; ++i) {
                        y[i] += a[i + off]*xj;
                    }
                }
            } else if (job == ADJOINT) {
                /* Apply adjoint operator. */
                for (int j = 0; j < ncols; ++j) {
                    double sum = 0;
                    int off = nrows*j;
                    for (int i = 0; i < nrows; ++i) {
                        sum += a[i + off]*x[i];
                    }
                    y[j] = sum;
                }
            }
        }
    }

    private static ShapedVectorSpace splitSpace(ShapedVector vec, int n, boolean input) {
        final int type = vec.getType();
        final int rank = vec.getRank();
        if (n < 1 || n >= rank) {
            throw new IndexOutOfBoundsException("Output rank is out of bounds");
        }
        int[] dims;
        if (input) {
            dims = new int[rank - n];
            for (int k = 0; k < rank - n; ++k) {
                dims[k] = vec.getDimension(n + k);
            }
        } else {
            dims = new int[n];
            for (int k = 0; k < n; ++k) {
                dims[k] = vec.getDimension(k);
            }
        }
        if (type == Traits.FLOAT) {
            return new FloatShapedVectorSpace(dims);
        } else if (type == Traits.FLOAT) {
            return new FloatShapedVectorSpace(dims);
        } else {
            throw new IllegalTypeException("Only generic floating-point types are supported");
        }
    }

}

