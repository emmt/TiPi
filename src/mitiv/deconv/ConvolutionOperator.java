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

package mitiv.deconv;

import mitiv.base.Traits;
import mitiv.exception.IncorrectSpaceException;
import mitiv.exception.NotImplementedException;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.RealComplexFFT;
import mitiv.linalg.shaped.ShapedLinearOperator;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Implements a FFT-based convolution.
 * 
 * <p>
 * The convolution operator {@code H} writes:
 * <pre>
 *   H = F'.diag(F.h).F</pre>
 * with {@code F} the FFT (Fast Fourier Transform) operator and {@code h} the
 * PSF (Point Spread Function).
 * 
 * @author Jonathan Léger
 */
public class ConvolutionOperator extends ShapedLinearOperator {

    protected RealComplexFFT FFT = null;
    protected Vector mtf;
    protected Vector tmp;
    protected final int number; // number of values in the direct space
    private final boolean single;

    /**
     * Create a new FFT-based convolution operator given the PSF.
     * 
     * @param FFT - The Fast Fourier Transform operator.
     * @param psf - The point spread function.
     */
    public ConvolutionOperator(RealComplexFFT FFT, Vector psf) {
        this(FFT, psf, null);
    }

    /**
     * Create a new FFT-based convolution operator given the PSF or the MTF.
     * 
     * <p>
     * At least one of the point spread function (PSF) or the modulation
     * transfer function (MTF) must be given (that is non-{@code null}).
     * If {@code mtf} is not {@code null} it is used as the MTF; otherwise
     * the FFT operator is applied to {@code psf} to compute the MTF.
     * Note that if the MTF is directly provided, a simple reference to
     * {@code mtf} is kept by the operator.  Thus, you may have to clone
     * {@code mtf} if you intend to modify its contents while using the
     * operator.
     * 
     * @param FFT - The Fast Fourier Transform operator.
     * @param psf - The point spread function (PSF) or {@code null}.
     * @param mtf - The modulation transfer function (MTF) or {@code null}.
     *            - If true, {@code h} is the MTF; otherwise, {@code h} is
     *              the PSF.  Note that if {@code h} is the MTF, a simple
     *              reference to it is kept by the operator.  Thus, you
     *              should clone {@code h} if you intend to modify its
     *              contents while using the operator.
     */
    public ConvolutionOperator(RealComplexFFT FFT, Vector psf, Vector mtf) {
        super(FFT.getInputSpace());
        ShapedVectorSpace realSpace = FFT.getInputSpace();
        ShapedVectorSpace complexSpace = FFT.getOutputSpace();
        if (psf != null && ! psf.belongsTo(realSpace)) {
            throw new IncorrectSpaceException("PSF must belong to the input space of the FFT operator");
        }
        if (mtf != null && ! mtf.belongsTo(complexSpace)) {
            throw new IncorrectSpaceException("MTF must belong to the output space of the FFT operator");
        }
        if (mtf == null) {
            if (psf == null) {
                throw new NullPointerException("At least one of PSF or MTF must be non-null");
            }
            this.mtf = complexSpace.create();
            FFT.apply(psf, this.mtf);
        } else {
            this.mtf = mtf;
        }
        this.FFT = FFT;
        tmp = complexSpace.create();
        number = realSpace.getNumber();
        int type = realSpace.getType();
        single = (type == Traits.FLOAT);
        if (! single && type != Traits.DOUBLE) {
            throw new IllegalArgumentException("Only float and double types supported");
        }
    }

    @Override
    protected void _apply(final Vector src, Vector dst, int job) {
        if (job != DIRECT && job != ADJOINT) {
            throw new NotImplementedException("For now we do not implement inverse convolution operations "+
                    "(talk to Éric if you ignore the dangers of doing that!)");
        }
        FFT.apply(src, tmp, DIRECT);
        if (single) {
            /* Single precision version. */
            float[] h = ((FloatShapedVector)mtf).getData();
            float[] z = ((FloatShapedVector)tmp).getData();
            if (job == DIRECT) {
                for (int k = 0; k < number; ++k) {
                    int real = k + k;
                    int imag = real + 1;
                    float h_re = h[real];
                    float h_im = h[imag];
                    float z_re = z[real];
                    float z_im = z[imag];
                    z[real] = h_re*z_re - h_im*z_im;
                    z[imag] = h_re*z_im + h_im*z_re;
                }
            } else {
                for (int k = 0; k < number; ++k) {
                    int real = k + k;
                    int imag = real + 1;
                    float h_re = h[real];
                    float h_im = h[imag];
                    float z_re = z[real];
                    float z_im = z[imag];
                    z[real] = h_re*z_re + h_im*z_im;
                    z[imag] = h_re*z_im - h_im*z_re;
                }
            }
        } else {
            /* Double precision version. */
            double[] h = ((DoubleShapedVector)mtf).getData();
            double[] z = ((DoubleShapedVector)tmp).getData();
            if (job == DIRECT) {
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
            } else {
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
            }
        }
        FFT.apply(tmp, dst, INVERSE);
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
