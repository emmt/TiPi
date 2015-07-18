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

import mitiv.linalg.shaped.ShapedVectorSpace;
import org.jtransforms.fft.DoubleFFT_1D;

/**
 * Implements FFT-based convolution for 1D arrays of double's.
 *
 * @author Éric Thiébaut
 */
public class ConvolutionDouble1D extends ConvolutionDouble {

    /* FFT operator. */
    private DoubleFFT_1D fft = null;

    /**
     * Create a new FFT-based convolution operator given the PSF.
     *
     * @param FFT - The Fast Fourier Transform operator.
     * @param psf - The point spread function.
     */
    public ConvolutionDouble1D(ShapedVectorSpace space) {
        super(space);
        if (space.getRank() != 1) {
            throw new IllegalArgumentException("Vector space must be have 1 dimension(s)");
        }
    }

    /** Create low-level FFT operator. */
    private final void createFFT() {
        if (fft == null) {
            timerForFFT.resume();
            fft = new DoubleFFT_1D(shape.dimension(0));
            timerForFFT.stop();
        }
    }

    /** Apply in-place forward complex FFT. */
    @Override
    public final void forwardFFT(double z[]) {
        if (z.length != 2*number) {
            throw new IllegalArgumentException("Bad workspace size");
        }
        if (fft == null) {
            createFFT();
        }
        timerForFFT.resume();
        fft.complexForward(z);
        timerForFFT.stop();
    }

    /** Apply in-place backward complex FFT. */
    @Override
    public final void backwardFFT(double z[]) {
        if (z.length != 2*number) {
            throw new IllegalArgumentException("Bad workspace size");
        }
        if (fft == null) {
            createFFT();
        }
        timerForFFT.resume();
        fft.complexInverse(z, false);
        timerForFFT.stop();
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
