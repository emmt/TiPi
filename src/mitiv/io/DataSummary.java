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

package mitiv.io;

import java.util.Locale;

import mitiv.array.ArrayFactory;
import mitiv.array.ByteArray;
import mitiv.array.Double2D;
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
import mitiv.utils.Timer;

/**
 * Summarize range of values and contents of an array.
 *
 * @author Éric.
 */
public class DataSummary {
    /** Minimum finite value or NaN. */
    protected double vmin = Double.NaN;

    /** Maximum finite value or NaN. */
    protected double vmax = Double.NaN;

    /** Sum of all finite data values. */
    protected double vsum = Double.NaN;

    /** Number of NaN values. */
    protected int nans = 0;

    /** Number of positive infinite values. */
    protected int posinfs = 0;

    /** Number of negative infinite values. */
    protected int neginfs = 0;

    /** Number of finite values. */
    protected int count = 0;

    /**
     * Get minimum finite value.
     * @return The minimum finite value, may be NaN if there are no finite
     *         values.
     */
    public double getMinimumValue() {
        return vmin;
    }

    /**
     * Get maximum finite value.
     * @return The maximum finite value, may be NaN if there are no finite
     *         values.
     */
    public double getMaximumValue() {
        return vmax;
    }

    /**
     * Get sum of finite values.
     * @return The sum of finite values, may be NaN if there are no finite
     *         values.
     */
    public double getTotalValue() {
        return vsum;
    }

    /**
     * Get number of finite values.
     * @return The number of finite values.
     */
    public int getNumberOfFiniteValues() {
        return count;
    }

    /**
     * Get number of NaN values
     * @return The number of NaN values.
     */
    public int getNumberOfNaNs() {
        return nans;
    }

    /**
     * Get number of positive infinite values
     * @return The number of positive infinite values.
     */
    public int getNumberOfPositiveInfinites() {
        return posinfs;
    }

    /**
     * Get number of negative infinite values
     * @return The number of negative infinite values.
     */
    public int getNumberOfNegativeInfinites() {
        return neginfs;
    }

    /**
     * Create a summary of values.
     */
    public DataSummary() {
    }

    /**
     * Create a summary from the values of a shaped array.
     *
     * @param arr - A shaped array.
     */
    public DataSummary(ShapedArray arr) {
        update(arr);
    }

    /**
     * Create a summary from the values of a shaped vector.
     *
     * @param vec - A shaped vector.
     *
     * @return The object itself after the updating.
     */
    public DataSummary(ShapedVector vec) {
        update(vec);
    }

    /**
     * Reset a summary of values.
     *
     * @return The object itself after the reseting.
     */
    public DataSummary reset() {
        vmin = Double.NaN;
        vmax = Double.NaN;
        vsum = Double.NaN;
        nans = 0;
        posinfs = 0;
        neginfs = 0;
        count = 0;
        return this;
    }

    /**
     * Update the summary with the values of a shaped array.
     *
     * @param arr - A shaped array.
     *
     * @return The object itself after the updating.
     */
    public DataSummary update(ShapedArray arr) {
        if (arr != null) {
            switch(arr.getType()) {
            case Traits.BYTE:
                ((ByteArray)arr).scan(new ByteSummary());
                this.count = arr.getNumber();
                break;
            case Traits.SHORT:
                ((ShortArray)arr).scan(new ShortSummary());
                this.count = arr.getNumber();
                break;
            case Traits.INT:
                ((IntArray)arr).scan(new IntSummary());
                this.count = arr.getNumber();
                break;
            case Traits.LONG:
                ((LongArray)arr).scan(new LongSummary());
                this.count = arr.getNumber();
                break;
            case Traits.FLOAT:
                ((FloatArray)arr).scan(new FloatSummary());
                break;
            case Traits.DOUBLE:
                ((DoubleArray)arr).scan(new DoubleSummary());
                break;
            default:
                throw new IllegalTypeException("Unsupported element type");
            }
        }
        return this;
    }

    /**
     * Update a summary with the values of a shaped vector.
     *
     * @param vec - A shaped array.
     *
     * @return The object itself after the updating.
     */
    public DataSummary update(ShapedVector vec) {
        return update(ArrayFactory.wrap(vec));
    }

    /**
     * (Re)compute a summary from the values of a shaped array.
     *
     * @param arr - A shaped array.
     *
     * @return The object itself after the updating.
     */
    public DataSummary compute(ShapedArray arr) {
        return reset().update(arr);
    }

    /**
     * (Re)compute a summary from the values of a shaped vector.
     *
     * @param vec - A shaped vector.
     *
     * @return The object itself after the updating.
     */
    public DataSummary compute(ShapedVector vec) {
        return reset().update(vec);
    }

    /**
     * Make a string representation of the summary.
     */
    @Override
    public String toString() {
        return String.format("DataSummary{min = %g; max = %g; sum = %g; count = %d; NaN = %d; -Inf = %d; +Inf = %d;}",
                this.vmin, this.vmax, this.vsum, this.count,
                this.nans, this.neginfs, this.posinfs);
    }

