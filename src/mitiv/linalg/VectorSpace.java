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
    protected final int number; /* All vector spaces have a number of components. */

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
     *
     * <p> This method may be overridden to provide a more efficient version.
     * </p>
     *
     * @param alpha
     *        A scalar value.
     *
     * @return A new vector of this space.
     */
    public Vector create(double alpha) {
        Vector vec = create();
        _fill(vec, alpha);
        return vec;
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

    /**
     * Compute the inner product of two vectors (low level).
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
     * Default low level implementation of the Euclidean (L2) norm of a vector.
     *
     * <p> This basic implementation calls the {@link Vector#dot} method and is
     * expected to be overwritten with a more efficient version by the
     * descendants of this class. </p>
     *
     * @param x   A vector.
     *
     * @return The square root of the sum of squared components of {@code x}.
     */
    protected double _norm2(Vector x) {
        return Math.sqrt(_dot(x, x));
    }

    /**
     * Compute the L1 norm of a vector (low level).
     *
     * @param x   A vector.
     *
     * @return The sum of absolute values of {@code x}.
     */
    protected abstract double _norm1(Vector x);

    /**
     * Compute the infinite norm of a vector.
     *
     * @param x   A vector.
     *
     * @return The maximum absolute value of {@code x}.
     */
    protected abstract double _normInf(Vector x);

    /**
     * Multiply the components of a vector by a constant factor (low level).
     *
     * @param dst     The destination vector.
     * @param alpha   The scale factor.
     * @param src     The source vector.
     */
    protected abstract void _scale(Vector dst, double alpha, Vector src);

    /**
     * Default low-level implementation for in-place scaling of a vector.
     *
     * <p> Multiply the components of a vector by a constant factor. </p>
     *
     * @param vec     The target vector.
     * @param alpha   The scale factor.
     */
    protected void _scale(Vector vec, double alpha) {
        _scale(vec, alpha, vec);
    }

    /**
     * Compute a linear combination of two vectors (low level).
     *
     * <p> In pseudo-code, this method must perform the following operations
     * (for all indices {@code i}): </p>
     *
     * <pre>
     * this[i] = alpha*x[i] + beta*y[i];
     * </pre>
     *
     * <p> As this method can be used to emulate other operations (as copy,
     * zero, etc.), actual code should be optimized for specific scaling factors
     * equal to +/-1 or 0. In particular vectors with zero scale factors should
     * not be accessed. </p>
     *
     * <p> This abstract method must be overridden by the descendants of this
     * class. It is called by {@link Vector#combine(double, Vector, double, Vector)}
     * which insures that all vectors do belong to this vector space. </p>
     *
     * @param dst      The destination vector.
     * @param alpha    The scalar factor for vector {@code x}.
     * @param x        A vector.
     * @param beta     The scalar factor for vector {@code y}.
     * @param y        Another vector.
     */
    protected abstract void _combine(Vector dst, double alpha,
            Vector x, double beta, Vector y);

    /**
     * Compute a linear combination of three vectors (low level).
     *
     * <p> In pseudo-code, this method must perform the following operations
     * (for all indices {@code i}): </p>
     *
     * <pre>
     * this[i] = alpha*x[i] + beta*y[i] + gamma*z[i];
     * </pre>
     *
     * <p> As this method can be used to emulate other operations (as copy,
     * zero, etc.), actual code should be optimized for specific scaling factors
     * equal to +/-1 or 0. In particular vectors with zero scale factors should
     * not be accessed. </p>
     *
     * <p> This abstract method must be overridden by the descendants of this
     * class. It is called by {@link Vector#combine(double, Vector, double,
     * Vector, double, Vector)} which insures that all vectors do belong to this
     * vector space. </p>
     *
     * @param dst      The destination vector.
     * @param alpha    The scalar factor for vector {@code x}.
     * @param x        A vector.
     * @param beta     The scalar factor for vector {@code y}.
     * @param y        Another vector.
     * @param gamma    The scalar factor for vector {@code z}.
     * @param z        Yet another vector.
     */
    protected abstract void _combine(Vector dst, double alpha,
            Vector x, double beta, Vector y, double gamma, Vector z);

    /**
     * Add a scaled vector to a vector (low level).
     *
     * <p> This method must perform the following in-place operation (for all
     * indices {@code i}): </p>
     *
     * <pre>
     * dst[i] += alpha*x[i]
     * </pre>
     *
     * <p> Descendants of this class may override this default implementation to
     * provide a faster version. This method is called by {@link Vector#add}
     * which insures that all vectors do belong to this vector space. </p>
     *
     * @param dst      The destination vector.
     * @param alpha    The scalar factor for vector {@code x}.
     * @param x        The vector to scale.
     */
    protected void _add(Vector dst, double alpha, Vector x) {
        _combine(dst, 1, dst, alpha, x);
    }

    /**
     * Perform a component-wise multiplication of two vectors (low level).
     *
     * <p> This low-level method is called by {@link Vector#multiply} which
     * insures that all vectors do belong to this vector space. </p>
     *
     * <p> In pseudo-code, this method must perform the following operation (for
     * all indices {@code i}): </p>
     *
     * <pre>
     * dst[i] = x[i]*y[i];
     * </pre>
     *
     * @param dst
     *        The destination vector.
     *
     * @param x
     *        A vector.
     *
     * @param y
     *        Another vector.
     */
    protected abstract void _multiply(Vector dst, Vector x, Vector y);

    /**
     * Copy the contents of a vector into another one (low level).
     *
     * <p> Low level method which is called by {@link Vector#copy} which inusres
     * that the arguments are vectors of this space. </p>
     *
     * <p> This basic implementation calls {@link #_combine(Vector, double,
     * Vector, double, Vector)} method and is expected to be overridden with a
     * more efficient version by the descendants of this class. </p>
     *
     * @param dst
     *        The destination vector.
     *
     * @param src
     *        The source vector.
     */
    protected void _copy(Vector dst, Vector src) {
        _combine(dst, 1, src, 0, src);
    }

    /**
     * Exchange the contents of two vectors (low level).
     *
     * <p> Any concrete derived class must implement this low level method which
     * is guaranteed to be called with arguments by {@link Vector#swap}. </p>
     *
     * @param x
     *        A vector of this space.
     *
     * @param y
     *        Another vector of this space.
     */
    protected abstract void _swap(Vector x, Vector y);

    /**
     * Create a new vector from this vector space as a copy of another vector.
     *
     * <p> This protected method is called by {@link Vector#clone} to do the
     * real work after checking the argument. Derived classes can implement a
     * more efficient version than this one which is based on the
     * {@link #create} method and the {@link #_copy} protected method. </p>
     *
     * @param vec
     *        A vector from this space.
     *
     * @return A new duplicate copy of the vector {@code vec}.
     */
    protected Vector _clone(Vector vec) {
        Vector cpy = create();
        _copy(cpy, vec);
        return cpy;
    }

    /**
     * Default low-level implementation for zero-filling a vector.
     *
     * <p> This method set all components of a vector to zero. It is called
     * by {@link Vector#zero} which insures that the argument do belong to
     * this space. </p>
     *
     * @param vec   A vector of this space.
     */
    protected void _zero(Vector vec) {
        _fill(vec, 0);
    }

    /**
     * Fill a vector with a value (low level).
     *
     * <p> This method set all components of a vector with a value. It is called
     * by {@link Vector#fill} which insures that the argument do belong to
     * this space. </p>
     *
     * @param x       A vector of this space.
     * @param alpha   A scalar value.
     */
    protected abstract void _fill(Vector x, double alpha);

}
