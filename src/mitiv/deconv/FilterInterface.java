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

/**
 * @author Leger Jonathan
 *
 */
public interface FilterInterface {
    
	/**
	 * Simple filter based on the wiener filter.
	 * This should be used for the first time
	 * 
	 * @param alpha
	 * @param FFT_PSF
	 * @param FFTImage
	 * @return
	 */
	public double[][] Wiener(double alpha, double[][] FFT_PSF, double[][] FFTImage);
	
	/**
     * Simple filter based on the wiener filter.
     * After the initialization use this function
     * 
     * @param alpha
     * @param FFT_PSF
     * @param FFTImage
     * @return
     */
	public double[][] Wiener(double alpha);

    /**
     * Use the quadratic approximation with circulant approximation
     * This should be used for the first time
     * 
     * @param alpha
     * @param FFT_PSF
     * @param FFTImage
     * @return
     */	
	public double[][] WienerQuad(double alpha, double[][] FFT_PSF, double[][] FFTImage);
	
	/**
	 * Use the quadratic approximation with circulant approximation
	 * After the initialization use this function
	 * 
	 * @param alpha
	 * @return
	 */
	public double[][] WienerQuad(double alpha);

	/**
	 * 
     * Use the quadratic approximation with circulant approximation
     * This should be used for the first time and as it is 1d we have to give 
     * more informations on the data we are working on
     * 
	 * @param alpha
	 * @param FFT_PSF
	 * @param FFTImage
	 * @param width
	 * @param height
	 * @return
	 */
	public double[] WienerQuad1D(double alpha, double[] FFT_PSF, double[] FFTImage, int width, int height);
	
	/**
	 * Use the quadratic approximation with circulant approximation
     * After the initialization use this function
     * 
	 * @param alpha
	 * @return
	 */
	public double[] WienerQuad1D(double alpha);
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
