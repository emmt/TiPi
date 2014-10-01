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
 * References:
 * [1] Hestenes, M.R. & Stiefel, E., "Methods of Conjugate Gradients for
 *     Solving Linear Systems," Journal of Research of the National Bureau
 *     of Standards 49, 409-436 (1952).
 *
 * [2] Hager, W.W. & Zhang, H., "A survey of nonlinear conjugate gradient
 *     methods," Pacific Journal of Optimization, Vol. 2, pp. 35-58 (2006).
 *
 * [3] Hager, W. W. & Zhang, H. "A New Conjugate Gradient Method with
 *     Guaranteed Descent and an Efficient Line Search," SIAM J. Optim.,
 *     Vol. 16, pp. 170-192 (2005).
 *
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */
public class NonLinearConjugateGradient implements ReverseCommunicationOptimizer {
    public static final int SUCCESS =  0;
    public static final int FAILURE = -1;

    public static final double STPMIN = 1E-20;
    public static final double STPMAX = 1E+6;

    /* Non-linear optimization method.  POWELL (forcing beta to be
       non-negative) and SHANNO_PHUA (to use their formula in CONMIN to
       estimate the step size) can be combined to the chosen method. */
    public static final int FLETCHER_REEVES      = 1;
    public static final int HESTENES_STIEFEL     = 2;
    public static final int POLAK_RIBIERE_POLYAK = 3;
    public static final int FLETCHER             = 4;
    public static final int LIU_STOREY           = 5;
    public static final int DAI_YUAN             = 6;
    public static final int PERRY_SHANNO         = 7;
    public static final int HAGER_ZHANG          = 8;
    public static final int POWELL               = (1<<8); /* force beta >= 0 */
    public static final int SHANNO_PHUA          = (1<<9); /* compute scale from previous
                                                       iteration */
    /* For instance: (POLAK_RIBIERE_POLYAK | POWELL) merely
       corresponds to PRP+ (Polak, Ribiere, Polyak) while (PERRY_SHANNO |
       SHANNO_PHUA) merely corresponds to the conjugate gradient method
       implemented in CONMIN. */

    /* Default settings for non linear conjugate gradient (should correspond to
       the method which is, in general, the most successful). */
    public static final int DEFAULT_METHOD = (HAGER_ZHANG | SHANNO_PHUA);


    private double f0;     /* Function value at the start of the line search. */
    private double g0norm; /* Euclidean norm of G0, the gradient at the start of the
                              line search. */
    private double g1norm; /* Euclidean norm of G1, the gradient of the end of the line
                              search / last accepted point. */
    private double dg0;    /* Directional derivative at the start of the line search;
                              given by the inner product: <d|g0> = - <p|g0> */
    private double dg1;    /* Directional derivative at the end or during the line search;
                              given by the inner product: <d|g1> = - <p|g1> */
    //private double alpha0; /* Scale factor for the initial step step size. */
    private double grtol;  /* Relative threshold for the norm or the gradient (relative
                              to GTEST the norm of the initial gradient) for convergence. */
    private double gatol;  /* Absolute threshold for the norm or the gradient for
                              convergence. */
    private double gtest;  /* Norm or the initial gradient. */
    private double fmin;   /* Minimal function value if provided. */
    private double alpha;  /* Current step length. */
    private double beta;   /* Current parameter in conjugate gradient update rule (for
                              information). */
    private final double stpmin; /* Relative lower bound for the step length. */
    private final double stpmax; /* Relative upper bound for the step length. */
    private final VectorSpace vsp;
    private final LineSearch lnsrch;
    private final Vector x0;     /* Variables at start of line search. */
    private final Vector g0;     /* Gradient at start of line search. */
    private final Vector p;      /* (Anti-)search direction, new iterate is searched
                              as: x1 = x0 - alpha*p, for alpha >= 0. */
    private final Vector y;      /* Work vector (e.g., to store the gradient difference:
                              Y = G1 - G0). */
    private int iter;      /* Iteration number. */
    private int nrestarts; /* Number of algorithm restarts. */
    private int nevals;    /* Number of function and gradient evaluations. */
    private final int method;   /* Conjugate gradient method. */
    private OptimTask task;     /* Current pending task. */
    private boolean starting;   /* Indicate whether algorithm is starting */
    private boolean fmin_given; /* Indicate whether FMIN is specified. */
    private final boolean update_Hager_Zhang_orig = false;

