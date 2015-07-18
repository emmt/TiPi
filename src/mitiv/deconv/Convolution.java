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
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;
import mitiv.utils.Timer;

public abstract class Convolution {

    protected final ShapedVectorSpace space;
    protected final Shape shape;
    protected final int number; // number of values in the direct space

    /**
     * The following constructors make this class non instantiable, but still
     * let others inherit from this class.  You must use the {@link #build()}
     * factory to build a convolution operator.
     */
    protected Convolution(ShapedVectorSpace space) {
        this.shape = space.getShape();
        this.number = (int)shape.number();
        this.space = space;
    }

    /** Retrieve the vector space of arguments of the convolution. */
    public final ShapedVectorSpace getSpace() {
        return space;
    }

    /** Retrieve the rank of the convolution. */
    public final int getRank() {
        return space.getRank();
    }

    /** Retrieve the type of the elements of the arguments of the convolution. */
    public final int getType() {
        return space.getType();
    }

    /** Get the number of elements of the arguments of the convolution. */
    public final int getNumber() {
        return number;
    }

    /**
     * Get the length of a given dimension for the arguments of the convolution.
     * @param k - The index of the dimension.
     * @return The length of the {@code (k+1)}-th dimension.
     */
    public final int dimension(int k) {
        return shape.dimension(k);
    }

    /**
     * Build a convolution operator.
     * <p>
     * The returned object is not a valid operator until you set the
     * point spread function (PSF) with one of the {@link #setPSF()}
     * methods.
     * </p><p>
     * The vector space of the operator is that of the arguments of the
     * convolution.  Currently only 1D, 2D or 3D arguments of type float
     * or double are supported.
     * </p><p>
     * A convolution operator provides methods to perform the convolution
     * but also to apply the forward or backward FFT -- see
     * {@link #forwardFFT()} and {@link #backwardFFT()}.
     * </p>
     * @param space - The vector space of the arguments of the convolution.
     * @return An instance of the convolution operator.
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

    /** Perform in-place forward FFT on the internal workspace. */
    public abstract void forwardFFT();

    /** Perform in-place backward FFT on the internal workspace. */
    public abstract void backwardFFT();

    /**
     * Copy data to the internal workspace.
     * @param inp - The real array to copy to the internal workspace.
     */
    public abstract void push(ShapedVector inp);

    /**
     * Extract real part of the internal workspace.
     * @param out - The real array to store the real part of the internal workspace.
     */
    public abstract void pull(ShapedVector out);

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
        int rank = shape.rank();
        if (psfShape.rank() != rank) {
            throw new IllegalArgumentException("PSF rank not conformable.");
        }
        if (off.length != rank) {
            throw new IllegalArgumentException("Number of coordinates not conformable.");
        }
        int [] shift = new int[rank];
        for (int k = 0; k < rank; ++k) {
            int srcDim = psfShape.dimension(k);
            int dstDim = shape.dimension(k);
            if (srcDim > dstDim) {
                throw new IllegalArgumentException("PSF dimension(s) too large.");
            }
            int margin = (dstDim/2) - (srcDim/2); // margin for zero-padding
            shift[k] = - (margin + off[k]);
        }
        return ArrayUtils.roll(ArrayUtils.zeroPadding(psf, shape), shift);
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
