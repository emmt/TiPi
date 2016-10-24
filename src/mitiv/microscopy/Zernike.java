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

import mitiv.old.MathUtils;
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
    public static   double[] coeffRadialZCumSumLog(int n, int m)
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

    public static  double[] radialZDegree(int n, int m)
    {
        double [] degR_mn = new double[(n - m)/2 + 1];
        for( int s = 0; s <= (n - m)/2; s++)
        {
            degR_mn[s] = n - 2*s;
        }
        return degR_mn;
    }




    /**
     * Create Nzern Zernike polynomials over a radius
     * Pre-compute the power distance r, r^2 = r*r, r^3=r^2*r, ect.
     * Z_even(r,theta) = R_mn*zr*cos(m*theta)
     * Z_odd(r,theta) = R_mn*zr*sin(m*theta)
     * Zr(r) =
     * @param nbZernike - number of zernike modes
     * @param width - width of the array
     * @param height - width of the array
     * @param radius - radius of the pupil
     * @param normalize - when true all polynomial a L2 normalized
     * @param radial  - when true only radial polynomial are considered (m =0)
     * @return array of Zernike polynomials
     */
    public static double[] zernikeArray(int nbZernike, int width, int height, double radius, boolean normalize,boolean radial)
    {

        double Z[] =  new double[nbZernike*width*height];
        int WH = width*height;

        double r[] = MathUtils.fftDist1D(width, height);
        double theta[] = MathUtils.fftAngle1D(width, height);


        int nm[],n,m;
        double rPowers[] ;

        if(radial){
                n = nbZernike + 1;
        }else{
                nm = zernumeroNoll(nbZernike + 1);
                n = nm[0];
            m = nm[1];
        }

        rPowers = new double[(n + 1)*width*height];


        /* Initialize rPowers */
        for (int l = 0; l < WH; l++)
        {
                if(r[l] < radius)
                {
                        rPowers[l] = 1; // rPowers_0 = r^0 = 1
                        Z[l] = 1; // the piston : Z_0 = 1;
                        rPowers[l + WH] = r[l]/radius; // rPowers_1 = r/radius
                }
        }

        if( normalize)
        {
                double NormZ = 1/Math.sqrt(MathUtils.sum(MathUtils.abs2(Z, 0, width + WH - 1, 0)));
                for (int l = 0; l < WH; l++) // PUPIL
                {
                        Z[l] = Z[l]*NormZ;
                }
        }



        if(radial)  { // Compute only radial polynomial (m=0)

                for (int k = 2; k < nbZernike + 1; k++)
                {
                        for (int l = 0; l < WH; l++)
                        {
                                rPowers[l + k*WH] = rPowers[l + (k - 1)*WH]*rPowers[l + WH]; // rPowers(X) = rPowers(X-1)*rPowers(1)
                        }
                }
                for (int nz = 1; nz < nbZernike; nz++)
                {
                        double[] R_mn = coeffRadialZCumSumLog(nz, 0);
                        double N;
                        double zr = 0;
                        /* J != 1 & m = 0 */
                                for (int l = 0; l < WH; l++)
                                {
                                        zr = 0;
                                        N = Math.sqrt(nz + 1);
                                        for( int s = (nz )/2; s >= 0; s-- )
                                        {
                                                zr = zr +  R_mn[s]*rPowers[l + (nz - 2*s)*WH];
                                        }
                                        Z[l + nz*WH] = N*zr;
                                }
                                if( normalize)
                                {
                                        double NormZ = 1/Math.sqrt(MathUtils.sum(MathUtils.abs2(Z, nz*WH, nz*WH + WH - 1, 0)));
                                        for (int l = 0; l < WH; l++) // PUPIL
                                        {
                                                Z[l + nz*WH] *= NormZ;
                                        }
                                }
                        }
        }else{

                for (int k = 2; k < n + 1; k++)
                {
                        for (int l = 0; l < WH; l++)
                        {
                                rPowers[l + k*WH] = rPowers[l + (k - 1)*WH]*rPowers[l + WH]; // rPowers(X) = rPowers(X-1)*rPowers(1)
                        }
                }
                for (int nz = 1; nz < nbZernike; nz++)
                {
                        nm = zernumeroNoll(nz + 1);
                        n = nm[0];
                        m = nm[1];
                        double[] R_mn = coeffRadialZCumSumLog(n, m);
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
                                if( normalize)
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
                                /* J > 0 & J even & m != 0 --> azimuthal part is a cosine */
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
                                        if( normalize)
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
                                        /* J > 0 & J odd & m != 0 --> azimuthal part is a sine */
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
                                        if( normalize)
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
        }
        return Z;
    }


}
