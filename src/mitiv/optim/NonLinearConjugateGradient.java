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

package mitiv.optim;

import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.tests.MinPack1Tests;

/**
 * Non-linear conjugate gradient methods.
 *
 * References: [1] Hestenes, M.R. & Stiefel, E., "Methods of Conjugate Gradients
 * for Solving Linear Systems," Journal of Research of the National Bureau of
 * Standards 49, 409-436 (1952).
 *
 * [2] Hager, W.W. & Zhang, H., "A survey of nonlinear conjugate gradient
 * methods," Pacific Journal of Optimization, Vol. 2, pp. 35-58 (2006).
 *
 * [3] Hager, W. W. & Zhang, H. "A New Conjugate Gradient Method with Guaranteed
 * Descent and an Efficient Line Search," SIAM J. Optim., Vol. 16, pp. 170-192
 * (2005).
 *
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */
public class NonLinearConjugateGradient
extends ReverseCommunicationOptimizerWithLineSearch {

    public static final double STPMIN  = 1.0E-20;
    public static final double STPMAX  = 1.0E+20;
    public static final double DELTA   = 5.0E-2;
    public static final double EPSILON = 1.0E-2;

    /* Non-linear optimization method.  POWELL (forcing beta to be
       non-negative) and SHANNO_PHUA (to use their formula in CONMIN to
       estimate the step size) can be combined to the chosen method. */
    public static final int FLETCHER_REEVES = 1;
    public static final int HESTENES_STIEFEL = 2;
    public static final int POLAK_RIBIERE_POLYAK = 3;
    public static final int FLETCHER = 4;
    public static final int LIU_STOREY = 5;
    public static final int DAI_YUAN = 6;
    public static final int PERRY_SHANNO = 7;
    public static final int HAGER_ZHANG = 8;
    public static final int POWELL = (1 << 8); /* force beta >= 0 */
    public static final int SHANNO_PHUA = (1 << 9); /* compute scale from previous
                                                    iteration */
    /* For instance: (POLAK_RIBIERE_POLYAK | POWELL) merely
       corresponds to PRP+ (Polak, Ribiere, Polyak) while (PERRY_SHANNO |
       SHANNO_PHUA) merely corresponds to the conjugate gradient method
       implemented in CONMIN. */

    /* Default settings for non linear conjugate gradient (should correspond to
       the method which is, in general, the most successful). */
    public static final int DEFAULT_METHOD = (POLAK_RIBIERE_POLYAK | POWELL | SHANNO_PHUA);

    private double f0;      /* Function value at the start of the line search. */
    private double g0norm;  /* Euclidean norm of G0, the gradient at the start of the
                                line search. */
    private double gnorm;   /* Euclidean norm of G, the gradient of the last accepted point. */
    private double dtg0;    /* Directional derivative at the start of the line search;
                               given by the inner product: -<d,g0> */
    private double dtg;     /* Directional derivative at the last trial point;
                              given by the inner product: -<d,g> */
    private double grtol;   /* Relative threshold for the norm or the gradient (relative
                                to GTEST the norm of the initial gradient) for convergence. */
    private double gatol;   /* Absolute threshold for the norm or the gradient for
                               convergence. */
    private double ginit;   /* Norm or the initial gradient. */
    private double fmin;    /* Minimal function value if provided. */
    private double delta;   /* Relative size for a small step. */
    private double epsilon; /* Threshold to accept descent direction. */
    private double alpha;   /* Current step length. */
    private double beta;    /* Current parameter in conjugate gradient update rule (for
                               information). */
    private final double stpmin; /* Relative lower bound for the step length. */
    private final double stpmax; /* Relative upper bound for the step length. */
    private final VectorSpace vsp;
    private final Vector x0; /* Variables at start of line search. */
    private final Vector g0; /* Gradient at start of line search. */
    private final Vector d;  /* (Anti-)search direction, new iterate is searched
                                 as: x1 = x0 - alpha*d, for alpha >= 0. */
    private final Vector y;  /* Work vector (e.g., to store the gradient difference:
                                Y = G - G0). */
    private final int method; /* Conjugate gradient method. */
    private boolean fmin_given; /* Indicate whether FMIN is specified. */
    private final boolean update_Hager_Zhang_orig = false;

    private static double max(double a1, double a2, double a3) {
        if (a3 >= a2) {
            return (a3 >= a1 ? a3 : a1);
        } else {
            return (a2 >= a1 ? a2 : a1);
        }
    }

    public NonLinearConjugateGradient(VectorSpace vsp) {
        this(vsp, DEFAULT_METHOD);
    }

    public NonLinearConjugateGradient(VectorSpace vsp, int method) {
        /* FIXME: choose more suitable values (e.g., in CG+: FTOL=1E-4, GTOL=1E-1,
        not less than 1E-4, XTOL=1E-17, STPMIN=1E-20 STPMAX=1E+20 and MAXFEV=40) */
        this(vsp, method, new MoreThuenteLineSearch(/* sftol */ 0.05,
                /* sgtol */ 0.1, /* sxtol */ 1E-17));
    }

    public NonLinearConjugateGradient(VectorSpace vsp, int method,
            LineSearch lnsrch) {
        /* Check the input arguments for errors. */
        boolean g0_needed, y_needed;
        if (vsp == null) {
            throw new IllegalArgumentException("illegal null vector space");
        }
        if (method == 0) {
            method = DEFAULT_METHOD;
        }
        switch ((method & 0xff)) {
        case FLETCHER_REEVES:
            g0_needed = false;
            y_needed = false;
            break;
        case HESTENES_STIEFEL:
            g0_needed = true;
            y_needed = true;
            break;
        case POLAK_RIBIERE_POLYAK:
            g0_needed = true;
            y_needed = true;
            break;
        case FLETCHER:
            g0_needed = false;
            y_needed = false;
            break;
        case LIU_STOREY:
            g0_needed = true;
            y_needed = true;
            break;
        case DAI_YUAN:
            g0_needed = true;
            y_needed = true;
            break;
        case PERRY_SHANNO:
            g0_needed = true;
            y_needed = true;
            break;
        case HAGER_ZHANG:
            g0_needed = true;
            y_needed = true;
            break;
        default:
            throw new IllegalArgumentException("illegal method value");
        }

        /* We allocate work vectors. */
        this.vsp = vsp; /* must be set early in case of errors */
        unsetFMin();
        this.method = method;
        this.lnsrch = lnsrch;
        this.grtol = 1e-3;
        this.gatol = 0.0;
        this.ginit = 0.0;
        this.stpmin = STPMIN;
        this.stpmax = STPMAX;
        this.delta = DELTA;
        this.epsilon = EPSILON;
        this.x0 = vsp.create();
        this.g0 = (g0_needed ? vsp.create() : null);
        this.d = vsp.create();
        this.y = (y_needed ? vsp.create() : null);
        this.evaluations = 0;
        failure(OptimStatus.NOT_STARTED);
    }

    /*
     * Most non-linear conjugate gradient methods, the new search direction
     * is updated by the following rule:
     *
     *     d' = -g + beta*d
     *
     * with d' the new search direction, g the current gradient, d the previous
     * search direction and beta a parameter which depends on the method.
     *
     * Some methods (e.g., Perry & Shanno) implement the following rule:
     *
     *     d' = (-g + beta*d + gamma*y)*delta
     *
     * with y = g - g0.
     *
     * For us, d is rather an anti-search direction thus:
     *
     *     d' = g + beta*d
     */

    /* Helper function to compute anti-search direction as: d' = g + beta*d. */
    private int update0(Vector g, double beta) {
        this.beta = beta;
        if (this.beta != 0.0) {
            vsp.combine(1.0, g, beta, d);
            return SUCCESS;
        } else {
            return FAILURE;
        }
    }

    /* Helper function to compute search direction as: d' = g + beta*d
       possibly with the constraint that beta > 0. */
    private int update1(Vector g, double beta) {
        if ((method & POWELL) == POWELL && beta < 0.0) {
            ++restarts;
            this.beta = 0.0;
        } else {
            this.beta = beta;
        }
        if (this.beta != 0.0) {
            vsp.combine(1.0, g, beta, d, d);
            return SUCCESS;
        } else {
            return FAILURE;
        }
    }

    /* Form: Y = G - G0 */
    private void form_y(Vector g) {
        vsp.combine(1.0, g, -1.0, g0, y);
    }

    /*
     * For Hestenes & Stiefel method:
     *
     *     beta = <g,y>/<d,y> = - <g,y>/<p,y>
     *
     * with y = g - g0.
     */
    private int update_Hestenes_Stiefel(Vector x, Vector g) {
        form_y(g);
        double gty =  g.dot(y);
        double dty = -d.dot(y);
        double beta = (dty != 0.0 ? gty/dty : 0.0);
        return update1(g, beta);
    }

    /*
     * For Fletcher & Reeves method:
     *
     *     beta = <g,g>/<g0,g0>
     *
     * (this value is always >= 0 and can only be zero at a stationnary point).
     */
    private int update_Fletcher_Reeves(Vector x, Vector g) {
        double r = gnorm/g0norm;
        return update0(g, r*r);
    }

    /*
     * For Polak-Ribière-Polyak method:
     *
     *     beta = <g1,y>/<g0,g0>
     */
    private int update_Polak_Ribiere_Polyak(Vector x, Vector g) {
        form_y(g);
        double beta = (g.dot(y)/g0norm)/g0norm;
        return update1(g, beta);
    }

    /*
     * For Fletcher "Conjugate Descent" method:
     *
     *     beta = -<g,g>/<d,g0>
     *
     * (this value is always >= 0 and can only be zero at a stationary point).
     */
    private int update_Fletcher(Vector x, Vector g) {
        double beta = -gnorm*(gnorm/dtg0);
        return update0(g, beta);
    }

    /*
     * For Liu & Storey method:
     *
     *     beta = -<g,y>/<d,g0>
     */
    private int update_Liu_Storey(Vector x, Vector g) {
        form_y(g);
        double gty = g.dot(y);
        double beta = -gty/dtg0;
        return update1(g, beta);
    }

    /*
     * For Dai & Yuan method:
     *
     *     beta = <g,g>/<d,y>
     */
    private int update_Dai_Yuan(Vector x, Vector g) {
        form_y(g);
        double dty = -d.dot(y);
        double beta = (dty != 0.0 ? gnorm*(gnorm/dty) : 0.0);
        return update1(g, beta);
    }

    /*
     * For Hager & Zhang method:
     *
     *     beta = <y - (2*<y,y>/<d,y>)*d,g>
     *          = (<g,y> - 2*<y,y>*<d,g>/<d,y>)/<d,y>
     */
    private int update_Hager_Zhang(Vector x, Vector g) {
        form_y(g);
        double dty = -d.dot(y);
        double beta;
        if (dty != 0.0) {
            if (update_Hager_Zhang_orig) {
                /* Original formulation, using Y as a scratch vector. */
                double q = 1.0/dty;
                double r = q*vsp.norm2(y);
                vsp.combine(q, y, 2.0*r*r, d, y);
                beta = y.dot(g);
            } else {
                /* Improved formulation which spares one linear combination and thus has
                   less overhead (only 3 scalar products plus 2 linear combinations
                   instead of 3 scalar products and 3 linear combinations).  The rounding
                   errors are however different, so one or the other formulation can be by
                   chance more efficient.  Though there is no systematic trend. */
                double ytg = y.dot(g);
                double ynorm = vsp.norm2(y);
                beta = (ytg - 2.0*(ynorm/dty)*ynorm*dtg)/dty;
            }
        } else {
            beta = 0.0;
        }
        return update1(g, beta);
    }

    /* Perry & Shanno, update rule (used in CONMIN and see Eq. (1.4) in [2])
     * writes:
     *
     *     d' = alpha*(-c1*g1 + c2*d - c3*y)  ==>   p' = c1*g1 + c2*p + c3*y
     *
     *     c1 = (1/alpha)*<s,y>/<y,y>
     *        =  <d,y>/<y,y>
     *        = -<p,y>/<y,y>
     *
     *     c2 = <g1,y>/<y,y> - 2*<s,g1>/<s,y>
     *	  = <g1,y>/<y,y> - 2*<d,g1>/<d,y>
     *	  = <g1,y>/<y,y> - 2*<p,g1>/<p,y>
     *
     *     c3 = -(1/alpha)*<s,g1>/<y,y>
     *        = -<d,g1>/<y,y>
     *        =  <p,g1>/<y,y>
     *
     * with alpha the step length, s = x1 - x0 = alpha*d = -alpha*p.  For this
     * method, beta = c2/c1.
     */
    private int update_Perry_Shanno(Vector x, Vector g) {
        form_y(g);
        double yty = y.dot(y);
        if (yty <= 0.0)
            return FAILURE;
        double dty = -d.dot(y);
        if (dty == 0.0)
            return FAILURE;
        double gty = g.dot(y);
        double c1 = dty/yty;
        double c2 = gty/yty - 2.0*dtg/dty;
        double c3 = -dtg/yty;
        beta = c2/c1;
        vsp.combine(c1, g, c2, d, c3, y, d);
        return SUCCESS;
    }

    /* This method is called to update the search direction.  The returned
    value indicates whether the updating rule has been successful, otherwise
    a restart is needed. */
    private int update(Vector x, Vector g) {
        switch (method & 0xff) {
        case FLETCHER_REEVES:
            return update_Fletcher_Reeves(x, g);
        case HESTENES_STIEFEL:
            return update_Hestenes_Stiefel(x, g);
        case POLAK_RIBIERE_POLYAK:
            return update_Polak_Ribiere_Polyak(x, g);
        case FLETCHER:
            return update_Fletcher(x, g);
        case LIU_STOREY:
            return update_Liu_Storey(x, g);
        case DAI_YUAN:
            return update_Dai_Yuan(x, g);
        case PERRY_SHANNO:
            return update_Perry_Shanno(x, g);
        case HAGER_ZHANG:
            return update_Hager_Zhang(x, g);
        default:
            return FAILURE;
        }
    }

    @Override
    public OptimTask start() {
        iterations = 0;
        evaluations = 0;
        restarts = 0;
        ginit = 0.0;
        return success(OptimTask.COMPUTE_FG);
    }

    @Override
    public OptimTask restart() {
        ++restarts;
        return success(OptimTask.COMPUTE_FG);
    }

    @Override
    public OptimTask iterate(Vector x, double f, Vector g) {
        /*
         * The new iterate is:
         *    x_{k+1} = x_{k} - \alpha_{k} p_{k}
         * as we consider the anti-search direction here.
         */
        switch (getTask()) {

        case COMPUTE_FG:

            ++evaluations;
            if (evaluations > 1) {
                /* A line search is in progress.  Compute directional
                   derivative and check whether line search has converged. */
                dtg = -d.dot(g);
                LineSearchTask lnsrchTask = lnsrch.iterate(alpha, f, dtg);
                if (lnsrchTask != LineSearchTask.CONVERGENCE) {
                    if (lnsrchTask == LineSearchTask.SEARCH) {
                        /* Line search has not converged, break to compute a
                           new trial point along the search direction. */
                        break;
                    }
                    OptimStatus lnsrchStatus = lnsrch.getStatus();
                    if (lnsrchTask != LineSearchTask.WARNING ||
                            lnsrchStatus != OptimStatus.ROUNDING_ERRORS_PREVENT_PROGRESS) {
                        return failure(lnsrchStatus);
                    }
                }
                /* Line search has converged. */
                ++iterations;
            }

            /* The current step is acceptable.  Check for global convergence. */
            gnorm = g.norm2();
            if (evaluations <= 1) {
                ginit = gnorm;
            }
            return success(gnorm <= getGradientThreshold() ? OptimTask.FINAL_X
                    : OptimTask.NEW_X);

        case NEW_X:
        case FINAL_X:

            /* Compute a search direction and start line search. */
            if (evaluations <= 1|| update(x, g) != SUCCESS) {
                /* First evaluation or update failed, set DTG to zero to use
                   the steepest descent direction. */
                dtg = 0.0;
            } else {
                dtg = -d.dot(g);
                if (epsilon > 0 &&
                        dtg > -epsilon*d.norm2()*gnorm) {
                    /* Set DTG to zero to indicate that we do not have a sufficient
                       descent direction. */
                    dtg = 0.0;
                }
            }
            if (dtg < 0.0) {
                /* The recursion yields a sufficient descent direction
                   (not all methods warrant that).  Compute an initial
                   step size ALPHA along the new direction. */
                if ((method & SHANNO_PHUA) == SHANNO_PHUA) {
                    /* Initial step size is such that:
                           <alpha_{k+1}*d_{k+1},g_{k+1}> = <alpha_{k}*d_{k},g_{k}> */
                    alpha *= (dtg0/dtg);
                }
            } else {
                /* Initial search direction or recurrence has been restarted.
                   Other possibility is to use Fletcher's formula, see BGLS
                   p. 39) */
                if (evaluations > 1) {
                    ++restarts;
                }
                vsp.copy(g, d);
                dtg = -gnorm*gnorm;
                if (f != 0.0) {
                    alpha = 2.0*Math.abs(f/dtg);
                } else {
                    double dnorm = gnorm;
                    double xnorm = x.norm2();
                    if (xnorm > 0.0) {
                        alpha = delta*xnorm/dnorm;
                    } else {
                        alpha = delta/dnorm;
                    }
                }
                beta = 0.0;
            }

            /* Store current position as X0, f0, etc. */
            vsp.copy(x, x0);
            f0 = f;
            if (g0 != null) {
                vsp.copy(g, g0);
            }
            g0norm = gnorm;
            dtg0 = dtg;

            /* Start the line search and break to compute the first trial
               point along the line search. */
            if (lnsrch.start(f0, dtg0, alpha, stpmin*alpha,
                    stpmax*alpha) != LineSearchTask.SEARCH) {
                return failure(lnsrch.getStatus());
            }
            break;

        default:
            return getTask();
        }

        /* Compute a trial point along the line search. */
        x.combine(1.0, x0, -alpha, d);
        return success(OptimTask.COMPUTE_FG);

    }

    /**
     * Set the absolute tolerance for the convergence criterion.
     *
     * @param gatol
     *            - Absolute tolerance for the convergence criterion.
     * @see {@link #setRelativeTolerance}, {@link #getAbsoluteTolerance},
     *      {@link #getGradientThreshold}.
     */
    public void setAbsoluteTolerance(double gatol) {
        this.gatol = gatol;
    }

    /**
     * Set the relative tolerance for the convergence criterion.
     *
     * @param grtol
     *            - Relative tolerance for the convergence criterion.
     * @see {@link #setAbsoluteTolerance}, {@link #getRelativeTolerance},
     *      {@link #getGradientThreshold}.
     */
    public void setRelativeTolerance(double grtol) {
        this.grtol = grtol;
    }

    /**
     * Query the absolute tolerance for the convergence criterion.
     *
     * @see {@link #setAbsoluteTolerance}, {@link #getRelativeTolerance},
     *      {@link #getGradientThreshold}.
     */
    public double getAbsoluteTolerance() {
        return gatol;
    }

    /**
     * Query the relative tolerance for the convergence criterion.
     *
     * @see {@link #setRelativeTolerance}, {@link #getAbsoluteTolerance},
     *      {@link #getGradientThreshold}.
     */
    public double getRelativeTolerance() {
        return grtol;
    }

    /**
     * Query the gradient threshold for the convergence criterion.
     *
     * The convergence of the optimization method is achieved when the Euclidean
     * norm of the gradient at a new iterate is less or equal the threshold:
     *
     * <pre>
     * max(0.0, gatol, grtol * gtest)
     * </pre>
     *
     * where {@code gtest} is the norm of the initial gradient, {@code gatol}
     * {@code grtol} are the absolute and relative tolerances for the
     * convergence criterion.
     *
     * @return The gradient threshold.
     * @see {@link #setAbsoluteTolerance}, {@link #setRelativeTolerance},
     *      {@link #getAbsoluteTolerance}, {@link #getRelativeTolerance}.
     */
    public double getGradientThreshold() {
        return max(0.0, gatol, grtol * ginit);
    }

    /**
     * Query the assumed function least value.
     *
     * @return The assumed function least value if it has been set,
     *         {@code Double.NaN} else.
     */
    public double getFMin() {
        return (fmin_given ? fmin : Double.NaN);
    }

    /**
     * Set the assumed function least value.
     *
     * @param value
     *            - An estimate of the function least value.
     */
    public void setFMin(double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            unsetFMin();
        } else {
            fmin = value;
            fmin_given = true;
        }
    }

    /**
     * Forget the assumed function least value.
     */
    public void unsetFMin() {
        fmin = Double.NaN;
        fmin_given = false;
    }

    /**
     * Testing routine.
     *
     * @param args
     */
    public static void main(String[] args) {

        int[] prob = new int[] { 1, 2, 3, 4, 5, 6, 7, 7, 8, 9, 10, 11, 12, 13,
                14, 15, 16, 17, 18 };
        int[] size = new int[] { 3, 6, 3, 2, 3, 5, 6, 9, 6, 8, 2, 4, 3, 8, 8,
                12, 2, 4, 30 };
        double factor = 1.0;

        /* Create line search object and set options. */
        int method = HAGER_ZHANG;
        MoreThuenteLineSearch lineSearch = new MoreThuenteLineSearch(0.05, 0.1,
                1E-8);

        for (int k = 0; k < prob.length; ++k) {
            int p = prob[k];
            int n = size[k];
            DoubleShapedVectorSpace space = new DoubleShapedVectorSpace(n);
            double[] xData = new double[n];
            double[] gData = new double[n];
            int iter = -1;
            int nf = 0;
            int ng = 0;
            double fx = 0.0;
            DoubleShapedVector x = space.wrap(xData);
            DoubleShapedVector gx = space.wrap(gData);

            /* Store initial solution. */
            MinPack1Tests.umipt(xData, p, factor);

            /* Create conjugate gradient minimizer. */
            NonLinearConjugateGradient minimizer = new NonLinearConjugateGradient(
                    space, method, lineSearch);

            OptimTask task = minimizer.start();
            while (true) {
                if (task == OptimTask.COMPUTE_FG) {
                    fx = MinPack1Tests.umobj(xData, p);
                    ++nf;
                    MinPack1Tests.umgrd(xData, gData, p);
                    ++ng;
                } else if (task == OptimTask.NEW_X) {
                    ++iter;
                    if (iter == 0) {
                        System.out.println("\nProblem #" + p + " with " + n
                                + " variables.");
                        System.out.println("|x0| = " + space.norm2(x) + " f0 = "
                                + fx + " |g0| = " + space.norm2(gx));
                        /*} else {
                        System.out.println("iter = " + iter + " f(x) = " + fx + " |g(x)| = " + space.norm2(gx)); */
                    }
                } else if (task == OptimTask.FINAL_X) {
                    ++iter;
                    System.out.println("|xn| = " + space.norm2(x) + " f(xn) = "
                            + fx + " |g(xn)| = " + space.norm2(gx));
                    System.out.println("in " + iter + " iterations, " + nf
                            + " function calls and " + ng + " gradient calls");
                    break;
                } else {
                    System.err.println(
                            "TiPi: NonLinearConjugateGradient, error/warning: "
                                    + task);
                    break;
                }
                task = minimizer.iterate(x, fx, gx);
            }
        }

        /* Make sure all messages are displayed. */
        System.out.flush();
        System.err.flush();
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
