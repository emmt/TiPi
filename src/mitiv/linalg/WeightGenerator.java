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
import mitiv.array.DoubleArray;
import mitiv.array.ShapedArray;

public class WeightGenerator {

    private double saturationLevel = Double.MAX_VALUE;
    private ShapedArray weightMap = null;
    private ShapedArray varianceMap = null;
    private ShapedArray pixelMap = null;
    private double gain = -1.0;
    private double readNoise = -1.0;
    
    public WeightGenerator() {

    }

    public ShapedArray getWeightMap(){
        double[] output = null;
        if ( weightMap != null) {
            output = weightMap.toDouble().flatten();//Flatten make a copy so we are sure that we are not braking anything
            for (int i = 0; i < output.length; i++) {
                if (output[i] < 0) {
                    throw new IllegalArgumentException("A weight map can not contain negative values");
                }
            }
        } else if (varianceMap != null) {
            DoubleArray tsmp = varianceMap.toDouble();
            output = varianceMap.toDouble().flatten();//Bis
            for (int i = 0; i < output.length; i++) {
                output[i] = 1.0/(Math.max(output[i], 0.0));
            }
        } else if (gain != -1 && readNoise != -1) {
            output = weightMap.toDouble().flatten();//Ter + We use weightMap BUT it is data that we are using
            for (int i = 0; i < output.length; i++) {
                output[i] = 1.0/((Math.max(output[i], 0.0)/gain)+readNoise*readNoise);
            }
        } else {
            throw new IllegalArgumentException("Before getting a weight map you should give something");
        }
        applyDeadPixelMapAndVerify(output);
        return Double1D.wrap(output, output.length);
    }
    
    private void applyDeadPixelMapAndVerify(double[] input){
        for (int i = 0; i < input.length; i++) {
            if (input[i] == saturationLevel || Double.isNaN(input[i])) {
                input[i] = 0.0;
            }
        }
        if (pixelMap != null) {
            double[] dblBad = pixelMap.toDouble().flatten();
            for (int i = 0; i < input.length; i++) {
                input[i] = input[i]*dblBad[i];
            }
        }
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
    
    public void setWeightMap(ShapedArray map){
        weightMap = map;
    }
    public void setVarianceMap(ShapedArray map){
        varianceMap = map;
    }
    public void setPixelMap(ShapedArray map){
        pixelMap = map;
    }
    public void setComputedVariance(ShapedArray data, double alpha, double beta){
        if (alpha < 0 || beta < 0) {
            throw new IllegalArgumentException("Computed variance canno't be negative");
        }
        this.gain = alpha;
        this.readNoise = beta;
        setVarianceMap(data);//We store the data in the weightMap, to save one variable
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