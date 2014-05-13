package plugins.mitiv.microscopy;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;

public class MicroscopyModelPSF2D2 {
	protected double numericalAperture = 0.0; // the numerical aperture
	protected double wavelength = 0.0; // the emission wavelength in meters
	protected double refractiveIndex = 1.0; // the refractive index of the immersion medium (FIXME: variable)

	protected double lateralStep = 1.0; // the lateral pixel size in meter
	protected double axialStep = 1.0; // the axial sampling step size in meter

	protected int Nx; // number of samples along lateral X-dimension
	protected int Ny; // number of samples along lateral Y-dimension
	protected int Nz; // number of samples along axial Z-dimension (FIXME: not used?)

	protected int Nzern; // number of Zernike modes
	protected int Nfreq; // number of frequels in a pupil plane
	protected int Npix;

	protected double[][][] zernCoef3D;
	protected double[] zernCoef;
	protected double[][] rho;
	protected double[][] phi;
	protected double[][] psi;
	protected double[][] phasePupil;
	protected double[][] complexAmplitude;
	protected double[][][] a;
	protected Zernike2D zernike;

	MicroscopyModelPSF2D2(double numericalAperture,
			double wavelength,
			double refractiveIndex, // FIXME:
			double lateralStep,
			double axialStep,
			int Nx, int Ny, int Nz, int Nzern, int Nfreq) {
		// FIXME: check arguments
		this.numericalAperture = numericalAperture;
		this.wavelength = wavelength;
		this.refractiveIndex = refractiveIndex;
		this.lateralStep = lateralStep;
		this.axialStep = axialStep;
		this.Nx = Nx;
		this.Ny = Ny;
		this.Nz = Nz;
		this.Nzern = Nzern;
		this.Nfreq = Nfreq;
		this.Npix = Nx*Ny;

		// Allocate workspaces.
		this.zernCoef = new double[Nzern*Nfreq];
		this.rho = new double[Nx][Ny];
		this.phi = new double[Nx][Ny];
		this.psi = new double[Nx][Ny];
		this.phasePupil = new double[Nx][Ny];
		this.complexAmplitude = new double[Ny][2*Nx];
		this.a = new double[Nz][Ny][2*Nx];
		zernike = new Zernike2D(Nx, Ny, lateralStep, Nzern, numericalAperture/wavelength);
		// FIXME: PSF 2D, etc
	}

	public final double getZernCoef(int k1, int k2, int n) {
		//return zernCoef3D[n][k2][k1];
		return zernike.getZernCoef(k1, k2, n);
	}
	public final void setZernCoef(int k1, int k2, int n, double value) {
		zernCoef3D[n][k2][k1] = value;
	}

	public final double getZernCoef(int k, int n) {
		return zernCoef[k*Nzern + n];
	}
	public final void setZernCoef(int k, int n, double value) {
		zernCoef[k*Nzern + n] = value;
	}

	public double rho_k1_k2(double beta[], int k1, int k2)
	{
		double rho = 0;
		double coef;
		double betaNorm = 1./(Math.sqrt(Utils.sum(beta)));
		for (int n = 0; n < beta.length; ++n)
		{
			coef = getZernCoef(k1, k2, n);
			rho += beta[n]*coef*betaNorm;
		}
		return rho;
	}

	public double phi_k1_k2(double alpha[], int k1, int k2)
	{
		double phi = 0;
		double coef;
		for (int n = 0; n < alpha.length; ++n)
		{
			coef = getZernCoef(k1, k2, n + 3);
			phi += alpha[n]*coef;
		}
		return phi;
	}
	// Compute 2D PSF (i.e. for a given depth
			/**
			 * 
			 * @param psf        Output PSF (nx-by-ny array).
			 * @param alpha      Input weights for the phase (nzern elements).
			 * @param beta       Input weights for the modulus (nzern elements).
			 * @param z          Depth (in meters).
			 * @param deltaX     X-offset of optical axis (units?).
			 * @param deltaY     Y-offset of optical axis (units?)
			 */
	public void computePSF(double[] psf, final double[] alpha, final double[] beta,
			double z, double deltaX, double deltaY)
	{
		if (psf.length != Npix ||
				alpha.length > Nzern || beta.length > Nzern)
		{
			throw new IllegalArgumentException();
		}
		// Compute pupil function
		DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);
		double A_2[][];
		double psi0 = refractiveIndex/wavelength;
		double q0 = psi0*psi0;
		double slopeZ12 = 6.196374419403353E-4;
		double kappaX = 1/(Nx*lateralStep);
		double kappaY = 1/(Ny*lateralStep);

