// *WARNING* This file has been automatically generated by TPP do not edit directly.
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

import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Homogeneous edge preserving regularization.
 *
 * Homogeneous edge preserving regularization is the homogenous version of the hyperbolic approximation of the total variation.
 *
 * @author Ferréol Soulez
 */
public class HomogeneousHyperbolicTotalVariation implements DifferentiableCostFunction, HomogeneousFunction {

    static boolean debug =false;
    /** The vector space for the variables. */
    protected ShapedVectorSpace inputSpace;

    /** The number of dimensions. */
    protected int rank;

    /** The data type. */
    protected int type;

    /** The dimensions of the variables. */
    protected Shape shape;

    /** The threshold */
    protected double epsilon;

    /** The scaling of the finite differences along each dimension. */
    protected double[] scale = null;

    /** Array temporary recording the scale when the input space is not declared  */
    protected double[] delta = null;

    /**
     * @param inputSpace
     * @param epsilon
     */
    public HomogeneousHyperbolicTotalVariation(ShapedVectorSpace inputSpace,
            double epsilon) {
        if (debug) {
            System.out.println("HomogeneousHyperbolicTotalVariation");
        }

        this.inputSpace = inputSpace;
        shape = inputSpace.getShape();
        rank = (shape == null ? 0 : shape.rank());
        type = inputSpace.getType();
        setThreshold(epsilon);
        scale = new double[rank];
        defaultScale();
    }

    /**
     * @param inputSpace
     * @param epsilon
     * @param delta
     */
    public HomogeneousHyperbolicTotalVariation(ShapedVectorSpace inputSpace,
            double epsilon, double[] delta) {
        this(inputSpace, epsilon);
        setScale(delta);
    }

    /**
     * @param epsilon
     * @param delta
     */
    public HomogeneousHyperbolicTotalVariation(double epsilon, double[] delta) {
        setThreshold(epsilon);
        setDelta(delta);
    }

    /**
     * @param delta
     */
    public HomogeneousHyperbolicTotalVariation(double[] delta) {
        setDelta(delta);
    }

    /**
     * @param epsilon
     */
    public HomogeneousHyperbolicTotalVariation(double epsilon) {
        setThreshold(epsilon);
    }

    /**
     * @param epsilon
     */
    public void setThreshold(double epsilon) {
        if (notFinite(epsilon) || epsilon <= 0.0) {
            throw new IllegalArgumentException("Bad threshold value");
        }
        this.epsilon = epsilon;
    }

    /**
     * @return the threshold
     */
    public double getThreshold() {
        return epsilon;
    }

    /**
     *
     */
    public void defaultScale() {
        if (delta==null) {
            setScale(1.0);
        }else {
            setScale(delta );
        }
    }


    /**
     * @param delta
     */
    private void setDelta(double value[]) {
        if (value == null) {
            throw new IllegalArgumentException("Bad delta value");
        }
        for (int k = 0; k < delta.length; ++k) {
            if (notFinite(value[k]) || value[k] <= 0.0) {
                throw new IllegalArgumentException("Bad delta value");
            }
        }
        this.delta = value.clone();
    }

    /**
     * @param value
     */
    public void setScale(double value) {
        if (notFinite(value) || value <= 0.0) {
            throw new IllegalArgumentException("Bad delta value");
        }
        for (int k = 0; k < rank; ++k) {
            this.scale[k] = value;
        }
        delta = null;
    }

    /**
     * @param scale
     */
    public void setScale(double[] scale) {

        if (scale == null) {
            throw new IllegalArgumentException("Bad scale size");
        }
        if (scale.length == 1) {
            setScale(scale[1]);
        }else if( scale.length != rank) {
            throw new IllegalArgumentException("Bad scale size");
        }else {
            for (int k = 0; k < rank; ++k) {
                if (notFinite(scale[k]) || scale[k] <= 0.0) {
                    throw new IllegalArgumentException("Bad scale value");
                }
            }
            for (int k = 0; k < rank; ++k) {
                this.scale[k] = scale[k];
            }
        }
        delta = null;
    }
    /**
     * @param k
     * @return the scale
     */
    public double getScale(int k) {
        return (k >= 0 && k < rank ? scale[k] : 1.0);
    }

