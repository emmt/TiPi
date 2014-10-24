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


public class FormatOptions {
    private double minValue = 0.0;
    private boolean minValueGiven = false;

    private double maxValue = 0.0;
    private boolean maxValueGiven = false;

    private ColorModel colorModel = null;

    public FormatOptions() {
    }

    public double getMinValue() {
        return (minValueGiven ? minValue : Double.NaN);
    }

    public void setMinValue(double value) {
        minValue = value;
        minValueGiven = true;
    }

    public void unsetMinValue() {
        minValue = Double.NaN;
        minValueGiven = false;
    }

    public double getMaxValue() {
        return (maxValueGiven ? maxValue : Double.NaN);
    }

    public void setMaxValue(double value) {
        maxValue = value;
        maxValueGiven = true;
    }

    public void unsetMaxValue() {
        maxValue = Double.NaN;
        maxValueGiven = false;
    }

    /**
     * Get the chosen color model.
     * @return The color model or {@code null} if not set.
     */
    public ColorModel getColorModel() {
        return colorModel;
    }

    public double[] getScaling(double arr[], double fileMin, double fileMax) {
        return getScaling(arr, 0, arr.length, fileMin, fileMax);
    }

    public double[] getScaling(double arr[], int offset, int number,
            double fileMin, double fileMax) {
        if (arr == null || offset < 0 || number <= 0 || offset + number > arr.length) {
            /* Invalid arguments or no elements to consider, silently return neutral
               scaling parameters. */
            return new double[]{1.0, 0.0};
        }
        double dataMin, dataMax;
        if (minValueGiven && maxValueGiven) {
            dataMin = minValue;
            dataMax = maxValue;
        } else if (minValueGiven) {
            dataMin = minValue;
            dataMax = arr[offset];
            for (int i = 1; i < number; ++i){
                double value = arr[offset + i];
                if (value > dataMax) dataMax = value;
            }
        } else if (maxValueGiven) {
            dataMin = arr[offset];
            dataMax = maxValue;
            for (int i = 1; i < number; ++i){
                double value = arr[offset + i];
                if (value < dataMin) dataMin = value;
            }
        } else {
            dataMin = dataMax = arr[offset];
            for (int i = 1; i < number; ++i){
                double value = arr[offset + i];
                if (value > dataMax) dataMax = value;
                if (value < dataMin) dataMin = value;
            }
        }
        return computeScalingFactors(dataMin, dataMax, fileMin, fileMax);
    }

    /*=======================================================================*/
    /* BRIGHTNESS SCALING FACTORS */

    /**
     * Compute scaling factors SCALE and BIAS.
     * <p>
     * Scaling factors {@code SCALE} and {@code BIAS} are used to convert
     * data values into scaled values suitable to be stored in integers.
     * To convert integer value {@code fileValue} into a data value {@code dataValue},
     * the formula is:
     * <pre>
     *     dataValue = SCALE*fileValue + BIAS
     * </pre> the reciprocal formula is:
     * <pre>
     *     fileValue = round((dataValue - BIAS)/SCALE)
     * </pre>
     * @param dataMin - The minimum data value.
     * @param dataMax - The maximum data value.
     * @param fileMin - The minimum file value.
     * @param fileMax - The maximum file value (should be strictly greater than {@code dataMin}).
     * @param result  - An array of 2 doubles to store SCALE and BIAS (in that order).
     */
    public static double[] computeScalingFactors(double dataMin, double dataMax,
            double fileMin, double fileMax) {
        double[] result = new double[2];
        computeScalingFactors(dataMin, dataMax, fileMin, fileMax, result);
        return result;
    }

