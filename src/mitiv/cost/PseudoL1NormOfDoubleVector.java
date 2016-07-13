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
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;

public class PseudoL1NormOfDoubleVector extends PseudoL1Norm {
    public PseudoL1NormOfDoubleVector(DoubleShapedVectorSpace space) {
        super(space);
    }

    /* (non-Javadoc)
     * @see mitiv.cost.ProximalOperator#applyProx(double, mitiv.linalg.Vector, mitiv.linalg.Vector, double)
     */
    @Override
    public void applyProx(Vector out, double alpha, Vector inp, double tol) {
        if (alpha == 0.0) {
            out.copyFrom(inp);
        } else if (alpha > 0.0) {
            if (! inp.belongsTo(inputSpace) || ! out.belongsTo(inputSpace)) {
                throw new IncorrectSpaceException();
            }
            double[] x = ((DoubleShapedVector)inp).getData();
            double[] y = ((DoubleShapedVector)inp).getData();
            double tmin = -alpha;
            double tmax = +alpha;
            int n = x.length;
            for (int j = 0; j < n; ++j) {
                double t = x[j];
                if (t <= tmin) {
                    y[j] = t - tmin;
                } else if (t >= tmax) {
                    y[j] = t - tmax;
                } else {
                    y[j] = 0.0;
                }
            }
        }
    }
}
