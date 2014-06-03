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

package mitiv.linalg;

import mitiv.exception.NonconformingArrayException;


/**
 * Implementation of optimized array operations.
 *
 * The main purpose of this class is to collect basic vectorized operations
 * (static methods) which are useful elsewhere.
 *
 * TODO: The code for most static methods should be automatically written from
 *       template code (easier maintenance and less bugs).
 * 
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */
public class ArrayOps {
    /**
     * Non instanciable class of static routines for linear algebra.
     */

    protected ArrayOps() {
    }

    public static void main(String[] args) {
        int n = 200;
        double[] a = new double[n];
        double[] b = new double[n];
        double[] c = new double[n];
        for (int i = 0; i < n; ++i) {
            a[i] = 3.0 * i - 10.0;
            b[i] = 50.0 - 7.0 * i;
            c[i] = 4.0 * i - 110.0;
        }
        try {
            System.out.println("dot(a,b) = " + dot(a, b));
            System.out.println("dot(a,b,c) = " + dot(a, b, c));
        } catch (Exception e) {
            System.err.println("error: " + e);
        }
    }

    /*-----------------------------------------------------------------------*/
    /* GET (AND CHECK) DIMENSIONS */

    public static final int getLength(final byte[] x) {
        return (x == null ? 0 : x.length);
    }

    public static final int getLength(final short[] x) {
        return (x == null ? 0 : x.length);
    }

    public static final int getLength(final int[] x) {
        return (x == null ? 0 : x.length);
    }

    public static final int getLength(final long[] x) {
        return (x == null ? 0 : x.length);
    }

    public static final int getLength(final float[] x) {
        return (x == null ? 0 : x.length);
    }

    public static final int getLength(final double[] x) {
        return (x == null ? 0 : x.length);
    }

    public static final int getNonZeroLength(final float[] x) {
        if (x == null) {
            throw new IllegalArgumentException("Illegal NULL array.");
        }
        int n = x.length;
        if (n == 0) {
            throw new IllegalArgumentException("Illegal zero-length array.");
        }
        return n;
    }

    public static final int getNonZeroLength(final double[] x) {
        if (x == null) {
            throw new IllegalArgumentException("Illegal NULL array.");
        }
        int n = x.length;
        if (n == 0) {
            throw new IllegalArgumentException("Illegal zero-length array.");
        }
        return n;
    }

    public static final int getLength(final float[] x, final float[] y) {
        int n = getLength(x);
        if (getLength(y) != n) {
            throw new NonconformingArrayException(1);
        }
        return n;
    }

    public static final int getLength(final double[] x, final double[] y) {
        int n = getLength(x);
        if (getLength(y) != n) {
            throw new NonconformingArrayException(1);
        }
        return n;
    }

    public static final int getLength(final float[] w, final float[] x,
            final float[] y) {
        int n = getLength(w);
        if (getLength(x) != n || getLength(y) != n) {
            throw new NonconformingArrayException(1);
        }
        return n;
    }

    public static final int getLength(final double[] w, final double[] x,
            final double[] y) {
        int n = getLength(w);
        if (getLength(x) != n || getLength(y) != n) {
            throw new NonconformingArrayException(1);
        }
        return n;
    }

    public static final int getLength(final double[] w, final double[] x,
            final double[] y, final double[] z) {
        int n = getLength(w);
        if (getLength(x) != n || getLength(y) != n || getLength(z) != n) {
            throw new NonconformingArrayException(1);
        }
        return n;
    }

    public static final int getLength(final float[] w, final float[] x,
            final float[] y, final float[] z) {
        int n = getLength(w);
        if (getLength(x) != n || getLength(y) != n || getLength(z) != n) {
            throw new NonconformingArrayException(1);
        }
        return n;
    }

    public static final int getLength(final float[][] x) {
        if (x == null) {
            throw new IllegalArgumentException("Illegal NULL array.");
        }
        int n = x.length;
        if (n == 0) {
            throw new IllegalArgumentException("Illegal zero-length array.");
        }
        return n;
    }

    public static final int getLength(final double[][] x) {
        if (x == null) {
            throw new IllegalArgumentException("Illegal NULL array.");
        }
        int n = x.length;
        if (n == 0) {
            throw new IllegalArgumentException("Illegal zero-length array.");
        }
        return n;
    }

