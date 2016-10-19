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

package mitiv.utils;

import mitiv.array.ArrayFactory;
import mitiv.array.ByteArray;
import mitiv.array.DoubleArray;
import mitiv.array.FloatArray;
import mitiv.array.IntArray;
import mitiv.array.LongArray;
import mitiv.array.ShapedArray;
import mitiv.array.ShortArray;
import mitiv.base.Traits;

/**
 * Static methods for computing weights.
 *
 * <h3>Description</h3>
 *
 * Assuming uncorrelated noise, statistical weights are arrays of nonnegative values
 * of same shape as the data to process. The following stages are required to build
 * and validate the weights:
 * <ul>
 * <li>Compute initial weights</li>
 *     <ul>
 *     <li>using a given array of weights</li>
 *     <li>using a given array with the variance of the data</li>
 *     <li>assuming uniform noise distribution</li>
 *     <li>assuming a simple model for the variance of the data</li>
 *     </ul>
 * <li>Account for bad data (using a mask whose values indicate which are the bad measurement)</li>
 * <li>Check the weights and fix the data.  This step is needed to check that weights
 *     are nonnegative and to invalidate data with non finite values.</li>
 * </ul>
 *
 * @author Éric
 *
 */
public class WeightFactory {

    /* This class cannot be instantiated. */
    private WeightFactory() {
    }

    /**
     * Make default weights from a data array.
     *
     * @param dat - The data array.
     * @return The weights.
     */

    static public ShapedArray defaultWeights(ShapedArray dat) {
        switch (dat.getType()) {
        case Traits.FLOAT: {
            float[] wgt = new float[dat.getNumber()];
            defaultWeights(wgt, ((FloatArray)dat).flatten(false));
            return ArrayFactory.wrap(wgt, dat.getShape());
        }
        case Traits.DOUBLE: {
            double[] wgt = new double[dat.getNumber()];
            defaultWeights(wgt, ((DoubleArray)dat).flatten(false));
            return ArrayFactory.wrap(wgt, dat.getShape());
        }
        default:
            throw new IllegalArgumentException("Unsupported data type");
        }
    }

    static public void defaultWeights(float[] wgt, float[] dat) {
        computeWeightsFromData(wgt, dat, 0, 1);
    }

    static public void defaultWeights(double[] wgt, double[] dat) {
        computeWeightsFromData(wgt, dat, 0, 1);
    }

    /**
     * Compute weights given the variance of the data.
     *
     * @param var - The variance of the data.
     *
     * @return An array of weights.
     */
    static public ShapedArray computeWeightsFromVariance(ShapedArray var) {
        switch (var.getType()) {
        case Traits.FLOAT: {
            float[] wgt = new float[var.getNumber()];
            computeWeightsFromVariance(wgt, ((FloatArray)var).flatten(false));
            return ArrayFactory.wrap(wgt, var.getShape());
        }
        case Traits.DOUBLE: {
            double[] wgt = new double[var.getNumber()];
            computeWeightsFromVariance(wgt, ((DoubleArray)var).flatten(false));
            return ArrayFactory.wrap(wgt, var.getShape());
        }
        default:
            throw new IllegalArgumentException("Unsupported data type");
        }
    }

    static public void computeWeightsFromVariance(float[] wgt, float[] var) {
        if (wgt.length != var.length) {
            throw new IllegalArgumentException("Weighting and variance arrays must have the same length");
        }
        final float zero = 0;
        final float one = 1;
        for (int i = 0; i < wgt.length; ++i) {
            if (isnan(var[i]) || var[i] <= zero) {
                throw new IllegalArgumentException("Invalid variance value(s)");
            }
            wgt[i] = (isinf(var[i]) ? zero : one/var[i]);
        }
    }

