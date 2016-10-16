/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014-2016 the MiTiV project, http://mitiv.univ-lyon1.fr/
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
 * Abstract basic class for vector spaces.
 *
 * <p>
 * A <b>vector space</b> E over the field F is a collection of elements which
 * can be added and scaled by a scalar (of the field F), these operations must
 * have the following properties (axioms):
 * </p><ul>
 * <li>associativity of addition: u + (v + w) = (u + v) + w</li>
 * <li>commutativity of addition: u + v = v + u</li>
 * <li>there exists an identity element (the zero vector 0) for the
 *     addition: u + 0 = u</li>
 * <li>every vector has an inverse for the addition: v + (-v) = 0</li>
 * <li>scalar multiplication is distributive w.r.t. vector addition:
 *     α (u + v) = α u + α v</li>
 * <li>distributivity of scalar multiplication with respect to field
 *     addition: (α + β) v = α v + β v</li>
 * <li>compatibility of scalar multiplication with field multiplication: α
 *     (β v) = (α β) v</li>
 * <li>identity element of scalar multiplication: 1 v = v, where 1 denotes
 *     the multiplicative identity in F.</li>
 * </ul><p>
 * A <b>Hilbert space</b> {H, 〈⋅,⋅〉} is a vector space endowed with an inner
 * product (which yields length and angle). A Hilbert space can have
 * infinite dimension.
 * </p><p>
 * A <b>Euclidean space</b> is a vector space of finite dimension endowed with
 * an inner product (which yields length and angle).
 * </p><p>
 * For our purposes (inverse problems and numerical optimization), we
 * mainly consider vector spaces over the field of reals (either single
 * or double precision floating points) of finite size (for obvious
 * practical reasons) and endowed with an inner product. Mathematically
 * speaking these kind of spaces are Euclidean spaces.
 * </p><p>
 * To achieve simple control of consistency and for clarity, operations
 * involving vectors are implemented as methods of their vector space.  These
 * methods are reflected at the level of the vectors for more flexibility and
 * code readability.
 * </p>
 * @author Éric Thiébaut.
 */
public abstract class VectorSpace {
    protected final int number; /* All vector spaces have a number of elements. */

    protected VectorSpace(int number) {
        if (number < 1) {
            throw new IllegalArgumentException("Bad vector space size.");
        }
        this.number = number;
    }

    /**
     * Get the number of components of the vectors of a vector space.
     * @return The number of components of the vectors of this vector space.
     */
    public final int getNumber() {
        return number;
    }

    /**
     * Create a new vector from this vector space with undefined contents.
     * @return A new vector of this space.
     */
    public abstract Vector create();

    /**
     * Create a new vector from this vector space filled with given value.
     * @param alpha   A scalar value.
     * @return A new vector of this space.
     */
    public abstract Vector create(double alpha);

    /**
     * Compute the inner product of two vectors.
     * <p>
     * The inner product, also called dot or scalar product of two vectors, is
     * the sum of the products of the corresponding elements of the two
     * vectors.  The inner product is defined on a vector space, the two
     * vectors must belong to this vector space.
     * </p>
     * @param x   A vector of this vector space.
     * @param y   Another vector of this vector space.
     * @return The inner product of {@code x} and {@code y}.
     * @throws IncorrectSpaceException Not all vectors belong to this vector space.
     */
    public final double dot(Vector x, Vector y) {
        check(x);
        check(y);
        return _dot(x, y);
    }

    /**
     * Compute the inner product of three vectors or a weighted inner product
     * of two vectors.
     * <p>
     * The inner product of three vectors is the sum of the products of the
     * corresponding elements of the three vectors.  The inner product is
     * defined on a vector space, the three vectors must belong to this vector
     * space.
     * <p>
     * @param w   A vector of this vector space.
     * @param x   Another vector of this vector space.
     * @param y   Yet another vector of this vector space.
     * @return The inner product of {@code w}, {@code x} and {@code y}.
     * @throws IncorrectSpaceException Not all vectors belong to this vector space.
     */
    public final double dot(Vector w, Vector x, Vector y) {
        check(w);
        check(x);
        check(y);
        return _dot(w, x, y);
    }

