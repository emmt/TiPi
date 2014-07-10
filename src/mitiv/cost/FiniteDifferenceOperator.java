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
import mitiv.linalg.DoubleVectorSpaceWithRank;
import mitiv.linalg.DoubleVectorWithRank;
import mitiv.linalg.FloatVectorSpaceWithRank;
import mitiv.linalg.FloatVectorWithRank;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.Utils;
import mitiv.linalg.Vector;
import mitiv.random.DoubleGenerator;
import mitiv.random.FloatGenerator;
import mitiv.random.UniformDistribution;

public class FiniteDifferenceOperator extends LinearOperator {
    private int[][] index;

    private static void testDoubleOperator(DoubleGenerator generator, int[] shape, int condition) {
        DoubleVectorSpaceWithRank inp = new DoubleVectorSpaceWithRank(shape);
        LinearOperator D = new FiniteDifferenceOperator(inp, condition);
        DoubleVectorSpaceWithRank out = (DoubleVectorSpaceWithRank) D.getOutputSpace();
        DoubleVectorWithRank a = inp.create();
        DoubleVectorWithRank b = out.create();
        a.fill(generator);
        b.fill(generator);
        System.out.printf("Testing the adjoint in %dD arrays, shape = [%d", shape.length, shape[0]);
        for (int k = 1; k < shape.length; ++k) System.out.printf(",%d", shape[k]);
        System.out.printf("], of random values:\n");
        System.out.println("  relative error = " + D.checkAdjoint(a, b));
    }

    private static void testFloatOperator(FloatGenerator generator, int[] shape, int condition) {
        FloatVectorSpaceWithRank inp = new FloatVectorSpaceWithRank(shape);
        LinearOperator D = new FiniteDifferenceOperator(inp, condition);
        FloatVectorSpaceWithRank out = (FloatVectorSpaceWithRank) D.getOutputSpace();
        FloatVectorWithRank a = inp.create();
        FloatVectorWithRank b = out.create();
        a.fill(generator);
        b.fill(generator);
        System.out.printf("Testing the adjoint in %dD arrays, shape = [%d", shape.length, shape[0]);
        for (int k = 1; k < shape.length; ++k) System.out.printf(",%d", shape[k]);
        System.out.printf("], of random values:\n");
        System.out.println("  relative error = " + D.checkAdjoint(a, b));
    }

