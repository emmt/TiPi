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

package mitiv.random;

/** Abstract class for low-level random generator.
 * <p>
 * A random engine generates a pseudo-random integer uniformly distributed in
 * a specific range and serves as a basis for other generators.   The engine is
 * also able to generate a pseudo-random real value uniformly distributed in
 * the range (0,1), i.e., the bounds being excluded.
 * <p>
 * This behavior differs from Java random generator which generates pseudo-random
 * real values between 0 (inclusive) and 1 (exclusive) and pseudo-random integer
 * random values given the number of bits or between 0 (inclusive) and N (exclusive).
 *
 * @author Éric Thiébaut.
 *
 */
public abstract class RandomEngine {

    /**
     * Makes this class non instantiable, but still let's others inherit from it.
     */
    protected RandomEngine() {}

    /**
     * Create a new default random engine.
     * @return A new instance of the Press & Teukolsky random engine.
     */
    public static RandomEngine NewDefaultEngine() {
        return new PressTeukolskyGenerator();
    }

    /**
     * Create a new default random engine.
     * @param seed - A seed to initialize the engine.
     * @return A new instance of the Press & Teukolsky random engine.
     */
    public static RandomEngine NewDefaultEngine(int seed) {
        return new PressTeukolskyGenerator(seed);
    }

    /** Seed the pseudo-random generator. */
    public abstract void reset(int seed);

    /** Query next pseudo-random integer. */
    public abstract int next();

    /** Query next pseudo-random value uniformly distributed in (0,1) exclusive.
     * @return A single precision floating point value uniformly distributed
     *         in the range (0,1), bounds excluded.
     */
    public float nextFloat() {
        return (float)nextDouble();
    };

    /** Query next pseudo-random value uniformly distributed in (0,1) exclusive.
     * @return A double precision floating point value uniformly distributed
     *         in the range (0,1), bounds excluded.
     */
    public abstract double nextDouble();

    /** Query minimum achievable generated integer. */
    public abstract int min();

    /** Query maximum achievable generated integer. */
    public abstract int max();

    protected static final double standardBias(double minValue, double maxValue) {
        return (minValue - 0.5)/(maxValue - minValue + 1.0);
    }

    protected static final double standardScale(double minValue, double maxValue) {
        return 1.0/(maxValue - minValue + 1.0);
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