    private static double max(double a1, double a2, double a3)
    {
        if (a3 >= a2) {
            return (a3 >= a1 ? a3 : a1);
        } else {
            return (a2 >= a1 ? a2 : a1);
        }
    }

    public NonLinearConjugateGradient(VectorSpace vsp)
    {
        this(vsp, DEFAULT_METHOD);
    }

    public NonLinearConjugateGradient(VectorSpace vsp, int method)
    {
        /* FIXME: choose more suitable values (e.g., in CG+: FTOL=1E-4, GTOL=1E-1,
        not less than 1E-4, XTOL=1E-17, STPMIN=1E-20 STPMAX=1E+20 and MAXFEV=40) */
        this(vsp, method, new MoreThuenteLineSearch(/* sftol */ 0.05, /* sgtol */ 0.1, /* sxtol */ 1E-17));
    }

    public NonLinearConjugateGradient(VectorSpace vsp, int method, LineSearch lnsrch)
    {
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
        this.gtest = 0.0;
        this.stpmin = STPMIN;
        this.stpmax = STPMAX;
        this.x0 = vsp.create();
        this.g0 = (g0_needed ? vsp.create() : null);
        this.p = vsp.create();
        this.y = (y_needed ? vsp.create() : null);
        this.task = OptimTask.ERROR;
        this.nevals = 0;
    }

    /*
     * Most non-linear conjugate gradient methods, update the new search direction
     * by the following rule:
     *
     *     d' = -g1 + beta*d
     *
     * with d' the new search direction, g1 the current gradient, d the previous
     * search direction and beta a parameter which depends on the method.  For us,
     * the anti-search direction is:
     *
     *     p' = -d' = g1 + beta*p
     *
     * with p = -d the previous anti-search direction.  Some methods (e.g., Perry
     * & Shanno) implement the following rule:
     *
     *     d' = (-g1 + beta*d + gamma*y)*delta
     *
     * with y = g1 - g0.
     */

    /* Helper function to compute search direction as: p' = g1 + beta*p. */
    private int update0(Vector g1, double beta) {
        this.beta = beta;
        if (this.beta != 0.0) {
            vsp.axpby(1.0, g1, beta, p);
            return SUCCESS;
        } else {
            return FAILURE;
        }
    }

    /* Helper function to compute search direction as: p' = g1 + beta*p
       possibly with the constraint that beta > 0. */
    private int update1(Vector g1, double beta)
    {
        if ((method & POWELL) != 0 && beta < 0.0) {
            ++nrestarts;
            this.beta = 0.0;
        } else {
            this.beta = beta;
        }
        if (this.beta != 0.0) {
            vsp.axpby(1.0, g1, beta, p, p);
            return SUCCESS;
        } else {
            return FAILURE;
        }
    }

    /* Form: Y = G1 - G0 */
    private void form_y(Vector g1)
    {
        vsp.axpby(1.0, g1, -1.0, g0, y);
    }

    /*
     * For Hestenes & Stiefel method:
     *
     *     beta = <g1|y>/<d|y> = - <g1|y>/<p|y>
     *
     * with y = g1 - g0.
     */
    private int update_Hestenes_Stiefel(Vector x1, Vector g1)
    {
        form_y(g1);
        double g1y =  vsp.dot(g1, y);   /* Compute: g1y = <g1|y> */
        double dy = -vsp.dot(p, y); /* Compute: dy = <d|y> = - <p|y> */
        double beta = (dy != 0.0 ? g1y/dy : 0.0);
        return update1(g1, beta);
    }

    /*
     * For Fletcher & Reeves method:
     *
     *     beta = <g1|g1>/<g0|g0>
     *
     * (this value is always >= 0 and can only be zero at a stationnary point).
     */
    private int update_Fletcher_Reeves(Vector x1, Vector g1)
    {
        double r = g1norm/g0norm;
        return update0(g1, r*r);
    }

    /*
     * For Polak-Ribière-Polyak method:
     *
     *     beta = <g1|y>/<g0|g0>
     */
    private int update_Polak_Ribiere_Polyak(Vector x1, Vector g1)
    {
        double beta = vsp.dot(g1, y)/g0norm/g0norm;
        return update1(g1, beta);
    }

    /*
     * For Fletcher "Conjugate Descent" method:
     *
     *     beta = <g1|g1>/(-<d|g0>)
     *
     * (this value is always >= 0 and can only be zero at a stationary point).
     */
    private int update_Fletcher(Vector x1, Vector g1)
    {
        double beta = g1norm*(g1norm/(-dg0));
        return update0(g1, beta);
    }

