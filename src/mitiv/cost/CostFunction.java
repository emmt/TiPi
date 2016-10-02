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
import mitiv.linalg.VectorSpace;

/**
 * Base class for multivariate cost functions.
 * 
 * The solution of many inverse problems can be expressed as minimizing a cost
 * which is a function of the parameters of interest.  A cost function is then
 * just a mapping of an input vector space to the set of reals (possibly
 * including {@link Double#POSITIVE_INFINITY}.  An instance of this class is
 * able to return {@code f(x)} given the variables {@code x} (a vector).
 * <p>
 * There are many "optimizable" cost functions: smooth functions which are differentiable,
 * non-smooth functions for which we can compute their proximal operator, continuous
 * separable functions (which can be minimized by, e.g., Brent's method), non-smooth
 * separable functions but for which we can compute the subgradient, etc.
 * 
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */
public interface CostFunction {
    /**
     * Get the input space of the cost function.
     * 
     * A cost function is a mapping from an input space (which can be queried by this
     * method) to the set of reals (possibly including {@link Double#POSITIVE_INFINITY}).
     * @return The input space of the cost function.
     */
    abstract public VectorSpace getInputSpace();

    /**
     * Compute the value of the cost function.
     *
     * @param alpha - A non-negative multiplier for the cost.
     * @param x     - The vector of variables.
     * 
     * @return The value of the cost function times {@code alpha}.
     */
    abstract public double evaluate(double alpha, Vector x);

}

