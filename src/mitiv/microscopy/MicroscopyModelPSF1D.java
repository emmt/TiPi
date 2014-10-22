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
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_3D;
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
public class MicroscopyModelPSF1D
{
    protected final static int NORMALIZED = 1;
    protected static final double DEUXPI = 2*Math.PI;

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
    protected double deltaX;
    protected double deltaY;
    protected double defocus_L2;
    protected double depth_dot_defocus;
    protected double sum_depth_over_defocus;
    protected double sum_defocus_over_depth;
    protected int nb_defocus_coefs;
    protected int nb_modulus_coefs;
    protected int nb_phase_coefs;
    protected double[] modulus_coefs;
    protected double[] phase_coefs;

    protected double lambda_ni;
    protected double lambda_ns;
    protected double radius; // radius of the pupil in meter
    protected double NormZ1;
    protected double[] rho; // pupil modulus based on Zernike polynomials
    protected double[] phi; // part of the phase based on Zernike polynomials
    protected double[] psi; // defocus
    protected double[] phasePupil; // phase of the pupil
    protected double[] gamma; // phase of the pupil
    protected double[] a; // fourier transform of the pupil function
    public double[] Z; // Zernike polynomials
    public double PHASE[];
    protected double psf[];
    protected double[] maskPupil; // mask of the pupil
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
    public MicroscopyModelPSF1D()
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
    public MicroscopyModelPSF1D(double NA, double lambda, double ni,double ns,
            double zdepth, double dxy, double dz, int Nx, int Ny, int Nz, int Nzern, int use_depth_scaling)
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
        this.lambda_ni = ni/lambda;
        this.lambda_ns = ns/lambda;
        this.nb_defocus_coefs = 0;
        this.nb_modulus_coefs = 0;
        this.nb_phase_coefs = 0;
        this.phi = new double[Ny*Nx];
        this.psi = new double[Ny*Nx];
        this.phasePupil = new double[Ny*Nx];
        this.psf = new double[Nz*Ny*Nx];
        this.gamma = new double[Ny*Nx];
        this.a = new double[Nz*Ny*2*Nx];
        this.PHASE = new double [Nz*Ny*Nx];
        this.use_depth_scaling = use_depth_scaling;
        this.Z = computeZernike(Nzern, Ny, Nx, radius*dxy*Nx);
        this.maskPupil = computeMaskPupil(Ny, Nx, radius);
        setRho(new double[] {1.});
        setDefocus(new double[] {ni/lambda, 0., 0.});
    }

    /**
     * Computes the mask of the pupil
     * @param Nx
     * @param Ny
     * @param radius
     */
    private double[] computeMaskPupil(int Nx, int Ny, double radius)
    {
        double[] maskPupil = new double[Nx*Ny];
        double r[] = MathUtils.fftDist1D(Nx, Ny);
        int Npix = Nx*Ny;
        for(int in = 0; in < Npix; in++)
        {
            if(r[in] < radius)
            {
                maskPupil[in] = 1;
            }
        }
        return maskPupil;
    }

    private double[] computeZernike(int Nzern, int Nx, int Ny, double radius)
    {
        Zernike1D zernike = new Zernike1D(Nx, Ny);
        Z = zernike.zernikePupilMultipleOpt(Nzern, Nx, Ny, radius, NORMALIZED);
        return Z = MathUtils.gram_schmidt_orthonormalization(Z, Nx, Ny, Nzern);
    }

    /**
     * Compute the modulus ρ on a Zernike polynomial basis
     * <p>
     * The coefficients β are normalized and the modulus is 
     * ρ = Σ_n β_n Z_n
     * @param beta Zernike coefficients
     * @return Nzern Zernike polynomials
     */
    public void setRho(double[] beta)
    {
        nb_modulus_coefs = beta.length;
        modulus_coefs = new double[nb_modulus_coefs];
        for (int i = 0; i < nb_modulus_coefs; i++)
        {
            modulus_coefs[i] = beta[i];
        }

        int Npix = Nx*Ny;
        rho = new double[Npix];
        double betaNorm = 1./(Math.sqrt(MathUtils.innerProd(modulus_coefs, modulus_coefs)));
        for(int in = 0; in < Npix; in++)
        {
            if (maskPupil[in] == 1)
            {
                for (int n = 0; n < nb_modulus_coefs; ++n)
                {
                    rho[in] += Z[in + n*Npix]*modulus_coefs[n]*betaNorm;
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
    public void setPhi(double[] alpha)
    {
        nb_phase_coefs = alpha.length;
        phase_coefs = new double[nb_phase_coefs];
        for (int i = 0; i < nb_phase_coefs; i++)
        {
            phase_coefs[i] = alpha[i];
        }
        int Npix = Nx*Ny;
        phi = new double[Npix];
        for(int in = 0; in < Npix; in++)
        {
            if (maskPupil[in] == 1)
            {
                for (int n = 0; n < nb_phase_coefs; ++n)
                {
                    phi[in] += Z[in + (n + 3)*Npix]*phase_coefs[n];
                }
            }
        }
    }

    /**
     * Compute the defocus aberration ψ and depth aberration
     * γ of the phase pupil
     * <p>
     * @param deltaX
     * @param deltaY
     * @param zdepth
     */
    public void computeDefocus(double deltaX, double deltaY, double zdepth)
    {
        maskPupil = computeMaskPupil(Nx, Ny, radius);  
        double lambda_ns2 = lambda_ns*lambda_ns;
        double lambda_ni2 = lambda_ni*lambda_ni;
        double scale_x = 1/(Nx*dxy);
        double scale_y = 1/(Ny*dxy);
        double q, rx, ry, tmpdepth;
        for (int ny = 0; ny < Ny; ny++)
        {
            if(ny > Ny/2)
            {
                ry = Math.pow(scale_y*(ny - Ny) - deltaY, 2);
            }
            else
            {
                ry = Math.pow(scale_y*ny - deltaY, 2);
            }

            for (int nx = 0; nx < Nx; nx++)
            {
                int nxy = nx + ny*Nx;
                if (maskPupil[nxy] == 1)
                {
                    if(nx > Nx/2)
                    {
                        rx = Math.pow(scale_x*(nx - Nx) - deltaX, 2);
                    }
                    else
                    {
                        rx = Math.pow(scale_x*nx - deltaX, 2);
                    }

                    q = lambda_ni2 - rx - ry;

                    if (q < 0.0)
                    {
                        // Psi = 0 by default
                        psi[nxy] = 0;
                        maskPupil[nxy] = 0;
                    }
                    else
                    {
                        psi[nxy] = Math.sqrt(q);
                        if(zdepth != 0)
                        {
                            tmpdepth = lambda_ns2 - rx - ry;
                            if(tmpdepth<0)
                            {
                                maskPupil[nxy] = 0;
                            }
                            else
                            {
                                gamma[nxy] =  Math.sqrt(tmpdepth);
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
            sum_depth_over_defocus = 0;
            sum_defocus_over_depth = 0;
            for (int Nxy = 0; Nxy < Nx*Ny; Nxy++)
            {
                if(maskPupil[Nxy] == 1)
                {
                    depth_dot_defocus += gamma[Nxy]*psi[Nxy];
                    defocus_L2 += psi[Nxy]*psi[Nxy];
                    sum_depth_over_defocus += gamma[Nxy]/gamma[Nxy];
                    sum_defocus_over_depth += psi[Nxy]/gamma[Nxy];
                }
            }
            eta =(depth_dot_defocus/defocus_L2) - 1;     
        }
        else
        {
            eta = 0;
        }
    }

    /**
     * Update the defocus and the depth functions according the parameters
     * defocus. Depending on the number of elements of defocus:
     * 4 :  defocus = {nu_s, c_x, c_y, \nu_i}
     * 3 :  defocus = {c_x, c_y, \nu_i}
     * 2 :  defocus = {\nu_s, \nu_i}
     * 1 :  defocus = {\nu_i}
     * where
     * \NU_S : wavenumber in specimen medium (n_s/lambda)
     * \NU_I : wavenumber in specimen medium (n_i/lambda)
     * [c_y, c_x] : center of both defocus and depth function
     */
    public void setDefocus(double[] defocus)
    {
        nb_defocus_coefs = defocus.length;
        switch (nb_defocus_coefs)
        {
        case 4:
            lambda_ns = defocus[3];
            if(zdepth==0)
            {
                throw new IllegalArgumentException("zdepth == 0!!!");
            }
        case 3:
            deltaX = defocus[1];
            deltaY = defocus[2];
        case 1:
            lambda_ni = defocus[0];
            break;
        case 2:
            lambda_ni = defocus[0];
            lambda_ns = defocus[1];
            break;
        default:
            throw new IllegalArgumentException("bad defocus / depth parameters");
        }
        computeDefocus(deltaX, deltaY, zdepth);
    }

    /**
     * Compute the point spread function
     * <p>
     * h_k(z) = |a_j(z)|² = |Σ_{j,k}A_k(z)|²
     * @param alpha
     * @param beta
     * @param deltaX
     * @param deltaY
     * @param zdepth
     */
    public void computePSF()
    {
        //computeDefocus(deltaX, deltaY, zdepth);
        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);
        double PSFnorm = 1.0/(Nx*Ny*Nz);
        // double defoc_scale[] = Utils.indgen((-Nz+1)/2, Nz/2);
        double defoc_scale;
        double phasePupil;  
        int Npix = Nx*Ny, Ci;
        double[] A = new double[2*Npix];

        if (zdepth != 0)
        {
            for (int in = 0; in < Npix; in++)
            {
                phi[in] += DEUXPI*ni*zdepth*(gamma[in] - (1 - eta)*psi[in]);
            }
        }

        for (int iz = 0; iz < Nz; iz++)
        {
            if (iz > Nz/2)
            {
                defoc_scale = DEUXPI*(iz - Nz)*dz;
            }
            else
            {
                defoc_scale = DEUXPI*iz*dz;
            }

            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                phasePupil = phi[in] + defoc_scale*psi[in];
                PHASE[Ci] = phasePupil;
                A[2*in] = rho[in]*Math.cos(phasePupil);
                A[2*in + 1] = rho[in]*Math.sin(phasePupil);    
            }
            /* Fourier transform of the pupil function A(z) */
            FFT2D.complexForward(A);

            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                psf[Ci] = (A[2*in]*A[2*in] + A[2*in + 1]*A[2*in + 1])*PSFnorm;
                a[2*Ci] = A[2*in];
                a[2*Ci + 1] = -A[2*in + 1];
            }
        }
    }

    public void computePSF_3Dfft()
    {
        //computeDefocus(deltaX, deltaY, zdepth);
        DoubleFFT_3D FFT3D = new DoubleFFT_3D(Nz, Ny, Nx);
        double PSFnorm = 1.0/(Nx*Ny*Nz);
        // double defoc_scale[] = Utils.indgen((-Nz+1)/2, Nz/2);
        double defoc_scale;
        double phasePupil;  
        int Npix = Nx*Ny, Ci;

        if (zdepth != 0)
        {
            for (int in = 0; in < Npix; in++)
            {
                phi[in] += DEUXPI*ni*zdepth*(gamma[in] - (1 - eta)*psi[in]);
            }
        }

        for (int iz = 0; iz < Nz; iz++)
        {
            if (iz > Nz/2)
            {
                defoc_scale = DEUXPI*(iz - Nz)*dz;
            }
            else
            {
                defoc_scale = DEUXPI*iz*dz;
            }

            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                phasePupil = phi[in] + defoc_scale*psi[in];
                PHASE[Ci] = phasePupil;
                a[2*in] = rho[in]*Math.cos(phasePupil);
                a[2*in + 1] = rho[in]*Math.sin(phasePupil);    
            }
        }
        /* Fourier transform of the pupil function A(z) */
        FFT3D.complexForward(a);

        for (int in = 0; in < Npix*Nz; in++)
        {
            psf[in] = (a[2*in]*a[2*in] + a[2*in + 1]*a[2*in + 1])*PSFnorm;
            a[2*in + 1] = -a[2*in + 1];
        }

    }

    /**
     * Compute the point spread function
     * <p>
     * h_k(z) = |a_j(z)|² = |Σ_{j,k}A_k(z)|²
     * @param alpha
     * @param beta
     * @param deltaX
     * @param deltaY
     * @param zdepth
     */
    public void computePSF(final double[] alpha, final double[] beta, double deltaX, double deltaY, double zdepth)
    {
        if(alpha.length > Nzern || beta.length > Nzern)
        {
            throw new IllegalArgumentException("alpha and coefficients must be lower than number of zernike mod Nzern");
        }
        computeDefocus(deltaX, deltaY, zdepth);
        setPhi(alpha);
        setRho(beta);

        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);

        double a_2[];
        double PSFnorm = 1.0/(Nx*Ny*Nz);
        // double defoc_scale[] = Utils.indgen((-Nz+1)/2, Nz/2);
        double defoc_scale;
        double phasePupil;  
        int Npix = Nx*Ny, Ci;
        double[] A = new double[2*Npix];

        if (zdepth != 0)
        {
            for (int in = 0; in < Npix; in++)
            {
                phi[in] += DEUXPI*ni*zdepth*(gamma[in] - (1 - eta)*psi[in]);
            }
        }

        for (int iz = 0; iz < Nz; iz++)
        {
            if (iz > Nz/2)
            {
                defoc_scale = DEUXPI*(iz - Nz)*dz;
            }
            else
            {
                defoc_scale = DEUXPI*iz*dz;
            }

            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                phasePupil = phi[in] + defoc_scale*psi[in];
                PHASE[Ci] = phasePupil;
                A[2*in] = rho[in]*Math.cos(phasePupil);
                A[2*in + 1] = rho[in]*Math.sin(phasePupil);

            }
            /* Fourier transform of the pupil function A(z) */
            FFT2D.complexForward(A);

            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                a[2*Ci] = A[2*in];
                a[2*Ci + 1] = A[2*in + 1];
            }
        }

        /* Square modulus */
        a_2 = MathUtils.abs2(a, 1);
        for (int in = 0; in < Npix*Nz; in++)
        {
            psf[in] = a_2[in]*PSFnorm;
        }
        /* Conjugate of a */
        MathUtils.conj2(a);
    }

    public double[] apply_J_rho(double[] q)
    {
        int Ci, Npix = Nx*Ny;
        double PSFNorm = 1.0/(Nx*Ny*Nz);
        double Aq[] = new double[2*Npix];
        double J[] = new double[Ny*Nx];
        double[] JRho = new double[nb_modulus_coefs];
        double NBeta = 1/(Math.sqrt(MathUtils.innerProd(modulus_coefs, modulus_coefs)));
        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);

        for (int iz = 0; iz < Nz; iz++)
        {
            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                Aq[2*in] = a[2*Ci]*q[Ci];
                Aq[2*in + 1] = a[2*Ci + 1]*q[Ci];
            }

            FFT2D.complexForward(Aq);

            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                J[in] = J[in] + Aq[2*in]*Math.cos(PHASE[Ci]) - Aq[2*in + 1]*Math.sin(PHASE[Ci]);
            }   

        }

        for (int k = 0; k < nb_modulus_coefs; k++)
        {
            double tmp = 0;
            for (int in = 0; in < Npix; in++)
            {
                Ci = k*Npix + in;
                tmp += J[in]*Z[Ci];
            }
            JRho[k] = 2*PSFNorm*tmp*(1 - modulus_coefs[k]*modulus_coefs[k]*NBeta*NBeta)*NBeta;
        }
        return JRho;
    }

    public double[] apply_J_phi(double[] q)
    {
        int Ci, Npix = Nx*Ny;
        double PSFNorm = 1.0/(Nx*Ny*Nz);
        double JPhi[] = new double[nb_phase_coefs];
        double J[] = new double[Ny*Nx];
        double[] Aq = new double[2*Npix];
        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);

        for (int iz = 0; iz < Nz; iz++)
        {
            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                Aq[2*in] = a[2*Ci]*q[Ci];
                Aq[2*in + 1] = a[2*Ci + 1]*q[Ci];
            }

            FFT2D.complexForward(Aq);

            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                J[in] = J[in] + rho[in]*(Aq[2*in]*Math.sin(PHASE[Ci]) + Aq[2*in + 1]*Math.cos(PHASE[Ci]));
            }
        }

        for (int k = 0; k < nb_phase_coefs; k++)
        {
            double tmp = 0;
            for (int in = 0; in < Npix; in++)
            {
                Ci = k*Npix + in;
                tmp += J[in]*Z[Ci + 3*Npix];
            }
            JPhi[k] = -2*PSFNorm*tmp;
        }
        return JPhi;
    }

    public double[] apply_J_defocus(double[] q)
    {

        double scale_x = 1/(Nx*dxy);
        double scale_y = 1/(Ny*dxy);
        double defoc, tmpvar, idef, idepth, d0 = 0, d1 = 0, d2 = 0, d3 = 0;
        double[] rx = new double[Nx];
        double[] ry = new double[Ny];
        double sum_rx=0, sum_ry=0, etadx=0, etady=0, Npupil=0, etadni=0, etadns=0;
        double ni_depth = lambda_ni*lambda*zdepth;
        int Npix =  Nx*Ny;
        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);
        double Aq[] = new double[2*Npix];
        double PSFNorm = 1.0/(Nx*Ny*Nz);
        int Ci;
        double[] grd = new double[nb_defocus_coefs];

        for(int nx = 0; nx < Nx; nx++)
        {
            if(nx > Nx/2)
            {
                rx[nx] = (nx - Nx)*scale_x - deltaX;
            }
            else
            {
                rx[nx]  = nx*scale_x - deltaX;
            }
        }

        for(int ny = 0; ny < Ny; ny++)
        {
            if(ny > Ny/2)
            {
                ry[ny] = (ny - Ny)*scale_y - deltaY;
            }else
            {
                ry[ny]  = ny*scale_y - deltaY;
            }
        }

        if(use_depth_scaling == 1)
        {
            for (int j = 0; j < Ny; j++)
            {
                for (int i = 0; i < Nx; i++)
                {
                    int in = i + Nx*j;
                    if(maskPupil[in] == 1)
                    {
                        etadx += rx[i]*(gamma[in]/psi[in] + psi[in]/gamma[in]);
                        etady += ry[j]*(gamma[in]/psi[in] + psi[in]/gamma[in]);
                        Npupil += 1;
                        sum_ry +=ry[j];
                        sum_rx +=rx[i];
                    }
                }
            }
            etadx = (etadx * defocus_L2 + 2*depth_dot_defocus * sum_rx)/(defocus_L2*defocus_L2 );
            etady = (etady * defocus_L2 + 2*depth_dot_defocus * sum_ry)/(defocus_L2*defocus_L2 );
            etadni =  lambda_ni * (sum_depth_over_defocus*defocus_L2 - 2* Npupil*depth_dot_defocus) /(defocus_L2 *defocus_L2 );
            etadns =  lambda_ns * sum_defocus_over_depth/(defocus_L2 *defocus_L2 );
        }

        for (int iz = 0; iz < Nz; iz++)
        {
            if (iz > Nz/2)
            {
                defoc = (iz - Nz)*dz;
            }
            else
            {
                defoc = iz*dz;
            }

            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                Aq[2*in] = a[2*Ci]*q[Ci];
                Aq[2*in + 1] = a[2*Ci + 1]*q[Ci];
            }

            FFT2D.complexForward(Aq);

            if(use_depth_scaling == 1)
            {
                for (int j = 0; j < Ny; j++)
                {
                    for (int i = 0; i < Nx; i++)
                    {
                        int in = i + j*Nx;
                        if(maskPupil[in] == 1)
                        {
                            Ci = iz*Npix + in;
                            idef = 1./psi[in];
                            idepth = 0 ;
                            tmpvar = -DEUXPI*rho[in]*( Aq[2*in]*Math.sin(PHASE[Ci]) + Aq[2*in + 1]*Math.cos(PHASE[Ci]) )*PSFNorm;
                            //tmpvar =  -2 * DEUXPI  * cimag(P2P->A[Ci] *P2P->modulus[nxy] * cexp(I*(P2P->phi[nxy] + DEUXPI *defoc * P2P->defocus[nxy])))*(P2P->PSFnorm);
                            switch(nb_defocus_coefs)
                            {
                            case 4:// \nu_s
                                idepth = 1./gamma[in];
                                d3 += tmpvar*ni_depth*(lambda_ns*idepth + gamma[in]*etadns);
                            case 3:// [c_x, c_y]
                                d1 -= tmpvar*( rx[i]*(defoc*idef + ni_depth*(idepth - (1 - eta)*idef)) + ni_depth*psi[in]*etadx );
                                d2 -= tmpvar*( ry[j]*(defoc*idef + ni_depth*(idepth - (1 - eta)*idef)) + ni_depth*psi[in]*etady );

                            case 1:// \nu_i
                                d0 += tmpvar*( idef*lambda_ni*defoc*(1 - ni_depth*(1 - eta)) +
                                        zdepth*(gamma[in] - (1 - eta)*psi[in])/lambda_ni*lambda - ni_depth*etadni*psi[in] );
                                break;
                            case 2://[nu_s, nu_i]
                                idepth = 1./gamma[in];
                                d1 += tmpvar*ni_depth*(lambda_ns*idepth + gamma[in]*etadns);
                                d0 += tmpvar*(idef*lambda_ni *defoc*(1 - ni_depth *(1 - eta)) + zdepth*(gamma[in] -
                                        (1 - eta)*psi[in])/lambda_ni*lambda - ni_depth*etadni*psi[in]);
                                break;
                            }
                        }
                    }
                }
            }
            else
            {
                for (int j = 0; j < Ny; j++)
                {
                    for (int i = 0; i < Nx; i++)
                    {
                        int in = i + j*Nx;
                        if(maskPupil[in] == 1)
                        {
                            Ci = iz*Npix + in;
                            idef= 1./psi[in];
                            idepth = 0 ;
                            tmpvar = -DEUXPI*rho[in]*( Aq[2*in]*Math.sin(PHASE[Ci]) + Aq[2*in + 1]*Math.cos(PHASE[Ci]) )*PSFNorm;
                            //tmpvar =  -1 * DEUXPI  * cimag(P2P->A[Ci] *P2P->modulus[nxy] * cexp(I*(P2P->phi[nxy] + DEUXPI *defoc * P2P->defocus[nxy])))*(P2P->PSFnorm);
                            switch(nb_defocus_coefs)
                            {
                            case 4:
                                idepth= 1./gamma[in];
                                d3 +=  tmpvar*ni_depth*lambda_ns*idepth;
                                d0 += tmpvar*( zdepth* (gamma[in] - psi[in])/lambda_ni*lambda );
                            case 3:
                                d1 -= tmpvar*( rx[i]*(defoc*idef + ni_depth*(idepth - idef)) );
                                d2 -= tmpvar*( ry[j]*(defoc*idef + ni_depth*(idepth - idef)) );
                            case 1:
                                d0 += tmpvar*( idef*lambda_ni*defoc*(1 - ni_depth) );
                                break;
                            case 2:
                                idepth= 1./gamma[in];
                                d0 += tmpvar*(  idef*lambda_ni*defoc*(1 - ni_depth) +
                                        zdepth*(gamma[in] - psi[in])/lambda_ni*lambda );
                                d1 += tmpvar*ni_depth*lambda_ns*idepth;
                                break;
                            }
                        }
                    }
                }
            }
        }

        switch(nb_defocus_coefs)
        {
        case 4:
            grd[3] = d3;
        case 3:
            grd[2] = d2;
        case 2:
            grd[1] = d1;
        case 1:
            grd[0] = d0;
        }
        return grd;
    }

    public void info()
    {
        System.out.println("Number of Zernikes : " + Nzern);
        System.out.println("Wavelength NA: " + lambda);
        System.out.println("Nx: " + Nx);
        System.out.println("dxy: " + dxy);
        System.out.println("Radius : " + NA/lambda*Nx*dxy);
    }

    public double[] getRho() {
        return rho;
    }

    public double[] getPhi() {
        return phi;
    }

    public double[] getPsi() {
        return psi;
    }

    public double[] getGamma() {
        return psi;
    }

    public double[] getMaskPupil() {
        return maskPupil;
    }

    public double[] getPSF() {
        return psf;
    }

    public double[] getPSF_shift_pad(double coefPad) {

        return MathUtils.fftShift3D(CommonUtils.imagePad(MathUtils.fftShift3D(psf, Nx, Ny, Nz), Nx, Ny, Nz, coefPad),
                (int)(Nx*coefPad), (int)(Ny*coefPad), (int)(Nz*coefPad));
    }
    public double[] getPSF(int k) {
        return MathUtils.getArray(getPSF(), Nx, Ny, k);
    }

    public double[] getZernike() {
        return Z;
    }

    public double[] getZernike(int k) {
        return MathUtils.getArray(Z, Nx, Ny, k);
    }

    public double[] get_a() {
        return a;
    }

    public void getInfo()
    {
        System.out.println("----PSF----");
        MathUtils.stat(psf);
        System.out.println();

        System.out.println("----PHI----");
        MathUtils.stat(phi);
        System.out.println();

        System.out.println("----RHO----");
        MathUtils.stat(rho);
        System.out.println();

        System.out.println("----PSI----");
        MathUtils.stat(psi);
        System.out.println();

        System.out.println("----PUPIL----");
        MathUtils.stat(maskPupil);
        System.out.println();

        System.out.println("----a----");
        MathUtils.statC(a);
        System.out.println();

        System.out.println("----ZERNIKES----");
        MathUtils.stat(Z);
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