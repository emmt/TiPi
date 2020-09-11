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

import mitiv.exception.IncorrectSpaceException;

/**
 * A Vector is an element of a {@link VectorSpace}.
 *
 * <p> An instance of this class is a collection of real values which can be
 * addressed individually and for which it makes sense to apply the operations
 * supported by the elements of a vector space (inner product, linear
 * combination, <i>etc.</i>). </p>
 *
 * <p> At this level of abstraction nothing more has to be known.  For
 * instance, the restriction that indexed components of a vector are reals is
 * not an issue for complex valued vectors (a complex is just two reals). </p>
 *
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */
public abstract class Vector {

    /**
     * Reference to the VectorSpace to which this vector belongs.
     */
    protected final VectorSpace space;

    /**
     * Number of components.
     */
    protected final int number;

    /**
     * Create a vector from a given vector space.
     *
     * @param owner
     *        The vector space to which the result belongs to.
     */
    protected Vector(VectorSpace owner) {
        this.space = owner;
        this.number = owner.number;
    }

    /**
     * Get the vector space which owns the vector.
     *
     * @return The vector space which owns the vector.
     */
    public VectorSpace getOwner() {
        return space;
    }

    /**
     * Get the vector space of the vector.
     *
     * @return The vector space of the vector.
     */
    public VectorSpace getSpace() {
        return space;
    }

    /**
     * Get the size of the vector.
     *
     * @return The number of scalars collected in the vector.
     */
    public final int getNumber() {
        return number;
    }

    /**
     * Get the size of the vector.
     *
     * @return The number of scalars collected in the vector.
     */
    public final int length() {
        return number;
    }

    /**
     * Check whether a vector belongs to a given vector space.
     *
     * @param space
     *        A vector space.
     *
     * @return True or false.
     */
    public final boolean belongsTo(VectorSpace space) {
        return (this.space == space);
    }

    /**
     * Throw an exception if a vector does not belong to a given vector space.
     *
     * @param space
     *        The vector space.
     *
     * @throws IncorrectSpaceException The instance does not belong to the
     *         given vector space.
     */
    public final void assertBelongsTo(VectorSpace space)
            throws IncorrectSpaceException {
        if (!belongsTo(space)) {
            throw new IncorrectSpaceException();
        }
    }

    /**
     * Get the value of a component of the vector.
     *
     * <p> This method is not meant to be efficient, it is mainly provided for
     * testing or debugging purposes. To remain efficient, vector contents must
     * be managed in a more specific way. </p>
     *
     * @param i
     *        The index of the value (runs from 0 to {@code n}-1, with
     *        {@code n} the number of components of the vector).
     *
     * @return The value of the vector at the given index.
     *
     * @throws IndexOutOfBoundsException The index {@code i} is out of bounds.
     *
     * @see #set(int, double)
     */
    public abstract double get(int i) throws IndexOutOfBoundsException;

    /**
     * Set the value of a component of the vector.
     *
     * <p> This method is not meant to be efficient, it is mainly provided for
     * testing or debugging purposes. To remain efficient, vector contents must
     * be managed in a more specific way. </p>
     *
     * @param i
     *        The index of the value (runs from 0 to {@code n}-1, with
     *        {@code n} the number of components of the vector).
     *
     * @param value
     *        The value to store at the index position.
     *
     * @throws IndexOutOfBoundsException The index {@code i} is out of bounds.
     *
     * @see #get(int)
     */
    public abstract void set(int i, double value)
            throws IndexOutOfBoundsException;

    /**
     * Create another vector of the same vector space.
     *
     * @return A new vector of the same vector space.
     */
    public Vector create() {
        return this.space.create();
    }

    /**
     * Create a clone of the vector.
     *
     * <p> The clone is a new vector with (initially) the same contents of the
     * vector but using independent storage. </p>
     *
     * @return A clone of the vector.
     */
    @Override
    public Vector clone() {
        return space._clone(this);
    }

    /**
     * Copy the contents of the vector from another one.
     *
     * <p> This method copies the components of the source vector <i>src</i>
     * into <i>this</i>.  In pseudo-code (for all indices <i>i</i>): </p>
     *
     * <pre>
     * this[i] = src[i];
     * </pre>
     *
     * @param src
     *        The source vector.
     *
     * @throws IncorrectSpaceException {@code src} does not belong to the
     *         same vector space.
     */
    public final void copy(Vector src) throws IncorrectSpaceException {
        if (src != this) {
            space.check(src);
            space._copy(this, src);
        }
    }

