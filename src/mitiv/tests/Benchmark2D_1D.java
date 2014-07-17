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

package mitiv.tests;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;

import mitiv.deconv.Deconvolution;
import mitiv.utils.CommonUtils;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;

/**
 * This class is to test Jtransform and the effect to use array 2D or 1D arrays
 * 
 * Also some micro benchmark on one kind of array manipulation
 * 
 * @author Leger Jonathan
 *
 */
public class Benchmark2D_1D {

    static double [] tab1D;
    static double [][] tab2D;

    static double moy = 0;
    static double moytmp = 0;

    static boolean debug = false;

    static int size = 50;

    static int NB_BENCH = 20;
    static BufferedImage imageout;
    
    private static String pathImage = "/home/light/workspace2/FATRAS/saturn.png";
    private static String pathPsf = "/home/light/workspace2/FATRAS/saturn_psf.png";

    private static final int greyToColor(double g, double alpha, double beta)
    {
        double x = alpha*g + beta;
        int i;
        if (x < 0.5) {
            i = 0;
        } else if (x > 254.5) {
            i = 255;
        } else {
            i = (int)(x + 0.5);
        }
        return i;
        //return (i << 16) & 0x00FF0000 | (i << 8) & 0x0000FF00 |  i & 0x000000FF;
    }

    private static double[] computeMinMax(double[][] tab){
        double min = tab[0][0],max = tab[0][0]; 
        for(int i = 0; i<tab.length; i++){
            for(int j = 0; j<tab[0].length; j+=2){
                if(tab[i][j] < min ){
                    min = tab[i][j];
                }
                if(tab[i][j] > max ){
                    max = tab[i][j];
                }
            }
        }
        double [] out = {min,max};
        return out;
    }

