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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import mitiv.deconv.ConvolutionOperator;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.RealComplexFFT;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;

// TODO: Auto-generated Javadoc
/**
 * The Class MathUtils.
 */
public class MathUtils {

    /** The Constant COLORMAP_GRAYSCALE. */
    public static final int COLORMAP_GRAYSCALE = 0;

    /** The Constant COLORMAP_JET. */
    public static final int COLORMAP_JET = 1;

    /** The Constant GAUSSIAN. */
    public static final int GAUSSIAN = 2;

    /** The Constant AVERAGE. */
    public static final int AVERAGE = 3;

    /** The Constant PREWITT. */
    public static final int PREWITT = 4;

    /** The Constant SOBEL. */
    public static final int SOBEL = 5;

    /** The Constant KIRSH. */
    public static final int KIRSH = 6;

    /** The Constant DISK. */
    public static final int DISK = 7;

    /**
     * Check if the number is even. 
     *
     * @param x the x
     * @return return a boolean
     */
    public static boolean even(int x)
    {
        return ( x % 2 == 0 );
    }

    /**
     * Returns the factorial of a number. 
     *
     * @param n the n
     * @return the factorial of the number
     */
    public static long factorial(long n)
    {
        if (n == 0)
        {
            return 1;
        }
        else
        {
            return n * factorial(n-1);
        }
    }

    /**
     * Return an array(a meshgrid) of cartesian distance of
     * value x between [(-W+1)/2, W/2] and y
     * between [(-H+1)/2, H/2].
     *
     * @param W width of the array
     * @param H height of the array
     * @return 2d array of cartesian distance
     */
    public static double[][] cartesDist2D(int W, int H)
    {
        double R[][] = new double[H][W];
        double[] x = indgen((-W+1)/2, W/2, 1);
        double[] y = indgen((-H+1)/2, H/2, 1);
        for( int j = 0; j < W; j++)
        {
            for( int i = 0; i < H; i++)
            {
                R[i][j] = Math.sqrt(x[i]*x[i] + y[j]*y[j]);
            }
        }
        return R;
    }

    /**
     * Return an 1d array(a meshgrid) of cartesian distance of
     * value x between [(-W+1)/2, W/2] and y
     * between [(-H+1)/2, H/2].
     *
     * @param W width of the array
     * @param H height of the array
     * @return 1d array of cartesian distance
     */
    public static double[] cartesDist1D(int W, int H)
    {
        double R[] = new double[H*W];
        double[] x = indgen((-W+1)/2, W/2);
        double[] y = indgen((-H+1)/2, H/2);
        for( int j = 0; j < W; j++)
        {
            for( int i = 0; i < H; i++)
            {
                R[j*H + i] = Math.sqrt(x[i]*x[i] + y[j]*y[j]);
            }
        }
        return R;
    }

    /**
     * Return an 1d array of polar angle of
     * value x between [(-W+1)/2, W/2] and y
     * between [(-H+1)/2, H/2] in FFT indexing.
     *
     * @param W width of the array
     * @param H height of the array
     * @return 2d array of angle
     */
    public static double[] cartesAngle1D(int W, int H)
    {
        double THETA[] = new double[H*W];
        double[] x = indgen((-W+1)/2, W/2);
        double[] y = indgen((-H+1)/2, H/2);
        for( int j = 0; j < W; j++)
        {
            for( int i = 0; i < H; i++)
            {
                THETA[j*H + i] = Math.atan2( y[i], x[j]);
            }
        }
        return THETA;
    }

    /**
     * Return an 2d array of polar angle of
     * value x between [(-W+1)/2, W/2] and y
     * between [(-H+1)/2, H/2] in FFT indexing.
     *
     * @param W width of the array
     * @param H height of the array
     * @return 2d array of angle
     */
    public static double[][] cartesAngle2D(int W, int H)
    {
        double THETA[][] = new double[H][W];
        double[] x = indgen((-W+1)/2, W/2);
        double[] y = indgen((-H+1)/2, H/2);
        for( int j = 0; j < W; j++)
        {
            for( int i = 0; i < H; i++)
            {
                THETA[i][j] = Math.atan2( y[i], x[j]);
            }
        }
        return THETA;
    }

    /**
     * Standard deviation of the matrix σ.
     *
     * @param a the a
     * @return the double
     */
    public static double[] catArray(double a[], double[] b)
    {
        int L = a.length + b.length;
        double[] out = new double[L];
        for (int i = 0; i < a.length; i++)
        {
            out[i] = a[i];
        }
        for (int i = b.length; i < L; i++)
        {
            out[i] = b[i - b.length];
        }
        return out;
    }

    public static DoubleShapedVector convolution(double[] x, double[] h, int nx, int ny, int nz)
    {
        int[] shape = {nx, nx, nz};
        DoubleShapedVectorSpace space = new DoubleShapedVectorSpace(shape);
        DoubleShapedVector hVector = space.wrap(h);
        DoubleShapedVector xVector = space.wrap(x);

        /* Convolve x by the psf h */
        RealComplexFFT FFT = new RealComplexFFT(space);
        ConvolutionOperator H = new ConvolutionOperator(FFT, hVector);
        DoubleShapedVector y = space.create();
        H.apply(xVector, y);
        return y;
    }

    public static double[] convol(double[] x, double[] h, int nx, int ny, int nz)
    {
        int[] shape = {nx, nx, nz};
        DoubleShapedVectorSpace space = new DoubleShapedVectorSpace(shape);
        DoubleShapedVector hVector = space.wrap(h);
        DoubleShapedVector xVector = space.wrap(x);

        /* Convolve x by the psf h */
        RealComplexFFT FFT = new RealComplexFFT(space);
        ConvolutionOperator H = new ConvolutionOperator(FFT, hVector);
        DoubleShapedVector y = space.create();
        H.apply(xVector, y);

        return y.getData();
    }
    
    public static double[] create3DSquare(int nxy, int nz, int sizeSquare)
    {
        double[] out = new double[nxy*nxy*nz];
        for (int i = 0; i < nz; i++)
        {
            for(int j = 0; j < 2; j++)
            {
                out[i*nxy*nxy + nxy*nxy/2 + j + nxy/2] = 1;
                out[i*nxy*nxy + nxy*nxy/2 + j + nxy+nxy/2] = 1;
            }
        }
        return out;
    }
    
    /**
     * Return an array of polar angle of
     * value x between [(-W+1)/2, W/2] and y
     * between [(-H+1)/2, H/2] in FFT indexing.
     *
     * @param W width of the array
     * @param H height of the array
     * @return 2d array of angle
     */
    public static double[][] fftAngle(int W, int H)
    {
        double THETA[][] = new double[H][W];
        double[] x = fftIndgen(W);
        double[] y = fftIndgen(H);
        for( int j = 0; j < W; j++)
        {
            for( int i = 0; i < H; i++)
            {
                THETA[i][j] = Math.atan2( y[i], x[j]);
            }
        }
        return THETA;
    }

