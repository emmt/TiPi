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

public enum LineSearchStatus {
    ERROR_ILLEGAL_FX(                        -13, "Illegal function value."),
    ERROR_ILLEGAL_ADDRESS(                   -12, "Illegal address"),
    ERROR_CORRUPTED_WORKSPACE(               -11, "Corrupted workspace"),
    ERROR_BAD_WORKSPACE(                     -10, "Bad workspace"),
    ERROR_STP_CHANGED(                        -9, "Step changed"),
    ERROR_STP_OUTSIDE_BRACKET(                -8, "Step outside bracket"),
    ERROR_NOT_A_DESCENT(                      -7, "Not a descent direction"),
    ERROR_STPMIN_GT_STPMAX(                   -6, "Upper step bound smaller than lower bound"),
    ERROR_STPMIN_LT_ZERO(                     -5, "Lower step bound less than zero"),
    ERROR_STP_LT_STPMIN(                      -4, "Step below lower bound"),
    ERROR_STP_GT_STPMAX(                      -3, "Step above upper bound"),
    ERROR_INITIAL_DERIVATIVE_GE_ZERO(         -2, "Initial directional derivative greater or equal zero"),
    ERROR_NOT_STARTED(                        -1, "Linesearch not started"),
    SEARCH(                                    0, "Linesearch in progress"),
    CONVERGENCE(                               1, "Linesearch has converged"),
    WARNING_ROUNDING_ERRORS_PREVENT_PROGRESS(  2, "Rounding errors prevent progress"),
    WARNING_XTOL_TEST_SATISFIED(               3, "Search interval smaller than tolerance"),
    WARNING_STP_EQ_STPMAX(                     4, "Step at upper bound"),
    WARNING_STP_EQ_STPMIN(                     5, "Step at lower bound");

    private final String description;
    private final int code;

    LineSearchStatus(int code, String description) {
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
        return (code > CONVERGENCE.code);
    }

    /**
     * Check whether line search status corresponds to an error.
     */
    public final boolean isError()
    {
        return (code < 0);
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
