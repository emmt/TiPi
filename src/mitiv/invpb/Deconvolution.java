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
package mitiv.invpb;

import mitiv.array.ArrayFactory;
import mitiv.array.ShapedArray;
import mitiv.base.Traits;
import mitiv.conv.WeightedConvolutionCost;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;
import mitiv.optim.OptimTask;

/**
 * @author ferreol
 *
 */
public class Deconvolution extends SmoothInverseProblem {

    /** Indicate whether internal parameters should be recomputed. */
    protected boolean updatePending = true;

    /** The current solution. */
    protected Vector x = null;

    /** The result.  If non-null at the start, it is assumed to be the starting solution. */
    protected ShapedArray object = null;



    protected void forceRestart() {
        updatePending = true;
    }

    public OptimTask start() {
        return start(false);
    }

    public OptimTask start(boolean reset) {
        /* Make sure everything is correctly initialized. */
        if (updatePending) {
            update();
        }
        return super.start(x, reset);
    }

    public OptimTask iterate() {
        if (updatePending) {
            return start();
        } else {
            return super.iterate(x);
        }
    }

    protected static void error(String reason) {
        throw new IllegalArgumentException(reason);
    }


    public void setInitialSolution(ShapedArray arr) {
        if (object != arr) {
            object = arr;
            forceRestart();
            resetIteration();
        }
    }

    @Override
    public ShapedVector getBestSolution() {
        return (ShapedVector)super.getBestSolution();
    }

    protected void update() {
        /* Make sure the vector of variables share its contents with the
        object shaped array. */
        ShapedVectorSpace objectSpace = ((ShapedVectorSpace) super.getLikelihood().getInputSpace());
        int type = objectSpace.getType();
        boolean wrap = (object.getType() != type || ! object.isFlat());
        x = objectSpace.create(object, false);
        if (wrap) {
            if (type == Traits.FLOAT) {
                object = ArrayFactory.wrap(((FloatShapedVector)x).getData(), objectSpace.getShape());
            } else {
                object = ArrayFactory.wrap(((DoubleShapedVector)x).getData(), objectSpace.getShape());
            }
        }
        updatePending = false;

    }

    public ShapedArray getSolution() {
        return object;
    }

    public ShapedArray getModel() {
        return ((WeightedConvolutionCost) getLikelihood()).getModel( getBestSolution().asShapedArray()).asShapedArray();
    }


}
