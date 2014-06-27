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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;

public class MathUtils {
    
    public static final int COLORMAP_GRAYSCALE = 0;
    public static final int COLORMAP_JET = 1;
    //FIXME changer le span1 en span et les span dans les autres fonction en indgen
    /**
     * Check if the number is even. 
     *
     * @param  x  an integer value
     * @return return a boolean
     */
    public static boolean even(int x)
    {
        return ( x % 2 == 0 );
    }

    /**
     * Returns the factorial of a number. 
     *
     * @param  n  an integer value
     * @return the factorial of the number
     */
    public static long factorial(long n)
    {
        if (n == 0)
            return 1;
        else
            return n * factorial(n-1);
    }

    /**
     * Return an array of cartesian distance of
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
        double[] x = span((-W+1)/2, W/2, 1);
        double[] y = span((-H+1)/2, H/2, 1);
        for( int j = 0; j < W; j++)
            for( int i = 0; i < H; i++)
                R[i][j] = Math.sqrt(x[i] * x[i] + y[j] * y[j]);
        return R;
    }

    public static double[] cartesDist1D(int W, int H)
    {
        double R[] = new double[H*W];
        double[] x = span((-W+1)/2, W/2, 1);
        double[] y = span((-H+1)/2, H/2, 1);
        for( int j = 0; j < W; j++)
            for( int i = 0; i < W; i++)
                R[i*W + j] = Math.sqrt(x[i] * x[i] + y[j] * y[j]);
        return R;
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
            for( int i = 0; i < H; i++)
                THETA[i][j] = Math.atan2( y[i], x[j]);
        return THETA;
    }

    /**
     * Generate index of FFT frequencies/coordinates.
     *
     * @param L width of the array
     * @return FFT frequencies along a dimension of length LEN.
     */
    public static double[] fftIndgen(int L)
    {   

        double[] k = new double[L];
        double u[] = span(0, L - 1, 1);
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

    public static double[] fftDist(int L)
    {
        double x[] = new double[L];
        double R[] = new double[L];
        x = fftIndgen(L);
        for( int i = 0; i < L; i++)
            R[i] = Math.sqrt(x[i] * x[i] + x[i] * x[i]);
        return R;
    }

    /**
     * Compute length of FFT frequencies/coordinates.
     *
     * @param W width
     * @param H height
     * @return Euclidian lenght of spatial frequencies in frequel units for a
     * FFT of dimensions [W,H].
     */
    public static double[][] fftDist(int W, int H)
    {
        double x[] = new double[W];
        double y[] = new double[H];
        double R[][] = new double[H][W];

        x = fftIndgen(W);
        y = fftIndgen(H);
        for( int j = 0; j < W; j++)
            for( int i = 0; i < H; i++)
                R[i][j] = Math.sqrt(x[i] * x[i] + y[j] * y[j]);
        return R;
    }

    public static double[] cartesAngle1D(int W, int H)
    {
        double THETA[] = new double[H*W];
        double[] x = span((-W+1)/2, W/2, 1);
        double[] y = span((-H+1)/2, H/2, 1);
        for( int j = 0; j < W; j++)
            for( int i = 0; i < H; i++)
                THETA[i*W + j] = Math.atan2( y[i], x[j]);
        return THETA;
    }

    public static double[][] cartesAngle2D(int W, int H)
    {
        double THETA[][] = new double[H][W];
        double[] x = span((-W+1)/2, W/2, 1);
        double[] y = span((-H+1)/2, H/2, 1);
        for( int j = 0; j < W; j++)
            for( int i = 0; i < H; i++)
                THETA[i][j] = Math.atan2( y[i], x[j]);
        return THETA;
    }

    /**
     * Returns array of N doubles equally spaced from START to STOP.
     *
     * @param W width
     * @param H height
     * @return Euclidian lenght of spatial frequencies in frequel units for a
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
     * START to STOP with a step of 1
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
     * START to STOP with a SCALE
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
     * @param end of the array
     * @param n number of element in the array
     * @return array of N doubles equally spaced from START to STOP.
     */
    public static double[] span1(double start, double stop, int n)
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
     *
     */
    public static double[] scaleArrayTo8bit(double[] A)
    {
        int L = A.length;
        double[] scaleA = new double[L];
        double minScaleA = min(A);
        double maxScaleA = max(A);
        double deltaScaleA = maxScaleA - minScaleA;
        for(int i = 0; i < L; i++)
            scaleA[i] = (A[i] - minScaleA)*255/deltaScaleA;

        return scaleA;
    }

    /**
     * Scale array values into a 8bit (between 0 and 255).
     *
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
            for(int i = 0; i < H; i++)
                scaleA[i][j] = (A[i][j] - minScaleA)*255/deltaScaleA;

        return scaleA;
    }

    public static double[][] conj1(double[][] A)
    {
        int H = A.length;
        int W = A[0].length;
        double[][] conjA = new double[H][W];
        for(int j = 0; j < W/2; j++)
            for(int i = 0; i < H; i++)
            {
                conjA[i][2*j] = A[i][2*j];
                conjA[i][2*j + 1] = -A[i][2*j + 1];
            }
        return conjA;
    }
    
    public static void conj2(double[][] A)
    {
        int H = A.length;
        int W = A[0].length;
        double[][] conjA = new double[H][W];
        for(int j = 0; j < W/2; j++)
            for(int i = 0; i < H; i++)
                conjA[i][2*j + 1] = -A[i][2*j + 1];
    }
    
    /**
     * Scale array values into a 8bit (between 0 and 255).
     *
     */
    public static void uint8(double[][] A)
    {
        int H = A.length;
        int W = A[0].length;
        double minScaleA = min(A);
        double maxScaleA = max(A);
        double deltaScaleA = maxScaleA - minScaleA;
        for(int j = 0; j < W; j++)
            for(int i = 0; i < H; i++)
                A[i][j] = (A[i][j] - minScaleA)*255/deltaScaleA;
    }

    /**
     * Returns the squared absolute value of a 2d array.
     *
     * @param IN array
     * @return square absolute value.
     */
    public static double[][] abs2(double[][] IN)
    {
        int H = IN.length;
        int W = IN[0].length/2;
        double[][] OUT = new double[H][W];
        for(int j = 0; j < W; j++)
            for(int i = 0; i < H; i++)
                OUT[i][j] = IN[i][2*j]*IN[i][2*j] + IN[i][2*j + 1]*IN[i][2*j + 1];

        return OUT;
    }

    /**
     * Returns the squared absolute value of 1d array.
     *
     * @param IN array
     * @return square absolute value.
     */
    public static double[] abs2(double[] IN)
    {
        int L = IN.length/2;
        double[] out = new double[L];
        for(int i = 0; i < L; i++)
            out[i] = IN[2*i]*IN[2*i] + IN[2*i + 1]*IN[2*i + 1];

        return out;
    }

    /**
     * Compute the minimum value of a 1d array.
     *
     * @param matrix array
     * @return square absolute value.
     */
    public static double min(double[] matrix)
    {
        double min = matrix[0];
        for (int i = 0; i < matrix.length; i++)
        {
            if (min > matrix[i])
                min = matrix[i];
        }
        return min;
    }

    /**
     * Compute the minimum value of a 2d array.
     *
     * @param matrix array
     * @return square absolute value.
     */
    public static double min(double[][] matrix) {
        double min = matrix[0][0];
        for (int col = 0; col < matrix.length; col++) {
            for (int row = 0; row < matrix[col].length; row++) {
                if (min > matrix[col][row]) {
                    min = matrix[col][row];
                }
            }
        }
        return min;
    }

    /**
     * Compute the maximum value of a 1d array.
     */
    public static double max(double[] matrix)
    {
        double max = matrix[0];
        for (int i = 0; i < matrix.length; i++)
        {
            if (max < matrix[i])
                max = matrix[i];
        }
        return max;
    }

    /**
     * Compute the maximum value of a 2d array.
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

    public static void showArrayI(double A[], int W)
    {	
        ColorMap map = ColorMap.getJet(256);
        int L = A.length;
        int H = L/W;
        double S[];
        S = scaleArrayTo8bit(A);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);

        for(int j = 0; j < W; j++)
        {
            for(int i = 0; i < H; i++)
            {
                Color b = map.table[ (int) S[i*W + j] ];
                bufferedI.setRGB(j, i, b.getRGB());	// j, i inversé
            }
        }

        JFrame frame = new JFrame();
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(bufferedI));
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void printArray(double A[])
    {   
        System.out.println(Arrays.toString(A));
    }

    public static void printArray(double A[][])
    {	
        int H = A.length;
        for(int i = 0; i < H; i++ )
            System.out.println(Arrays.toString(A[i]));
    }

    /**
     * Display image of an 2d array
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
            System.out.println("Bad value for colorMap");
            break;
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
     * Different from the "pli" function, uses "naviguablePanel" for a better displaying
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
            System.out.println("Bad value for colorMap");
            break;
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
     */
    public static void fftPli(double A[][])
    {	
        uint8(A);
        double[][] A_padded = fftShift(A);
        pli(A_padded, COLORMAP_JET);
    }

    
    /**
     * Shift zero-frequency component to center of spectrum
     * 
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

    public static double[][] fftConv(double img[][], double h[][])
    {
        int H = img.length; // hauteur
        int W = img[0].length; // largeur
        double[][] hC = new double[H][2*W];
        double[][] imgC = new double[H][2*W];
        DoubleFFT_2D FFT2D = new DoubleFFT_2D(H, W);
        double[][] fft_img_h = new double[H][2*W];
        double[][] res = new double[H][W];

        /* h complex */      
        for(int i = 0; i < H; i++)
        {
            for (int j = 0; j < W; j++)
            {
                hC[i][2*j] = h[i][j];
            }
        }
        /* complex image */      
        for(int i = 0; i < H; i++)
        {
            for (int j = 0; j < W; j++)
            {
                imgC[i][2*j] = img[i][j];
            }
        }
        /* fft hC & img */
        FFT2D.complexForward(hC);
        FFT2D.complexForward(imgC);
        /* Product H*IMG */
        for(int i = 0; i < H; i++)
        {
            for (int j = 0; j < W; j++)
            {
                fft_img_h[i][2*j] = hC[i][2*j]*imgC[i][2*j] - hC[i][2*j + 1]*imgC[i][2*j + 1];
                fft_img_h[i][2*j + 1] = hC[i][2*j]*imgC[i][2*j + 1] + hC[i][2*j + 1]*imgC[i][2*j];
            }
        }
        /* fft inverse of the product */
        FFT2D.complexInverse(fft_img_h, true);
        /* Real part of the inverse fft */
        for(int i = 0; i < H; i++)
        {
            for(int j = 0; j < W; j++)
            {
                res[i][j] = fft_img_h[i][2*j];
            }
        }
        return res;
    }

