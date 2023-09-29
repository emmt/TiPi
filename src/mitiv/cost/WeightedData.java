/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014-2016 the MiTiV project, http://mitiv.univ-lyon1.fr/
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

package mitiv.cost;

import mitiv.array.ByteArray;
import mitiv.array.DoubleArray;
import mitiv.array.FloatArray;
import mitiv.array.IntArray;
import mitiv.array.LongArray;
import mitiv.array.ShapedArray;
import mitiv.array.ShortArray;
import mitiv.base.ArrayDescriptor;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.exception.NonConformableArrayException;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.FloatShapedVectorSpace;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;
import mitiv.weights.WeightFactory;

/**
 * Implement weighted data.
 *
 * <p>Weighted data is the association of a vector of measurements (the data)
 * and their corresponding statistical weights. The data are assumed to be
 * uncorrelated. The weights are nonnegative and should be set to the inverse of
 * the variance of the corresponding data or to zero for invalid data. This is
 * consistent to assuming infinite variance to bad or missing data.</p>
 *
 * <p>The reasons to have this class are to maintain the coherence between the
 * data and theirs weights (data should always have a given level of
 * uncertainty) and to insure that the data and the weights can be used by
 * iterative methods without taking care of non-finite values. The data returned
 * by the {@link #getData()} method are guaranteed to all have finite values.
 * Similarly, the weights returned by the {@link #getWeights()} method are
 * guaranteed to all have finite and nonnegative values. To avoid polluting
 * arrays that can be shared by others, the caller can specify whether the given
 * data and weights are writable or not. To avoid spoiling memory, methods of
 * this class take care of creating copies of the data and weights only when
 * necessary.</p>
 *
 * <p>To construct an instance of this class, the data must be specified
 * exactly once and the weights can be specified at most once, using {@link
 * #setWeights(ShapedVector, boolean)} or {@link
 * #computeWeightsFromData(double, double)}. When the checked/fixed data or
 * weights are requested by {@link #getData()} or {@link #getWeights()},
 * default weights, initially all equal to one, are created if no weights have
 * been specified yet. Non-finite values in the input data and zero weights are
 * assumed to indicate invalid data. It is possible to indicate additional bad
 * data with one of the {@code markBadData} methods. Marking bad data can be
 * done in as many steps as needed but cannot be undone</p>
 *
 * <p>A typical usage is:</p>
 *
 * <pre>
 * WeightedData wd = new WeightedData(data); // create instance with given data
 * wd.setWeights(weights); // set the weights
 * wd.markBadData(mask); // mark some bad data
 * ShapedVector wgt = wd.getWeights();
 * ShapedVector dat = wd.getData();
 * </pre>
 *
 * <p>This class also implements the {@link DifferentiableCostFunction}
 * interface, a weighted data instance can therefore be directly used to solve
 * a denoising problem.</p>
 *
 * @author Éric.
 *
 */
public class WeightedData implements DifferentiableCostFunction {

    private final ShapedVectorSpace dataSpace;
    private final boolean single;
    private ShapedVector data = null;
    private ShapedVector weights = null;
    private boolean updatePending = true;
    private boolean writableData = false;
    private boolean writableWeights = false;
    private int validDataNumber = 0;

    /**
     * Create an empty instance of weighted data.
     *
     * <p> The data and, possibly, the weights can be specified later with one
     * of the {@link #setData} and {@link #setWeights} methods. </p>
     *
     * @param descr
     *        The array descriptor of the data (type must be
     *        {@link Traits#FLOAT} or {@link Traits#DOUBLE}).
     */
    public WeightedData(ArrayDescriptor descr) {
        this(descr.getType(), descr.getShape());
    }

    /**
     * Create an empty instance of weighted data.
     *
     * <p> The data and, possibly, the weights can be specified later with one
     * of the {@link #setData} and {@link #setWeights} methods. </p>
     *
     * @param space
     *        The vector space of the data.
     */
    public WeightedData(ShapedVectorSpace space) {
        switch (space.getType()) {
            case Traits.FLOAT:
                single = true;
                break;
            case Traits.DOUBLE:
                single = false;
                break;
            default:
                throw mustBeFloatingPoint();
        }
        this.dataSpace = space;
    }

