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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import mitiv.linalg.DoubleVector;
import mitiv.linalg.DoubleVectorSpaceWithRank;
import mitiv.linalg.FloatVector;
import mitiv.linalg.FloatVectorSpaceWithRank;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;

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
     */
    public static final int LOWER_LEFT = 0;
    /**
     * padding options: Nothing is done
     * _______
     * |      |
     * |  #   |
     * |______|
     */
    public static final int CENTERED = 1;
    /**
     * padding options: Nothing is done
     * _______
     * |#    #|
     * |      |
     * |#____#|
     */
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

    /**
     * If we want virtual color for the computed image
     */
    public static int SCALE_COLORMAP = 6;

    /**
     * If we want a correction on the scale + color
     */
    public static int SCALE_CORRECTED_COLORMAP = 7;

    /**
     * Will convert a grey value to another
     *
     * @param g
     * @param alpha
     * @param beta
     * @return
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
     * Convert a RGB value to an int grey value
     *
     * @param r
     * @param g
     * @param b
     * @return
     */
    public static int colorToGrey(double r, double g, double b)
    {
        return (int)(0.2126*r+0.7152*g+0.0722*b);
    }

    /**
     * Take an array (max size 3) of rgb value and convert it to grey
     * 
     * @param rgb
     * @return
     */
    public static int colorToGrey(int[]rgb)
    {
        if (rgb.length == 3) {
            return (int)(0.2126*rgb[0]+0.7152*rgb[1]+0.0722*rgb[2]);
        } else {
            return rgb[0];
        }
    }
    /**
     * Convert a RGB value to an int grey value (float version)
     *
     * @param r
     * @param g
     * @param b
     * @return
     */
    public static int colorToGrey(float r, float g, float b)
    {
        return (int)(0.2126*r+0.7152*g+0.0722*b);
    }

    /**
     * Will scan the tab and return the highest and smallest value
     * @param array Array to parse
     * @return an array of 2 value: {min,max}
     */
    public static double[] computeMinMax(double[][] array, boolean isComplex){
        //trouver min max du tableau
        double min = array[0][0],max = array[0][0];
        int sizeArray = isComplex ? array[0].length/2 : array[0].length;
        for(int j = 0; j<sizeArray; j++){
            for(int i = 0; i<array.length; i++){
                double value;
                if (isComplex) {
                    value = array[i][2*j];
                }else {
                    value = array[i][j];
                }
                if(value < min ){
                    min = value;
                }
                if(value > max ){
                    max = value;
                }
            }
        }
        return new double[]{min,max};
    }

    /**
     * Will scan the tab and return the highest and smallest value (float version)
     * 
     * @param array Array to parse
     * @return an array of 2 value: {min,max}
     */
    public static float[] computeMinMax(float[][] array, boolean isComplex){
        //trouver min max du tableau
        float min = array[0][0],max = array[0][0];
        int sizeArray = isComplex ? array[0].length/2 : array[0].length;
        for(int j = 0; j<sizeArray; j++){
            for(int i = 0; i<array.length; i++){
                float value;
                if (isComplex) {
                    value = array[i][2*j];
                }else {
                    value = array[i][j];
                }
                if(value < min ){
                    min = value;
                }
                if(value > max ){
                    max = value;
                }
            }
        }
        return new float[]{min,max};
    }

    /**
     * Will scan the tab and return the highest and smallest value
     * 
     * @param array Array to parse
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
     * Will scan the tab and return the highest and smallest value
     * 
     * @param array Array to parse
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
     * @param array
     * @param width
     * @param height
     * @param isComplex
     * @return
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
     * @param array
     * @param width
     * @param height
     * @param isComplex
     * @return
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
     * Will scan the tab and return the highest and smallest value
     * 
     * @param array Array to parse
     * @return an array of 2 value: {min,max}
     */
    public static double[] computeAlphaBeta(double[][] array, boolean isComplex){
        double [] out = computeMinMax(array, isComplex);
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
     * Will scan the tab and return the highest and smallest value
     * 
     * @param array Array to parse
     * @return an array of 2 value: {min,max}
     */
    public static float[] computeAlphaBeta(float[][] array, boolean isComplex){
        float [] out = computeMinMax(array,isComplex);
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
     * Compute alpha, beta that are the two parameters needed to scale the data afterward
     * 
     * @param array
     * @param isComplex
     * @return
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
     * Compute alpha, beta that are the two parameters needed to scale the data afterward
     * 
     * @param array
     * @param isComplex
     * @return
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

    /********************************** X TO ARRAY **********************************/

    /**
     * Convert a image to an array of double
     * 
     * @param image
     * @param isComplex
     * @return
     */
    public static double[][] imageToArray(BufferedImage image, boolean isComplex) {
        int height = image.getHeight();
        int width = image.getWidth();
        WritableRaster raster = image.getRaster();
        double [][]out;
        if (isComplex) {
            out = new double[width][2*height];
        } else {
            out = new double[width][height];
        }
        int[] tmp = new int[raster.getNumBands()];
        for(int j=0;j<height;j++){
            for(int i=0;i<width;i++){
                raster.getPixel(i, j, tmp);
                if (tmp.length == 1 || tmp.length == 2) {
                    out[i][j] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                } else {
                    out[i][j] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                }
            }
        }
        return out;
    }

    /**
     * Convert image to float array
     *
     * @param isComplex
     * @return
     */
    public static float[][] imageToArrayFloat(BufferedImage image, boolean isComplex) {
        int height = image.getHeight();
        int width = image.getWidth();
        WritableRaster raster = image.getRaster();
        float [][]out;
        if (isComplex) {
            out = new float[width][2*height];
        } else {
            out = new float[width][height];
        }
        int[] tmp = new int[raster.getNumBands()];
        for(int j=0;j<height;j++){
            for(int i=0;i<width;i++){
                raster.getPixel(i, j, tmp);
                if (tmp.length == 1 || tmp.length == 2) {
                    out[i][j] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                } else {
                    out[i][j] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                }
            }
        }
        return out;
    }

    /**
     * Convert a image to an 1D array of double
     * 
     * @param image
     * @param isComplex
     * @return
     */
    public static double[] imageToArray1D(BufferedImage image, boolean isComplex) {
        int height = image.getHeight();
        int width = image.getWidth();
        WritableRaster raster = image.getRaster();
        double []out;
        if (isComplex) {
            out = new double[width*2*height];
        } else {
            out = new double[width*height];
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
     * Convert image to float 1D array
     * 
     * @param isComplex
     * @return
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

    /**
     * Covert an image to a vector
     * 
     * @param outputSpace
     * @param image
     * @param singlePrecision
     * @param isComplex
     * @return
     */
    public static Vector imageToVector(VectorSpace outputSpace, BufferedImage image, boolean singlePrecision ,boolean isComplex){
        if (singlePrecision) {
            FloatVectorSpaceWithRank space = (FloatVectorSpaceWithRank)outputSpace;
            float[] tab = imageToArray1DFloat(image, isComplex);
            return space.wrap(tab);
        } else {
            DoubleVectorSpaceWithRank space = (DoubleVectorSpaceWithRank)outputSpace;
            double[] tab = imageToArray1D(image, isComplex);
            return space.wrap(tab);
        }
    }

    /**
     * Covert an image to a vector
     * 
     * @param outputSpace
     * @param image
     * @param singlePrecision
     * @param isComplex
     * @return
     */
    public static BufferedImage vectorToImage(VectorSpace outputSpace, Vector vector, int job, boolean singlePrecision ,boolean isComplex){
        if (singlePrecision) {
            FloatVectorSpaceWithRank space = (FloatVectorSpaceWithRank)outputSpace;
            int[] shape = space.cloneShape();
            if (!(space.getRank() == 2)) {
                throw new IllegalArgumentException("The vector should be of rank 2 to create an image");
            }
            return arrayToImage1D(((DoubleVector)vector).getData(), job, shape[1], shape[0], isComplex);
        } else {
            DoubleVectorSpaceWithRank space = (DoubleVectorSpaceWithRank)outputSpace;
            int[] shape = space.cloneShape();
            if (!(space.getRank() == 2)) {
                throw new IllegalArgumentException("The vector should be of rank 2 to create an image");
            }
            return arrayToImage1D(((DoubleVector)vector).getData(), job, shape[1], shape[0], isComplex);
        }
    }

    /********************************** ARRAY TO IMAGE **********************************/

    /**
     * Create a buffered image from another buffered image
     * @param originalImage
     * @return BufferedImage with same characteristics as originalImage
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
     * Create a BufferedImage from a size (WxH)
     * 
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage createNewBufferedImage(int width, int height){
        return new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    }

    /**
     * Create a BufferedImage from an array dimension.
     * <br>
     * It will NOT use array data as input of the BufferedImage, just the SIZE.
     * 
     * @param tab
     * @param isComplex
     * @return
     */
    public static BufferedImage createNewBufferedImage(double[][] tab, boolean isComplex){
        int width, height;
        if (isComplex) {
            height = tab[0].length/2;
            width = tab.length;
        }else{
            height = tab[0].length;
            width = tab.length;
        }
        return createNewBufferedImage(width, height);
    }

    /**
     * Create a BufferedImage from an array dimension.
     * <br>
     * It will NOT use array data as input of the BufferedImage, just the SIZE.
     * 
     * @param tab
     * @param isComplex
     * @return
     */
    public static BufferedImage createNewBufferedImage(float[][] tab, boolean isComplex){
        int width, height;
        if (isComplex) {
            height = tab[0].length/2;
            width = tab.length;
        }else{
            height = tab[0].length;
            width = tab.length;
        }
        return createNewBufferedImage(width, height);
    }

    /**
     * Scale an array by doing in place operations
     * 
     * @param array
     */
    public static void scaleArray(double[][] array, boolean isComplex){
        //get max min for scaling
        double[] alphta = computeAlphaBeta(array, isComplex);
        int grey;
        int sizeArray = isComplex ? array[0].length/2 : array[0].length;
        for(int j = 0; j<sizeArray; j++){
            for(int i = 0; i<array.length; i++){
                if (isComplex) {
                    grey = greyToColor(array[i][2*j],alphta[0], alphta[1]);
                    array[i][2*j]= grey;
                } else {
                    grey = greyToColor(array[i][j],alphta[0], alphta[1]);
                    array[i][j]= grey;
                }
            }
        }
    }

    /**
     * Scale a float array by doing in place operations
     * 
     * @param array
     */
    public static void scaleArray(float[][] array, boolean isComplex){
        //get max min for scaling
        float[] alphta = computeAlphaBeta(array, isComplex);
        int grey;
        int sizeArray = isComplex ? array[0].length/2 : array[0].length;
        for(int j = 0; j<sizeArray; j++){
            for(int i = 0; i<array.length; i++){
                if (isComplex) {
                    grey = greyToColor(array[i][2*j],alphta[0], alphta[1]);
                    array[i][2*j]= grey;
                } else {
                    grey = greyToColor(array[i][j],alphta[0], alphta[1]);
                    array[i][j]= grey;
                }
            }
        }
    }

    /**
     * Scale an 1D array by doing in place operations
     * 
     * @param array
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
     * Scale an 1D array by doing in place operations
     * 
     * @param array
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
     * Scale a float 1D array by doing in place operations
     * 
     * @param array
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
     * Scale a float 1D array by doing in place operations
     * 
     * @param array
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
     * @param array
     */
    public static void correctArray(double[][] array, boolean isComplex){
        //We get the repartitions of the pixels
        int[] tmpOut = new int[256];
        int grey;
        int sizeArray = isComplex ? array[0].length/2 : array[0].length;
        for(int j = 0; j<sizeArray; j++){
            for(int i = 0;i<array.length; i++){
                if (isComplex) {
                    grey = (int) array[i][2*j];
                }else{
                    grey = (int) array[i][j];
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
        for(int j = 0; j<sizeArray; j++){
            for(int i = 0; i<array.length; i++){
                if (isComplex) {
                    grey = greyToColor(array[i][2*j],alpha,beta);
                    array[i][2*j] = grey;
                }else{
                    grey = greyToColor(array[i][j],alpha,beta);
                    array[i][j] = grey;
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
     * @param array
     */
    public static void correctArray(float[][] array, boolean isComplex){
        //We get the repartitions of the pixels
        int[] tmpOut = new int[256];
        int grey;
        int sizeArray = isComplex ? array[0].length/2 : array[0].length;
        for(int j = 0; j<sizeArray; j++){
            for(int i = 0;i<array.length; i++){
                if (isComplex) {
                    grey = (int) array[i][2*j];
                }else{
                    grey = (int) array[i][j];
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
        for(int j = 0; j<sizeArray; j++){
            for(int i = 0; i<array.length; i++){
                if (isComplex) {
                    grey = greyToColor(array[i][2*j],alpha,beta);
                    array[i][2*j] = grey;
                }else{
                    grey = greyToColor(array[i][j],alpha,beta);
                    array[i][j] = grey;
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
     * @param array
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
     * @param array
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
     * @param array
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
     * @param array
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
     * Create a buffered image and use a colormap with the grey value in input
     * 
     * @param array
     * @return
     */
    public static BufferedImage colorArray(double[][] array, boolean isComplex){
        BufferedImage imageout = createNewBufferedImage(array, isComplex);
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        int sizeArray = isComplex ? array[0].length/2 : array[0].length;
        int[] tmp = new int[3];
        for(int j = 0; j<sizeArray; j++){
            for(int i = 0; i<imageout.getWidth(); i++){
                if (isComplex) {
                    grey = (int)array[i][2*j];
                }else{
                    grey = (int)array[i][j];
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
     * Create a buffered image and use a colormap with the grey value in input
     * 
     * @param array
     * @return
     */
    public static BufferedImage colorArray(float[][] array, boolean isComplex){
        BufferedImage imageout = createNewBufferedImage(array, isComplex);
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        int sizeArray = isComplex ? array[0].length/2 : array[0].length;
        int[] tmp = new int[3];
        for(int j = 0; j<sizeArray; j++){
            for(int i = 0; i<imageout.getWidth(); i++){
                if (isComplex) {
                    grey = (int)array[i][2*j];
                }else {
                    grey = (int)array[i][j];
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
     * Create a buffered image and use a colormap with the grey value in input
     * 
     * @param array
     * @return
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
     * Create a buffered image and use a colormap with the grey value in input
     * 
     * @param array
     * @return
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
     * Create a buffered image, simply copy array to buffered image
     * 
     * @param array
     * @return
     */
    public static BufferedImage arrayToImage(double[][] array, boolean isComplex){
        BufferedImage imageout;
        if (isComplex) {
            imageout = createNewBufferedImage(array.length, array[0].length/2);
        }else {
            imageout = createNewBufferedImage(array.length, array[0].length);
        }
        WritableRaster raster = imageout.getRaster();
        int grey;
        int[] tmp = new int[3];
        for(int j = 0; j<imageout.getHeight(); j++){
            for(int i = 0; i<imageout.getWidth(); i++){
                if (isComplex) {
                    grey = (int)array[i][2*j];
                }else {
                    grey = (int)array[i][j];
                }
                tmp[0]=tmp[1]=tmp[2]=grey;
                raster.setPixel(i, j, tmp);
            }
        }
        return imageout;
    }

    /**
     * Create a buffered image, simply copy array to buffered image
     * 
     * @param array
     * @return
     */
    public static BufferedImage arrayToImage(float[][] array, boolean isComplex){
        BufferedImage imageout;
        if (isComplex) {
            imageout = createNewBufferedImage(array.length, array[0].length/2);
        }else {
            imageout = createNewBufferedImage(array.length, array[0].length);
        }
        WritableRaster raster = imageout.getRaster();
        int grey;
        int[] tmp = new int[3];
        for(int j = 0; j<imageout.getHeight(); j++){
            for(int i = 0; i<imageout.getWidth(); i++){
                if (isComplex) {
                    grey = (int)array[i][2*j];
                }else {
                    grey = (int)array[i][j];
                }
                tmp[0]=tmp[1]=tmp[2]=grey;
                raster.setPixel(i, j, tmp);
            }
        }
        return imageout;
    }

    /**
     * Create a buffered image, simply copy array to buffered image
     * 
     * @param array
     * @return
     */
    public static BufferedImage arrayToImage1D(double[] array, int width, int height, boolean isComplex){
        BufferedImage imageout = createNewBufferedImage(width,height);
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
     * Create a buffered image, simply copy array to buffered image
     * 
     * @param array
     * @return
     */
    public static BufferedImage arrayToImage1D(float[] array, int width, int height, boolean isComplex){
        BufferedImage imageout = createNewBufferedImage(width, height);
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
     * Front function that will apply different job on the given array
     *
     * @param array a complex array
     * @param job
     * @return
     */
    public static BufferedImage arrayToImage(double[][] array, int job, boolean isComplex){
        if (job > SCALE_CORRECTED_COLORMAP) {
            System.err.println("Wrong job");
            throw new IllegalArgumentException();
        }
        BufferedImage out;
        if (!(job == NO_SCALE)) {
            scaleArray(array, isComplex);
        }
        scaleArray(array, isComplex);
        //If necessary we correct
        if (job == SCALE_CORRECTED || job == SCALE_CORRECTED_COLORMAP) {
            correctArray(array, isComplex);
        }
        //We apply lastly the colormap transformation
        if (job == SCALE_COLORMAP || job == SCALE_CORRECTED_COLORMAP) {
            out = colorArray(array, isComplex);
        }else{
            out = arrayToImage(array, isComplex);
        }
        return out;
    }

    /**
     * Front function that will apply different job on the given array
     *
     * @param array a complex array
     * @param job
     * @return
     */
    public static BufferedImage arrayToImage(float[][] array, int job, boolean isComplex){
        if (job > SCALE_CORRECTED_COLORMAP) {
            System.err.println("Wrong job");
            throw new IllegalArgumentException();
        }
        BufferedImage out;
        if (!(job == NO_SCALE)) {
            scaleArray(array, isComplex);
        }
        //If necessary we correct
        if (job == SCALE_CORRECTED || job == SCALE_CORRECTED_COLORMAP) {
            correctArray(array, isComplex);
        }
        //We apply lastly the colormap transformation
        if (job == SCALE_COLORMAP || job == SCALE_CORRECTED_COLORMAP) {
            out = colorArray(array, isComplex);
        }else{
            out = arrayToImage(array, isComplex);
        }
        return out;
    }

    /**
     * Front function that will apply different job on the given array
     *
     * @param array a complex array
     * @param job
     * @return
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
     * Front function that will apply different job on the given array
     *
     * @param array a complex array
     * @param job
     * @return
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

    /********************************** Image PADDING **********************************/

    /**
     * PSF has a size that begin with pixels that are non zeros pixels.
     * To find this size we assume the PSF to be centered and so we search for the two first non zero
     * on width/2 and height/2.
     * 
     * @param PSF
     * @return
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

    /**
     * Zero pad an image with the new size:<br>
     * WidthOutput = WidthInput + sizePSF<br>
     * HeightOutput = HeightInput +sizePSF
     * 
     * @param image
     * @param sizePSF
     * @return
     */
    public static BufferedImage imagePad(BufferedImage image, int sizePSF) {
        BufferedImage pad = new BufferedImage(image.getWidth()+sizePSF, image.getHeight()+sizePSF, image.getType());
        Raster rasterImage = image.getData();
        WritableRaster rasterPad = pad.getRaster();
        int hlf = sizePSF/2;
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                rasterPad.setPixel(i+hlf,j+hlf , rasterImage.getPixel(i,j, (int[])null));
            }
        }
        return pad;
    }

    /**
     * remove the pad of an image with the size given:<br>
     * WidthInput = WidthOutput + sizePSF<br>
     * HeightInput = HeightOutput +sizePSF
     * 
     * @param image
     * @param sizePSF
     * @return
     */
    public static BufferedImage imageUnPad(BufferedImage image, int sizePSF) {
        int hlf = sizePSF/2;
        return image.getSubimage(hlf, hlf, image.getWidth()-sizePSF, image.getHeight()-sizePSF);
    }

    /********************************** PSF PADDING **********************************/

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
     * @param image
     * @param imagePsf
     * @param isComplex
     * @return
     */
    public static double[][] psfPadding(BufferedImage image, BufferedImage imagePsf, boolean isComplex) {
        double [][]tableau_psf;
        int width = image.getWidth();
        int height = image.getHeight();
        if (isComplex) {
            tableau_psf = new double[width][2*height];
        } else {
            tableau_psf = new double[width][height];
        }
        double[][]test = imageToArray(imagePsf, false);
        return psfPadding(tableau_psf, test, isComplex);
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
     * @param image
     * @param imagePsf
     * @param isComplex
     * @return
     */
    public static double[][] psfPadding(double[][] imageOut, double[][] imagePsf, boolean isComplex) {
        int height, width = imageOut.length;
        if (isComplex) {
            height = imageOut[0].length/2;
        } else {
            height = imageOut[0].length;
        }
        int psfH = imagePsf[0].length, psfW = imagePsf.length;
        int demiPsfH = imagePsf[0].length/2, demiPsfW = imagePsf.length/2;
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

        //bloc haut a gauche: A
        for(int j = 0; j<demiPsfH; j++){
            for(int i=0;i<demiPsfW;i++){
                imageOut[width-demiPsfW+i][(height-demiPsfH)+j] = imagePsf[i][j];
            }
        }
        //bloc haut a droite: B
        for(int j=0;j<demiPsfH;j++){
            for(int i = demiPsfW; i<psfW; i++){
                imageOut[i-demiPsfW][(height-demiPsfH)+j] = imagePsf[i][j];
            }
        }
        //bloc bas a gauche: C
        for(int j = demiPsfH; j < psfH; j++){
            for(int i = 0; i < demiPsfW; i++){
                imageOut[width-demiPsfW+i][j-demiPsfH] = imagePsf[i][j];
            }
        }
        //bloc bas a droite: D
        for(int j=demiPsfH; j<psfH; j++){
            for(int i = demiPsfW; i<psfW; i++){
                imageOut[i-demiPsfW][j-demiPsfH] = imagePsf[i][j];
            }
        }
        return imageOut;
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
     * @param image
     * @param imagePsf
     * @param isComplex
     * @return
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
     * @param image
     * @param imagePsf
     * @param isComplex
     * @return
     */
    public static Vector psfPadding1D(VectorSpace inputSpace, VectorSpace outputSpace, Vector imagePsf, boolean singlePrecision, boolean isComplex) {
        if (singlePrecision) {
            FloatVectorSpaceWithRank spaceFloat = (FloatVectorSpaceWithRank)outputSpace;
            FloatVector vectorPsf = (FloatVector)imagePsf;
            if (!(spaceFloat.getRank() == 2)) {
                throw new IllegalArgumentException("The rank of vector must be 2");
            }
            int[] shape = spaceFloat.cloneShape();
            int[] shapePsf = ((FloatVectorSpaceWithRank)imagePsf.getSpace()).cloneShape();
            float[] psfPad = psfPadding1D(spaceFloat.create().getData(),shape[1],
                    shape[0],vectorPsf.getData(),shapePsf[1],shapePsf[0],isComplex);
            return spaceFloat.wrap(psfPad);
        } else {
            DoubleVectorSpaceWithRank spaceDoubleOut = (DoubleVectorSpaceWithRank)outputSpace;
            DoubleVectorSpaceWithRank spaceDoubleIn = (DoubleVectorSpaceWithRank)inputSpace;
            DoubleVector vectorPsf = (DoubleVector)imagePsf;
            if (!(spaceDoubleOut.getRank() == 2)) {
                throw new IllegalArgumentException("The rank of vector must be 2");
            }
            int[] shape = spaceDoubleIn.cloneShape();
            int[] shapePsf = ((DoubleVectorSpaceWithRank)imagePsf.getSpace()).cloneShape();
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
     * @param image
     * @param imagePsf
     * @param isComplex
     * @return
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
     * @param image
     * @param imagePsf
     * @param isComplex
     * @return
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
     * @param image
     * @param imagePsf
     * @param isComplex
     * @return
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
     * @param  In 2d array
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
     * @param W Width of the 2d array In
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
     * Convert an image into an 2d array.
     * 
     */
    public static double[][] imToArray(String imageName)
    {
        return buffI2array(openAsBufferedImage(imageName));
    }

    public static BufferedImage array2BuffI(double[][] I) //TODO delete and use arrayToImage(array, SCALE_CORRECTED_COLORMAP, false)
    {

        int W = I.length;
        int H = I[0].length;
        ColorMap map = ColorMap.getJet(256);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        for(int j = 0; j < H; j++)
        {
            for(int i = 0; i < W; i++)
            {
                Color b = map.table[ (int)I[i][j] ];
                bufferedI.setRGB(i, j, b.getRGB());
            }
        }
        return bufferedI;
    }

    public static double[][] buffI2array(BufferedImage I) //TODO delete and use imageToArray(I , false)
    {
        int H = I.getHeight();
        int W = I.getWidth();
        double ImArray[][] = new double[H][W];
        WritableRaster raster = I.getRaster();
        for (int j = 0; j < H; j++)
        {
            for (int i = 0; i < W; i++)
            {
                int[] pixels = raster.getPixel(i, j, (int[]) null);
                ImArray[i][j] = pixels[0];
            }
        }
        return ImArray;
    }

    public static void saveArrayToImage(double[] A, int W, String name)
    {
        int H = A.length/W;
        BufferedImage I = arrayToImage1D(A, W, H, false);
        saveBufferedImage(I, name);
    }

    public static void saveArrayToImage(double[][] A,  String name)
    {
        saveBufferedImage(arrayToImage(A, false), name);
    }

    /**
     * Expand an image
     *
     * Pad an image to another size of DIM1 and DIM2
     * The justification is set by keyword JUST:
     *  JUST =  0 -> lower-left (the default)
     *          1 -> center
     *         -1 -> at corners to preserve FFT indexing
     */
    public static double[][] imgPad(double img[][], int dim1, int dim2, int just)
    {
        double New[][] = new double[dim2][dim1];
        return imgPad(img, New, just);
    }

    /**
     * Expand an image
     *
     * Pad an image to another size of DIM1 and DIM2
     * The justification is set by keyword JUST:
     *  JUST =  0 -> lower-left (the default)
     *          1 -> center
     *         -1 -> at corners to preserve FFT indexing
     */
    public static double[][] imgPad(double oldImg[][], double newImg[][] , int just)
    {   
        int oldH = oldImg.length; // hauteur
        int oldW = oldImg[0].length; // largeur
        int newH = newImg.length;
        int newW = newImg[0].length;
        double New[][] = new double[newH][newW];
        switch (just)
        {
        case LOWER_LEFT:
            /* image will not be centered */
            for(int i = 0; i < oldH; i++)
            {
                for(int j = 0; j < oldW; j++)
                {
                    New[i][j] = oldImg[i][j];
                }
            }
            break;
        case CENTERED:
            /* image will be centered */
            int i1 = (newW - oldW)/2;
            int i2 = (newH - oldH)/2;
            for(int i = 0; i < oldH; i++)
            {
                for(int j = 0; j < oldW; j++)
                {
                    New[i2 + i][j + i1] = oldImg[i][j];
                }
            }
            break;
        case FFT_INDEXING:
            /* preserve FFT indexing */
            int oldW2 = oldW/2;
            int oldH2 = oldH/2;
            if (oldW2 != 0 || oldH2 != 0) // haut gauche->bas droit
            {
                for(int i = 0; i < oldH2; i++)
                {
                    for(int j = 0; j < oldW2; j++)
                    {
                        New[newH - oldH2 + i][newW - oldW2 + j] = oldImg[i][j];
                    }
                }
            }
            if(oldW2 != 0) // Haut droit->bas gauche
            {
                for(int i = 0; i < oldH2; i++)
                {
                    for(int j = 0; j < oldW-oldW2; j++)
                    {
                        New[newH - oldH2 + i][j] = oldImg[i][j + oldW2];
                    }
                }
            }
            if(oldH2 != 0) // bas gauche->haut droit
            {
                for(int i = 0; i < oldH - oldH2; i++)
                {
                    for(int j = 0; j < oldW2; j++)
                    {
                        New[i][newW - oldW2 + j] = oldImg[i + oldH2][j];
                    }
                }
            }

            for(int i = 0; i < oldH-oldH2; i++) // Bas droit->Haut gaucHe
            {
                for(int j = 0; j < oldW-oldW2; j++)
                {
                    New[i][j] = oldImg[i + oldH2][j + oldW2];
                }
            }
            break;
        default:
            throw new IllegalArgumentException("bad value for justification");

        }
        return New;
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