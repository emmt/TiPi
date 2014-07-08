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

package mitiv.linalg;


/**
 * Class vector spaces which own instances of the DoubleVector class.
 * 
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 * 
 */
public class DoubleVectorSpace extends VectorSpace {
    public DoubleVectorSpace(int size) {
        super(size, Utils.TYPE_DOUBLE);
    }

    @Override
    public DoubleVector create() {
        return new DoubleVector(this);
    }

    @Override
    public DoubleVector create(double value) {
        DoubleVector v = new DoubleVector(this);
        ArrayOps.fill(size, v.getData(), value);
        return v;
    }

    @Override
    public DoubleVector create(final Vector v) {
        check(v);
        DoubleVector vc = new DoubleVector(this);
        _copy(v, vc);
        return vc;
    }

    public DoubleVector wrap(double[] x) {
        return new DoubleVector(this, x);
    }

    public void copy(final double[] src, Vector dst) {
        check(dst);
        ((DoubleVector)dst).set(src);
    }

    @Override
    protected void _copy(Vector src, Vector dst) {
        ArrayOps.copy(size, ((DoubleVector) src).getData(),
                ((DoubleVector) dst).getData());
    }

    @Override
    protected void _fill(Vector x, double alpha) {
        ArrayOps.fill(size, ((DoubleVector) x).getData(), alpha);
    }

    @Override
    protected double _dot(Vector x, Vector y) {
        return ArrayOps.dot(size, ((DoubleVector) x).getData(),
                ((DoubleVector) y).getData());
    }

    @Override
    protected double _norm2(Vector x) {
        return ArrayOps.norm2(((DoubleVector) x).getData());
    }

    @Override
    protected double _norm1(Vector x) {
        return ArrayOps.norm1(((DoubleVector) x).getData());
    }

    @Override
    protected double _normInf(Vector x) {
        return ArrayOps.normInf(((DoubleVector) x).getData());
    }

    @Override
    protected void _axpby(double alpha, final Vector x,
            double beta, Vector y) {
        ArrayOps.axpby(size,
                alpha, ((DoubleVector) x).getData(),
                beta,  ((DoubleVector) y).getData());
    }

    @Override
    protected void _axpby(double alpha, final Vector x,
            double beta, final Vector y, Vector dst) {
        ArrayOps.axpby(size,
                alpha, ((DoubleVector) x).getData(),
                beta,  ((DoubleVector) y).getData(), ((DoubleVector) dst).getData());
    }

    @Override
    protected void _axpbypcz(double alpha, final Vector x,
            double beta,  final Vector y,
            double gamma, final Vector z, Vector dst) {
        ArrayOps.axpbypcz(size,
                alpha, ((DoubleVector) x).getData(),
                beta,  ((DoubleVector) y).getData(),
                gamma, ((DoubleVector) z).getData(), ((DoubleVector) dst).getData());
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
