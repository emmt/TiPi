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

package mitiv.base.view;

public abstract class View1D implements View {
    protected final int n1;
    protected final int s0, s1;
    protected final int order;

    protected View1D(int length, int n1, int s0, int s1) {
        if (n1 < 1) {
            throw new IllegalArgumentException("bad dimension");
        }
        int imin, imax, offset;
        offset = (n1 - 1)*s1;
        if (offset >= 0) {
            imin = s0;
            imax = s0 + offset;
        } else {
            imin = s0 + offset;
            imax = s0;
        }
        if (imin < 0 || imax >= length) {
            throw new IndexOutOfBoundsException("1D view is not within available space");
        }
        this.n1 = n1;
        this.s0 = s0;
        this.s1 = s1;
        this.order = COLUMN_MAJOR;
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public final int getRank() {
        return 1;
    }

    @Override
    public final int[] getShape() {
        return new int[]{n1};
    }

    public final int getDim1() {
        return n1;
    }

    public final int getStride0() {
        return s0;
    }

    public final int getStride1() {
        return s1;
    }

    protected final int index(int i1) {
        return s0 + s1*i1;
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
