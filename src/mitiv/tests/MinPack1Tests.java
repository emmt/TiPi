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

/*
 * MinPack1Tests.java --
 *
 * Implementation of unconstrained minimization test suite from the MINPACK-1
 * project.
 *
 * References:
 * [1] J. J. Moré, B. S. Garbow and K. E. Hillstrom, "Testing unconstrained
 *     optimization software," ACM Trans. Math. Software 7, 17-41 (1981).
 * [2] J. J. Moré, B. S. Garbow and K. E. Hillstrom, "Fortran subroutines for
 *     testing unconstrained optimization software," ACM Trans. Math. Software
 *     7, 136-140 (1981).
 *
 * History:
 *  - Argonne National Laboratory. MINPACK-1 Project.  March 1980.
 *    Burton S. Garbow, Kenneth E. Hillstrom, Jorge J. More.
 *  - Conversion to Yorick.  November 2001. Éric Thiébaut.
 *  - Conversion to C.  February 2014. Éric Thiébaut.
 *  - Conversion to Java.  March 2014. Éric Thiébaut.
 *
 *-----------------------------------------------------------------------------
 *
 * Copyright (C) 2014 Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy, modify
 * and redistribute granted by the license, users are provided only with a
 * limited warranty and the software's author, the holder of the economic
 * rights, and the successive licensors have only limited liability.
 *
 * In this respect, the user's attention is drawn to the risks associated with
 * loading, using, modifying and/or developing or reproducing the software by
 * the user in light of its specific status of free software, that may mean
 * that it is complicated to manipulate, and that also therefore means that it
 * is reserved for developers and experienced professionals having in-depth
 * computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling
 * the security of their systems and/or data to be ensured and, more generally,
 * to use and operate it in the same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *
 *-----------------------------------------------------------------------------
 */
package mitiv.tests;

public class MinPack1Tests {
    public static final int TEST18_NMAX = 50;
    public static final int SUCCESS = 0;
    public static final int FAILURE = -1;

    protected static final double TPI = 2.0*Math.PI;
    protected static final double AP = 1e-5;
    protected static final double BP = 1.0;

    public static void main(String[] args) {
        String[] info = new String[1];
        final int MX = Integer.MAX_VALUE;
        // Allowed sizes for problems.
        //                  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18
        int[] minSize = {0, 3, 6, 3, 2, 3, 1, 2, 1, 1, 2, 4, 3, 1, 2, 4, 2, 4, 1};
        int[] maxSize = {0, 3, 6, 3, 2, 3,MX,MX,MX,MX, 2, 4, 3,MX,MX,MX, 2, 4, TEST18_NMAX};
        int[] mulSize = {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 4, 1, 1, 1};

        for (int prob = 1; prob < minSize.length; ++prob) {
            int n;
            if (minSize[prob] == maxSize[prob]) {
                n = minSize[prob];
            } else if (maxSize[prob] < MX) {
                n = maxSize[prob];
            } else {
                n = roundUp(20, mulSize[prob]);
            }
            int status = umck(info, n, prob);
            if (status == SUCCESS) {
                System.out.println("SUCCESS prob = " + prob + " (with n = " + n + "): " + info[0]);
            } else {
                System.out.println("FAILURE prob = " + prob + " (with n = " + n + "): " + info[0]);
                continue;
            }
            double[] x = new double[n];
            double[] g = new double[n];
            umipt(x, prob, 1.0);
            double f0 = umobj(x, prob);
            System.out.println("   initial cost: " + f0);
            umgrd(x, g, prob);
            double alpha = 0.01;
            for (int i = 0; i < n; ++i) {
                x[i] -= alpha*g[i];
            }
            double f1 = umobj(x, prob);
            System.out.println("   cost after a small descent step: " + f1);
        }
    }

    private static int roundUp(int a, int b) {
        return ((a + (b - 1)) / b) * b;
    }

    private static int check(boolean success, String[] str, final String descr,
            final String reason) {
        if (success) {
            if (str != null && str.length >= 1) {
                str[0] = descr;
            }
            return SUCCESS;
        } else {
            if (str != null && str.length >= 1) {
                str[0] = reason;
            }
            return FAILURE;
        }
    }

