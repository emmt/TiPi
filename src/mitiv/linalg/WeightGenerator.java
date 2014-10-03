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

package mitiv.linalg;

import mitiv.array.Double1D;
import mitiv.array.ShapedArray;

public class WeightGenerator {

    private double saturationLevel = Double.MAX_VALUE;
    /**
     * The weight map shaped array should contain:
     * <br>
     * <pre>    0 if the value is not known.</pre>
     * <pre>    1/var(y) or 1/&sigma;&sup2; else.</pre>;
     * 
     * @param weightMap 
     * @return map The weight map
     */

    public ShapedArray getWeightMap(ShapedArray weightMap){
        double[] dblMap = weightMap.toDouble().flatten();
        for (int i = 0; i < dblMap.length; i++) {
            if (dblMap[i] < 0) {
                throw new IllegalArgumentException("A weight map can not contain negative values");
            }
        }
        return weightMap;
    }

    /**
     * The weight map shaped array should contain:
     * <br>
     * <pre>    0 if the value is not known.</pre>
     * <pre>    1/var(y) or 1/&sigma;&sup2; else.</pre>;
     * 
     * @param varianceMap 
     * @param stdMap 
     * @return map The weight map
     */
    public ShapedArray getWeightMap(ShapedArray varianceMap, double stdMap){
        double[] varMap = varianceMap.toDouble().flatten();
        double out[] = new double[varMap.length];
        for (int i = 0; i < varMap.length; i++) {
            out[i] = 1.0/(Math.max(varMap[i], 0.0) + stdMap*stdMap);
        }
        return Double1D.wrap(out, out.length);
    }

    /**
     * We build the map from the variance map by using the formula:<br>
     * Xk = 1/var(Yk)<br>
     * X &isin; Map, Y &isin; Variance Map
     * 
     * @param varianceMap 
     * @param stdMap 
     * @param deadPixelMap 
     * @return 
     */
    public ShapedArray getWeightMapFromVariance(ShapedArray varianceMap, double stdMap, ShapedArray deadPixelMap){
        double[] varMap = varianceMap.toDouble().flatten();
        double[] dblBad = deadPixelMap.toDouble().flatten();
        double out[] = new double[varMap.length];
        for (int i = 0; i < varMap.length; i++) {
            if (varMap[i] == saturationLevel) {
                out[i] = 0.0;
            } else {
                out[i] = 1.0/((dblBad[i] == 0) ? 0.0 : Math.max(varMap[i], 0.0) + stdMap*stdMap);
            }

        }
        return Double1D.wrap(out, out.length);
    }

    /**
     * In order to be able to detect saturation we have to know a value.
     * Default = Double.MAX_VALUE
     * 
     * @param saturation
     */
    public void setSaturation(double saturation){
        saturationLevel = saturation;
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