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

package mitiv.utils;

import java.util.stream.IntStream;

import mitiv.array.ArrayFactory;
import mitiv.array.ByteArray;
import mitiv.array.DoubleArray;
import mitiv.array.FloatArray;
import mitiv.array.IntArray;
import mitiv.array.LongArray;
import mitiv.array.ShapedArray;
import mitiv.array.ShortArray;
import mitiv.base.Traits;
import mitiv.base.mapping.ByteScanner;
import mitiv.base.mapping.DoubleScanner;
import mitiv.base.mapping.FloatScanner;
import mitiv.base.mapping.IntScanner;
import mitiv.base.mapping.LongScanner;
import mitiv.base.mapping.ShortScanner;
import mitiv.exception.IllegalTypeException;
import mitiv.linalg.shaped.ShapedVector;

/**
 * Compute histogram of an array
 *
 * @author Ferréol.
 */
public class Histogram {
    /** Minimum finite value or NaN. */
    protected double vmin = Double.NaN;

    /** Maximum finite value or NaN. */
    protected double vmax = Double.NaN;

    /** Sum of all finite data values. */
    protected int[] histo = null;

    /** Number of NaN values. */
    protected int nans = 0;

    /** Number of positive infinite values. */
    protected int posinfs = 0;

    /** Number of negative infinite values. */
    protected int neginfs = 0;

    /** Number of finite values. */
    protected int[] count = null;

    /** Number of bin. */
    protected int nbin = 1;

    /**
     * Get minimum finite value.
     *
     * @return The minimum finite value, may be NaN if there are no finite
     *         values.
     */
    public double getMinimumValue() {
        return vmin;
    }

    /**
     * Get maximum finite value.
     *
     * @return The maximum finite value, may be NaN if there are no finite
     *         values.
     */
    public double getMaximumValue() {
        return vmax;
    }


    /**
     * Get number of NaN values.
     *
     * @return The number of NaN values.
     */
    public int getNumberOfNaNs() {
        return nans;
    }

    /**
     * Get number of positive infinite values.
     *
     * @return The number of positive infinite values.
     */
    public int getNumberOfPositiveInfinites() {
        return posinfs;
    }

    /**
     * Get number of negative infinite values.
     *
     * @return The number of negative infinite values.
     */
    public int getNumberOfBins() {
        return nbin;
    }
    /**
     * Create an histogram of values.
     */
    public Histogram() {
    }

    /**
     * Create an histogram from the values of a shaped array.
     *
     * @param arr
     *        A shaped array.
     */
    public Histogram(ShapedArray arr) {
        update(arr);
    }

    /**
     * Create an histogram from the values of a shaped vector.
     *
     * @param vec
     *        A shaped vector.
     */
    public Histogram(ShapedVector vec) {
        update(vec);
    }

    /**
     * Reset a summary of values.
     *
     */
    public Histogram reset() {
        vmin = Double.NaN;
        vmax = Double.NaN;
        histo = null;
        nans = 0;
        posinfs = 0;
        neginfs = 0;
        nbin =1;
        count = null;
        return this;
    }

    /**
     * Update the summary with the values of a shaped array.
     *
     * @param arr - A shaped array.
     *
     * @return The object itself after the updating.
     */
    public Histogram update(ShapedArray arr) {
        if (arr != null) {
            double tmpmin, tmpmax;
            switch(arr.getType()) {
                case Traits.BYTE:
                {
                    int [] mm = ((ByteArray)arr).getMinAndMax();
                    tmpmin = mm[0];
                    tmpmax = mm[1];
                }
                break;
                case Traits.SHORT:
                {
                    short[] mm = ((ShortArray)arr).getMinAndMax();
                    tmpmin = mm[0];
                    tmpmax = mm[1];
                }
                break;
                case Traits.INT:
                {
                    int[] mm = ((IntArray)arr).getMinAndMax();
                    tmpmin = mm[0];
                    tmpmax = mm[1];
                }
                break;
                case Traits.LONG:
                {
                    long[] mm = ((LongArray)arr).getMinAndMax();
                    tmpmin = mm[0];
                    tmpmax = mm[1];
                }
                break;
                case Traits.FLOAT:
                {
                    float[] mm = ((FloatArray)arr).getMinAndMax();
                    tmpmin = mm[0];
                    tmpmax = mm[1];
                }
                break;
                case Traits.DOUBLE:
                {
                    double[] mm = ((DoubleArray)arr).getMinAndMax();
                    tmpmin = mm[0];
                    tmpmax = mm[1];
                }
                break;
                default:
                    throw new IllegalTypeException("Unsupported element type");
            }

            updateSize(tmpmin,tmpmax);
            if (arr != null) {
                switch(arr.getType()) {
                    case Traits.BYTE:
                        ((ByteArray)arr).scan(new ByteHistogram());
                        break;
                    case Traits.SHORT:
                        ((ShortArray)arr).scan(new ShortHistogram());
                        break;
                    case Traits.INT:
                        ((IntArray)arr).scan(new IntHistogram());
                        break;
                    case Traits.LONG:
                        ((LongArray)arr).scan(new LongHistogram());
                        break;
                    case Traits.FLOAT:
                        ((FloatArray)arr).scan(new FloatHistogram());
                        break;
                    case Traits.DOUBLE:
                        ((DoubleArray)arr).scan(new DoubleHistogram());
                        break;
                    default:
                        throw new IllegalTypeException("Unsupported element type");

                }
            }
        }


        return this;
    }