    /**
     * Check validity of parameters for a given MINPACK1 unconstrained
     * minimization problem.
     *
     * @param str   If not null, used to store the description of the
     *              problem on success or an error message on failure.
     * @param n     Size of the problem.
     * @param prob  Problem number.
     * @return SUCCESS or FAILURE.
     */
    public static int umck(String[] str, int n, int prob)
    {
        int status;

        /* Check compatibility of arguments N and PROB. */
        switch (prob) {
        case 1:
            status = check(n == 3, str, "Helical valley function.",
                    "N must be 3 for problem #1");
            break;
        case 2:
            status = check(n == 6, str, "Biggs exp6 function.",
                    "N must be 6 for problem #2");
            break;
        case 3:
            status = check(n == 3, str, "Gaussian function.",
                    "N must be 3 for problem #3");
            break;
        case 4:
            status = check(n == 2, str, "Powell badly scaled function.",
                    "N must be 2 for problem #4");
            break;
        case 5:
            status = check(n == 3, str, "Box 3-dimensional function.",
                    "N must be 3 for problem #5");
            break;
        case 6:
            status = check(n >= 1, str, "Variably dimensioned function.",
                    "N must be >= 1 in problem #6");
            break;
        case 7:
            status = check(n >= 2, str, "Watson function.",
                    "N may be 2 or greater but is usually 6 or 9 for problem #7");
            break;
        case 8:
            status = check(n >= 1, str, "Penalty function I.",
                    "N must be >= 1 in problem #8");
            break;
        case 9:
            status = check(n >= 1, str, "Penalty function II.",
                    "N must be >= 1 in problem #9");
            break;
        case 10:
            status = check(n == 2, str, "Brown badly scaled function.",
                    "N must be 2 for problem #10");
            break;
        case 11:
            status = check(n == 4, str, "Brown and Dennis function.",
                    "N must be 4 for problem #11");
            break;
        case 12:
            status = check(n == 3, str, "Gulf research and development function.",
                    "N must be 3 for problem #12");
            break;
        case 13:
            status = check(n >= 1, str, "Trigonometric function.",
                    "N must be >= 1 in problem #13");
            break;
        case 14:
            status = check(n >= 1 && n%2 == 0, str, "Extended Rosenbrock function.",
                    "N must be a multiple of 2 in problem #14");
            break;
        case 15:
            status = check(n >= 1 && n%4 == 0, str, "Extended Powell function.",
                    "N must be a multiple of 4 in problem #15");
            break;
        case 16:
            status = check(n == 2, str, "Beale function.",
                    "N must be 2 for problem #16");
            break;
        case 17:
            status = check(n == 4, str, "Wood function.",
                    "N must be 4 for problem #17");
            break;
        case 18:
            status = check(n >= 1 && n <= TEST18_NMAX, str,
            "Chebyquad function.",
            "N must be <= " + TEST18_NMAX +
                    " for problem #18");
            break;
        default:
            status = FAILURE;
            if (str != null && str.length >= 1) {
                str[0] = "PROB must be an integer between 1 and 18";
            }
        }
        return status;
    }

    private static final double[] um_y = {9.0e-4,   4.4e-3,   1.75e-2,
        5.4e-2,   1.295e-1, 2.42e-1,
        3.521e-1, 3.989e-1, 3.521e-1,
        2.42e-1,  1.295e-1, 5.4e-2,
        1.75e-2,  4.4e-3,   9.0e-4};

