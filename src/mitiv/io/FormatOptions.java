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

package mitiv.io;

import mitiv.array.ByteArray;
import mitiv.array.DoubleArray;
import mitiv.array.FloatArray;
import mitiv.array.IntArray;
import mitiv.array.LongArray;
import mitiv.array.ShapedArray;
import mitiv.array.ShortArray;
import mitiv.base.Traits;
import mitiv.exception.IllegalByteOrderException;
import mitiv.exception.IllegalTypeException;


/**
 * This class is used to collect options for reading/writing data in different
 * format.
 *
 * @author Éric.
 */
public class FormatOptions {
    private double minValue = 0.0;
    private boolean minValueGiven = false;

    private double maxValue = 0.0;
    private boolean maxValueGiven = false;

    private int type = Traits.VOID;

    private int order = Traits.NATIVE_BYTE_ORDER;

    private ColorModel colorModel = null;

    private DataFormat dataFormat = null;

    private boolean interpolate = false;

    /**
     * Get preferred data type for saving.
     *
     * @return A type identifier like {@link Traits#SHORT}.
     *         The value {@link Traits#VOID} indicates that no
     *         preferred type has been specified.
     */
    public int getType() {
        return type;
    }

    /**
     * Set preferred data type for saving.
     *
     * @param type
     *        A type identifier like {@link Traits#SHORT}.  The value {@link
     *        Traits#VOID} indicates that no preferred type has been specified.
     *
     * @throws IllegalTypeException if {@code type} is invalid.
     */
    public void setType(int type) {
        switch (type) {
        case Traits.VOID:
        case Traits.BYTE:
        case Traits.SHORT:
        case Traits.INT:
        case Traits.LONG:
        case Traits.FLOAT:
        case Traits.DOUBLE:
            this.type = type;
            break;
        default:
            throw new IllegalTypeException();
        }
    }

    /**
     * Get preferred byte order for saving.
     *
     * @return A byte order identifier like {@link Traits#BIG_ENDIAN} or
     *         {@link Traits#LITTLE_ENDIAN}.
     */
    public int getOrder() {
        return order;
    }

    /**
     * Set preferred byte order for saving.
     *
     * @param order
     *        A byte order identifier like {@link Traits#BIG_ENDIAN},
     *        {@link Traits#LITTLE_ENDIAN}, or {@link Traits#NATIVE_BYTE_ORDER}.
     *
     * @throws IllegalByteOrderException if {@code order} is invalid.
     */
    public void setOrder(int order) {
        switch (order) {
        case Traits.BIG_ENDIAN:
        case Traits.LITTLE_ENDIAN:
            this.order = order;
            break;
        default:
            throw new IllegalByteOrderException();
        }
    }


    public FormatOptions() {
    }

    /**
     * Get the specified minimum data value.
     *
     * @return The specified value or NaN if it has not been specified.
     */
    public double getMinValue() {
        return (minValueGiven ? minValue : Double.NaN);
    }

    /**
     * Set the minimum data value.
     *
     * @param value
     *        The new minimum data value.
     */
    public void setMinValue(double value) {
        if (nonfinite(value)) {
            throw new IllegalArgumentException("Minimum data value must be finite");
        }
        minValue = value;
        minValueGiven = true;
    }

    /**
     * Unset the minimum data value.
     *
     * <p> Unset the minimum data value if any has been specified. </p>
     */
    public void unsetMinValue() {
        minValue = Double.NaN;
        minValueGiven = false;
    }

    /**
     * Get the specified maximum data value.
     *
     * @return The specified value or NaN if it has not been specified.
     */
    public double getMaxValue() {
        return (maxValueGiven ? maxValue : Double.NaN);
    }

    /**
     * Set the maximum data value.
     *
     * @param value
     *        The new maximum data value.
     */
    public void setMaxValue(double value) {
        if (nonfinite(value)) {
            throw new IllegalArgumentException("Maximum data value must be finite");
        }
        maxValue = value;
        maxValueGiven = true;
    }

    /**
     * Unset the maximum data value.
     *
     * <p> Unset the maximum data value if any has been specified. </p>
     */
    public void unsetMaxValue() {
        maxValue = Double.NaN;
        maxValueGiven = false;
    }

    /**
     * Get whether extreme data values are exactly represented after
     * digitization.
     *
     * @return A boolean.
     */
    public boolean getInterpolate() {
        return interpolate;
    }

    /**
     * Set whether extreme data values are exactly represented after
     * digitization.
     *
     * @param value
     *        If true, the digitization will exactly interpolate the extreme
     *        data values; otherwise, digitization attempts to preserve
     *        specific data values such as zero.
     */
    public void setInterpolate(boolean value) {
        interpolate = value;
    }

    /**
     * Get the chosen color model.
     *
     * @return The color model or {@code null} if not set.
     */
    public ColorModel getColorModel() {
        return colorModel;
    }

    /**
     * Set the color model.
     *
     * @param value
     *        The color model.
     */
    public void setColorModel(ColorModel value) {
        colorModel = value;
    }

    /**
     * Unset the color model.
     */
    public void unsetColorModel() {
        colorModel = null;
    }


