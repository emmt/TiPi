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

package mitiv.io;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import mitiv.array.ArrayFactory;
import mitiv.array.ArrayUtils;
import mitiv.array.Byte2D;
import mitiv.array.Byte3D;
import mitiv.array.Double2D;
import mitiv.array.Double3D;
import mitiv.array.Float2D;
import mitiv.array.Float3D;
import mitiv.array.Int2D;
import mitiv.array.Int3D;
import mitiv.array.Long2D;
import mitiv.array.Long3D;
import mitiv.array.ShapedArray;
import mitiv.array.Short2D;
import mitiv.array.Short3D;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.exception.IllegalTypeException;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.FloatShapedVector;
import mitiv.linalg.shaped.ShapedVector;

/**
 * This enumeration deals with identifying, reading and writing
 * various data format.
 *
 * @author Éric Thiébaut.
 */
public enum DataFormat {

    PNM("PNM", "Portable anymap (PBM/PGM/PPM/PNM) image.", new String[]{"pnm", "ppm", "pgm", "pbm"}),
    JPEG("JPEG", "JPEG image.", new String[]{"jpg", "jpeg"}),
    PNG("PNG", "Portable Network Graphic (PNG) image.", new String[]{"png"}),
    GIF("GIF", "GIF image.", new String[]{"gif"}),
    BMP("BMP", "BMP image.", new String[]{"bmp"}),
    WBMP("WBMP", "Wireless Bitmap (WBMP) image format.", new String[]{"wbmp"}),
    TIFF("TIFF", "TIFF image format.", new String[]{"tiff", "tif"}),
    FITS("FITS", "Flexible Image Transport System (FITS) format.", new String[]{"fits", "fts", "fit"}),
    MDA("MDA", "Multi-dimensional array (MDA) format.", new String[]{"mda"});

    private final String identifier;

    private final String description;

    private final String[] extensions;

    private DataFormat(String ident, String descr, String[] extensions) {
        this.identifier = ident;
        this.description = descr;
        this.extensions = extensions;
    }

    /**
     * Get the name of the data file format.
     *
     * For image format supported by Java, the format name is suitable for
     * {@link ImageIO#write}.
     * @return The name of the format.
     */
    public String identifier() {
        return identifier;
    }

    /** Get the description of the file format. */
    public String description() {
        return description;
    }

    /** Get the recognized file name extensions for the format. */
    public String[] extensions() {
        return extensions;
    }

    /** Get a string representation of the color model. */
    @Override
    public String toString() {
        return description;
    }

