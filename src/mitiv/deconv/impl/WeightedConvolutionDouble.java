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
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Abstract class for FFT-based weighted convolution of arrays of double's.
 *
 * @author Éric Thiébaut
 */
public abstract class WeightedConvolutionDouble
     extends WeightedConvolutionCost
{
    /** The data. */
    protected double dat[] = null;

    /** The statistical weights of the data. */
    protected double wgt[] = null;


    /**
     * The following constructors make this class non instantiable, but still
     * let others inherit from this class.
     */
    public WeightedConvolutionDouble(ShapedVectorSpace objectSpace,
                        ShapedVectorSpace dataSpace)
    {
        /* Initialize super class and check types. */
        super(objectSpace, dataSpace);
        if (objectSpace.getType() != Traits.DOUBLE) {
            throw new IllegalArgumentException("Object space must be for double data type");
        }
        if (dataSpace.getType() != Traits.DOUBLE) {
            throw new IllegalArgumentException("Data space must be for double data type");
        }
    }

    @Override
    public void setWeightsAndData(ShapedVector weight, ShapedVector data)
    {
        if (data == null || ! data.belongsTo(dataSpace)) {
            throw new IllegalArgumentException("Data must belong to the data space.");
        }
        if (weight == null) {
            setWeightsAndData(((DoubleShapedVector)data).getData());
        } else if (weight.belongsTo(dataSpace)) {
            setWeightsAndData(((DoubleShapedVector)weight).getData(),
                              ((DoubleShapedVector)data).getData());
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
            setWeightsAndData(data.toDouble().flatten(false));
        } else if (dataSpace.getShape().equals(data.getShape())) {
            setWeightsAndData(weight.toDouble().flatten(false),
                              data.toDouble().flatten(false));
        } else {
            throw new IllegalArgumentException("Weights must have the same shape as the vectors of the data space.");
        }
    }

    private void setWeightsAndData(double data[])
    {
        wgt = null;
        dat = data;
    }

    private void setWeightsAndData(double weight[], double data[])
    {
        int n = weight.length;
        for (int k = 0; k < n; ++k) {
            double w = weight[k];
            if (Double.isNaN(w) || Double.isInfinite(w) || w < 0.0) {
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