    /**
     * Compute the inner product of two vectors.
     * <p>
     * This abstract method must be overwritten by its descendants to
     * implement the inner product.  The passed arguments are guaranteed to
     * belong to this vector space.
     * </p>
     * @param x   A vector of this vector space.
     * @param y   Another vector of this vector space.
     * @return The inner product of {@code x} and {@code y}.
     */
    protected abstract double _dot(Vector x, Vector y);

    /**
     * Compute the inner product of three vectors or a weighted inner product
     * of two vectors.
     * <p>
     * This abstract method must be overwritten by its descendants to
     * implement the inner product.  The passed arguments are guaranteed to
     * belong to this vector space.
     * </p>
     * @param w   A vector of this vector space.
     * @param x   Another vector of this vector space.
     * @param y   Yet another vector of this vector space.
     * @return The inner product of {@code w}, {@code x} and {@code y}.
     */
    protected abstract double _dot(Vector w, Vector x, Vector y);

    /**
     * Compute the Euclidean (L2) norm of a vector.
     * <p>
     * This basic implementation calls dot() method and is expected to be
     * overwritten with a more efficient version by the descendants of this
     * class.
     * </p>
     * @param x   A vector.
     *
     * @return The square root of the sum of squared elements of {@code x}.
     *
     * @throws IncorrectSpaceException {@code x} does not belong to this vector space.
     */
    public final double norm2(Vector x) throws IncorrectSpaceException {
        check(x);
        return _norm2(x);
    }

    /** Default low level implementation. */
    protected double _norm2(Vector x) {
        return Math.sqrt(_dot(x, x));
    }

    /**
     * Compute the L1 norm of a vector.
     *
     * @param x   A vector.
     *
     * @return The sum of absolute values of {@code x}.
     *
     * @throws IncorrectSpaceException {@code x} does not belong to this vector space.
     */
    public final double norm1(Vector x) throws IncorrectSpaceException {
        check(x);
        return _norm1(x);
    }

    protected abstract double _norm1(Vector x);

    /**
     * Compute the infinite norm of a vector.
     *
     * @param x   A vector.
     *
     * @return The maximum absolute value of {@code x}.
     *
     * @throws IncorrectSpaceException {@code x} does not belong to this vector space.
     */
    public final double normInf(Vector x) throws IncorrectSpaceException {
        check(x);
        return _normInf(x);
    }

    protected abstract double _normInf(Vector x);

    /**
     * Multiply the components of a vector by a constant factor.
     *
     * @param vec       The target vector.
     * @param alpha   The scale factor.
     *
     * @throws IncorrectSpaceException Vector {@code vec} does not belong to this vector space.
     */
    public void scale(Vector vec, double alpha) {
        check(vec);
        _scale(vec, alpha);
    }

    /**
     * Multiply the components of a vector by a constant factor.
     *
     * @param dst     The destination vector.
     * @param alpha   The scale factor.
     * @param src     The source vector.
     *
     * @throws IncorrectSpaceException Not all vectors belong to this vector space.
     */
    public void scale(Vector dst, double alpha, Vector src) {
        check(dst);
        check(src);
        _scale(dst, alpha, src);
    }

    /** Low level method for scaling a vector. */
    protected abstract void _scale(Vector dst, double alpha, Vector src);

    /** Default implementation. */
    protected void _scale(Vector v, double alpha) {
        _scale(v, alpha, v);

    }

    /**
     * Compute a linear combination of two vectors.
     * <p>
     * In pseudo-code, this method performs the following operation:
     * <pre>
     * dst[i] = alpha*x[i] + beta*y[i];
     * </pre>
     * for all indices {@code i}.
     * </p><p>
     * This abstract method must be overwritten by its descendants. As this
     * method can be used to emulate other operations (as copy, zero, etc.),
     * actual code should be optimized for specific factors alpha and/or beta
     * equal to +/-1 or 0.  In particular when ALPHA (or BETA) is zero, then X
     * (or Y) must not be referenced.
     * </p>
     * @param dst     The destination vector.
     * @param alpha   The scalar factor for vector {@code x}.
     * @param x       A vector.
     * @param beta    The scalar factor for vector {@code y}.
     * @param y       Another vector.
     *
     * @throws IncorrectSpaceException Not all vectors belong to this vector space.
     */
    public final void combine(Vector dst, double alpha,
            Vector x, double beta, Vector y) throws IncorrectSpaceException {
        check(x);
        check(y);
        check(dst);
        _combine(dst, alpha, x, beta, y);
    }

