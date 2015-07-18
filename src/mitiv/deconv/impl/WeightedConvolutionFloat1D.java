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

import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Implements a FFT-based weighted convolution for 1D arrays of float's.
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
public class WeightedConvolutionFloat1D
     extends WeightedConvolutionFloat
{
    /** Factor to scale the result of the backward FFT. */
    private final float scale;

    /** Number of variables. */
    private final int number;

    /** Number of element along 1st dimension of the variables. */
    private final int dim1;

    /** Offset of data along 1st dimension. */
    private final int off1;

    /** Offset of first cell after data along 1st dimension. */
    private final int end1;

    /** Convolution operator. */
    protected ConvolutionFloat1D cnvl = null;

    /**
     * Create a new FFT-based convolution operator given the PSF.
     *
     * @param FFT - The Fast Fourier Transform operator.
     * @param psf - The point spread function.
     */
    public WeightedConvolutionFloat1D(ShapedVectorSpace variableSpace,
            ShapedVectorSpace dataSpace, int[] dataOffset) {
        /* Initialize super class and check rank and dimensions. */
        super(variableSpace, dataSpace);

        Shape variableShape = variableSpace.getShape();
        Shape dataShape = dataSpace.getShape();
        number = (int)variableShape.number();
        scale = 1.0F/number;
        dim1 = variableShape.dimension(0);
        off1 = dataOffset[0];
        end1 = off1 + dataShape.dimension(0);
        if (off1 < 0 || off1 >= dim1) {
            throw new IllegalArgumentException("Out of range offset along 1st dimension.");
        }
        if (end1 > dim1) {
            throw new IllegalArgumentException("Data (+ offset) beyond 1st dimension.");
        }
        cnvl = new ConvolutionFloat1D(variableSpace);
    }

    @Override
    protected double cost(double alpha, Vector x)
    {
        /* Check whether instance has been fully initialized. */
        checkData();

        /* Compute the convolution. */
        cnvl.push(((FloatShapedVector)x).getData());
        float z[] = cnvl.convolve(false);

        /* Integrate cost. */
        double sum = 0.0;
        int j = 0; // index in data and weight arrays
        int real = 0; // index of real part in model and FFT arrays
        if (wgt == null) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                if (off1 <= i1 && i1 < end1) {
                    float r = scale*z[real] - dat[j];
                    sum += r*r;
                    ++j;
                }
                real += 2;
            }
        } else {
            float w;
            for (int i1 = 0; i1 < dim1; ++i1) {
                if (off1 <= i1 && i1 < end1) {
                    if ((w = wgt[j]) > 0.0F) {
                        float r = scale*z[real] - dat[j];
                        sum += w*r*r;
                    }
                    ++j;
                }
                real += 2;
            }
        }
        return alpha*sum;
    }

    @Override
    protected double cost(double alpha, Vector x, Vector gx, boolean clr)
    {
        /* Check whether instance has been fully initialized. */
        checkData();

        /* Compute the convolution. */
        cnvl.push(((FloatShapedVector)x).getData());
        float z[] = cnvl.convolve(false);

        /* Integrate cost and gradient. */
        final float q = 2*scale*(float)alpha;
        double sum = 0.0;
        int j = 0; // index in data and weight arrays
        int real = 0; // index of real part in model and FFT arrays
        int imag = 1; // index of imaginary parts in model and FFT arrays
        if (wgt == null) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                if (off1 <= i1 && i1 < end1) {
                    float r = scale*z[real] - dat[j];
                    sum += r*r;
                    z[real] = q*r;
                    z[imag] = 0.0F;
                    ++j;
                } else {
                    z[real] = 0.0F;
                    z[imag] = 0.0F;
                }
                real += 2;
                imag += 2;
            }
        } else {
            float w;
            for (int i1 = 0; i1 < dim1; ++i1) {
                if (off1 <= i1 && i1 < end1) {
                    if ((w = wgt[j]) > 0.0F) {
                        float r = scale*z[real] - dat[j];
                        float wr = w*r;
                        sum += wr*r;
                        z[real] = q*wr;
                    } else {
                        z[real] = 0.0F;
                    }
                    z[imag] = 0.0F;
                    ++j;
                } else {
                    z[real] = 0.0F;
                    z[imag] = 0.0F;
                }
                real += 2;
                imag += 2;
            }
        }

        /* Finalize computation of gradient. */
        float g[] = ((FloatShapedVector)gx).getData();
        cnvl.convolve(true);
        real = 0;
        if (clr) {
            for (int k = 0; k < number; ++k, real += 2) {
                g[k] = z[real];
            }
        } else {
            for (int k = 0; k < number; ++k, real += 2) {
                g[k] += z[real];
            }
        }

        /* Returns cost. */
        return alpha*sum;
    }

    @Override
    public void setPSF(ShapedArray psf, int[] off)
    {
        cnvl.setPSF(psf, off);
    }

    @Override
    public void setPSF(ShapedVector psf)
    {
        cnvl.setPSF(psf);
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
