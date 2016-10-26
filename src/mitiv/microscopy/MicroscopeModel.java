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


import org.jtransforms.fft.DoubleFFT_2D;

import mitiv.old.MathUtils;

/**
 * Compute a 3D point spread function of a wide field fluorescence microscope
 * (WFFM).
 *
 * <p> The 3D PSF is modeled after a parameterized pupil function. It is a
 * monochromatic scalar model that defines the 3D PSF h from pupil function p.
 * Both modulus ρ(i,j) and phase φ(i,j) of pupil function p are expressed on a
 * basis of Zernike polynomials Zn.</p>
 *
 * <pre>
 * A(z) = ρ.exp(iΦ(z)) with Φ(z) = φ + 2π( z.ψ)
 * ψ the defocus function :  ψ
 * </pre>

 * <p>
 * References:
 * [1] Yves Tourneur & Eric Thiébaut, Ferreol Soulez, Loïc Denis.
 * Blind deconvolution of 3d data in wide field fluorescence microscopy.
 * </p>
 *
 * @author Ferréol Soulez	 <ferreol.soulez@epfl.ch>
 */
public class MicroscopeModel
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
    protected boolean radial=false; // when true, the PSF is radially symmetric
    protected int PState=0;   // flag to prevent useless recomputation of the PSF
    protected double deltaX;   // position in X of the center of the defocus function inside the pupil
    protected double deltaY;    // position in X of the center of the defocus function inside the pupil
    protected int nb_defocus_coefs; // number of  defocus coefficients 1: (ni/lambda) 2: {deltaX, deltaX} 3: { (ni/lambda) , deltaX, deltaX}
    protected int nb_modulus_coefs; // number of Zernike coefficients to describe the modulus
    protected int nb_phase_coefs; // number of Zernike coefficients to describe the phase
    protected double[] modulus_coefs;  // array of Zernike coefficients that describe the modulus
    protected double[] phase_coefs;  // array of Zernike coefficients that describe the phase

    protected double lambda_ni;  // (ni / \lambda)
    protected double radius; // radius of the pupil in meter
    protected double pupil_area; // area of the pupil
    protected double[] rho; // pupil modulus based on Zernike polynomials
    protected double[] phi; // pupil phase based on Zernike polynomials
    protected double[] psi; // defocus function
    protected double[] a; // fourier transform of the pupil function
    protected double[] Z; // Zernike polynomials basis
    protected double psf[]; // P3D point spread function
    protected double[] maskPupil; // position in the where the pupil is non null



    /** Initialize the WFFM PSF model containing parameters
     *  @param NA numerical aperture
     *  @param lambda emission wavelength
     *  @param ni refractive index of the immersion medium
     *  @param dxy lateral pixel size
     *  @param dz axial sampling step size
     *  @param Nx number of samples along lateral X-dimension
     *  @param Ny number of samples along lateral Y-dimension
     *  @param Nz number of samples along axial Z-dimension
     *  @param radial when true use only radial zernike polynomial
     */
    public MicroscopeModel(double NA, double lambda, double ni, double dxy, double dz, int Nx, int Ny, int Nz,boolean radial)
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
        this.radial = radial;
        this.radius = NA/lambda;
        this.lambda_ni = ni/lambda;
        this.nb_defocus_coefs = 0;
        this.nb_modulus_coefs = 0;
        this.nb_phase_coefs = 0;
        this.phi = new double[Ny*Nx];
        this.psi = new double[Ny*Nx];
        computeZernike();
        computeMaskPupil();
        this.PState = 0;
        setRho(new double[] {1.});
        setDefocus(new double[] {ni/lambda, 0., 0.});
    }

    /** Determine the map where the pupil in non null.  It sets  maskPupil and its area pupil_area
     */
    private void computeMaskPupil()
    {
        maskPupil = new double[Nx*Ny];
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
        freePSF();
    }

    /**
     * Compute the Zernike basis Z.
     */
    private void computeZernike(){
        Z = Zernike.zernikeArray(Nzern, Nx, Ny, radius*dxy*Nx, NORMALIZED,radial);
        Z = MathUtils.gram_schmidt_orthonormalization(Z, Nx, Ny, Nzern);
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
            computeZernike();
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

        freePSF();
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
        if(radial){
            if( alpha.length+1 > Nzern)
            {
                Nzern = alpha.length+1 ;
                computeZernike();
            }
        }else{
            if( alpha.length+3 > Nzern)
            {
                Nzern = alpha.length+3 ;
                computeZernike();
            }
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
                    if(radial){
                        phi[in] += Z[in + (n + 1)*Npix]*phase_coefs[n];
                    }else{
                        phi[in] += Z[in + (n + 3)*Npix]*phase_coefs[n];
                    }
                }
            }
        }
        freePSF();
    }

    /**
     * Compute the defocus aberration ψ of the phase pupil
     * <p>
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
        freePSF();
    }

    /**
     * @param defocus Update the defocus and the depth functions according the parameters
     * defocus. Depending on the number of elements of defocus:
     * 3 :  defocus = {n_i / \lambda, \delta_x, \delta_y}
     * 2 :  defocus = { \delta_x, \delta_y}
     * 1 :  defocus = {n_i / \lambda}
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
            throw new IllegalArgumentException("bad defocus  parameters");
        }
        computeDefocus();
        freePSF();
    }


    /**
     * Compute the point spread function
     * <p>
     * h_k(z) = |a_k(z)|² = |Σ_j F_{j,k} A_j(z)|²
     */
    public void computePSF()
    {
        if (PState>0)
            return;

        this.psf = new double[Nz*Ny*Nx];
        this.a = new double[Nz*Ny*2*Nx];

        DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);

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
                a[2*Ci + 1] = -A[2*in + 1]; // store conjugate of A
                psf[Ci] = (A[2*in]*A[2*in] + A[2*in+1]*A[2*in+1])*PSFnorm ;
            }
        }

        PState = 1;
    }

    /**
     * Apply the Jacobian matrix to go from  the PSF space to modulus coefficients space.
     * @param q : the gradient of some criterion in the PSF space
     * @return the gradient of this criterion in the modulus coefficients space.
     */
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


    /**
     * Apply the Jacobian matrix to go from  the PSF space to phase coefficients space.
     * @param q : the gradient of some criterion in the PSF space
     * @return the gradient of this criterion in the phase coefficients space.
     */
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
                if(radial){
                    tmp += J[in]*Z[Ci + 1*Npix];
                }else{
                    tmp += J[in]*Z[Ci + 3*Npix];
                }
            }
            JPhi[k] = -2*PSFNorm*tmp;
        }
        return JPhi;
    }


    /**
     * Apply the Jacobian matrix to go from  the PSF space to defocus coefficients space.
     * @param q : the gradient of some criterion in the PSF space
     * @return the gradient of this criterion in the defocus coefficients space.
     */
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


    /**
     * @return the modulus of the pupil
     */
    public double[] getRho() {
        if (PState<1){
            computePSF();
        }
        return rho;
    }

    /**
     * @return the wavelength used in the computation
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * @return the refractive index of the immersion medium used in the computation
     */
    public double getNi() {
        return ni;
    }


    /**
     * @return the phase of the pupil
     */
    public double[] getPhi() {
        if (PState<1){
            computePSF();
        }
        return phi;
    }

    /**
     * @return the defocus function
     */
    public double[] getPsi() {
        if (PState<1){
            computePSF();
        }
        return psi;
    }

    /**
     * @return modulus coefficients
     */
    public double[] getBeta() {
        return modulus_coefs;
    }

    /**
     * @return phase coefficients
     */
    public double[] getAlpha() {
        return phase_coefs;
    }

    /**
     * @return defocus coefficients in 1./wavelength
     */
    public double[] getDefocusMultiplyByLambda() {
        if (PState<1){
            computePSF();
        }
        double[] defocus = {lambda_ni*lambda, deltaX*lambda, deltaY*lambda};
        return defocus;
    }

    /**
     * @return defocus coefficients
     */
    public double[] getDefocus() {
        if (PState<1){
            computePSF();
        }
        double[] defocus = {lambda_ni, deltaX, deltaY};
        return defocus;
    }


    /**
     * @return the pupil mask
     */
    public double[] getMaskPupil() {
        if (PState<1){
            computePSF();
        }
        return maskPupil;
    }

    /**
     * @return the PSF
     */
    public double[] getPSF() {

        if (PState<1){
            computePSF();
        }
        return psf;
    }


    /**
     * @param z
     * @return the  PSF at depth z
     */
    public double[] getPSF(int z) {
        if (PState<1){
            computePSF();
        }
        return MathUtils.getArray(psf, Nx, Ny, z);
    }

    /**
     * @return the Zernike basis
     */
    public double[] getZernike() {
        return Z;
    }

    /**
     * @return the number of zernike polynomial used in the Zernike basis
     */
    public int getNZern() {
        return Nzern;
    }

    /**
     * @param k
     * @return the k-th zernike of the basis
     */
    public double[] getZernike(int k) {
        return MathUtils.getArray(Z, Nx, Ny, k);
    }

    /**
     * @return the complex PSF
     */
    public double[] get_a() {
        if (PState<1){
            computePSF();
        }
        return a;
    }

    /**
     * Plot some information about the WideFieldModel object for debugging purpose
     */
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

    /**
     * reset PSF and its complex a  to free some memory
     * Set the flag PState to 0
     */
    public void freePSF() {
        // TODO Auto-generated method stub
        PState =0;
        a = null;
        psf = null;
    }

}
