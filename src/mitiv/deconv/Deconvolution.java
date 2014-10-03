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
import java.util.ArrayList;
import mitiv.invpb.LinearDeconvolver;
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
    DoubleShapedVector vector_image;
    DoubleShapedVector vector_psf;
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
        }else if(image instanceof ArrayList){
            //we pad the image in any case
            utils.readImage((ArrayList<BufferedImage>)image, (ArrayList<BufferedImage>)PSF);

        }else{
            throw new IllegalArgumentException("Input should be a IcyBufferedImage, BufferedImage or a path");
        }
        this.correction = correction;
        wiener = new Filter();
    }

    private ArrayList<BufferedImage> list(BufferedImage im){
        ArrayList<BufferedImage> tmp = new ArrayList<BufferedImage>();
        tmp.add(im);
        return tmp;
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
    public ArrayList<BufferedImage> firstDeconvolution(double alpha){
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
    public ArrayList<BufferedImage> firstDeconvolution(double alpha , boolean isPsfSplitted){
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
    public ArrayList<BufferedImage> firstDeconvolution(double alpha, int job, boolean isPsfSplitted){
        this.isPsfSplitted = isPsfSplitted;
        switch (job) {
        case PROCESSING_1D:
            return list(firstDeconvolutionSimple1D(alpha));
        case PROCESSING_VECTOR:
            return list(firstDeconvolutionVector(alpha));
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
    public ArrayList<BufferedImage> nextDeconvolution(double alpha){
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
    public ArrayList<BufferedImage> nextDeconvolution(double alpha, int job){
        switch (job) {
        case PROCESSING_1D:
            return list(nextDeconvolutionSimple1D(alpha));
        case PROCESSING_VECTOR:
            return list(nextDeconvolutionVector(alpha));
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
    private BufferedImage firstDeconvolutionSimple1D(double alpha){
        image1D = utils.imageToArray1D(true);
        if (isPsfSplitted) {
            psf1D = utils.psfToArray1D(true);
        } else {
            psf1D = utils.psfPadding1D(true);
        }
        utils.FFT1D(image1D);
        utils.FFT1D(psf1D);
        System.out.println("3D 1D");
        double[] out = wiener.wiener1D(alpha, psf1D, image1D,utils.width,utils.height);
        utils.IFFT1D(out);
        return(utils.arrayToImage1D(out, correction, true));
    }

    /**
     * Will compute less than firstDeconvolution: 1FTT inverse instead
     * of 2FFT + 1 inverse FFT
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private BufferedImage nextDeconvolutionSimple1D(double alpha){
        double[] out = wiener.wiener3D(alpha);
        utils.IFFT1D(out);
        return(utils.arrayToImage1D(out, correction,true));
    }

    /**
     * First deconvolution for the wiener filter
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ArrayList<BufferedImage> firstDeconvolutionSimple3D(double alpha){
        space = new DoubleShapedVectorSpace(utils.width, utils.height,utils.sizeZ);
        fft = new RealComplexFFT(space);
        complexSpace = (DoubleShapedVectorSpace) fft.getOutputSpace();

        //vector_psf = space.wrap(utils.shiftPsf3DToArray1D(false));
        //vector_image = space.wrap(utils.image3DToArray1D(false));

        vector_image = space.wrap(CommonUtils.image3DToArray1D(utils.listImage, utils.width, utils.height, utils.sizeZ, false));
        vector_psf = space.wrap(CommonUtils.shiftPsf3DToArray1D(utils.listPSF, utils.width, utils.height, utils.sizeZ, false));

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
    }

    /**
     * Will compute less than firstDeconvolution: 1FTT inverse instead
     * of 2FFT + 1 inverse FFT
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ArrayList<BufferedImage> nextDeconvolutionSimple3D(double alpha){
        DoubleShapedVector out = complexSpace.wrap(wiener.wiener3D(alpha));
        DoubleShapedVector outReal = space.create();
        fft.apply(out, outReal,RealComplexFFT.ADJOINT);
        return utils.arrayToIcyImage3D(outReal.getData(), correction,false);
    }

    private BufferedImage firstDeconvolutionVector(double alpha){
        vector_image = (DoubleShapedVector) utils.cloneImageVect();
        vector_psf = (DoubleShapedVector) utils.getPsfPadVect();
        //TODO add getPsfVect need change on opening of the image
        utils.FFT1D(vector_image);
        utils.FFT1D(vector_psf);
        ShapedVector out = wiener.wienerVect(alpha, vector_psf, vector_image);
        utils.IFFT1D(out);
        return(utils.arrayToImage(out, correction,true));
    }

    private BufferedImage nextDeconvolutionVector(double alpha){
        ShapedVector out = wiener.wienerVect(alpha);
        utils.IFFT1D(out);
        return(utils.arrayToImage(out, correction,true));
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
    public ArrayList<BufferedImage> firstDeconvolutionQuad(double alpha){
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
    public ArrayList<BufferedImage> firstDeconvolutionQuad(double alpha, boolean isPsfSplitted){
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
    public ArrayList<BufferedImage> firstDeconvolutionQuad(double alpha, int job, boolean isPsfSplitted){
        this.isPsfSplitted = isPsfSplitted;
        switch (job) {
        case PROCESSING_1D:
            return list(firstDeconvolutionQuad1D(alpha));
        case PROCESSING_3D:
            return firstDeconvolutionQuad3D(alpha);
        case PROCESSING_VECTOR:
            return list(firstDeconvolutionQuadVector(alpha));
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
    public ArrayList<BufferedImage> nextDeconvolutionQuad(double alpha){
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
    public ArrayList<BufferedImage> nextDeconvolutionQuad(double alpha, int job){
        switch (job) {
        case PROCESSING_1D:
            return list(nextDeconvolutionQuad1D(alpha));
        case PROCESSING_3D:
            return nextDeconvolutionQuad3D(alpha);
        case PROCESSING_VECTOR:
            return list(nextDeconvolutionQuadVector(alpha));
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
    private BufferedImage firstDeconvolutionQuad1D(double alpha){
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
        return(utils.arrayToImage1D(out, correction,true));
    }

    /**
     * Will compute less than firstDeconvolutionQuad: 1FTT inverse instead
     * of 2FFT + 1 inverse FFT, with 1D arrays optimization
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private BufferedImage nextDeconvolutionQuad1D(double alpha){
        double[] out = wiener.wienerQuad1D(alpha);
        utils.IFFT1D(out);
        return(utils.arrayToImage1D(out, correction, true));
    }

    /**
     * First deconvolution with quadratic option and use in internal only 1D arrays
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ArrayList<BufferedImage> firstDeconvolutionQuad3D(double alpha){
        space = new DoubleShapedVectorSpace(utils.width, utils.height,utils.sizeZ);
        fft = new RealComplexFFT(space);
        complexSpace = (DoubleShapedVectorSpace) fft.getOutputSpace();

        vector_image = space.wrap(CommonUtils.image3DToArray1D(utils.listImage, utils.width, utils.height, utils.sizeZ, false));
        vector_psf = space.wrap(CommonUtils.shiftPsf3DToArray1D(utils.listPSF, utils.width, utils.height, utils.sizeZ, false));

        DoubleShapedVector imgComplex = complexSpace.create(0);
        DoubleShapedVector psfComplex = complexSpace.create(0);

        fft.apply(vector_psf, psfComplex);
        fft.apply(vector_image, imgComplex);
        vector_psf = psfComplex;
        vector_image = imgComplex;

        DoubleShapedVector out = complexSpace.wrap(wiener.wienerQuad3D(alpha, psfComplex.getData(), imgComplex.getData(),utils.width,utils.height, utils.sizeZ,utils.sizePadding));
        DoubleShapedVector outReal = space.create();
        fft.apply(out, outReal,RealComplexFFT.ADJOINT);
        return utils.arrayToIcyImage3D(outReal.getData(), correction,false);
    }

    /**
     * Will compute less than firstDeconvolutionQuad: 1FTT inverse instead
     * of 2FFT + 1 inverse FFT, with 1D arrays optimization
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private ArrayList<BufferedImage> nextDeconvolutionQuad3D(double alpha){
        DoubleShapedVector out = complexSpace.wrap(wiener.wienerQuad3D(alpha));
        DoubleShapedVector outReal = space.create();
        fft.apply(out, outReal,RealComplexFFT.ADJOINT);
        return utils.arrayToIcyImage3D(outReal.getData(), correction,false);
    }

    private BufferedImage firstDeconvolutionQuadVector(double alpha){
        vector_image = (DoubleShapedVector) utils.cloneImageVect();
        vector_psf = (DoubleShapedVector) utils.getPsfPadVect();
        utils.FFT1D(vector_image);
        utils.FFT1D(vector_psf);
        ShapedVector out = wiener.wienerQuadVect(alpha, vector_psf, vector_image);
        utils.IFFT1D(out);
        return(utils.arrayToImage(out, correction,true));
    }

    private BufferedImage nextDeconvolutionQuadVector(double alpha){
        ShapedVector out = wiener.wienerQuadVect(alpha);
        utils.IFFT1D(out);
        return(utils.arrayToImage(out, correction,true));
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
     * Use the conjugate gradients to deconvoluate the image
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    public ArrayList<BufferedImage> firstDeconvolutionCG(double alpha){
        return firstDeconvolutionCG(alpha, PROCESSING_VECTOR, false);
    }

    /**
     * Use the conjugate gradients to deconvoluate the image
     * 
     * @param alpha
     * @param isPsfSplitted If the psf is centered or not
     * @return The bufferedImage for the input value given
     */
    public ArrayList<BufferedImage> firstDeconvolutionCG(double alpha, boolean isPsfSplitted){
        return firstDeconvolutionCG(alpha, PROCESSING_VECTOR, isPsfSplitted);
    }

    /**
     * Use the conjugate gradients to deconvoluate the image
     * 
     * @param alpha
     * @param job see static PROCESSING_?
     * @param isPsfSplitted If the psf is centered or not
     * @return The bufferedImage for the input value given
     */
    public ArrayList<BufferedImage> firstDeconvolutionCG(double alpha, int job, boolean isPsfSplitted){
        this.isPsfSplitted = isPsfSplitted;
        switch (job) {
        case PROCESSING_VECTOR:
            return list(firstDeconvolutionCGNormal(alpha));
        case PROCESSING_3D:
            return firstDeconvolutionCG3D(alpha);
        default:
            throw new IllegalArgumentException("The job given does not exist");
        }
    }

    /**
     * Use the conjugate gradients to deconvoluate the image.<br>
     * Do less computations and allocations.
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    public ArrayList<BufferedImage> nextDeconvolutionCG(double alpha){
        return nextDeconvolutionCG(alpha, PROCESSING_VECTOR);
    }

    /**
     * Use the conjugate gradients to deconvoluate the image.<br>
     * Do less computations and allocations.
     * 
     * @param alpha
     * @param job see static PROCESSING_?
     * @return The bufferedImage for the input value given
     */
    public ArrayList<BufferedImage> nextDeconvolutionCG(double alpha, int job){
        switch (job) {
        case PROCESSING_VECTOR:
            return list(nextDeconvolutionCGNormal(alpha));
        case PROCESSING_3D:
            return nextDeconvolutionCG3D(alpha);
        default:
            throw new IllegalArgumentException("The job given does not exist");
        }
    }

    /**
     * Use the conjugate gradients to deconvoluate the image
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private BufferedImage firstDeconvolutionCGNormal(double alpha){
        space = new DoubleShapedVectorSpace(utils.width, utils.height);
        if (isPsfSplitted) {
            vector_psf = space.wrap(utils.psfToArray1D(false));
        } else {
            vector_psf = space.wrap(utils.psfPadding1D(false));
        }
        vector_image = space.wrap(utils.imageToArray1D(false));
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
                space.cloneShape(), vector_image.getData(), vector_psf.getData(), w.getData(), alpha);
        outputValue = linDeconv.solve(x.getData(), maxIter, false);
        parseOuputCG(outputValue); //print nothing if good, print in err else
        return (utils.arrayToImage1D(x.getData(), correction, false));
    }

    /**
     * Use the conjugate gradients to deconvoluate the image.<br>
     * Do less computations and allocations.
     * 
     * @param alpha
     * @return The bufferedImage for the input value given
     */
    private BufferedImage nextDeconvolutionCGNormal(double alpha){
        boolean verbose = false;
        x = space.create(0);
        linDeconv.setMu(alpha);
        outputValue = linDeconv.solve(x.getData(), maxIter, false);
        if (verbose) {
            parseOuputCG(outputValue); //print nothing if good, print in err else
        }
        return(utils.arrayToImage1D(x.getData(), correction, false));
    }

    private ArrayList<BufferedImage> firstDeconvolutionCG3D(double alpha){
        space = new DoubleShapedVectorSpace(utils.width, utils.height,utils.sizeZ);

        vector_image = space.wrap(CommonUtils.image3DToArray1D(utils.listImage, utils.width, utils.height, utils.sizeZ, false));
        vector_psf = space.wrap(CommonUtils.shiftPsf3DToArray1D(utils.listPSF, utils.width, utils.height, utils.sizeZ, false));

        x = space.create(0);
        //WeightGenerator weightGen = new WeightGenerator();
        //Creation of a weight map
        w = space.create(1);
        int pad = utils.sizePadding/2;
        for (int k = 0; k < utils.sizeZ; k++) {
            for (int j = 0; j < utils.height; j++) {
                for (int i = 0; i < utils.width; i++) {
                    if ((i > pad && i < (utils.width-pad)) || (j > pad && j < (utils.height - pad))) {
                        w.getData()[i+j*utils.width+k*utils.height*utils.width] = 1;
                    }
                }
            }
        }
        //End of weight map generation

        maxIter = 50;
        linDeconv = new LinearDeconvolver(
                space.cloneShape(), vector_image.getData(), vector_psf.getData(), w.getData(), alpha);
        outputValue = linDeconv.solve(x.getData(), maxIter, false);
        parseOuputCG(outputValue); //print nothing if good, print in err else
        return (utils.arrayToIcyImage3D(x.getData(), correction,false));
    }

    private ArrayList<BufferedImage> nextDeconvolutionCG3D(double alpha){
        boolean verbose = false;
        x = space.create(0);
        linDeconv.setMu(alpha);
        outputValue = linDeconv.solve(x.getData(), maxIter, false);
        if (verbose) {
            parseOuputCG(outputValue); //print nothing if good, print in err else
        }
        return(utils.arrayToIcyImage3D(x.getData(), correction,false));
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
