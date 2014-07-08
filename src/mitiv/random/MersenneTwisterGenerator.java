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
 *
 * ----------------------------------------------------------------------------
 *
 * The code is based on the Colt library developed by the Cern.
 *
 * Copyright (c) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice appear in all copies and that both that
 * copyright notice and this permission notice appear in supporting
 * documentation.  CERN makes no representations about the suitability of this
 * software for any purpose.  It is provided "as is" without expressed or
 * implied warranty.
 */

package mitiv.random;

import java.util.Date;

/**
 * MersenneTwister (MT19937) is one of the strongest uniform pseudo-random
 * number generators known so far; at the same time it is quick.
 *
 * <p> Produces uniformly distributed {@code int}'s and {@code long}'s in
 * the closed intervals {@code [Integer.MIN_VALUE,Integer.MAX_VALUE]} and
 * {@code [Long.MIN_VALUE,Long.MAX_VALUE]}, respectively, as well as
 * {@code float}'s and {@code double}'s in the open unit intervals
 * {@code (0.0f,1.0f)} and {@code (0.0,1.0)}, respectively.  The seed can be
 * any 32-bit integer except {@code 0}. Shawn J. Cokus commented that perhaps
 * the seed should preferably be odd.
 *
 *
 * <h3>Quality:</h3>
 *
 * MersenneTwister is designed to pass the k-distribution test. It has an
 * astronomically large period of 2<sup>19937</sup>-1 (=10<sup>6001</sup>) and
 * 623-dimensional equidistribution up to 32-bit accuracy.  It passes many
 * stringent statistical tests, including the <a
 * href="http://stat.fsu.edu/~geo/diehard.html">diehard</a> test of
 * G. Marsaglia and the load test of P. Hellekalek and S. Wegenkittl.
 *
 *
 * <h3>Performance:</h3>
 *
 * Its speed is comparable to other modern generators (in particular, as fast
 * as {@code java.util.Random.nextFloat()}).  2.5 million calls to {@code
 * raw()} per second (Pentium Pro 200 Mhz, JDK 1.2, NT).  Be aware, however,
 * that there is a non-negligible amount of overhead required to initialize
 * the data structures used by a MersenneTwister. Code like:
 * <pre>
 *     double sum = 0.0;
 *     for (int i=0; i<100000; ++i) {
 *        RandomElement twister = new MersenneTwister(new java.util.Date());
 *        sum += twister.raw();
 *     }
 * </pre>
 * will be wildly inefficient. Consider using
 * <pre>
 *     double sum = 0.0;
 *     RandomElement twister = new MersenneTwister(new java.util.Date());
 *     for (int i=0; i<100000; ++i) {
 *        sum += twister.raw();
 *     }
 * </pre>
 * instead.  This allows the cost of constructing the MersenneTwister object
 * to be borne only once, rather than once for each iteration in the loop.
 *
 *
 * <h3>Implementation:</h3>
 *
 * After M. Matsumoto and T. Nishimura, "Mersenne Twister: A 623-Dimensionally
 * Equidistributed Uniform Pseudo-Random Number Generator", ACM Transactions
 * on Modeling and Computer Simulation, Vol. 8, No. 1, January 1998, pp 3--30.
 *
 * <p>More info on <A HREF="http://www.math.keio.ac.jp/~matumoto/eindex.html">
 * Masumoto's homepage</A>.</p>
 *
 * <p>More info on <A
 * HREF="http://www.ncsa.uiuc.edu/Apps/CMP/RNG/www-rng.html"> Pseudo-random
 * number generators is on the Web</A>.</p>
 *
 * <p>Yet <A HREF="http://nhse.npac.syr.edu/random"> some more info</A>.</p>
 *
 * <p>The correctness of this implementation has been verified against the
 * published output sequence <a
 * href="http://www.math.keio.ac.jp/~nisimura/random/real2/mt19937-2.out">mt19937-2.out</a>
 * of the C-implementation <a
 * href="http://www.math.keio.ac.jp/~nisimura/random/real2/mt19937-2.c">mt19937-2.c</a>.
 * (Call {@code test(1000)} to print the sequence).
 *
 * Note that this implementation is <i><b>not synchronized</b></i>.
 *
 *
 * <h3>Details:</h3>
 *
 * MersenneTwister is designed with consideration of the flaws of various
 * existing generators in mind.  It is an improved version of TT800, a very
 * successful generator.  MersenneTwister is based on linear recurrences
 * modulo 2.  Such generators are very fast, have extremely long periods, and
 * appear quite robust.  MersenneTwister produces 32-bit numbers, and every
 * {@code k}-dimensional vector of such numbers appears the same number of
 * times as {@code k} successive values over the period length, for each
 * {@code k &lt;= 623} (except for the zero vector, which appears one time
 * less).  If one looks at only the first {@code n &lt;= 16} bits of each
 * number, then the property holds for even larger {@code k}, as shown in the
 * following table (taken from the publication cited above):
 *
 * <div align="center">
 * <table width="75%" border="1" cellspacing="0" cellpadding="0">
 *   <tr>
 *         <td width="2%"> <div align="center">n</div> </td>
 *         <td width="6%"> <div align="center">1</div> </td>
 *         <td width="5%"> <div align="center">2</div> </td>
 *         <td width="5%"> <div align="center">3</div> </td>
 *         <td width="5%"> <div align="center">4</div> </td>
 *         <td width="5%"> <div align="center">5</div> </td>
 *         <td width="5%"> <div align="center">6</div> </td>
 *         <td width="5%"> <div align="center">7</div> </td>
 *         <td width="5%"> <div align="center">8</div> </td>
 *         <td width="5%"> <div align="center">9</div> </td>
 *         <td width="5%"> <div align="center">10</div> </td>
 *         <td width="5%"> <div align="center">11</div> </td>
 *         <td width="10%"> <div align="center">12 .. 16</div> </td>
 *         <td width="10%"> <div align="center">17 .. 32</div> </td>
 *   </tr>
 *   <tr>
 *         <td width="2%"> <div align="center">k</div> </td>
 *         <td width="6%"> <div align="center">19937</div> </td>
 *         <td width="5%"> <div align="center">9968</div> </td>
 *         <td width="5%"> <div align="center">6240</div> </td>
 *         <td width="5%"> <div align="center">4984</div> </td>
 *         <td width="5%"> <div align="center">3738</div> </td>
 *         <td width="5%"> <div align="center">3115</div> </td>
 *         <td width="5%"> <div align="center">2493</div> </td>
 *         <td width="5%"> <div align="center">2492</div> </td>
 *         <td width="5%"> <div align="center">1869</div> </td>
 *         <td width="5%"> <div align="center">1869</div> </td>
 *         <td width="5%"> <div align="center">1248</div> </td>
 *         <td width="10%"> <div align="center">1246</div> </td>
 *         <td width="10%"> <div align="center">623</div> </td>
 *   </tr>
 * </table>
 * </div>
 * <p>
 * MersenneTwister generates random numbers in batches of 624 numbers at a
 * time, so the caching and pipelining of modern systems is exploited.  The
 * generator is implemented to generate the output by using the fastest
 * arithmetic operations only: 32-bit additions and bit operations (no
 * division, no multiplication, no mod).  These operations generate sequences
 * of 32 random bits ({@code int}'s).  {@code long}'s are formed by
 * concatenating two 32 bit {@code int}'s.  {@code float}'s are formed by
 * dividing the interval {@code [0.0,1.0]} into 2<sup>32</sup> sub intervals,
 * then randomly choosing one subinterval.  {@code double}'s are formed by
 * dividing the interval {@code [0.0,1.0]} into 2<sup>64</sup> sub intervals,
 * then randomly choosing one subinterval.
 * <p>
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 * @see java.util.Random
 */
