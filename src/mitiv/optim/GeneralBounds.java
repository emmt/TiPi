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

public class GeneralBounds extends BoundProjector {

    /** The lower bound. */
    private final Vector lowerBound;

    /** The upper bound. */
    private final Vector upperBound;

    /** Use single precision? */
    private final boolean single;

    /**
     * Create a projector with scalar upper bound.
     * @param vsp        - The input and output vector space for the variables.
     * @param upperBound - The values of the upper bounds (some can be NEGATIVE_INFINITY).
     * @param lowerBound - The values of the lower bounds (some can be POSITIVE_INFINITY).
     */
    public GeneralBounds(ShapedVectorSpace vsp, Vector lowerBound, Vector upperBound) {
        super(vsp);
        if (vsp.getType() == Traits.DOUBLE) {
            single = false;
        } else if (vsp.getType() == Traits.FLOAT) {
            single = true;
        } else {
            throw new IllegalArgumentException("Only double/double type supported");
        }
        if (! lowerBound.belongsTo(vsp)) {
            throw new IllegalArgumentException("Lower bound does not belong to the same vector space as the variables");
        }
        if (! upperBound.belongsTo(vsp)) {
            throw new IllegalArgumentException("Upper bound does not belong to the same vector space as the variables");
        }
        int n = vsp.getNumber();
        if (single) {
            float[] xmin = ((FloatShapedVector)lowerBound).getData();
            float[] xmax = ((FloatShapedVector)upperBound).getData();
            for (int j = 0; j < n; ++j) {
                if (Float.isNaN(xmin[j]) || (Float.isInfinite(xmin[j]) && xmin[j] != Float.NEGATIVE_INFINITY)) {
                    throw new IllegalArgumentException("Invalid lower bound value(s)");
                }
                if (Float.isNaN(xmax[j]) || (Float.isInfinite(xmax[j]) && xmax[j] != Float.POSITIVE_INFINITY)) {
                    throw new IllegalArgumentException("Invalid upper bound value(s)");
                }
                if (xmin[j] > xmax[j]) {
                    throw new IllegalArgumentException("Lower bound must be less or equal upper bound");
                }
            }
        } else {
            double[] xmin = ((DoubleShapedVector)lowerBound).getData();
            double[] xmax = ((DoubleShapedVector)upperBound).getData();
            for (int j = 0; j < n; ++j) {
                if (Double.isNaN(xmin[j]) || (Double.isInfinite(xmin[j]) && xmin[j] != Double.NEGATIVE_INFINITY)) {
                    throw new IllegalArgumentException("Invalid lower bound value(s)");
                }
                if (Double.isNaN(xmax[j]) || (Double.isInfinite(xmax[j]) && xmax[j] != Double.POSITIVE_INFINITY)) {
                    throw new IllegalArgumentException("Invalid upper bound value(s)");
                }
                if (xmin[j] > xmax[j]) {
                    throw new IllegalArgumentException("Lower bound must be less or equal upper bound");
                }
            }
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    protected void _projectVariables(Vector dst, Vector src) {
        final int n = src.getNumber();
        if (single) {
            float[] xmin = ((FloatShapedVector)lowerBound).getData();
            float[] xmax = ((FloatShapedVector)upperBound).getData();
            float[] x    = ((FloatShapedVector)src).getData();
            float[] xp   = ((FloatShapedVector)dst).getData();
            for (int j = 0; j < n; ++j) {
                xp[j] = clamp(x[j], xmin[j], xmax[j]);
            }
        } else {
            double[] xmin = ((DoubleShapedVector)lowerBound).getData();
            double[] xmax = ((DoubleShapedVector)upperBound).getData();
            double[] x    = ((DoubleShapedVector)src).getData();
            double[] xp   = ((DoubleShapedVector)dst).getData();
            for (int j = 0; j < n; ++j) {
                xp[j] = clamp(x[j], xmin[j], xmax[j]);
            }
        }
    }

    @Override
    protected void _projectDirection(Vector vx, Vector vd, boolean ascent,
            Vector vdp, double[] bnd) {
        final int n = vx.getNumber();
        if (single) {
            final float zero = 0;
            float[] xmin = ((FloatShapedVector)lowerBound).getData();
            float[] xmax = ((FloatShapedVector)upperBound).getData();
            float[] x    = ((FloatShapedVector)vx).getData();
            float[] d    = ((FloatShapedVector)vd).getData();
            float[] dp   = ((FloatShapedVector)vdp).getData();
            if (bnd == null) {
                /* Step length bounds not required. */
                if (ascent) {
                    /* Ascent direction. */
                    for (int j = 0; j < n; ++j) {
                        if (d[j] < zero) {
                            /* Variable will increase. */
                            dp[j] = (x[j] < xmax[j] ? d[j] : zero);
                        } else if (d[j] > zero) {
                            /* Variable will decrease. */
                            dp[j] = (x[j] > xmin[j] ? d[j] : zero);
                        } else {
                            /* Variable will not change. */
                            dp[j] = zero;
                        }
                    }
                } else {
                    /* Descent direction. */
                    for (int j = 0; j < n; ++j) {
                        if (d[j] > zero) {
                            /* Variable will increase. */
                            dp[j] = (x[j] < xmax[j] ? d[j] : zero);
                        } else if (d[j] < zero) {
                            /* Variable will decrease. */
                            dp[j] = (x[j] > xmin[j] ? d[j] : zero);
                        } else {
                            /* Variable will not change. */
                            dp[j] = zero;
                        }
                    }
                }
            } else {
                /* Compute step length bounds. */
                float tmp, amin = Float.POSITIVE_INFINITY, amax = zero;
                if (ascent) {
                    /* Ascent direction. */
                    for (int j = 0; j < n; ++j) {
                        if (d[j] < zero) {
                            /* Variable will increase. */
                            if (x[j] < xmax[j]) {
                                /* Variable is unbinded. */
                                dp[j] = d[j];
                                if (xmax[j] == Float.POSITIVE_INFINITY) {
                                    amax = Float.POSITIVE_INFINITY;
                                } else {
                                    tmp = (x[j] - xmax[j])/d[j];
                                    if (tmp < amin) {
                                        amin = tmp;
                                    }
                                    if (tmp > amax) {
                                        amax = tmp;
                                    }
                                }
                            } else {
                                /* Variable is binded. */
                                dp[j] = zero;
                            }
                        } else if (d[j] > zero) {
                            /* Variable will decrease. */
                            if (x[j] > xmin[j]) {
                                /* Variable is unbinded. */
                                dp[j] = d[j];
                                if (xmin[j] == Float.NEGATIVE_INFINITY) {
                                    amax = Float.POSITIVE_INFINITY;
                                } else {
                                    tmp = (x[j] - xmin[j])/d[j];
                                    if (tmp < amin) {
                                        amin = tmp;
                                    }
                                    if (tmp > amax) {
                                        amax = tmp;
                                    }
                                }
                            } else {
                                /* Variable is binded. */
                                dp[j] = zero;
                            }
                        } else {
                            /* Variable will not change. */
                            dp[j] = zero;
                        }
                    }
                } else {
                    /* Descent direction. */
                    for (int j = 0; j < n; ++j) {
                        if (d[j] > zero) {
                            /* Variable will increase. */
                            if (x[j] < xmax[j]) {
                                /* Variable is unbinded. */
                                dp[j] = d[j];
                                if (xmax[j] == Float.POSITIVE_INFINITY) {
                                    amax = Float.POSITIVE_INFINITY;
                                } else {
                                    tmp = (xmax[j] - x[j])/d[j];
                                    if (tmp < amin) {
                                        amin = tmp;
                                    }
                                    if (tmp > amax) {
                                        amax = tmp;
                                    }
                                }
                            } else {
                                /* Variable is binded. */
                                dp[j] = zero;
                            }
                        } else if (d[j] < zero) {
                            /* Variable will decrease. */
                            if (x[j] > xmin[j]) {
                                /* Variable is unbinded. */
                                dp[j] = d[j];
                                if (xmin[j] == Float.NEGATIVE_INFINITY) {
                                    amax = Float.POSITIVE_INFINITY;
                                } else {
                                    tmp = (xmin[j] - x[j])/d[j];
                                    if (tmp < amin) {
                                        amin = tmp;
                                    }
                                    if (tmp > amax) {
                                        amax = tmp;
                                    }
                                }
                            } else {
                                /* Variable is binded. */
                                dp[j] = zero;
                            }
                        } else {
                            /* Variable will not change. */
                            dp[j] = zero;
                        }
                    }
                }
                bnd[0] = amin;
                bnd[1] = amax;
            }

        } else {
            final double zero = 0;
            double[] xmin = ((DoubleShapedVector)lowerBound).getData();
            double[] xmax = ((DoubleShapedVector)upperBound).getData();
            double[] x    = ((DoubleShapedVector)vx).getData();
            double[] d    = ((DoubleShapedVector)vd).getData();
            double[] dp   = ((DoubleShapedVector)vdp).getData();
            if (bnd == null) {
                /* Step length bounds not required. */
                if (ascent) {
                    /* Ascent direction. */
                    for (int j = 0; j < n; ++j) {
                        if (d[j] < zero) {
                            /* Variable will increase. */
                            dp[j] = (x[j] < xmax[j] ? d[j] : zero);
                        } else if (d[j] > zero) {
                            /* Variable will decrease. */
                            dp[j] = (x[j] > xmin[j] ? d[j] : zero);
                        } else {
                            /* Variable will not change. */
                            dp[j] = zero;
                        }
                    }
                } else {
                    /* Descent direction. */
                    for (int j = 0; j < n; ++j) {
                        if (d[j] > zero) {
                            /* Variable will increase. */
                            dp[j] = (x[j] < xmax[j] ? d[j] : zero);
                        } else if (d[j] < zero) {
                            /* Variable will decrease. */
                            dp[j] = (x[j] > xmin[j] ? d[j] : zero);
                        } else {
                            /* Variable will not change. */
                            dp[j] = zero;
                        }
                    }
                }
            } else {
                /* Compute step length bounds. */
                double tmp, amin = Double.POSITIVE_INFINITY, amax = zero;
                if (ascent) {
                    /* Ascent direction. */
                    for (int j = 0; j < n; ++j) {
                        if (d[j] < zero) {
                            /* Variable will increase. */
                            if (x[j] < xmax[j]) {
                                /* Variable is unbinded. */
                                dp[j] = d[j];
                                if (xmax[j] == Double.POSITIVE_INFINITY) {
                                    amax = Double.POSITIVE_INFINITY;
                                } else {
                                    tmp = (x[j] - xmax[j])/d[j];
                                    if (tmp < amin) {
                                        amin = tmp;
                                    }
                                    if (tmp > amax) {
                                        amax = tmp;
                                    }
                                }
                            } else {
                                /* Variable is binded. */
                                dp[j] = zero;
                            }
                        } else if (d[j] > zero) {
                            /* Variable will decrease. */
                            if (x[j] > xmin[j]) {
                                /* Variable is unbinded. */
                                dp[j] = d[j];
                                if (xmin[j] == Double.NEGATIVE_INFINITY) {
                                    amax = Double.POSITIVE_INFINITY;
                                } else {
                                    tmp = (x[j] - xmin[j])/d[j];
                                    if (tmp < amin) {
                                        amin = tmp;
                                    }
                                    if (tmp > amax) {
                                        amax = tmp;
                                    }
                                }
                            } else {
                                /* Variable is binded. */
                                dp[j] = zero;
                            }
                        } else {
                            /* Variable will not change. */
                            dp[j] = zero;
                        }
                    }
                } else {
                    /* Descent direction. */
                    for (int j = 0; j < n; ++j) {
                        if (d[j] > zero) {
                            /* Variable will increase. */
                            if (x[j] < xmax[j]) {
                                /* Variable is unbinded. */
                                dp[j] = d[j];
                                if (xmax[j] == Double.POSITIVE_INFINITY) {
                                    amax = Double.POSITIVE_INFINITY;
                                } else {
                                    tmp = (xmax[j] - x[j])/d[j];
                                    if (tmp < amin) {
                                        amin = tmp;
                                    }
                                    if (tmp > amax) {
                                        amax = tmp;
                                    }
                                }
                            } else {
                                /* Variable is binded. */
                                dp[j] = zero;
                            }
                        } else if (d[j] < zero) {
                            /* Variable will decrease. */
                            if (x[j] > xmin[j]) {
                                /* Variable is unbinded. */
                                dp[j] = d[j];
                                if (xmin[j] == Double.NEGATIVE_INFINITY) {
                                    amax = Double.POSITIVE_INFINITY;
                                } else {
                                    tmp = (xmin[j] - x[j])/d[j];
                                    if (tmp < amin) {
                                        amin = tmp;
                                    }
                                    if (tmp > amax) {
                                        amax = tmp;
                                    }
                                }
                            } else {
                                /* Variable is binded. */
                                dp[j] = zero;
                            }
                        } else {
                            /* Variable will not change. */
                            dp[j] = zero;
                        }
                    }
                }
                bnd[0] = amin;
                bnd[1] = amax;
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
