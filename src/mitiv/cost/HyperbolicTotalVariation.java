/*3
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
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Hyperbolic approximation of the total variation.
 * 
 * The hyperbolic approximation of the total variation is a smooth (i.e. differentiable)
 * cost function which can also be used to implement edge-preserving smoothness.
 * The current implementation is also isotropic.
 * 
 * @author Lucie Thiébaut
 */
public class HyperbolicTotalVariation implements DifferentiableCostFunction {
    /** The vector space for the variables. */
    final protected ShapedVectorSpace inputSpace;

    /** The number of dimensions. */
    protected int rank;

    /** The dimensions of the variables. */
    protected Shape shape;

    /** The threshold */
    protected double epsilon;

    /** The scaling of the finite differences along each dimension. */
    protected double[] delta;

    public HyperbolicTotalVariation(ShapedVectorSpace inputSpace, double epsilon) {
        this.inputSpace = inputSpace;
        shape = inputSpace.getShape();
        rank = (shape == null ? 0 : shape.rank());
        setThreshold(epsilon);
        delta = new double[rank];
        defaultScale();
    }

    public HyperbolicTotalVariation(ShapedVectorSpace inputSpace, double epsilon, double[] delta) {
        this(inputSpace, epsilon);
        setScale(delta);
    }

    public void setThreshold(double epsilon) {
        if (epsilon <= 0.0) {
            throw new IllegalArgumentException("Bad threshold value.");
        }
        this.epsilon = epsilon;
    }
    public double getThreshold() {
        return epsilon;
    }

    public void defaultScale() {
        setScale(1.0);
    }

    public void setScale(double value) {
        if (value <= 0.0) {
            throw new IllegalArgumentException("Bad scale value.");
        }
        for (int k = 0; k < rank; ++k) {
            this.delta[k] = value;
        }
    }

    public void setScale(double[] delta) {
        if (delta == null || delta.length != rank) {
            throw new IllegalArgumentException("Bad scale size.");
        }
        for (int k = 0; k < rank; ++k) {
            if (delta[k] <= 0.0) {
                throw new IllegalArgumentException("Bad scale value.");
            }
        }
        for (int k = 0; k < rank; ++k) {
            this.delta[k] = delta[k];
        }
    }
    public double getScale(int k) {
        return (k >= 0 && k < rank ? delta[k] : 1.0);
    }

    @Override
    public VectorSpace getInputSpace() {
        return inputSpace;
    }

    @Override
    public double
    computeCostAndGradient(double alpha, Vector x, Vector gx, boolean clr)
    {
        if (gx != null && clr) {
            gx.fill(0.0);
        }
        if (rank == 2) {
            return computeCostAndGradient2D(alpha, x, gx);
        } else if (rank == 3) {
            return computeCostAndGradient3D(alpha, x, gx);
        } else {
            throw new IllegalArgumentException("Unsupported number of dimensions for Total Variation.");
        }
    }

