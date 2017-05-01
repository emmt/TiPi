/**
 *
 */
package mitiv.old;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.RealComplexFFT;
/**
 * @author ferreol
 *
 */
public class OldMathUtils {

    public static DoubleShapedVector convolution(double[] x, double[] h, int nx, int ny, int nz)
    {
        int[] shape = {nx, nx, nz};
        DoubleShapedVectorSpace space = new DoubleShapedVectorSpace(shape);
        DoubleShapedVector hVector = space.wrap(h);
        DoubleShapedVector xVector = space.wrap(x);

        /* Convolve x by the psf h */
        RealComplexFFT FFT = new RealComplexFFT(space);
        ConvolutionOperator H = new ConvolutionOperator(FFT, hVector);
        DoubleShapedVector y = space.create();
        H.apply(y, xVector);
        return y;
    }

    public static double[] convol(double[] x, double[] h, int nx, int ny, int nz)
    {
        int[] shape = {nx, nx, nz};
        DoubleShapedVectorSpace space = new DoubleShapedVectorSpace(shape);
        DoubleShapedVector hVector = space.wrap(h);
        DoubleShapedVector xVector = space.wrap(x);

        /* Convolve x by the psf h */
        RealComplexFFT FFT = new RealComplexFFT(space);
        ConvolutionOperator H = new ConvolutionOperator(FFT, hVector);
        DoubleShapedVector y = space.create();
        H.apply(y, xVector);

        return y.getData();
    }
    /**
     * Display image of an 2d array.
     *
     * @param A array to display
     * @param colorMap 0 for a grayscale display and 1 with a colormap
     */
    public static void pli(double A[][], int colorMap)
    {
        int H = A.length;
        int W = A[0].length;
        double S[][];
        S = mitiv.utils.MathUtils.scaleArrayTo8bit(A);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        //ColorMap map = ColorMap.getJet(256);

        switch (colorMap) {
            case  mitiv.utils.MathUtils.COLORMAP_JET:
                ColorMap map = ColorMap.getJet(256);
                for(int j = 0; j < W; j++)
                {
                    for(int i = 0; i < H; i++)
                    {
                        Color b = map.table[ (int) S[i][j] ];
                        bufferedI.setRGB(i, j, b.getRGB()); // j, i inversé
                    }
                }
                break;
            case mitiv.utils.MathUtils.COLORMAP_GRAYSCALE:
                Color b;
                for(int j = 0; j < W; j++)
                {
                    for(int i = 0; i < H; i++)
                    {
                        b = new Color( (int) S[i][j], (int) S[i][j], (int) S[i][j] );
                        bufferedI.setRGB(i, j, b.getRGB()); // j, i inversé
                    }
                }
            default:
                throw new IllegalArgumentException("bad value for colorMap");
        }

        JFrame frame = new JFrame();
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(bufferedI));
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Display image of an 2d array
     *
     * Different from the "pli" function, uses "naviguablePanel" for a better displaying.
     *
     * @param A the a
     * @param colorMap the color map
     */
    public static void pli2(double A[][], int colorMap)
    {
        int H = A.length;
        int W = A[0].length;
        double S[][];
        S = mitiv.utils.MathUtils.scaleArrayTo8bit(A);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);

        switch (colorMap) {
            case mitiv.utils.MathUtils.COLORMAP_JET:
                ColorMap map = ColorMap.getJet(256);
                for(int j = 0; j < W; j++)
                {
                    for(int i = 0; i < H; i++)
                    {
                        Color b = map.table[ (int) S[i][j] ];
                        bufferedI.setRGB(i, j, b.getRGB()); // j, i inversé
                    }
                }
                break;
            case mitiv.utils.MathUtils.COLORMAP_GRAYSCALE:
                Color b;
                for(int j = 0; j < W; j++)
                {
                    for(int i = 0; i < H; i++)
                    {
                        b = new Color( (int) S[i][j], (int) S[i][j], (int) S[i][j] );
                        bufferedI.setRGB(i, j, b.getRGB()); // j, i inversé
                    }
                }
            default:
                throw new IllegalArgumentException("bad value for colorMap");
        }
        NavigableImagePanel.afficher(bufferedI);
    }

    /**
     * Display image of an 2d array
     *
     * Different from the "pli" function, uses "naviguablePanel" for a better displaying.
     *
     * @param A the a
     * @param W
     * @param H
     * @param colorMap the color map
     */
    public static void pli2(double A[], int W, int H, int colorMap)
    {
        double S[];
        //int L = A.length;
        S = mitiv.utils.MathUtils.scaleArrayTo8bit(A);
        BufferedImage bufferedI = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);

        switch (colorMap)
        {
            case mitiv.utils.MathUtils.COLORMAP_JET:
                ColorMap map = ColorMap.getJet(256);
                for(int j = 0; j < H; j++)
                {
                    for(int i = 0; i < W; i++)
                    {
                        Color b = map.table[ (int) S[i + j*W] ];
                        bufferedI.setRGB(j, i, b.getRGB()); // j, i inversé
                    }
                }
                break;
            case mitiv.utils.MathUtils.COLORMAP_GRAYSCALE:
                Color b;
                for(int j = 0; j < H; j++)
                {
                    for(int i = 0; i < W; i++)
                    {
                        b = new Color( (int) S[i + j*W], (int) S[i + j*W], (int) S[i + j*W] );
                        bufferedI.setRGB(j, i, b.getRGB()); // j, i inversé
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("bad value for colorMap");
        }
        NavigableImagePanel.afficher(bufferedI);
    }


    /**
     * Plot 2-D FFT array A as an image, taking care of "rolling" A and setting
     * correct world boundaries.  Keyword SCALE can be used to indicate the
     * "frequel" scale along both axis (SCALE is a scalar) or along each axis
     * (SCALE is a 2-element vector: SCALE=[XSCALE,YSCALE]); by default,
     * SCALE=[1.0, 1.0].
     *
     * @param A the a
     */
    public static void fftPli2(double A[][])
    {
        mitiv.utils.MathUtils.uint8(A);
        double[][] A_padded = mitiv.utils.MathUtils.fftShift(A);
        pli2(A_padded, mitiv.utils.MathUtils.COLORMAP_JET);
    }

    /**
     * Plot 2-D FFT array A as an image, taking care of "rolling" A and setting
     * correct world boundaries.  Keyword SCALE can be used to indicate the
     * "frequel" scale along both axis (SCALE is a scalar) or along each axis
     * (SCALE is a 2-element vector: SCALE=[XSCALE,YSCALE]); by default,
     * SCALE=[1.0, 1.0].
     *
     * @param A the a
     */
    public static void fftPli(double A[][])
    {
        mitiv.utils.MathUtils.uint8(A);
        double[][] A_padded = mitiv.utils.MathUtils.fftShift(A);
        pli(A_padded, mitiv.utils.MathUtils.COLORMAP_JET);
    }


}
