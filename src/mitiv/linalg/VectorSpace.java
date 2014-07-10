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
 * Abstract basic class for vector spaces.
 *
 * @author Éric Thiébaut.
 *
 *         A vector space E over the field F is a collection of elements can be
 *         added and scaled by a scalar (of the field F), these operations must
 *         have the following properties (axioms):
 *
 *         - associativity of addition: u + (v + w) = (u + v) + w
 *
 *         - commutativity of addition: u + v = v + u
 *
 *         - there exists an identity element (the zero vector 0) for the
 *         addition: u + 0 = u
 *
 *         - every vector has an inverse for the addition: v + (-v) = 0
 *
 *         - scalar multiplication is distributive w.r.t. vector addition: α (u
 *         + v) = α u + α v
 *
 *         - distributivity of scalar multiplication with respect to field
 *         addition: (α + β) v = α v + β v
 *
 *         - compatibility of scalar multiplication with field multiplication: α
 *         (β v) = (α β) v
 *
 *         - identity element of scalar multiplication: 1 v = v, where 1 denotes
 *         the multiplicative identity in F.
 *
 *         A Hilbert space {H, 〈⋅,⋅〉} is a vector space endowed with an inner
 *         product (which yields length and angle). A Hilbert space can have
 *         infinite dimension.
 *
 *         A Euclidean space is a vector space of finite dimension endowed with
 *         an inner product (which yields length and angle).
 *
 *         For our purposes (inverse problems and numerical optimization), we
 *         mainly consider vector spaces over the field of reals (either single
 *         or double precision floating points) of finite size (for obvious
 *         practical reasons) and endowed with an inner product. Mathematically
 *         speaking these kind of spaces are Euclidean spaces.
 *
 *         To achieve simple control of consistency and for clarity, operations
 *         involving vectors are implemented as methods of their vector space.
 *
 */
public abstract class VectorSpace {
    protected int size = 0; /* All vector spaces have a size. */
    protected int type = Utils.TYPE_OPAQUE; /* All vector spaces have a type. */

    protected VectorSpace(int size, int type) {
        if (size < 1) {
            throw new IllegalArgumentException("Bad vector space size.");
        }
        this.size = size;
        this.type = type;
    }
    public int getSize() {
        return size;
    }
    public int getType() {
        return type;
    }


    /**
     * Create a new vector from this vector space with undefined contents.
     * @return A new vector of this space.
     */
    public abstract Vector create();

    /**
     * Create a new vector from this vector space filled with given value.
     * @param alpha - A scalar value.
     * @return A new vector of this space.
     */
    public abstract Vector create(double alpha);

    /**
     * Compute the inner product of two vectors.
     *
     * The inner product, also called dot or scalar product of two vectors, is
     * the sum of the products of the corresponding elements of the two vectors.
     * The inner product is defined on a vector space, the two vectors must belong
     * to this vector space.
     * 
     * @param x - A vector of this vector space.
     * @param y - Another vector of this vector space.
     * @return The inner product of {@code x} and {@code y}.
     * @throws IncorrectSpaceException {@code x} and {@code y} must belong to this vector space.
     */
    public double dot(Vector x, Vector y) {
        check(x);
        check(y);
        return _dot(x, y);
    }

    /**
     * Compute the inner product of two vectors.
     *
     * This abstract method must be overwritten by its descendants to implement the
     * inner product.  The passed arguments are guaranteed to belong to this vector
     * space.
     *
     * @param x - A vector of this vector space.
     * @param y - Another vector of this vector space.
     * @return the inner product of X and Y.
     * @throws IncorrectSpaceException X and Y must belong to this vector space.
     */
    protected abstract double _dot(final Vector x, final Vector y)
            throws IncorrectSpaceException;

    /**
     * Compute the Euclidean (L2) norm of a vector.
     *
     * This basic implementation calls dot() method and is expected to be
     * overwritten with a more efficient version by the descendants of this
     * class.
     *
     * @param x   A vector.
     *
     * @return The square root of the sum of squared elements of x.
     * 
     * @throws IncorrectSpaceException x must belong to this vector space.
     */
    public double norm2(Vector x) throws IncorrectSpaceException {
        check(x);
        return _norm2(x);
    }