    /**
     * Get the chosen data format.
     *
     * @return The data format or {@code null} if not set.
     */
    public DataFormat getDataFormat() {
        return dataFormat;
    }

    /**
     * Set the data format.
     *
     * @param value
     *        The data format.
     */
    public void setDataFormat(DataFormat value) {
        dataFormat = value;
    }

    /**
     * Unset the data format.
     */
    public void unsetDataFormat() {
        dataFormat = null;
    }

    public static double[] neutralScaling() {
        return new double[]{1.0, 0.0};
    }

    /**
     * Get scaling parameters under user constraints solely.
     *
     * @param arr
     *        The array to save.
     *
     * @return An array of 2 doubles <b>{scale,bias}</b> (in that order).
     */
    public double[] getScaling(ShapedArray arr) {
        if (arr == null || type == Traits.VOID) {
            return neutralScaling();
        }
        switch (type) {
        case Traits.BYTE:
            return getScaling(arr, 0, 255);
        case Traits.SHORT:
            return getScaling(arr, Short.MIN_VALUE, Short.MAX_VALUE);
        case Traits.INT:
            return getScaling(arr, Integer.MIN_VALUE, Integer.MAX_VALUE);
        case Traits.LONG:
            return getScaling(arr, Long.MIN_VALUE, Long.MAX_VALUE);
        case Traits.FLOAT:
        case Traits.DOUBLE:
            return neutralScaling();
        default:
            throw new IllegalTypeException();
        }
    }

    /**
     * Get scaling parameters under user and file constraints.
     *
     * <p> This method determines the best scaling parameters.  No scaling
     * (SCALE = 1, BIAS = 0) is preferred if possible.  </p>
     *
     * @param arr
     *        The array to save.
     *
     * @param kmin
     *        The minimum digitization level.
     *
     * @param kmax
     *        The maximum digitization level.
     *
     * @return An array of 2 doubles <b>{scale,bias}</b> (in that order).
     */
    public double[] getScaling(ShapedArray arr, long kmin, long kmax) {
        if (arr == null) {
            /* Invalid arguments or no elements to consider, silently return
               neutral scaling parameters. */
            return neutralScaling();
        }

        /*
         * No scaling is chosen if the number of digitization levels are
         * sufficient.
         */
        switch (arr.getType()) {
        case Traits.BYTE:
            if (kmin <= 0 && kmax >= 255) {
                return neutralScaling();
            }
            break;
        case Traits.SHORT:
            if (kmin <= Short.MIN_VALUE && kmax >= Short.MAX_VALUE) {
                return neutralScaling();
            }
            break;
        case Traits.INT:
            if (kmin <= Integer.MIN_VALUE && kmax >= Integer.MAX_VALUE) {
                return neutralScaling();
            }
            break;
        case Traits.LONG:
            if (kmin <= Long.MIN_VALUE && kmax >= Long.MAX_VALUE) {
                return neutralScaling();
            }
            break;
        case Traits.FLOAT:
        case Traits.DOUBLE:
            break;
        default:
            throw new IllegalTypeException("Unsupported array data type");
        }

        /* Figure out the minimum and maximum data value. */
        double dataMin, dataMax;
        if (minValueGiven && maxValueGiven) {
            dataMin = minValue;
            dataMax = maxValue;
            //} else {
            //    DataSummary ds = new DataSummary(arr);

        } else if (minValueGiven) {
            dataMin = minValue;
            switch (arr.getType()) {
            case Traits.BYTE:
                dataMax = ((ByteArray)arr).max();
                break;
            case Traits.SHORT:
                dataMax = ((ShortArray)arr).max();
                break;
            case Traits.INT:
                dataMax = ((IntArray)arr).max();
                break;
            case Traits.LONG:
                dataMax = ((LongArray)arr).max();
                break;
            case Traits.FLOAT:
                dataMax = ((FloatArray)arr).max();
                break;
            case Traits.DOUBLE:
                dataMax = ((DoubleArray)arr).max();
                break;
            default:
                throw new IllegalTypeException();
            }
        } else if (maxValueGiven) {
            dataMax = maxValue;
            switch (arr.getType()) {
            case Traits.BYTE:
                dataMin = ((ByteArray)arr).min();
                break;
            case Traits.SHORT:
                dataMin = ((ShortArray)arr).min();
                break;
            case Traits.INT:
                dataMin = ((IntArray)arr).min();
                break;
            case Traits.LONG:
                dataMin = ((LongArray)arr).min();
                break;
            case Traits.FLOAT:
                dataMin = ((FloatArray)arr).min();
                break;
            case Traits.DOUBLE:
                dataMin = ((DoubleArray)arr).min();
                break;
            default:
                throw new IllegalTypeException();
            }
        } else {
            short[] shortResult;
            int[] intResult;
            long[] longResult;
            float[] floatResult;
            double[] doubleResult;
            switch (arr.getType()) {
            case Traits.BYTE:
                /* Bytes are interpreted as unsigned. */
                intResult = ((ByteArray)arr).getMinAndMax();
                dataMin = intResult[0];
                dataMax = intResult[1];
                break;
            case Traits.SHORT:
                shortResult = ((ShortArray)arr).getMinAndMax();
                dataMin = shortResult[0];
                dataMax = shortResult[1];
                break;
            case Traits.INT:
                intResult = ((IntArray)arr).getMinAndMax();
                dataMin = intResult[0];
                dataMax = intResult[1];
                break;
            case Traits.LONG:
                longResult = ((LongArray)arr).getMinAndMax();
                dataMin = longResult[0];
                dataMax = longResult[1];
                break;
            case Traits.FLOAT:
                floatResult = ((FloatArray)arr).getMinAndMax();
                dataMin = floatResult[0];
                dataMax = floatResult[1];
                break;
            case Traits.DOUBLE:
                doubleResult = ((DoubleArray)arr).getMinAndMax();
                dataMin = doubleResult[0];
                dataMax = doubleResult[1];
                break;
            default:
                throw new IllegalTypeException();
            }
        }
        return computeScalingFactors(dataMin, dataMax, kmin, kmax,
                interpolate);
    }