    /**
     * Create an empty instance of weighted data.
     *
     * <p> The data and, possibly, the weights can be specified later with one
     * of the {@link #setData} and {@link #setWeights} methods. </p>
     *
     * @param type
     *        The type of the data (must be {@link Traits#FLOAT} or
     *        {@link Traits#DOUBLE}).
     *
     * @param shape
     *        The dimensions of the data.
     */
    public WeightedData(int type, Shape shape) {
        switch (type) {
            case Traits.FLOAT:
                single = true;
                this.dataSpace = new FloatShapedVectorSpace(shape);
                break;
            case Traits.DOUBLE:
                single = false;
                this.dataSpace = new DoubleShapedVectorSpace(shape);
                break;
            default:
                throw mustBeFloatingPoint();
        }
    }

    /**
     * Create an empty instance of weighted data.
     *
     * <p> The data and, possibly, the weights can be specified later with one
     * of the {@link #setData} and {@link #setWeights} methods. </p>
     *
     * @param type
     *        The type of the data (must be {@link Traits#FLOAT} or
     *        {@link Traits#DOUBLE}).
     *
     * @param dims
     *        The dimensions of the data.
     */
    public WeightedData(int type, int... dims) {
        this(type, new Shape(dims));
    }

    /**
     * Create an instance of weighted data from given data.
     *
     * <p> The weights may be specified later with one of the
     * {@link #setWeights} methods. </p>
     *
     * @param data
     *        The data as a shaped array of type {@link Traits#FLOAT} or
     *        {@link Traits#DOUBLE}.
     *
     * @param writable
     *        Specify whether the {@code data} argument is writable.
     */
    public WeightedData(ShapedArray data, boolean writable) {
        this(data.getType(), data.getShape());
        setData(data, writable);
    }

    /**
     * Create an instance of weighted data from given data.
     *
     * <p> The weights may be specified later with one of the
     * {@link #setWeights} methods. </p>
     *
     * @param data
     *        The data as a shaped array of type {@link Traits#FLOAT} or
     *        {@link Traits#DOUBLE}. The data is assumed to be read-only.
     */
    public WeightedData(ShapedArray data) {
        this(data, false);
    }

    /**
     * Create an instance of weighted data from given data.
     *
     * <p> The weights may be specified later with one of the
     * {@link #setWeights} methods. </p>
     *
     * @param data
     *        The data.
     *
     * @param writable
     *        Specify whether the {@code data} argument is writable.
     */
    public WeightedData(ShapedVector data, boolean writable) {
        this(data.getSpace());
        setData(data, writable);
    }

    /**
     * Create an instance of weighted data from given data.
     *
     * <p> The data is assumed to be non-writable and the weights may be
     * specified later with one of the {@link #setWeights} methods. </p>
     *
     * @param data
     *        The data (assumed to be read-only).
     */
    public WeightedData(ShapedVector data) {
        this(data.getSpace());
        setData(data);
    }

    /**
     * Create an instance of weighted data from given weights and data.
     *
     * <p> The data and the weights are assumed to be non-writable and must
     * belong to the same vector space. </p>
     *
     * @param data
     *        The data (assumed to be read-only).
     *
     * @param weights
     *        The weights (assumed to be read-only).
     */
    public WeightedData(ShapedArray data, ShapedArray weights) {
        this(Math.max(data.getType(), weights.getType()), data.getShape());
        setData(data);
        setWeights(weights);
    }

    /**
     * Create an instance of weighted data from given weights and data.
     *
     * <p> The data and the weights must belong to the same vector space. </p>
     *
     * @param data
     *        The data (assumed to be read-only).
     *
     * @param writableData
     *        Specify whether the data are writable.
     *
     * @param weights
     *        The weights (assumed to be read-only).
     *
     * @param writableWeights
     *        Specify whether the weights are writable.
     */
    public WeightedData(ShapedVector data, boolean writableData,
            ShapedVector weights, boolean writableWeights) {
        this(data.getSpace());
        setData(data, writableData);
        setWeights(weights, writableWeights);
    }

    /**
     * Create an instance of weighted data from given weights and data.
     *
     * <p> The data and the weights are assumed to be non-writable and must
     * belong to the same vector space. </p>
     *
     * @param data
     *        The data (assumed to be read-only).
     *
     * @param weights
     *        The weights (assumed to be read-only).
     */
    public WeightedData(ShapedVector data, ShapedVector weights) {
        this(data.getSpace());
        setData(data);
        setWeights(weights);
    }

