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

import mitiv.array.Double2D;
import mitiv.array.Double3D;
import mitiv.array.DoubleArray;
import mitiv.array.ShapedArray;
import mitiv.base.indexing.Range;
import mitiv.invpb.LinearDeconvolver;
import mitiv.io.BufferedImageUtils;
import mitiv.linalg.LinearConjugateGradient;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.RealComplexFFT;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.utils.CommonUtils;

/**
 * @author Leger Jonathan
 *
 */
public class Deconvolution{
    /**
     * Compute all the operations with 1D arrays
     */
    public static final int PROCESSING_1D = 1;

    /**
     * Compute all the operations with 3D arrays
     */
    public static final int PROCESSING_3D = 3;
    /**
     * Compute all the operations with Vectors i.e 1D vector
     */
    public static final int PROCESSING_VECTOR = 2;

    private final int standardProcessing = PROCESSING_1D;

    boolean verbose = false;

    DeconvUtils utils;
    Filter wiener;
    double[] image1D;
    double[] psf1D;
    DoubleShapedVector vectorImage;
    DoubleShapedVector vectorPsf;
    int correction;
    boolean isPsfSplitted = false;
    boolean useVectors;

    //CG needs
    DoubleShapedVectorSpace space;
    DoubleShapedVectorSpace complexSpace;
    DoubleShapedVector x;
    DoubleShapedVector w;
    RealComplexFFT fft;
    LinearDeconvolver linDeconv;
    int outputValue = LinearConjugateGradient.CONVERGED;
    int maxIter = 20;
    double coef = 1.0;

    static boolean forceVectorUsage = false;

    /**
     * Initial constructor that take the image and the PSF as parameters
     * <br>
     * More options: another correction and use vectors
     * 
     * @param image can be path, bufferedImage or IcyBufferedImage
     * @param PSF can be path, bufferedImage or IcyBufferedImage
     */
    public Deconvolution(Object image, Object PSF){
        this(image,PSF,CommonUtils.SCALE,forceVectorUsage);
    }


    /**
     * Initial constructor that take the image and the PSF as parameters
     * <br>
     * More options: another correction and use vectors
     * 
     * @param image can be path, bufferedImage or IcyBufferedImage
     * @param PSF can be path, bufferedImage or IcyBufferedImage
     * @param correction see static {@link CommonUtils}
     */
    public Deconvolution(Object image, Object PSF, int correction){
        this(image,PSF,correction,forceVectorUsage);
    }

    /**
     * Initial constructor that take the image and the PSF as parameters
     * <br>
     * More options: another correction and use vectors
     * 
     * @param image can be path, bufferedImage or IcyBufferedImage
     * @param PSF can be path, bufferedImage or IcyBufferedImage
     * @param correction see static {@link CommonUtils}
     * @param useVectors
     */
    public Deconvolution(Object image, Object PSF, int correction, boolean useVectors){
        utils = new DeconvUtils();
        this.useVectors = useVectors;
        if(image instanceof String){
            if (useVectors) {
                utils.readImageVect((String)image, (String)PSF, false);
            } else {
                utils.readImage((String)image, (String)PSF);
            }
        }else if(image instanceof BufferedImage){
            if (useVectors) {
                utils.readImageVect((BufferedImage)image, (BufferedImage)PSF, false);
            } else {
                utils.readImage((BufferedImage)image, (BufferedImage)PSF);
            }
        }else if(image instanceof ShapedArray){
            //we pad the image in any case
            utils.readImage((ShapedArray) image, (ShapedArray)PSF);

        }else{
            throw new IllegalArgumentException("Input should be a ShappedArray, BufferedImage or a path");
        }
        this.correction = correction;
        wiener = new Filter();
    }
    
    public void setPaddingCoefficient(double coef){
        this.coef = coef;
    }

