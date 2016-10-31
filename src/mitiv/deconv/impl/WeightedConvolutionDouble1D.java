// *WARNING* This file has been automatically generated by TPP do not edit directly.
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
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;

/**
 * Implements a FFT-based weighted convolution for 1D arrays of double's.
 *
 * <p> It is recommended not to directly instantiate this class but rather use
 * one of the factory methods of the parent class
 * {@link mitiv.deconv.WeightedConvolutionCost#build}.  Have a look at the
 * documentation of {@link mitiv.deconv.WeightedConvolutionCost} for a
 * description of what exactly does this kind of operator.  </p>
 *
 * @author Éric Thiébaut
 *
 * @see mitiv.deconv.WeightedConvolutionCost
 */
public class WeightedConvolutionDouble1D
     extends WeightedConvolutionDouble
{
    /** Number of element along 1st dimension of the variables. */
    private final int dim1;

    /** Offset of data along 1st dimension. */
    private final int off1;

    /** End of data along 1st dimension. */
    private final int end1;

    /** Convolution operator. */
    private final ConvolutionDouble1D cnvl;

    /**
     * Create a new FFT-based weighted convolution cost function.
     *
     * @param objectSpace
     *        The object space which also gives the size of the work space.
     *
     * @param dataSpace
     *        The data space.
     *
     * @param dataOffsets
     *        The position of the data space relative to the object space.
     */
    public WeightedConvolutionDouble1D(DoubleShapedVectorSpace objectSpace,
                        DoubleShapedVectorSpace dataSpace, int[] dataOffsets) {
        /* Initialize super class and check rank and dimensions (element type
           is checked by the super class constructor). */
        super(objectSpace, dataSpace);
        if (objectSpace.getRank() != 1) {
            throw new IllegalArgumentException("Object space is not 1D");
        }
        if (dataSpace.getRank() != 1) {
            throw new IllegalArgumentException("Data space is not 1D");
        }

        /* Create the convolution (which checks arguments). */
        cnvl = new ConvolutionDouble1D(objectSpace.getShape(),
                                              objectSpace, null,
                                              dataSpace, dataOffsets);

        /* Store dimensions, offsets, etc. */
        dim1 = objectSpace.getDimension(0);
        off1 = dataOffsets[0];
        end1 = off1 + dataSpace.getDimension(0);
    }


    @Override
    protected double _cost(double alpha, Vector x) {
        /* Check whether instance has been fully initialized. */
        checkSetup();

        /* Compute the convolution. */
        cnvl.push((ShapedVector)x, false);
        cnvl.convolve(false);

        /* Integrate cost. */
        double sum = 0.0;
        double z[] = cnvl.getWorkArray();
        int j = 0; // index in data and weight arrays
        int k = 2*off1; // index in work array z
        if (wgt == null) {
            for (int i1 = off1; i1 < end1; ++i1) {
                double r = z[k] - dat[j];
                sum += r*r;
                j += 1;
                k += 2;
            }
        } else {
            for (int i1 = off1; i1 < end1; ++i1) {
                double w = wgt[j];
                double r = z[k] - dat[j];
                sum += w*r*r;
                j += 1;
                k += 2;
            }
        }
        return alpha*sum/2;
    }

    @Override
    protected double _cost(double alpha, Vector x, Vector gx, boolean clr) {
        /* Check whether instance has been fully initialized. */
        checkSetup();

        /* Compute the convolution. */
        cnvl.push((ShapedVector)x, false);
        cnvl.convolve(false);

        /* Integrate cost and gradient. */
        final boolean weighted = (wgt != null);
        final double zero = 0.0;
        final double q = alpha;
        double sum = 0.0;
        double z[] = cnvl.getWorkArray();
        int j = 0; // index in data and weight arrays
        int k = 0; // index in work array z
        for (int i1 = 0; i1 < off1; ++i1) {
            z[k] = zero;
            z[k+1] = zero;
            k += 2;
        }
        if (weighted) {
            for (int i1 = off1; i1 < end1; ++i1) {
                double w = wgt[j];
                double r = z[k] - dat[j];
                double wr = w*r;
                sum += r*wr;
                z[k] = q*wr;
                z[k+1] = zero;
                j += 1;
                k += 2;
            }
        } else {
            for (int i1 = off1; i1 < end1; ++i1) {
                double r = z[k] - dat[j];
                sum += r*r;
                z[k] = q*r;
                z[k+1] = zero;
                j += 1;
                k += 2;
            }
        }
        for (int i1 = end1; i1 < dim1; ++i1) {
            z[k] = zero;
            z[k+1] = zero;
            k += 2;
        }

        /* Finalize computation of gradient. */
        double g[] = ((DoubleShapedVector)gx).getData();
        cnvl.convolve(true);
        if (clr) {
            for (j = 0, k = 0; j < g.length; ++j, k += 2) {
                g[j] = z[k];
            }
        } else {
            for (j = 0, k = 0; j < g.length; ++j, k += 2) {
                g[j] += z[k];
            }
        }

        /* Returns cost. */
        return alpha*sum/2;
    }

    @Override
        public void setPSF(ShapedArray psf, int[] off, boolean normalize) {
        cnvl.setPSF(psf, off, normalize);
    }

    @Override
    public void setPSF(ShapedVector psf) {
        cnvl.setPSF(psf);
    }
}