    /**
     * Test and demonstrator for this class.
     *
     * <p>
     * After warm-up and compilation, using the scanner is only 10% slower than
     * directly accessing the elements of the array.
     * </p>
     * @param args - Unused.
     */
    public static void main(String[] args) {
        // Switch to "US" locale to avoid problems with number formats.
        Locale.setDefault(Locale.US);
        Timer timer = new Timer();
        double elapsed;
        ShapedArray a = ArrayFactory.create(Traits.BYTE, 20, 45);
        ShapedArray b = ArrayFactory.create(Traits.SHORT, 12, 70);
        ShapedArray c = ArrayFactory.create(Traits.INT, 7, 8);
        ShapedArray d = ArrayFactory.create(Traits.LONG, 7, 3);
        ShapedArray e = ArrayFactory.create(Traits.FLOAT, 3, 7);
        ShapedArray f = ArrayFactory.create(Traits.DOUBLE, 3000, 700);
        ((ByteArray)a).fill((byte)5);
        ((ShortArray)b).fill((short)7);
        ((IntArray)c).fill(1);
        ((LongArray)d).fill(2);
        ((FloatArray)e).fill(2);
        ((DoubleArray)f).fill(1.2);
        Double2D F = (Double2D)f;
        F.set(0, 0, Double.NaN);
        F.set(2, 5, Double.POSITIVE_INFINITY);
        F.set(1, 3, Double.POSITIVE_INFINITY);
        F.set(1, 6, Double.NEGATIVE_INFINITY);
        DataSummary s = new DataSummary();
        System.out.format("summary for A: %s\n", s.compute(a).toString());
        System.out.format("summary for B: %s\n", s.compute(b).toString());
        System.out.format("summary for C: %s\n", s.compute(b).toString());
        System.out.format("summary for D: %s\n", s.compute(b).toString());
        System.out.format("summary for E: %s\n", s.compute(b).toString());
        for (int pass = 1; pass <= 10; ++pass) {
            timer.start();
            DataSummary sf = new DataSummary(f);
            elapsed = timer.getElapsedTime()*1E6;
            if (pass == 1) {
                System.out.format("summary for F: %s\n", sf.toString());
            }
            System.out.format("total time: %.3f µs (scanner, pass %d)\n", elapsed, pass);
        }
        double[] arr = F.flatten();
        for (int pass = 1; pass <= 10; ++pass) {
            int nans = 0, neginfs = 0, posinfs = 0, count = 0;
            double vmin = Double.NaN, vmax = Double.NaN, vsum = Double.NaN;
            timer.start();
            for (int i = 0; i < arr.length; ++i) {
                double val = arr[i];
                if (Double.isNaN(val)) {
                    ++nans;
                } else if (Double.isInfinite(val)) {
                    if (val > 0) {
                        ++posinfs;
                    } else {
                        ++neginfs;
                    }
                } else {
                    if (++count == 1) {
                        vsum = val;
                        vmin = val;
                        vmax = val;
                    } else {
                        vsum += val;
                        if (val < vmin) {
                            vmin = val;
                        }
                        if (val > vmax) {
                            vmax = val;
                        }
                    }
                }
            }
            elapsed = timer.getElapsedTime()*1E6;
            s.nans = nans;
            s.count = count;
            s.posinfs = posinfs;
            s.neginfs = neginfs;
            s.vmin = vmin;
            s.vmax = vmax;
            s.vsum = vsum;
            //System.out.format("summary for F: %s\n", s.toString());
            System.out.format("total time: %.3f µs (pass %d)\n", elapsed, pass);
        }
    }

    private class ByteSummary implements ByteScanner {

        @Override
        public void initialize(byte arg) {
            double val = arg;
            vsum = val;
            vmin = val;
            vmax = val;
        }

        @Override
        public void update(byte arg) {
            double val = arg;
            vsum += val;
            if (val < vmin) {
                vmin = val;
            }
            if (val > vmax) {
                vmax = val;
            }
        }
    }

    private class ShortSummary implements ShortScanner {

        @Override
        public void initialize(short arg) {
            double val = arg;
            vsum = val;
            vmin = val;
            vmax = val;
        }

        @Override
        public void update(short arg) {
            double val = arg;
            vsum += val;
            if (val < vmin) {
                vmin = val;
            }
            if (val > vmax) {
                vmax = val;
            }
        }
    }

    private class IntSummary implements IntScanner {

        @Override
        public void initialize(int arg) {
            double val = arg;
            vsum = val;
            vmin = val;
            vmax = val;
        }

        @Override
        public void update(int arg) {
            double val = arg;
            vsum += val;
            if (val < vmin) {
                vmin = val;
            }
            if (val > vmax) {
                vmax = val;
            }
        }
    }

    private class LongSummary implements LongScanner {

        @Override
        public void initialize(long arg) {
            double val = arg;
            vsum = val;
            vmin = val;
            vmax = val;
        }

        @Override
        public void update(long arg) {
            double val = arg;
            vsum += val;
            if (val < vmin) {
                vmin = val;
            }
            if (val > vmax) {
                vmax = val;
            }
        }
    }

    private class FloatSummary implements FloatScanner {

        @Override
        public void initialize(float arg) {
            update(arg);
        }

        @Override
        public void update(float arg) {
            if (Float.isNaN(arg)) {
                ++nans;
            } else if (Float.isInfinite(arg)) {
                if (arg > 0) {
                    ++posinfs;
                } else {
                    ++neginfs;
                }
            } else {
                double val = arg;
                if (++count == 1) {
                    vsum = val;
                    vmin = val;
                    vmax = val;
                } else {
                    vsum += val;
                    if (val < vmin) {
                        vmin = val;
                    }
                    if (val > vmax) {
                        vmax = val;
                    }
                }
            }
        }
    }

    private class DoubleSummary implements DoubleScanner {

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
                if (++count == 1) {
                    vsum = val;
                    vmin = val;
                    vmax = val;
                } else {
                    vsum += val;
                    if (val < vmin) {
                        vmin = val;
                    }
                    if (val > vmax) {
                        vmax = val;
                    }
                }
            }
        }
    }
}