    static public void computeWeightsFromVariance(double[] wgt, double[] var) {
        if (wgt.length != var.length) {
            throw new IllegalArgumentException("Weighting and variance arrays must have the same length");
        }
        final double zero = 0;
        final double one = 1;
        for (int i = 0; i < wgt.length; ++i) {
            if (isnan(var[i]) || var[i] <= zero) {
                throw new IllegalArgumentException("Invalid variance value(s)");
            }
            wgt[i] = (isinf(var[i]) ? zero : one/var[i]);
        }
    }

    /**
     * Compute statistical weights for counting data.
     *
     * <h3>Description</h3>
     *
     * This routine computes statistical weights, say <tt>wgt</tt>, for the data <tt>dat</tt> assuming the
     * following simple model for the variance of the data:
     *  <pre>
     *     Var(dat[i]) = alpha*max(dat[i],0) + beta                      (1)
     * </pre>
     * where <tt>alpha ≥ 0</tt> and <tt>beta > 0</tt> are the parameters of the noise model.
     * The computed weights are:
     *  <pre>
     *     wgt[i] = 1/Var(dat[i])    if dat[i] is finite and not a NaN
     *            = 0                else
     * </pre>
     * and thus account for valid data which must have a finite value and not
     * be a NaN.  The rationale is that saturations may be marked with an
     * infinite value while bad data are marked by a NaN.  Note that the
     * weights are guaranteed to be nonnegative and that, with <tt>alpha = 0</tt>,
     * uniform variance is assumed.
     * <br>
     *
     * An error is thrown if it is found that there are no valid data.
     * <br>
     *
     * If argument <tt>bad</tt> is specified with a finite value, all data with this specific
     * value will be considered as being invalid.
     * <br>
     *
     *
     * <h3>Rationale</h3>
     *
     * For a signal based on counts (for instance, photo-electrons), the
     * variance of the data should be given by:
     * <pre>
     *     Var(dat) = (E(gamma*dat) + sigma^2)/gamma^2
     * </pre>
     * with <tt>gamma</tt> the <i>gain</i> of the detector and <tt>sigma</tt> the standard
     * deviation (rms value) of the detector noise in electrons per pixel per
     * frame.  The gain <tt>gamma</tt> is the conversion factor in electrons per
     * analog digital unit (ADU) such that <tt>gamma*dat</tt> is the measured data in
     * count units, <tt>E(gamma*dat)</tt> is the expected number of counts (which is
     * also the variance of the counts assuming Poisson statistics).  Expanding
     * the above expression yields:
     * <pre>
     *     Var(dat) = alpha*E(dat) + beta
     * </pre>
     * with <tt>alpha = 1/gamma</tt> and <tt>beta = (sigma/gamma)^2</tt>.  Finally, the
     * following approximation:
     * <pre>
     *     E(dat) ≈ max(dat, 0)
     * </pre>
     * leads to Eq. (1).
     *
     *
     * <h3>References</h3>
     *
     * <ul>
     * <li> L. M. Mugnier, T. Fusco & J.-M. Conan, "MISTRAL: a myopic
     *   edge-preserving image restoration method, with application to
     *   astronomical adaptive-optics-corrected long-exposure images",
     *   J. Opt. Soc. Am. A, vol. 21, pp.1841-1854 (2004).</li>
     *
     * <li> A. Foi, M. Trimeche, V. Katkovnik & K. Egiazarian, "Practical
     *   Poissonian-Gaussian Noise Modeling and Fitting for Single-Image
     *   Raw-Data", IEEE Transactions on Image Processing, vol. 17,
     *   pp. 1737-1754 (2008).</li>
     * </ul>
     *
     * @param dat   - The data array.
     * @param alpha - The first scalar parameter of the variance model.
     * @param beta  - The second parameter of the variance model.
     * @param bad   - The value of bad data.
     * @return An array of weights.
     */