    /**
     * Average or mean value of array
     */
    public static double avg(double[] array)
    {
        int L = array.length;
        double mean = 0;
        for(int i = 0; i < L; i++)
            mean = mean + array[i];
        return mean/L;
    }

    /**
     * Average or mean value of array
     */
    public static double avg(long[] array)
    {
        int L = array.length;
        double mean = 0;
        for(int i = 0; i < L; i++)
            mean = mean + array[i];
        return mean/L;
    }

    /**
     * Average or mean value of array
     */
    public static double avg(double[][] array)
    {
        int H = array.length;
        int W = array[0].length;
        double mean = 0;
        for(int i = 0; i < H; i++)
            for(int j = 0; j < W; j++)
                mean = mean + array[i][j];

        return mean/(H*W);
    }

    /**
     * Sum of the values in the array
     */
    public static double sum(double array[])
    {
        int size = array.length;
        double sum = 0;
        for (int i = 0; i < size; i++) {
            sum += array[i];
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
        for (int i = 0; i < size; i++) {
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
        int W = array[0].length;
        double sum = 0;
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                sum += array[i][j];
            }
        }
        return sum;
    }

    /**
     * Inner product
     */
    public static double[] innerProd(double a[], double b[])
    {
        int L = a.length;
        double out[] = new double[L];
        for (int i = 0; i < L; i++) {
            out[i] = a[i]*b[i];
        }
        return out;
    }