    // The quadratic norm of the spatial gradient is the sum along all
    // the dimensions of the average squared differences along the
    // dimension.  A weight is applied along each dimension to account
    // for the regularization weight and for unequal sampling along the
    // dimensions.
    //
    // More specifically, the regularization writes:
    //
    //     alpha*(sqrt(sd1 + sd2 + ... + sdn + eps^2) - eps)
    //
    // with alpha > 0 the regularization weight, n the rank and sdk the
    // average squared scaled differences along the kth dimension.  The
    // removal of eps is just to have the cost equal to zero for a flat
    // array.
    //
    // For instance, in 2D, the total variation is computed for each 2x2
    // blocs with:
    //
    //     sd1 = (((x2 - x1)/delta1)^2 + ((x4 - x3)/delta1)^2)/2
    //         = w1*((x2 - x1)^2 + (x4 - x3)^2)
    //     sd2 = (((x3 - x1)/delta2)^2 + ((x4 - x2)/delta2)^2)/2
    //         = w2*((x3 - x1)^2 + (x4 - x2)^2)
    //
    // where division by delta1 and delta2 is to scale the finite
    // differences along each dimension and dimension by 2 is to average
    // (there 2 possible finite differences in a 2x2 bloc along a given
    // dimension).  The weights w1 and w2 are given by:
    //
    //     w1 = 1/(2*delta1^2)
    //     w2 = 1/(2*delta2^2)
    //     s = eps^2
    private final double
    computeCostAndGradient2D(double alpha, Vector x, Vector gx)
    {
        // Note that ALPHA is not taken into account when summing FCOST (this
        // is done at the end) while ALPHA is taken into account when
        // integrating the gradient GCOST.
        final boolean computeGradient = (gx != null);
        final int dim1 = shape.dimension(0);
        final int dim2 = shape.dimension(1);
        final double w1 = 1.0/(2.0*square(delta[0]));
        final double w2 = 1.0/(2.0*square(delta[1]));
        final double s = square(epsilon);

        double fcost = 0.0;
        double x1, x2, x3, x4;
        int j1, j2, j3, j4;
        if (w1 == w2) /* same weights along all directions */ {
            final double w = w1;
            for (int i2 = 1; i2 < dim2; ++i2) {
                j2 = (i2 - 1)*dim1;
                j4 = i2*dim1;
                x2 = x.get(j2);
                x4 = x.get(j4);
                for (int i1 = 1; i1 < dim1; ++i1) {
                    j1 = j2++;
                    j3 = j4++;
                    x1 = x2;
                    x2 = x.get(j2);
                    x3 = x4;
                    x4 = x.get(j4);
                    double y21 = x2 - x1;
                    double y43 = x4 - x3;
                    double y31 = x3 - x1;
                    double y42 = x4 - x2;
                    double r = Math.sqrt((square(y21) + square(y43)
                            + square(y31) + square(y42))*w + s);
                    fcost += r;
                    if (computeGradient) {
                        double p = alpha*w/r;
                        gx.set(j1, gx.get(j1) - (y21 + y31)*p);
                        gx.set(j2, gx.get(j2) + (y21 - y42)*p);
                        gx.set(j3, gx.get(j3) - (y43 - y31)*p);
                        gx.set(j4, gx.get(j4) + (y43 + y42)*p);
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
                x2 = x.get(j2);
                x4 = x.get(j4);
                for (int i1 = 1; i1 < dim1; ++i1) {
                    // Move to next 2x2 bloc.
                    // +---+---+
                    // | 3 | 4 |
                    // +---+---+
                    // | 1 | 2 |
                    // +---+---+
                    j1 = j2++;
                    x1 = x2;
                    x2 = x.get(j2);
                    j3 = j4++;
                    x3 = x4;
                    x4 = x.get(j4);

                    // Compute horizontal and vertical differences.
                    double y21 = x2 - x1;
                    double y43 = x4 - x3;
                    double y31 = x3 - x1;
                    double y42 = x4 - x2;

                    // Compute hyperbolic approximation of L2 norm of
                    // the spatial gradient.
                    double r = Math.sqrt((square(y21) + square(y43))*w1 +
                            (square(y31) + square(y42))*w2 + s);
                    fcost += r;
                    if (computeGradient) {
                        double q = alpha/r;
                        double p1 = w1*q;
                        y21 *= p1;
                        y43 *= p1;
                        double p2 = w2*q;
                        y31 *= p2;
                        y42 *= p2;
                        gx.set(j1, gx.get(j1) - (y21 + y31));
                        gx.set(j2, gx.get(j2) + (y21 - y42));
                        gx.set(j3, gx.get(j3) - (y43 - y31));
                        gx.set(j4, gx.get(j4) + (y43 + y42));
                    }
                }
            }
        }

        /* Remove the "bias" and make sure the result is non-negative (it
           can only be negative due to rounding errors). */
        fcost -= (dim1 - 1)*(dim2 - 1)*epsilon;
        if (fcost < 0.0) {
            fcost = 0.0;
        }
        return alpha*fcost;
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
    private final double
    computeCostAndGradient3D(double alpha, Vector x, Vector gx)
    {
        // Note that ALPHA is not taken into account when summing FCOST (this
        // is done at the end) while ALPHA is taken into account when
        // integrating the gradient GCOST.
        final boolean computeGradient = (gx != null);
        final int dim1 = shape.dimension(0);
        final int dim2 = shape.dimension(1);
        final int dim3 = shape.dimension(2);
        final double w1 = 1.0/(2.0*square(delta[0]));
        final double w2 = 1.0/(2.0*square(delta[1]));
        final double w3 = 1.0/(2.0*square(delta[2]));
        final double s = square(epsilon);

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
                x2 = x.get(j2);
                x4 = x.get(j4);
                x6 = x.get(j6);
                x8 = x.get(j8);
                for (int i1 = 1; i1 < dim1; ++i1) {
                    // Move to next 2x2x2 bloc.
                    j1 = j2++;
                    x1 = x2;
                    x2 = x.get(j2);
                    j3 = j4++;
                    x3 = x4;
                    x4 = x.get(j4);
                    j5 = j6++;
                    x5 = x6;
                    x6 = x.get(j6);
                    j7 = j8++;
                    x7 = x8;
                    x8 = x.get(j8);

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
                    double r = Math.sqrt(w1*r1 + w2*r2 + w3*r3 + s);
                    fcost += r;
                    if (computeGradient) {
                        double q = alpha/r;
                        double p1 = w1*q;
                        y21 *= p1;
                        y43 *= p1;
                        y65 *= p1;
                        y87 *= p1;
                        double p2 = w2*q;
                        y31 *= p2;
                        y42 *= p2;
                        y75 *= p2;
                        y86 *= p2;
                        double p3 = w3*q;
                        y51 *= p3;
                        y62 *= p3;
                        y73 *= p3;
                        y84 *= p3;
                        gx.set(j1, gx.get(j1) - (y21 + y31 + y51));
                        gx.set(j2, gx.get(j2) + (y21 - y42 - y62));
                        gx.set(j3, gx.get(j3) - (y43 - y31 + y73));
                        gx.set(j4, gx.get(j4) + (y43 + y42 - y84));
                        gx.set(j5, gx.get(j5) - (y65 + y75 - y51));
                        gx.set(j6, gx.get(j6) + (y65 - y86 + y62));
                        gx.set(j7, gx.get(j7) - (y87 - y75 - y73));
                        gx.set(j8, gx.get(j8) + (y87 + y86 + y84));
                    }
                }
            }
        }

        /* Remove the "bias" and make sure the result is non-negative (it
           can only be negative due to rounding errors). */
        fcost -= (dim1 - 1)*(dim2 - 1)*(dim3 - 1)*epsilon;
        if (fcost < 0.0) {
            fcost = 0.0;
        }
        return alpha*fcost;
    }

    static final private double square(double a) {
        return a*a;
    }

    @Override
    public double evaluate(double alpha, Vector x) {
        return computeCostAndGradient(alpha, x, null, false);
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
