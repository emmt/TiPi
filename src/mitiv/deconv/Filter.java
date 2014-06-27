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
 * This class contain all the methods to compute the solutions
 * 
 * @author Leger Jonathan
 *
 */
public class Filter implements FilterInterface{

    double[][] FFT_PSF;
    double[][] FFT_Image;
    int X;
    int Y;
    double cc;
    double [][]tabcc;

    //1D
    double[] FFT_PSF1D;
    double[] FFT_Image1D;
    double[] tabcc1D;

    @Override
    public double[][] Wiener(double alpha, double[][] FFT_PSF, double[][] FFTImage) {
        this.FFT_PSF = FFT_PSF;
        this.FFT_Image = FFTImage;
        X = FFTImage.length;
        Y = FFTImage[0].length/2;
        cc = FFT_PSF[0][0]*FFT_PSF[0][0]+FFT_PSF[0][1]*FFT_PSF[0][1];
        return Wiener(alpha);
    }

    @Override
    public double[][] Wiener(double alpha) {
        double a,b,c,d,q;
        double[][]out = new double[X][2*Y];
        for(int i = 0; i<X; i++){
            for(int j=0;j<Y;j++){
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

    @Override
    public double[][] WienerQuad(double alpha, double[][] FFT_PSF,double[][] FFTImage) {
        this.FFT_PSF = FFT_PSF;
        this.FFT_Image = FFTImage;
        X = FFTImage.length;
        Y = FFTImage[0].length/2;
        double e,f;
        tabcc = new double[X][Y];
        for(int i = 0; i<X; i++){
            for(int j=0;j<Y;j++){
                if(i<=X/2){
                    e = ((double)i/X);
                }else{
                    e = ((double)(i-X)/X);
                }
                if(j<=Y/2){
                    f = ((double)j/Y);
                }else{
                    f = ((double)(j-Y)/Y);
                }
                tabcc[i][j] = 4*Math.PI*Math.PI*(e*e+f*f);
            }
        }
        return WienerQuad(alpha);
    }

    @Override
    public double[][] WienerQuad(double alpha) {
        double a,b,c,d,q;
        double[][]out = new double[X][2*Y];
        for(int i = 0; i<X; i++){
            for(int j=0; j<Y; j++){
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

    /************************************** 1D TESTING *************************************************/

    @Override
    public double[] WienerQuad1D(double alpha, double[] FFT_PSF,double[] FFTImage, int XX, int YY) {
        this.FFT_PSF1D = FFT_PSF;
        this.FFT_Image1D = FFTImage;
        this.X = XX;
        this.Y = YY;
        double e,f;
        tabcc1D = new double[X*Y];

        for(int i = 0; i<X; i++){
            for(int j=0;j<Y;j++){
                if(i<=X/2){
                    e = ((double)i/X);
                }else{
                    e = ((double)(i-X)/X);
                }
                if(j<=Y/2){
                    f = ((double)j/Y);
                }else{
                    f = ((double)(j-Y)/Y);
                }
                tabcc1D[j+i*Y] = 4*Math.PI*Math.PI*(e*e+f*f);
            }
        }
        return WienerQuad1D(alpha);
    }

    @Override
    public double[] WienerQuad1D(double alpha) {
        double a,b,c,d,q;
        double[]out = new double[X*2*Y];
        for(int i = 0; i<X; i++){
            for(int j=0;j<Y;j++){
                a = FFT_PSF1D[2*(j+i*Y)];
                b = FFT_PSF1D[2*((j+1)+i*Y)];
                c = FFT_Image1D[2*(j+i*Y)];
                d = FFT_Image1D[2*((j+1)+i*Y)];
                q = 1.0/(a*a + b*b + tabcc1D[j+i*Y]*alpha);
                out[2*(j+i*Y)] = (a*c + b*d)*q;
                out[2*((j+1)+i*Y)] = (a*d - b*c)*q;
            }
        }
        return out;
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
