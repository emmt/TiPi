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
     * Open the images and store them
     * @param pathImage The path to the image
     * @param pathPSF The path to the image
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
     * Open the images and store them
     * 
     * @param image A buffered image that is the image
     * @param PSF A BufferedImage that is the PSF
     */

    public void readImage(BufferedImage image, BufferedImage PSF) {
        readImage(image, PSF,false);
    }

    /**
     * Open the images and store them
     * 
     * @param image A buffered image that is the image
     * @param PSF A buffered image that is the PSF
     * @param padding Do we zero pad the image ?
     */
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

    /**
     * Open the images and store them as vectors
     * @param pathImage
     * @param pathPSF
     * @param singlePrecision Double or Float ?
     */
    public void readImageVect(String pathImage, String pathPSF, boolean singlePrecision) {
        try {
            readImageVect(ImageIO.read(new File(pathImage)), ImageIO.read(new File(pathPSF)), singlePrecision);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Wrong path given");
        }
    }

    /**
     * Open the images and store them as vectors
     * 
     * @param image
     * @param PSF
     * @param singlePrecision Double or Float
     */
    public void readImageVect(BufferedImage image, BufferedImage PSF, boolean singlePrecision) {
        readImageVect(image, PSF, false, singlePrecision,true);
    }

    /**
     * Open the images and store them as vectors
     * 
     * @param image
     * @param PSF
     * @param padding Do we zero pad the image ?
     * @param singlePrecision Double or Float ?
     * @param isComplex is the input of size 2*size image ?
     */
    public void readImageVect(BufferedImage image, BufferedImage PSF, Boolean padding, boolean singlePrecision, boolean isComplex) {
        //For now, no padding option: TODO add padding option
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
            //we will not create a complex now (cf pad)
            this.imagePsfVect = CommonUtils.imageToVector(psfSpace, PSF, singlePrecision, false); 
        }
        width = image.getWidth();
        height = image.getHeight();
        single = singlePrecision;
        this.image = image;
        this.image_psf = PSF;
        this.isComplex = isComplex;
    }

    /********************************** Vector quick util **********************************/

    /**
     * Clone the image
     * 
     * @return A vector
     */
    public Vector cloneImageVect(){
        return imageVect.getSpace().clone(imageVect);
    }

    /**
     * Clone the PSF
     * 
     * @return A vector
     */
    public Vector clonePsfVect(){
        return imagePsfVect.getSpace().clone(imagePsfVect);
    }

    /**
     * Pad the PSF and return a vector
     * 
     * @return A vector
     */
    public Vector getPsfPadVect(){
        return CommonUtils.psfPadding1D(imageSpace,imageSpaceComplex, imagePsfVect, single, isComplex);
    }

    /**
     * Convert an array to an image with possibility of scaling, adding colors 
     * 
     * @param vector
     * @param correction see CommonUtils static
     * @param isComplex is the input of size 2*size image ?
     * @return A buffered image that is the image
     */
    public BufferedImage arrayToImage(Vector vector, int correction,boolean isComplex){
        return CommonUtils.vectorToImage(imageSpace, vector, correction ,single, isComplex);
    }

    /********************************** X TO ARRAY **********************************/

    /**
     * Convert the image to a double array
     * 
     * @param isComplex is the input of size 2*size image ?
     * @return An array
     */
    public double[][] imageToArray(boolean isComplex) {
        return CommonUtils.imageToArray(image, isComplex);
    }

    /**
     * Convert the PSF to a double array
     * 
     * @param isComplex is the input of size 2*size image ?
     * @return An array
     */
    public double[][] psfToArray(boolean isComplex) {
        return CommonUtils.imageToArray(image_psf, isComplex);
    }

    /**
     * Convert image to float array
     *
     * @param isComplex is the input of size 2*size image ?
     * @return An array
     */
    public float[][] imageToArrayFloat(boolean isComplex) {
        return CommonUtils.imageToArrayFloat(image, isComplex);
    }

    /**
     * Convert an image to a 1D array
     * 
     * @param isComplex is the input of size 2*size image ?
     * @return A 1D array
     */
    public double[] imageToArray1D(boolean isComplex) {
        return CommonUtils.imageToArray1D(image, isComplex);
    }

    /**
     * Convert the PSF to a 1D array
     * 
     * @param isComplex is the input of size 2*size image ?
     * @return A 1D array
     */
    public double[] psfToArray1D(boolean isComplex) {
        if (!(image_psf.getWidth() == image.getWidth() && image_psf.getHeight() == image.getHeight())) {
            throw new IllegalArgumentException("The PSF should be of same size as image (No scale for now when splitted)");
        } else {
            return CommonUtils.imageToArray1D(image_psf, isComplex);
        }

    }

    /**
     * Convert image to float 1D array
     * 
     * @param isComplex is the input of size 2*size image ?
     * @return A 1D array
     */
    public float[] imageToArray1DFloat(boolean isComplex) {
        return CommonUtils.imageToArray1DFloat(image, isComplex);
    }

    /**
     * Front function that will apply different job on the given array
     *
     * @param array a complex array
     * @param job see DeconUtils static
     * @return A buffered image that is the image
     */
    public BufferedImage arrayToImage(double[][] array, int job){
        return arrayToImage(array, job, false);
    }

    /**
     * Front function that will apply different job on the given complex array. 
     * It may also unpad the image.
     * 
     * @param array
     * @param job see CommonUtils static
     * @param isImagePadded
     * @return A buffered image that is the image
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
     * Front function that will apply different job on the given complex array
     *
     * @param array a complex array
     * @param job
     * @return A buffered image that is the image
     */
    public BufferedImage arrayToImage(float[][] array, int job){
        return arrayToImage(array, job, false);
    }

    /**
     * Front function that will apply different job on the given complex array. 
     * It may also unpad the image.
     *
     * @param array a complex array
     * @param job
     * @param isImagePadded 
     * @return A buffered image that is the image
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
     * Front function that will apply different job on the given complex array
     *
     * @param array a complex array
     * @param job
     * @param isComplex is the input of size 2*size image ?
     * @return A buffered image that is the image
     */
    public BufferedImage arrayToImage1D(double[] array, int job, boolean isComplex){
        return CommonUtils.arrayToImage1D(array, job, image.getWidth(), image.getHeight(), isComplex);
    }

    /**
     * Front function that will apply different job on the given complex array
     *
     * @param array a complex array
     * @param job
     * @param isComplex isComplex is the input of size 2*size image ?
     * @return A buffered image that is the image
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
        for (int j = 0; j < array[0].length; j++) {
            for (int i = 0; i < array.length; i++) {
                array[i][j]*=scale;
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
        for (int j = 0; j < array[0].length; j++) {
            for (int i = 0; i < array.length; i++) {
                array[i][j]*=scale;
            }
        }
    }

    /**
     * Make in place computation of the FFT
     * 
     * @param array
     */
    public void FFT(double[][] array) {
        if(fft == null){
            fft = new DoubleFFT_2D(width, height);
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
            fftFloat = new FloatFFT_2D(width, height);
        }
        fftFloat.realForwardFull(array);
    }

    /**
     * Make in place computation of the inverse FFT
     * 
     * @param array
     */
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

    /**
     * Make in place computation of FFT
     * 
     * @param array
     */
    public void FFT1D(double[] array) {
        if(fft1D == null){
            fft1D = new DoubleFFT_1D(width*height);
        }
        fft1D.realForwardFull(array);
    }

    /**
     * FFT using float with 1D float array input
     *
     * @param array
     */
    public void FFT1D(float[] array) {
        if(fft1DFloat == null){
            fft1DFloat = new FloatFFT_1D(width*height);
        }
        fft1DFloat.realForwardFull(array);
    }

    /**
     * inverse FFT using 1D double array
     * 
     * @param array
     */
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

    /**
     * Compute FFTD with vector in input
     * 
     * @param vector
     */
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

    /**
     * Compute inverse FFTD with vector in input
     * 
     * @param vector
     */
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

    /**
     * Pad the PSF to the size of the image and split it.
     * @param isComplex 
     * @return An array
     */
    public double[][] psfPadding(boolean isComplex) {
        return CommonUtils.psfPadding(image, image_psf, isComplex);
    }

    /**
     * Pad the PSF to the size of the image and split it.
     * 
     * @param isComplex
     * @return An array
     */
    public double[] psfPadding1D(boolean isComplex) {
        return CommonUtils.psfPadding1D(image, image_psf, isComplex);
    }

    /**
     * Float PSF shift + padding
     *
     * @param isComplex
     * @return An array
     */
    public float[] psfPadding1DFloat(boolean isComplex) {
        return CommonUtils.psfPadding1DFloat(image, image_psf, isComplex);
    }

    /********************************** Utils functions **********************************/

    /**
     * Get the evaluated size of the F
     * 
     * @return Evaluated size
     */
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
