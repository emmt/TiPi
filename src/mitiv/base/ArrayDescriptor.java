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

package mitiv.base;

import java.util.Arrays;

public class ArrayDescriptor implements Shaped, Typed {
    final int type;
    final int rank;
    final int[] shape;
    final int number;

    public ArrayDescriptor(int type, int[] shape, boolean copyShape) {
        this.number = computeNumber(shape);
        this.type = type;
        this.rank = shape.length;
        if (copyShape) {
            this.shape = new int[rank];
            for (int r = 0; r < rank; ++r) {
                this.shape[r] = shape[r];
            }
        } else {
            this.shape = shape;
        }
    }

    /**
     * Compute the number of elements from the list of dimensions.
     *
     * This utility function computes the number of elements given a list of
     * dimensions and throws an exception if the list of dimensions is invalid.
     *
     * @param shape  The list of dimensions.
     * @return The product of the dimensions.
     * @throws IllegalArgumentException All dimensions must be greater or equal 1.
     */
    public static int computeNumber(int[] shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Illegal NULL shape.");
        }
        int number = 1;
        for (int r = 0; r < shape.length; ++r) {
            if (shape[r] <= 0) {
                throw new IllegalArgumentException("Bad dimension length.");
            }
            number *= shape[r];
        }
        return number;
    }

    @Override
    public final int getType() {
        return type;
    }

    @Override
    public final int getOrder() {
        return COLUMN_MAJOR;
    }

    @Override
    public final int getRank() {
        return rank;
    }

    @Override
    public final int getNumber() {
        return number;
    }

    @Override
    public final int[] cloneShape() {
        return Arrays.copyOf(shape, shape.length);
    }

    @Override
    public final int getDimension(int k) {
        return (k < rank ? shape[k] : 1);
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