    private static double[] computeMinMax1D(double[] tab){
        double min = tab[0],max = tab[0]; 
        int current = 0;
        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                current = 2*j+i*size*2;
                if(tab[current] < min ){
                    min = tab[current];
                }
                if(tab[current] > max ){
                    max = tab[current];
                }
            }
        }
        double [] out = {min,max};
        return out;
    }

    @SuppressWarnings("unused")
    private static BufferedImage ArrayToImageWithScale(double[][] array) {
        double [] out = computeMinMax(array);
        double min = out[0];
        double max = out[1];
        double alpha, beta;
        int value[] = new int[3];
        if (min < max) {
            alpha = 255.0/(max - min);
            beta = -alpha*min;
        } else {
            alpha = 0.0;
            beta = 0.0;
        }
        if(imageout == null){
            imageout = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);
        }	
        WritableRaster raster = imageout.getRaster();
        int grey;
        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                grey = greyToColor(array[i][2*j],alpha,beta);
                value[0]=value[1]=value[2]=grey;
                raster.setPixel(i, j, value);
            }
        }
        return imageout;
    }

    @SuppressWarnings("unused")
    private static BufferedImage ArrayToImageWithScale1D(double[] array) {
        double [] out = computeMinMax1D(array);
        double min = out[0];
        double max = out[1];
        double alpha, beta;
        int value[] = new int[3];
        if (min < max) {
            alpha = 255.0/(max - min);
            beta = -alpha*min;
        } else {
            alpha = 0.0;
            beta = 0.0;
        }
        if(imageout == null){
            imageout = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);
        }
        WritableRaster raster = imageout.getRaster();
        int grey;
        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                //*2 here
                grey = greyToColor(array[2*j+i*size*2],alpha,beta);
                value[0]=value[1]=value[2]=grey;
                raster.setPixel(j, i, value);
            }
        }
        return imageout;
    }


    private static void fillTab(){
        Random rand = new Random(System.nanoTime());
        if(tab1D != null){
            for(int i=0;i<size;i++){
                for(int j=0;j<size;j++){
                    double tmp = rand.nextDouble();
                    tab1D[j+i*size] = tmp;
                }
            }
        }
        if(tab2D != null){
            for(int i=0;i<size;i++){
                for(int j=0;j<size;j++){
                    double tmp = rand.nextDouble();
                    tab2D[i][j] = tmp;
                }
            }
        }
    }

    private static void fillMat(double[][] a){
        Random rand = new Random(System.nanoTime());
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                double tmp = rand.nextDouble();
                a[i][j] = tmp;
            }
        }
    }

    private static void fillMat(double[] a){
        Random rand = new Random(System.nanoTime());
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                double tmp = rand.nextDouble();
                a[j+i*size] = tmp;
            }
        }
    }

    /**
     * Will compute the fft2d NB_BENCH time and will print the mean time
     */
    public static void bench_fft2D(){
        tab2D = new double[size][size*2];

        fillTab();

        DoubleFFT_2D fft2d = new DoubleFFT_2D(size, size);
        double moy2 = 0;

        System.out.println("Benching fft 2D");
        for(int i=0;i<NB_BENCH;i++){
            long time1 = System.nanoTime();
            fft2d.complexForward(tab2D);
            long time2 = System.nanoTime();
            fft2d.complexInverse(tab2D,false);
            long time3 = System.nanoTime();

            long time2d = (time2-time1)/1000000;
            long time2_2D = (time3-time2)/1000000;
            if(debug){
                System.out.println("Time2D: "+time2d+" ms, Time2D ifft: "+time2_2D+" ms");
            }
            if(i == 0){ moy = time2d;moy2 = time2_2D;}
            moy = (moy + time2d)/2;
            moy2 = (moy2 + time2_2D)/2;
        }
        System.out.println("Moyenne: "+moy+"ms, ifft: "+moy2+" ms");
    }

    /**
     * Will compute the fft1d NB_BENCH time and will print the mean time
     */
    public static void bench_fft1D(){
        tab1D = new double[size*size*2];

        fillTab();

        DoubleFFT_1D fft1d = new DoubleFFT_1D(size*size);
        double moy2 = 0;
        System.out.println("Benching fft 1D");
        for(int i=0;i<NB_BENCH;i++){
            long time1 = System.nanoTime();
            fft1d.complexForward(tab1D);
            long time2 = System.nanoTime();
            fft1d.complexInverse(tab1D, false);
            long time3 = System.nanoTime();

            long time1D = (time2-time1)/1000000;
            long time2_1D = (time3-time2)/1000000;
            if(debug){
                System.out.println("Time1D: "+time1D+" ms, Time1D ifft: "+time2_1D+" ms");
            }
            if(i == 0){ moy = time1D;moy2 = time2_1D;}
            moy = (moy + time1D)/2;
            moy2 = (moy2 + time2_1D)/2;
        }
        System.out.println("Moyenne sur "+NB_BENCH+" tours: "+moy+"ms, ifft: "+moy2+" ms\n");
    }

    /**
     * Will compute the matrix product NB_BENCH time and will print the mean time
     */
    public static void bench_matrix2D(){
        //2d
        double[][] a = new double[size][size];
        double[][] b = new double[size][size];
        double[][] out = new double[size][size];
        fillMat(a);
        fillMat(b);
        System.out.println("BENCHING Mat 2D");
        for(int i=0;i<NB_BENCH;i++){
            long time1 = System.nanoTime();
            for(int j =0;j<size;j++){
                for(int k=0;k<size;k++){
                    for(int l=0;l<size;l++){
                        out[j][k] = a[j][l]*b[l][k];
                    }
                }
            }
            long time2 = System.nanoTime();

            long time1D = (time2-time1)/1000000;
            if (debug) {
                System.out.println("Time2D: "+time1D+" ms");
            }
            if(i == 0){ moy = time1D;}
            moy = (moy + time1D)/2;
        }
        System.out.println("Moyenne2D: "+moy+"ms\n");
    }

    /**
     * Will compute the matrix product 1d NB_BENCH time and will print the mean time
     */
    public static void bench_matrix1D(){
        //2d
        double[] a = new double[size*size];
        double[] b = new double[size*size];
        double[] out = new double[size*size];
        fillMat(a);
        fillMat(b);

        System.out.println("BENCHING Mat 1D");
        for(int i=0;i<NB_BENCH;i++){
            long time1 = System.nanoTime();
            for(int j=0; j<size; j++){
                for(int k=0; k<size; k++){
                    for(int l=0; l<size; l++){
                        out[k+j*size] = a[l+j*size]*b[k+l*size];
                    }
                }
            }
            long time2 = System.nanoTime();

            long time1D = (time2-time1)/1000000;
            if (debug) {
                System.out.println("Time1D: "+time1D+" ms");
            }

            if(i == 0){ moy = time1D;}
            moy = (moy + time1D)/2;
        }
        System.out.println("Moyenne1D: "+moy+"ms\n");
    }

    public static void benchVectorDouble(){
        Deconvolution deconvVect = new Deconvolution(pathImage, pathPsf,CommonUtils.SCALE,true);
        Deconvolution deconv = new Deconvolution(pathImage, pathPsf,CommonUtils.SCALE,false);

        for(int i=0;i<NB_BENCH;i++){
            System.out.println("Round "+i);
            deconvVect.firstDeconvolutionQuad(0,Deconvolution.PROCESSING_VECTOR, false);
            long begin = System.nanoTime();
            for (int j = 0; j < 100; j++) {
                deconvVect.nextDeconvolutionQuad(j,Deconvolution.PROCESSING_VECTOR);
            }
            long end = System.nanoTime();
            moy = (end-begin + moy)/2;
            computeStat();
            deconv.firstDeconvolutionQuad(0,Deconvolution.PROCESSING_1D, false);
            begin = System.nanoTime();
            for (int j = 0; j < 100; j++) {
                deconv.nextDeconvolutionQuad(j,Deconvolution.PROCESSING_1D);
            }
            end = System.nanoTime();
            moy = (end-begin + moy)/2;
            computeStat();
            moytmp = 0;
        }
    }

    private static void computeStat(){
        if(moytmp == 0){
            moytmp = moy;
        }else{
            double gain = ((moytmp-moy)/moytmp)*100;
            if((int)gain >=0){
                System.out.println("Le bench actuel est "+(int)gain+"% plus rapide\n");
            }else{
                System.out.println("Le bench actuel est "+(int)-gain+"% plus lent\n");
            }
            moytmp = moy;
        }
    }

    private static int computeQuad1D(Deconvolution a){
        long begin,end;
        begin = System.nanoTime();
        a.firstDeconvolutionQuad(1.0,Deconvolution.PROCESSING_1D, false);
        end = System.nanoTime();
        return (int)((end-begin)/1e6);
    }

    private static int computeQuad2D(Deconvolution a){
        long begin,end;
        begin = System.nanoTime();
        a.firstDeconvolutionQuad(1,Deconvolution.PROCESSING_2D, false);
        end = System.nanoTime();
        return (int)((end-begin)/1e6);
    }

    private static int computeQuad1DNext(Deconvolution a){
        long begin,end;
        begin = System.nanoTime();
        a.nextDeconvolutionQuad(1.0,Deconvolution.PROCESSING_1D);
        end = System.nanoTime();
        return (int)((end-begin)/1e6);
    }

    private static int computeQuad2DNext(Deconvolution a){
        long begin,end;
        begin = System.nanoTime();
        a.nextDeconvolutionQuad(1,Deconvolution.PROCESSING_2D);
        end = System.nanoTime();
        return (int)((end-begin)/1e6);
    }

    public static void benchQuad2D1D(){
        Deconvolution a = new Deconvolution(pathImage, pathPsf, CommonUtils.SCALE,false);
        int deuxD = computeQuad2D(a);
        int unD = computeQuad1D(a);
        System.out.println("Beginnig of the bench on "+NB_BENCH+" rounds");
        for (int i = 0; i < NB_BENCH; i++) {
            unD = (unD + computeQuad1D(a))/2;
            deuxD = (deuxD + computeQuad2D(a))/2;
        }
        System.out.println("QUAD: "+deuxD+" ms");
        System.out.println("QUAD1D: "+unD+" ms\n");
    }

    public static void benchQuad2D1DWithOptims(){
        Deconvolution a = new Deconvolution(pathImage, pathPsf, CommonUtils.SCALE,false);
        int deuxD = computeQuad2D(a);
        int unD = computeQuad1D(a);
        System.out.println("Beginnig of the bench on "+NB_BENCH+" rounds");
        for (int i = 0; i < NB_BENCH; i++) {
            unD = (unD + computeQuad1DNext(a))/2;
            deuxD = (deuxD + computeQuad2DNext(a))/2;
        }
        System.out.println("QUAD: "+deuxD+" ms");
        System.out.println("QUAD1D: "+unD+" ms\n");
    }

    public static void benchCG(){
        Deconvolution a = new Deconvolution(pathImage, pathPsf);
        System.out.println("Beginnig of the bench on "+NB_BENCH+" rounds");
        
        double mu = 1000000;
        long begin = System.currentTimeMillis();
        for (int i = 0; i < NB_BENCH; i++) {
            a.firstDeconvolutionCG(mu);
        }
        long end = System.currentTimeMillis();
        long moyFirst = (end-begin)/NB_BENCH;
        
        begin = System.currentTimeMillis();
        for (int i = 0; i < NB_BENCH; i++) {
            a.nextDeconvolutionCG(mu);
        }
        end = System.currentTimeMillis();
        long moyNext = (end-begin)/NB_BENCH;
        
        System.out.println("CG First: "+moyFirst+" ms");
        System.out.println("CG Next : "+moyNext +" ms");
        System.out.println("Speedup by: "+(int)(((double)(moyFirst-moyNext)/moyFirst)*100) +"%\n");
    }
    
    /**
     * Only uncomment what tests you want to see
     * 
     * @param args No args needed
     */
    public static void main(String[] args) {

        /*
         * jtransform->fft->DoubleFFT2D->useThreads True/False
         * */
        //bench_fft1D();
        //computeStat();
        //bench_fft2D();
        //computeStat();

        benchCG();
        
        /*
        bench_matrix2D();
        computeStat();
        bench_matrix1D();
        computeStat();

        benchQuad2D1D();
        benchQuad2D1DWithOptims();

        benchVectorDouble();
         */
        System.exit(0);
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