    /**
     * @param tmpmin
     * @param tmpmax
     */
    private void updateSize(double tmpmin, double tmpmax) {
        boolean updte=false;
        int[] oldcount;
        if (Double.isNaN(vmin) || vmin > tmpmin ) {
            vmin = tmpmin;
            updte=true;
        }

        if (Double.isNaN(vmax) || vmax < tmpmax ) {
            vmax = tmpmax;
            updte=true;
        }

        if(updte) {
            nbin = (int) (Math.ceil(vmax) - Math.floor(vmin))+1;
            if (count==null || histo==null) {
                count = new int[nbin];
                histo = IntStream.rangeClosed((int)Math.floor(vmin), (int)Math.ceil(vmax)).toArray();
            }else {
                oldcount = count.clone();
                int first = (int)Math.floor(vmin) - histo[0] ;
                count = new int[nbin];
                histo = IntStream.rangeClosed((int)Math.floor(vmin), (int)Math.ceil(vmax)).toArray();
                for (int i = 0; i < oldcount.length; i++) {
                    count[first + i] = oldcount[i];
                }
            }

        }

    }

    /**
     * Update a summary with the values of a shaped vector.
     *
     * @param vec - A shaped array.
     *
     * @return The object itself after the updating.
     */
    public Histogram update(ShapedVector vec) {
        return update(ArrayFactory.wrap(vec));
    }

    /**
     * (Re)compute a summary from the values of a shaped array.
     *
     * @param arr - A shaped array.
     *
     * @return The object itself after the updating.
     */
    public Histogram compute(ShapedArray arr) {
        return reset().update(arr);
    }

    /**
     * (Re)compute a summary from the values of a shaped vector.
     *
     * @param vec - A shaped vector.
     *
     * @return The object itself after the updating.
     */
    public Histogram compute(ShapedVector vec) {
        return reset().update(vec);
    }

    public void Show() {
        System.out.format(" Histogram :\n");
        if (count !=null) {
            for (int j = 0; j < count.length; j++) {
                System.out.format("  %d \t %d \n", histo[j], count[j]);
            }
        }
    }

    private class ByteHistogram implements ByteScanner {

        @Override
        public void initialize(byte arg) {
            update(arg);
        }


        @Override
        public void update(byte val) {
            int idx = (int)Math.floor(val) - histo[0];
            count[idx]++;
        }

    }

    private class ShortHistogram implements ShortScanner {

        @Override
        public void initialize(short arg) {
            update(arg);
        }


        @Override
        public void update(short val) {
            int idx = (int)Math.floor(val) - histo[0];
            count[idx]++;
        }

    }

    private class IntHistogram implements IntScanner {

        @Override
        public void initialize(int arg) {
            update(arg);
        }


        @Override
        public void update(int val) {
            int idx = (int)Math.floor(val) - histo[0];
            count[idx]++;
        }

    }


    private class LongHistogram implements LongScanner {

        @Override
        public void initialize(long arg) {
            update(arg);
        }


        @Override
        public void update(long val) {
            int idx = (int)Math.floor(val) - histo[0];
            count[idx]++;
        }

    }



    private class FloatHistogram implements FloatScanner {

        @Override
        public void initialize(float arg) {
            update(arg);
        }


        @Override
        public void update(float val) {
            if (Float.isNaN(val)) {
                ++nans;
            } else if (Float.isInfinite(val)) {
                if (val > 0) {
                    ++posinfs;
                } else {
                    ++neginfs;
                }
            } else {
                int idx = (int)Math.floor(val) - histo[0];
                count[idx]++;
            }
        }

    }


    private class DoubleHistogram implements DoubleScanner {

        @Override
        public void initialize(double arg) {
            update(arg);
        }


        @Override
        public void update(double val) {
            if (Double.isNaN(val)) {
                ++nans;
            } else if (Double.isInfinite(val)) {
                if (val > 0) {
                    ++posinfs;
                } else {
                    ++neginfs;
                }
            } else {
                int idx = (int)Math.floor(val) - histo[0];
                count[idx]++;
            }
        }

    }
}