    public static final int getLength(final double[][] x, final double[][] y) {
        int n = getLength(x);
        if (getLength(y) != n) {
            throw new NonconformingArrayException(2);
        }
        return n;
    }

    public static final int getLength(final double[][] w, final double[][] x,
            final double[][] y) {
        int n = getLength(w);
        if (getLength(x) != n || getLength(y) != n) {
            throw new NonconformingArrayException(2);
        }
        return n;
    }

    public static final int getLength(final float[][] x, final float[][] y) {
        int n = getLength(x);
        if (getLength(y) != n) {
            throw new NonconformingArrayException(2);
        }
        return n;
    }

    public static final int getLength(final float[][] w, final float[][] x,
            final float[][] y) {
        int n = getLength(w);
        if (getLength(x) != n || getLength(y) != n) {
            throw new NonconformingArrayException(2);
        }
        return n;
    }

    public static final int getLength(final double[][][] x) {
        if (x == null) {
            throw new IllegalArgumentException("Illegal NULL array.");
        }
        int n = x.length;
        if (n == 0) {
            throw new IllegalArgumentException("Illegal zero-length array.");
        }
        return n;
    }

    public static final int getLength(final float[][][] x) {
        if (x == null) {
            throw new IllegalArgumentException("Illegal NULL array.");
        }
        int n = x.length;
        if (n == 0) {
            throw new IllegalArgumentException("Illegal zero-length array.");
        }
        return n;
    }

    public static final int getLength(final double[][][] x, final double[][][] y) {
        int n = getLength(x);
        if (getLength(y) != n) {
            throw new NonconformingArrayException(3);
        }
        return n;
    }

    public static final int getLength(final float[][][] x, final float[][][] y) {
        int n = getLength(x);
        if (getLength(y) != n) {
            throw new NonconformingArrayException(3);
        }
        return n;
    }

    /**
     * Get the common length of "vectors".
     *
     * @param w
     * @param x
     * @param y
     * @return
     * @throws NonconformingArrayException
     */
    public static final int getLength(final double[][][] w,
            final double[][][] x, final double[][][] y) {
        int n = getLength(w);
        if (getLength(x) != n || getLength(y) != n) {
            throw new NonconformingArrayException(3);
        }
        return n;
    }

    public static final int getLength(final float[][][] w,
            final float[][][] x, final float[][][] y) {
        int n = getLength(w);
        if (getLength(x) != n || getLength(y) != n) {
            throw new NonconformingArrayException(3);
        }
        return n;
    }

    /*-----------------------------------------------------------------------*/
    /* MIN/MAX VALUES */
    
    /* FLOAT VERSION */

    public static final float getMin(final float[] x) {
        int n = getNonZeroLength(x);
        float xmin = x[0];
        for (int i = 1; i < n; ++i) {
            float xval = x[i];
            if (xval < xmin) xmin = xval;
        }
        return xmin;
    }

    public static final float getMax(final float[] x) {
        int n = getNonZeroLength(x);
        float xmax = x[0];
        for (int i = 1; i < n; ++i) {
            float xval = x[i];
            if (xval > xmax) xmax = xval;
        }
        return xmax;
    }
   
    public static final float[] getMinMax(final float[] x) {
        return getMinMax(x, new float[2]);
    }

    public static final float[] getMinMax(final float[] x, float[] minMax) {
        if (minMax == null || minMax.length < 2) {
            throw new IllegalArgumentException("Invalid storage for min/max value.");
        }
        int n = getNonZeroLength(x);
        float xmin = x[0];
        float xmax = x[0];
        for (int i = 1; i < n; ++i) {
            float xval = x[i];
            if (xval < xmin) xmin = xval;
            if (xval > xmax) xmax = xval;
        }
        minMax[0] = xmin;
        minMax[1] = xmax;
        return minMax;
    }


    /* DOUBLE VERSION */
    
    public static final double getMin(final double[] x) {
        int n = getNonZeroLength(x);
        double xmin = x[0];
        for (int i = 1; i < n; ++i) {
            double xval = x[i];
            if (xval < xmin) xmin = xval;
        }
        return xmin;
    }

    public static final double getMax(final double[] x) {
        int n = getNonZeroLength(x);
        double xmax = x[0];
        for (int i = 1; i < n; ++i) {
            double xval = x[i];
            if (xval > xmax) xmax = xval;
        }
        return xmax;
    }

