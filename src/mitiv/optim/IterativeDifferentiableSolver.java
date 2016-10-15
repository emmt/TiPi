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

package mitiv.optim;

import mitiv.cost.DifferentiableCostFunction;
import mitiv.exception.IncorrectSpaceException;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;
import mitiv.utils.Timer;

/**
 * Implement iterative solving of a smooth optimization problem.
 *
 * @author Éric
 */
public class IterativeDifferentiableSolver {
    private VectorSpace space = null;
    private DifferentiableCostFunction cost = null;
    private ReverseCommunicationOptimizer optimizer = null;
    private Vector gx = null; // current gradient
    private Vector xBest = null; // best solution so far
    private Vector gxBest = null; // gradient at best solution
    private double fx = -1; // current cost
    private double fxBest; // best cost function so far
    private boolean firstTime = true;
    private boolean stepping = false;
    private boolean saveBest = false;
    private final Timer timer = new Timer();
    private boolean updatePending = false;
    private int iterations = 0;
    private int evaluations = 0;
    private int restarts = 0;

    /** Maximum number of evaluations, -1 for no limits. */
    private int maxeval = -1;

    /** Maximum number of iterations, -1 for no limits. */
    private int maxiter = 200;

    /**
     * Create a new solver.
     *
     * <p>
     * A solver instance can be used to simplify the control of a reverse
     * communication solver.  For instance:
     * <pre>
     * solver = new IterativeDifferentiableSolver(cost, optimizer);
     * x = ...; // initial solution
     * OptimTask task = solver.start();
     * while (true) {
     *     if (task == OptimTask.NEW_X || task == OptimTask.FINAL_X) {
     *         // Display new solution.
     *         ...;
     *         if (task == OptimTask.FINAL_X) {
     *             break;
     *         }
     *     } else if (task == OptimTask.COMPUTE_FG) {
     *         if (solver.getEvaluations() > maxEvals) {
     *             // too many evaluations
     *             ...;
     *         }
     *     } else {
     *         System.err.println("Something wrong occurred: %s\n",
     *                 solver.getReason());
     *         break;
     *     }
     *     task = solver.iterate();
     * }
     * </pre>
     * @param cost       - The cost function.
     * @param optimizer  - The optimizer.
     */
    public IterativeDifferentiableSolver(DifferentiableCostFunction cost,
            ReverseCommunicationOptimizer optimizer) {
        setComponents(cost, optimizer);
    }

    public IterativeDifferentiableSolver() {
    }

    /**
     * Start iterations with some initial variables.
     *
     * <p>
     * This method is equivalent to {@code start(x, false)}.
     *
     * @param x     - On entry, the initial variables.  On exit, the new
     *                value of the variables.
     *
     * @return The next pending task.
     */
    public OptimTask start(Vector x) {
        return start(x, false);
    }

    /**
     * Start or restart iterations with some initial variables.
     *
     * @param x     - On entry, the initial variables.  On exit, the new
     *                value of the variables.
     * @param reset - Indicate whether to reset all internal counters
     *                 (iterations, evaluations, restarts) and timer.
     *
     * @return The next pending task.
     */
    public OptimTask start(Vector x, boolean reset) {
        /* Check for proper initialization. */
        if (optimizer == null) {
            throw new RuntimeException("Optimizer not yet specified");
        }
        if (cost == null) {
            throw new RuntimeException("Cost function not yet specified");
        }

        /* Cleanup workspace and, maybe, reset counters. */
        if (gx == null || gx.getSpace() != space) {
            gx = space.create();
        }
        if (! saveBest || (xBest != null && xBest.getSpace() != space)) {
            xBest = null;
        }
        if (! saveBest || (gxBest != null && gxBest.getSpace() != space)) {
            gxBest = null;
        }
        updatePending = false;
        firstTime = true;
        if (reset) {
            timer.start();
            timer.stop();
            iterations = 0;
            evaluations = 0;
            restarts = 0;
        }

        /* After reverse communication start(), task should be COMPUTE_FG
           once and then NEW_X or FINAL_X. */
        OptimTask task = optimizer.start();
        while (task == OptimTask.COMPUTE_FG) {
            task = computeFG(x);
            if (stepping) {
                break;
            }
        }
        return task;
    }

