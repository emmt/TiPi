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
import mitiv.utils.DeconvUtils;
/**
 * @author Leger Jonathan
 * 
 * 
 **/
public class Main {

	/**
	 * 
	 * Debug debug and debug
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{   
        
	
		Deconvolution test = new Deconvolution("saturn2.png", "saturn_psf2.png",DeconvUtils.SCALE);
		long time1 = System.nanoTime();
		BufferedImage a =test.FirstDeconvolution(0);
		long time2 = System.nanoTime();
		ImageIO.write(a,"png",new File("TESSST.png"));
		long time3 = System.nanoTime();
		System.out.println("Time: "+(time3-time2)/1000000+" ms (écriture), Time: "+(time2-time1)/1000000+" ms (calcul)");
		
		
		//Thread.sleep(40000);
		/*
		
		System.out.println("start");
		Deconvolution test = new Deconvolution("saturn2.png", "saturn_psf2.png");
		ImageIO.write(test.FirstDeconvolutionQuad1D((float)0.5*Math.pow(10,0)),"png",new File("out0.png"));
		for(int a = 0;a<20;a++){
			int nbTest = 12;
			for(int i=1;i<nbTest;i++){

				long time1 = System.nanoTime();
				//test.NextDeconvolutionQuad((float)1*Math.pow(10,i));
				//ImageIO.write(test.NextDeconvolutionQuad1D((float)1*Math.pow(10,i)),"png",new File("out"+i+".png"));
				test.NextDeconvolutionQuad1D((float)1*Math.pow(10,i));
				long time2 = System.nanoTime();
				System.out.println("Time: "+(time2-time1)/1000000000+" s, Time: "+(time2-time1)/1000000+" ms, Time: "+(time2-time1)/1000+" us, ");
			}
			//System.out.println("RESET");
		}
		System.out.println("end");
		
		System.exit(0);*/
		

		/*
		int nbTest = 6;
		wiener test = new wiener("saturn2.png","saturn_psf.png");
		test.debug = false;
		for(int i=0;i<nbTest;i++){
			long time1 = System.nanoTime();
			//test.compute_wiener((float)0.5*Math.pow(10,-i),"out"+i+".jpg");
			long time2 = System.nanoTime();
			System.out.println("Time: "+(time2-time1)/1000000000+" s, Time: "+(time2-time1)/1000000+" ms, Time: "+(time2-time1)/1000+" us, ");
		}*/


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
