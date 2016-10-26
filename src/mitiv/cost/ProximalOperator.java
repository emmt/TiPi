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
 * Interface for cost functions implementing their proximal operator.
 *
 * <p> The proximal operator of a function {@code f : R^n -> R^n} is defined
 * by: </p>
 *
 * <pre>
 *     prox_f(x) = argmin_y { f(y) + (1/2) ||x - y||^2 }
 * </pre>
 *
 * <p> Functions for which the proximal operator can be computed can be
 * minimized even though there are non-smooth and thus non-differentiable.
 * They are notably useful to implement constraints or to impose sparsity. See
 * reference below for a review. </p>
 *
 * <ol>
 * <li>Parikh, N. & Boyd, S. "<i>Proximal Algorithms,</i>" in Foundations and
 *     Trends in Optimization, Vol. <b>1</b>, pp. 123-231 (2013)
 *     <a href="http://web.stanford.edu/~boyd/papers/prox_algs.html">web<a>
 *     <a href="http://dx.doi.org/10.1561/2400000003">doi</a>.</li>
 * </ol>
 *
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */
public interface ProximalOperator extends CostFunction {
    /**
     * Apply the proximal operator of a cost function.
     *
     * <p> The purpose of this method is to find the minimum of: </p>
     *
     * <pre>
     * alpha*f(dst) + (1/2)*norm2(dst - src)
     * </pre>
     *
     * <p> with respect to vector {@code dst} and given weight {@code alpha}
     * and the input vector {@code src}.  The argument {@code tol} is a
     * relative tolerance parameter to find an approximate solution to the
     * problem. </p>
     *
     * @param dst
     *        The vector of output variables.
     *
     * @param alpha
     *        A non-negative weight for the cost.
     *
     * @param src
     *        The vector of input variables.
     *
     * @param tol
     *        A non-negative tolerance parameter.
     */
    abstract public void applyProx(Vector dst, double alpha, Vector src, double tol);
}
