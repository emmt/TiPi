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

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDateTime;

import mitiv.array.ArrayUtils;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.FloatShapedVectorSpace;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;

/**
 * Contains all usual methods to work on color, image, arrays and conversion from one to another.
 * 
 * @author Leger Jonathan
 *
 */
public class CommonUtils {

    /** 
     * padding options: Nothing is done 
     * _______
     * |      |
     * |      |
     * |#_____|
     * 
     * */
    public static final int LOWER_LEFT = 0;

    /** 
     * padding options: Nothing is done 
     * _______
     * |      |
     * |  #   |
     * |______|
     * 
     * */
    public static final int CENTERED = 1;

    /** 
     * padding options: Nothing is done 
     * _______
     * |#    #|
     * |      |
     * |#____#|
     * 
     * */
    public static final int FFT_INDEXING = -1;

    /**
     * If we want the computed image not to be scaled.
     */
    public static int NO_SCALE = 3;
    /**
     * If we want the computed image to be scaled.
     */
    public static int SCALE = 4;

    /**
     * If we want the computed image to be corrected: a second scale to remove
     * potential errors.
     */
    public static int SCALE_CORRECTED = 5;

    /** If we want virtual color for the computed image. */
    public static int SCALE_COLORMAP = 6;

    /** If we want a correction on the scale + color. */
    public static int SCALE_CORRECTED_COLORMAP = 7;

    /**
     * Will convert a grey value to another.
     *
     * @param g the g
     * @param alpha the alpha
     * @param beta the beta
     * @return the int
     */
    public static int greyToColor(double g, double alpha, double beta)
    {
        double x = alpha*g + beta;
        int i;
        if (x < 0.5) {
            i = 0;
        } else if (x > 254.5) {
            i = 255;
        } else {
            i = (int)(x + 0.5);
        }
        return i;
    }

    /**
     * Convert a RGB value to an int grey value (float version).
     *
     * @param r the r
     * @param g the g
     * @param b the b
     * @return the int
     */
    public static int colorToGrey(double r, double g, double b)
    {
        return (int) ArrayUtils.colorToGrey(r, g, b);
    }

    public static int colorToGrey(int[]rgb)
    {
        if (rgb.length == 3) {
            return ArrayUtils.colorToGrey(rgb[0], rgb[1], rgb[2]);
        } else {
            return rgb[0];
        }
    }

    /**
     * Will scan the tab and return the highest and smallest value.
     *
     * @param array Array to parse
     * @param isComplex the is complex
     * @return an array of 2 value: {min,max}
     */
    public static double[] computeMinMax1D(double[] array, boolean isComplex){
        double min = array[0],max = array[0];
        int sizeArray = isComplex ? array.length/2 : array.length;
        for(int i = 0; i<sizeArray; i++){
            double value;
            if (isComplex) {
                value = array[2*i];
            } else {
                value = array[i];
            }
            if(value < min ){
                min = value;
            }
            if(value > max ){
                max = value;
            }
        }
        return new double[]{min,max};
    }

    /**
     * Will scan the tab and return the highest and smallest value.
     *
     * @param array Array to parse
     * @param isComplex the is complex
     * @return an array of 2 value: {min,max}
     */
    public static float[] computeMinMax1D(float[] array, boolean isComplex){
        float min = array[0],max = array[0];
        int sizeArray = isComplex ? array.length/2 : array.length;
        for(int i = 0; i<sizeArray; i++){
            float value;
            if (isComplex) {
                value = array[2*i];
            } else {
                value = array[i];
            }
            if(value < min ){
                min = value;
            }
            if(value > max ){
                max = value;
            }
        }
        return new float[]{min,max};
    }

    /**
     * Will scan the tab and return the highest and smallest value (float version).
     * The array is parsed like a 2D array.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @param isComplex the is complex
     * @return the float[]
     */
    public static float[] computeMinMax1Das2D(float[] array, int width,int height, boolean isComplex){
        float min = array[0],max = array[0];
        int current = 0;
        for(int j = 0; j<height; j++){
            for(int i = 0; i<width; i++){
                if (isComplex) {
                    current = 2*(i+j*width);
                } else {
                    current = i+j*width;
                }
                if(array[current] < min ){
                    min = array[current];
                }
                if(array[current] > max ){
                    max = array[current];
                }
            }
        }
        return new float[]{min,max};
    }

    /**
     * Will scan the tab and return the highest and smallest value (float version).
     * The array is parsed like a 2D array.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @param isComplex the is complex
     * @return the double[]
     */
    public static double[] computeMinMax1Das2D(double[] array, int width, int height,boolean isComplex){
        double min = array[0],max = array[0];
        int current = 0;
        for(int j = 0; j<height; j++){
            for(int i = 0; i<width; i++){
                if (isComplex) {
                    current = 2*(i+j*width);
                } else {
                    current = i+j*width;
                }
                if(array[current] < min ){
                    min = array[current];
                }
                if(array[current] > max ){
                    max = array[current];
                }
            }
        }
        return new double[]{min,max};
    }

    /**
     * Compute alpha, beta that are the two parameters needed to scale the data afterward.
     *
     * @param array the array
     * @param isComplex the is complex
     * @return the double[]
     */
    public static double[] computeAlphaBeta1D(double[] array, boolean isComplex){
        double [] out = computeMinMax1D(array, isComplex);
        double min = out[0];
        double max = out[1];
        double alpha, beta;
        if (min < max) {
            alpha = 255.0/(max - min);
            beta = -alpha*min;
        } else {
            alpha = 0.0;
            beta = 0.0;
        }
        return new double[]{alpha,beta};
    }

    /**
     * Compute alpha, beta that are the two parameters needed to scale the data afterward.
     *
     * @param tab the tab
     * @param isComplex the is complex
     * @return the float[]
     */
    public static float[] computeAlphaBeta1D(float[] tab, boolean isComplex){
        float [] out = computeMinMax1D(tab, isComplex);
        float min = out[0];
        float max = out[1];
        float alpha, beta;
        if (min < max) {
            alpha = 255f/(max - min);
            beta = -alpha*min;
        } else {
            alpha = 0;
            beta = 0;
        }
        return new float[]{alpha,beta};
    }

    /**
     * ******************************** X TO ARRAY *********************************.
     *
     * @param image the image
     * @param isComplex the is complex
     * @return the double[][]
     */

