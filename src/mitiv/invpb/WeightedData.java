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

package mitiv.invpb;

import mitiv.array.ByteArray;
import mitiv.array.DoubleArray;
import mitiv.array.FloatArray;
import mitiv.array.IntArray;
import mitiv.array.LongArray;
import mitiv.array.ShapedArray;
import mitiv.array.ShortArray;
import mitiv.base.Traits;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

public class WeightedData {

    protected final ShapedVectorSpace dataSpace;
    private final boolean single;
    private ShapedVector data = null;
    private ShapedVector weight = null;
    private boolean updatePending = true;
    private boolean writableData = false;
    private boolean writableWeight = false;

    public WeightedData(ShapedVectorSpace space) {
        switch (space.getType()) {
        case Traits.FLOAT:
            single = true;
            break;
        case Traits.DOUBLE:
            single = false;
            break;
        default:
            throw new IllegalArgumentException("Unsupported data type");
        }
        this.dataSpace = space;
    }

    public WeightedData(ShapedVector data, boolean writable) {
        this(data.getSpace());
        setData(data, writable);
    }

    public WeightedData(ShapedVector data) {
        this(data.getSpace());
        setData(data);
    }

    public WeightedData(ShapedVector data, boolean writableData, ShapedVector weight, boolean writableWeight) {
        this(data.getSpace());
        setData(data, writableData);
        setWeight(weight, writableWeight);
    }

    public WeightedData(ShapedVector data, ShapedVector weight) {
        this(data.getSpace());
        setData(data);
        setWeight(weight);
    }

    public final boolean singlePrecision() {
        return this.single;
    }

    public final ShapedVectorSpace getDataSpace() {
        return this.dataSpace;
    }

    public final ShapedVector getData() {
        if (this.updatePending) {
            update();
        }
        return this.data;
    }

    public final ShapedVector getWeight() {
        if (this.updatePending) {
            update();
        }
        return this.weight;
    }

    public void setData(ShapedVector data) {
        setData(data, false);
    }

    public void setData(ShapedVector data, boolean writable) {
        data.assertBelongsTo(dataSpace);
        if (this.data != null) {
            throw new IllegalArgumentException("Data can only be set once");
        }
        this.data = data;
        this.writableData = writable;
        this.updatePending = true;
    }

    public void setWeight(ShapedVector weight) {
        setWeight(weight, false);
    }

    public void setWeight(ShapedVector weight, boolean writable) {
        weight.assertBelongsTo(dataSpace);
        if (this.weight != null) {
            throw new IllegalArgumentException("Weights can only be set once");
        }
        this.weight = weight;
        this.writableWeight = writable;
        this.updatePending = true;
    }

    public void computeWeightsFromData(double alpha, double beta) {
        if (data == null) {
            throw new IllegalArgumentException("No data has been set");
        }

    }

    public void computeWeightsFromVariance(ShapedVector var) {
    }

    /**
     * Remove bad data.
     *
     * <p>
     * This method mark bad data by setting their weight to zero.  If no weight has been set
     * the weight of good data will be arbitrarily set to one.
     *
     * @param bad - The mask of bad data.  It must have the same shape as the data and
     *              be true (i.e. non-zero) where data must be considered as invalid.
     */
    public void markBadData(ShapedVector bad) {
        if (! bad.getShape().equals(dataSpace.getShape())) {
            throw new IllegalArgumentException("Mask of bad data must have the same shape as the data");
        }
        markBadData(toBoolean(bad));
    }

    public void markBadData(ShapedArray bad) {
        if (! bad.getShape().equals(dataSpace.getShape())) {
            throw new IllegalArgumentException("Mask of bad data must have same the shape as the data");
        }
        markBadData(toBoolean(bad));
    }

