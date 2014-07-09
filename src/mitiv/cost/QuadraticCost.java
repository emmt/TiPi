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

package mitiv.cost;

import mitiv.exception.IncorrectSpaceException;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

/**
 * Implementation of a quadratic cost function.
 * 
 * A general form of a quadratic cost is:
 * <pre>
 *     f(x) = (H.x - y)'.W.(H.x - y)
 * </pre>
 * where {@code H} and {@code W} are linear operators and {@code y} is a vector
 * of the the output space of {@code H}.
 * 
 * @author Éric Thiébaut <eric.thiebaut@univ-lyon1.fr>
 */
public class QuadraticCost implements DifferentiableCostFunction {
    /** The vector space for the variables {@code x}. */
    protected VectorSpace inputSpace = null;

    /** The linear operator {@code H}. */
    protected LinearOperator H = null;

    /** The statistical weights {@code W}, as a linear operator. */
    protected LinearOperator W = null;

    /** The "data" or "prior" vector {@code y}. */
    protected Vector y = null;

    /** Cached vector to store the (anti-)residuals.
     * The (anti-)residuals are: {@code r = H.x - y}.  If {@code H} and {@code y} are
     * {@code null} then {@code r = x} and the same storage can be used for {@code r}
     * and {@code x} however {@code r} is not writable in this case and thus not usable
     * for {@code Wr} unless {@code W} is also {@code null} and thus {@Wr = r}. */
    protected Vector r = null;

    /** Cached vector to store the weighted (anti-)residuals: {@code Wr = W.r = W.(H.x - y)} */
    protected Vector Wr = null;

    /** Cached vector to store the quasi gradient: {@code HtWr = H'.Wr = H'.W.r = H'.W.(H.x - y)} */
    protected Vector HtWr = null;

    protected boolean quickResiduals;         // H and y are null, thus r = x
    protected boolean quickWeightedResiduals; // W is null, thus W.r = r
    protected boolean quickQuasiGradient;     // H is null, thus H'.W.r = W.r
    protected boolean shareMemory;            // share storage between residuals r and quasi-gradients HtWr

    /** Constructor for a general quadratic cost function.
     * 
     * Create an instance of the differentiable cost function:
     * <pre>
     *     f(x) = (H.x - y)'.W.(H.x - y)
     * </pre>
     * where {@code H} and {@code W} are linear operators and {@code y} is a vector
     * of the the output space of {@code H}.  Operator {@code W} must map the output
     * space of {@code H} to the output space of {@code H}.  Operator {@code W} should
     * be symmetric and positive definite (respectively semi-definite) for {@code f(x)}
     * to be strictly convex (respectively convex).
     * 
     * <p>
     * Having any of these arguments equal to {@code null} amounts to ignore them in the
     * formula.  Thats is, {@code y = null} is the same (but faster) as having {@code y}
     * a vector of zeros; {@code H = null} (or {@code W = null}) is the same (but faster)
     * as assuming that {@code H} (or of {@code W}) is the identity.  Though not all the
     * arguments can be {@code null} as at least one of these is needed to determine the
     * input space of {@code f(x)}.
     * 
     * <p>
     * Note that none of the components {@code H}, {@code y}, nor {@code W} are copied.
     * If the caller does change one of these while using the cost function, the cost
     * function will use the actual contents of the components.  If this is inappropriate,
     * the cost must be instantiated with copies of the components which are subject to
     * changes.
     * 
     * @param H  - A linear operator.
     * @param y  - A vector of the output space of {@code H}.
     * @param W  - An endomorphism of the output space of {@code H}..
     */
    public QuadraticCost(LinearOperator H, Vector y, LinearOperator W) {
        setComponents(H, y, W);
    }

    /** Constructor for most simple quadratic cost.
     * This constructor build an instance if the most simple quadratic cost function:
     * <pre>
     *     f(x) = ||x||^2
     * </pre>
     * 
     * @param space - The vector space of the variables.
     */
    public QuadraticCost(VectorSpace space) {
        inputSpace = space;
    }

    public QuadraticCost(LinearOperator H, Vector y) {
        this(H, y, null);
    }

    public QuadraticCost(LinearOperator H) {
        this(H, null, null);
    }

    /** Set the components of the quadratic cost.
     * See the constructor {@link #QuadraticCost(LinearOperator, Vector, LinearOperator)}
     * for a comprehensive description of the arguments.
     * @param H  - A linear operator.
     * @param y  - A vector of the output space of {@code H}.
     * @param W  - An endomorphism of the output space of {@code H}..
     */
    public void setComponents(LinearOperator H, Vector y, LinearOperator W) {
        /* Check consistency of the arguments. */
        VectorSpace inputSpace, outputSpace;
        if (H != null) {
            inputSpace = H.getInputSpace();
            outputSpace = H.getOutputSpace();
        } else {
            inputSpace = null;
            outputSpace = null;
        }
        if (y != null) {
            if (outputSpace == null) {
                outputSpace = y.getSpace();
                inputSpace = outputSpace;
            } else if (! y.belongsTo(outputSpace)) {
                throw new IncorrectSpaceException("y must belong to the output space of H");
            }
        }
        if (W != null) {
            if (! W.isEndomorphism()) {
                throw new IncorrectSpaceException("W must be an endomorphism");
            } else if (outputSpace == null) {
                outputSpace = W.getInputSpace();
                inputSpace = outputSpace;
            } else if (W.getInputSpace() != outputSpace) {
                throw new IncorrectSpaceException("incompatible vector space for operator W");
            }
        }
        if (inputSpace == null) {
            throw new IllegalArgumentException("one of H, y, or W must be non null");
        }

        /* Instantiate components and cleanup cache. */
        this.H = H;
        this.y = y;
        this.W = W;
        this.inputSpace = inputSpace;
        quickResiduals = (H == null && y == null);
        quickWeightedResiduals = (W == null);
        quickQuasiGradient = (H == null);
        shareMemory = (! quickResiduals && inputSpace == outputSpace);
        if (quickResiduals || ! r.belongsTo(outputSpace)) {
            r = null;
        }
        if (quickWeightedResiduals || ! Wr.belongsTo(outputSpace)) {
            Wr = null;
        }
        if (shareMemory) {
            HtWr = r;
        } else if (quickQuasiGradient || ! HtWr.belongsTo(inputSpace)) {
            HtWr = null;
        }
    }

