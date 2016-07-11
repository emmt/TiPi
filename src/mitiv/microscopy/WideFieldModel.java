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


import mitiv.utils.MathUtils;
import org.jtransforms.fft.DoubleFFT_2D;

/**
 * Compute a 3D point spread function of a wild field fluorescence microscope (WFFM)
 * <p>
 * The 3D PSF is modeled after a parameterized pupil function. It is a monochromatic
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
 * @author Ferréol Soulez	 <ferreol.soulez@epfl.ch>
 */
public class WideFieldModel
{
    protected final static boolean NORMALIZED = true;
    protected static final double DEUXPI = 2*Math.PI;

    protected double NA; // the numerical aperture
    protected double lambda; // the emission wavelength in meters
    protected double ni; // the refractive index of the immersion medium
    protected double zdepth=0; // to be used in future depth varying framework 
    protected double dxy; // the lateral pixel size in meter
    protected double dz; // the axial sampling step size in meter
    protected int Nx; // number of samples along lateral X-dimension
    protected int Ny; // number of samples along lateral Y-dimension
    protected int Nz; // number of samples along axial Z-dimension
    protected int Nzern; // number of Zernike modes
    protected int PState=0;
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
    protected double radius; // radius of the pupil in meter
    protected double pupil_area;
    protected double NormZ1;
    protected double[] rho; // pupil modulus based on Zernike polynomials
    protected double[] phi; // part of the phase based on Zernike polynomials
    protected double[] psi; // defocus
    protected double[] phasePupil; // phase of the pupil
    protected double[] gamma; // phase of the pupil
    protected double[] a; // fourier transform of the pupil function
    public double[] Z; // Zernike polynomials
    protected double psf[];
    protected double[] maskPupil; // mask of the pupil



    /** Initialize the WFFM PSF model containing parameters
     *  @param NA numerical aperture
     *  @param lambda emission wavelength
     *  @param ni refractive index of the immersion medium
     *  @param dxy lateral pixel size
     *  @param dz axial sampling step size
     *  @param Nx number of samples along lateral X-dimension
     *  @param Ny number of samples along lateral Y-dimension
     *  @param Nz number of samples along axial Z-dimension
     */
    public WideFieldModel(double NA, double lambda, double ni, double dxy, double dz, int Nx, int Ny, int Nz)
    {
        this.NA = NA;
        this.lambda = lambda;
        this.ni = ni;
        this.dxy = dxy;
        this.dz = dz;
        this.Nx = Nx;
        this.Ny = Ny;
        this.Nz = Nz;
        this.Nzern = 4;
        this.radius = NA/lambda;
        this.lambda_ni = ni/lambda;
        this.nb_defocus_coefs = 0;
        this.nb_modulus_coefs = 0;
        this.nb_phase_coefs = 0;
        this.phi = new double[Ny*Nx];
        this.psi = new double[Ny*Nx];
        this.phasePupil = new double[Ny*Nx];
        this.psf = new double[Nz*Ny*Nx];
        this.gamma = new double[Ny*Nx];
        this.a = new double[Nz*Ny*2*Nx];
        this.Z = computeZernike();
        this.maskPupil = computeMaskPupil();
        this.PState = 0;
        setRho(new double[] {1.});
        setDefocus(new double[] {ni/lambda, 0., 0.});
    }

    private double[] computeMaskPupil()
    {
        double[] maskPupil = new double[Nx*Ny];
        double scale_y = Math.pow(1/dxy/Ny, 2);
        double scale_x = Math.pow(1/dxy/Nx, 2);
        double rx, ry, ix, iy;
        double radius2 = radius*radius;
        pupil_area =0.;
        for(int ny = 0; ny < Ny; ny++)
        {
            iy = Math.min(ny, Ny - ny);
            ry = iy*iy*scale_y;
            for(int nx = 0; nx < Nx; nx++)
            {
                ix = Math.min(nx, Nx - nx);
                rx = ix*ix*scale_x;
                if( (rx + ry) < radius2 )
                {
                    maskPupil[nx + ny*Nx] = 1;
                    pupil_area += 1;
                    
                }
            }
        }
        pupil_area = Math.sqrt(pupil_area);
        PState = 0;
        return maskPupil;
    }

