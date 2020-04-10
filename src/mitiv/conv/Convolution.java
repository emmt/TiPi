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

package mitiv.conv;

import static java.lang.Math.max;
import static mitiv.utils.FFTUtils.bestDimension;

import mitiv.array.ArrayUtils;
import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.NotImplementedException;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.ShapedLinearOperator;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;
import mitiv.utils.FFTUtils;
import mitiv.utils.Timer;

/**
 * This class implements a linear operator to apply the convolution.
 *
 * <p> The convolution operator <b>H</b> writes: </p>
 *
 * <p align="center">
 * <b>H</b> =
 * <b>R</b>.<b>F</b><sup>*</sup>.diag((1/n) <b>F</b>.<b><i>h</i></b>).<b>F</b>.<b>S</b>
 * </p>
 *
 * <p> with <b>F</b> the FFT (Fast Fourier Transform) operator, <b><i>h</i></b>
 * the point spread function (PSF), <b>R</b> a linear operator which selects a
 * sub-region of the output of the convolution and <b>S</b> a linear operator
 * which prepares the input for the FFT.  The * superscript denotes the adjoint
 * of the operator (complex transpose in this specific case),
 * diag(<i><b>v</i></b>) is a diagonal operator whose diagonal elements are
 * those of the vector <i><b>v</i></b> and <i><b>n</i></b> it the number of
 * elements used to scale the FFT.  Note that <b>R</b><sup>*</sup> is the same
 * kind of operator as <b>S</b>. </p>
 *
 * <p> The vector space of the operator is that of the arguments of the
 * convolution.  Currently only 1D, 2D or 3D arguments of type float or double
 * are supported due to a limitation of the FFT in JTransforms.  </p>
 *
 * <p> A convolution operator provides methods to perform the convolution but
 * also to apply the forward or backward FFT -- see {@link #forwardFFT()} and
 * {@link #backwardFFT()}.  </p>
 *
 * @author Éric Thiébaut.
 */
public abstract class Convolution extends ShapedLinearOperator {

    /** Number of dimensions. */
    protected final int rank;

    /** Floating-point type. */
    protected final int type;

    /** Workspace dimensions. */
    protected final Shape workShape;

    /** Offsets of input region within work space. */
    protected final int[] inputOffsets;

    /** Input region has same dimensions as work space? */
    protected final boolean fastInput;

    /** Offsets of output region within work space. */
    protected final int[] outputOffsets;

    /** Output region has same dimensions as work space? */
    protected final boolean fastOutput;

    /**
     * The following constructor makes this class non instantiable, but still
     * let others inherit from this class.  Users shall use the {@link #build}
     * factory to build a convolution operator.
     */
    protected Convolution(ShapedVectorSpace space) {
        this(null, space, space);
    }

    /**
     * The following constructor makes this class non instantiable, but still
     * let others inherit from this class.  Users shall use the {@link #build}
     * factory to build a convolution operator.
     */
    protected Convolution(ShapedVectorSpace inp, ShapedVectorSpace out) {
        this(null, inp, out);
    }

    /**
     * The following constructor makes this class non instantiable, but still
     * let others inherit from this class.  Users shall use the {@link #build}
     * factory to build a convolution operator.
     */
    protected Convolution(Shape wrk, ShapedVectorSpace inp, ShapedVectorSpace out) {
        this(wrk, inp, null, out, null);
    }

