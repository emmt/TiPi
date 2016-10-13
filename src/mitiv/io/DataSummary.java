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

    public double getTotalValue() {
        return vsum;
    }

    public int getNumberOfFiniteValues() {
        return count;
    }

    public int getNumberOfNaNs() {
        return nans;
    }

    public int getNumberOfPositiveInfinites() {
        return posinfs;
    }

    public int getNumberOfNegativeInfinites() {
        return neginfs;
    }

    /**
     * Compute a summary of the values of a shaped array.
     *
     * @param arr - A shaped array.
     */
    public DataSummary(ShapedArray arr) {
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

    }

    /**
     * Make a string representation of the summary.
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("DataSummary{min = ");
        buf.append(this.vmin);
        buf.append("; max = ");
        buf.append(this.vmax);
        buf.append("; sum = ");
        buf.append(this.vsum);
        buf.append("; count = ");
        buf.append(this.count);
        buf.append("; NaN = ");
        buf.append(this.nans);
        buf.append("; posInf = ");
        buf.append(this.posinfs);
        buf.append("; negInf = ");
        buf.append(this.neginfs);
        buf.append(";}");
        return buf.toString();
    }

    public static void main(String[] args) {
        // Switch to "US" locale to avoid problems with number formats.
        Locale.setDefault(Locale.US);
        Timer timer = new Timer();
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
        timer.start();
        DataSummary sa = new DataSummary(a);
        DataSummary sb = new DataSummary(b);
        DataSummary sc = new DataSummary(c);
        DataSummary sd = new DataSummary(d);
        DataSummary se = new DataSummary(e);
        DataSummary sf = new DataSummary(f);
        System.out.format("summary for A: %s\n", sa.toString());
        System.out.format("summary for B: %s\n", sb.toString());
        System.out.format("summary for C: %s\n", sc.toString());
        System.out.format("summary for D: %s\n", sd.toString());
        System.out.format("summary for E: %s\n", se.toString());
        System.out.format("summary for F: %s\n", sf.toString());
        System.out.format("total time: %.3f µs\n", timer.getElapsedTime()*1E6);
        timer.start();
        new DataSummary(a);
        new DataSummary(b);
        new DataSummary(c);
        new DataSummary(d);
        new DataSummary(e);
        new DataSummary(f);
        System.out.format("total time: %.3f µs\n", timer.getElapsedTime()*1E6);
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

