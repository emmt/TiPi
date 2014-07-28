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

import mitiv.linalg.Vector;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;

/**
 * This class contain all the methods to compute the solutions
 * 
 * @author Leger Jonathan
 *
 */
public class Filter{

    double[][] FFT_PSF;
    double[][] FFT_Image;
    int width;
    int height;
    double cc;
    double [][]tabcc;

    //1D
    double[] FFT_PSF1D;
    double[] FFT_Image1D;
    double[] tabcc1D;

    //vector
    Vector image;

    /**
     * Apply the Wiener filter
     * 
     * @param alpha
     * @param FFT_PSF
     * @param FFTImage
     * @return An array
     */
    public double[][] wiener(double alpha, double[][] FFT_PSF, double[][] FFTImage) {
        this.FFT_PSF = FFT_PSF;
        this.FFT_Image = FFTImage;
        width = FFTImage.length;
        height = FFTImage[0].length/2;
        cc = FFT_PSF[0][0]*FFT_PSF[0][0]+FFT_PSF[0][1]*FFT_PSF[0][1];
        return wiener(alpha);
    }

    /**
     * Apply the Wiener filter
     * Quick version (need to call Wiener with full arguments at least once) 
     * 
     * @param alpha
     * @return An array
     */
    public double[][] wiener(double alpha) {
        double a,b,c,d,q;
        double[][]out = new double[width][2*height];
        for(int j = 0; j<height; j++){
            for(int i=0;i<width;i++){
                a = FFT_PSF[i][2*j];
                b = FFT_PSF[i][2*j+1];
                c = FFT_Image[i][2*j];
                d = FFT_Image[i][2*j+1];
                //up = conjuguate(fft_psf)*fft_image
                //upRe = a*c + b*d;
                //upIm = a*d - b*c;
                //down = abs2(fft_psf)+alpha*abs2(fft_psf(0,0));
                q = 1.0/(a*a + b*b + cc*alpha);
                //out = up/down
                out[i][2*j] = (a*c + b*d)*q;
                out[i][2*j+1] = (a*d - b*c)*q;
            }
        }
        return out;
    }

    /**
     * Apply the Wiener filter on 1D input
     * 
     * @param alpha
     * @param FFT_PSF
     * @param FFTImage
     * @param Width
     * @param Height
     * @return An array
     */
    public double[] wiener1D(double alpha, double[] FFT_PSF,double[] FFTImage, int Width, int Height) {
        this.FFT_PSF1D = FFT_PSF;
        this.FFT_Image1D = FFTImage;
        width = Width;
        height = Height;
        cc = FFT_PSF1D[0]*FFT_PSF1D[0]+FFT_PSF1D[2*width]*FFT_PSF1D[2*width];
        return wiener1D(alpha);
    }

    /**
     * Apply the Wiener filter on 1D input
     * Quick version (need to call Wiener1D with full arguments at least once) 
     * 
     * @param alpha
     * @return An arrray
     */
    public double[] wiener1D(double alpha) {
        double a,b,c,d,q;
        double[]out = new double[width*2*height];
        for(int j = 0; j < height; j++){
            for(int i = 0; i < width; i++){
                a = FFT_PSF1D[2*i    +2*j*width];
                b = FFT_PSF1D[2*i+1  +2*j*width];
                c = FFT_Image1D[2*i  +2*j*width];
                d = FFT_Image1D[2*i+1+2*j*width];
                q = 1.0/(a*a + b*b + cc*alpha);
                out[2*i+   2*j*width] = (a*c + b*d)*q;
                out[2*i+1 +2*j*width] = (a*d - b*c)*q;
            }
        }
        return out;
    }

    /**
     * Apply the Wiener filter on Vector
     * 
     * @param alpha
     * @param PSF
     * @param image
     * @return A vector
     */
    public Vector wienerVect(double alpha, Vector PSF, Vector image) {
        this.image = image;
        int[]shape = ((DoubleShapedVectorSpace)image.getSpace()).cloneShape();
        double[] out = wiener1D(alpha, ((DoubleShapedVector)PSF).getData(), ((DoubleShapedVector)image).getData(), shape[1], shape[0]/2);
        return ((DoubleShapedVectorSpace)image.getSpace()).wrap(out);
    }

    /**
     * Apply the Wiener filter on Vector
     * Quick version (need to call Wiener1D with full arguments at least once)
     * 
     * @param alpha
     * @return A vector
     */
    public Vector wienerVect(double alpha) {
        double[] out = wiener1D(alpha);
        return ((DoubleShapedVectorSpace)image.getSpace()).wrap(out);
    }