    /**
     * Stores the standard starting points for the functions defined by subroutine
     * umobj.
     * 
     * Argument X is a vector of N elements, X is a multiple times FACTOR of the
     * standard starting point.  For the seventh function the standard starting
     * point is 0.0, so in this case, if FACTOR is not unity, then the function
     * returns X filled with FACTOR.  PROB has the same meaning as in umobj.
     * 
     * @param x       Variables of the problem.
     * @param prob    Problem number.
     * @param factor  Scale factor.
     */
    public static void umipt(double[] x, int prob, double factor)
    {
        double h;
        int j, n = x.length;

        /* Selection of initial point. */
        switch (prob) {
        case 1:
            /* Helical valley function. */
            x[0] = -1.0;
            x[1] = 0.0;
            x[2] = 0.0;
            break;
        case 2:
            /* Biggs exp6 function. */
            x[0] = 1.0;
            x[1] = 2.0;
            x[2] = 1.0;
            x[3] = 1.0;
            x[4] = 1.0;
            x[5] = 1.0;
            break;
        case 3:
            /* Gaussian function. */
            x[0] = 0.4;
            x[1] = 1.0;
            x[2] = 0.0;
            break;
        case 4:
            /* Powell badly scaled function. */
            x[0] = 0.0;
            x[1] = 1.0;
            break;
        case 5:
            /* Box 3-dimensional function. */
            x[0] = 0.0;
            x[1] = 10.0;
            x[2] = 20.0;
            break;
        case 6:
            /* Variably dimensioned function. */
            h = 1.0/((double)n);
            for (j = 0; j < n; ++j) {
                x[j] = 1.0 - ((double)(j+1))*h;
            }
            break;
        case 7:
            /* Watson function. */
            for (j = 0; j < n; ++j) {
                x[j] = 0.0;
            }
            break;
        case 8:
            /* Penalty function I. */
            for (j = 0; j < n; ++j) {
                x[j] = ((double)(j+1));
            }
            break;
        case 9:
            /* Penalty function II. */
            for (j = 0; j < n; ++j) {
                x[j] = 0.5;
            }
            break;
        case 10:
            /* Brown badly scaled function. */
            x[0] = 1.0;
            x[1] = 1.0;
            break;
        case 11:
            /* Brown and Dennis function. */
            x[0] = 25.0;
            x[1] =  5.0;
            x[2] = -5.0;
            x[3] = -1.0;
            break;
        case 12:
            /* Gulf research and development function. */
            x[0] = 5.0;
            x[1] = 2.5;
            x[2] = 0.15;
            break;
        case 13:
            /* Trigonometric function. */
            h = 1.0/((double)n);
            for (j = 0; j < n; ++j) {
                x[j] = h;
            }
            break;
        case 14:
            /* Extended Rosenbrock function. */
            for (j = 0; j < n; j += 2) {
                x[j]   = -1.2;
                x[j+1] =  1.0;
            }
            break;
        case 15:
            /* Extended Powell singular function. */
            for (j = 0; j < n; j += 4) {
                x[j]   =  3.0;
                x[j+1] = -1.0;
                x[j+2] =  0.0;
                x[j+3] =  1.0;
            }
            break;
        case 16:
            /* Beale function. */
            x[0] = 1.0;
            x[1] = 1.0;
            break;
        case 17:
            /* Wood function. */
            x[0] = -3.0;
            x[1] = -1.0;
            x[2] = -3.0;
            x[3] = -1.0;
            break;
        case 18:
            /* Chebyquad function. */
            h = 1.0/((double)(n + 1));
            for (j = 0; j < n; ++j) {
                x[j] = ((double)(j+1))*h;
            }
        }

        /* Compute multiple of initial point. */
        if (factor != 1.0) {
            if (prob == 7) {
                for (j = 0; j < n; ++j) {
                    x[j] = factor;
                }
            } else {
                for (j = 0; j < n; ++j) {
                    x[j] *= factor;
                }
            }
        }
    }