    /**
     * Simple filter based on the wiener filter.
     * This should be used for the first time
     * <br>
     * Options: job
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    public ShapedArray firstDeconvolution(double alpha){
        if (useVectors) {
            return firstDeconvolution(alpha, PROCESSING_VECTOR, false);
        } else {
            return firstDeconvolution(alpha, standardProcessing, false);
        }
    }
    

    /**
     * Simple filter based on the wiener filter.
     * This should be used for the first time
     * <br>
     * Options: job
     * 
     * @param alpha
     * @param isPsfSplitted IF the psf is centered or not
     * @return The bufferedImage for the input value given
     */
    public ShapedArray firstDeconvolution(double alpha , boolean isPsfSplitted){
        if (useVectors) {
            return firstDeconvolution(alpha, PROCESSING_VECTOR, isPsfSplitted);
        } else {
            return firstDeconvolution(alpha, standardProcessing, isPsfSplitted);
        }
    }

    /**
     * Simple filter based on the wiener filter.
     * This should be used for the first time
     * 
     * @param alpha
     * @param job see static PROCESSING_?
     * @param isPsfSplitted isPsfSplitted If the psf is centered or not
     * @return The bufferedImage for the input value given
     */
    public ShapedArray firstDeconvolution(double alpha, int job, boolean isPsfSplitted){
        this.isPsfSplitted = isPsfSplitted;
        switch (job) {
        case PROCESSING_1D:
            return firstDeconvolutionSimple1D(alpha);
        case PROCESSING_VECTOR:
            return firstDeconvolutionVector(alpha);
        case PROCESSING_3D:
            return firstDeconvolutionSimple3D(alpha);
        default:
            throw new IllegalArgumentException("The job given does not exist");
        }
    }

    /**
     * Simple filter based on the wiener filter.
     * This should be used for the first time.
     * <br>
     * option: job
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    public ShapedArray nextDeconvolution(double alpha){
        if (useVectors) {
            return nextDeconvolution(alpha, PROCESSING_VECTOR);
        } else {
            return nextDeconvolution(alpha, standardProcessing);
        }
    }

    /**
     * Simple filter based on the wiener filter.
     * This should be used for the first time.
     * 
     * @param alpha
     * @param job see static PROCESSING_?
     * @return The bufferedImage for the input value given
     */
    public ShapedArray nextDeconvolution(double alpha, int job){
        switch (job) {
        case PROCESSING_1D:
            return nextDeconvolutionSimple1D(alpha);
        case PROCESSING_VECTOR:
            return nextDeconvolutionVector(alpha);
        case PROCESSING_3D:
            return nextDeconvolutionSimple3D(alpha);
        default:
            throw new IllegalArgumentException("The job given does not exist");
        }
    }

