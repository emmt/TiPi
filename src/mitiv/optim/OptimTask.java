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

/**
 * Values returned by the reverse communication version of optimization
 * algorithms.
 *
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */
public enum OptimTask {
    /** An error has occurred. */
    ERROR (-1, "An error has occurred."),

    /** Caller shall compute {@code f(x)} and {@code g(x)}. */
    COMPUTE_FG (0, "Caller shall compute f(x) and g(x)."),

    /** A new iterate is available in {@code x}. */
    NEW_X (1, "A new iterate is available in x."),

    /** Algorithm has converged, solution is available in {@code x}. */
    FINAL_X (2, "Algorithm has converged, solution is available in x."),

    /** Algorithm terminated with a warning. */
    WARNING (3, "Algorithm terminated with a warning."),

    /** Caller shall compute {@code f(x)}. */
    COMPUTE_F (4, "Caller shall compute f(x)."),

    /** Caller shall project workspace vector {@code v} to the feasible set. */
    PROJECT_V (5, "Caller shall project v to the feasible set.");

    private final String description;
    private final int code;

    OptimTask(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Query the description of the optimization task.
     */
    @Override
    public String toString(){
        return description;
    }

    /**
     * Get a numerical code corresponding to the optimization task.
     */
    public int getCode() {
        return code;
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
