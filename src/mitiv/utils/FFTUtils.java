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

package mitiv.utils;

public class FFTUtils {
    /**
     * This class is not instantiable, it only provides static methods.
     */
    protected FFTUtils() {}

    /**
     * Compute the best dimension for the FFT.
     * @param dim - The minimal length.
     * @return The smallest integer which is greater or equal {@code len}
     * and which is a multiple of powers of 2.
     */
    public static int bestPowerOfTwo(int dim) {
        final int maxDim = (1 << 30);
        if (dim > maxDim) {
            throw new IllegalArgumentException("Integer overflow");
        }
        int best = 1;
        while (best < dim) {
            best *= 2;
        }
        return best;
    }

    /**
     * Compute the best dimension for the FFT.
     * @param dim - The minimal length.
     * @return The smallest integer which is greater or equal {@code len}
     * and which is a multiple of powers of 2, 3 and/or 5.
     */
    public static int bestDimension(int dim) {
        int best = 2*dim;
        for (int n5 = 1; n5 < best; n5 *= 5) {
            for (int n3 = n5; n3 < best; n3 *= 3) {
                /* innermost loop (power of 2) is exited as soon as N2 >= LEN */
                int n2 = n3;
                while (n2 < dim) {
                    n2 *= 2;
                }
                if (n2 == dim) {
                    return dim;
                }
                if (best > n2) {
                    best = n2;
                }
            }
        }
        return best;
    }

    /**
     * Generate discrete Fourier transform frequencies.
     * @param dim - The number of discrete frequencies.
     * @return An array of {@code dim} integers: {0,1,2,...,-2,-1}
     */
    public static int[] generateFrequels(int dim) {
        int[] freq = new int[dim];
        generateFrequels(freq);
        return freq;
    }

    /**
     * Generate discrete Fourier transform frequencies.
     * @param freq - The array of discrete frequencies to fill.
     */
    public static void generateFrequels(int[] freq) {
        int dim = freq.length;
        int cut = dim/2;
        for (int i = 0; i <= cut; ++i) {
            freq[i] = i;
        }
        for (int i = cut + 1; i < dim; ++i) {
            freq[i] = i - dim;
        }
    }

}
