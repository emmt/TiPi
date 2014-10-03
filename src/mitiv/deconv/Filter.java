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

import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.ShapedVector;

/**
 * This class contain all the methods to compute the solutions
 * 
 * @author Leger Jonathan
 *
 */
public class Filter{
    int width;
    int height;
    int sizeZ;
    double cc;
    //1D
    double[] FFT_PSF1D;
    double[] FFT_Image1D;
    double[] tabcc1D;

    //vector
    ShapedVector image;

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
     * @return An array
     */
    public double[] wiener1D(double alpha) {
        double a,b,c,d,q;
        double[]out = new double[width*2*height];
        /*for(int j = 0; j < height; j++){
            for(int i = 0; i < width; i++){
                a = FFT_PSF1D[2*i    +2*j*width];
                b = FFT_PSF1D[2*i+1  +2*j*width];
                c = FFT_Image1D[2*i  +2*j*width];
                d = FFT_Image1D[2*i+1+2*j*width];
                q = 1.0/(a*a + b*b + cc*alpha);
                out[2*i+   2*j*width] = (a*c + b*d)*q;
                out[2*i+1 +2*j*width] = (a*d - b*c)*q;
            }
        }*/
        int pos;
        for(int i = 0; i < width*height; i++){
            pos = 2*i;
            a = FFT_PSF1D[pos  ];
            b = FFT_PSF1D[pos+1];
            c = FFT_Image1D[pos];
            d = FFT_Image1D[pos+1];
            q = 1.0/(a*a + b*b + cc*alpha);
            out[pos  ] = (a*c + b*d)*q;
            out[pos+1] = (a*d - b*c)*q;
        }
        return out;
    }

    public double[] wiener3D(double alpha, double[] FFT_PSF,double[] FFTImage, int Width, int Height, int sizeZ) {
        this.FFT_PSF1D = FFT_PSF;
        this.FFT_Image1D = FFTImage;
        this.sizeZ = sizeZ;
        width = Width;
        height = Height;
        cc = FFT_PSF1D[0]*FFT_PSF1D[0];
        return wiener3D(alpha);
    }

    /**
     * Apply the Wiener filter on 1D input
     * Quick version (need to call Wiener1D with full arguments at least once) 
     * 
     * @param alpha
     * @return An array
     */
    public double[] wiener3D(double alpha) {
        double a,b,c,d,q;
        double[]out = new double[2*sizeZ*width*height];
        int pos;
        for(int i = 0; i < sizeZ*width*height; i++){
            pos = 2*i;
            a = FFT_PSF1D[pos  ];
            b = FFT_PSF1D[pos+1];
            c = FFT_Image1D[pos];
            d = FFT_Image1D[pos+1];
            q = 1.0/(a*a + b*b + cc*alpha);
            out[pos  ] = (a*c + b*d)*q;
            out[pos+1] = (a*d - b*c)*q;
        }
        /*
        for (int k = 0; k < sizeZ; k++) {
            for(int j = 0; j < height; j++){
                for(int i = 0; i < width; i++){
                    a = FFT_PSF1D[2*i    +2*j*width+2*k*width*height];
                    b = FFT_PSF1D[2*i+1  +2*j*width+2*k*width*height];
                    c = FFT_Image1D[2*i  +2*j*width+2*k*width*height];
                    d = FFT_Image1D[2*i+1+2*j*width+2*k*width*height];
                    q = 1.0/(a*a + b*b + cc*alpha);
                    out[2*i+   2*j*width+2*k*width*height] = (a*c + b*d)*q;
                    out[2*i+1 +2*j*width+2*k*width*height] = (a*d - b*c)*q;
                }
            }
        */
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
    public ShapedVector wienerVect(double alpha, ShapedVector PSF, ShapedVector image) {
        this.image = image;
        int[] shape = ((DoubleShapedVectorSpace)image.getSpace()).cloneShape();
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
    public ShapedVector wienerVect(double alpha) {
        double[] out = wiener1D(alpha);
        return ((DoubleShapedVectorSpace)image.getSpace()).wrap(out);
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
        width = Height;
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
        double[] out = new double[width*2*height];
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

    public double[] wienerQuad3D(double alpha, double[] FFT_PSF,double[] FFTImage, int Width, int Height, int sizeZ, int sizePading) {
        this.FFT_PSF1D = FFT_PSF;
        this.FFT_Image1D = FFTImage;
        width = Height;
        height = Width;
        this.sizeZ = sizeZ;
        double e,f;
        tabcc1D = new double[width*height];
        //int halfPad  = sizePading/2;
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
        return wienerQuad3D(alpha);
    }

    /**
     * Apply the Wiener filter with quadratic approximation on 1D
     * Quick version (need to call WienerQuad with full arguments at least once)
     * 
     * @param alpha
     * @return An array
     */
    public double[] wienerQuad3D(double alpha) {
        double a,b,c,d,q;
        double[] out = new double[sizeZ*width*2*height];
        for (int j = 0; j < sizeZ; j++) {
            for(int i = 0; i < width*height; i++){
                a = FFT_PSF1D[2*i    +j*width*height*2];
                b = FFT_PSF1D[2*i+1  +j*width*height*2];
                c = FFT_Image1D[2*i  +j*width*height*2];
                d = FFT_Image1D[2*i+1+j*width*height*2];
                q = 1.0/(a*a + b*b + tabcc1D[i]*alpha);
                out[2*i  +j*width*height*2] = (a*c + b*d)*q;
                out[2*i+1+j*width*height*2] = (a*d - b*c)*q;
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
    public ShapedVector wienerQuadVect(double alpha, ShapedVector PSF, ShapedVector image) {
        this.image = image;
        int[] shape = ((DoubleShapedVectorSpace)image.getSpace()).cloneShape();
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
    public ShapedVector wienerQuadVect(double alpha) {
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