    @Override
    public VectorSpace getInputSpace() {
        return inputSpace;
    }

    @Override
    public double evaluate(double alpha, Vector x) {
        /* Deal with a zero multiplier. */
        if (alpha == 0.0) {
            return 0.0;
        }

        /* Form the (anti-)residuals : r = H.x - y, their weighted counterpart Wr = W.r
         * and compute the quadratic cost q. */
        formResiduals(x);
        double q = r.dot(Wr);

        /* Cleanup any alias made so far. */
        if (quickResiduals) {
            r = null;
        }
        if (quickWeightedResiduals) {
            Wr = null;
        }

        /* Return the scaled cost. */
        return alpha*q;
    }

    @Override
    public double computeCostAndGradient(double alpha, Vector x, Vector gx, boolean clr) {
        /* Deal with a zero multiplier. */
        if (alpha == 0.0) {
            if (clr) {
                gx.zero();
            }
            return 0.0;
        }

        /* Form the residuals and compute the quadratic cost. */
        formResiduals(x);
        double q = r.dot(Wr);

        /* Compute/integrate the gradients. */
        if (quickQuasiGradient) {
            HtWr = Wr;
        } else {
            if (HtWr == null || ! HtWr.belongsTo(inputSpace)) {
                /* Create/find workspace to store HtWr = H'.W.r with the constraints that
                 * HtWr must be writable (thus not x) and must not be Wr as operator H is
                 * not warranted to be applicable in-place. */
                if (shareMemory) {
                    HtWr = r;
                } else {
                    HtWr = inputSpace.create();
                }
            }
            H.apply(Wr, HtWr, LinearOperator.ADJOINT);
        }
        inputSpace.axpby((clr ? 0.0 : 1.0), gx, 2.0*alpha, HtWr, gx);

        /* Cleanup any alias made so far. */
        if (quickResiduals) {
            r = null;
        }
        if (quickWeightedResiduals) {
            Wr = null;
        }
        if (quickQuasiGradient) {
            HtWr = null;
        }

        /* Return the quadratic cost times the multiplier. */
        return alpha*q;
    }

    /** Form the (anti-)residuals and their weighted counterpart.
     * 
     * This method updates the (anti-)residuals {@code r = H.x - y} and their weighted
     * counterpart {@code Wr = W.r} which are internally stored by the instance.
     * <p>
     * There are 4 different cases for the (anti-)residuals {@code r = H.x - y}
     * depending whether {@code H} and/or {@code y} are specified:
     * <pre>
     *     r = H.x - y    (H != null && y != null)
     *     r = H.x        (H != null && y == null)
     *     r = x - y      (H == null && y != null)
     *     r = x          (H == null && y == null)
     * </pre>
     * The weighted residuals are:
     * <pre>
     *     Wr = W.r       (W != null)
     *     Wr = r         (W == null)
     * </pre>
     */
    private void formResiduals(Vector x) {
        // Figure out the vector space for the residuals:
        VectorSpace innerSpace = (H == null ? inputSpace : H.getOutputSpace());

        /* Form the (anti-)residuals: r = H.x - y.  There are 4 different cases. */
        if (quickResiduals) {
            /* The (anti-)residuals are just an alias to X. */
            r = x;
        } else {
            /* The (anti-)residuals are: r = x - y, or r = H.x, or r = H.x - y. */
            if (r == null || ! r.belongsTo(innerSpace)) {
                r = innerSpace.create();
            }
            if (H == null) {
                /* The (anti-)residuals are: r = x - y. */
                innerSpace.axpby(1.0, x, -1.0, y, r);
            } else {
                /* The (anti-)residuals are: r = H.x or r = H.x - y. */
                H.apply(x, r);
                if (y != null) {
                    innerSpace.axpby(1.0, r, -1.0, y, r);
                }
            }
        }

        /* Form the weighted (anti-)residuals: Wr = W.r. */
        if (quickWeightedResiduals) {
            /* Same as if W is the identity: Wr = r.  Thus the weighted (anti-)residuals
             * are just an alias to r. */
            Wr = r;
        } else {
            if (Wr == null || ! Wr.belongsTo(innerSpace)) {
                Wr = innerSpace.create();
            }
            W.apply(r, Wr);
        }
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
