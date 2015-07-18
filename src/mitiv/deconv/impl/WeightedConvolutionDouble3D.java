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

package mitiv.deconv.impl;

import mitiv.array.DoubleArray;
import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.deconv.WeightedConvolutionOperator;
import mitiv.exception.IncorrectSpaceException;
import mitiv.exception.NotImplementedException;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

import org.jtransforms.fft.DoubleFFT_3D;

/**
 * Implements a FFT-based weighted convolution for 3D arrays of double's.
 *
 * <p>
 * It is recommended not to directly instantiate this class but rather use
 * one of the factory methods of the parent class
 * {@link  WeightedConvolutionOperator}.{@code build()}.  Have a look at the
 * documentation of {@link  WeightedConvolutionOperator} for a description
 * of what exaclty does this kind of operator.
 * </p>
 * @author Éric Thiébaut
 *
 * @see {@link WeightedConvolutionOperator}
 */
public class ConvolutionDouble3D extends WeightedConvolutionOperator {

    /* FFT operator and workspace arrays. */
    private DoubleFFT_3D fft = null;
    private double[] tmp = null;   // complex workspace
    private double[] wgt = null;   // array of weights (can be null)
    private double[] mtf = null;   // complex MTF

    /* Attributes that remains constant after creation. */
    private final int number; // number of values in the direct space
    private static final int rank = 3;
    private final int dim1; // 1st output dimension
    private final int dim2; // 2nd output dimension
    private final int dim3; // 3rd output dimension
    private final int offset; // offset of first output element in complex workspace
    private static final int stride1 = 2; // stride along 1st input dimension
    private final int stride2; // stride along 2nd input dimension
    private final int stride3; // stride along 3rd input dimension

    /**
     * Create a new FFT-based convolution operator given the PSF.
     *
     * @param FFT - The Fast Fourier Transform operator.
     * @param psf - The point spread function.
     */
    public ConvolutionDouble3D(ShapedVectorSpace inputSpace,
            ShapedVectorSpace outputSpace, int[] first) {
        super(inputSpace, outputSpace);

        /* Check type. */
        if (inputSpace.getType() != Traits.DOUBLE ||
            outputSpace.getType() != Traits.DOUBLE) {
            throw new IllegalArgumentException("Input and output spaces must be for double data type");
        }

        /* Check rank and dimensions. */
        Shape inputShape = inputSpace.getShape();
        Shape outputShape = outputSpace.getShape();
        offset = outputOffset(rank, inputShape, outputShape, first);
        number = (int)inputShape.number();
        dim1 = outputShape.dimension(0);
        dim2 = outputShape.dimension(1);
        dim3 = outputShape.dimension(2);
        stride2 = stride1*inputShape.dimension(0);
        stride3 = stride2*inputShape.dimension(1);
    }

    @Override
    public void setPSF(ShapedVector vec) {
        if (! vec.belongsTo(getInputSpace())) {
            throw new IncorrectSpaceException("PSF must belong to the input space of the operator.");
        }
        computeMTF(((DoubleShapedVector)vec).getData());
    }

    @Override
    public void setPSF(ShapedArray arr, int[] cen) {
        arr = adjustPSF(arr.toDouble(), cen);
        computeMTF(((DoubleArray)arr).flatten());
    }

    private final void computeMTF(double[] psf) {
        final double zero = 0;
        if (mtf == null) {
            mtf = new double[2*number];
        }
        for (int k = 0; k < number; ++k) {
            int real = k + k;
            int imag = real + 1;
            mtf[real] = psf[k];
            mtf[imag] = zero;
        }
        forwardFFT(mtf);
    }

    @Override
    public void setWeights(ShapedVector vec, boolean copy) {
        if (vec == null) {
            wgt = null;
        } else {
            if (! vec.belongsTo(getOutputSpace())) {
                throw new IllegalArgumentException("Weights must be a vector of the output space of the operator.");
            }
            wgt = checkWeights(((DoubleShapedVector)vec).getData(), copy);
        }
    }

    @Override
    public void setWeights(ShapedArray arr, boolean copy) {
        if (arr == null) {
            wgt = null;
        } else {
            if (! getOutputSpace().getShape().equals(arr.getShape())) {
                throw new IllegalArgumentException("Weights must have the same shape as the vectors of the output space of the operator.");
            }
            wgt = checkWeights(arr.toDouble().flatten(copy), false);
        }
    }

    /** Create low-level FFT operator. */
    private final void createFFT() {
        if (fft == null) {
            Shape shape = getInputSpace().getShape();
            timerForFFT.resume();
            fft = new DoubleFFT_3D(shape.dimension(2), shape.dimension(1), shape.dimension(0));
            timerForFFT.stop();
        }
    }

    /** Apply forward complex FFT. */
    private final void forwardFFT(double[] z) {
        if (fft == null) {
            createFFT();
        }
        timerForFFT.resume();
        fft.complexForward(z);
        timerForFFT.stop();
    }

