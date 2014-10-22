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

import java.util.Locale;


/**
 * A simple class to measure and integrate elapsed times.
 *
 * @author Éric Thiébaut.
 */
public class Timer {
    /* We use System.nanoTime() rather than System.currentTimeMillis()
     * to measure elapsed time.  The former is more accurate (though
     * probably not to the nanosecond despite its name) but cannot be
     * used for absolute time measurements.   Note that 2^63
     * nanoseconds is more than 292 years, so overflows are unexpected!
     */
    private long integrated = 0;
    private long marked = 0;
    private boolean running = false;
    private static final double NANOSECOND = 1e-9;

    /**
     * Start the timer.
     * <p>
     * Reset any integrated time and start timing at the current time.
     * </p>
     */
    public final void start() {
        integrated = 0;
        running = true;
        marked = System.nanoTime();
    }

    /**
     * Stop the timer.
     * <p>
     * Stop time integration, use {@link #resume()} method to resume integration
     * of time or {@link #start()} to restart the timer.
     * </p>
     */
    public final void stop() {
        if (running) {
            long current = System.nanoTime();
            integrated += current - marked;
            marked = current;
            running = false;
        }
    }

    /**
     * Resume the timer.
     * <p>
     * If timer has just been created, this is the same as calling the
     * {@link #start()} method.
     * </p>
     */
    public void resume() {
        if (! running) {
            marked = System.nanoTime();
            running = true;
        }
    }

    /**
     * Get elapsed time in seconds.
     * <p>
     * This method yields the total elapsed time integrated by the timer.
     * If the timer has been stopped the elapsed time will not change and
     * is simply given by the sum of the elapsed times between the last
     * call to {@link #start()} (denoted as {@code start} below) and the
     * next call to {@link #stop()} (denoted as {@code stop}<sub>1</sub>
     * below) and between the successive calls to {@link #resume()} and
     * {@link stop()} since then (denoted as {@code resume}<sub><i>k</i></sub>
     * and {@code stop}<sub><i>k</i></sub> respectively for the <i>k</i>-th
     * call).  The formula writes:
     * <pre>
     *   (stop<sub>1</sub> - start) + (resume<sub>1</sub> - stop<sub>2</sub>) + ... + (resume<sub><i>n</i>-1</sub> - stop<sub><i>n</i></sub>)
     * </pre>
     * If the timer is running the elapsed time between the current time
     * (denoted as {@code current} below) and the last call to
     * {@link #resume()} (denoted as {@code resume}<sub><i>n</i></sub>
     * below) is added, the formula becomes:
     * <pre>
     *   (stop<sub>1</sub> - start) + (resume<sub>1</sub> - stop<sub>2</sub>) + ... + (resume<sub><i>n</i></sub> - current)
     * </pre>
     * Thus the same timer can be used to integrate execution time.
     * </p>
     * @return The elapsed time in seconds.
     */
    public double getElapsedTime() {
        long total = integrated;
        if (running) {
            total += (System.nanoTime() - marked);
        }
        return total*NANOSECOND;
    }

    public static void main(String[] args) {
        // Switch to "US" locale to avoid problems with number formats.
        Locale.setDefault(Locale.US);
        Timer t1 = new Timer();
        Timer t2 = new Timer();
        long sum = 0;
        t1.start();
        t2.start();
        for (int j = 0; j < 1000; ++j) {
            sum += j*j;
            t2.stop();
            for (int k = 0; k < j; ++k) {
                sum += k*k*k;
            }
            t2.resume();
        }
        t2.stop();
        System.out.format("elapsed time 1 = %.9f s\n", t1.getElapsedTime());
        System.out.format("elapsed time 2 = %.9f s\n", t2.getElapsedTime());
        System.out.format("result = %d\n", sum);
        System.out.format("elapsed time 1 = %.9f s\n", t1.getElapsedTime());
        System.out.format("elapsed time 2 = %.9f s\n", t2.getElapsedTime());
        System.out.format("result = %d\n", sum);
        System.out.format("elapsed time 1 = %.9f s\n", t1.getElapsedTime());
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
