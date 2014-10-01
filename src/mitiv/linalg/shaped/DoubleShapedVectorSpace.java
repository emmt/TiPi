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

import mitiv.linalg.ArrayOps;
import mitiv.linalg.Vector;

/**
 * Class vector spaces which own instances of the DoubleVector class.
 * 
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 * 
 */
public class DoubleShapedVectorSpace extends ShapedVectorSpace {

    public DoubleShapedVectorSpace(int[] shape, boolean copyShape) {
        super(DOUBLE, shape, copyShape);
    }

    public DoubleShapedVectorSpace(int[] shape) {
        super(DOUBLE, shape);
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
        _copy((DoubleShapedVector)src, (DoubleShapedVector)dst);
    }

    @Override
    protected void _swap(Vector x, Vector y) {
        _copy((DoubleShapedVector)x, (DoubleShapedVector)y);
    }

    protected void _swap(DoubleShapedVector vx, DoubleShapedVector vy) {
        double[] x = vx.getData();
        double[] y = vy.getData();
        int n = vx.getNumber();
        for (int j = 0; j < n; ++j) {
            double a = x[j];
            x[j] = y[j];
            y[j] = a;
        }
    }

    @Override
    protected void _fill(Vector x, double alpha) {
        ArrayOps.fill(number, ((DoubleShapedVector) x).getData(), alpha);
    }

    @Override
    protected double _dot(final Vector x, final Vector y) {
        return ArrayOps.dot(number, ((DoubleShapedVector) x).getData(),
                ((DoubleShapedVector) y).getData());
    }

    @Override
    protected double _norm2(Vector x) {
        return ArrayOps.norm2(((DoubleShapedVector) x).getData());
    }

    @Override
    protected double _norm1(Vector x) {
        return ArrayOps.norm1(((DoubleShapedVector) x).getData());
    }

    @Override
    protected double _normInf(Vector x) {
        return ArrayOps.normInf(((DoubleShapedVector) x).getData());
    }

    @Override
    protected void _axpby(double alpha, final Vector x,
            double beta, Vector y) {
        ArrayOps.axpby(number,
                alpha, ((DoubleShapedVector) x).getData(),
                beta,  ((DoubleShapedVector) y).getData());
    }

    @Override
    protected void _axpby(double alpha, final Vector x,
            double beta, final Vector y, Vector dst) {
        ArrayOps.axpby(number,
                alpha, ((DoubleShapedVector) x).getData(),
                beta,  ((DoubleShapedVector) y).getData(), ((DoubleShapedVector) dst).getData());
    }

    @Override
    protected void _axpbypcz(double alpha, final Vector x,
            double beta,  final Vector y,
            double gamma, final Vector z, Vector dst) {
        ArrayOps.axpbypcz(number,
                alpha, ((DoubleShapedVector) x).getData(),
                beta,  ((DoubleShapedVector) y).getData(),
                gamma, ((DoubleShapedVector) z).getData(), ((DoubleShapedVector) dst).getData());
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
