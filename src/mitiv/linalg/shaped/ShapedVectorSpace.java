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

import mitiv.array.ShapedArray;
import mitiv.base.ArrayDescriptor;
import mitiv.base.Shape;
import mitiv.base.Shaped;
import mitiv.base.Traits;
import mitiv.base.Typed;
import mitiv.exception.NonConformableArrayException;
import mitiv.linalg.VectorSpace;

public abstract class ShapedVectorSpace extends VectorSpace implements Shaped, Typed {
    final ArrayDescriptor descr;

    ShapedVectorSpace(ArrayDescriptor descr) {
        super(descr.getNumber());
        this.descr = descr;
    }

    ShapedVectorSpace(int type, Shape shape) {
        this(new ArrayDescriptor(type, shape));
    }

    ShapedVectorSpace(int type, int[] shape) {
        this(new ArrayDescriptor(type, shape));
    }

    ShapedVectorSpace(int type, int dim1) {
        this(type, Shape.make(dim1));
    }

    ShapedVectorSpace(int type, int dim1, int dim2) {
        this(type, Shape.make(dim1, dim2));
    }

    ShapedVectorSpace(int type, int dim1, int dim2, int dim3) {
        this(type, Shape.make(dim1, dim2, dim3));
    }

    ShapedVectorSpace(int type, int dim1, int dim2, int dim3, int dim4) {
        this(type, Shape.make(dim1, dim2, dim3, dim4));
    }

    ShapedVectorSpace(int type, int dim1, int dim2, int dim3, int dim4, int dim5) {
        this(type, Shape.make(dim1, dim2, dim3, dim4, dim5));
    }

    ShapedVectorSpace(int type, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6) {
        this(type, Shape.make(dim1, dim2, dim3, dim4, dim5, dim6));
    }

    ShapedVectorSpace(int type, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7) {
        this(type, Shape.make(dim1, dim2, dim3, dim4, dim5, dim6, dim7));
    }

    ShapedVectorSpace(int type, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8) {
        this(type, Shape.make(dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8));
    }

    ShapedVectorSpace(int type, int dim1, int dim2, int dim3, int dim4, int dim5, int dim6, int dim7, int dim8, int dim9) {
        this(type, Shape.make(dim1, dim2, dim3, dim4, dim5, dim6, dim7, dim8, dim9));
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
    public final Shape getShape() {
        return descr.getShape();
    }

    @Override
    public final int getDimension(int k) {
        return descr.getDimension(k);
    }

    public ShapedVector clone(ShapedVector vec) {
        check(vec);
        return _clone(vec);
    }

    protected ShapedVector _clone(ShapedVector vec) {
        ShapedVector cpy;
        if (vec.getType() == Traits.DOUBLE) {
            cpy = new DoubleShapedVector((DoubleShapedVectorSpace)vec.getOwner());
        } else {
            cpy = new FloatShapedVector((FloatShapedVectorSpace)vec.getOwner());
        }
        _copy(cpy, vec);
        return cpy;
    }

    /**
     * Make sure the shape of an array matches that of the vectors in this space.
     * @param arr - The ShapedArray to check.
     * @throws NonConformableArrayException
     */
    public void checkShape(ShapedArray arr) {
        final int rank = getRank();
        if (rank != arr.getRank()) {
            throw new NonConformableArrayException("Shaped array rank mismatch.");
        }
        for (int k = 0; k < rank; ++k) {
            if (getDimension(k) != arr.getDimension(k)) {
                throw new NonConformableArrayException("Shaped array dimension mismatch.");
            }
        }
    }

    @Override
    public abstract ShapedVector create();

    /**
     * Create a new vector initialized with the contents of an array.
     *
     * <p>
     * The values of the elements of the returned vector will be copied (or
     * shared, see below) or converted from those of the input array.  Type
     * conversion is automatically performed but the shape of the array must
     * match that of the vectors of this space.
     * </p><p>
     * Depending on the array and on the vector storages, the vector and the
     * array may share the same data but there is no guarantees for that.  To
     * save memory, the default behavior is to share contents if possible.
     * If you want to make sure that the two objects have independent
     * contents, call {@link #create(ShapedArray, boolean)} with second
     * argument set to {@code true}.
     * </p><p>
     * In any cases, if the array data storage is not "<i>flat</i>" or if its
     * elements type does not match that of the vector, the two contents will
     * be stored independently.
     * </p>
     * @param arr - The input array.
     * @return A new shaped vector of this space.
     */
    public abstract ShapedVector create(ShapedArray arr);

    /**
     * Create a new vector initialized with the contents of an array.
     *
     * @param arr         - The input array.
     * @param forceCopy   - A flag to force a copy of the contents if true.
     *                      See {@link #create(ShapedArray)} for a
     *                      discussion of that.
     * @return A new shaped vector of this space.
     */
    public abstract ShapedVector create(ShapedArray arr, boolean forceCopy);

}
