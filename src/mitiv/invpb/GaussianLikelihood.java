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

public class GaussianLikelihood implements CostFunction {
    protected final WeightedData weighteddata;
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
        this.weighteddata = weightedData;
    }

    @Override
    public final VectorSpace getInputSpace() {
        return variableSpace;
    }

    public final VectorSpace getVariableSpace() {
        return variableSpace;
    }

    public final ShapedVectorSpace getDataSpace() {
        return dataSpace;
    }

    public final ShapedVector getData() {
        return weighteddata.getData();
    }

    public final ShapedVector getWeight() {
        return weighteddata.getWeight();
    }

    public final boolean singlePrecision() {
        return weighteddata.singlePrecision();
    }

    public final ShapedVector computeModel(ShapedVector x) {
        final ShapedVector dst = dataSpace.create();
        computeModel(dst, x);
        return dst;
    }

    public final void computeModel(ShapedVector dst, Vector src) {
        directModel.apply(dst, src);
    }

    /** Compute the (anti-)residuals in a protected work vector. */
    protected final ShapedVector computeResiduals(Vector x) {
        /* Compute the direct model. */
        if (work1 == null) {
            work1 = dataSpace.create();
        }
        computeModel(work1, x);

        /* Compute the residuals. */
        work1.combine(1.0, work1, -1.0, getData());

        return work1;
    }

    @Override
    public final double evaluate(double alpha, Vector x) {
        /* Shortcut? */
        if (alpha == 0.0) {
            return 0.0;
        }

        /* Compute the residuals. */
        final ShapedVector work = computeResiduals(x);

        /* Compute the cost. */
        double sum = 0.0;
        if (ignoreWeights) {
            sum = work.norm2();
        } else if (singlePrecision()){
            final float[] r = ((FloatShapedVector)work).getData();
            final float[] w = ((FloatShapedVector)getWeight()).getData();
            for (int i = 0; i < r.length; ++i) {
                sum += w[i]*r[i]*r[i];
            }
        } else {
            final double[] r = ((DoubleShapedVector)work).getData();
            final double[] w = ((DoubleShapedVector)getWeight()).getData();
            for (int i = 0; i < r.length; ++i) {
                sum += w[i]*r[i]*r[i];
            }
        }
        return alpha*sum/2;
    }

}
