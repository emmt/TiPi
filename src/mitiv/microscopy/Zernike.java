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
    protected double degR_mn[];
    protected double r[];
    protected double theta[];
    protected double z[];
    double Z[];


    /** Compute multiple Zernike polynomials
     * <p>
     *  Compute a Zernike polynomial of size Height rows and Width columns
     *  of degree J over the radius of the pupil.
     *  <p>
     *  @param Height number of rows
     *  @param Width number of columns
     *  radius the radius of the Zernike polynomial
     *  J mod number of the Zernike polynomials (Noll indexing)
     *
     */
    public Zernike(int Width, int Height)
    {
        this.r = MathUtils.fftDist1D(Width, Height);
        this.theta = MathUtils.fftAngle1D(Width, Height);
    }

    public Zernike(int J, int Width, int Height, double radius)
    {
        this.J = J;
        int nm[] = zernumeroNoll(J);
        this.n = nm[0];
        this.m = nm[1];
        this.R_mn = coeffRadialZCumSumLog(n,m);
        this.degR_mn = radialZDegree(n, m);
        //this.r = Utils.fftDist(Width, Height);
        //this.theta = Utils.fftAngle(Width, Height);
        //this.z = zernikePupil(r, theta, radius);
    }

    public static int[] zernumeroNoll(int J)
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
     *  k = mod number of the Zernike polynomials (Noll indexing)
     *  @param n 
     *  @param m
     * @return 
     */
    public double[] coeffRadialZCumSumLog(int n, int m)
    {
        int p = (n - m)/2;
        int q = (n + m)/2;
        double R_mn[] = new double[p + 1];
        double lfact[] = new double[n + 1];
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

    /** Compute the coefficient of the radial Zernike polynomial
     * <p>
     *  Compute a Zernike polynomial of size Height rows and Width columns
     *  of degree J over the radius of the pupil.
     *                                    (-1)^k (n-k)! r^{n-2*k}
     *   R^m_n(r) = sum_{k=0}^{(n-m)/2} ----------------------------
     *                                  k! ((n+m)/2-k)! ((n-m)/2-k)!
     *  The radial Zernike polynomials are the radial portion of the
     *  Zernike functions, which are an orthogonal basis on the unit
     *  circle.  The series representation of the radial Zernike 
     *  polynomials is
     *
     *          (n-m)/2
     *    m      \       s                                          n-2s
     *   Z(r) =  /__ (-1)  [(n-s)!/(s!((n-m)/2-s)!((n+m)/2-s)!)] * r
     *    n      s=0
     *  <p>
     *  The logarithm of the cumulated sum is used in place of the classic factorial
     *  <p>
     * @param J 
     * @param W 
     * @param H 
     * @param radius 
     * @return 
     */
    //FIXME Qu'elle définition choisir r < radius ou r <= radius
    public double[] zernikeNoll(int J, int W, int H, double radius)
    {
        double[] r = MathUtils.fftDist1D(W, H);
        double[] theta = MathUtils.fftAngle1D(W, H);
        double[] z = new double[H*W];
        int L = W*H;
        // J = 1
        if(J == 1)
        {
            for (int i = 0; i < L; i++)
            {
                if(r[i] < radius)
                {
                    z[i] = 1;
                }
            }
        }
        else
        {
            int nm[] = zernumeroNoll(J);
            this.n = nm[0];
            this.m = nm[1];
            this.R_mn = coeffRadialZCumSumLog(n,m);
            this.degR_mn = radialZDegree(n, m);
            double N, zr, sinCos_m;

            // J != 1 & m = 0
            if(m == 0)
            {
                N = Math.sqrt(n + 1);
                for (int i = 0; i < L; i++)
                {
                    if(r[i] < radius)
                    {
                        zr = 0;
                        r[i]= r[i]/radius;
                        for( int s = (n - m)/2; s >= 0; s-- )
                        {
                            zr = zr +  R_mn[s]*Math.pow(r[i], degR_mn[s]);
                        }
                        z[i] = N*zr;
                    }
                }
            }
            else
            {
                // J > 0 & J even & m != 0 --> azymuthal part is a cosinus
                N = Math.sqrt( 2*(n + 1) );
                if(MathUtils.even(J))
                {
                    for (int i = 0; i < L; i++)
                    {

                        if(r[i] < radius)
                        {
                            r[i] = r[i]/radius;
                            sinCos_m = Math.cos(m*theta[i]);
                            zr = 0;
                            for( int s = (n - m)/2; s >= 0; s-- )
                            {
                                zr = zr +  R_mn[s]*Math.pow(r[i], degR_mn[s]);
                            }
                            z[i] = N*zr*sinCos_m;
                        }

                    }
                }
                else
                {
                    // J > 0 & J odd & m != 0 --> azymuthal part is a sinus
                    for (int i = 0; i < L; i++)
                    {

                        if(r[i] < radius)
                        {
                            r[i]= r[i]/radius;
                            sinCos_m = Math.sin(m*theta[i]);
                            zr = 0;
                            for( int s = (n - m)/2; s >= 0; s-- )
                            {
                                zr = zr + R_mn[s]*Math.pow(r[i], degR_mn[s]);
                            }
                            z[i] = N*zr*sinCos_m;
                        }
                    }
                }
            }
        }
        return z;
    }



    /**
     * Create Nzern Zernike polynomials over a radius
     * Pre-compute the power distance r, r^2 = r*r, r^3=r^2*r, ect.
     * Z_even(r,theta) = R_mn*zr*cos(m*theta)
     * Z_odd(r,theta) = R_mn*zr*sin(m*theta)
     * Zr(r) = 
     * @param Nzer 
     * @param W 
     * @param H 
     * @param radius of the pupil
     * @param normalise 
     * @return Nzern Zernike polynomials over a radius
     */
    public double[] zernikePupilMultipleOpt(int Nzer, int W, int H, double radius, boolean normalise)
    {
        int nm[] = zernumeroNoll(Nzer + 1);
        n = nm[0];
        double rPowers[] = new double[(n + 1)*W*H];
        double Z[] = new double[Nzer*W*H];
        int WH = W*H;

        /* Initialise rPowers */
        for (int l = 0; l < WH; l++)
        {
            if(r[l] < radius)
            {
                rPowers[l] = 1; // rPowers_0 = r^0 = 1
                Z[l] = 1; // the piston : Z_0 = 1;
                rPowers[l + WH] = r[l]/radius; // rPowers_1 = r/radius
            }
        }

        if( normalise)
        {
            double NormZ = 1/Math.sqrt(MathUtils.sum(MathUtils.abs2(Z, 0, W + WH - 1, 0)));
            for (int l = 0; l < WH; l++) // PUPIL
            {
                Z[l] = Z[l]*NormZ;
            }
        }


        for (int k = 2; k < n + 1; k++)
        {
            for (int l = 0; l < WH; l++)
            {
                rPowers[l + k*WH] = rPowers[l + (k - 1)*WH]*rPowers[l + WH]; // rPowers(X) = rPowers(X-1)*rPowers(1)
            }
        }

        for (int nz = 1; nz < Nzer; nz++)
        {
            nm = zernumeroNoll(nz + 1);
            n = nm[0];
            m = nm[1];
            R_mn = coeffRadialZCumSumLog(n, m);
            double N;
            double zr = 0;
            /* J != 1 & m = 0 */
            if(m == 0)
            {
                for (int l = 0; l < WH; l++)
                {
                    zr = 0;
                    N = Math.sqrt(n + 1);
                    for( int s = (n - m)/2; s >= 0; s-- )
                    {
                        zr = zr +  R_mn[s]*rPowers[l + (n - 2*s)*WH];
                    }
                    Z[l + nz*WH] = N*zr;
                }
                if( normalise)
                {
                    double NormZ = 1/Math.sqrt(MathUtils.sum(MathUtils.abs2(Z, nz*WH, nz*WH + WH - 1, 0)));
                    for (int l = 0; l < WH; l++) // PUPIL
                    {
                        Z[l + nz*WH] *= NormZ;
                    }
                }
            }
            else
            {
                /* J > 0 & J even & m != 0 --> azymuthal part is a cosinus */
                if(MathUtils.even(nz + 1))
                {
                    for (int l = 0; l < WH; l++)
                    {
                        N = Math.sqrt( 2*(n + 1) );
                        zr = 0;
                        for( int s = (n - m)/2; s >= 0; s--)
                        {
                            zr = zr +  R_mn[s]*rPowers[l + (n - 2*s)*WH];
                        }
                        Z[l + nz*WH] = N*zr*Math.cos(m*theta[l]);
                    }
                    if( normalise)
                    {
                        double NormZ = 1/Math.sqrt(MathUtils.sum(MathUtils.abs2(Z, nz*WH, nz*WH + WH - 1, 0)));
                        for (int l = 0; l < WH; l++)
                        {  
                            Z[l + nz*WH] = Z[l + nz*WH]*NormZ;
                        }
                    }
                }
                else
                {
                    /* J > 0 & J odd & m != 0 --> azymuthal part is a sinus */
                    for (int l = 0; l < WH; l++)
                    {
                        N = Math.sqrt( 2*(n + 1) );
                        zr = 0;
                        for( int s = (n - m)/2; s >= 0; s-- )
                        {
                            zr = zr + R_mn[s]*rPowers[l + (n - 2*s)*WH];
                        }
                        Z[l + nz*WH] = N*zr*Math.sin(m*theta[l]);
                    }
                    if( normalise)
                    {
                        double NormZ = 1/Math.sqrt(MathUtils.sum(MathUtils.abs2(Z, nz*WH, nz*WH + WH - 1, 0)));
                        for (int l = 0; l < WH; l++)
                        { 
                            Z[l + nz*WH] = Z[l + nz*WH]*NormZ;
                        }
                    }
                }
            }
        }
        return Z;
    }

    /**
     * Create Nzern Zernike polynomials over a radius
     * Pre-compute the power distance r, r^2 = r*r, r^3=r^2*r, ect.
     * Pre-compute the pupil coordinate
     * @param Nzer 
     * @param H 
     * @param W 
     * @param radius of the pupil
     * @param normalise 
     * @return Nzern Zernike polynomials over a radius
     */
    public double[] zernikePupilMultipleOptTab(int Nzer, int H, int W, double radius, boolean normalise)
    {
        int nm[] = zernumeroNoll(Nzer + 1);
        n = nm[0];
        double Zr[] = new double[(n + 1)*W*H];
        double Z[] = new double[Nzer*W*H];
        int[] pupil = new int[H*W];
        int LPupil = 0;
        int L = H*W;
        for (int i = 0; i < L; i++)
        {   
            if(r[i] < radius)
            {
                Zr[i] = 1; // Initialing Zr
                Z[i] = 1; // First Zernike mod
                Zr[L + i] = r[i]/radius;
                pupil[LPupil] = i; // pupil coordinates
                LPupil = LPupil + 1;
            }
        }

        for (int nz = 2; nz < n + 1; nz++)
        {
            for (int k = 0; k < LPupil; k++)
            {
                Zr[pupil[k] + nz*L] = Zr[pupil[k] + (nz - 1)*L]*Zr[pupil[k] +  L];
            }
        }

        for (int nz = 1; nz < Nzer; nz++)
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
                for (int k = 0; k < LPupil; k++)
                {
                    zr = 0;
                    N = Math.sqrt(n + 1);
                    for( int s = (n - m)/2; s >= 0; s-- )
                    {
                        zr = zr +  R_mn[s]*Zr[pupil[k] + (n - 2*s)*W*H];
                    }
                    Z[pupil[k] + nz*W*H ] = N*zr;
                }
            }
            else
            {
                // J > 0 & J even & m != 0 --> azymuthal part is a cosinus
                if(MathUtils.even(nz + 1))
                {
                    for (int k = 0; k < LPupil; k++)
                    {
                        sinCos_m = Math.cos(m*theta[ pupil[k] ]);
                        N = Math.sqrt( 2*(n + 1) );
                        zr = 0;
                        for( int s = (n - m)/2; s >= 0; s--)
                        {
                            zr = zr +  R_mn[s]*Zr[pupil[k] + (n - 2*s)*W*H];
                        }
                        Z[pupil[k] + nz*W*H  ] = N*zr*sinCos_m;
                    }
                }
                else
                {
                    // J > 0 & J odd & m != 0 --> azymuthal part is a sinus
                    for (int k = 0; k < LPupil; k++)
                    {
                        sinCos_m = Math.sin(m*theta[ pupil[k] ]);
                        N = Math.sqrt( 2*(n + 1) );
                        zr = 0;
                        for( int s = (n - m)/2; s >= 0; s-- )
                        {
                            zr = zr + R_mn[s]*Zr[pupil[k] + (n - 2*s)*W*H];
                        }
                        Z[pupil[k] + nz*W*H ] = N*zr*sinCos_m;
                    }
                }
            }
        }
        return Z;
    }
}