    /**
     * The following constructor makes this class non instantiable, but still
     * let others inherit from this class.  Users shall use the {@link #build}
     * factory to build a convolution operator.
     */
    protected Convolution(Shape wrk,
            ShapedVectorSpace inp, int[] inpOff,
            ShapedVectorSpace out, int[] outOff) {
        /* Instanciate in super class and set type and rank. */
        super(inp, out);
        type = inp.getType();
        if (type != Traits.FLOAT && type != Traits.DOUBLE) {
            throw new IllegalArgumentException("Expecting a floating-point type");
        }
        if (out.getType() != type) {
            throw new IllegalTypeException("Input and output spaces must have the same element type");
        }
        rank = inp.getRank();
        if (out.getShape().rank() != rank) {
            throw new IllegalTypeException("Input and output spaces must have the same rank");
        }

        /* Build/check workspace dimensions. */
        if (wrk == null) {
            int[] dims = new int[rank];
            for (int k = 0; k < rank; ++k) {
                dims[k] = bestDimension(max(inp.getDimension(k), out.getDimension(k)));
            }
            wrk = new Shape(dims);
        } else {
            if (wrk.rank() != rank) {
                throw new IllegalArgumentException("Bad number of work space dimensions");
            }
            for (int k = 0; k < rank; ++k) {
                if (wrk.dimension(k) < max(inp.getDimension(k), out.getDimension(k))) {
                    throw new IllegalArgumentException("Work space dimension(s) too small");
                }
            }
        }
        this.workShape = wrk;

        /* Build/check input offsets. */
        boolean sameDims = true;
        inputOffsets = new int[rank];
        if (inpOff == null) {
            for (int k = 0; k < rank; ++k) {
                inputOffsets[k] = (wrk.dimension(k)/2) - (inp.getDimension(k)/2);
                if (inp.getDimension(k) != wrk.dimension(k)) {
                    sameDims = false;
                }
            }
        } else {
            if (inpOff.length != rank) {
                throw new IllegalArgumentException("Bad number of input offsets");
            }
            for (int k = 0; k < rank; ++k) {
                if (inpOff[k] < 0 || inpOff[k] + inp.getDimension(k) > wrk.dimension(k)) {
                    throw new IllegalArgumentException("Out of bound input offset(s)");
                }
                inputOffsets[k] = inpOff[k];
                if (inp.getDimension(k) != wrk.dimension(k)) {
                    sameDims = false;
                }
            }
        }
        fastInput = sameDims;

        /* Build/check output offsets. */
        sameDims = true;
        outputOffsets = new int[rank];
        if (outOff == null) {
            for (int k = 0; k < rank; ++k) {
                outputOffsets[k] = (wrk.dimension(k)/2) - (out.getDimension(k)/2);
                if (out.getDimension(k) != wrk.dimension(k)) {
                    sameDims = false;
                }
            }
        } else {
            if (outOff.length != rank) {
                throw new IllegalArgumentException("Bad number of output offsets");
            }
            for (int k = 0; k < rank; ++k) {
                if (outOff[k] < 0 || outOff[k] + out.getDimension(k) > wrk.dimension(k)) {
                    throw new IllegalArgumentException("Out of bound output offset(s)");
                }
                outputOffsets[k] = outOff[k];
                if (out.getDimension(k) != wrk.dimension(k)) {
                    sameDims = false;
                }
            }
        }
        fastOutput = sameDims;
    }

    /** Retrieve the rank of the convolution. */
    public final int getRank() {
        return rank;
    }

    /** Retrieve the type of the elements of the argument of the convolution. */
    public final int getType() {
        return type;
    }

    /** Get the number of frequencies. */
    public final int getNumberOfFrequencies() {
        long number = workShape.number();
        if (2*number > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Too many frequencies for 32-bit integers");
        }
        return (int)number;
    }

    /** Get the dimensions of the work space. */
    public final Shape getWorkShape() {
        return workShape;
    }

    /**
     * Build a convolution operator with identical input and output spaces.
     *
     * <p> This methods creates a convolution operator with a work space of
     * suitable dimensions for the FFT. The input and output regions are
     * identical and are assumed to be centered into the work space. </p>
     *
     * <p> If you want to force the work space to have the same size as the
     * input and output spaces, call: </p>
     *
     * <pre>
     * Convolution.build(space.getShape(), space, null, space, null);
     * </pre>
     *
     * @param space
     *        The input and output spaces.
     *
     * @return A new convolution operator. The returned object is not a valid
     *         operator until the point spread function (PSF) is set with one of
     *         the {@link #setPSF} methods.
     *
     * @see Convolution#build(Shape, ShapedVectorSpace, int[],
     *      ShapedVectorSpace, int[])
     */
    public static Convolution build(ShapedVectorSpace space) {
        return build(space, space);
    }

    /**
     * Build a convolution operator for given input and output spaces.
     *
     * <p> This methods creates a convolution operator with a work space of
     * suitable dimensions for the FFT. The input and output region are assumed
     * to be centered into the work space. </p>
     *
     * @param inp
     *        The input space.
     *
     * @param out
     *        The output space.
     *
     * @return A new convolution operator. The returned object is not a valid
     *         operator until the point spread function (PSF) is set with one of
     *         the {@link #setPSF} methods.
     *
     * @see Convolution#build(Shape, ShapedVectorSpace, int[],
     *      ShapedVectorSpace, int[])
     */
    public static Convolution build(ShapedVectorSpace inp,
            ShapedVectorSpace out) {
        return build(null, inp, null, out, null);
    }