public class MersenneTwisterGenerator extends RandomEngine
implements DoubleGenerator, FloatGenerator {

    private int mti;
    private final int[] mt = new int[N]; /* set initial seeds: N = 624 words */

    /* Period parameters */
    private static final int N = 624;
    private static final int M = 397;
    private static final int MATRIX_A   = 0x9908b0df; /* constant vector a */
    private static final int UPPER_MASK = 0x80000000; /* most significant w-r bits */
    private static final int LOWER_MASK = 0x7fffffff; /* least significant r bits */

    /* for tempering */
    private static final int TEMPERING_MASK_B = 0x9d2c5680;
    private static final int TEMPERING_MASK_C = 0xefc60000;

    private static final int mag0 = 0x0;
    private static final int mag1 = MATRIX_A;
    //private static final int[] mag01=new int[] {0x0, MATRIX_A};
    /* mag01[x] = x * MATRIX_A  for x=0,1 */

    public static final int DEFAULT_SEED = 4357;
    /**
     * Constructs and returns a random number generator with a default seed,
     * which is a <b>constant</b>.  Thus using this constructor will yield
     * generators that always produce exactly the same sequence.  This method
     * is mainly intended to ease testing and debugging.
     */
    public MersenneTwisterGenerator() {
        this(DEFAULT_SEED);
    }
    /**
     * Constructs and returns a random number generator with the given seed.
     */
    public MersenneTwisterGenerator(int seed) {
        reset(seed);
    }
    /**
     * Constructs and returns a random number generator seeded with the given date.
     *
     * @param d typically {@code new java.util.Date()}
     */
    public MersenneTwisterGenerator(Date d) {
        this((int)d.getTime());
    }

    /**
     * Generates N words at one time.
     */
    private void nextBlock() {
        int y, kk, p = N - M;
        for (kk = 0 ; kk < p; ++kk) {
            y = (mt[kk]&UPPER_MASK) | (mt[kk+1]&LOWER_MASK);
            mt[kk] = mt[kk + M] ^ (y >>> 1) ^ ((y & 0x1) == 0 ? mag0 : mag1);
        }
        for ( ; kk < N-1; ++kk) {
            y = (mt[kk]&UPPER_MASK) | (mt[kk+1]&LOWER_MASK);
            mt[kk] = mt[kk - p] ^ (y >>> 1) ^ ((y & 0x1) == 0 ? mag0 : mag1);
        }
        y = (mt[N - 1]&UPPER_MASK) | (mt[0]&LOWER_MASK);
        mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ ((y & 0x1) == 0 ? mag0 : mag1);
        mti = 0;
    }

    /**
     * Returns a 32 bit uniformly distributed random number in the closed
     * interval {@code [Integer.MIN_VALUE,Integer.MAX_VALUE]} (including
     * {@code Integer.MIN_VALUE} and {@code Integer.MAX_VALUE}).
     */
    @Override
    public int next() {
        /* Each single bit including the sign bit will be random */
        if (mti == N) nextBlock(); // generate N integers at one time
        int y = mt[mti++];
        y ^= y >>> 11; // y ^= TEMPERING_SHIFT_U(y );
        y ^= (y << 7) & TEMPERING_MASK_B; // y ^= TEMPERING_SHIFT_S(y) & TEMPERING_MASK_B;
        y ^= (y << 15) & TEMPERING_MASK_C; // y ^= TEMPERING_SHIFT_T(y) & TEMPERING_MASK_C;
        // y &= 0xffffffff; // you may delete this line if word size = 32
        y ^= y >>> 18; // y ^= TEMPERING_SHIFT_L(y);
        return y;
    }

    /**
     * Sets the receiver's seed.
     * This method resets the receiver's entire internal state.
     */
    @Override
    public void reset(int seed) {
        mt[0] = seed & 0xffffffff;

        for (int i = 1; i < N; ++i) {
            // See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier.
            // In the previous versions, MSBs of the seed affect
            // only MSBs of the array mt[].
            // 2002/01/09 modified by Makoto Matsumoto
            mt[i] = (1812433253 * (mt[i-1] ^ (mt[i-1] >> 30)) + i);
            //mt[i] &= 0xffffffff; // for >32 bit machines (not needed for Java int's)
        }

        // old version of the loop was:
        // for (int i = 0; i < N; ++i) {
        //     mt[i] = seed & 0xffff0000;
        //     seed = 69069 * seed + 1;
        //     mt[i] |= (seed & 0xffff0000) >>> 16;
        //     seed = 69069 * seed + 1;
        // }

        mti = N;
    }

    protected static final int MIN_VALUE = Integer.MIN_VALUE;
    protected static final int MAX_VALUE = Integer.MAX_VALUE;

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
        MersenneTwisterGenerator engine = new MersenneTwisterGenerator(7864);
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
