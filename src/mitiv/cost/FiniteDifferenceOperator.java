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
import mitiv.base.indexing.BoundaryConditions;
import mitiv.exception.IncorrectSpaceException;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.FloatShapedVectorSpace;
import mitiv.random.DoubleGenerator;
import mitiv.random.FloatGenerator;
import mitiv.random.UniformDistribution;

public class FiniteDifferenceOperator extends LinearOperator {
    private int[][] index;
    final boolean single;

    private static void testDoubleOperator(DoubleGenerator generator,
                                           int[] shape,
                                           BoundaryConditions condition) {
        DoubleShapedVectorSpace inp = new DoubleShapedVectorSpace(shape);
        LinearOperator D = new FiniteDifferenceOperator(inp, condition);
        DoubleShapedVectorSpace out = (DoubleShapedVectorSpace) D.getOutputSpace();
        DoubleShapedVector a = inp.create();
        DoubleShapedVector b = out.create();
        a.fill(generator);
        b.fill(generator);
        System.out.printf("Testing the adjoint in %dD arrays, shape = [%d", shape.length, shape[0]);
        for (int k = 1; k < shape.length; ++k) System.out.printf(",%d", shape[k]);
        System.out.printf("], of random values:\n");
        System.out.println("  relative error = " + D.checkAdjoint(a, b));
    }

    private static void testFloatOperator(FloatGenerator generator,
                                          int[] shape,
                                          BoundaryConditions condition) {
        FloatShapedVectorSpace inp = new FloatShapedVectorSpace(shape);
        LinearOperator D = new FiniteDifferenceOperator(inp, condition);
        FloatShapedVectorSpace out = (FloatShapedVectorSpace) D.getOutputSpace();
        FloatShapedVector a = inp.create();
        FloatShapedVector b = out.create();
        a.fill(generator);
        b.fill(generator);
        System.out.printf("Testing the adjoint in %dD arrays, shape = [%d", shape.length, shape[0]);
        for (int k = 1; k < shape.length; ++k) System.out.printf(",%d", shape[k]);
        System.out.printf("], of random values:\n");
        System.out.println("  relative error = " + D.checkAdjoint(a, b));
    }