    /**
     * Return an array of polar angle of
     * value x between [(-W+1)/2, W/2] and y
     * between [(-H+1)/2, H/2] in FFT indexing.
     *
     * @param W width of the array
     * @param H height of the array
     * @return 2d array of angle
     */
    public static double[] fftAngle1D(int W, int H)
    {
        double THETA[] = new double[W*H];
        double[] x = fftIndgen(W);
        double[] y = fftIndgen(H);
        for( int j = 0; j < H; j++)
        {
            for( int i = 0; i < W; i++)
            {
                THETA[i + j*W] = Math.atan2( y[j], x[i]);
            }
        }
        return THETA;
    }
    //FIXME upgrade the function
    /**
     * Generate index of FFT frequencies/coordinates.
     *
     * @param L width of the array
     * @return FFT frequencies along a dimension of length LEN.
     */
    public static double[] fftIndgen(int L)
    {   
        double[] k = new double[L];
        double u[] = indgen(0, L - 1);
        for (int i = 0; i < L; i++)
        {
            if (u[i] > L/2)
            {
                k[i] = u[i] - L;

            }else
            {
                k[i] = u[i];
            }
        }
        return k;
    }

    /**
     * Fft dist.
     *
     * @param L the l
     * @return the double[]
     */
    public static double[] fftDist(int L)
    {
        double x[] = new double[L];
        double R[] = new double[L];
        x = fftIndgen(L);
        for( int i = 0; i < L; i++){
            R[i] = Math.sqrt(x[i] * x[i] + x[i] * x[i]);
        }
        return R;
    }

    /**
     * Compute length of FFT frequencies/coordinates.
     *
     * @param W width
     * @param H height
     * @return Euclidian lenght of spatial frequencies in frequel units for a
     * FFT of dimensions [H,W].
     */
    public static double[][] fftDist(int W, int H)
    {
        double x[] = new double[W];
        double y[] = new double[H];
        double R[][] = new double[H][W];
        x = fftIndgen(W);
        y = fftIndgen(H);
        for( int j = 0; j < W; j++)
        {
            for( int i = 0; i < H; i++)
            {
                R[i][j] = Math.sqrt(x[i] * x[i] + y[j] * y[j]);
            }
        }
        return R;
    }

    /**
     * Compute length of FFT frequencies/coordinates.
     *
     * @param W width
     * @param H height
     * @return Euclidian lenght of spatial frequencies in frequel units for a
     * FFT of dimensions [H,W].
     */
    public static double[] fftDist1D(int W, int H)
    {
        double R[] = new double[H*W];
        double[] x = fftIndgen(W);
        double[] y = fftIndgen(H);
        for( int j = 0; j < H; j++)
        {
            for( int i = 0; i < W; i++)
            {
                R[i + j*W] = Math.sqrt(x[i]*x[i] + y[j]*y[j]);
            }
        }
        return R;
    }

    public static double[][][] gram_schmidt_orthonormalization(double[][][] A)
    {

        int D = A.length;
        int H = A[0].length;
        int W = A[0][0].length;
        double[][] Ai = new double[H][W];
        double[][] Aj = new double[H][W];
        double[][] s = new double[H][W];
        /* Gram-Schmidt Orthonormalization. */
        for (int i = 0 ; i < D ; ++i)
        {
            /* get i-th basis vector */
            for (int x = 0; x < W; x++)
            {
                for (int y = 0; y < H; y++)
                {
                    Ai[y][x] = A[i][y][x];
                }
            }

            /* make the i-th vector othogonal to previous ones */
            if (i > 0)
            {
                for (int x = 0; x < W; x++)
                {
                    for (int y = 0; y < H; y++)
                    {
                        Aj[y][x] = A[0][y][x];
                    }
                }

                double SAij = sum(hadamardProd(Ai, Aj, 0));

                for (int x = 0; x < W; x++)
                {
                    for (int y = 0; y < H; y++)
                    {
                        s[y][x] = SAij*Aj[y][x];
                    }
                }

                for ( int j = 1 ; j < i ; ++j)
                {
                    for (int x = 0; x < W; x++)
                    {
                        for (int y = 0; y < H; y++)
                        {
                            Aj[y][x] = A[j][y][x];
                        }
                    }

                    double SAij2 = sum(hadamardProd(Ai, Aj, 0));
                    for (int x = 0; x < W; x++)
                    {
                        for (int y = 0; y < H; y++)
                        {
                            s[y][x] += SAij2*Aj[y][x];
                        }
                    }
                }

                for (int x = 0; x < W; x++)
                {
                    for (int y = 0; y < H; y++)
                    {
                        Ai[y][x] -= s[y][x];
                    }
                }
            }

            /* normalize i-th vector */
            double SAii = sum(hadamardProd(Ai, Ai, 0));
            if ( SAii > 0)
            {
                for (int x = 0; x < W; x++)
                {
                    for (int y = 0; y < H; y++)
                    {
                        A[i][y][x] = 1/Math.sqrt(SAii)*Ai[y][x];
                    }
                }
            }
            else
            {
                for (int x = 0; x < W; x++)
                {
                    for (int y = 0; y < H; y++)
                    {
                        A[i][y][x] = 0;
                    }
                }
            }
        }
        return A;
    }

    public static double[] gram_schmidt_orthonormalization(double[] A, int dim1, int dim2, int dim3)
    {
        double[] Ak = new double[dim1*dim2];
        double[] Al = new double[dim1*dim2];
        double[] s = new double[dim1*dim2];
        double SA_kl;
        /* Gram-Schmidt Orthonormalization. */
        for (int k = 0 ; k < dim3 ; ++k)
        {
            /* get i-th basis vector */
            for (int i2 = 0; i2 < dim2; i2++)
            {
                for (int i1 = 0; i1 < dim1; i1++)
                {
                    Ak[i1 + dim1*i2] = A[i1 + dim1*i2 + k*dim1*dim2];
                }
            }

            /* make the i-th vector othogonal to previous ones */
            if (k > 0)
            {
                for (int i2 = 0; i2 < dim2; i2++)
                {
                    for (int i1 = 0; i1 < dim1; i1++)
                    {
                        Al[i1 + dim1*i2] = A[i1 + dim1*i2];
                    }
                }

                SA_kl = innerProd(Ak, Al);
                for (int i12 = 0; i12 < dim1*dim2; i12++)
                {
                    s[i12] = SA_kl*Al[i12];
                }

                for ( int l = 1 ; l < k ; ++l)
                {
                    for (int i2 = 0; i2 < dim2; i2++)
                    {
                        for (int i1 = 0; i1 < dim1; i1++)
                        {
                            Al[i1 + dim1*i2] = A[i1 + dim1*i2 + l*dim1*dim2];
                        }
                    }

                    SA_kl = innerProd(Ak, Al);
                    for (int i12 = 0; i12 < dim1*dim2; i12++)
                    {
                        s[i12] += SA_kl*Al[i12];
                    }
                }

                for (int i12 = 0; i12 < dim1*dim2; i12++)
                {
                    Ak[i12] -= s[i12];
                }
            }

            /* normalize i-th vector */
            SA_kl = innerProd(Ak, Ak);
            if ( SA_kl > 0)
            {
                for (int i2 = 0; i2 < dim2; i2++)
                {
                    for (int i1 = 0; i1 < dim1; i1++)
                    {
                        A[i1 + dim1*i2 + k*dim1*dim2] = 1/Math.sqrt(SA_kl)*Ak[i1 + dim1*i2];
                    }
                }
            }
            else
            {
                for (int i2 = 0; i2 < dim2; i2++)
                {
                    for (int i1 = 0; i1 < dim1; i1++)
                    {
                        A[i1 + dim1*i2 + k*dim1*dim2] = 0;
                    }
                }
            }
        }
        return A;
    }

