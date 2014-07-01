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

package mitiv.microscopy;

import mitiv.utils.*;
/** Compute Zernike polynomials 
 *
 * [1] Robert J. Noll. Zernike polynomials and atmospheric turbulence. Optical
 * Society of America, 1976.
 * @author Beaubras Ludovic
 * @version beta 5.32, février 2013
 */
public class Zernike
{
    protected int J;
    protected int n;
    protected int m;
    protected double R_mn[];
    protected double R_mn2[];
    protected double degR_mn[];
    protected double degR_mn2[];
    protected double r[][];
    protected double theta[][];
    protected double z[][];
    double RPowers[][][];
    double Z[][][];

    public Zernike()
    {
        this.J = 2;
        int Width = 256;
        int Height = 256;
        int radius = 60;
        int nm[] = zernumeroNoll(J);
        this.n = nm[0];
        this.m = nm[1];
        this.R_mn = coeffRadialZ(n, m);
        this.degR_mn = radialZDegree(n, m);
        this.r = MathUtils.cartesDist2D(Width, Height);
        this.theta = MathUtils.cartesAngle2D(Width, Height);
        //this.z = zernikePupil(J, r, theta, radius);
        this.z = zernikePupil(r, theta, radius);
    }

    /** Compute a Zernike polynomial
     * <p>
     *  Compute a Zernike polynomial of size Height rows and Width columns
     *  of degree J over the radius of the pupil.
     *  <p>
     *  @param J mod number of the Zernike polynomials (Noll indexing)
     *  @param Height number of rows
     *  @param Width number of columns
     *  @param radius the radius of the Zernike polynomial
     *
     */
    public Zernike(int J, int Height, int Width, double radius)
    {
        this.J = J;
        int nm[] = zernumeroNoll(J);
        this.n = nm[0];
        this.m = nm[1];
        this.R_mn = coeffRadialZCumSumLog(n,m);
        this.degR_mn = radialZDegree(n, m);
        this.r = MathUtils.fftDist(Width, Height);
        this.theta = MathUtils.fftAngle(Width, Height);
        this.z = zernikePupil(r, theta, radius);
        //normalizeZ(z);
    }

    /** Compute multiple Zernike polynomials
     * <p>
     *  Compute a Zernike polynomial of size Height rows and Width columns
     *  of degree J over the radius of the pupil.
     *  <p>
     *  @param J mod number of the Zernike polynomials (Noll indexing)
     *  @param Height number of rows
     *  @param Width number of columns
     *  @param radius the radius of the Zernike polynomial
     *
     */
    public Zernike(int Height, int Width)
    {
        this.r = MathUtils.fftDist(Width, Height);
        this.theta = MathUtils.fftAngle(Width, Height);
    }

    public int[] zernumeroNoll(int J)
    {
        int[] nm = new int[2];
        double k = 0;
        int n = 0;
        double n1 = ( Math.sqrt(1 + 8*J) -1 )/2;
        n = (int) Math.floor(n1);
        if (n1 == n)
        {
            n = n - 1;
        }
        k = (n + 1)*(n + 2)/2;
        nm[1] = (int) (n - 2*Math.floor( (k - J)/2 ));
        nm[0] = n;
        return nm;
    }