    /** Apply backward precision complex FFT. */
    private final void backwardFFT(double[] z) {
        if (fft == null) {
            createFFT();
        }
        timerForFFT.resume();
        fft.complexInverse(z, false);
        timerForFFT.stop();
    }

    @Override
    protected void privApply(Vector src, Vector dst, int job) {
        if (job != DIRECT && job != ADJOINT) {
            throw new NotImplementedException("For now we do not implement inverse convolution operations "+
                    "(talk to a specialist if you ignore the dangers of doing that!)");
        }
        if (mtf == null) {
            throw new IllegalArgumentException("You must set the PSF or the MTF first.");
        }
        if (fft == null) {
            createFFT();
        }
        if (tmp == null) {
            tmp = new double[2*number];
        }
        timer.resume();
        if (job == DIRECT) {
            applyDirect(mtf, wgt, ((DoubleShapedVector)src).getData(),
                        ((DoubleShapedVector)dst).getData(), tmp);
        } else {
            applyAdjoint(mtf, wgt, ((DoubleShapedVector)dst).getData(),
                         ((DoubleShapedVector)src).getData(), tmp);
        }
        timer.stop();
    }

    /** Direct operator for single precision variables. */
    private final void applyDirect(double[] h, double[] w, double[] x,
                                   double[] y, double[] z) {
        final double zero = 0;
        final double one = 1;

        /* Copy input array in workspace and make it complex. */
        for (int k = 0; k < number; ++k) {
            int real = k + k;
            int imag = real + 1;
            z[real] = x[k];
            z[imag] = zero;
        }

        /* Apply forward complex FFT, multiply by the MTF and
         * apply backward FFT. */
        forwardFFT(z);
        for (int k = 0; k < number; ++k) {
            int real = k + k;
            int imag = real + 1;
            double h_re = h[real];
            double h_im = h[imag];
            double z_re = z[real];
            double z_im = z[imag];
            z[real] = h_re*z_re - h_im*z_im;
            z[imag] = h_re*z_im + h_im*z_re;
        }
        backwardFFT(z);

        /* Select and scale. */
        final double s = one/number;
        int i = -1;
        if (w == null) {
            for (int i3 = 0; i3 < dim3; ++i3) {
                int j3 = offset + stride3*i3;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = j3 + stride2*i2;
                    for (int i1 = 0; i1 < dim1; ++i1) {
                        y[++i] = s*z[j2 + stride1*i1];
                    }
                }
            }
        } else {
            for (int i3 = 0; i3 < dim3; ++i3) {
                int j3 = offset + stride3*i3;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = j3 + stride2*i2;
                    for (int i1 = 0; i1 < dim1; ++i1) {
                        ++i;
                        y[i] = s*w[i]*z[j2 + stride1*i1];
                    }
                }
            }
        }
    }

    /** Adjoint operator for single precision variables. */
    private final void applyAdjoint(double[] h, double[] w, double[] x,
                                    double[] y, double[] z) {
        final double zero = 0;
        final double one = 1;

        /* Zero-fill workspace. (FIXME: improve this part, it is not
         * necessary to fill all parts, can be mixed with the next
         * operation.) */
        for (int k = 0; k < number; ++k) {
            int real = k + k;
            int imag = real + 1;
            z[real] = zero;
            z[imag] = zero;
        }

        /* Scale and expand. */
        final double s = one/number;
        int i = -1;
        if (w == null) {
            for (int i3 = 0; i3 < dim3; ++i3) {
                int j3 = offset + stride3*i3;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = j3 + stride2*i2;
                    for (int i1 = 0; i1 < dim1; ++i1) {
                        z[j2 + stride1*i1] = s*y[++i];
                    }
                }
            }
        } else {
            for (int i3 = 0; i3 < dim3; ++i3) {
                int j3 = offset + stride3*i3;
                for (int i2 = 0; i2 < dim2; ++i2) {
                    int j2 = j3 + stride2*i2;
                    for (int i1 = 0; i1 < dim1; ++i1) {
                        ++i;
                        z[j2 + stride1*i1] = s*w[i]*y[i];
                    }
                }
            }
        }

        /* Apply forward FFT, multiply by the conjugate of the MTF and
         * apply backward FFT. */
        forwardFFT(z);
        for (int k = 0; k < number; ++k) {
            int real = k + k;
            int imag = real + 1;
            double h_re = h[real];
            double h_im = h[imag];
            double z_re = z[real];
            double z_im = z[imag];
            z[real] = h_re*z_re + h_im*z_im;
            z[imag] = h_re*z_im - h_im*z_re;
        }
        backwardFFT(z);

        /* Copy real part of workspace into output array. */
        for (int k = 0; k < number; ++k) {
            int real = k + k;
            x[k] = z[real];
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
