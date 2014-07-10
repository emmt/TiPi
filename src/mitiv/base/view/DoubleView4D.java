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

import mitiv.base.mapping.DoubleFunction;

/**
 * This class implements 4D views of arrays of double's.
 * 
 * @see {@link View} for a general introduction to the concepts of <i>views</i>.
 * 
 * @author Éric Thiébaut.
 *
 */
public class DoubleView4D extends View4D {
    private final double[] data;

    /**
     * Create a 4D view of an array of double's.
     * @param data - The array to wrap in the view.
     * @param n1   - The 1st dimension of the view.
     * @param n2   - The 2nd dimension of the view.
     * @param n3   - The 3rd dimension of the view.
     * @param n4   - The 4th dimension of the view.
     * @param s0   - The offset of element (0,0,0).
     * @param s1   - The stride along the 1st dimension.
     * @param s2   - The stride along the 2nd dimension.
     * @param s3   - The stride along the 3rd dimension.
     * @param s4   - The stride along the 4th dimension.
     */
    public DoubleView4D(double[] data, int n1, int n2, int n3, int n4,
            int s0, int s1, int s2, int s3, int s4) {
        super(data.length, n1, n2, n3, n4, s0, s1, s2, s3, s4);
        this.data = data;
    }

    /**
     * Query the array wrapped by the view.
     * @return The array wrapped by the view.
     */
    public final double[] getData() {
        return data;
    }

    /**
     * Query the value stored at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @return The value stored at position {@code (i1,i2,i3,i4)} of the view.
     */
    public final double get(int i1, int i2, int i3, int i4) {
        return data[index(i1, i2, i3, i4)];
    }

    /**
     * Set the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param value - The value to store at position {@code (i1,i2,i3,i4)} of the view.
     */
    public final void set(int i1, int i2, int i3, int i4, double value) {
        data[index(i1, i2, i3, i4)] = value;
    }

    /**
     * Increment the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param value - The value to add to the value stored at position
     *                {@code (i1,i2,i3,i4)} of the view.
     */
    public final void incr(int i1, int i2, int i3, int i4, double value) {
        data[index(i1, i2, i3, i4)] += value;
    }

    /**
     * Decrement the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param value - The value to subtract to the value stored at position
     *                {@code (i1,i2,i3,i4)} of the view.
     */
    public final void decr(int i1, int i2, int i3, int i4, double value) {
        data[index(i1, i2, i3, i4)] -= value;
    }

    /**
     * Multiply the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param value - The value by which to scale the value at position
     *                {@code (i1,i2,i3,i4)} of the view.
     */
    public final void mult(int i1, int i2, int i3, int i4, double value) {
        data[index(i1, i2, i3, i4)] *= value;
    }

    /**
     * Map the value at a given position of the view by a function.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param f  - The function to use.
     */
    public final void map(int i1, int i2, int i3, int i4, DoubleFunction f) {
        int k = index(i1, i2, i3, i4);
        data[k] = f.apply(data[k]);
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