    /**
     * Returns array of N doubles equally spaced from START to STOP.
     *
     * @param begin the begin
     * @param end the end
     * @param scale the scale
     * @return Euclidian length of spatial frequencies in frequel units for a
     * FFT of dimensions [W,H].
     */
    public static double[] span(double begin, double end, double scale)
    {
        double a = begin;
        int L = 0;
        while( a <= end )
        {
            a = a + scale;
            L = L + 1;
        }
        double tab[] = new double[L];

        a = begin;
        int b = 0;	
        for(double i = begin; i <= end; i+= scale)
        {
            tab[b] = i;
            b++;
        }
        return tab;
    }

    /**
     * Returns "index generator" list -- an array of longs running from
     * 1 to N, inclusive.
     *
     * @param n number of elements in the array
     * @return an array
     */
    public static double[] indgen(int n)
    {
        double[] out = new double[n];
        for (int i = 0; i < n; i++)
        {
            out[i] = i + 1;
        }
        return out;
    }

    /**
     * Returns "index generator" list -- an array of longs running from
     * START to STOP with a step of 1.
     *
     * @param start array's first value
     * @param stop array's end value
     * @return an array
     */
    public static double[] indgen(int start, int stop)
    {
        int L = stop - start + 1;
        double[] out = new double[L];
        for (int i = start; i <= stop; i++)
        {
            out[i - start] = i;
        }
        /*
        out[0] = start;
        for (int i = 0; i < L; i++)
        {
            out[i] += start;
        }
         */
        return out;
    }

    /**
     * Returns "index generator" list -- an array of longs running from
     * START to STOP with a SCALE.
     *
     * @param start array's first value
     * @param stop array's end value
     * @param scale scale between values (steps)
     * @return an array
     */
    public static double[] indgen(int start, int stop, double scale)
    {
        double a = start;
        int L = 0;
        while( a <= stop )
        {
            a = a + scale;
            L = L + 1;
        }
        double tab[] = new double[L];

        a = start;
        int b = 0;  
        for(double i = start; i <= stop; i+= scale)
        {
            tab[b] = i;
            b++;
        }
        return tab;
    }

    /**
     * Returns array of N doubles equally spaced from START to STOP.
     *
     * @param start the begining of the array
     * @param stop the stop
     * @param n number of element in the array
     * @return array of N doubles equally spaced from START to STOP.
     */
    public static double[] span(double start, double stop, int n)
    {
        double[] out = new double[n];
        double c1 = (stop - start)/(n - 1);
        double c2 = (n + 1)/2.;
        double c3 = (start + stop)/2;
        for (int i = 0; i < n; i++)
        {
            out[i] = c1*(i + 1 - c2) + c3;
        }
        return out;
    }

    /**
     * Scale array values into a 8bit (between 0 and 255).
     * @param A array of double to convert in 8bit
     * @return scaleA
     */
    public static double[] scaleArrayTo8bit(double[] A)
    {
        int L = A.length;
        double[] scaleA = new double[L];
        double minScaleA = min(A);
        double maxScaleA = max(A);
        double deltaScaleA = maxScaleA - minScaleA;
        for(int i = 0; i < L; i++){
            scaleA[i] = (A[i] - minScaleA)*255/deltaScaleA;
        }
        return scaleA;
    }

    /**
     * Scale array values into a 8bit (between 0 and 255).
     *
     * @param A array of double to convert in 8bit
     * @return the double[][]
     */
    public static double[][] scaleArrayTo8bit(double[][] A)
    {
        int H = A.length;
        int W = A[0].length;
        double[][] scaleA = new double[H][W];
        double minScaleA = min(A);
        double maxScaleA = max(A);
        double deltaScaleA = maxScaleA - minScaleA;
        for(int j = 0; j < W; j++)
        {
            for(int i = 0; i < H; i++)
            {
                scaleA[i][j] = (A[i][j] - minScaleA)*255/deltaScaleA;
            }
        }

        return scaleA;
    }

    /**
     * Scale array values into a 8bit (between 0 and 255).
     *
     * @param A the a
     */
    public static void uint8(double[][] A)
    {
        int H = A.length;
        int W = A[0].length;
        double minScaleA = min(A);
        double maxScaleA = max(A);
        double deltaScaleA = maxScaleA - minScaleA;
        for(int j = 0; j < W; j++)
        {
            for(int i = 0; i < H; i++)
            {
                A[i][j] = (A[i][j] - minScaleA)*255/deltaScaleA;
            }
        }
    }

    /* converts the image in range [0, 1] */
    /**
     * Im2double.
     *
     * @param a the a
     */
    public static void im2double(double[][] a)
    {
        int H = a.length;
        int W = a[0].length;
        double minA = min(a);
        double maxA = max(a);
        double delta = maxA - minA;
        for (int j = 0; j < W; j++)
        {
            for (int i = 0; i < H; i++)
            {
                a[i][j] = (a[i][j] - minA)/delta;
            }
        }
    }

    /**
     * Conj1.
     *
     * @param A the a
     * @return the double[][]
     */
    public static double[][] conj1(double[][] A)
    {
        int H = A.length;
        int W = A[0].length;
        double[][] conjA = new double[H][W];
        for(int j = 0; j < W/2; j++){
            for(int i = 0; i < H; i++)
            {
                conjA[i][2*j] = A[i][2*j];
                conjA[i][2*j + 1] = -A[i][2*j + 1];
            }
        }
        return conjA;
    }

    public static void conj2(double[] A)
    {
        for(int i = 0; i < A.length/2; i++)
        {

            A[2*i + 1] = -A[2*i + 1];
        }
    }
    /**
     * Returns the squared absolute value of a 2d array (complex).
     *
     * @param IN array
     * @return square absolute value.
     */
    public static double[][] abs2(double[][] IN)
    {
        int H = IN.length;
        int W = IN[0].length/2;
        double[][] OUT = new double[H][W];
        for(int i = 0; i < H; i++)
        {
            OUT[i] = abs2(IN[i], 1);
        }
        return OUT;
    }

    /**
     * Returns the squared absolute value of 1d array.
     *
     * @param IN array
     * @return square absolute value.
     */
    public static double[] abs2(double[] x, int isComplex)
    {
        if(isComplex == 1)
        {
            int L = x.length/2;
            double[] y = new double[L];
            for(int i = 0; i < L; i++)
            {
                y[i] = x[2*i]*x[2*i] + x[2*i + 1]*x[2*i + 1];
            }
            return y;
        }
        else
        {
            int L = x.length;
            double[] y = new double[L];
            for(int i = 0; i < L; i++)
            {
                y[i] = x[i]*x[i];
            }
            return y;
        }  
    }

    /**
     * Returns the squared absolute value of 1d array.
     *
     * @param IN array
     * @return square absolute value.
     */
    public static double[] abs2(double[] x, int begin, int end, int isComplex)
    {
        //TODO regarder taille
        if(isComplex == 1)
        {
            int L = x.length/2;
            double[] y = new double[L];
            for(int i = begin; i < end; i++)
            {
                y[i] = x[2*i]*x[2*i] + x[2*i + 1]*x[2*i + 1];
            }
            return y;
        }
        else
        {
            int L = end - begin + 1;
            double[] y = new double[L];
            for(int i = 0; i < L; i++)
            {
                y[i] = x[i + begin]*x[i + begin];
            }
            return y;
        }  
    }



    /**
     * Compute the minimum value of a 1d array.
     *
     * @param matrix array
     * @return min
     */
    public static double min(double[] matrix)
    {
        double min = matrix[0];
        for (int i = 0; i < matrix.length; i++)
        {
            if (min > matrix[i])
            {
                min = matrix[i];
            }
        }
        return min;
    }

