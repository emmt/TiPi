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
 * A Vector is an element of a VectorSpace.
 *
 * An instance of this class is a collection of real values which can be addressed
 * individually and for which it makes sense to apply the operations supported by
 * the elements of a vector space (inner product, linear combination, etc.).
 * <p>
 * At this level of abstraction nothing more has to be known.  For instance, the
 * restriction that indexed elements of a vector are reals is not an issue for
 * complex valued vectors (a complex is just two reals).
 *
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 *
 */
public abstract class Vector {

    /**
     * Reference to the VectorSpace to which this vector belongs.
     */
    protected final VectorSpace space;
    protected final int number;

    /**
     * Create a vector from a given vector space.
     * @param owner  The vector space to which the result belongs to.
     */
    protected Vector(VectorSpace owner) {
        this.space = owner;
        this.number = owner.number;
    }

    /**
     * Get the vector space which owns the vector.
     * @return The vector space owning the vector.
     */
    public VectorSpace getOwner() {
        return space;
    }

    /**
     * Get the size of the vector.
     * @return The number of scalars collected in the vector.
     */
    public final int getNumber() {
        return number;
    }

    /**
     * Get the size of the vector.
     * @return The number of scalars collected in the vector.
     */
    public final int length() {
        return number;
    }

    /**
     * Get the vector space of the vector.
     * @return The vector space of the vector.
     */
    public VectorSpace getSpace() {
        return space;
    }

    /**
     * Check whether a vector belongs to a given vector space.
     *
     * @param space
     *            a vector space.
     * @return true or false.
     */
    public final boolean belongsTo(VectorSpace space) {
        return (this.space == space);
    }

    /**
     * Throw an exception if a vector does not belong to a given vector space.
     * @param space  The vector space.
     * @throws IncorrectSpaceException The instance must belong to the given vector space.
     */
    public final void assertBelongsTo(VectorSpace space)
            throws IncorrectSpaceException {
        if (!belongsTo(space)) {
            throw new IncorrectSpaceException();
        }
    }

    /**
     * Get one of the values gathered in the vector.
     *
     * This method is not meant to be efficient, it is mainly provided for testing or
     * debugging purposes.  To remain efficient, vectors should be managed at a more
     * global level.
     *
     * @param i  - The index of the value (runs from 0 to {@code n}-1, with {@code n}
     *             the number of elements of the vector).
     * @return The value of the vector at the given index.
     * @throws IndexOutOfBoundsException if index {@code i} is out of bounds;
     */
    public abstract double get(int i) throws IndexOutOfBoundsException;

    /**
     * Set one of the values gathered in the vector.
     *
     * This method is not meant to be efficient, it is mainly provided for testing or
     * debugging purposes.  To remain efficient, vectors should be managed at a more
     * global level.
     *
     * @param i - The index of the value (runs from 0 to {@code n}-1, with {@code n}
     *            the number of elements of the vector).
     * @param value - The value to store at the index position.
     * @throws IndexOutOfBoundsException if index {@code i} is out of bounds;
     */
    public abstract void set(int i, double value) throws IndexOutOfBoundsException;

    /**
     * Create a clone of the vector.
     *
     * @return A clone of the vector.
     */
    @Override
    public Vector clone() {
        return space._clone(this);
    }

    /**
     * Copy the contents of the vector into another one.
     *
     * @param dst - The destination vector.
     * @throws IncorrectSpaceException {@code dst} must belong to the same vector space.
     */
    public final void copyTo(Vector dst)
            throws IncorrectSpaceException {
        if (dst != this) {
            space.check(dst);
            space._copy(dst, this);
        }
    }

    /**
     * Copy the contents of the vector from another one.
     *
     * @param src - The source vector.
     * @throws IncorrectSpaceException {@code src} must belong to the same vector space.
     */
    public final void copyFrom(Vector src)
            throws IncorrectSpaceException {
        if (src != this) {
            space.check(src);
            space._copy(this, src);
        }
    }

    /**
     * Exchange the contents of the vector with that of another one.
     *
     * @param v - The other vector.
     * @throws IncorrectSpaceException {@code dst} must belong to the same vector space.
     */
    public final void swap(Vector v)
            throws IncorrectSpaceException {
        if (v != this) {
            space.check(v);
            space._swap(this, v);
        }
    }

    /**
     * Multiply the values of the vector by a constant factor.
     *
     * @param alpha - The scale factor.
     */
    public final void scale(double alpha) {
        space._scale(this, alpha);
    }