    /**
     * Figure out whether the weighted data are in single precision.
     *
     * @return True if the weighted data are in single precision; false if the
     *         data are in double precision.
     */
    public final boolean isSinglePrecision() {
        return this.single;
    }

    /**
     * Get the data space.
     *
     * @return The vector space of the data.
     */
    public final ShapedVectorSpace getDataSpace() {
        return this.dataSpace;
    }

    /**
     * Get the data.
     *
     * <p> This method takes care of updating the weighted data contents (if
     * necessary) before returning the result. At least the data must have been
     * specified. If no weights have been specified yet, uniform weights will be
     * created (and cannot be changed later). </p>
     *
     * @return The data (never {@code null}).
     */
    public final ShapedVector getData() {
        if (this.updatePending) {
            update();
        }
        return this.data;
    }

    /**
     * Get the weights.
     *
     * <p> This method takes care of updating the weighted data contents (if
     * necessary) before returning the result. At least the data must have been
     * specified. If no weights have been specified yet, uniform weights will be
     * created (and cannot be changed later). </p>
     *
     * @return The weights (never {@code null}).
     */
    public final ShapedVector getWeights() {
        if (this.updatePending) {
            update();
        }
        return this.weights;
    }

    /**
     * Get the number of valid data.
     *
     * <p>This method takes care of updating the weighted data contents (if
     * necessary) before returning the result. At least the data must have been
     * specified. If no weights have been specified yet, uniform weights will be
     * created (and cannot be changed later).</p>
     *
     * @return The number of valid data (the ones which have non-zero weights).
     */
    public final int getValidDataNumber() {
        if (this.updatePending) {
            update();
        }
        return this.validDataNumber;
    }

    /**
     * Get the weighted mean of the valid data.
     *
     * <p>This method takes care of updating the weighted data contents (if
     * necessary) before returning the result. At least the data must have been
     * specified. If no weights have been specified yet, uniform weights will be
     * created (and cannot be changed later).</p>
     *
     * @return The number of valid data (the ones which have non-zero weights).
     */
    public double getWeightedMean() {
        if (this.updatePending) {
            update();
        }
        if (this.validDataNumber < 1) {
            return Double.NaN;
        }
        double swy = 0, sw = 0;
        if (single) {
            float[] w = ((FloatShapedVector)weights).getData();
            float[] y = ((FloatShapedVector)data).getData();
            for (int i = 0; i < y.length; ++i) {
                sw += w[i];
                swy += w[i]*y[i];
            }
        } else {
            double[] w = ((DoubleShapedVector)weights).getData();
            double[] y = ((DoubleShapedVector)data).getData();
            for (int i = 0; i < y.length; ++i) {
                sw += w[i];
                swy += w[i]*y[i];
            }
        }
        return swy/sw;
    }

    /**
     * Set the data.
     *
     * @param arr
     *        The data as a shaped array (assumed to be non-writable).
     */
    public void setData(ShapedArray arr) {
        setData(arr, false);
    }

    /**
     * Set the data.
     *
     * @param arr
     *        The data as a shaped array.
     *
     * @param writable
     *        Specify whether the data are writable.
     */
    public void setData(ShapedArray arr, boolean writable) {
        if (! arr.getShape().equals(dataSpace.getShape())) {
            throw nonconformableData();
        }
        if (arr.getType() != dataSpace.getType()) {
            arr = (single ? arr.toFloat() : arr.toDouble());
            writable = true;
        } else if (! arr.isFlat()) {
            writable = true;
        }
        ShapedVector vec;
        if (single) {
            vec = new FloatShapedVector((FloatShapedVectorSpace)dataSpace,
                    ((FloatArray)arr).flatten());
        } else {
            vec = new DoubleShapedVector((DoubleShapedVectorSpace)dataSpace,
                    ((DoubleArray)arr).flatten());
        }
        setData(vec, writable);
    }

    /**
     * Set the data.
     *
     * @param data
     *        The data as a shaped vector (assumed to be non-writable).
     */
    public void setData(ShapedVector data) {
        setData(data, false);
    }