    protected double _norm2(Vector x) {
        return Math.sqrt(_dot(x, x));
    }

    /**
     * Compute the L1 norm of a vector.
     *
     * @param x   A vector.
     * 
     * @return The sum of absolute values of x.
     *
     * @throws IncorrectSpaceException x must belong to this vector space.
     */
    public double norm1(Vector x) throws IncorrectSpaceException {
        check(x);
        return _norm1(x);
    }
    protected abstract double _norm1(Vector x);

    /**
     * Compute the infinite norm of a vector.
     *
     * @param x   A vector.
     * 
     * @return The maximum absolute value of x.
     *
     * @throws IncorrectSpaceException x must belong to this vector space.
     */
    public double normInf(Vector x) throws IncorrectSpaceException {
        check(x);
        return _normInf(x);
    }
    protected abstract double _normInf(Vector x);

    /**
     * Compute a linear combination of two vectors.
     *
     * In pseudo-code, this method does:
     *
     * y[i] = alpha*x[i] + beta*y[i];
     *
     * for all indices i.
     *
     * This abstract method must be overwritten by its descendants. As this
     * method can be used to emulate other operations (as copy, zero, etc.),
     * actual code should be optimized for specific factors alpha and/or beta
     * equal to +/-1 or 0.  In particular when ALPHA is zero, then X must not
     * be referenced.
     *
     * @param alpha
     *            scalar factor for vector X
     * @param x
     *            the vector X
     * @param beta
     *            scalar factor for vector Y
     * @param y
     *            the vector Y (also used to store the result)
     * @throws IncorrectSpaceException X and Y must belong to this vector space.
     */
    public void axpby(double alpha, final Vector x,
            double beta, Vector y) throws IncorrectSpaceException {
        check(x);
        check(y);
        _axpby(alpha, x, beta, y);
    }

    protected void _axpby(double alpha, final Vector x,
            double beta, Vector y){
        _axpby(alpha, x, beta, y, y);
    }


    /**
     * Compute a linear combination of two vectors.
     *
     * In pseudo-code, this method does:
     *
     * dst[i] = alpha*x[i] + beta*y[i];
     *
     * for all indices i.
     *
     * This abstract method must be overwritten by its descendants. As this
     * method can be used to emulate other operations (as copy, zero, etc.),
     * actual code should be optimized for specific factors alpha and/or beta
     * equal to +/-1 or 0.  In particular when ALPHA (or BETA) is zero, then X
     * (or Y) must not be referenced.
     *
     * @param alpha
     *            scalar factor for vector X
     * @param x
     *            the vector X
     * @param beta
     *            scalar factor for vector Y
     * @param y
     *            the vector Y
     * 
     * @param dst
     *            the destination vector
     *
     * @throws IncorrectSpaceException X and Y must belong to this vector space.
     */
    public void axpby(double alpha, final Vector x,
            double beta, Vector y, Vector dst) throws IncorrectSpaceException {
        check(x);
        check(y);
        check(dst);
        _axpby(alpha, x, beta, y, dst);
    }

    protected abstract void _axpby(double alpha, final Vector x,
            double beta, Vector y, Vector dst);

    /**
     * Compute a linear combination of three vectors.
     *
     * In pseudo-code, this method does:
     *
     * dst[i] = alpha*x[i] + beta*y[i] + gamma*z[i];
     *
     * for all indices i.
     *
     * This abstract method must be overwritten by its descendants.
     *
     * @param alpha
     *            scalar factor for vector X
     * @param x
     *            the vector X
     * @param beta
     *            scalar factor for vector Y
     * @param y
     *            the vector Y
     * 
     * @param gamma
     *            scalar factor for vector Z
     * @param z
     *            the vector Z
     * @param dst
     *            the destination vector
     *
     * @throws IncorrectSpaceException X and Y must belong to this vector space.
     */
    public void axpbypcz(double alpha, final Vector x,
            double beta,  final Vector y,
            double gamma, final Vector z,
            Vector dst) throws IncorrectSpaceException {
        check(x);
        check(y);
        check(z);
        check(dst);
        _axpbypcz(alpha, x, beta, y, gamma, z, dst);
    }
    protected abstract void _axpbypcz(double alpha, final Vector x,
            double beta,  final Vector y,
            double gamma, final Vector z, Vector dst);

