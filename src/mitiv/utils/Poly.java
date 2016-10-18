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

public class Poly {
    /**
     * Compute the roots of a 2nd degree polynomial.
     *
     * <p> Solve the quadratic equation: </p>
     *
     * <pre>
     * a*x^2 + b*x + c = 0
     * </pre>
     *
     * <p> for real {@code x}. The number {@code n} of distinct real roots is
     * returned and the roots, if any, are stored into {@code x}. If there is
     * no roots, the contents of {@code x} is left unchanged; otherwise
     * ({@code n} = 1 or 2) the two values of {@code x} are set with the
     * roots in ascending order, <i>i.e.</i> such that {@code x[0] <= x[1]}. </p>
     *
     * @param x   A 2-element output array to store the result.
     * @param a   The 2nd degree coefficient.
     * @param b   The 1st degree coefficient.
     * @param c   The 0th degree coefficient.
     *
     * @return The number {@code n} of distinct real roots. If {@code n} = 1
     * or 2, then {@code x[0] <= x[1]}; otherwise ({@code n} = 0) and
     * {@code x} contents is left unchanged.
     */
    public static int solveQuadratic(double x[], double a, double b, double c) {
        if (a != 0.0) {
            double p = a + a;
            double q = c + c;
            double r = b*b - p*q;
            if (r > 0.0) {
                if (b >= 0.0) {
                    r = -Math.sqrt(r) - b;
                } else {
                    r = +Math.sqrt(r) - b;
                }
                double x0 = q/r;
                double x1 = r/p;
                if (x0 < x1) {
                    x[0] = x0;
                    x[1] = x1;
                } else {
                    x[0] = x1;
                    x[1] = x0;
                }
                return 2;
            } else if (r == 0.0) {
                x[0] = x[1] = -b/p;
                return 1;
            }
        } else if (b != 0.0) {
            x[0] = x[1] = -c/b;
            return 1;
        }
        return 0;
    }


    final static double THIRD = 1.0/3.0;
    final static double HALF = 1.0/2.0;
    final static double TWO_PI_OVER_THREE = (2.0*Math.PI)/3.0;
    public static int solveCubic(double[] x, double a, double b, double c) {
        double t = THIRD*a; // a/3
        double t2 = a*a;
        double q = t2 - THIRD*b;
        double q3 = q*q*q;
        double r = (t2 - HALF*b)*t + HALF*c; // (2*a^3 - 9*a*b + 27*c)/54
        double r2 = r*r;
        if (r2 < q3) {
            /* The cubic equation has three real roots. */
            double phi = THIRD*Math.acos(r/Math.sqrt(q3));
            double rho = -2.0*Math.sqrt(q);
            double x0 = rho*Math.cos(phi) - t;
            double x1 = rho*Math.cos(phi - TWO_PI_OVER_THREE) - t;
            double x2 = rho*Math.cos(phi + TWO_PI_OVER_THREE) - t;
            if (x0 > x1) {
                double xt = x0; x0 = x1; x1 = xt;
            }
            if (x0 > x2) {
                double xt = x0; x0 = x2; x2 = xt;
            }
            if (x1 > x2) {
                double xt = x1; x1 = x2; x2 = xt;
            }
            x[0] = x0;
            x[1] = x1;
            x[2] = x2;
            return 3;
        } else {
            // FIXME: save a cubic root when p=0
            double s = Math.sqrt(r2 - q3);
            double p = (r >= 0.0 ? -Math.cbrt(r + s) : Math.cbrt(s - r));
            if (p == 0.0) {
                x[0] = -t;
            } else {
                x[0] = (p + q/p) - t;
            }
            return 1;
        }
    }
}
