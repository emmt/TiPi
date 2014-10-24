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

import mitiv.array.ArrayUtils;
import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.deconv.impl.ConvolutionDouble1D;
import mitiv.deconv.impl.ConvolutionDouble2D;
import mitiv.deconv.impl.ConvolutionDouble3D;
import mitiv.deconv.impl.ConvolutionFloat1D;
import mitiv.deconv.impl.ConvolutionFloat2D;
import mitiv.deconv.impl.ConvolutionFloat3D;
import mitiv.exception.IllegalTypeException;
import mitiv.linalg.shaped.ShapedLinearOperator;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;
import mitiv.utils.Timer;

/**
 * Implements a FFT-based convolution.
 *
 * <h3>Definition</h3>
 * <p>
 * The <i>weighted</i> convolution operator <b>A</b> writes:
 * </p><p align="center">
 * <b>A</b> = <b>R</b>.<b>F</b><sup>*</sup>.diag(<b>F</b>.<b><i>h</i></b>).<b>F</b>
 * </p>
 * with <b>F</b> the FFT (Fast Fourier Transform) operator, <b><i>h</i></b> the
 * PSF (Point Spread Function) and <b>R</b> a linear operator which selects a
 * a sub-region of the output of the convolution and weights it.  The * superscript
 * denotes the adjoint of the operator (complex transpose in this specific case) and
 * diag(<i><b>v</i></b>) is a diagonal operator whose diagonal elements are those of
 * the vector <i><b>v</i></b>.
 * </p>
 * 
 * <h3>Usage in deconvolution problems</h3>
 * <p>
 * Introducing the simple cyclic convolution operator:
 * </p><p align="center">
 * <b>H</b> = <b>F</b><sup>*</sup>.diag(<b>F</b>.<b><i>h</i></b>).<b>F</b>,
 * </p>
 * the model of the data <b><i>y</i></b> writes:
 * </p><p align="center">
 * <b><i>y</i></b> = <b>M</b>.<b><i>x</i></b> + <i>noise</i>
 * = <b>S</b>.<b>H</b>.<b><i>x</i></b> + <i>noise</i>,
 * </p><p>
 * with <b>M</b> = <b>S</b>.<b>H</b> the linear model operator and <b>S</b> a
 * <i>selection</i> operator which extracts form the output of the cyclic convolution
 * the region corresponding to the measurements.  This selection is typically needed
 * when, to avoid border artifacts, the reconstructed object is larger than the size
 * of the observed region.
 * </p><p>
 * Now assuming non-stationary Gaussian noise, the likelihood function writes:
 * </p><p align="center">
 * f(<b><i>x</i></b>) = (<b>M</b>.<b><i>x</i></b> - <b><i>y</i></b>)<sup>*</sup>.<b>W</b>.(<b>M</b>.<b><i>x</i></b> - <b><i>y</i></b>),
 * </p><p>
 * with <b>W</b> the precision matrix of the noise (also known as the statistical
 * weights of the data).  Assuming uncorrelated noise, then
 * <b>W</b>&nbsp;=&nbsp;diag(<b><i>w</i></b>) and the likelihood penalty can be out
 * in the form:
 * </p><p align="center">
 * f(<b><i>x</i></b>) = &#8214;<b>R</b>.<b><i>x</i></b> - <b><i>z</i></b>&#8214;<sup>2</sup>,
 * </p><p>
 * with:
 * </p><p align="center">
 * <b>R</b> = diag(<b><i>u</i></b>).<b>S</b>,<br>
 * <b><i>z</i></b> = diag(<b><i>u</i></b>).<b><i>y</i></b>,
 * </p><p>
 * where the elements of <b><i>u</i></b> are the square roots of the
 * statistical weights (for all indices <i>n</i>):
 * </p><p align="center">
 * <i>u</i>[<i>n</i>] =
 * <span style="white-space: nowrap; font-size:larger">&radic;</span><span style="text-decoration:overline;">&nbsp;<i>w</i>[<i>n</i>]&nbsp;</span>
,
 * </p><p>
 * <i>i.e.</i>, such that diag(<b><i>w</i></b>)&nbsp;=&nbsp;diag(<b><i>u</i></b>)<sup>2</sup>.
 * </p>
 * 
 * <h3>Summary</h3>
 * <p>
 * The weighted convolution operator <b>A</b> is defined by the PSF
 * <b><i>h</i></b>, the square roots of the statistical weights
 * <b><i>u</i></b> and the selection operator <b>S</b>.  In our
 * implementation, this latter is defined by the dimensions of the output
 * (data) space and the position of the first element to select in the result
 * of the cyclic convolution <b>H</b> (thus we only consider
 * <i>rectangular</i> output spaces).  A typical usage of the code is:
 * <pre>
 * // Create operator:
 * WeightedConvolutionOperator A = WeightedConvolutionOperator.build(inputSpace, outputSpace, first);
 * 
 * // Specify the PSF (mandatory):
 * A.setPSF(h);
 * 
 * // Optionally specify the weights (actually the square roots of the statistical weights):
 * A.setWeights(u);
 *
 * // Apply the operator (compute A.x):
 * A.apply(x, result);</pre>
 * where the PSF <b><i>h</i></b> is a shaped vector of the input space of the
 * operator <b>A</b> or a shaped array (in the former case, the PSF must be
 * appropriately centered, in the sense of the FFT; in the latter case, the
 * shaped array is zero-padded and rolled for you), the square roots of the
 * statistical weights <b><i>u</i></b> is a shaped vector of the output space
 * of the operator <b>A</b> or a shaped array (with same shape as the vectors
 * of the output space of the operator <b>A</b>, type conversion is
 * automatically done for you).  If <b><i>u</i></b> is not specified, the
 * behavior is as if all elements of <b><i>u</i></b> have been set to 1, thus
 * as if
 * diag(<b><i>w</i></b>)&nbsp;=&nbsp;diag(<b><i>u</i></b>)&nbsp;=&nbsp;<b>I</b>
 * the identity operator.
 * </p><p>
 * If <b><i>u</i></b> is unspecified (or if all elements of <b><i>u</i></b>
 * are equal to 1) and if the output and input vector spaces are the same,
 * then operator <b>A</b> is just the cyclic convolution operator <b>H</b>.
 * </p>
 * @author Éric Thiébaut
 */
