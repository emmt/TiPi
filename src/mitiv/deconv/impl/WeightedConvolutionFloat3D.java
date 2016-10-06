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
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Implements a FFT-based weighted convolution for 3D arrays of float's.
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
public class WeightedConvolutionFloat3D
     extends WeightedConvolutionFloat
{
    /** Factor to scale the result of the backward FFT. */
    private final float scale;

    /** Number of variables. */
    private final int number;

    /** Number of element along 1st dimension of the variables. */
    private final int dim1;

    /** Number of element along 2nd dimension of the variables. */
    private final int dim2;

    /** Number of element along 3rd dimension of the variables. */
    private final int dim3;

    /** Offset of data along 1st dimension. */
    private final int off1;

    /** Offset of data along 2nd dimension. */
    private final int off2;

    /** Offset of data along 3rd dimension. */
    private final int off3;

    /** End of data along 1st dimension. */
    private final int end1;

    /** End of data along 2nd dimension. */
    private final int end2;

    /** End of data along 3rd dimension. */
    private final int end3;

    /** Convolution operator. */
    private final ConvolutionFloat3D cnvl;

    /**
     * Create a new FFT-based weighted convolution cost function.
     *
     * @param objectSpace - The object space.
     * @param dataSpace   - The data space.
     * @param dataOffset  - The position of the data space relative
     *                      to the object space.
     */
    public WeightedConvolutionFloat3D(ShapedVectorSpace objectSpace,
            ShapedVectorSpace dataSpace, int[] dataOffset) {
        /* Initialize super class and check rank and dimensions (element type
           is checked by the super class constructor). */
        super(objectSpace, dataSpace);
        if (objectSpace.getRank() != 3) {
            throw new IllegalArgumentException("Object space is not 3D");
        }
        if (dataSpace.getRank() != 3) {
            throw new IllegalArgumentException("Data space is not 3D");
        }
        number = (int)objectSpace.getNumber();
        scale = 1.0F/number;
        dim1 = objectSpace.getDimension(0);
        off1 = dataOffset[0];
        end1 = off1 + dataSpace.getDimension(0);
        if (off1 < 0 || off1 >= dim1) {
            throw new IllegalArgumentException("Out of range offset along 1st dimension.");
        }
        if (end1 > dim1) {
            throw new IllegalArgumentException("Data (+ offset) beyond 1st dimension.");
        }
        dim2 = objectSpace.getDimension(1);
        off2 = dataOffset[1];
        end2 = off2 + dataSpace.getDimension(1);
        if (off2 < 0 || off2 >= dim2) {
            throw new IllegalArgumentException("Out of range offset along 2nd dimension.");
        }
        if (end2 > dim2) {
            throw new IllegalArgumentException("Data (+ offset) beyond 2nd dimension.");
        }
        dim3 = objectSpace.getDimension(2);
        off3 = dataOffset[2];
        end3 = off3 + dataSpace.getDimension(2);
        if (off3 < 0 || off3 >= dim3) {
            throw new IllegalArgumentException("Out of range offset along 3rd dimension.");
        }
        if (end3 > dim3) {
            throw new IllegalArgumentException("Data (+ offset) beyond 3rd dimension.");
        }
        cnvl = new ConvolutionFloat3D(objectSpace);
    }

    @Override
    protected double cost(double alpha, Vector x)
    {
        /* Check whether instance has been fully initialized. */
        checkData();

        /* Compute the convolution. */
        cnvl.push(((FloatShapedVector)x).getData(), false);
        cnvl.convolve(false);

        /* Integrate cost. */
        double sum = 0.0;
        float z[] = cnvl.getWorkspace();
        int j = 0; // index in data and weight arrays
        int real = 0; // index of real part in model and FFT arrays
        if (wgt == null) {
            for (int i3 = 0; i3 < dim3; ++i3) {
                boolean test = (off3 <= i3 && i3 < end3);
                for (int i2 = 0; i2 < dim2; ++i2) {
                    test = (test && off2 <= i2 && i2 < end2);
                    for (int i1 = 0; i1 < dim1; ++i1) {
                        if (test && off1 <= i1 && i1 < end1) {
                            float r = scale*z[real] - dat[j];
                            sum += r*r;
                            ++j;
                        }
                        real += 2;
                    }
                }
            }
        } else {
            float w;
            for (int i3 = 0; i3 < dim3; ++i3) {
                boolean test = (off3 <= i3 && i3 < end3);
                for (int i2 = 0; i2 < dim2; ++i2) {
                    test = (test && off2 <= i2 && i2 < end2);
                    for (int i1 = 0; i1 < dim1; ++i1) {
                        if (test && off1 <= i1 && i1 < end1) {
                            if ((w = wgt[j]) > 0.0F) {
                                float r = scale*z[real] - dat[j];
                                sum += w*r*r;
                            }
                            ++j;
                        }
                        real += 2;
                    }
                }
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
        cnvl.push(((FloatShapedVector)x).getData(), false);
        cnvl.convolve(false);

        /* Integrate cost and gradient. */
        final float q = 2*scale*(float)alpha;
        double sum = 0.0;
        float z[] = cnvl.getWorkspace();
        int j = 0; // index in data and weight arrays
        int real = 0; // index of real part in model and FFT arrays
        int imag = 1; // index of imaginary parts in model and FFT arrays
        if (wgt == null) {
            for (int i3 = 0; i3 < dim3; ++i3) {
                boolean test = (off3 <= i3 && i3 < end3);
                for (int i2 = 0; i2 < dim2; ++i2) {
                    test = (test && off2 <= i2 && i2 < end2);
                    for (int i1 = 0; i1 < dim1; ++i1) {
                        if (test && off1 <= i1 && i1 < end1) {
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
                }
            }
        } else {
            float w;
            for (int i3 = 0; i3 < dim3; ++i3) {
                boolean test = (off3 <= i3 && i3 < end3);
                for (int i2 = 0; i2 < dim2; ++i2) {
                    test = (test && off2 <= i2 && i2 < end2);
                    for (int i1 = 0; i1 < dim1; ++i1) {
                        if (test && off1 <= i1 && i1 < end1) {
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
