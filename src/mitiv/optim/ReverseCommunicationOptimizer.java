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

public interface ReverseCommunicationOptimizer {
    public static final int SUCCESS =  0;
    public static final int FAILURE = -1;

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
    public abstract OptimTask getTask();

    /**
     * Get the iteration number.
     * @return The number of iterations since last start.
     */
    public abstract int getIterations();

    /**
     * Get the number of function (and gradient) evaluations since
     * last start.
     * @return The number of function and gradient evaluations since
     *         last start.
     */
    public abstract int getEvaluations();

    /**
     * Get the number of restarts.
     * @return The number of times algorithm has been restarted since
     *         last start.
     */
    public abstract int getRestarts();

    /**
     * Query a textual description of the reason of an abnormal
     * termination.
     * @param reason - The code given by {@link #getReason}().
     * @return A textual description of the reason of the abnormal
     *         termination.
     */
    public abstract String getMessage(int reason);

    /**
     * Get the code corresponding to the reason of the abnormal termination.
     * @return A numerical code corresponding to the reason of the abnormal
     *         termination.
     * @see {@link #getMessage}();
     */
    public abstract int getReason();
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
