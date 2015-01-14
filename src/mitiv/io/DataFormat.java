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
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;

import mitiv.array.ArrayFactory;
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
 * Deals with identifying, reading and writing various data format.
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
     * @param name - The name of the destination file.
     * @return A file format identifier: {@code null} if the
     *         format is not recognized; otherwise {@link #PNM},
     *         {@link #JPEG}, {@link #PNG}, {@link #TIFF},
     *         {@link #FITS}, {@link #GIF}, or {@link #MDA}.
     */
    public static final DataFormat guessFormat(String name) {
        int index = name.lastIndexOf('.');
        if (index < 0) {
            return null;
        }
        String suffix = name.substring(index + 1);
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
     * @param name - The name of the destination file.
     * @return A file format identifier: {@code null} if the
     *         format is not recognized; otherwise {@link #PNM},
     *         {@link #JPEG}, {@link #PNG}, {@link #TIFF},
     *         {@link #FITS}, {@link #GIF}, or {@link #MDA}.
     */
    public static final DataFormat guessFormat(String name,
            FormatOptions opts) {
        DataFormat format = (opts == null ? null : opts.getDataFormat());
        if (format != null) {
            return format;
        } else {
            return guessFormat(name);
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
     * Load formatted data from a file.
     * <p>
     * This method attempts to give the most apprpriate representation of the
     * data stored in the file as a shaped array.  For instance, it relies on
     * {@link #imageToShapedArray} to convert an image in a shaped array.  See
     * {@link ColorModel} for the assumed conventions about the representation
     * of an image as a shaped array.  You may use
     * {@link ColorModel#guessColorModel} to determine the color model of an
     * image loaded by this method and {@link ColorModel#filterImageAsFloat} or
     * {@link ColorModel#filterImageAsDouble} to convert such an image
     * according to your needs.
     * </p>
     * @param name - The name of the source file.
     * @return A shaped array.
     */
    public static ShapedArray load(String name) {
        ShapedArray arr = null;
        DataFormat format = guessFormat(name);
        try {
            if (format == MDA) {
                arr = MdaFormat.load(name);
            } else {
                BufferedImage img = ImageIO.read(new File(name));
                arr = imageToShapedArray(img);
            }
        } catch (Exception e) {
            fatal("Error while reading " + name + "(" + e.getMessage() +").");
        }
        return arr;
    }

    /**
     * Save data to file.
     * <p>
     * The file format is guessed from the extension of the file name and
     * default options are used to encode the file.
     * </p>
     * @param arr  - The data to save.
     * @param name - The name of the destination file.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void save(ShapedArray arr, String name)
            throws FileNotFoundException, IOException {
        save(arr, name, new FormatOptions());
    }

    /**
     * Save data to file with given options.
     * <p>
     * The file format is guessed from the extension of the file name and
     * default options are used to encode the file.
     * </p>
     * @param arr  - The data to save.
     * @param name - The name of the destination file.
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

    /*
     * See: {@link Color#getRGB()} for the packing of colors in an int (Bits
     * 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
     */

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

    protected static boolean isFlat(ShapedArray arr) {
        // FIXME: dummy method for further optimization
        return false;
    }

    /**
     * Make a buffered image from a shaped array with given options.
     * <p>
     * Different kind of shaped array can be interpreted as an image: 2D
     * shaped array are interpreted as grey scale images, 3D shaped arrays are
     * interpreted as RGB array if their first dimension is 3 and as RGBA
     * array if their first dimension is 4.  All other shapes result in
     * throwing an {@link IllegalArgumentException}.  The penultimate
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
        int arrayType = arr.getType();
        int depth = -1;
        int imageType = -1;
        if (rank == 2) {
            depth = 1;
            if (arrayType == Traits.BYTE) {
                imageType = BufferedImage.TYPE_BYTE_GRAY;
            } else {
                imageType = BufferedImage.TYPE_USHORT_GRAY;
            }
        } else if (rank == 3) {
            depth = shape.dimension(0);
            if (depth == 3) {
                imageType = BufferedImage.TYPE_3BYTE_BGR;
            } else if (depth == 4 && arrayType == Traits.BYTE) {
                imageType = BufferedImage.TYPE_4BYTE_ABGR;
            } else {
                depth = -1;
            }
        }
        if (depth < 0) {
            throw new IllegalArgumentException("Conversion to image is only allowed for WIDTH x HEIGHT arrays, 3 x WIDTH x HEIGHT arrays or 4 x WIDTH x HEIGHT byte arrays.");
        }
        final int width = shape.dimension(rank - 2);
        final int height = shape.dimension(rank - 1);
        BufferedImage image = new BufferedImage(width, height, imageType);
        WritableRaster raster = image.getRaster();
        final int minX = raster.getMinX();
        final int minY = raster.getMinY();
        if (imageType == BufferedImage.TYPE_BYTE_GRAY) {
            /* Input is a 2-D byte array. */
            Byte2D src = (Byte2D)arr;
            if (raster.getNumBands() != 1 || raster.getNumDataElements() != 1 ||
                raster.getTransferType() != DataBuffer.TYPE_BYTE) {
                throw new IllegalArgumentException("assertion failed for TYPE_BYTE_GRAY");
            }
            if (isFlat(src)) {
                raster.setDataElements(minX, minY, width, height, src.flatten());
            } else {
                byte[] data = new byte[1];
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        data[0] = src.get(x, y);
                        raster.setDataElements(minX + x, minY + y, data);
                    }
                }
            }
        } else if (imageType == BufferedImage.TYPE_USHORT_GRAY) {
            /* Input is 2-D array of any type but byte. */
            if (raster.getNumBands() != 1 || raster.getNumDataElements() != 1 ||
                raster.getTransferType() != DataBuffer.TYPE_USHORT) {
                throw new IllegalArgumentException("assertion failed for TYPE_USHORT_GRAY");
            }
            short[] data = new short[1];
            switch (arrayType) {
            case Traits.SHORT: {
                Short2D src = (Short2D)arr;
                double[] sf = opts.getScaling(arr, 0, 0xFFFF);
                final float scale = (float)sf[0];
                final float bias = (float)sf[1];
                final float factor = 1/scale;
                final float minLevel = 0;
                final float maxLevel = 0xFFFF;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        data[0] = (short)((int)Math.max(minLevel, Math.min(((float)src.get(x,y) - bias)*factor, maxLevel)) & 0xFFFF);
                        raster.setDataElements(minX + x, minY + y, data);
                    }
                }
            }
            case Traits.INT: {
                Int2D src = (Int2D)arr;
                double[] sf = opts.getScaling(arr, 0, 0xFFFF);
                final float scale = (float)sf[0];
                final float bias = (float)sf[1];
                final float factor = 1/scale;
                final float minLevel = 0;
                final float maxLevel = 0xFFFF;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        data[0] = (short)((int)Math.max(minLevel, Math.min(((float)src.get(x,y) - bias)*factor, maxLevel)) & 0xFFFF);
                        raster.setDataElements(minX + x, minY + y, data);
                    }
                }
            }
            case Traits.LONG: {
                Long2D src = (Long2D)arr;
                double[] sf = opts.getScaling(arr, 0, 0xFFFF);
                final double scale = (double)sf[0];
                final double bias = (double)sf[1];
                final double factor = 1/scale;
                final double minLevel = 0;
                final double maxLevel = 0xFFFF;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        data[0] = (short)((int)Math.max(minLevel, Math.min(((double)src.get(x,y) - bias)*factor, maxLevel)) & 0xFFFF);
                        raster.setDataElements(minX + x, minY + y, data);
                    }
                }
            }
            case Traits.FLOAT: {
                Float2D src = (Float2D)arr;
                double[] sf = opts.getScaling(arr, 0, 0xFFFF);
                final float scale = (float)sf[0];
                final float bias = (float)sf[1];
                final float factor = 1/scale;
                final float minLevel = 0;
                final float maxLevel = 0xFFFF;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        data[0] = (short)((int)Math.max(minLevel, Math.min((src.get(x,y) - bias)*factor, maxLevel)) & 0xFFFF);
                        raster.setDataElements(minX + x, minY + y, data);
                    }
                }
            }
            case Traits.DOUBLE: {
                Double2D src = (Double2D)arr;
                double[] sf = opts.getScaling(arr, 0, 0xFFFF);
                final double scale = (double)sf[0];
                final double bias = (double)sf[1];
                final double factor = 1/scale;
                final double minLevel = 0;
                final double maxLevel = 0xFFFF;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        data[0] = (short)((int)Math.max(minLevel, Math.min((src.get(x,y) - bias)*factor, maxLevel)) & 0xFFFF);
                        raster.setDataElements(minX + x, minY + y, data);
                    }
                }
            }
            default:
                throw new IllegalTypeException();
            }
        } else if (imageType == BufferedImage.TYPE_3BYTE_BGR) {
            /* Input is 3-D array of any type. */
            if (raster.getNumBands() != 3 || raster.getNumDataElements() != 3 ||
                raster.getTransferType() != DataBuffer.TYPE_BYTE) {
                throw new IllegalArgumentException("assertion failed for TYPE_3BYTE_BGR");
            }
            byte[] data = new byte[3];
            switch (arrayType) {
            case Traits.BYTE: {
                Byte3D src = (Byte3D)arr;
                if (isFlat(src)) {
                    raster.setDataElements(minX, minY, width, height, src.flatten());
                } else {
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            data[0] = src.get(0, x, y);
                            data[1] = src.get(1, x, y);
                            data[2] = src.get(2, x, y);
                            raster.setDataElements(minX + x, minY + y, data);
                        }
                    }
                }
            }
            case Traits.SHORT: {
                Short3D src = (Short3D)arr;
                double[] sf = opts.getScaling(arr, 0, 0xFF);
                final float scale = (float)sf[0];
                final float bias = (float)sf[1];
                final float factor = 1/scale;
                final float minLevel = 0;
                final float maxLevel = 0xFF;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        data[0] = (byte)((int)Math.max(minLevel, Math.min(((float)src.get(0,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        data[1] = (byte)((int)Math.max(minLevel, Math.min(((float)src.get(1,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        data[2] = (byte)((int)Math.max(minLevel, Math.min(((float)src.get(2,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        raster.setDataElements(minX + x, minY + y, data);
                    }
                }
            }
            case Traits.INT: {
                Int3D src = (Int3D)arr;
                double[] sf = opts.getScaling(arr, 0, 0xFF);
                final float scale = (float)sf[0];
                final float bias = (float)sf[1];
                final float factor = 1/scale;
                final float minLevel = 0;
                final float maxLevel = 0xFF;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        data[0] = (byte)((int)Math.max(minLevel, Math.min(((float)src.get(0,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        data[1] = (byte)((int)Math.max(minLevel, Math.min(((float)src.get(1,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        data[2] = (byte)((int)Math.max(minLevel, Math.min(((float)src.get(2,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        raster.setDataElements(minX + x, minY + y, data);
                    }
                }
            }
            case Traits.LONG: {
                Long3D src = (Long3D)arr;
                double[] sf = opts.getScaling(arr, 0, 0xFF);
                final double scale = (double)sf[0];
                final double bias = (double)sf[1];
                final double factor = 1/scale;
                final double minLevel = 0;
                final double maxLevel = 0xFF;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        data[0] = (byte)((int)Math.max(minLevel, Math.min(((double)src.get(0,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        data[1] = (byte)((int)Math.max(minLevel, Math.min(((double)src.get(1,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        data[2] = (byte)((int)Math.max(minLevel, Math.min(((double)src.get(2,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        raster.setDataElements(minX + x, minY + y, data);
                    }
                }
            }
            case Traits.FLOAT: {
                Float3D src = (Float3D)arr;
                double[] sf = opts.getScaling(arr, 0, 0xFF);
                final float scale = (float)sf[0];
                final float bias = (float)sf[1];
                final float factor = 1/scale;
                final float minLevel = 0;
                final float maxLevel = 0xFF;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        data[0] = (byte)((int)Math.max(minLevel, Math.min((src.get(0,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        data[1] = (byte)((int)Math.max(minLevel, Math.min((src.get(1,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        data[2] = (byte)((int)Math.max(minLevel, Math.min((src.get(2,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        raster.setDataElements(minX + x, minY + y, data);
                    }
                }
            }
            case Traits.DOUBLE: {
                Double3D src = (Double3D)arr;
                double[] sf = opts.getScaling(arr, 0, 0xFF);
                final double scale = (double)sf[0];
                final double bias = (double)sf[1];
                final double factor = 1/scale;
                final double minLevel = 0;
                final double maxLevel = 0xFF;
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        data[0] = (byte)((int)Math.max(minLevel, Math.min((src.get(0,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        data[1] = (byte)((int)Math.max(minLevel, Math.min((src.get(1,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        data[2] = (byte)((int)Math.max(minLevel, Math.min((src.get(2,x,y) - bias)*factor, maxLevel)) & 0xFF);
                        raster.setDataElements(minX + x, minY + y, data);
                    }
                }
            }
            default:
                throw new IllegalTypeException();
            }
        } else /* imageType == BufferedImage.TYPE_4BYTE_ABGR */ {
            /* Input is 3-D byte array. */
            Byte3D src = (Byte3D)arr;
            if (raster.getNumBands() != 4 || raster.getNumDataElements() != 4 ||
                raster.getTransferType() != DataBuffer.TYPE_BYTE) {
                throw new IllegalArgumentException("assertion failed for TYPE_4BYTE_ABGR");
            }
            if (isFlat(src)) {
                raster.setDataElements(minX, minY, width, height, src.flatten());
            } else {
                byte[] data = new byte[4];
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        data[0] = src.get(0, x, y);
                        data[1] = src.get(1, x, y);
                        data[2] = src.get(2, x, y);
                        data[3] = src.get(3, x, y);
                        raster.setDataElements(minX + x, minY + y, data);
                    }
                }
            }
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
    /* BUFFERED IMAGES TO ARRAYS */

    public static String getImageTypeName(BufferedImage image) {
        return getImageTypeName(image.getType());
    }

    public static String getImageTypeName(int type) {
        switch (type) {
        case BufferedImage.TYPE_INT_RGB:
            return "TYPE_INT_RGB";
        case BufferedImage.TYPE_INT_BGR:
            return "TYPE_INT_BGR";
        case BufferedImage.TYPE_3BYTE_BGR:
            return "TYPE_3BYTE_BGR";
        case BufferedImage.TYPE_USHORT_565_RGB:
            return "TYPE_USHORT_565_RGB";
        case BufferedImage.TYPE_USHORT_555_RGB:
            return "TYPE_USHORT_555_RGB";
        case BufferedImage.TYPE_INT_ARGB:
            return "TYPE_INT_ARGB";
        case BufferedImage.TYPE_INT_ARGB_PRE:
            return "TYPE_INT_ARGB_PRE";
        case BufferedImage.TYPE_4BYTE_ABGR:
            return "TYPE_4BYTE_ABGR";
        case BufferedImage.TYPE_4BYTE_ABGR_PRE:
            return "TYPE_4BYTE_ABGR_PRE";
        case BufferedImage.TYPE_BYTE_GRAY:
            return "TYPE_BYTE_GRAY";
        case BufferedImage.TYPE_USHORT_GRAY:
            return "TYPE_USHORT_GRAY";
        case BufferedImage.TYPE_BYTE_BINARY:
            return "TYPE_BYTE_BINARY";
        case BufferedImage.TYPE_BYTE_INDEXED:
            return "TYPE_BYTE_INDEXED";
        case BufferedImage.TYPE_CUSTOM:
            return "TYPE_CUSTOM";
        default:
            return "UNKOWN";
        }
    }

    /** Get the name of the data type of a DataBuffer object. */
    public static String getDataTypeName(DataBuffer buffer) {
        return getDataTypeName(buffer.getDataType());
    }

    /** Get the name of the data type of a DataBuffer object. */
    public static String getDataTypeName(int type) {
        switch (type) {
        case DataBuffer.TYPE_BYTE:
            /* Unsigned byte data. */
            return "TYPE_BYTE";
        case DataBuffer.TYPE_SHORT:
            return "TYPE_SHORT";
        case DataBuffer.TYPE_USHORT:
            return "TYPE_USHORT";
        case DataBuffer.TYPE_INT:
            return "TYPE_INT";
        case DataBuffer.TYPE_FLOAT:
            return "TYPE_FLOAT";
        case DataBuffer.TYPE_DOUBLE:
            return "TYPE_DOUBLE";
        case DataBuffer.TYPE_UNDEFINED:
            return "TYPE_UNDEFINED";
        default:
            return "UNKOWN";
        }
    }

    public static void printImageInfo(PrintStream output, BufferedImage image, String name) {
        Raster raster = image.getRaster();
        final int height = raster.getHeight();
        final int width = raster.getWidth();
        final int minX = raster.getMinX();
        final int minY = raster.getMinY();
        final int numBands = raster.getNumBands();
        final int transferType = raster.getTransferType();
        final int numElements = raster.getNumDataElements();
        if (name != null) {
            output.format("image name       %s\n", name);
        }
        output.format("image type:      %s\n", getImageTypeName(image));
        output.format("image size:      %d x %d\n", width, height);
        output.format("image origin:    (%d,%d)\n", minX, minY);
        output.format("number of bands: %d\n", numBands);
        output.format("transfer data:   %s x %d\n", getDataTypeName(transferType), numElements);
    }

    /**
     * Convert a BufferedImage to a given image type.
     * <p>
     * This method converts an image of any type to the given type.  The
     * operation is lazy: if the image type of the original image is already
     * the correct one, it is returned as the result.
     * </p>>
     * @param img - The input image.
     * @param type - The image type of the result.
     * @return A {@link BufferedImage} of the requested type.
     * @see {@link BufferedImage#TYPE_3BYTE_BGR}
     *      {@link BufferedImage#TYPE_4BYTE_ABGR},
     *      {@link BufferedImage#TYPE_4BYTE_ABGR_PRE},
     *      {@link BufferedImage#TYPE_BYTE_BINARY},
     *      {@link BufferedImage#TYPE_BYTE_INDEXED},
     *      {@link BufferedImage#TYPE_BYTE_GRAY},
     *      {@link BufferedImage#TYPE_USHORT_555_RGB},
     *      {@link BufferedImage#TYPE_USHORT_565_RGB},
     *      {@link BufferedImage#TYPE_USHORT_GRAY},
     *      {@link BufferedImage#TYPE_INT_RGB},
     *      {@link BufferedImage#TYPE_INT_BGR},
     *      {@link BufferedImage#TYPE_INT_ARGB},
     *      {@link BufferedImage#TYPE_INT_ARGB_PRE}.
     */
    public static BufferedImage convertImage(BufferedImage img, int type) {
        if (img.getType() == type) {
            return img;
        }
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), type);
        Graphics2D g2 = result.createGraphics();
        g2.drawImage(img, null, null);
        g2.dispose();
        return result;
    }

    /**
     * Convert an image into a shaped array.
     * <p>
     * This method attempts to convert the image data with no loss of
     * information.  The data type and rank of the result depend on the image
     * type.
     * </p><p>
     * Gray scaled images yield 2D array while colored (RGB) and translucent
     * (RGBA) images yields 3D images.  The penultimate dimension is the width
     * of the image and the last dimension is the height of the image.  For
     * RGB and RGBA images, the first dimension is respectively 3 and 4 with
     * the red red, green, blue, and alpha channels (if any) given by:
     * <pre>
     *    Array3D arr   = imageToShapedArray(image);
     *    Array2D red   = arr.slice(0, 0);
     *    Array2D green = arr.slice(1, 0);
     *    Array2D blue  = arr.slice(2, 0);
     *    Array2D alpha = arr.slice(3, 0);
     * </pre>
     * If the result is a {@link ByteArray}, the bytes are to be interpreted
     * as being unsigned.  For {@link BufferedImage#TYPE_USHORT_GRAY} images,
     * the result is a {@link Int2D} array as TiPi does not implement
     * {@code unsigned short} data.
     * <p>
     * For translucent images, the color levels in the result are not
     * pre-multiplied by the alpha value (for
     * {@link BufferedImage#TYPE_INT_ARGB_PRE} and
     * {@link BufferedImage#TYPE_4BYTE_ABGR_PRE}, the pre-multiplication is
     * reversed).
     * </p>
     * @return A shaped array.
     */
    public static ShapedArray imageToShapedArray(BufferedImage image) {
        WritableRaster raster = image.getRaster();
        final int height = raster.getHeight();
        final int width = raster.getWidth();
        final int minX = raster.getMinX();
        final int minY = raster.getMinY();
        final int numBands = raster.getNumBands();
        final int transferType = raster.getTransferType();
        final int numDataElements = raster.getNumDataElements();
        final int type = image.getType();
        switch (type) {
        case BufferedImage.TYPE_INT_RGB: {
            if (numBands != 3 || numDataElements != 1 || transferType != DataBuffer.TYPE_INT) {
                throw new IllegalArgumentException("assertion failed for TYPE_INT_RGB");
            }
            Byte3D arr = Byte3D.wrap(new byte[3*width*height], 3, width, height);
            int[] data = new int[1];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getDataElements(x + minX, y + minY, data);
                    int pixel = data[0];
                    int red   = (pixel >> 16) & 0xFF;
                    int green = (pixel >>  8) & 0xFF;
                    int blue  = (pixel      ) & 0xFF;
                    arr.set(0, x, y, (byte)red);
                    arr.set(1, x, y, (byte)green);
                    arr.set(2, x, y, (byte)blue);
                }
            }
            return arr;
        }
        case BufferedImage.TYPE_INT_ARGB: {
            if (numBands != 4 || numDataElements != 1 || transferType != DataBuffer.TYPE_INT) {
                throw new IllegalArgumentException("assertion failed for TYPE_INT_ARGB");
            }
            Byte3D arr = Byte3D.wrap(new byte[4*width*height], 4, width, height);
            int[] data = new int[1];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getDataElements(x + minX, y + minY, data);
                    int pixel = data[0];
                    int alpha = (pixel >> 24) & 0xFF;
                    int red   = (pixel >> 16) & 0xFF;
                    int green = (pixel >>  8) & 0xFF;
                    int blue  = (pixel      ) & 0xFF;
                    arr.set(0, x, y, (byte)red);
                    arr.set(1, x, y, (byte)green);
                    arr.set(2, x, y, (byte)blue);
                    arr.set(3, x, y, (byte)alpha);
                }
            }
            return arr;
        }
        case BufferedImage.TYPE_INT_ARGB_PRE: {
            /* Assume colors have been premultiplied by alpha/255. */
            final int b = 255;
            final int a = 2*b;
            if (numBands != 4 || numDataElements != 1 || transferType != DataBuffer.TYPE_INT) {
                throw new IllegalArgumentException("assertion failed for TYPE_INT_ARGB_PRE");
            }
            Byte3D arr = Byte3D.wrap(new byte[4*width*height], 4, width, height);
            int[] data = new int[1];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getDataElements(x + minX, y + minY, data);
                    int pixel = data[0];
                    int alpha = (pixel >> 24) & 0xFF;
                    int c = 2*alpha;
                    int red   = (((pixel >> 16) & 0xFF)*c + b)/a;
                    int green = (((pixel >>  8) & 0xFF)*c + b)/a;
                    int blue  = (((pixel      ) & 0xFF)*c + b)/a;
                    arr.set(0, x, y, (byte)red);
                    arr.set(1, x, y, (byte)green);
                    arr.set(2, x, y, (byte)blue);
                    arr.set(3, x, y, (byte)alpha);
                }
            }
            return arr;
        }
        case BufferedImage.TYPE_INT_BGR: {
            if (numBands != 3 || numDataElements != 1 || transferType != DataBuffer.TYPE_INT) {
                throw new IllegalArgumentException("assertion failed for TYPE_INT_BGR");
            }
            Byte3D arr = Byte3D.wrap(new byte[3*width*height], 3, width, height);
            int[] data = new int[1];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getDataElements(x + minX, y + minY, data);
                    int pixel = data[0];
                    int red   = (pixel >>  8) & 0xFF;
                    int green = (pixel >> 16) & 0xFF;
                    int blue  = (pixel >> 24) & 0xFF;
                    arr.set(0, x, y, (byte)red);
                    arr.set(1, x, y, (byte)green);
                    arr.set(2, x, y, (byte)blue);
                }
            }
            return arr;
        }
        case BufferedImage.TYPE_USHORT_565_RGB: {
            if (numBands != 3 || numDataElements != 1 || transferType != DataBuffer.TYPE_USHORT) {
                throw new IllegalArgumentException("assertion failed for TYPE_USHORT_565_RGB");
            }
            Byte3D arr = Byte3D.wrap(new byte[3*width*height], 3, width, height);
            short[] data = new short[1];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getDataElements(x + minX, y + minY, data);
                    int pixel = data[0];
                    int red   = (pixel >> 11) & 0x1F;
                    int green = (pixel >>  5) & 0x3F;
                    int blue  = (pixel      ) & 0x1F;
                    arr.set(0, x, y, (byte)red);
                    arr.set(1, x, y, (byte)green);
                    arr.set(2, x, y, (byte)blue);
                }
            }
            return arr;
        }
        case BufferedImage.TYPE_USHORT_555_RGB: {
            if (numBands != 3 || numDataElements != 1 || transferType != DataBuffer.TYPE_USHORT) {
                throw new IllegalArgumentException("assertion failed for TYPE_USHORT_555_RGB");
            }
            Byte3D arr = Byte3D.wrap(new byte[3*width*height], 3, width, height);
            short[] data = new short[1];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getDataElements(x + minX, y + minY, data);
                    int pixel = data[0];
                    int red   = (pixel >> 10) & 0x1F;
                    int green = (pixel >>  5) & 0x1F;
                    int blue  = (pixel      ) & 0x1F;
                    arr.set(0, x, y, (byte)red);
                    arr.set(1, x, y, (byte)green);
                    arr.set(2, x, y, (byte)blue);
                }
            }
            return arr;
        }
        case BufferedImage.TYPE_3BYTE_BGR: {
            if (numBands != 3 || numDataElements != 3 || transferType != DataBuffer.TYPE_BYTE) {
                throw new IllegalArgumentException("assertion failed for TYPE_3BYTE_BGR");
            }
            Byte3D arr = Byte3D.wrap(new byte[3*width*height], 3, width, height);
            byte[] data = new byte[3];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getDataElements(x + minX, y + minY, data);
                    arr.set(0, x, y, data[0]); /* red*/
                    arr.set(1, x, y, data[1]); /* green */
                    arr.set(2, x, y, data[2]); /* blue */
                }
            }
            return arr;
        }
        case BufferedImage.TYPE_4BYTE_ABGR: {
            if (numBands != 4 || numDataElements != 4 || transferType != DataBuffer.TYPE_BYTE) {
                throw new IllegalArgumentException("assertion failed for TYPE_4BYTE_ABGR");
            }
            Byte3D arr = Byte3D.wrap(new byte[4*width*height], 4, width, height);
            byte[] data = new byte[4];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getDataElements(x + minX, y + minY, data);
                    arr.set(0, x, y, data[0]); /* red*/
                    arr.set(1, x, y, data[1]); /* green */
                    arr.set(2, x, y, data[2]); /* blue */
                    arr.set(3, x, y, data[3]); /* alpha */
                }
            }
            return arr;
        }
        case BufferedImage.TYPE_4BYTE_ABGR_PRE: {
            /* Assume colors have been premultiplied by alpha/255. */
            final int b = 255;
            final int a = 2*b;
            if (numBands != 4 || numDataElements != 4 || transferType != DataBuffer.TYPE_BYTE) {
                throw new IllegalArgumentException("assertion failed for TYPE_4BYTE_ABGR_PRE");
            }
            Byte3D arr = Byte3D.wrap(new byte[4*width*height], 4, width, height);
            byte[] data = new byte[4];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getDataElements(x + minX, y + minY, data);
                    int alpha = data[3];
                    int c = 2*alpha;
                    int red   = (data[0]*c + b)/a;
                    int green = (data[1]*c + b)/a;
                    int blue  = (data[2]*c + b)/a;
                    arr.set(0, x, y, (byte)red);
                    arr.set(1, x, y, (byte)green);
                    arr.set(2, x, y, (byte)blue);
                    arr.set(3, x, y, (byte)alpha);
                }
            }
            return arr;
        }
        case BufferedImage.TYPE_BYTE_GRAY: {
            if (numBands != 1 || numDataElements != 1 || transferType != DataBuffer.TYPE_BYTE) {
                throw new IllegalArgumentException("assertion failed for TYPE_BYTE_GRAY");
            }
            Byte2D arr = Byte2D.wrap(new byte[width*height], width, height);
            byte[] data = new byte[1];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getDataElements(x + minX, y + minY, data);
                    arr.set(x, y, (byte)data[0]);
                }
            }
            return arr;
        }
        case BufferedImage.TYPE_USHORT_GRAY: {
            if (numBands != 1 || numDataElements != 1 || transferType != DataBuffer.TYPE_USHORT) {
                throw new IllegalArgumentException("assertion failed for TYPE_USHORT_GRAY");
            }
            Int2D arr = Int2D.wrap(new int[width*height], width, height);
            short[] data = new short[1];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getDataElements(x + minX, y + minY, data);
                    arr.set(x, y, data[0]);
                }
            }
            return arr;
        }
        case BufferedImage.TYPE_BYTE_BINARY: {
            if (numBands != 1 || numDataElements != 1 || transferType != DataBuffer.TYPE_BYTE) {
                throw new IllegalArgumentException("assertion failed for TYPE_BYTE_BINARY");
            }
            Byte2D arr = Byte2D.wrap(new byte[width*height], width, height);
            // FIXME:
            byte[] data = new byte[1];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getDataElements(x + minX, y + minY, data);
                    arr.set(x, y, data[0]);
                }
            }
            return arr;
        }
        //case BufferedImage.TYPE_BYTE_INDEXED:
        //case BufferedImage.TYPE_CUSTOM:
        }

        /* For all other image types, we use a fallback method. */
        if (transferType == DataBuffer.TYPE_FLOAT) {
            if (numBands == 1) {
                Float2D arr = Float2D.wrap(new float[width*height], width, height);
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        arr.set(x, y, raster.getSampleFloat(x + minX, y + minY, 0));
                    }
                }
                return arr;
            } else {
                Float3D arr = Float3D.wrap(new float[numBands*width*height], numBands, width, height);
                if (numBands == 3) {
                    /* unroll the innermost loop for the most common case */
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            arr.set(0, x, y, raster.getSampleFloat(x + minX, y + minY, 0));
                            arr.set(1, x, y, raster.getSampleFloat(x + minX, y + minY, 1));
                            arr.set(2, x, y, raster.getSampleFloat(x + minX, y + minY, 2));
                        }
                    }
                } else {
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            for (int b = 0; b < numBands; ++b) {
                                arr.set(b, x, y, raster.getSampleFloat(x + minX, y + minY, b));
                            }
                        }
                    }
                }
                return arr;
            }
        } else if (transferType == DataBuffer.TYPE_DOUBLE) {
            if (numBands == 1) {
                Double2D arr = Double2D.wrap(new double[width*height], width, height);
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        arr.set(x, y, raster.getSampleDouble(x + minX, y + minY, 0));
                    }
                }
                return arr;
            } else {
                Double3D arr = Double3D.wrap(new double[numBands*width*height], numBands, width, height);
                if (numBands == 3) {
                    /* unroll the innermost loop for the most common case */
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            arr.set(0, x, y, raster.getSampleDouble(x + minX, y + minY, 0));
                            arr.set(1, x, y, raster.getSampleDouble(x + minX, y + minY, 1));
                            arr.set(2, x, y, raster.getSampleDouble(x + minX, y + minY, 2));
                        }
                    }
                } else {
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            for (int b = 0; b < numBands; ++b) {
                                arr.set(b, x, y, raster.getSampleDouble(x + minX, y + minY, b));
                            }
                        }
                    }
                }
                return arr;
            }
        } else {
            if (numBands == 1) {
                Int2D arr = Int2D.wrap(new int[width*height], width, height);
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        arr.set(x, y, raster.getSample(x + minX, y + minY, 0));
                    }
                }
                return arr;
            } else {
                Int3D arr = Int3D.wrap(new int[numBands*width*height], numBands, width, height);
                if (numBands == 3) {
                    /* unroll the innermost loop for the most common case */
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            arr.set(0, x, y, raster.getSample(x + minX, y + minY, 0));
                            arr.set(1, x, y, raster.getSample(x + minX, y + minY, 1));
                            arr.set(2, x, y, raster.getSample(x + minX, y + minY, 2));
                        }
                    }
                } else {
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            for (int b = 0; b < numBands; ++b) {
                                arr.set(b, x, y, raster.getSample(x + minX, y + minY, b));
                            }
                        }
                    }
                }
                return arr;
            }
        }
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

        if (args == null || args.length == 0) {
            args = new String[]{"/tmp/test-image.jpg"};
        }
        BufferedImage image;
        int[] imageTypes = new int[]{
                BufferedImage.TYPE_INT_RGB,
                BufferedImage.TYPE_INT_BGR,
                BufferedImage.TYPE_3BYTE_BGR,
                BufferedImage.TYPE_USHORT_565_RGB,
                BufferedImage.TYPE_USHORT_555_RGB,
                BufferedImage.TYPE_INT_ARGB,
                BufferedImage.TYPE_INT_ARGB_PRE,
                BufferedImage.TYPE_4BYTE_ABGR,
                BufferedImage.TYPE_4BYTE_ABGR_PRE,
                BufferedImage.TYPE_BYTE_GRAY,
                BufferedImage.TYPE_USHORT_GRAY,
                BufferedImage.TYPE_BYTE_BINARY,
                BufferedImage.TYPE_BYTE_INDEXED
        };
        for (int k = 0; k < args.length; ++k) {
            String name = args[k];
            try {
                image = ImageIO.read(new File(name));
                printImageInfo(System.out, image, name);
                System.out.format("\n");
                for (int j = 0; j < imageTypes.length; ++j) {
                    BufferedImage converted = convertImage(image, imageTypes[j]);
                    printImageInfo(System.out, converted, name);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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