    /**
     * Hadamard product
     */
    public static double[][] hadamardProd(double a[][], double b[][])
    {
        int H = a.length;
        int W = a[0].length;
        double out[][] = new double[H][W];
        for (int j = 0; j < W; j++)
        {
            for (int i = 0; i < H; i++)
            {
                out[i][j] = a[i][j]*b[i][j];
            }
        }
        return out;
    }

    /**
     * Sum of the values in the array
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

    public static double std(double a[][])
    {
        double V = var(a);
        return Math.sqrt(V);
    }

    public static double var(double a[][])
    {
        double mean = avg(a);
        double out;
        out = avg(hadamardProd(a, a)) - mean*mean;
        return out;
    }

    public static void stat(double a[][])
    {
        System.out.println("H " + a.length + " W " + a[0].length);
        System.out.println("min " + min(a) + " max " + max(a));
        System.out.println("avg " + avg(a) + " var " + var(a) + " std " + std(a));
    }

    public static double[][] imnoise(double[][] img, String type, double arg1, double arg2)
    {
        int H = img.length;
        int W = img[0].length;
        double[][] imnoise = new double[H][W];
        switch (type)
        {
        case "gaussian":
            Random rand = new Random();
            double std = Math.sqrt(arg1);
            double mean = arg2;
            double minImg = min(img);
            double maxImg = max(img);
            double delta = maxImg - minImg;
            /* converts the image in range [0, 1] */
            for (int j = 0; j < W; j++)
            {
                for (int i = 0; i < H; i++)
                {
                    img[i][j] = (img[i][j] - minImg)/delta;
                }
            }
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
            System.err.println("The type does not exist");
        }
        return imnoise;
    }
    /* FIX :
     *      [x,y] = meshgrid(-c2:c2, -r2:r2);
     *radsqrd = x.^2 + y.^2;
     *f = exp(-radsqrd/(2*sigma^2));
     *f = f/sum(f(:));
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

    public static double[][] fspecial(String type, int arg1)
    { 
        switch (type)
        {
        case "disk":
            double cd;
            int radius = arg1;
            int diameter = 2*radius;
            double[][] r = MathUtils.cartesDist2D(diameter, diameter);
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
            cd = MathUtils.sum(mask);
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
            return new double[arg1][arg1];
        }
    }

    public static double[][] fspecial(String type, int[] arg1, double arg2)
    { 
        switch (type)
        {
        case "average":
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
        case "gaussian":
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
            return new double[arg1[0]][arg1[1]];
        }
    }

    public static double[][] fspecial(String type)
    { 
        switch (type)
        {
        case "average":
            double ca = 1./9;
            double[][] ha = {{ca,ca,ca},{ca,ca,ca},{ca,ca,ca}};
            return ha;
        case "disk":
            double cd;
            int radius = 5;
            int diameter = 2*radius;
            double[][] r = MathUtils.cartesDist2D(diameter, diameter);
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
            cd = MathUtils.sum(mask);
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
        case "sobel":
            double[][] hs = {{1,2,1},{0,0,0},{-1,-2,-1}};
            return hs;
        case "prewitt":
            double[][] hp = {{1,1,1},{0,0,0},{-1,-1,-1}};
            return hp;
        case "kirsh":
            double[][] hk = {{3,3,3},{3,0,3},{-5,-5,-5}};
            return hk;
        default:
            return new double[3][3];
        }
    }

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