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

import mitiv.linalg.DoubleVector;
import mitiv.linalg.DoubleVectorSpaceWithRank;
import mitiv.linalg.Vector;

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

    public double[][] wiener(double alpha, double[][] FFT_PSF, double[][] FFTImage) {
        this.FFT_PSF = FFT_PSF;
        this.FFT_Image = FFTImage;
        width = FFTImage.length;
        height = FFTImage[0].length/2;
        cc = FFT_PSF[0][0]*FFT_PSF[0][0]+FFT_PSF[0][1]*FFT_PSF[0][1];
        return wiener(alpha);
    }

    public double[][] wiener(double alpha) {
        double a,b,c,d,q;
        double[][]out = new double[width][2*height];
        for(int i = 0; i<width; i++){
            for(int j=0;j<height;j++){
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

    public double[] wiener1D(double alpha, double[] FFT_PSF,double[] FFTImage, int Width, int Height) {
        this.FFT_PSF1D = FFT_PSF;
        this.FFT_Image1D = FFTImage;
        width = Width;
        height = Height;
        cc = FFT_Image1D[0]*FFT_Image1D[0]+FFT_PSF1D[2*height]*FFT_PSF1D[2*height];
        return wiener1D(alpha);
    }

    public double[] wiener1D(double alpha) {
        double a,b,c,d,q;
        double[]out = new double[width*2*height];
        for(int j = 0; j < width; j++){
            for(int i = 0; i < height; i++){
                a = FFT_PSF1D[2*i    +2*j*height];
                b = FFT_PSF1D[2*i+1  +2*j*height];
                c = FFT_Image1D[2*i  +2*j*height];
                d = FFT_Image1D[2*i+1+2*j*height];
                q = 1.0/(a*a + b*b + cc*alpha);
                out[2*i+   2*j*height] = (a*c + b*d)*q;
                out[2*i+1 +2*j*height] = (a*d - b*c)*q;
            }
        }
        return out;
    }

    public Vector wienerVect(double alpha, Vector PSF, Vector image) {
        this.image = image;
        int[]shape = ((DoubleVectorSpaceWithRank)image.getSpace()).getShape();
        double[] out = wiener1D(alpha, ((DoubleVector)PSF).getData(), ((DoubleVector)image).getData(), shape[1], shape[0]/2);
        return ((DoubleVectorSpaceWithRank)image.getSpace()).wrap(out);
    }

    public Vector wienerVect(double alpha) {
        double[] out = wiener1D(alpha);
        return ((DoubleVectorSpaceWithRank)image.getSpace()).wrap(out);
    }

    public double[][] wienerQuad(double alpha, double[][] FFT_PSF,double[][] FFTImage) {
        this.FFT_PSF = FFT_PSF;
        this.FFT_Image = FFTImage;
        width = FFTImage.length;
        height = FFTImage[0].length/2;
        double e,f;
        tabcc = new double[width][height];
        for(int i = 0; i<width; i++){
            for(int j = 0; j<height; j++){
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

    public double[][] wienerQuad(double alpha) {
        double a,b,c,d,q;
        double[][]out = new double[width][2*height];
        for(int i = 0; i<width; i++){
            for(int j=0; j<height; j++){
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

    public double[] wienerQuad1D(double alpha, double[] FFT_PSF,double[] FFTImage, int Width, int Height) {
        this.FFT_PSF1D = FFT_PSF;
        this.FFT_Image1D = FFTImage;
        width = Width;
        height = Height;
        double e,f;
        tabcc1D = new double[width*height];

        for(int j = 0; j<width; j++){
            for(int i = 0; i<height; i++){
                if(j<=width/2){
                    e = ((double)j/width);
                }else{
                    e = ((double)(j-width)/width);
                }
                if(i<=height/2){
                    f = ((double)i/height);
                }else{
                    f = ((double)(i-height)/height);
                }
                tabcc1D[i+j*height] = 4*Math.PI*Math.PI*(e*e+f*f);
            }
        }
        return wienerQuad1D(alpha);
    }

    public double[] wienerQuad1D(double alpha) {
        double a,b,c,d,q;
        double[]out = new double[width*2*height];
        for(int j = 0; j < width; j++){
            for(int i = 0; i < height; i++){
                a = FFT_PSF1D[2*i    +2*j*height];
                b = FFT_PSF1D[2*i+1  +2*j*height];
                c = FFT_Image1D[2*i  +2*j*height];
                d = FFT_Image1D[2*i+1+2*j*height];
                q = 1.0/(a*a + b*b + tabcc1D[i+j*height]*alpha);
                out[2*i+   2*j*height] = (a*c + b*d)*q;
                out[2*i+1 +2*j*height] = (a*d - b*c)*q;
            }
        }
        return out;
    }

    public Vector wienerQuadVect(double alpha, Vector PSF, Vector image) {
        this.image = image;
        int[]shape = ((DoubleVectorSpaceWithRank)image.getSpace()).getShape();
        double[] out = wienerQuad1D(alpha, ((DoubleVector)PSF).getData(), ((DoubleVector)image).getData(), shape[1], shape[0]/2);
        return ((DoubleVectorSpaceWithRank)image.getSpace()).wrap(out);
    }

    public Vector wienerQuadVect(double alpha) {
        double[] out = wienerQuad1D(alpha);
        return ((DoubleVectorSpaceWithRank)image.getSpace()).wrap(out);
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