    public double[][] zernikePupil(double r[][], double theta[][], double radius)
    {
        int H = r.length;
        int W = r[0].length;
        double[][] z = new double[H][W];
        // J = 1
        if(J == 1)
        {
            for (int i = 0; i < H; i++)
            {
                for (int j = 0; j < W; j++)
                {	
                    if(r[i][j] < radius)
                    {
                        z[i][j] = 1;
                    }
                }
            }
        }
        else
        {
            double N;
            double zr;
            double sinCos_m;
            // J != 1 & m = 0
            if(m == 0)
            {
                N = Math.sqrt(n + 1);
                for (int i = 0; i < H; i++)
                {
                    for (int j = 0; j < W; j++)
                    {
                        if(r[i][j] < radius)
                        {
                            zr = 0;
                            r[i][j]= r[i][j]/radius;
                            for( int s = (n - m)/2; s >= 0; s-- )
                            {
                                zr = zr +  R_mn[s]*Math.pow(r[i][j], degR_mn[s]);
                            }
                            z[i][j] = N*zr;
                        }
                    }
                }
            }
            else
            {
                // J > 0 & J even & m != 0 --> azymuthal part is a cosinus
                N = Math.sqrt( 2*(n + 1) );
                if(MathUtils.even(J))
                {
                    for (int i = 0; i < H; i++)
                    {
                        for (int j = 0; j < W; j++)
                        {
                            if(r[i][j] < radius)
                            {
                                r[i][j] = r[i][j]/radius;
                                sinCos_m = Math.cos(m*theta[i][j]);
                                zr = 0;
                                for( int s = (n - m)/2; s >= 0; s-- )
                                {
                                    zr = zr +  R_mn[s]*Math.pow(r[i][j], degR_mn[s]);
                                }
                                z[i][j] = N*zr*sinCos_m;
                            }

                        }
                    }
                }
                else
                {
                    // J > 0 & J odd & m != 0 --> azymuthal part is a sinus
                    for (int i = 0; i < H; i++)
                    {
                        for (int j = 0; j < W; j++)
                        {
                            if(r[i][j] < radius)
                            {
                                r[i][j]= r[i][j]/radius;
                                sinCos_m = Math.sin(m*theta[i][j]);
                                zr = 0;
                                for( int s = (n - m)/2; s >= 0; s-- )
                                {
                                    zr = zr + R_mn[s]*Math.pow(r[i][j], degR_mn[s]);
                                }
                                z[i][j] = N*zr*sinCos_m;
                            }
                        }
                    }
                }
            }
        }
        return z;
    }

    public void normalizeZ(double z[][])
    {
        double NormZ;
        NormZ = 1/Math.sqrt(MathUtils.sum(MathUtils.hadamardProd(z, z, 0)));
        for (int i = 0; i < z.length; i++)
        {
            for (int j = 0; j < z[0].length; j++) {

                z[i][j] = z[i][j]*NormZ;
            }  	
        }
    }

    public double[][] normalizeZ2(double z[][])
    {
        double[][] out = new double[z.length][z[0].length];
        double NormZ;
        NormZ = 1/Math.sqrt(MathUtils.sum(MathUtils.hadamardProd(z, z, 0)));
        for (int i = 0; i < z.length; i++)
        {
            for (int j = 0; j < z[0].length; j++) {

                z[i][j] = z[i][j]*NormZ;
            }   
        }
        return out;
    }



    public double[] coeffRadialZ(int n, int m)
    {
        double R_mn[] = new double[(n - m)/2 + 1];
        for( int s = 0; s <= (n - m)/2; s++ )
        {

            R_mn[s] = Math.pow(-1, s) * MathUtils.factorial(n - s)/
                    ( MathUtils.factorial(s) * MathUtils.factorial((n + m)/2 - s) * MathUtils.factorial((n - m)/2 - s) );
        }
        return R_mn;
    }

    
    /** Compute the coefficient of the radial Zernike polynomial
     * <p>
     *  Compute a Zernike polynomial of size Height rows and Width columns
     *  of degree J over the radius of the pupil.
     *                                    (-1)^k (n-k)! r^{n-2*k}
     *   R^m_n(r) = sum_{k=0}^{(n-m)/2} ----------------------------
     *                                  k! ((n+m)/2-k)! ((n-m)/2-k)!
     *  <p>
     *  Utilisation du logarithme de la somme cumulé
     *  <p>
     *  @param k mod number of the Zernike polynomials (Noll indexing)
     *  @param n 
     *  @param m
     */
    public static double[] coeffRadialZCumSumLog(int n, int m)
    {
        int p = (n - m)/2;
        int q = (n + m)/2;
        double R_mn[] = new double[p + 1];
        double lfact[] = new double[n+1];
        for (int i = 1; i < n + 1; i++)
        {
            lfact[i] = Math.log(i);
        }
        lfact = MathUtils.cumSum(lfact);
        for( int s = 0; s <= (n - m)/2; s++ )
        {
            R_mn[s] = Math.exp(lfact[n-s] - lfact[s] - lfact[p - s] - lfact[q - s]);
            if (!MathUtils.even(s))
            {
                R_mn[s] = -R_mn[s];
            }
        }
        return R_mn;
    }