    /**
     * Convert a image to an 1D array of double.
     *
     * @param image the image
     * @param isComplex the is complex
     * @return the double[]
     */
    public static double[] imageToArray1D(BufferedImage image, boolean isComplex) {
        int height = image.getHeight();
        int width = image.getWidth();
        WritableRaster raster = image.getRaster();
        double []out;
        int size = width*height;
        if (isComplex) {
            out = new double[2*size];
            for (int i = 0; i < size; i++) {
                out[size+i] = 0;
            }
        } else {
            out = new double[size];
        }
        int[] tmp = new int[raster.getNumBands()];
        for(int j=0;j<height;j++){
            for(int i=0;i<width;i++){
                raster.getPixel(i, j, tmp);
                if (tmp.length == 1 || tmp.length == 2) {
                    out[(i+j*width)] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                } else {
                    out[(i+j*width)] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                }
            }
        }
        return out;
    }

    /**
     * Convert image to float 1D array.
     *
     * @param image the image
     * @param isComplex the is complex
     * @return the float[]
     */
    public static float[] imageToArray1DFloat(BufferedImage image, boolean isComplex) {
        int height = image.getHeight();
        int width = image.getWidth();
        WritableRaster raster = image.getRaster();
        float []out;
        if (isComplex) {
            out = new float[width*2*height];
        } else {
            out = new float[width*height];
        }
        int[] tmp = new int[raster.getNumBands()];
        if (isComplex) {
            for(int j=0;j<height;j++){
                for(int i=0;i<width;i++){
                    raster.getPixel(i, j, tmp);
                    if (tmp.length == 1 || tmp.length == 2) {
                        out[2*(i+j*width)] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                    } else {
                        out[2*(i+j*width)] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                    }
                }
            }
        } else {
            for(int j=0;j<height;j++){
                for(int i=0;i<width;i++){
                    raster.getPixel(i, j, tmp);
                    if (tmp.length == 1 || tmp.length == 2) {
                        out[(i+j*width)] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                    } else {
                        out[(i+j*width)] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                    }
                }
            }
        }
        return out;
    }

    public static double[] image3DToArray1D(ArrayList<BufferedImage>listImage, int sizeZ, int width,int height, boolean isComplex) {
        double[] out;
        if (isComplex) {
            out = new double[2*sizeZ*width*height];
            int strideW = width;
            int strideH = width*height;
            int strideZ = sizeZ*width*height;
            for (int k = 0; k < sizeZ; k++) {
                double[] tmp = CommonUtils.imageToArray1D(listImage.get(k), false);
                for (int j = 0; j < height; j++) {
                    for (int i = 0; i < width; i++) {
                        out[2*i+2*j*strideW+2*k*strideH] = tmp[i+j*strideW];
                    }
                }
            }
        } else {
            out = new double[sizeZ*width*height];
            for (int j = 0; j < sizeZ; j++) {
                double[] tmp = CommonUtils.imageToArray1D(listImage.get(j), false);
                for (int i = 0; i < tmp.length; i++) {
                    out[i+j*tmp.length] = tmp[i];
                }
            }
        }

        return out;
    }

    /**
     * Convert an image to a vector.
     *
     * @param outputSpace the output space
     * @param image the image
     * @param singlePrecision the single precision
     * @param isComplex the is complex
     * @return the shaped vector
     */
    public static ShapedVector imageToVector(ShapedVectorSpace outputSpace, BufferedImage image, boolean singlePrecision ,boolean isComplex){
        if (singlePrecision) {
            FloatShapedVectorSpace space = (FloatShapedVectorSpace)outputSpace;
            float[] tab = imageToArray1DFloat(image, isComplex);
            return space.wrap(tab);
        } else {
            DoubleShapedVectorSpace space = (DoubleShapedVectorSpace)outputSpace;
            double[] tab = imageToArray1D(image, isComplex);
            return space.wrap(tab);
        }
    }

    /**
     * Convert an image to a vector.
     *
     * @param outputSpace the output space
     * @param vector the vector
     * @param job the job
     * @param singlePrecision the single precision
     * @param isComplex the is complex
     * @return the buffered image
     */
    public static BufferedImage vectorToImage(ShapedVectorSpace outputSpace, ShapedVector vector, int job, boolean singlePrecision ,boolean isComplex){
        if (singlePrecision) {
            int[] shape = outputSpace.cloneShape();
            if (!(outputSpace.getRank() == 2)) {
                throw new IllegalArgumentException("The vector should be of rank 2 to create an image");
            }
            return arrayToImage1D(((FloatShapedVector)vector).getData(), job, shape[1], shape[0], isComplex);
        } else {
            int[] shape = outputSpace.cloneShape();
            if (!(outputSpace.getRank() == 2)) {
                throw new IllegalArgumentException("The vector should be of rank 2 to create an image");
            }
            return arrayToImage1D(((DoubleShapedVector)vector).getData(), job, shape[1], shape[0], isComplex);
        }
    }

    /**
     * ******************************** ARRAY TO IMAGE *********************************
     */

    /**
     * Create a buffered image from another buffered image
     * @param originalImage the original image
     * @return BufferedImage the buffered image with same characteristics as originalImage
     */
    public static BufferedImage createNewBufferedImage(BufferedImage originalImage){
        BufferedImage imageout;
        //In certain cases we need a specific type
        if(originalImage.getType() == 0){
            imageout = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        }else{
            imageout = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        }
        return imageout;
    }

    /**
     * Create a BufferedImage from a size (WxH).
     *
     * @param width the width
     * @param height the height
     * @return the buffered image
     */
    public static BufferedImage createNewBufferedImage(int width, int height){
        return new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    }

