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

import mitiv.linalg.Vector;

/**
 * Interface for differentiable cost functions.
 * 
 * A differentiable cost function is a smooth cost function for which it
 * is possible to compute the function value and its gradient.
 * 
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */
public interface DifferentiableCostFunction extends CostFunction {
    /**
     * Compute the value of the cost function and its gradient.
     *
     * <p>
     * This method is intended to compute the value and the gradient of a
     * cost function which are needed by limited memory non-linear optimization
     * methods like the non-linear conjugate gradient and quasi-Newton methods
     * such as L-BFGS.
     * <p>
     * The rationale of the {@code alpha} and {@code clear} arguments is to
     * let one builds a composite cost function efficiently.  Below is the
     * pseudo-code of an example which shows how to compute the value and the
     * gradient of a function which is a linear combination of other functions
     * (in the array {@code f}) with different multipliers (in the array
     * {@code alpha}):
     *
     * <pre>
     *     DifferentiableCostFunction[] f;
     *     double[] alpha;
     *     Vector x;
     *     Vector gx;
     *     double fx;
     *     fx = 0.0;
     *     for (int j = 0; j < f.length; ++j) {
     *         fx += f[j].computeCostAndGradient(alpha[j], x, gx, (j == 0));
     *     }
     * </pre>
     * 
     * Note that the gradient vector has to be cleared for the first
     * ({@code j == 0}) function of the list.
     *
     * @param alpha - A non-negative multiplier for the cost.
     * @param x     - The vector of variables.
     * @param gx    - The vector to store the gradient of the cost function times the
     *                weight.
     * @param clr   - Indicate whether the gradient vector has to be cleared (that is,
     *                filled with zero) prior to the computation.  If false, the
     *                contents of the gradient vector is incremented with the gradient
     *                of the cost function.
     * 
     * @return The value of the cost function times the weight.
     */
    abstract public double computeCostAndGradient(double alpha, Vector x,
            Vector gx, boolean clr);
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
