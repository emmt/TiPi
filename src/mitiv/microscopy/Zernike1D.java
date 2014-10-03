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
 *  Pas que Noll
 * [1] Robert J. Noll. Zernike polynomials and atmospheric turbulence. Optical
 * Society of America, 1976.
 * @author Beaubras Ludovic
 * @version beta 5.32, février 2013
 */
public class Zernike1D
{
    protected int J;
    protected int n;
    protected int m;
    protected double R_mn[];
    protected double R_mn2[];
    protected double degR_mn[];
    protected double degR_mn2[];
    protected double r[];
    protected double theta[];
    protected double z[];
    double RPowers[][][];
    double Z[];



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
    public Zernike1D(int Width, int Height)
    {
        this.r = MathUtils.fftDist1D(Width, Height);
        this.theta = MathUtils.fftAngle1D(Width, Height);
    }

    public Zernike1D(int J, int Width, int Height, double radius)
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

    public int[] azimutal_number(int n)
    {
        int[] azimut_m = new int[n + 1];
        azimut_m[0] = -n;
        for (int i = 1; i < n + 1; i++)
        {
            azimut_m[i] += azimut_m[i - 1] + 2;
        }
        return azimut_m;
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

    /** Generate Zernike radial polynomials
     *  <p>
     *  q-recursive method
     *  Example :
     *  L = number of element, the distance
     *  n = 5
     *  R[0] to R[L] for m = 0
     *  R[L + 1] to R[2L] for m = 1
     *  R[2L + 1] to R[3L] for m = 2
     *  ...
     *  R[4L + 1] to R[5L] for m = 5
     *  <p>
     *  References:
     *  A comparative analysis of algorithms for fast computation of Zernike moments
     *  Pattern Recognition, Volume 36, Issue 3, March 2003, Pages 731–742
     *  Chee-Way Chonga,, P. Raveendranb, R. Mukundanc [Author Vitae]
     *  @param n 
     *  @param m
     *  @param r
     *  @author Wiren
     */
    public double[] z_radial_polynomials_qRecursive(int n, double[] r)
    {
        int L = r.length;
        double[] R = new double[(n + 1)*L];

        double[] r22 = new double[L];

        if( n == 0)
        {
            for(int ir = 0; ir < L; ir++)
            {
                R[ir] = 1;
            }
        }
        else if( n == 1)
        {
            for(int ir = 0; ir < L; ir++)
            {
                R[L + ir] = r[ir];
            }
        }
        else if( n == 2)
        {
            for(int ir = 0; ir < L; ir++)
            {
                R[ir] = 2*r[ir]*r[ir] - 1;
                R[2*L + ir] = r[ir]*r[ir];
            }
        }
        else if( n == 3)
        {
            for(int ir = 0; ir < L; ir++)
            {
                R[L + ir] = (3*r[ir]*r[ir] - 2)*r[ir];
                R[3*L + ir] = r[ir]*r[ir]*r[ir];
            }
        }
        else
        {
            for(int ir = 0; ir < L; ir++)
            {
                R[n*L + ir] = Math.pow(r[ir], n);
                R[(n - 2)*L + ir] = n*Math.pow(r[ir], n) - (n - 1)*Math.pow(r[ir], n - 2);
                r22[ir] = r[ir]*r[ir];
            }

            for(int m = n - 4; m >= n%1; m = m - 2 )
            {
                int p = n;
                int q = m;

                double H3 =  -( 4.*(q + 2.)*(q + 1.) )/( (p + q + 2.)*(p - q) );
                double H2 =  (H3*(p + q + 4.)*(p - q - 2.))/(4*(q + 3.)) + (q + 2.);
                double H1 =  ((q + 4.)*(q + 3.))/2 - H2*(q + 4.) + (H3*(p + q + 6.)*(p - q - 4.))/8.;
                for(int ir = 0; ir < L; ir++)
                {
                    R[m*L + ir] =   H1*R[(m + 4)*L + ir] +  R[(m + 2)*L + ir]*(H2 + H3/r22[ir]);
                }
            }
        }
        return R;
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
     *  Utilisation du logarithme de la somme cumulé
     *  <p>
     *  @param k mod number of the Zernike polynomials (Noll indexing)
     *  @param n 
     *  @param m
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
     * @param Nzern Number of Zernike mod
     * @param Nx Number of pixels along x axis
     * @param Ny Number of pixels y axis
     * @param radius of the pupil
     * @return Nzern Zernike polynomials over a radius
     */
    public double[] zernikePupilMultipleOpt(int Nzer, int W, int H, double radius, int normalise)
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

        if( normalise == 1)
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
                if( normalise == 1)
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
                    if( normalise == 1)
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
                    if( normalise == 1)
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
     * Précalcule les puissances des distances, ex: r, r^2 = r*r, r^3=r^2*r, ect.
     * Précalcule des coordonnées de la pupil
     * @param Nzern Number of Zernike mod
     * @param Nx Number of pixels along x axis
     * @param Ny Number of pixels y axis
     * @param radius of the pupil
     * @return Nzern Zernike polynomials over a radius
     */
    public double[] zernikePupilMultipleOptTab(int Nzer, int H, int W, double radius, int normalise)
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

    // Avec q-recursive il est facile de faire du parallélisme en calculant chaque ordre séparément..
    /**
     * Create Nzern Zernike polynomials over a radius
     * Use q-recursive algorithm
     * Compute the entire set of Zernike, for exemple
     * if the order is n = 3 it will compute (3,-3), (3,-1), (3,1), (3,3)
     * @param Nzern Number of Zernike mod
     * @param Nx Number of pixels along x axis
     * @param Ny Number of pixels y axis
     * @param radius of the pupil
     * @return Nzern Zernike polynomials over a radius
     */
    public double[] zernike(int Nzer, int W, int H, double radius, int normalise)
    {
        int L = W*H;
        int J = 0;
        double[] R_nm, Z;;
        double N_nm;
        Z = new double[(Nzer + 1)*(Nzer + 2)*L/2];
        double[] r2 = new double[L];
        for (int l = 0; l < L; l++)
        {
            r2[l] = r[l]/radius;
        }

        for (int n = 0; n <= Nzer; n++)
        {
            R_nm = z_radial_polynomials_qRecursive(n, r2);
            int[] m = azimutal_number(n);
            for (int k = 0; k < n + 1; k++)
            {
                if (m[k] < 0)
                {
                    N_nm = Math.sqrt(2*(n + 1));
                    for (int l = 0; l < L; l++)
                    {
                        if(r2[l] < 1)
                        {
                            Z[l + J*L] = N_nm*R_nm[l - m[k]*L]*Math.sin(-m[k]*theta[l]);
                        }
                    }
                }
                else if (m[k] > 0)
                {
                    N_nm = Math.sqrt(2*(n + 1));
                    for (int l = 0; l < L; l++)
                    {
                        if(r2[l] < 1)
                        {
                            Z[l + J*L] = N_nm*R_nm[l + m[k]*L]*Math.cos(m[k]*theta[l]);
                        }
                    }
                }
                else
                {
                    N_nm = Math.sqrt((n + 1));
                    for (int l = 0; l < L; l++)
                    {
                        if(r2[l] < 1)
                        {
                            Z[l + J*L] = N_nm*R_nm[l];
                        }
                    }
                }
                J++;
            }
        }
        return Z;
    }

    /**
     * Create Nzern Zernike polynomials over a radius
     * Use q-recursive algorithm
     * Compute the entire set of Zernike, for exemple
     * if the order is n = 3 it will compute (3,-3), (3,-1), (3,1), (3,3)
     * @param Nzern Number of Zernike mod
     * @param Nx Number of pixels along x axis
     * @param Ny Number of pixels y axis
     * @param radius of the pupil
     * @return Nzern Zernike polynomials over a radius
     */
    public double[] zernike_with_mask(int Nzer, int W, int H, double radius, int normalise)
    {
        int L = W*H;
        int J = 0;
        double[] R_nm, Z;;
        double N_nm;
        Z = new double[(Nzer + 1)*(Nzer + 2)*L/2];
        int[] pupil = new int[H*W];
        int LPupil = 0;
        double[] r_pupil = new double[L];

        for (int i = 0; i < L; i++)
        {   
            if(r[i] < radius)
            {
                r_pupil[i] = r[i]/radius;
                pupil[LPupil] = i; // pupil coordinates
                LPupil = LPupil + 1;
            }
        }

        for (int n = 0; n <= Nzer; n++)
        {
            R_nm = z_radial_polynomials_qRecursive(n, r_pupil);
            int[] m = azimutal_number(n);
            for (int k = 0; k < n + 1; k++)
            {
                if (m[k] < 0)
                {
                    N_nm = Math.sqrt(2*(n + 1));
                    for (int l = 0; l < LPupil; l++)
                    {
                        Z[pupil[l] + J*L] = N_nm*R_nm[pupil[l] - m[k]*L]*Math.sin(-m[k]*theta[pupil[l]]);
                    }
                }
                else if (m[k] > 0)
                {
                    N_nm = Math.sqrt(2*(n + 1));
                    for (int l = 0; l < LPupil; l++)
                    {
                        Z[pupil[l] + J*L] = N_nm*R_nm[pupil[l] + m[k]*L]*Math.cos(m[k]*theta[pupil[l]]);
                    }
                }
                else
                {
                    N_nm = Math.sqrt((n + 1));
                    for (int l = 0; l < LPupil; l++)
                    {
                        Z[pupil[l] + J*L] = N_nm*R_nm[pupil[l]];
                    }
                }
                J++;
            }
        }
        return Z;
    }

    public static void main(String[] args)
    {
        int NZernike = 36;
        int Nx = 6;
        int Ny = 6;
        int radius = 2;
        int NORMALIZED = 0;
        Zernike1D zernike = new Zernike1D(Nx, Ny);

        double[] z = zernike.zernikeNoll(NZernike, Nx, Ny, radius);
        double[] Z1 = zernike.zernikePupilMultipleOpt(NZernike, Nx, Ny, radius, NORMALIZED);
        double[] Z2 = zernike.zernikePupilMultipleOptTab(NZernike, Nx, Ny, radius, NORMALIZED);
        double[] Z3 = zernike.zernike(7, Nx, Ny, radius, NORMALIZED);
        double[] Z4 = zernike.zernike_with_mask(7, Nx, Ny, radius, NORMALIZED);

        MathUtils.printArray(z, Nx, Ny, 1, 0);

        System.out.println("zernikePupilMultipleOpt");
        MathUtils.printArray(Z1, 35*Nx*Ny, 36*Ny*Nx - 1, 0);
        System.out.println();

        System.out.println("zernikePupilMultipleOpt with cosinus sinus separation");
        MathUtils.printArray(Z2, 35*Nx*Ny, 36*Ny*Nx - 1, 0);
        System.out.println();

        System.out.println("zernike with q-recursive");
        MathUtils.printArray(Z3, 35*Nx*Ny, 36*Ny*Nx - 1, 0);
        System.out.println();

        System.out.println("zernike with q-recursive with mask");
        MathUtils.printArray(Z4, 35*Nx*Ny, 36*Ny*Nx - 1, 0);
        System.out.println();

        
        int Nxy = 500;
        double radius2 = 200;
        int n_order = 13;
        int NbZernike = 105;  //use zernumeroNoll
        long begin;
        long end;

        Zernike1D zernike1D = new Zernike1D(Nxy, Nxy);
        System.out.println("Multiple Zernike 1D");
        for (int k = 0; k < 3; k++)
        {
            begin = System.nanoTime();
            zernike1D.zernikePupilMultipleOpt(NbZernike, Nxy, Nxy, radius2, 0);
            end = System.nanoTime();
            System.out.println((end-begin)*1e-9);
        }

        System.out.println("Multiple zernikePupilMultipleOptTab 1D");
        for (int k = 0; k < 3; k++)
        {
            begin = System.nanoTime();
            zernike1D.zernikePupilMultipleOptTab(NbZernike, Nxy, Nxy, radius2, 0);
            end = System.nanoTime();
            System.out.println((end-begin)*1e-9);
        }      

        System.out.println("Multiple zernike with q-recurvive");
        for (int k = 0; k < 3; k++)
        {
            begin = System.nanoTime();
            zernike1D.zernike(n_order, Nxy, Nxy, radius2, NORMALIZED);;
            end = System.nanoTime();
            System.out.println((end-begin)*1e-9);
        }

        System.out.println("Multiple zernike with q-recurvive with mask");
        for (int k = 0; k < 3; k++)
        {
            begin = System.nanoTime();
            zernike1D.zernike_with_mask(n_order, Nxy, Nxy, radius2, NORMALIZED);;
            end = System.nanoTime();
            System.out.println((end-begin)*1e-9);
        }
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