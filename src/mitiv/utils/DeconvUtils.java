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

import icy.image.IcyBufferedImage;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;

/**
 * @author Leger Jonathan
 *
 */
@SuppressWarnings("unused")
public class DeconvUtils implements DeconvUtilsInterface {

    private BufferedImage image;
    private BufferedImage image_psf;
    private DoubleFFT_2D fft;
    private DoubleFFT_1D fft1D;
    private FloatFFT_2D fftFloat;
    private FloatFFT_1D fft1DFloat;

    /**
     * Job to compute with the wiener filter
     */
    public static final int JOB_WIENER = 1;
    /**
     * Job to compute using quadratic and circulant approximation
     */
    public static final int JOB_QUAD = 2;
    /**
     * Job to compute with Conjugate gradients
     */
    public static final int JOB_CG = 3;
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
     * The width of the image given to ReadImage
     */
    public int width;
    /**
     * The height of the image given to ReadImage
     */
    public int height;

    /********************************** SOME PRIVATE FUNCTIONS **********************************/
    /**
     * Will convert a value to another
     * 
     * @param g
     * @param alpha
     * @param beta
     * @return
     */
    private final int greyToColor(double g, double alpha, double beta)
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
    private final int colorToGrey(double r, double g, double b)
    {
        return (int)(0.2126*r+0.7152*g+0.0722*b);
    }

    /**
     * Convert a RGB value to an int grey value (float version)
     * 
     * @param r
     * @param g
     * @param b
     * @return
     */
    private final int colorToGrey(float r, float g, float b)
    {
        return (int)(0.2126*r+0.7152*g+0.0722*b);
    }

    /**
     * Kind of setter that will be called after we have open the image, whatever 
     * the input image cf ReadImage
     */
    private void setValue(){
        width = image.getWidth();
        height = image.getHeight();
        if (image_psf.getWidth() > image.getWidth() || image_psf.getHeight() > image.getHeight()) {
            throw new IllegalArgumentException("You may have mistook the image and the PSF");
        }
    }

    /**
     * Will scan the tab and return the highest and smallest value
     * @param tab Array to parse
     * @return an array of 2 value: {min,max}
     */
    private double[] computeMinMax(double[][] tab){
        //trouver min max du tableau
        double min = tab[0][0],max = tab[0][0]; 
        for(int i = 0; i<tab.length; i++){
            for(int j = 0; j<tab[0].length; j+=2){
                if(tab[i][j] < min ){
                    min = tab[i][j];
                }
                if(tab[i][j] > max ){
                    max = tab[i][j];
                }
            }
        }
        return new double[]{min,max};
    }

    /**
     * Will scan the tab and return the highest and smallest value (float version)
     * @param tab Array to parse
     * @return an array of 2 value: {min,max}
     */
    private float[] computeMinMax(float[][] tab){
        //trouver min max du tableau
        float min = tab[0][0],max = tab[0][0]; 
        for(int i = 0; i<tab.length; i++){
            for(int j = 0; j<tab[0].length; j+=2){
                if(tab[i][j] < min ){
                    min = tab[i][j];
                }
                if(tab[i][j] > max ){
                    max = tab[i][j];
                }
            }
        }
        return new float[]{min,max};
    }

    /**
     * Will scan the tab and return the highest and smallest value
     * @param tab Array to parse
     * @return an array of 2 value: {min,max}
     */
    private double[] computeMinMax1D(double[] tab, boolean isComplex){
        double min = tab[0],max = tab[0]; 
        int current = 0;
        for(int i = 0; i<height; i++){
            for(int j = 0; j<width; j++){
                if (isComplex) {
                    current = 2*(j+i*width);
                } else {
                    current = j+i*width;
                }
                if(tab[current] < min ){
                    min = tab[current];
                }
                if(tab[current] > max ){
                    max = tab[current];
                }
            }
        } 
        return new double[]{min,max};
    }

    /**
     * Will scan the tab and return the highest and smallest value (float version)
     * @param tab Array to parse
     * @return an array of 2 value: {min,max}
     */
    private float[] computeMinMax1D(float[] tab, boolean isComplex){
        float min = tab[0],max = tab[0]; 
        int current = 0;
        for(int i = 0; i<height; i++){
            for(int j = 0; j<width; j++){
                if (isComplex) {
                    current = 2*(j+i*width);
                } else {
                    current = j+i*width;
                }
                if(tab[current] < min ){
                    min = tab[current];
                }
                if(tab[current] > max ){
                    max = tab[current];
                }
            }
        } 
        return new float[]{min,max};
    }

