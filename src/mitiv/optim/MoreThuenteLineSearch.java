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

/**
 * Moré & Thuente inexact line search based on cubic interpolation.
 *
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */
package mitiv.optim;

// FIXME: deal with overflows

public class MoreThuenteLineSearch extends LineSearch {

    /* Convergence parameters. */
    private double ftol = 0.0;
    private double gtol = 0.0;
    private double xtol = 0.0;

    /* GTEST is used to check for Wolfe conditions. */
    private double gtest = 0.0;

    /* The variables STX, FX, GX contain the values of the step, function, and
       derivative at the best step. */
    double stx, fx, gx;

    /* The variables STY, FY, GY contain the value of the step, function, and
       derivative at STY. */
    double sty, fy, gy;

    /* Parameters to track the interval where to seek for the step. */
    private double stmin = 0.0; // FIXME: merge with stpmin?
    private double stmax = 0.0; // FIXME: merge with stpmax?
    private double width = 0.0;
    private double width1 = 0.0;
    private boolean brackt = false;
    private double[] ws;

    /* The algorithm has two different stages. */
    private int stage = 0;

    public MoreThuenteLineSearch(double ftol, double gtol, double xtol)
    {
        /* FIXME: check arguments */
        ws = new double[9];
        this.ftol = Math.max(ftol, 0.0);
        this.gtol = Math.max(gtol, 0.0);
        this.xtol = Math.max(xtol, 0.0);
    }

    @Override
    public boolean useDerivative()
    {
        return true;
    }

    @Override
    protected void startHook() {
        /* Convergence threshold for this step. */
        gtest = ftol*ginit;

        /* Initialize parameters for the interval of search. */
        stmin = stpmin;
        stmax = stpmax;
        width = stpmax - stpmin;
        width1 = 2.0*width;
        brackt = false;

        /* The variables STX, FX, GX contain the values of the step,
           function, and derivative at the best step. */
        stx = 0.0;
        fx = finit;
        gx = ginit;

        /* The variables STY, FY, GY contain the value of the step,
           function, and derivative at STY. */
        sty = 0.0;
        fy = finit;
        gy = ginit;

        /* Algorithm starts with STAGE = 1. */
        stage = 1;
        success(LineSearchTask.SEARCH);
    }

    @Override
    protected void iterateHook(double f, double g)
    {
        /* Test for convergence. */
        double ftest = finit + stp*gtest;
        if (f <= ftest && Math.abs(g) <= -gtol*ginit) {
            /* Strong Wolfe conditions satisfied. */
            success(LineSearchTask.CONVERGENCE);
            return;
        }

        /* Test for warnings. */
        if (stp == stpmin && (f > ftest || g >= gtest)) {
            failure(OptimStatus.STEP_EQ_STPMIN);
            return;
        }
        if (stp == stpmax && f <= ftest && g <= gtest) {
            warning(OptimStatus.STEP_EQ_STPMAX);
            return;
        }
        if (brackt && stmax - stmin <= xtol*stmax) {
            warning(OptimStatus.XTOL_TEST_SATISFIED);
            return;
        }
        if (brackt && (stp <= stmin || stp >= stmax)) {
            warning(OptimStatus.ROUNDING_ERRORS_PREVENT_PROGRESS);
            return;
        }

        /* If psi(stp) <= 0 and f'(stp) >= 0 for some step, then the
           algorithm enters the second stage. */
        if (stage == 1 && f <= ftest && g >= 0.0) {
            stage = 2;
        }

        /* A modified function is used to predict the step during the first stage if
         a lower function value has been obtained but the decrease is not
         sufficient. */
        if (this.stage == 1 && f <= this.fx && f > ftest) {
            /* Define the modified function and derivative values and call CSTEP to
             update STX, STY, and to compute the new step.  Then restore the
             function and derivative values for F. */
            ws[0] = stx;
            ws[1] = fx - gtest*stx;
            ws[2] = gx - gtest;
            ws[3] = sty;
            ws[4] = fy - gtest*sty;
            ws[5] = gy - gtest;
            ws[6] = stp;
            ws[7] = f - gtest*stp;
            ws[8] = g - gtest;
            OptimStatus result = cstep();
            if (result != OptimStatus.SUCCESS) {
                failure(result);
                return;
            }
            stx = ws[0];
            fx  = ws[1] + gtest*stx;
            gx  = ws[2] + gtest;
            sty = ws[3];
            fy  = ws[4] + gtest*sty;
            gy  = ws[5] + gtest;
            stp = ws[6];
        } else {
            /* Call CSTEP to update STX, STY, and to compute the new step. */
            ws[0] = stx;
            ws[1] = fx;
            ws[2] = gx;
            ws[3] = sty;
            ws[4] = fy;
            ws[5] = gy;
            ws[6] = stp;
            ws[7] = f;
            ws[8] = g;
            OptimStatus result = cstep();
            if (result != OptimStatus.SUCCESS) {
                failure(result);
                return;
            }
            stx = ws[0];
            fx  = ws[1];
            gx  = ws[2];
            sty = ws[3];
            fy  = ws[4];
            gy  = ws[5];
            stp = ws[6];
        }

        /* Decide if a bisection step is needed. */
        if (this.brackt) {
            double new_width = Math.abs(this.sty - this.stx);
            if (new_width >= 0.66*this.width1) {
                stp = this.stx + 0.5*(this.sty - this.stx);
            }
            this.width1 = this.width;
            this.width = new_width;
        }

        /* Set the minimum and maximum steps allowed for stp. */
        if (this.brackt) {
            this.stmin = Math.min(this.stx, this.sty);
            this.stmax = Math.max(this.stx, this.sty);
        } else {
            this.stmin = stp + (stp - this.stx)*1.1;
            this.stmax = stp + (stp - this.stx)*4.0;
        }

        /* Force the step to be within the bounds stpmax and stpmin. */
        stp = Math.max(stp, this.stpmin);
        stp = Math.min(stp, this.stpmax);

        /* If further progress is not possible, let stp be the best
         point obtained during the search. */
        if (this.brackt && (stp <= this.stmin || stp >= this.stmax ||
                this.stmax - this.stmin <= this.xtol * this.stmax)) {
            stp = this.stx;
        }

        /* Obtain another function and derivative. */
        success(LineSearchTask.SEARCH);
    }