    /**
     * Set the data.
     *
     * <p> Note that the data can only be specified once. </p>
     *
     * @param data
     *        The data as a shaped vector.
     *
     * @param writable
     *        Specify whether the data are writable.
     */
    public void setData(ShapedVector data, boolean writable) {
        data.assertBelongsTo(dataSpace);
        if (this.data != null) {
            throw dataCanOnlyBeSpecifiedOnce();
        }
        this.data = data;
        this.writableData = writable;
        this.updatePending = true;
    }

    /**
     * Set the weights.
     *
     * @param arr
     *        The weights as a shaped array (assumed to be non-writable).
     */
    public void setWeights(ShapedArray arr) {
        setWeights(arr, false);
    }

    /**
     * Set the weights.
     *
     * @param arr
     *        The weights as a shaped array.
     *
     * @param writable
     *        Specify whether the weights are writable.
     */
    public void setWeights(ShapedArray arr, boolean writable) {
        if (! arr.getShape().equals(dataSpace.getShape())) {
            throw nonconformableData();
        }
        if (arr.getType() != dataSpace.getType()) {
            arr = (single ? arr.toFloat() : arr.toDouble());
            writable = true;
        } else if (! arr.isFlat()) {
            writable = true;
        }
        ShapedVector vec;
        if (single) {
            vec = new FloatShapedVector((FloatShapedVectorSpace)dataSpace,
                    ((FloatArray)arr).flatten());
        } else {
            vec = new DoubleShapedVector((DoubleShapedVectorSpace)dataSpace,
                    ((DoubleArray)arr).flatten());
        }
        setWeights(vec, writable);
    }

    /**
     * Set the weights.
     *
     * <p> Note that the weights can only be specified once. </p>
     *
     * @param weights
     *        The weights as a shaped vector (assumed to be non-writable).
     */
    public void setWeights(ShapedVector weights) {
        setWeights(weights, false);
    }

    /**
     * Set the weights.
     *
     * <p> Note that the weights can only be specified once. </p>
     *
     * @param weights
     *        The weights as a shaped vector.
     *
     * @param writable
     *        Specify whether the weights are writable.
     */
    public void setWeights(ShapedVector weights, boolean writable) {
        weights.assertBelongsTo(dataSpace);
        if (this.weights != null&&(!this.writableWeights)) {
            throw weightsCanOnlyBeSpecifiedOnce();
        }
        this.weights = weights;
        this.writableWeights = writable;
        this.updatePending = true;
    }

    /**
     * Compute the weights assuming a simple data-based model.
     *
     * <p> The variance of the data is assumed to be given by: </p>
     *
     * <pre>
     * var(data) = alpha * max(0, data) + beta
     * </pre>
     *
     * <p> and the weights are the reciprocal or the variances. </p>
     *
     * @param alpha
     *        Linear parameter of the variance model.
     *
     * @param beta
     *        Affine parameter of the variance mode.
     */
    public void computeWeightsFromData(double alpha, double beta) {
        if (data == null) {
            noDataHasBeenSpecified();
        }
        if (weights != null) {
            // Weights cannot be recomputed because data are modified by this operation.
            throw weightsCanOnlyBeSpecifiedOnce();
        }
        weights = data.create();
        writableWeights = true;
        if (single) {
            float[] wgt = ((FloatShapedVector)weights).getData();
            float[] dat = ((FloatShapedVector)data).getData();
            validDataNumber = WeightFactory.computeWeightsFromData(wgt, dat, (float)alpha, (float)beta);
        } else {
            double[] wgt = ((DoubleShapedVector)weights).getData();
            double[] dat = ((DoubleShapedVector)data).getData();
            validDataNumber = WeightFactory.computeWeightsFromData(wgt, dat, alpha, beta);

        }

        /* Updating is needed to set invalid data to zero. */
        this.updatePending = true;
    }

    /**
     * Remove bad data.
     *
     * <p> This method marks bad data by setting their weights to zero. If no
     * weights have been set, the weights of good data will be arbitrarily set
     * to one. This operation cannot be undone (the only possibility is to
     * rebuilt the weighted data instance). </p>
     *
     * @param bad
     *        The mask of bad data specified as a shaped vector. It must have
     *        the same shape as the data and be true (i.e. non-zero) where data
     *        has to be discarded.
     */
    public void markBadData(ShapedVector bad) {
        if (! bad.getShape().equals(dataSpace.getShape())) {
            throw badMaskShape();
        }
        markBadData(toBoolean(bad));
    }

