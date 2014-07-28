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

/**
 * Implement proximal operator for pseudo L1 norm, possibly with bounds.
 * 
 * <p>
 * The proximal operator of the pseudo L1 norm is a soft thresholding.
 *
 * @author eric
 *
 */
public class PseudoL1NormOfBoundedDoubleVector extends PseudoL1NormOfDoubleVector {
    public static final int SCALAR_MIN = 1;
    public static final int VECTOR_MIN = 2;
    public static final int SCALAR_MAX = 4;
    public static final int VECTOR_MAX = 8;
    protected double xmin = Double.NEGATIVE_INFINITY;
    protected double xmax = Double.POSITIVE_INFINITY;
    protected double xmid = 0.0;
    protected int typeOfBounds = 0;

    public PseudoL1NormOfBoundedDoubleVector(DoubleShapedVectorSpace inputSpace) {
        super(inputSpace);
    }

    public PseudoL1NormOfBoundedDoubleVector(DoubleShapedVectorSpace inputSpace, double xmin, double xmax) {
        super(inputSpace);
        setBounds(xmin, xmax);
    }

    public void setBounds(double xmin, double xmax) {
        if (Double.isNaN(xmax) || Double.isNaN(xmin)) {
            throw new IllegalArgumentException("Bounds must be regular values or +/-infinity.");
        }
        if (xmin > xmax) {
            throw new IllegalArgumentException("Empty feasible set.");
        }
        typeOfBounds = 0;
        xmid = 0.0;
        if (Double.isInfinite(xmin)) {
            this.xmin = Double.NEGATIVE_INFINITY;
        } else {
            this.xmin = xmin;
            xmid = Math.max(xmid, xmin);
            typeOfBounds |= SCALAR_MIN;
        }
        if (Double.isInfinite(xmax)) {
            this.xmax = Double.POSITIVE_INFINITY;
        } else {
            this.xmax = xmax;
            xmid = Math.min(xmid, xmax);
            typeOfBounds |= SCALAR_MAX;
        }
    }

    private final double minCut(double x) {
        return Math.max(x, xmin);
    }
    private final double maxCut(double x) {
        return Math.min(x, xmax);
    }
    private final double cut(double x) {
        return Math.min(Math.max(x, xmin), xmax);
    }

    /* (non-Javadoc)
     * @see mitiv.cost.ProximalOperator#applyProx(double, mitiv.linalg.Vector, mitiv.linalg.Vector, double)
     */
    @Override
    public void applyProx(double alpha, Vector inp, Vector out, double tol) {
        if (alpha == 0.0) {
            inputSpace.copy(inp, out);
        } else if (alpha > 0.0) {
            if (! inp.belongsTo(inputSpace) || ! out.belongsTo(inputSpace)) {
                throw new IncorrectSpaceException();
            }
            double[] x = ((DoubleShapedVector)inp).getData();
            double[] y = ((DoubleShapedVector)inp).getData();
            double tmin = -alpha;
            double tmax = +alpha;
            int n = x.length;
            switch (typeOfBounds) {

            case 0:
                for (int j = 0; j < n; ++j) {
                    double t = x[j];
                    if (t <= tmin) {
                        y[j] = t - tmin;
                    } else if (t >= tmax) {
                        y[j] = t - tmax;
                    } else {
                        y[j] = xmid;
                    }
                }
                break;

            case SCALAR_MIN:
                if (xmin >= 0.0) {
                    for (int j = 0; j < n; ++j) {
                        y[j] = minCut(x[j] - alpha);
                    }
                } else {
                    for (int j = 0; j < n; ++j) {
                        double t = x[j];
                        if (t <= tmin) {
                            y[j] = minCut(t - tmin);
                        } else if (t >= tmax) {
                            y[j] = minCut(t - tmax);
                        } else {
                            y[j] = xmid;
                        }
                    }
                }
                break;

            case SCALAR_MAX:
                if (xmax <= 0.0) {
                    for (int j = 0; j < n; ++j) {
                        y[j] = maxCut(x[j] + alpha);
                    }
                } else {
                    for (int j = 0; j < n; ++j) {
                        double t = x[j];
                        if (t <= tmin) {
                            y[j] = maxCut(t - tmin);
                        } else if (t >= tmax) {
                            y[j] = maxCut(t - tmax);
                        } else {
                            y[j] = xmid;
                        }
                    }
                }
                break;

            case (SCALAR_MIN|SCALAR_MAX):
                for (int j = 0; j < n; ++j) {
                    double t = x[j];
                    if (t <= tmin) {
                        y[j] = cut(t - tmin);
                    } else if (t >= tmax) {
                        y[j] = cut(t - tmax);
                    } else {
                        y[j] = xmid;
                    }
                }
            break;

            } /* end of switch statement */
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
