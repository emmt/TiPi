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

package mitiv.array;
import mitiv.base.Shape;


/**
 * Define abstract class for scalar arrays of rank 0.
 *
 * @author Éric Thiébaut.
 */
public abstract class Scalar implements ShapedArray {
    static protected final Shape shape = Shape.make();
    static protected final int number = 1;

    /*
     * The following constructors make this class non instantiable, but still
     * let others inherit from this class.
     */
    protected Scalar() {
    }

    protected Scalar(int[] dims) {
        this(Shape.make(dims));
    }

    protected Scalar(Shape shape) {
        if (shape.rank() != 0) {
            throw new IllegalArgumentException("Bad number of dimensions for 0-D array.");
        }
    }

    @Override
    public final int getRank() {
        return 0;
    }

    @Override
    public final Shape getShape() {
        return shape;
    }

    @Override
    public final int getNumber() {
        return number;
    }

    @Override
    public final int getDimension(int k) {
        return shape.dimension(k);
    }

    /**
     * Get a view of the scalar as a 1D array.
     *
     * @return A 1D view of the scalar.
     */
    public abstract Array1D as1D();

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
