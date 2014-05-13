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

/**
 * 
 * @author Leger Jonathan
 *
 */

public interface DeconvUtilsInterface {

	/**
	 * Will open the image
	 * 
	 * @param pathImage
	 * @param pathPSF
	 */
	public void ReadImage(String pathImage, String pathPSF);			//DONE
	
	/**
     * Will open the image
     * 
     * @param pathImage
     * @param pathPSF
     */
	public void ReadImage(BufferedImage image, BufferedImage PSF);		//DONE
	
	/**
     * Will open the image
     * 
     * @param pathImage
     * @param pathPSF
     */
	public void ReadImage(IcyBufferedImage image, IcyBufferedImage PSF);//DONE
	
	/**
	 * Will return an image that is the PSF padded to fit the image size and 
	 * correspond to a fft_shift. The output is grey value
	 * 
	 * @param isComplex If true return a array of size width*2*height
	 * @return
	 */
	public double[][] PSF_Padding(boolean isComplex);		//DONE

	/**
	 * Will return the image with grey double value
	 * 
	 * @param isComplex If true return a array of size width*2*height
	 * @return
	 */
	public double[][] ImageToArray(boolean isComplex);		//DONE							

	/**
	 * Convert double arrays to a bufferedImage and make a min/max on the value 
	 * to scale them
	 * 
	 * @param array
	 * @return
	 */
	public BufferedImage ArrayToImageWithScale(double[][] array);		//DONE

	/**
	 * Make a FFT an a array using JTranforms
	 * 
	 * @param array
	 */
	public void FFT(double[][] array);		//DONE
	
	/**
     * Make an inverse FFT an a array using JTranforms
     * 
     * @param array
     */
	public void IFFT(double[][] array);		//DONE

	/**
	 * Debug function
	 * 
	 * @param tab
	 */
	public void printTab(double[][]tab);	//DONE
	
	//1D equivalent, means take one dimension array
    /**
     * Make a 1d FFT an a array using JTranforms
     * 
     * @param array
     */
	public void FFT1D(double[] array);
	
	/**
     * Make an inverse 1d FFT an a array using JTranforms
     * 
     * @param array
     */
	public void IFFT1D(double[] array);		
	
	/**
	 * Debug function
	 * 
	 * @param tab
	 * @param isComplex If true return a array of size width*2*height
	 */
	public void printTab1D(double[]tab, boolean isComplex);	
	
	/**
	 * Convert double arrays to a bufferedImage and make a min/max on the value
	 * 
	 * @param array
	 * @param isComplex If true return a array of size width*2*height
	 * @return
	 */
	public BufferedImage ArrayToImageWithScale1D(double[] array, boolean isComplex);	
	
	/**
	 *  Convert double arrays to a bufferedImage and make a min/max on the value 
     * to scale them
     * 
	 * @param isComplex
	 * @return
	 */
	public double[] ImageToArray1D(boolean isComplex);	
	
	/**
	 * Will return an image that is the PSF padded to fit the image size and 
     * correspond to a fft_shift. The output is grey value
	 * 
	 * @param isComplex
	 * @return
	 */
	public double[] PSF_Padding1D(boolean isComplex);

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
