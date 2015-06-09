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

/**
 * Interface for multivariate optimization methods with reverse communication.
 *
 * The typical usage of reverse communication optimization methods is as follows:
 * <pre>
 *   // Define the vector space to which belong the variables.
 *   VectorSpace space = new ...;
 *
 *   // Create the optimizer and configure it.
 *   ReverseCommunicationOptimizer optimizer = new ...;
 *
 *   // Allocate storage for the variables and the gradient.
 *   Vector x = space.create();
 *   Vector gx = space.create();
 *
 *   // Choose initial variables.
 *   x.fill(0.0);
 *
 *   // Loop to optimize.
 *   int eval = 0;
 *   int iter = 0;
 *   double fx = 0.0;
 *   OptimTask task = optimizer.start();
 *   while (true) {
 *       if (task == OptimTask.COMPUTE_FG) {
 *           // Compute the function and its gradient at x:
 *           fx = ...;
 *           gx = ...;
 *           ++eval; // keep track of the number of function evaluations
 *       } else if (task == OptimTask.NEW_X) {
 *           // A new iterate is available for examination.
 *           ++iter; // keep track of the number of iterations
 *       } else if (task == OptimTask.FINAL_X) {
 *           // Algorithm has converged.
 *           ++iter; // keep track of the number of iterations
 *           System.out.println("Algorithm has converged.");
 *       } else {
 *           // An error or a warning has occurred.
 *           int reason = optimizer.getMessage();
 *           System.err.format("Algorithm terminates with errors or warnings: %s\n",
 *               optimizer.getMessage(reason));
 *           break;
 *       }
 *       task = optimizer.iterate(x, fx, gx);
 *    }
 *
 *    // On normal exit, X contains the solution (or at least the best
 *    // solution so far).
 * </pre>
 *
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */

public abstract class ReverseCommunicationOptimizer {
    public static final int SUCCESS =  0;
    public static final int FAILURE = -1;

    /** Pending task for the caller. */
    protected OptimTask task = null;

    /** Reason of failure. */
    protected int reason = NO_PROBLEMS;

    /** Line search status (for error messages). */
    protected LineSearchStatus lineSearchStatus = null;


    public static final int NO_PROBLEMS = 0;
    public static final int BAD_PRECONDITIONER = 1; /* preconditioner is not positive definite */
    public static final int LNSRCH_WARNING = 2; /* warning in line search */
    public static final int LNSRCH_ERROR = 3; /* error in line search */

    /** Number of function (and gradient) evaluations since start. */
    protected int evaluations = 0;

    /** Number of iterations since start. */
    protected int iterations = 0;

    /** Number of restarts. */
    protected int restarts = 0;

    /**
     * Start the search.
     * @return The next task to perform.
     */
    public abstract OptimTask start();

    /**
     * Restart the search.
     * @return The next task to perform.
     */
    public abstract OptimTask restart();

    /**
     * Proceed with next iteration.
     *
     * @param x  - The current set of variables.
     * @param fx - The value of the function at {@code x}.
     * @param gx - The value of the gradient at {@code x}.
     * @return The next task to perform.
     */
    public abstract OptimTask iterate(Vector x, double fx, Vector gx);

    /**
     * Get the current pending task.
     * @return The pending task to perform..
     */
    public final OptimTask getTask() {
        return task;
    }

    /**
     * Get the iteration number.
     * @return The number of iterations since last start.
     */
    public final int getIterations() {
        return iterations;
    }

    /**
     * Get the number of function (and gradient) evaluations since
     * last start.
     * @return The number of function and gradient evaluations since
     *         last start.
     */
    public final int getEvaluations() {
        return evaluations;
    }

    /**
     * Get the number of restarts.
     * @return The number of times algorithm has been restarted since
     *         last start.
     */
    public final int getRestarts() {
        return restarts;
    }

    /**
     * Query a textual description of the reason of an abnormal
     * termination.
     * @return A textual description of the reason of the abnormal
     *         termination.
     */
    public String getErrorMessage() {
        String msg = new String();
        if (reason == NO_PROBLEMS) {
            msg = "no problem";
        } else if (reason == BAD_PRECONDITIONER) {
            msg = "preconditioner is not positive definite";
        } else if (reason == LNSRCH_WARNING) {
            msg = "warning in line search: " + lineSearchStatus.getDescription();
        } else if (reason == LNSRCH_ERROR) {
            msg = "error in line search: " + lineSearchStatus.getDescription();;
        } else {
            msg = "unknown problem!";
        }
        return msg;
    }

    /**
     * Get the code corresponding to the reason of the abnormal termination.
     * @return A numerical code corresponding to the reason of the abnormal
     *         termination.
     * @see {@link #getErrorMessage}();
     */
    public final int getReason() {
        return reason;
    }

    /** Set next pending task (not a failure). */
    protected final OptimTask schedule(OptimTask task) {
        this.lineSearchStatus = null;
        this.reason = NO_PROBLEMS;
        this.task = task;
        return task;
    }

    /** Set task so as to report a failure (not related to line search). */
    protected final OptimTask failure(int reason) {
        this.lineSearchStatus = null;
        this.reason = reason;
        this.task = OptimTask.ERROR;
        return this.task;
    }

    /** Set task so as to report a line search failure. */
    protected final OptimTask lineSearchFailure(LineSearchStatus status) {
        lineSearchStatus = status;
        if (status.isWarning()) {
            reason = LNSRCH_WARNING;
            task = OptimTask.WARNING;
        } else {
            reason = LNSRCH_ERROR;
            task = OptimTask.ERROR;
        }
        return task;
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
