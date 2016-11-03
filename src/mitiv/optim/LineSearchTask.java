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

public enum LineSearchTask {
    ERROR(-1, "An error has occured in line search"),
    SEARCH(0, "Line search in progress"),
    CONVERGENCE(1, "Line search has converged"),
    WARNING(2, "Line search teminated with a warning");

    private final String description;
    private final int code;

    LineSearchTask(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Query the description of the optimization task.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    /**
     * Get a numerical code corresponding to the optimization task.
     */
    public int getCode() {
        return code;
    }

    /**
     * Check whether line search status corresponds to a warning.
     */
    public boolean isWarning() {
        return (code > 1);
    }

    /**
     * Check whether line search status corresponds to an error.
     */
    public final boolean isError() {
        return (code < 0);
    }

}