    /** Compute the coefficient of the radial Zernike polynomial
     * <p>
     *  Compute a Zernike polynomial of size Height rows and Width columns
     *  of degree J over the radius of the pupil.
     *                                    (-1)^k (n-k)! r^{n-2*k}
     *   R^m_n(r) = sum_{k=0}^{(n-m)/2} ----------------------------
     *                                  k! ((n+m)/2-k)! ((n-m)/2-k)!
     *  <p>
     *  Simple somme des logarithmes
     *  <p>
     *  @param k mod number of the Zernike polynomials (Noll indexing)
     *  @param n 
     *  @param m
     */
    public double[] coeffRadialZLog(int n, int m)
    {
        int p = (n - m)/2;
        int q = (n + m)/2;
        double R_mn[] = new double[p + 1];
        double num1, den1, den2, den3;
        double c;
        for( int s = 0; s <= p; s++ )
        {
            num1 = 0;
            den1 = 0;
            den2 = 0;
            den3 = 0;
            for (int x = 1; x <= n - s; x++)
            {
                num1 += Math.log(x);
            }
            for (int x = 1; x <= s; x++)
            {
                den1 += Math.log(x);
            }
            for (int x = 1; x <= q - s; x++)
            {
                den2 += Math.log(x);
            }
            for (int x = 1; x <= p - s; x++)
            {
                den3 += Math.log(x);
            }
            c = Math.exp(num1 - den1 - den2 - den3);
            if (!MathUtils.even(s))
            {
                c = -c;
            }
            R_mn[s] = c;
            /*
			R_mn[s] = Math.pow(-1, s) * Utils.Factorial(n - s)/
					( Utils.Factorial(s) * Utils.Factorial((n + m)/2 - s) * Utils.Factorial((n - m)/2 - s) );
             */
        }
        return R_mn;
    }

    public double[] radialZDegree(int n, int m)
    {
        double [] degR_mn = new double[(n - m)/2 + 1];
        for( int s = 0; s <= (n - m)/2; s++)
        {
            degR_mn[s] = n - 2*s;
        }
        return degR_mn;
    }


    public void info()
    {
        System.out.println("Zernike mode : " + J);
        System.out.println("Radius : " + J);
    }

    public void zernikePupilMultiple(int Nzer, int Height, int Width, double radius)
    {
        // J = 1
        for (int i = 0; i < Height; i++)
        {
            for (int j = 0; j < Width; j++)
            {	
                if(r[i][j] >= radius)
                {
                    Z[0][i][j] = 0;
                }
                else
                {
                    Z[0][i][j] = 1;
                }
            }
        }

        for (int nz = 1; nz < Nzer; nz++)
        {
            System.out.println(nz);
            int nm[] = zernumeroNoll(nz + 1);
            this.n = nm[0];
            this.m = nm[1];
            this.R_mn2 = coeffRadialZ(n, m);
            this.degR_mn = radialZDegree(n, m);
            this.z = zernikePupil(r, theta, radius);
            normalizeZ(z);
            for(int i = 0; i < Height; i++)
            {
                for (int j = 0; j < Width; j++)
                {
                    Z[nz][i][j] = z[i][j];
                }
            }
        }
    }


