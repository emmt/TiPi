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

package mitiv.deconv.impl;

import mitiv.array.FloatArray;
import mitiv.array.ShapedArray;
import mitiv.base.Traits;
import mitiv.deconv.Convolution;
import mitiv.exception.IncorrectSpaceException;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Implements abstract class for FFT-based convolution of arrays of float's.
 *
 * @author Éric Thiébaut
 */
public abstract class ConvolutionFloat extends Convolution {

    /* Workspace arrays. */
    private float[] tmp = null;   // complex workspace
    private float[] mtf = null;   // complex MTF

    /**
     * The following constructors make this class non instantiable, but still
     * let others inherit from this class.
     */
    protected ConvolutionFloat(ShapedVectorSpace space) {
        super(space);
        if (space.getType() != Traits.FLOAT) {
            throw new IllegalArgumentException("Vector space must be for float data type");
        }
    }

    /**
     * Retrieve the internal workspace used for in-place FFT.
     *
     * The methods {@link #forwardFFT()} and {@link #backwardFFT()} are
     * applied in-place to the internal workspace array of the instance of
     * this class.  This workspace is large enough to store
     * <tt>2*<b>N</b></tt> values with <tt><b>N</b></tt> the number of
     * elements of the convolution.
     *
     * @return The internal workspace used for in-place FFT.
     */
    public float[] getWorkspace() {
        if (tmp == null) {
            tmp = new float[2*number];
        }
        return tmp;
    }

    @Override
    public void push(ShapedVector inp) {
        if (! inp.belongsTo(space)) {
            throw new IllegalArgumentException("Input vector does not belong to correct vector space");
        }
        push(((FloatShapedVector)inp).getData());
    }

    /** Copy input array in workspace and make it complex. */
    public void push(float x[]) {
        final float zero = 0;
        float z[] = getWorkspace();
        if (x == null || x.length != number) {
            throw new IllegalArgumentException("Bad input size");
        }
        for (int k = 0; k < number; ++k) {
            int real = k + k;
            int imag = real + 1;
            z[real] = x[k];
            z[imag] = zero;
        }
    }

    @Override
    public void pull(ShapedVector out) {
        if (! out.belongsTo(space)) {
            throw new IllegalArgumentException("Output vector does not belong to correct vector space");
        }
        pull(((FloatShapedVector)out).getData());
    }

    /** Copy real part of workspace into output array. */
    public void pull(float x[]) {
        if (x == null || x.length != number) {
            throw new IllegalArgumentException("Bad output size");
        }
        float z[] = getWorkspace();
        for (int k = 0; k < number; ++k) {
            int real = k + k;
            x[k] = z[real];
        }
    }

    /** Apply in-place forward complex FFT. */
    public abstract void forwardFFT(float z[]);

    @Override
    public void forwardFFT() {
        forwardFFT(getWorkspace());
    }

    /** Apply in-place backward complex FFT. */
    public abstract void backwardFFT(float z[]);

    @Override
    public void backwardFFT() {
        forwardFFT(getWorkspace());
    }

    /**
     * Compute F^*.diag(mtf).F.x
     * where x is internal workspace.
     */
    public float[] convolve(boolean conj) {
        if (mtf == null) {
            throw new IllegalArgumentException("You must set the PSF or the MTF first");
        }
        float h[] = mtf;
        float z[] = getWorkspace();

        /* Apply forward complex FFT, multiply by the MTF and
         * apply backward FFT. */
        forwardFFT();
        if (conj) {
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
        } else {
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
        }
        backwardFFT(z);
        return z;
    }

    @Override
    public void setPSF(ShapedVector psf) {
        if (! psf.belongsTo(space)) {
            throw new IncorrectSpaceException("PSF does not belong to the correct space");
        }
        computeMTF(((FloatShapedVector)psf).getData());
    }

    @Override
    public void setPSF(ShapedArray psf, int[] cen) {
        psf = adjustPSF(psf.toFloat(), cen);
        computeMTF(((FloatArray)psf).flatten());
    }

    private final void computeMTF(float[] psf) {
        final float zero = 0;
        if (mtf == null) {
            mtf = new float[2*number];
        }
        for (int k = 0; k < number; ++k) {
            int real = k + k;
            int imag = real + 1;
            mtf[real] = psf[k];
            mtf[imag] = zero;
        }
        forwardFFT(mtf);
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
