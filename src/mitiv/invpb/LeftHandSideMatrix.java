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

package mitiv.invpb;

import mitiv.exception.IllegalLinearOperationException;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

public class LeftHandSideMatrix extends LinearOperator {
    private LinearOperator H;
    private LinearOperator W;
    private LinearOperator Q;
    private double mu;
    private Vector tmp1;
    private Vector tmp2;

    /**
     * Create a linear operator suitable to iteratively solve a linear inverse problem.
     * 
     * The general form of a linear inverse problem is:
     * 
     *     x = arg min (H.x - y)'.W.(H.x - y) + mu*x'.Q.x
     * 
     * which amounts to solve the linear system:
     * 
     *     A.x = b
     *     
     * The left hand side matrix of the linear inverse problem is:
     * 
     *     A = H'.W.H + mu*Q
     *     
     * and the right hand side vector of the linear inverse problem is:
     * 
     *     b = H'.W.y
     *     
     * As a consequence, the linear operator A is an endomorphism operating on the
     * input space of H (Q should also be an endomorphism on the same space and W
     * should be an endomorphism on the output space of H). 
     * 
     * @param H    A linear operator which implements the direct model of the data.
     * @param W    A linear operator which implements multiplication by the
     *             statistical weights (a.k.a. precision matrix).
     * @param Q    A linear operator which implements the regularization.
     * @param mu   The regularization relative weight (must be non-negative).
     */
    public LeftHandSideMatrix(LinearOperator H, LinearOperator W, LinearOperator Q, double mu) {
        super(H.getInputSpace());
        if (W.getOutputSpace() != W.getInputSpace()) {
            throw new IllegalArgumentException("Linear operator W must be an endomorphism.");
        }
        if (W.getInputSpace() != H.getOutputSpace()) {
            throw new IllegalArgumentException("Linear operator W must operate on output space of H.");
        }
        if (Q.getOutputSpace() != Q.getInputSpace()) {
            throw new IllegalArgumentException("Linear operator Q must be an endomorphism.");
        }
        if (Q.getInputSpace() != H.getInputSpace()) {
            throw new IllegalArgumentException("Linear operator Q must operate on input space of H.");
        }
        this.H = H;
        this.W = W;
        this.Q = Q;
        setMu(mu);
    }


    /**
     * Apply the LHS operator A = Ht.W.H + mu*Q
     */
    protected void _apply(Vector dst, Vector src, int job) {
        if (job != DIRECT) {
            throw new IllegalLinearOperationException();
        }


        /*
         * First do: dst = Ht.W.H.src
         * using tmp1 as a scratch vector (from output space of H).
         */
        if (tmp1 == null) {
            tmp1 = H.getOutputSpace().create();
        }
        H.apply(tmp1, src);
        W.apply(tmp1, tmp1); // W can be done in place
        H.apply(dst, tmp1, ADJOINT);

        if (mu > 0.0) {
            /*
             * Second, compute tmp2 = Q.src, then add mu*tmp2 to dst.
             */
            if (tmp2 == null) {
                VectorSpace space = Q.getOutputSpace();
                if (tmp1.belongsTo(space)) {
                    tmp2 = tmp1;
                } else {
                    tmp2 = space.create();
                }
            }
            Q.apply(tmp2, src);
            outputSpace.combine(mu, tmp2, 1.0, dst);    
        }
    }

    /**
     * @return double value mu
     */
    public double getMu() {
        return this.mu;
    }

    /**
     * @param mu Set the mu value
     * 
     */
    public void setMu(double mu) {
        if (mu < 0.0) {
            throw new IllegalArgumentException("Regularization weight MU must be non-negative.");            
        }
        this.mu = mu;
    }

    /**
     * Compute the right-hand-side vector of the linear inverse problem.
     *
     * @param y The source vector (the data).
     * @param b The destination vector (the right-hand-side vector of the linear problem).
     */
    public void computeRightHandSideVector(Vector y, Vector b) {
        if (tmp1 == null) {
            tmp1 = H.getOutputSpace().create();
        }
        W.apply(tmp1, y);
        H.apply(b, tmp1, ADJOINT);
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
