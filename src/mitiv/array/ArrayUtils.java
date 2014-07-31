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

package mitiv.array;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.FloatShapedVectorSpace;

public class ArrayUtils {

    /*=======================================================================*/
    /* COLORS */

    public static double colorToGrey(double red, double green, double blue) {
        return 0.2126*red + 0.7152*green + 0.0722*blue;
    }
    public static float colorToGrey(float red, float green, float blue) {
        return 0.2126F*red + 0.7152F*green + 0.0722F*blue;
    }
    public static int colorToGrey(int red, int green, int blue) {
        return Math.round(0.2126F*red + 0.7152F*green + 0.0722F*blue);
    }

    /*=======================================================================*/
    /* BRIGHTNESS SCALING FACTORS */

    public static double[] computeScalingFactors(double dataMin, double dataMax,
            double fileMin, double fileMax) {
        double scale = Math.abs(dataMin);
        scale = Math.max(scale, Math.abs(dataMax));
        scale = Math.max(scale, Math.abs(fileMin));
        scale = Math.max(scale, Math.abs(fileMax));
        if (scale > 0.0) {
            dataMin /= scale;
            dataMax /= scale;
            fileMin /= scale;
            fileMax /= scale;
        } else {
            scale = 1.0;
        }
        return computeScalingFactors(dataMin, dataMax, fileMin, fileMax, scale);
    }
    public static double[] computeScalingFactors(double dataMin, double dataMax,
            double fileMin, double fileMax, double scale) {
        double[] result = new double[2];
        computeScalingFactors(dataMin, dataMax, fileMin, fileMax, scale, result);
        return result;
    }

