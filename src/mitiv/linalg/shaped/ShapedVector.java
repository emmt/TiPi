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
import mitiv.base.Typed;
import mitiv.linalg.Vector;

public abstract class ShapedVector extends Vector implements Shaped, Typed {
    final ArrayDescriptor descr;

    ShapedVector(ShapedVectorSpace owner) {
        super(owner);
        descr = owner.descr;
    }

    @Override
    public ShapedVectorSpace getOwner() {
        return (ShapedVectorSpace)space;
    }


    @Override
    public ShapedVectorSpace getSpace(){
        return getOwner();
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

    @Override
    public ShapedVector clone() {
        return getSpace()._clone(this);
    }

    /**
     * Assign the elements of a shaped vector form those of an array.
     *
     * <p>
     * The shape of the array must match that of the vector. Type
     * conversion is automatically performed and the copy is optimized
     * if the array and the vector already share the same contents.
     * </p>
     * @param arr - The input array.
     */
    public abstract void assign(ShapedArray arr);

    /**
     * Get a string representation.
     */
    @Override
    public String toString() {
        int len = getNumber();
        String str = "ShapedVector: " + descr.toString() + " = {";
        if (len < 9) {
            for (int i = 0; i < len; ++i) {
                str += String.format((i > 0 ? ", %g" : " %g"), get(i));
            }
        } else {
            for (int i = 0; i < 3; ++i) {
                str += String.format((i > 0 ? ", %g" : " %g"), get(i));
            }
            str += ", ...";
            for (int i = len - 3; i < len; ++i) {
                str += String.format(", %g", get(i));
            }
        }
        return str + "}";
    }
}