    /**
     * Build a convolution operator.
     *
     * <p> This version of the factory for building a convolution operator let
     * the caller specify precisely the position of the regions corresponding to
     * the input and output of the convolution relative to the work region over
     * which the FFT is computed. The offsets of these regions must be such
     * that: </p>
     *
     * <pre>
     * 0 &le; inpOff[k] &le; wrkDim[k] - inpDim[k]
     * 0 &le; outOff[k] &le; wrkDim[k] - outDim[k]
     * </pre>
     *
     * <p> where {@code wrkDim}, {@code inpDim} and {@code outDim} are the
     * respective dimensions of the work, input and output spaces. As a
     * consequence, the work space must be larger (or equal) than the input and
     * output spaces. If these constraints do not hold (for all <i>k</i>), an
     * {@link ArrayIndexOutOfBoundsException} is thrown. </p>
     *
     * @param wrk
     *        The dimensions of the work space. If {@code null}, the dimensions
     *        of the work space are automatically computed to be the smallest
     *        dimensions suitable for the FFT (see
     *        {@link FFTUtils#bestDimension(int)}) and large enough to encompass
     *        the input and output dimensions. If {@code wrk} is {@code null},
     *        it is probably better to left the offsets unspecified and set
     *        {@code inpOff} and {@code outOff} to be {@code null}.
     *
     * @param inp
     *        The input space, assumed to be centered within the work space.
     *
     * @param out
     *        The output space, assumed to be centered within the work space.
     *
     * @return A new convolution operator. The returned object is not a valid
     *         operator until the point spread function (PSF) is set with one of
     *         the {@link #setPSF} methods.
     *
     * @see Convolution#build(Shape, ShapedVectorSpace, int[],
     *      ShapedVectorSpace, int[])
     */
    public static Convolution build(Shape wrk,
            ShapedVectorSpace inp, ShapedVectorSpace out) {
        return build(wrk, inp, null, out, null);
    }

    /**
     * Build a convolution operator.
     *
     * <p> This version of the factory for building a convolution operator let
     * the caller specify precisely the position of the regions corresponding to
     * the input and output of the convolution relative to the work region over
     * which the FFT is computed. The offsets of these regions must be such
     * that: </p>
     *
     * <pre>
     * 0 &le; inpOff[k] &le; wrkDim[k] - inpDim[k]
     * 0 &le; outOff[k] &le; wrkDim[k] - outDim[k]
     * </pre>
     *
     * <p> where {@code wrkDim}, {@code inpDim} and {@code outDim} are the
     * respective dimensions of the work, input and output spaces. As a
     * consequence, the work space must be larger (or equal) than the input and
     * output spaces. If these constraints do not hold (for all <i>k</i>), an
     * {@link ArrayIndexOutOfBoundsException} is thrown. </p>
     *
     * @param wrk
     *        The dimensions of the work space. If {@code null}, the dimensions
     *        of the work space are automatically computed to be the smallest
     *        dimensions suitable for the FFT (see
     *        {@link FFTUtils#bestDimension(int)}) and large enough to encompass
     *        the input and output dimensions. If {@code wrk} is {@code null},
     *        it is probably better to left the offsets unspecified and set
     *        {@code inpOff} and {@code outOff} to be {@code null}.
     *
     * @param inp
     *        The input space.
     *
     * @param inpOff
     *        The position of the input region within the work space. If
     *        {@code null}, the input region is assumed to be centered;
     *        otherwise, it must have as many values as the rank of the input
     *        and output spaces of the operator.
     *
     * @param out
     *        The output space.
     *
     * @param outOff
     *        The position of the output region within the work space. If
     *        {@code null}, the output region assumed to be centered; otherwise,
     *        it must have as many values as the rank of the input and output
     *        spaces of the operator.
     *
     * @return A new convolution operator. The returned object is not a valid
     *         operator until the point spread function (PSF) is set with one of
     *         the {@link #setPSF} methods.
     */
    public static Convolution build(Shape wrk,
            ShapedVectorSpace inp, int[] inpOff,
            ShapedVectorSpace out, int[] outOff) {
        final int type = inp.getType();
        final int rank = inp.getRank();
        switch (type) {
            case Traits.FLOAT:
                switch (rank) {
                    case 1:
                        return new ConvolutionFloat1D(wrk, inp, inpOff, out, outOff);
                    case 2:
                        return new ConvolutionFloat2D(wrk, inp, inpOff, out, outOff);
                    case 3:
                        return new ConvolutionFloat3D(wrk, inp, inpOff, out, outOff);
                }
                break;
            case Traits.DOUBLE:
                switch (rank) {
                    case 1:
                        return new ConvolutionDouble1D(wrk, inp, inpOff, out, outOff);
                    case 2:
                        return new ConvolutionDouble2D(wrk, inp, inpOff, out, outOff);
                    case 3:
                        return new ConvolutionDouble3D(wrk, inp, inpOff, out, outOff);
                }
                break;
            default:
                throw new IllegalTypeException("Only float and double types are implemented");
        }
        throw new IllegalArgumentException("Only 1D, 2D and 3D convolution are implemented");
    }

