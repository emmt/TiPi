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

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;
import mitiv.utils.*;

/**
 * Compute a 3D point spread function of a wild field fluorescente microscope (WFFM)
 * <p>
 * The 3D PSF is modeled after a parametrized pupil function. It is a monochromatic
 * scalar model that defines the 3D PSF h from pupil function p. This pupil function
 * is the in-focus point source wavefront phase and modulus at the exit pupil of the
 * objective.
 * The support of p(κx , κy ) is a disk thus Zernike polynomials Zn provide a suitable
 * basis to express both modulus ρ(κx , κy ) and phase φ(κx , κy ) of pupil function p
 * <p>
 * <p>
 * This version doesn't use Zernike's mod to compute the pupil's mask (Z0)
 * and defocus with Z1 and Z2.
 * <p>
 * References:
 * [1] Yves Tourneur & Eric Thiébaut, Ferreol Soulez, Loïc Denis.
 * Blind deconvolution of 3d data in wide field fluorescence microscopy.
 * <p>
 * @version
 * @author Boba Fett <boba.fett@bounty-hunter.sw>
 */
public class MicroscopyModelPSF
{
    protected double NA; // the numerical aperture
    protected double lambda; // the emission wavelength in meters
    protected double ni; // the refractive index of the immersion medium
    protected double ns; // the refractive index of the immersion medium
    protected double zdepth;
    protected double dxy; // the lateral pixel size in meter
    protected double dz; // the axial sampling step size in meter
    protected int Nx; // number of samples along lateral X-dimension
    protected int Ny; // number of samples along lateral Y-dimension
    protected int Nz; // number of samples along axial Z-dimension
    protected int Nzern; // number of Zernike modes

    protected double radius; // radius of the pupil in meter
    protected double NormZ1;
    protected double[][] rho; // pupil modulus based on Zernike polynomials
    protected double[][] phi; // part of the phase based on Zernike polynomials
    protected double[][] psi; // defocus
    protected double[][] phasePupil; // phase of the pupil
    protected double[][] depth; // phase of the pupil
    protected double[][][] a; // pupil function
    protected double[][][] Z; // Zernike polynomials
    protected double PHASE[][][];
    protected double A[][][];
    protected double[][] maskPupil; // mask of the pupil

    public MicroscopyModelPSF()
    {
        this(1.4, 542e-9, 1.518, 0, 0, 64.5e-9, 160e-9, 256, 256, 64, 10);
    }

    /** Compute the 3D PSF
     *  @param NA numerical aperture
     *  @param lambda
     *  @param ni
     *  @param ns
     *  @param zdepth
     *  @param dxy
     *  @param dz
     *  @param NX
     *  @param Ny
     *  @param Nz
     *  @param Nzern
     *
     */
    public MicroscopyModelPSF(double NA, double lambda, double ni,double ns, double zdepth, double dxy, double dz, int Nx, int Ny, int Nz, int Nzern)
    {
        this.NA = NA;
        this.lambda = lambda;
        this.ni = ni;
        this.ns = ns;
        this.zdepth = zdepth;
        this.dxy = dxy;
        this.dz = dz;
        this.Nx = Nx;
        this.Ny = Ny;
        this.Nz = Nz;
        this.Nzern = Nzern;
        this.radius = NA/lambda;
        this.rho = new double[Ny][Nx];
        this.phi = new double[Ny][Nx];
        this.psi = new double[Ny][Nx];
        this.phasePupil = new double[Ny][Nx];
        this.depth = new double[Ny][Nx];
        this.a = new double[Nz][Ny][2*Nx];
        this.PHASE = new double [Nz][Ny][Nx];
        this.A = new double[Nz][Ny][2*Nx];
        this.Z = setZernike(Nzern, Ny, Nx, radius*dxy*Nx); // Initialise Zernike mod
        this.maskPupil = computeMaskPupil(Ny, Nx, radius*dxy*Nx);
    }