    /**
     * Remove bad data.
     *
     * @param bad
     *        The mask of bad data specified as a shaped array. It must have the
     *        same shape as the data and be true (i.e. non-zero) where data has
     *        to be discarded.
     *
     * @see #markBadData(ShapedVector) for details.
     */
    public void markBadData(ShapedArray bad) {
        if (! bad.getShape().equals(dataSpace.getShape())) {
            throw badMaskShape();
        }
        markBadData(toBoolean(bad));
    }

    /**
     * Remove bad data.
     *
     * @param bad
     *        The mask of bad data specified as an array of boolean values. It
     *        must have the same number of elements as the data and be true
     *        where data has to be discarded.
     *
     * @see #markBadData(ShapedVector) for details.
     */
    public final void markBadData(boolean[] bad) {
        if (data == null) {
            throw noDataHasBeenSpecified();
        }
        final int len = data.getNumber();
        if (bad.length != len) {
            throw badMaskLength();
        }
        if (weights == null) {
            weights = data.create();
            writableWeights = true;
            if (single) {
                final float one = 1;
                final float zero = 0;
                final float[] wgt = ((FloatShapedVector)weights).getData();
                for (int i = 0; i < len; ++i) {
                    wgt[i] = (bad[i] ? zero : one);
                }
            } else {
                final double one = 1;
                final double zero = 0;
                final double[] wgt = ((DoubleShapedVector)weights).getData();
                for (int i = 0; i < len; ++i) {
                    wgt[i] = (bad[i] ? zero : one);
                }
            }
            updatePending = true;
        } else {
            if (single) {
                final float zero = 0;
                float[] wgt = ((FloatShapedVector)weights).getData();
                for (int i = 0; i < len; ++i) {
                    if (bad[i] && wgt[i] != zero) {
                        if (! writableWeights) {
                            cloneWeights();
                            wgt = ((FloatShapedVector)weights).getData();
                        }
                        wgt[i] = zero;
                        updatePending = true;
                    }
                }
            } else {
                final double zero = 0;
                double[] wgt = ((DoubleShapedVector)weights).getData();
                for (int i = 0; i < len; ++i) {
                    if (bad[i] && wgt[i] != zero) {
                        if (! writableWeights) {
                            cloneWeights();
                            wgt = ((DoubleShapedVector)weights).getData();
                        }
                        wgt[i] = zero;
                        updatePending = true;
                    }
                }
            }
        }
    }

    private final void cloneData() {
        if (! writableData) {
            data = data.clone();
            writableData = true;
        }
    }

    private final void cloneWeights() {
        if (! writableWeights) {
            weights = weights.clone();
            writableWeights = true;
        }
    }

