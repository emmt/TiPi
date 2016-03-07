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

import mitiv.linalg.Vector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.FloatShapedVectorSpace;


/**
 * Implementation of boxed set for vectors of single precision floating point
 * elements.
 *
 * @author Éric Thiébaut.
 */
public class FloatBoxedSet extends SimpleBoxedSet {
    //static final float zero = 0.0F;
    //static final float one  = 1.0F;
    //static final float inf  = Float.POSITIVE_INFINITY;

    private float[] lo = null;
    private float[] up = null;
    private int bound = 0;

    public FloatBoxedSet(FloatShapedVectorSpace space)
    {
        super(space);
    }

    private float[] getData(Vector vec)
    {
        space.check(vec);
        return ((FloatShapedVector)vec).getData();
    }

    @Override
    public void setLowerBound(double _val)
    {
        float val = (float)_val;
        if (val > Float.NEGATIVE_INFINITY) {
            lo = new float[]{val};
            bound = (bound/3)*3 + 1;
        } else {
            lo = null;
            bound = (bound/3)*3;
        }
    }

    @Override
    public void setLowerBound(Vector vec)
    {
        if (vec != null) {
            lo = getData(vec);
            bound = (bound/3)*3 + 2;
        } else {
            lo = null;
            bound = (bound/3)*3;
        }
    }

    @Override
    public void setUpperBound(double _val)
    {
        float val = (float)_val;
        if (val < Float.POSITIVE_INFINITY) {
            up = new float[]{val};
            bound = (bound%3) + 3;
        } else {
            up = null;
            bound = (bound%3);
        }
    }

    @Override
    public void setUpperBound(Vector vec)
    {
        if (vec != null) {
            up = getData(vec);
            bound = (bound%3) + 6;
        } else {
            up = null;
            bound = (bound%3);
        }
    }

    @Override
    public final void projectVariables(Vector dst, Vector vec)
    {
        float[] xp = getData(dst);
        float[] x = getData(vec);
        float a, b, t;
        int n = x.length;

        switch (bound) {
        case 0:
            if (xp != x) {
                System.arraycopy(xp, 0, x, 0, n);
            }
            break;
        case 1:
            a = lo[0];
            for (int i = 0; i < n; ++i) {
                t = x[i];
                if (t < a) t = a;
                xp[i] = t;
            }
            break;
        case 2:
            for (int i = 0; i < n; ++i) {
                a = lo[i];
                t = x[i];
                if (t < a) t = a;
                xp[i] = t;
            }
            break;
        case 3:
            b = up[0];
            for (int i = 0; i < n; ++i) {
                t = x[i];
                if (t > b) t = b;
                xp[i] = t;
            }
            break;
        case 4:
            a = lo[0];
            b = up[0];
            for (int i = 0; i < n; ++i) {
                t = x[i];
                if (t < a) t = a;
                if (t > b) t = b;
                xp[i] = t;
            }
            break;
        case 5:
            b = up[0];
            for (int i = 0; i < n; ++i) {
                a = lo[i];
                t = x[i];
                if (t < a) t = a;
                if (t > b) t = b;
                xp[i] = t;
            }
            break;
        case 6:
            for (int i = 0; i < n; ++i) {
                t = x[i];
                b = up[i];
                if (t > b) t = b;
                xp[i] = t;
            }
            break;
        case 7:
            a = lo[0];
            for (int i = 0; i < n; ++i) {
                t = x[i];
                b = up[i];
                if (t < a) t = a;
                if (t > b) t = b;
                xp[i] = t;
            }
            break;
        case 8:
            for (int i = 0; i < n; ++i) {
                a = lo[i];
                t = x[i];
                b = up[i];
                if (t < a) t = a;
                if (t > b) t = b;
                xp[i] = t;
            }
            break;
        }
    }