    /**
     * Scale an 1D array by doing in place operations.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @param isComplex the is complex
     */
    public static void scaleArray1Das2D(double[] array, int width, int height, boolean isComplex){
        //get max min for scaling
        double[] alphta = computeAlphaBeta1D(array,isComplex);
        int grey;
        for(int j = 0; j<height; j++){
            for(int i = 0; i<width; i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*width)],alphta[0], alphta[1]);
                    array[2*(i+j*width)]= grey;
                } else {
                    grey = greyToColor(array[(i+j*width)],alphta[0], alphta[1]);
                    array[(i+j*width)]= grey;
                }
            }
        }
    }

    /**
     * Scale an 1D array by doing in place operations.
     *
     * @param array the array
     * @param isComplex the is complex
     */
    public static void scaleArray1D(double[] array, boolean isComplex){
        //get max min for scaling
        double[] alphta = computeAlphaBeta1D(array,isComplex);
        int grey;
        int sizeArray = isComplex ? array.length/2 : array.length;
        for(int i = 0; i<sizeArray; i++){
            if (isComplex) {
                grey = greyToColor(array[2*i],alphta[0], alphta[1]);
                array[2*i]= grey;
            } else {
                grey = greyToColor(array[i],alphta[0], alphta[1]);
                array[i]= grey;
            }
        }
    }

    /**
     * Scale a float 1D array by doing in place operations.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @param isComplex the is complex
     */
    public static void scaleArray1Das2D(float[] array, int width, int height, boolean isComplex){
        //get max min for scaling
        float[] alphta = computeAlphaBeta1D(array,isComplex);
        int grey;
        for(int j = 0; j<height; j++){
            for(int i = 0; i<width; i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*width)],alphta[0], alphta[1]);
                    array[2*(i+j*width)]= grey;
                } else {
                    grey = greyToColor(array[(i+j*width)],alphta[0], alphta[1]);
                    array[(i+j*width)]= grey;
                }
            }
        }
    }

    /**
     * Scale a float 1D array by doing in place operations.
     *
     * @param array the array
     * @param isComplex the is complex
     */
    public static void scaleArray1D(float[] array, boolean isComplex){
        //get max min for scaling
        float[] alphta = computeAlphaBeta1D(array,isComplex);
        int grey;
        int sizeArray = isComplex ? array.length/2 : array.length;
        for(int i = 0; i<sizeArray; i++){
            if (isComplex) {
                grey = greyToColor(array[2*i],alphta[0], alphta[1]);
                array[2*i]= grey;
            } else {
                grey = greyToColor(array[i],alphta[0], alphta[1]);
                array[i]= grey;
            }
        }
    }

    /**
     * Correct an array looking at the repartitions of the pixels.
     * <br>
     * The idea here is when we have at least 2 (hardcoded value) pixel with the same value
     * it becames the new min, same for the max.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @param isComplex the is complex
     */
    public static void correctArray1Das2D(double[] array, int width, int height, boolean isComplex){
        //We get the repartitions of the pixels
        int[] tmpOut = new int[256];
        int grey;
        for(int j = 0; j<height; j++){
            for(int i = 0;i<width; i++){
                if (isComplex) {
                    grey = (int) array[2*(i+j*width)];
                } else {
                    grey = (int) array[(i+j*width)];
                }

                tmpOut[grey]++;
            }
        }
        //We search a new min
        double alpha,beta;
        int newMin=0;
        int newMax=255;
        //If at least 2 pixels have the color we consider it as new min or new max
        for (int i = 0; i < 256; i++) {
            if (newMin == 0 && tmpOut[i] > 2) {
                newMin = i;
            }
            if (newMax == 255 && tmpOut[255-i] > 2) {
                newMax = 255-i;
            }
        }
        //We recompute the min max with these values
        if (newMin < newMax) {
            alpha = 255.0/(newMax - newMin);
            beta = -alpha*newMin;
        } else {
            alpha = 0.0;
            beta = 0.0;
        }
        //We apply this min max on the input arrays
        for(int j = 0; j<height; j++){
            for(int i = 0; i<width; i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*width)],alpha,beta);
                    array[2*(i+j*width)] = grey;
                } else {
                    grey = greyToColor(array[(i+j*width)],alpha,beta);
                    array[(i+j*width)] = grey;
                }
            }
        }
    }

    /**
     * Correct an array looking at the repartitions of the pixels.
     * <br>
     * The idea here is when we have at least 2 (hardcoded value) pixel with the same value
     * it becames the new min, same for the max.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @param isComplex the is complex
     */
    public static void correctArray1Das2D(float[] array, int width, int height, boolean isComplex){
        //We get the repartitions of the pixels
        int[] tmpOut = new int[256];
        int grey;
        for(int j = 0; j<height; j++){
            for(int i = 0;i<width; i++){
                if (isComplex) {
                    grey = (int) array[2*(i+j*width)];
                } else {
                    grey = (int) array[(i+j*width)];
                }
                tmpOut[grey]++;
            }
        }
        //We search a new min
        double alpha,beta;
        int newMin=0;
        int newMax=255;
        //If at least 2 pixels have the color we consider it as new min or new max
        for (int i = 0; i < 256; i++) {
            if (newMin == 0 && tmpOut[i] > 2) {
                newMin = i;
            }
            if (newMax == 255 && tmpOut[255-i] > 2) {
                newMax = 255-i;
            }
        }
        //We recompute the min max with these values
        if (newMin < newMax) {
            alpha = 255.0/(newMax - newMin);
            beta = -alpha*newMin;
        } else {
            alpha = 0.0;
            beta = 0.0;
        }
        //We apply this min max on the input arrays
        for(int j = 0; j<height; j++){
            for(int i = 0; i<width; i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*width)],alpha,beta);
                    array[2*(i+j*width)] = grey;
                } else {
                    grey = greyToColor(array[(i+j*width)],alpha,beta);
                    array[(i+j*width)] = grey;
                }
            }
        }
    }

    /**
     * Correct an array looking at the repartitions of the pixels.
     * <br>
     * The idea here is when we have at least 2 (hardcoded value) pixel with the same value
     * it becames the new min, same for the max.
     *
     * @param array the array
     * @param isComplex the is complex
     */
    public static void correctArray1D(double[] array, boolean isComplex){
        //We get the repartitions of the pixels
        int[] tmpOut = new int[256];
        int grey;
        int sizeArray = isComplex ? array.length/2 : array.length;
        for(int i = 0; i<sizeArray; i++){
            if (isComplex) {
                grey = (int) array[2*i];
            } else {
                grey = (int) array[i];
            }
            tmpOut[grey]++;
        }
        //We search a new min
        double alpha,beta;
        int newMin=0;
        int newMax=255;
        //If at least 2 pixels have the color we consider it as new min or new max
        for (int i = 0; i < 256; i++) {
            if (newMin == 0 && tmpOut[i] > 2) {
                newMin = i;
            }
            if (newMax == 255 && tmpOut[255-i] > 2) {
                newMax = 255-i;
            }
        }
        //We recompute the min max with these values
        if (newMin < newMax) {
            alpha = 255.0/(newMax - newMin);
            beta = -alpha*newMin;
        } else {
            alpha = 0.0;
            beta = 0.0;
        }
        //We apply this min max on the input arrays
        for(int i = 0; i<sizeArray; i++){
            if (isComplex) {
                grey = greyToColor(array[2*i],alpha,beta);
                array[2*i] = grey;
            } else {
                grey = greyToColor(array[i],alpha,beta);
                array[i] = grey;
            }
        }
    }

    /**
     * Correct an array looking at the repartitions of the pixels.
     * <br>
     * The idea here is when we have at least 2 (hardcoded value) pixel with the same value
     * it becames the new min, same for the max.
     *
     * @param array the array
     * @param isComplex the is complex
     */
    public static void correctArray1D(float[] array, boolean isComplex){
        //We get the repartitions of the pixels
        int[] tmpOut = new int[256];
        int grey;
        int sizeArray = isComplex ? array.length/2 : array.length;
        for(int i = 0; i<sizeArray; i++){
            if (isComplex) {
                grey = (int) array[2*i];
            } else {
                grey = (int) array[i];
            }
            tmpOut[grey]++;
        }
        //We search a new min
        double alpha,beta;
        int newMin=0;
        int newMax=255;
        //If at least 2 pixels have the color we consider it as new min or new max
        for (int i = 0; i < 256; i++) {
            if (newMin == 0 && tmpOut[i] > 2) {
                newMin = i;
            }
            if (newMax == 255 && tmpOut[255-i] > 2) {
                newMax = 255-i;
            }
        }
        //We recompute the min max with these values
        if (newMin < newMax) {
            alpha = 255.0/(newMax - newMin);
            beta = -alpha*newMin;
        } else {
            alpha = 0.0;
            beta = 0.0;
        }
        //We apply this min max on the input arrays
        for(int i = 0; i<sizeArray; i++){
            if (isComplex) {
                grey = greyToColor(array[2*i],alpha,beta);
                array[2*i] = grey;
            } else {
                grey = greyToColor(array[i],alpha,beta);
                array[i] = grey;
            }
        }
    }

    /**
     * Create a buffered image and use a colormap with the grey value in input.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @param isComplex the is complex
     * @return the buffered image
     */
    public static BufferedImage colorArray1D(double[] array, int width, int height, boolean isComplex){
        BufferedImage imageout = createNewBufferedImage(width, height);
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        int[] tmp = new int[3];
        for(int j = 0; j<imageout.getHeight(); j++){
            for(int i = 0; i<imageout.getWidth(); i++){
                if (isComplex) {
                    grey = (int)array[2*(i+j*imageout.getWidth())];
                } else {
                    grey = (int)array[(i+j*imageout.getWidth())];
                }
                tmp[0]=map.r[grey];
                tmp[1]=map.g[grey];
                tmp[2]=map.b[grey];
                raster.setPixel(i, j, tmp);
            }
        }
        return imageout;
    }

    /**
     * Create a buffered image and use a colormap with the grey value in input.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @param isComplex the is complex
     * @return the buffered image
     */
    public static BufferedImage colorArray1D(float[] array,  int width, int height, boolean isComplex){
        BufferedImage imageout = createNewBufferedImage(width, height);
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        int[] tmp = new int[3];
        for(int j = 0; j<imageout.getHeight(); j++){
            for(int i = 0; i<imageout.getWidth(); i++){
                if (isComplex) {
                    grey = (int)array[2*(i+j*imageout.getWidth())];
                } else {
                    grey = (int)array[(i+j*imageout.getWidth())];
                }
                tmp[0]=map.r[grey];
                tmp[1]=map.g[grey];
                tmp[2]=map.b[grey];
                raster.setPixel(i, j, tmp);
            }
        }
        return imageout;
    }

    /**
     * Create a buffered image, simply copy array to buffered image.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @param isComplex the is complex
     * @return the buffered image
     */
    public static BufferedImage arrayToImage1D(double[] array, int width, int height, boolean isComplex){
        //BufferedImage imageout = createNewBufferedImage(width,height);
        BufferedImage imageout = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
        WritableRaster raster = imageout.getRaster();
        int grey;
        int[] tmp = new int[3];
        for(int j = 0; j<imageout.getHeight(); j++){
            for(int i = 0; i<imageout.getWidth(); i++){
                if (isComplex) {
                    grey = (int)array[2*(i+j*imageout.getWidth())];
                } else {
                    grey = (int)array[(i+j*imageout.getWidth())];
                }
                tmp[0]=tmp[1]=tmp[2]=grey;
                raster.setPixel(i, j, tmp);
            }
        }
        return imageout;
    }

    /**
     * Create a buffered image, simply copy array to buffered image.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @param isComplex the is complex
     * @return the buffered image
     */
    public static BufferedImage arrayToImage1D(float[] array, int width, int height, boolean isComplex){
        //BufferedImage imageout = createNewBufferedImage(width, height);
        BufferedImage imageout = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
        WritableRaster raster = imageout.getRaster();
        int grey;
        int[] tmp = new int[3];
        for(int j = 0; j<imageout.getHeight(); j++){
            for(int i = 0; i<imageout.getWidth(); i++){
                if (isComplex) {
                    grey = (int)array[2*(i+j*imageout.getHeight())];
                } else {
                    grey = (int)array[(i+j*imageout.getHeight())];
                }
                tmp[0]=tmp[1]=tmp[2]=grey;
                raster.setPixel(i, j, tmp);
            }
        }
        return imageout;
    }

    /**
     * Front function that will apply different job on the given array.
     *
     * @param array a complex array
     * @param job the job
     * @param width the width
     * @param height the height
     * @param isComplex the is complex
     * @return the buffered image
     */
    public static BufferedImage arrayToImage1D(double[] array, int job, int width, int height, boolean isComplex){
        if (job > SCALE_CORRECTED_COLORMAP) {
            System.err.println("Wrong job");
            throw new IllegalArgumentException();
        }
        BufferedImage out;

        if (!(job == NO_SCALE)) {
            scaleArray1D(array, isComplex);
        }
        //If necessary we correct
        if (job == SCALE_CORRECTED || job == SCALE_CORRECTED_COLORMAP) {
            correctArray1D(array, isComplex);
        }
        //We apply lastly the colormap transformation
        if (job == SCALE_COLORMAP || job == SCALE_CORRECTED_COLORMAP) {
            out = colorArray1D(array, width, height, isComplex);
        }else{
            out = arrayToImage1D(array, width, height, isComplex);
        }
        return out;
    }

    /**
     * Front function that will apply different job on the given array.
     *
     * @param array a complex array
     * @param job the job
     * @param width the width
     * @param height the height
     * @param isComplex the is complex
     * @return the buffered image
     */
    public static BufferedImage arrayToImage1D(float[] array, int job, int width, int height, boolean isComplex){
        if (job > SCALE_CORRECTED_COLORMAP) {
            System.err.println("Wrong job");
            throw new IllegalArgumentException();
        }
        BufferedImage out;
        if (!(job == NO_SCALE)) {
            scaleArray1D(array, isComplex);
        }
        //If necessary we correct
        if (job == SCALE_CORRECTED || job == SCALE_CORRECTED_COLORMAP) {
            correctArray1D(array, isComplex);
        }
        //We apply lastly the colormap transformation
        if (job == SCALE_COLORMAP || job == SCALE_CORRECTED_COLORMAP) {
            out = colorArray1D(array, width, height, isComplex);
        }else{
            out = arrayToImage1D(array, width, height, isComplex);
        }
        return out;
    }

    /**
     * ******************************** Image PADDING *********************************.
     */

    /**
     * PSF has a size that begin with pixels that are non zeros pixels.
     * To find this size we assume the PSF to be centered and so we search for the two first non zero
     * on width/2 and height/2.
     * 
     * @param PSF the psf
     * @return the int
     */
    public static int estimatePsfSize(BufferedImage PSF){
        int width = PSF.getWidth();
        int height = PSF.getHeight();
        WritableRaster raster = PSF.getRaster();
        int min = -1, max = width, prev = 0;

        for (int i = 0; i < width; i++){
            int greyBegin = colorToGrey(raster.getPixel(i,height/2, (int[])null));
            int greyEnd = colorToGrey(raster.getPixel(width-i-1,height/2, (int[])null));
            if (greyBegin != 0 && min == -1) {
                min = i;
            }
            if (greyEnd != 0 && max == width) {
                max = width-i-1;
            }
        }
        prev = max -min;
        max = height;
        min = -1;
        for (int i = 0; i < height; i++){
            int greyBegin = colorToGrey(raster.getPixel(width/2,i, (int[])null));
            int greyEnd = colorToGrey(raster.getPixel(width/2,height-i-1, (int[])null));
            if (greyBegin != 0 && min == -1) {
                min = i;
            }
            if (greyEnd != 0 && max == height) {
                max = height-i-1;
            }
        }
        return (max-min) > prev ? (max-min) : prev;
    }

    private static BufferedImage createZeroBufferedImage(int width, int height){
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
        WritableRaster rasterImage = out.getRaster();
        int[] tmp = new int[]{0,0,0};
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                rasterImage.setPixel(i,j,tmp);
            }
        }
        return out;
    }

    /**
     * Zero pad an image with the new size:<br>
     * WidthOutput = WidthInput + sizePSF<br>
     * HeightOutput = HeightInput +sizePSF.
     *
     * @param image the image
     * @param coef 
     * @return the buffered image
     */
    public static BufferedImage imagePad(BufferedImage image, double coef) {
        BufferedImage pad = createZeroBufferedImage((int)(image.getWidth()*coef), (int)(image.getHeight()*coef));
        //BufferedImage pad = new BufferedImage(image.getWidth()+sizePading, image.getHeight()+sizePading, BufferedImage.TYPE_USHORT_GRAY);
        WritableRaster rasterImage = image.getRaster();
        WritableRaster rasterPad = pad.getRaster();
        int padW = (int)(image.getWidth()*coef-image.getWidth())/2;
        int padH = (int)(image.getHeight()*coef-image.getHeight())/2;
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                rasterPad.setPixel(padW+i,padH+j , rasterImage.getPixel(i,j, (int[])null));
            }
        }
        return pad;
    }

    public static ArrayList<BufferedImage> imagePad(ArrayList<BufferedImage> image, double coef,boolean isPsf) {
        ArrayList<BufferedImage> out = new ArrayList<BufferedImage>();
        int width = image.get(0).getWidth();
        int height = image.get(0).getHeight();
        int size = image.size();
        //BufferedImage zero = createNewBufferedImage(width+sizePSF, height+sizePSF);
        BufferedImage zero = createZeroBufferedImage((int)(width*coef), (int)(height*coef));
        double sizePad = size*coef-size;
        //isPsf = false;
        for (int i = 0; i < size; i++) {
            out.add(imagePad(image.get(i), coef));
        }
        for (int i = 0; i < (int)sizePad; i++) {
            out.add(zero);
        }
        return out;
    }

    /**
     * remove the pad of an image with the size given:<br>
     * WidthInput = WidthOutput + sizePSF<br>
     * HeightInput = HeightOutput +sizePSF.
     *
     * @param image the image
     * @param sizePSF the size psf
     * @return the buffered image
     */
    public static BufferedImage imageUnPad(BufferedImage image, int sizePSF) {
        int hlf = sizePSF/2;
        return image.getSubimage(hlf, hlf, image.getWidth()-sizePSF, image.getHeight()-sizePSF);
    }

    public static ArrayList<BufferedImage> imageUnPad(ArrayList<BufferedImage> image, int sizePSF) {
        ArrayList<BufferedImage> out = new ArrayList<BufferedImage>();
        for (int i = image.size()/4; i < (image.size()*3)/4; i++) {
            out.add(imageUnPad(image.get(i), sizePSF));
        }

        /*for (int i = (image.size()*3)/4; i < image.size(); i++) {
            out.add(imageUnPad(image.get(i), sizePSF));
        }
        for (int i = 0; i < image.size()/4; i++) {
            out.add(imageUnPad(image.get(i), sizePSF));
        }*/
        return out;
    }

    /**
     * ******************************** PSF PADDING *********************************.
     */

    /**
     * psfPadding will split the input psf and scale it to the size of the image.<br>
     * <br>
     * PSF (WidthPsf HeightPsf)<br>
     * Image (WidthImage HeightImage)<br>
     * <br>
     *     A | B     <br>
     *     -------   PSF(width height)<br>
     *     C | D     <br>
     *  <br>
     *   And we want will send them to the other side in the image<br>
     *  <br>
     *     D | C     <br>
     *     -------    Image (WidthImage HeightImage)<br>
     *     B | A     <br>
     *
     * @param image the image
     * @param imagePsf the image psf
     * @param isComplex the is complex
     * @return the double[]
     */
    public static double[] psfPadding1D(BufferedImage image, BufferedImage imagePsf, boolean isComplex) {
        double []tableau_psf;
        int width = image.getWidth();
        int height = image.getHeight();
        if (isComplex) {
            tableau_psf = new double[width*2*height];
        } else {
            tableau_psf = new double[width*height];
        }
        int psfH = imagePsf.getHeight();
        int psfW = imagePsf.getWidth();
        double[]test = imageToArray1D(imagePsf,false);

        return psfPadding1D(tableau_psf,width,height,test,psfW,psfH,isComplex);
    }

    /**
     * psfPadding will split the input psf and scale it to the size of the image.<br>
     * <br>
     * PSF (WidthPsf HeightPsf)<br>
     * Image (WidthImage HeightImage)<br>
     * <br>
     *     A | B     <br>
     *     -------   PSF(width height)<br>
     *     C | D     <br>
     *  <br>
     *   And we want will send them to the other side in the image<br>
     *  <br>
     *     D | C     <br>
     *     -------    Image (WidthImage HeightImage)<br>
     *     B | A     <br>
     *
     * @param inputSpace the input space
     * @param outputSpace the output space
     * @param imagePsf the image psf
     * @param singlePrecision the single precision
     * @param isComplex the is complex
     * @return the shaped vector
     */
    public static ShapedVector psfPadding1D(ShapedVectorSpace inputSpace, ShapedVectorSpace outputSpace, ShapedVector imagePsf, boolean singlePrecision, boolean isComplex) {
        if (singlePrecision) {
            FloatShapedVectorSpace spaceFloat = (FloatShapedVectorSpace)outputSpace;
            FloatShapedVector vectorPsf = (FloatShapedVector)imagePsf;
            if (!(spaceFloat.getRank() == 2)) {
                throw new IllegalArgumentException("The rank of vector must be 2");
            }
            int[] shape = spaceFloat.cloneShape();
            int[] shapePsf = ((FloatShapedVectorSpace)imagePsf.getSpace()).cloneShape();
            float[] psfPad = psfPadding1D(spaceFloat.create().getData(),shape[1],
                    shape[0],vectorPsf.getData(),shapePsf[1],shapePsf[0],isComplex);
            return spaceFloat.wrap(psfPad);
        } else {
            DoubleShapedVectorSpace spaceDoubleOut = (DoubleShapedVectorSpace)outputSpace;
            DoubleShapedVectorSpace spaceDoubleIn = (DoubleShapedVectorSpace)inputSpace;
            DoubleShapedVector vectorPsf = (DoubleShapedVector)imagePsf;
            if (!(spaceDoubleOut.getRank() == 2)) {
                throw new IllegalArgumentException("The rank of vector must be 2");
            }
            int[] shape = spaceDoubleIn.cloneShape();
            int[] shapePsf = ((DoubleShapedVectorSpace)imagePsf.getSpace()).cloneShape();
            double[] psfPad = psfPadding1D(spaceDoubleOut.create().getData(),shape[1],
                    shape[0], vectorPsf.getData(),shapePsf[1],shapePsf[0],isComplex);
            return spaceDoubleOut.wrap(psfPad);
        }
    }

    /**
     * psfPadding will split the input psf and scale it to the size of the image.<br>
     * <br>
     * PSF (WidthPsf HeightPsf)<br>
     * Image (WidthImage HeightImage)<br>
     * <br>
     *     A | B     <br>
     *     -------   PSF(width height)<br>
     *     C | D     <br>
     *  <br>
     *   And we want will send them to the other side in the image<br>
     *  <br>
     *     D | C     <br>
     *     -------    Image (WidthImage HeightImage)<br>
     *     B | A     <br>
     *
     * @param imageout the imageout
     * @param imageWidth the image width
     * @param imageHeight the image height
     * @param imagePsf the image psf
     * @param psfWidth the psf width
     * @param psfHeight the psf height
     * @param isComplex the is complex
     * @return the double[]
     */
    public static double[] psfPadding1D(double[] imageout,int imageWidth, int imageHeight, double[] imagePsf, int psfWidth, int psfHeight, boolean isComplex) {
        int demiPsfW = psfWidth/2;int demiPsfH = psfHeight/2;
        //System.out.println(imageWidth+" "+imageHeight+" "+psfWidth+" "+psfHeight+" "+isComplex);
        // IMAGE point of view:
        // It means we have the PSF split in four blocks A,B,C,D
        //   A | B     ->
        //   -----     -> PSF
        //   C | D     ->
        //
        // And we want will send them to the other side in the image
        //
        //   D | C     ->
        //   -----     -> Image
        //   B | A     ->
        //Bloc haut a gauche: bloc psf D
        for(int j = 0; j < demiPsfH; j++){
            for(int i = 0; i < demiPsfW; i++){
                imageout[i+j*imageWidth] = imagePsf[(demiPsfW+i)+(demiPsfH+j)*psfWidth];
            }
        }
        //bloc haut a droite: bloc psf C
        for(int j = 0; j < demiPsfH; j++){
            for(int i = imageWidth-demiPsfW; i < imageWidth ; i++){
                imageout[i+j*imageWidth] = imagePsf[(demiPsfW-imageWidth+i)+(demiPsfH+j)*psfWidth];
            }
        }
        //bloc bas a gauche: bloc psf B
        for(int j = imageHeight-demiPsfH; j < imageHeight; j++){
            for(int i = 0; i < demiPsfW; i++){
                imageout[i+j*imageWidth] = imagePsf[(demiPsfW+i)+(demiPsfH-imageHeight+j)*psfWidth];
            }
        }
        //bloc bas a droite: bloc psf A
        for(int j = imageHeight-demiPsfH; j < imageHeight; j++){
            for(int i = imageWidth-demiPsfW; i < imageWidth; i++){
                imageout[i+j*imageWidth] = imagePsf[(demiPsfW-imageWidth+i)+(demiPsfH-imageHeight+j)*psfWidth];
            }
        }
        return imageout;
    }

    /**
     * Shift zero-frequency component to center of spectrum for a 3D array.
     *
     * @param psf the a
     * @param out 
     * @param w the width
     * @param h the height
     * @param d the depth
     * @return the double[]
     */
    public static double[] fftShift3D(double[] psf, double[] out, int w, int h, int d)
    {   
        int wh = w*h;
        for (int k = 0; k < d/2; k++)
        {
            for(int j = 0; j < h/2; j++)
            {
                for(int i = 0; i < w/2; i++)
                {
                    //Le coté face
                    //0,0,0-->w,h,d ou bas gauche face en haut droit fond
                    out[w - w/2 + i + w*(h - h/2 + j) + wh*(d - d/2 + k)] = psf[i + w*j + k*wh];
                    //w/2,0,0-->0,h/2,d/2 ou bas droite face en haut gauche fond
                    out[i + w*(h - h/2 + j) + wh*(d - d/2 + k)] = psf[i + w/2 + w*j + k*wh];
                    //haut gauche face en bas droite fond
                    out[w - w/2 + i + w*j + wh*(d - d/2 + k)] = psf[i + w*(j + h/2) + k*wh];
                    //haut droit face en bas gauche fond
                    out[i + w*j+ wh*(d - d/2 + k)] = psf[i + w/2 + w*(j + h/2) + k*wh];

                    //le coté fond en face
                    out[w - w/2 + i + w*(h - h/2 + j) + k*wh] = psf[i + w*j + wh*(d - d/2 + k)];
                    out[i + w*(h - h/2 + j) + k*wh] = psf[i + w/2 + w*j + wh*(d - d/2 + k)];
                    out[w - w/2 + i + w*j + k*wh] = psf[i + w*(j + h/2) + wh*(d - d/2 + k)];
                    out[i + w*j+ k*wh] = psf[i + w/2 + w*(j + h/2) + wh*(d - d/2 + k)];
                }
            }
        }
        return out;
    }

    /**
     * Here we are padding a cube, no scale only padding
     * 
     * Cube : 
     *     H
     *    /
     *   /
     *  o---- W
     *  |
     *  |
     *  Z
     *  
     * Front:
     *  1 2
     *  3 4
     *  
     *  back:
     *  5 6
     *  7 8
     *  
     *  1<=>8
     *  3<=>6
     *  2<=>7
     *  4<=>5
     * Bloc X to Y
     * 
     * dest( pos(Y) ) = in( pos(X) )
     * 
     * @param psfOut
     * @param psfIn
     * @param psfWidth
     * @param psfHeight
     * @param psfZ
     * @return
     */
    public static double[] psf3DPadding1D(double[] psfOut, double[] psfIn, int psfWidth, int psfHeight, int psfZ) {
        int demiPsfW = psfWidth/2;
        int demiPsfH = psfHeight/2;
        int demiPsfZ = psfZ/2;
        int strideJ = psfWidth;
        int strideK = psfWidth*psfHeight;
        //Bloc 8 to 1
        for (int k = 0; k < demiPsfZ; k++) {
            for(int j = 0; j < demiPsfH; j++){
                for(int i = 0; i < demiPsfW; i++){
                    psfOut[i+j*strideJ+k*strideK] = psfIn[(demiPsfW+i)+(demiPsfH+j)*strideJ+(demiPsfZ+k)*strideK];
                }
            }
        }
        //Bloc 1 to 8
        for (int k = demiPsfZ; k < psfZ; k++) {
            for(int j = demiPsfH; j < psfHeight; j++){
                for(int i = demiPsfW; i < psfWidth; i++){
                    psfOut[i+j*strideJ+k*strideK] = psfIn[(i-demiPsfW)+(j-demiPsfH)*strideJ+(k-demiPsfZ)*strideK];
                }
            }
        }
        //bloc 7 to 2
        for (int k = 0; k < demiPsfZ; k++) {
            for(int j = 0; j < demiPsfH; j++){
                for(int i = demiPsfW; i < psfWidth ; i++){
                    psfOut[i+j*strideJ+k*strideK] = psfIn[(i-demiPsfW)+(demiPsfH+j)*strideJ+(demiPsfZ+k)*strideK];
                }
            }
        }
        //bloc 2 to 7
        for (int k = demiPsfZ; k < psfZ; k++) {
            for(int j = demiPsfH; j < psfHeight; j++){
                for(int i = 0; i < demiPsfW ; i++){
                    psfOut[i+j*strideJ+k*strideK] = psfIn[(demiPsfW+i)+(j-demiPsfH)*strideJ+(k-demiPsfZ)*strideK];
                }
            }
        }
        //bloc 6 to 3
        for (int k = demiPsfZ; k < psfZ; k++) {
            for(int j = 0; j < demiPsfH; j++){
                for(int i = 0; i < demiPsfW; i++){
                    psfOut[i+j*strideJ+k*strideK] = psfIn[(demiPsfW+i)+(demiPsfH+j)*strideJ+(k-demiPsfZ)*strideK];
                }
            }
        }
        //bloc 3 to 6
        for (int k = 0; k < demiPsfZ; k++) {
            for(int j = demiPsfH; j < psfHeight; j++){
                for(int i = demiPsfW; i < psfWidth; i++){
                    psfOut[i+j*strideJ+k*strideK] = psfIn[(i-demiPsfW)+(j-demiPsfH)*strideJ+(demiPsfZ+k)*strideK];
                }
            }
        }
        //bloc 5 to 4
        for (int k = demiPsfZ; k < psfZ; k++) {
            for(int j = 0; j < demiPsfH; j++){
                for(int i = demiPsfW; i < psfWidth; i++){
                    psfOut[i+j*strideJ+k*strideK] = psfIn[(i-demiPsfW)+(demiPsfH+j)*strideJ+(k-demiPsfZ)*strideK];
                }
            }
        }

        //bloc 4 to 5
        for (int k = 0; k < demiPsfZ; k++) {
            for(int j = demiPsfH; j < psfHeight; j++){
                for(int i = 0; i < demiPsfW; i++){
                    psfOut[i+j*strideJ+k*strideK] = psfIn[(demiPsfW+i)+(j-demiPsfH)*strideJ+(demiPsfZ+k)*strideK];
                }
            }
        }
        return psfOut;
    }

    /**
     * psfPadding will split the input psf and scale it to the size of the image.<br>
     * <br>
     * PSF (WidthPsf HeightPsf)<br>
     * Image (WidthImage HeightImage)<br>
     * <br>
     *     A | B     <br>
     *     -------   PSF(width height)<br>
     *     C | D     <br>
     *  <br>
     *   And we want will send them to the other side in the image<br>
     *  <br>
     *     D | C     <br>
     *     -------    Image (WidthImage HeightImage)<br>
     *     B | A     <br>
     *
     * @param image the image
     * @param imagePsf the image psf
     * @param isComplex the is complex
     * @return the float[]
     */
    public static float[] psfPadding1DFloat(BufferedImage image, BufferedImage imagePsf, boolean isComplex) {
        float []tableau_psf;
        int width = image.getWidth();
        int height = image.getHeight();
        if (isComplex) {
            tableau_psf = new float[width*2*height];
        } else {
            tableau_psf = new float[width*height];
        }
        int psfH = imagePsf.getHeight();
        int psfW = imagePsf.getWidth();
        float[]test = imageToArray1DFloat(imagePsf,false);

        return psfPadding1D(tableau_psf,width,height,test,psfH,psfW,isComplex);
    }

    /**
     * psfPadding will split the input psf and scale it to the size of the image.<br>
     * <br>
     * PSF (WidthPsf HeightPsf)<br>
     * Image (WidthImage HeightImage)<br>
     * <br>
     *     A | B     <br>
     *     -------   PSF(width height)<br>
     *     C | D     <br>
     *  <br>
     *   And we want will send them to the other side in the image<br>
     *  <br>
     *     D | C     <br>
     *     -------    Image (WidthImage HeightImage)<br>
     *     B | A     <br>
     *
     * @param imageout the imageout
     * @param imageWidth the image width
     * @param imageHeight the image height
     * @param imagePsf the image psf
     * @param psfWidth the psf width
     * @param psfHeight the psf height
     * @param isComplex the is complex
     * @return the float[]
     */
    public static float[] psfPadding1D(float[] imageout,int imageWidth, int imageHeight, float[] imagePsf, int psfWidth, int psfHeight, boolean isComplex) {
        int demiPsfW = psfWidth/2;int demiPsfH = psfHeight/2;

        // IMAGE point of view:
        // It means we have the PSF split in four blocks A,B,C,D
        //   A | B     ->
        //   -----     -> PSF
        //   C | D     ->
        //
        // And we want will send them to the other side in the image
        //
        //   D | C     ->
        //   -----     -> Image
        //   B | A     ->
        //Here we are writing at (i+j*imageHeight)
        int coef = isComplex ? 1 : 1;
        //Bloc haut a gauche: D
        for(int j = 0; j < demiPsfH; j++){
            for(int i = 0; i < demiPsfW; i++){
                imageout[i+coef*j*imageWidth] = imagePsf[(demiPsfW+i)+(demiPsfH+j)*psfWidth];
            }
        }
        //bloc haut a droite: C
        for(int j = 0; j < demiPsfH; j++){
            for(int i = imageWidth-demiPsfW; i < imageWidth ; i++){
                imageout[i+coef*j*imageWidth] = imagePsf[(demiPsfW-imageWidth+i)+(demiPsfH+j)*psfWidth];
            }
        }
        //bloc bas a gauche: B
        for(int j = imageHeight-demiPsfH; j < imageHeight; j++){
            for(int i = 0; i < demiPsfW; i++){
                imageout[i+coef*j*imageWidth] = imagePsf[(demiPsfW+i)+(demiPsfH-imageHeight+j)*psfWidth];
            }
        }
        //bloc bas a droite: A
        for(int j = imageHeight-demiPsfH; j < imageHeight; j++){
            for(int i = imageWidth-demiPsfW; i < imageWidth; i++){
                imageout[i+coef*j*imageWidth] = imagePsf[(demiPsfW-imageWidth+i)+(demiPsfH-imageHeight+j)*psfWidth];
            }
        }
        return imageout;
    }

    /**
     * Returns 1d array (column major) of a 2d array.
     *
     * @param In the in
     * @return 1d array
     */
    public static double[] array2DTo1D(double[][] In)
    {
        int W = In.length;
        int H = In[0].length;
        double[] Out = new double[H*W];
        for (int j = 0; j < H; j++)
            for (int i = 0; i < W; i++)
                Out[j*W + i] = In[i][j];
        return Out;
    }

    /**
     * Returns 2d array (column major) of a 1d array.
     *
     * @param In 1d array of double
     * @param H the h
     * @return 2d array
     */
    public static double[][] array1DTo2D(double[] In, int H)
    {
        int W = In.length/H;
        double Out[][] = new double[H][W];
        for (int j = 0; j < H; j++)
            for (int i = 0; i < W; i++)
                Out[i][j] = In[j*W + i];
        return Out;
    }


    /**
     * Show buffered image.
     *
     * @param I the i
     */
    public static void showBufferedImage(BufferedImage I)
    {
        JFrame frame = new JFrame();
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(I));
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Save buffered image.
     *
     * @param I the i
     * @param name the name
     */
    public static void saveBufferedImage(BufferedImage I, String name)
    {
        try
        {
            ImageIO.write(I, "PNG", new File(name));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open as buffered image.
     *
     * @param path the path
     * @return the buffered image
     */
    public static BufferedImage openAsBufferedImage(String path) {
        BufferedImage I = null;
        try {
            I = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return I;
    }

    /**
     * Save array to image.
     *
     * @param A the a
     * @param W the w
     * @param name the name
     */
    public static void saveArrayToImage(double[] A, int W, String name)
    {
        int H = A.length/W;
        BufferedImage I = arrayToImage1D(A, W, H, false);
        saveBufferedImage(I, name);
    }
    private static String printTime(){
        LocalDateTime now = LocalDateTime.now();
        return now.get(DateTimeFieldType.hourOfDay())+"h "+now.getMinuteOfHour()+"min "+now.getSecondOfMinute()+"sec";

    }

    public static void printTimeNow(){
        System.out.println(printTime());
    }

    public static void printTimeNow(String before){
        System.out.println(before +" "+printTime());
    }

    public static void printTimeNow(String before, String after){
        System.out.println(before +" "+printTime()+" "+after);
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