		for (int i = 0; i < Ny; i++)
		{
			for (int j = 0; j < Nx; j++)
			{
				double psi_k = 0.0;
				double phi_k = 0.0;
				double phasePupil_k = 0.0;
				double rho_k = 0.0;
				double re_amp_k = 0.0;
				double im_amp_k = 0.0;

				if (getZernCoef(i, j, 0) != 0.0)
				{
					double qx = kappaX*zernike.Z[1][i][j]/slopeZ12 - deltaX;
					double qy = kappaY*zernike.Z[2][i][j]/slopeZ12 - deltaY;
					double q = q0 - (qx*qx) - (qy*qy);
					// Without defocus center deltaXY
					//double qx = kappaX*zernike.r[i][j];
					//double q = q0 - qx*qx;
					if (q > 0.0)
					{
						psi_k = Math.sqrt(q);
						phi_k = phi_k1_k2(alpha,i, j);
						rho_k = rho_k1_k2(beta, i, j);
						phasePupil_k = phi_k + z*2*Math.PI*psi_k;
						re_amp_k = rho_k*Math.cos(phasePupil_k);
						im_amp_k = rho_k*Math.sin(phasePupil_k);
					}
				}
				psi[i][j] = psi_k;
				phi[i][j] = phi_k;
				rho[i][j] = rho_k;
				phasePupil[i][j] = phasePupil_k;
				complexAmplitude[i][2*j] = re_amp_k;
				complexAmplitude[i][2*j+1] = im_amp_k;
			}

		}
		//double PSFnorm = 1/(Nx*Ny*Nz);
		FFT2D.complexForward(complexAmplitude);
		A_2 = Utils.abs2(complexAmplitude);
		System.out.println("psf");
		Utils.stat(A_2);
		/*
		for (int i = 0; i < ny; i++) {
			for (int j = 0; j < nx; j++) {
				A_2[i][j] = A_2[i][j]*PSFnorm;
			}

		}
		System.out.println("psf");
		Utils.stat(A_2);
		 */
		A_2 = Utils.fftshift(A_2);


