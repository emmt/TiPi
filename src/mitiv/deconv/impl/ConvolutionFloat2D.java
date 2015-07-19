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
import org.jtransforms.fft.FloatFFT_2D;

/**
 * Implements FFT-based convolution for 2D arrays of float's.
 *
 * @author Éric Thiébaut
 */
public class ConvolutionFloat2D extends ConvolutionFloat {

    /** FFT operator. */
    private FloatFFT_2D fft = null;

    /** Factor to scale the result of the backward FFT. */
    private final float scale;

    /** Number of input variables. */
    private final int number;

    /** Number of element along 1st dimension of the input variables. */
    private final int dim1;

    /** Number of element along 2nd dimension of the input variables. */
    private final int dim2;

    /** Offset of output along 1st input dimension. */
    private final int off1;

    /** Offset of output along 2nd input dimension. */
    private final int off2;

    /** End of output along 1st input dimension. */
    private final int end1;

    /** End of output along 2nd input dimension. */
    private final int end2;

    /** Fast pull operation? */
    private final boolean fastPull;

    /**
     * Create a new convolution operator for 2D arrays of float's.
     *
     * @param space - The input and output space.
     */
    public ConvolutionFloat2D(ShapedVectorSpace space) {
        super(space);
        if (space.getRank() != 2) {
            throw new IllegalArgumentException("Vector space must be have 2 dimension(s)");
        }
        number = (int)space.getNumber();
        scale = 1.0F/number;
        dim1 = space.getDimension(0);
        off1 = 0;
        end1 = dim1;
        dim2 = space.getDimension(1);
        off2 = 0;
        end2 = dim2;
        fastPull = true;
    }

    /**
     * Create a new convolution operator for 2D arrays of float's.
     *
     * @param inp - The input space.
     * @param out - The output space.
     * @param off - The position of the output relative to the result
     *              of the convolution.
     */
    public ConvolutionFloat2D(ShapedVectorSpace inp, ShapedVectorSpace out, int[] off) {
        /* Initialize super class and check rank and dimensions (element type
           is checked by the super class constructor). */
        super(inp, out);
        if (inp.getRank() != 2) {
            throw new IllegalArgumentException("Input space is not 2D");
        }
        if (out.getRank() != 2) {
            throw new IllegalArgumentException("Output space is not 2D");
        }
        number = (int)inp.getNumber();
        scale = 1.0F/number;
        dim1 = inp.getDimension(0);
        off1 = off[0];
        end1 = off1 + out.getDimension(0);
        if (off1 < 0 || off1 >= dim1) {
            throw new IllegalArgumentException("Out of range offset along 1st dimension.");
        }
        if (end1 > dim1) {
            throw new IllegalArgumentException("Data (+ offset) beyond 1st dimension.");
        }
        dim2 = inp.getDimension(1);
        off2 = off[1];
        end2 = off2 + out.getDimension(1);
        if (off2 < 0 || off2 >= dim2) {
            throw new IllegalArgumentException("Out of range offset along 2nd dimension.");
        }
        if (end2 > dim2) {
            throw new IllegalArgumentException("Data (+ offset) beyond 2nd dimension.");
        }
        fastPull = out.getShape().equals(inp.getShape());
    }


    /** Create low-level FFT operator. */
    private final void createFFT() {
        if (fft == null) {
            fft = new FloatFFT_2D(dim1, dim2);
        }
    }

    /** Apply in-place forward complex FFT. */
    @Override
    public final void forwardFFT(float z[]) {
        if (z.length != 2*number) {
            throw new IllegalArgumentException("Bad workspace size");
        }
        timerForFFT.resume();
        if (fft == null) {
            createFFT();
        }
        fft.complexForward(z);
        timerForFFT.stop();
    }

    /** Apply in-place backward complex FFT. */
    @Override
    public final void backwardFFT(float z[]) {
        if (z.length != 2*number) {
            throw new IllegalArgumentException("Bad argument size");
        }
        timerForFFT.resume();
        if (fft == null) {
            createFFT();
        }
       fft.complexInverse(z, false);
        timerForFFT.stop();
    }

    @Override
    public void pull(float x[]) {
        if (x == null || x.length != number) {
            throw new IllegalArgumentException("Bad output size");
        }
        float z[] = getWorkspace();
        int real = 0; // index of real part in model and FFT arrays
        if (fastPull) {
            /* Output and input have the same size. */
            for (int k = 0; k < number; ++k) {
                x[k] = scale*z[real];
                real += 2;
            }
        } else {
            /* Output size is smaller than input size. */
            int k = 0; // index in data and weight arrays
            for (int i2 = 0; i2 < dim2; ++i2) {
               boolean test = (off2 <= i2 && i2 < end2);
                for (int i1 = 0; i1 < dim1; ++i1) {
                    if (test && off1 <= i1 && i1 < end1) {
                        x[k] = scale*z[real];
                        ++k;
                    }
                    real += 2;
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