    /*
     * For Liu & Storey method:
     *
     *     beta = <g1|y>/(-<d|g0>)
     */
    private int update_Liu_Storey(Vector x1, Vector g1)
    {
        form_y(g1);
        double g1y =  vsp.dot(g1, y);    /* Compute: g1y = <g1|y> */
        double beta = g1y/(-dg0);
        return update1(g1, beta);
    }

    /*
     * For Dai & Yuan method:
     *
     *     beta = <g1|g1>/<d|y>
     */
    private int update_Dai_Yuan(Vector x1, Vector g1)
    {
        form_y(g1);
        double dy = -vsp.dot(p, y); /* Compute: dy = <d|y> = - <p|y> */
        double beta = (dy != 0.0 ? g1norm*(g1norm/dy) : 0.0);
        return update1(g1, beta);
    }

    /*
     * For Hager & Zhang method:
     *
     *     beta = <y - (2*<y|y>/<d|y>)*d|g1>
     *          = (<g1|y> - 2*<y|y>*<d|g1>/<d|y>)/<d|y>
     */
    private int update_Hager_Zhang(Vector x1, Vector g1)
    {
        form_y(g1);
        double dy = -vsp.dot(p, y);
        double beta;
        if (dy != 0.0) {
            if (update_Hager_Zhang_orig) {
                /* Original formulation. */
                double q = 1.0/dy;
                double r = q*vsp.norm2(y);
                vsp.axpby(q, y, 2.0*r*r, p, y);
                beta = vsp.dot(y, g1);
            } else {
                /* Improved formulation which spares one linear combination and thus has
                   less overhead (only 3 scalar products plus 2 linear combinations
                   instead of 3 scalar products and 3 linear combinations).  The rounding
                   errors are however different, so one or the other formulation can be by
                   chance more efficient.  Though there is no systematic trend. */
                double yg = vsp.dot(y, g1);
                double dg = dg1;
                double r = vsp.norm2(y)/dy;
                beta = yg/dy - 2.0*r*r*dg;
            }
        } else {
            beta = 0.0;
        }
        return update1(g1, beta);
    }

    /* Perry & Shanno, update rule (used in CONMIN and see Eq. (1.4) in [2])
     * writes:
     *
     *     d' = alpha*(-c1*g1 + c2*d - c3*y)  ==>   p' = c1*g1 + c2*p + c3*y
     *
     *     c1 = (1/alpha)*<s|y>/<y|y>
     *        =  <d|y>/<y|y>
     *        = -<p|y>/<y|y>
     *
     *     c2 = <g1|y>/<y|y> - 2*<s|g1>/<s|y>
     *	  = <g1|y>/<y|y> - 2*<d|g1>/<d|y>
     *	  = <g1|y>/<y|y> - 2*<p|g1>/<p|y>
     *
     *     c3 = -(1/alpha)*<s|g1>/<y|y>
     *        = -<d|g1>/<y|y>
     *        =  <p|g1>/<y|y>
     *
     * with alpha the step length, s = x1 - x0 = alpha*d = -alpha*p.  For this
     * method, beta = c2/c1.
     */
    private int update_Perry_Shanno(Vector x1, Vector g1)
    {
        form_y(g1);
        double yy = vsp.dot(y, y);
        if (yy <= 0.0) return FAILURE;
        double dy = -vsp.dot(p, y);
        if (dy == 0.0) return FAILURE;
        double g1y = vsp.dot(g1, y);
        double c1 = dy/yy;
        double c2 = g1y/yy - 2.0*dg1/dy;
        double c3 = -dg1/yy;
        beta = c2/c1;
        vsp.axpbypcz(c1, g1, c2, p, c3, y, p);
        return SUCCESS;
    }

    /* This method is called to update the search direction.  The returned
    value indicates whether the updating rule has been successful, otherwise
    a restart is needed. */
    private int update(Vector x1, Vector g1)
    {
        switch (method & 0xff) {
        case FLETCHER_REEVES:
            return update_Fletcher_Reeves(x1, g1);
        case HESTENES_STIEFEL:
            return update_Hestenes_Stiefel(x1, g1);
        case POLAK_RIBIERE_POLYAK:
            return update_Polak_Ribiere_Polyak(x1, g1);
        case FLETCHER:
            return update_Fletcher(x1, g1);
        case LIU_STOREY:
            return update_Liu_Storey(x1, g1);
        case DAI_YUAN:
            return update_Dai_Yuan(x1, g1);
        case PERRY_SHANNO:
            return update_Perry_Shanno(x1, g1);
        case HAGER_ZHANG:
            return update_Hager_Zhang(x1, g1);
        default:
            return FAILURE;
        }
    }