    /**
     * Compute a linear combination of two vectors (low level).
     * <p>
     * This abstract method must be overwritten by the descendants of this
     * class.  It is guaranteed that all passed vectors belong to the same
     * vector space.
     * </p><p>
     * As this method can be used to emulate other operations (as copy, zero,
     * etc.), actual code should be optimized for specific factors alpha
     * and/or {@code beta} equal to +/-1 or 0.  In particular when {@code
     * alpha} (or {@code beta}) is zero, then {@code x} (or {@code y}) must
     * not be referenced.
     * </p>
     * @param dst     The destination vector.
     * @param alpha   The scalar factor for vector {@code x}.
     * @param x       A vector.
     * @param beta    The scalar factor for vector {@code y}.
     * @param y       Another vector.
     */
    protected abstract void _combine(Vector dst, double alpha,
            Vector x, double beta, Vector y);

    /**
     * Compute a linear combination of three vectors.
     * <p>
     * In pseudo-code, this method performs the following operation:
     * <pre>
     * dst[i] = alpha*x[i] + beta*y[i] + gamma*z[i];
     * </pre>
     * for all indices {@code i}.
     * </p>
     * @param dst     The destination vector.
     * @param alpha   The scalar factor for vector {@code x}.
     * @param x       A vector.
     * @param beta    The scalar factor for vector {@code y}.
     * @param y       Another vector.
     * @param gamma   The scalar factor for vector {@code z}.
     * @param z       Yet another vector.
     *
     * @throws IncorrectSpaceException Not all vectors belong to this vector space.
     */
    public final void combine(Vector dst, double alpha, Vector x,
            double beta, Vector y, double gamma, Vector z)
                    throws IncorrectSpaceException {
        check(x);
        check(y);
        check(z);
        check(dst);
        _combine(dst, alpha, x, beta, y, gamma, z);
    }

    /**
     * Compute a linear combination of two vectors (low level).
     * <p>
     * This abstract method must be overwritten by the descendants of this
     * class.  It is guaranteed that all passed vectors belong to the same
     * vector space.
     * </p>
     * @param dst     The destination vector.
     * @param alpha   The scalar factor for vector {@code x}.
     * @param x       A vector.
     * @param beta    The scalar factor for vector {@code y}.
     * @param y       Another vector.
     * @param gamma   The scalar factor for vector {@code z}.
     * @param z       Yet another vector.
     */
    protected abstract void _combine(Vector dst, double alpha,
            Vector x, double beta, Vector y, double gamma, Vector z);

    /**
     * Perform a component-wise multiplication of two vectors.
     * <p>
     * In pseudo-code, this method performs the following operation:
     * <pre>
     * dst[i] = x[i]*y[i];
     * </pre>
     * for all indices {@code i}.
     * </p>
     * @param dst  The destination vector.
     * @param x    A vector.
     * @param y -  Another vector.
     *
     * @throws IncorrectSpaceException Not all vectors belong to this vector space.
     */
    public final void multiply(Vector dst, Vector x, Vector y)
            throws IncorrectSpaceException {
        check(x);
        check(y);
        check(dst);
        _multiply(dst, x, y);
    }
    protected abstract void _multiply(Vector dst, Vector x, Vector y);

    /**
     * Copy the contents of a vector into another one.
     *
     * @param dst   The destination vector.
     * @param src   The source vector.
     *
     * @throws IncorrectSpaceException Not all vectors belong to this vector space.
     */
    public final void copy(Vector dst, Vector src)
            throws IncorrectSpaceException {
        check(src);
        if (dst != src) {
            check(dst);
            _copy(dst, src);
        }
    }