    public static void main(String[] args) {
        DoubleVectorSpaceWithRank inp = new DoubleVectorSpaceWithRank(new int[]{5});
        DoubleVectorWithRank a = inp.create();
        a.set(0,  1.2);
        a.set(1,  2.7);
        a.set(2, -3.1);
        a.set(3,  5.3);
        a.set(4, -9.0);
        LinearOperator dif = new FiniteDifferenceOperator(inp);
        DoubleVectorSpaceWithRank out = (DoubleVectorSpaceWithRank) dif.getOutputSpace();
        DoubleVectorWithRank b = out.create();
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
        dif.apply(a, b);
        DoubleVectorWithRank c = inp.create();
        dif.apply(b, c, LinearOperator.ADJOINT);
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
     * <p>
     * The finite difference operator, computes the difference between each element
     * of its argument and its preceding element for each dimensions.  Thus a 1D array yields
     * a 1D result while a {@code n}-dimensional array yields a {@code n + 1} dimensional
     * result with an additional leading dimension of length {@code n} to store the finite
     * differences along each of the {@code n} dimensions.
     * @param inputSpace - The inputs space of the operator (the output space is automatically
     *                     built).
     * @param bounds     - An array of integers indicating the boundary conditions for
     *                     each dimension.  If {@code null}, normal conditions are assumed for
     *                     all dimensions; otherwise, if shorter than {@code n}, the rank of
     *                     {@code inputSpace}, missing values are assumed to be
     *                     {@link BoundaryConditions#NORMAL}.
     */
    public FiniteDifferenceOperator(DoubleVectorSpaceWithRank inputSpace, int[] bounds) {
        super(inputSpace, new DoubleVectorSpaceWithRank(buildShape(inputSpace.cloneShape())));
        buildIndex(inputSpace.cloneShape(), bounds);
    }

    /**
     * Create an instance of a finite difference operator.
     * <p>
     * See {@link #FiniteDifferenceOperator(DoubleVectorSpaceWithRank, boolean[])} for more
     * explanations.
     * @param inputSpace - The inputs space of the operator
     * @param bounds     - Indicates the boundary conditions for all dimensions, one of
     *                     {@link BoundaryConditions#PERIODIC}, or
     *                     {@link BoundaryConditions#MIRROR}, otherwise
     *                     {@link BoundaryConditions#NORMAL} is assumed.
     */
    public FiniteDifferenceOperator(DoubleVectorSpaceWithRank inputSpace, int bounds) {
        super(inputSpace, new DoubleVectorSpaceWithRank(buildShape(inputSpace.cloneShape())));
        buildIndex(inputSpace.cloneShape(), bounds);
    }

    /**
     * Create an instance of a finite difference operator.
     * <p>
     * Same as {@link #FiniteDifferenceOperator(DoubleVectorSpaceWithRank, boolean[])} but with
     * non-periodic conditions along all dimensions.
     * @param inputSpace - The inputs space of the operator
     */
    public FiniteDifferenceOperator(DoubleVectorSpaceWithRank inputSpace) {
        this(inputSpace, BoundaryConditions.NORMAL);
    }

    /**
     * Create an instance of a finite difference operator.
     * <p>
     * Same as {@link #FiniteDifferenceOperator(DoubleVectorSpaceWithRank, boolean[])} but for
     * single precision floating point shaped vectors.
     * @param inputSpace - The inputs space of the operator
     * @param bounds     - An array of integers indicating the boundary conditions for
     *                     each dimension.  If {@code null}, normal conditions are assumed for
     *                     all dimensions; otherwise, if shorter than {@code n}, the rank of
     *                     {@code inputSpace}, missing values are assumed to be
     *                     {@link BoundaryConditions#NORMAL}.
     */
    public FiniteDifferenceOperator(FloatVectorSpaceWithRank inputSpace, int[] bounds) {
        super(inputSpace, new FloatVectorSpaceWithRank(buildShape(inputSpace.cloneShape())));
        buildIndex(inputSpace.cloneShape(), bounds);
    }

    /**
     * Create an instance of a finite difference operator.
     * <p>
     * Same as {@link #FiniteDifferenceOperator(DoubleVectorSpaceWithRank, boolean)} but for
     * single precision floating point shaped vectors.
     * @param inputSpace - The inputs space of the operator
     * @param bounds     - Indicates the boundary conditions for all dimensions, one of
     *                     {@link BoundaryConditions#PERIODIC}, or
     *                     {@link BoundaryConditions#MIRROR}, otherwise
     *                     {@link BoundaryConditions#NORMAL} is assumed.
     */
    public FiniteDifferenceOperator(FloatVectorSpaceWithRank inputSpace, int bounds) {
        super(inputSpace, new FloatVectorSpaceWithRank(buildShape(inputSpace.cloneShape())));
        buildIndex(inputSpace.cloneShape(), bounds);
    }

    /**
     * Create an instance of a finite difference operator.
     * <p>
     * Same as {@link #FiniteDifferenceOperator(DoubleVectorSpaceWithRank)} but for
     * single precision floating point shaped vectors.
     * @param inputSpace - The inputs space of the operator
     */
    public FiniteDifferenceOperator(FloatVectorSpaceWithRank inputSpace) {
        this(inputSpace, BoundaryConditions.NORMAL);
    }


    private static int[] buildShape(int[] inputShape) {
        int[] outShape;
        int rank = inputShape.length;
        if (rank == 1) {
            outShape = new int[] {inputShape[0]};
        } else {
            outShape = new int[rank + 1];
            outShape[0] = rank;
            for (int k = 0; k < rank; ++k) {
                outShape[k + 1] = inputShape[k];
            }
        }
        return outShape;
    }

    private int getBound(int[] arr, int k) {
        return ((arr == null || k < 0 || k >= arr.length) ? BoundaryConditions.NORMAL : arr[k]);
    }

    private void buildIndex(int[] inputShape, int[] bounds) {
        int rank = inputShape.length;
        index = new int[rank][];
        for (int k =0; k < rank; ++k) {
            index[k] = BoundaryConditions.buildIndex(inputShape[k], -1, getBound(bounds, k));
        }
    }

    private void buildIndex(int[] inputShape, int bounds) {
        int rank = inputShape.length;
        index = new int[rank][];
        for (int k =0; k < rank; ++k) {
            index[k] = BoundaryConditions.buildIndex(inputShape[k], -1, bounds);
        }
    }

    private double[] getDoubleData(Vector v) {
        return ((DoubleVectorWithRank)v).getData();
    }
    private float[] getFloatData(Vector v) {
        return ((FloatVectorWithRank)v).getData();
    }

    @Override
    protected void privApply(Vector src, Vector dst, int job)
            throws IncorrectSpaceException {
        boolean single = (inputSpace.getType() == Utils.TYPE_FLOAT);
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