    private final static double max3(double val1, double val2, double val3)
    {
        double result = val1;
        if (val2 > result) result = val2;
        if (val3 > result) result = val3;
        return result;
    }

    private final OptimStatus cstep()
    {
        /* Constants. */
        final double ZERO = 0.0;
        final double TWO = 2.0;
        final double THREE = 3.0;

        /* Extract curve parameters. */
        double stx = ws[0];
        double fx  = ws[1];
        double dx  = ws[2];
        double sty = ws[3];
        double fy  = ws[4];
        double dy  = ws[5];
        double stp = ws[6];
        double fp  = ws[7];
        double dp  = ws[8];

        /* Local variables. */
        double gamma, theta, p, q, r, s, temp;
        double stpc; /* cubic step */
        double stpq; /* quadratic step */
        double stpf;
        boolean opposite;

        /* Check the input parameters for errors. */
        if (brackt && (stx < sty ? (stp <= stx || stp >= sty)
                : (stp <= sty || stp >= stx))) {
            return OptimStatus.STEP_OUTSIDE_BRACKET;
        } else if (dx*(stp - stx) >= ZERO) {
            return OptimStatus.NOT_A_DESCENT;
        } else if (stpmin > stpmax) {
            return OptimStatus.STPMIN_GT_STPMAX;
        }

        /* Determine if the derivatives have opposite signs. */
        opposite = ((dp < ZERO && dx > ZERO) || (dp > ZERO && dx < ZERO));

        if (fp > fx) {
            /* First case.  A higher function value.  The minimum is bracketed.  If
               the cubic step is closer to STX than the quadratic step, the cubic step
               is taken, otherwise the average of the cubic and quadratic steps is
               taken. */
            brackt = true;
            theta = THREE*(fx - fp)/(stp - stx) + dx + dp;
            s = max3(Math.abs(theta), Math.abs(dx), Math.abs(dp));
            temp = theta/s;
            gamma = s*Math.sqrt(temp*temp - (dx/s)*(dp/s));
            if (stp < stx) gamma = -gamma;
            p =  (gamma - dx) + theta;
            q = ((gamma - dx) + gamma) + dp;
            r = p/q;
            stpc = stx + r*(stp - stx);
            stpq = stx + ((dx/((fx - fp)/(stp - stx) + dx))/TWO)*(stp - stx);
            if (Math.abs(stpc - stx) < Math.abs(stpq - stx)) {
                stpf = stpc;
            } else {
                stpf = stpc + (stpq - stpc)/TWO;
            }
        } else if (opposite) {
            /* Second case.  A lower function value and derivatives of opposite sign.
               The minimum is bracketed.  If the cubic step is farther from STP than
               the secant (quadratic) step, the cubic step is taken, otherwise the
               secant step is taken. */
            brackt = true;
            theta = THREE*(fx - fp)/(stp - stx) + dx + dp;
            s = max3(Math.abs(theta), Math.abs(dx), Math.abs(dp));
            temp = theta/s;
            gamma = s*Math.sqrt(temp*temp - (dx/s)*(dp/s));
            if (stp > stx) gamma = -gamma;
            p =  (gamma - dp) + theta;
            q = ((gamma - dp) + gamma) + dx;
            r = p/q;
            stpc = stp + r*(stx - stp);
            stpq = stp + (dp/(dp - dx))*(stx - stp);
            if (Math.abs(stpc - stp) > Math.abs(stpq - stp)) {
                stpf = stpc;
            } else {
                stpf = stpq;
            }
        } else if (Math.abs(dp) < Math.abs(dx)) {
            /* Third case.  A lower function value, derivatives of the same sign, and
               the magnitude of the derivative decreases.  The cubic step is computed
               only if the cubic tends to infinity in the direction of the step or if
               the minimum of the cubic is beyond STP.  Otherwise the cubic step is
               defined to be the secant step.  The case GAMMA = 0 only arises if the
               cubic does not tend to infinity in the direction of the step. */
            theta = THREE*(fx - fp)/(stp - stx) + dx + dp;
            s = max3(Math.abs(theta), Math.abs(dx), Math.abs(dp));
            temp = theta/s;
            temp = temp*temp - (dx/s)*(dp/s);
            if (temp > ZERO) {
                gamma = s*Math.sqrt(temp);
                if (stp > stx) gamma = -gamma;
            } else {
                gamma = ZERO;
            }
            p = (gamma - dp) + theta;
            q = (gamma + (dx - dp)) + gamma;
            r = p/q;
            if (r < ZERO && gamma != ZERO) {
                stpc = stp + r*(stx - stp);
            } else if (stp > stx) {
                stpc = stpmax;
            } else {
                stpc = stpmin;
            }
            stpq = stp + (dp/(dp - dx))*(stx - stp);

            if (brackt) {
                /* A minimizer has been bracketed.  If the cubic step is closer to STP
                   than the secant step, the cubic step is taken, otherwise the secant
                   step is taken. */
                if (Math.abs(stpc - stp) < Math.abs(stpq - stp)) {
                    stpf = stpc;
                } else {
                    stpf = stpq;
                }
                temp = stp + 0.66*(sty - stp);
                if (stp > stx ? stpf > temp : stpf < temp) {
                    stpf = temp;
                }
            } else {
                /* A minimizer has not been bracketed. If the cubic step is farther from
                   stp than the secant step, the cubic step is taken, otherwise the
                   secant step is taken. */
                if (Math.abs(stpc - stp) > Math.abs(stpq - stp)) {
                    stpf = stpc;
                } else {
                    stpf = stpq;
                }
                if (stpf > stpmax) stpf = stpmax;
                if (stpf < stpmin) stpf = stpmin;
            }
        } else {
            /* Fourth case.  A lower function value, derivatives of the same sign, and
               the magnitude of the derivative does not decrease.  If the minimum is
               not bracketed, the step is either STPMIN or STPMAX, otherwise the cubic
               step is taken. */
            if (brackt) {
                theta = THREE*(fp - fy)/(sty - stp) + dy + dp;
                s = max3(Math.abs(theta), Math.abs(dy), Math.abs(dp));
                temp = theta/s;
                gamma = s*Math.sqrt(temp*temp - (dy/s)*(dp/s));
                if (stp > sty) gamma = -gamma;
                p =  (gamma - dp) + theta;
                q = ((gamma - dp) + gamma) + dy;
                r = p/q;
                stpc = stp + r*(sty - stp);
                stpf = stpc;
            } else if (stp > stx) {
                stpf = stpmax;
            } else {
                stpf = stpmin;
            }
        }

        /* Update the interval which contains a minimizer. */
        if (fp > fx) {
            sty = stp;
            fy = fp;
            dy = dp;
        } else {
            if (opposite) {
                sty = stx;
                fy = fx;
                dy = dx;
            }
            stx = stp;
            fx = fp;
            dx = dp;
        }

        /* Store the new safeguarded step and other parameters. */
        ws[0] = stx;
        ws[1] = fx;
        ws[2] = dx;
        ws[3] = sty;
        ws[4] = fy;
        ws[5] = dy;
        ws[6] = stpf; /* stp */
        return OptimStatus.SUCCESS;
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
