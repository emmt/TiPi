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

import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.cost.DifferentiableCostFunction;
import mitiv.cost.QuadraticCost;
import mitiv.deconv.impl.WeightedConvolutionDouble1D;
import mitiv.deconv.impl.WeightedConvolutionDouble2D;
import mitiv.deconv.impl.WeightedConvolutionDouble3D;
import mitiv.deconv.impl.WeightedConvolutionFloat1D;
import mitiv.deconv.impl.WeightedConvolutionFloat2D;
import mitiv.deconv.impl.WeightedConvolutionFloat3D;
import mitiv.exception.IllegalTypeException;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;
import mitiv.utils.Timer;

/**
 * Implements a quadratic cost function for models by FFT-based convolution.
 *
 * <h3>Definition</h3>
 * <p>
 * The ``<i>weighted convolution</i>'' cost writes:
 * </p><p align="center">
 * f(<b><i>x</i></b>) = (<b>H</b>.<b><i>x</i></b> - <b><i>y</i></b>)<sup>t</sup>.<b>W</b>.(<b>H</b>.<b><i>x</i></b> - <b><i>y</i></b>),
 * </p><p>
 * with <b>H</b> a convolution operator and <b>W</b> a diagonal positive
 * semi-definite weighting operator.  The convolution operator writes:
 * </p><p align="center">
 * <b>H</b> = <b>R</b>.<b>F</b><sup>*</sup>.diag(<b>F</b>.<b><i>h</i></b>).<b>F</b>
 * </p><p>
 * with <b>F</b> the FFT (Fast Fourier Transform) operator, <b><i>h</i></b> the
 * point spread function (PSF) and <b>R</b> a linear operator which selects a
 * sub-region of the output of the convolution and weights it.  The * superscript
 * denotes the adjoint of the operator (complex transpose in this specific case) and
 * diag(<i><b>v</i></b>) is a diagonal operator whose diagonal elements are those of
 * the vector <i><b>v</i></b>.
 * </p><p>
 * Currently only diagonal weighting operators are implemented, thus:
 * </p><p align="center">
 * <b>W</b>&nbsp;=&nbsp;diag(<b><i>w</i></b>) .
 * </p><p>
 * If this restriction is inappropriate for your purpose, you may build your own
 * {@link QuadraticCost} function.
 * </p>
 *
 * <h3>Usage in deconvolution problems</h3>
 * <p>
 * Assuming uncorrelated Gaussian noise and that the cyclic convolution (with
 * sufficient zero padding) is suitable to approximate the effects of the
 * instrument, the cost function f(<b><i>x</i></b>) can be used to implements
 * the likelihood of the data <b><i>y</i></b> given the parameters <b><i>y</i></b>
 * and with <b>W</b>&nbsp;=&nbsp;diag(<b><i>w</i></b>) the precision matrix
 * of the noise (also known as the statistical weights of the data).
 * </p>
 *
 * <h3>Synopsis</h3>
 * <p>
 * Because of the many different possibilities (1D, 2D or 3D arrays with float
 * or double elements), creating a new instance of the cost function involves
 * a factory. A typical usage of the code is:
 * <pre>
 * // Create operator:
 * WeightedConvolutionCost cost = WeightedConvolutionCost.build(variableSpace, dataSpace);
 *
 * // Specify the PSF (mandatory):
 * cost.setPSF(h);
 *
 * // Specify the weights and the data (y mandatory, w can be null):
 * cost.setWeightsAndData(w, y);</pre>
 * where the arguments are:
 * <ul>
 *     <li>The vector space <b>variableSpace</b> of the variables
 *         <b><i>x</i></b>.</li>
 *     <li>The vector space <b>dataSpace</b> of the data
 *         <b><i>y</i></b>. The data space may be smaller than the
 *         variables space which involves zero padding (symbolically
 *         implemented by the operator <b>R</b>).  In our implementation,
 *         <b>R</b> is defined by the dimensions of the data space and the
 *         position of the first element to select in the result of the
 *         cyclic convolution <b>H</b> (thus we only consider
 *         <i>rectangular</i> output spaces).  By default, the data space
 *         (if smaller than the result of the convolution) is taken to be
 *         approximately the central part of the output of <b>H</b>.  It
 *         is possible to specify a different position by calling the build
 *         method with a list of offsets as additional argument.</li>
 *     <li>The PSF <b><i>h</i></b> is a shaped vector of the variables space
 *         or a shaped array (in the former case, the PSF must be appropriately
 *         centered, in the sense of the FFT; in the latter case, the shaped
 *         array is zero-padded and rolled for you; if the center of the PSF is
 *         not at the geometric center of the PSF array, you may specify its
 *         position).</li>
 *     <li>The data <b><i>y</i></b> is a shaped vector of the data space or a
 *         shaped array with the same dimensions as the those of the data
 *         space.</li>
 *     <li>The weights <b><i>w</i></b> can be unspecified (with
 *         <b><i>w</i></b>&nbsp;=&nbsp;<tt>null</tt>) which is the same as
 *         having all weights equal to 1; otherwise <b><i>w</i></b> must be
 *         the same kind of object as <b><i>y</i></b> (shaped array or shaped
 *         vector with the same dimensions) and all weights must be nonnegative.
 *         If weights are specified, the data are never used where the weights
 *         are equal to zero which is a consistent way to indicate missing
 *         data.</li>
 * </ul>
 * </p>
 *
 * @author Éric Thiébaut
 */