    /**
     * Computes the objective function of one of the nonlinear unconstrained
     * minimization problems.
     * 
     * Returns the objective functions of eighteen nonlinear unconstrained
     * minimization problems.  X is the parameter array: a vector of length N,
     * PROB is the problem number (a positive integer between 1 and 18).
     *
     * The values of N for functions 1,2,3,4,5,10,11,12,16 and 17 are
     * 3,6,3,2,3,2,4,3,2 and 4, respectively.  For function 7, N may be 2 or
     * greater but is usually 6 or 9.  For functions 6,8,9,13,14,15 and 18, N may
     * be variable, however it must be even for function 14, a multiple of 4 for
     * function 15, and not greater than 50 for function 18.
     * 
     * @param x       Variables of the problem.
     * @param prob    Problem number.
     * @return Objective function value.
     */
    public static double umobj(final double x[], int prob)
    {
        double f, arg, d1, d2, r, s1, s2, s3, t, t1, t2, t3, th;
        double[] fvec = new double[TEST18_NMAX];
        int i, j, n = x.length;
        int iev;

        /* Function routine selector. */
        switch (prob) {
        case 1:
            /* Helical valley function. */
            if      (x[0] > 0.0) th = Math.atan(x[1]/x[0])/TPI;
            else if (x[0] < 0.0) th = Math.atan(x[1]/x[0])/TPI + 0.5;
            else                 th = (x[1] >= 0.0 ? 0.25 : -0.25);
            arg = x[0]*x[0] + x[1]*x[1];
            r = Math.sqrt(arg) - 1.0;
            t = x[2] - 10.0*th;
            f = 100.0*(t*t + r*r) + x[2]*x[2];
            break;
        case 2:
            /* Biggs exp6 function. */
            f = 0.0;
            for (i = 1; i <= 13; ++i) {
                d1 = ((double)i)/10.0;
                d2 = Math.exp(-d1) - 5.0*Math.exp(-10.0*d1) + 3.0*Math.exp(-4.0*d1);
                s1 = Math.exp(-d1*x[0]);
                s2 = Math.exp(-d1*x[1]);
                s3 = Math.exp(-d1*x[4]);
                t = x[2]*s1 - x[3]*s2 + x[5]*s3 - d2;
                f += t*t;
            }
            break;
        case 3:
            /* Gaussian function. */
            f = 0.0;
            for (i = 0; i < 15; ++i) {
                d1 = 0.5*((double)i);
                d2 = 3.5 - d1 - x[2];
                arg = -0.5*x[1]*(d2*d2);
                r = Math.exp(arg);
                t = x[0]*r - um_y[i];
                f += t*t;
            }
            break;
        case 4:
            /* Powell badly scaled function. */
            t1 = 1e4*x[0]*x[1] - 1.0;
            s1 = Math.exp(-x[0]);
            s2 = Math.exp(-x[1]);
            t2 = s1 + s2 - 1.0001;
            f = t1*t1 + t2*t2;
            break;
        case 5:
            /* Box 3-dimensional function. */
            f = 0.0;
            for (i = 1; i <= 10; ++i) {
                d1 = (double)i;
                d2 = d1/10.0;
                s1 = Math.exp(-d2*x[0]);
                s2 = Math.exp(-d2*x[1]);
                s3 = Math.exp(-d2) - Math.exp(-d1);
                t = s1 - s2 - s3*x[2];
                f += t*t;
            }
            break;
        case 6:
            /* Variably dimensioned function. */
            t1 = 0.0;
            t2 = 0.0;
            for (j = 0; j < n; ++j) {
                t1 += ((double)(j + 1))*(x[j] - 1.0);
                t = x[j] - 1.0;
                t2 += t*t;
            }
            t = t1*t1;
            f = t2 + t*(1.0 + t);
            break;
        case 7:
            /* Watson function. */
            f = 0.0;
            for (i = 1; i <= 29; ++i) {
                d1 = ((double)i)/29.0;
                s1 = 0.0;
                d2 = 1.0;
                for (j = 1; j < n; ++j) {
                    s1 += ((double)j)*d2*x[j];
                    d2 *= d1;
                }
                s2 = 0.0;
                d2 = 1.0;
                for (j = 0; j < n; ++j) {
                    s2 += d2*x[j];
                    d2 *= d1;
                }
                t = s1 - s2*s2 - 1.0;
                f += t*t;
            }
            t = x[0]*x[0];
            t1 = x[1] - t - 1.0;
            f += t + t1*t1;
            break;
        case 8:
            /* Penalty function I. */
            t1 = -0.25;
            t2 = 0.0;
            for (j = 0; j < n; ++j) {
                t1 += x[j]*x[j];
                t = x[j] - 1.0;
                t2 += t*t;
            }
            f = AP*t2 + BP*(t1*t1);
            break;
        case 9:
            /* Penalty function II. */
            t1 = -1.0;
            t2 =  0.0;
            t3 =  0.0;
            d1 =  Math.exp(0.1);
            d2 =  1.0;
            s2 =  0.0; /* avoid compiler warning about s2 used uninitialized */
            for (j = 0; j < n; ++j) {
                t1 += ((double)(n - j))*(x[j]*x[j]);
                s1 = Math.exp(x[j]/10.0);
                if (j > 0) {
                    s3 = s1 + s2 - d2*(d1 + 1.0);
                    t2 += s3*s3;
                    t = s1 - 1.0/d1;
                    t3 += t*t;
                }
                s2 = s1;
                d2 *= d1;
            }
            t = x[0] - 0.2;
            f = AP*(t2 + t3) + BP*(t1*t1 + t*t);
            break;
        case 10:
            /* Brown badly scaled function. */
            t1 = x[0] - 1e6;
            t2 = x[1] - 2e-6;
            t3 = x[0]*x[1] - 2.0;
            f = t1*t1 + t2*t2 + t3*t3;
            break;
        case 11:
            /* Brown and Dennis function. */
            f = 0.0;
            for (i = 1; i <= 20; ++i) {
                d1 = ((double)i)/5.0;
                d2 = Math.sin(d1);
                t1 = x[0] + d1*x[1] - Math.exp(d1);
                t2 = x[2] + d2*x[3] - Math.cos(d1);
                t = t1*t1 + t2*t2;
                f += t*t;
            }
            break;
        case 12:
            /* Gulf research and development function. */
            f = 0.0;
            d1 = 2.0/3.0;
            for (i = 1; i <= 99; ++i) {
                arg = ((double)i)/100.0;
                r = Math.abs(Math.pow(-50.0*Math.log(arg), d1) + 25.0 - x[1]);
                t1 = Math.pow(r, x[2])/x[0];
                t2 = Math.exp(-t1);
                t = t2 - arg;
                f += t*t;
            }
            break;
        case 13:
            /* Trigonometric function. */
            s1 = 0.0;
            for (j = 0; j < n; ++j) {
                s1 += Math.cos(x[j]);
            }
            f = 0.0;
            for (j = 0; j < n; ++j) {
                t = ((double)(n + 1 + j)) - Math.sin(x[j]) - s1
                        - ((double)(1 + j))*Math.cos(x[j]);
                f += t*t;
            }
            break;
        case 14:
            /* Extended Rosenbrock function. */
            f = 0.0;
            for (j = 0; j < n; j += 2) {
                t1 = 1.0 - x[j];
                t2 = 10.0*(x[j+1] - x[j]*x[j]);
                f += t1*t1 + t2*t2;
            }
            break;
        case 15:
            /* Extended Powell function. */
            f = 0.0;
            for (j = 0; j < n; j += 4) {
                t = x[j] + 10.0*x[j+1];
                t1 = x[j+2] - x[j+3];
                s1 = 5.0*t1;
                t2 = x[j+1] - 2.0*x[j+2];
                s2 = t2*t2*t2;
                t3 = x[j] - x[j+3];
                s3 = 10.0*(t3*t3*t3);
                f += t*t + s1*t1 + s2*t2 + s3*t3;
            }
            break;
        case 16:
            /* Beale function. */
            s1 = 1.0 - x[1];
            t1 = 1.5 - x[0]*s1;
            s2 = 1.0 - x[1]*x[1];
            t2 = 2.25 - x[0]*s2;
            s3 = 1.0 - x[1]*x[1]*x[1];
            t3 = 2.625 - x[0]*s3;
            f = t1*t1 + t2*t2 + t3*t3;
            break;
        case 17:
            /* Wood function. */
            s1 = x[1] - x[0]*x[0];
            s2 = 1.0 - x[0];
            s3 = x[1] - 1.0;
            t1 = x[3] - x[2]*x[2];
            t2 = 1.0 - x[2];
            t3 = x[3] - 1.0;
            d1 = s3 + t3;
            d2 = s3 - t3;
            f = (100.0*(s1*s1) + (s2*s2) + 90.0*(t1*t1) + (t2*t2)
                    + 10.0*(d1*d1) + (d2*d2)/10.0);
            break;
        case 18:
            /* Chebyquad function. */
            for (i = 0; i < n; ++i) {
                fvec[i] = 0.0;
            }
            for (j = 0; j < n; ++j) {
                t1 = 1.0;
                t2 = 2.0*x[j] - 1.0;
                t = 2.0*t2;
                for (i = 0; i < n; ++i) {
                    fvec[i] += t2;
                    th = t*t2 - t1;
                    t1 = t2;
                    t2 = th;
                }
            }
            f = 0.0;
            d1 = 1.0/((double) n);
            iev = -1;
            for (i = 0; i < n; ++i) {
                t = d1*fvec[i];
                if (iev > 0) {
                    r = (double)(i + 1);
                    t += 1.0/(r*r - 1.0);
                }
                f += t*t;
                iev = -iev;
            }
            break;
        default:
            f = 0.0;
        }
        return f;
    }