    public static final double[] getMinMax(final double[] x) {
        return getMinMax(x, new double[2]);
    }

    public static final double[] getMinMax(final double[] x, double[] minMax) {
        if (minMax == null || minMax.length < 2) {
            throw new IllegalArgumentException("Invalid storage for min/max value.");
        }
        int n = getNonZeroLength(x);
        double xmin = x[0];
        double xmax = x[0];
        for (int i = 1; i < n; ++i) {
            double xval = x[i];
            if (xval < xmin) xmin = xval;
            if (xval > xmax) xmax = xval;
        }
        minMax[0] = xmin;
        minMax[1] = xmax;
        return minMax;
    }

    /*-----------------------------------------------------------------------*/
    /* SUM OF VALUES */
    
    /* FLOAT VERSION */

    public static final float sum(final float[] x) {
        float s = 0.0F;
        int n = getLength(x);
        for (int i = 1; i < n; ++i) {
            s += x[i];
        }
        return s;
    }

    public static final float sum(final float[][] x) {
        float s = 0.0F;
        int n = getLength(x);
        for (int i = 1; i < n; ++i) {
            s += sum(x[i]);
        }
        return s;
    }

    public static final float sum(final float[][][] x) {
        float s = 0.0F;
        int n = getLength(x);
        for (int i = 1; i < n; ++i) {
            s += sum(x[i]);
        }
        return s;
    }

    /* DOUBLE VERSION */

    public static final double sum(final double[] x) {
        double s = 0.0;
        int n = getLength(x);
        for (int i = 1; i < n; ++i) {
            s += x[i];
        }
        return s;
    }

    public static final double sum(final double[][] x) {
        double s = 0.0;
        int n = getLength(x);
        for (int i = 1; i < n; ++i) {
            s += sum(x[i]);
        }
        return s;
    }

    public static final double sum(final double[][][] x) {
        double s = 0.0;
        int n = getLength(x);
        for (int i = 1; i < n; ++i) {
            s += sum(x[i]);
        }
        return s;
    }

    /*-----------------------------------------------------------------------*/
    /* DOT PRODUCT */

    /**
     * Dot product for 1D vectors of double's.
     *
     * @param n
     *            - number of elements
     * @param x
     *            - first vector
     * @param y
     *            - second vector
     * @return
     */
    public static final double dot(int n, final double[] x, final double[] y) {
        double result = 0.0;
        for (int i = 0; i < n; ++i) {
            result += x[i] * y[i];
        }
        return result;
    }

    public static final double dot(int n, final float[] x, final float[] y) {
        float result = 0.0F;
        for (int i = 0; i < n; ++i) {
            result += x[i] * y[i];
        }
        return (double)result;
    }

    public static final double dot(int n, final double[] w, final double[] x,
            final double[] y) {
        double result = 0.0;
        for (int i = 0; i < n; ++i) {
            result += w[i] * x[i] * y[i];
        }
        return result;
    }

    public static final double dot(int n, final float[] w, final float[] x,
            final float[] y) {
        float result = 0.0F;
        for (int i = 0; i < n; ++i) {
            result += w[i] * x[i] * y[i];
        }
        return (double)result;
    }

    /**
     * Dot product for 1D vectors of double's.
     *
     * @param x     A vector.
     * @param y     Another vector.
     * @return x'.y
     */
    public static final double dot(final double[] x, final double[] y) {
        return dot(getLength(x, y), x, y);
    }

    public static final double dot(final float[] x, final float[] y) {
        return dot(getLength(x, y), x, y);
    }

    /**
     * Weighted dot product for 1D vectors of double's.
     *
     * @param w     A vector.
     * @param x     Another vector.
     * @param y     Yet another vector.
     * @return w[0]*x[0]*y[0] + w[1]*x[1]*y[1] + ...
     */
    public static final double dot(final double[] w, final double[] x,
            final double[] y) {
        return dot(getLength(w, x, y), w, x, y);
    }

    public static final double dot(final float[] w, final float[] x,
            final float[] y) {
        return dot(getLength(w, x, y), w, x, y);
    }

    public static final double dot(final double[][] x, final double[][] y) {
        double s = 0.0;
        int n = getLength(x, y);
        for (int i = 0; i < n; ++i) {
            s += dot(x[i], y[i]);
        }
        return s;
    }

