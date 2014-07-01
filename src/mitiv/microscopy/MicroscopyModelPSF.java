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
 * Both modulus ρ(i,j) and phase φ(i,j) of pupil function p are expressed on a basis
 * of Zernike polynomials Zn.
 * <p>
 * A(z) = ρ.exp(iΦ(z)) with Φ(z) = φ + 2π(d.ω + z.ψ)
 * φ 
 * d depth within the system
 * ω
 * ψ the defocus aberration
 * <p>
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
    protected double ns; // the refractive index of the specimen
    protected double zdepth;
    protected double dxy; // the lateral pixel size in meter
    protected double dz; // the axial sampling step size in meter
    protected int Nx; // number of samples along lateral X-dimension
    protected int Ny; // number of samples along lateral Y-dimension
    protected int Nz; // number of samples along axial Z-dimension
    protected int Nzern; // number of Zernike modes
    protected double use_depth_scaling; //use_depth_scaling = 1, PSF are centered on the plan with maximum strehl
    
    protected double radius; // radius of the pupil in meter
    protected double NormZ1;
    protected double[][] rho; // pupil modulus based on Zernike polynomials
    protected double[][] phi; // part of the phase based on Zernike polynomials
    protected double[][] psi; // defocus
    protected double[][] phasePupil; // phase of the pupil
    protected double[][] gamma; // phase of the pupil
    protected double[][][] a; // fourier transform of the pupil function
    protected double[][][] Z; // Zernike polynomials
    protected double PHASE[][][];
    protected double PSF[][][];
    protected double A[][][]; // pupil function
    protected double[][] maskPupil; // mask of the pupil
    protected double eta;


    /** Initialize the WFFM PSF model containing parameters
     *  @param NA 1.4, numerical aperture
     *  @param lambda 542e-9,  emission wavelength
     *  @param ni 1.518, ni refractive index of the immersion medium
     *  @param ns 0, refractive index of the specimen
     *  @param zdepth 0, zdepth
     *  @param dxy 64.5e-9, lateral pixel size
     *  @param dz 160e-9 axial sampling step size
     *  @param Nx 256, number of samples along lateral X-dimension
     *  @param Ny 256, number of samples along lateral Y-dimension
     *  @param Nz 64, number of samples along axial Z-dimension
     *  @param use_depth_scaling 0
     */
    public MicroscopyModelPSF()
    {
        this(1.4, 542e-9, 1.518, 0, 0, 64.5e-9, 160e-9, 256, 256, 64, 10, 0);
    }

    /** Initialize the WFFM PSF model containing parameters
     *  @param NA numerical aperture
     *  @param lambda emission wavelength
     *  @param ni refractive index of the immersion medium
     *  @param ns refractive index of the specimen
     *  @param zdepth
     *  @param dxy lateral pixel size
     *  @param dz axial sampling step size
     *  @param Nx number of samples along lateral X-dimension
     *  @param Ny number of samples along lateral Y-dimension
     *  @param Nz number of samples along axial Z-dimension
     *  @param use_depth_scaling use_depth_scaling = 1, PSF are centered on the plan with maximum strehl
     */
    public MicroscopyModelPSF(double NA, double lambda, double ni,double ns, double zdepth, double dxy, double dz, int Nx, int Ny, int Nz, int Nzern, int use_depth_scaling)
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
        this.PSF = new double[Ny][Nx][Nz];
        this.gamma = new double[Ny][Nx];
        this.a = new double[Nz][Ny][2*Nx];
        this.PHASE = new double [Nz][Ny][Nx];
        this.A = new double[Nz][Ny][2*Nx];
        this.use_depth_scaling = use_depth_scaling;
        this.Z = setZernike(Nzern, Ny, Nx, radius*dxy*Nx); // Initialise Zernike mod
        this.maskPupil = computeMaskPupil(Ny, Nx, radius*dxy*Nx);
    }

    /**
     * Computes the mask of the pupil
     * @param Nx
     * @param Ny
     * @param radius
     */
    //FIX: ne pas utiliser fft_dist, faire plus simplement
    private double[][] computeMaskPupil(int Nx, int Ny, double radius)
    {
        double[][] maskPupil = new double[Nx][Ny];
        double r[][] = MathUtils.fftDist(Nx, Ny);
        for (int j = 0; j < Nx; j++)
        {
            for (int i = 0; i < Ny; i++)
            {
                if(r[i][j] < radius)
                {
                    maskPupil[i][j] = 1;
                }
            }
        }
        return maskPupil;
    }
    
    /**
     * Compute Nzern Zernike polynomials over the radius of the pupil
     * @param Nzern Number of Zernike mod
     * @param Nx Number of pixels along x axis
     * @param Ny Number of pixels y axis
     * @param radius of the pupil
     * @return Nzern Zernike polynomials
     */
    private double[][][] setZernike(int Nzern, int Nx, int Ny, double radius)
    {
        Zernike zernike = new Zernike(Ny, Nx);
        double[][][] Z = zernike.zernikePupilMultipleOptTab(Nzern, Ny, Nx, radius);
        double NormZ;
        for (int n = 0; n < Nzern; n++)
        {
            NormZ = 1/Math.sqrt(MathUtils.sum(MathUtils.hadamardProd(Z[n], Z[n], 0)));
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

    /**
     * Compute the modulus ρ on a Zernike polynomial basis
     * <p>
     * The coefficients β are normalized and the modulus is 
     * ρ = Σ_n β_n Z_n
     * @param beta Zernike coefficients
     * @return Nzern Zernike polynomials
     */
    private void setRho(double[] beta)
    {
        double betaNorm = 1./(Math.sqrt(MathUtils.sum(MathUtils.innerProd(beta, beta))));
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

    /**
     * Compute φ the part of the phase of the pupil function
     * on a Zernike polynomial basis
     * <p>
     * φ = Σ_n α_n Z_{n+3}
     * @param alpha Zernike coefficients
     * @return phi Zernike polynomials 
     */
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

    /* DOCUMENT P2P_set_defocus(self, def)
    *
    * Update the defocus and the depth functions according the parameters
    * DEF. Depending on the number of elements of DEF:
    * NUMBEROF(DEF)=
    * 4 :  DEF=[nu_s, c_y, c_x, \nu_i]
    * 3 :  DEF=[c_y, c_x, \nu_i]
    * 2 :  DEF=[\nu_s, \nu_i]
    * 1 :  DEF=[\nu_i]
    * where
    * \NU_S : wavenumber in specimen medium (n_s/lambda)
    * \NU_I : wavenumber in specimen medium (n_i/lambda)
    * [c_y, c_x] : center of both defocus and depth function
    * 
    */
    /**
     * Compute the defocus aberration ψ and depth aberration
     * γ of the phase pupil
     * <p>
     * @param alpha Zernike coefficients
     * @return Nzern Zernike polynomials 
     */
    private void computeDefocus(double deltaX, double deltaY, double zdepth)
    {
        double[] x = MathUtils.fftIndgen(Nx);
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
                                gamma[i][j] =  Math.sqrt(tmpdepth);
                            }
                        }
                    }
                }
            }
        }
        
        if(zdepth != 0 && use_depth_scaling == 1)
        {
            double depth_dot_defocus = 0;
            double defocus_L2 = 0;
            double sum_depth_over_defocus = 0;
            double sum_defocus_over_depth = 0;
            for (int j = 0; j < Nx; j++)
            {
                for (int i = 0; i < Ny; i++)
                {
                    if(maskPupil[i][j] == 1)
                    {
                        depth_dot_defocus += gamma[i][j]*psi[i][j];
                        defocus_L2 += psi[i][j]*psi[i][j];
                        sum_depth_over_defocus += gamma[i][j]/gamma[i][j];
                        sum_defocus_over_depth += psi[i][j]/gamma[i][j];
                    }
                }
                eta =(depth_dot_defocus/defocus_L2) - 1;
            }
        }
        else
            eta = 0;
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
    /**
     * Compute the point spread function
     * <p>
     */
    public void computePSF(double[][][] psf, final double[] alpha, final double[] beta, double deltaX, double deltaY, double zdepth)
    {
        computeDefocus(deltaX, deltaY, zdepth);
        setPhi(alpha);
        setRho(beta);

        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);

        double a_2[][];
        double PSFnorm = 1.0/(Nx*Ny*Nz);
        double defoc_scale[] = MathUtils.indgen((-Nz+1)/2, Nz/2);
        double phasePupil;  
        double real_a;
        double image_a;
        
        if (zdepth != 0)
        {
            for (int i = 0; i < Ny; i++)
            {
                for (int j = 0; j < Nx; j++)
                {
                    phi[i][j] += 2*Math.PI*zdepth*(gamma[i][j] - (1 - eta)*psi[i][j]);
                }
            }
        }

        for (int z = 0; z < Nz; z++)
        {
            for (int i = 0; i < Ny; i++)
            {
                for (int j = 0; j < Nx; j++)
                {
                    phasePupil = phi[i][j] + 2*Math.PI*dz*defoc_scale[z]*psi[i][j];
                    PHASE[z][i][j] = phasePupil;
                    real_a = rho[i][j]*Math.cos(phasePupil);
                    image_a = rho[i][j]*Math.sin(phasePupil);

                    a[z][i][2*j] = real_a;
                    a[z][i][2*j+1] = image_a;
                }
            }
            /* Fourier transform of the pupil function A(z) */
            FFT2D.complexForward(a[z]);
            /* Square modulus */
            a_2 = MathUtils.abs2(a[z]);
            for (int i = 0; i < Ny; i++)
            {
                for (int j = 0; j < Nx; j++)
                {
                    psf[z][i][j] = a_2[i][j]*PSFnorm;

                }
            }
            /* Conjugate of A */
            MathUtils.conj2(a[z]);
        }
    }

    public double[] apply_J_rho(double[] beta, double[][][] q)
    {
        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);
        double[] JRho = new double[beta.length];
        double PSFNorm = 1.0/(Nx*Ny*Nz);
        double AJ[][][] = new double[Nz][Ny][2*Nx];
        double J[][] = new double[Ny][Nx];
        double betaNorm = 1/(Math.sqrt(MathUtils.sum(MathUtils.innerProd(beta, beta))));
        for (int z = 0; z < Nz; z++)
        {
            for (int j = 0; j < Nx; j++)
            {
                for (int i = 0; i < Ny; i++)
                {
                    AJ[z][i][2*j] = a[z][i][2*j]*q[z][i][j];
                    AJ[z][i][2*j+1] = a[z][i][2*j+1]*q[z][i][j];
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
            JRho[k] = 2*PSFNorm*MathUtils.sum(MathUtils.hadamardProd(J, Z[k], 0))*(1-beta[k]*beta[k]*betaNorm*betaNorm)*betaNorm;
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
                    AJ[z][i][2*j] = a[z][i][2*j]*q[z][i][j];
                    AJ[z][i][2*j+1] = a[z][i][2*j+1]*q[z][i][j];
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
            JPhi[k] = -2*PSFNorm*MathUtils.sum(MathUtils.hadamardProd(J, Z[k+3], 0));
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
    
    public double[][] get_a(int z) {
        return a[z];
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