public abstract class WeightedConvolutionOperator extends ShapedLinearOperator {

    /**
     * The following constructors make this class non instantiable, but still
     * let others inherit from this class.  You must use the {@link #build()}
     * factory to build a convolution operator.
     */
    protected WeightedConvolutionOperator(ShapedVectorSpace inputSpace,
            ShapedVectorSpace outputSpace) {
        super(inputSpace, outputSpace);
    }

    public static WeightedConvolutionOperator build(ShapedVectorSpace space) {
        return build(space, space, null);
    }

    /**
     * Build a weighted convolution operator with centered output.
     * <p>
     * The returned object is not a valid operator until you set the
     * point spread function (PSF) with one of the {@link #setPSF()} methods and,
     * optionally, the weights with one of the {@link #setWeights()} methods.
     * </p><p>
     * The input space of the operator usually corresponds to the <i>object</i> (or
     * parameters) space while the output space usually corresponds to the <i>data</i>.
     * </p><p>
     * The size of the output space must be smaller or equal that of the input
     * space.  The rank of the output and input space must be the same and
     * comparing the "<i>sizes</i>" involves a comparison between every dimension.
     * If the shape of the output (data) space is smaller than that of the input
     * (object) space, then the output is the central part of the result of the
     * simple convolution.
     * The factory {@link #build(ShapedVectorSpace, ShapedVectorSpace, int[])}
     * can be used to select a different region for the output.
     * </p><p>
     * See {@link WeightedConvolutionOperator}
     * for a description of what exactly does this operator.
     * </p>
     * @param inputSpace  - The input space of the operator.
     * @param outputSpace - The output space of the operator.
     * @return A weighted convolution operator.
     * @see {@link #build(ShapedVectorSpace, ShapedVectorSpace, int[])}
     */
    public static WeightedConvolutionOperator build(ShapedVectorSpace inputSpace,
            ShapedVectorSpace outputSpace) {
        return build(inputSpace, outputSpace, null);
    }

