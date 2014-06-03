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
     *
     * This abstract method must be overwritten by its descendants.
     *
     * @return a new vector of this space.
     */
    public abstract Vector create();

    /**
     * Create a new vector from this vector space filled with given value.
     *
     * This abstract method must be overwritten by its descendants.
     *
     * @param alpha
     *            a scalar value.
     * @return a new vector of this space.
     */
    public abstract Vector create(double alpha);

    /**
     * Compute the inner product of two vectors.
     *
     * This abstract method must be overwritten by its descendants.
     *
     * @param x
     *            a vector
     * @param y
     *            another vector
     * @return the inner product of X and Y.
     * @throws IncorrectSpaceException X and Y must belong to this vector space.
     */
    public abstract double dot(final Vector x, final Vector y)
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
    public double norm2(Vector x)
            throws IncorrectSpaceException {
        return Math.sqrt(dot(x, x));
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
    public abstract double norm1(Vector x)
            throws IncorrectSpaceException;

    /**
     * Compute the infinite norm of a vector.
     *
     * @param x   A vector.
     * 
     * @return The maximum absolute value of x.
     *
     * @throws IncorrectSpaceException x must belong to this vector space.
     */
    public abstract double normInf(Vector x)
            throws IncorrectSpaceException;

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
    public abstract void axpby(double alpha, final Vector x,
                               double beta,        Vector y) throws IncorrectSpaceException;


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
    public abstract void axpby(double alpha, final Vector x,
                               double beta,  final Vector y,
                               Vector dst) throws IncorrectSpaceException;

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
    public abstract void axpbypcz(double alpha, final Vector x,
                                  double beta,  final Vector y,
                                  double gamma, final Vector z,
                                  Vector dst) throws IncorrectSpaceException;

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
        axpby(1.0, src, 0.0, dst);
    }

    /**
     * Create a new vector as a clone of another vector.
     *
     * As implemented in the base class, this method is not optimized but
     * provides the reference behavior.
     *
     * @param v
     *            the vector to clone (must belongs to this vector space).
     * @return A clone of the input vector.
     * @throws IncorrectSpaceException V must belong to this vector space.
     */
    public Vector clone(Vector v) throws IncorrectSpaceException {
        if (!v.belongsTo(this)) {
            throw new IncorrectSpaceException();
        }
        Vector u = create();
        copy(v, u);
        return u;
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
        axpby(0.0, v, 0.0, v);
    }

    public void check(Vector v) throws IncorrectSpaceException {
        if (! v.belongsTo(this)) {
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