    @Override
    public void projectDirection(Vector dst, Vector vec,
            Vector dir, int orient)
    {
        float[] dp = getData(dst);
        float[] x = getData(vec);
        float[] d = getData(dir);
        float a, b;
        int n = x.length;

        switch (bound) {
        case 0:
            if (dp != d) {
                System.arraycopy(dp, 0, d, 0, n);
            }
            break;
        case 1:
            a = lo[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] >= 0 || x[i] > a ? d[i] : 0);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] <= 0 || x[i] > a ? d[i] : 0);
                }
            }
            break;
        case 2:
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] >= 0 || x[i] > lo[i] ? d[i] : 0);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] <= 0 || x[i] > lo[i] ? d[i] : 0);
                }
            }
            break;
        case 3:
            b = up[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] <= 0 || x[i] < b ? d[i] : 0);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] >= 0 || x[i] < b ? d[i] : 0);
                }
            }
            break;
        case 4:
            a = lo[0];
            b = up[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] < 0 ? (x[i] > a ? d[i] : 0) : (d[i] > 0 ? (x[i] < b ? d[i] : 0) : 0));
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] > 0 ? (x[i] > a ? d[i] : 0) : (d[i] < 0 ? (x[i] < b ? d[i] : 0) : 0));
                }
            }
            break;
        case 5:
            b = up[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] < 0 ? (x[i] > lo[i] ? d[i] : 0) : (d[i] > 0 ? (x[i] < b ? d[i] : 0) : 0));
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] > 0 ? (x[i] > lo[i] ? d[i] : 0) : (d[i] < 0 ? (x[i] < b ? d[i] : 0) : 0));
                }
            }
            break;
        case 6:
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] <= 0 || x[i] < up[i] ? d[i] : 0);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] >= 0 || x[i] < up[i] ? d[i] : 0);
                }
            }
            break;
        case 7:
            a = lo[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] < 0 ? (x[i] > a ? d[i] : 0) : (d[i] > 0 ? (x[i] < up[i] ? d[i] : 0) : 0));
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] > 0 ? (x[i] > a ? d[i] : 0) : (d[i] < 0 ? (x[i] < up[i] ? d[i] : 0) : 0));
                }
            }
            break;
        case 8:
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] < 0 ? (x[i] > lo[i] ? d[i] : 0) : (d[i] > 0 ? (x[i] < up[i] ? d[i] : 0) : 0));
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    dp[i] = (d[i] > 0 ? (x[i] > lo[i] ? d[i] : 0) : (d[i] < 0 ? (x[i] < up[i] ? d[i] : 0) : 0));
                }
            }
            break;
        }
    }

    @Override
    public void findFreeVariables(Vector dst, Vector vec,
            Vector dir, int orient)
    {
        float[] w = getData(dst);
        float[] x = getData(vec);
        float[] d = getData(dir);
        float a, b;
        int n = x.length;

        switch (bound) {
        case 0:
            for (int i = 0; i < n; ++i) {
                w[i] = 1;

            }
            break;
        case 1:
            a = lo[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] < 0 ? (x[i] > a ? 1 : 0) : (d[i] > 0 ? 1 : 0);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] > 0 ? (x[i] > a ? 1 : 0) : (d[i] < 0 ? 1 : 0);
                }
            }
            break;
        case 2:
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] < 0 ? (x[i] > lo[i] ? 1 : 0) : (d[i] > 0 ? 1 : 0);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] > 0 ? (x[i] > lo[i] ? 1 : 0) : (d[i] < 0 ? 1 : 0);
                }
            }
            break;
        case 3:
            b = up[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] < 0 ? 1 : (d[i] > 0 ? (x[i] < b ? 1 : 0) : 0);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] > 0 ? 1 : (d[i] < 0 ? (x[i] < b ? 1 : 0) : 0);
                }
            }
            break;
        case 4:
            a = lo[0];
            b = up[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] < 0 ? (x[i] > a ? 1 : 0) : (d[i] > 0 ? (x[i] < b ? 1 : 0) : 0);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] > 0 ? (x[i] > a ? 1 : 0) : (d[i] < 0 ? (x[i] < b ? 1 : 0) : 0);
                }
            }
            break;
        case 5:
            b = up[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] < 0 ? (x[i] > lo[i] ? 1 : 0) : (d[i] > 0 ? (x[i] < b ? 1 : 0) : 0);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] > 0 ? (x[i] > lo[i] ? 1 : 0) : (d[i] < 0 ? (x[i] < b ? 1 : 0) : 0);
                }
            }
            break;
        case 6:
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] < 0 ? 1 : (d[i] > 0 ? (x[i] < up[i] ? 1 : 0) : 0);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] > 0 ? 1 : (d[i] < 0 ? (x[i] < up[i] ? 1 : 0) : 0);
                }
            }
            break;
        case 7:
            a = lo[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] < 0 ? (x[i] > a ? 1 : 0) : (d[i] > 0 ? (x[i] < up[i] ? 1 : 0) : 0);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] > 0 ? (x[i] > a ? 1 : 0) : (d[i] < 0 ? (x[i] < up[i] ? 1 : 0) : 0);
                }
            }
            break;
        case 8:
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] < 0 ? (x[i] > lo[i] ? 1 : 0) : (d[i] > 0 ? (x[i] < up[i] ? 1 : 0) : 0);
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    w[i] = d[i] > 0 ? (x[i] > lo[i] ? 1 : 0) : (d[i] < 0 ? (x[i] < up[i] ? 1 : 0) : 0);
                }
            }
            break;
        }
    }

    @Override
    public double[] findStepLimits(Vector vec, Vector dir, int orient)
    {
        float[] x = getData(vec);
        float[] d = getData(dir);
        float a, b, p, s, s1, s2, s3;
        int n = x.length;

        s1 = s2 = s3 = Float.POSITIVE_INFINITY;
        switch (bound) {
        case 1:
            s3 = 0;
            a = lo[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p < 0) {
                        s = (a - x[i])/p;
                        if (s < s1) s1 = s;
                        if (s < s2 && s > 0) s2 = s;
                        if (s > s3) s3 = s;
                    } else if (p > 0) {
                        s3 = Float.POSITIVE_INFINITY;
                    }
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p > 0) {
                        s = (x[i] - a)/p;
                        if (s < s1) s1 = s;
                        if (s < s2 && s > 0) s2 = s;
                        if (s > s3) s3 = s;
                    } else if (p < 0) {
                        s3 = Float.POSITIVE_INFINITY;
                    }
                }
            }
            break;
        case 2:
            s3 = 0;
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p < 0) {
                        s = (lo[i] - x[i])/p;
                        if (s < s1) s1 = s;
                        if (s < s2 && s > 0) s2 = s;
                        if (s > s3) s3 = s;
                    } else if (p > 0) {
                        s3 = Float.POSITIVE_INFINITY;
                    }
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p > 0) {
                        s = (x[i] - lo[i])/p;
                        if (s < s1) s1 = s;
                        if (s < s2 && s > 0) s2 = s;
                        if (s > s3) s3 = s;
                    } else if (p < 0) {
                        s3 = Float.POSITIVE_INFINITY;
                    }
                }
            }
            break;
        case 3:
            s3 = 0;
            b = up[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p > 0) {
                        s = (b - x[i])/p;
                        if (s < s1) s1 = s;
                        if (s < s2 && s > 0) s2 = s;
                        if (s > s3) s3 = s;
                    } else if (p < 0) {
                        s3 = Float.POSITIVE_INFINITY;
                    }
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p < 0) {
                        s = (x[i] - b)/p;
                        if (s < s1) s1 = s;
                        if (s < s2 && s > 0) s2 = s;
                        if (s > s3) s3 = s;
                    } else if (p > 0) {
                        s3 = Float.POSITIVE_INFINITY;
                    }
                }
            }
            break;
        case 4:
            s3 = 0;
            a = lo[0];
            b = up[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p > 0) {
                        s = (b - x[i])/p;
                    } else if (p < 0) {
                        s = (a - x[i])/p;
                    } else {
                        continue;
                    }
                    if (s < s1) s1 = s;
                    if (s < s2 && s > 0) s2 = s;
                    if (s > s3) s3 = s;
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p < 0) {
                        s = (x[i] - b)/p;
                    } else if (p > 0) {
                        s = (x[i] - a)/p;
                    } else {
                        continue;
                    }
                    if (s < s1) s1 = s;
                    if (s < s2 && s > 0) s2 = s;
                    if (s > s3) s3 = s;
                }
            }
            break;
        case 5:
            s3 = 0;
            b = up[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p > 0) {
                        s = (b - x[i])/p;
                    } else if (p < 0) {
                        s = (lo[i] - x[i])/p;
                    } else {
                        continue;
                    }
                    if (s < s1) s1 = s;
                    if (s < s2 && s > 0) s2 = s;
                    if (s > s3) s3 = s;
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p < 0) {
                        s = (x[i] - b)/p;
                    } else if (p > 0) {
                        s = (x[i] - lo[i])/p;
                    } else {
                        continue;
                    }
                    if (s < s1) s1 = s;
                    if (s < s2 && s > 0) s2 = s;
                    if (s > s3) s3 = s;
                }
            }
            break;
        case 6:
            s3 = 0;
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p > 0) {
                        s = (up[i] - x[i])/p;
                        if (s < s1) s1 = s;
                        if (s < s2 && s > 0) s2 = s;
                        if (s > s3) s3 = s;
                    } else if (p < 0) {
                        s3 = Float.POSITIVE_INFINITY;
                    }
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p < 0) {
                        s = (x[i] - up[i])/p;
                        if (s < s1) s1 = s;
                        if (s < s2 && s > 0) s2 = s;
                        if (s > s3) s3 = s;
                    } else if (p > 0) {
                        s3 = Float.POSITIVE_INFINITY;
                    }
                }
            }
            break;
        case 7:
            s3 = 0;
            a = lo[0];
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p > 0) {
                        s = (up[i] - x[i])/p;
                    } else if (p < 0) {
                        s = (a - x[i])/p;
                    } else {
                        continue;
                    }
                    if (s < s1) s1 = s;
                    if (s < s2 && s > 0) s2 = s;
                    if (s > s3) s3 = s;
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p < 0) {
                        s = (x[i] - up[i])/p;
                    } else if (p > 0) {
                        s = (x[i] - a)/p;
                    } else {
                        continue;
                    }
                    if (s < s1) s1 = s;
                    if (s < s2 && s > 0) s2 = s;
                    if (s > s3) s3 = s;
                }
            }
            break;
        case 8:
            s3 = 0;
            if (orient > 0) {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p > 0) {
                        s = (up[i] - x[i])/p;
                    } else if (p < 0) {
                        s = (lo[i] - x[i])/p;
                    } else {
                        continue;
                    }
                    if (s < s1) s1 = s;
                    if (s < s2 && s > 0) s2 = s;
                    if (s > s3) s3 = s;
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    p = d[i];
                    if (p < 0) {
                        s = (x[i] - up[i])/p;
                    } else if (p > 0) {
                        s = (x[i] - lo[i])/p;
                    } else {
                        continue;
                    }
                    if (s < s1) s1 = s;
                    if (s < s2 && s > 0) s2 = s;
                    if (s > s3) s3 = s;
                }
            }
            break;
        }

        return new double[]{s1, s2, s3};
    }

}
