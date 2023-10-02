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

/**
 *
 */
package mitiv.cost;

import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

/**
 * Separable cost function implementing the L1 pseudo-norm.
 * @author eric
 *
 */
public abstract class PseudoL1Norm implements ProximalOperator, HomogeneousFunction {
    protected VectorSpace inputSpace;

    protected PseudoL1Norm(VectorSpace inputSpace) {
        this.inputSpace = inputSpace;
    }

    /* (non-Javadoc)
     * @see mitiv.cost.CostFunction#getInputSpace()
     */
    @Override
    public VectorSpace getInputSpace() {
        return inputSpace;
    }

    /* (non-Javadoc)
     * @see mitiv.cost.CostFunction#evaluate(double, mitiv.linalg.Vector)
     */
    @Override
    public double evaluate(double alpha, Vector x) {
        return alpha*x.norm1();
    }


    @Override
    public double getHomogeneousDegree() {
        return 1;
    }

}