    /**
     * Compute scaling factors BSCALE and BZERO.
     * <p>
     * Scaling factors {@code BSCALE} and {@code BZERO} are used to convert
     * data values into scaled values suitable to be stored in integers.
     * To convert integer value {@code fileValue} into a data value {@code dataValue},
     * the formula is:
     * <pre>
     *     dataValue = BSCALE*fileValue + BZERO
     * </pre> the reciprocal formula is:
     * <pre>
     *     fileValue = round((dataValue - BZERO)/BSCALE)
     * </pre>
     * @param dataMin - The (scaled) minimum data value.
     * @param dataMax - The (scaled) maximum data value.
     * @param fileMin - The (scaled) minimum file value.
     * @param fileMax - The (scaled) maximum file value.
     * @param scale   - The scale factor (all input parameters are assumed
     *                  to have been divided by this value).
     * @param result  - An array of 2 doubles to store BSCALE and
     *                  BZERO (in that order).
     */
    public static void computeScalingFactors(double dataMin, double dataMax,
            double fileMin, double fileMax, double scale, double[] result) {
        /*
         * We compute the scaling parameters BSCALE and BZERO to
         * map the data values to the file values according to the
         * chosen BITPIX.
         *
         * Notation:
         *     y = data (physical) value
         *     x = file value (below integer file value is assumed)
         * When reading:
         *     y = BSCALE*x + BZERO
         * When writing:
         *     x = round((y - BZERO)/BSCALE)
         * where the round() function returns an interger such that:
         *     u - 1/2 <= round(u) < u + 1/2
         *     <==> round(u) - 1/2 < u <= round(u) + 1/2
         *
         * a/ We want the smallest BSCALE (in magnitude) such that
         *    XMIN <= x <= XMAX whatever y in [YMIN,YMAX].
         *    Assuming BSCALE > 0, this yields:
         *        XMIN <= round(UMIN)
         *        XMAX >= round(UMAX)
         *    with:
         *        UMIN = (YMIN - BZERO)/BSCALE
         *        UMAX = (YMAX - BZERO)/BSCALE
         *    From the bounds of the round() function, we have:
         *        UMAX - UMIN - 1 < DRU < UMAX - UMIN + 1
         *          DY/BSCALE - 1 < DRU < DY/BSCALE + 1
         *    with DRU = round(UMAX) - round(UMIN) and DY = YMAX - YMIN.
         *
         * b/ In order to maximize the precision, we want to
         *    minimize BSCALE (in magnitude).  This amounts to
         *    maximize DRU = round(UMAX) - round(UMIN).  Since
         *    DRU = round(UMAX) - round(UMIN) <= DX = XMAX - XMIN,
         *    we want to have (if possible) DRU = DX.
         *
         *  Combining a/ and b/ yields:
         *           DY/BSCALE - 1 < DX < DY/BSCALE + 1
         *      <==> DX - 1 < DY/BSCALE < DX + 1
         *  Hence (assuming DX > 0 and BSCALE > 0):
         *      DY/(DX + 1) < BSCALE < DY/(DX - 1)
         *  Since we want the smallest BSCALE, we should choose:
         *      BSCALE = (1 - EPSILON)*DY/(DX + 1)
         *  with EPSILON the relative machine precision, but:
         *      BSCALE = DY/DX
         *  may be good enough (see below).
         *
         * c/ To determine BZERO, we can center the errors on the
         *    spanned intervals.  We can also compute BZERO such
         *    that a physical value of zero is always preserved
         *    across conversions.  This latter choice will limit
         *    the risk of drifting.  So we take BZERO = k*BSCALE
         *    and search k integer such that:
         *        XMIN <= round(UMIN) = round((YMIN - BZERO)/BSCALE)
         *                            = round(YMIN/BSCALE) - k
         *        XMAX >= round(UMAX) = round((YMAX - BZERO)/BSCALE)
         *                            = round(YMAX/BSCALE) - k
         *    Thus:
         *        round(YMAX/BSCALE) - XMAX <= k
         *        round(YMIN/BSCALE) - XMIN >= k
         *    Using the bounds of the round() function yields:
         *        YMAX/BSCALE - XMAX - 1/2 <= k
         *        YMIN/BSCALE - XMIN + 1/2 > k
         *    there is a unique integer solution if the two bounds
         *    differ by 1, thus we want to have:
         *        YMAX/BSCALE - XMAX = YMIN/BSCALE - XMIN
         *        ==> BSCALE = (YMAX - YMIN)/(XMAX - XMIN) = DY/DX
         *
         * Finally:
         *     BSCALE = (YMAX - YMIN)/(XMAX - XMIN) = DY/DX
         *     BZERO = k*BSCALE
         *     k = round(YMAX/BSCALE) - XMAX
         *       = round(YMIN/BSCALE) - XMIN
         *       = round((XMAX*YMIN - XMIN*YMAX)/(YMAX - YMIN))
         *
         * The code below applies these formulae except for the
         * normalization factor introduced to avoid overflows.
         */

        // FIXME: divide by zero
        double bscale = (dataMax - dataMin)/(fileMax - fileMin);
        double bzero = Math.rint((fileMax*dataMin - fileMin*dataMax)/(dataMax - dataMin)*scale)*bscale;
        result[0] = bscale;
        result[1] = bzero;
    }


    public final static int RED   = 0;
    public final static int GREEN = 1;
    public final static int BLUE  = 2;
    public final static int ALPHA = 3;
    public final static int GREY  = 4;
    public final static int RGB   = 5;
    public final static int RGBA  = 6;

    /*=======================================================================*/
    /* FUNCTIONS FOR DOUBLE TYPE */

    public static Double3D imageAsDouble3D(BufferedImage image, int colorModel) {
        int depth;
        switch (colorModel) {
        case RGB:
            depth = 3;
            break;
        case RGBA:
            depth = 4;
            break;
        default:
            throw new IllegalArgumentException("Bad color model for representing an image as a 3D array");
        }
        return Double3D.wrap(imageAsDouble(image, colorModel), depth, image.getWidth(), image.getHeight());
    }

    public static Double2D imageAsDouble2D(BufferedImage image, int colorModel) {
        switch (colorModel) {
        case RED:
        case GREEN:
        case BLUE:
        case ALPHA:
        case GREY:
            break;
        default:
            throw new IllegalArgumentException("Bad color model for representing an image as a 2D array");
        }
        return Double2D.wrap(imageAsDouble(image, colorModel), image.getWidth(), image.getHeight());
    }

    public static Double2D imageAsDouble2D(BufferedImage image) {
        return Double2D.wrap(imageAsDouble(image, GREY), image.getWidth(), image.getHeight());
    }