    /**
     * Will scan the tab and return the highest and smallest value
     * @param tab Array to parse
     * @return an array of 2 value: {min,max}
     */
    private double[] computeAlphaBeta(double[][] tab){
        double [] out = computeMinMax(tab);
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
     * @param tab Array to parse
     * @return an array of 2 value: {min,max}
     */
    private float[] computeAlphaBeta(float[][] tab){
        float [] out = computeMinMax(tab);
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

    private double[] computeAlphaBeta1D(double[] tab, boolean isComplex){
        double [] out = computeMinMax1D(tab, isComplex);
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

    private float[] computeAlphaBeta1D(float[] tab, boolean isComplex){
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

    /********************************** READ INPUT IMAGE **********************************/
    /**
     * Open the image and store them
     */
    @Override
    public void ReadImage(String pathImage, String pathPSF) {
        try {
            this.image = ImageIO.read(new File(pathImage));
            this.image_psf = ImageIO.read(new File(pathPSF));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Wrong path given");
        }
        setValue();
    }

    /**
     * Open the image and store them
     */
    @Override
    public void ReadImage(BufferedImage image, BufferedImage PSF) {
        this.image = image;
        this.image_psf = PSF;
        setValue();
    }

    /**
     * Open the image and store them
     */
    @Override
    public void ReadImage(IcyBufferedImage image, IcyBufferedImage PSF) {
        this.image = IcyBufferedImage.createFrom(image);
        this.image_psf = IcyBufferedImage.createFrom(PSF);
        setValue();
    }

    /********************************** X TO ARRAY **********************************/

    @Override
    public double[][] ImageToArray(boolean isComplex) {
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
        //printTab(out);
        return out;
    }

    /**
     * Convert image to float array
     * 
     * @param isComplex
     * @return
     */
    public float[][] ImageToArrayFloat(boolean isComplex) {
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

    @Override
    public double[] ImageToArray1D(boolean isComplex) {
        WritableRaster raster = image.getRaster();
        double []out;
        if (isComplex) {
            out = new double[width*2*height];
        } else {
            out = new double[width*height];
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

    /**
     * Convert image to float 1D array
     * @param isComplex
     * @return
     */
    public float[] ImageToArray1DFloat(boolean isComplex) {
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

    private int[][] PSFToArray() {
        WritableRaster raster = image_psf.getRaster();
        int [][]out = new int[image_psf.getHeight()][image_psf.getWidth()];
        for(int j=0;j<image_psf.getWidth();j++){
            for(int i=0;i<image_psf.getHeight();i++){
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


    private int[] PSFToArray1D() {
        WritableRaster raster = image_psf.getRaster();
        int []out = new int[image_psf.getWidth()*image_psf.getHeight()];
        for(int j=0;j<image_psf.getWidth();j++){
            for(int i=0;i<image_psf.getHeight();i++){
                int[] tmp = raster.getPixel(j, i, (int[])null);
                if (tmp.length == 1 || tmp.length == 2) {
                    out[i+j*image_psf.getHeight()] = colorToGrey(tmp[0], tmp[0], tmp[0]);
                } else {
                    out[i+j*image_psf.getHeight()] = colorToGrey(tmp[0], tmp[1], tmp[2]);
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


    private BufferedImage CreateBufferedImage(){
        BufferedImage imageout;
        //In certain cases we need a specific type
        if(image.getType() == 0){
            imageout = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        }else{
            imageout = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        }
        return imageout;
    }

    /**
     * Scale an array by doing in place operations
     * @param array
     */
    private void scaleArray(double[][] array){
        //get max min for scaling
        double[] alphta = computeAlphaBeta(array);
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                grey = greyToColor(array[i][2*j],alphta[0], alphta[1]);
                array[i][2*j]= grey;
            }
        }
    }

    /**
     * Scale a float array by doing in place operations
     * @param array
     */
    private void scaleArray(float[][] array){
        //get max min for scaling
        float[] alphta = computeAlphaBeta(array);
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                grey = greyToColor(array[i][2*j],alphta[0], alphta[1]);
                array[i][2*j]= grey;
            }
        }
    }

    /**
     * Scale an 1Darray by doing in place operations
     * @param array
     */
    private void scaleArray1D(double[] array, boolean isComplex){
        //get max min for scaling
        double[] alphta = computeAlphaBeta1D(array,isComplex);
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*image.getHeight())],alphta[0], alphta[1]);
                    array[2*(i+j*image.getHeight())]= grey;
                } else {
                    grey = greyToColor(array[(i+j*image.getHeight())],alphta[0], alphta[1]);
                    array[(i+j*image.getHeight())]= grey;
                } 
            }
        }
    }

    /**
     * Scale a float 1Darray by doing in place operations
     * @param array
     */
    private void scaleArray1D(float[] array, boolean isComplex){
        //get max min for scaling
        float[] alphta = computeAlphaBeta1D(array,isComplex);
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*image.getHeight())],alphta[0], alphta[1]);
                    array[2*(i+j*image.getHeight())]= grey;
                } else {
                    grey = greyToColor(array[(i+j*image.getHeight())],alphta[0], alphta[1]);
                    array[(i+j*image.getHeight())]= grey;
                } 
            }
        }
    }

    /**
     * Scale an array by doing in place operations
     * @param array
     */
    private void correctArray(double[][] array){
        //We get the repartitions of the pixels
        int[] tmpOut = new int[256];
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0;i<image.getHeight(); i++){
                grey = (int) array[i][2*j];
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
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                grey = greyToColor(array[i][2*j],alpha,beta);
                array[i][2*j] = grey;
            }
        }
    }

    /**
     * Scale a float array by doing in place operations
     * @param array
     */
    private void correctArray(float[][] array){
        //We get the repartitions of the pixels
        int[] tmpOut = new int[256];
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0;i<image.getHeight(); i++){
                grey = (int) array[i][2*j];
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
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                grey = greyToColor(array[i][2*j],alpha,beta);
                array[i][2*j] = grey;
            }
        }
    }

    /**
     * Scale an 1D array by doing in place operations
     * @param array
     */
    private void correctArray1D(double[] array, boolean isComplex){
        //We get the repartitions of the pixels
        int[] tmpOut = new int[256];
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0;i<image.getHeight(); i++){
                if (isComplex) {
                    grey = (int) array[2*(i+j*image.getHeight())];
                } else {
                    grey = (int) array[(i+j*image.getHeight())];
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
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*image.getHeight())],alpha,beta);
                    array[2*(i+j*image.getHeight())] = grey;
                } else {
                    grey = greyToColor(array[(i+j*image.getHeight())],alpha,beta);
                    array[(i+j*image.getHeight())] = grey;
                }
            }
        }
    }

    /**
     * Scale a float 1D array by doing in place operations
     * @param array
     */
    private void correctArray1D(float[] array, boolean isComplex){
        //We get the repartitions of the pixels
        int[] tmpOut = new int[256];
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0;i<image.getHeight(); i++){
                if (isComplex) {
                    grey = (int) array[2*(i+j*image.getHeight())];
                } else {
                    grey = (int) array[(i+j*image.getHeight())];
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
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*image.getHeight())],alpha,beta);
                    array[2*(i+j*image.getHeight())] = grey;
                } else {
                    grey = greyToColor(array[(i+j*image.getHeight())],alpha,beta);
                    array[(i+j*image.getHeight())] = grey;
                }
            }
        }
    }

    /**
     * Create a buffered image and use a colormap with the grey value in input
     * @param array
     * @return
     */
    private BufferedImage colorArray(double[][] array){
        BufferedImage imageout = CreateBufferedImage();
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                grey = (int)array[i][2*j];
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
    private BufferedImage colorArray(float[][] array){
        BufferedImage imageout = CreateBufferedImage();
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                grey = (int)array[i][2*j];
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
    private BufferedImage colorArray1D(double[] array, boolean isComplex){
        BufferedImage imageout = CreateBufferedImage();
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                if (isComplex) {
                    grey = (int)array[2*(i+j*image.getHeight())];
                } else {
                    grey = (int)array[(i+j*image.getHeight())];
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
    private BufferedImage colorArray1D(float[] array, boolean isComplex){
        BufferedImage imageout = CreateBufferedImage();
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                if (isComplex) {
                    grey = (int)array[2*(i+j*image.getHeight())];
                } else {
                    grey = (int)array[(i+j*image.getHeight())];
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
    private BufferedImage arrayToImage(double[][] array){
        BufferedImage imageout = CreateBufferedImage();
        WritableRaster raster = imageout.getRaster();
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                grey = (int)array[i][2*j];
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
    private BufferedImage arrayToImage(float[][] array){
        BufferedImage imageout = CreateBufferedImage();
        WritableRaster raster = imageout.getRaster();
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                grey = (int)array[i][2*j];
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
    private BufferedImage arrayToImage1D(double[] array, boolean isComplex){
        BufferedImage imageout = CreateBufferedImage();
        WritableRaster raster = imageout.getRaster();
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                if (isComplex) {
                    grey = (int)array[2*(i+j*image.getHeight())];
                } else {
                    grey = (int)array[(i+j*image.getHeight())];
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
    private BufferedImage arrayToImage1D(float[] array, boolean isComplex){
        BufferedImage imageout = CreateBufferedImage();
        WritableRaster raster = imageout.getRaster();
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                if (isComplex) {
                    grey = (int)array[2*(i+j*image.getHeight())];
                } else {
                    grey = (int)array[(i+j*image.getHeight())];
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
    public BufferedImage ArrayToImage(double[][] array, int job){
        if (job > SCALE_CORRECTED_COLORMAP) {
            System.err.println("Wrong job");
            throw new IllegalArgumentException();
        }
        BufferedImage out;
        //First we scale in any case
        scaleArray(array);
        //If necessary we correct
        if (job == SCALE_CORRECTED || job == SCALE_CORRECTED_COLORMAP) {
            correctArray(array);
        }

        //We apply lastly the colormap transformation
        if (job == SCALE_COLORMAP || job == SCALE_CORRECTED_COLORMAP) {
            out = colorArray(array);
        }else{
            out = arrayToImage(array);
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
    public BufferedImage ArrayToImage(float[][] array, int job){
        if (job > SCALE_CORRECTED_COLORMAP) {
            System.err.println("Wrong job");
            throw new IllegalArgumentException();
        }
        BufferedImage out;
        //First we scale in any case
        scaleArray(array);
        //If necessary we correct
        if (job == SCALE_CORRECTED || job == SCALE_CORRECTED_COLORMAP) {
            correctArray(array);
        }

        //We apply lastly the colormap transformation
        if (job == SCALE_COLORMAP || job == SCALE_CORRECTED_COLORMAP) {
            out = colorArray(array);
        }else{
            out = arrayToImage(array);
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
    public BufferedImage ArrayToImage1D(double[] array, int job, boolean isComplex){
        if (job > SCALE_CORRECTED_COLORMAP) {
            System.err.println("Wrong job");
            throw new IllegalArgumentException();
        }
        BufferedImage out;

        //First we scale in any case
        scaleArray1D(array, isComplex);

        //If necessary we correct
        if (job == SCALE_CORRECTED || job == SCALE_CORRECTED_COLORMAP) {
            correctArray1D(array, isComplex);
        }

        //We apply lastly the colormap transformation
        if (job == SCALE_COLORMAP || job == SCALE_CORRECTED_COLORMAP) {
            out = colorArray1D(array, isComplex);
        }else{
            out = arrayToImage1D(array, isComplex);
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
    public BufferedImage ArrayToImage1D(float[] array, int job, boolean isComplex){
        if (job > SCALE_CORRECTED_COLORMAP) {
            System.err.println("Wrong job");
            throw new IllegalArgumentException();
        }
        BufferedImage out;

        //First we scale in any case
        scaleArray1D(array, isComplex);

        //If necessary we correct
        if (job == SCALE_CORRECTED || job == SCALE_CORRECTED_COLORMAP) {
            correctArray1D(array, isComplex);
        }

        //We apply lastly the colormap transformation
        if (job == SCALE_COLORMAP || job == SCALE_CORRECTED_COLORMAP) {
            out = colorArray1D(array, isComplex);
        }else{
            out = arrayToImage1D(array, isComplex);
        }
        return out;
    }


    /**
     * This function will scale the raw data into 0-255 grey value but will also
     * check that there is no single value that may have make an incorrect min/max
     * value, by checking when we have for the first time at least 2-3 pixels 
     * that have this color, and take this pixel as new min/max.
     * 
     * @param array
     * @return
     * @deprecated  replaced by {@link #ArrayToImage(double[][], int)}
     */
    @Deprecated
    public BufferedImage ArrayToImageWithScaleCorrected(double[][] array) {
        //get max min then compute alpha beta for scaling
        double[] alphta = computeAlphaBeta(array);
        int value[] = new int[3];
        BufferedImage imageout = CreateBufferedImage();
        int[][] imagetmp = new int[image.getWidth()][image.getHeight()];
        WritableRaster raster = imageout.getRaster();
        int[] tmpOut = new int[256];
        int grey;
        for(int i = 0; i<image.getWidth(); i++){
            for(int j = 0; j<image.getHeight(); j++){
                grey = greyToColor(array[i][2*j],alphta[0],alphta[1]);
                imagetmp[i][j] = grey;
                tmpOut[grey]++;
            }
        }
        //Here we search for the min value that at least 2 pixel have, idem for max

        double alpha,beta;
        int newMin=0;
        int newMax=255;
        for (int i = 0; i < 256; i++) {
            if (newMin == 0 && tmpOut[i] > 2) {
                newMin = i;
            }
            if (newMax == 255 && tmpOut[255-i] > 2) {
                newMax = 255-i;
            }
        }

        if (newMin < newMax) {
            alpha = 255.0/(newMax - newMin);
            beta = -alpha*newMin;
        } else {
            alpha = 0.0;
            beta = 0.0;
        }
        for(int i = 0; i<image.getWidth(); i++){
            for(int j = 0; j<image.getHeight(); j++){
                grey = greyToColor(imagetmp[i][j],alpha,beta);
                value[0]=value[1]=value[2]=grey;
                raster.setPixel(i, j, value);
            }
        }
        return imageout;
    }

    @Override
    @Deprecated
    public BufferedImage ArrayToImageWithScale(double[][] array) {
        //get max min for scaling
        double[] alphta = computeAlphaBeta(array);
        int value[] = new int[3];
        BufferedImage imageout = CreateBufferedImage();
        WritableRaster raster = imageout.getRaster();
        int grey;
        for(int i = 0; i<image.getWidth(); i++){
            for(int j = 0; j<image.getHeight(); j++){
                grey = greyToColor(array[i][2*j],alphta[0], alphta[1]);
                value[0]=value[1]=value[2]=grey;
                raster.setPixel(i, j, value);
            }
        }
        return imageout;
    }

    /**
     * Will scale the raw data and use a colormap instead of grey value
     * @param array
     * @return
     * @deprecated  replaced by {@link #ArrayToImage(double[][], int)}
     */
    @Deprecated
    public BufferedImage ArrayToImageWithScaleColorMap(double[][] array) {
        double[] alphta = computeAlphaBeta(array);
        BufferedImage imageout = CreateBufferedImage();
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        for(int i = 0; i<image.getWidth(); i++){
            for(int j = 0; j<image.getHeight(); j++){
                grey = greyToColor(array[i][2*j],alphta[0],alphta[1]);
                raster.setPixel(i, j, new int[]{map.r[grey],map.g[grey],map.b[grey]});
            }
        }

        return imageout;
    }


    @Override
    @Deprecated
    public BufferedImage ArrayToImageWithScale1D(double[] array, boolean isComplex) {
        //get max min for scaling
        double [] alphta = computeAlphaBeta1D(array, isComplex);
        BufferedImage imageout = CreateBufferedImage();
        int[] value = new int[3];
        WritableRaster raster = imageout.getRaster();
        int grey;

        for(int j = 0; j<width; j++){
            for(int i = 0; i<height; i++){
                if (isComplex) {
                    grey = greyToColor(array[2*(i+j*height)],alphta[0],alphta[1]);
                }else{
                    grey = greyToColor(array[i+j*height],alphta[0],alphta[1]);
                }
                value[0]=value[1]=value[2]=grey;
                raster.setPixel(j, i, value);
            }
        }
        return imageout;
    }

    /**
     * Convert 1D array to bufferedImage and add a double correction
     * 
     * @param array
     * @param isComplex
     * @return
     * @deprecated  replaced by {@link #ArrayToImage(double[][], int)}
     */
    @Deprecated
    public BufferedImage ArrayToImageWithScale1DCorrected(double[] array, boolean isComplex) {
        //get max min then compute alpha beta for scaling
        double[] alphta = computeAlphaBeta1D(array,isComplex);
        int value[] = new int[3];
        BufferedImage imageout = CreateBufferedImage();
        int[][] imagetmp = new int[image.getWidth()][image.getHeight()];
        WritableRaster raster = imageout.getRaster();
        int[] tmpOut = new int[256];
        int grey;
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                grey = greyToColor(array[2*(i+j*image.getHeight())],alphta[0],alphta[1]);
                imagetmp[i][j] = grey;
                tmpOut[grey]++;
            }
        }
        //Here we search for the min value that at least 2 pixel have, idem for max

        double alpha,beta;
        int newMin=0;
        int newMax=255;
        for (int i = 0; i < 256; i++) {
            if (newMin == 0 && tmpOut[i] > 2) {
                newMin = i;
            }
            if (newMax == 255 && tmpOut[255-i] > 2) {
                newMax = 255-i;
            }
        }

        if (newMin < newMax) {
            alpha = 255.0/(newMax - newMin);
            beta = -alpha*newMin;
        } else {
            alpha = 0.0;
            beta = 0.0;
        }
        for(int j = 0; j<image.getWidth(); j++){
            for(int i = 0; i<image.getHeight(); i++){
                grey = greyToColor(imagetmp[i][j],alpha,beta);
                value[0]=value[1]=value[2]=grey;
                raster.setPixel(j, i, value);
            }
        }
        return imageout;
    }

    /**
     * Will scale the 1D raw data and use a colormap instead of grey value
     * @param array
     * @return
     * @deprecated  replaced by {@link #ArrayToImage(double[][], int)}
     */
    @Deprecated
    public BufferedImage ArrayToImageWithScale1DColorMap(double[] array) {
        //get max min for scaling
        double[] alphta = computeAlphaBeta1D(array, false);
        BufferedImage imageout = CreateBufferedImage();
        WritableRaster raster = imageout.getRaster();
        ColorMap map = ColorMap.getJet(256);
        int grey;
        for(int i = 0; i<height; i++){
            for(int j = 0; j<width; j++){
                //*2 here
                grey = greyToColor(array[j+i*width],alphta[0],alphta[1]);
                raster.setPixel(j, i, new int[]{map.r[grey],map.g[grey],map.b[grey]});
            }
        }
        return imageout;
    }

    /********************************** FFT PART **********************************/

    private void scale(double[] array){
        double scale = 1.0/(width*height);
        for (int i = 0; i < array.length; i++) {
            array[i]*=scale;
        }
    }

    private void scale(double[][] array){
        double scale = 1.0/(width*height);
        for (int i = 0; i < array[0].length; i++) {
            for (int j = 0; j < array.length; j++) {
                array[j][i]*=scale;
            }
        }
    }

    private void scale(float[] array){
        double scale = 1.0/(width*height);
        for (int i = 0; i < array.length; i++) {
            array[i]*=scale;
        }
    }

    private void scale(float[][] array){
        double scale = 1.0/(width*height);
        for (int i = 0; i < array[0].length; i++) {
            for (int j = 0; j < array.length; j++) {
                array[j][i]*=scale;
            }
        }
    }

    @Override
    public void FFT(double[][] array) {
        if(fft == null){
            fft = new DoubleFFT_2D(height, width);
        }
        fft.realForwardFull(array);
    }

    /**
     * FFT using float with 2D float array
     * 
     * @param array
     */
    public void FFT(float[][] array) {
        if(fftFloat == null){
            fftFloat = new FloatFFT_2D(height, width);
        }
        fftFloat.realForwardFull(array);
    }

    @Override
    public void IFFT(double[][] array) {
        fft.complexInverse(array, false);
        scale(array);
    }

    /**
     * inverse FFT using float with 2D float array
     * 
     * @param array
     */
    public void IFFT(float[][] array) {
        fftFloat.complexInverse(array, false);
        scale(array);
    }

    @Override
    public void FFT1D(double[] array) {
        if(fft1D == null){
            fft1D = new DoubleFFT_1D(width*height);
        }
        fft1D.realForwardFull(array);
    }

    /**
     * inverse FFT using float with 1D float array
     * 
     * @param array
     */
    public void FFT1D(float[] array) {
        if(fft1DFloat == null){
            fft1DFloat = new FloatFFT_1D(width*height);
        }
        fft1DFloat.realForwardFull(array);
    }

    @Override
    public void IFFT1D(double[] array) {
        fft1D.complexInverse(array, false);
        scale(array);
    }

    /**
     * inverse FFT using float with 1D non complex float array
     * 
     * @param array
     */
    public void IFFT1D(float[] array) {
        fft1DFloat.complexInverse(array, false);
        scale(array);
    }

    /********************************** PSF PADDING **********************************/
    /*
     * Memo: even if y = y*2, we store the psf in a array x*y !!
     * 
     * */

    @Override
    public double[][] PSF_Padding(boolean isComplex) {
        double [][]tableau_psf;
        if (isComplex) {
            tableau_psf = new double[height][2*width];
        } else {
            tableau_psf = new double[height][width];
        }
        int demiPsfW = image_psf.getWidth()/2;int demiPsfH = image_psf.getHeight()/2;

        int[][]test = PSFToArray();

        //bloc haut à gauche: A
        for(int j = 0; j<demiPsfW; j++){
            for(int i=0;i<demiPsfH;i++){
                tableau_psf[(height-demiPsfH)+i][width-demiPsfW+j] = test[i][j];
            }
        }
        //bloc haut a droite: B
        for(int j = demiPsfW; j<image_psf.getWidth(); j++){
            for(int i=0;i<demiPsfH;i++){
                tableau_psf[(height-demiPsfH)+i][j-demiPsfW] = test[i][j]; 
            }
        }
        //bloc bas a gauche: C
        for(int j = 0; j<demiPsfW; j++){
            for(int i=demiPsfH;i<image_psf.getHeight();i++){
                tableau_psf[i-demiPsfH][width-demiPsfW+j] = test[i][j]; 
            }
        }
        //bloc bas a droite: D
        for(int j = demiPsfW; j<image_psf.getWidth(); j++){
            for(int i=demiPsfH;i<image_psf.getHeight();i++){
                tableau_psf[i-demiPsfH][j-demiPsfW] = test[i][j]; 
            }
        }
        //printTab(tableau_psf);
        return tableau_psf;
    }


    @Override
    public double[] PSF_Padding1D(boolean isComplex) {
        double []tableau_psf;
        int hght = 0;
        if (isComplex) {
            tableau_psf = new double[width*2*height];
        } else {
            tableau_psf = new double[width*height];
        }
        hght = height;
        int psfH = image_psf.getHeight();
        int psfW = image_psf.getWidth();
        int demiPsfW = psfW/2;int demiPsfH = psfH/2;
        int[]test = PSFToArray1D();

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
            //Bloc haut à gauche: D
            for(int j = 0; j < demiPsfW; j++){
                for(int i = 0; i < demiPsfH; i++){
                    tableau_psf[2*(i+j*hght)] = test[(demiPsfH+i)+(demiPsfW+j)*psfH]; 
                }
            }
            //bloc haut a droite: C
            for(int j = width-demiPsfW; j < width; j++){
                for(int i = 0; i < demiPsfH; i++){
                    tableau_psf[2*(i+j*hght)] = test[(demiPsfH+i)+(demiPsfW-width+j)*psfH]; 
                }
            }
            //bloc bas a gauche: B
            for(int j = 0; j < demiPsfW; j++){
                for(int i = height-demiPsfH; i < height; i++){
                    tableau_psf[2*(i+j*hght)] = test[(demiPsfH-height+i)+(demiPsfW+j)*psfH]; 
                }
            }
            //bloc bas a droite: A
            for(int j = width-demiPsfW; j < width; j++){
                for(int i = height-demiPsfH; i < height; i++){
                    tableau_psf[2*(i+j*hght)] = test[(demiPsfH-height+i)+(demiPsfW-width+j)*psfH]; 
                }
            }
        }else{
            //Here we are writing at (i+j*hght)
            //Bloc haut à gauche: D
            for(int j = 0; j < demiPsfW; j++){
                for(int i = 0; i < demiPsfH; i++){
                    tableau_psf[i+j*hght] = test[(demiPsfH+i)+(demiPsfW+j)*psfH]; 
                }
            }
            //bloc haut a droite: C
            for(int j = width-demiPsfW; j < width; j++){
                for(int i = 0; i < demiPsfH; i++){
                    tableau_psf[i+j*hght] = test[(demiPsfH+i)+(demiPsfW-width+j)*psfH]; 
                }
            }
            //bloc bas a gauche: B
            for(int j = 0; j < demiPsfW; j++){
                for(int i = height-demiPsfH; i < height; i++){
                    tableau_psf[i+j*hght] = test[(demiPsfH-height+i)+(demiPsfW+j)*psfH]; 
                }
            }
            //bloc bas a droite: A
            for(int j = width-demiPsfW; j < width; j++){
                for(int i = height-demiPsfH; i < height; i++){
                    tableau_psf[i+j*hght] = test[(demiPsfH-height+i)+(demiPsfW-width+j)*psfH]; 
                }
            }
        }
        return tableau_psf;
    }

    /**
     * float PSF shift + padding
     * 
     * @param isComplex
     * @return
     */
    public float[] PSF_Padding1DFloat(boolean isComplex) {
        float []tableau_psf;
        int hght = 0;
        if (isComplex) {
            tableau_psf = new float[width*2*height];
        } else {
            tableau_psf = new float[width*height];
        }
        hght = height;
        int psfH = image_psf.getHeight();
        int psfW = image_psf.getWidth();
        int demiPsfW = psfW/2;int demiPsfH = psfH/2;
        int[]test = PSFToArray1D();

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
            //UP, Left Bloc: D
            for(int j = 0; j < demiPsfW; j++){
                for(int i = 0; i < demiPsfH; i++){
                    tableau_psf[2*(i+j*hght)] = test[(demiPsfH+i)+(demiPsfW+j)*psfH]; 
                }
            }
            //UP, right, bloc: C
            for(int j = width-demiPsfW; j < width; j++){
                for(int i = 0; i < demiPsfH; i++){
                    tableau_psf[2*(i+j*hght)] = test[(demiPsfH+i)+(demiPsfW-width+j)*psfH]; 
                }
            }
            //Bottom, Left bloc: B
            for(int j = 0; j < demiPsfW; j++){
                for(int i = height-demiPsfH; i < height; i++){
                    tableau_psf[2*(i+j*hght)] = test[(demiPsfH-height+i)+(demiPsfW+j)*psfH]; 
                }
            }
            //Bottom, right bloc: A
            for(int j = width-demiPsfW; j < width; j++){
                for(int i = height-demiPsfH; i < height; i++){
                    tableau_psf[2*(i+j*hght)] = test[(demiPsfH-height+i)+(demiPsfW-width+j)*psfH]; 
                }
            }
        }else{
            //Here we are writing at (i+j*hght)
            //UP, Left Bloc: D
            for(int j = 0; j < demiPsfW; j++){
                for(int i = 0; i < demiPsfH; i++){
                    tableau_psf[i+j*hght] = test[(demiPsfH+i)+(demiPsfW+j)*psfH]; 
                }
            }
            //UP, right, bloc: C
            for(int j = width-demiPsfW; j < width; j++){
                for(int i = 0; i < demiPsfH; i++){
                    tableau_psf[i+j*hght] = test[(demiPsfH+i)+(demiPsfW-width+j)*psfH]; 
                }
            }
            //Bottom, Left bloc: B
            for(int j = 0; j < demiPsfW; j++){
                for(int i = height-demiPsfH; i < height; i++){
                    tableau_psf[i+j*hght] = test[(demiPsfH-height+i)+(demiPsfW+j)*psfH]; 
                }
            }
            //Bottom, right bloc: A
            for(int j = width-demiPsfW; j < width; j++){
                for(int i = height-demiPsfH; i < height; i++){
                    tableau_psf[i+j*hght] = test[(demiPsfH-height+i)+(demiPsfW-width+j)*psfH]; 
                }
            }
        }
        return tableau_psf;
    }

    /********************************** Utils functions **********************************/

    public void printTab(double[][]tab){
        System.out.println("Normal");
        for(double[]ab:tab){
            for(double a:ab){
                System.out.print((int)a+",");
            }
            System.out.println("");
        }
    }

    /**
     * util function
     * @param tab
     */
    public void printTab(float[][]tab){
        System.out.println("Normal");
        for(float[]ab:tab){
            for(float a:ab){
                System.out.print((int)a+",");
            }
            System.out.println("");
        }
    }

    @Override
    public void printTab1D(double[] tab, boolean isComplex) {
        int count =0;
        int wdth = 0;
        if (isComplex) {
            System.out.println("1D Complex");
            wdth = 2*width;
        } else {
            System.out.println("1D");
            wdth = width;
        }

        for(double ab:tab){
            System.out.print((int)ab+",");
            count++;
            if(count == wdth){
                System.out.println("");
                count=0;
            }
        }
    }

    /**
     * 
     * @param tab
     * @param isComplex
     */
    public void printTab1D(float[] tab, boolean isComplex) {
        int count =0;
        int wdth = 0;
        if (isComplex) {
            System.out.println("1D Complex");
            wdth = 2*width;
        } else {
            System.out.println("1D");
            wdth = width;
        }

        for(float ab:tab){
            System.out.print((int)ab+",");
            count++;
            if(count == wdth){
                System.out.println("");
                count=0;
            }
        }
    }

    /**
     * Purely debug function
     * 
     * @param tab
     * @param isComplex
     */
    public void printPSF1D(double[] tab, boolean isComplex) {
        int count =0;
        int wdth = image_psf.getHeight();
        for(double ab:tab){
            System.out.print((int)ab+",");
            count++;
            if(count == wdth){
                System.out.println("");
                count=0;
            }
        }
    }

    /**
     * Purely debug function
     * 
     * @param tab
     * @param isComplex
     */
    public void printPSF1D(float[] tab, boolean isComplex) {
        int count =0;
        int wdth = image_psf.getHeight();
        for(float ab:tab){
            System.out.print((int)ab+",");
            count++;
            if(count == wdth){
                System.out.println("");
                count=0;
            }
        }
    }

    /**
     * test the tab for bad values
     * @param tab
     */
    public static void testLongTab(float[] tab){
        for(float a: tab){
            if (a == (Long.MAX_VALUE+1)) {
                System.err.println("Your Long value is too long");
            }
        }
    }

    /**
     * Still testing tab for bad values
     * @param tab
     */
    public static void testLongTab(float[][] tab){
        for(float[] a: tab){
            for(float b:a){
                if (b == (Long.MAX_VALUE+1)) {
                    System.err.println("Your Long value is too long");
                }
            }
        }
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