    public final void markBadData(boolean[] bad) {
        if (data == null) {
            throw new IllegalArgumentException("No data has been set");
        }
        final int len = dataSpace.getNumber();
        if (bad.length != len) {
            throw new IllegalArgumentException("Mask of bad data must have the same length as the data");
        }
        if (weight == null) {
            weight = dataSpace.create();
            writableWeight = true;
            if (single) {
                final float one = 1;
                final float zero = 0;
                final float[] wgt = ((FloatShapedVector)weight).getData();
                for (int i = 0; i < len; ++i) {
                    wgt[i] = (bad[i] ? zero : one);
                }
            } else {
                final double one = 1;
                final double zero = 0;
                final double[] wgt = ((DoubleShapedVector)weight).getData();
                for (int i = 0; i < len; ++i) {
                    wgt[i] = (bad[i] ? zero : one);
                }
            }
            updatePending = true;
        } else {
            if (single) {
                final float zero = 0;
                float[] wgt = ((FloatShapedVector)weight).getData();
                for (int i = 0; i < len; ++i) {
                    if (bad[i] && wgt[i] != zero) {
                        if (! writableWeight) {
                            cloneWeight();
                            wgt = ((FloatShapedVector)weight).getData();
                        }
                        wgt[i] = zero;
                        updatePending = true;
                    }
                }
            } else {
                final double zero = 0;
                double[] wgt = ((DoubleShapedVector)weight).getData();
                for (int i = 0; i < len; ++i) {
                    if (bad[i] && wgt[i] != zero) {
                        if (! writableWeight) {
                            cloneWeight();
                            wgt = ((DoubleShapedVector)weight).getData();
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

    private final void cloneWeight() {
        if (! writableWeight) {
            weight = weight.clone();
            writableWeight = true;
        }
    }

    private void update() {
        if (data == null) {
            throw new IllegalArgumentException("No data has been set");
        }
        if (weight == null) {
            weight = dataSpace.create();
            writableWeight = true;
            if (single) {
                final float one = 1;
                final float zero = 0;
                float[] dat = ((FloatShapedVector)data).getData();
                final float[] wgt = ((FloatShapedVector)weight).getData();
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
                    }
                }
            } else {
                final double one = 1;
                final double zero = 0;
                double[] dat = ((DoubleShapedVector)data).getData();
                final double[] wgt = ((DoubleShapedVector)weight).getData();
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
                    }
                }
            }
        } else {
            if (single) {
                final float zero = 0;
                float[] dat = ((FloatShapedVector)data).getData();
                final float[] wgt = ((FloatShapedVector)weight).getData();
                for (int i = 0; i < dat.length; ++i) {
                    if (nonfinite(wgt[i]) || wgt[i] < zero) {
                        throw new IllegalArgumentException("Weights must be finite and nonnegative");
                    }
                    if (nonfinite(dat[i])) {
                        if (wgt[i]> zero) {
                            throw new IllegalArgumentException("Non-finite data must have zero weight");
                        }
                        if (! writableData) {
                            cloneData();
                            dat = ((FloatShapedVector)data).getData();
                        }
                        dat[i] = zero; // to avoid numerical problems
                    }
                }
            } else {
                final double zero = 0;
                double[] dat = ((DoubleShapedVector)data).getData();
                final double[] wgt = ((DoubleShapedVector)weight).getData();
                for (int i = 0; i < dat.length; ++i) {
                    if (nonfinite(wgt[i]) || wgt[i] < zero) {
                        throw new IllegalArgumentException("Weights must be finite and nonnegative");
                    }
                    if (nonfinite(dat[i])) {
                        if (wgt[i]> zero) {
                            throw new IllegalArgumentException("Non-finite data must have zero weight");
                        }
                        if (! writableData) {
                            cloneData();
                            dat = ((DoubleShapedVector)data).getData();
                        }
                        dat[i] = zero; // to avoid numerical problems
                    }
                }
            }
        }
        updatePending = false;
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
            throw new IllegalArgumentException("Unsupported data type");
        }
    }

    private boolean[] toBoolean(ShapedVector vec) {
        switch (vec.getType()) {
        case Traits.FLOAT:
            return toBoolean(((FloatShapedVector)vec).getData());
        case Traits.DOUBLE:
            return toBoolean(((DoubleShapedVector)vec).getData());
        default:
            throw new IllegalArgumentException("Unsupported data type");
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

}
