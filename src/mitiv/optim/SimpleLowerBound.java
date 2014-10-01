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

package mitiv.optim;

import mitiv.base.Traits;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Implements a simple lower bound projector.
 * 
 * The bound is a scalar, it has the same value for all the variables.
 * 
 * @author Éric Thiébaut.
 */
public class SimpleLowerBound extends BoundProjector {


    /** The lower bound. */
    private final double lowerBound;

    /** Use single precision? */
    private final boolean single;

    /**
     * Create a projector with scalar lower bound.
     * @param vsp _ The input and output vector space for the variables.
     * @param lowerBound - The value of the lower bound.
     */
    public SimpleLowerBound(ShapedVectorSpace vsp, double lowerBound) {
        super(vsp);
        this.lowerBound = lowerBound;
        if (vsp.getType() == Traits.DOUBLE) {
            single = false;
        } else if (vsp.getType() == Traits.FLOAT) {
            single = true;
        } else {
            throw new IllegalArgumentException("Only double/double type supported");
        }
    }

    @Override
    protected void _projectGradient(Vector vecX, Vector vecG, Vector vecGP) {
        final int n = vecX.getNumber();
        if (single) {
            final float lowerBound = (float)this.lowerBound;
            final float zero = 0.0F;
            float[] x = ((FloatShapedVector)vecX).getData();
            float[] g = ((FloatShapedVector)vecG).getData();
            float[] gp = ((FloatShapedVector)vecGP).getData();
            for (int j = 0; j < n; ++j) {
                if (x[j] > lowerBound || g[j] < zero) {
                    gp[j] = g[j];
                } else {
                    gp[j] = zero;
                }
            }
        } else {
            final double zero = 0.0;
            double[] x = ((DoubleShapedVector)vecG).getData();
            double[] g = ((DoubleShapedVector)vecG).getData();
            double[] gp = ((DoubleShapedVector)vecGP).getData();
            for (int j = 0; j < n; ++j) {
                if (x[j] > lowerBound || g[j] < zero) {
                    gp[j] = g[j];
                } else {
                    gp[j] = zero;
                }
            }
        }
    }

    @Override
    protected void _apply(Vector src, Vector dst) {
        final int n = src.getNumber();
        if (single) {
            final float lowerBound = (float)this.lowerBound;
            float[] inp = ((FloatShapedVector)src).getData();
            float[] out = ((FloatShapedVector)dst).getData();
            for (int j = 0; j < n; ++j) {
                out[j] = max(lowerBound, inp[j]);
            }
        } else {
            double[] inp = ((DoubleShapedVector)src).getData();
            double[] out = ((DoubleShapedVector)dst).getData();
            for (int j = 0; j < n; ++j) {
                out[j] = max(lowerBound, inp[j]);
            }
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
