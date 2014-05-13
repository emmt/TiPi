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
 * Class for vectors which belongs to an instance of the FloatVectorSpace
 * class.
 * 
 * Implements (flat) vectors of float precision reals.
 * 
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 * 
 */
public class FloatVector extends Vector {
    protected float[] data;

    /**
     * Create a new instance of a FloatVector with its own storage.
     * 
     * @param owner   The vector space of the result.
     */
    public FloatVector(FloatVectorSpace owner) {
        super(owner);
        data = new float[owner.size];
    }

    /**
     * Wrap an array of floats into a FloatVector.
     * 
     * The created vector will share its storage with the provided array.
     * 
     * @param owner  The vector space of the result.
     * @param data   The input data.
     * @throws IllegalArgumentException The length of the input array does not match
     *                                  the size of the vector space.
     */
    public FloatVector(FloatVectorSpace owner, float[] data) {
        super(owner);
        if (data == null || data.length != owner.size) {
            throw new IllegalArgumentException("Array size not compatible with vector space.");
        }
        this.data = data;
    }

    /**
     * Get the value of a given coefficient of a vector.
     * 
     * @param i
     *            The index of the coefficient.
     * @return The value of the coefficient.
     */
    public final float get(int i) {
        return data[i];
    }

    /**
     * Set the value of a given coefficient of a vector.
     * 
     * @param i
     *            The index of the coefficient.
     * @param value
     *            The value of the coefficient.
     */
    public final void set(int i, float value) {
        data[i] = value;
    }

    /**
     * Set the values of the vector from a Java array.
     * 
     * This method copies the values of the input array arr into the vector
     * (their sizes must match). Note that it may be more efficient to wrap
     * a vector around the Java array.
     *  
     * @param arr     The Java array to copy.
     */
    public void set(final float arr[]) {
        ArrayOps.copy(arr, data);
    }


    /**
     * Get vector length.
     * 
     * @return The number of elements of the vector.
     */
    public final int length() {
        return data.length;
    }

    /**
     * Get the array of reals which store the coefficients of the vector.
     * 
     * This method may be removed later when efficient set and get methods will
     * be fully tested and implemented.
     * 
     * @return the array of vector coefficients.
     */
    public float[] getData() {
        return data;
    }

    public String toString() {
        int n = data.length;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < n; ++i) {
            buf.append(i == 0 ? "{" : ", ");
            buf.append(get(i));
        }
        buf.append("}");
        return buf.toString();
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