    /**
     * First deconvolution for the wiener filter
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ShapedArray firstDeconvolutionSimple1D(double alpha){
        image1D = utils.imageToArray1D(true);
        if (isPsfSplitted) {
            psf1D = utils.psfToArray1D(true);
        } else {
            psf1D = utils.psfPadding1D(true);
        }
        utils.FFT1D(image1D);
        utils.FFT1D(psf1D);
        double[] out = wiener.wiener1D(alpha, psf1D, image1D,utils.width,utils.height);
        utils.IFFT1D(out);

        Double2D outArray =  Double2D.wrap(out, utils.width*2, utils.height);
        return outArray.view(new Range(0,-1,2), null);
        
        /*for (int i = 0; i < utils.width*utils.height; i++) {
            out[i] = out[2*i];
        }
        return Double2D.wrap(out, utils.width, utils.height);*/
        //return utils.arrayToImage1D(out, correction, true);
    }

    /**
     * Will compute less than firstDeconvolution: 1FTT inverse instead
     * of 2FFT + 1 inverse FFT
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ShapedArray nextDeconvolutionSimple1D(double alpha){
        double[] out = wiener.wiener1D(alpha);
        utils.IFFT1D(out);
        /*for (int i = 0; i < utils.width*utils.height; i++) {
            out[i] = out[2*i];
        }
        return Double2D.wrap(out, utils.width, utils.height)*/;
        Double2D outArray =  Double2D.wrap(out, utils.width*2, utils.height);
        return outArray.view(new Range(0,-1,2), null);
        //return(utils.arrayToImage1D(out, correction,true));
    }

    /**
     * First deconvolution for the wiener filter
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ShapedArray firstDeconvolutionSimple3D(double alpha){
        ShapedArray imgArray = utils.getImgShaped();
        ShapedArray psfArray = utils.getPsfShaped();
        ShapedArray weightArray = ((DoubleArray) psfArray).create();
        
        ((DoubleArray)weightArray).fill(1.0);
        
        imgArray = BufferedImageUtils.imagePad(imgArray, coef);
        psfArray = BufferedImageUtils.imagePad(psfArray, coef);
        weightArray = BufferedImageUtils.imagePad(weightArray, coef);
        psfArray = BufferedImageUtils.shiftPsf(psfArray);
        
        utils.PadImageAndPSF(coef);//Multiply utils.{width,height,sizeZ} by coef
        
        space = new DoubleShapedVectorSpace(utils.width, utils.height, utils.sizeZ);
        fft = new RealComplexFFT(space);
        complexSpace = (DoubleShapedVectorSpace) fft.getOutputSpace();
        vectorPsf = space.wrap(psfArray.toDouble().flatten());
        vectorImage = space.wrap(imgArray.toDouble().flatten());
        DoubleShapedVector vectorWgt = space.wrap(weightArray.toDouble().flatten());
        DoubleShapedVector imgComplex = complexSpace.create(0);
        DoubleShapedVector psfComplex = complexSpace.create(0);
        DoubleShapedVector wgtComplex = complexSpace.create(0);
        fft.apply(wgtComplex, vectorWgt);
        fft.apply(psfComplex, vectorPsf);
        fft.apply(imgComplex, vectorImage);
        vectorPsf = psfComplex;
        vectorImage = imgComplex;
        DoubleShapedVector out = complexSpace.wrap(wiener.wiener3D(alpha, psfComplex.getData(), imgComplex.getData() , wgtComplex.getData() ,utils.width,utils.height, utils.sizeZ, coef));
        //DoubleShapedVector out = complexSpace.clone(imgComplex);
        DoubleShapedVector outReal = space.create();
        fft.apply(outReal, out, RealComplexFFT.ADJOINT);
        
        return Double3D.wrap(outReal.getData(), space.getShape());
        //return utils.arrayToIcyImage3D(outReal.getData(), correction,false);
        /*
         * GOOD OLD WAY
         */
        
        /*
        double[] image = utils.image3DToArray1D(false);
        double[] psf = utils.psf3DToArray1Dexp(false);
        image = CommonUtils.imagePad(image, utils.width, utils.height, utils.sizeZ, coef);
        psf = CommonUtils.imagePad(psf, utils.width, utils.height, utils.sizeZ, coef);
        
        utils.PadImageAndPSF(2.0);
        
        double[] psfShift = new double[utils.width*utils.height*utils.sizeZ];
        //CommonUtils.fftShift3D(psf,psfShift, utils.width, utils.height, utils.sizeZ);
        CommonUtils.psf3DPadding1D(psfShift, psf , utils.width, utils.height, utils.sizeZ);
        
        space = new DoubleShapedVectorSpace(utils.width, utils.height, utils.sizeZ);
        fft = new RealComplexFFT(space);
        complexSpace = (DoubleShapedVectorSpace) fft.getOutputSpace();
        
        //vector_psf = space.wrap(utils.shiftPsf3DToArray1D(false));
        //vector_image = space.wrap(utils.image3DToArray1D(false));
        vector_psf = space.wrap(psfShift);
        vector_image = space.wrap(image);

        DoubleShapedVector imgComplex = complexSpace.create(0);
        DoubleShapedVector psfComplex = complexSpace.create(0);
        fft.apply(vector_psf, psfComplex);
        fft.apply(vector_image, imgComplex);
        vector_psf = psfComplex;
        vector_image = imgComplex;
        DoubleShapedVector out = complexSpace.wrap(wiener.wiener3D(alpha, psfComplex.getData(), imgComplex.getData(),utils.width,utils.height, utils.sizeZ));
        DoubleShapedVector outReal = space.create();
        fft.apply(out, outReal,RealComplexFFT.ADJOINT);
        return utils.arrayToIcyImage3D(outReal.getData(), correction,false);
        */
    }

    /**
     * Will compute less than firstDeconvolution: 1FTT inverse instead
     * of 2FFT + 1 inverse FFT
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ShapedArray nextDeconvolutionSimple3D(double alpha){
        DoubleShapedVector out = complexSpace.wrap(wiener.wiener3D(alpha));
        //DoubleShapedVector out = complexSpace.clone(vector_image);
        DoubleShapedVector outReal = space.create();
        fft.apply(outReal, out, RealComplexFFT.ADJOINT);
        return Double3D.wrap(outReal.getData(), space.getShape());
        //return utils.arrayToIcyImage3D(outReal.getData(), correction,false);
    }

    private ShapedArray firstDeconvolutionVector(double alpha){
        vectorImage = (DoubleShapedVector) utils.cloneImageVect();
        vectorPsf = (DoubleShapedVector) utils.getPsfPadVect();
        //TODO add getPsfVect need change on opening of the image
        utils.FFT1D(vectorImage);
        utils.FFT1D(vectorPsf);
        ShapedVector out = wiener.wienerVect(alpha, vectorPsf, vectorImage);
        utils.IFFT1D(out);
        return Double2D.wrap(((DoubleShapedVector)out).getData(), utils.width, utils.height);
        //return utils.arrayToImage(out, correction,true);
    }

    private ShapedArray nextDeconvolutionVector(double alpha){
        ShapedVector out = wiener.wienerVect(alpha);
        utils.IFFT1D(out);
        return Double2D.wrap(((DoubleShapedVector)out).getData(), utils.width, utils.height);
        //return(utils.arrayToImage(out, correction, true));
    }

    /**
     * Use the quadratic approximation with circulant approximation
     * This should be used for the first time
     * <br>
     * option: job
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    public ShapedArray firstDeconvolutionQuad(double alpha){
        if (useVectors) {
            return firstDeconvolutionQuad(alpha, PROCESSING_VECTOR, false);
        } else {
            return firstDeconvolutionQuad(alpha, standardProcessing, false);
        }
    }

    /**
     * @param alpha
     * @param isPsfSplitted If the psf is centered or not
     * @return The bufferedImage for the input value given
     */
    public ShapedArray firstDeconvolutionQuad(double alpha, boolean isPsfSplitted){
        if (useVectors) {
            return firstDeconvolutionQuad(alpha, PROCESSING_VECTOR, isPsfSplitted);
        } else {
            return firstDeconvolutionQuad(alpha, standardProcessing, isPsfSplitted);
        }
    }

    /**
     * Use the quadratic approximation with circulant approximation
     * This should be used for the first time
     * 
     * @param alpha
     * @param job see static PROCESSING_?
     * @param isPsfSplitted isPsfSplitted If the psf is centered or not
     * @return The bufferedImage for the input value given
     */
    public ShapedArray firstDeconvolutionQuad(double alpha, int job, boolean isPsfSplitted){
        this.isPsfSplitted = isPsfSplitted;
        switch (job) {
        case PROCESSING_1D:
            return firstDeconvolutionQuad1D(alpha);
        case PROCESSING_3D:
            return firstDeconvolutionQuad3D(alpha);
        case PROCESSING_VECTOR:
            return firstDeconvolutionQuadVector(alpha);
        default:
            throw new IllegalArgumentException("The job given does not exist");
        }
    }

    /**
     * Use the quadratic approximation with circulant approximation
     * After the initialization use this function
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    public ShapedArray nextDeconvolutionQuad(double alpha){
        if (useVectors) {
            return nextDeconvolutionQuad(alpha, PROCESSING_VECTOR);
        } else {
            return nextDeconvolutionQuad(alpha, standardProcessing);
        }
    }

    /**
     * Use the quadratic approximation with circulant approximation
     * After the initialization use this function
     * 
     * @param alpha
     * @param job see static PROCESSING_?
     * @return The bufferedImage for the input value given
     */
    public ShapedArray nextDeconvolutionQuad(double alpha, int job){
        switch (job) {
        case PROCESSING_1D:
            return nextDeconvolutionQuad1D(alpha);
        case PROCESSING_3D:
            return nextDeconvolutionQuad3D(alpha);
        case PROCESSING_VECTOR:
            return nextDeconvolutionQuadVector(alpha);
        default:
            throw new IllegalArgumentException("The job given does not exist");
        }
    }

    /**
     * First deconvolution with quadratic option and use in internal only 1D arrays
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ShapedArray firstDeconvolutionQuad1D(double alpha){
        image1D = utils.imageToArray1D(true);
        if (isPsfSplitted) {
            psf1D = utils.psfToArray1D(true);
        } else {
            psf1D = utils.psfPadding1D(true);
        }
        utils.FFT1D(image1D);
        utils.FFT1D(psf1D);
        double[] out = wiener.wienerQuad1D(alpha, psf1D, image1D, utils.width, utils.height);
        utils.IFFT1D(out);
        Double2D outArray =  Double2D.wrap(out, utils.width*2, utils.height);
        return outArray.view(new Range(0,-1,2), null);
        //return Double2D.wrap(out, utils.width, utils.height);
        //return(utils.arrayToImage1D(out, correction,true));
    }

    /**
     * Will compute less than firstDeconvolutionQuad: 1FTT inverse instead
     * of 2FFT + 1 inverse FFT, with 1D arrays optimization
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ShapedArray nextDeconvolutionQuad1D(double alpha){
        double[] out = wiener.wienerQuad1D(alpha);
        utils.IFFT1D(out);
        Double2D outArray =  Double2D.wrap(out, utils.width*2, utils.height);
        return outArray.view(new Range(0,-1,2), null);
        //return Double2D.wrap(out, utils.width, utils.height);
        //return(utils.arrayToImage1D(out, correction, true));
    }

    /**
     * First deconvolution with quadratic option and use in internal only 1D arrays
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ShapedArray firstDeconvolutionQuad3D(double alpha){
        space = new DoubleShapedVectorSpace(utils.width, utils.height, utils.sizeZ);
        fft = new RealComplexFFT(space);
        complexSpace = (DoubleShapedVectorSpace) fft.getOutputSpace();

        vectorPsf = space.wrap(utils.shiftPsf3DToArray1D(false));
        vectorImage = space.wrap(utils.image3DToArray1D(false));
        
        DoubleShapedVector imgComplex = complexSpace.create(0);
        DoubleShapedVector psfComplex = complexSpace.create(0);

        fft.apply(psfComplex, vectorPsf);
        fft.apply(imgComplex, vectorImage);
        vectorPsf = psfComplex;
        vectorImage = imgComplex;

        DoubleShapedVector out = complexSpace.wrap(wiener.wienerQuad3D(alpha, psfComplex.getData(), imgComplex.getData(),utils.width,utils.height, utils.sizeZ,utils.sizePadding));
        DoubleShapedVector outReal = space.create();
        fft.apply(outReal, out,RealComplexFFT.ADJOINT);
        
        return Double3D.wrap(outReal.getData(), space.getShape());
        //return utils.arrayToIcyImage3D(outReal.getData(), correction,false);
    }

    /**
     * Will compute less than firstDeconvolutionQuad: 1FTT inverse instead
     * of 2FFT + 1 inverse FFT, with 1D arrays optimization
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ShapedArray nextDeconvolutionQuad3D(double alpha){
        DoubleShapedVector out = complexSpace.wrap(wiener.wienerQuad3D(alpha));
        DoubleShapedVector outReal = space.create();
        fft.apply(outReal, out,RealComplexFFT.ADJOINT);
        return Double3D.wrap(outReal.getData(), space.getShape());
        //return utils.arrayToIcyImage3D(outReal.getData(), correction,false);
    }

    private ShapedArray firstDeconvolutionQuadVector(double alpha){
        vectorImage = (DoubleShapedVector) utils.cloneImageVect();
        vectorPsf = (DoubleShapedVector) utils.getPsfPadVect();
        utils.FFT1D(vectorImage);
        utils.FFT1D(vectorPsf);
        ShapedVector out = wiener.wienerQuadVect(alpha, vectorPsf, vectorImage);
        utils.IFFT1D(out);
        return Double2D.wrap(((DoubleShapedVector)out).getData(), space.getShape());
        //return(utils.arrayToImage(out, correction,true));
    }

    private ShapedArray nextDeconvolutionQuadVector(double alpha){
        ShapedVector out = wiener.wienerQuadVect(alpha);
        utils.IFFT1D(out);
        return Double2D.wrap(((DoubleShapedVector)out).getData(), space.getShape());
        //return(utils.arrayToImage(out, correction,true));
    }

    private void parseOuputCG(int output){
        //If it does not end normally
        if ( output != LinearConjugateGradient.CONVERGED && output != LinearConjugateGradient.IN_PROGRESS) {
            if (output == LinearConjugateGradient.A_IS_NOT_POSITIVE_DEFINITE) {
                System.err.println("A_IS_NOT_POSITIVE_DEFINITE");
            }else if (output == LinearConjugateGradient.TOO_MANY_ITERATIONS && verbose) {
                System.err.println("TOO_MANY_ITERATIONS");
            }else if (verbose){
                System.err.println("Not ended normally "+output);
            }
        }
    }

    /**
     * Use the conjugate gradients to deconvolve the image
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    public ShapedArray firstDeconvolutionCG(double alpha){
        return firstDeconvolutionCG(alpha, PROCESSING_VECTOR, false);
    }

    /**
     * Use the conjugate gradients to deconvolve the image
     * 
     * @param alpha
     * @param isPsfSplitted If the psf is centered or not
     * @return The bufferedImage for the input value given
     */
    public ShapedArray firstDeconvolutionCG(double alpha, boolean isPsfSplitted){
        return firstDeconvolutionCG(alpha, PROCESSING_VECTOR, isPsfSplitted);
    }

    /**
     * Use the conjugate gradients to deconvolve the image
     * 
     * @param alpha
     * @param job see static PROCESSING_?
     * @param isPsfSplitted If the psf is centered or not
     * @return The bufferedImage for the input value given
     */
    public ShapedArray firstDeconvolutionCG(double alpha, int job, boolean isPsfSplitted){
        this.isPsfSplitted = isPsfSplitted;
        switch (job) {
        case PROCESSING_VECTOR:
            return firstDeconvolutionCGNormal(alpha);
        case PROCESSING_3D:
            return firstDeconvolutionCG3D(alpha);
        default:
            throw new IllegalArgumentException("The job given does not exist");
        }
    }

    /**
     * Use the conjugate gradients to deconvolve the image.<br>
     * Do less computations and allocations.
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    public ShapedArray nextDeconvolutionCG(double alpha){
        return nextDeconvolutionCG(alpha, PROCESSING_VECTOR);
    }

    /**
     * Use the conjugate gradients to deconvolve the image.<br>
     * Do less computations and allocations.
     * 
     * @param alpha
     * @param job see static PROCESSING_?
     * @return The bufferedImage for the input value given
     */
    public ShapedArray nextDeconvolutionCG(double alpha, int job){
        switch (job) {
        case PROCESSING_VECTOR:
            return nextDeconvolutionCGNormal(alpha);
        case PROCESSING_3D:
            return nextDeconvolutionCG3D(alpha);
        default:
            throw new IllegalArgumentException("The job given does not exist");
        }
    }

    /**
     * Use the conjugate gradients to deconvolve the image
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ShapedArray firstDeconvolutionCGNormal(double alpha){
        space = new DoubleShapedVectorSpace(utils.width, utils.height);
        if (isPsfSplitted) {
            vectorPsf = space.wrap(utils.psfToArray1D(false));
        } else {
            vectorPsf = space.wrap(utils.psfPadding1D(false));
        }
        vectorImage = space.wrap(utils.imageToArray1D(false));
        /*//We should use this one BUT as there is only CG with vectors, then in the case of no vectors, there is no imageVect
        In others words, if useVectors is False, getImageVect return null.
        if (vector_image == null) {
            vector_image = (DoubleShapedVector) utils.getImageVect();
        }
        if (vector_psf == null) {
            vector_psf = (DoubleShapedVector) utils.getPsfPad();
        }
         */

        x = space.create(0);
        w = space.create(1);

        linDeconv = new LinearDeconvolver(
                space.getShape(), vectorImage.getData(), vectorPsf.getData(), w.getData(), alpha);
        outputValue = linDeconv.solve(x.getData(), maxIter, false);
        parseOuputCG(outputValue); //print nothing if good, print in err else
        return Double2D.wrap(x.getData(), space.getShape());
        //return utils.arrayToImage1D(x.getData(), correction, false);
    }

    /**
     * Use the conjugate gradients to deconvolve the image.<br>
     * Do less computations and allocations.
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ShapedArray nextDeconvolutionCGNormal(double alpha){
        boolean verbose = false;
        x = space.create(0);
        linDeconv.setMu(alpha);
        outputValue = linDeconv.solve(x.getData(), maxIter, false);
        if (verbose) {
            parseOuputCG(outputValue); //print nothing if good, print in err else
        }
        return Double2D.wrap(x.getData(), space.getShape());
        //return utils.arrayToImage1D(x.getData(), correction, false);
    }

    private ShapedArray firstDeconvolutionCG3D(double alpha){
        ShapedArray imgArray = utils.getImgShaped();
        ShapedArray psfArray = utils.getPsfShaped();
        double[] weight = new double[utils.width*utils.height*utils.sizeZ];
        for (int i = 0; i < weight.length; i++) {
            weight[i] = 1;
        }
        weight = CommonUtils.imagePad(weight, utils.width, utils.height, utils.sizeZ, coef);
        imgArray = BufferedImageUtils.imagePad(imgArray, coef);
        psfArray = BufferedImageUtils.imagePad(psfArray, coef);
        psfArray = BufferedImageUtils.shiftPsf(psfArray);
        
        utils.PadImageAndPSF(coef);
        
        space = new DoubleShapedVectorSpace(utils.width, utils.height, utils.sizeZ);

        vectorPsf = space.wrap(psfArray.toDouble().flatten());
        vectorImage = space.wrap(imgArray.toDouble().flatten());
        
        x = space.create(0);

        w = space.wrap(weight);

        maxIter = 50;
        linDeconv = new LinearDeconvolver(
                space.getShape(), vectorImage.getData(), vectorPsf.getData(), w.getData(), alpha);
        outputValue = linDeconv.solve(x.getData(), maxIter, false);
        parseOuputCG(outputValue); //print nothing if good, print in err else
        return Double3D.wrap(x.getData(), space.getShape());
        //return utils.arrayToIcyImage3D(x.getData(), correction,false);

        /*
         * Good OLD WAY
         */
        /*utils.PadImageAndPSF();
        space = new DoubleShapedVectorSpace(utils.width, utils.height,utils.sizeZ);

        vector_psf = space.wrap(utils.shiftPsf3DToArray1D(false));
        vector_image = space.wrap(utils.image3DToArray1D(false));

        x = space.create(0);
        //WeightGenerator weightGen = new WeightGenerator();
        //Creation of a weight map
        w = space.create(1);

        //End of weight map generation

        maxIter = 50;
        linDeconv = new LinearDeconvolver(
                space.getShape(), vector_image.getData(), vector_psf.getData(), w.getData(), alpha);
        outputValue = linDeconv.solve(x.getData(), maxIter, false);
        parseOuputCG(outputValue); //print nothing if good, print in err else
        return (utils.arrayToIcyImage3D(x.getData(), correction,false));*/
    }

    private ShapedArray nextDeconvolutionCG3D(double alpha){
        boolean verbose = false;
        x = space.create(0);
        linDeconv.setMu(alpha);
        outputValue = linDeconv.solve(x.getData(), maxIter, false);
        if (verbose) {
            parseOuputCG(outputValue); //print nothing if good, print in err else
        }
        return Double3D.wrap(x.getData(), space.getShape());
        //return utils.arrayToIcyImage3D(x.getData(), correction,false);
    }

    /**
     * @return The value of the result of the computation of the gradients
     */
    public int getOuputValue(){
        return outputValue;
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
