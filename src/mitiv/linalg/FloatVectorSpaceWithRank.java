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

import java.util.Arrays;

public class FloatVectorSpaceWithRank extends FloatVectorSpace {
    protected int rank;
    protected int[] shape;

    private FloatVectorSpaceWithRank(int[] shape, boolean copyShape) {
        super(computeSize(shape));
        rank = shape.length;
        if (copyShape) {
            this.shape = new int[rank];
            for (int r = 0; r < rank; ++r) {
                this.shape[r] = shape[r];
            }
        } else {
            this.shape = shape;
        }
    }

    public FloatVectorSpaceWithRank(int[] shape) {
        this(shape, true);
    }

    public FloatVectorSpaceWithRank(int dim1) {
        this(new int[]{dim1}, false);
    }

    public FloatVectorSpaceWithRank(int dim1, int dim2) {
        this(new int[]{dim1, dim2}, false);
    }

    public FloatVectorSpaceWithRank(int dim1, int dim2, int dim3) {
        this(new int[]{dim1, dim2, dim3}, false);
    }

    /**
     * Get the shape of the vectors of this vector space.
     * @return A copy (you can change the values) of the shape.
     */
    public int[] getShape() {
        return Arrays.copyOf(shape, shape.length);
    }
    public int getRank() {
        return rank;
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