// *WARNING* This file has been automatically generated by TPP do not edit directly.
/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014-2015 the MiTiV project, http://mitiv.univ-lyon1.fr/
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

package mitiv.conv;

import mitiv.array.ArrayUtils;
import mitiv.array.FloatArray;
import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.conv.Convolution;
import mitiv.exception.IncorrectSpaceException;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.FloatShapedVectorSpace;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Implements abstract class for FFT-based convolution of arrays of float's.
 *
 * @author Éric Thiébaut
 */
abstract class ConvolutionFloat extends Convolution {

    /** Workspace array. */
    private float[] wrk = null;

    /** Complex modulation transfer function (MTF). */
    private float[] mtf = null;

    /**
     * The following constructor make this class non instantiable, but still
     * let others inherit from this class.
     */
    protected ConvolutionFloat(Shape wrk,
                           ShapedVectorSpace inp, int[] inpOff,
                           ShapedVectorSpace out, int[] outOff) {
        super(wrk, inp, inpOff, out, outOff);
        if (getType() != Traits.FLOAT) {
            throw new IllegalArgumentException("Input and output vector spaces must be for float data type");
        }
    }

    /**
     * Retrieve the internal workspace used for in-place FFT.
     *
     * <p> The methods {@link #forwardFFT()} and {@link #backwardFFT()} are
     * applied in-place to the internal workspace array of the instance of this
     * class.  This workspace is large enough to store <tt>2*<b>N</b></tt>
     * values with <tt><b>N</b></tt> the number of elements of the
     * convolution. </p>
     *
     * @return The internal workspace used for in-place FFT.
     */
    public float[] getWorkArray() {
        if (wrk == null) {
            wrk = new float[2*getNumberOfFrequencies()];
        }
        return wrk;
    }

    @Override
    public void push(ShapedVector src, boolean adjoint) {
        if (adjoint) {
            if (! src.belongsTo(outputSpace)) {
                throw new IncorrectSpaceException("Vector does not belong to output space");
            }
        } else {
            if (! src.belongsTo(inputSpace)) {
                throw new IncorrectSpaceException("Vector does not belong to input space");
            }
        }
        push(getWorkArray(), ((FloatShapedVector)src).getData(), adjoint);
    }

    @Override
    public void pull(ShapedVector dst, boolean adjoint) {
        if (adjoint) {
            if (! dst.belongsTo(inputSpace)) {
                throw new IncorrectSpaceException("Vector does not belong to input space");
            }
        } else {
            if (! dst.belongsTo(outputSpace)) {
                throw new IncorrectSpaceException("Vector does not belong to output space");
            }
        }
        pull(((FloatShapedVector)dst).getData(), getWorkArray(), adjoint);
    }

    /**
     * Copy user array to work array.
     *
     * <p> This methods applies operator <b>S</b> if <b>adjoint</b> is false
     * and operator <b>R</b><sup>*</sup> otherwise. </p>
     *
     * @param dst
     *        The destination work array.
     *
     * @param src
     *        The source user array.
     *
     * @param adjoint
     *        Push for the adjoint operation?
     */
    public abstract void push(float dst[], float src[], boolean adjoint);

    /**
     * Copy real part of work array into user array.
     *
     * @param dst
     *        The destination user array.
     *
     * @param src
     *        The source work array.
     *
     * @param adjoint
     *        Pull for the adjoint operation?
     */
    public abstract void pull(float dst[], float src[], boolean adjoint);

    /** Apply in-place forward complex FFT. */
    public abstract void forwardFFT(float z[]);

    @Override
    public void forwardFFT() {
        forwardFFT(getWorkArray());
    }

    /** Apply in-place backward complex FFT. */
    public abstract void backwardFFT(float z[]);

    @Override
    public void backwardFFT() {
        forwardFFT(getWorkArray());
    }

    /**
     * Compute {@code F^*.diag(mtf).F.x} where {@code x} is internal
     * workspace.
     */
    @Override
    public void convolve(boolean conj) {
        if (mtf == null) {
            throw new IllegalArgumentException("You must set the PSF or the MTF first");
        }
        final float h[] = mtf;
        final float z[] = getWorkArray();
        final int n = getNumberOfFrequencies();

        /* Apply forward complex FFT, multiply by the MTF and
         * apply backward FFT. */
        forwardFFT();
        if (conj) {
            for (int k = 0; k < n; ++k) {
                int real = k + k;
                int imag = real + 1;
                float h_re = h[real];
                float h_im = h[imag];
                float z_re = z[real];
                float z_im = z[imag];
                z[real] = h_re*z_re + h_im*z_im;
                z[imag] = h_re*z_im - h_im*z_re;
            }
        } else {
            for (int k = 0; k < n; ++k) {
                int real = k + k;
                int imag = real + 1;
                float h_re = h[real];
                float h_im = h[imag];
                float z_re = z[real];
                float z_im = z[imag];
                z[real] = h_re*z_re - h_im*z_im;
                z[imag] = h_re*z_im + h_im*z_re;
            }
        }
        backwardFFT(z);
    }

    @Override
    public void setPSF(ShapedVector psf) {
        if (! psf.belongsTo(inputSpace)) {
            throw new IncorrectSpaceException("PSF does not belong to the correct space");
        }
        computeMTF(((FloatShapedVector)psf).getData());
    }

    @Override
    public void setPSF(ShapedArray psf, int[] off, boolean normalize) {
        boolean writable = false;
        if (psf.getType() != Traits.FLOAT) {
            psf = psf.toFloat();
            writable = true;
        }
        if (normalize) {
            float sum = (float)ArrayUtils.sum(psf);
            if (sum != 1) {
                if (! writable) {
                    psf = psf.copy();
                }
                ((FloatArray)psf).scale(1.0F/sum);
            }
        }
        psf = adjustPSF(psf, off);
        computeMTF(((FloatArray)psf).flatten());
    }

    private final void computeMTF(float[] psf) {
        final float zero = 0;
        final int n = getNumberOfFrequencies();
        final float scale = 1.0F/(float)n;
        if (mtf == null) {
            mtf = new float[2*n];
        }
        for (int k = 0; k < n; ++k) {
            int real = k + k;
            int imag = real + 1;
            mtf[real] = psf[k]*scale;
            mtf[imag] = zero;
        }
        forwardFFT(mtf);
    }

    @Override
    public FloatShapedVectorSpace getInputSpace() {
        return (FloatShapedVectorSpace)inputSpace;
    }

    @Override
    public FloatShapedVectorSpace getOutputSpace() {
        return (FloatShapedVectorSpace)outputSpace;
    }
}