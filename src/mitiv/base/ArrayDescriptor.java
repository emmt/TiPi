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

public class ArrayDescriptor implements Shaped, Typed {
    final int type;
    final int number;
    final Shape shape;

    /**
     * Create array descriptor.
     * @param type - The element type of the array.
     * @param shape - The dimension list of the array.
     */
    public ArrayDescriptor(int type, int[] shape) {
        this(type, new Shape(shape));
    }

    /**
     * Create array descriptor.
     * @param type - The element type of the array.
     * @param shape - The shape of the array.
     */
    public ArrayDescriptor(int type, Shape shape) {
        this.type = type;
        this.shape = shape;
        if (this.shape.number() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Total number of elements is too large.");
        }
        this.number = (int)this.shape.number();
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
        return shape.rank();
    }

    @Override
    public final int getNumber() {
        return number;
    }

    @Override
    public final int getDimension(int k) {
        return shape.dimension(k);
    }

    @Override
    public final Shape getShape() {
        return shape;
    }

    /**
     * Get the string representation of an array descriptor.
     */
    @Override
    public String toString() {
        return Traits.nameOf(type) + shape.toString();
    }
}
