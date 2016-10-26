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

/**
 * Interface for multivariate optimization methods with reverse communication.
 *
 * <p> The typical usage of reverse communication optimization methods is as
 * follows: </p>
 *
 * <pre>
 * / Define the vector space to which belong the variables.
 * ectorSpace space = new ...;
 *
 * / Create the optimizer and configure it.
 * everseCommunicationOptimizer optimizer = new ...;
 *
 * / Allocate storage for the variables and the gradient.
 * ector x = space.create();
 * ector gx = space.create();
 *
 * / Choose initial variables.
 * .fill(0.0);
 *
 * / Loop to optimize.
 * nt eval = 0;
 * nt iter = 0;
 * ouble fx = 0.0;
 * ptimTask task = optimizer.start();
 * hile (true) {
 *    if (task == OptimTask.COMPUTE_FG) {
 *        // Compute the function and its gradient at x:
 *        fx = ...;
 *        gx = ...;
 *        ++eval; // keep track of the number of function evaluations
 *    } else if (task == OptimTask.NEW_X) {
 *        // A new iterate is available for examination.
 *        ++iter; // keep track of the number of iterations
 *    } else if (task == OptimTask.FINAL_X) {
 *        // Algorithm has converged.
 *        ++iter; // keep track of the number of iterations
 *        System.out.println("Algorithm has converged.");
 *    } else {
 *        // An error or a warning has occurred.
 *        int reason = optimizer.getMessage();
 *        System.err.format("Algorithm terminates with errors or warnings: %s\n",
 *            optimizer.getMessage(reason));
 *        break;
 *    }
 *    task = optimizer.iterate(x, fx, gx);
 * }
 *
 * // On normal exit, X contains the solution (or at least the best
 * // solution so far).
 * </pre>
 *
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */

public abstract class ReverseCommunicationOptimizer {

    public static final int SUCCESS = 0;
    public static final int FAILURE = -1;

    /** Vector space of the variables. */
    protected final VectorSpace space;

    /** Pending task for the caller. */
    protected OptimTask task;

    /** Reason of failure. */
    protected OptimStatus status;

    /** Number of function (and gradient) evaluations since start. */
    protected int evaluations = 0;

    /** Number of iterations since start. */
    protected int iterations = 0;

    /** Number of restarts. */
    protected int restarts = 0;

    /**
     * Create a reverse communication optimizer.
     *
     * @param space
     *        The vector space to which belong the variables of the
     *        optimization problem.
     */
    protected ReverseCommunicationOptimizer(VectorSpace space) {
        if (space == null) {
            throw new IllegalArgumentException("Illegal null vector space");
        }
        this.space = space;
    }

    /**
     * Start the search.
     *
     * @return The next task to perform.
     */
    public abstract OptimTask start();

    /**
     * Restart the search.
     *
     * @return The next task to perform.
     */
    public abstract OptimTask restart();

    /**
     * Proceed with next iteration.
     *
     * @param x
     *        The current set of variables.
     *
     * @param fx
     *        The value of the function at {@code x}.
     *
     * @param gx
     *        The value of the gradient at {@code x}.
     *
     * @return The next task to perform.
     */
    public abstract OptimTask iterate(Vector x, double fx, Vector gx);

    /**
     * Get the current pending task.
     *
     * @return The pending task to perform.
     */
    public final OptimTask getTask() {
        return task;
    }

    /**
     * Get the vector space of the variables.
     *
     * @return The vector space to which belong the variables of the
     *         optimization problem.
     */
    public final VectorSpace getSpace() {
        return space;
    }

    /**
     * Get the iteration number.
     *
     * @return The number of iterations since last start.
     */
    public final int getIterations() {
        return iterations;
    }

    /**
     * Get the number of function (and gradient) evaluations since last start.
     *
     * @return The number of function and gradient evaluations since last start.
     */
    public final int getEvaluations() {
        return evaluations;
    }

    /**
     * Get the number of restarts.
     *
     * @return The number of times algorithm has been restarted since last
     *         start.
     */
    public final int getRestarts() {
        return restarts;
    }

    /**
     * Set internal state to indicate a failure.
     *
     * @param status
     *        The reason of failure.
     *
     * @return The next pending task which has been set to
     *         {@link LineSearchTask#ERROR}.
     */
    protected final OptimTask failure(OptimStatus status) {
        this.status = status;
        this.task = OptimTask.ERROR;
        return this.task;
    }

    /**
     * Set internal state to indicate a non-fatal abnormal termination.
     *
     * @param status
     *        The reason of the termination.
     *
     * @return The next pending task which has been set to
     *         {@link LineSearchTask#WARNING}.
     */
    protected final OptimTask warning(OptimStatus status) {
        this.status = status;
        this.task = OptimTask.WARNING;
        return this.task;
    }

    /**
     * Set next pending task.
     *
     * @param task
     *        The next pending task (must not be {@link LineSearchTask#ERROR}).
     *
     * @return The next pending task.
     */
    protected final OptimTask success(OptimTask task) {
        this.status = OptimStatus.SUCCESS;
        this.task = task;
        return this.task;
    }

    /**
     * Get a literal description of the current optimizer state.
     *
     * @return A string describing the state.
     */
    public final String getReason() {
        return (status == OptimStatus.SUCCESS ?
                task.toString() : status.toString());
    }

    /**
     * Get the optimizer internal status.
     *
     * @return The current optimizer status.
     *
     * @see OptimStatus#toString
     */
    public final OptimStatus getStatus() {
        return status;
    }

}