    /**
     * Check whether a suffix matches recognized file name extensions.
     *
     * @param suffix - The suffix to check.
     * @return A boolean value.
     */
    public boolean match(String suffix) {
        if (suffix != null && suffix.length() > 0) {
            int n = (extensions == null ? 0 : extensions.length);
            for (int k = 0; k < n; ++k) {
                if (suffix.equalsIgnoreCase(extensions[k])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Guess data format from the extension of the file name.
     * <p>
     * This function examines the extension of the file name
     * to determine the data format.
     * </p>
     * @param fileName - The name of the file.
     * @return A file format identifier: {@code null} if the
     *         format is not recognized; otherwise {@link #PNM},
     *         {@link #JPEG}, {@link #PNG}, {@link #TIFF},
     *         {@link #FITS}, {@link #GIF}, or {@link #MDA}.
     */
    public static final DataFormat guessFormat(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0) {
            return null;
        }
        String suffix = fileName.substring(index + 1);
        if (MDA.match(suffix)) {
            return MDA;
        }
        if (PNG.match(suffix)) {
            return PNG;
        }
        if (JPEG.match(suffix)) {
            return JPEG;
        }
        if (PNM.match(suffix)) {
            return PNM;
        }
        if (TIFF.match(suffix)) {
            return TIFF;
        }
        if (FITS.match(suffix)) {
            return FITS;
        }
        if (BMP.match(suffix)) {
            return BMP;
        }
        if (WBMP.match(suffix)) {
            return WBMP;
        }
        if (FITS.match(suffix)) {
            return FITS;
        }
        if (GIF.match(suffix)) {
            return GIF;
        }
        return null;
    }


    /**
     * Guess data format from the given options or from the extension of
     * the file name.
     * <p>
     * It a preferred data format is specified in the options, this format
     * is returned; otherwise, the extension of the file name is examined
     * to determine the data format.
     * </p>
     * @param fileName - The name of the file.
     * @return A file format identifier: {@code null} if the
     *         format is not recognized; otherwise {@link #PNM},
     *         {@link #JPEG}, {@link #PNG}, {@link #TIFF},
     *         {@link #FITS}, {@link #GIF}, or {@link #MDA}.
     */
    public static final DataFormat guessFormat(String fileName,
            FormatOptions opts) {
        DataFormat format = (opts == null ? null : opts.getDataFormat());
        if (format != null) {
            return format;
        } else {
            return guessFormat(fileName);
        }
    }

    /**
     * Guess data format from a few magic bytes.
     * <p>
     * This function examines the few next bytes available from the data
     * stream to determine the data format.  In any case, the stream position
     * is left where it was prior to calling the function (i.e. there is no data
     * consumption).
     *
     * @param stream - The input data stream.
     *
     * @return A file format identifier (see {@link #guessFormat(String)}),
     *         or {@code null} if the format is not recognized,.
     * @throws IOException
     */
    public static final DataFormat guessFormat(BufferedInputDataStream stream)
            throws IOException {
        int preserved = stream.insure(80);
        if (preserved < 2) {
            return null;
        }
        DataFormat format = null;
        stream.mark();
        try {
            int length = Math.min(preserved, 80);
            byte[] magic = new byte[length];
            length = stream.read(magic, 0, length);
            if (length < 2) {
                return null;
            }
            if (length >= 2 && matchMagic(magic, '\377', '\330')) {
                format = JPEG;
            } else if (length >= 4 && matchMagic(magic, '\211', 'P', 'N', 'G')) {
                format = PNG;
            } else if (length >= 4 && matchMagic(magic, 'M', 'M', '\000', '\052')) {
                format = TIFF;
            } else if (length >= 4 && matchMagic(magic, 'I', 'I', '\052', '\000')) {
                format = TIFF;
            } else if (length >= 4 && matchMagic(magic, 'G', 'I', 'F', '8')) {
                format = GIF;
            } else if (length >= 3 && magic[0] == (byte)'P' && isSpace(magic[2])
                    && (byte)'1' <= magic[1] && magic[1] <= (byte)'6') {
                /* "P1" space = ascii PBM (portable bitmap)
                 * "P2" space = ascii PGM (portable graymap)
                 * "P3" space = ascii PPM (portable pixmap)
                 * "P4" space = raw PBM
                 * "P5" space = raw PGM
                 * "P6" space = raw PPM
                 */
                format = PNM;
            } else if (length >= 3
                    && matchMagic(magic, 'S', 'I', 'M', 'P', 'L', 'E', ' ', ' ', '=')) {
                format = FITS;
            }
        } catch (IOException ex) {
            throw ex;
        } finally{
            stream.reset();
        }
        return format;
    }

    private static final boolean matchMagic(byte[] b,
            char c0, char c1) {
        return ((b[0] == (byte)c0) && (b[1] == (byte)c1));
    }

    private static final boolean matchMagic(byte[] b,
            char c0, char c1, char c2, char c3) {
        return ((b[0] == (byte)c0) && (b[1] == (byte)c1) &&
                (b[2] == (byte)c2) && (b[3] == (byte)c3));
    }

    private static final boolean matchMagic(byte[] b,
            char c0, char c1, char c2, char c3,
            char c4, char c5, char c6, char c7, char c8) {
        return ((b[0] == (byte)c0) && (b[1] == (byte)c1) &&
                (b[2] == (byte)c2) && (b[3] == (byte)c3) &&
                (b[4] == (byte)c4) && (b[5] == (byte)c5) &&
                (b[6] == (byte)c6) && (b[7] == (byte)c7) &&
                (b[8] == (byte)c8));
    }

    private static final boolean isSpace(byte s) {
        return (s == (byte)' ' || s == (byte)'\n' || s == (byte)'\r' || s == (byte)'\t');
    }

    private static void fatal(String reason) {
        throw new IllegalArgumentException(reason);
    }

    /**
     * Load formatted data from afile.
     * @param fileName - The name of the input file.
     * @param colorModel - The model for dealing with color images.
     * @param description - A description for error messages.
     * @return
     */
    public static ShapedArray load(String fileName, FormatOptions opts) {
        ShapedArray arr = null;
        DataFormat format = guessFormat(fileName);
        try {
            if (format == MDA) {
                arr = MdaFormat.load(fileName);
            } else {
                BufferedImage img = ImageIO.read(new File(fileName));
                arr = ArrayUtils.imageAsDouble(img, opts.getColorModel());
            }
        } catch (Exception e) {
            fatal("Error while reading " + fileName + "(" + e.getMessage() +").");
        }
        return arr;
    }

    /**
     * Save data to file.
     * <p>
     * The file format is guessed from the extension of the file name and default
     * options are used to encode the file.
     * </p>
     * @param arr - The data to save.
     * @param fileName - The destination file.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void save(ShapedArray arr, String fileName)
            throws FileNotFoundException, IOException {
        save(arr, fileName, new FormatOptions());
    }

    /**
     * Save data to file with given options.
     * <p>
     * The file format is guessed from the extension of the file name and default
     * options are used to encode the file.
     * </p>
     * @param arr  - The data to save.
     * @param name - The destination file.
     * @param opts - Options for encoding the data.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void save(ShapedArray arr, String name,
            FormatOptions opts) throws FileNotFoundException, IOException {
        DataFormat format = DataFormat.guessFormat(name, opts);
        String identifier = null;
        switch (format) {
        //case PNM:
        //case TIFF:
        //case FITS:
        case JPEG:
        case PNG:
        case GIF:
        case BMP:
        case WBMP:
            identifier = format.identifier();
            break;
        case MDA:
            MdaFormat.save(arr, name);
            return;
        default:
            identifier = null;
        }
        if (identifier == null) {
            fatal("Unknown/unsupported format name.");
        }
        BufferedImage image = makeBufferedImage(arr, opts);
        ImageIO.write(image, identifier, new File(name));
    }



    /*=======================================================================*/
    /* MAKE BUFFERED IMAGES */

    // FIXME: make this for any array/vector types

    /**
     * Make a buffered image from a shaped array with default options.
     * <p>
     *
     * </p>
     * @param arr  - The array to convert into an image.
     * @see {@link #makeBufferedImage(ShapedArray, FormatOptions)} for more
     *      details about how is interpreted the array.
     */
    public static BufferedImage makeBufferedImage(ShapedArray arr) {
        return makeBufferedImage(arr, new FormatOptions());
    }

    // FIXME: deal with NaN and INFINITE

    private static final int clip(float value) {
        return Math.min(255, Math.max(0, (int)Math.round(value)));
    }

    private static final int clip(double value) {
        return Math.min(255, Math.max(0, (int)Math.round(value)));
    }

    /**
     * Make a buffered image from a shaped array with given options.
     * <p>
     * Different kind of shaped array can be interpreted as an image: 2D
     * shaped array are interpreted as gresy scale images, 3D shaped arrays
     * are interpreted as RGB array if their first dimension is 3 and as RGBA
     * array if their first dimension is 4.  All other shapes result in
     * throxwin an {@link IllegalArgumentException}.  The penultimate
     * dimension of the shaped array is the width of the image and the last
     * dimension of the shaped array is the height of the image.  Currently
     * alpha channel (RGBA images) are only implemented for byte shaped
     * arrays.
     * </p><p>
     * If a color model of the format options is specified, then the source
     * image (that is the input shaped array interpreted as explained above)
     * is automatically converted according to this setting.  Otherwise, the
     * returned buffer image will have the same color model as the input one.
     * </p>
     * @param arr  - The array to convert into an image.
     * @param opts - Options for conversion.
     */
    public static BufferedImage makeBufferedImage(ShapedArray arr, FormatOptions opts) {
        Shape shape = arr.getShape();
        int rank = shape.rank();
        int type = arr.getType();
        int depth = -1;
        int imageType = -1;
        if (rank == 2) {
            depth = 1;
            imageType = BufferedImage.TYPE_BYTE_GRAY;
        } else if (rank == 3) {
            depth = shape.dimension(0);
            if (depth == 3 || (depth == 4 && type == Traits.BYTE)) {
                imageType = BufferedImage.TYPE_3BYTE_BGR;
            } else {
                depth = -1;
            }
        }
        if (depth < 0) {
            throw new IllegalArgumentException("Conversion to image is only allowed for WIDTH x HEIGHT arrays, 3 x WIDTH x HEIGHT arrays or 4 x WIDTH x HEIGHT byte arrays.");
        }
        int width = shape.dimension(rank - 2);
        int height = shape.dimension(rank - 1);
        BufferedImage image = new BufferedImage(width, height, imageType);
        WritableRaster raster = image.getRaster();
        int[] argb = new int[4];
        int[] rgb = new int[3];
        if (type == Traits.BYTE) {
            if (depth == 1) {
                Byte2D src = (Byte2D)arr;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int level = ((int)(src.get(x,y)) & 0xFF);
                        rgb[0] = level;
                        rgb[1] = level;
                        rgb[2] = level;
                        raster.setPixel(x, y, rgb);
                    }
                }
            } else {
                Byte3D src = (Byte3D)arr;
                if (depth == 3) {
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            rgb[0] = ((int)(src.get(0,x,y)) & 0xFF);
                            rgb[1] = ((int)(src.get(1,x,y)) & 0xFF);
                            rgb[2] = ((int)(src.get(2,x,y)) & 0xFF);
                            raster.setPixel(x, y, rgb);
                        }
                    }
                } else /* depth == 4 */ {
                    for (int y = 0; y < height; ++y){
                        for (int x = 0; x < width; ++x){
                            argb[0] = ((int)(src.get(0,x,y)) & 0xFF);
                            argb[1] = ((int)(src.get(1,x,y)) & 0xFF);
                            argb[2] = ((int)(src.get(2,x,y)) & 0xFF);
                            argb[3] = ((int)(src.get(3,x,y)) & 0xFF);
                            raster.setPixel(x, y, argb);
                        }
                    }
                }
            }
        } else if (type == Traits.SHORT) {
            double[] sf = opts.getScaling(arr, 0, 255);
            float scale = (float)sf[0];
            float bias = (float)sf[1];
            float factor = 1/scale;
            if (depth == 1) {
                Short2D src = (Short2D)arr;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int level = clip(((float)(src.get(x,y)) - bias)*factor);
                        rgb[0] = level;
                        rgb[1] = level;
                        rgb[2] = level;
                        raster.setPixel(x, y, rgb);
                    }
                }
            } else {
                Short3D src = (Short3D)arr;
                if (depth == 3) {
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            rgb[0] = clip(((float)(src.get(0,x,y)) - bias)*factor);
                            rgb[1] = clip(((float)(src.get(1,x,y)) - bias)*factor);
                            rgb[2] = clip(((float)(src.get(2,x,y)) - bias)*factor);
                            raster.setPixel(x, y, rgb);
                        }
                    }
                } else /* depth == 4 */ {
                    for (int y = 0; y < height; ++y){
                        for (int x = 0; x < width; ++x){
                            argb[0] = clip(((float)(src.get(0,x,y)) - bias)*factor);
                            argb[1] = clip(((float)(src.get(1,x,y)) - bias)*factor);
                            argb[2] = clip(((float)(src.get(2,x,y)) - bias)*factor);
                            argb[3] = clip(((float)(src.get(3,x,y)) - bias)*factor);
                            raster.setPixel(x, y, argb);
                        }
                    }
                }
            }
        } else if (type == Traits.INT) {
            double[] sf = opts.getScaling(arr, 0, 255);
            float scale = (float)sf[0];
            float bias = (float)sf[1];
            float factor = 1/scale;
            if (depth == 1) {
                Int2D src = (Int2D)arr;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int level = clip(((float)(src.get(x,y)) - bias)*factor);
                        rgb[0] = level;
                        rgb[1] = level;
                        rgb[2] = level;
                        raster.setPixel(x, y, rgb);
                    }
                }
            } else {
                Int3D src = (Int3D)arr;
                if (depth == 3) {
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            rgb[0] = clip(((float)(src.get(0,x,y)) - bias)*factor);
                            rgb[1] = clip(((float)(src.get(1,x,y)) - bias)*factor);
                            rgb[2] = clip(((float)(src.get(2,x,y)) - bias)*factor);
                            raster.setPixel(x, y, rgb);
                        }
                    }
                } else /* depth == 4 */ {
                    for (int y = 0; y < height; ++y){
                        for (int x = 0; x < width; ++x){
                            argb[0] = clip(((float)(src.get(0,x,y)) - bias)*factor);
                            argb[1] = clip(((float)(src.get(1,x,y)) - bias)*factor);
                            argb[2] = clip(((float)(src.get(2,x,y)) - bias)*factor);
                            argb[3] = clip(((float)(src.get(3,x,y)) - bias)*factor);
                            raster.setPixel(x, y, argb);
                        }
                    }
                }
            }
        } else if (type == Traits.LONG) {
            double[] sf = opts.getScaling(arr, 0, 255);
            double scale = (double)sf[0];
            double bias = (double)sf[1];
            double factor = 1/scale;
            if (depth == 1) {
                Long2D src = (Long2D)arr;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int level = clip(((double)(src.get(x,y)) - bias)*factor);
                        rgb[0] = level;
                        rgb[1] = level;
                        rgb[2] = level;
                        raster.setPixel(x, y, rgb);
                    }
                }
            } else {
                Long3D src = (Long3D)arr;
                if (depth == 3) {
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            rgb[0] = clip(((double)(src.get(0,x,y)) - bias)*factor);
                            rgb[1] = clip(((double)(src.get(1,x,y)) - bias)*factor);
                            rgb[2] = clip(((double)(src.get(2,x,y)) - bias)*factor);
                            raster.setPixel(x, y, rgb);
                        }
                    }
                } else /* depth == 4 */ {
                    for (int y = 0; y < height; ++y){
                        for (int x = 0; x < width; ++x){
                            argb[0] = clip(((double)(src.get(0,x,y)) - bias)*factor);
                            argb[1] = clip(((double)(src.get(1,x,y)) - bias)*factor);
                            argb[2] = clip(((double)(src.get(2,x,y)) - bias)*factor);
                            argb[3] = clip(((double)(src.get(3,x,y)) - bias)*factor);
                            raster.setPixel(x, y, argb);
                        }
                    }
                }
            }
        } else if (type == Traits.FLOAT) {
            double[] sf = opts.getScaling(arr, 0, 255);
            float scale = (float)sf[0];
            float bias = (float)sf[1];
            float factor = 1/scale;
            if (depth == 1) {
                Float2D src = (Float2D)arr;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int level = clip((src.get(x,y) - bias)*factor);
                        rgb[0] = level;
                        rgb[1] = level;
                        rgb[2] = level;
                        raster.setPixel(x, y, rgb);
                    }
                }
            } else {
                Float3D src = (Float3D)arr;
                if (depth == 3) {
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            rgb[0] = clip((src.get(0,x,y) - bias)*factor);
                            rgb[1] = clip((src.get(1,x,y) - bias)*factor);
                            rgb[2] = clip((src.get(2,x,y) - bias)*factor);
                            raster.setPixel(x, y, rgb);
                        }
                    }
                } else /* depth == 4 */ {
                    for (int y = 0; y < height; ++y){
                        for (int x = 0; x < width; ++x){
                            argb[0] = clip((src.get(0,x,y) - bias)*factor);
                            argb[1] = clip((src.get(1,x,y) - bias)*factor);
                            argb[2] = clip((src.get(2,x,y) - bias)*factor);
                            argb[3] = clip((src.get(3,x,y) - bias)*factor);
                            raster.setPixel(x, y, argb);
                        }
                    }
                }
            }
        } else if (type == Traits.DOUBLE) {
            double[] sf = opts.getScaling(arr, 0, 255);
            double scale = (double)sf[0];
            double bias = (double)sf[1];
            double factor = 1/scale;
            if (depth == 1) {
                Double2D src = (Double2D)arr;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int level = clip((src.get(x,y) - bias)*factor);
                        rgb[0] = level;
                        rgb[1] = level;
                        rgb[2] = level;
                        raster.setPixel(x, y, rgb);
                    }
                }
            } else {
                Double3D src = (Double3D)arr;
                if (depth == 3) {
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            rgb[0] = clip((src.get(0,x,y) - bias)*factor);
                            rgb[1] = clip((src.get(1,x,y) - bias)*factor);
                            rgb[2] = clip((src.get(2,x,y) - bias)*factor);
                            raster.setPixel(x, y, rgb);
                        }
                    }
                } else /* depth == 4 */ {
                    for (int y = 0; y < height; ++y){
                        for (int x = 0; x < width; ++x){
                            argb[0] = clip((src.get(0,x,y) - bias)*factor);
                            argb[1] = clip((src.get(1,x,y) - bias)*factor);
                            argb[2] = clip((src.get(2,x,y) - bias)*factor);
                            argb[3] = clip((src.get(3,x,y) - bias)*factor);
                            raster.setPixel(x, y, argb);
                        }
                    }
                }
            }
        } else {
            throw new IllegalTypeException();
        }

        return image;
    }

    public static BufferedImage makeBufferedImage(ShapedVector vec) {
        if (vec instanceof DoubleShapedVector) {
            return makeBufferedImage(ArrayFactory.wrap(((DoubleShapedVector)vec).getData(), vec.getShape()));
        }
        if (vec instanceof FloatShapedVector) {
            return makeBufferedImage(ArrayFactory.wrap(((FloatShapedVector)vec).getData(), vec.getShape()));
        }
        throw new IllegalArgumentException("Unable to convert shaped vector to image.");
    }

    public static BufferedImage makeBufferedImage(ShapedVector vec,
                                                  FormatOptions opts) {
        if (vec instanceof DoubleShapedVector) {
            return makeBufferedImage(ArrayFactory.wrap(((DoubleShapedVector)vec).getData(), vec.getShape()), opts);
        }
        if (vec instanceof FloatShapedVector) {
            return makeBufferedImage(ArrayFactory.wrap(((FloatShapedVector)vec).getData(), vec.getShape()), opts);
        }
        throw new IllegalArgumentException("Unable to convert shaped vector to image.");
    }


    /*=======================================================================*/
    /* TESTS */

    public static void main(String[] args) {
        String[] str;
        str = ImageIO.getReaderFormatNames();
        System.out.format("Format names understood by registered readers:\n");
        for (int i = 0; i < str.length; ++i) {
            System.out.format("  - %s\n", str[i]);
        }

        str = ImageIO.getReaderFileSuffixes();
        System.out.format("\nImage suffixes understood by registered readers:\n");
        for (int i = 0; i < str.length; ++i) {
            System.out.format("  - %s\n", str[i]);
        }

        str = ImageIO.getWriterFormatNames();
        System.out.format("\nFormat names understood by registered writers:\n");
        for (int i = 0; i < str.length; ++i) {
            System.out.format("  - %s\n", str[i]);
        }

        str = ImageIO.getWriterFileSuffixes();
        System.out.format("\nImage suffixes understood by registered writers:\n");
        for (int i = 0; i < str.length; ++i) {
            System.out.format("  - %s\n", str[i]);
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