    static public ShapedArray computeWeightsFromData(ShapedArray dat,
            double alpha, double beta, double bad) {
        switch (dat.getType()) {
        case Traits.FLOAT: {
            float[] wgt = new float[dat.getNumber()];
            computeWeightsFromData(wgt, ((FloatArray)dat).flatten(false),
                    (float)alpha, (float)beta, (float)bad);
            return ArrayFactory.wrap(wgt, dat.getShape());
        }
        case Traits.DOUBLE: {
            double[] wgt = new double[dat.getNumber()];
            //	System.out.println("# of data: " + dat.getNumber() + ", # of weights: "+wgt.length);
            computeWeightsFromData(wgt, ((DoubleArray)dat).flatten(false), alpha, beta, bad);
            return ArrayFactory.wrap(wgt, dat.getShape());
        }
        default:
            throw new IllegalArgumentException("Unsupported data type");
        }
    }

    static public ShapedArray computeWeightsFromData(ShapedArray dat,
            double alpha, double beta) {
        return computeWeightsFromData(dat, alpha, beta, Double.NaN);
    }

    /**
     * Compute statistical weights for counting data.
     *
     * <p>This method computes weights assuming the following simple variance
     * model:</p>
     *
     * <pre>
     * var[i] = alpha*max(data[i], 0) + beta
     * </pre>
     *
     * @param wgt
     *        The destination array of weights.
     *
     * @param dat
     *        The data array (its contents is left unchanged).
     *
     * @param alpha
     *        The linear parameter of the variance model.
     *
     * @param beta
     *        The offset parameter of the variance model.
     *
     * @param bad
     *        Any data with this specific value is also considered as being
     *        invalid.
     *
     * @return The number of valid data which have nonzero weights.
     *
     * @see {@link #computeWeightsFromData(ShapedArray, double, double)} for
     *      explanation about the arguments.
     */
    static public int computeWeightsFromData(float[] wgt, float[] dat,
            float alpha, float beta, float bad) {
        final float zero = 0;
        final float one = 1;
        int count = 0;
        if (isnan(alpha) || isinf(alpha) || alpha < zero) {
            throw new IllegalArgumentException("Parameter ALPHA must be finite and nonnegative");
        }
        if (isnan(beta) || isinf(beta) || beta <= zero) {
            throw new IllegalArgumentException("Parameter BETA must be finite and strictly positive");
        }
        if (wgt.length != dat.length) {
            throw new IllegalArgumentException("Weighting and data arrays must have the same length");
        }
        final float wmax = one/beta;
        final int len = dat.length;
        if (isnan(bad) || isinf(bad)) {
            if (alpha > zero) {
                for (int i = 0; i < len; ++i) {
                    if (isinf(dat[i]) || isnan(dat[i])) {
                        wgt[i] = zero;
                    } else if (dat[i] > zero) {
                        wgt[i] = one/(alpha*dat[i] + beta);
                        ++count;
                    } else {
                        wgt[i] = wmax;
                        ++count;
                    }
                }
            } else {
                for (int i = 0; i < len; ++i) {
                    if (isinf(dat[i]) || isnan(dat[i])) {
                        wgt[i] = zero;
                    } else {
                        wgt[i] = wmax;
                        ++count;
                    }
                }
            }
        } else {
            if (alpha > zero) {
                for (int i = 0; i < len; ++i) {
                    if (dat[i] == bad || isinf(dat[i]) || isnan(dat[i])) {
                        wgt[i] = zero;
                    } else if (dat[i] > zero) {
                        wgt[i] = one/(alpha*dat[i] + beta);
                        ++count;
                    } else {
                        wgt[i] = wmax;
                        ++count;
                    }
                }
            } else {
                for (int i = 0; i < len; ++i) {
                    if (dat[i] == bad || isinf(dat[i]) || isnan(dat[i])) {
                        wgt[i] = zero;
                    } else if (wgt[i] > zero) {
                        wgt[i] = wmax;
                        ++count;
                    }
                }
            }
        }
        return count;
    }