    @Override
    public OptimTask start()
    {
        iter = 0;
        nevals = 0;
        nrestarts = 0;
        starting = true;
        gtest = 0.0;
        return (task = OptimTask.COMPUTE_FG);
    }

    @Override
    public OptimTask restart()
    {
        ++nrestarts;
        starting = true;
        return (task = OptimTask.COMPUTE_FG);
    }

    @Override
    public OptimTask iterate(Vector x1, double f1, Vector g1)
    {
        /*
         * The new iterate is:
         *    x_{k+1} = x_{k} + \alpha_{k} d_{k}
         *            = x_{k} - \alpha_{k} p_{k}
         * as we consider the anti-search direction p = -d here.
         */
        if (task == OptimTask.COMPUTE_FG) {
            boolean accept;
            ++nevals;
            if (starting) {
                g1norm = vsp.norm2(g1);
                gtest = g1norm;
                accept = true;
            } else {
                /* Compute directional derivative and check whether line search has
                   converged. */
                dg1 = -vsp.dot(p, g1);
                int status = lnsrch.iterate(alpha, f1, dg1);
                alpha = lnsrch.getStep();
                if (status == LineSearch.SEARCH) {
                    accept = false;
                } else if (status == LineSearch.CONVERGENCE ||
                        status == LineSearch.WARNING_ROUNDING_ERRORS_PREVENT_PROGRESS) {
                    ++iter;
                    g1norm = vsp.norm2(g1);
                    accept = true;
                } else {
                    System.out.println("lnsrch status: " + lnsrch.getStatus());
                    if (lnsrch.hasWarnings()) {
                        /* FIXME: some warnings can be safely considered as a convergence */
                        task = OptimTask.WARNING;
                    } else {
                        task = OptimTask.ERROR;
                    }
                    return task;
                }
            }
            if (accept) {
                /* Check for global convergence. */
                if (g1norm <= getGradientThreshold()) {
                    task = OptimTask.FINAL_X;
                } else {
                    task = OptimTask.NEW_X;
                }
                return task;
            }
        } else if (task == OptimTask.NEW_X) {
            /* Compute a search direction and start line search. */
            boolean restart;
            if (starting) {
                restart = true;
                beta = 0.0;
            } else {
                restart = (update(x1, g1) != SUCCESS);
                if (! restart) {
                    double dg = -vsp.dot(p, g1);
                    if (dg >= 0.0) {
                        /* Restart if not a descent direction, not all updates warrant that.
                           (FIXME: Generate an error instead?) */
                        restart = true;
                    } else {
                        /* Compute an initial step size ALPHA. */
                        if ((method & SHANNO_PHUA) != 0) {
                            /* Initial step size is such that:
                               <alpha_{k+1}*d_{k+1}|g_{k+1}> = <alpha_{k}*d_{k}|g_{k}> */
                            alpha *= dg0/dg;
                        }
                    }
                    /* Save directional derivative. */
                    dg0 = dg;
                }
            }
            if (restart) {
                /* Initial search direction or recurrence has been restarted.  FIXME:
                   other possibility is to use Fletcher's formula, see BGLS p. 39) */
                if (! starting) {
                    ++nrestarts;
                }
                beta = 0.0;
                // FIXME: cleanup code
                //double x1norm = vsp.norm2(x1);
                //if (x1norm > 0.0) {
                //    alpha = (x1norm/g1norm)*tiny;
                //} else {
                //    alpha = (1e-3*Math.max(Math.abs(f1), 1.0)/g1norm)/g1norm;
                //}
                alpha = 1.0/g1norm;
                vsp.copy(g1, p);
                dg0 = -g1norm*g1norm;
            }

            /* Store current position as X0, f0, etc. */
            vsp.copy(x1, x0);
            f0 = f1;
            if (g0 != null) {
                vsp.copy(g1, g0);
            }
            g0norm = g1norm;

            /* Start the line search. */
            dg1 = dg0;
            int status = lnsrch.start(f0, dg0, alpha,
                    stpmin*alpha,
                    stpmax*alpha);
            if (status != LineSearch.SEARCH) {
                if (lnsrch.hasWarnings()) {
                    task = OptimTask.WARNING;
                } else {
                    task = OptimTask.ERROR;
                }
                return task;
            }
        } else {
            return task;
        }

        /* Build a new step to try. */
        x1.axpby(1.0, x0, -alpha, p);
        starting = false;
        task = OptimTask.COMPUTE_FG;
        return task;

    }

