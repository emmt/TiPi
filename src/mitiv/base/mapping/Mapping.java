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

package mitiv.base.mapping;

import mitiv.exception.IncorrectSpaceException;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

/**
 * This abstract class implements mappings between two vector spaces.
 *
 * @author Éric Thiébaut
 */
public abstract class Mapping {
    /** The input vector space. */
    protected final VectorSpace inputSpace;

    /** The output vector space. */
    protected final VectorSpace outputSpace;

    protected Mapping(VectorSpace inp, VectorSpace out) {
        inputSpace = inp;
        outputSpace = out;
    }

    protected Mapping(VectorSpace space) {
        inputSpace = space;
        outputSpace = space;
    }

    /**
     * Get the input space of a mapping.
     *
     * @return The input space of the mapping.
     */
    public VectorSpace getInputSpace() {
        return inputSpace;
    }

    /**
     * Get the output space of a mapping.
     *
     * @return The output space of the mapping.
     */
    public VectorSpace getOutputSpace() {
        return outputSpace;
    }

    /**
     * Check whether a mapping is an endomorphism.
     *
     * @return <tt>true</tt> is the input and output spaces of the mapping
     *         are the same; <tt>false</tt> otherwise.
     */
    public boolean isEndomorphism() {
        return (outputSpace == inputSpace);
    }

    /**
     * Apply a mapping to a vector.
     *
     * @param dst - The destination vector.
     * @param src - The source vector.
     *
     * @throws IncorrectSpaceException
     *         Vector {@code src} must belongs to the input vector space of
     *         the mapping and {@code dst} must belongs to the output vector
     *         space of the mapping.
     */
    public void apply(Vector dst, Vector src)
            throws IncorrectSpaceException {
        if (! outputSpace.owns(dst)) {
            throw new IncorrectSpaceException("Destination does not belong to the output space");
        }
        if (! inputSpace.owns(src)) {
            throw new IncorrectSpaceException("Source does not belong to the input space");
        }
        _apply(dst, src);
    }

    protected abstract void _apply(Vector dst, Vector src);

}