    public static final double dot(final float[][] x, final float[][] y) {
        double s = 0.0;
        int n = getLength(x, y);
        for (int i = 0; i < n; ++i) {
            s += dot(x[i], y[i]);
        }
        return s;
    }

    public static final double dot(final double[][] w, final double[][] x,
            final double[][] y) {
        double s = 0.0;
        int n = getLength(w, x, y);
        for (int i = 0; i < n; ++i) {
            s += dot(w[i], x[i], y[i]);
        }
        return s;
    }

    public static final double dot(final float[][] w, final float[][] x,
            final float[][] y) {
        double s = 0.0;
        int n = getLength(w, x, y);
        for (int i = 0; i < n; ++i) {
            s += dot(w[i], x[i], y[i]);
        }
        return s;
    }

    public static final double dot(final double[][][] x, final double[][][] y) {
        double s = 0.0;
        int n = getLength(x, y);
        for (int i = 0; i < n; ++i) {
            s += dot(x[i], y[i]);
        }
        return s;
    }

    public static final double dot(final float[][][] x, final float[][][] y) {
        double s = 0.0;
        int n = getLength(x, y);
        for (int i = 0; i < n; ++i) {
            s += dot(x[i], y[i]);
        }
        return s;
    }

    public static final double dot(double[][][] w, double[][][] x,
            double[][][] y) {
        double s = 0.0;
        int n = getLength(w, x, y);
        for (int i = 0; i < n; ++i) {
            s += dot(w[i], x[i], y[i]);
        }
        return s;
    }

    public static final double dot(float[][][] w, float[][][] x,
            float[][][] y) {
        double s = 0.0;
        int n = getLength(w, x, y);
        for (int i = 0; i < n; ++i) {
            s += dot(w[i], x[i], y[i]);
        }
        return s;
    }

    /*-----------------------------------------------------------------------*/
    /* NORMS */