    /**
     * Copy the contents of a vector into another one.
     *
     * This basic implementation calls axpby() method and is expected to be
     * overwritten with a more efficient version by the descendants of this
     * class.
     *
     * @param src
     *            - source vector
     * @param dst
     *            - destination vector
     * @throws IncorrectSpaceException SRC and DST must belong to this vector space.
     */
    public void copy(final Vector src, Vector dst)
            throws IncorrectSpaceException {
        check(src);
        if (dst != src) {
            check(dst);
            _copy(src, dst);
        }
    }
    protected void _copy(Vector src, Vector dst)
            throws IncorrectSpaceException {
        _axpby(1.0, src, 0.0, dst);
    }

    /**
     * Create a new vector as a clone of another vector.
     *
     * As implemented in the base class, this method is not optimized but
     * provides the reference behavior.
     *
     * @param vec - The vector to clone (must belongs to this vector space).
     * @return A clone of the input vector.
     * @throws IncorrectSpaceException V must belong to this vector space.
     */
    public Vector clone(Vector vec) throws IncorrectSpaceException {
        check(vec);
        return _clone(vec);
    }
    /**
     * Create a new vector from this vector space as a copy of another vector.
     *
     * This protected method is called by {@link #clone} to do the real work after
     * checking the argument.  Derived classes can implement a more efficient
     * version than this one which is based on the {@link #create} method and the
     * {@link #_copy} protected method.
     * @param vec - A vector from this space (this has been checked).
     * @return A new duplicate copy of the vector {@code v}.
     */
    protected Vector _clone(Vector vec) {
        Vector cpy = create();
        _copy(vec, cpy);
        return cpy;
    }

    /**
     * Create a new vector of one's.
     * @return A new vector of this space filled with ones.
     */
    public Vector one() {
        return create(1.0);
    }

    /**
     * Create a new vector of zero's.
     * @return A new vector of this space filled with zeros.
     */
    public Vector zero() {
        return create(0.0);
    }

    /**
     * Fill a vector with zeros.
     *
     * This basic implementation calls axpby() method and is expected to be
     * overwritten with a more efficient version by the descendants of this
     * class.
     *
     * @param v   A vector of this space.
     * @throws IncorrectSpaceException V must belong to this vector space.
     */
    public void zero(Vector v) {
        check(v);
        _zero(v);
    }
    protected void _zero(Vector v) {
        _fill(v, 0.0);
    }

    public void fill(Vector x, double alpha) {
        check(x);
        _fill(x, alpha);
    }
    protected abstract void _fill(Vector x, double alpha);


    /**
     * Check whether a given vector belongs to the vector space.
     *
     * @param v - A vector.
     * @return True if {@code v} is not {@code null} and belongs to this vector space.
     */
    public final boolean owns(Vector v) {
        return (v != null && v.belongsTo(this));
    }

    /**
     * Make sure a given vector belongs to the vector space.
     *
     * This method throws an {@code IncorrectSpaceException} exception
     * if its argument does not belong to the vector space.
     *
     * @param v   A vector to check.
     * @throws IncorrectSpaceException V must belong to this vector space.
     */
    public final void check(Vector v) throws IncorrectSpaceException {
        if (! owns(v)) {
            throw new IncorrectSpaceException();
        }
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
    protected static int computeSize(int[] shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Illegal NULL shape.");
        }
        int size = 1;
        for (int r = 0; r < shape.length; ++r) {
            if (shape[r] <= 0) {
                throw new IllegalArgumentException("Bad dimension length.");
            }
            size *= shape[r];
        }
        return size;
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