    public static double[] imageAsDouble(BufferedImage image, int colorModel) {
        final double OPAQUE = 255.0;
        int height = image.getHeight();
        int width = image.getWidth();
        int depth;
        switch (colorModel) {
        case RED:
        case GREEN:
        case BLUE:
        case ALPHA:
        case GREY:
            depth = 1;
            break;
        case RGB:
            depth = 3;
            break;
        case RGBA:
            depth = 4;
            break;
        default:
            throw new IllegalArgumentException("Unknown color model");
        }
        WritableRaster raster = image.getRaster();
        final int nbands = raster.getNumBands();
        double[] out = new double[depth*width*height];
        int[] pixval = new int[nbands];
        if (nbands == 1) {
            /* Assume input is greyscale image. */
            if (colorModel == GREY || colorModel == RED || colorModel == GREEN || colorModel == BLUE) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = pixval[0];
                    }
                }
            } else if (colorModel == ALPHA) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        out[x + y*width] = OPAQUE;
                    }
                }
            } else if (colorModel == RGB) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 3*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        double level = pixval[0];
                        out[k]   = level;
                        out[k+1] = level;
                        out[k+2] = level;
                    }
                }
            } else {
                /* Output must be RGBA. */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 4*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        double level = pixval[0];
                        out[k]   = level;
                        out[k+1] = level;
                        out[k+2] = level;
                        out[k+3] = OPAQUE;
                    }
                }
            }
        } else if (nbands == 3) {
            /* Assume input is RGB image. */
            if (colorModel == GREY) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = colorToGrey((double)pixval[0], (double)pixval[1], (double)pixval[2]);
                    }
                }
            } else if (colorModel == RGB) {
                /* Flatten the RGB image. */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 3*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        out[k]   = pixval[0];
                        out[k+1] = pixval[1];
                        out[k+2] = pixval[2];
                    }
                }
            } else if (colorModel == RGBA) {
                /* Flatten the RGBA image. */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 4*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        out[k]   = pixval[0];
                        out[k+1] = pixval[1];
                        out[k+2] = pixval[2];
                        out[k+3] = OPAQUE;
                    }
                }
            } else if (colorModel == ALPHA) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        out[x + y*width] = OPAQUE;
                    }
                }
            } else {
                int band;
                if (colorModel == RED) {
                    band = 0;
                } else if (colorModel == GREEN) {
                    band = 1;
                } else /* Output must be BLUE channel. */ {
                    band = 2;
                }
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = pixval[band];
                    }
                }
            }
        } else if (nbands == 4) {
            /* Assume input is RGBA image. */
            if (colorModel == GREY) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = colorToGrey((double)pixval[0], (double)pixval[1], (double)pixval[2]);
                    }
                }
            } else if (colorModel == RGB) {
                /* Flatten the RGB image (ignoring the ALPHA channel). */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 3*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        out[k]   = pixval[0];
                        out[k+1] = pixval[1];
                        out[k+2] = pixval[2];
                    }
                }
            } else if (colorModel == RGBA) {
                /* Flatten the RGBA image. */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 4*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        out[k]   = pixval[0];
                        out[k+1] = pixval[1];
                        out[k+2] = pixval[2];
                        out[k+3] = pixval[3];
                    }
                }
            } else {
                int band;
                if (colorModel == RED) {
                    band = 0;
                } else if (colorModel == GREEN) {
                    band = 1;
                } else if (colorModel == BLUE) {
                    band = 2;
                } else /* Output must be ALPHA channel. */ {
                    band = 3;
                }
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = pixval[band];
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Unknown pixel format");
        }
        return out;
    }

    public static void writeImage(Double2D img, String fileName) {
        writeImage(img.flatten(), img.getDimension(0), img.getDimension(1), fileName);
    }

    public static void writeImage(Double2D img, String fileName, double scale, double bias) {
        writeImage(img.flatten(), img.getDimension(0), img.getDimension(1), fileName, scale, bias);
    }

    public static void writeImage(DoubleShapedVector img, String fileName) {
        int[] shape = ((DoubleShapedVectorSpace)(img.getSpace())).cloneShape();
        int width = shape[0];
        int height = shape[1];
        writeImage(img.getData(), width, height, fileName);
    }

    public static void writeImage(DoubleShapedVector img, String fileName, double scale, double bias) {
        int[] shape = ((DoubleShapedVectorSpace)(img.getSpace())).cloneShape();
        int width = shape[0];
        int height = shape[1];
        writeImage(img.getData(), width, height, fileName, scale, bias);
    }

    // FIXME: deal with NaN and INFINITE
    public static void writeImage(double[] arr, int width, int height, String fileName) {
        double dataMin, dataMax;
        int number = width*height;
        dataMin = dataMax = arr[0];
        for(int i = 1; i < number; ++i){
            double value = arr[i];
            if (value > dataMax) dataMax = value;
            if (value < dataMin) dataMin = value;
        }
        double[] sf = computeScalingFactors(dataMin, dataMax, 0, 255);
        writeImage(arr, width, height, fileName, sf[0], sf[1]);
    }

    public static void writeImage(double[] arr, int width, int height, String fileName, double scale, double bias) {
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = buf.getRaster();
        int[] rgb = new int[3];
        double factor = 1.0/scale;
        for (int y = 0; y < height; ++y){
            for (int x = 0; x < width; ++x){
                int grey = Math.min(255, Math.max(0, (int)Math.round((arr[x + y*width] - bias)*factor)));
                rgb[0] = grey;
                rgb[1] = grey;
                rgb[2] = grey;
                raster.setPixel(x, y, rgb);
            }
        }
        try {
            ImageIO.write(buf, "png", new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*=======================================================================*/
    /* FUNCTIONS FOR FLOAT TYPE */

    public static Float3D imageAsFloat3D(BufferedImage image, int colorModel) {
        int depth;
        switch (colorModel) {
        case RGB:
            depth = 3;
            break;
        case RGBA:
            depth = 4;
            break;
        default:
            throw new IllegalArgumentException("Bad color model for representing an image as a 3D array");
        }
        return Float3D.wrap(imageAsFloat(image, colorModel), depth, image.getWidth(), image.getHeight());
    }

    public static Float2D imageAsFloat2D(BufferedImage image, int colorModel) {
        switch (colorModel) {
        case RED:
        case GREEN:
        case BLUE:
        case ALPHA:
        case GREY:
            break;
        default:
            throw new IllegalArgumentException("Bad color model for representing an image as a 2D array");
        }
        return Float2D.wrap(imageAsFloat(image, colorModel), image.getWidth(), image.getHeight());
    }

    public static Float2D imageAsFloat2D(BufferedImage image) {
        return Float2D.wrap(imageAsFloat(image, GREY), image.getWidth(), image.getHeight());
    }

    public static float[] imageAsFloat(BufferedImage image, int colorModel) {
        final float OPAQUE = 255.0F;
        int height = image.getHeight();
        int width = image.getWidth();
        int depth;
        switch (colorModel) {
        case RED:
        case GREEN:
        case BLUE:
        case ALPHA:
        case GREY:
            depth = 1;
            break;
        case RGB:
            depth = 3;
            break;
        case RGBA:
            depth = 4;
            break;
        default:
            throw new IllegalArgumentException("Unknown color model");
        }
        WritableRaster raster = image.getRaster();
        final int nbands = raster.getNumBands();
        float[] out = new float[depth*width*height];
        int[] pixval = new int[nbands];
        if (nbands == 1) {
            /* Assume input is greyscale image. */
            if (colorModel == GREY || colorModel == RED || colorModel == GREEN || colorModel == BLUE) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = pixval[0];
                    }
                }
            } else if (colorModel == ALPHA) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        out[x + y*width] = OPAQUE;
                    }
                }
            } else if (colorModel == RGB) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 3*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        float level = pixval[0];
                        out[k]   = level;
                        out[k+1] = level;
                        out[k+2] = level;
                    }
                }
            } else {
                /* Output must be RGBA. */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 4*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        float level = pixval[0];
                        out[k]   = level;
                        out[k+1] = level;
                        out[k+2] = level;
                        out[k+3] = OPAQUE;
                    }
                }
            }
        } else if (nbands == 3) {
            /* Assume input is RGB image. */
            if (colorModel == GREY) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = colorToGrey((float)pixval[0], (float)pixval[1], (float)pixval[2]);
                    }
                }
            } else if (colorModel == RGB) {
                /* Flatten the RGB image. */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 3*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        out[k]   = pixval[0];
                        out[k+1] = pixval[1];
                        out[k+2] = pixval[2];
                    }
                }
            } else if (colorModel == RGBA) {
                /* Flatten the RGBA image. */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 4*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        out[k]   = pixval[0];
                        out[k+1] = pixval[1];
                        out[k+2] = pixval[2];
                        out[k+3] = OPAQUE;
                    }
                }
            } else if (colorModel == ALPHA) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        out[x + y*width] = OPAQUE;
                    }
                }
            } else {
                int band;
                if (colorModel == RED) {
                    band = 0;
                } else if (colorModel == GREEN) {
                    band = 1;
                } else /* Output must be BLUE channel. */ {
                    band = 2;
                }
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = pixval[band];
                    }
                }
            }
        } else if (nbands == 4) {
            /* Assume input is RGBA image. */
            if (colorModel == GREY) {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = colorToGrey((float)pixval[0], (float)pixval[1], (float)pixval[2]);
                    }
                }
            } else if (colorModel == RGB) {
                /* Flatten the RGB image (ignoring the ALPHA channel). */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 3*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        out[k]   = pixval[0];
                        out[k+1] = pixval[1];
                        out[k+2] = pixval[2];
                    }
                }
            } else if (colorModel == RGBA) {
                /* Flatten the RGBA image. */
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int k = 4*(x + y*width);
                        raster.getPixel(x, y, pixval);
                        out[k]   = pixval[0];
                        out[k+1] = pixval[1];
                        out[k+2] = pixval[2];
                        out[k+3] = pixval[3];
                    }
                }
            } else {
                int band;
                if (colorModel == RED) {
                    band = 0;
                } else if (colorModel == GREEN) {
                    band = 1;
                } else if (colorModel == BLUE) {
                    band = 2;
                } else /* Output must be ALPHA channel. */ {
                    band = 3;
                }
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        raster.getPixel(x, y, pixval);
                        out[x + y*width] = pixval[band];
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Unknown pixel format");
        }
        return out;
    }

    public static void writeImage(Float2D img, String fileName) {
        writeImage(img.flatten(), img.getDimension(0), img.getDimension(1), fileName);
    }

    public static void writeImage(Float2D img, String fileName, float scale, float bias) {
        writeImage(img.flatten(), img.getDimension(0), img.getDimension(1), fileName, scale, bias);
    }

    public static void writeImage(FloatShapedVector img, String fileName) {
        int[] shape = ((FloatShapedVectorSpace)(img.getSpace())).cloneShape();
        int width = shape[0];
        int height = shape[1];
        writeImage(img.getData(), width, height, fileName);
    }

    public static void writeImage(FloatShapedVector img, String fileName, float scale, float bias) {
        int[] shape = ((FloatShapedVectorSpace)(img.getSpace())).cloneShape();
        int width = shape[0];
        int height = shape[1];
        writeImage(img.getData(), width, height, fileName, scale, bias);
    }

    public static void writeImage(float[] arr, int width, int height, String fileName) {
        float dataMin, dataMax;
        int number = width*height;
        dataMin = dataMax = arr[0];
        for(int i = 1; i < number; ++i){
            float value = arr[i];
            if (value > dataMax) dataMax = value;
            if (value < dataMin) dataMin = value;
        }
        double[] sf = computeScalingFactors(dataMin, dataMax, 0, 255);
        writeImage(arr, width, height, fileName, (float)sf[0], (float)sf[1]);

    }

    public static void writeImage(float[] arr, int width, int height, String fileName, float scale, float bias) {
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = buf.getRaster();
        int[] rgb = new int[3];
        float factor = 1.0F/scale;
        for(int y = 0; y < height; ++y){
            for(int x = 0; x<width; ++x){
                int grey = Math.min(255, Math.max(0, Math.round((arr[x + y*width] - bias)*factor)));
                rgb[0] = grey;
                rgb[1] = grey;
                rgb[2] = grey;
                raster.setPixel(x, y, rgb);
            }
        }
        try {
            ImageIO.write(buf, "png", new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
