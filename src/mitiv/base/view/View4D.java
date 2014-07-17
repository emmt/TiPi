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

public abstract class View4D implements View {
    protected final int n1, n2, n3, n4;
    protected final int s0, s1, s2, s3, s4;
    protected final int order;

    protected View4D(int length, int n1, int n2, int n3, int n4,
            int s0, int s1, int s2, int s3, int s4) {
        if (n1 < 1 || n2 < 1 || n3 < 1 || n4 < 1) {
            throw new IllegalArgumentException("bad dimension(s)");
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
        offset = (n2 - 1)*s2;
        if (offset >= 0) {
            imax += offset;
        } else {
            imin += offset;
        }
        offset = (n3 - 1)*s3;
        if (offset >= 0) {
            imax += offset;
        } else {
            imin += offset;
        }
        offset = (n4 - 1)*s4;
        if (offset >= 0) {
            imax += offset;
        } else {
            imin += offset;
        }
        if (imin < 0 || imax >= length) {
            throw new IndexOutOfBoundsException("4D view is not within available space");
        }
        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;
        this.n4 = n4;
        this.s0 = s0;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
        int abs_s1 = Math.abs(s1);
        int abs_s2 = Math.abs(s2);
        int abs_s3 = Math.abs(s3);
        int abs_s4 = Math.abs(s4);
        if (abs_s1 <= abs_s2 && abs_s2 <= abs_s3 && abs_s3 <= abs_s4) {
            this.order = COLUMN_MAJOR;
        } else if (abs_s1 >= abs_s2 && abs_s2 >= abs_s3 && abs_s3 >= abs_s4) {
            this.order = ROW_MAJOR;
        } else {
            this.order = NONSPECIFIC_ORDER;
        }
    }

    @Override
    public final int getOrder() {
        return order;
    }

    @Override
    public final int getRank() {
        return 4;
    }

    @Override
    public final int[] getShape() {
        return new int[]{n1, n2, n3, n4};
    }

    public final int getDim1() {
        return n1;
    }

    public final int getDim2() {
        return n2;
    }

    public final int getDim3() {
        return n3;
    }

    public final int getDim4() {
        return n4;
    }

    public final int getStride0() {
        return s0;
    }

    public final int getStride1() {
        return s1;
    }

    public final int getStride2() {
        return s2;
    }

    public final int getStride3() {
        return s3;
    }

    public final int getStride4() {
        return s4;
    }

    protected final int index(int i1, int i2, int i3, int i4) {
        return s0 + s1*i1 + s2*i2 + s3*i3 + s4*i4;
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
