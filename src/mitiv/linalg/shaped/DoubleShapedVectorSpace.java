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

package mitiv.linalg.shaped;

import mitiv.array.DoubleArray;
import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.linalg.ArrayOps;
import mitiv.linalg.Vector;

/**
 * Class vector spaces which own instances of the DoubleVector class.
 *
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 *
 */
public class DoubleShapedVectorSpace extends ShapedVectorSpace {

    public DoubleShapedVectorSpace(Shape shape) {
        super(DOUBLE, shape);
    }

    public DoubleShapedVectorSpace(int[] dims) {
        super(DOUBLE, dims);
    }

    public DoubleShapedVectorSpace(int dim1) {
        super(DOUBLE, dim1);
    }

    public DoubleShapedVectorSpace(int dim1, int dim2) {
        super(DOUBLE, dim1, dim2);
    }

    public DoubleShapedVectorSpace(int dim1, int dim2, int dim3) {
        super(DOUBLE, dim1, dim2, dim3);
    }

    public DoubleShapedVectorSpace(int dim1, int dim2, int dim3, int dim4) {
        super(DOUBLE, dim1, dim2, dim3, dim4);
    }

    private double[] getData(Vector v)
    {
        return ((DoubleShapedVector) v).getData();
    }

    @Override
    public DoubleShapedVector create() {
        return new DoubleShapedVector(this);
    }

    @Override
    public DoubleShapedVector create(double value) {
        DoubleShapedVector v = new DoubleShapedVector(this);
        ArrayOps.fill(number, v.getData(), value);
        return v;
    }

    @Override
    public DoubleShapedVector create(ShapedArray arr) {
        return create(arr, false);
    }

    @Override
    public DoubleShapedVector create(ShapedArray arr, boolean forceCopy) {
        /* Verify shape, then convert to correct data type and avoid forcing a
         * copy if conversion yields a different array. */
        checkShape(arr);
        DoubleArray tmp = arr.toDouble();
        return new DoubleShapedVector(this, tmp.flatten(forceCopy && tmp == arr));
    }

    /**
     * Create a new vector initialized with the contents of an array.
     *
     * <p>
     * This is a variant of {@link #create(ShapedArray)} for an array of
     * known data type.
     * </p>
     * @param arr - A shaped array with elements of type {@code double}.
     * @return A new DoubleShapedVector.
     */
    public DoubleShapedVector create(DoubleArray arr) {
        return create(arr, false);
    }

    /**
     * Create a new vector initialized with the contents of an array.
     *
     * <p>
     * This is a variant of {@link #create(ShapedArray, boolean)} for an array of
     * known data type.
     * </p>
     * @param arr       - A shaped array with elements of type {@code double}.
     * @param forceCopy - A flag to force a copy of the contents if true.
     * @return A new DoubleShapedVector.
     */
    public DoubleShapedVector create(DoubleArray arr, boolean forceCopy) {
        checkShape(arr);
        return new DoubleShapedVector(this, arr.flatten(forceCopy));
    }

    public DoubleShapedVector clone(DoubleShapedVector vec) {
        check(vec);
        return _clone(vec);
    }

    protected DoubleShapedVector _clone(DoubleShapedVector vec) {
        DoubleShapedVector cpy = new DoubleShapedVector(this);
        _copy(vec, cpy);
        return cpy;
    }

    @Override
    public DoubleShapedVector clone(Vector vec) {
        check(vec);
        return _clone(vec);
    }

    @Override
    protected DoubleShapedVector _clone(Vector vec) {
        return _clone((DoubleShapedVector)vec);
    }

    public DoubleShapedVector wrap(double[] x) {
        return new DoubleShapedVector(this, x);
    }

    // FIXME:
    public void copy(double[] src, Vector dst) {
        check(dst);
        ((DoubleShapedVector)dst).set(src);
    }

    protected void _copy(DoubleShapedVector src, DoubleShapedVector dst) {
        ArrayOps.copy(number, src.getData(), dst.getData());
    }

    @Override
    protected void _copy(Vector src, Vector dst) {
        ArrayOps.copy(number, getData(src),  getData(dst));
    }

    @Override
    protected void _swap(Vector vx, Vector vy) {
        double[] x = getData(vx);
        double[] y = getData(vy);
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            double xi = x[i];
            x[i] = y[i];
            y[i] = xi;
        }
    }

    @Override
    protected void _fill(Vector x, double alpha) {
        ArrayOps.fill(number, getData(x), alpha);
    }

    @Override
    protected double _dot(final Vector x, final Vector y) {
        return ArrayOps.dot(number, getData(x), getData(y));
    }

    @Override
    protected double _dot(final Vector w, final Vector x, final Vector y) {
        return ArrayOps.dot(number, getData(w), getData(x), getData(y));
    }

    @Override
    protected double _norm2(Vector x) {
        return ArrayOps.norm2(getData(x));
    }

    @Override
    protected double _norm1(Vector x) {
        return ArrayOps.norm1(getData(x));
    }

    @Override
    protected double _normInf(Vector x) {
        return ArrayOps.normInf(getData(x));
    }

    @Override
    protected void _combine(double alpha, final Vector x,
            double beta, Vector y) {
        ArrayOps.combine(number,
                alpha, getData(x),
                beta,  getData(y));
    }

    @Override
    protected void _combine(double alpha, final Vector x,
            double beta, final Vector y, Vector dst) {
        ArrayOps.combine(number,
                alpha, getData(x),
                beta,  getData(y), getData(dst));
    }

    @Override
    protected void _combine(double alpha, final Vector x,
            double beta,  final Vector y,
            double gamma, final Vector z, Vector dst) {
        ArrayOps.combine(number,
                alpha, getData(x),
                beta,  getData(y),
                gamma, getData(z), getData(dst));
    }

    @Override
    protected void _multiply(Vector vx, Vector vy, Vector dst)
    {
        double[] x = getData(vx);
        double[] y = getData(vy);
        double[] z = getData(dst);
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            z[i] = x[i]*y[i];
        }
    }
}
