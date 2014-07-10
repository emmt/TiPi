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
 * This class implement the differentiable cost function of a typical inverse problem.
 * <p>
 * The cost function of a typical inverse problem writes:
 * <pre>
 *     f(x) = fdata(x) + mu*fprior(x);
 * </pre>
 */
public class DifferentiableInverseProblemCost implements DifferentiableCostFunction {
    protected final VectorSpace inputSpace;
    protected final DifferentiableCostFunction fdata;
    protected final DifferentiableCostFunction fprior;
    protected double mu;
    protected int nfx, ngx;

    public DifferentiableInverseProblemCost(DifferentiableCostFunction fdata, double mu, DifferentiableCostFunction fprior) {
        if ((this.inputSpace = fdata.getInputSpace()) != fprior.getInputSpace()) {
            throw new IncorrectSpaceException("Fdata and Fprior must operate on the same vector space");
        }
        this.fdata = fdata;
        this.fprior = fprior;
        this.mu = mu;
        nfx = 0;
        ngx = 0;
    }

    public void setRelativeWeight(double value) {
        this.mu = value;
    }

    public double getRelativeWeight() {
        return mu;
    }

    public int getNumberOfFunctionCalls() {
        return nfx;
    }

    public int getNumberOfGradientCalls() {
        return ngx;
    }

    @Override
    public double evaluate(double alpha, Vector x) {
        double fx = fdata.evaluate(alpha, x) + fprior.evaluate(mu*alpha, x);
        ++nfx;
        return fx;
    }

    @Override
    public double computeCostAndGradient(double alpha, Vector x, Vector gx, boolean clr) {
        double fx = fdata.computeCostAndGradient(alpha, x, gx, clr);
        fx += fprior.computeCostAndGradient(mu*alpha, x, gx, false);
        ++nfx;
        ++ngx;
        return fx;
    }

    @Override
    public VectorSpace getInputSpace() {
        return inputSpace;
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
