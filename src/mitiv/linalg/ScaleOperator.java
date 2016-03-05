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

import mitiv.exception.SingularOperatorException;

public class ScaleOperator extends LinearEndomorphism {

    protected double scale = 1.0;

    /**
     * Create a new scale operator which operates on a given vector space.
     *
     * The returned operator has a scaling factor equal to 1 and is thus
     * similar to the identity.
     * @param vsp
     *            the vector space.
     */
    public ScaleOperator(VectorSpace vsp) {
        super(vsp);
    }

    /**
     * Create a new scale operator which operates on a given vector space.
     *
     * @param vsp
     *            the vector space.
     * @param alpha
     *            the scaling factor.
     */
    public ScaleOperator(VectorSpace vsp, double alpha) {
        super(vsp);
        this.scale = alpha;
    }

    @Override
    protected void _apply(Vector dst, final Vector src, int job) {
        if (job == DIRECT || job == ADJOINT) {
            if (scale == 0.0) {
                space._fill(dst, 0.0);
            } else if (scale == 1.0) {
                if (dst != src) {
                    space._copy(dst, src);
                }
            } else {
                if (dst == src) {
                    space._scale(dst, scale);
                } else {
                    space._combine(dst, scale, src, 0.0, dst);
                }
            }
        } else {
            if (scale == 1.0) {
                if (dst != src) {
                    space._copy(dst, src);
                }
            } else if (scale != 0.0) {
                if (dst == src) {
                    space._scale(dst, 1.0/scale);
                } else {
                    space._combine(dst, 1.0/scale, src, 0.0, dst);
                }
            } else {
                throw new SingularOperatorException();
            }
        }

    }

    /**
     * Get the scaling factor of a scale operator.
     * @return the scaling factor of the operator.
     */
    public double getScale() {
        return scale;
    }

    /**
     * Set the scaling factor of a scale operator.
     * @param alpha
     *            the new scaling factor.
     */
    public void setScale(double alpha) {
        this.scale = alpha;
    }

}
