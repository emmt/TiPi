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

import mitiv.exception.NotImplementedException;

public class NormalDistribution implements DoubleGenerator, FloatGenerator {
    public static final double DEFAULT_MEAN = 0.0;
    public static final double DEFAULT_VARIANCE = 1.0;

    protected RandomEngine engine;
    protected double mean;
    protected double variance;
    protected double standardDeviation;

    private double savedValue;
    private boolean haveSavedValue = false;

    public NormalDistribution(double mean, double variance) {
        this.engine = RandomEngine.NewDefaultEngine();
        setState(mean, variance);
    }

    public NormalDistribution(int seed, double mean, double variance) {
        this.engine = RandomEngine.NewDefaultEngine(seed);
        setState(mean, variance);
    }

    public NormalDistribution(RandomEngine engine, double mean, double variance) {
        this.engine = engine;
        setState(mean, variance);
    }

    public NormalDistribution() {
        this(DEFAULT_MEAN, DEFAULT_VARIANCE);
    }

    public NormalDistribution(int seed) {
        this(seed, DEFAULT_MEAN, DEFAULT_VARIANCE);
    }

    public NormalDistribution(RandomEngine engine) {
        this(engine, DEFAULT_MEAN, DEFAULT_VARIANCE);
    }

    public void reset(int seed) {
        haveSavedValue = false;
        engine.reset(seed);
    }

    public void setState(double mean, double variance) {
        this.mean = mean;
        this.variance = variance;
        this.standardDeviation = Math.sqrt(variance);
    }

    /**
     * Returns the next pseudo-random normally distributed double value.
     *
     * Based ont the polar method of G. E. P. Box, M. E. Muller, and G. Marsaglia, as
     * described by Donald E. Knuth in "The Art of Computer Programming," Volume 3:
     * Seminumerical Algorithms, section 3.4.1, subsection C, algorithm P. Note that it
     * generates two independent values at the cost of only one call to Math.log and one
     * call to Math.sqrt.
     *
     * @return The next pseudo-random normally distributed double value.
     */
    @Override
    public double nextDouble() {
        if (haveSavedValue) {
            haveSavedValue = false;
            return mean + standardDeviation*savedValue;
        } else {
            double v1, v2, s;
            do {
                v1 = 2.0*engine.nextDouble() - 1.0;
                v2 = 2.0*engine.nextDouble() - 1.0;
                s = v1*v1 + v2*v2;
            } while (s >= 1.0 || s == 0.0);
            s = Math.sqrt(-2.0*Math.log(s)/s);
            savedValue = s*v2;
            haveSavedValue = true;
            return mean + standardDeviation*(s*v1);
        }
    }

    /**
     * Returns the next pseudo-random normally distributed float value.
     *
     * @return The next pseudo-random normally distributed float value.
     */
    @Override
    public float nextFloat() {
        return (float)nextDouble();
    }

    public double cdf(double x) {
        throw new NotImplementedException();
    }

    public double pdf(double x) {
        throw new NotImplementedException();
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