    /**
     * Create Nzern Zernike polynomials over a radius
     * Précalcule les puissances des distances, ex: r, r^2 = r*r, r^3=r^2*r, ect.
     * @param Nzern Number of Zernike mod
     * @param Nx Number of pixels along x axis
     * @param Ny Number of pixels y axis
     * @param radius of the pupil
     * @return Nzern Zernike polynomials over a radius
     */
    public double[][][] zernikePupilMultipleOpt(int Nzer, int H, int W, double radius)
    {
        int nm[] = zernumeroNoll(Nzer + 1);
        n = nm[0];
        double Zr[][][] = new double[n+1][W][H];
        double Z[][][] = new double[Nzer][W][H];

        // Initialise Zr
        for (int i = 0; i < H; i++)
        {
            for (int j = 0; j < W; j++)
            {
                if(r[i][j] < radius)
                {
                    Zr[0][i][j] = 1;
                    Z[0][i][j] = 1;
                    Zr[1][i][j] = r[i][j]/radius;
                }
            }
        }

        for (int k = 2; k < n+1; k++)
        {
            for (int i = 0; i < H; i++)
            {
                for (int j = 0; j < W; j++)
                {
                    Zr[k][i][j] = Zr[k-1][i][j]*Zr[1][i][j];
                }
            }
        }

        for (int nz = 1; nz < Nzer; nz++) {
            nm = zernumeroNoll(nz + 1);
            n = nm[0];
            m = nm[1];
            R_mn = coeffRadialZCumSumLog(n, m);
            double N;
            double zr = 0;
            double sinCos_m;
            // J != 1 & m = 0
            if(m == 0)
            {
                for (int i = 0; i < H; i++)
                {
                    for (int j = 0; j < W; j++)
                    {

                        zr = 0;
                        N = Math.sqrt(n + 1);
                        for( int s = (n - m)/2; s >= 0; s-- )
                        {
                            zr = zr +  R_mn[s]*Zr[n-2*s][i][j];
                        }
                        Z[nz][i][j] = N*zr;

                    }
                }
            }
            else
            {
                // J > 0 & J even & m != 0 --> azymuthal part is a cosinus
                if(MathUtils.even(nz + 1))
                {
                    for (int i = 0; i < H; i++)
                    {
                        for (int j = 0; j < W; j++)
                        {

                            sinCos_m = Math.cos(m*theta[i][j]);
                            N = Math.sqrt( 2*(n + 1) );
                            zr = 0;
                            for( int s = (n - m)/2; s >= 0; s--)
                            {
                                zr = zr +  R_mn[s]*Zr[n-2*s][i][j];
                            }
                            Z[nz][i][j] = N*zr*sinCos_m;
                        }
                    }
                }
                else
                {
                    // J > 0 & J odd & m != 0 --> azymuthal part is a sinus
                    for (int i = 0; i < H; i++)
                    {
                        for (int j = 0; j < W; j++)
                        {

                            sinCos_m = Math.sin(m*theta[i][j]);
                            N = Math.sqrt( 2*(n + 1) );
                            zr = 0;
                            for( int s = (n - m)/2; s >= 0; s-- )
                            {
                                zr = zr + R_mn[s]*Zr[n-2*s][i][j];
                            }
                            Z[nz][i][j] = N*zr*sinCos_m;

                        }
                    }
                }
            }
        }
        return Z;
    }