    @Override
    public OptimTask getTask() {
        return task;
    }

    /**
     * Set the absolute tolerance for the convergence criterion.
     * @param gatol - Absolute tolerance for the convergence criterion.
     * @see {@link #setRelativeTolerance}, {@link #getAbsoluteTolerance},
     *      {@link #getGradientThreshold}.
     */
    public void setAbsoluteTolerance(double gatol) {
        this.gatol = gatol;
    }

    /**
     * Set the relative tolerance for the convergence criterion.
     * @param grtol - Relative tolerance for the convergence criterion.
     * @see {@link #setAbsoluteTolerance}, {@link #getRelativeTolerance},
     *      {@link #getGradientThreshold}.
     */
    public void setRelativeTolerance(double grtol) {
        this.grtol = grtol;
    }

    /**
     * Query the absolute tolerance for the convergence criterion.
     * @see {@link #setAbsoluteTolerance}, {@link #getRelativeTolerance},
     *      {@link #getGradientThreshold}.
     */
    public double getAbsoluteTolerance() {
        return gatol;
    }

    /**
     * Query the relative tolerance for the convergence criterion.
     * @see {@link #setRelativeTolerance}, {@link #getAbsoluteTolerance},
     *      {@link #getGradientThreshold}.
     */
    public double getRelativeTolerance() {
        return grtol;
    }

    /**
     * Query the gradient threshold for the convergence criterion.
     * 
     * The convergence of the optimization method is achieved when the
     * Euclidean norm of the gradient at a new iterate is less or equal
     * the threshold:
     * <pre>
     *    max(0.0, gatol, grtol*gtest)
     * </pre>
     * where {@code gtest} is the norm of the initial gradient, {@code gatol}
     * {@code grtol} are the absolute and relative tolerances for the
     * convergence criterion.
     * @return The gradient threshold.
     * @see {@link #setAbsoluteTolerance}, {@link #setRelativeTolerance},
     *      {@link #getAbsoluteTolerance}, {@link #getRelativeTolerance}.
     */
    public double getGradientThreshold() {
        return max(0.0, gatol, grtol*gtest);
    }

    /**
     * Query the assumed function least value.
     * @return The assumed function least value if it has been set,
     *         {@code Double.NaN} else.
     */
    public double getFMin()
    {
        return (fmin_given ? fmin : Double.NaN);
    }

    /**
     * Set the assumed function least value.
     * @param value - An estimate of the function least value.
     */
    public void setFMin(double value)
    {
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
    public void unsetFMin()
    {
        fmin = Double.NaN;
        fmin_given = false;
    }

    @Override
    public int getIterations() {
        return iter;
    }

    @Override
    public int getEvaluations() {
        return nevals;
    }

    @Override
    public int getRestarts() {
        return nrestarts;
    }

    @Override
    public String getMessage(int reason) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getReason() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Testing routine.
     * @param args
     */
    public static void main(String[] args) {

        int[] prob = new int[] {1, 2, 3, 4, 5, 6, 7, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
        int[] size = new int[] {3, 6, 3, 2, 3, 5, 6, 9, 6, 8,  2,  4,  3,  8,  8, 12,  2,  4, 30};
        double factor = 1.0;

        /* Create line search object and set options. */
        int method = HAGER_ZHANG;
        MoreThuenteLineSearch lineSearch = new MoreThuenteLineSearch(0.05, 0.1, 1E-8);

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
            NonLinearConjugateGradient minimizer = new NonLinearConjugateGradient(space, method, lineSearch);

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
                        System.out.println("\nProblem #" + p + " with " + n + " variables.");
                        System.out.println("|x0| = " + space.norm2(x) + " f0 = " + fx + " |g0| = " + space.norm2(gx));
                        /*} else {
                        System.out.println("iter = " + iter + " f(x) = " + fx + " |g(x)| = " + space.norm2(gx)); */
                    }
                } else if (task == OptimTask.FINAL_X) {
                    ++iter;
                    System.out.println("|xn| = " + space.norm2(x) + " f(xn) = " + fx + " |g(xn)| = " + space.norm2(gx));
                    System.out.println("in " + iter + " iterations, " + nf + " function calls and "
                            + ng + " gradient calls");
                    break;
                } else {
                    System.err.println("error/warning: " + task);
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