    private double[] computeZernike(){
        Zernike zernike = new Zernike(Nx, Ny);
        Z = zernike.zernikePupilMultipleOpt(Nzern, Nx, Ny, radius*dxy*Nx, NORMALIZED);
        Z= MathUtils.gram_schmidt_orthonormalization(Z, Nx, Ny, Nzern);
        return Z ;
    }

    /**
     * Compute the modulus ρ on a Zernike polynomial basis
     * <p>
     * The coefficients β are normalized and the modulus is
     * ρ = Σ_n β_n Z_n
     * @param beta Zernike coefficients
     */
    public void setRho(double[] beta)
    {

        if( beta.length > Nzern)
        {
        	Nzern = beta.length;
        	Z = computeZernike();
        }
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

        PState = 0;
    }


    /**
     * Compute φ the part of the phase of the pupil function
     * on a Zernike polynomial basis
     * <p>
     * φ = Σ_n α_n Z_{n+3}
     * @param alpha Zernike coefficients
     */
    public void setPhi(double[] alpha)
    {

        if( alpha.length+3 > Nzern)
        {
        	Nzern = alpha.length+3 ;
        	Z = computeZernike();
        }
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
        PState = 0;
    }

    /**
     * Compute the defocus aberration ψ and depth aberration
     * γ of the phase pupil
     * <p>
     * @param deltaX
     * @param deltaY
     * @param zdepth
     */
    public void computeDefocus()
    {	
        double lambda_ni2 = lambda_ni*lambda_ni;
        double scale_x = 1/(Nx*dxy);
        double scale_y = 1/(Ny*dxy);
        double q, rx, ry;
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
                        psi[nxy] = 0;
                        maskPupil[nxy] = 0;
                    }
                    else
                    {
                        psi[nxy] = Math.sqrt(q);  
                        }
                }
            }
        }
        PState = 0;
    }

    /**
     * @param defocus Update the defocus and the depth functions according the parameters
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
        case 3:
            deltaX = defocus[1];
            deltaY = defocus[2];
        case 1:
            lambda_ni = defocus[0];
            break;
        case 2:
            deltaX = defocus[1];
            deltaY = defocus[2];
            break;
        default:
            throw new IllegalArgumentException("bad defocus / depth parameters");
        }
        computeDefocus();
        PState = 0;
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
    public void computePSF(final double[] alpha, final double[] beta, double deltaX, double deltaY)
    {
        computeDefocus();
        setPhi(alpha);
        setRho(beta);
        computePSF();
    }

    /**
     * Compute the point spread function
     * <p>
     * h_k(z) = |a_j(z)|² = |Σ_{j,k}A_k(z)|²
     */
    public void computePSF()
        {
    	if (PState>0)
    		return;
    	
        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);

        double a_2[];
        double PSFnorm = 1.0/(Nx*Ny*Nz);
        double defoc_scale;
        double phasePupil;
        int Npix = Nx*Ny, Ci;
        double[] A = new double[2*Npix];

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
                A[2*in] = rho[in]*Math.cos(phasePupil);
                A[2*in + 1] = rho[in]*Math.sin(phasePupil);

            }
            /* Fourier transform of the pupil function A(z) */
            FFT2D.complexForward(A);

            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                a[2*Ci] = A[2*in];
                a[2*Ci + 1] = -A[2*in + 1]; // Conjugate a
            }
        }

        /* Square modulus */
        a_2 = MathUtils.abs2(a, 1);
        for (int in = 0; in < Npix*Nz; in++)
        {
            psf[in] = a_2[in]*PSFnorm;
        }

        PState = 1;
    }

    public double[] apply_J_rho(double[] q)
    {
        int Ci, Npix = Nx*Ny;
        double defoc_scale = 0.;
        double PSFNorm = 1.0/(Nx*Ny*Nz);
        double Aq[] = new double[2*Npix];
        double J[] = new double[Ny*Nx];
        double[] JRho = new double[nb_modulus_coefs];
        double NBeta = 1/(Math.sqrt(MathUtils.innerProd(modulus_coefs, modulus_coefs)));
        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);
        
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
                Aq[2*in] = a[2*Ci]*q[Ci];
                Aq[2*in + 1] = a[2*Ci + 1]*q[Ci];
            }

            FFT2D.complexForward(Aq);

            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                double ph = phi[in] + defoc_scale*psi[in];
                J[in] = J[in] + Aq[2*in]*Math.cos(ph) - Aq[2*in + 1]*Math.sin(ph);
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

            double defoc_scale=0.;
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
                Aq[2*in] = a[2*Ci]*q[Ci];
                Aq[2*in + 1] = a[2*Ci + 1]*q[Ci];
            }

            FFT2D.complexForward(Aq);

            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
                double ph = phi[in] + defoc_scale*psi[in];
                J[in] = J[in] + rho[in]*(Aq[2*in]*Math.sin(ph) + Aq[2*in + 1]*Math.cos(ph));
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
        double defoc, tmpvar, idef, d0 = 0, d1 = 0, d2 = 0;
        double[] rx = new double[Nx];
        double[] ry = new double[Ny];
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
        for (int iz = 0; iz < Nz; iz++)
        {
            double defoc_scale =0.;
			if (iz > Nz/2)
            {
                defoc = (iz - Nz)*dz;
                defoc_scale  = DEUXPI*defoc;
            }
            else
            {
                defoc = iz*dz;
                defoc_scale = DEUXPI*defoc;
            }
			
            for (int in = 0; in < Npix; in++)
            {
                Ci = iz*Npix + in;
              Aq[2*in] = a[2*Ci]*q[Ci];
               Aq[2*in + 1] = a[2*Ci + 1]*q[Ci];
            }
            FFT2D.complexForward(Aq);

            
                for (int j = 0; j < Ny; j++)
                {
                    for (int i = 0; i < Nx; i++)
                    {
                        int in = i + j*Nx;
                        if(maskPupil[in] == 1)
                        {
                            Ci = iz*Npix + in;
                            idef= 1./psi[in];
                            double ph = phi[in] + defoc_scale*psi[in];
                            tmpvar = -DEUXPI*rho[in]*( Aq[2*in]*Math.sin(ph) + Aq[2*in + 1]*Math.cos(ph) )*PSFNorm;
                            {
                                d1 -= tmpvar*( rx[i]*(defoc*idef ));
                                d2 -= tmpvar*( ry[j]*(defoc*idef) );
                                d0 += tmpvar*( idef*lambda_ni*defoc );
                            }
                        }
                    }
                }
            }
        

        switch(nb_defocus_coefs)
        {        
        case 3:
            grd[2] = d2;
            grd[1] = d1;
        case 1:
            grd[0] = d0;
            break;
        case 2:
            grd[2] = d2;
            grd[1] = d1;
            break;
        }
        return grd;
    }


    public double[] getRho() {
        if (PState<1){
            computePSF();
        }
        return rho;
    }
    
    public double getLambda() {
        return lambda;
    }
    
    public double getNi() {
        return ni;
    }


    public double[] getPhi() {
        if (PState<1){
            computePSF();
        }
        return phi;
    }

    public double[] getPsi() {
        if (PState<1){
            computePSF();
        }
        return psi;
    }

    public double[] getBeta() {
        return modulus_coefs;
    }

    public double[] getAlpha() {
        return phase_coefs;
    }

    public double[] getDefocusMultiplyByLambda() {
        if (PState<1){
            computePSF();
        }
        double[] defocus = {lambda_ni*lambda, deltaX*lambda, deltaY*lambda};
        return defocus;
    }
    
    public double[] getDefocus() {
        if (PState<1){
            computePSF();
        }
        double[] defocus = {lambda_ni, deltaX, deltaY};
        return defocus;
    }

    public double[] getGamma() {
        if (PState<1){
            computePSF();
        }
        return psi;
    }

    public double[] getMaskPupil() {
        if (PState<1){
            computePSF();
        }
        return maskPupil;
    }

    public double[] getPSF() {

        if (PState<1){
            computePSF();
        }
    return psf;
    }


    public double[] getPSF(int k) {
        if (PState<1){
            computePSF();
        }
        return MathUtils.getArray(psf, Nx, Ny, k);
    }

    public double[] getZernike() {
        return Z;
    }

    public int getNZern() {
        return Nzern;
    }
  
    public double[] getZernike(int k) {
        return MathUtils.getArray(Z, Nx, Ny, k);
    }

    public double[] get_a() {
        if (PState<1){
            computePSF();
        }
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