		Utils.showArrayI(A_2);
		Utils.saveArray2Image(A_2, "psf.png");


	}


	public void computePSF(double[][][] psf, final double[] alpha, final double[] beta, double deltaX, double deltaY)
	{
		// Compute pupil function
		DoubleFFT_2D FFT2D = new DoubleFFT_2D(Ny, Nx);
		double fft_a[][] = new double[Ny][2*Nx];
		double A_2[][];
		double psi0 = refractiveIndex/wavelength;
		double q0 = psi0*psi0;
		double slopeZ12 = 6.196374419403353E-4;
		double kappaX = 1/(Nx*lateralStep);
		double kappaY = 1/(Ny*lateralStep);
		double PSFnorm = 1.0/(Nx*Ny*Nz);
		double defoc_scale2[] = Utils.span((-Nz+1)/2, Nz/2, 1);
		
		for (int z = 0; z < Nz; z++)
		{


			for (int i = 0; i < Ny; i++)
			{
				for (int j = 0; j < Nx; j++)
				{
					double psi_k = 0.0;
					double phi_k = 0.0;
					double phasePupil_k = 0.0;	
					double rho_k = 0.0;
					double re_amp_k = 0.0;
					double im_amp_k = 0.0;

					if (getZernCoef(i, j, 0) != 0.0)
					{
						double qx = kappaX*zernike.Z[1][i][j]/slopeZ12 - deltaX;
						double qy = kappaY*zernike.Z[2][i][j]/slopeZ12 - deltaY;
						double q = q0 - (qx*qx) - (qy*qy);
						// Without defocus center deltaXY
						//double qx = kappaX*zernike.r[i][j];
						//double q = q0 - qx*qx;
						if (q > 0.0)
						{
							psi_k = Math.sqrt(q);
							phi_k = phi_k1_k2(alpha,i, j);
							rho_k = rho_k1_k2(beta, i, j);
							phasePupil_k = phi_k + 2*Math.PI*axialStep*defoc_scale2[z]*psi_k;
							//phasePupil_k = phi_k + defoc_scale*psi_k;
							re_amp_k = rho_k*Math.cos(phasePupil_k);
							im_amp_k = rho_k*Math.sin(phasePupil_k);
						}
					}
					psi[i][j] = psi_k;
					phi[i][j] = phi_k;
					rho[i][j] = rho_k;
					a[z][i][2*j] = re_amp_k;
					a[z][i][2*j+1] = im_amp_k;
					fft_a[i][2*j] = re_amp_k;
					fft_a[i][2*j+1] = im_amp_k;
				}
			}
			
			FFT2D.complexForward(fft_a);
			A_2 = Utils.abs2(fft_a);
			A_2 = Utils.fftshift(A_2);
			for (int i = 0; i < Ny; i++)
			{
				for (int j = 0; j < Nx; j++)
				{
					psf[z][i][j] = A_2[i][j]*PSFnorm;
				}
			}
		}
		/*System.out.println("Z0");
		Utils.stat(zernike.Z[0]);
		System.out.println("Z1");
		Utils.stat(zernike.Z[1]);
		System.out.println("Z2");
		Utils.stat(zernike.Z[2]);*/
	}
	
	public void apply_J_rho(double JRho[], final double[] beta)
	{
		double PSFNorm = 1.0/(Nx*Ny*Nz);
		double J[][] = new double[Ny][Nx];
		double defoc_scale;
		double phasePupil_k;
		double betaNorm = Math.sqrt(Utils.sum(beta));
		for (int z = 0; z < Nz; z++)
		{
			if (z > Nz/2) {
				defoc_scale = 2*Math.PI*(z - Nz)*axialStep;
			}
			else
			{
				defoc_scale = 2*Math.PI*z*axialStep;
			}
			for (int i = 0; i < Ny; i++)
			{
				for (int j = 0; j < Nx; j++)
				{
					phasePupil_k = phi[i][j] + defoc_scale*psi[i][j];
					J[i][j] = J[i][j] + a[z][i][2*j]*Math.cos(phasePupil_k) - a[z][i][2*j+1]*Math.sin(phasePupil_k);
				}
			}
		}

		for (int k = 0; k < beta.length; k++)
		{
			//for (int i = 0; i < Ny; i++)
			//{
				//for (int j = 0; j < Nx; j++)
				//{
				//	JRho[k] = JRho[k] + J[i][j]*zernike.Z[k][i][j];
			JRho[k] = Utils.sum(Utils.OuterProd(J, zernike.Z[k]));
			//	}
			//}
		}
		
		for (int k = 0; k < beta.length; k++)
		{
			JRho[k] = 2*PSFNorm*JRho[k]*(1-beta[k]*beta[k]*betaNorm)*betaNorm*betaNorm;
		}
		
	}
	
	public void apply_J_phi(double JPhi[], final double[] alpha)
	{
		double J[][] = new double[Ny][Nx];
		double defoc_scale;
		double phasePupil_k;
		double PSFNorm = 1.0/(Nx*Ny*Nz);
		for (int z = 0; z < Nz; z++)
		{
			if (z > Nz/2) {
				defoc_scale = 2*Math.PI*(z - Nz)*axialStep;
			}
			else
			{
				defoc_scale = 2*Math.PI*z*axialStep;
			}
			for (int i = 0; i < Ny; i++)
			{
				for (int j = 0; j < Nx; j++)
				{
					phasePupil_k = phi[i][j] + defoc_scale*psi[i][j];
					J[i][j] = J[i][j] + rho[i][j]*(a[z][i][2*j]*Math.sin(phasePupil_k) + a[z][i][2*j+1]*Math.cos(phasePupil_k));
				}
			}
		}

		for (int k = 0; k < alpha.length; k++)
		{
			JPhi[k] = -2*PSFNorm*Utils.sum(Utils.OuterProd(J, zernike.Z[k+3]));
		}
		
		
	}
	
	public void info()
	{
		System.out.println("Number of Zernikes : " + Nzern);
		System.out.println("Wavelength NA: " + wavelength);
		System.out.println("Nx: " + Nx);
		System.out.println("dxy: " + lateralStep);
		System.out.println("Radius : " + numericalAperture/wavelength*Nx*lateralStep);
	}
}