    /**
     * Proceed with next step of the iterative method.
     *
     * @param x - On entry, the current variables.  On exit, the new
     *            value of the variables.
     *
     * @return The next pending task.
     */
    public OptimTask iterate(Vector x) {
        if (updatePending) {
            return start(x, false);
        } else {
            OptimTask task = optimizer.getTask();
            if (task == OptimTask.ERROR || task == OptimTask.WARNING) {
                return task;
            }
            if (maxiter >= 0 && iterations >= maxiter) {
                return optimizer.warning(OptimStatus.TOO_MANY_ITERATIONS);
            }
            if (task == OptimTask.NEW_X || task == OptimTask.FINAL_X) {
                /* Proceed with next iteration which should yield COMPUTE_FG. */
                task = optimizer.iterate(x, fx, gx);
            }
            while (task == OptimTask.COMPUTE_FG) {
                task = computeFG(x);
                if (stepping) {
                    break;
                }
            }
            if (task == OptimTask.NEW_X || task == OptimTask.FINAL_X) {
                ++iterations;
            }
            return task;
        }
    }

    private OptimTask computeFG(Vector x) {
        if (maxeval >= 0 && evaluations >= maxeval) {
            return optimizer.warning(OptimStatus.TOO_MANY_EVALUATIONS);
        }
        int restarts = optimizer.getRestarts();
        timer.resume();
        fx = cost.computeCostAndGradient(1.0, x, gx, true);
        timer.stop();
        ++evaluations;
        restarts = (optimizer.getRestarts() - restarts);
        if (restarts > 0) {
            this.restarts += restarts;
        }
        if (firstTime || fx < fxBest) {
            if (saveBest) {
                if (xBest == null) {
                    xBest = space.create();
                }
                xBest.copy(x);
            }
            fxBest = fx;
            firstTime = false;
        }
        return optimizer.iterate(x, fx, gx);
    }


    /**
     * Retrieve the current value of the cost function.
     *
     * @return The current value of the cost function.
     */
    public double getCost() {
        return fx;
    }

    /**
     * Retrieve the best solution so far.
     *
     * @return The best solution so far or <b>null</b> if the best solution is
     *          not saved.
     */
    public Vector getBestSolution() {
        return xBest;
    }

    /**
     * Retrieve the best value of the cost function so far.
     *
     * @return The best value of the cost function so far.
     */
    public double getBestCost() {
        return fxBest;
    }

    /**
     * Retrieve the current gradient.
     *
     * <p>
     * The returned vector should be considered as read-only.
     *
     * @return The current gradient.
     */
    public Vector getGradient() {
        return gx;
    }

    /**
     * Retrieve the gradient at the best solution so far.
     *
     * <p>
     * The returned vector should be considered as read-only.
     *
     * @return The gradient at the best solution so far or <b>null</b> if the
     *          best gradient is not saved.
     */
    public Vector getBestGradient() {
        return gxBest;
    }

    /**
     * Get current stepping.
     *
     * This function returns the current behavior of the iterator.  When
     * stepping is false (the default) the algorithm automatically iterates
     * for each line search and the returned task by the {@link #start()} and
     * {@link #iterate()} methods is only {@link OptimTask#NEW_X} or
     * {@link OptimTask#FINAL_X} or an error.  When stepping is false, each
     * call to the {@link #start} and {@link #iterate} methods results in at
     * most one cost function evaluation and the returned task can also be
     * {@link OptimTask#COMPUTE_FG} to indicate that the current line search
     * has not converged.
     *
     * @return Current stepping setting.
     */
    public boolean getStepping() {
        return stepping;
    }

    /**
     * Set the stepping behavior.
     *
     * @param value - The new stepping value.
     *
     * @see {@link #getStepping()} for a description of the stepping behavior.
     */
    public void setStepping(boolean value) {
        stepping = value;
    }

    /**
     * Get the iteration number.
     *
     * @return The number of iterations since last start.
     */
    public int getIterations() {
        return iterations;
    }

    /**
     * Get the number of function (and gradient) evaluations since last start.
     *
     * @return The number of function and gradient evaluations since last start.
     */
    public int getEvaluations() {
        return evaluations;
    }

    /**
     * Get the number of restarts.
     *
     * @return The number of times algorithm has been restarted since last
     *         start.
     */
    public int getRestarts() {
        return restarts;
    }

    /**
     * Get time taken by computations in seconds.
     *
     * @return The elapsed time in seconds.
     */
    public double getElapsedTime() {
        return timer.getElapsedTime();
    }

