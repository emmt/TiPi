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

import java.io.PrintStream;

import mitiv.utils.Timer;

/**
 * Implement a simple viewer for displaying information during an iterative
 * reconstruction.
 *
 * @author Éric and Jonathan
 */
public class SimpleViewer implements ReconstructionViewer {

    private PrintStream stream;
    private Timer timer;

    /**
     * Create a simple viewer which prints to the standard output stream.
     */
    public SimpleViewer() {
        this(null);
        timer = new Timer();
        timer.start();
    }

    /**
     * Create a simple viewer which prints to a given output stream.
     */
    public SimpleViewer(PrintStream output) {
        setOutput(output);
    }

    /**
     * Set the output stream of the viewer.
     *
     *  @param output
     *         The output stream to use, {@code null} to use the standard
     *         output stream.
     */
    public void setOutput(PrintStream output) {
        stream = (output == null ? System.out : output);
    }

    /**
     * get the output stream of the viewer.
     */
    public PrintStream getOutput() {
        return stream;
    }

    @Override
    public void display(ReconstructionJob job) {
        stream.format("iter: %4d    eval: %4d    time: %7.3f s.    fx = %22.16e    |gx| = %8.2e\n",
                job.getIterations(), job.getEvaluations(),
                timer.getElapsedTime(),
                job.getCost(), job.getGradientNorm2());
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
