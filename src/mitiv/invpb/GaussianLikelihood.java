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

import mitiv.base.mapping.Mapping;
import mitiv.cost.CostFunction;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Implement simple Gaussian co-log-likelihood cost function.
 *
 * <p>
 * The Gaussian co-log-likelihood cost writes:
 * <p>
 * </p>
 * <p align="center">
 * f(<b><i>x</i></b>) = (1/2) ||<b>H</b>(<b><i>x</i></b>) - <b><i>y</i></b>||<sup>2</sup><sub><b>W</b></sub>
 * </p>
 * <p>
 * with <b><i>x</i></b> the variables, <b>H</b>(<b><i>x</i></b>) the direct model,
 * <b><i>y</i></b> the data and <b>W</b> the weighting operator (which must be
 * positive semi-definite).  In this simple implementation, the weighting operator
 * is diagonal: <b>W</b>=diag(<b><i>w</i></b>).  The weighted norm of a vector
 * <b><i>u</i></b> is given by:
 * </p>
 * <p align="center">
 * ||<b><i>u</i></b>||<sup>2</sup><sub><b>W</b></sub>
 * = <b><i>u</i></b><sup>t</sup>.<b>W</b>.<b><i>u</i></b>
 * </p>
 * <p>
 * This class uses an instance of {@link WeightedData} to store the data
 * <b><i>y</i></b> and the weights <b><i>w</i></b> and an instance of
 * {@link Mapping} to implement the direct model <b>H</b>(<b><i>x</i></b>).
 * </p>
 *
 * @author Éric
 */
public class GaussianLikelihood implements CostFunction {
    protected final WeightedData weightedData;
    protected final Mapping directModel;
    protected final VectorSpace variableSpace;
    protected final ShapedVectorSpace dataSpace;
    protected ShapedVector work1 = null; // work vector in data space
    protected boolean ignoreWeights = false;

    public GaussianLikelihood(WeightedData weightedData, Mapping directModel) {
        if (directModel.getOutputSpace() != weightedData.getDataSpace()) {
            throw new IllegalArgumentException("Output space of the direct model must be the data space");
        }
        this.directModel = directModel;
        this.variableSpace = directModel.getInputSpace();
        this.dataSpace = weightedData.getDataSpace();
        this.weightedData = weightedData;
    }

    @Override
    public final VectorSpace getInputSpace() {
        return variableSpace;
    }

    /**
     * Get space of the variables.
     * @return The variables space.
     */
    public final VectorSpace getVariableSpace() {
        return variableSpace;
    }

    /**
     * Get the data space.
     * @return The data space.
     */
    public final ShapedVectorSpace getDataSpace() {
        return dataSpace;
    }

    /**
     * Get the data.
     * @return The data.
     */
    public final ShapedVector getData() {
        return weightedData.getData();
    }

    /**
     * Get the weights.
     * @return The weights.
     */
    public final ShapedVector getWeight() {
        return weightedData.getWeight();
    }

    /**
     * Query whether the data are stored as single precision
     * floating-point values.
     * @return A boolean.
     */
    public final boolean singlePrecision() {
        return weightedData.singlePrecision();
    }

    /**
     * Compute the direct model.
     * @param x - The given variables.
     * @return A vector with the direct model <b>H</b>(<b><i>x</i></b>) of the
     *         data for the given variables <b><i>x</i></b>.
     */
    public final ShapedVector computeModel(ShapedVector x) {
        final ShapedVector dst = dataSpace.create();
        computeModel(dst, x);
        return dst;
    }

    /**
     * Compute the direct model.
     *
     * <p>
     * This method computes the direct model <b>H</b>(<b><i>x</i></b>) of the
     * data for the given variables <b><i>x</i></b> and stores it in the
     * destination vector.

     * @param dst - The destination vector to store the direct model
     *              <b>H</b>(<b><i>x</i></b>).
     * @param src - The source vector with the given variables <b><i>x</i></b>.
     */
    public final void computeModel(ShapedVector dst, Vector src) {
        directModel.apply(dst, src);
    }

    /** Compute the (anti-)residuals in a protected work vector. */
    protected final void computeResiduals(Vector x) {
        /* Compute the direct model. */
        if (work1 == null) {
            work1 = dataSpace.create();
        }
        computeModel(work1, x);

        /* Compute the residuals. */
        work1.combine(1.0, work1, -1.0, getData());
    }

    @Override
    public final double evaluate(double alpha, Vector x) {
        /* Shortcut? */
        if (alpha == 0.0) {
            return 0.0;
        }

        /* Compute the residuals. */
        computeResiduals(x);

        /* Compute the cost. */
        double sum = 0.0;
        if (ignoreWeights) {
            sum = work1.norm2();
        } else if (singlePrecision()){
            final float[] r = ((FloatShapedVector)work1).getData();
            final float[] w = ((FloatShapedVector)getWeight()).getData();
            for (int i = 0; i < r.length; ++i) {
                sum += w[i]*r[i]*r[i];
            }
        } else {
            final double[] r = ((DoubleShapedVector)work1).getData();
            final double[] w = ((DoubleShapedVector)getWeight()).getData();
            for (int i = 0; i < r.length; ++i) {
                sum += w[i]*r[i]*r[i];
            }
        }
        return alpha*sum/2;
    }

}