    /**
     * Apply the Wiener filter with quadratic approximation on Vector
     * 
     * @param alpha
     * @param FFT_PSF
     * @param FFTImage
     * @return An array
     */
    public double[][] wienerQuad(double alpha, double[][] FFT_PSF,double[][] FFTImage) {
        this.FFT_PSF = FFT_PSF;
        this.FFT_Image = FFTImage;
        width = FFTImage.length;
        height = FFTImage[0].length/2;
        double e,f;
        tabcc = new double[width][height];
        for(int j = 0; j<height; j++){
            for(int i = 0; i<width; i++){
                if(i <= width/2){
                    e = ((double)i/width);
                }else{
                    e = ((double)(i-width)/width);
                }
                if(j <= height/2){
                    f = ((double)j/height);
                }else{
                    f = ((double)(j-height)/height);
                }
                tabcc[i][j] = 4*Math.PI*Math.PI*(e*e+f*f);
            }
        }
        return wienerQuad(alpha);
    }

    /**
     * Apply the Wiener filter with quadratic approximation on Vector
     * Quick version (need to call WienerQuad with full arguments at least once)
     * 
     * @param alpha
     * @return An array
     */
    public double[][] wienerQuad(double alpha) {
        double a,b,c,d,q;
        double[][]out = new double[width][2*height];
        for(int j = 0; j<height; j++){
            for(int i=0; i<width; i++){
                a = FFT_PSF[i][2*j];
                b = FFT_PSF[i][2*j+1];
                c = FFT_Image[i][2*j];
                d = FFT_Image[i][2*j+1];
                q = 1.0/(a*a + b*b + tabcc[i][j]*alpha);
                out[i][2*j] = (a*c + b*d)*q;
                out[i][2*j+1] = (a*d - b*c)*q;
            }
        }
        return out;
    }

    /**
     * Apply the Wiener filter with quadratic approximation on 1D input
     * 
     * @param alpha
     * @param FFT_PSF
     * @param FFTImage
     * @param Width 
     * @param Height 
     * @return An array
     */
    public double[] wienerQuad1D(double alpha, double[] FFT_PSF,double[] FFTImage, int Width, int Height) {
        this.FFT_PSF1D = FFT_PSF;
        this.FFT_Image1D = FFTImage;
        width = Height; //Don't know why but this have to be done ...
        height = Width;
        double e,f;
        tabcc1D = new double[width*height];
        for(int j = 0; j < height; j++){
            for(int i = 0; i < width; i++){
                if(j<=height/2){
                    e = ((double)j/height);
                }else{
                    e = ((double)(j-height)/height);
                }
                if(i<=width/2){
                    f = ((double)i/width);
                }else{
                    f = ((double)(i-width)/width);
                }
                tabcc1D[i+j*width] = 4*Math.PI*Math.PI*(e*e+f*f);
            }
        }
        return wienerQuad1D(alpha);
    }

    /**
     * Apply the Wiener filter with quadratic approximation on 1D
     * Quick version (need to call WienerQuad with full arguments at least once)
     * 
     * @param alpha
     * @return An array
     */
    public double[] wienerQuad1D(double alpha) {
        double a,b,c,d,q;
        double[]out = new double[width*2*height];
        for(int j = 0; j < height; j++){
            for(int i = 0; i < width; i++){
                a = FFT_PSF1D[2*i    +2*j*width];
                b = FFT_PSF1D[2*i+1  +2*j*width];
                c = FFT_Image1D[2*i  +2*j*width];
                d = FFT_Image1D[2*i+1+2*j*width];
                q = 1.0/(a*a + b*b + tabcc1D[i+j*width]*alpha);
                out[2*i+   2*j*width] = (a*c + b*d)*q;
                out[2*i+1 +2*j*width] = (a*d - b*c)*q;
            }
        }
        return out;
    }

    /**
     * Apply the Wiener filter with quadratic approximation on Vector
     * 
     * @param alpha
     * @param PSF 
     * @param image 
     * @return A vector
     */
    public Vector wienerQuadVect(double alpha, Vector PSF, Vector image) {
        this.image = image;
        int[]shape = ((DoubleShapedVectorSpace)image.getSpace()).cloneShape();
        double[] out = wienerQuad1D(alpha, ((DoubleShapedVector)PSF).getData(), ((DoubleShapedVector)image).getData(), shape[1], shape[0]/2);
        return ((DoubleShapedVectorSpace)image.getSpace()).wrap(out);
    }

    /**
     * Apply the Wiener filter with quadratic approximation on Vector
     * Quick version (need to call WienerQuad with full arguments at least once)
     * 
     * @param alpha
     * @return A vector
     */
    public Vector wienerQuadVect(double alpha) {
        double[] out = wienerQuad1D(alpha);
        return ((DoubleShapedVectorSpace)image.getSpace()).wrap(out);
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