    @Override
    public VectorSpace getInputSpace() {
        return inputSpace;
    }


    /**
     * @param inputSpace
     */
    final public void setInputSpace(ShapedVectorSpace inputSpace) {
        this.inputSpace = inputSpace;
        shape = inputSpace.getShape();
        rank = (shape == null ? 0 : shape.rank());
        type = inputSpace.getType();
        scale = new double[rank];
        defaultScale();
    }


    @Override
    public double
    computeCostAndGradient(double alpha, Vector vx, Vector vgx, boolean clr)
    {
        if (vgx != null && clr) {
            vgx.fill(0.0);
        }
        if (type == Traits.FLOAT) {
            float[] x = ((FloatShapedVector)vx).getData();
            float[] gx = null;
            if (vgx != null) {
                gx = ((FloatShapedVector)vgx).getData();
            }
            if (rank == 1) {
                return computeFloat1D(alpha, x, gx);
            } else if (rank == 2) {
                return computeFloat2D(alpha, x, gx);
            } else if (rank == 3) {
                return computeFloat3D(alpha, x, gx);
            } else {
                badRank();
            }
        } else if (type == Traits.DOUBLE) {
            double[] x = ((DoubleShapedVector)vx).getData();
            double[] gx = null;
            if (vgx != null) {
                gx = ((DoubleShapedVector)vgx).getData();
            }
            if (rank == 1) {
                return computeDouble1D(alpha, x, gx);
            } else if (rank == 2) {
                return computeDouble2D(alpha, x, gx);
            } else if (rank == 3) {
                return computeDouble3D(alpha, x, gx);
            } else {
                badRank();
            }
        } else {
            badType();
        }
        return 0.0;
    }

    protected static void badRank() {
        throw new IllegalArgumentException("Unsupported number of dimensions for Total Variation");
    }

    protected static void badType() {
        throw new IllegalArgumentException("Unsupported data type for Total Variation");
    }

    /*
     *
     *
     * rgl(x) = sum_n r_n(x)
     *
     *  with:
     *
     *  r_n(x) = τ⋅sqrt(‖D_n⋅x‖² + τ²⋅‖x‖²) - τ²⋅‖x‖
     *
     *  where `τ > 0` is the value of the keyword `tau` divided by the square root
     *  of the number of elements in `x`.
     */

