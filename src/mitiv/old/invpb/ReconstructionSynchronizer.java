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

package mitiv.old.invpb;

/**
 * A reconstruction synchronizer is used to schedule an iterative
 * reconstruction and modify its parameters "<i>on the fly</i>".
 *
 * @author Éric and Jonathan.
 */
public class ReconstructionSynchronizer {

    /** The reconstruction process can run. */
    final public static int RUN = 0;

    /** The reconstruction process must stop. */
    final public static int STOP = 1;

    private int task = RUN;
    private double[] parameters = null;
    private int number = 0;

    /** Get the currently scheduled task. */
    public synchronized int getTask() {
        return task;
    }

    /**
     * Schedule the task for a reconstruction process.
     *
     *  <p> Any change in the scheduled task will be taken into account as
     * soon as possible by the reconstruction process.  If reconstruction has
     * not yet been started, the task will be taken into account when the
     * reconstruction is started.  </p>
     *
     * <p> Note that there is no queuing of the tasks any new value replaces
     * the current one.  </p>
     *
     * @param value
     *        {@link #RUN} to let the reconstruction run, or
     *        {@link #STOP} to interrupt it as soon as possible.
     */
    public synchronized void setTask(int value) {
        task = value;
    }

    /**
     * Set a given reconstruction parameter.
     *
     * <p> Any change in the reconstruction parameter values will be taken
     * into account as soon as possible by the reconstruction process.  If
     * reconstruction has not yet been started, the parameters will be taken
     * into account when the reconstruction is started.  </p>
     *
     * @param i
     *        The index of the parameter to change.
     *
     * @param value
     *        The new value of the parameter.
     *
     * @return Whether the parameter has changed.
     */
    public synchronized boolean setParameter(int i, double value) {
        if (parameters[i] != value) {
            parameters[i] = value;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Create an instance of synchronizer for a reconstruction task.
     *
     * @param init
     *        The initial values of the reconstruction method.
     */
    public ReconstructionSynchronizer(double[] init) {
        number = (init != null ? init.length : 0);
        task = RUN;
        parameters = new double[number];
        for (int i = 0; i < number; ++i) {
            parameters[i] = init[i];
        }
    }

    /**
     * Update the values of the reconstruction parameters.
     *
     * <p> This synchronized method compares its own set of parameters to the
     * ones given in argument.  The values of the argument are updated and any
     * change is reported.  </p>
     *
     * @param values
     *        The values of the parameter of the running reconstruction.
     *
     * @param changed
     *        On return, set to indicate which parameters have changed.
     *
     * @return Whether some parameters have changed.
     */
    public synchronized boolean updateParameters(double[] values, boolean[] changed) {
        boolean anyChange = false;
        for (int i = 0; i < number; ++i) {
            if (values[i] != parameters[i]) {
                anyChange = true;
                changed[i] = true;
                values[i] = parameters[i];
            } else {
                changed[i] = false;
            }
        }
        return anyChange;
    }

}