    static public int computeWeightsFromData(float[] wgt, float[] dat,
            float alpha, float beta) {
        return computeWeightsFromData(wgt, dat, alpha, beta, Float.NaN);
    }

    static public int computeWeightsFromData(double[] wgt, double[] dat,
            double alpha, double beta, double bad) {
        //  System.out.println("# of data: " + dat.length + ", # of weights: "+wgt.length);
        final double zero = 0;
        final double one = 1;
        int count = 0;
        if (isnan(alpha) || isinf(alpha) || alpha < zero) {
            throw new IllegalArgumentException("Parameter ALPHA must be finite and nonnegative");
        }
        if (isnan(beta) || isinf(beta) || beta <= zero) {
            throw new IllegalArgumentException("Parameter BETA must be finite and strictly positive");
        }
        if (wgt.length != dat.length) {
            throw new IllegalArgumentException("Weighting and data arrays must have the same length");
        }
        final double wmax = one/beta;
        final int len = dat.length;
        if (isnan(bad) || isinf(bad)) {
            if (alpha > zero) {
                for (int i = 0; i < len; ++i) {
                    if (isinf(dat[i]) || isnan(dat[i])) {
                        wgt[i] = zero;
                    } else if (dat[i] > zero) {
                        wgt[i] = one/(alpha*dat[i] + beta);
                        ++count;
                    } else {
                        wgt[i] = wmax;
                        ++count;
                    }
                }
            } else {
                for (int i = 0; i < len; ++i) {
                    if (isinf(dat[i]) || isnan(dat[i])) {
                        wgt[i] = zero;
                    } else {
                        wgt[i] = wmax;
                        ++count;
                    }
                }
            }
        } else {
            if (alpha > 0) {
                for (int i = 0; i < len; ++i) {
                    if (dat[i] == bad || isinf(dat[i]) || isnan(dat[i])) {
                        wgt[i] = zero;
                    } else if (dat[i] > zero) {
                        wgt[i] = one/(alpha*dat[i] + beta);
                        ++count;
                    } else {
                        wgt[i] = wmax;
                        ++count;
                    }
                }
            } else {
                for (int i = 0; i < len; ++i) {
                    if (dat[i] == bad || isinf(dat[i]) || isnan(dat[i])) {
                        wgt[i] = zero;
                    } else if (wgt[i] > zero) {
                        wgt[i] = wmax;
                        ++count;
                    }
                }
            }
        }
        return count;
    }

    static public int computeWeightsFromData(double[] wgt, double[] dat,
            double alpha, double beta) {
        return computeWeightsFromData(wgt, dat, alpha, beta, Double.NaN);
    }

    /**
     * Remove bad data by setting their weights to zero.
     *
     * @param wgt - The array of weights.  Operation is done in-place an this array must be flat.
     * @param bad - An array whose elements are true (non-zero) where data have to be discarded.
     *
     * @throws IllegalArgumentException if <tt>wgt</tt> is not flat or has unsupported type or if <tt>wgt</tt>
     *         and <tt>bad</tt> do not have the same shape.
     */
    static public void removeBads(ShapedArray wgt, ShapedArray bad) {
        if (! wgt.getShape().equals(bad.getShape())) {
            throw new IllegalArgumentException("Array of weights and bad data must have the same shape");
        }
        if (! wgt.isFlat()) {
            throw new IllegalArgumentException("Weights must be a flat array");
        }
        boolean b[];
        switch (bad.getType()) {
        case Traits.BYTE:
            b = toBoolean(((ByteArray)bad).flatten(false));
            break;
        case Traits.SHORT:
            b = toBoolean(((ShortArray)bad).flatten(false));
            break;
        case Traits.INT:
            b = toBoolean(((IntArray)bad).flatten(false));
            break;
        case Traits.LONG:
            b = toBoolean(((LongArray)bad).flatten(false));
            break;
        case Traits.FLOAT:
            b = toBoolean(((FloatArray)bad).flatten(false));
            break;
        case Traits.DOUBLE:
            b = toBoolean(((DoubleArray)bad).flatten(false));
            break;
        default:
            throw new IllegalArgumentException("Unsupported data type");
        }

        switch (wgt.getType()) {
        case Traits.FLOAT:
            removeBads(((FloatArray)wgt).flatten(false), b);
            break;
        case Traits.DOUBLE:
            removeBads(((DoubleArray)wgt).flatten(false), b);
            break;
        default:
            throw new IllegalArgumentException("Unsupported data type");
        }
    }

