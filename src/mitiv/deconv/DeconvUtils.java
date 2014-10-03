/*
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

import icy.image.IcyBufferedImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.FloatShapedVectorSpace;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.linalg.shaped.ShapedVectorSpace;
import mitiv.utils.CommonUtils;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_3D;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;

/**
 * @author Leger Jonathan
 *
 */
public class DeconvUtils {

    //Buffered and double
    private BufferedImage image;
    private BufferedImage image_psf;
    private DoubleFFT_1D fft1D;
    private FloatFFT_1D fft1DFloat;
    public int sizePadding = -1;

    //Vector part
    private ShapedVector imageVect;
    private ShapedVector imagePsfVect;
    private ShapedVectorSpace imageSpace;
    private ShapedVectorSpace imageSpaceComplex;
    private boolean single = true;
    boolean isComplex;

    //3D
    ArrayList<BufferedImage> listImage;
    ArrayList<BufferedImage> listPSF;
    public int sizeZ;
    private DoubleFFT_3D fft3D;

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
     * @param image 
     * @param PSF 
     */
    public void readImage(ArrayList<BufferedImage> image, ArrayList<BufferedImage> PSF) {
        //IMPORTANT WE PAD AS WE COMPUTE
        //FIXME here padding
        int max = Math.max(PSF.get(0).getHeight(), PSF.get(0).getWidth());
        //sizePadding = max;
        sizePadding = 0;
        double coef = (double)(sizePadding+max)/max;
        ArrayList<BufferedImage> listImagePad = CommonUtils.imagePad(image, coef, false);
        ArrayList<BufferedImage> listPSFPad = CommonUtils.imagePad(PSF, coef, true);
        this.listImage = listImagePad;
        this.listPSF = listPSFPad;
        sizeZ = listImagePad.size();
        width = listImagePad.get(0).getWidth();
        height = listImagePad.get(0).getHeight();
        //System.out.format("W %d H %d Z %d coef %f\n",width,height,sizeZ,coef);
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

    public void readImageVect(ArrayList<BufferedImage>image, ArrayList<BufferedImage> PSF, boolean singlePrecision) {
        throw new RuntimeException("Not implemented yet");
    }

    public void readImageVect(BufferedImage image, BufferedImage PSF, Boolean padding, boolean singlePrecision, boolean isComplex) {
        //For now, no padding option: TODO add padding option
        if (singlePrecision) {
            imageSpace = new FloatShapedVectorSpace(image.getHeight(), image.getWidth());
            FloatShapedVectorSpace psfSpace = new FloatShapedVectorSpace(PSF.getWidth(), PSF.getHeight());
            imageSpaceComplex = new FloatShapedVectorSpace(image.getHeight()*2, image.getWidth());

            this.imageVect = CommonUtils.imageToVector(imageSpace, image, singlePrecision, isComplex);
            this.imagePsfVect = CommonUtils.imageToVector(psfSpace, PSF, singlePrecision, isComplex);
        } else {
            imageSpace = new DoubleShapedVectorSpace(image.getHeight(), image.getWidth());
            DoubleShapedVectorSpace psfSpace = new DoubleShapedVectorSpace(PSF.getHeight(), PSF.getWidth());
            imageSpaceComplex = new DoubleShapedVectorSpace(image.getHeight()*2, image.getWidth());
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
    public ShapedVector cloneImageVect(){
        return imageVect.getSpace().clone(imageVect);
    }

    public ShapedVector getImageVect(){
        return imageVect;
    }

    /**
     * Clone the PSF
     * 
     * @return A vector
     */
    public ShapedVector clonePsfVect(){
        return imagePsfVect.getSpace().clone(imagePsfVect);
    }

    public ShapedVector getPSfVect(){
        return imagePsfVect;
    }
    /**
     * Pad the PSF and return a vector
     * 
     * @return A vector
     */
    public ShapedVector getPsfPadVect(){
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
    public BufferedImage arrayToImage(ShapedVector vector, int correction,boolean isComplex){
        return CommonUtils.vectorToImage(imageSpace, vector, correction ,single, isComplex);
    }

    /********************************** X TO ARRAY **********************************/

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
     * Convert a stack of image to a 1D array
     * @param isComplex 
     * 
     * @return A 1D array
     */
    public double[] image3DToArray1D(boolean isComplex) {
        return CommonUtils.image3DToArray1D(listImage, width, height, sizeZ, isComplex);
    }
    /**
     * Convert the PSF to a 1D array
     * @param isComplex 
     * 
     * @return A 1D array
     */
    public double[] psf3DToArray1D(boolean isComplex) {
        double[] out;
        if (isComplex) {
            out = new double[sizeZ*width*height*2];
        } else {
            out = new double[sizeZ*width*height];
        }
        for (int j = 0; j < sizeZ; j++) {
            double[] tmp = CommonUtils.psfPadding1D(listImage.get(j), listPSF.get(j), false);
            for (int i = 0; i < tmp.length; i++) {
                out[i+j*tmp.length] = tmp[i];
            }
        }
        return out;
    }

    public double[] psf3DToArray1Dexp(boolean isComplex) {
        return CommonUtils.image3DToArray1D(listPSF, width, height, sizeZ, isComplex);
    }

    public double[] shiftPsf3DToArray1D(boolean isComplex) {
        double[] out;
        if (isComplex) {
            out = new double[width*height*sizeZ*2];
        } else {
            out = new double[width*height*sizeZ];
        }
        double[] psfIn = psf3DToArray1Dexp(isComplex);
        if (psfIn.length != out.length) {
            System.err.println("Bad size for psf and output deconvutil l356");
        }
        CommonUtils.fftShift3D(psfIn,out, width, height, sizeZ);
        //CommonUtils.psf3DPadding1D(out, psfIn , width, height, sizeZ);
        return out;
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

    public ArrayList<BufferedImage> arrayToImage3D(double[] array, int job, boolean isComplex){
        ArrayList<BufferedImage> out = new ArrayList<BufferedImage>();
        if (isComplex) {
            double[] tmp = new double[width*height*2];
            for (int j = 0; j < sizeZ; j++) {
                for (int i = 0; i < width*height*2; i++) {
                    tmp[i] = array[i+2*j*height*width];
                }
                out.add(CommonUtils.arrayToImage1D(tmp, job, width, height, true));
            }
        }else{
            double[] tmp = new double[width*height];
            for (int j = 0; j < sizeZ; j++) {
                for (int i = 0; i < width*height; i++) {
                    tmp[i] = array[i+j*height*width];
                }
                out.add(CommonUtils.arrayToImage1D(tmp, job, width, height, false));
            }
        }
        //IMPORTANT WE DEPAD AS WE COMPUTE OR NOT ...
        //return CommonUtils.imageUnPad(out, sizePadding);
        return out;
        //FIXME pad option
    }


    public ArrayList<BufferedImage> arrayToIcyImage3D(double[] array, int job, boolean isComplex){
        ArrayList<BufferedImage> out = new ArrayList<BufferedImage>();
        if (isComplex) {
            for (int k = 0; k < sizeZ; k++) {
                double[] tmp = new double[width*height];
                for (int j = 0; j < height; j++) {
                    for (int i = 0; i < width; i++) {
                        tmp[i+j*width] = array[2*i+2*j*width+2*k*height*width];
                    }
                }
                out.add(new IcyBufferedImage(width, height, tmp));
            }
        }else{
            for (int j = 0; j < sizeZ; j++) {
                double[] tmp = new double[width*height];
                for (int i = 0; i < width*height; i++) {
                    tmp[i] = array[i+j*height*width];
                }
                out.add(new IcyBufferedImage(width, height, tmp));
            }
        }
        //IMPORTANT WE DEPAD AS WE COMPUTE OR NOT ...
        //return CommonUtils.imageUnPad(out, sizePadding);
        return out;
    }

    /********************************** FFT PART **********************************/

    private void scale(double[] array){
        double scale = 1.0/(width*height);
        for (int i = 0; i < array.length; i++) {
            array[i]*=scale;
        }
    }

    private void scale(float[] array){
        double scale = 1.0/(width*height);
        for (int i = 0; i < array.length; i++) {
            array[i]*=scale;
        }
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
    
    public void FFT1DComplex(double[] array) {
        if(fft1D == null){
            fft1D = new DoubleFFT_1D(width*height);
        }
        fft1D.complexForward(array);
    }

    public void FFT3D(double[] array) {
        if(fft3D == null){
            fft3D = new DoubleFFT_3D(sizeZ,height,width);
        }
        fft3D.realForwardFull(array);
    }

    public void FFT3DComplex(double[] array) {
        if(fft3D == null){
            fft3D = new DoubleFFT_3D(sizeZ,height,width);
        }
        fft3D.complexForward(array);
    }

    public void IFFT3D(double[] array) {
        fft3D.complexInverse(array, true);
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
    public void FFT1D(ShapedVector vector) {
        if (single) {
            FloatShapedVector vectorFloat = (FloatShapedVector)vector;
            float[] array = vectorFloat.getData();
            int size = imageSpace.getNumber();
            if(fft1DFloat == null){
                fft1DFloat = new FloatFFT_1D(size);
            }
            fft1DFloat.realForwardFull(array);
        } else {
            DoubleShapedVector vectorDouble = (DoubleShapedVector)vector;
            double[] array = vectorDouble.getData();
            int size = imageSpace.getNumber();
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
    public void IFFT1D(ShapedVector vector) {
        if (single) {
            FloatShapedVector vectorFloat = (FloatShapedVector)vector;
            float[] array = vectorFloat.getData();
            fft1DFloat.complexInverse(array, true);
        } else {
            DoubleShapedVector vectorDouble = (DoubleShapedVector)vector;
            double[] array = vectorDouble.getData();
            fft1D.complexInverse(array, true);
        }
    }

    /********************************** PSF PADDING **********************************/

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