    /**
     * Get the maximum number of evaluations.
     *
     * <p>
     * The {@link #iterate()} and {@link #start()} methods of the solver return
     * a warning ({@link OptimTask#WARNING}) if the cost function is about to
     * be called more than this parameter.  A negative value allows an infinite
     * number of evaluations.
     *
     * @return The maximum number of evaluations.
     * @see {@link #setMaximumEvaluations()}, {@link #getEvaluations()}.
     */
    public int getMaximumEvaluations() {
        return maxeval;
    }

    /**
     * Set the maximum number of evaluations.
     * @see {@link #getMaximumEvaluations()}, {@link #getEvaluations()}.
     */
    public void setMaximumEvaluations(int value) {
        if (value < 0) {
            value = -1;
        }
        maxeval = value;
    }

    /**
     * Get the maximum number of iterations.
     *
     * <p>
     * The {@link #iterate()} and {@link #start()} methods of the solver return
     * a warning ({@link OptimTask#WARNING}) if the number of iteration exceeds
     * this parameter.  A negative value allows an infinite number of iterations.
     *
     * @return The maximum number of iterations.
     * @see {@link #setMaximumIterations()}, {@link #getIterations()}.
     */
    public int getMaximumIterations() {
        return maxiter;
    }

    /**
     * Set the maximum number of iterations.
     * @see {@link #getMaximumIterations()}, {@link #getIterations()}.
     */
    public void setMaximumIterations(int value) {
        if (value < 0) {
            value = -1;
        }
        maxiter = value;
    }

    /**
     * Get the optimizer used to solve the problem.
     *
     * @return The current reverse communication optimizer (may be
     * {@code null} if none has been chosen yet).
     */
    public ReverseCommunicationOptimizer getOptimizer() {
        return optimizer;
    }

    /**
     * Set the optimizer used to solve the problem.
     */
    public void setOptimizer(ReverseCommunicationOptimizer optimizer) {
        setComponents(this.cost, optimizer);
    }

    /**
     * Get the cost function of the problem.
     *
     * @return The current cost function (may be {@code null} if none
     * has been chosen yet).
     */
    public DifferentiableCostFunction getCostFunction() {
        return cost;
    }

    /**
     * Set the cost function of the problem.
     * @param cost - The new differentiable cost function.
     */
    public void setCostFunction(DifferentiableCostFunction cost) {
        setComponents(cost, this.optimizer);
    }

    /**
     * Get the vector space of the variables of the problem.
     */
    public VectorSpace getSpace() {
        return space;
    }

    /**
     * Get whether the best solution is saved.
     * @return A boolean value.
     * @see {@link #getBestSolution()}, {@link #setSaveBest()}.
     */
    public boolean getSaveBest() {
        return saveBest;
    }

    /**
     * Set whether the best solution is saved.
     * <p>
     * The last tried solution may not be the best one.  The solver may keep
     * a track of the best (according to the value of the cost function) of the
     * best solution so that it is possible to retrieve it at any time.  The
     * drawback is that this require some memory.
     * @param value - True, to save the best solution; false, to save memory.
     * @see {@link #getBestSolution()}, {@link #getSaveBest()}.
     */
    public void setSaveBest(boolean value) {
        saveBest = value;
    }

    private void setComponents(DifferentiableCostFunction cost,
            ReverseCommunicationOptimizer optimizer) {
        if (optimizer != this.optimizer || cost != this.cost) {
            if (cost != null && optimizer != null && cost.getInputSpace() != optimizer.getSpace()) {
                throw new IncorrectSpaceException("Optimizer and cost function must operate on the same vector space");
            }
            this.optimizer = optimizer;
            this.cost = cost;
            if (cost != null) {
                space = cost.getInputSpace();
            } else if (optimizer != null) {
                space = optimizer.getSpace();
            } else {
                space = null;
            }
            updatePending = true;
        }
    }

    /**
     * Retrieve description of last operation.
     *
     * @return A message describing the result of last iterative operation.
     */
    public String getReason() {
        if (optimizer == null) {
            return "Iterative algorithm not yet specified";
        }
        return optimizer.getReason();
    }

    /**
     * Retrieve pending optimization task.
     *
     * @return The pending optimization task.
     */
    public OptimTask getTask() {
        if (optimizer == null) {
            return OptimTask.ERROR;
        }
        return optimizer.getTask();
    }

}

