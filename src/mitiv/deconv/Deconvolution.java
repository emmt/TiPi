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

import icy.image.IcyBufferedImage;

import java.awt.image.BufferedImage;

import mitiv.invpb.LinearDeconvolver;
import mitiv.linalg.DoubleVector;
import mitiv.linalg.DoubleVectorSpaceWithRank;
import mitiv.linalg.LinearConjugateGradient;
import mitiv.utils.DeconvUtils;

/**
 * @author Leger Jonathan
 *
 */
public class Deconvolution{
	DeconvUtils utils;
	Filter wiener;
	double[][] image;
	double[][] psf;
	double[] image1D;
	double[] psf1D;
	DoubleVector vector_y;
	DoubleVector vector_psf;
	int correction;
	
	//CG needs
	DoubleVectorSpaceWithRank space;
	DoubleVector x;
	LinearDeconvolver deconv;
	int outputValue;

	boolean verbose = false;
	/**
	 * Initial constructor that take the image and the PSF as parameters
	 * 
	 * @param image can be path, bufferedImage or IcyBufferedImage
	 * @param PSF can be path, bufferedImage or IcyBufferedImage
	 */
	public Deconvolution(Object image, Object PSF, int correction){
		utils = new DeconvUtils();
		wiener = new Filter();
		this.correction = correction;
		if(image instanceof String){
			utils.ReadImage((String)image, (String)PSF);
		}else if(image instanceof BufferedImage){
			utils.ReadImage((BufferedImage)image, (BufferedImage)PSF);
		}else if(image instanceof IcyBufferedImage){
			utils.ReadImage((IcyBufferedImage)image, (IcyBufferedImage)PSF);
		}
	}

	/**
	 * First deconvolution for the wiener filter
	 * 
	 * @param alpha
	 * @return deconvoluated image
	 */
	public BufferedImage FirstDeconvolution(double alpha){
		image = utils.ImageToArray(true);
		psf = utils.PSF_Padding(true);
		utils.FFT(image);
		utils.FFT(psf);
		double[][] out = wiener.Wiener(alpha, psf, image);
		utils.IFFT(out);
		outputValue = LinearConjugateGradient.CONVERGED;
		return(utils.ArrayToImage(out, correction));
	}

	/**
	 * Will compute less than firstDeconvolution: 1FTT inverse instead
	 * of 2FFT + 1 inverse FFT
	 * 
	 * @param alpha
	 * @return deconvoluated image
	 */
	public BufferedImage NextDeconvolution(double alpha){
		double[][] out = wiener.Wiener(alpha);
		utils.IFFT(out);
		outputValue = LinearConjugateGradient.CONVERGED;
		return(utils.ArrayToImage(out, correction));
	}

	/**
	 * First deconvolution with quadratic option
	 * 
	 * @param alpha
	 * @return deconvoluated image
	 */
	public BufferedImage FirstDeconvolutionQuad(double alpha){
		image = utils.ImageToArray(true);
		psf = utils.PSF_Padding(true);
		utils.FFT(image);
		utils.FFT(psf);
		double[][] out = wiener.WienerQuad(alpha, psf, image);
		utils.IFFT(out);
		outputValue = LinearConjugateGradient.CONVERGED;
		return(utils.ArrayToImage(out, correction));
	}

	/**
	 * Will compute less than firstDeconvolutionQuad: 1FTT inverse instead
	 * of 2FFT + 1 inverse FFT
	 * 
	 * @param alpha
	 * @return deconvoluated image
	 */
	public BufferedImage NextDeconvolutionQuad(double alpha){
		double[][] out = wiener.WienerQuad(alpha);
		utils.IFFT(out);
		outputValue = LinearConjugateGradient.CONVERGED;
		return(utils.ArrayToImage(out, correction));
	}

	/**
	 * First deconvolution with quadratic option and use in internal only 1D arrays
	 * 
	 * @param alpha
	 * @return deconvoluated image
	 */
	public BufferedImage FirstDeconvolutionQuad1D(double alpha){
		image1D = utils.ImageToArray1D(true);
		psf1D = utils.PSF_Padding1D(true);
		utils.FFT1D(image1D);
		utils.FFT1D(psf1D);
		double[] out = wiener.WienerQuad1D(alpha, psf1D, image1D,utils.height,utils.width);
		utils.IFFT1D(out);
		return(utils.ArrayToImage1D(out, correction,true));
	}

	/**
	 * Will compute less than firstDeconvolutionQuad: 1FTT inverse instead
	 * of 2FFT + 1 inverse FFT, with 1D arrays optimization
	 * 
	 * @param alpha
	 * @return deconvoluated image
	 */
	public BufferedImage NextDeconvolutionQuad1D(double alpha){
		double[] out = wiener.WienerQuad1D(alpha);
		utils.IFFT1D(out);
		return(utils.ArrayToImage1D(out, correction, true));
	}

	private void parseOuputCG(int output){
		//If we are on verbose mode and it does not ended normally
		if ( output != LinearConjugateGradient.CONVERGED && output != LinearConjugateGradient.IN_PROGRESS && verbose) {
			if (output == LinearConjugateGradient.A_IS_NOT_POSITIVE_DEFINITE) {
				System.err.println("A_IS_NOT_POSITIVE_DEFINITE");
			}else if (output == LinearConjugateGradient.TOO_MANY_ITERATIONS) {
				System.err.println("TOO_MANY_ITERATIONS");
			}else{
				System.err.println("Not ended normally "+output);
			}
		}
	}
	
	/**
	 * Use the conjugate gradients to deconvoluate the image
	 * 
	 * @param alpha
	 * @return deconvoluated image
	 */
	public BufferedImage FirstDeconvolutionCG(double alpha){
		space = new DoubleVectorSpaceWithRank(utils.width, utils.height);
		if (vector_psf == null) {
			vector_psf = space.wrap(utils.PSF_Padding1D(false));
		}
		if (vector_y == null) {
			vector_y = space.wrap(utils.ImageToArray1D(false));
		}

		x = space.create(0);
		DoubleVector w = space.create(1);

		deconv = new LinearDeconvolver(
				space.getShape(), vector_y.getData(), vector_psf.getData(), w.getData(), alpha);
		outputValue = deconv.solve(x.getData(), 20, false);
		parseOuputCG(outputValue);
		return(utils.ArrayToImage1D(x.getData(), correction, false));
	}

	/**
	 * @param alpha
	 * @return deconvoluated image
	 */
	public BufferedImage NextDeconvolutionCG(double alpha){
		if (outputValue != LinearConjugateGradient.IN_PROGRESS) {
			x = space.create(0);
		}
		deconv.setMu(alpha);
		outputValue = deconv.solve(x.getData(), 50, false);
		parseOuputCG(outputValue);
		return(utils.ArrayToImage1D(x.getData(), correction, false));
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