    /** Perform in-place forward FFT on the internal workspace. */
    public abstract void forwardFFT();

    /** Perform in-place backward FFT on the internal workspace. */
    public abstract void backwardFFT();

    /**
     * Copy data to the internal workspace.
     *
     * <p> This methods applies operator <b>R</b> if <b>adjoint</b> is false and
     * operator <b>S</b><sup>*</sup> otherwise. This operation should be done
     * before calling the {@link #convolve} method. </p>
     *
     * @param src
     *        The source vector to copy to the internal workspace.
     *
     * @param adjoint
     *        Indicate whether to apply the adjoint or the direct operator.
     */
    public abstract void push(ShapedVector src, boolean adjoint);

    /**
     * Retrieve the result of the convolution.
     *
     * <p> This methods applies operator <b>S</b> if <b>adjoint</b> is false
     * and operator <b>R</b><sup>*</sup> otherwise. After calling the {@link
     * #convolve} method, this operation extracts the real part of the internal
     * workspace for the output region and scales the values. </p>
     *
     * @param dst
     *        The destination vector to store the real part of the internal
     *        workspace.
     *
     * @param adjoint
     *        Indicate whether to apply the adjoint or the direct operator.
     */
    public abstract void pull(ShapedVector dst, boolean adjoint);

    /**
     * Apply the operator to the internal workspace.
     *
     * <p> The linear operator which implements the convolution performs the
     * following operations: </p>
     *
     * <pre>
     * this.push(src, adjoint);
     * this.convolve(adjoint);
     * this.pull(dst, adjoint);
     * </pre>
     *
     * <p> where <b>src</b> is the source vector, <b>dst</b> is the destination
     * vector, and <b>adjoint</b> is true to apply the adjoint of the operator.
     * </p> <p> This splitting is intended to implement other operators or
     * classes on top of the convolution operator. </p>
     *
     * @param adjoint
     *        Apply the adjoint of the operator if true.
     */
    public abstract void convolve(boolean adjoint);

    /**
     * Compute offset of array center.
     *
     * The center is at offset shape.dimension(k)/2 along each dimension k.
     *
     * @param shape
     *        The dimensions of the array.
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
     *
     * @param psf
     *        The PSF must belongs to the input space of the operator and must
     *        be centered in the sense of of the FFT.
     */
    public abstract void setPSF(ShapedVector psf);

    /**
     * Set the PSF of the operator.
     *
     * @param psf
     *        The PSF in the form of a shaped array. It is automatically
     *        converted to the correct data type, zero-padded and rolled. It is
     *        assumed to be geometrically centered (i.e. the center of the PSF
     *        is at offset dim/2 along each dimension).
     */
    public void setPSF(ShapedArray psf) {
        setPSF(psf, null, false);
    }

    /**
     * Set the PSF of the operator with given center coordinates.
     *
     * @param psf
     *        The PSF in the form of a shaped array. It is automatically
     *        converted to the correct data type, zero-padded and rolled.
     *
     * @param off
     *        The offsets of the central element of the PSF. There must be as
     *        many elements as the rank of the PSF, each element is the 0-based
     *        offset of the center along the corresponding dimension. If
     *        {@code null}, the position of the geometric center is used.
     */
    public void setPSF(ShapedArray psf, int[] off) {
        setPSF(psf, off, false);
    }

    /**
     * Set the PSF of the operator with given center coordinates.
     *
     * @param psf
     *        The PSF in the form of a shaped array. It is automatically
     *        converted to the correct data type, zero-padded and rolled.
     *
     * @param normalize
     *        Normalize the PSF? If true, all PSF values are divided by the sum
     *        of the PSF values; otherwise, the PSF is used as it is.
     */
    public void setPSF(ShapedArray psf, boolean normalize) {
        setPSF(psf, null, normalize);
    }

