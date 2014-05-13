package plugins.mitiv.microscopy;

/** mes boules 
*
* @author Beaubras Ludovic
* @version beta 5.32, février 2013
*/
public class Zernike2D {
	public double r[][];
	public double theta[][];
	public double Z[][][];
	//final int J;

	

	
	public Zernike2D(int nx, int ny, double dxy, int J, double radius)
	{
		zernCoef_Pupiltab2D(J, nx, ny, radius*nx*dxy);
	}
	

	public int[] zernumeroNoll(int J)
	{
		int[] nm = new int[2];
		int k = 0;
		mainLoop:
		for(int n = 0; n <= 100; n++)
			for(int m=0; m <= n; m++)
				if(Utils.Even(n - m))
				{
					k = k + 1;
					if (k == J)
					{
						nm[0] = n;
						nm[1] = m;
						break mainLoop;
					}
					if (m != 0)
					{
						k = k + 1;
						if (k == J)
						{
							nm[0] = n;
							nm[1] = m;
							break mainLoop;
						}
					}
				}
		return nm;
	}

	public void zernCoef_Pupiltab2D(int J, int nx, int ny, double radius)
	{
		Z = new double[J][nx][ny];
		double A[][];
		double N;
		double z[][];
		for(int n = 0; n < J; n++)
		{
			z  = zernikePupil(n + 1, nx, ny, radius);
			A = Utils.OuterProd(z, z);
			N = Math.sqrt(Utils.sum(A));
			for(int i = 0; i < ny; i++)
			{
				for (int j = 0; j < nx; j++) {

					Z[n][i][j] = z[i][j]/N;
				}
			}
		}
		
	}

	public double[][] zernikePupil(int J, int W, int H, double radius)
	{
		double z[][];
		r = Utils.CartesDist2D(W, H);
		theta = Utils.CartesAngle2D(W, H);
		z = zernikePupil(J, r, theta, radius);
		return z;
	}
    
	public double[][] zernikePupil(int J, double r[][], double theta[][], double radius)
    {
    	int H = r.length;
    	int W = r[0].length;
    	double[][] z = new double[H][W];

    	for (int i = 0; i < H; i++)
    	{
    		for (int j = 0; j < W; j++) {
				
    		z[i][j] = zernikePupil(J, r[i][j], theta[i][j], radius);
		}
    }
    	
    	return z;
    }
	
	public double zernikePupil(int J, double r, double theta, double radius)
    {
    	int[] nm = zernumeroNoll(J);
    	int n = nm[0];
    	int m = nm[1];
    	double z = 0;

    	if(r > radius) {
    		z = 0;
    	}
    	else
    	{
    		if( J == 1) {
    			z = 1;
    		}
    		else {
    			r = r/radius;
    			for( int s = 0; s <= (n - m)/2; s++ )
    				z = z + Math.pow(-1, s) * Utils.Factorial(n - s) * Math.pow(r, n - 2*s)
    				/( Utils.Factorial(s) * Utils.Factorial((n + m)/2 - s) * Utils.Factorial((n - m)/2 - s) );

    			if(Utils.Even(J)) {
    				if( m == 0 ) {
    					z = Math.sqrt( n + 1 )*z;
    				}
    				else {
    					z = Math.sqrt( 2*(n + 1) ) * z * Math.cos( m * theta );
    				}
    			}
    			else {
    				if( m == 0 ) {
    					z = Math.sqrt( n + 1 )*z;
    				}
    				else {
    					z = Math.sqrt( 2*(n + 1) ) * z * Math.sin( m * theta );
    				}
    			}
    		}
    	}

    	return z;
    }
	
	public final double getZernCoef(int k1, int k2, int n) {
		
        return Z[n][k2][k1];
    }