    /**
     * Computes the mask of the pupil
     */
    //FIX: ne pas utiliser fft_dist, faire plus simplement
    private double[][] computeMaskPupil(int Nx, int Ny, double radius)
    {
        double[][] maskPupil = new double[Nx][Ny];
        double r[][] = Utils.fft_dist(Nx, Ny);
        for (int j = 0; j < Nx; j++)
        {
            for (int i = 0; i < Ny; i++)
            {
                if(r[i][j] < radius)
                    maskPupil[i][j] = 1;
            }
        }
        return maskPupil;
    }

    /**
     * Compute Nzern Zernike polynomials over a radius
     * @param Nzern Number of Zernike mod
     * @param Nx Number of pixels along x axis
     * @param Ny Number of pixels y axis
     * @param radius of the pupil
     * @return Nzern Zernike polynomials over a radius
     */
    /*private double[][][] setZernike1(int Nzern, int Nx, int Ny, double radius)
    {
        //double[][][] Z = new double[Nzern][Nx][Ny];
        Zernike zernike = new Zernike(Ny, Nx);
        double[][][] Z = zernike.zernikePupilMultipleOptTab2(Nzern, Ny, Nx, radius);
        double NormZ;
        for (int n = 0; n < Nzern; n++)
        {
            Zernike zer = new Zernike(n+1, Nx, Ny, radius);
            NormZ = 1/Math.sqrt(Utils.sum(Utils.OuterProd(zer.z, zer.z)));
            if (n == 1) {
                NormZ1 = NormZ;
            }
            for (int j = 0; j < Nx; j++)
            {
                for (int i = 0; i < Ny; i++)
                {
                    Z[n][i][j] = zer.z[i][j]*NormZ;
                }
            }
        }
        return Z;
    }*/
    
    private double[][][] setZernike(int Nzern, int Nx, int Ny, double radius)
    {
        Zernike zernike = new Zernike(Ny, Nx);
        double[][][] Z = zernike.zernikePupilMultipleOptTab(Nzern, Ny, Nx, radius);
        double NormZ;
        for (int n = 0; n < Nzern; n++)
        {
            NormZ = 1/Math.sqrt(Utils.sum(Utils.hadamardProd(Z[n], Z[n])));
            /* Pour le Z1 pour computePSI*/
            if (n == 1)
            {
                NormZ1 = NormZ;
            }
            for (int j = 0; j < Nx; j++)
            {
                for (int i = 0; i < Ny; i++)
                {
                    Z[n][i][j] = Z[n][i][j]*NormZ;
                }
            }
        }
        return Z;
    }

    private void setRho(double[] beta)
    {
        double betaNorm = 1./(Math.sqrt(Utils.sum(Utils.innerProd(beta, beta))));
        for (int j = 0; j < Nx; j++)
        {
            for (int i = 0; i < Ny; i++)
            {
                if (maskPupil[i][j] == 1)
                {
                    for (int n = 0; n < beta.length; ++n)
                    {
                        rho[i][j] += Z[n][i][j]*beta[n]*betaNorm;
                    }
                }
            }
        }
    }

    private void setPhi(double[] alpha)
    {
        for (int j = 0; j < Nx; j++)
        {
            for (int i = 0; i < Ny; i++)
            {
                if (maskPupil[i][j] == 1)
                {
                    for (int n = 0; n < alpha.length; ++n)
                    {
                        phi[i][j] += Z[n+3][i][j]*alpha[n];
                    }
                }
            }
        }
    }

