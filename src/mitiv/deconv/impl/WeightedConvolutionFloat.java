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

package mitiv.deconv.impl;

import mitiv.array.ShapedArray;
import mitiv.base.Traits;
import mitiv.deconv.WeightedConvolutionCost;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Abstract class for FFT-based weighted convolution of arrays of float's.
 *
 * @author Éric Thiébaut
 */
public abstract class WeightedConvolutionFloat
     extends WeightedConvolutionCost
{
    /** The data. */
    protected float dat[] = null;

    /** The statistical weights of the data. */
    protected float wgt[] = null;


    /**
     * The following constructors make this class non instantiable, but still
     * let others inherit from this class.
     */
    public WeightedConvolutionFloat(ShapedVectorSpace variableSpace,
                        ShapedVectorSpace dataSpace)
    {
        /* Initialize super class and check types. */
        super(variableSpace, dataSpace);
        if (variableSpace.getType() != Traits.FLOAT) {
            throw new IllegalArgumentException("Variable space must be for float data type");
        }
        if (dataSpace.getType() != Traits.FLOAT) {
            throw new IllegalArgumentException("Data space must be for float data type");
        }
    }

    @Override
    public void setWeightsAndData(ShapedVector weight, ShapedVector data)
    {
        if (data == null || ! data.belongsTo(dataSpace)) {
            throw new IllegalArgumentException("Data must belong to the data space.");
        }
        if (weight == null) {
            setWeightsAndData(((FloatShapedVector)data).getData());
        } else if (weight.belongsTo(dataSpace)) {
            setWeightsAndData(((FloatShapedVector)weight).getData(),
                              ((FloatShapedVector)data).getData());
        } else {
            throw new IllegalArgumentException("Weights must belong to the data space.");
        }
    }

    @Override
    public void setWeightsAndData(ShapedArray weight, ShapedArray data)
    {
        if (data == null || ! dataSpace.getShape().equals(data.getShape())) {
            throw new IllegalArgumentException("Data must have the same shape as the vectors of the data space.");
        }
        if (weight == null) {
            setWeightsAndData(data.toFloat().flatten(false));
        } else if (dataSpace.getShape().equals(data.getShape())) {
            setWeightsAndData(weight.toFloat().flatten(false),
                              data.toFloat().flatten(false));
        } else {
            throw new IllegalArgumentException("Weights must have the same shape as the vectors of the data space.");
        }
    }

    private void setWeightsAndData(float data[])
    {
        wgt = null;
        dat = data;
    }

    private void setWeightsAndData(float weight[], float data[])
    {
        int n = weight.length;
        for (int k = 0; k < n; ++k) {
            float w = weight[k];
            if (Float.isNaN(w) || Float.isInfinite(w) || w < 0.0F) {
                badWeights();
            }
        }
        wgt = weight;
        dat = data;
    }

    protected void checkData()
    {
        if (dat == null) {
            throw new IllegalArgumentException("You must set the data (and the weights) first.");
        }
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