    /**
     * Compute the inner product of this vector with another vector.
     *
     * The inner product, also called dot or scalar product of two vectors, is
     * the sum of the products of the corresponding elements of the two
     * vectors.  The inner product is defined on a vector space, the two
     * vectors must belong to this vector space.
     *
     * @param other - Another vector of this vector space.
     * @return The inner product of {@code this} and {@code other}.
     * @throws IncorrectSpaceException {@code other} must belong to the vector
     *         space of {@code this} .
     */
    public final double dot(Vector other) {
        if (other == null || ! other.belongsTo(space)) {
            throw new IncorrectSpaceException();
        }
        return space._dot(this, other);
    }

    /**
     * Compute the inner product of this vector with two other vectors.
     *
     * The inner product of three vectors is the sum of the products of the
     * corresponding elements of the three vectors.  The inner product is
     * defined on a vector space, the three vectors must belong to this vector
     * space.
     *
     * @param v1 - Another vector of this vector space.
     * @param v2 - Yet another vector of this vector space.
     * @return The inner product of {@code this}, {@code v1} and {@code v2}.
     * @throws IncorrectSpaceException {@code v1} and {@code v2} must belong
     * to the vector space of {@code this} .
     */
    public final double dot(Vector v1, Vector v2) {
        if (v1 == null || ! v1.belongsTo(space) ||
                v2 == null || ! v2.belongsTo(space)) {
            throw new IncorrectSpaceException();
        }
        return space._dot(this, v1, v2);
    }

    /**
     * Compute the L1 norm of the vector.
     *
     * @return The sum of absolute values of the vector.
     */
    public final double norm1() {
        return space._norm1(this);
    }

    /**
     * Compute the Euclidean (L2) norm of the vector.
     *
     * @return The square root of the sum of squared elements of the vector.
     */
    public final double norm2() {
        return space._norm2(this);
    }

    /**
     * Compute the infinite norm of the vector.
     *
     * @return The maximum absolute value of the vector.
     */
    public final double normInf() {
        return space._normInf(this);
    }

    /**
     * Fill the vector with a value.
     *
     * Set all elements of the vector with a value.
     *
     * @param alpha   A scalar value.
     */
    public final void fill(double value) {
        space._fill(this, value);
    }

    /**
     * Fill the vector with zeros.
     *
     * Set to zero all elements of the vector.
     */
    public final void zero() {
        space._zero(this);
    }

    /**
     * Compute the linear combination of two vectors.
     *
     * In pseudo-code, this method performs the following operation:
     * <pre>
     * this[i] = alpha*x[i] + beta*y[i];
     * </pre>
     * for all indices {@code i}.
     *
     * @param alpha - The scalar factor for vector {@code x}.
     * @param x     - A vector.
     * @param beta  - The scalar factor for vector {@code y}.
     * @param y     - Another vector.
     *
     * @throws IncorrectSpaceException all vectors must belong to the same
     * vector space.
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
     * In pseudo-code, this method performs the following operation:
     * <pre>
     * this[i] = alpha*x[i] + beta*y[i] + gamma*z[i];
     * </pre>
     * for all indices {@code i}.
     *
     * @param alpha - The scalar factor for vector {@code x}.
     * @param x     - A vector.
     * @param beta  - The scalar factor for vector {@code y}.
     * @param y     - Another vector.
     * @param gamma - The scalar factor for vector {@code z}.
     * @param z     - Yet another vector.
     *
     * @throws IncorrectSpaceException all vectors must belong to the same
     * vector space.
     */
    public final void combine(double alpha, Vector x,
            double beta, Vector y,
            double gamma, Vector z)
                    throws IncorrectSpaceException {
        space.check(x);
        space.check(y);
        space.check(z);
        space._combine(this, alpha, x, beta, y, gamma, z);
    }

    /**
     * Perform a component-wise multiplication by another vector.
     *
     * In pseudo-code, this method performs the following operation:
     * <pre>
     * this[i] *= other[i];
     * </pre>
     * for all indices {@code i}.
     * @param other - Another vector.
     *
     * @throws IncorrectSpaceException The argument must belong to this vector space.
     */
    public final void multiply(Vector other)
            throws IncorrectSpaceException {
        space.check(other);
        space._multiply(this, this, other);
    }

    /**
     * Make a string representation of the vector contents.
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < number; ++i) {
            buf.append(i == 0 ? "{" : ", ");
            buf.append(get(i));
        }
        buf.append("}");
        return buf.toString();
    }
}