    private final double computeFloat1D(double alpha, float[] x,
            float[] gx)
    {
        /* Note that ALPHA is not taken into account when summing FCOST (this
         * is done at the end) while ALPHA is taken into account when
         * integrating the gradient GCOST.  The sum is done in double
         * precision whatever the type. */
        final boolean computeGradient = (gx != null);
        final int dim1 = shape.dimension(0);
        final float xnorm =  mitiv.linalg.ArrayOps.norm2(x);
        final float tau = (float)(epsilon * scale[0] / Math.sqrt(shape.number()));
        final float t = tau*xnorm;
        final float t2 = square(t);
        double fcost = 0.0;
        float beta = (float)(alpha/scale[0]);
        float sq = 0.0f;
        for (int i1 = 1; i1 < dim1; ++i1) {
            float d = x[i1] - x[i1 - 1];
            float r =  sqrt(d*d + t2);
            fcost  += tau * (r - t);
            if (computeGradient) {
                float q = tau/r;
                sq += q;
                float p = beta*q*d;
                gx[i1 - 1] -= p;
                gx[i1] += p;
            }
        }
        sq = (sq - 1.0f/xnorm)* beta*square(tau);
        if (computeGradient) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                gx[i1] += sq * x[i1] ;
            }
        }


        /* Remove the "bias" and make sure the result is non-negative (it
           can only be negative due to rounding errors). */
        fcost = (fcost/scale[0]) ;
        return (fcost > 0.0 ? alpha*fcost : 0.0);
    }
    private final double computeDouble1D(double alpha, double[] x,
            double[] gx)
    {
        /* Note that ALPHA is not taken into account when summing FCOST (this
         * is done at the end) while ALPHA is taken into account when
         * integrating the gradient GCOST.  The sum is done in double
         * precision whatever the type. */
        final boolean computeGradient = (gx != null);
        final int dim1 = shape.dimension(0);
        final double xnorm =  mitiv.linalg.ArrayOps.norm2(x);
        final double tau = epsilon * scale[0] / Math.sqrt(shape.number());
        final double t = tau*xnorm;
        final double t2 = square(t);
        double fcost = 0.0;
        double beta = alpha/scale[0];
        double sq = 0.0;
        for (int i1 = 1; i1 < dim1; ++i1) {
            double d = x[i1] - x[i1 - 1];
            double r = sqrt(d*d + t2);

            fcost  += tau * (r - t);
            if (computeGradient) {
                double q = tau/r;
                sq += q;
                double p = beta*q*d;
                gx[i1 - 1] -= p;
                gx[i1] += p;
            }

        }

        sq = (sq - 1.0f/xnorm)* beta*square(tau);
        if (computeGradient) {
            for (int i1 = 0; i1 < dim1; ++i1) {
                gx[i1] += sq * x[i1] ;
            }
        }


        /* Make sure the result is non-negative (it
           can only be negative due to rounding errors). */
        return (fcost > 0.0 ? beta*fcost : 0.0);
    }

    private final double computeFloat2D(double alpha, float[] x, float[] gx)
    {
        /* Note that ALPHA is not taken into account when summing FCOST (this
         * is done at the end) while ALPHA is taken into account when
         * integrating the gradient GCOST.  The sum is done in double
         * precision whatever the type. */
        final boolean computeGradient = (gx != null);
        final int dim1 = shape.dimension(0);
        final int dim2 = shape.dimension(1);
        final float w1 = (float)(1.0/(2.0*square(scale[0])));
        final float w2 = (float)(1.0/(2.0*square(scale[1])));

        final float xnorm =  mitiv.linalg.ArrayOps.norm2(x);
        final float tau = (float)(epsilon  / Math.sqrt(shape.number()));
        final float t = tau*xnorm;
        final float t2 = square(t);
        float sq = 0.0f;

        double fcost = 0.0;
        float _alpha = (float)alpha;
        float x1, x2, x3, x4;
        int j1, j2, j3, j4;
        if (w1 == w2) /* same weights along all directions */ {
            final float w = w1;
            for (int i2 = 1; i2 < dim2; ++i2) {
                j2 = (i2 - 1)*dim1;
                j4 = i2*dim1;
                x2 = x[j2];
                x4 = x[j4];
                for (int i1 = 1; i1 < dim1; ++i1) {
                    j1 = j2++;
                    j3 = j4++;
                    x1 = x2;
                    x2 = x[j2];
                    x3 = x4;
                    x4 = x[j4];
                    float y21 = x2 - x1;
                    float y43 = x4 - x3;
                    float y31 = x3 - x1;
                    float y42 = x4 - x2;
                    float r = sqrt((square(y21) + square(y43)
                    + square(y31) + square(y42))*w + t2);
                    fcost += tau * (r - t);
                    if (computeGradient) {
                        float q = tau/r;
                        sq += q;
                        float p = _alpha*w*q;
                        gx[j1] -= (y21 + y31)*p;
                        gx[j2] += (y21 - y42)*p;
                        gx[j3] -= (y43 - y31)*p;
                        gx[j4] += (y43 + y42)*p;
                    }
                }
            }
        } else /* not same weights along all directions */ {
            for (int i2 = 1; i2 < dim2; ++i2) {
                // Put 2x2 bloc at start of a new line.  In the code, (i1,i2) is
                // 2D coordinates, j1, j2, j3 and j4 are the 1D indices of the 2x2
                // elements.
                j2 = (i2 - 1)*dim1;
                j4 = i2*dim1;
                x2 = x[j2];
                x4 = x[j4];
                for (int i1 = 1; i1 < dim1; ++i1) {
                    // Move to next 2x2 bloc.
                    // +---+---+
                    // | 3 | 4 |
                    // +---+---+
                    // | 1 | 2 |
                    // +---+---+
                    j1 = j2++;
                    x1 = x2;
                    x2 = x[j2];
                    j3 = j4++;
                    x3 = x4;
                    x4 = x[j4];

                    // Compute horizontal and vertical differences.
                    float y21 = x2 - x1;
                    float y43 = x4 - x3;
                    float y31 = x3 - x1;
                    float y42 = x4 - x2;

                    // Compute hyperbolic approximation of L2 norm of
                    // the spatial gradient.
                    float r = sqrt((square(y21) + square(y43))*w1 +
                            (square(y31) + square(y42))*w2 + t2);
                    fcost += tau * (r - t);
                    if (computeGradient) {
                        float q = tau/r;
                        sq += q;
                        float p = _alpha*q;
                        float p1 = w1*p;
                        y21 *= p1;
                        y43 *= p1;
                        float p2 = w2*p;
                        y31 *= p2;
                        y42 *= p2;
                        gx[j1] -= y21 + y31;
                        gx[j2] += y21 - y42;
                        gx[j3] -= y43 - y31;
                        gx[j4] += y43 + y42;
                    }
                }
            }

        }


        sq = (sq - 1.0f/xnorm)* _alpha*square(tau);
        if (computeGradient) {
            for (int i1 = 0; i1 < gx.length; i1++) {
                gx[i1] += sq * x[i1] ;
            }
        }
        /* Make sure the result is non-negative (it
           can only be negative due to rounding errors). */
        return (fcost > 0.0 ? alpha*fcost : 0.0);
    }
    private final double computeDouble2D(double alpha, double[] x, double[] gx)
    {
        /* Note that ALPHA is not taken into account when summing FCOST (this
         * is done at the end) while ALPHA is taken into account when
         * integrating the gradient GCOST.  The sum is done in double
         * precision whatever the type. */
        final boolean computeGradient = (gx != null);
        final int dim1 = shape.dimension(0);
        final int dim2 = shape.dimension(1);
        final double w1 = 1.0/(2.0*square(scale[0]));
        final double w2 = 1.0/(2.0*square(scale[1]));

        final double xnorm =  mitiv.linalg.ArrayOps.norm2(x);
        final double tau = epsilon  / Math.sqrt(shape.number());
        final double t = tau*xnorm;
        final double t2 = square(t);
        double sq = 0.0f;

        double fcost = 0.0;
        double x1, x2, x3, x4;
        int j1, j2, j3, j4;
        if (w1 == w2) /* same weights along all directions */ {
            final double w = w1;
            for (int i2 = 1; i2 < dim2; ++i2) {
                j2 = (i2 - 1)*dim1;
                j4 = i2*dim1;
                x2 = x[j2];
                x4 = x[j4];
                for (int i1 = 1; i1 < dim1; ++i1) {
                    j1 = j2++;
                    j3 = j4++;
                    x1 = x2;
                    x2 = x[j2];
                    x3 = x4;
                    x4 = x[j4];
                    double y21 = x2 - x1;
                    double y43 = x4 - x3;
                    double y31 = x3 - x1;
                    double y42 = x4 - x2;
                    double r = sqrt((square(y21) + square(y43)
                    + square(y31) + square(y42))*w + t2);
                    fcost += tau * (r - t);
                    if (computeGradient) {
                        double q = tau/r;
                        sq += q;
                        double p = alpha*w*q;
                        gx[j1] -= (y21 + y31)*p;
                        gx[j2] += (y21 - y42)*p;
                        gx[j3] -= (y43 - y31)*p;
                        gx[j4] += (y43 + y42)*p;
                    }
                }
            }
        } else /* not same weights along all directions */ {
            for (int i2 = 1; i2 < dim2; ++i2) {
                // Put 2x2 bloc at start of a new line.  In the code, (i1,i2) is
                // 2D coordinates, j1, j2, j3 and j4 are the 1D indices of the 2x2
                // elements.
                j2 = (i2 - 1)*dim1;
                j4 = i2*dim1;
                x2 = x[j2];
                x4 = x[j4];
                for (int i1 = 1; i1 < dim1; ++i1) {
                    // Move to next 2x2 bloc.
                    // +---+---+
                    // | 3 | 4 |
                    // +---+---+
                    // | 1 | 2 |
                    // +---+---+
                    j1 = j2++;
                    x1 = x2;
                    x2 = x[j2];
                    j3 = j4++;
                    x3 = x4;
                    x4 = x[j4];

                    // Compute horizontal and vertical differences.
                    double y21 = x2 - x1;
                    double y43 = x4 - x3;
                    double y31 = x3 - x1;
                    double y42 = x4 - x2;

                    // Compute hyperbolic approximation of L2 norm of
                    // the spatial gradient.
                    double r = sqrt((square(y21) + square(y43))*w1 +
                            (square(y31) + square(y42))*w2 + t2);
                    fcost += tau * (r - t);
                    if (computeGradient) {
                        double q = tau/r;
                        sq += q;
                        double p = alpha*q;
                        double p1 = w1*p;
                        y21 *= p1;
                        y43 *= p1;
                        double p2 = w2*p;
                        y31 *= p2;
                        y42 *= p2;

                        gx[j1] -= y21 + y31;
                        gx[j2] += y21 - y42;
                        gx[j3] -= y43 - y31;
                        gx[j4] += y43 + y42;
                    }
                }
            }
        }


        sq = (sq - 1.0f/xnorm)* alpha*square(tau);
        if (computeGradient) {
            for (int i1 = 0; i1 < gx.length; i1++) {
                gx[i1] += sq * x[i1] ;
            }   
        }

        /* Make sure the result is non-negative (it
           can only be negative due to rounding errors). */
        return (fcost > 0.0 ? alpha*fcost : 0.0);
    }

    /*
     * NOTATIONS FOR 3-D VOLUME X(i1,i2,i3)
     *
     *                 i3  i2
     *                  | /
     *                  |/              X1 = X(i1-1,i2-1,i3-1)
     *        X7--------X8---> i1       X2 = X(i1  ,i2-1,i3-1)
     *       /:        /|               X3 = X(i1-1,i2  ,i3-1)
     *      / :       / |               X4 = X(i1  ,i2  ,i3-1)
     *     X5--------X6 |               X5 = X(i1-1,i2-1,i3  )
     *     |  X3.....|..X4              X6 = X(i1  ,i2-1,i3  )
     *     | '       | /                X7 = X(i1-1,i2  ,i3  )
     *     |'        |/                 X8 = X(i1  ,i2  ,i3  )
     *     X1--------X2
     *
     */
    //
    private final double computeFloat3D(double alpha, float[] x, float[] gx)
    {
        /* Note that ALPHA is not taken into account when summing FCOST (this
         * is done at the end) while ALPHA is taken into account when
         * integrating the gradient GCOST.  The sum is done in double
         * precision whatever the type. */
        final boolean computeGradient = (gx != null);
        final int dim1 = shape.dimension(0);
        final int dim2 = shape.dimension(1);
        final int dim3 = shape.dimension(2);
        final float w1 = (float)(1.0/(4.0*square(scale[0])));
        final float w2 = (float)(1.0/(4.0*square(scale[1])));
        final float w3 = (float)(1.0/(4.0*square(scale[2])));

        final float xnorm =  mitiv.linalg.ArrayOps.norm2(x);
        final float tau = (float)( epsilon  / Math.sqrt(shape.number()));
        final float t = tau*xnorm;
        final float t2 = square(t);
        float sq = 0.0f;

        // The sum is done in double precision whatever the type.
        double fcost = 0.0;
        float _alpha = (float)alpha;
        float x1, x2, x3, x4, x5, x6, x7, x8;
        int j1, j2, j3, j4, j5, j6, j7, j8;
        for (int i3 = 1; i3 < dim3; ++i3) {
            for (int i2 = 1; i2 < dim2; ++i2) {
                // Put 2x2x2 bloc such that 8th point is at coordinates (0,i2,i3).
                j2 = (i2 - 1 + (i3 - 1)*dim2)*dim1; // (0,i2-1,i3-1)
                j4 = (i2     + (i3 - 1)*dim2)*dim1; // (0,i2,i3-1)
                j6 = (i2 - 1 + (i3    )*dim2)*dim1; // (0,i2-1,i3)
                j8 = (i2     + (i3    )*dim2)*dim1; // (0,i2,i3)
                x2 = x[j2];
                x4 = x[j4];
                x6 = x[j6];
                x8 = x[j8];
                for (int i1 = 1; i1 < dim1; ++i1) {
                    // Move to next 2x2x2 bloc.
                    j1 = j2++;
                    x1 = x2;
                    x2 = x[j2];
                    j3 = j4++;
                    x3 = x4;
                    x4 = x[j4];
                    j5 = j6++;
                    x5 = x6;
                    x6 = x[j6];
                    j7 = j8++;
                    x7 = x8;
                    x8 = x[j8];

                    // Compute differences along 1st dimension.
                    float y21 = x2 - x1;
                    float y43 = x4 - x3;
                    float y65 = x6 - x5;
                    float y87 = x8 - x7;
                    float r1 = square(y21) + square(y43) + square(y65) + square(y87);

                    // Compute differences along 2nd dimension.
                    float y31 = x3 - x1;
                    float y42 = x4 - x2;
                    float y75 = x7 - x5;
                    float y86 = x8 - x6;
                    float r2 = square(y31) + square(y42) + square(y75) + square(y86);

                    // Compute differences along 3rd dimension.
                    float y51 = x5 - x1;
                    float y62 = x6 - x2;
                    float y73 = x7 - x3;
                    float y84 = x8 - x4;
                    float r3 = square(y51) + square(y62) + square(y73) + square(y84);

                    // Compute hyperbolic approximation of L2 norm of
                    // the spatial gradient.
                    float r = sqrt(w1*r1 + w2*r2 + w3*r3 + t2);
                    fcost += r;
                    if (computeGradient) {
                        float q = tau/r;
                        sq += q;
                        float p = _alpha*q;
                        float p1 = w1*p;
                        y21 *= p1;
                        y43 *= p1;
                        y65 *= p1;
                        y87 *= p1;
                        float p2 = w2*p;
                        y31 *= p2;
                        y42 *= p2;
                        y75 *= p2;
                        y86 *= p2;
                        float p3 = w3*p;
                        y51 *= p3;
                        y62 *= p3;
                        y73 *= p3;
                        y84 *= p3;
                        gx[j1] -= y21 + y31 + y51;
                        gx[j2] += y21 - y42 - y62;
                        gx[j3] -= y43 - y31 + y73;
                        gx[j4] += y43 + y42 - y84;
                        gx[j5] -= y65 + y75 - y51;
                        gx[j6] += y65 - y86 + y62;
                        gx[j7] -= y87 - y75 - y73;
                        gx[j8] += y87 + y86 + y84;
                    }
                }
            }
        }

        sq = (sq - 1.0f/xnorm)* _alpha*square(tau);
        if (computeGradient) {
            for (int i1 = 0; i1 < gx.length; i1++) {
                gx[i1] += sq * x[i1] ;
            }
        }
        /* Make sure the result is non-negative (it
           can only be negative due to rounding errors). */
        return (fcost > 0.0 ? alpha*fcost : 0.0);
    }
    private final double computeDouble3D(double alpha, double[] x, double[] gx)
    {
        /* Note that ALPHA is not taken into account when summing FCOST (this
         * is done at the end) while ALPHA is taken into account when
         * integrating the gradient GCOST.  The sum is done in double
         * precision whatever the type. */
        final boolean computeGradient = (gx != null);
        final int dim1 = shape.dimension(0);
        final int dim2 = shape.dimension(1);
        final int dim3 = shape.dimension(2);
        final double w1 = 1.0/(4.0*square(scale[0]));
        final double w2 = 1.0/(4.0*square(scale[1]));
        final double w3 = 1.0/(4.0*square(scale[2]));

        final double xnorm =  mitiv.linalg.ArrayOps.norm2(x);
        final double tau = epsilon  / Math.sqrt(shape.number());
        final double t = tau*xnorm;
        final double t2 = square(t);
        double sq = 0.0f;


        // The sum is done in double precision whatever the type.
        double fcost = 0.0;
        double x1, x2, x3, x4, x5, x6, x7, x8;
        int j1, j2, j3, j4, j5, j6, j7, j8;
        for (int i3 = 1; i3 < dim3; ++i3) {
            for (int i2 = 1; i2 < dim2; ++i2) {
                // Put 2x2x2 bloc such that 8th point is at coordinates (0,i2,i3).
                j2 = (i2 - 1 + (i3 - 1)*dim2)*dim1; // (0,i2-1,i3-1)
                j4 = (i2     + (i3 - 1)*dim2)*dim1; // (0,i2,i3-1)
                j6 = (i2 - 1 + (i3    )*dim2)*dim1; // (0,i2-1,i3)
                j8 = (i2     + (i3    )*dim2)*dim1; // (0,i2,i3)
                x2 = x[j2];
                x4 = x[j4];
                x6 = x[j6];
                x8 = x[j8];
                for (int i1 = 1; i1 < dim1; ++i1) {
                    // Move to next 2x2x2 bloc.
                    j1 = j2++;
                    x1 = x2;
                    x2 = x[j2];
                    j3 = j4++;
                    x3 = x4;
                    x4 = x[j4];
                    j5 = j6++;
                    x5 = x6;
                    x6 = x[j6];
                    j7 = j8++;
                    x7 = x8;
                    x8 = x[j8];

                    // Compute differences along 1st dimension.
                    double y21 = x2 - x1;
                    double y43 = x4 - x3;
                    double y65 = x6 - x5;
                    double y87 = x8 - x7;
                    double r1 = square(y21) + square(y43) + square(y65) + square(y87);

                    // Compute differences along 2nd dimension.
                    double y31 = x3 - x1;
                    double y42 = x4 - x2;
                    double y75 = x7 - x5;
                    double y86 = x8 - x6;
                    double r2 = square(y31) + square(y42) + square(y75) + square(y86);

                    // Compute differences along 3rd dimension.
                    double y51 = x5 - x1;
                    double y62 = x6 - x2;
                    double y73 = x7 - x3;
                    double y84 = x8 - x4;
                    double r3 = square(y51) + square(y62) + square(y73) + square(y84);

                    // Compute hyperbolic approximation of L2 norm of
                    // the spatial gradient.
                    double r = sqrt(w1*r1 + w2*r2 + w3*r3 + t2);
                    fcost +=  tau * (r - t);

                    if (computeGradient) {
                        double q = tau/r;
                        sq += q;
                        double p = alpha*q;

                        double p1 = w1*p;
                        y21 *= p1;
                        y43 *= p1;
                        y65 *= p1;
                        y87 *= p1;
                        double p2 = w2*p;
                        y31 *= p2;
                        y42 *= p2;
                        y75 *= p2;
                        y86 *= p2;
                        double p3 = w3*p;
                        y51 *= p3;
                        y62 *= p3;
                        y73 *= p3;
                        y84 *= p3;
                        gx[j1] -= y21 + y31 + y51;
                        gx[j2] += y21 - y42 - y62;
                        gx[j3] -= y43 - y31 + y73;
                        gx[j4] += y43 + y42 - y84;
                        gx[j5] -= y65 + y75 - y51;
                        gx[j6] += y65 - y86 + y62;
                        gx[j7] -= y87 - y75 - y73;
                        gx[j8] += y87 + y86 + y84;
                    }
                }
            }
        }


        sq = (sq - 1.0f/xnorm)* alpha*square(tau);
        if (computeGradient) {
            for (int i1 = 0; i1 < gx.length; i1++) {
                gx[i1] += sq * x[i1] ;
            }
        }


        /* Make sure the result is non-negative (it
           can only be negative due to rounding errors). */
        return (fcost > 0.0 ? alpha*fcost : 0.0);
    }

    static final private boolean notFinite(double val) {
        return Double.isInfinite(val) || Double.isNaN(val);
    }

    static final private float sqrt(float a) {
        return (float)Math.sqrt(a);
    }

    static final private double sqrt(double a) {
        return Math.sqrt(a);
    }

    static final private float square(float a) {
        return a*a;
    }

    static final private double square(double a) {
        return a*a;
    }

    @Override
    public double evaluate(double alpha, Vector x) {
        return computeCostAndGradient(alpha, x, null, false);
    }

    @Override
    public double getHomogeneousDegree() {
        return 1.0;
    }
}