    /**
     * Create Nzern Zernike polynomials over a radius
     * Précalcule les puissances des distances, ex: r, r^2 = r*r, r^3=r^2*r, ect.
     * Séparation sinus, cosinus, plus d'utilsation du J pair et impair
     * @param Nzern Number of Zernike mod
     * @param Nx Number of pixels along x axis
     * @param Ny Number of pixels y axis
     * @param radius of the pupil
     * @return Nzern Zernike polynomials over a radius
     */
    public double[][][] zernikePupilMultipleOpt2(int Nzer, int H, int W, double radius)
    {
        int nm[] = zernumeroNoll(Nzer + 1);
        n = nm[0];
        double Zr[][][] = new double[n+1][W][H];
        double Z[][][] = new double[Nzer][W][H];

        // Initialise Zr
        for (int i = 0; i < H; i++)
        {
            for (int j = 0; j < W; j++)
            {
                if(r[i][j] < radius)
                {
                    Zr[0][i][j] = 1;
                    Z[0][i][j] = 1;
                    Zr[1][i][j] = r[i][j]/radius;
                }
            }
        }

        for (int k = 2; k < n+1; k++)
        {
            for (int i = 0; i < H; i++)
            {
                for (int j = 0; j < W; j++)
                {
                    Zr[k][i][j] = Zr[k-1][i][j]*Zr[1][i][j];
                }
            }
        }

        for (int nz = 1; nz < Nzer; nz+=3) // Even
        {
            nm = zernumeroNoll(nz + 1);
            n = nm[0];
            m = nm[1];
            R_mn = coeffRadialZCumSumLog(n, m);
            double N;
            double zr = 0;
            double sinCos_m;
            // J != 1 & m = 0
            if(m == 0)
            {
                for (int i = 0; i < H; i++)
                {
                    for (int j = 0; j < W; j++)
                    {
                        zr = 0;
                        N = Math.sqrt(n + 1);
                        for( int s = (n - m)/2; s >= 0; s-- )
                        {
                            zr = zr +  R_mn[s]*Zr[n-2*s][i][j];
                        }
                        Z[nz][i][j] = N*zr;

                    }
                }
            }
            else
            {
                // J > 0 & J even & m != 0 --> azymuthal part is a cosinus
                for (int i = 0; i < H; i++)
                {
                    for (int j = 0; j < W; j++)
                    {

                        sinCos_m = Math.cos(m*theta[i][j]);
                        N = Math.sqrt( 2*(n + 1) );
                        zr = 0;
                        for( int s = (n - m)/2; s >= 0; s--)
                        {
                            zr = zr +  R_mn[s]*Zr[n-2*s][i][j];
                        }
                        Z[nz][i][j] = N*zr*sinCos_m;
                    }
                }
            }
        }

        for (int nz = 2; nz < Nzer; nz+=2) // Odd
        {
            nm = zernumeroNoll(nz + 1);
            n = nm[0];
            m = nm[1];
            R_mn = coeffRadialZCumSumLog(n, m);
            double N;
            double zr = 0;
            double sinCos_m;
            // J != 1 & m = 0
            if(m == 0)
            {
                for (int i = 0; i < H; i++)
                {
                    for (int j = 0; j < W; j++)
                    {

                        zr = 0;
                        N = Math.sqrt(n + 1);
                        for( int s = (n - m)/2; s >= 0; s-- )
                        {
                            zr = zr +  R_mn[s]*Zr[n-2*s][i][j];
                        }
                        Z[nz][i][j] = N*zr;

                    }
                }
            }
            else
            {
                // J > 0 & J even & m != 0 --> azymuthal part is a cosinus
                for (int i = 0; i < H; i++)
                {
                    for (int j = 0; j < W; j++)
                    {
                        sinCos_m = Math.sin(m*theta[i][j]);
                        N = Math.sqrt( 2*(n + 1) );
                        zr = 0;
                        for( int s = (n - m)/2; s >= 0; s--)
                        {
                            zr = zr +  R_mn[s]*Zr[n-2*s][i][j];
                        }
                        Z[nz][i][j] = N*zr*sinCos_m;
                    }
                }
            }
        }
        return Z;
    }

