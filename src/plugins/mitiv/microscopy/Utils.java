package plugins.mitiv.microscopy;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import mitiv.utils.ColorMap;

public class Utils {
    
    public double r[][];
    public double theta[][];
    
    
    // FIXME fonction 1D en 2D et vice versa; addition, multi de matrices; zernike en 1D; zernike sur une seule coordonnée
    // orthonormalisation
    /* Check if the number is even */
    public static boolean Even(int x)
    {
        return ( x % 2 == 0 );
    }
    /**
     * Returns the factorial of a number. 
     *
     * @param  n  an integer value
     * @return the factorial of the number
     */
    public static int Factorial(int n)
    {
        if (n == 0)
            return 1;
        else
            return n * Factorial(n-1);
    }

    public static double[] Array2DTo1D(double[][] In)
    {
        int H = In.length;
        int W = In[0].length;
        double[] Out = new double[H*W];
        for (int i = 0; i < H; i++)
            for (int j = 0; j < W; j++)
                Out[i*W + j] = In[i][j];
        return Out;
        
    }
    
    public static double[][] Array1DTo2D(double[] In, int W)
    {
        int H = In.length;
        double Out[][] = new double[H][W];
        for (int i = 0; i < H; i++)
            for (int j = 0; j < W; j++)
                Out[i][j] = In[i*W + j];
        return Out;
    }
    /* Transform Cartesian to polar or cylindrical coordinates */
    // faire deux fonctions r, et theta
    public void cart2pol(double[] x, double[] y)
    {
        int W = x.length;
        int H = y.length;
        r = new double[H][W];
        theta = new double[H][W];
        for( int i = 0; i < W; i++)
            for( int j = 0; j < H; j++)
            {
                theta[i][j] = Math.atan2( y[i], x[j]);
                r[i][j] = Math.sqrt(x[i] * x[i] + y[j] * y[j]);
            }
    }
    
    public void dist(double r, double x, double y)
    {
        r = Math.sqrt(x*x + y*y);
    }
    
    public static double[][] CartesDist2D(int W, int H)
    {
        double x[] = new double[W];
        double y[] = new double[H];
        double R[][] = new double[H][W];
        int b;
        int e;
        if(Even(W))
        {
            b = -(W - 1)/2;
            e = (W + 1)/2;
        }
        else
        {
            b = -(W - 1)/2;
            e = -b;
        }
            
        x = span(b, e, 1);
        y = span(b, e, 1);
        for( int i = 0; i < H; i++)
            for( int j = 0; j < W; j++)
                R[i][j] = Math.sqrt(x[i] * x[i] + y[j] * y[j]);
        return R;
    }
    
    public static double[][] fft_dist(int W, int H)
    {
        double R[][] = CartesDist2D(W, H);
        R = fftshift(R);
        return R;
    }
    
    public static double[] CartesDist1D(int W, int H)
    {
        double x[] = new double[W];
        double y[] = new double[H];
        double R[] = new double[H*W];
        int b;
        int e;
        if(Even(W))
        {
            b = -(W - 1)/2;
            e = (W + 1)/2;
        }
        else
        {
            b = -(W - 1)/2;
            e = -b;
        }
            
        x = span(b, e, 1);
        y = span(b, e, 1);
        for( int i = 0; i < H; i++)
            for( int j = 0; j < W; j++)
                R[i*W + j] = Math.sqrt(x[i] * x[i] + y[j] * y[j]);
        return R;
    }
    
    public static double[] CartesAngle1D(int W, int H)
    {
        double x[] = new double[W];
        double y[] = new double[H];
        double THETA[] = new double[H*W];
        int b;
        int e;
        if(Even(W))
        {
            b = -(W - 1)/2;
            e = (W + 1)/2;
        }
        else
        {
            b = -(W - 1)/2;
            e = -b;
        }
            
        x = span(b, e, 1);
        y = span(b, e, 1);
        for( int i = 0; i < H; i++)
            for( int j = 0; j < W; j++)
                THETA[i*W + j] = Math.atan2( y[i], x[j]);
        return THETA;
    }
    
