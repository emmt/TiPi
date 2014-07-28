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

import mitiv.base.ArrayDescriptor;
import mitiv.base.Shaped;
import mitiv.base.Typed;
import mitiv.linalg.VectorSpace;

public abstract class ShapedVectorSpace extends VectorSpace implements Shaped, Typed {
    final ArrayDescriptor descr;

    ShapedVectorSpace(ArrayDescriptor descr) {
        super(descr.getNumber());
        this.descr = descr;
    }

    ShapedVectorSpace(int type, int[] shape, boolean copyShape) {
        this(new ArrayDescriptor(type, shape, copyShape));
    }

    ShapedVectorSpace(int type, int[] shape) {
        this(type, shape, true);
    }

    ShapedVectorSpace(int type, int dim1) {
        this(type, new int[]{dim1}, false);
    }

    ShapedVectorSpace(int type, int dim1, int dim2) {
        this(type, new int[]{dim1, dim2}, false);
    }

    ShapedVectorSpace(int type, int dim1, int dim2, int dim3) {
        this(type, new int[]{dim1, dim2, dim3}, false);
    }

    ShapedVectorSpace(int type, int dim1, int dim2, int dim3, int dim4) {
        this(type, new int[]{dim1, dim2, dim3, dim4}, false);
    }

    @Override
    public final int getType() {
        return descr.getType();
    }

    @Override
    public final int getRank() {
        return descr.getRank();
    }

    @Override
    public final int getOrder() {
        return descr.getOrder();
    }

    @Override
    public final int[] cloneShape() {
        return descr.cloneShape();
    }

    @Override
    public final int getDimension(int k) {
        return descr.getDimension(k);
    }

    /*
    @Override
    public Vector create() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector create(double alpha) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected double _dot(Vector x, Vector y) throws IncorrectSpaceException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected double _norm1(Vector x) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected double _normInf(Vector x) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void _axpby(double alpha, Vector x, double beta, Vector y,
            Vector dst) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void _axpbypcz(double alpha, Vector x, double beta, Vector y,
            double gamma, Vector z, Vector dst) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void _fill(Vector x, double alpha) {
        // TODO Auto-generated method stub

    }
     */
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