    public void computePsi(double deltaX, double deltaY)
    {
        //ZernikePupil2_1 zernike = new ZernikePupil2_1(1, Nx, Ny, radius*dxy*Nx);
        double psi0 = ni/lambda;
        double q0 = psi0*psi0;
        double ZFactor = radius*Nx*dxy/(2*NormZ1);
        double kappa = 1/(Nx*dxy);
        double q, qx, qy;
        double qq[][] = new double[Ny][Nx];
        //double qq2[][] = new double[Ny][Nx];
        for (int j = 0; j < Nx; j++)
        {
            for (int i = 0; i < Ny; i++)
            {
                if (maskPupil[i][j] == 1)
                {

                    // Without defocus center deltaXY
                    //qx = kappa*zernike.r[i][j];
                    //q = q0 - qx*qx;
                    //qq[i][j] = q;
                    //qq2[i][j] = Math.sqrt(q);
                    //qx = kappa*zernike.Z[1][i][j]/slopeZ12 - deltaX;
                    //qy = kappa*zernike.Z[2][i][j]/slopeZ12 - deltaY;
                    qx = kappa*Z[1][i][j]*ZFactor - deltaX;
                    qy = kappa*Z[2][i][j]*ZFactor - deltaY;
                    q = q0 - (qx*qx) - (qy*qy);
                    qq[i][j] = q;
                    //System.out.println(q);
                    if (q < 0.0)
                    {
                        maskPupil[i][j] = 0;
                    }
                    else
                    {
                        psi[i][j] = Math.sqrt(q);
                    }
                }
            }
        }
        //Utils.pli(qq);
        //Utils.pli(maskPupil);
        //Utils.pli(psi);
    }

    private void computePsi2(double deltaX, double deltaY, double zdepth)
    {
        double[] x = Utils.fft_indgen(Nx);
        double lambda_ni = ni/lambda;
        double lambda_ns = ns/lambda;
        double lambda_ns2 = lambda_ns*lambda_ns;
        double lambda_ni2 = lambda_ni*lambda_ni;
        double kappa = 1/(Nx*dxy);
        double q, rx, ry, tmpdepth;
        for (int j = 0; j < Nx; j++)
        {
            for (int i = 0; i < Ny; i++)
            {
                if (maskPupil[i][j] == 1)
                {
                    rx = (kappa*x[j] - deltaX)*(kappa*x[j] - deltaX);
                    ry = (kappa*x[i] - deltaY)*(kappa*x[i] - deltaY);
                    q = lambda_ni2 - rx - ry;
                    if (q < 0.0)
                    {
                        maskPupil[i][j] = 0;
                    }
                    else
                    {
                        psi[i][j] = Math.sqrt(q);
                        if(zdepth != 0)
                        {
                            tmpdepth = lambda_ns2 - rx - ry;
                            if(tmpdepth<0)
                            {
                                maskPupil[i][j] = 0;
                            }
                            else
                            {
                                depth[i][j] =  Math.sqrt(tmpdepth);
                            }
                        }
                    }
                }
            }
        }
    }

    public void computePSF(double[][][] psf)
    {
        computePSF(psf, new double[]{0}, new double[]{1},0, 0, 0);
    }

    /*FIXME Fereol utilise :
     *       
            if (z > Nz/2) {
                defoc_scale = 2*Math.PI*(z - Nz)*dz;
            }
            else
            {
                defoc_scale = 2*Math.PI*z*dz;
            }
             
     */
    public void computePSF(double[][][] psf, final double[] alpha, final double[] beta, double deltaX, double deltaY, double zdepth)
    {
        computePsi2(deltaX, deltaY, zdepth);
        setPhi(alpha);
        setRho(beta);

        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);

