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

package mitiv.deconv;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import mitiv.linalg.DoubleVector;
import mitiv.linalg.DoubleVectorSpaceWithRank;
import mitiv.linalg.FloatVector;
import mitiv.linalg.FloatVectorSpaceWithRank;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;
import mitiv.utils.CommonUtils;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;

/**
 * @author Leger Jonathan
 *
 */
public class DeconvUtils {

    //Buffered and double 
    private BufferedImage image;
    private BufferedImage image_psf;
    private DoubleFFT_2D fft;
    private DoubleFFT_1D fft1D;
    private FloatFFT_2D fftFloat;
    private FloatFFT_1D fft1DFloat;
    private int sizePadding = -1;

    //Vector part
    private Vector imageVect;
    private Vector imagePsfVect;
    private VectorSpace imageSpace;
    private VectorSpace imageSpaceComplex;
    private boolean single = true;
    boolean isComplex;

    /**
     * Job to compute with the wiener filter
     */
    public static final int JOB_WIENER = 0;
    /**
     * Job to compute using quadratic and circulant approximation
     */
    public static final int JOB_QUAD = 1;
    /**
     * Job to compute with Conjugate gradients
     */
    public static final int JOB_CG = 2;
    /**
     * The width of the image given to ReadImage
     */
    public int width;
    /**
     * The height of the image given to ReadImage
     */
    public int height;

    /**
     * Kind of setter that will be called after we have open the image, whatever
     * the input image cf ReadImage
     */
    private void setValue(){
        width = image.getWidth();
        height = image.getHeight();
        if (image_psf.getWidth() > image.getWidth() || image_psf.getHeight() > image.getHeight()) {
            throw new IllegalArgumentException("PSF is too large");
        }
    }

    /********************************** READ INPUT IMAGE **********************************/
    /**
     * Open the image and store them
     */
    public void readImage(String pathImage, String pathPSF) {
        try {
            readImage(ImageIO.read(new File(pathImage)), ImageIO.read(new File(pathPSF)));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Wrong path given");
        }
    }

    /**
     * Open the image and store them
     */

    public void readImage(BufferedImage image, BufferedImage PSF) {
        readImage(image, PSF,false);
    }

    public void readImage(BufferedImage image, BufferedImage PSF, Boolean padding) {
        if (padding) {
            sizePadding = CommonUtils.estimatePsfSize(PSF);
            this.image = CommonUtils.imagePad(image, sizePadding);
        } else {
            this.image = image;
        }
        this.image_psf = PSF;
        setValue();
    }

    /********************************** Read image vector **********************************/

    public void readImageVect(String pathImage, String pathPSF, boolean singlePrecision) {
        try {
            readImageVect(ImageIO.read(new File(pathImage)), ImageIO.read(new File(pathPSF)), singlePrecision);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Wrong path given");
        }
    }

    public void readImageVect(BufferedImage image, BufferedImage PSF, boolean singlePrecision) {
        readImageVect(image, PSF, false, singlePrecision,true);
    }

    public void readImageVect(BufferedImage image, BufferedImage PSF, Boolean padding, boolean singlePrecision, boolean isComplex) {
        //For now, no padding option TODO add padding option
        if (singlePrecision) {
            imageSpace = new FloatVectorSpaceWithRank(image.getHeight(), image.getWidth());
            FloatVectorSpaceWithRank psfSpace = new FloatVectorSpaceWithRank(PSF.getWidth(), PSF.getHeight());
            imageSpaceComplex = new FloatVectorSpaceWithRank(image.getHeight()*2, image.getWidth());

            this.imageVect = CommonUtils.imageToVector(imageSpace, image, singlePrecision, isComplex);
            this.imagePsfVect = CommonUtils.imageToVector(psfSpace, PSF, singlePrecision, isComplex);
        } else {
            imageSpace = new DoubleVectorSpaceWithRank(image.getHeight(), image.getWidth());
            DoubleVectorSpaceWithRank psfSpace = new DoubleVectorSpaceWithRank(PSF.getHeight(), PSF.getWidth());
            imageSpaceComplex = new DoubleVectorSpaceWithRank(image.getHeight()*2, image.getWidth());
            if (isComplex) {
                this.imageVect = CommonUtils.imageToVector(imageSpaceComplex, image, singlePrecision , isComplex);
            } else {
                this.imageVect = CommonUtils.imageToVector(imageSpace, image, singlePrecision , isComplex);
            }
            this.imagePsfVect = CommonUtils.imageToVector(psfSpace, PSF, singlePrecision, false); //we will not create a complex now (cf pad)
        }
        width = image.getWidth();
        height = image.getHeight();
        single = singlePrecision;
        this.image = image;
        this.image_psf = PSF;
        this.isComplex = isComplex;
    }

    /********************************** Vector quick util **********************************/

    public Vector getImageVect(){
        return imageVect.getSpace().clone(imageVect);
    }

    public Vector getPsfPad(){
        return CommonUtils.psfPadding1D(imageSpace,imageSpaceComplex, imagePsfVect, single, isComplex);
    }