    static public final void removeBads(float[] wgt, boolean[] bad) {
        if (wgt.length != bad.length) {
            throw new IllegalArgumentException("Array of weights and bad data must have the same length");
        }
        final float zero = 0;
        final int len = wgt.length;
        for (int i = 0; i < len; ++i) {
            if (bad[i]) {
                wgt[i] = zero;
            }
        }
    }

    static public final void removeBads(double[] wgt, boolean[] bad) {
        if (wgt.length != bad.length) {
            throw new IllegalArgumentException("Array of weights and bad data must have the same length");
        }
        final double zero = 0;
        final int len = wgt.length;
        for (int i = 0; i < len; ++i) {
            if (bad[i]) {
                wgt[i] = zero;
            }
        }
    }

    /**
     * Check array of weights.
     *
     * This function checks that all weights have finite, nonnegative values and throws
     * an <tt>IllegalArgumentException</tt> otherwise.
     *
     * @param wgt - The array of weights.
     */
    static public void checkWeights(ShapedArray wgt) {
        switch (wgt.getType()) {
        case Traits.FLOAT:
            checkWeights(((FloatArray)wgt).flatten(false));
            break;
        case Traits.DOUBLE:
            checkWeights(((DoubleArray)wgt).flatten(false));
            break;
        default:
            throw new IllegalArgumentException("Unsupported data type");
        }
    }

    static public void checkWeights(float[] wgt) {
        final float zero = 0;
        final int len = wgt.length;
        int cnt = 0;
        for (int i = 0; i < len; ++i) {
            if (isinf(wgt[i]) || isnan(wgt[i]) || wgt[i] < zero) {
                throw new IllegalArgumentException("Invalid weight value");
            } else if (wgt[i] > zero) {
                ++cnt;
            }
        }
        if (cnt < 1) {
            throw new IllegalArgumentException("No valid data!");
        }
    }

    static public void checkWeights(double[] wgt) {
        final double zero = 0;
        final int len = wgt.length;
        int cnt = 0;
        for (int i = 0; i < len; ++i) {
            if (isinf(wgt[i]) || isnan(wgt[i]) || wgt[i] < zero) {
                throw new IllegalArgumentException("Invalid weight value");
            } else if (wgt[i] > zero) {
                ++cnt;
            }
        }
        if (cnt < 1) {
            throw new IllegalArgumentException("No valid data!");
        }
    }

    /**
     * Fix statistical weights and data.
     *
     * This function fixes the statistical weights and the data arrays.
     * On input, weights must be nonnegative.  Invalid data (because their
     * weights are zero or because they have non-finite value
     * are replaced by zeros (to avoid further numerical issues) and their
     * corresponding weights are also set to zero (to make sure invalid data
     * are never used).
     *
     * <br>
     * The rationale is to assume that invalid data are marked by a NaN
     * or by infinity (e.g. to indicate a saturation).
     *
     * <br>
     * Beware that operation is done in-place: the contents of the array may be modified.
     *
     * <br>
     * The two arguments must be flat arrays of same floating point type and of same shape.
     *
     * @param wgt - The weights.
     * @param dat - The data.
     */