    /**
     * Copy the contents of a vector into another one (low level).
     * <p>
     * Low level method which is guaranteed to be called with checked
     * arguments by {@link #copy}.
     * </p><p>
     * This basic implementation calls {@link #_combine(double, Vector, double, Vector)}
     * method and is expected to be overwritten with a more efficient version by the
     * descendants of this class.
     * </p>
     * @param dst   The destination vector.
     * @param src   The source vector.
     */
    protected void _copy(Vector dst, Vector src) {
        _combine(dst, 1.0, src, 0.0, dst);
    }

    /**
     * Exchange the contents of two vectors.
     *
     * @param x   A Vector.
     * @param y   Another vector
     *
     * @throws IncorrectSpaceException Not all vectors belong to this vector space.
     */
    public final void swap(Vector x, Vector y)
            throws IncorrectSpaceException {
        check(x);
        if (y != x) {
            check(y);
            _swap(x, y);
        }
    }

    /**
     * Exchange the contents of two vectors (low level).
     * <p>
     * Any concrete derived class must implement this low level method
     * which is guaranteed to be called with checked arguments by {@link #swap}.
     * </p>
     * @param x   A Vector.
     * @param y   Another vector
     */
    protected abstract void _swap(Vector x, Vector y);

    /**
     * Create a new vector as a clone of another vector.
     * <p>
     * As implemented in the base class, this method is not optimized but
     * provides the reference behavior.
     * </p>
     * @param vec   The vector to clone (must belongs to this vector space).
     * @return A clone of the input vector.
     * @throws IncorrectSpaceException Vector {@code vec} does not belong to this vector space.
     */
    public Vector clone(Vector vec) throws IncorrectSpaceException {
        check(vec);
        return _clone(vec);
    }

    /**
     * Create a new vector from this vector space as a copy of another vector.
     * <p>
     * This protected method is called by {@link #clone} to do the real work
     * after checking the argument.  Derived classes can implement a more
     * efficient version than this one which is based on the {@link #create}
     * method and the {@link #_copy} protected method.
     * </p>
     * @param vec   A vector from this space (this has been checked).
     * @return A new duplicate copy of the vector {@code vec}.
     */
    protected Vector _clone(Vector vec) {
        Vector cpy = create();
        _copy(cpy, vec);
        return cpy;
    }

    /**
     * Create a new vector of one's.
     *
     * @return A new vector of this space filled with ones.
     */
    public Vector one() {
        return create(1.0);
    }

    /**
     * Create a new vector of zero's.
     *
     * @return A new vector of this space filled with zeros.
     */
    public Vector zero() {
        return create(0.0);
    }

    /**
     * Fill a vector with zeros.
     * <p>
     * This method set to zero all elements of a vector.
     * </p>
     * @param vec   A vector of this space.
     * @throws IncorrectSpaceException Vector {@code vec} does not belong to this vector space.
     */
    public final void zero(Vector vec) {
        check(vec);
        _zero(vec);
    }

    /** Default low-level implementation (arguments are guaranteed to be correct). */
    protected void _zero(Vector vec) {
        _fill(vec, 0.0);
    }

    /**
     * Fill a vector with a value.
     * <p>
     * This method set all elements of a vector with a value.
     * </p>
     * @param x       A vector of this space.
     * @param alpha   A scalar value.
     *
     * @throws IncorrectSpaceException V must belong to this vector space.
     */
    public final void fill(Vector x, double alpha) {
        check(x);
        _fill(x, alpha);
    }

    /** Low-level implementation (arguments are guaranteed to be correct). */
    protected abstract void _fill(Vector x, double alpha);

    /**
     * Check whether a given vector belongs to the vector space.
     *
     * @param vec   A vector.
     * @return True if {@code vec} is not {@code null} and belongs to this vector space.
     */
    public final boolean owns(Vector vec) {
        return (vec != null && vec.belongsTo(this));
    }

    /**
     * Make sure a given vector belongs to the vector space.
     * <p>
     * This method throws an {@code IncorrectSpaceException} exception
     * if its argument does not belong to the vector space.
     * </p>
     * @param vec   A vector to check.
     * @throws IncorrectSpaceException Vector {@code vec} does not belong to this vector space.
     */
    public final void check(Vector vec) throws IncorrectSpaceException {
        if (! owns(vec)) {
            throw new IncorrectSpaceException();
        }
    }

}
