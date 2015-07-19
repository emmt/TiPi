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
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Implements a FFT-based weighted convolution for 2D arrays of double's.
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
public class WeightedConvolutionDouble2D
     extends WeightedConvolutionDouble
{
    /** Factor to scale the result of the backward FFT. */
    private final double scale;

    /** Number of variables. */
    private final int number;

    /** Number of element along 1st dimension of the variables. */
    private final int dim1;
    /** Number of element along 2nd dimension of the variables. */
    private final int dim2;

    /** Offset of data along 1st dimension. */
    private final int off1;
    /** Offset of data along 2nd dimension. */
    private final int off2;

    /** Offset of first cell after data along 1st dimension. */
    private final int end1;
    /** Offset of first cell after data along 2nd dimension. */
    private final int end2;

    /** Convolution operator. */
    private final ConvolutionDouble2D cnvl;

    /**
     * Create a new FFT-based weighted convolution cost function.
     *
     * @param objectSpace - The object space.
     * @param dataSpace   - The data space.
     * @param dataOffset  - The position of the data space relative
     *                      to the object space.
     */
    public WeightedConvolutionDouble2D(ShapedVectorSpace objectSpace,
            ShapedVectorSpace dataSpace, int[] dataOffset) {
        /* Initialize super class and check rank and dimensions (element type
           is checked by the super class constructor). */
        super(objectSpace, dataSpace);
        if (objectSpace.getRank() != 2) {
            throw new IllegalArgumentException("Object space is not 2D");
        }
        if (dataSpace.getRank() != 2) {
            throw new IllegalArgumentException("Data space is not 2D");
        }
        number = (int)objectSpace.getNumber();
        scale = 1.0/number;
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
        cnvl = new ConvolutionDouble2D(objectSpace);
    }

    @Override
    protected double cost(double alpha, Vector x)
    {
        /* Check whether instance has been fully initialized. */
        checkData();

        /* Compute the convolution. */
        cnvl.push(((DoubleShapedVector)x).getData());
        cnvl.convolve(false);

        /* Integrate cost. */
        double sum = 0.0;
        double z[] = cnvl.getWorkspace();
        int j = 0; // index in data and weight arrays
        int real = 0; // index of real part in model and FFT arrays
        if (wgt == null) {
            for (int i2 = 0; i2 < dim2; ++i2) {
               boolean test = (off2 <= i2 && i2 < end2);
                for (int i1 = 0; i1 < dim1; ++i1) {
                    if (test && off1 <= i1 && i1 < end1) {
                        double r = scale*z[real] - dat[j];
                        sum += r*r;
                        ++j;
                    }
                    real += 2;
                }
            }
        } else {
            double w;
            for (int i2 = 0; i2 < dim2; ++i2) {
               boolean test = (off2 <= i2 && i2 < end2);
                for (int i1 = 0; i1 < dim1; ++i1) {
                    if (test && off1 <= i1 && i1 < end1) {
                        if ((w = wgt[j]) > 0.0) {
                            double r = scale*z[real] - dat[j];
                            sum += w*r*r;
                        }
                        ++j;
                    }
                    real += 2;
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
        cnvl.push(((DoubleShapedVector)x).getData());
        cnvl.convolve(false);

        /* Integrate cost and gradient. */
        final double q = 2*scale*alpha;
        double sum = 0.0;
        double z[] = cnvl.getWorkspace();
        int j = 0; // index in data and weight arrays
        int real = 0; // index of real part in model and FFT arrays
        int imag = 1; // index of imaginary parts in model and FFT arrays
        if (wgt == null) {
            for (int i2 = 0; i2 < dim2; ++i2) {
               boolean test = (off2 <= i2 && i2 < end2);
                for (int i1 = 0; i1 < dim1; ++i1) {
                    if (test && off1 <= i1 && i1 < end1) {
                        double r = scale*z[real] - dat[j];
                        sum += r*r;
                        z[real] = q*r;
                        z[imag] = 0.0;
                        ++j;
                    } else {
                        z[real] = 0.0;
                        z[imag] = 0.0;
                    }
                    real += 2;
                    imag += 2;
                }
            }
        } else {
            double w;
            for (int i2 = 0; i2 < dim2; ++i2) {
               boolean test = (off2 <= i2 && i2 < end2);
                for (int i1 = 0; i1 < dim1; ++i1) {
                    if (test && off1 <= i1 && i1 < end1) {
                        if ((w = wgt[j]) > 0.0) {
                            double r = scale*z[real] - dat[j];
                            double wr = w*r;
                            sum += wr*r;
                            z[real] = q*wr;
                        } else {
                            z[real] = 0.0;
                        }
                        z[imag] = 0.0;
                        ++j;
                    } else {
                        z[real] = 0.0;
                        z[imag] = 0.0;
                    }
                    real += 2;
                    imag += 2;
                }
            }
        }

        /* Finalize computation of gradient. */
        double g[] = ((DoubleShapedVector)gx).getData();
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