    /**
     * Set the PSF of the operator with given center coordinates.
     *
     * @param psf
     *        The PSF in the form of a shaped array. It is automatically
     *        converted to the correct data type, zero-padded and rolled.
     *
     * @param off
     *        The offsets of the central element of the PSF. There must be as
     *        many elements as the rank of the PSF, each element is the 0-based
     *        offset of the center along the corresponding dimension. If
     *        {@code null}, the position of the geometric center is used.
     *
     * @param normalize
     *        Normalize the PSF? If true, all PSF values are divided by the sum
     *        of the PSF values; otherwise, the PSF is used as it is.
     */
    public abstract void setPSF(ShapedArray psf, int[] off, boolean normalize);

    /**
     * Helper function to zero-pad and roll the PSF.
     *
     * @param psf
     *        The PSF as a shaped array.
     *
     * @param off
     *        The offsets of the central element of the PSF. There must be as
     *        many elements as the rank of the PSF, each element is the 0-based
     *        offset of the center along the corresponding dimension. If
     *        {@code null}, the position of the geometric center is used.
     *
     * @return The PSF zero padded and appropriately rolled for the FFT.
     */
    protected ShapedArray adjustPSF(ShapedArray psf, int[] off) {
        Shape psfShape = psf.getShape();
        Shape inpShape = getInputSpace().getShape();
        final int rank = inpShape.rank();
        if (psfShape.rank() != rank) {
            throw new IllegalArgumentException("PSF rank not conformable");
        }
        if (off == null) {
            off = center(psfShape);
        }
        if (off.length != rank) {
            throw new IllegalArgumentException("Number of coordinates not conformable");
        }
        int [] shift = new int[rank];
        for (int k = 0; k < rank; ++k) {
            int psfDim = psfShape.dimension(k);
            int inpDim = inpShape.dimension(k);
            if (psfDim > inpDim) {
                throw new IllegalArgumentException("PSF dimension(s) too large");
            }
            int margin = (inpDim/2) - (psfDim/2); // margin for zero-padding
            shift[k] = - (margin + off[k]);
        }
        return ArrayUtils.roll(ArrayUtils.pad(psf, inpShape), shift);
    }

    @Override
    protected void _apply(Vector dst, Vector src, int job) {
        if (job != DIRECT && job != ADJOINT) {
            throw new NotImplementedException("For now we do not implement inverse convolution operations "+
                    "(talk to a specialist if you ignore the dangers of doing that!)");
        }
        boolean adjoint = (job == ADJOINT);
        push((ShapedVector)src, adjoint);
        convolve(adjoint);
        pull((ShapedVector)dst, adjoint);

    }

    /**
     * Check arguments to define a push/pull operator.
     *
     * <p> A push/pull operator is in charge of exchanging the contents of
     * vectors between an internal space (the work space) and an external space
     * (the user space). The dimensions of the user space must be smaller or
     * equal those of the work space. This method asserts that the arguments are
     * valid (an exception is thrown otherwise) and returns whether the two
     * spaces have the same dimensions. </p>
     *
     * @param rank
     *        The expected number of dimensions.
     *
     * @param wrk
     *        The dimensions of the work space.
     *
     * @param usr
     *        The dimensions of the user space.
     *
     * @param off
     *        The offsets of the user space relative to the work space. If
     *        {@code null}, the user space is assumed to be centered into the
     *        work space.
     *
     * @return True if internal and external spaces have the same dimensions;
     *         false otherwise.
     */
    protected static boolean checkPushPullArguments(final int rank, Shape wrk, Shape usr, int[] off) {
        boolean sameDims = true;
        if (wrk == null || wrk.rank() != rank) {
            throw new IllegalArgumentException(String.format("The work space must have %d dimension(s)", rank));
        }
        if (usr == null || usr.rank() != rank) {
            throw new IllegalArgumentException(String.format("The user space must have %d dimension(s)", rank));
        }
        if (off == null) {
            for (int k = 0; k < rank; ++k) {
                int wrkDim = wrk.dimension(k);
                int usrDim = usr.dimension(k);
                if (usrDim > wrkDim) {
                    throw new IllegalArgumentException("User region is too large");
                }
                if (usrDim != wrkDim) {
                    sameDims = false;
                }
            }
        } else {
            if (off.length != rank) {
                throw new IllegalArgumentException(String.format("The offsets must have %d element(s)", rank));
            }
            for (int k = 0; k < rank; ++k) {
                int wrkDim = wrk.dimension(k);
                int usrDim = usr.dimension(k);
                if (off[k] < 0 || off[k] >= wrkDim) {
                    throw new IllegalArgumentException("Out of range offset");
                }
                if (off[k] + usrDim > wrkDim) {
                    throw new IllegalArgumentException("User region beyond limits");
                }
                if (usrDim != wrkDim) {
                    sameDims = false;
                }
            }
        }
        return sameDims;
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