    /**
     * Computes the gradient of one of the nonlinear unconstrained
     * minimization problems.
     *
     * @param x       Variables of the problem.
     * @param g       Array to store the gradient.
     * @param prob    Problem number.
     */
    public static void umgrd(final double x[], double g[], int prob)
    {
        double arg, d1, d2, r, s1, s2, s3, t, t1, t2, t3, th;
        double[] fvec = new double[TEST18_NMAX];
        int i, j, n = x.length;
        int iev;

        /* Gradient routine selector. */
        switch (prob) {
        case 1:
            /* Helical valley function. */
            if      (x[0] > 0.0) th = Math.atan(x[1]/x[0])/TPI;
            else if (x[0] < 0.0) th = Math.atan(x[1]/x[0])/TPI + 0.5;
            else                 th = (x[1] >= 0.0 ? 0.25 : -0.25);
            arg = x[0]*x[0] + x[1]*x[1];
            r = Math.sqrt(arg);
            t = x[2] - 10.0*th;
            s1 = 10.0*t/(TPI*arg);
            g[0] = 200.0*(x[0] - x[0]/r + x[1]*s1);
            g[1] = 200.0*(x[1] - x[1]/r - x[0]*s1);
            g[2] = 2.0*(100.0*t + x[2]);
            break;
        case 2:
            /* Biggs exp6 function. */
            for (j = 0; j < 6; ++j) g[j] = 0.0;
            for (i = 1; i <= 13; ++i) {
                d1 = ((double)i)/10.0;
                d2 = Math.exp(-d1) - 5.0*Math.exp(-10.0*d1) + 3.0*Math.exp(-4.0*d1);
                s1 = Math.exp(-d1*x[0]);
                s2 = Math.exp(-d1*x[1]);
                s3 = Math.exp(-d1*x[4]);
                t = x[2]*s1 - x[3]*s2 + x[5]*s3 - d2;
                th = d1*t;
                g[0] -= s1*th;
                g[1] += s2*th;
                g[2] += s1*t;
                g[3] -= s2*t;
                g[4] -= s3*th;
                g[5] += s3*t;
            }
            g[0] *= 2.0*x[2];
            g[1] *= 2.0*x[3];
            g[2] *= 2.0;
            g[3] *= 2.0;
            g[4] *= 2.0*x[5];
            g[5] *= 2.0;
            break;
        case 3:
            /* Gaussian function. */
            g[0] = 0.0;
            g[1] = 0.0;
            g[2] = 0.0;
            for (i = 0; i < 15; ++i) {
                d1 = 0.5*((double)i);
                d2 = 3.5 - d1 - x[2];
                arg = -0.5*x[1]*(d2*d2);
                r = Math.exp(arg);
                t = x[0]*r - um_y[i];
                s1 = r*t;
                s2 = d2*s1;
                g[0] += s1;
                g[1] -= d2*s2;
                g[2] += s2;
            }
            g[0] *= 2.0;
            g[1] *= x[0];
            g[2] *= 2.0*x[0]*x[1];
            break;
        case 4:
            /* Powell badly scaled function. */
            t1 = 1e4*x[0]*x[1] - 1.0;
            s1 = Math.exp(-x[0]);
            s2 = Math.exp(-x[1]);
            t2 = s1 + s2 - 1.0001;
            g[0] = 2.0*(1e4*x[1]*t1 - s1*t2);
            g[1] = 2.0*(1e4*x[0]*t1 - s2*t2);
            break;
        case 5:
            /* Box 3-dimensional function. */
            g[0] = 0.0;
            g[1] = 0.0;
            g[2] = 0.0;
            for (i = 1; i <= 10; ++i) {
                d1 = (double)i;
                d2 = d1/10.0;
                s1 = Math.exp(-d2*x[0]);
                s2 = Math.exp(-d2*x[1]);
                s3 = Math.exp(-d2) - Math.exp(-d1);
                t = s1 - s2 - s3*x[2];
                th = d2*t;
                g[0] -= s1*th;
                g[1] += s2*th;
                g[2] -= s3*t;
            }
            g[0] *= 2.0;
            g[1] *= 2.0;
            g[2] *= 2.0;
            break;
        case 6:
            /* Variably dimensioned function. */
            t1 = 0.0;
            for (j = 0; j < n; ++j) {
                t1 += ((double)(j+1))*(x[j] - 1.0);
            }
            t = t1*(1.0 + 2.0*(t1*t1));
            for (j = 0; j < n; ++j) {
                g[j] = 2.0*(x[j] - 1.0 + ((double)(j+1))*t);
            }
            break;
        case 7:
            /* Watson function. */
            for (j = 0; j < n; ++j) {
                g[j] = 0.0;
            }
            for (i = 1; i <= 29; ++i) {
                d1 = ((double)i)/29.0;
                s1 = 0.0;
                d2 = 1.0;
                for (j = 1; j < n; ++j) {
                    s1 += ((double)j)*d2*x[j];
                    d2 *= d1;
                }
                s2 = 0.0;
                d2 = 1.0;
                for (j = 0; j < n; ++j) {
                    s2 += d2*x[j];
                    d2 *= d1;
                }
                t = s1 - s2*s2 - 1.0;
                s3 = 2.0*d1*s2;
                d2 = 2.0/d1;
                for (j = 0; j < n; ++j) {
                    g[j] += d2*(((double)j) - s3)*t;
                    d2 *= d1;
                }
            }
            t1 = x[1] - x[0]*x[0] - 1.0;
            g[0] += x[0]*(2.0 - 4.0*t1);
            g[1] += 2.0*t1;
            break;
        case 8:
            /* Penalty function I. */
            t1 = -0.25;
            for (j = 0; j < n; ++j) {
                t1 += x[j]*x[j];
            }
            d1 = 2.0*AP;
            th = 4.0*BP*t1;
            for (j = 0; j < n; ++j) {
                g[j] = d1*(x[j] - 1.0) + x[j]*th;
            }
            break;
        case 9:
            /* Penalty function II. */
            s2 = 0.0; /* avoid compiler warning about s2 used uninitialized */
            t1 = -1.0;
            for (j = 0; j < n; ++j) {
                t1 += ((double)(n - j))*(x[j]*x[j]);
            }
            d1 = Math.exp(0.1);
            d2 = 1.0;
            th = 4.0*BP*t1;
            for (j = 0; j < n; ++j) {
                g[j] = ((double)(n - j))*x[j]*th;
                s1 = Math.exp(x[j]/10.0);
                if (j > 0) {
                    s3 = s1 + s2 - d2*(d1 + 1.0);
                    g[j] += AP*s1*(s3 + s1 - 1.0/d1)/5.0;
                    g[j-1] += AP*s2*s3/5.0;
                }
                s2 = s1;
                d2 *= d1;
            }
            g[0] += 2.0*BP*(x[0] - 0.2);
            break;
        case 10:
            /* Brown badly scaled function. */
            t1 = x[0] - 1e6;
            t2 = x[1] - 2e-6;
            t3 = x[0]*x[1] - 2.0;
            g[0] = 2.0*(t1 + x[1]*t3);
            g[1] = 2.0*(t2 + x[0]*t3);
            break;
        case 11:
            /* Brown and Dennis function. */
            g[0] = 0.0;
            g[1] = 0.0;
            g[2] = 0.0;
            g[3] = 0.0;
            for (i = 1; i <= 20; ++i) {
                d1 = ((double)i)/5.0;
                d2 = Math.sin(d1);
                t1 = x[0] + d1*x[1] - Math.exp(d1);
                t2 = x[2] + d2*x[3] - Math.cos(d1);
                t = t1*t1 + t2*t2;
                s1 = t1*t;
                s2 = t2*t;
                g[0] += s1;
                g[1] += d1*s1;
                g[2] += s2;
                g[3] += d2*s2;
            }
            g[0] *= 4.0;
            g[1] *= 4.0;
            g[2] *= 4.0;
            g[3] *= 4.0;
            break;
        case 12:
            /* Gulf research and development function. */
            g[0] = 0.0;
            g[1] = 0.0;
            g[2] = 0.0;
            d1 = 2.0/3.0;
            for (i = 1; i <= 99; ++i) {
                arg = ((double)i)/100.0;
                r = Math.abs(Math.pow(-50.0*Math.log(arg), d1) + 25.0 - x[1]);
                t1 = Math.pow(r, x[2])/x[0];
                t2 = Math.exp(-t1);
                t = t2 - arg;
                s1 = t1*t2*t;
                g[0] += s1;
                g[1] += s1/r;
                g[2] -= s1*Math.log(r);
            }
            g[0] *= 2.0/x[0];
            g[1] *= 2.0*x[2];
            g[2] *= 2.0;
            break;
        case 13:
            /* Trigonometric function. */
            s1 = 0.0;
            for (j = 0; j < n; ++j) {
                g[j] = Math.cos(x[j]);
                s1 += g[j];
            }
            s2 = 0.0;
            for (j = 0; j < n; ++j) {
                th = Math.sin(x[j]);
                t = ((double)(n+1+j)) - th - s1 - ((double)(1+j))*g[j];
                s2 += t;
                g[j] = (((double)(1+j))*th - g[j])*t;
            }
            for (j = 0; j < n; ++j) {
                g[j] = 2.0*(g[j] + Math.sin(x[j])*s2);
            }
            break;
        case 14:
            /* Extended Rosenbrock function. */
            for (j = 0; j < n; j += 2) {
                t1 = 1.0 - x[j];
                g[j+1] = 200.0*(x[j+1] - x[j]*x[j]);
                g[j] = -2.0*(x[j]*g[j+1] + t1);
            }
            break;
        case 15:
            /* Extended Powell function. */
            for (j = 0; j < n; j += 4) {
                t = x[j] + 10.0*x[j+1];
                t1 = x[j+2] - x[j+3];
                s1 = 5.0*t1;
                t2 = x[j+1] - 2.0*x[j+2];
                s2 = 4.0*(t2*t2*t2);
                t3 = x[j] - x[j+3];
                s3 = 20.0*(t3*t3*t3);
                g[j] = 2.0*(t + s3);
                g[j+1] = 20.0*t + s2;
                g[j+2] = 2.0*(s1 - s2);
                g[j+3] = -2.0*(s1 + s3);
            }
            break;
        case 16:
            /* Beale function. */
            s1 = 1.0 - x[1];
            t1 = 1.5 - x[0]*s1;
            s2 = 1.0 - x[1]*x[1];
            t2 = 2.25 - x[0]*s2;
            s3 = 1.0 - x[1]*x[1]*x[1];
            t3 = 2.625 - x[0]*s3;
            g[0] = -2.0*(s1*t1 + s2*t2 + s3*t3);
            g[1] = 2.0*x[0]*(t1 + x[1]*(2.0*t2 + 3.0*x[1]*t3));
            break;
        case 17:
            /* Wood function. */
            s1 = x[1] - x[0]*x[0];
            s2 = 1.0 - x[0];
            s3 = x[1] - 1.0;
            t1 = x[3] - x[2]*x[2];
            t2 = 1.0 - x[2];
            t3 = x[3] - 1.0;
            g[0] = -2.0*(200.0*x[0]*s1 + s2);
            g[1] = 200.0*s1 + 20.2*s3 + 19.8*t3;
            g[2] = -2.0*(180.0*x[2]*t1 + t2);
            g[3] = 180.0*t1 + 20.2*t3 + 19.8*s3;
            break;
        case 18:
            /* Chebyquad function. */
            for (i = 0; i < n; ++i) {
                fvec[i] = 0.0;
            }
            for (j = 0; j < n; ++j) {
                t1 = 1.0;
                t2 = 2.0*x[j] - 1.0;
                t = 2.0*t2;
                for (i = 0; i < n; ++i) {
                    fvec[i] += t2;
                    th = t*t2 - t1;
                    t1 = t2;
                    t2 = th;
                }
            }
            d1 = 1.0/((double)n);
            iev = -1;
            for (i = 0; i < n; ++i) {
                fvec[i] *= d1;
                if (iev > 0) {
                    r = (double)(i + 1);
                    fvec[i] += 1.0/(r*r - 1.0);
                }
                iev = -iev;
            }
            for (j = 0; j < n; ++j) {
                g[j] = 0.0;
                t1 = 1.0;
                t2 = 2.0*x[j] - 1.0;
                t = 2.0*t2;
                s1 = 0.0;
                s2 = 2.0;
                for (i = 0; i < n; ++i) {
                    g[j] += fvec[i]*s2;
                    th = 4.0*t2 + t*s2 - s1;
                    s1 = s2;
                    s2 = th;
                    th = t*t2 - t1;
                    t1 = t2;
                    t2 = th;
                }
            }
            d2 = 2.0*d1;
            for (j = 0; j < n; ++j) {
                g[j] *= d2;
            }
            break;
        default:
            for (j = 0; j < n; ++j) {
                g[j] = 0.0;
            }
        }
    }

}

/*
 * Local Variables:
 * mode: java
 * tab-width: 8
 * indent-tabs-mode: nil
 * c-basic-offset: 2
 * fill-column: 79
 * coding: utf-8
 * End:
 */

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
