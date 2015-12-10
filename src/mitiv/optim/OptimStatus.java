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
public enum OptimStatus {

    SUCCESS("Success"),
    INVALID_ARGUMENT("Invalid argument"),
    INSUFFICIENT_MEMORY("Insufficient memory"),
    ILLEGAL_ADDRESS("Illegal address"),
    NOT_IMPLEMENTED("Not implemented"),
    CORRUPTED_WORKSPACE("Corrupted workspace"),
    BAD_SPACE("Bad variable space"),
    OUT_OF_BOUNDS_INDEX("Out of bounds index"),
    NOT_STARTED("Line search not started"),
    NOT_A_DESCENT("Not a descent direction"),
    STEP_CHANGED("Step changed"),
    STEP_OUTSIDE_BRACKET("Step outside bracket"),
    STPMIN_GT_STPMAX("Lower step bound larger than upper bound"),
    STPMIN_LT_ZERO("Minimal step length less than zero"),
    STEP_LT_STPMIN("Step lesser than lower bound"),
    STEP_GT_STPMAX("Step greater than upper bound"),
    FTOL_TEST_SATISFIED("Convergence within variable tolerance"),
    GTOL_TEST_SATISFIED("Convergence within function tolerance"),
    XTOL_TEST_SATISFIED("Convergence within gradient tolerance"),
    STEP_EQ_STPMAX("Step blocked at upper bound"),
    STEP_EQ_STPMIN("Step blocked at lower bound"),
    ROUNDING_ERRORS_PREVENT_PROGRESS("Rounding errors prevent progress"),
    BAD_PRECONDITIONER("Preconditioner is not positive definite"),
    INFEASIBLE_BOUNDS("Box set is infeasible"),
    WOULD_BLOCK("Variables cannot be improved (would block)"),
    UNDEFINED_VALUE("Undefined value");

    private final String description;

    OptimStatus(String description) {
        this.description = description;
    }

    /**
     * Query the description of the optimization task.
     */
    @Override
    public String toString(){
        return description;
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