    /**
     * Exchange the contents of the vector with that of another one.
     *
     * @param vec
     *        The other vector.
     *
     * @throws IncorrectSpaceException {@code vec} does not belong to the
     *         same vector space.
     */
    public final void swap(Vector vec)
            throws IncorrectSpaceException {
        if (vec != this) {
            space.check(vec);
            space._swap(this, vec);
        }
    }

    /**
     * Compute the inner product of this vector with another vector.
     *
     * <p> The inner product, also called dot or scalar product of two vectors,
     * is the sum of the products of the corresponding components of the two
     * vectors. The inner product is defined on a vector space, the two vectors
     * must belong to this vector space. </p>
     *
     * <p> In pseudo-code, this method returns the following result ({@code n}
     * is the number of components of the vectors): </p>
     *
     * <pre>
     * this[0]*vec[0] + this[1]*vec[1] + ... + this[n-1]*vec[n-1]
     * </pre>
     *
     * @param vec
     *        Another vector of the vector space of {@code this}.
     *
     * @return The inner product of {@code this} and {@code vec}.
     *
     * @throws IncorrectSpaceException {@code vec} does not belong to the
     *         vector space of {@code this} .
     */
    public final double dot(Vector vec) {
        space.check(vec);
        return space._dot(this, vec);
    }

    /**
     * Weight the inner product of two other vectors by this vector.
     *
     * <p> The weighted inner product is the sum of the products of the
     * corresponding components of the three vectors. This inner product is
     * defined on a vector space, the three vectors must belong to this vector
     * space.  </p>
     *
     * <p> In pseudo-code, this method returns the following result ({@code n}
     * is the number of components of the vectors): </p>
     *
     * <pre>
     * this[0]*x[0]*y[0] + this[1]*x[1]*y[0] + ... + this[n-1]*x[n-1]*y[n-1]
     * </pre>
     *
     * @param x
     *        Another vector of the vector space of {@code this}.
     *
     * @param y
     *        Yet another vector of the vector space of {@code this}.
     *
     * @return The sum of the products of the corresponding components of
     *         {@code this}, {@code v1} and {@code v2}.
     *
     * @throws IncorrectSpaceException Vectors {@code x} or {@code y} do not
     *         belong to the vector space of {@code this}.
     */
    public final double dot(Vector x, Vector y) {
        space.check(x);
        space.check(y);
        return space._dot(this, x, y);
    }

    /**
     * Compute the L1 norm of the vector.
     *
     * @return The sum of absolute values of the components of the vector.
     */
    public final double norm1() {
        return space._norm1(this);
    }

    /**
     * Compute the Euclidean (L2) norm of the vector.
     *
     * @return The square root of the sum of the squared values of the
     *         components of the vector.
     */
    public final double norm2() {
        return space._norm2(this);
    }

    /**
     * Compute the infinite norm of the vector.
     *
     * @return The maximum absolute value of the components of the vector.
     */
    public final double normInf() {
        return space._normInf(this);
    }

    /**
     * Fill the vector with a value.
     *
     * <p> Set all components of the vector with a value.  </p>
     *
     * @param alpha
     *        A scalar value.
     */
    public final void fill(double alpha) {
        space._fill(this, alpha);
    }

    /**
     * Fill the vector with zeros.
     *
     * <p> Set all components of the vector to zero.  </p>
     */
    public final void zero() {
        space._zero(this);
    }

    /**
     * Multiply the components of the vector by a constant factor.
     *
     * <p> In pseudo-code, this method performs the following operation (for
     * all indices {@code i}): </p>
     *
     * <pre>
     * this[i] *= alpha;
     * </pre>
     *
     * @param alpha
     *        The scale factor.
     */
    public final void scale(double alpha) {
        space._scale(this, alpha);
    }

    /**
     * Set the vector to a vector times a constant factor.
     *
     * <p> In pseudo-code, this method performs the following operation (for
     * all indices {@code i}): </p>
     *
     * <pre>
     * this[i] = alpha*vec[i];
     * </pre>
     *
     * @param alpha
     *        The scale factor.
     *
     * @param vec
     *        A vector of the vector space of {@code this}.
     *
     * @throws IncorrectSpaceException {@code vec} does not belong to the
     *         vector space of {@code this}.
     */
    public final void scale(double alpha, Vector vec) {
        space.check(vec);
        space._scale(this, alpha, vec);
    }

    /**
     * Add a scaled vector.
     *
     * <p> In pseudo-code, this method performs the following operation (for
     * all indices {@code i}): </p>
     *
     * <pre>
     * this[i] += alpha*x[i];
     * </pre>
     *
     * @param alpha
     *        The scalar factor.
     *
     * @param x
     *        A vector of the vector space of {@code this}.
     *
     * @throws IncorrectSpaceException Vector {@code x} does not belong to the
     *         vector space of {@code this}.
     */
    public final void add(double alpha, Vector x)
            throws IncorrectSpaceException {
        space.check(x);
        space._add(this, alpha, x);
    }

