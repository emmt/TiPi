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

public class CommonUtils {

    public static final int LOWER_LEFT = 0;
    public static final int CENTERED = 1;
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
     * Will convert a value to another
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
     * Will scan the tab and return the highest and smallest value (float version)
     * @param array Array to parse
     * @return an array of 2 value: {min,max}
     */
    public static float[] computeMinMax1Das2D(float[] array, int width,int height, boolean isComplex){
        float min = array[0],max = array[0];
        int current = 0;
        for(int j = 0; j<width; j++){
            for(int i = 0; i<height; i++){
                if (isComplex) {
                    current = 2*(i+j*height);
                } else {
                    current = i+j*height;
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

    public static double[] computeMinMax1Das2D(double[] array, int width, int height,boolean isComplex){
        double min = array[0],max = array[0];
        int current = 0;
        for(int i = 0; i<width; i++){
            for(int j = 0; j<height; j++){
                if (isComplex) {
                    current = 2*(i+j*height);
                } else {
                    current = i+j*height;
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

    public static double[][] imageToArray(BufferedImage image, boolean isComplex) {
        int height = image.getHeight();
        int width = image.getWidth();
        WritableRaster raster = image.getRaster();
        double [][]out;
        if (isComplex) {
            out = new double[height][2*width];
        } else {
            out = new double[height][width];
        }
        for(int j=0;j<width;j++){
            for(int i=0;i<height;i++){
                int[] tmp = raster.getPixel(j, i, (int[])null);
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
            out = new float[height][2*width];
        } else {
            out = new float[height][width];
        }
        for(int j=0;j<width;j++){
            for(int i=0;i<height;i++){
                int[] tmp = raster.getPixel(j, i, (int[])null);
                if (tmp.length == 1 || tmp.length == 2) {
                    out[i][j] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                } else {
                    out[i][j] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                }
            }
        }
        return out;
    }

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
        for(int j=0;j<width;j++){
            for(int i=0;i<height;i++){
                int[] tmp = raster.getPixel(j, i, (int[])null);
                if (tmp.length == 1 || tmp.length == 2) {
                    out[(i+j*height)] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                } else {
                    out[(i+j*height)] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                }
            }
        }
        return out;
    }

    /**
     * Convert image to float 1D array
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
        if (isComplex) {
            for(int j=0;j<width;j++){
                for(int i=0;i<height;i++){
                    int[] tmp = raster.getPixel(j, i, (int[])null);
                    if (tmp.length == 1 || tmp.length == 2) {
                        out[2*(i+j*height)] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                    } else {
                        out[2*(i+j*height)] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                    }
                }
            }
        } else {
            for(int j=0;j<width;j++){
                for(int i=0;i<height;i++){
                    int[] tmp = raster.getPixel(j, i, (int[])null);
                    if (tmp.length == 1 || tmp.length == 2) {
                        out[(i+j*height)] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                    } else {
                        out[(i+j*height)] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                    }
                }
            }
        }
        return out;
    }

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

    public static BufferedImage vectorToImage(VectorSpace outputSpace, Vector vector, int job, boolean singlePrecision ,boolean isComplex){
        if (singlePrecision) {
            FloatVectorSpaceWithRank space = (FloatVectorSpaceWithRank)outputSpace;
            int[] shape = space.getShape();
            if (!(space.getRank() == 2)) {
                throw new IllegalArgumentException("The vector should be of rank 2 to create an image");
            }
            return arrayToImage1D(((DoubleVector)vector).getData(), job, shape[1], shape[0], isComplex);
        } else {
            DoubleVectorSpaceWithRank space = (DoubleVectorSpaceWithRank)outputSpace;
            int[] shape = space.getShape();
            if (!(space.getRank() == 2)) {
                throw new IllegalArgumentException("The vector should be of rank 2 to create an image");
            }
            return arrayToImage1D(((DoubleVector)vector).getData(), job, shape[1], shape[0], isComplex);
        }
    }

    public static int[][] psfToArrayInt(BufferedImage psf) {
        WritableRaster raster = psf.getRaster();
        int [][]out = new int[psf.getHeight()][psf.getWidth()];
        for(int j=0;j<psf.getWidth();j++){
            for(int i=0;i<psf.getHeight();i++){
                int[] tmp = raster.getPixel(j, i, (int[])null);
                if (tmp.length == 1 || tmp.length == 2) {
                    out[i][j] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                } else {
                    out[i][j] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                }
            }
        }
        return out;
    }

    public static double[][] psfToArrayDouble(BufferedImage psf) {
        WritableRaster raster = psf.getRaster();
        double [][]out = new double[psf.getHeight()][psf.getWidth()];
        for(int j=0;j<psf.getWidth();j++){
            for(int i=0;i<psf.getHeight();i++){
                int[] tmp = raster.getPixel(j, i, (int[])null);
                if (tmp.length == 1 || tmp.length == 2) {
                    out[i][j] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                } else {
                    out[i][j] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                }
            }
        }
        return out;
    }

    public static double[] psfToArray1DDouble(BufferedImage psf) {
        WritableRaster raster = psf.getRaster();
        double []out = new double[psf.getWidth()*psf.getHeight()];
        for(int j=0;j<psf.getWidth();j++){
            for(int i=0;i<psf.getHeight();i++){
                int[] tmp = raster.getPixel(j, i, (int[])null);
                if (tmp.length == 1 || tmp.length == 2) {
                    out[i+j*psf.getHeight()] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                } else {
                    out[i+j*psf.getHeight()] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                }
            }
        }
        return out;
    }

    public static int[] psfToArray1DInt(BufferedImage psf) {
        WritableRaster raster = psf.getRaster();
        int []out = new int[psf.getWidth()*psf.getHeight()];
        for(int j=0;j<psf.getWidth();j++){
            for(int i=0;i<psf.getHeight();i++){
                int[] tmp = raster.getPixel(j, i, (int[])null);
                if (tmp.length == 1 || tmp.length == 2) {
                    out[i+j*psf.getHeight()] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                } else {
                    out[i+j*psf.getHeight()] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                }
            }
        }
        return out;
    }

    public static float[] psfToArray1DFloat(BufferedImage psf) {
        WritableRaster raster = psf.getRaster();
        float []out = new float[psf.getWidth()*psf.getHeight()];
        for(int j=0;j<psf.getWidth();j++){
            for(int i=0;i<psf.getHeight();i++){
                int[] tmp = raster.getPixel(j, i, (int[])null);
                if (tmp.length == 1 || tmp.length == 2) {
                    out[i+j*psf.getHeight()] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                } else {
                    out[i+j*psf.getHeight()] = colorToGrey(tmp[0], tmp[1], tmp[2]);
                }
            }
        }
        return out;
    }

    /********************************** ARRAY TO IMAGE **********************************/
    /*
     * Memo: even if y = y*2, we store the image in a array x*y !!
     *
     * */

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

    public static BufferedImage createNewBufferedImage(int width, int height){
        return new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    }

    public static BufferedImage createNewBufferedImage(double[][] tab, boolean isComplex){
        int width, height;
        if (isComplex) {
            width = tab[0].length/2;
            height = tab.length;
        }else{
            width = tab[0].length;
            height = tab.length;
        }
        return createNewBufferedImage(width, height);
    }

    public static BufferedImage createNewBufferedImage(float[][] tab, boolean isComplex){
        int width, height;
        if (isComplex) {
            width = tab[0].length/2;
            height = tab.length;
        }else{
            width = tab[0].length;
            height = tab.length;
        }
        return createNewBufferedImage(width, height);
    }

    /**
     * Scale an array by doing in place operations
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
     * Scale an 1Darray by doing in place operations
     * @param array
     */
    public static void scaleArray1Das2D(double[] array, int width, int height, boolean isComplex){
        //get max min for scaling
        double[] alphta = computeAlphaBeta1D(array,isComplex);
        int grey;
        for(int j = 0; j<width; j++){
            for(int i = 0; i<height; i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*height)],alphta[0], alphta[1]);
                    array[2*(i+j*height)]= grey;
                } else {
                    grey = greyToColor(array[(i+j*height)],alphta[0], alphta[1]);
                    array[(i+j*height)]= grey;
                }
            }
        }
    }

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
     * Scale a float 1Darray by doing in place operations
     * @param array
     */
    public static void scaleArray1Das2D(float[] array, int width, int height, boolean isComplex){
        //get max min for scaling
        float[] alphta = computeAlphaBeta1D(array,isComplex);
        int grey;
        for(int j = 0; j<width; j++){
            for(int i = 0; i<height; i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*height)],alphta[0], alphta[1]);
                    array[2*(i+j*height)]= grey;
                } else {
                    grey = greyToColor(array[(i+j*height)],alphta[0], alphta[1]);
                    array[(i+j*height)]= grey;
                }
            }
        }
    }

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
     * Scale an array by doing in place operations
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
     * Scale a float array by doing in place operations
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
     * Scale an 1D array by doing in place operations
     * @param array
     */
    public static void correctArray1Das2D(double[] array, int width, int height, boolean isComplex){
        //We get the repartitions of the pixels
        int[] tmpOut = new int[256];
        int grey;
        for(int j = 0; j<width; j++){
            for(int i = 0;i<height; i++){
                if (isComplex) {
                    grey = (int) array[2*(i+j*height)];
                } else {
                    grey = (int) array[(i+j*height)];
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
        for(int j = 0; j<width; j++){
            for(int i = 0; i<height; i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*height)],alpha,beta);
                    array[2*(i+j*height)] = grey;
                } else {
                    grey = greyToColor(array[(i+j*height)],alpha,beta);
                    array[(i+j*height)] = grey;
                }
            }
        }
    }

    public static void correctArray1Das2D(float[] array, int width, int height, boolean isComplex){
        //We get the repartitions of the pixels
        int[] tmpOut = new int[256];
        int grey;
        for(int j = 0; j<width; j++){
            for(int i = 0;i<height; i++){
                if (isComplex) {
                    grey = (int) array[2*(i+j*height)];
                } else {
                    grey = (int) array[(i+j*height)];
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
        for(int j = 0; j<width; j++){
            for(int i = 0; i<height; i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*height)],alpha,beta);
                    array[2*(i+j*height)] = grey;
                } else {
                    grey = greyToColor(array[(i+j*height)],alpha,beta);
                    array[(i+j*height)] = grey;
                }
            }
        }
    }

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
     * Scale a float 1D array by doing in place operations
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
     * @param array
     * @return
     */
    public static BufferedImage colorArray(double[][] array, boolean isComplex){
        BufferedImage imageout = createNewBufferedImage(array, isComplex);
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        int sizeArray = isComplex ? array[0].length/2 : array[0].length;
        for(int j = 0; j<sizeArray; j++){
            for(int i = 0; i<imageout.getHeight(); i++){
                if (isComplex) {
                    grey = (int)array[i][2*j];
                }else{
                    grey = (int)array[i][j];
                }     
                raster.setPixel(j, i, new int[]{map.r[grey],map.g[grey],map.b[grey]});
            }
        }
        return imageout;
    }

    /**
     * Create a buffered image and use a colormap with the grey value in input
     * @param array
     * @return
     */
    public static BufferedImage colorArray(float[][] array, boolean isComplex){
        BufferedImage imageout = createNewBufferedImage(array, isComplex);
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        int sizeArray = isComplex ? array[0].length/2 : array[0].length;
        for(int j = 0; j<sizeArray; j++){
            for(int i = 0; i<imageout.getHeight(); i++){
                if (isComplex) {
                    grey = (int)array[i][2*j];
                }else {
                    grey = (int)array[i][j];
                }
                raster.setPixel(j, i, new int[]{map.r[grey],map.g[grey],map.b[grey]});
            }
        }
        return imageout;
    }

    /**
     * Create a buffered image and use a colormap with the grey value in input
     * @param array
     * @return
     */
    public static BufferedImage colorArray1D(double[] array, int width, int height, boolean isComplex){
        BufferedImage imageout = createNewBufferedImage(width, height);
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        for(int j = 0; j<imageout.getWidth(); j++){
            for(int i = 0; i<imageout.getHeight(); i++){
                if (isComplex) {
                    grey = (int)array[2*(i+j*imageout.getHeight())];
                } else {
                    grey = (int)array[(i+j*imageout.getHeight())];
                }
                raster.setPixel(j, i, new int[]{map.r[grey],map.g[grey],map.b[grey]});
            }
        }
        return imageout;
    }

    /**
     * Create a buffered image and use a colormap with the grey value in input
     * @param array
     * @return
     */
    public static BufferedImage colorArray1D(float[] array,  int width, int height, boolean isComplex){
        BufferedImage imageout = createNewBufferedImage(width, height);
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        for(int j = 0; j<imageout.getWidth(); j++){
            for(int i = 0; i<imageout.getHeight(); i++){
                if (isComplex) {
                    grey = (int)array[2*(i+j*imageout.getHeight())];
                } else {
                    grey = (int)array[(i+j*imageout.getHeight())];
                }
                raster.setPixel(j, i, new int[]{map.r[grey],map.g[grey],map.b[grey]});
            }
        }
        return imageout;
    }

    /**
     * Create a buffered image, simply copy array to buffered image
     * @param array
     * @return
     */
    public static BufferedImage arrayToImage(double[][] array, boolean isComplex){
        BufferedImage imageout;
        if (isComplex) {
            imageout = createNewBufferedImage(array[0].length/2, array.length);
        }else {
            imageout = createNewBufferedImage(array[0].length, array.length);
        }
        WritableRaster raster = imageout.getRaster();
        int grey;
        for(int j = 0; j<imageout.getWidth(); j++){
            for(int i = 0; i<imageout.getHeight(); i++){
                if (isComplex) {
                    grey = (int)array[i][2*j];
                }else {
                    grey = (int)array[i][j];
                }
                raster.setPixel(j, i, new int[]{grey,grey,grey});
            }
        }
        return imageout;
    }

    /**
     * Create a buffered image, simply copy array to buffered image
     * @param array
     * @return
     */
    public static BufferedImage arrayToImage(float[][] array, boolean isComplex){
        BufferedImage imageout;
        if (isComplex) {
            imageout = createNewBufferedImage(array[0].length/2, array.length);
        }else {
            imageout = createNewBufferedImage(array[0].length, array.length);
        }
        WritableRaster raster = imageout.getRaster();
        int grey;
        for(int j = 0; j<imageout.getWidth(); j++){
            for(int i = 0; i<imageout.getHeight(); i++){
                if (isComplex) {
                    grey = (int)array[i][2*j];
                }else {
                    grey = (int)array[i][j];
                }
                raster.setPixel(j, i, new int[]{grey,grey,grey});
            }
        }
        return imageout;
    }

    /**
     * Create a buffered image, simply copy array to buffered image
     * @param array
     * @return
     */
    public static BufferedImage arrayToImage1D(double[] array, int width, int height, boolean isComplex){
        BufferedImage imageout = createNewBufferedImage(width,height);
        WritableRaster raster = imageout.getRaster();
        int grey;
        for(int j = 0; j<imageout.getWidth(); j++){
            for(int i = 0; i<imageout.getHeight(); i++){
                if (isComplex) {
                    grey = (int)array[2*(i+j*imageout.getHeight())];
                } else {
                    grey = (int)array[(i+j*imageout.getHeight())];
                }
                raster.setPixel(j, i, new int[]{grey,grey,grey});
            }
        }
        return imageout;
    }

    /**
     * Create a buffered image, simply copy array to buffered image
     * @param array
     * @return
     */
    public static BufferedImage arrayToImage1D(float[] array, int width, int height, boolean isComplex){
        BufferedImage imageout = createNewBufferedImage(width, height);
        WritableRaster raster = imageout.getRaster();
        int grey;
        for(int j = 0; j<imageout.getWidth(); j++){
            for(int i = 0; i<imageout.getHeight(); i++){
                if (isComplex) {
                    grey = (int)array[2*(i+j*imageout.getHeight())];
                } else {
                    grey = (int)array[(i+j*imageout.getHeight())];
                }
                raster.setPixel(j, i, new int[]{grey,grey,grey});
            }
        }
        return imageout;
    }

    /**
     * Front function that will apply different job on the given array of size
     * height,witdh*2
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
     * Front function that will apply different job on the given array of size
     * height,witdh*2
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
     * Front function that will apply different job on the given array of size
     * height,witdh*2
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
     * Front function that will apply different job on the given array of size
     * height,witdh*2
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
        System.out.println("W: "+prev);
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
        System.out.println("H: "+(max-min));
        return (max-min) > prev ? (max-min) : prev;
    }

    public static BufferedImage imagePad(BufferedImage image, int sizePSF) {
        BufferedImage pad = new BufferedImage(image.getWidth()+sizePSF, image.getHeight()+sizePSF, image.getType());
        Raster rasterImage = image.getData();
        WritableRaster rasterPad = pad.getRaster();
        int hlf = sizePSF/2;
        for (int j = 0; j < image.getWidth(); j++) {
            for (int i = 0; i < image.getHeight(); i++) {
                rasterPad.setPixel(j+hlf,i+hlf , rasterImage.getPixel(j,i, (int[])null));
            }
        }
        return pad;
    }

    public static BufferedImage imageUnPad(BufferedImage image, int sizePSF) {
        int hlf = sizePSF/2;
        return image.getSubimage(hlf, hlf, image.getWidth()-sizePSF, image.getHeight()-sizePSF);
    }

    /********************************** PSF PADDING **********************************/
    /*
     * Memo: even if y = y*2, we store the psf in a array x*y !!
     *
     * */

    public static double[][] psfPadding(BufferedImage image, BufferedImage imagePsf, boolean isComplex) {
        double [][]tableau_psf;
        int width = image.getWidth();
        int height = image.getHeight();
        if (isComplex) {
            tableau_psf = new double[height][2*width];
        } else {
            tableau_psf = new double[height][width];
        }
        double[][]test = psfToArrayDouble(imagePsf);
        return psfPadding(tableau_psf, test, isComplex);
    }

    public static double[][] psfPadding(double[][] imageOut, double[][] imagePsf, boolean isComplex) {
        int width, height = imageOut.length;
        if (isComplex) {
            width = imageOut[0].length/2;
        } else {
            width = imageOut[0].length;
        }
        int psfW = imagePsf[0].length, psfH = imagePsf.length;
        int demiPsfW = imagePsf[0].length/2, demiPsfH = imagePsf.length/2;
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

        //bloc haut �� gauche: A
        for(int j = 0; j<demiPsfW; j++){
            for(int i=0;i<demiPsfH;i++){
                imageOut[(height-demiPsfH)+i][width-demiPsfW+j] = imagePsf[i][j];
            }
        }
        //bloc haut a droite: B
        for(int j = demiPsfW; j<psfW; j++){
            for(int i=0;i<demiPsfH;i++){
                imageOut[(height-demiPsfH)+i][j-demiPsfW] = imagePsf[i][j];
            }
        }
        //bloc bas a gauche: C
        for(int j = 0; j<demiPsfW; j++){
            for(int i=demiPsfH; i<psfH; i++){
                imageOut[i-demiPsfH][width-demiPsfW+j] = imagePsf[i][j];
            }
        }
        //bloc bas a droite: D
        for(int j = demiPsfW; j<psfW; j++){
            for(int i=demiPsfH; i<psfH; i++){
                imageOut[i-demiPsfH][j-demiPsfW] = imagePsf[i][j];
            }
        }
        return imageOut;
    }

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
        double[]test = psfToArray1DDouble(imagePsf);

        return psfPadding1D(tableau_psf,width,height,test,psfH,psfW,isComplex);
    }
    
    //is complex is not important as we give the output space
    public static Vector psfPadding1D(VectorSpace inputSpace, VectorSpace outputSpace, Vector imagePsf, boolean singlePrecision, boolean isComplex) {
        if (singlePrecision) {
            FloatVectorSpaceWithRank spaceFloat = (FloatVectorSpaceWithRank)outputSpace;
            FloatVector vectorPsf = (FloatVector)imagePsf;
            if (!(spaceFloat.getRank() == 2)) {
                throw new IllegalArgumentException("The rank of vector must be 2");
            }
            int[] shape = spaceFloat.getShape();
            int[] shapePsf = ((FloatVectorSpaceWithRank)imagePsf.getSpace()).getShape();
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
            int[] shape = spaceDoubleIn.getShape();
            int[] shapePsf = ((DoubleVectorSpaceWithRank)imagePsf.getSpace()).getShape();
            double[] psfPad = psfPadding1D(spaceDoubleOut.create().getData(),shape[1],
                    shape[0], vectorPsf.getData(),shapePsf[1],shapePsf[0],isComplex);
            return spaceDoubleOut.wrap(psfPad);
        }
    }

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

        if (isComplex) {
            //Here we are writing at 2*(i+j*hght)
            //Bloc haut �� gauche: D
            for(int j = 0; j < demiPsfW; j++){
                for(int i = 0; i < demiPsfH; i++){
                    imageout[i+2*j*imageHeight] = imagePsf[(demiPsfH+i)+(demiPsfW+j)*psfHeight];
                }
            }
            //bloc haut a droite: C
            for(int j = imageWidth-demiPsfW; j < imageWidth; j++){
                for(int i = 0; i < demiPsfH; i++){
                    imageout[i+2*j*imageHeight] = imagePsf[(demiPsfH+i)+(demiPsfW-imageWidth+j)*psfHeight];
                }
            }
            //bloc bas a gauche: B
            for(int j = 0; j < demiPsfW; j++){
                for(int i = imageHeight-demiPsfH; i < imageHeight; i++){
                    imageout[i+2*j*imageHeight] = imagePsf[(demiPsfH-imageHeight+i)+(demiPsfW+j)*psfHeight];
                }
            }
            //bloc bas a droite: A
            for(int j = imageWidth-demiPsfW; j < imageWidth; j++){
                for(int i = imageHeight-demiPsfH; i < imageHeight; i++){
                    imageout[i+2*j*imageHeight] = imagePsf[(demiPsfH-imageHeight+i)+(demiPsfW-imageWidth+j)*psfHeight];
                }
            }
        }else{
            //Here we are writing at (i+j*hght)
            //Bloc haut �� gauche: D
            for(int j = 0; j < demiPsfW; j++){
                for(int i = 0; i < demiPsfH; i++){
                    imageout[i+j*imageHeight] = imagePsf[(demiPsfH+i)+(demiPsfW+j)*psfHeight];
                }
            }
            //bloc haut a droite: C
            for(int j = imageWidth-demiPsfW; j < imageWidth; j++){
                for(int i = 0; i < demiPsfH; i++){
                    imageout[i+j*imageHeight] = imagePsf[(demiPsfH+i)+(demiPsfW-imageWidth+j)*psfHeight];
                }
            }
            //bloc bas a gauche: B
            for(int j = 0; j < demiPsfW; j++){
                for(int i = imageHeight-demiPsfH; i < imageHeight; i++){
                    imageout[i+j*imageHeight] = imagePsf[(demiPsfH-imageHeight+i)+(demiPsfW+j)*psfHeight];
                }
            }
            //bloc bas a droite: A
            for(int j = imageWidth-demiPsfW; j < imageWidth; j++){
                for(int i = imageHeight-demiPsfH; i < imageHeight; i++){
                    imageout[i+j*imageHeight] = imagePsf[(demiPsfH-imageHeight+i)+(demiPsfW-imageWidth+j)*psfHeight];
                }
            }
        }
        return imageout;
    }

    /**
     * float PSF shift + padding
     *
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
        float[]test = psfToArray1DFloat(imagePsf);

        return psfPadding1D(tableau_psf,width,height,test,psfH,psfW,isComplex);
    }

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
        //Bloc haut �� gauche: D
        for(int j = 0; j < demiPsfW; j++){
            for(int i = 0; i < demiPsfH; i++){
                imageout[i+j*imageHeight] = imagePsf[(demiPsfH+i)+(demiPsfW+j)*psfHeight];
            }
        }
        //bloc haut a droite: C
        for(int j = imageWidth-demiPsfW; j < imageWidth; j++){
            for(int i = 0; i < demiPsfH; i++){
                imageout[i+j*imageHeight] = imagePsf[(demiPsfH+i)+(demiPsfW-imageWidth+j)*psfHeight];
            }
        }
        //bloc bas a gauche: B
        for(int j = 0; j < demiPsfW; j++){
            for(int i = imageHeight-demiPsfH; i < imageHeight; i++){
                imageout[i+j*imageHeight] = imagePsf[(demiPsfH-imageHeight+i)+(demiPsfW+j)*psfHeight];
            }
        }
        //bloc bas a droite: A
        for(int j = imageWidth-demiPsfW; j < imageWidth; j++){
            for(int i = imageHeight-demiPsfH; i < imageHeight; i++){
                imageout[i+j*imageHeight] = imagePsf[(demiPsfH-imageHeight+i)+(demiPsfW-imageWidth+j)*psfHeight];
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
        int H = In.length;
        int W = In[0].length;
        double[] Out = new double[H*W];
        for (int j = 0; j < W; j++)
            for (int i = 0; i < H; i++)
                Out[j*H + i] = In[i][j];
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
        for (int j = 0; j < W; j++)
            for (int i = 0; i < H; i++)
                Out[i][j] = In[j*H + i];
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

    public static BufferedImage array2BuffI(double[][] I) //TODO delete and use arrayToImage(array, false)
    {

        int H = I.length;
        int W = I[0].length;
        ColorMap map = ColorMap.getJet(256);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        for(int j = 0; j < W; j++)
        {
            for(int i = 0; i < H; i++)
            {
                Color b = map.table[ (int)I[i][j] ];
                bufferedI.setRGB(j, i, b.getRGB()); // j, i invers��
            }
        }
        return bufferedI;
    }

    public static double[][] buffI2array(BufferedImage I) //TODO delete and use imageToArray(I, false)
    {
        int H = I.getHeight();
        int W = I.getWidth();
        double ImArray[][] = new double[H][W];
        WritableRaster raster = I.getRaster();
        for (int j = 0; j < W; j++)
        {
            for (int i = 0; i < H; i++)
            {
                int[] pixels = raster.getPixel(j, i, (int[]) null);
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