    public static double[][] CartesAngle2D(int W, int H)
    {
        double x[] = new double[W];
        double y[] = new double[H];
        double THETA[][] = new double[H][W];
        int b;
        int e;
        if(Even(W))
        {
            b = -(W - 1)/2;
            e = (W + 1)/2;
        }
        else
        {
            b = -(W - 1)/2;
            e = -b;
        }
            
        x = span(b, e, 1);
        y = span(b, e, 1);
        for( int i = 0; i < H; i++)
            for( int j = 0; j < W; j++)
                THETA[i][j] = Math.atan2( y[i], x[j]);
        return THETA;
    }
    
    /* Generate 1-D array with span */
    public static double[] span(double begin, double end, double scale)
    {
        double a = begin;
        int L = 0;
        while( a <= end )
        {
            a = a + scale;
            L = L + 1;
        }
        double tab[] = new double[L];
        
        a = begin;
        int b = 0;  
        for(double i = begin; i <= end; i+= scale)
        {
            tab[b] = i;
            b++;
        }
        return tab;
    }

    public int[] zernumero(int j)
    {
        int n, m;
        int[] nm = new int[2];
        n = (int) Math.ceil( (-3 + Math.sqrt(9 + 8*j))/2 );
        m = 2*j - n*(n+2);
        nm[0] = n;
        nm[1] = m;
        
        return nm;
    }