    public static void main(String[] args) {
        DoubleShapedVectorSpace inp = new DoubleShapedVectorSpace(new int[]{5});
        DoubleShapedVector a = inp.create();
        a.set(0,  1.2);
        a.set(1,  2.7);
        a.set(2, -3.1);
        a.set(3,  5.3);
        a.set(4, -9.0);
        LinearOperator dif = new FiniteDifferenceOperator(inp);
        DoubleShapedVectorSpace out = (DoubleShapedVectorSpace) dif.getOutputSpace();
        DoubleShapedVector b = out.create();
        b.set(0,  2.1);
        b.set(1,  7.2);
        b.set(2, -1.3);
        b.set(3,  3.5);
        b.set(4, -0.9);
        System.out.println("Testing the adjoint:");
        System.out.println("  a = " + a);
        System.out.println("  b = " + b);
        System.out.println("  relative error = " + dif.checkAdjoint(a, b));
        System.out.println("");
        System.out.println("Testing the operator:");
        dif.apply(b, a);
        DoubleShapedVector c = inp.create();
        dif.apply(c, b, LinearOperator.ADJOINT);
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);
        System.out.println("");
        UniformDistribution rand = new UniformDistribution(-1.0, +1.0);
        testDoubleOperator(rand, new int[]{3,4,5}, BoundaryConditions.MIRROR);
        testDoubleOperator(rand, new int[]{3,4,5}, BoundaryConditions.NORMAL);
        testFloatOperator(rand, new int[]{3,4,5,6}, BoundaryConditions.NORMAL);
        testFloatOperator(rand, new int[]{3,4,5,6,7}, BoundaryConditions.PERIODIC);
    }

    /**
     * Create an instance of a finite difference operator.
     *
     * <p> The finite difference operator, computes the difference between
     * each element of its argument and its preceding element for each
     * dimensions.  Thus a 1D array yields a 1D result while a {@code
     * n}-dimensional array yields a {@code n + 1} dimensional result with an
     * additional leading dimension of length {@code n} to store the finite
     * differences along each of the {@code n} dimensions. </p>
     *
     * @param inputSpace
     *        The inputs space of the operator (the output space is
     *        automatically built).
     *
     * @param bounds An array indicating the boundary conditions for each
     *        dimension.  If {@code null}, normal conditions are assumed for
     *        all dimensions; otherwise, if shorter than {@code n}, the rank
     *        of {@code inputSpace}, missing values are assumed to be {@link
     *        BoundaryConditions#NORMAL}.
     */
    public FiniteDifferenceOperator(DoubleShapedVectorSpace inputSpace, BoundaryConditions[] bounds) {
        super(inputSpace, new DoubleShapedVectorSpace(buildShape(inputSpace.getShape())));
        buildIndex(inputSpace.getShape(), bounds);
        single = false;
    }

    /**
     * Create an instance of a finite difference operator.
     *
     * <p> See {@link #FiniteDifferenceOperator(DoubleShapedVectorSpace,
     * BoundaryConditions[])} for more explanations. </p>
     *
     * @param inputSpace
     *        The inputs space of the operator
     *
     * @param bounds
     *        Indicates the boundary conditions for all dimensions, one of
     *        {@link BoundaryConditions#PERIODIC}, or {@link
     *        BoundaryConditions#MIRROR}, otherwise {@link
     *        BoundaryConditions#NORMAL} is assumed.
     */
    public FiniteDifferenceOperator(DoubleShapedVectorSpace inputSpace, BoundaryConditions bounds) {
        super(inputSpace, new DoubleShapedVectorSpace(buildShape(inputSpace.getShape())));
        buildIndex(inputSpace.getShape(), bounds);
        single = false;
    }

    /**
     * Create an instance of a finite difference operator.
     *
     * <p> Same as {@link #FiniteDifferenceOperator(DoubleShapedVectorSpace,
     * BoundaryConditions[])} but with non-periodic conditions along all
     * dimensions. </p>
     *
     * @param inputSpace
     *        The inputs space of the operator
     */
    public FiniteDifferenceOperator(DoubleShapedVectorSpace inputSpace) {
        this(inputSpace, BoundaryConditions.NORMAL);
    }

    /**
     * Create an instance of a finite difference operator.
     *
     * <p> Same as {@link #FiniteDifferenceOperator(DoubleShapedVectorSpace,
     * BoundaryConditions[])} but for single precision floating point shaped
     * vectors. </p>
     *
     * @param inputSpace
     *        The inputs space of the operator
     *
     * @param bounds
     *        An array of integers indicating the boundary conditions for each
     *        dimension.  If {@code null}, normal conditions are assumed for
     *        all dimensions; otherwise, if shorter than {@code n}, the rank
     *        of {@code inputSpace}, missing values are assumed to be {@link
     *        BoundaryConditions#NORMAL}.
     */
    public FiniteDifferenceOperator(FloatShapedVectorSpace inputSpace,
            BoundaryConditions[] bounds) {
        super(inputSpace, new FloatShapedVectorSpace(buildShape(inputSpace.getShape())));
        buildIndex(inputSpace.getShape(), bounds);
        single = true;
    }

    /**
     * Create an instance of a finite difference operator.
     *
     * <p> Same as {@link #FiniteDifferenceOperator(DoubleShapedVectorSpace,
     * BoundaryConditions)} but for single precision floating point shaped
     * vectors. </p>
     *
     * @param inputSpace
     *        The inputs space of the operator.
     *
     * @param bounds
     *        Indicates the boundary conditions for all dimensions, one of
     *        {@link BoundaryConditions#PERIODIC}, or {@link
     *        BoundaryConditions#MIRROR}, otherwise {@link
     *        BoundaryConditions#NORMAL} is assumed.
     */
    public FiniteDifferenceOperator(FloatShapedVectorSpace inputSpace,
            BoundaryConditions bounds) {
        super(inputSpace, new FloatShapedVectorSpace(buildShape(inputSpace.getShape())));
        buildIndex(inputSpace.getShape(), bounds);
        single = true;
    }

    /**
     * Create an instance of a finite difference operator.
     *
     * <p> Same as {@link #FiniteDifferenceOperator(DoubleShapedVectorSpace)}
     * but for single precision floating point shaped vectors. </p>
     *
     * @param inputSpace
     *        The inputs space of the operator.
     */
    public FiniteDifferenceOperator(FloatShapedVectorSpace inputSpace) {
        this(inputSpace, BoundaryConditions.NORMAL);
    }

    private static Shape buildShape(Shape inputShape) {
        int rank = inputShape.rank();
        if (rank == 1) {
            return inputShape;
        } else {
            int[] outDims = new int[rank + 1];
            outDims[0] = rank;
            for (int k = 0; k < rank; ++k) {
                outDims[k + 1] = inputShape.dimension(k);
            }
            return new Shape(outDims);
        }
    }

    private BoundaryConditions getBoundaryConditions(BoundaryConditions[] arr, int k) {
        return ((arr == null || k < 0 || k >= arr.length) ? BoundaryConditions.NORMAL : arr[k]);
    }

    private void buildIndex(Shape inputShape, BoundaryConditions[] bounds) {
        int rank = inputShape.rank();
        index = new int[rank][];
        for (int k =0; k < rank; ++k) {
            index[k] = BoundaryConditions.buildIndex(inputShape.dimension(k),
                    -1, getBoundaryConditions(bounds, k));
        }
    }

    private void buildIndex(Shape inputShape, BoundaryConditions bounds) {
        int rank = inputShape.rank();
        index = new int[rank][];
        for (int k =0; k < rank; ++k) {
            index[k] = BoundaryConditions.buildIndex(inputShape.dimension(k),
                    -1, bounds);
        }
    }

    private double[] getDoubleData(Vector v) {
        return ((DoubleShapedVector)v).getData();
    }
    private float[] getFloatData(Vector v) {
        return ((FloatShapedVector)v).getData();
    }

    @Override
    protected void _apply(Vector dst, Vector src, int job)
            throws IncorrectSpaceException {
        boolean transpose;
        if (job == ADJOINT) {
            dst.zero();
            transpose = true;
        } else if (job == DIRECT) {
            transpose = false;
        } else {
            throw new IllegalArgumentException("illegal job");
        }
        int rank = index.length;
        if (rank == 1) {
            if (single) {
                apply1D(getFloatData(src), getFloatData(dst), transpose);
            } else {
                apply1D(getDoubleData(src), getDoubleData(dst), transpose);
            }
        } else if (rank == 2) {
            if (single) {
                apply2D(getFloatData(src), getFloatData(dst), transpose);
            } else {
                apply2D(getDoubleData(src), getDoubleData(dst), transpose);
            }
        } else if (rank == 3) {
            if (single) {
                apply3D(getFloatData(src), getFloatData(dst), transpose);
            } else {
                apply3D(getDoubleData(src), getDoubleData(dst), transpose);
            }
        } else if (rank == 4) {
            if (single) {
                apply4D(getFloatData(src), getFloatData(dst), transpose);
            } else {
                apply4D(getDoubleData(src), getDoubleData(dst), transpose);
            }
        } else if (rank == 5) {
            if (single) {
                apply5D(getFloatData(src), getFloatData(dst), transpose);
            } else {
                apply5D(getDoubleData(src), getDoubleData(dst), transpose);
            }
        } else {
            throw new IllegalArgumentException("too many dimensions");
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * */
    /* DOUBLE PRECISION VERSION OF THE OPERATOR  */
    /* * * * * * * * * * * * * * * * * * * * * * */

    private final void apply1D(double[] x, double[] y, boolean transpose) {
        int[] prev1 = index[0]; // index to previous position along 1st dimension
        int n1 = prev1.length;  // length of 1st dimension
        if (transpose) {
            for (int i1 = 0; i1 < n1; ++i1) {
                double x1 = x[i1];
                y[i1] += x1;
                y[prev1[i1]] -= x1;
            }
        } else {
            for (int i1 = 0; i1 < n1; ++i1) {
                y[i1] = x[i1] - x[prev1[i1]];
            }
        }
    }

    private final void apply2D(double[] x, double[] y, boolean transpose) {
        int[] prev1 = index[0]; // index to previous position along 1st dimension
        int n1 = prev1.length;  // length of 1st dimension
        int[] prev2 = index[1]; // index to previous position along 2nd dimension
        int n2 = prev2.length;  // length of 2nd dimension
        if (transpose) {
            for (int i2 = 0; i2 < n2; ++i2) {
                int j2 = n1*i2;
                int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                for (int i1 = 0; i1 < n1; ++i1) {
                    int j1 = i1 + j2;
                    int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                    int l1 = 2*j1;
                    double x1 = x[l1], x2 = x[l1 + 1];
                    y[j1] += x1 + x2;
                    y[j1 + k1] -= x1;
                    y[j1 + k2] -= x2;
                }
            }
        } else {
            for (int i2 = 0; i2 < n2; ++i2) {
                int j2 = n1*i2;
                int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                for (int i1 = 0; i1 < n1; ++i1) {
                    int j1 = i1 + j2;
                    int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                    int l1 = 2*j1;
                    double x_j1 = x[j1];
                    y[l1]     = x_j1 - x[j1 + k1];
                    y[l1 + 1] = x_j1 - x[j1 + k2];
                }
            }
        }
    }

    private final void apply3D(double[] x, double[] y, boolean transpose) {
        int[] prev1 = index[0]; // index to previous position along 1st dimension
        int n1 = prev1.length;  // length of 1st dimension
        int[] prev2 = index[1]; // index to previous position along 2nd dimension
        int n2 = prev2.length;  // length of 2nd dimension
        int[] prev3 = index[2]; // index to previous position along 3rd dimension
        int n3 = prev3.length;  // length of 3rd dimension
        int n1n2 = n1*n2;       // stride along 3rd dimension
        if (transpose) {
            for (int i3 = 0; i3 < n3; ++i3) {
                int j3 = n1n2*i3;
                int k3 = n1n2*(prev3[i3] - i3); // offset to previous element along 3rd dimension
                for (int i2 = 0; i2 < n2; ++i2) {
                    int j2 = n1*i2 + j3;
                    int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                    for (int i1 = 0; i1 < n1; ++i1) {
                        int j1 = i1 + j2;
                        int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                        int l1 = 3*j1;
                        double x1 = x[l1], x2 = x[l1 + 1], x3 = x[l1 + 2];
                        y[j1] += x1 + x2 + x3;
                        y[j1 + k1] -= x1;
                        y[j1 + k2] -= x2;
                        y[j1 + k3] -= x3;
                    }
                }
            }
        } else {
            for (int i3 = 0; i3 < n3; ++i3) {
                int j3 = n1n2*i3;
                int k3 = n1n2*(prev3[i3] - i3); // offset to previous element along 3rd dimension
                for (int i2 = 0; i2 < n2; ++i2) {
                    int j2 = n1*i2 + j3;
                    int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                    for (int i1 = 0; i1 < n1; ++i1) {
                        int j1 = i1 + j2;
                        int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                        int l1 = 3*j1;
                        double x_j1 = x[j1];
                        y[l1]     = x_j1 - x[j1 + k1];
                        y[l1 + 1] = x_j1 - x[j1 + k2];
                        y[l1 + 2] = x_j1 - x[j1 + k3];
                    }
                }
            }
        }
    }

    private final void apply4D(double[] x, double[] y, boolean transpose) {
        int[] prev1 = index[0]; // index to previous position along 1st dimension
        int n1 = prev1.length;  // length of 1st dimension
        int[] prev2 = index[1]; // index to previous position along 2nd dimension
        int n2 = prev2.length;  // length of 2nd dimension
        int[] prev3 = index[2]; // index to previous position along 3rd dimension
        int n3 = prev3.length;  // length of 3rd dimension
        int n1n2 = n1*n2;       // stride along 3rd dimension
        int[] prev4 = index[3]; // index to previous position along 4th dimension
        int n4 = prev4.length;  // length of 4th dimension
        int n1n2n3 = n1n2*n3;   // stride along 4th dimension
        if (transpose) {
            for (int i4 = 0; i4 < n4; ++i4) {
                int j4 = n1n2n3*i4;
                int k4 = n1n2n3*(prev4[i4] - i4); // offset to previous element along 4th dimension
                for (int i3 = 0; i3 < n3; ++i3) {
                    int j3 = n1n2*i3 + j4;
                    int k3 = n1n2*(prev3[i3] - i3); // offset to previous element along 3rd dimension
                    for (int i2 = 0; i2 < n2; ++i2) {
                        int j2 = n1*i2 + j3;
                        int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                        for (int i1 = 0; i1 < n1; ++i1) {
                            int j1 = i1 + j2;
                            int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                            int l1 = 4*j1;
                            double x1 = x[l1], x2 = x[l1 + 1], x3 = x[l1 + 2], x4 = x[l1 + 3];
                            y[j1] += x1 + x2 + x3 + x4;
                            y[j1 + k1] -= x1;
                            y[j1 + k2] -= x2;
                            y[j1 + k3] -= x3;
                            y[j1 + k4] -= x4;
                        }
                    }
                }
            }
        } else {
            for (int i4 = 0; i4 < n4; ++i4) {
                int j4 = n1n2n3*i4;
                int k4 = n1n2n3*(prev4[i4] - i4); // offset to previous element along 4th dimension
                for (int i3 = 0; i3 < n3; ++i3) {
                    int j3 = n1n2*i3 + j4;
                    int k3 = n1n2*(prev3[i3] - i3); // offset to previous element along 3th dimension
                    for (int i2 = 0; i2 < n2; ++i2) {
                        int j2 = n1*i2 + j3;
                        int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                        for (int i1 = 0; i1 < n1; ++i1) {
                            int j1 = i1 + j2;
                            int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                            int l1 = 4*j1;
                            double x_j1 = x[j1];
                            y[l1]     = x_j1 - x[j1 + k1];
                            y[l1 + 1] = x_j1 - x[j1 + k2];
                            y[l1 + 2] = x_j1 - x[j1 + k3];
                            y[l1 + 3] = x_j1 - x[j1 + k4];
                        }
                    }
                }
            }
        }
    }

    private final void apply5D(double[] x, double[] y, boolean transpose) {
        int[] prev1 = index[0];   // index to previous position along 1st dimension
        int n1 = prev1.length;    // length of 1st dimension
        int[] prev2 = index[1];   // index to previous position along 2nd dimension
        int n2 = prev2.length;    // length of 2nd dimension
        int[] prev3 = index[2];   // index to previous position along 3rd dimension
        int n3 = prev3.length;    // length of 3rd dimension
        int n1n2 = n1*n2;         // stride along 3rd dimension
        int[] prev4 = index[3];   // index to previous position along 4th dimension
        int n4 = prev4.length;    // length of 4th dimension
        int n1n2n3 = n1n2*n3;     // stride along 4th dimension
        int[] prev5 = index[4];   // index to previous position along 5th dimension
        int n5 = prev5.length;    // length of 5th dimension
        int n1n2n3n4 = n1n2n3*n4; // stride along 5th dimension
        if (transpose) {
            for (int i5 = 0; i5 < n5; ++i5) {
                int j5 = n1n2n3n4*i5;
                int k5 = n1n2n3n4*(prev5[i5] - i5); // offset to previous element along 5th dimension
                for (int i4 = 0; i4 < n4; ++i4) {
                    int j4 = n1n2n3*i4 + j5;
                    int k4 = n1n2n3*(prev4[i4] - i4); // offset to previous element along 4th dimension
                    for (int i3 = 0; i3 < n3; ++i3) {
                        int j3 = n1n2*i3 + j4;
                        int k3 = n1n2*(prev3[i3] - i3); // offset to previous element along 3rd dimension
                        for (int i2 = 0; i2 < n2; ++i2) {
                            int j2 = n1*i2 + j3;
                            int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                            for (int i1 = 0; i1 < n1; ++i1) {
                                int j1 = i1 + j2;
                                int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                                int l1 = 5*j1;
                                double x1 = x[l1], x2 = x[l1 + 1], x3 = x[l1 + 2], x4 = x[l1 + 3], x5 = x[l1 + 4];
                                y[j1] += x1 + x2 + x3 + x4 + x5;
                                y[j1 + k1] -= x1;
                                y[j1 + k2] -= x2;
                                y[j1 + k3] -= x3;
                                y[j1 + k4] -= x4;
                                y[j1 + k5] -= x5;
                            }
                        }
                    }
                }
            }
        } else {
            for (int i5 = 0; i5 < n5; ++i5) {
                int j5 = n1n2n3n4*i5;
                int k5 = n1n2n3n4*(prev5[i5] - i5); // offset to previous element along 5th dimension
                for (int i4 = 0; i4 < n4; ++i4) {
                    int j4 = n1n2n3*i4 + j5;
                    int k4 = n1n2n3*(prev4[i4] - i4); // offset to previous element along 4th dimension
                    for (int i3 = 0; i3 < n3; ++i3) {
                        int j3 = n1n2*i3 + j4;
                        int k3 = n1n2*(prev3[i3] - i3); // offset to previous element along 3th dimension
                        for (int i2 = 0; i2 < n2; ++i2) {
                            int j2 = n1*i2 + j3;
                            int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                            for (int i1 = 0; i1 < n1; ++i1) {
                                int j1 = i1 + j2;
                                int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                                int l1 = 5*j1;
                                double x_j1 = x[j1];
                                y[l1]     = x_j1 - x[j1 + k1];
                                y[l1 + 1] = x_j1 - x[j1 + k2];
                                y[l1 + 2] = x_j1 - x[j1 + k3];
                                y[l1 + 3] = x_j1 - x[j1 + k4];
                                y[l1 + 4] = x_j1 - x[j1 + k5];
                            }
                        }
                    }
                }
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * */
    /* SINGLE PRECISION VERSION OF THE OPERATOR  */
    /* * * * * * * * * * * * * * * * * * * * * * */

    private final void apply1D(float[] x, float[] y, boolean transpose) {
        int[] prev1 = index[0]; // index to previous position along 1st dimension
        int n1 = prev1.length;  // length of 1st dimension
        if (transpose) {
            for (int i1 = 0; i1 < n1; ++i1) {
                float x1 = x[i1];
                y[i1] += x1;
                y[prev1[i1]] -= x1;
            }
        } else {
            for (int i1 = 0; i1 < n1; ++i1) {
                y[i1] = x[i1] - x[prev1[i1]];
            }
        }
    }

    private final void apply2D(float[] x, float[] y, boolean transpose) {
        int[] prev1 = index[0]; // index to previous position along 1st dimension
        int n1 = prev1.length;  // length of 1st dimension
        int[] prev2 = index[1]; // index to previous position along 2nd dimension
        int n2 = prev2.length;  // length of 2nd dimension
        if (transpose) {
            for (int i2 = 0; i2 < n2; ++i2) {
                int j2 = n1*i2;
                int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                for (int i1 = 0; i1 < n1; ++i1) {
                    int j1 = i1 + j2;
                    int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                    int l1 = 2*j1;
                    float x1 = x[l1], x2 = x[l1 + 1];
                    y[j1] += x1 + x2;
                    y[j1 + k1] -= x1;
                    y[j1 + k2] -= x2;
                }
            }
        } else {
            for (int i2 = 0; i2 < n2; ++i2) {
                int j2 = n1*i2;
                int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                for (int i1 = 0; i1 < n1; ++i1) {
                    int j1 = i1 + j2;
                    int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                    int l1 = 2*j1;
                    float x_j1 = x[j1];
                    y[l1]     = x_j1 - x[j1 + k1];
                    y[l1 + 1] = x_j1 - x[j1 + k2];
                }
            }
        }
    }

    private final void apply3D(float[] x, float[] y, boolean transpose) {
        int[] prev1 = index[0]; // index to previous position along 1st dimension
        int n1 = prev1.length;  // length of 1st dimension
        int[] prev2 = index[1]; // index to previous position along 2nd dimension
        int n2 = prev2.length;  // length of 2nd dimension
        int[] prev3 = index[2]; // index to previous position along 3rd dimension
        int n3 = prev3.length;  // length of 3rd dimension
        int n1n2 = n1*n2;       // stride along 3rd dimension
        if (transpose) {
            for (int i3 = 0; i3 < n3; ++i3) {
                int j3 = n1n2*i3;
                int k3 = n1n2*(prev3[i3] - i3); // offset to previous element along 3rd dimension
                for (int i2 = 0; i2 < n2; ++i2) {
                    int j2 = n1*i2 + j3;
                    int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                    for (int i1 = 0; i1 < n1; ++i1) {
                        int j1 = i1 + j2;
                        int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                        int l1 = 3*j1;
                        float x1 = x[l1], x2 = x[l1 + 1], x3 = x[l1 + 2];
                        y[j1] += x1 + x2 + x3;
                        y[j1 + k1] -= x1;
                        y[j1 + k2] -= x2;
                        y[j1 + k3] -= x3;
                    }
                }
            }
        } else {
            for (int i3 = 0; i3 < n3; ++i3) {
                int j3 = n1n2*i3;
                int k3 = n1n2*(prev3[i3] - i3); // offset to previous element along 3rd dimension
                for (int i2 = 0; i2 < n2; ++i2) {
                    int j2 = n1*i2 + j3;
                    int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                    for (int i1 = 0; i1 < n1; ++i1) {
                        int j1 = i1 + j2;
                        int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                        int l1 = 3*j1;
                        float x_j1 = x[j1];
                        y[l1]     = x_j1 - x[j1 + k1];
                        y[l1 + 1] = x_j1 - x[j1 + k2];
                        y[l1 + 2] = x_j1 - x[j1 + k3];
                    }
                }
            }
        }
    }

    private final void apply4D(float[] x, float[] y, boolean transpose) {
        int[] prev1 = index[0]; // index to previous position along 1st dimension
        int n1 = prev1.length;  // length of 1st dimension
        int[] prev2 = index[1]; // index to previous position along 2nd dimension
        int n2 = prev2.length;  // length of 2nd dimension
        int[] prev3 = index[2]; // index to previous position along 3rd dimension
        int n3 = prev3.length;  // length of 3rd dimension
        int n1n2 = n1*n2;       // stride along 3rd dimension
        int[] prev4 = index[3]; // index to previous position along 4th dimension
        int n4 = prev4.length;  // length of 4th dimension
        int n1n2n3 = n1n2*n3;   // stride along 4th dimension
        if (transpose) {
            for (int i4 = 0; i4 < n4; ++i4) {
                int j4 = n1n2n3*i4;
                int k4 = n1n2n3*(prev4[i4] - i4); // offset to previous element along 4th dimension
                for (int i3 = 0; i3 < n3; ++i3) {
                    int j3 = n1n2*i3 + j4;
                    int k3 = n1n2*(prev3[i3] - i3); // offset to previous element along 3rd dimension
                    for (int i2 = 0; i2 < n2; ++i2) {
                        int j2 = n1*i2 + j3;
                        int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                        for (int i1 = 0; i1 < n1; ++i1) {
                            int j1 = i1 + j2;
                            int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                            int l1 = 4*j1;
                            float x1 = x[l1], x2 = x[l1 + 1], x3 = x[l1 + 2], x4 = x[l1 + 3];
                            y[j1] += x1 + x2 + x3 + x4;
                            y[j1 + k1] -= x1;
                            y[j1 + k2] -= x2;
                            y[j1 + k3] -= x3;
                            y[j1 + k4] -= x4;
                        }
                    }
                }
            }
        } else {
            for (int i4 = 0; i4 < n4; ++i4) {
                int j4 = n1n2n3*i4;
                int k4 = n1n2n3*(prev4[i4] - i4); // offset to previous element along 4th dimension
                for (int i3 = 0; i3 < n3; ++i3) {
                    int j3 = n1n2*i3 + j4;
                    int k3 = n1n2*(prev3[i3] - i3); // offset to previous element along 3th dimension
                    for (int i2 = 0; i2 < n2; ++i2) {
                        int j2 = n1*i2 + j3;
                        int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                        for (int i1 = 0; i1 < n1; ++i1) {
                            int j1 = i1 + j2;
                            int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                            int l1 = 4*j1;
                            float x_j1 = x[j1];
                            y[l1]     = x_j1 - x[j1 + k1];
                            y[l1 + 1] = x_j1 - x[j1 + k2];
                            y[l1 + 2] = x_j1 - x[j1 + k3];
                            y[l1 + 3] = x_j1 - x[j1 + k4];
                        }
                    }
                }
            }
        }
    }

    private final void apply5D(float[] x, float[] y, boolean transpose) {
        int[] prev1 = index[0];   // index to previous position along 1st dimension
        int n1 = prev1.length;    // length of 1st dimension
        int[] prev2 = index[1];   // index to previous position along 2nd dimension
        int n2 = prev2.length;    // length of 2nd dimension
        int[] prev3 = index[2];   // index to previous position along 3rd dimension
        int n3 = prev3.length;    // length of 3rd dimension
        int n1n2 = n1*n2;         // stride along 3rd dimension
        int[] prev4 = index[3];   // index to previous position along 4th dimension
        int n4 = prev4.length;    // length of 4th dimension
        int n1n2n3 = n1n2*n3;     // stride along 4th dimension
        int[] prev5 = index[4];   // index to previous position along 5th dimension
        int n5 = prev5.length;    // length of 5th dimension
        int n1n2n3n4 = n1n2n3*n4; // stride along 5th dimension
        if (transpose) {
            for (int i5 = 0; i5 < n5; ++i5) {
                int j5 = n1n2n3n4*i5;
                int k5 = n1n2n3n4*(prev5[i5] - i5); // offset to previous element along 5th dimension
                for (int i4 = 0; i4 < n4; ++i4) {
                    int j4 = n1n2n3*i4 + j5;
                    int k4 = n1n2n3*(prev4[i4] - i4); // offset to previous element along 4th dimension
                    for (int i3 = 0; i3 < n3; ++i3) {
                        int j3 = n1n2*i3 + j4;
                        int k3 = n1n2*(prev3[i3] - i3); // offset to previous element along 3rd dimension
                        for (int i2 = 0; i2 < n2; ++i2) {
                            int j2 = n1*i2 + j3;
                            int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                            for (int i1 = 0; i1 < n1; ++i1) {
                                int j1 = i1 + j2;
                                int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                                int l1 = 5*j1;
                                float x1 = x[l1], x2 = x[l1 + 1], x3 = x[l1 + 2], x4 = x[l1 + 3], x5 = x[l1 + 4];
                                y[j1] += x1 + x2 + x3 + x4 + x5;
                                y[j1 + k1] -= x1;
                                y[j1 + k2] -= x2;
                                y[j1 + k3] -= x3;
                                y[j1 + k4] -= x4;
                                y[j1 + k5] -= x5;
                            }
                        }
                    }
                }
            }
        } else {
            for (int i5 = 0; i5 < n5; ++i5) {
                int j5 = n1n2n3n4*i5;
                int k5 = n1n2n3n4*(prev5[i5] - i5); // offset to previous element along 5th dimension
                for (int i4 = 0; i4 < n4; ++i4) {
                    int j4 = n1n2n3*i4 + j5;
                    int k4 = n1n2n3*(prev4[i4] - i4); // offset to previous element along 4th dimension
                    for (int i3 = 0; i3 < n3; ++i3) {
                        int j3 = n1n2*i3 + j4;
                        int k3 = n1n2*(prev3[i3] - i3); // offset to previous element along 3th dimension
                        for (int i2 = 0; i2 < n2; ++i2) {
                            int j2 = n1*i2 + j3;
                            int k2 = n1*(prev2[i2] - i2); // offset to previous element along 2nd dimension
                            for (int i1 = 0; i1 < n1; ++i1) {
                                int j1 = i1 + j2;
                                int k1 = prev1[i1] - i1; // offset to previous element along 1st dimension
                                int l1 = 5*j1;
                                float x_j1 = x[j1];
                                y[l1]     = x_j1 - x[j1 + k1];
                                y[l1 + 1] = x_j1 - x[j1 + k2];
                                y[l1 + 2] = x_j1 - x[j1 + k3];
                                y[l1 + 3] = x_j1 - x[j1 + k4];
                                y[l1 + 4] = x_j1 - x[j1 + k5];
                            }
                        }
                    }
                }
            }
        }
    }

}
