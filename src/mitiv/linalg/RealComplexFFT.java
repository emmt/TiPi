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

package mitiv.linalg;
import edu.emory.mathcs.jtransforms.fft.*;

/**
 * Emulate real-complex FFT-1D/2D/3D.
 * @author ...
 *
 */
public class RealComplexFFT extends LinearOperator {

    private Object xform = null;
    private double[] tempDouble = null;
    private float[] tempFloat = null;
    private int size = 0; // number of values in the direct space
    private int rank = 0; // number of dimensions
    private int[] shape;
    private boolean single = false;

    private RealComplexFFT(VectorSpace inputSpace, VectorSpace outputSpace,
            int[] shape, boolean single) {
        super(inputSpace, outputSpace);
        this.size = inputSpace.getSize();
        this.rank = shape.length;
        this.shape = shape;
        this.single = single;
    }

    public RealComplexFFT(DoubleVectorSpaceWithRank space) {
        this(space, complexSpace(space), space.cloneShape(), false);
    }

    public RealComplexFFT(FloatVectorSpaceWithRank space) {
        this(space, complexSpace(space), space.cloneShape(), true);
    }

    static private DoubleVectorSpaceWithRank complexSpace(DoubleVectorSpaceWithRank realSpace) {
        int[] complexShape = realSpace.cloneShape();
        complexShape[0] *= 2;
        return new DoubleVectorSpaceWithRank(complexShape);
    }

    static private FloatVectorSpaceWithRank complexSpace(FloatVectorSpaceWithRank realSpace) {
        int[] complexShape = realSpace.cloneShape();
        complexShape[0] *= 2;
        return new FloatVectorSpaceWithRank(complexShape);
    }

    @Override
    protected void privApply(Vector src, Vector dst, int job) {
        if (single) {
            /* Single precision version of the code. */
            if (tempFloat == null) {
                tempFloat = new float[2*size];
            }
            float[] w = tempFloat;
            float[] x = ((FloatVector)src).getData();
            float[] y = ((FloatVector)dst).getData();
            if (job == LinearOperator.ADJOINT || job == LinearOperator.INVERSE) {
                for (int k = 0; k < size; ++k) {
                    w[2*k] = x[2*k];
                    w[2*k+1] = x[2*k+1];
                }
                if (rank == 1) {
                    if (xform == null) xform = new FloatFFT_1D(shape[0]);
                    ((FloatFFT_1D)xform).complexInverse(w, false);
                } else if (rank == 2) {
                    if (xform == null) xform = new FloatFFT_2D(shape[0], shape[1]);
                    ((FloatFFT_2D)xform).complexInverse(w, false);
                } else {
                    if (xform == null) xform = new FloatFFT_3D(shape[0], shape[1], shape[2]);
                    ((FloatFFT_3D)xform).complexInverse(w, false);
                }
                if (job == LinearOperator.INVERSE) {
                    /* Copy real part with scaling in the destination array. */
                    float s = 1.0F/size;
                    for (int k = 0; k < size; ++k) {
                        y[k] = s*w[2*k];
                    }
                } else {
                    /* Copy real part in the destination array. */
                    for (int k = 0; k < size; ++k) {
                        y[k] = w[2*k];
                    }
                }
            } else {
                if (job == LinearOperator.DIRECT) {
                    for (int k = 0; k < size; ++k) {
                        y[2*k] = x[k];
                        y[2*k+1] = 0.0F;
                    }
                } else {
                    float s = 1.0F/size;
                    for (int k = 0; k < size; ++k) {
                        y[2*k] = s*x[k];
                        y[2*k+1] = 0.0F;
                    }

                }
                if (rank == 1) {
                    if (xform == null) xform = new FloatFFT_1D(shape[0]);
                    ((FloatFFT_1D)xform).complexForward(y);
                } else if (rank == 2) {
                    if (xform == null) xform = new FloatFFT_2D(shape[0], shape[1]);
                    ((FloatFFT_2D)xform).complexForward(y);
                } else {
                    if (xform == null) xform = new FloatFFT_3D(shape[0], shape[1], shape[2]);
                    ((FloatFFT_3D)xform).complexForward(y);
                }
            }
        } else {
            /* Double precision version of the code. */
            if (tempDouble == null) {
                tempDouble = new double[2*size];
            }
            double[] w = tempDouble;
            double[] x = ((DoubleVector)src).getData();
            double[] y = ((DoubleVector)dst).getData();
            if (job == LinearOperator.ADJOINT || job == LinearOperator.INVERSE) {
                for (int k = 0; k < size; ++k) {
                    w[2*k] = x[2*k];
                    w[2*k+1] = x[2*k+1];
                }
                if (rank == 1) {
                    if (xform == null) xform = new DoubleFFT_1D(shape[0]);
                    ((DoubleFFT_1D)xform).complexInverse(w, false);
                } else if (rank == 2) {
                    if (xform == null) xform = new DoubleFFT_2D(shape[0], shape[1]);
                    ((DoubleFFT_2D)xform).complexInverse(w, false);
                } else {
                    if (xform == null) xform = new DoubleFFT_3D(shape[0], shape[1], shape[2]);
                    ((DoubleFFT_3D)xform).complexInverse(w, false);
                }
                if (job == LinearOperator.INVERSE) {
                    /* Copy real part with scaling in the destination array. */
                    double s = 1.0/size;
                    for (int k = 0; k < size; ++k) {
                        y[k] = s*w[2*k];
                    }
                } else {
                    /* Copy real part in the destination array. */
                    for (int k = 0; k < size; ++k) {
                        y[k] = w[2*k];
                    }
                }
            } else {
                if (job == LinearOperator.DIRECT) {
                    for (int k = 0; k < size; ++k) {
                        y[2*k] = x[k];
                        y[2*k+1] = 0.0;
                    }
                } else {
                    double s = 1.0/size;
                    for (int k = 0; k < size; ++k) {
                        y[2*k] = s*x[k];
                        y[2*k+1] = 0.0;
                    }

                }
                if (rank == 1) {
                    if (xform == null) xform = new DoubleFFT_1D(shape[0]);
                    ((DoubleFFT_1D)xform).complexForward(y);
                } else if (rank == 2) {
                    if (xform == null) xform = new DoubleFFT_2D(shape[0], shape[1]);
                    ((DoubleFFT_2D)xform).complexForward(y);
                } else {
                    if (xform == null) xform = new DoubleFFT_3D(shape[0], shape[1], shape[2]);
                    ((DoubleFFT_3D)xform).complexForward(y);
                }
            }
        }
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