        double A_2[][];
        double PSFnorm = 1.0/(Nx*Ny*Nz);
        double defoc_scale[] = Utils.span((-Nz+1)/2, Nz/2, 1);
        double phasePupil;  
        double real_a;
        double image_a;
        for (int z = 0; z < Nz; z++)
        {
            for (int i = 0; i < Ny; i++)
            {
                for (int j = 0; j < Nx; j++)
                {
                    phasePupil = phi[i][j] + 2*Math.PI*dz*defoc_scale[z]*psi[i][j];
                    PHASE[z][i][j] = phi[i][j] + 2*Math.PI*dz*defoc_scale[z]*psi[i][j];
                    real_a = rho[i][j]*Math.cos(phasePupil);
                    image_a = rho[i][j]*Math.sin(phasePupil);

                    a[z][i][2*j] = real_a;
                    a[z][i][2*j+1] = image_a;
                    A[z][i][2*j] = real_a;
                    A[z][i][2*j+1] = image_a;
                }
            }
            /* Fourier transform of the pupil function a */
            FFT2D.complexForward(A[z]);
            /* Conjugate of A */
            for (int i = 0; i < Ny; i++)
            {
                for (int j = 0; j < Nx; j++)
                {
                    A[z][i][2*j+1] = -A[z][i][2*j+1];
                }
            }
            /* Square modulus */
            A_2 = Utils.abs2(A[z]);
            for (int i = 0; i < Ny; i++)
            {
                for (int j = 0; j < Nx; j++)
                {
                    psf[z][i][j] = A_2[i][j]*PSFnorm;
                }
            }
        }
    }

    public double[] apply_J_rho(double[] beta, double[][][] q)
    {
        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);
        double[] JRho = new double[beta.length];
        double PSFNorm = 1.0/(Nx*Ny*Nz);
        double AJ[][][] = new double[Nz][Ny][2*Nx];
        double J[][] = new double[Ny][Nx];
        double betaNorm = 1/(Math.sqrt(Utils.sum(Utils.innerProd(beta, beta))));
        for (int z = 0; z < Nz; z++)
        {
            for (int j = 0; j < Nx; j++)
            {
                for (int i = 0; i < Ny; i++)
                {
                    AJ[z][i][2*j] = A[z][i][2*j]*q[z][i][j];
                    AJ[z][i][2*j+1] = A[z][i][2*j+1]*q[z][i][j];
                }
            }
            FFT2D.complexForward(AJ[z]);
            for (int j = 0; j < Nx; j++)
            {
                for (int i = 0; i < Ny; i++)
                {
                    J[i][j] = J[i][j] + AJ[z][i][2*j]*Math.cos(PHASE[z][i][j]) - AJ[z][i][2*j+1]*Math.sin(PHASE[z][i][j]);
                }
            }
        }

        for (int k = 0; k < beta.length; k++)
        {
            JRho[k] = 2*PSFNorm*Utils.sum(Utils.hadamardProd(J, Z[k]))*(1-beta[k]*beta[k]*betaNorm*betaNorm)*betaNorm;
        }
        return JRho;
    }

    public double[] apply_J_phi(double[] alpha, double[][][] q)
    {
        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);
        double JPhi[] = new double[alpha.length];
        double J[][] = new double[Ny][Nx];
        double AJ[][][] = new double[Nz][Ny][2*Nx];
        double PSFNorm = 1.0/(Nx*Ny*Nz);
        for (int z = 0; z < Nz; z++)
        {
            for (int j = 0; j < Nx; j++)
            {
                for (int i = 0; i < Ny; i++)
                {
                    AJ[z][i][2*j] = A[z][i][2*j]*q[z][i][j];
                    AJ[z][i][2*j+1] = A[z][i][2*j+1]*q[z][i][j];
                }
            }
            FFT2D.complexForward(AJ[z]);
            for (int i = 0; i < Ny; i++)
            {
                for (int j = 0; j < Nx; j++)
                {
                    J[i][j] = J[i][j] + rho[i][j]*(AJ[z][i][2*j]*Math.sin(PHASE[z][i][j]) + AJ[z][i][2*j+1]*Math.cos(PHASE[z][i][j]));
                }
            }
        }
        for (int k = 0; k < alpha.length; k++)
        {
            JPhi[k] = -2*PSFNorm*Utils.sum(Utils.hadamardProd(J, Z[k+3]));
        }
        return JPhi;
    }

    public void info()
    {
        System.out.println("Number of Zernikes : " + Nzern);
        System.out.println("Wavelength NA: " + lambda);
        System.out.println("Nx: " + Nx);
        System.out.println("dxy: " + dxy);
        System.out.println("Radius : " + NA/lambda*Nx*dxy);
    }

    public double[][] getRho() {
        return rho;
    }
    
    public double[][] getPhi() {
        return phi;
    }

    public double[][] getPsi() {
        return psi;
    }

    public double[][] getMaskPupil() {
        return maskPupil;
    }
    
    public double[][] getA(int z) {
        return A[z];
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