    /**
     * Compute scaling factors SCALE and BIAS.
     * <p>
     * This function is the same as {@link #computeScalingFactors(double, double, double, double)}.
     * 
     * @param dataMin - The minimum data value.
     * @param dataMax - The maximum data value.
     * @param fileMin - The minimum file value.
     * @param fileMax - The maximum file value (should be strictly greater than {@code dataMin}).
     * @param param   - The array to store the scaling parameters: SCALE and BIAS (in that order).
     */
    public static void computeScalingFactors(double dataMin, double dataMax,
            double fileMin, double fileMax, double[] param) {
        /*
         * We compute the scaling parameters SCALE and BIAS to
         * map the data values to the file values according to the
         * chosen BITPIX.
         *
         * Notation:
         *     y = data (physical) value
         *     x = file value (integer type is assumed in what follows)
         * When reading:
         *     y = SCALE*x + BIAS
         * When writing:
         *     x = round((y - BIAS)/SCALE)
         * where the round() function rounds to the nearest integer:
         *          u - 1/2 <= round(u) < u + 1/2
         *     <==> round(u) - 1/2 < u <= round(u) + 1/2
         *
         * a/ We want the smallest SCALE (in magnitude) such that
         *    XMIN <= x <= XMAX whatever y in [YMIN,YMAX].
         *    Assuming SCALE > 0, this yields:
         *        XMIN <= round(UMIN)
         *        XMAX >= round(UMAX)
         *    with:
         *        UMIN = (YMIN - BIAS)/SCALE
         *        UMAX = (YMAX - BIAS)/SCALE
         *    From the bounds of the round() function, we have:
         *        UMAX - UMIN - 1 < DRU < UMAX - UMIN + 1
         *          DY/SCALE - 1 < DRU < DY/SCALE + 1
         *    with DRU = round(UMAX) - round(UMIN) and DY = YMAX - YMIN.
         *
         * b/ In order to maximize the precision, we want to
         *    minimize SCALE (in magnitude).  This amounts to
         *    maximize DRU = round(UMAX) - round(UMIN).  Since
         *    DRU = round(UMAX) - round(UMIN) <= DX = XMAX - XMIN,
         *    we want to have (if possible) DRU = DX.
         *
         *  Combining a/ and b/ yields:
         *           DY/SCALE - 1 < DX < DY/SCALE + 1
         *      <==> DX - 1 < DY/SCALE < DX + 1
         *  Hence (assuming DX > 0 and SCALE > 0):
         *      DY/(DX + 1) < SCALE < DY/(DX - 1)
         *  Since we want the smallest SCALE, we should choose:
         *      SCALE = (1 - EPSILON)*DY/(DX + 1)
         *  with EPSILON the relative machine precision, but:
         *      SCALE = DY/DX
         *  may be good enough (see below).
         *
         * c/ To determine BIAS, we can center the errors on the
         *    spanned intervals.  We can also compute BIAS such
         *    that a physical value of zero is always preserved
         *    across conversions.  This latter choice will limit
         *    the risk of drifting.  So we take BIAS = k*SCALE
         *    and search k integer such that:
         *        XMIN <= round(UMIN) = round((YMIN - BIAS)/SCALE)
         *                            = round(YMIN/SCALE) - k
         *        XMAX >= round(UMAX) = round((YMAX - BIAS)/SCALE)
         *                            = round(YMAX/SCALE) - k
         *    Thus:
         *        round(YMAX/SCALE) - XMAX <= k
         *        round(YMIN/SCALE) - XMIN >= k
         *    Using the bounds of the round() function yields:
         *        YMAX/SCALE - XMAX - 1/2 <= k
         *        YMIN/SCALE - XMIN + 1/2 > k
         *    there is a unique integer solution if the two bounds
         *    differ by 1, thus we want to have:
         *        YMAX/SCALE - XMAX = YMIN/SCALE - XMIN
         *        ==> SCALE = (YMAX - YMIN)/(XMAX - XMIN) = DY/DX
         *
         * Finally:
         *     SCALE = (YMAX - YMIN)/(XMAX - XMIN) = DY/DX
         *     BIAS = k*SCALE
         *     k = round(YMAX/SCALE) - XMAX
         *       = round(YMIN/SCALE) - XMIN
         *       = round((XMAX*YMIN - XMIN*YMAX)/(YMAX - YMIN))
         *
         * The code below applies these formulae except for the
         * normalization factor introduced to avoid overflows.
         */

        double scale, bias;
        if (dataMin == dataMax || fileMin == fileMax) {
            scale = 1.0;
            bias = (dataMin + dataMax)/2.0;
        } else {
            /* Normalize the input values to avoid overflows. */
            double factor = Math.abs(dataMin);
            factor = Math.max(factor, Math.abs(dataMax));
            factor = Math.max(factor, Math.abs(fileMin));
            factor = Math.max(factor, Math.abs(fileMax));
            if (factor > 0.0 && factor != 1.0) {
                dataMin /= factor;
                dataMax /= factor;
                fileMin /= factor;
                fileMax /= factor;
            } else {
                factor = 1.0;
            }
            scale = (dataMax - dataMin)/(fileMax - fileMin);
            bias = Math.rint((fileMax*dataMin - fileMin*dataMax)/(dataMax - dataMin)*factor)*scale;
        }
        param[0] = scale;
        param[1] = bias;
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