    public BufferedImage arrayToImage(Vector vector, int correction,boolean isComplex){
        return CommonUtils.vectorToImage(imageSpace, vector, correction ,single, isComplex);
    }

    /********************************** X TO ARRAY **********************************/


    public double[][] imageToArray(boolean isComplex) {
        return CommonUtils.imageToArray(image, isComplex);
    }

    /**
     * Convert image to float array
     *
     * @param isComplex
     * @return
     */
    public float[][] imageToArrayFloat(boolean isComplex) {
        return CommonUtils.imageToArrayFloat(image, isComplex);
    }


    public double[] imageToArray1D(boolean isComplex) {
        return CommonUtils.imageToArray1D(image, isComplex);
    }

    /**
     * Convert image to float 1D array
     * @param isComplex
     * @return
     */
    public float[] imageToArray1DFloat(boolean isComplex) {
        return CommonUtils.imageToArray1DFloat(image, isComplex);
    }

    /**
     * Front function that will apply different job on the given array of size
     * height,witdh*2
     *
     * @param array a complex array
     * @param job
     * @return
     */
    public BufferedImage arrayToImage(double[][] array, int job){
        return arrayToImage(array, job, false);
    }

    /**
     * Front function that will apply different job on the given array of size
     * height,witdh*2. It may also unpad the image.
     * 
     * @param array
     * @param job
     * @param isImagePadded
     * @return
     */
    public BufferedImage arrayToImage(double[][] array, int job, boolean isImagePadded){
        if (isImagePadded) {
            BufferedImage tmp =  CommonUtils.arrayToImage(array, job, true);
            return CommonUtils.imageUnPad(tmp, sizePadding);
        } else {
            return CommonUtils.arrayToImage(array, job, true);
        }
    }

    /**
     * Front function that will apply different job on the given array of size
     * height,witdh*2
     *
     * @param array a complex array
     * @param job
     * @return
     */
    public BufferedImage arrayToImage(float[][] array, int job){
        return arrayToImage(array, job, false);
    }

    /**
     * Front function that will apply different job on the given array of size
     * height,witdh*2. It may also unpad the image.
     *
     * @param array a complex array
     * @param job
     * @re
     */
    public BufferedImage arrayToImage(float[][] array, int job, boolean isImagePadded){
        if (isImagePadded) {
            BufferedImage tmp =  CommonUtils.arrayToImage(array, job, true);
            return CommonUtils.imageUnPad(tmp, sizePadding);
        } else {
            return CommonUtils.arrayToImage(array, job, true);
        }
    }

    /**
     * Front function that will apply different job on the given array of size
     * height,witdh*2
     *
     * @param array a complex array
     * @param job
     * @return
     */
    public BufferedImage arrayToImage1D(double[] array, int job, boolean isComplex){
        return CommonUtils.arrayToImage1D(array, job, image.getWidth(), image.getHeight(), isComplex);
    }

    /**
     * Front function that will apply different job on the given array of size
     * height,witdh*2
     *
     * @param array a complex array
     * @param job
     * @return
     */
    public BufferedImage arrayToImage1D(float[] array, int job, boolean isComplex){
        return CommonUtils.arrayToImage1D(array, job, image.getWidth(), image.getHeight(), isComplex);
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

    /********************************** FFT Vector **********************************/

    public void FFT1D(Vector vector) {
        if (single) {
            FloatVector vectorFloat = (FloatVector)vector;
            float[] array = vectorFloat.getData();
            int size = ((FloatVectorSpaceWithRank)imageSpace).getSize();
            if(fft1DFloat == null){
                fft1DFloat = new FloatFFT_1D(size);
            }
            fft1DFloat.realForwardFull(array);
        } else {
            DoubleVector vectorDouble = (DoubleVector)vector;
            double[] array = vectorDouble.getData();
            int size = ((DoubleVectorSpaceWithRank)imageSpace).getSize();
            if(fft1D == null){
                fft1D = new DoubleFFT_1D(size);
            }
            fft1D.realForwardFull(array);
        }
    }

    public void IFFT1D(Vector vector) {
        if (single) {
            FloatVector vectorFloat = (FloatVector)vector;
            float[] array = vectorFloat.getData();
            fft1DFloat.complexInverse(array, true);
        } else {
            DoubleVector vectorDouble = (DoubleVector)vector;
            double[] array = vectorDouble.getData();
            fft1D.complexInverse(array, true);
        }
    }

    /********************************** PSF PADDING **********************************/
    /*
     * Memo: even if y = y*2, we store the psf in a array x*y !!
     *
     * */

    public double[][] psfPadding(boolean isComplex) {
        return CommonUtils.psfPadding(image, image_psf, isComplex);
    }

    public double[] psfPadding1D(boolean isComplex) {
        return CommonUtils.psfPadding1D(image, image_psf, isComplex);
    }

    /**
     * float PSF shift + padding
     *
     * @param isComplex
     * @return
     */
    public float[] psfPadding1DFloat(boolean isComplex) {
        return CommonUtils.psfPadding1DFloat(image, image_psf, isComplex);
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

    public int getImagePadding(){
        return sizePadding;
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