    /**
     * Create Nzern Zernike polynomials over a radius
     * Précalcule les puissances des distances, ex: r, r^2 = r*r, r^3=r^2*r, ect.
     * Précalcule des coordonnées de la pupil
     * @param Nzern Number of Zernike mod
     * @param Nx Number of pixels along x axis
     * @param Ny Number of pixels y axis
     * @param radius of the pupil
     * @return Nzern Zernike polynomials over a radius
     */
    public double[][][] zernikePupilMultipleOptTab(int Nzer, int H, int W, double radius)
    {
        int nm[] = zernumeroNoll(Nzer + 1);
        n = nm[0];
        double Zr[][][] = new double[n+1][W][H];
        double Z[][][] = new double[Nzer][W][H];
        int[] X = new int[H*W];
        int[] Y = new int[H*W];
        int Lxy = 0;

        for (int i = 0; i < H; i++) // PUPIL
        {
            for (int j = 0; j < W; j++)
            {   
                if(r[i][j] < radius)
                {
                    Zr[0][i][j] = 1; // Initialing Zr
                    Z[0][i][j] = 1; // First Zernike mod
                    Zr[1][i][j] = r[i][j]/radius;
                    X[Lxy] = j; // pupil coordinates
                    Y[Lxy] = i;
                    Lxy = Lxy + 1;
                }
            }
        }

        for (int nz = 2; nz < n+1; nz++)
        {
            for (int k = 0; k < Lxy; k++)
            {
                Zr[nz][ Y[k] ][ X[k] ] = Zr[nz-1][ Y[k] ][ X[k] ]*Zr[1][ Y[k] ][ X[k] ];
            }
        }

        for (int nz = 1; nz < Nzer; nz++)
        {
            nm = zernumeroNoll(nz + 1);
            n = nm[0];
            m = nm[1];
            //R_mn = coeffRadialZ(n, m);
            R_mn = coeffRadialZCumSumLog(n, m);
            double N;
            double zr = 0;
            double sinCos_m;
            // J != 1 & m = 0
            if(m == 0)
            {
                for (int k = 0; k < Lxy; k++)
                {
                    zr = 0;
                    N = Math.sqrt(n + 1);
                    for( int s = (n - m)/2; s >= 0; s-- )
                    {
                        zr = zr +  R_mn[s]*Zr[n-2*s][ Y[k] ][ X[k] ];
                    }
                    Z[nz][ Y[k] ][ X[k] ] = N*zr;
                }
            }
            else
            {
                // J > 0 & J even & m != 0 --> azymuthal part is a cosinus
                if(MathUtils.even(nz + 1))
                {
                    for (int k = 0; k < Lxy; k++)
                    {
                        sinCos_m = Math.cos(m*theta[ Y[k] ][ X[k] ]);
                        N = Math.sqrt( 2*(n + 1) );
                        zr = 0;
                        for( int s = (n - m)/2; s >= 0; s--)
                        {
                            zr = zr +  R_mn[s]*Zr[n-2*s][ Y[k] ][ X[k] ];
                        }
                        Z[nz][ Y[k] ][ X[k] ] = N*zr*sinCos_m;
                    }
                }
                else
                {
                    // J > 0 & J odd & m != 0 --> azymuthal part is a sinus
                    for (int k = 0; k < Lxy; k++)
                    {
                        sinCos_m = Math.sin(m*theta[ Y[k] ][ X[k] ]);
                        N = Math.sqrt( 2*(n + 1) );
                        zr = 0;
                        for( int s = (n - m)/2; s >= 0; s-- )
                        {
                            zr = zr + R_mn[s]*Zr[n-2*s][ Y[k] ][ X[k] ];
                        }
                        Z[nz][ Y[k] ][ X[k] ] = N*zr*sinCos_m;
                    }
                }
            }
        }
        return Z;
    }

