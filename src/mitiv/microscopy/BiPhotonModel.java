
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

public class BiPhotonModel {
 protected WideFieldModel WFFM;

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
    public BiPhotonModel(double NA, double lambda, double ni,double ns,
            double zdepth, double dxy, double dz, int Nx, int Ny, int Nz, boolean use_depth_scaling)
    {
    	WFFM = new WideFieldModel( NA,  lambda,  ni, ns,
                 zdepth,  dxy,  dz,  Nx,  Ny,  Nz,  use_depth_scaling); 
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
    }

    /**
     * Compute the defocus aberration ψ and depth aberration
     * γ of the phase pupil
     * <p>
     * @param deltaX
     * @param deltaY
     * @param zdepth
     */
   
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

    }

  
    public double[] apply_J_rho(double[] q)
    {
    	// TODO
          return JRho;
    }

    public double[] apply_J_phi(double[] q)
    {
    	// TODO
      }

    public double[] apply_J_defocus(double[] q)
    {
    	// TODO
    }


    public double[] getRho() {
        return rho;
    }
    
    public double getLambda() {
        return WFFM.getLambda();
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


    public double[] getPSF(int k) 

    // TODO 
        if (PState<1){
            computePSF();
        }
        return MathUtils.getArray(psf, Nx, Ny, k);
    }

    public double[] getZernike() {
        return WFFM.getZernike();
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