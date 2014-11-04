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

package mitiv.linalg.shaped;
import mitiv.base.Shape;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.Vector;

/* The following lines are for the 3.0 version of JTransforms. */
import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.DoubleFFT_2D;
import org.jtransforms.fft.DoubleFFT_3D;
import org.jtransforms.fft.FloatFFT_1D;
import org.jtransforms.fft.FloatFFT_2D;
import org.jtransforms.fft.FloatFFT_3D;

/* The following lines are for the 2.4 version of JTransforms. */
//import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
//import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;
//import edu.emory.mathcs.jtransforms.fft.DoubleFFT_3D;
//import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;
//import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;
//import edu.emory.mathcs.jtransforms.fft.FloatFFT_3D;

/**
 * Emulate real-complex FFT-1D/2D/3D.
 * 
 * @author Éric and Jonathan
 */
public class RealComplexFFT extends ShapedLinearOperator {

    private Object xform = null;
    private double[] tempDouble = null;
    private float[] tempFloat = null;
    private final int number; // number of values in the direct space
    private final int rank; // number of dimensions
    private final Shape shape; // shape in the direct space
    private final boolean single;
    public boolean useSystemArrayCopy = false;

    public RealComplexFFT(ShapedVectorSpace space) {
        super(space, complexSpace(space));
        this.number = space.getNumber();
        this.shape = space.getShape();
        single = (space.getType() == FLOAT);
        this.rank = space.getRank();
        if (this.rank < 1 || this.rank > 3) {
            throw new IllegalArgumentException("Only 1D, 2D or 3D transforms supported");
        }
    }

    static private ShapedVectorSpace complexSpace(ShapedVectorSpace realSpace) {
        ShapedVectorSpace complexSpace;
        Shape realShape =realSpace.getShape();
        if (realSpace.getRank() < 1) {
            throw new IllegalArgumentException("Rank must be at least 1 for the FFT.");
        }
        int[] complexDims = realShape.copyDimensions();
        complexDims[0] *= 2;
        Shape complexShape = Shape.make(complexDims);
        int type = realSpace.getType();
        if (type == FLOAT) {
            complexSpace = new FloatShapedVectorSpace(complexShape);
        } else if (type == DOUBLE) {
            complexSpace = new DoubleShapedVectorSpace(complexShape);
        } else {
            throw new IllegalArgumentException("Only float or double supported");
        }
        return complexSpace;
    }

    @Override
    protected void privApply(Vector src, Vector dst, int job) {
        if (single) {
            /* Single precision version of the code. */
            if (xform == null) {
                /* Create low-level FFT operator. */
                if (rank == 1) {
                    xform = new FloatFFT_1D(shape.dimension(0));
                } else if (rank == 2) {
                    xform = new FloatFFT_2D(shape.dimension(1), shape.dimension(0));
                } else {
                    xform = new FloatFFT_3D(shape.dimension(2), shape.dimension(1), shape.dimension(0));
                }
            }
            if (tempFloat == null) {
                tempFloat = new float[2*number];
            }
            float[] w = tempFloat;
            float[] x = ((FloatShapedVector)src).getData();
            float[] y = ((FloatShapedVector)dst).getData();
            if (job == LinearOperator.ADJOINT || job == LinearOperator.INVERSE) {
                if (useSystemArrayCopy) {
                    System.arraycopy(x, 0, w, 0, 2*number);
                } else {
                    for (int k = 0; k < number; ++k) {
                        int real = k + k;
                        int imag = real + 1;
                        w[real] = x[real];
                        w[imag] = x[imag];
                    }
                }
                if (rank == 1) {
                    ((FloatFFT_1D)xform).complexInverse(w, false);
                } else if (rank == 2) {
                    ((FloatFFT_2D)xform).complexInverse(w, false);
                } else {
                    ((FloatFFT_3D)xform).complexInverse(w, false);
                }
                if (job == LinearOperator.INVERSE) {
                    /* Copy real part with scaling in the destination array. */
                    float s = 1.0F/number;
                    for (int k = 0; k < number; ++k) {
                        y[k] = s*w[2*k];
                    }
                } else {
                    /* Copy real part in the destination array. */
                    for (int k = 0; k < number; ++k) {
                        y[k] = w[2*k];
                    }
                }
            } else {
                if (job == LinearOperator.DIRECT) {
                    for (int k = 0; k < number; ++k) {
                        y[2*k] = x[k];
                        y[2*k+1] = 0.0F;
                    }
                } else {
                    float s = 1.0F/number;
                    for (int k = 0; k < number; ++k) {
                        y[2*k] = s*x[k];
                        y[2*k+1] = 0.0F;
                    }

                }
                if (rank == 1) {
                    ((FloatFFT_1D)xform).complexForward(y);
                } else if (rank == 2) {
                    ((FloatFFT_2D)xform).complexForward(y);
                } else {
                    ((FloatFFT_3D)xform).complexForward(y);
                }
            }
        } else {
            /* Double precision version of the code. */
            if (xform == null) {
                /* Create low-level FFT operator. */
                if (rank == 1) {
                    xform = new DoubleFFT_1D(shape.dimension(0));
                } else if (rank == 2) {
                    xform = new DoubleFFT_2D(shape.dimension(1), shape.dimension(0));
                } else {
                    xform = new DoubleFFT_3D(shape.dimension(2), shape.dimension(1), shape.dimension(0));
                }
            }
            if (tempDouble == null) {
                tempDouble = new double[2*number];
            }
            double[] w = tempDouble;
            double[] x = ((DoubleShapedVector)src).getData();
            double[] y = ((DoubleShapedVector)dst).getData();
            if (job == LinearOperator.ADJOINT || job == LinearOperator.INVERSE) {
                if (useSystemArrayCopy) {
                    System.arraycopy(x, 0, w, 0, 2*number);
                } else {
                    for (int k = 0; k < number; ++k) {
                        int real = k + k;
                        int imag = real + 1;
                        w[real] = x[real];
                        w[imag] = x[imag];
                    }
                }
                if (rank == 1) {
                    ((DoubleFFT_1D)xform).complexInverse(w, false);
                } else if (rank == 2) {
                    ((DoubleFFT_2D)xform).complexInverse(w, false);
                } else {
                    ((DoubleFFT_3D)xform).complexInverse(w, false);
                }
                if (job == LinearOperator.INVERSE) {
                    /* Copy real part with scaling in the destination array. */
                    double s = 1.0/number;
                    for (int k = 0; k < number; ++k) {
                        y[k] = s*w[2*k];
                    }
                } else {
                    /* Copy real part in the destination array. */
                    for (int k = 0; k < number; ++k) {
                        y[k] = w[2*k];
                    }
                }
            } else {
                if (job == LinearOperator.DIRECT) {
                    for (int k = 0; k < number; ++k) {
                        y[2*k] = x[k];
                        y[2*k+1] = 0.0;
                    }
                } else {
                    double s = 1.0/number;
                    for (int k = 0; k < number; ++k) {
                        y[2*k] = s*x[k];
                        y[2*k+1] = 0.0;
                    }

                }
                if (rank == 1) {
                    ((DoubleFFT_1D)xform).complexForward(y);
                } else if (rank == 2) {
                    ((DoubleFFT_2D)xform).complexForward(y);
                } else {
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
