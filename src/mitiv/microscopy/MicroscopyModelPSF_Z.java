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
 * Computes the mask of the pupil
 * This version doesn't use Zernike's mod to compute the pupil's mask (Z0)
 * and to compute the defocus with Z1 and Z2.
 */
public class MicroscopyModelPSF_Z
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

    public MicroscopyModelPSF_Z()
    {
        this(1.4, 542e-9, 1.518, 0, 0, 64.5e-9, 160e-9, 256, 256, 64, 10);
    }

    public MicroscopyModelPSF_Z(double NA, double lambda, double ni,double ns, double zdepth, double dxy, double dz, int Nx, int Ny, int Nz, int Nzern)
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
        this.maskPupil = computeMaskPupil();
    }

    /**
     * Computes the mask of the pupil
     */
    private double[][] computeMaskPupil()
    {
        double[][] maskPupil = Z[0];
        return maskPupil;
    }

    /**
     * Create Nzern Zernike polynomials over a radius
     * @param Nzern Number of Zernike mod
     * @param Nx Number of pixels along x axis
     * @param Ny Number of pixels y axis
     * @param radius of the pupil
     * @return Nzern Zernike polynomials over a radius
     */
    private double[][][] setZernike(int Nzern, int Nx, int Ny, double radius)
    {
        double[][][] Z = new double[Nzern][Nx][Ny];
        double NormZ;
        for (int n = 0; n < Nzern; n++)
        {
            Zernike zer = new Zernike(n+1, Nx, Ny, radius);
            NormZ = 1/Math.sqrt(Utils.sum(Utils.hadamardProd(zer.z, zer.z)));
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
    }

    private void setRho(double[] beta)
    {
        double betaNorm = 1./(Math.sqrt(Utils.sum(Utils.innerProd(beta, beta))));
        for (int j = 0; j < Nx; j++)
        {
            for (int i = 0; i < Ny; i++)
            {
                if (maskPupil[i][j] != 0)
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
                if (maskPupil[i][j] != 0)
                {
                    for (int n = 0; n < alpha.length; ++n)
                    {
                        phi[i][j] += Z[n+3][i][j]*alpha[n];
                    }
                }
            }
        }
    }
/* FIXME Without 
 *  Without defocus center deltaXY (si deltaX, deltaY = 0)
 *  qx = kappa*zernike.r[i][j];
 *  q = q0 - qx*qx;
 *  Ca permet d'éviter les calculs en passant par les zernikes, c'est juste le calul de distances cartésiennes
 */

    public void computePsi(double deltaX, double deltaY, double zdepth)
    {
        double lambda_ni = ni/lambda;
        double lambda_ns = ns/lambda;
        double lambda_ns2 = lambda_ns*lambda_ns;
        double lambda_ni2 = lambda_ni*lambda_ni;
        double ZFactor = radius*Nx*dxy/(2*NormZ1);
        double kappa = 1/(Nx*dxy);
        double q, qx, qy, tmpdepth;
        for (int j = 0; j < Nx; j++)
        {
            for (int i = 0; i < Ny; i++)
            {
                if (maskPupil[i][j] != 0)
                {
                    qx = kappa*Z[1][i][j]*ZFactor - deltaX;
                    qy = kappa*Z[2][i][j]*ZFactor - deltaY;
                    q = lambda_ni2 - (qx*qx) - (qy*qy);
                    if (q < 0.0)
                    {
                        maskPupil[i][j] = 0;
                    }
                    else
                    {
                        psi[i][j] = Math.sqrt(q);
                        if(zdepth != 0)
                        {
                            tmpdepth = lambda_ns2 - qx - qy;
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

    public void computePSF(double[][][] psf, final double[] alpha, final double[] beta, double deltaX, double deltaY, double zdepth)
    {
        computePsi(deltaX, deltaY, zdepth);
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
            FFT2D.complexForward(A[z]);
            for (int i = 0; i < Ny; i++)
            {
                for (int j = 0; j < Nx; j++)
                {
                    A[z][i][2*j+1] = -A[z][i][2*j+1];
                }
            }
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
        //double defoc_scale;
        //double phasePupil_k;
        double betaNorm = 1/(Math.sqrt(Utils.sum(Utils.innerProd(beta, beta))));
        //double defoc_scale[] = Utils.span((-Nz+1)/2, Nz/2, 1);
        for (int z = 0; z < Nz; z++)
        {
            /*
            if (z > Nz/2) {
                defoc_scale = 2*Math.PI*(z - Nz)*dz;
            }
            else
            {
                defoc_scale = 2*Math.PI*z*dz;
            }
             */
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
    /* DOCUMENT P2P_apply_J_Phase(self, q)
    *
    * Apply the Jacobian Matrix to vector Q with respect to the alpha
    * parameters.
    *
    */
    public double[] apply_J_phi(double[] alpha, double[][][] q)
    {
        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);
        double JPhi[] = new double[alpha.length];
        double J[][] = new double[Ny][Nx];
        double AJ[][][] = new double[Nz][Ny][2*Nx];
        //double defoc_scale[] = Utils.span((-Nz+1)/2, Nz/2, 1);
        //double phasePupil_k;
        double PSFNorm = 1.0/(Nx*Ny*Nz);
        for (int z = 0; z < Nz; z++)
        {
            /*
            if (z > Nz/2) {
                defoc_scale = 2*Math.PI*(z - Nz)*dz;
            }
            else
            {
                defoc_scale = 2*Math.PI*z*dz;
            }
             */
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