public abstract class WeightedConvolutionCost
implements DifferentiableCostFunction
{
    protected ShapedVectorSpace variableSpace;
    protected ShapedVectorSpace dataSpace;

    /**
     * The following constructors make this class non instantiable, but still
     * let others inherit from this class.  You must use the {@link #build()}
     * factory to build a convolution cost function.
     */
    protected WeightedConvolutionCost(ShapedVectorSpace variableSpace,
            ShapedVectorSpace dataSpace) {
        this.variableSpace = variableSpace;
        this.dataSpace = dataSpace;
    }

    /**
     * Get the variable space of the cost function.
     *
     * @return The variable space of the cost function.
     */
    public VectorSpace getVariableSpace() {
        return variableSpace;
    }

    /**
     * Get the input space of the cost function.
     *
     * The input space is the same as the variable space.
     *
     * @return The variable space of the cost function.
     */
    @Override
    public VectorSpace getInputSpace() {
        return variableSpace;
    }

    /**
     * Get the data space of the cost function.
     *
     * @return The data space of the cost function.
     */
    public VectorSpace getDataSpace() {
        return dataSpace;
    }

    public static WeightedConvolutionCost build(ShapedVectorSpace space) {
        return build(space, space, null);
    }

    /**
     * Build a weighted convolution cost function with centered output.
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
     * See {@link WeightedConvolutionCost}
     * for a description of what exactly does this operator.
     * </p>
     * @param variableSpace  - The variable space.
     * @param dataSpace      - The data space.
     * @return An instance of the weighted convolution cost function.
     * @see {@link #build(ShapedVectorSpace, ShapedVectorSpace, int[])}
     */
    public static WeightedConvolutionCost build(ShapedVectorSpace variableSpace,
            ShapedVectorSpace dataSpace) {
        return build(variableSpace, dataSpace, null);
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
     * the meaning of the input and output spaces and {@link WeightedConvolutionCost}
     * for a description of what exactly does this operator.
     * </p>
     * @param variableSpace  - The input space of the operator.
     * @param dataSpace - The output space of the operator.
     * @param first       - The coordinates of the first element of the output
     *                      in the result of the simple convolution.  It must
     *                      have as many values as the rank of the input and output
     *                      spaces of the operator.
     * @return A weighted convolution operator.
     * @see {@link #build(ShapedVectorSpace, ShapedVectorSpace)}
     */
    public static WeightedConvolutionCost build(ShapedVectorSpace variableSpace,
            ShapedVectorSpace dataSpace, int[] first) {
        int type = variableSpace.getType();
        if (dataSpace.getType() != type) {
            throw new IllegalTypeException("Input and output spaces must have same element type.");
        }
        int rank = variableSpace.getRank();
        if (dataSpace.getShape().rank() != rank) {
            throw new IllegalTypeException("Input and output spaces must have same rank.");
        }
        switch (type) {
        case Traits.FLOAT:
            switch (rank) {
            case 1:
                return new WeightedConvolutionFloat1D(variableSpace, dataSpace, first);
            case 2:
                return new WeightedConvolutionFloat2D(variableSpace, dataSpace, first);
            case 3:
                return new WeightedConvolutionFloat3D(variableSpace, dataSpace, first);
            }
            break;
        case Traits.DOUBLE:
            switch (rank) {
            case 1:
                return new WeightedConvolutionDouble1D(variableSpace, dataSpace, first);
            case 2:
                return new WeightedConvolutionDouble2D(variableSpace, dataSpace, first);
            case 3:
                return new WeightedConvolutionDouble3D(variableSpace, dataSpace, first);
            }
            break;
        default:
            throw new IllegalTypeException("Only float and double types are implemented.");
        }
        throw new IllegalArgumentException("Only 1D, 2D and 3D convolution are implemented.");
    }

    @Override
    public double evaluate(double alpha, Vector x) {
        /* Check arguments. */
        if (x.belongsTo(getVariableSpace())) {
            throw new IllegalArgumentException("Variables X does not belong to the correct space.");
        }

        /* Deal with a zero multiplier. */
        if (alpha == 0.0) {
            return 0.0;
        }

        /* Call low-level function. */
        return cost(alpha, x);
    }

    @Override
    public double computeCostAndGradient(double alpha, Vector x, Vector gx, boolean clr) {
        /* Check arguments. */
        if (x.belongsTo(getVariableSpace())) {
            throw new IllegalArgumentException("Variables X does not belong to the correct space.");
        }
        if (gx.belongsTo(getVariableSpace())) {
            throw new IllegalArgumentException("Gradient GX does not belong to the correct space.");
        }

        /* Deal with a zero multiplier. */
        if (alpha == 0.0) {
            if (clr) {
                gx.zero();
            }
            return 0.0;
        }

        /* Call low-level function. */
        return cost(alpha, x, gx, clr);
    }

    protected abstract double cost(double alpha, Vector x);
    protected abstract double cost(double alpha, Vector x, Vector gx, boolean clr);

    /**
     * Set the weights and the data for the cost function.
     * @param weight - The weights in the form of a shaped vector.  It can be
     *                 {@code null}, in which case any weighting is discarded.
     *                 If non-{@code null}, its values are checked for validity
     *                 and it must belong to the output space of the operator.
     * @param data   - The data.
     */
    public abstract void setWeightsAndData(ShapedVector weight, ShapedVector data);

    /**
     * Set the weights and the data for the cost function.
     * @param weight - The weights in the form of a shaped array.  It can be
     *                 {@code null}, in which case any weighting is discarded.
     *                 If non-{@code null}, it is automatically converted to the
     *                 correct data type and its values are checked for validity.
     *                 If non-{@code null}, its shape must be that of the vectors
     *                 of the output space of the operator.
     * @param data   - The data.
     */
    public abstract void setWeightsAndData(ShapedArray weight, ShapedArray data);


    /**
     * Throw invalid weight exception.
     * @throws IllegalArgumentException weight has an invalid value.
     */
    protected static void badWeights() {
        throw new IllegalArgumentException("Weights must be finite and non-negative.");
    }

    /**
     * Set the PSF of the operator.
     * @param psf - The PSF must belongs to the input space of the operator
     *              and must be centered in the sense of of the FFT.
     */
    public abstract void setPSF(ShapedVector psf);

    /**
     * Set the PSF of the operator.
     * @param psf - The PSF in the form of a shaped array.  It is
     *              automatically converted to the correct data type,
     *              zero-padded and rolled.  It is assumed to be
     *              geometrically centered (i.e. the center of the PSF
     *              is at offset dim/2 along each dimension).
     */
    public void setPSF(ShapedArray psf) {
        setPSF(psf, Convolution.center(psf.getShape()));
    }

    /**
     * Set the PSF of the operator with given center coordinates.
     * @param psf - The PSF in the form of a shaped array.  It is
     *              automatically converted to the correct data type,
     *              zero-padded and rolled.
     * @param off - The offsets of the central element of the PSF.
     *              There must be as many elements as the rank of
     *              the PSF, each element is the 0-based offset of
     *              the center along the corresponding dimension.
     */
    public abstract void setPSF(ShapedArray psf, int[] off);

    /**
     * Check rank and input/output dimensions.
     * @param rank         - The number of dimensions.
     * @param inputShape   - The shape of input arrays.
     * @param output Shape - The shape of output arrays (must be smaller or
     *                       equal input shape for all dimensions).
     * @param offset       - For each dimension, the offset in input array
     *                       of the first element to copy in output array.
     * @return The (univariate) offset of the output in the input space.
     */
    protected static int outputOffset(int rank, Shape inputShape, Shape outputShape, int[] offset) {
        if (inputShape.rank() != rank) {
            throw new IllegalArgumentException("Bad rank for input space.");
        }
        if (outputShape.rank() != rank) {
            throw new IllegalArgumentException("Bad rank for output space.");
        }
        if (offset != null && offset.length != rank) {
            throw new IllegalArgumentException("Bad number of coordinates for the first position");
        }
        int totalOffset = 0;
        int stride = 1;
        for (int k = 0; k < rank; ++k) {
            int inpDim = inputShape.dimension(k);
            int outDim = outputShape.dimension(k);
            if (outDim > inpDim) {
                throw new IllegalArgumentException("Output dimensions must be at most as large as input dimensions");
            }
            int thisOffset;
            if (offset == null) {
                thisOffset = (inpDim/2) - (outDim/2);
            } else {
                thisOffset = offset[k];
                if (thisOffset < 0 || thisOffset + outDim > inpDim) {
                    throw new IllegalArgumentException("Output region is outside bounds");
                }

            }
            totalOffset += stride*thisOffset;
            stride *= inpDim;
        }
        return totalOffset;
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