    /**
     * Build a weighted convolution operator.
     * <p>
     * This version of the factory to build a weighted convolution operator let you
     * specify precisely the position of the region to select as the output of the
     * operator (<i>e.g.</i> corresponding to the model of data).  The coordinates
     * of the <i>first</i> element of the output with respect to the result of the
     * simple convolution must be such that:
     * <pre>0 <= first[k] <= inpDim[k] - outDim[k]</pre>where {@code inpDim}
     * and {@code outDim} are the respective dimensions of the input and output spaces.
     * If this does not hold (for all <i>k</i>), an {@link ArrayIndexOutOfBoundsException}
     * is thrown.
     * </p><p>
     * See {@link #build(ShapedVectorSpace, ShapedVectorSpace)} for more details on
     * the meaning of the input and output spaces and {@link WeightedConvolutionOperator}
     * for a description of what exactly does this operator.
     * </p>
     * @param inputSpace  - The input space of the operator.
     * @param outputSpace - The output space of the operator.
     * @param first       - The coordinates of the first element of the output
     *                      in the result of the simple convolution.  It must
     *                      have as many values as the rank of the input and output
     *                      spaces of the operator.
     * @return A weighted convolution operator.
     * @see {@link #build(ShapedVectorSpace, ShapedVectorSpace)}
     */
    public static WeightedConvolutionOperator build(ShapedVectorSpace inputSpace,
            ShapedVectorSpace outputSpace, int[] first) {
        int type = inputSpace.getType();
        if (outputSpace.getType() != type) {
            throw new IllegalTypeException("Input and output spaces must have same element type.");
        }
        int rank = inputSpace.getRank();
        if (outputSpace.getShape().rank() != rank) {
            throw new IllegalTypeException("Input and output spaces must have same rank.");
        }
        switch (type) {
        case Traits.FLOAT:
            switch (rank) {
            case 1:
                return new ConvolutionFloat1D(inputSpace, outputSpace, first);
            case 2:
                return new ConvolutionFloat2D(inputSpace, outputSpace, first);
            case 3:
                return new ConvolutionFloat3D(inputSpace, outputSpace, first);
            }
            break;
        case Traits.DOUBLE:
            switch (rank) {
            case 1:
                return new ConvolutionDouble1D(inputSpace, outputSpace, first);
            case 2:
                return new ConvolutionDouble2D(inputSpace, outputSpace, first);
            case 3:
                return new ConvolutionDouble3D(inputSpace, outputSpace, first);
            }
            break;
        default:
            throw new IllegalTypeException("Only float and double types are implemented.");
        }
        throw new IllegalArgumentException("Only 1D, 2D and 3D convolution are implemented.");
    }

    /**
     * Set the PSF of the operator.
     * @param vec - The PSF must belongs to the input space of the operator
     *              and must be centered in the sense of of the FFT.
     */
    public abstract void setPSF(ShapedVector vec);

    /**
     * Set the PSF of the operator.
     * @param arr - The PSF in the form of a shaped array.  It is
     *              automatically converted to the correct data type,
     *              zero-padded and rolled.  It is assumed to be
     *              geometrically centered.
     */
    public abstract void setPSF(ShapedArray arr);

    /**
     * Set the PSF of the operator with given center coordinates.
     * @param arr - The PSF in the form of a shaped array.  It is
     *              automatically converted to the correct data type,
     *              zero-padded and rolled.
     * @param cen - The coordinates of the central element of the PSF.
     *              There must be as many coordinates as the rank of
     *              the PSF, each coordinate is the 0-based offset of
     *              the center along the corresponding dimension.
     */
    public abstract void setPSF(ShapedArray arr, int[] cen);

    /**
     * Set the weights of the operator.
     * @param vec  - The weights in the form of a shaped vector.  Its values are
     *               checked for validity.  It must belong to the output space
     *               of the operator.
     * @param copy - If true, a copy (not a reference) of the contents of <i>vec</i>
     *               is forced.
     */
    public abstract void setWeights(ShapedVector vec, boolean copy);

    /**
     * Set the weights of the operator.
     * <p>
     * This method is the same as {@link #setWeights(ShapedVector, boolean)} except
     * that a copy is never forced.
     * </p>
     * @param vec  - The weights.
     */
    public void setWeights(ShapedVector vec) {
        setWeights(vec, false);
    }

    /**
     * Set the weights of the operator.
     * @param arr  - The weights in the form of a shaped array.  It is automatically
     *               converted to the correct data type and its values are checked for
     *               validity.  Its shape must be that of the vectors of the output
     *               space of the operator.
     * @param copy - If true, a copy (not a reference) of the contents of <i>arr</i>
     *               is forced.
     */
    public abstract void setWeights(ShapedArray arr, boolean copy);

    /**
     * Set the weights of the operator.
     * <p>
     * This method is the same as {@link #setWeights(ShapedArray, boolean)} except
     * that a copy is never forced.
     * </p>
     * @param vec  - The weights.
     */
    public void setWeights(ShapedArray arr) {
        setWeights(arr, false);
    }

    /**
     * Throw invalid weight exception.
     * @throws IllegalArgumentException weight has an invalid value.
     */
    protected static void badWeights() {
        throw new IllegalArgumentException("Weights must be finite and non-negative.");
    }

    /**
     * Check weight value.
     * @return Its argument.
     * @throws IllegalArgumentException weight has an invalid value.
     */
    protected final static float checkWeight(float wgt) {
        if (Float.isNaN(wgt) || Float.isInfinite(wgt) || wgt < 0.0F) {
            badWeights();
        }
        return wgt;
    }

