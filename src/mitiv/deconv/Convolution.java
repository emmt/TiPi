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
import mitiv.exception.NotImplementedException;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.ShapedLinearOperator;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;
import mitiv.utils.Timer;

/**
 * This class implements a linear operator to apply the convolution.
 *
 * <p>
 * The convolution operator <b>H</b> writes:
 * </p>
 * <p align="center">
 * <b>H</b> =
 * <b>R</b>.<b>F</b><sup>*</sup>.diag(<b>F</b>.<b><i>h</i></b>).<b>F</b>
 * </p>
 * <p>
 * with <b>F</b> the FFT (Fast Fourier Transform) operator, <b><i>h</i></b> the
 * point spread function (PSF) and <b>R</b> a linear operator which selects a
 * sub-region of the output of the convolution. The * superscript denotes the
 * adjoint of the operator (complex transpose in this specific case) and
 * diag(<i><b>v</i></b>) is a diagonal operator whose diagonal elements are
 * those of the vector <i><b>v</i></b>.
 * </p>
 * <p>
 * The vector space of the operator is that of the arguments of the
 * convolution.  Currently only 1D, 2D or 3D arguments of type float
 * or double are supported.
 * </p><p>
 * A convolution operator provides methods to perform the convolution
 * but also to apply the forward or backward FFT -- see
 * {@link #forwardFFT()} and {@link #backwardFFT()}.
 * </p>
 *
 * @author Éric Thiébaut.
 *
 */
public abstract class Convolution extends ShapedLinearOperator {

    protected final int number; // number of values in the direct space

    /**
     * The following constructor make this class non instantiable, but still
     * let others inherit from this class.  You must use the {@link #build()}
     * factory to build a convolution operator.
     */
    protected Convolution(ShapedVectorSpace space) {
        this(space, space);
    }

    /**
     * The following constructor make this class non instantiable, but still
     * let others inherit from this class.  You must use the {@link #build()}
     * factory to build a convolution operator.
     */
    protected Convolution(ShapedVectorSpace inp, ShapedVectorSpace out) {
        super(inp, out);
        this.number = inp.getNumber();
    }

    /** Retrieve the rank of the convolution. */
    public final int getRank() {
        return getInputSpace().getRank();
    }

    /** Retrieve the type of the elements of the argument of the convolution. */
    public final int getType() {
        return getInputSpace().getType();
    }

    /** Get the number of elements of the argument of the convolution. */
    public final int getNumber() {
        return number;
    }

    /**
     * Build a convolution operator.
     *
     * @param space - The input and output spaces.
     *
     * @return A new convolution operator.  The returned object is not a
     *         valid operator until the point spread function (PSF) is set
     *         with one of the {@link #setPSF()} methods.
     */
    public static Convolution build(ShapedVectorSpace space) {
        int type = space.getType();
        int rank = space.getRank();
        switch (type) {
        case Traits.FLOAT:
            switch (rank) {
            case 1:
                return new ConvolutionFloat1D(space);
            case 2:
                return new ConvolutionFloat2D(space);
            case 3:
                return new ConvolutionFloat3D(space);
            }
            break;
        case Traits.DOUBLE:
            switch (rank) {
            case 1:
                return new ConvolutionDouble1D(space);
            case 2:
                return new ConvolutionDouble2D(space);
            case 3:
                return new ConvolutionDouble3D(space);
            }
            break;
        default:
            throw new IllegalTypeException("Only float and double types are implemented.");
        }
        throw new IllegalArgumentException("Only 1D, 2D and 3D convolution are implemented.");
    }

    /**
     * Build a convolution operator with centered output.
     *
     * @param inp - The input space.
     * @param out - The output space.
     *
     * @return A new convolution operator.  The returned object is not a
     *         valid operator until the point spread function (PSF) is set
     *         with one of the {@link #setPSF()} methods.
     */
    public static Convolution build(ShapedVectorSpace inp,
            ShapedVectorSpace out) {
        /* Compute offsets (we take the least rank to avoid out of bound index exception
         * although the subsequent call to the builder will fail if the ranks are not
         * equal). */
        int rank = Math.min(inp.getRank(), out.getRank());
        int[] off = new int[rank];
        for (int k = 0; k < rank; ++k) {
            off[k] = (inp.getDimension(k)/2) - (out.getDimension(k)/2);
        }
        return build(inp, out, off);
    }

    /**
     * Build a convolution operator.
     * <p>
     * This version of the factory for building a convolution operator
     * let you specify precisely the position of the region corresponding to the
     * output in the result of the convolution. The offsets of this region must be
     * such that:
     *
     * <pre>
     * 0 &lt;= off[k] &lt;= inpDim[k] - outDim[k]
     * </pre>
     *
     * where {@code inpDim} and {@code outDim} are the respective dimensions of
     * the input and output spaces. If this does not hold (for all <i>k</i>),
     * an {@link ArrayIndexOutOfBoundsException} is thrown.
     * </p>
     *
     * @param inp
     *            - The input space.
     * @param out
     *            - The output space.
     * @param off
     *            - The relative position of the output with respect to the
     *              result of the cyclic convolution. It must have as many
     *              values as the rank of the input and output spaces of the
     *              operator.
     * @return A convolution operator.
     * @see {@link #build(ShapedVectorSpace, ShapedVectorSpace)}
     */
    public static Convolution build(ShapedVectorSpace inp,
            ShapedVectorSpace out, int[] off) {
        int type = inp.getType();
        if (out.getType() != type) {
            throw new IllegalTypeException("Input and output spaces must have same element type.");
        }
        int rank = inp.getRank();
        if (out.getShape().rank() != rank) {
            throw new IllegalTypeException("Input and output spaces must have same rank.");
        }
        switch (type) {
        case Traits.FLOAT:
            switch (rank) {
            case 1:
                return new ConvolutionFloat1D(inp, out, off);
            case 2:
                return new ConvolutionFloat2D(inp, out, off);
            case 3:
                return new ConvolutionFloat3D(inp, out, off);
            }
            break;
        case Traits.DOUBLE:
            switch (rank) {
            case 1:
                return new ConvolutionDouble1D(inp, out, off);
            case 2:
                return new ConvolutionDouble2D(inp, out, off);
            case 3:
                return new ConvolutionDouble3D(inp, out, off);
            }
            break;
        default:
            throw new IllegalTypeException("Only float and double types are implemented.");
        }
        throw new IllegalArgumentException("Only 1D, 2D and 3D convolution are implemented.");
    }

