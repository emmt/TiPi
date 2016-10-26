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

package mitiv.base.indexing;

/**
 * Manage different boundaries conditions.
 *
 * @author Éric Thiébaut.
 */
public enum BoundaryConditions {
    /**
     * Ordinary boundary conditions amounts to propagate leftmost or rightmost
     * values.
     */
    NORMAL(0, "propagate leftmost or rightmost value"),

    /** Periodic boundary conditions. */
    PERIODIC(1, "periodic boundary conditions"),

    /** Mirror boundary conditions. */
    MIRROR(2, "mirror boundary conditions");

    private final int identifier;
    private final String description;
    private BoundaryConditions(int id, String descr) {
        identifier = id;
        description = descr;
    }

    /** Get a unique numerical identifier of the boundary conditions. */
    public int getIdentifier() {
        return identifier;
    }

    /** Get the description of the boundary conditions. */
    public String getDescription() {
        return description;
    }

    /** Get a string representation of the boundary conditions. */
    @Override
    public String toString() {
        return description;
    }

    /**
     * Store index values with given offset and boundary conditions.
     *
     * <p> This static method stores the shifted index: </p>
     *
     * <pre>
     *     index[j] = f(j + offset);
     * </pre>
     *
     * <p> where {@code f(k)} depends on the boundary condition and maps index
     * {@code k} in <tt>&#123;0, 1, ..., N-1&#125;</tt> with {@code N =
     * index.length} is the length of the considered dimension.  </p>
     *
     * @param index
     *        An array to store the result.
     *
     * @param offset
     *        The offset of positions.
     *
     * @param condition
     *        The boundary conditions ({@link #PERIODIC}, or {@link #MIRROR},
     *        otherwise {@link #NORMAL}).
     *
     * @see #buildIndex for examples.
     */
    public static final void buildIndex(int[] index, int offset, BoundaryConditions condition) {
        int n = index.length;
        if (condition == PERIODIC) {
            for (int j = 0; j < n; ++j) {
                int k = j + offset;
                if (k < 0) {
                    k = n + k%n;
                }
                if (k >= n) {
                    k %= n;
                }
                index[j] = k;
            }
        } else if (condition == MIRROR) {
            int p = 2*n, pm1 = p - 1;
            for (int j = 0; j < n; ++j) {
                int k = j + offset;
                if (k < 0) {
                    k = -k;
                }
                if (k >= p) {
                    k %= p;
                }
                if (k >= n) {
                    k = pm1 - k;
                }
                index[j] = k;
            }
        } else {
            for (int j = 0; j < n; ++j) {
                int k = j + offset;
                if (k < 0) {
                    k = 0;
                }
                if (k >= n) {
                    k = n - 1;
                }
                index[j] = k;
            }
        }
    }

    /**
     * Create an array of index values with given offset and boundary
     * conditions.
     *
     * <p> For instance:</p>
     *
     * <pre>
     *     buildIndex(7, -1, NORMAL) -----> {0, 0, 1, 2, 3, 4, 5}
     *     buildIndex(7, -2, PERIODIC) ---> {5, 6, 0, 1, 2, 3, 4}
     *     buildIndex(7, -2, MIRROR) -----> {2, 1, 0, 1, 2, 3, 4}
     * </pre>
     *
     * @param length
     *        The length of the dimension.
     *
     * @param offset
     *        The offset along the dimension.
     *
     * @param condition
     *        The boundary conditions ({@link #PERIODIC}, or {@link #MIRROR},
     *        otherwise {@link #NORMAL}).
     *
     * @return An array of indexes in the set
     *         <tt>&#123;0, 1, ..., length-1&#125;</tt>.
     */
    public static final int[] buildIndex(int length, int offset, BoundaryConditions condition) {
        int[] index = new int[length];
        buildIndex(index, offset, condition);
        return index;
    }

    /**
     * Make a string representation of the vector contents.
     */
    public static String stringify(byte[] arr) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            buf.append(i == 0 ? "{" : ", ");
            buf.append(arr[i]);
        }
        buf.append("}");
        return buf.toString();
    }
    public static String stringify(short[] arr) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            buf.append(i == 0 ? "{" : ", ");
            buf.append(arr[i]);
        }
        buf.append("}");
        return buf.toString();
    }
    public static String stringify(int[] arr) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            buf.append(i == 0 ? "{" : ", ");
            buf.append(arr[i]);
        }
        buf.append("}");
        return buf.toString();
    }
    public static String stringify(long[] arr) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            buf.append(i == 0 ? "{" : ", ");
            buf.append(arr[i]);
        }
        buf.append("}");
        return buf.toString();
    }
    public static String stringify(float[] arr) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            buf.append(i == 0 ? "{" : ", ");
            buf.append(arr[i]);
        }
        buf.append("}");
        return buf.toString();
    }
    public static String stringify(double[] arr) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            buf.append(i == 0 ? "{" : ", ");
            buf.append(arr[i]);
        }
        buf.append("}");
        return buf.toString();
    }

    public static void main(String[] args) {
        int n = 10;
        int[] i1 = buildIndex(n, -1, NORMAL);
        int[] i2 = buildIndex(n, -1, PERIODIC);
        int[] i3 = buildIndex(n, -1, MIRROR);
        System.out.println("i1 = " + stringify(i1));
        System.out.println("i2 = " + stringify(i2));
        System.out.println("i3 = " + stringify(i3));
        System.out.println("i4 = " + stringify(buildIndex(n, -3, MIRROR)));
        System.out.println("i5 = " + stringify(buildIndex(n, -12, PERIODIC)));
        System.out.println("buildIndex(7, -1, NORMAL) -----> " + stringify(buildIndex(7, -1, NORMAL)));
        System.out.println("buildIndex(7, -2, PERIODIC) ---> " + stringify(buildIndex(7, -2, PERIODIC)));
        System.out.println("buildIndex(7, -2, MIRROR) -----> " + stringify(buildIndex(7, -2, MIRROR)));
    }
}