    /**
     * Check weights and, optionally, copy them.
     * @param wgt - The array of weights to check.
     * @param copy - If true, the result is a copy (not a reference) of the <i>wgt</i> argument.
     * @return An array of checked weights.
     */
    protected static float[] checkWeights(float[] wgt, boolean copy) {
        int n = wgt.length;
        if (copy) {
            float[] result = new float[n];
            for (int j = 0; j < n; ++j) {
                result[j] = checkWeight(wgt[j]);
            }
            return result;
        } else {
            for (int j = 0; j < n; ++j) {
                checkWeight(wgt[j]);
            }
            return wgt;
        }
    }

    /**
     * Check weight value.
     * @return Its argument.
     * @throws IllegalArgumentException weight has an invalid value.
     */
    protected final static double checkWeight(double wgt) {
        if (Double.isNaN(wgt) || Double.isInfinite(wgt) || wgt < 0.0) {
            badWeights();
        }
        return wgt;
    }

    /**
     * Check weights and, optionally, copy them.
     * @param wgt - The array of weights to check.
     * @param copy - If true, the result is a copy (not a reference) of the <i>wgt</i> argument.
     * @return An array of checked weights.
     */
    protected static double[] checkWeights(double[] wgt, boolean copy) {
        int n = wgt.length;
        if (copy) {
            double[] result = new double[n];
            for (int j = 0; j < n; ++j) {
                result[j] = checkWeight(wgt[j]);
            }
            return result;
        } else {
            for (int j = 0; j < n; ++j) {
                checkWeight(wgt[j]);
            }
            return wgt;
        }
    }

    /**
     * Check rank and dimensions.
     * @return The offset of the output in the complex workspace.
     */
    protected static int outputOffset(int rank, Shape inputShape, Shape outputShape, int[] first) {
        if (inputShape.rank() != rank) {
            throw new IllegalArgumentException("Bad rank for input space.");
        }
        if (outputShape.rank() != rank) {
            throw new IllegalArgumentException("Bad rank for output space.");
        }
        if (first != null && first.length != rank) {
            throw new IllegalArgumentException("Bad number of coordinates for the first position");
        }
        int offset = 0;
        int stride = 2; // stride1
        for (int k = 0; k < rank; ++k) {
            int inpDim = inputShape.dimension(k);
            int outDim = outputShape.dimension(k);
            if (outDim > inpDim) {
                throw new IllegalArgumentException("Output dimensions must be at most as large as input dimensions");
            }
            int index;
            if (first == null) {
                index = (inpDim/2) - (outDim/2);
            } else {
                index = first[k];
                if (index < 0 || index + outDim > inpDim) {
                    throw new IllegalArgumentException("Output region is outside bounds");
                }

            }
            offset += stride*index;

        }

        return offset;
    }

    /**
     * Helper function to zero-pad and roll the PSF.
     * @param arr - The PSF as a shaped array.
     * @param cen - The coordinates of the central element of the PSF.
     *              There must be as many coordinates as the rank of
     *              the PSF, each coordinate is the 0-based offset of
     *              the center along the corresponding dimension.
     * @return The offsets for the {@link ArrayUtils#roll} method.
     */
    protected ShapedArray adjustPSF(ShapedArray arr, int[] cen) {
        Shape dstShape = getInputSpace().getShape();
        Shape srcShape = arr.getShape();
        int rank = dstShape.rank();
        if (srcShape.rank() != rank) {
            throw new IllegalArgumentException("PSF rank not conformable.");
        }
        if (cen.length != rank) {
            throw new IllegalArgumentException("Number of coordinates not conformable.");
        }
        int [] off = new int[rank];
        for (int k = 0; k < rank; ++k) {
            int srcDim = srcShape.dimension(k);
            int dstDim = dstShape.dimension(k);
            if (srcDim > dstDim) {
                throw new IllegalArgumentException("PSF dimension(s) too large.");
            }
            int margin = (dstDim/2) - (srcDim/2); // margin for zero-padding
            off[k] = - (margin + cen[k]);
        }
        return ArrayUtils.roll(ArrayUtils.zeroPadding(arr, dstShape), off);
    }


    /*======================================================================*/
    /* TIMERS */

    protected Timer timerForFFT = new Timer();
    protected Timer timer = new Timer();
    public void resetTimers() {
        timerForFFT.stop();
        timerForFFT.reset();
        timer.stop();
        timer.reset();
    }
    public double getElapsedTime() {
        return timer.getElapsedTime();
    }
    public double getElapsedTimeInFFT() {
        return timerForFFT.getElapsedTime();
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
