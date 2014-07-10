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

import mitiv.linalg.Utils;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

public class GradientChecker {
    static final int FORWARD_DIFFERENCE  =  1;
    static final int BACKWARD_DIFFERENCE = -1;
    static final int CENTERED_DIFFERENCE =  0;
    static final double MINIMAL_EPSILON = Utils.DBL_EPSILON;
    static final double DEFAULT_EPSILON = Math.max(MINIMAL_EPSILON, 1e-3);
    private final VectorSpace space;
    private double stepScale = DEFAULT_EPSILON;
    private double minStep = Double.MIN_NORMAL;
    private Vector x = null;
    private Vector y = null;
    private double fx;
    private Vector gx = null;
    private final DifferentiableCostFunction f;
    private int method = FORWARD_DIFFERENCE;

    /**
     * Create a gradient checker.
     * @param cost - The differentiable cost function to check.
     */
    public GradientChecker(DifferentiableCostFunction cost) {
        this.f = cost;
        this.space = cost.getInputSpace();
    }

    /**
     * Set the value of the variables.
     *
     * Setting the variables must be done prior to any gradient check and trigger
     * the computation of the function and its gradient at the position of the
     * variables.
     *
     * @param x     - The value of the variables where to perform the check.
     * @param clone - Indicate whether the vector {@code x} should be cloned;
     *                otherwise, a simple reference to {@code x} is stored.
     */
    public void setVariables(Vector x, boolean clone) {
        if (clone) {
            if (this.x == null) {
                this.x = space.clone(x);
            } else {
                space.copy(x,  this.x);
            }
        } else {
            this.x = x;
        }
        if (gx == null) {
            gx = space.create();
        }
        fx = f.computeCostAndGradient(1.0, x, gx, true);
    }

    /**
     * Set the relative size of the finite difference step.
     * @param value - The relative step size.  If smaller than {@code MINIMAL_EPSILON},
     *                the value {@ DEFAULT_EPSILON} is taken.
     */
    public void setStepScale(double value) {
        if (value < MINIMAL_EPSILON) {
            stepScale = value;
        } else {
            stepScale = DEFAULT_EPSILON;
        }
    }

    /**
     * Get the relative step size.
     * @return The value of the relative step size.
     */
    public final double getStepScale() {
        return stepScale;
    }

    /**
     * Set the minimum size of the finite difference step.
     * @param value - The minimum step size.  If negative, 0 is taken.
     */
    public void setMinStep(double value) {
        minStep = Math.max(value, 0.0);
    }

    /**
     * Get the minimal step size.
     * @return The value of the minimal step size.
     */
    public final double getMinStep() {
        return minStep;
    }

    /**
     * Set the finite difference method.
     * @param value - The finite difference method to use.  Backward differences if
     *                {@code value < 0}; forward differences if {@code value > 0};
     *                centered differences otherwise.
     */
    public void setMethod(int value) {
        if (value < 0) {
            method = BACKWARD_DIFFERENCE;
        } else if (value > 0) {
            method = FORWARD_DIFFERENCE;
        } else {
            method = CENTERED_DIFFERENCE;
        }
    }

    /**
     * Get finite difference method.
     * @return A value indicating the current finite differences method
     *         used to approximate the gradient: -1 ({@code BACKWARD_DIFFERENCE}),
     *         0 ({@code CENTERED_DIFFERENCE}), or +1 ({@code FORWARD_DIFFERENCE}).
     */
    public final int getMethod() {
        return method;
    }

    public void check(int i) {
        check(new int[] {i});
    }

    public void check(int[] idx) {
        if (gx == null) {
            System.err.println("Set variables first.");
            return;
        }
        if (y == null) {
            y = space.clone(x);
        } else {
            space.copy(x, y);
        }
        for (int k = 0; k < idx.length; ++k) {
            int j = idx[k];
            double xj = x.get(j);
            double h = stepSize(xj);
            double gxj = gx.get(j);
            double gxj_approx;
            if (method == BACKWARD_DIFFERENCE) {
                y.set(j, xj - h);
                double fy = f.evaluate(1.0, y);
                gxj_approx = (fx - fy)/h;
            } else if (method == CENTERED_DIFFERENCE) {
                y.set(j, xj - h);
                double f1 = f.evaluate(1.0, y);
                y.set(j, xj + h);
                double f2 = f.evaluate(1.0, y);
                gxj_approx = (f2 - f1)/(h + h);
            } else {
                /* Assume forward differences. */
                y.set(j, xj + h);
                double fy = f.evaluate(1.0, y);
                gxj_approx = (fy - fx)/h;
            }
            y.set(j, xj);
            double relativeError = Math.abs(relativeDifference(gxj, gxj_approx));
            System.out.printf("gx[%6d] = %20.12E .:. (fx - fy)/h = %20.12E .:. relative error =%8.1E\n",
                    j, gxj, gxj_approx, relativeError);
        }
    }

    /**
     * Compute relative difference.
     * @param a - Left operand.
     * @param b - Right operand.
     * @return The difference {@code a - b} divided by the largest magnitude
     *         of {@code a} and {@code b}.  Division by zero is avoided by
     *         returning 0 whenever {@code a} and {@code b} are equal.
     */
    public static double relativeDifference(double a, double b) {
        if (a == b) {
            return 0.0;
        } else {
            return (a - b)/Math.max(Math.abs(a), Math.abs(b));
        }
    }

    /**
     * Compute a small step size.
     *
     * This method computes a small but non-negligible step size given the
     * value of the parameter to pertubate.
     *
     * @param x - The value of the parameter to pertubate.
     * @return A small step size
     */
    public double stepSize(double x) {
        double h = Math.max(stepScale*Math.abs(x), minStep);
        if (h <= 0.0) {
            h = stepScale;
        }
        while (true) {
            double tmp = x + h;
            if (tmp != x) return h;
            h += h;
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
