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

import mitiv.exception.NotImplementedException;
import mitiv.linalg.DoubleVector;
import mitiv.linalg.FloatVector;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.RealComplexFFT;
import mitiv.linalg.Utils;
import mitiv.linalg.Vector;

/**
 * 
 * @author Leger Jonathan
 *
 */
public class ConvolutionOperator extends LinearOperator {

    protected RealComplexFFT FFT = null;
    protected Vector mtf;
    protected Vector tmp;
    protected int size = 0; // number of values in the direct space
    
    /**
     * The goal of the convolution operator is to make the operation:
     * F'.diag(ĥ).F
     * 
     * @param FFT
     * @param psf
     */
    public ConvolutionOperator(RealComplexFFT FFT, Vector psf) {
        super(FFT.getInputSpace());
        this.FFT = FFT;
        mtf = FFT.getOutputSpace().create();
        FFT.apply(psf, mtf);
        tmp = FFT.getOutputSpace().create();
        size = FFT.getInputSpace().getSize();
    }

    protected void privApply(final Vector src, Vector dst, int job) {
        if (job != DIRECT && job != ADJOINT) {
            throw new NotImplementedException("For now we do not implement inverse convolution operations "+
                    "(talk to Éric if you ignore the dangers of doing that!)");
        }
        FFT.apply(src, tmp, DIRECT);
        if (inputSpace.getType() == Utils.TYPE_DOUBLE) {
            final double[] h = ((DoubleVector)mtf).getData();
            double[] z = ((DoubleVector)tmp).getData(); 
            if (job == DIRECT) {
                for (int k = 0; k < size; ++k) {
                    double h_re = h[2*k];
                    double h_im = h[2*k+1];
                    double z_re = z[2*k];
                    double z_im = z[2*k+1];
                    z[2*k]   = h_re*z_re - h_im*z_im;
                    z[2*k+1] = h_re*z_im + h_im*z_re;
                }
            } else {
                for (int k = 0; k < size; ++k) {
                    double h_re = h[2*k];
                    double h_im = h[2*k+1];
                    double z_re = z[2*k];
                    double z_im = z[2*k+1];
                    z[2*k]   = h_re*z_re + h_im*z_im;
                    z[2*k+1] = h_re*z_im - h_im*z_re;
                }
            }
        } else if (inputSpace.getType() == Utils.TYPE_FLOAT) {
            final float[] h = ((FloatVector)mtf).getData();
            float[] z = ((FloatVector)tmp).getData(); 
            if (job == DIRECT) {
                for (int k = 0; k < size; ++k) {
                    float h_re = h[2*k];
                    float h_im = h[2*k+1];
                    float z_re = z[2*k];
                    float z_im = z[2*k+1];
                    z[2*k]   = h_re*z_re - h_im*z_im;
                    z[2*k+1] = h_re*z_im + h_im*z_re;
                }
            } else {
                for (int k = 0; k < size; ++k) {
                    float h_re = h[2*k];
                    float h_im = h[2*k+1];
                    float z_re = z[2*k];
                    float z_im = z[2*k+1];
                    z[2*k]   = h_re*z_re + h_im*z_im;
                    z[2*k+1] = h_re*z_im - h_im*z_re;
                }
            }
        } else {
            throw new IllegalArgumentException("Unexpected data type (MUST BE A BUG).");
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