    public int[] zernumeroNoll(int J)
    {
        int[] nm = new int[2];
        int k = 0;
        mainLoop:
        for(int n = 0; n <= 100; n++)
            for(int m=0; m <= n; m++)
                if(Even(n - m))
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

    
    /*
    public final double getZernCoef(int k1, int k2, int n) {
        
        return Z[n][k2][k1];
    }
    
    public void zernCoef3D(int J)
    {
        double xx[];
        xx = span(-1, 1, 0.01);
        int H = xx.length;
        int W = xx.length;
        Z = new double[J + 1][H][W];
        double z[][] = null;
        cart2pol(xx, xx);
        for(int k = 1; k <= J; k++)
        {       
            z  = zernikeNoll(k, r, theta);
            // Condition r < 1
            for(int i = 0; i < H; i++)
                for(int j = 0; j < W; j++)
                {
                    if(r[i][j] > 1)
                        z[i][j] = 0;
                    Z[k][i][j] = z[i][j];
                }
        }
    }
    */
    public double[][] zernike(int n, int m, double[][] r, double theta[][])
    {
        //FIXME Résultat à revoir, division par 2 (à comparer avec zernfun)
        int Lx = r[0].length;
        int Ly = r.length;
        int m_abs;
        double N;
        double[][] z = new double[Lx][Ly];
        int rpower[] = new int[n + 1];
        
        // Compute Normalization Constant
        if( m == 0 )
            N = Math.sqrt( n + 1 );
        else
            N = Math.sqrt( 2 * (n + 1) );
        // Determine the required powers of r:
        /*------------------------------------*/
        m_abs = Math.abs(m);
        
        for(int i = m_abs; i <= n; i++)
            rpower[i] = 2*i;
        
        // Pre-compute the values of r raised to the required powers,
        // and compile them in a matrix:
        // -----------------------------
        
        if( n == 0)
        {
            for( int i = 0; i < Lx; i++)
                for( int j = 0; j < Ly; j++)
                {
                    {
                        z[i][j] = 1;
                    }
                }
        }
        else
        {
            for( int i = 0; i < Lx; i++ )
                for( int j = 0; j < Ly; j++ )
                    for( int s = 0; s <= (n - m_abs)/2; s++ )
                    {
                        z[i][j] = z[i][j] + Math.pow(-1, s) * Factorial(n - s) * Math.pow(r[i][j], n-2*s)
                                /( Factorial(s) * Factorial((n + m_abs)/2 - s) * Factorial((n - m_abs)/2 - s) );
                    }
            if( m == 0 )
            {
                for( int i = 0; i < Lx; i++ )
                    for( int j = 0; j < Ly; j++ )
                    {
                        z[i][j] = N * z[i][j];
                    }
            }
            else if( m > 0 )
            {
                for( int i = 0; i < Lx; i++ )
                    for( int j = 0; j < Ly; j++ )
                    {
                        z[i][j] = N * z[i][j] * Math.cos( m_abs * theta[i][j] );
                    }
            }
            else
            {
                {
                    for( int i = 0; i < Lx; i++ )
                        for( int j = 0; j < Ly; j++ )
                            z[i][j] = N * z[i][j] * Math.sin( (-m_abs) * theta[i][j] );
                }
            }
        }
        return z;
    }
    /**
    * Compute the Zernike polynomials. Zernike functions of order n and frequency m on the unit circle.
    * returns the Zernike functions of order N and angular frequency M, evaluated at positions (R,THETA) on the unit circle.
    * @param n highest power or order of the radial polynomial (a positive integer)
    * @param m azimuthal (angular) frequency of the sinusoidal component (a signed integer)
    * @param r 2D table of polar coordinates (distance)
    * @param theta 2D table of angles
    * @return z 2D table of Zernike functions (n,m) evaluated in (r, theta)
    * @see cart2pol
    */
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
                        z[i][j] = z[i][j] + Math.pow(-1, s) * Factorial(n - s) * Math.pow(r[i][j], n -2*s)
                        /( Factorial(s) * Factorial((n + m)/2 - s) * Factorial((n - m)/2 - s) );

            if(Even(J))
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

    public double[][] Rho(double[] beta, double[][][] Z)
    {
        int H = Z[0].length;
        int W = Z[0][0].length;
        int n = beta.length;
        double rho_n[][] = new double[H][W];
        
        for(int k = 1; k <= n; k++)
            for(int i = 0; i < H; i++)
                for(int j = 0; j < W; j++)
                    rho_n[i][j] = rho_n[i][j] + beta[k - 1]*Z[k][i][j];
        return rho_n;
    }
    
    public double[][] Phi(double[] alpha, double[][][] Z)
    {
        int H = Z[0].length;
        int W = Z[0][0].length;
        int n = alpha.length;
        double phi_n[][] = new double[H][W];
        
        for(int k = 4; k < n + 4; k++)
            for(int i = 0; i < H; i++)
                for(int j = 0; j < W; j++)
                    phi_n[i][j] = phi_n[i][j] + alpha[k - 4]*Z[k][i][j];
        return phi_n;
    }
    
    public double[][] Psi(double lambda, double ni, double deltaX, double deltaY, final double[][][] Z)
    {
        int H = Z[0].length;
        int W = Z[0][0].length;
        double psi0 = ni/lambda;
        double q = psi0*psi0;
        double psi[][] = new double[H][W];
        final double[][] xTilt = Z[2]; // FIXME: check indices
        final double[][] yTilt = Z[3]; // FIXME: check indices
        
        for(int i = 0; i < H; i++) {
            for(int j = 0; j < W; j++) {
                double dx = xTilt[i][j] - deltaX;
                double dy = yTilt[i][j] - deltaY;
                // FIXME: check that q >= dx*dx + dy*dy
                psi[i][j] = Math.sqrt(q - (dx*dx + dy*dy));
            }
        }
        return psi;
    }
    
    public static double[] ScaleArray8bit(double[] A)
    {
        int L = A.length;
        double[] scaleA = new double[L];
        double minScaleA = min(A);
        double maxScaleA = max(A);
        double deltaScaleA = maxScaleA - minScaleA;
        for(int i = 0; i < L; i++)
                scaleA[i] = (A[i] - minScaleA)*255/deltaScaleA;
        
        return scaleA;
    }
    
    public static double[][] ScaleArray8bit(double[][] A)
    {
        int H = A.length;
        int W = A[0].length;
        double[][] scaleA = new double[H][W];
        double minScaleA = min(A);
        double maxScaleA = max(A);
        double deltaScaleA = maxScaleA - minScaleA;
        for(int i = 0; i < H; i++)
            for(int j = 0; j < W; j++)
                scaleA[i][j] = (A[i][j] - minScaleA)*255/deltaScaleA;
        
        return scaleA;
    }
  
    public static double[][] abs2(double[][] IN)
    {
        int H = IN.length;
        int W = IN[0].length/2;
        double[][] OUT = new double[H][W];
        for(int i = 0; i < H; i++)
            for(int j = 0; j < W; j++)
                OUT[i][j] = IN[i][2*j]*IN[i][2*j] + IN[i][2*j + 1]*IN[i][2*j + 1];

        return OUT;
    }
    
    public static double[] abs2(double[] IN)
    {
        int L = IN.length/2;
        double[] OUT = new double[L];
        for(int i = 0; i < L; i++)
                OUT[i] = IN[2*i]*IN[2*i] + IN[2*i + 1]*IN[2*i + 1];

        return OUT;
    }
    
    public static double min(double[] matrix)
    {
        double min = matrix[0];
        for (int i = 0; i < matrix.length; i++)
        {
                if (min > matrix[i])
                    min = matrix[i];
        }
        return min;
    }
    
    public static double min(double[][] matrix) {
        double min = matrix[0][0];
        for (int col = 0; col < matrix.length; col++) {
            for (int row = 0; row < matrix[col].length; row++) {
                if (min > matrix[col][row]) {
                    min = matrix[col][row];
                }
            }
        }
        return min;
    }
    
    public static double max(double[] matrix)
    {
        double max = matrix[0];
        for (int i = 0; i < matrix.length; i++)
        {
                if (max < matrix[i])
                    max = matrix[i];
        }
        return max;
    }
    
    public static double max(double[][] matrix) {
        double max = matrix[0][0];
        for (int col = 0; col < matrix.length; col++) {
            for (int row = 0; row < matrix[col].length; row++) {
                if (max < matrix[col][row]) {
                    max = matrix[col][row];
                }
            }
        }
        return max;
    }
    
    public void saveBufferedImage(BufferedImage I, String name)
    {
        File output_zern = new File(name);
        try {
            ImageIO.write(I, "PNG", output_zern);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
 
    public static void showBufferedImage(BufferedImage I)
    {
        JFrame caca = new JFrame();
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(I));
        caca.add(label);
        caca.pack();
        caca.setVisible(true);
        caca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void showArrayI(double A[], int W)
    {   
        ColorMap map = ColorMap.getJet(256);
        int L = A.length;
        int H = L/W;
        double S[];
        S = ScaleArray8bit(A);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        
        for(int i = 0; i < H; i++)
        {
            for(int j = 0; j < W; j++)
            {
                Color b = map.table[ (int) S[i*W + j] ];
                bufferedI.setRGB(j, i, b.getRGB()); // j, i inversé
            }
        }
        
        JFrame caca = new JFrame();
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(bufferedI));
        caca.add(label);
        caca.pack();
        caca.setVisible(true);
        caca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void showArrayI(double A[][])
    {   
        ColorMap map = ColorMap.getJet(256);
        int H = A.length;
        int W = A[0].length;
        double S[][];
        S = ScaleArray8bit(A);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        
        for(int i = 0; i < H; i++)
        {
            for(int j = 0; j < W; j++)
            {
                Color b = map.table[ (int) S[i][j] ];
                bufferedI.setRGB(j, i, b.getRGB()); // j, i inversé
            }
        }
        
        JFrame caca = new JFrame();
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(bufferedI));
        caca.add(label);
        caca.pack();
        caca.setVisible(true);
        caca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void showArrayFFT(double A[][])
    {   
        ColorMap map = ColorMap.getJet(256);
        int H = A.length;
        int W = A[0].length;
        double S[][];
        double S_pad[][] = new double[H][W];
        S = ScaleArray8bit(A);
        /**Pad PSF array H*W : PSFArrayPad**/
        //Essayer avec une matric simple..
        //Premier cadrant : haut gauche se retrouve en bas à droite
        for(int i = 0; i < H/2; i++)
        {
            for(int j = 0; j < W/2; j++)
            {
                S_pad[H - H/2 + i][W - W/2 + j] = S[i][j];
            }
        }
        //Haut droit en bas à gauche
        for(int i = 0; i < H/2; i++)
        {
            for(int j = 0; j < W/2; j++)
            {
                S_pad[H - H/2 + i][j] = S[i][j + W/2];
            }
        }
        //Bas gaucHe en Haut à droite
        for(int i = 0; i < H/2; i++)
        {
            for(int j = 0; j < W/2; j++)
            {
                S_pad[i][W - W/2 + j] = S[i + H/2][j];
            }
        }
        //Bas droit en Haut gaucHe
        for(int i = 0; i < H/2; i++)
        {
            for(int j = 0; j < W/2; j++)
            {
                S_pad[i][j] = S[i + H/2][j + W/2];
            }
        }
        
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        
        for(int i = 0; i < H; i++)
        {
            for(int j = 0; j < W; j++)
            {
                Color b = map.table[ (int) S_pad[i][j] ];
                bufferedI.setRGB(j, i, b.getRGB()); // j, i inversé
            }
        }
        
        JFrame caca = new JFrame();
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(bufferedI));
        caca.add(label);
        caca.pack();
        caca.setVisible(true);
        caca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static BufferedImage Array2BufferedImage(double[][] A)
    {
        int H = A.length;
        int W = A[0].length;
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < H; i++)
            for(int j = 0; j < W; j++)
            {
                Color b = new Color( (int) A[i][j], (int) A[i][j], (int) A[i][j] );
                bufferedI.setRGB(j, i, b.getRGB()); // j, i inversé
            }
        return bufferedI;
    }
    
    public static BufferedImage Array2BufferedImageColor(double[][] A)
    {
        double[][] S = ScaleArray8bit(A);
        ColorMap map = ColorMap.getJet(256);
        int H = A.length;
        int W = A[0].length;
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        
        for(int i = 0; i < H; i++)
            for(int j = 0; j < W; j++)
            {
                Color b = map.table[ (int)S[i][j] ];
                bufferedI.setRGB(j, i, b.getRGB()); // j, i inversé
            }
        return bufferedI;
    }
    
    public static double[][] fftshift(double A[][])
    {   
        int H = A.length;
        int W = A[0].length;
        double S_shift[][] = new double[H][W];
        /**Pad PSF array H*W : PSFArrayPad**/
        //Essayer avec une matric simple..
        //Premier cadrant : haut gauche se retrouve en bas à droite
        for(int i = 0; i < H/2; i++)
        {
            for(int j = 0; j < W/2; j++)
            {
                S_shift[H - H/2 + i][W - W/2 + j] = A[i][j];
            }
        }
        //Haut droit en bas à gauche
        for(int i = 0; i < H/2; i++)
        {
            for(int j = 0; j < W/2; j++)
            {
                S_shift[H - H/2 + i][j] = A[i][j + W/2];
            }
        }
        //Bas gaucHe en Haut à droite
        for(int i = 0; i < H/2; i++)
        {
            for(int j = 0; j < W/2; j++)
            {
                S_shift[i][W - W/2 + j] = A[i + H/2][j];
            }
        }
        //Bas droit en Haut gaucHe
        for(int i = 0; i < H/2; i++)
        {
            for(int j = 0; j < W/2; j++)
            {
                S_shift[i][j] = A[i + H/2][j + W/2];
            }
        }
        return S_shift;
    }
    
    public static double[][] fft_shift8bit(double A[][])
    {   
        int H = A.length;
        int W = A[0].length;
        double S[][];
        double S_pad[][] = new double[H][W];
        S = ScaleArray8bit(A);
        /**Pad PSF array H*W : PSFArrayPad**/
        //Essayer avec une matric simple..
        //Premier cadrant : haut gauche se retrouve en bas à droite
        for(int i = 0; i < H/2; i++)
        {
            for(int j = 0; j < W/2; j++)
            {
                S_pad[H - H/2 + i][W - W/2 + j] = S[i][j];
            }
        }
        //Haut droit en bas à gauche
        for(int i = 0; i < H/2; i++)
        {
            for(int j = 0; j < W/2; j++)
            {
                S_pad[H - H/2 + i][j] = S[i][j + W/2];
            }
        }
        //Bas gaucHe en Haut à droite
        for(int i = 0; i < H/2; i++)
        {
            for(int j = 0; j < W/2; j++)
            {
                S_pad[i][W - W/2 + j] = S[i + H/2][j];
            }
        }
        //Bas droit en Haut gaucHe
        for(int i = 0; i < H/2; i++)
        {
            for(int j = 0; j < W/2; j++)
            {
                S_pad[i][j] = S[i + H/2][j + W/2];
            }
        }
        return S_pad;
    }
  
    public static void saveArray2Image(double[] A, int W, String name)
    {
        ColorMap map = ColorMap.getJet(256);
        int L = A.length;
        int H = L/W;
        double S[];
        S = Utils.ScaleArray8bit(A);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        
        for(int i = 0; i < H; i++)
        {
            for(int j = 0; j < W; j++)
            {
                Color b = map.table[ (int) S[i*W + j] ];
                bufferedI.setRGB(j, i, b.getRGB()); // j, i inversé
            }
        }
        
        try {
            ImageIO.write(bufferedI, "png", new File(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveArray2Image(double[][] A,  String name)
    {
        ColorMap map = ColorMap.getJet(256);
        int H = A.length;
        int W = A[0].length;
        double S[][];
        S = Utils.ScaleArray8bit(A);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        
        for(int i = 0; i < H; i++)
        {
            for(int j = 0; j < W; j++)
            {
                Color b = map.table[ (int) S[i][j] ];
                bufferedI.setRGB(j, i, b.getRGB()); // j, i inversé
            }
        }
        
        try {
            ImageIO.write(bufferedI, "png", new File(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static double avg(double[] array)
    {
        int L = array.length;
        double mean = 0;
        for(int i = 0; i < L; i++)
                mean = mean + array[i];
        return mean/L;
    }
    
    public static double avg(double[][] array)
    {
        int H = array.length;
        int W = array[0].length;
        double mean = 0;
        for(int i = 0; i < H; i++)
            for(int j = 0; j < W; j++)
                mean = mean + array[i][j];

        return mean/(H*W);
    }
    
    public static double sum(double array[])
    {
        int size = array.length;
        double sum = 0;
        for (int i = 0; i < size; i++) {
            sum += array[i];
        }
        return sum;
    }
    
    public static double sum(double array[][])
    {
        int H = array.length;
        int W = array[0].length;
        double sum = 0;
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                sum += array[i][j];
            }
        }
        return sum;
    }
 
    public static double[] OuterProd(double a[], double b[])
    {
        int L = a.length;
        double out[] = new double[L];
        for (int i = 0; i < L; i++) {
            out[i] = a[i]*b[i];
        }
        return out;
    }
    
    public static double[][] OuterProd(double a[][], double b[][])
    {
        int H = a.length;
        int W = a[0].length;
        double out[][] = new double[H][W];
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                out[i][j] = a[i][j]*b[i][j];
            }
        }
        return out;
    }
    
    public static double[][] sumArrays(double a[][], double b[][], String sign)
    {
        int H = a.length;
        int W = a[0].length;
        double out[][] = new double[H][W];
        if(sign == "+")
        {
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                out[i][j] = a[i][j] + b[i][j];
            }
        }
        }else{
            for (int i = 0; i < H; i++) {
                for (int j = 0; j < W; j++) {
                    out[i][j] = a[i][j] - b[i][j];
                }
            }
        }
        return out;
    }
    
    public static double std(double a[][])
    {
        double V = var(a);
        return Math.sqrt(V);
    }
    
    public static double var(double a[][])
    {
        double mean = avg(a);
        double out;
        out = avg(OuterProd(a, a)) - mean*mean;
        return out;
    }
    
    public static void stat(double a[][])
    {
        System.out.println("H " + a.length + " W " + a[0].length);
        System.out.println("min " + min(a) + " max " + max(a));
        System.out.println("avg " + avg(a) + " var " + var(a) + " std " + std(a));
    }
    
    public static double[][][] XY2XZ(double inXY[][][])
    {
        int Nz = inXY.length;
        int Ny = inXY[0].length;
        int Nx = inXY[0][0].length;
        double outXZ[][][] = new double[Ny][Nz][Nx];
        for(int j = 0; j<Nx; j++)
        {
            for (int z = Nz-1; z >= 0; z--) {
                for (int i = 0; i < Nx; i++) {
                    outXZ[j][Math.abs(z - 1)][i] = inXY[z][i][j];
                }
            }
        }
        return outXZ;
    }
}
