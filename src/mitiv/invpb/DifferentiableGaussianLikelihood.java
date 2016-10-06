/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014-2016 the MiTiV project, http://mitiv.univ-lyon1.fr/
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

package mitiv.invpb;

import mitiv.base.mapping.DifferentiableMapping;
import mitiv.base.mapping.Mapping;
import mitiv.cost.DifferentiableCostFunction;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.FloatShapedVector;

/**
 * Implement simple Gaussian co-log-likelihood differentiable cost function.
 *
 * <p>
 * This sub-class of {@link GaussianLikelihood} exploits a differentiable
 * mapping for the direct model to implement a differentiable cost function
 * whose gradient is given by:
 * <p>
 * </p>
 * <p align="center">
 * &nabla;f(<b><i>x</i></b>) = &nabla;<b>H</b>(<b><i>x</i></b>)<sup>*</sup>.<b>W</b>.
 * (<b>H</b>(<b><i>x</i></b>) - <b><i>y</i></b>)
 * </p>
 * <p>
 * with <b><i>x</i></b> the variables, <b>H</b>(<b><i>x</i></b>) the direct
 * model, &nabla;<b>H</b>(<b><i>x</i></b>) it Jacobian, <b><i>y</i></b> the
 * data and <b>W</b> the weighting operator.
 * </p>
 * <p>
 * This class uses an instance of {@link WeightedData} to store the data
 * <b><i>y</i></b> and the weights <b><i>w</i></b> and an instance of
 * {@link Mapping} to implement the direct model <b>H</b>(<b><i>x</i></b>).
 * </p>
 *
 * @author Éric
 */
public class DifferentiableGaussianLikelihood extends GaussianLikelihood implements DifferentiableCostFunction {
    protected Vector work2 = null; // work vector in variable space

    public DifferentiableGaussianLikelihood(WeightedData weightedData,
            DifferentiableMapping directModel) {
        super(weightedData, directModel);
    }

    @Override
    public double computeCostAndGradient(double alpha, Vector x, Vector gx, boolean clr)
    {
        /* Shortcut? */
        if (alpha == 0.0) {
            if (clr) {
                gx.zero();
            }
            return 0.0;
        }

        /* Compute the residuals (in work1). */
        computeResiduals(x);

        /* Compute the cost and the weighted residuals. */
        double sum = 0.0;
        if (ignoreWeights) {
            sum = work1.norm2();
        } else if (singlePrecision()) {
            final float[] r = ((FloatShapedVector)work1).getData();
            final float[] w = ((FloatShapedVector)getWeight()).getData();
            for (int i = 0; i < r.length; ++i) {
                final float ri = r[i];
                final float wr = w[i]*ri;
                sum += wr*ri;
                r[i] = wr;
            }
        } else {
            final double[] r = ((DoubleShapedVector)work1).getData();
            final double[] w = ((DoubleShapedVector)getWeight()).getData();
            for (int i = 0; i < r.length; ++i) {
                final double ri = r[i];
                final double wr = w[i]*ri;
                sum += wr*ri;
                r[i] = wr;
            }
        }

        /* Compute the gradient by applying the Jacobian of the model to the weighted residuals. */
        if (clr) {
            ((DifferentiableMapping)directModel).applyJacobian(gx, x, work1);
            gx.scale(alpha);
        } else {
            if (work2 == null) {
                work2 = variableSpace.create();
            }
            ((DifferentiableMapping)directModel).applyJacobian(work2, x, work1);
            gx.combine(1.0, gx, alpha, work2);
        }
        return alpha*sum/2;
    }

}