    /**
     * Compute the Euclidean (L2) norm of an array.
     *
     * @param x   An array of double's.
     *
     * @return The square root of the sum of squared elements of x.
     */
    public static double norm2(final double[] x) {
        double s = 0.0;
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            s += x[i]*x[i];
        }
        return Math.sqrt(s);
    }

    /**
     * Compute the L1 norm of an array.
     *
     * @param x   An array of double's.
     *
     * @return The sum of absolute values of x.
     */
    public static double norm1(final double[] x) {
        double s = 0.0;
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            s += Math.abs(x[i]);
        }
        return s;
    }

    /**
     * Compute the infinite norm of an array.
     *
     * @param x   An array of double's.
     *
     * @return The maximum absolute value of x.
     */
    public static double normInf(final double[] x) {
        double s = 0.0;
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            double r = Math.abs(x[i]);
            if (r > s) {
                s = r;
            }
        }
        return s;
    }

    /**
     * Compute the Euclidean (L2) norm of an array.
     *
     * @param x   An array of float's.
     *
     * @return The square root of the sum of squared elements of x.
     */
    public static float norm2(final float[] x) {
        float s = 0.0F;
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            s += x[i]*x[i];
        }
        return (float)Math.sqrt(s);
    }

    /**
     * Compute the L1 norm of an array.
     *
     * @param x   An array of float's.
     *
     * @return The sum of absolute values of x.
     */
    public static double norm1(final float[] x) {
        float s = 0.0F;
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            s += Math.abs(x[i]);
        }
        return s;
    }

    /**
     * Compute the infinite norm of an array.
     *
     * @param x   An array of float's.
     *
     * @return The maximum absolute value of x.
     */
    public static float normInf(final float[] x) {
        float s = 0.0F;
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            float r = Math.abs(x[i]);
            if (r > s) {
                s = r;
            }
        }
        return s;
    }

    /*-----------------------------------------------------------------------*/
    /* ZERO */

    public static void zero(int n, double[] x) {
        fill(n, x, 0.0);
    }

    public static void zero(int n, float[] x) {
        fill(n, x, 0.0);
    }

    public static void zero(double[] x) {
        zero(x.length, x);
    }

    public static void zero(float[] x) {
        zero(x.length, x);
    }

    public static void zero(double[][] x) {
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            zero(x[i]);
        }
    }

    public static void zero(float[][] x) {
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            zero(x[i]);
        }
    }

    public static void zero(double[][][] x) {
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            zero(x[i]);
        }
    }

    public static void zero(float[][][] x) {
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            zero(x[i]);
        }
    }

    public static void copy(int n, final double[] src, double[] dst) {
        for (int i = 0; i < n; ++i) {
            dst[i] = src[i];
        }
    }

    public static void copy(int n, final float[] src, float[] dst) {
        for (int i = 0; i < n; ++i) {
            dst[i] = src[i];
        }
    }

    public static void copy(final double[] src, double[] dst) {
        copy(getLength(src, dst), src, dst);
    }

    public static void copy(final float[] src, float[] dst) {
        copy(getLength(src, dst), src, dst);
    }

    public static void copy(final double[][] src, double[][] dst) {
        int n = getLength(src, dst);
        for (int i = 0; i < n; ++i) {
            copy(src[i], dst[i]);
        }
    }

    public static void copy(final float[][] src, float[][] dst) {
        int n = getLength(src, dst);
        for (int i = 0; i < n; ++i) {
            copy(src[i], dst[i]);
        }
    }

    /*-----------------------------------------------------------------------*/
    /* FILL */

    public static void fill(int n, double[] x, double alpha) {
        for (int i = 0; i < n; ++i) {
            x[i] = alpha;
        }
    }

    public static void fill(int n, float[] x, double alpha) {
        float a = (float)alpha;
        for (int i = 0; i < n; ++i) {
            x[i] = a;
        }
    }

    /*-----------------------------------------------------------------------*/
    /* ALPHA*X + BETA*Y */

    public static void axpby(int n, double alpha, final double[] x,
            double beta, double[] y) {
        if (beta == 1.0) {
            /* Job: Y += ALPHA*X */
            if (alpha == 1.0) {
                /* Job: Y += X */
                for (int i = 0; i < n; ++i) {
                    y[i] += x[i];
                }
            } else if (alpha == -1.0) {
                /* Job: Y -= X */
                for (int i = 0; i < n; ++i) {
                    y[i] -= x[i];
                }
            } else if (alpha != 0.0) {
                /* Job: Y += ALPHA*X (nothing to do if ALPHA = 0) */
                for (int i = 0; i < n; ++i) {
                    y[i] += alpha * x[i];
                }
            }
        } else if (beta == 0.0) {
            /* Job: Y = ALPHA*X */
            if (alpha == 1.0) {
                /* Job: Y = X */
                copy(n, x, y);
            } else if (alpha == 0.0) {
                /* Job: Y = 0 */
                zero(n, y);
            } else if (alpha == -1.0) {
                /* Job: Y = -X */
                for (int i = 0; i < n; ++i) {
                    y[i] = -x[i];
                }
            } else {
                /* Job: Y = ALPHA*X */
                for (int i = 0; i < n; ++i) {
                    y[i] = alpha * x[i];
                }
            }
        } else if (beta == -1.0) {
            /* Job: Y = ALPHA*X - Y */
            if (alpha == 1.0) {
                /* Job: Y = ALPHA*X - Y */
                for (int i = 0; i < n; ++i) {
                    y[i] = x[i] - y[i];
                }
            } else if (alpha == 0.0) {
                for (int i = 0; i < n; ++i) {
                    y[i] = -y[i];
                }
            } else if (alpha == -1.0) {
                for (int i = 0; i < n; ++i) {
                    y[i] = -x[i] - y[i];
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    y[i] = alpha * x[i] - y[i];
                }
            }
        } else {
            if (alpha == 1.0) {
                for (int i = 0; i < n; ++i) {
                    y[i] = x[i] + beta * y[i];
                }
            } else if (alpha == -1.0) {
                for (int i = 0; i < n; ++i) {
                    y[i] = beta * y[i] - x[i];
                }
            } else if (alpha == 0.0) {
                for (int i = 0; i < n; ++i) {
                    y[i] *= beta;
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    y[i] = alpha * x[i] + beta * y[i];
                }
            }
        }
    }

    public static void axpby(int n, double alpha, final double[] x,
            double beta, final double[] y, double[] dst) {
        if (beta == 1.0) {
            if (alpha == 1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = y[i] + x[i];
                }
            } else if (alpha == -1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = y[i] - x[i];
                }
            } else if (alpha == 0.0) {
                for (int i = 0; i < n; ++i) {
                    copy(n, y, dst);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    dst[i] = alpha*x[i] + y[i];
                }
            }
        } else if (beta == -1.0) {
            if (alpha == 1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = x[i] - y[i];
                }
            } else if (alpha == -1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = -x[i] - y[i];
                }
            } else if (alpha == 0.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = -y[i];
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    dst[i] = alpha*x[i] - y[i];
                }
            }
        } else if (beta == 0.0) {
            if (alpha == 1.0) {
                copy(n, x, dst);
            } else if (alpha == -1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = -x[i];
                }
            } else if (alpha == 0.0) {
                zero(n, dst);
            } else {
                for (int i = 0; i < n; ++i) {
                    dst[i] = alpha*x[i];
                }
            }
        } else {
            if (alpha == 1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = x[i] + beta*y[i];
                }
            } else if (alpha == -1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = beta*y[i] - x[i];
                }
            } else if (alpha == 0.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = beta*y[i];
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    dst[i] = alpha*x[i] + beta*y[i];
                }
            }
        }
    }

    public static void axpby(double alpha, final double[] x, double beta,
            double[] y) {
        axpby(getLength(x, y), alpha, x, beta, y);
    }

    public static void axpby(double alpha, final double[] x, double beta,
            final double[] y, double dst[]) {
        axpby(getLength(x, y, dst), alpha, x, beta, y, dst);
    }

    public static void axpby(int n, double alpha, final float[] x,
            double beta, float[] y) {
        if (beta == 1.0) {
            /* Job: Y += ALPHA*X */
            if (alpha == 1.0) {
                /* Job: Y += X */
                for (int i = 0; i < n; ++i) {
                    y[i] += x[i];
                }
            } else if (alpha == -1.0) {
                /* Job: Y -= X */
                for (int i = 0; i < n; ++i) {
                    y[i] -= x[i];
                }
            } else if (alpha != 0.0) {
                /* Job: Y += ALPHA*X (nothing to do if ALPHA = 0) */
                float a = (float)alpha;
                for (int i = 0; i < n; ++i) {
                    y[i] += a * x[i];
                }
            }
        } else if (beta == 0.0) {
            /* Job: Y = ALPHA*X */
            if (alpha == 1.0) {
                /* Job: Y = X */
                copy(n, x, y);
            } else if (alpha == 0.0) {
                /* Job: Y = 0 */
                zero(n, y);
            } else if (alpha == -1.0) {
                /* Job: Y = -X */
                for (int i = 0; i < n; ++i) {
                    y[i] = -x[i];
                }
            } else {
                /* Job: Y = ALPHA*X */
                float a = (float)alpha;
                for (int i = 0; i < n; ++i) {
                    y[i] = a*x[i];
                }
            }
        } else if (beta == -1.0) {
            /* Job: Y = ALPHA*X - Y */
            if (alpha == 1.0) {
                /* Job: Y = ALPHA*X - Y */
                for (int i = 0; i < n; ++i) {
                    y[i] = x[i] - y[i];
                }
            } else if (alpha == 0.0) {
                for (int i = 0; i < n; ++i) {
                    y[i] = -y[i];
                }
            } else if (alpha == -1.0) {
                for (int i = 0; i < n; ++i) {
                    y[i] = -x[i] - y[i];
                }
            } else {
                float a = (float)alpha;
                for (int i = 0; i < n; ++i) {
                    y[i] = a* x[i] - y[i];
                }
            }
        } else {
            float b = (float)beta;
            if (alpha == 1.0) {
                for (int i = 0; i < n; ++i) {
                    y[i] = x[i] + b*y[i];
                }
            } else if (alpha == -1.0) {
                for (int i = 0; i < n; ++i) {
                    y[i] = b*y[i] - x[i];
                }
            } else if (alpha == 0.0) {
                for (int i = 0; i < n; ++i) {
                    y[i] *= b;
                }
            } else {
                float a = (float)alpha;
                for (int i = 0; i < n; ++i) {
                    y[i] = a*x[i] + b*y[i];
                }
            }
        }
    }

    public static void axpby(int n, double alpha, final float[] x,
            double beta, final float[] y, float[] dst) {
        if (beta == 1.0) {
            if (alpha == 1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = y[i] + x[i];
                }
            } else if (alpha == -1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = y[i] - x[i];
                }
            } else if (alpha == 0.0) {
                for (int i = 0; i < n; ++i) {
                    copy(n, y, dst);
                }
            } else {
                float a = (float)alpha;
                for (int i = 0; i < n; ++i) {
                    dst[i] = a*x[i] + y[i];
                }
            }
        } else if (beta == -1.0) {
            if (alpha == 1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = x[i] - y[i];
                }
            } else if (alpha == -1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = -x[i] - y[i];
                }
            } else if (alpha == 0.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = -y[i];
                }
            } else {
                float a = (float)alpha;
                for (int i = 0; i < n; ++i) {
                    dst[i] = a*x[i] - y[i];
                }
            }
        } else if (beta == 0.0) {
            if (alpha == 1.0) {
                copy(n, x, dst);
            } else if (alpha == -1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = -x[i];
                }
            } else if (alpha == 0.0) {
                zero(n, dst);
            } else {
                float a = (float)alpha;
                for (int i = 0; i < n; ++i) {
                    dst[i] = a*x[i];
                }
            }
        } else {
            float b = (float)beta;
            if (alpha == 1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = x[i] + b*y[i];
                }
            } else if (alpha == -1.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = b*y[i] - x[i];
                }
            } else if (alpha == 0.0) {
                for (int i = 0; i < n; ++i) {
                    dst[i] = b*y[i];
                }
            } else {
                float a = (float)alpha;
                for (int i = 0; i < n; ++i) {
                    dst[i] = a*x[i] + b*y[i];
                }
            }
        }
    }

    public static void axpby(double alpha, final float[] x, double beta, float[] y) {
        axpby(getLength(x, y), alpha, x, beta, y);
    }

    public static void axpby(double alpha, final float[] x, double beta, final float[] y, float dst[]) {
        axpby(getLength(x, y, dst), alpha, x, beta, y, dst);
    }

    /*-----------------------------------------------------------------------*/
    /* ALPHA*X + BETA*Y + GAMMA*Z */

    public static void axpbypcz(int n,
            double alpha, final double[] x,
            double beta,  final double[] y,
            double gamma, final double[] z,
            double[] dst)
    {
        if (alpha == 0.0) {
            axpby(beta, y, gamma, z, dst);
        } else if (beta == 0.0) {
            axpby(alpha, x, gamma, z, dst);
        } else if (gamma == 0.0) {
            axpby(alpha, x, beta, y, dst);
        } else {
            for (int i = 0; i < n; ++i) {
                dst[i] = alpha*x[i] + beta*y[i] + gamma*z[i];
            }
        }
    }

    public static void axpbypcz(double alpha, final double[] x, double beta,
            final double[] y, double gamma, final double[] z, double[] dst) {
        axpbypcz(getLength(x, y, z, dst), alpha, x, beta, y, gamma, z, dst);
    }

    public static void axpbypcz(int n,
            double alpha, final float[] x,
            double beta,  final float[] y,
            double gamma, final float[] z,
            float[] dst)
    {
        if (alpha == 0.0) {
            axpby(beta, y, gamma, z, dst);
        } else if (beta == 0.0) {
            axpby(alpha, x, gamma, z, dst);
        } else if (gamma == 0.0) {
            axpby(alpha, x, beta, y, dst);
        } else {
            float a = (float)alpha;
            float b = (float)beta;
            float c = (float)gamma;
            for (int i = 0; i < n; ++i) {
                dst[i] = a*x[i] + b*y[i] + c*z[i];
            }
        }
    }

    public static void axpbypcz(float alpha, final float[] x, float beta,
            final float[] y, float gamma, final float[] z, float[] dst) {
        axpbypcz(getLength(x, y, z, dst), alpha, x, beta, y, gamma, z, dst);
    }

    /*-----------------------------------------------------------------------*/
    /* DOT PRODUCT */

    /**
     * Compute the dot product of two "vectors".
     *
     * @param x
     *            the first argument passed to the function.
     * @param y
     *            the second argument passed to the function.
     * @return the result of the function.
     */
    public static double dot(final Object x, final Object y) {
        return 0.0;
    }

    /**
     * Compute the dot product of three "vectors".
     *
     * @param w
     *            the first argument passed to the function.
     * @param x
     *            the second argument passed to the function.
     * @param y
     *            the third argument passed to the function.
     * @return the result of the function.
     */
    public static double dot(final Object w, final Object x, final Object y) {
        return 0.0;
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
