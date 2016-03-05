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

package mitiv.optim;

public enum InverseHessianApproximation {

    /**
     * The inverse Hessian is approximated by the identity.
     */
    NONE(0, "1"),

    /**
     * Approximate the inverse Hessian by a scaling by the Rayleigh quotient
     * of the mean inverse Hessian in the direction of the first gradient
     * difference.
     * <p>
     * This approximation is a scaling by the latest value of:
     * </p><p align="center">
     * <i>&gamma;</i>'<sub>0</sub> =
     * <i>s</i><sub>0</sub><sup>t</sup>.<i>s</i><sub>0</sub>/<i>s</i><sub>0</sub><sup>t</sup>.<i>y</i><sub>0</sub>
     * </p>
     */
    BY_INITIAL_STS_OVER_STY(1, "<s0,s0>/<s0,y0>"),

    /**
     * Approximate the inverse Hessian by a scaling by the reciprocal of the
     * Rayleigh quotient of the mean Hessian in the direction of the first
     * step.
     * <p>
     * This approximation is a scaling by the latest value of:
     * </p><p align="center">
     * <i>&gamma;</i>''<sub>0</sub> =
     * <i>s</i><sub>0</sub><sup>t</sup>.<i>y</i><sub>0</sub>/<i>y</i><sub>0</sub><sup>t</sup>.<i>y</i><sub>0</sub>
     * </p>
     */
    BY_INITIAL_STY_OVER_YTY(2, "<s0,y0>/<y0,y0>"),

    /**
     * Approximate the inverse Hessian by a scaling by the Rayleigh quotient
     * of the mean inverse Hessian in the direction of the latest gradient
     * difference.
     * <p>
     * This approximation is a scaling by the latest value of:
     * </p><p align="center">
     * <i>&gamma;</i>'<sub><i>k</i></sub> =
     * <i>s</i><sub><i>k</i></sub><sup>t</sup>.<i>s</i><sub><i>k</i></sub>/<i>s</i><sub><i>k</i></sub><sup>t</sup>.<i>y</i><sub><i>k</i></sub>
     * </p><p>
     * This approximation was introduced by Shanno and works well with limited
     * memory quasi-Newton methods and conjugate gradient methods with monotone
     * line searches.
     * </p>
     */
    BY_STS_OVER_STY(3, "<sk,sk>/<sk,yk>"),

    /**
     * Approximate the inverse Hessian by a scaling by the reciprocal of the
     * Rayleigh quotient of the mean Hessian in the direction of the latest
     * step.
     * <p>
     * This approximation is a scaling by the latest value of:
     * </p><p align="center">
     * <i>&gamma;</i>''<sub><i>k</i></sub> =
     * <i>s</i><sub><i>k</i></sub><sup>t</sup>.<i>y</i><sub><i>k</i></sub>/<i>y</i><sub><i>k</i></sub><sup>t</sup>.<i>y</i><sub><i>k</i></sub>
     * </p><p>
     * This approximation works well with nonmonotone line search.
     * </p>
     */
    BY_STY_OVER_YTY(4, "<sk,yk>/<yk,yk>"),

    /**
     * The approximation of the inverse Hessian is provided by a preconditionner.
     */
    BY_USER(5, "(user)");

    private int identifier;
    private String description;

    InverseHessianApproximation(int id, String descr) {
        identifier = id;
        description = descr;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getDescription() {
        return description;
    }

}