	public void zernCoef3D(int J)
	{
		int H = r.length;
		int W = r[0].length;
		Z = new double[J][H][W];
		double A[][];
		double N;
		double z[][];
		for(int k = 0; k < J; k++)
		{		
			z  = zernikeNoll(k + 1, r, theta);
			A = Utils.OuterProd(z, z);
			N = Math.sqrt(Utils.sum(A));
			// Condition r < 1
			for(int i = 0; i < H; i++)
				for(int j = 0; j < W; j++)
				{
					if(r[i][j] > 1)
						z[i][j] = 0;
					Z[k][i][j] = z[i][j]/N;
				}
		}
	}
	
	

	public double[][] zernikeNoll(int J, double[][] r, double theta[][])
	{
		int H = r.length;
		int W = r[0].length;
		int[] nm = zernumeroNoll(J);
		int n = nm[0];
		int m = nm[1];
		double[][] z = new double[H][W];

		if( J == 1)
		{
			for( int i = 0; i < H; i++)
				for( int j = 0; j < W; j++)
					z[i][j] = 1;
		}
		else
		{
			for( int i = 0; i < H; i++ )
				for( int j = 0; j < W; j++ )
					for( int s = 0; s <= (n - m)/2; s++ )
						z[i][j] = z[i][j] + Math.pow(-1, s) * Utils.Factorial(n - s) * Math.pow(r[i][j], n -2*s)
						/( Utils.Factorial(s) * Utils.Factorial((n + m)/2 - s) * Utils.Factorial((n - m)/2 - s) );

			if(Utils.Even(J))
			{
				if( m == 0 )
				{
					for( int i = 0; i < H; i++ )
						for( int j = 0; j < W; j++ )
							z[i][j] = Math.sqrt( n + 1 )*z[i][j];

				}
				else
				{
					for( int i = 0; i < H; i++ )
						for( int j = 0; j < W; j++ )
							z[i][j] = Math.sqrt( 2*(n + 1) ) * z[i][j] * Math.cos( m * theta[i][j] );
				}
			}
			else
			{
				if( m == 0 )
				{
					for( int i = 0; i < H; i++ )
						for( int j = 0; j < W; j++ )
							z[i][j] = Math.sqrt( n + 1 )*z[i][j];
				}
				else
				{
					for( int i = 0; i < H; i++ )
						for( int j = 0; j < W; j++ )
							z[i][j] = Math.sqrt( 2*(n + 1) ) * z[i][j] * Math.sin( m * theta[i][j] );
				}
			}
		}
		return z;
	}
    
    /**
     * Computes 2D forward DFT of complex data leaving the result in
     * <code>a</code>. The data is stored in 1D array in row-major order.
     * Complex number is stored as two double values in sequence: the real and
     * imaginary part, i.e. the input array must be of size rows*2*columns. The
     * physical layout of the input data has to be as follows:<br>
     * 
     * <pre>
     * a[k1*2*columns+2*k2] = Re[k1][k2], 
     * a[k1*2*columns+2*k2+1] = Im[k1][k2], 0&lt;=k1&lt;rows, 0&lt;=k2&lt;columns,
     * </pre>
     * 
     * @param a
     *            data to transform
     */
	public double[] coeffRadialZ(int J)
	{
		int[] nm = zernumeroNoll(J);
		int n = nm[0];
		int m = nm[1];
		double R_mn[] = new double[n];
		for( int s = 0; s <= (n - m)/2; s++ )
		{
			R_mn[s] = Math.pow(-1, s) * Utils.Factorial(n - s)/
					( Utils.Factorial(s) * Utils.Factorial((n + m)/2 - s) * Utils.Factorial((n - m)/2 - s) );
		}
		return R_mn;
	}
	
	public double[] degRadialZ(int J)
	{
		int[] nm = zernumeroNoll(J);
		int n = nm[0];
		int m = nm[1];
		double degZ[] = new double[n];
		for( int s = 0; s <= (n - m)/2; s++ )
		{
			degZ[s] = n-2*s;
		}
		return degZ;
	}
	



}