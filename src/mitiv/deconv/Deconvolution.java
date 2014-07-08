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

import mitiv.invpb.LinearDeconvolver;
import mitiv.linalg.DoubleVector;
import mitiv.linalg.DoubleVectorSpaceWithRank;
import mitiv.linalg.LinearConjugateGradient;
import mitiv.linalg.Vector;
import mitiv.utils.CommonUtils;

/**
 * @author Leger Jonathan
 *
 */
public class Deconvolution{
    public static final int PROCESSING_2D = 0;
    public static final int PROCESSING_1D = 1; 
    public static final int PROCESSING_VECTOR = 2; 

    DeconvUtils utils;
    Filter wiener;
    double[][] image;
    double[][] psf;
    double[] image1D;
    double[] psf1D;
    DoubleVector vector_image;
    DoubleVector vector_psf;
    int correction;

    //CG needs
    DoubleVectorSpaceWithRank space;
    DoubleVector x;
    LinearDeconvolver linDeconv;
    int outputValue = LinearConjugateGradient.CONVERGED;

    /**
     * Initial constructor that take the image and the PSF as parameters
     * <br>
     * More options: another correction and use vectors
     * 
     * @param image can be path, bufferedImage or IcyBufferedImage
     * @param PSF can be path, bufferedImage or IcyBufferedImage
     */
    public Deconvolution(Object image, Object PSF){
        this(image,PSF,CommonUtils.SCALE,false);
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
        this(image,PSF,correction,false);
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
        }else{
            throw new IllegalArgumentException("Input should be a IcyBufferedImage, BufferedImage or a path");
        }
        this.correction = correction;
        wiener = new Filter();
    }

    /**
     * Simple filter based on the wiener filter.
     * This should be used for the first time
     * <br>
     * Options: job
     * 
     * @param alpha
     * @param FFT_PSF
     * @param FFTImage
     * @return
     */
    public BufferedImage firstDeconvolution(double alpha){
        return firstDeconvolution(alpha, PROCESSING_1D);
    }

    /**
     * Simple filter based on the wiener filter.
     * This should be used for the first time
     * 
     * @param alpha
     * @param job see static PROCESSING_?
     * @return
     */
    public BufferedImage firstDeconvolution(double alpha, int job){
        switch (job) {
        case PROCESSING_1D:
            return firstDeconvolutionSimple(alpha);
        case PROCESSING_2D:
            return firstDeconvolutionSimple(alpha);
        case PROCESSING_VECTOR:
            return firstDeconvolutionVector(alpha);
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
     * @return
     */
    public BufferedImage nextDeconvolution(double alpha){
        return nextDeconvolution(alpha, PROCESSING_1D);
    }

    public BufferedImage nextDeconvolution(double alpha, int job){
        switch (job) {
        case PROCESSING_1D:
            return nextDeconvolutionSimple(alpha);
        case PROCESSING_2D:
            return nextDeconvolutionSimple(alpha);
        case PROCESSING_VECTOR:
            return nextDeconvolutionVector(alpha);
        default:
            throw new IllegalArgumentException("The job given does not exist");
        }
    }

    /**
     * First deconvolution for the wiener filter
     * 
     * @param alpha
     * @return deconvoluated image
     */
    private BufferedImage firstDeconvolutionSimple(double alpha){
        image = utils.imageToArray(true);
        psf = utils.psfPadding(true);
        utils.FFT(image);
        utils.FFT(psf);
        double[][] out = wiener.wiener(alpha, psf, image);
        utils.IFFT(out);
        return(utils.arrayToImage(out, correction));
    }

    /**
     * Will compute less than firstDeconvolution: 1FTT inverse instead
     * of 2FFT + 1 inverse FFT
     * 
     * @param alpha
     * @return deconvoluated image
     */
    private BufferedImage nextDeconvolutionSimple(double alpha){
        double[][] out = wiener.wiener(alpha);
        utils.IFFT(out);
        return(utils.arrayToImage(out, correction));
    }

    private BufferedImage firstDeconvolutionVector(double alpha){
        vector_image = (DoubleVector) utils.getImageVect();
        vector_psf = (DoubleVector) utils.getPsfPad();
        utils.FFT1D(vector_image);
        utils.FFT1D(vector_psf);
        Vector out = wiener.wienerVect(alpha, vector_psf, vector_image);
        utils.IFFT1D(out);
        return(utils.arrayToImage(out, correction,true));
    }

    private BufferedImage nextDeconvolutionVector(double alpha){
        Vector out = wiener.wienerVect(alpha);
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
     * @return
     */ 
    public BufferedImage firstDeconvolutionQuad(double alpha){
        return firstDeconvolutionQuad(alpha, PROCESSING_2D);
    }

    /**
     * Use the quadratic approximation with circulant approximation
     * This should be used for the first time
     * 
     * @param alpha
     * @param job see static PROCESSING_?
     * @return
     */ 
    public BufferedImage firstDeconvolutionQuad(double alpha, int job){
        switch (job) {
        case PROCESSING_1D:
            return firstDeconvolutionQuad1D(alpha);
        case PROCESSING_2D:
            return firstDeconvolutionQuadSimple(alpha);
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
     * @return
     */
    public BufferedImage nextDeconvolutionQuad(double alpha){
        return nextDeconvolutionQuad(alpha, PROCESSING_2D);
    }

    /**
     * Use the quadratic approximation with circulant approximation
     * After the initialization use this function
     * 
     * @param alpha
     * @param job see static PROCESSING_?
     * @return
     */
    public BufferedImage nextDeconvolutionQuad(double alpha, int job){
        switch (job) {
        case PROCESSING_1D:
            return nextDeconvolutionQuad1D(alpha);
        case PROCESSING_2D:
            return nextDeconvolutionQuadSimple(alpha);
        case PROCESSING_VECTOR:
            return nextDeconvolutionQuadVector(alpha);
        default:
            throw new IllegalArgumentException("The job given does not exist");
        }
    }

    /**
     * First deconvolution with quadratic option
     * 
     * @param alpha
     * @return deconvoluated image
     */
    private BufferedImage firstDeconvolutionQuadSimple(double alpha){
        image = utils.imageToArray(true);
        psf = utils.psfPadding(true);
        utils.FFT(image);
        utils.FFT(psf);
        double[][] out = wiener.wienerQuad(alpha, psf, image);
        utils.IFFT(out);
        return(utils.arrayToImage(out, correction));
    }

    /**
     * Will compute less than firstDeconvolutionQuad: 1FTT inverse instead
     * of 2FFT + 1 inverse FFT
     * 
     * @param alpha
     * @return deconvoluated image
     */
    private BufferedImage nextDeconvolutionQuadSimple(double alpha){
        double[][] out = wiener.wienerQuad(alpha);
        utils.IFFT(out);
        return(utils.arrayToImage(out, correction));
    }

    /**
     * First deconvolution with quadratic option and use in internal only 1D arrays
     * 
     * @param alpha
     * @return deconvoluated image
     */
    private BufferedImage firstDeconvolutionQuad1D(double alpha){
        image1D = utils.imageToArray1D(true);
        psf1D = utils.psfPadding1D(true);
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
     * @return deconvoluated image
     */
    private BufferedImage nextDeconvolutionQuad1D(double alpha){
        double[] out = wiener.wienerQuad1D(alpha);
        utils.IFFT1D(out);
        return(utils.arrayToImage1D(out, correction, true));
    }

    private BufferedImage firstDeconvolutionQuadVector(double alpha){
        vector_image = (DoubleVector) utils.getImageVect();
        vector_psf = (DoubleVector) utils.getPsfPad();
        utils.FFT1D(vector_image);
        utils.FFT1D(vector_psf);
        Vector out = wiener.wienerQuadVect(alpha, vector_psf, vector_image);
        utils.IFFT1D(out);
        return(utils.arrayToImage(out, correction,true));
    }

    private BufferedImage nextDeconvolutionQuadVector(double alpha){
        Vector out = wiener.wienerQuadVect(alpha);
        utils.IFFT1D(out);
        return(utils.arrayToImage(out, correction,true));
    }

    private void parseOuputCG(int output){
        //If it does not end normally
        if ( output != LinearConjugateGradient.CONVERGED && output != LinearConjugateGradient.IN_PROGRESS) {
            if (output == LinearConjugateGradient.A_IS_NOT_POSITIVE_DEFINITE) {
                System.err.println("A_IS_NOT_POSITIVE_DEFINITE");
            }else if (output == LinearConjugateGradient.TOO_MANY_ITERATIONS) {
                System.err.println("TOO_MANY_ITERATIONS");
            }else{
                System.err.println("Not ended normally "+output);
            }
        }
    }

    public BufferedImage firstDeconvolutionCG(double alpha){
        return firstDeconvolutionCG(alpha, PROCESSING_VECTOR);
    }

    public BufferedImage firstDeconvolutionCG(double alpha, int job){
        switch (job) {
        case PROCESSING_VECTOR:
            return firstDeconvolutionCGNormal(alpha);
        default:
            throw new IllegalArgumentException("The job given does not exist");
        }
    }

    public BufferedImage nextDeconvolutionCG(double alpha){
        return firstDeconvolutionCG(alpha, PROCESSING_VECTOR);
    }

    public BufferedImage nextDeconvolutionCG(double alpha, int job){
        switch (job) {
        case PROCESSING_VECTOR:
            return nextDeconvolutionCGNormal(alpha);
        default:
            throw new IllegalArgumentException("The job given does not exist");
        }
    }
    /**
     * Use the conjugate gradients to deconvoluate the image
     * 
     * @param alpha
     * @return deconvoluated image
     */
    private BufferedImage firstDeconvolutionCGNormal(double alpha){
        boolean verbose = false;
        space = new DoubleVectorSpaceWithRank(utils.width, utils.height);
        if (vector_psf == null) {
            vector_psf = space.wrap(utils.psfPadding1D(false));
        }
        if (vector_image == null) {
            vector_image = space.wrap(utils.imageToArray1D(false));
        }

        x = space.create(0);
        DoubleVector w = space.create(1);

        linDeconv = new LinearDeconvolver(
                space.cloneShape(), vector_image.getData(), vector_psf.getData(), w.getData(), alpha);
        outputValue = linDeconv.solve(x.getData(), 20, false);
        if (verbose) {
            parseOuputCG(outputValue); //print nothing if good, print in err else
        }
        return(utils.arrayToImage1D(x.getData(), correction, false));
    }

    /**
     * @param alpha
     * @return deconvoluated image
     */
    private BufferedImage nextDeconvolutionCGNormal(double alpha){
        return firstDeconvolutionCGNormal(alpha);
    }

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
