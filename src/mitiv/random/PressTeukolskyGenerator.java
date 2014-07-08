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
 * *
 * ----------------------------------------------------------------------------
 *
 * The code is based on Yorick (http://yorick.sourceforge.net) software
 * developed by Dave Munro.
 *
 * Copyright (c) 2005, The Regents of the University of California.
 * All rights reserved.
 */

package mitiv.random;

/**
 * Basic uniform random generator based on Press and Teukolsky algorithm.
 *
 * <p>
 * Algorithm from Press and Teukolsky, Computers in Physics, 6, #5,
 * Sep/Oct 1992, pp. 522-524.  They offer a $1000 reward to anyone
 * who finds a statistical test this generator fails non-trivially.
 * Based on a generator of L'Ecuyer with a shuffle algorithm of
 * Bays-Durham and other improvements.
 * The period of the generator is 2.3e18.
 *
 * @author Éric Thiébaut
 *
 */
public class PressTeukolskyGenerator extends RandomEngine {
    /* Choose two sets of linear congruential parameters -- the
       multiplier is IA, and the modulus is IM.  The IR and IQ are
       cunningly chosen so that (IA*x)%IM can be computed as
       IA*(x%IQ) - IR*(x/IQ), the latter expression having the
       advantage that the product will not overflow.  The IM values
       are near 2^31-1, the largest guaranteed positive long.
       The clever calculation of (IA*x)%IM is due to Schrage,
       ACM Trans. Mathem. Software 5, 132-138;
       IM = IA*IQ+IR with IR < IQ is the required relation.  */
    private static final int IM1 = 2147483563;
    private static final int IA1 =      40014;
    private static final int IQ1 =      53668;
    private static final int IR1 =      12211;

    private static final int IM2 = 2147483399;
    private static final int IA2 =      40692;
    private static final int IQ2 =      52774;
    private static final int IR2 =       3791;

    private static final int NTAB = 32; /* size of shuffle table */
    private static final int IMM1 = (IM1 - 1);
    private static final int NDIV = (1 + IMM1/NTAB);

    /* The long period is achieved by combining two sequences with
       nearly incommensurate periods.  */
    private int idum1 = 0;      /* this is the primary seed */
    private int idum2 = 0;      /* this is the secondary seed */
    private final int[] iv = new int[NTAB]; /* shuffle table (of idum1) */
    private int iy;                   /* previous result */

    /** Default seed. */
    public static final int DEFAULT_SEED = (int)(0.6180339885*IM2);

    public PressTeukolskyGenerator(int seed) {
        reset(seed);
    }

    public PressTeukolskyGenerator() {
        reset(DEFAULT_SEED);
    }

    /** Reset the pseudo-random generator.
     * 
     * @param seed - The seed should be strictly in (0,1); otherwise a default
     *               value is used.
     */
    @Override
    public void reset(int seed) {
        /* translate seed to integer between 1 and IM2-1 inclusive */
        if (seed <= 0) seed = DEFAULT_SEED;
        idum2 = 1 + seed%(IM2 - 1);
        idum1 = idum2;

        /* do 8 warm-ups, then load shuffle table */
        for (int j = NTAB + 7; j >= 0; --j) {
            idum1 = IA1*(idum1%IQ1) - IR1*(idum1/IQ1);
            if (idum1 < 0) idum1 += IM1;
            if (j < NTAB) iv[j] = idum1;
        }
        iy = iv[0];
    }

    /** Generate the next integer random value.
     * @return The next integer random value.
     */
    @Override
    public int next() {
        /* compute idum = (IA1*idum)%IM1 without overflow */
        idum1 = IA1*(idum1%IQ1) - IR1*(idum1/IQ1);
        if (idum1 < 0) idum1 += IM1;

        /* compute idum2 = (IA2*idum2)%IM2 without overflow */
        idum2 = IA2*(idum2%IQ2) - IR2*(idum2/IQ2);
        if (idum2 < 0) idum2 += IM2;

        /* previous result is used to determine which element of the shuffle
           table to use for this result */
        int j = iy/NDIV;            /* in range 0..NTAB-1 */
        iy = iv[j] - idum2;
        iv[j] = idum1;
        if (iy < 1) iy += IMM1;
        return iy;
    }

    /** Get next pseudo-random value uniformly distributed between bounds.
     * 
     * @param a - The first end-point of the interval.
     * @param b - The second end-point of the interval.
     * @return A pseudo-random value uniformly distributed between {@code a}
     *         and {@code b} (exclusive if the bounds are different).
     */
    public double generate(double a, double b) {
        return a + (b - a)*next();
    }

    void generate(double[] arr) {
        int n = arr.length;
        for (int k = 0; k < n; ++k) {
            arr[k] = next();
        }
    }

    void generate(double[] arr, double a, double b) {
        double scale = (b - a)*DBL_SCALE;
        double bias = (b - a)*DBL_BIAS - a;
        int n = arr.length;
        for (int k = 0; k < n; ++k) {
            arr[k] = scale*next() - bias;
        }
    }

    /* Really only IMM1 possible values can be returned, 1 <= iy <= IMM1.
       Algorithm given by Press and Teukolsky has a slight bug.
       Here, the return values are centered in IMM1 equal bins.
       If 2.0e9 distinct values are not enough, could use, say, idum2
       to scoot the points around randomly within bins...  */
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = IMM1;
    public static final double DBL_SCALE = standardScale(MIN_VALUE, MAX_VALUE);
    public static final double DBL_BIAS  = standardBias(MIN_VALUE, MAX_VALUE);
    public static final float  FLT_SCALE = (float)DBL_SCALE;
    public static final float  FLT_BIAS  = (float)DBL_BIAS;

    @Override
    public int min() {
        return MIN_VALUE;
    }

    @Override
    public int max() {
        return MAX_VALUE;
    }

    @Override
    public double nextDouble() {
        return DBL_SCALE*next() - DBL_BIAS;
    }

    @Override
    public float nextFloat() {
        return FLT_SCALE*next() - FLT_BIAS;
    }

    public static void main(String[] args) {
        System.out.printf("MIN_VALUE = 0x%08x\n", MIN_VALUE);
        System.out.printf("MAX_VALUE = 0x%08x\n", MAX_VALUE);
        System.out.printf("DBL_SCALE = %g\n", DBL_SCALE);
        System.out.printf("DBL_BIAS = %g\n", DBL_BIAS);
        System.out.printf("DBL_BIAS/DBL_SCALE = %g\n", DBL_BIAS/DBL_SCALE);
        PressTeukolskyGenerator engine = new PressTeukolskyGenerator();
        System.out.printf("Sample:");
        for (int k = 1; k < 10; k++) {
            System.out.printf("  %8.6f", engine.nextDouble());
        }
        System.out.printf("\n");
        double sum, min, max;
        int n = 10000000;
        sum = min = max = engine.nextDouble();
        for (int k = 1; k < n; k++) {
            double val = engine.nextDouble();
            sum += val;
            if (val < min) min = val;
            if (val > max) max = val;
        }
        System.out.printf("Test of %d draws: avg = %g; min = %g; 1 - max = %g;\n", n, sum/n, min, 1.0 - max);
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
