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

package mitiv.cost;

import mitiv.exception.IncorrectSpaceException;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

/**
 * Class implementing a combination of differentiable cost functions.
 *
 * An instance of this class is a weighted sum of differentiable cost functions
 * and is itself a differentiable cost function.  When computing the function value
 * and its gradient, it takes care of avoiding unnecessary calculations.  A
 * restriction is that the input spaces of all the combined costs functions must
 * be the same.
 *
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */
public class CompositeDifferentiableCostFunction implements DifferentiableCostFunction {
    private final VectorSpace inputSpace;
    private final DifferentiableCostFunction[] func;
    private final double[] wght;

    /** Number of function evaluations. */
    protected int nfx = 0;

    /** Number of gradient evaluations. */
    protected int ngx = 0;

    /**
     * Combine a single cost function in a composite cost function.
     *
     * @param w1 - The weight of the cost function (must be finite and nonnegative).
     * @param f1 - The cost function.
     *
     * @throws IllegalArgumentException if any one of the weights is invalid or if the cost
     * functions do not have the same input space.
     */
    public CompositeDifferentiableCostFunction(double w1, DifferentiableCostFunction f1) {
        checkWeight(w1);
        inputSpace = f1.getInputSpace();
        func = new DifferentiableCostFunction[]{f1};
        wght = new double[]{w1};
    }


    /**
     * Combine two cost functions in a composite cost function.
     *
     * The cost function of a typical inverse problem writes:
     * <pre>
     *     f(x) = fdata(x) + mu*fprior(x);
     * </pre>
     * with:
     * <ul>
     * <li> fdata  - A likelihood term which imposes consistency with the data.</li>
     * <li> mu     - The regularization level.</li>
     * <li> fprior - A regularization term which imposes consistency with the priors.</li>
     * </ul>
     * Such a composite cost function is created by:
     * <pre>
     *     f = new CompositeDifferentiableCostFunction(1.0, fdata, mu, fprior);
     * </pre>
     *
     * @param w1 - The weight of the first cost function (must be finite and nonnegative).
     * @param f1 - The first cost function.
     * @param w2 - The weight of the second cost function (must be finite and nonnegative).
     * @param f2 - The second cost function.
     *
     * @throws IllegalArgumentException if any one of the weights is invalid or if the cost
     * functions do not have the same input space.
     */
    public CompositeDifferentiableCostFunction(double w1, DifferentiableCostFunction f1,
            double w2, DifferentiableCostFunction f2) {
        checkWeight(w1);
        checkWeight(w2);
        inputSpace = f1.getInputSpace();
        if (f2.getInputSpace() != inputSpace) {
            throw new IncorrectSpaceException("All functions must have the same input space.");
        }
        func = new DifferentiableCostFunction[]{f1, f2};
        wght = new double[]{w1, w2};
    }

    /**
     * Combine three cost functions in a composite cost function.
     *
     * @param w1 - The weight of the first cost function (must be finite and nonnegative).
     * @param f1 - The first cost function.
     * @param w2 - The weight of the second cost function (must be finite and nonnegative).
     * @param f2 - The second cost function.
     * @param w3 - The weight of the third cost function (must be finite and nonnegative).
     * @param f3 - The third cost function.
     *
     * @throws IllegalArgumentException if any one of the weights is invalid or if the cost
     * functions do not have the same input space.
     */
    public CompositeDifferentiableCostFunction(double w1, DifferentiableCostFunction f1,
            double w2, DifferentiableCostFunction f2,
            double w3, DifferentiableCostFunction f3) {
        checkWeight(w1);
        checkWeight(w2);
        checkWeight(w3);
        inputSpace = f1.getInputSpace();
        if (f2.getInputSpace() != inputSpace || f3.getInputSpace() != inputSpace) {
            throw new IncorrectSpaceException("All functions must have the same input space.");
        }
        func = new DifferentiableCostFunction[]{f1, f2, f3};
        wght = new double[]{w1, w2, w3};
    }

    @Override
    public double computeCostAndGradient(double alpha, Vector x, Vector gx, boolean clr) {
        double cost = 0.0;
        if (alpha == 0.0) {
            if (clr) {
                gx.fill(0.0);
            }
        } else {
            for (int k = 0; k < func.length; ++k) {
                if (wght[k] != 0.0) {
                    cost += func[k].computeCostAndGradient(alpha*wght[k], x, gx, clr);
                    clr = false;
                }
            }
        }
        ++nfx;
        ++ngx;
        return cost;
    }

    @Override
    public VectorSpace getInputSpace() {
        return inputSpace;
    }

    @Override
    public double evaluate(double alpha, Vector x) {
        double cost = 0.0;
        if (alpha != 0.0) {
            for (int k = 0; k < func.length; ++k) {
                if (wght[k] != 0.0) {
                    cost += func[k].evaluate(alpha*wght[k], x);
                }
            }
        }
        ++nfx;
        return cost;
    }

    /**
     * Get number of function evaluations.
     * @return The number of function evaluations.
     */
    public int getNumberOfFunctionCalls() {
        return nfx;
    }

    /**
     * Get number of gradient evaluations.
     * @return The number of gradient evaluations.
     */
    public int getNumberOfGradientCalls() {
        return ngx;
    }

    static final private void checkWeight(double w)
    {
       if (Double.isNaN(w) || Double.isInfinite(w) || w < 0.0) {
           throw new IllegalArgumentException("Cost function weight must be finite and nonnegative");
       }
    }
}