    public double[][][] zernikePupilMultipleOptTab2(int Nzer, int H, int W, double radius)
    {
        int nm[] = zernumeroNoll(Nzer + 1);
        n = nm[0];
        double Zr[][][] = new double[n+1][W][H];
        double Z[][][] = new double[Nzer][W][H];
        int[] X = new int[H*W];
        int[] Y = new int[H*W];
        int Lxy = 0;

        for (int i = 0; i < H; i++) // PUPIL
        {
            for (int j = 0; j < W; j++)
            {   
                if(r[i][j] < radius)
                {
                    Zr[0][i][j] = 1; // Initialing Zr
                    Z[0][i][j] = 1; // First Zernike mod
                    Zr[1][i][j] = r[i][j]/radius;
                    X[Lxy] = j; // pupil coordinates
                    Y[Lxy] = i;
                    Lxy = Lxy + 1;
                }
            }
        }

        for (int nz = 2; nz < n+1; nz++)
        {
            for (int k = 0; k < Lxy; k++)
            {
                Zr[nz][ Y[k] ][ X[k] ] = Zr[nz-1][ Y[k] ][ X[k] ]*Zr[1][ Y[k] ][ X[k] ];
            }
        }

        for (int nz = 1; nz < Nzer; nz+=2)
        {
            nm = zernumeroNoll(nz + 1);
            n = nm[0];
            m = nm[1];
            //R_mn = coeffRadialZ(n, m);
            R_mn = coeffRadialZCumSumLog(n, m);
            double N;
            double zr = 0;
            double sinCos_m;
            // J != 1 & m = 0
            if(m == 0)
            {
                for (int k = 0; k < Lxy; k++)
                {
                    zr = 0;
                    N = Math.sqrt(n + 1);
                    for( int s = (n - m)/2; s >= 0; s-- )
                    {
                        zr = zr +  R_mn[s]*Zr[n-2*s][ Y[k] ][ X[k] ];
                    }
                    Z[nz][ Y[k] ][ X[k] ] = N*zr;
                }
            }
            else
            {
                for (int k = 0; k < Lxy; k++)
                {
                    sinCos_m = Math.cos(m*theta[ Y[k] ][ X[k] ]);
                    N = Math.sqrt( 2*(n + 1) );
                    zr = 0;
                    for( int s = (n - m)/2; s >= 0; s--)
                    {
                        zr = zr +  R_mn[s]*Zr[n-2*s][ Y[k] ][ X[k] ];
                    }
                    Z[nz][ Y[k] ][ X[k] ] = N*zr*sinCos_m;
                }
            }
        }

        for (int nz = 2; nz < Nzer; nz+=3)
        {
            nm = zernumeroNoll(nz + 1);
            n = nm[0];
            m = nm[1];
            //R_mn = coeffRadialZ(n, m);
            R_mn = coeffRadialZCumSumLog(n, m);
            double N;
            double zr = 0;
            double sinCos_m;
            // J != 1 & m = 0
            if(m == 0)
            {
                for (int k = 0; k < Lxy; k++)
                {
                    zr = 0;
                    N = Math.sqrt(n + 1);
                    for( int s = (n - m)/2; s >= 0; s-- )
                    {
                        zr = zr +  R_mn[s]*Zr[n-2*s][ Y[k] ][ X[k] ];
                    }
                    Z[nz][ Y[k] ][ X[k] ] = N*zr;
                }
            }
            else
            {
                // J > 0 & J odd & m != 0 --> azymuthal part is a sinus
                for (int k = 0; k < Lxy; k++)
                {
                    sinCos_m = Math.sin(m*theta[ Y[k] ][ X[k] ]);
                    N = Math.sqrt( 2*(n + 1) );
                    zr = 0;
                    for( int s = (n - m)/2; s >= 0; s-- )
                    {
                        zr = zr + R_mn[s]*Zr[n-2*s][ Y[k] ][ X[k] ];
                    }
                    Z[nz][ Y[k] ][ X[k] ] = N*zr*sinCos_m;
                }
            }
        }
        return Z;
    }

    public double[][][] compute_RPowers(int deg, double[][] r)
    {
        for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < r.length; j++) {
                RPowers[deg][i][j] = RPowers[deg-1][i][j]*r[i][i];
            }
        }
        return RPowers;
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