    /** Perform in-place forward FFT on the internal workspace. */
    public abstract void forwardFFT();

    /** Perform in-place backward FFT on the internal workspace. */
    public abstract void backwardFFT();

    /**
     * Copy data to the internal workspace.
     *
     * This operation should be done before calling the {@link #convolve}
     * method.
     *
     * @param inp - The source vector to copy to the internal workspace.
     */
    public abstract void push(ShapedVector inp);

    /**
     * Retrieve the result of the convolution.
     *
     * After calling the {@link #convolve} method, this operation extracts the
     * real part of the internal workspace for the output region and scales
     * the values.
     *
     * @param out
     *            - The destination vector to store the real part of the internal
     *            workspace.
     */
    public abstract void pull(ShapedVector out);

    /**
     * Apply the operator to the internal workspace.
     *
     * <p>
     * The linear operator which implements the convolution performs the
     * following operations:
     *
     * <pre>
     * push(src);
     * convolve(adjoint);
     * pull(dst);
     * </pre>
     *
     * where <b>src</b> is the source vector, <b>dst</b> is the destination
     * vector, and <b>adjoint</b> is true to apply the adjoint of the operator.
     * </p>
     * <p>
     * This splitting is intended to implement other operators or classes on top
     * of the convolution operator.
     * <p>
     *
     * @param adjoint
     *            - Apply the adjoint of the operator if true.
     */
    public abstract void convolve(boolean adjoint);

    /**
     * Set the PSF of the operator.
     * @param vec - The PSF must belongs to the input space of the operator
     *              and must be centered in the sense of of the FFT.
     */
    public abstract void setPSF(ShapedVector vec);

    /**
     * Compute offset of array center.
     *
     * The center is at offset shape.dimension(k)/2 along each dimension k.
     *
     * @param shape - The dimensions of the array.
     */
    public static int[] center(Shape shape) {
        int rank = shape.rank();
        int[] off = new int[rank];
        for (int k = 0; k < rank; ++k) {
            off[k] = shape.dimension(k)/2;
        }
        return off;
    }

    /**
     * Set the PSF of the operator.
     * @param arr - The PSF in the form of a shaped array.  It is
     *              automatically converted to the correct data type,
     *              zero-padded and rolled.  It is assumed to be
     *              geometrically centered (i.e. the center of the PSF
     *              is at offset dim/2 along each dimension).
     */
    public void setPSF(ShapedArray arr) {
        setPSF(arr, center(arr.getShape()));
    }

    /**
     * Set the PSF of the operator with given center coordinates.
     * @param arr - The PSF in the form of a shaped array.  It is
     *              automatically converted to the correct data type,
     *              zero-padded and rolled.
     * @param off - The offsets of the central element of the PSF.
     *              There must be as many elements as the rank of
     *              the PSF, each element is the 0-based offset of
     *              the center along the corresponding dimension.
     */
    public abstract void setPSF(ShapedArray arr, int[] off);

    /**
     * Helper function to zero-pad and roll the PSF.
     * @param psf - The PSF as a shaped array.
     * @param off - The offsets of the central element of the PSF.
     *              There must be as many elements as the rank of
     *              the PSF, each element is the 0-based offset of
     *              the center along the corresponding dimension.
     * @return The PSF zero padded and appropriately rolled for the
     *         FFT.
     */
    protected ShapedArray adjustPSF(ShapedArray psf, int[] off) {
        Shape psfShape = psf.getShape();
        Shape inpShape = getInputSpace().getShape();
        int rank = inpShape.rank();
        if (psfShape.rank() != rank) {
            throw new IllegalArgumentException("PSF rank not conformable.");
        }
        if (off.length != rank) {
            throw new IllegalArgumentException("Number of coordinates not conformable.");
        }
        int [] shift = new int[rank];
        for (int k = 0; k < rank; ++k) {
            int srcDim = psfShape.dimension(k);
            int dstDim = inpShape.dimension(k);
            if (srcDim > dstDim) {
                throw new IllegalArgumentException("PSF dimension(s) too large.");
            }
            int margin = (dstDim/2) - (srcDim/2); // margin for zero-padding
            shift[k] = - (margin + off[k]);
        }
        return ArrayUtils.roll(ArrayUtils.zeroPadding(psf, inpShape), shift);
    }

    @Override
    protected void privApply(Vector src, Vector dst, int job) {
        if (job != DIRECT && job != ADJOINT) {
            throw new NotImplementedException("For now we do not implement inverse convolution operations "+
                    "(talk to a specialist if you ignore the dangers of doing that!)");
        }
        push((ShapedVector)src);
        convolve(job == ADJOINT);
        pull((ShapedVector)dst);

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