    private void update() {
        if (data == null) {
            throw noDataHasBeenSpecified();
        }
        int count = 0;
        if (weights == null) {
            weights = dataSpace.create();
            writableWeights = true;
            if (single) {
                final float one = 1;
                final float zero = 0;
                float[] dat = ((FloatShapedVector)data).getData();
                final float[] wgt = ((FloatShapedVector)weights).getData();
                for (int i = 0; i < dat.length; ++i) {
                    if (nonfinite(dat[i])) {
                        if (! writableData) {
                            cloneData();
                            dat = ((FloatShapedVector)data).getData();
                        }
                        dat[i] = zero; // to avoid numerical problems
                        wgt[i] = zero;
                    } else {
                        wgt[i] = one;
                        ++count;
                    }
                }
            } else {
                final double one = 1;
                final double zero = 0;
                double[] dat = ((DoubleShapedVector)data).getData();
                final double[] wgt = ((DoubleShapedVector)weights).getData();
                for (int i = 0; i < dat.length; ++i) {
                    if (nonfinite(dat[i])) {
                        if (! writableData) {
                            cloneData();
                            dat = ((DoubleShapedVector)data).getData();
                        }
                        dat[i] = zero; // to avoid numerical problems
                        wgt[i] = zero;
                    } else {
                        wgt[i] = one;
                        ++count;
                    }
                }
            }
        } else {
            if (single) {
                final float zero = 0;
                float[] dat = ((FloatShapedVector)data).getData();
                final float[] wgt = ((FloatShapedVector)weights).getData();
                for (int i = 0; i < dat.length; ++i) {
                    if (nonfinite(wgt[i]) || wgt[i] < zero) {
                        throw weightsMustBeFiniteAndNonnegative();
                    }
                    if (nonfinite(dat[i])) {
                        if (wgt[i]> zero) {
                            throw nonfiniteDataMustHaveZeroWeight();
                        }
                        if (! writableData) {
                            cloneData();
                            dat = ((FloatShapedVector)data).getData();
                        }
                        dat[i] = zero; // to avoid numerical problems
                    }
                    if (wgt[i]> zero) {
                        ++count;
                    }
                }
            } else {
                final double zero = 0;
                double[] dat = ((DoubleShapedVector)data).getData();
                final double[] wgt = ((DoubleShapedVector)weights).getData();
                for (int i = 0; i < dat.length; ++i) {
                    if (nonfinite(wgt[i]) || wgt[i] < zero) {
                        throw weightsMustBeFiniteAndNonnegative();
                    }
                    if (nonfinite(dat[i])) {
                        if (wgt[i]> zero) {
                            throw nonfiniteDataMustHaveZeroWeight();
                        }
                        if (! writableData) {
                            cloneData();
                            dat = ((DoubleShapedVector)data).getData();
                        }
                        dat[i] = zero; // to avoid numerical problems
                    }
                    if (wgt[i]> zero) {
                        ++count;
                    }
                }
            }
        }
        updatePending = false;
        this.validDataNumber = count;
    }

    /* Utilities */

    private static final boolean nonfinite(float val) {
        return (Float.isInfinite(val) || Float.isNaN(val));
    }

    private static final boolean nonfinite(double val) {
        return (Double.isInfinite(val) || Double.isNaN(val));
    }

    private boolean[] toBoolean(ShapedArray arr) {
        switch (arr.getType()) {
            case Traits.BYTE:
                return toBoolean(((ByteArray)arr).flatten());
            case Traits.SHORT:
                return toBoolean(((ShortArray)arr).flatten());
            case Traits.INT:
                return toBoolean(((IntArray)arr).flatten());
            case Traits.LONG:
                return toBoolean(((LongArray)arr).flatten());
            case Traits.FLOAT:
                return toBoolean(((FloatArray)arr).flatten());
            case Traits.DOUBLE:
                return toBoolean(((DoubleArray)arr).flatten());
            default:
                throw unsupportedDataType();
        }
    }

    private boolean[] toBoolean(ShapedVector vec) {
        switch (vec.getType()) {
            case Traits.FLOAT:
                return toBoolean(((FloatShapedVector)vec).getData());
            case Traits.DOUBLE:
                return toBoolean(((DoubleShapedVector)vec).getData());
            default:
                throw unsupportedDataType();
        }
    }