    /**
     * Compute the linear combination of two vectors.
     *
     * <p> In pseudo-code, this method performs the following operation (for
     * all indices {@code i}): </p>
     *
     * <pre>
     * this[i] = alpha*x[i] + beta*y[i];
     * </pre>
     *
     * @param alpha
     *        The scalar factor for vector {@code x}.
     *
     * @param x
     *        A vector of the vector space of {@code this}.
     *
     * @param beta
     *        The scalar factor for vector {@code y}.
     *
     * @param y
     *        Another vector of the vector space of {@code this}.
     *
     * @throws IncorrectSpaceException Not all vectors belong to the vector
     *         space of {@code this}.
     */
    public final void combine(double alpha, Vector x,
            double beta, Vector y) throws IncorrectSpaceException {
        space.check(x);
        space.check(y);
        space._combine(this, alpha, x, beta, y);
    }

    /**
     * Compute the linear combination of three vectors.
     *
     * <p> In pseudo-code, this method performs the following operation (for
     * all indices {@code i}): </p>
     *
     * <pre>
     * this[i] = alpha*x[i] + beta*y[i] + gamma*z[i];
     * </pre>
     *
     * @param alpha
     *        The scalar factor for vector {@code x}.
     *
     * @param x
     *        A vector of the vector space of {@code this}.
     *
     * @param beta
     *        The scalar factor for vector {@code y}.
     *
     * @param y
     *        Another vector of the vector space of {@code this}.
     *
     * @param gamma
     *        The scalar factor for vector {@code z}.
     *
     * @param z
     *        Yet another vector of the vector space of {@code this}.
     *
     * @throws IncorrectSpaceException Not all vectors belong to the vector
     *         space of {@code this}.
     */
    public final void combine(double alpha, Vector x,
            double beta, Vector y,
            double gamma, Vector z) throws IncorrectSpaceException {
        space.check(x);
        space.check(y);
        space.check(z);
        space._combine(this, alpha, x, beta, y, gamma, z);
    }

    /**
     * Perform a component-wise multiplication by another vector.
     *
     * <p> In pseudo-code, this method performs the following operation (for
     * all indices {@code i}): </p>
     *
     * <pre>
     * this[i] *= x[i];
     * </pre>
     *
     * @param x
     *        A vector of the vector space of {@code this}.
     *
     * @throws IncorrectSpaceException The argument does not belong to the
     *         vector space of {@code this}.
     */
    public final void multiply(Vector x) throws IncorrectSpaceException {
        space.check(x);
        space._multiply(this, this, x);
    }

    /**
     * Compute the component-wise multiplication of two vectors.
     *
     * <p> In pseudo-code, this method performs the following operation (for
     * all indices {@code i}): </p>
     *
     * <pre>
     * this[i] = x[i]*y[i];
     * </pre>
     *
     * for all indices {@code i}.
     * </p>
     *
     * @param x
     *        A vector of the vector space of {@code this}.
     *
     * @param y
     *        Another vector of the vector space of {@code this}.
     *
     * @throws IncorrectSpaceException Not all arguments belong to the vector
     *         space of {@code this}.
     */
    public final void multiply(Vector x, Vector y)
            throws IncorrectSpaceException {
        space.check(x);
        space.check(y);
        space._multiply(this, x, y);
    }

    /**
     * Make a string representation of the vector contents.
     */
    @Override
    public String toString() {
        return toString(number);
    }


    /**
     * Make a, possibly truncated, string representation of the vector
     * contents.
     *
     * @param n
     *        The maximum number of components to show.
     *
     * @return A string like "{1.1, 0.0, ..., 9.7}", if {@code n = 3}.
     */
    public String toString(int n) {
        StringBuffer buf = new StringBuffer();
        buf.append("{");
        if (number > n) {
            if (n <= 0 ) {
                buf.append("...");
            } else {
                for (int i = 0; i < Math.min(number, (n + 1)/2); ++i) {
                    buf.append(String.format((i > 0 ? ", %g" : "%g"), get(i)));
                }
                buf.append(", ...");
                for (int i = number - (n - ((n + 1)/2)); i < number; ++i) {
                    buf.append(String.format(", %g", get(i)));
                }
            }
        } else {
            for (int i = 0; i < number; ++i) {
                buf.append(String.format((i > 0 ? ", %g" : "%g"), get(i)));
            }
        }
        return buf.append("}").toString();
    }

}
