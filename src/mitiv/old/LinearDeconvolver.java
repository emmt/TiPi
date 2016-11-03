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

package mitiv.old;

import mitiv.base.Shape;
import mitiv.exception.NotImplementedException;
import mitiv.linalg.ArrayOps;
import mitiv.linalg.IdentityOperator;
import mitiv.linalg.LinearConjugateGradient;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.FloatShapedVectorSpace;
import mitiv.linalg.shaped.RealComplexFFT;
import mitiv.old.invpb.LeftHandSideMatrix;
/**
 *
 * @author Jonathan Léger
 */
public class LinearDeconvolver {
    private int rank;
    private final Vector h;
    private Vector q;
    private final Vector w;
    private final Vector y;
    private Vector z; // scratch vector in complex space
    private Vector b;
    private LinearOperator H;
    private LinearOperator W;
    private LinearOperator Q;
    private LeftHandSideMatrix A;
    private final RealComplexFFT FFT;
    private LinearConjugateGradient cg;
    private double muFactor = 1.0;
    private final boolean single;

    /**
     * Float Version.
     *
     * @param shape
     * @param data
     * @param psf
     * @param wgt
     * @param mu
     */
    public LinearDeconvolver(Shape shape, float[] data, float[] psf, float[] wgt, double mu) {
        /* See single precision version for comments. */
        FloatShapedVectorSpace space = new FloatShapedVectorSpace(shape);
        y = space.wrap(data);
        h = space.wrap(psf);
        w = (wgt == null ? null : space.wrap(wgt));
        FFT = new RealComplexFFT(space);
        single = true;
        setup(shape, mu);
    }

    /**
     * Double version.
     *
     * @param shape
     * @param data
     * @param psf
     * @param wgt
     * @param mu
     */
    public LinearDeconvolver(Shape shape, double[] data, double[] psf, double[] wgt, double mu) {
        /* Check dimensions and create FFT operator.
         * We assume that all cases (i.e. 1D, 2D, 3D) can be wrapped
         * into a simple vector space.  Note that calling space.wrap() checks
         * whether array size is compatible with vector space.
         */
        DoubleShapedVectorSpace space = new DoubleShapedVectorSpace(shape);
        y = space.wrap(data);
        h = space.wrap(psf);
        w = (wgt == null ? null : space.wrap(wgt));
        FFT = new RealComplexFFT(space);
        single = false;
        setup(shape, mu);
    }

    private void setup(Shape shape, double mu) {
        rank = shape.rank();
        if (rank > 3) {
            throw new IllegalArgumentException("Too many dimensions.");
        }

        /* Allocate workspaces. */
        VectorSpace space = FFT.getInputSpace();
        z = FFT.getOutputSpace().create();
        q = space.create(); // not outputSpace!

        if (single) {
            generateIsotropicQ(shape, ((FloatShapedVector)q).getData());
            Q = new LinearOperator(space) {
                @Override
                protected void _apply(Vector dst, Vector src, int job) {
                    if (job == DIRECT || job == ADJOINT) {
                        FFT.apply(z, src, DIRECT);
                        multiplyByQ(((FloatShapedVector)q).getData(), ((FloatShapedVector)z).getData());
                        FFT.apply(dst, z, INVERSE);
                    } else {
                        throw new NotImplementedException();
                    }
                }
            };
        } else {
            generateIsotropicQ(shape, ((DoubleShapedVector)q).getData());
            Q = new LinearOperator(space) {
                @Override
                protected void _apply(Vector dst, Vector src, int job) {
                    if (job == DIRECT || job == ADJOINT) {
                        FFT.apply(z, src, DIRECT);
                        multiplyByQ(((DoubleShapedVector)q).getData(), ((DoubleShapedVector)z).getData());
                        FFT.apply(dst, z, INVERSE);
                    } else {
                        throw new NotImplementedException();
                    }
                }
            };
        }

        /* Create convolution operator H. */
        H = new ConvolutionOperator(FFT, h);

        /* Check weights. */
        if (w == null) {
            muFactor = 1.0;
            W = new IdentityOperator(H.getOutputSpace());
        } else {
            double wMin, wMax;
            if (single) {
                float[] wMinMax = ArrayOps.getMinMax(((FloatShapedVector)w).getData());
                wMin = wMinMax[0];
                wMax = wMinMax[1];
            } else {
                double[] wMinMax = ArrayOps.getMinMax(((DoubleShapedVector)w).getData());
                wMin = wMinMax[0];
                wMax = wMinMax[1];
            }
            if (wMin < 0.0) {
                throw new IllegalArgumentException("Weights must be non-negative.");
            }
            if (wMax <= 0.0) {
                // FIXME: this is not an exception, just a very special case (a general solution
                // is to return  a vector of zeros).
                throw new IllegalArgumentException("All weights are zero.");
            }
            if (wMin == wMax) {
                // FIXME: more optimization possible?
                muFactor = 1.0/wMax;
                W = new IdentityOperator(H.getOutputSpace());
            } else {
                muFactor = 1.0;
                if (single) {
                    W = new LinearOperator(space) {
                        @Override
                        protected void _apply(Vector dst, Vector src, int job) {
                            if (job == DIRECT || job == ADJOINT) {
                                multiplyByW(((FloatShapedVector)w).getData(),
                                        ((FloatShapedVector)src).getData(),
                                        ((FloatShapedVector)dst).getData());
                            } else {
                                throw new NotImplementedException();
                            }
                        }
                    };
                } else {
                    W = new LinearOperator(space) {
                        @Override
                        protected void _apply(Vector dst, Vector src, int job) {
                            if (job == DIRECT || job == ADJOINT) {
                                multiplyByW(((DoubleShapedVector)w).getData(),
                                        ((DoubleShapedVector)src).getData(),
                                        ((DoubleShapedVector)dst).getData());
                            } else {
                                throw new NotImplementedException();
                            }
                        }
                    };
                }
            }
        }

        /* Regularization weight. */
        if (mu < 0.0) {
            throw new IllegalArgumentException("Regularization weight must be non-negative.");
        }
        mu *= muFactor;

        /* Creation of the LHS matrix A and RHS vector b for the linear problem. */
        A = new LeftHandSideMatrix(H, W, Q , mu);
        b = A.getOutputSpace().create();
        A.computeRightHandSideVector(y, b);
        cg = new LinearConjugateGradient(A, b);
    }