    /**
     * Compute the minimum value of a 2d array.
     *
     * @param matrix array
     * @return min
     */
    public static double min(double[][] matrix) {
        double min = matrix[0][0];
        for (int col = 0; col < matrix.length; col++)
        {
            for (int row = 0; row < matrix[col].length; row++)
            {
                if (min > matrix[col][row])
                {
                    min = matrix[col][row];
                }
            }
        }
        return min;
    }

    /**
     * Compute the maximum value of a 1d array.
     *
     * @param matrix the matrix
     * @return the double
     */
    public static double max(double[] matrix)
    {
        double max = matrix[0];
        for (int i = 0; i < matrix.length; i++)
        {
            if (max < matrix[i])
            {
                max = matrix[i];
            }
        }
        return max;
    }

    /**
     * Compute the maximum value of a 2d array.
     *
     * @param matrix the matrix
     * @return the double
     */
    public static double max(double[][] matrix) {
        double max = matrix[0][0];
        for (int col = 0; col < matrix.length; col++) {
            for (int row = 0; row < matrix[col].length; row++) {
                if (max < matrix[col][row]) {
                    max = matrix[col][row];
                }
            }
        }
        return max;
    }

    public static void printArray(int A[])
    {   
        System.out.println(Arrays.toString(A));
    }

    /**
     * Prints the array.
     *
     * @param A the a
     */
    public static void printArray(double A[])
    {   
        System.out.println(Arrays.toString(A));
    }

    public static void printArray(double A[], int begin, int end, int decimal)
    {
        if( decimal == 0)
        {
            for( int k = begin; k <= end; k++)
            {
                System.out.printf("%f ", A[k]);
            }
        }
        else
        {
            for( int k = begin; k <= end; k++)
            {
                System.out.printf("%.4E ", A[k]);
            }
        }
    }

    /**
     * Prints 3d array of 1d array.
     *
     * @param A the 1d array
     * @param width
     * @param height
     * @param deepth
     * @param decimal
     * 
     */
    public static void printArray(double A[], int width, int height, int deepth, int decimal)
    {   
        if( decimal == 0)
        {
            for(int k = 0; k < deepth; k++)
            {
                System.out.println("k = " + k);
                for( int j = 0; j < height; j++)
                {
                    for( int i = 0; i < width; i++)
                    {
                        System.out.printf("%.2f ", A[i + j*width + k*width*height]);
                    }
                    System.out.println();
                }
            }
        }
        else
        {
            for(int k = 0; k < deepth; k++)
            {
                System.out.println("k = " + k);
                for( int j = 0; j < height; j++)
                {
                    for( int i = 0; i < width; i++)
                    {
                        System.out.printf("%.4E ", A[i + j*width + k*width*height]);
                    }
                    System.out.println();
                }
            }
        }
        System.out.println();
    }

    /**
     * Prints the array.
     *
     * @param A the a
     */
    public static void printArray(double A[][])
    {	
        int H = A.length;
        for(int i = 0; i < H; i++ ){
            System.out.println(Arrays.toString(A[i]));
        }
    }

    /**
     * Display image of an 2d array.
     *
     * @param A array to display
     * @param colorMap 0 for a grayscale display and 1 with a colormap
     */
    public static void pli(double A[][], int colorMap)
    {   
        int H = A.length;
        int W = A[0].length;
        double S[][];
        S = scaleArrayTo8bit(A);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        //ColorMap map = ColorMap.getJet(256);

        switch (colorMap) {
        case COLORMAP_JET:
            ColorMap map = ColorMap.getJet(256);
            for(int j = 0; j < W; j++)
            {
                for(int i = 0; i < H; i++)
                {
                    Color b = map.table[ (int) S[i][j] ];
                    bufferedI.setRGB(i, j, b.getRGB()); // j, i inversé
                }
            }
            break;
        case COLORMAP_GRAYSCALE:
            Color b;
            for(int j = 0; j < W; j++)
            {
                for(int i = 0; i < H; i++)
                {
                    b = new Color( (int) S[i][j], (int) S[i][j], (int) S[i][j] );
                    bufferedI.setRGB(i, j, b.getRGB()); // j, i inversé
                }
            }
        default:
            throw new IllegalArgumentException("bad value for colorMap");
        }

        JFrame frame = new JFrame();
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(bufferedI));
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Display image of an 2d array
     * 
     * Different from the "pli" function, uses "naviguablePanel" for a better displaying.
     *
     * @param A the a
     * @param colorMap the color map
     */
    public static void pli2(double A[][], int colorMap)
    {	
        int H = A.length;
        int W = A[0].length;
        double S[][];
        S = scaleArrayTo8bit(A);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);

