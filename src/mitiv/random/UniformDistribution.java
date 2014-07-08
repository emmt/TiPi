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


public class UniformDistribution {
    public static final double DEFAULT_INF = 0.0;
    public static final double DEFAULT_SUP = 1.0;

    protected RandomEngine engine;
    protected double inf = DEFAULT_INF;
    protected double sup = DEFAULT_SUP;
    protected double alpha = 1.0/(DEFAULT_SUP - DEFAULT_INF);
    protected double scale = 1.0;
    protected double bias = 1.0;

    public UniformDistribution(double a, double b) {
        this.engine = RandomEngine.NewDefaultEngine();
        setState(a, b);
    }

    public UniformDistribution(int seed, double a, double b) {
        this.engine = RandomEngine.NewDefaultEngine(seed);
        setState(a, b);
    }

    public UniformDistribution(RandomEngine engine, double a, double b) {
        this.engine = engine;
        setState(a, b);
    }

    public UniformDistribution() {
        this(DEFAULT_INF, DEFAULT_SUP);
    }

    public UniformDistribution(int seed) {
        this(seed, DEFAULT_INF, DEFAULT_SUP);
    }

    public UniformDistribution(RandomEngine engine) {
        this(engine, DEFAULT_INF, DEFAULT_SUP);
    }

    public void reset(int seed) {
        engine.reset(seed);
    }

    /*
     * x = scale*i - bias;
     * 
     * To generate x in [xmin, xmax] inclusively, we want:
     * <pre>
     *   xmin = scale*MIN_VALUE - bias;
     *   xmax = scale*MAX_VALUE - bias;
     * </pre>
     * which yields (operations in floating point):
     * <pre>
     *     scale = (xmax - xmin)/(MAX_VALUE - MIN_VALUE);
     *     bias = ;
     * </pre>
     * 
     * To generate x in (inf, sup) exclusively, we want:
     * <pre>
     *   inf + scale/2 = scale*MIN_VALUE - bias;
     *   sup - scale/2 = scale*MAX_VALUE - bias;
     * </pre>
     * which yields (operations in floating point):
     * <pre>
     *     scale = (sup - inf)/(MAX_VALUE - MIN_VALUE + 1);
     *     bias = (scale*(MAX_VALUE + MIN_VALUE) - (inf + sup))/2;
     * </pre>
     */
    public void setState(double a, double b) {
        inf = Math.min(a, b);
        sup = Math.max(a, b);
        if (sup > inf) {
            alpha = 1.0/(sup - inf);
        } else {
            alpha = Double.POSITIVE_INFINITY;
        }
        double minValue = engine.min();
        double maxValue = engine.max();
        scale = (sup - inf)/(maxValue - minValue + 1.0);
        bias = ((maxValue + minValue)*scale - (inf + sup))/2.0;
    }

    public double nextDouble() {
        return scale*engine.next() - bias;
    }

    public double cdf(double x) {
        if (x <= inf) return 0.0;
        if (x >= sup) return 1.0;
        return (x - inf)/(sup - inf);
    }

    public double pdf(double x) {
        if (x <= inf) return 0.0;
        if (x >= sup) return 1.0;
        return alpha*(x - inf);
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