    static public void fixWeightsAndData(ShapedArray wgt, ShapedArray dat) {
        if (! wgt.getShape().equals(dat.getShape())) {
            throw new IllegalArgumentException("Array of weights and data must have the same shape");
        }
        if (! wgt.isFlat()) {
            throw new IllegalArgumentException("Weights must be a flat array");
        }
        if (! dat.isFlat()) {
            throw new IllegalArgumentException("Data must be a flat array");
        }
        if (wgt.getType() != dat.getType()) {
            throw new IllegalArgumentException("Data and weights have the same element type");
        }
        switch (dat.getType()) {
        case Traits.FLOAT:
            fixWeightsAndData(((FloatArray)wgt).flatten(false), ((FloatArray)dat).flatten(false));
            break;
        case Traits.DOUBLE:
            fixWeightsAndData(((DoubleArray)wgt).flatten(false), ((DoubleArray)dat).flatten(false));
            break;
        default:
            throw new IllegalArgumentException("Unsupported data type");
        }
    }

    static public void fixWeightsAndData(float[] wgt, float[] dat) {
        if (wgt.length != dat.length) {
            throw new IllegalArgumentException("Weighting and data arrays must have the same length");
        }
        final float zero = 0;
        final int len = wgt.length;
        int cnt = 0;
        for (int i = 0; i < len; ++i) {
            if (wgt[i] == zero) {
                dat[i] = zero;
            } else {
                if (isinf(wgt[i]) || isnan(wgt[i]) || wgt[i] < zero) {
                    throw new IllegalArgumentException("Invalid weight value");
                }
                if (isinf(dat[i]) || isnan(dat[i])) {
                    wgt[i] = zero;
                    dat[i] = zero;
                } else {
                    ++cnt;
                }
            }
        }
        if (cnt < 1) {
            throw new IllegalArgumentException("No valid data!");
        }
    }

    static public void fixWeightsAndData(double[] wgt, double[] dat) {
        if (wgt.length != dat.length) {
            throw new IllegalArgumentException("Weighting and data arrays must have the same length");
        }
        final double zero = 0;
        final int len = wgt.length;
        int cnt = 0;
        for (int i = 0; i < len; ++i) {
            if (wgt[i] == zero) {
                dat[i] = zero;
            } else {
                if (isinf(wgt[i]) || isnan(wgt[i]) || wgt[i] < zero) {
                    throw new IllegalArgumentException("Invalid weight value");
                }
                if (isinf(dat[i]) || isnan(dat[i])) {
                    wgt[i] = zero;
                    dat[i] = zero;
                } else {
                    ++cnt;
                }
            }
        }
        if (cnt < 1) {
            throw new IllegalArgumentException("No valid data!");
        }
    }

    /* Utilities */

    private static final boolean isinf(float val) {
        return Float.isInfinite(val);
    }

    private static final boolean isnan(float val) {
        return Float.isNaN(val);
    }

    private static final boolean isinf(double val) {
        return Double.isInfinite(val);
    }

    private static final boolean isnan(double val) {
        return Double.isNaN(val);
    }

    private static final boolean[] toBoolean(byte[] arr) {
        final byte zero = 0;
        boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            res[i] = (arr[i] != zero);
        }
        return res;
    }

    private static final boolean[] toBoolean(short[] arr) {
        final short zero = 0;
        boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            res[i] = (arr[i] != zero);
        }
        return res;
    }

    private static final boolean[] toBoolean(int[] arr) {
        final int zero = 0;
        boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            res[i] = (arr[i] != zero);
        }
        return res;
    }

    private static final boolean[] toBoolean(long[] arr) {
        final long zero = 0;
        boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            res[i] = (arr[i] != zero);
        }
        return res;
    }

    private static final boolean[] toBoolean(float[] arr) {
        final float zero = 0;
        boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            res[i] = (arr[i] != zero);
        }
        return res;
    }

    private static final boolean[] toBoolean(double[] arr) {
        final double zero = 0;
        boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            res[i] = (arr[i] != zero);
        }
        return res;
    }

}