        switch (colorMap) {
        case COLORMAP_JET:
            ColorMap map = ColorMap.getJet(256);
            for(int j = 0; j < W; j++)
            {
                for(int i = 0; i < H; i++)
                {
                    Color b = map.table[ (int) S[i][j] ];
                    bufferedI.setRGB(i, j, b.getRGB()); // j, i inversé
                }
            }
            break;
        case COLORMAP_GRAYSCALE:
            Color b;
            for(int j = 0; j < W; j++)
            {
                for(int i = 0; i < H; i++)
                {
                    b = new Color( (int) S[i][j], (int) S[i][j], (int) S[i][j] );
                    bufferedI.setRGB(i, j, b.getRGB()); // j, i inversé
                }
            }
        default:
            throw new IllegalArgumentException("bad value for colorMap");
        }
        NavigableImagePanel.afficher(bufferedI);
    }

    /**
     * Display image of an 2d array
     * 
     * Different from the "pli" function, uses "naviguablePanel" for a better displaying.
     *
     * @param A the a
     * @param colorMap the color map
     */
    public static void pli2(double A[], int W, int H, int colorMap)
    {   
        double S[];
        //int L = A.length;
        S = scaleArrayTo8bit(A);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);

        switch (colorMap)
        {
        case COLORMAP_JET:
            ColorMap map = ColorMap.getJet(256);
            for(int j = 0; j < H; j++)
            {
                for(int i = 0; i < W; i++)
                {
                    Color b = map.table[ (int) S[i + j*W] ];
                    bufferedI.setRGB(j, i, b.getRGB()); // j, i inversé
                }
            }
            break;
        case COLORMAP_GRAYSCALE:
            Color b;
            for(int j = 0; j < H; j++)
            {
                for(int i = 0; i < W; i++)
                {
                    b = new Color( (int) S[i + j*W], (int) S[i + j*W], (int) S[i + j*W] );
                    bufferedI.setRGB(j, i, b.getRGB()); // j, i inversé
                }
            }
            break;
        default:
            throw new IllegalArgumentException("bad value for colorMap");
        }
        NavigableImagePanel.afficher(bufferedI);
    }


    /**
     * Plot 2-D FFT array A as an image, taking care of "rolling" A and setting
     * correct world boundaries.  Keyword SCALE can be used to indicate the
     * "frequel" scale along both axis (SCALE is a scalar) or along each axis
     * (SCALE is a 2-element vector: SCALE=[XSCALE,YSCALE]); by default,
     * SCALE=[1.0, 1.0].
     *
     * @param A the a
     */
    public static void fftPli2(double A[][])
    {	
        uint8(A);
        double[][] A_padded = fftShift(A);
        pli2(A_padded, COLORMAP_JET);
    }

    /**
     * Plot 2-D FFT array A as an image, taking care of "rolling" A and setting
     * correct world boundaries.  Keyword SCALE can be used to indicate the
     * "frequel" scale along both axis (SCALE is a scalar) or along each axis
     * (SCALE is a 2-element vector: SCALE=[XSCALE,YSCALE]); by default,
     * SCALE=[1.0, 1.0].
     *
     * @param A the a
     */
    public static void fftPli(double A[][])
    {	
        uint8(A);
        double[][] A_padded = fftShift(A);
        pli(A_padded, COLORMAP_JET);
    }


    /**
     * Shift zero-frequency component to center of spectrum.
     *
     * @param A the a
     * @return the double[][]
     */
    public static double[][] fftShift(double A[][])
    {	
        int H = A.length;
        int W = A[0].length;
        double A_shift[][] = new double[H][W];
        /* Higher-left to lower-right */
        for(int j = 0; j < W/2; j++)
        {
            for(int i = 0; i < H/2; i++)
            {
                A_shift[H - H/2 + i][W - W/2 + j] = A[i][j];
            }
        }
        /* Higher-right to lower-left */
        for(int j = 0; j < W/2; j++)
        {
            for(int i = 0; i < H/2; i++)
            {
                A_shift[H - H/2 + i][j] = A[i][j + W/2];
            }
        }
        /* Lower-left to higher-right */
        for(int j = 0; j < W/2; j++)
        {
            for(int i = 0; i < H/2; i++)
            {
                A_shift[i][W - W/2 + j] = A[i + H/2][j];
            }
        }
        /* Lower-right to higher-left*/
        for(int j = 0; j < W/2; j++)
        {
            for(int i = 0; i < H/2; i++)
            {
                A_shift[i][j] = A[i + H/2][j + W/2];
            }
        }
        return A_shift;
    }

    /**
     * Shift zero-frequency component to center of spectrum.
     *
     * @param A the a
     * @return the double[][]
     */
    public static double[] fftShift1D(double A[], int w, int h, int d)
    {   
        double A_shift[] = new double[w*h*d];
        for (int k = 0; k < d; k++)
        {
            /* Higher-left to lower-right */
            for(int j = 0; j < h/2; j++)
            {
                for(int i = 0; i < w/2; i++)
                {
                    A_shift[w*(h - h/2 + j) + w - w/2 + i] = A[i + w*j];
                    A_shift[w*(h - h/2 + j) + i] = A[w*j + i + w/2];
                    A_shift[w*j + w - w/2 + i] = A[w*(j + h/2) + i];
                    A_shift[i + w*j] = A[w*(j + h/2) + i + w/2];
                }
            }
        }
        return A_shift;
    }
    
    /**
     * Shift zero-frequency component to center of spectrum.
     *
     * @param A the a
     * @return the double[][]
     */
    public static double[] fftShift1D(double A[], int w, int h)
    {   
        double A_shift[] = new double[w*h];

            /* Higher-left to lower-right */
            for(int j = 0; j < h/2; j++)
            {
                for(int i = 0; i < w/2; i++)
                {
                    A_shift[w*(h - h/2 + j) + w - w/2 + i] = A[i + w*j];
                    A_shift[w*(h - h/2 + j) + i] = A[w*j + i + w/2];
                    A_shift[w*j + w - w/2 + i] = A[w*(j + h/2) + i];
                    A_shift[i + w*j] = A[w*(j + h/2) + i + w/2];
                }
            }
        return A_shift;
    }

    /**
     * Shift zero-frequency component to center of spectrum for a 3D array.
     *
     * @param A the a
     * @param w the width
     * @param h the height
     * @param d the depth
     * @return the double[]
     */
    public static double[] fftShift3D(double A[], int w, int h, int d)
    {   
        double A_shift[] = new double[w*h*d];
        int wh = w*h;
        for (int k = 0; k < d/2; k++)
        {
            for(int j = 0; j < h/2; j++)
            {
                for(int i = 0; i < w/2; i++)
                {
                    //Le coté face
                    //0,0,0-->w,h,d ou bas gauche face en haut droit fond
                    A_shift[w - w/2 + i + w*(h - h/2 + j) + wh*(d - d/2 + k)] = A[i + w*j + k*wh];
                    //w/2,0,0-->0,h/2,d/2 ou bas droite face en haut gauche fond
                    A_shift[i + w*(h - h/2 + j) + wh*(d - d/2 + k)] = A[i + w/2 + w*j + k*wh];
                    //haut gauche face en bas droite fond
                    A_shift[w - w/2 + i + w*j + wh*(d - d/2 + k)] = A[i + w*(j + h/2) + k*wh];
                    //haut droit face en bas gauche fond
                    A_shift[i + w*j+ wh*(d - d/2 + k)] = A[i + w/2 + w*(j + h/2) + k*wh];
                    
                    //le coté fond en face
                    A_shift[w - w/2 + i + w*(h - h/2 + j) + k*wh] = A[i + w*j + wh*(d - d/2 + k)];
                    A_shift[i + w*(h - h/2 + j) + k*wh] = A[i + w/2 + w*j + wh*(d - d/2 + k)];
                    A_shift[w - w/2 + i + w*j + k*wh] = A[i + w*(j + h/2) + wh*(d - d/2 + k)];
                    A_shift[i + w*j+ k*wh] = A[i + w/2 + w*(j + h/2) + wh*(d - d/2 + k)];
                }
            }
        }
        return A_shift;
    }
    
    /**
     * Convolution using fast fourier transform.
     *
     * @param img the img
     * @param h the h
     * @return convolution between img and h
     */
    public static double[][] fftConv(double img[][], double h[][])
    {
        int H = img.length; // hauteur
        int W = img[0].length; // largeur
        DoubleFFT_2D FFT2D = new DoubleFFT_2D(H, W);
        double[][] res = new double[H][W];
        /* convert to complex array */
        double[][] hC = real2complex(h);    
        double[][] imgC = real2complex(img);
        /* fft hC & img */
        FFT2D.complexForward(hC);
        FFT2D.complexForward(imgC);
        /* "Product" H*IMG */
        double[][] fft_img_h = hadamardProd(hC, imgC, 1);
        /* fft inverse of the product */
        FFT2D.complexInverse(fft_img_h, true);
        /* Real part of the inverse fft */
        for(int j = 0; j < W; j++)
        {
            for(int i = 0; i < H; i++)
            {
                res[i][j] = fft_img_h[i][2*j];
            }
        }
        return res;
    }

    /**
     * Compute an array of real value in array of complex
     * value thus the dimension will be [H,2*W].
     *
     * @param a the a
     * @return the double[][]
     */
    public static double[][] real2complex(double[][] a)
    {
        int H = a.length; // high
        int W = a[0].length; // weight
        double[][] c = new double[H][2*W];
        for(int j = 0; j < W; j++)
        {
            for (int i = 0; i < H; i++)
            {
                c[i][2*j] = a[i][j];
            }
        }
        return c;
    }

    /**
     * Average or mean value of array
     */
    public static double avg(double[] a)
    {
        return sum(a)/(a.length);
    }

    /**
     * Average or mean value of array
     */
    public static double avg(long[] a)
    {
        return sum(a)/(a.length);
    }

    /**
     * Average or mean value of array
     */
    public static double avg(double[][] a)
    {
        int H = a.length;
        int W = a[0].length;
        return sum(a)/(H*W);
    }

    /**
     * Average or mean value of array
     */
    public static double avg(double[][][] a)
    {
        int W = a.length;
        int H = a[0].length;
        int D = a[0][0].length;
        return sum(a)/(D*H*W);
    }

    /**
     * Sum of the values in the array
     */
    public static double sum(double a[])
    {
        int size = a.length;
        double sum = 0;
        for (int i = 0; i < size; i++)
        {
            sum += a[i];
        }
        return sum;
    }

    /**
     * Sum of the values in the array
     */
    public static double sum(double a[], int begin, int end)
    {
        double sum = 0;
        for (int i = begin; i <= end; i++)
        {
            sum += a[i];
        }
        return sum;
    }

    /**
     * Sum of the values in the array
     */
    public static long sum(long array[])
    {
        int size = array.length;
        long sum = 0;
        for (int i = 0; i < size; i++)
        {
            sum += array[i];
        }
        return sum;
    }

    /**
     * Sum of the values in the array
     */
    public static double sum(double array[][])
    {
        int H = array.length;
        double sum = 0;
        for (int i = 0; i < H; i++)
        {
            sum += sum(array[i]);
        }
        return sum;
    }

    /**
     * Sum of the values in the array
     */
    public static double sum(double array[][][])
    {
        int D = array.length;
        double sum = 0;
        for (int k = 0; k < D; k++)
        {
            sum += sum(array[k]);
        }
        return sum;
    }

    /**
     * Inner product.
     *
     * @param a the a
     * @param b the b
     * @return the double[]
     */
    public static double innerProd(double a[], double b[])
    {
        int L = a.length;
        double out = 0;
        for (int i = 0; i < L; i++)
        {
            out += a[i]*b[i];
        }
        return out;
    }

    public static double[] im2array(String imageName)
    {
        File fileI = new File(imageName);
        BufferedImage I = null;
        try {
            I = ImageIO.read(fileI);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int H = I.getHeight();
        int W = I.getWidth();
        double ImArray[] = new double[H*W];
        WritableRaster raster = I.getRaster();
        for (int j = 0; j < H; j++) {
            for (int i = 0; i < W; i++) {
                int[] pixels = raster.getPixel(j, i, (int[]) null);
                //System.out.println(Arrays.toString(pixels));
                ImArray[i + j*W] = pixels[0];
            }
        }
        return ImArray;
    }


    public static double[][] img_pad(double img[][], int newW, int newH, int just)
    {   
        int oldH = img.length; // hauteur
        int oldW = img[0].length; // largeur
        double New[][] = new double[newH][newW];
        switch (just)
        {
        case 0:
            /* image will not be centered */
            for(int i = 0; i < oldH; i++)
            {
                for(int j = 0; j < oldW; j++)
                {
                    New[i][j] = img[i][j];
                }
            }
            break;
        case 1:
            /* image will be centered */
            int i1 = (newW - oldW)/2;
            int i2 = (newH - oldH)/2;
            for(int i = 0; i < oldH; i++)
            {
                for(int j = 0; j < oldW; j++)
                {
                    New[i2 + i][j + i1] = img[i][j];
                }
            }
            break;
        case -1:
            /* preserve FFT indexing */
            int oldW2 = oldW/2;
            int oldH2 = oldH/2;
            if (oldW2 != 0 || oldH2 != 0) // haut gauche->bas droit
            {
                for(int i = 0; i < oldH2; i++)
                {
                    for(int j = 0; j < oldW2; j++)
                    {
                        New[newH - oldH2 + i][newW - oldW2 + j] = img[i][j];
                    }
                }
            }
            if(oldW2 != 0) // Haut droit->bas gauche
            {
                for(int i = 0; i < oldH2; i++)
                {
                    for(int j = 0; j < oldW-oldW2; j++)
                    {
                        New[newH - oldH2 + i][j] = img[i][j + oldW2];
                    }
                }
            }
            if(oldH2 != 0) // bas gauche->haut droit
            {
                for(int i = 0; i < oldH - oldH2; i++)
                {
                    for(int j = 0; j < oldW2; j++)
                    {
                        New[i][newW - oldW2 + j] = img[i + oldH2][j];
                    }
                }
            }

            for(int i = 0; i < oldH-oldH2; i++) // Bas droit->Haut gaucHe
            {
                for(int j = 0; j < oldW-oldW2; j++)
                {
                    New[i][j] = img[i + oldH2][j + oldW2];
                }
            }
            break;
        default:
            System.out.println("bad value for keyword JUST");            
        }

        return New;
    }

    
    public static double[] img_pad1d(double img[], int oldH, int oldW, int newW, int newH, int just)
    {   int L = img.length;
    double New[] = new double[newH*newW];
    switch (just)
    {
    case 0:
        /* image will not be centered */
        for(int i = 0; i < L; i++)
        {
            New[i] = img[i];
        }
        break;
    case 1:
        /* image will be centered */
        int i1 = (newW - oldW)/2;
        int i2 = (newH - oldH)/2;
        for(int j = 0; j < oldH; j++)
        {
            for(int i = 0; i < oldW; i++)
            {
                New[i2 + j + (i + i1)*oldW] = img[i*oldW + j];
            }
        }
        break;
    case -1:
        /* preserve FFT indexing */
        int oldW2 = oldW/2;
        int oldH2 = oldH/2;
        if (oldW2 != 0 || oldH2 != 0) // haut gauche->bas droit
        {
            for(int j = 0; j < oldH2; j++)
            {
                for(int i = 0; i < oldW2; i++)
                {
                    New[newH - oldH2 + j + (newW - oldW2 + i)*oldW2] = img[i*oldW2 + j];
                }
            }
        }
        if(oldW2 != 0) // Haut droit->bas gauche
        {
            for(int i = 0; i < oldH2; i++)
            {
                for(int j = 0; j < oldW-oldW2; j++)
                {
                    New[newH - oldH2 + i][j] = img[i][j + oldW2];
                }
            }
        }
        if(oldH2 != 0) // bas gauche->haut droit
        {
            for(int i = 0; i < oldH - oldH2; i++)
            {
                for(int j = 0; j < oldW2; j++)
                {
                    New[i][newW - oldW2 + j] = img[i + oldH2][j];
                }
            }
        }

        for(int i = 0; i < oldH-oldH2; i++) // Bas droit->Haut gaucHe
        {
            for(int j = 0; j < oldW-oldW2; j++)
            {
                New[i][j] = img[i + oldH2][j + oldW2];
            }
        }
        break;
    default:
        System.out.println("bad value for keyword JUST");            
    }

    return New;
    }

    /**
     * Hadamard product between two matrices 
     * Hadamard (also known as the element-wise product)
     * product A ○ B is a matrix of the same dimensions,
     * the i, j element of A is multiplied with the i, j element of B.
     *
     * @param a the a
     * @param b the b
     * @param isComplex the is complex
     * @return the double[][]
     */
    public static double[][] hadamardProd(double a[][], double b[][], int isComplex)
    {
        int H = a.length;
        int W = a[0].length;
        double out[][] = new double[H][W];
        if( isComplex == 0 )
        {
            for (int j = 0; j < W; j++)
            {
                for (int i = 0; i < H; i++)
                {
                    out[i][j] = a[i][j]*b[i][j];

                }
            }
        }
        else
        {
            for (int j = 0; j < W; j++)
            {
                for (int i = 0; i < H; i++)
                {
                    out[i][2*j] = a[i][2*j]*b[i][2*j] - a[i][2*j + 1]*b[i][2*j + 1];
                    out[i][2*j + 1] = a[i][2*j]*b[i][2*j + 1] + a[i][2*j + 1]*b[i][2*j];
                }
            }
        }
        return out;
    }

    //FIXME faire un ax + y
    /**
     * Sum of the values in the array.
     *
     * @param a the a
     * @param b the b
     * @param sign the sign
     * @return the double[][]
     */
    public static double[][] sumArrays(double a[][], double b[][], String sign)
    {
        int H = a.length;
        int W = a[0].length;
        double out[][] = new double[H][W];
        if(sign == "+")
        {
            for (int j = 0; j < W; j++) {
                for (int i = 0; i < H; i++) {
                    out[i][j] = a[i][j] + b[i][j];
                }
            }
        }else{
            for (int j = 0; j < W; j++) {
                for (int i = 0; i < H; i++) {
                    out[i][j] = a[i][j] - b[i][j];
                }
            }
        }
        return out;
    }

    /**
     * Standard deviation of an 1d array σ
     */
    public static double std(double a[])
    {
        double V = var(a);
        return Math.sqrt(V);
    }

    /**
     * Standard deviation of the matrix σ
     */
    public static double std(double a[][])
    {
        double V = var(a);
        return Math.sqrt(V);
    }

    /**
     * Standard deviation of the matrix σ
     */
    public static double std(double a[][][])
    {
        double V = var(a);
        return Math.sqrt(V);
    }

    /**
     * Variance σ² of a 1d array
     * var = 1/N \sum_i (x_i - E[x])^2 = E[x'x]-E[x]^2 
     */
    public static double var(double a[])
    {
        double mean = avg(a);
        double out = 0;
        for(int k = 0; k < a.length; k++)
        {
            out = out + a[k]*a[k];
        }
        return out/a.length - mean*mean;
    }

    /**
     * Variance of the matrix 
     */
    public static double var(double a[][])
    {
        double mean = avg(a);
        double out;
        out = avg(hadamardProd(a, a, 0)) - mean*mean;
        return out;
    }

    /**
     * Variance σ² of the 3d matrix
     * var = 1/N \sum_i (x_i - E[x])^2
     */
    public static double var(double a[][][])
    {
        double x = 0;
        double mean = avg(a);
        double N = 0;
        double tmp = 0;
        for(int k = 0; k < a[0][0].length; k++)
        {
            for (int j = 0; j < a[0].length; j++)
            {
                for (int i = 0; i < a.length; i++)
                {
                    tmp = a[i][j][k] - mean;
                    x += tmp*tmp;
                    N++;
                }
            }
        }
        return x/N;
    }

    /**
     * Some information about the matrix
     * height, width, minimum, maximum, average, variance, standard deviation.
     *
     * @param a the a
     */
    public static void stat(double a[][])
    {
        System.out.println("H " + a.length + " W " + a[0].length);
        System.out.println("min " + min(a) + " max " + max(a));
        System.out.println("avg " + avg(a) + " var " + var(a) + " std " + std(a));
    }

    public static void stat(double a[])
    {
        System.out.println("size " + a.length);
        System.out.println("min " + min(a) + " max " + max(a));
        System.out.println("avg " + avg(a) + " var " + var(a) + " std " + std(a));
    }

    public static void stat(double a[], int begin, int end)
    {
        double[] b = new double[end - begin + 1];
        for(int k = begin; k <= end; k++)
        {
            b[k - begin] = a[k];
        }
        System.out.println("size " + b.length);
        System.out.println("min " + min(b) + " max " + max(b));
        System.out.println("avg " + avg(b) + " var " + var(b) + " std " + std(b));
    }

    //FIXME add poisson
    /**
     * Add noise to image.
     *
     * @param img image
     * @param type GAUSSIAN : for gaussian white noise, POISSON
     * @param arg1 standard deviation of the gaussian noise
     * @param arg2 mean of the gaussian noise
     * @return the double[][]
     */
    public static double[][] imnoise(double[][] img, int type, double arg1, double arg2)
    {
        int H = img.length;
        int W = img[0].length;
        double[][] imnoise = new double[H][W];
        switch (type)
        {
        case GAUSSIAN:
            Random rand = new Random();
            double std = Math.sqrt(arg1);
            double mean = arg2;
            /* converts the image in range [0, 1] */
            im2double(img);
            /* Add noise */
            for (int j = 0; j < W; j++)
            {
                for (int i = 0; i < H; i++)
                {
                    imnoise[i][j] = img[i][j] + std*rand.nextGaussian() + mean;
                }
            }
            uint8(imnoise);
            break;
        default:
            throw new IllegalArgumentException("The type does not exist");
        }
        return imnoise;
    }

    /**
     * Fspecial average.
     *
     * @param arg1 the arg1
     * @return the double[][]
     */
    public static double[][] fspecialAverage(int[] arg1)
    { 
        double[][] ha = new double[arg1[0]][arg1[1]];
        double coef = 1./(arg1[0]*arg1[1]);
        int H = arg1[0];
        int W = arg1[1];
        for (int k2 = 0; k2 < H; k2++)
        {
            for (int k1 = 0; k1 < W; k1++)
            {
                ha[k2][k1] = coef;
            }
        }
        return ha;
    }

    /**
     * Create predefined 2-D filter.
     *
     * @param type type of the filter
     * @param arg1 the arg1
     * @return the double[][]
     */
    public static double[][] fspecial(int type, int arg1)
    { 
        switch (type)
        {
        case DISK:
            double cd;
            int radius = arg1;
            int diameter = 2*radius;
            double[][] r = cartesDist2D(diameter, diameter);
            double[][] mask = new double[diameter][diameter];
            double[][] hd = new double[diameter][diameter];
            for (int j = 0; j < r.length; j++)
            {
                for (int i = 0; i < r.length; i++)
                {
                    if (r[i][j] <= radius)
                    {
                        mask[i][j] =  1;
                    }
                }
            }
            cd = sum(mask);
            for (int j = 0; j < r.length; j++)
            {
                for (int i = 0; i < r.length; i++)
                {
                    if (mask[i][j] == 1)
                    {
                        hd[i][j] =  1/cd;
                    }
                }
            }
            return hd;
        default:
            throw new IllegalArgumentException("The type does not exist");
        }
    }

    /**
     * Create predefined 2-D filter.
     *
     * @param type type of the filter
     * @param arg1 Height
     * @param arg2 Width
     * @return the double[][]
     */
    public static double[][] fspecial(int type, int[] arg1, double arg2)
    { 
        switch (type)
        {
        case AVERAGE:
            double[][] ha = new double[arg1[0]][arg1[1]];
            double coef = 1./(arg1[0]*arg1[1]);
            int H = arg1[0];
            int W = arg1[1];
            for (int k2 = 0; k2 < H; k2++)
            {
                for (int k1 = 0; k1 < W; k1++)
                {
                    ha[k2][k1] = coef;
                }
            }
            return ha;
        case GAUSSIAN:
            double[][] hg = new double[arg1[0]][arg1[1]];
            double A = 2*arg2*arg2;
            double B = 1/Math.sqrt(Math.PI*A);
            int bk1 = (-arg1[1]+1)/2;
            int ek1 = arg1[1]/2;
            int bk2 = (-arg1[0]+1)/2;
            int ek2 = arg1[0]/2;
            for (int k2 = bk2; k2 <= ek2; k2++)
            {
                for (int k1 = bk1; k1 <= ek1; k1++)
                {
                    hg[k2 - bk2][k1 - bk1] = B*Math.exp(-(k1*k1+k2*k2)/A);
                }
            }
            return hg;
        default:
            throw new IllegalArgumentException("The type does not exist");
        }
    }

    /**
     * Create predefined 2-D filter.
     *
     * @param type type of the filter
     * @param arg1 Height
     * @param arg2 Width
     * @return the double[][]
     */
    public static double[] fspecial1D(int type, int[] arg1, double arg2)
    { 
        int L = arg1[0]*arg1[1];
        switch (type)
        {
        case AVERAGE:

            double[] ha = new double[L];
            double coef = 1./(L);
            for (int i = 0; i < L; i++)
            {
                ha[i] = coef;
            }
            return ha;
        case GAUSSIAN:
            double[] hg = new double[L];
            double A = 2*arg2*arg2;
            double B = 1/Math.sqrt(Math.PI*A);
            int bk1 = (-arg1[1]+1)/2;
            int ek1 = arg1[1]/2;
            int bk2 = (-arg1[0]+1)/2;
            int ek2 = arg1[0]/2;
            for (int k2 = bk2; k2 <= ek2; k2++)
            {
                for (int k1 = bk1; k1 <= ek1; k1++)
                {
                    // hg[k2 - bk2][k1 - bk1] = B*Math.exp(-(k1*k1+k2*k2)/A);
                }
            }
            return hg;
        default:
            throw new IllegalArgumentException("The type does not exist");
        }
    }


    /**
     * Create predefined 3*3 filter kernel.
     *
     * @param type type of the filter : AVERAGE, DISK,
     * SOBEL, PREWITT, KIRSH
     * @return the double[][]
     */
    public static double[] fspecial1D(int type)
    { 
        switch (type)
        {
        case AVERAGE:
            double ca = 1./9;
            double[] ha = {ca,ca,ca,ca,ca,ca,ca,ca,ca};
            return ha;
        case DISK:
            double cd;
            int radius = 5;
            int diameter = 2*radius;
            double[] r = cartesDist1D(diameter, diameter);
            double[] mask = new double[diameter*diameter];
            double[] hd = new double[diameter*diameter];
            for (int i = 0; i < r.length; i++)
            {
                if (r[i] <= radius)
                {
                    mask[i] =  1;
                }
            }
            cd = sum(mask);
            for (int i = 0; i < r.length; i++)
            {
                if (mask[i] == 1)
                {
                    hd[i] =  1/cd;
                }
            }
            return hd;
        case SOBEL:
            double[] hs = {1,2,1,0,0,0,-1,-2,-1};
            return hs;
        case PREWITT:
            double[] hp = {1,1,1,0,0,0,-1,-1,-1};
            return hp;
        case KIRSH:
            double[] hk = {3,3,3,3,0,3,-5,-5,-5};
            return hk;
        default:
            throw new IllegalArgumentException("The type does not exist");
        }
    }

    /**
     * Create predefined 3*3 filter kernel.
     *
     * @param type type of the filter : AVERAGE, DISK,
     * SOBEL, PREWITT, KIRSH
     * @return the double[][]
     */
    public static double[][] fspecial(int type)
    { 
        switch (type)
        {
        case AVERAGE:
            double ca = 1./9;
            double[][] ha = {{ca,ca,ca},{ca,ca,ca},{ca,ca,ca}};
            return ha;
        case DISK:
            double cd;
            int radius = 5;
            int diameter = 2*radius;
            double[][] r = cartesDist2D(diameter, diameter);
            double[][] mask = new double[diameter][diameter];
            double[][] hd = new double[diameter][diameter];
            for (int j = 0; j < r.length; j++)
            {
                for (int i = 0; i < r.length; i++)
                {
                    if (r[i][j] <= radius)
                    {
                        mask[i][j] =  1;
                    }
                }
            }
            cd = sum(mask);
            for (int j = 0; j < r.length; j++)
            {
                for (int i = 0; i < r.length; i++)
                {
                    if (mask[i][j] == 1)
                    {
                        hd[i][j] =  1/cd;
                    }
                }
            }
            return hd;
        case SOBEL:
            double[][] hs = {{1,2,1},{0,0,0},{-1,-2,-1}};
            return hs;
        case PREWITT:
            double[][] hp = {{1,1,1},{0,0,0},{-1,-1,-1}};
            return hp;
        case KIRSH:
            double[][] hk = {{3,3,3},{3,0,3},{-5,-5,-5}};
            return hk;
        default:
            throw new IllegalArgumentException("The type does not exist");

        }
    }

    /**
     * Returns 2d array (column major) of a 1d array.
     *
     * @param In 1d array of double
     * @param H the h
     * @return 2d array
     */
    public static double[] getArray(double[] In, int width, int height, int depth)
    {
        int L = width*height;
        double Out[] = new double[L];
        for (int i = 0; i < L; i++)
        {
            Out[i] = In[depth*L + i];
        }
        return Out;
    }

    public static double[] transpose(double array[], int W, int H, int D)
    {
        int L = array.length;
        double[] out = new double[L];
        for (int k = 0; k < D; k++)
        {
            for (int j = 0; j < H; j++)
            {
                for (int i = 0; i < W; i++)
                {
                    out[j + H*i + k*W*H] = array[i + W*j + k*W*H];
                }
            }
        }
        return out;
    }

    /**
     * Write stat.
     *
     * @param a the a
     * @param name the name
     */
    public static void writeStat(double a[][], String name)
    {
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(new File("temp"), true));

            StringBuffer sb = new StringBuffer();

            for(int i =0;i<=5;i++){
                sb.append(name); 
            }

            sb.append("H " + a.length + " W " + a[0].length + "\n min " + min(a) + " max " + max(a) +
                    "\n avg " + avg(a) + " var " + var(a) + " std " + std(a));
            bw.write(sb.toString());
            bw.close();
        }
        catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * X y2 xz.
     *
     * @param inXY the in xy
     * @return the double[][][]
     */
    public static double[][][] XY2XZ(double inXY[][][])
    {
        int Nz = inXY.length;
        int Ny = inXY[0].length;
        int Nx = inXY[0][0].length;
        double outXZ[][][] = new double[Ny][Nz][Nx];
        for(int j = 0; j<Nx; j++)
        {
            for (int z = Nz-1; z >= 0; z--) {
                for (int i = 0; i < Nx; i++) {
                    outXZ[j][Math.abs(z - 1)][i] = inXY[z][i][j];
                }
            }
        }
        return outXZ;
    }

    /**
     * Cum sum.
     *
     * @param tab the tab
     * @return the double[]
     */
    public static double[] cumSum(double tab[])
    {
        int L = tab.length;
        double out[] = new double[L];
        out[0] = tab[0];
        for(int i = 1; i < L; i++)
        {
            out[i] = out[i-1] + tab[i];
        }
        return out;
    }
    
    /**
     * Cum sum.
     *
     * @param tab the tab
     * @return the double[]
     */
    public static double mse(double yEstimate[], double[] yTrue)
    {
        int n = yTrue.length;
        double tmp1 = 0;
        double out = 0;
        for(int i = 0; i < n; i++)
        {
            tmp1 = yTrue[i] - yEstimate[i];
            out = out + tmp1*tmp1;
        }
        return out/n;
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