    private static final boolean[] toBoolean(byte[] arr)
    {
        final byte zero = 0;
        final boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            res[i] = (arr[i] != zero);
        }
        return res;
    }

    private static final boolean[] toBoolean(short[] arr)
    {
        final short zero = 0;
        final boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            res[i] = (arr[i] != zero);
        }
        return res;
    }

    private static final boolean[] toBoolean(int[] arr)
    {
        final int zero = 0;
        final boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            res[i] = (arr[i] != zero);
        }
        return res;
    }

    private static final boolean[] toBoolean(long[] arr)
    {
        final long zero = 0;
        final boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            res[i] = (arr[i] != zero);
        }
        return res;
    }

    private static final boolean[] toBoolean(float[] arr)
    {
        final float zero = 0;
        final boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            res[i] = (arr[i] != zero);
        }
        return res;
    }

    private static final boolean[] toBoolean(double[] arr)
    {
        final double zero = 0;
        final boolean[] res = new boolean[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            res[i] = (arr[i] != zero);
        }
        return res;
    }

    /* Implement `differentiableCostFunction` interface. */

    @Override
    public VectorSpace getInputSpace() {
        return dataSpace;
    }

    @Override
    public double evaluate(double alpha, Vector vx) {
        /* Check arguments. */
        dataSpace.check(vx);

        /* Shortcut? */
        if (alpha == 0.0) {
            return 0.0;
        }

        /* Compute the cost. */
        double sum = 0.0;
        if (single){
            final float[] w = ((FloatShapedVector)weights).getData();
            final float[] x = ((FloatShapedVector)vx).getData();
            final float[] y = ((FloatShapedVector)data).getData();
            for (int i = 0; i < y.length; ++i) {
                float r = x[i] - y[i];
                sum += r*w[i]*r;
            }
        } else {
            final double[] w = ((DoubleShapedVector)weights).getData();
            final double[] x = ((DoubleShapedVector)vx).getData();
            final double[] y = ((DoubleShapedVector)data).getData();
            for (int i = 0; i < y.length; ++i) {
                double r = x[i] - y[i];
                sum += r*w[i]*r;
            }
        }
        return (alpha/2)*sum;
    }

    @Override
    public double computeCostAndGradient(double alpha, Vector vx, Vector vg, boolean clr) {
        /* Check arguments. */
        dataSpace.check(vx);
        dataSpace.check(vg);

        /* Shortcut? */
        if (alpha == 0.0) {
            if (clr) {
                vg.zero();
            }
            return 0.0;
        }

        /* Compute the cost and the gradient. */
        double sum = 0.0;
        if (single){
            final float q = (float)alpha;
            final float[] g = ((FloatShapedVector)vg).getData();
            final float[] w = ((FloatShapedVector)weights).getData();
            final float[] x = ((FloatShapedVector)vx).getData();
            final float[] y = ((FloatShapedVector)data).getData();
            if (clr) {
                for (int i = 0; i < y.length; ++i) {
                    float r = x[i] - y[i];
                    float wr = w[i]*r;
                    sum += r*wr;
                    g[i] = q*wr;
                }
            } else {
                for (int i = 0; i < y.length; ++i) {
                    float r = x[i] - y[i];
                    float wr = w[i]*r;
                    sum += r*wr;
                    g[i] += q*wr;
                }
            }
        } else {
            final double q = alpha;
            final double[] g = ((DoubleShapedVector)vg).getData();
            final double[] w = ((DoubleShapedVector)weights).getData();
            final double[] x = ((DoubleShapedVector)vx).getData();
            final double[] y = ((DoubleShapedVector)data).getData();
            if (clr) {
                for (int i = 0; i < y.length; ++i) {
                    double r = x[i] - y[i];
                    double wr = w[i]*r;
                    sum += r*wr;
                    g[i] = q*wr;
                }
            } else {
                for (int i = 0; i < y.length; ++i) {
                    double r = x[i] - y[i];
                    double wr = w[i]*r;
                    sum += r*wr;
                    g[i] += q*wr;
                }
            }
        }
        return (alpha/2)*sum;
    }

    private static final NonConformableArrayException nonconformableData() {
        return new NonConformableArrayException("Data array has non conformable dimensions");
    }

    private static final IllegalArgumentException badMaskShape() {
        return new IllegalArgumentException("Mask of bad data must have the same shape as the data");
    }

    private static final IllegalArgumentException badMaskLength() {
        return new IllegalArgumentException("Mask of bad data must have the same length as the data");
    }

    private static final IllegalArgumentException noDataHasBeenSpecified() {
        return new IllegalArgumentException("Data has not yet been specified");
    }

    private static final IllegalArgumentException dataCanOnlyBeSpecifiedOnce() {
        return new IllegalArgumentException("Data can only be specified once");
    }

    private static final IllegalArgumentException weightsCanOnlyBeSpecifiedOnce() {
        return new IllegalArgumentException("Weights can only be specified or computed once");
    }

    private static final IllegalArgumentException nonfiniteDataMustHaveZeroWeight() {
        return new IllegalArgumentException("Non-finite data must have zero weight");
    }

    private static final IllegalArgumentException weightsMustBeFiniteAndNonnegative() {
        return new IllegalArgumentException("Weights must be finite and nonnegative");
    }

    private static final IllegalArgumentException unsupportedDataType() {
        return new IllegalArgumentException("Unsupported data type");
    }

    private static final IllegalArgumentException mustBeFloatingPoint() {
        return new IllegalArgumentException("Weighted data type must be 'float' or 'double'");
    }

}
