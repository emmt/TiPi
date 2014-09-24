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

package mitiv.deconv;

import mitiv.exception.IllegalLinearOperationException;
import mitiv.exception.IncorrectSpaceException;
import mitiv.linalg.LinearOperator;
import mitiv.linalg.Vector;
import mitiv.linalg.shaped.DoubleShapedVector;

/**
 * Weighting operator will apply a vector give to the constructor 
 * to the src and write it in dest vector
 * 
 * Will be deleted in near future, if still alive kill it.
 * @author Leger Jonathan
 *
 */
public class WeightingOperator extends LinearOperator {
    Vector weight;
    /**
     * Constructor ^^
     * 
     * @param weight
     */
    public WeightingOperator(final Vector weight) {
        super(weight.getSpace());
        this.weight = weight;
    }

    @Override
    protected void privApply(Vector src, Vector dst, int job)
            throws IncorrectSpaceException {
        int n = inputSpace.getNumber();
        DoubleShapedVector vectSrc = (DoubleShapedVector)src; // FIXME: should be more general
        DoubleShapedVector vectDst = (DoubleShapedVector)dst;
        DoubleShapedVector vectW = (DoubleShapedVector)weight;
        if (job == INVERSE) {
            for (int i = 0; i < n; i++) {
                vectDst.set(i, vectSrc.get(i)/vectW.get(i));
            }
        } else if (job == DIRECT) {
            for (int i = 0; i < n; i++) {
                vectDst.set(i, vectSrc.get(i)*vectW.get(i));
            }
        } else {
            throw new IllegalLinearOperationException("This job is not possible");
        }
    }

    /**
     * A gettterrr
     * @return
     */
    public Vector getWeight(){
        return weight;
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
