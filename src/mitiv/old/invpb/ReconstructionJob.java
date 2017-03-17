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

package mitiv.old.invpb;

import mitiv.array.ShapedArray;

/**
 * Interface to allow external viewers display information during an
 * iterative reconstruction.
 *
 * @author Éric and Jonathan
 */
@Deprecated
public interface ReconstructionJob {

    /** Get the current reconstruction result. */
    public abstract ShapedArray getResult();

    /** Get the current number of iterations. */
    public abstract int getIterations();

    /** Get the current number of function evaluations. */
    public abstract int getEvaluations();

    /** Get the current value of the objective function. */
    public abstract double getCost();

    /**
     * Get the Euclidean (L2) norm of the current gradient of the objective *
     * function.
     */
    public abstract double getGradientNorm2();

    /** Get the L1 norm of the current gradient of the objective function. */
    public abstract double getGradientNorm1();

    /**
     * Get the infinite norm of the current gradient of the objective
     * function.
     */
    public abstract double getGradientNormInf();

    public abstract double getRelativeTolerance();
}