    /*=======================================================================*/
    /* BRIGHTNESS SCALING FACTORS */

    /**
     * Compute scaling factors SCALE and BIAS.
     *
     * <p> Scaling factors {@code SCALE} and {@code BIAS} are used to convert
     * data values into scaled values suitable to be stored in integers.  This
     * conversion is similar to digitization.  To convert integer value {@code
     * fileValue} into a data value {@code dataValue}, the formula is: </p>
     *
     * <pre>
     *     dataValue = SCALE*fileValue + BIAS
     * </pre>
     *
     * <p> Assuming SCALE is not equal to zero, the (approximate, but with
     * least error) reciprocal formula is: </p>
     *
     * <pre>
     *     fileValue = round((dataValue - BIAS)/SCALE)
     * </pre>
     *
     * <p> where {@code round()} rounds its argument to the nearest
     * integer. </p>
     *
     * @param dmin
     *        The minimum data value.
     *
     * @param dmax
     *        The maximum data value.
     *
     * @param kmin
     *        The minimum digitization level.
     *
     * @param kmax
     *        The maximum digitization level (should be strictly greater than
     *        {@code kmin}).
     *
     * @param interp
     *        Compute an exact transform for the bounds; otherwise make BIAS a
     *        multiple of SCALE to preserve specific data values such as zero.
     *
     * @return An array of 2 doubles {SCALE,BIAS} (in that order).
     */
    public static double[] computeScalingFactors(double dmin, double dmax,
            double kmin, double kmax, boolean interp) {
        double[] result = new double[2];
        computeScalingFactors(dmin, dmax, kmin, kmax, interp, result);
        return result;
    }

    /**
     * Compute scaling factors SCALE and BIAS.
     *
     * @param dmin
     *        The minimum data value.
     *
     * @param dmax
     *        The maximum data value.
     *
     * @param kmin
     *        The minimum digitization level.
     *
     * @param kmax
     *        The maximum digitization level (should be strictly greater than
     *        {@code kmin}).
     *
     * @param interp
     *        Compute an exact transform for the bounds; otherwise make BIAS a
     *        multiple of SCALE to preserve specific data values such as zero.
     *
     * @param param
     *        An array to store the scaling parameters: SCALE and BIAS (in that
     *        order).
     *
     * @see #computeScalingFactors(double,double,double,double,boolean)
     */
    public static void computeScalingFactors(double dmin, double dmax,
            double kmin, double kmax, boolean interp, double[] param) {
        /* See `notes/digitization.tex` for explanations.  The code below
         * applies the formulae in this document except for the normalization
         * factor introduced to avoid overflows.
         */
        if (nonfinite(dmin)) {
            throw new IllegalArgumentException("Minimum data value must be finite");
        }
        if (nonfinite(dmax)) {
            throw new IllegalArgumentException("Maximum data value must be finite");
        }
        if (nonfinite(kmin)) {
            throw new IllegalArgumentException("Minimum digitization level must be finite");
        }
        if (nonfinite(kmax)) {
            throw new IllegalArgumentException("Maximum digitization level must be finite");
        }
        double alpha, beta, gamma, big;
        if (dmin == dmax || kmin == kmax) {
            alpha = 0.0;
            beta = (dmin + dmax)/2.0;
        } else {
            alpha = (dmax - dmin)/(kmax - kmin);
            big = Math.abs(dmin);
            big = Math.max(big, Math.abs(dmax));
            big = Math.max(big, Math.abs(kmin));
            big = Math.max(big, Math.abs(kmax));
            if (big != 1.0) {
                /* Normalize the input values to avoid overflows in the
                   computation of the ratio gamma = beta/alpha. */
                dmin /= big;
                dmax /= big;
            }
            gamma = (kmax*dmin - kmin*dmax)/(dmax - dmin);
            beta = (interp ? gamma : Math.rint(gamma))*alpha;
        }
        param[0] = alpha;
        param[1] = beta;
    }

    static private final boolean nonfinite(double val) {
        return Double.isNaN(val) || Double.isInfinite(val);
    }
}
