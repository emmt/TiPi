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

import mitiv.array.FloatArray;
import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.linalg.ArrayOps;
import mitiv.linalg.Vector;

/**
 * Class vector spaces which own instances of the FloatVector class.
 *
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 *
 */
public class FloatShapedVectorSpace extends ShapedVectorSpace {

    public FloatShapedVectorSpace(Shape shape) {
        super(Traits.FLOAT, shape);
    }

    public FloatShapedVectorSpace(int[] dims) {
        super(Traits.FLOAT, dims);
    }

    public FloatShapedVectorSpace(int dim1) {
        super(Traits.FLOAT, dim1);
    }

    public FloatShapedVectorSpace(int dim1, int dim2) {
        super(Traits.FLOAT, dim1, dim2);
    }

    public FloatShapedVectorSpace(int dim1, int dim2, int dim3) {
        super(Traits.FLOAT, dim1, dim2, dim3);
    }

    public FloatShapedVectorSpace(int dim1, int dim2, int dim3, int dim4) {
        super(Traits.FLOAT, dim1, dim2, dim3, dim4);
    }

    private float[] getData(Vector v)
    {
        return ((FloatShapedVector) v).getData();
    }

    @Override
    public FloatShapedVector create() {
        return new FloatShapedVector(this);
    }

    @Override
    public FloatShapedVector create(double value) {
        FloatShapedVector v = new FloatShapedVector(this);
        ArrayOps.fill(v.getData(), number, value);
        return v;
    }

    @Override
    public FloatShapedVector create(ShapedArray arr) {
        return create(arr, false);
    }

    @Override
    public FloatShapedVector create(ShapedArray arr, boolean forceCopy) {
        /* Verify shape, then convert to correct data type and avoid forcing a
         * copy if conversion yields a different array. */
        checkShape(arr);
        FloatArray tmp = arr.toFloat();
        return new FloatShapedVector(this, tmp.flatten(forceCopy && tmp == arr));
    }

    /**
     * Create a new vector initialized with the contents of an array.
     *
     * <p> This is a variant of {@link #create(ShapedArray)} for an array of
     * known data type. </p>
     *
     * @param arr   A shaped array with elements of type {@code float}.
     *
     * @return A new FloatShapedVector.
     */
    public FloatShapedVector create(FloatArray arr) {
        return create(arr, false);
    }

    /**
     * Create a new vector initialized with the contents of an array.
     *
     * <p> This is a variant of {@link #create(ShapedArray, boolean)} for
     * an array of known data type. </p>
     *
     * @param arr         A shaped array with elements of type {@code float}.
     * @param forceCopy   A flag to force a copy of the contents if true.
     *
     * @return A new FloatShapedVector.
     */
    public FloatShapedVector create(FloatArray arr, boolean forceCopy) {
        checkShape(arr);
        return new FloatShapedVector(this, arr.flatten(forceCopy));
    }

    public FloatShapedVector clone(FloatShapedVector vec) {
        check(vec);
        return _clone(vec);
    }

    protected FloatShapedVector _clone(FloatShapedVector vec) {
        FloatShapedVector cpy = new FloatShapedVector(this);
        _copy(cpy, vec);
        return cpy;
    }

    @Override
    protected FloatShapedVector _clone(Vector vec) {
        return _clone((FloatShapedVector)vec);
    }

    public FloatShapedVector wrap(float[] x) {
        return new FloatShapedVector(this, x);
    }

    public void copy(Vector dst, float[] src) {
        check(dst);
        ArrayOps.copy(getData(dst), src);
    }

    @Override
    protected void _copy(Vector dst, Vector src) {
        if (dst != src) {
            System.arraycopy(getData(src), 0, getData(dst), 0, number);
        }
    }

    @Override
    protected void _swap(Vector vx, Vector vy) {
        float[] x = getData(vx);
        float[] y = getData(vy);
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            float xi = x[i];
            x[i] = y[i];
            y[i] = xi;
        }
    }

    @Override
    protected void _fill(Vector vec, double alpha) {
        float[] x = getData(vec);
        float a = (float)alpha;
        for (int i = 0; i < number; ++i) {
            x[i] = a;
        }
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
    protected void _scale(Vector vec, double alpha)
    {
        if (alpha == 0.0) {
            _fill(vec, 0.0);
        } else if (alpha != 1.0) {
            float[] x = getData(vec);
            float a = (float)alpha;
            for (int i = 0; i < number; ++i) {
                x[i] *= a;
            }
        }
    }

    @Override
    protected void _scale(Vector dst, double alpha, Vector src)
    {
        if (alpha == 0.0) {
            _fill(dst, 0.0);
        } else if (alpha == 1.0) {
            _copy(dst, src);
        } else {
            float[] x = getData(src);
            float[] y = getData(dst);
            float a = (float)alpha;
            for (int i = 0; i < number; ++i) {
                y[i] = a*x[i];
            }
        }
    }

    @Override
    protected void _combine(Vector dst, double alpha,
            final Vector x, double beta, final Vector y) {
        ArrayOps.combine(getData(dst),
                number, alpha,
                getData(x),  beta, getData(y));
    }

    @Override
    protected void _combine(Vector dst, double alpha,
            final Vector x,  double beta,
            final Vector y, double gamma, final Vector z) {
        ArrayOps.combine(getData(dst),
                number, alpha,
                getData(x),  beta,
                getData(y), gamma, getData(z));
    }

    @Override
    protected void _multiply(Vector dst, Vector vx, Vector vy)
    {
        float[] x = getData(vx);
        float[] y = getData(vy);
        float[] z = getData(dst);
        int n = x.length;
        for (int i = 0; i < n; ++i) {
            z[i] = x[i]*y[i];
        }
    }
}