    private static void multiplyByQ(final double[] q, double[] z) {
        int size = q.length;
        for (int k = 0; k < size; ++k) {
            z[2*k] *= q[k];
            z[2*k + 1] *= q[k];
        }
    }

    private static void multiplyByQ(final float[] q, float[] z) {
        int size = q.length;
        for (int k = 0; k < size; ++k) {
            z[2*k] *= q[k];
            z[2*k + 1] *= q[k];
        }
    }

    private static void multiplyByW(final double[] w, final double[] x, double[] y) {
        int n = w.length;
        for (int j = 0; j < n; ++j) {
            y[j] = w[j]*x[j];
        }
    }

    private static void multiplyByW(final float[] w, final float[] x, float[] y) {
        int n = w.length;
        for (int j = 0; j < n; ++j) {
            y[j] = w[j]*x[j];
        }
    }

    private static double[] generateFrequency(double s, int length) {
        double[] u = new double[length];
        int k0 = length/2;
        for (int k = 0; k <= k0; ++k) {
            u[k] = s*k;
        }
        for (int k = k0 + 1; k < length; ++k) {
            u[k] = s*(k - length);
        }
        return u;
    }
    private static void generateIsotropicQ(Shape shape, float[] q) {
        int n = q.length;
        double[] qTemp = new double[n];
        generateIsotropicQ(shape, qTemp);
        for (int j = 0; j < n; ++j) {
            q[j] = (float)qTemp[j];
        }
    }
    private static void generateIsotropicQ(Shape shape, double[] q) {
        /* Compute weights q = 4*PI^2*sum(kj/Nj) for operator Q. */
        int rank = shape.rank();
        double[][] u = new double[rank][];
        for (int r = 0; r < rank; ++r) {
            double[] t = generateFrequency(2.0*Math.PI/shape.dimension(r),
                    shape.dimension(r));
            for (int k = 0; k < t.length; ++k) {
                t[k] = t[k]*t[k];
            }
            u[r] = t;
        }
        if (rank == 1) {
            int n1 = shape.dimension(0);
            double[] u1 = u[0];
            for (int k1 = 0; k1 < n1; ++k1) {
                q[k1] = u1[k1];
            }
        } else if (rank == 2) {
            int n1 = shape.dimension(0);
            int n2 = shape.dimension(1);
            double[] u1 = u[0];
            double[] u2 = u[1];
            for (int k2 = 0; k2 < n2; ++k2) {
                for (int k1 = 0; k1 < n1; ++k1) {
                    q[k1 + n1*k2] = u1[k1] + u2[k2];
                }
            }
        } else {
            int n1 = shape.dimension(0);
            int n2 = shape.dimension(1);
            int n3 = shape.dimension(2);
            double[] u1 = u[0];
            double[] u2 = u[1];
            double[] u3 = u[2];
            for (int k3 = 0; k3 < n3; ++k3) {
                for (int k2 = 0; k2 < n2; ++k2) {
                    for (int k1 = 0; k1 < n1; ++k1) {
                        q[k1 + n1*k2 + n1*n2*k3] = u1[k1] + u2[k2] + u3[k3];
                    }
                }
            }
        }
    }

    /**
     * @return mu
     */
    public double getMu() {
        return A.getMu()/muFactor;
    }

    /**
     * @param mu
     */
    public void setMu(double mu) {
        A.setMu(mu*muFactor);
    }

    /**
     * Compute the solution and store the result in x
     *
     * @param x
     * @param maxiter
     * @param reset
     * @return a value
     */
    public int solve(float[] x, int maxiter, boolean reset) {
        if (! single) {
            throw new IllegalArgumentException("Expecting a single precision floating point array.");
        }
        return solve(((FloatShapedVectorSpace)A.getInputSpace()).wrap(x), maxiter, reset);
    }

    /**
     * Compute the solution and store the result in x
     *
     * @param x
     * @param maxiter
     * @param reset
     * @return a value
     */
    public int solve(double[] x, int maxiter, boolean reset) {
        if (single) {
            throw new IllegalArgumentException("Expecting a double precision floating point array.");
        }
        return solve(((DoubleShapedVectorSpace)A.getInputSpace()).wrap(x), maxiter, reset);
    }

    /**
     *
     * Not sure if can really be used
     * @param x
     * @param maxiter
     * @param reset
     * @return a value
     */
    public int solve(Vector x, int maxiter, boolean reset) {
        return cg.solve(x, maxiter, reset);
    }
}
