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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import mitiv.array.ArrayUtils;
import mitiv.array.ShapedArray;

/**
 * This class deals with identifying various data format.
 * 
 * @author Éric Thiébaut.
 *
 */
public class DataFormat {

    /** Make this class non-instantiable. */
    protected DataFormat() {}

    /** Error while scanning data. */
    public static final int FMT_ERROR = -1;

    /** Unknown data format. */
    public static final int FMT_UNKNOWN = 0;

    /** Portable anymap (PBM/PGM/PPM) image. */
    public static final int FMT_PNM = 1;

    /** JPEG image. */
    public static final int FMT_JPEG = 2;

    /** Portable Network Graphic (PNG) image. */
    public static final int FMT_PNG = 3;

    /** GIF image. */
    public static final int FMT_GIF = 4;

    /** BMP image. */
    public static final int FMT_BMP = 5;

    /** Wireless Bitmap (WBMP) image format. */
    public static final int FMT_WBMP = 6;

    /** TIFF image format. */
    public static final int FMT_TIFF = 7;

    /** Flexible Image Transport System (FITS) format. */
    public static final int FMT_FITS = 8;

    /** Multi-dimensional array (MDA) format. */
    public static final int FMT_MDA = 9;

    private static final String[] formatNames = {null, "PNM", "JPEG", "PNG", "GIF", "BMP", "WBMP", "TIFF", "FITS", "MDA"};

    /**
     * Get the name of a data file format.
     * 
     * For image format supported by Java, the format name is suitable for
     * {@link ImageIO#write}.
     * @param id - The identifier of the file format, as {@link #FMT_JPGEG}.
     * @return The name of the format, {@code null} if {@code id} does not correspond to a known format.
     */
    public static String getFormatName(int id) {
        return (id >= 0 && id < formatNames.length ? formatNames[id] : null);
    }

    /**
     * Guess data format from the extension of the file name.
     * <p>
     * This function examines the extension of the file name
     * to determine the data format.
     * 
     * @param fileName - The name of the file.
     * @return A file format identifier: {@link #FMT_UNKNOWN} if the
     *         format is not recognized; otherwise {@link #FMT_PNM},
     *         {@link #FMT_JPEG}, {@link #FMT_PNG}, {@link #FMT_TIFF},
     *         {@link #FMT_FITS}, {@link #FMT_GIF}, or {@link #FMT_MDA}.
     */
    public static final int guessFormat(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0) {
            return FMT_UNKNOWN;
        }
        String extension = fileName.substring(index + 1);
        int length = extension.length();
        if (length == 3) {
            if (extension.equalsIgnoreCase("jpg")) {
                return FMT_JPEG;
            } else if (extension.equalsIgnoreCase("png")) {
                return FMT_PNG;
            } else if (extension.equalsIgnoreCase("pnm") |
                    extension.equalsIgnoreCase("pbm") |
                    extension.equalsIgnoreCase("pgm") |
                    extension.equalsIgnoreCase("ppm")) {
                return FMT_PNM;
            } else if (extension.equalsIgnoreCase("fts") |
                    extension.equalsIgnoreCase("fit")) {
                return FMT_FITS;
            } else if (extension.equalsIgnoreCase("gif")) {
                return FMT_GIF;
            } else if (extension.equalsIgnoreCase("tif")) {
                return FMT_TIFF;
            } else if (extension.equalsIgnoreCase("mda")) {
                return FMT_MDA;
            }
        } else if (length == 4) {
            if (extension.equalsIgnoreCase("jpeg")) {
                return FMT_JPEG;
            } else if (extension.equalsIgnoreCase("fits")) {
                return FMT_FITS;
            } else if (extension.equalsIgnoreCase("tiff")) {
                return FMT_TIFF;
            }
        } else if (length == 6) {
            if (extension.equalsIgnoreCase("oifits")) {
                return FMT_FITS;
            }
        }
        return FMT_UNKNOWN;
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
     *         or {@link #FMT_UNKNOWN} if the format is not recognized, or
     *         {@link #FMT_ERROR} in case of error.
     */
    public static final int guessFormat(BufferedInputDataStream stream) {
        int length;
        try {
            int preserved = stream.insure(80);
            if (preserved < 2) {
                return FMT_UNKNOWN;
            }
            length = Math.min(preserved, 80);
        } catch (IOException ex) {
            return FMT_ERROR;
        }
        byte[] magic = new byte[length];
        int format = FMT_UNKNOWN;
        stream.mark();
        try {
            length = stream.read(magic, 0, length);
            if (length < 2) {
                return FMT_ERROR;
            }
            if (length >= 2 && matchMagic(magic, '\377', '\330')) {
                format = FMT_JPEG;
            } else if (length >= 4 && matchMagic(magic, '\211', 'P', 'N', 'G')) {
                format = FMT_PNG;
            } else if (length >= 4 && matchMagic(magic, 'M', 'M', '\000', '\052')) {
                format = FMT_TIFF;
            } else if (length >= 4 && matchMagic(magic, 'I', 'I', '\052', '\000')) {
                format = FMT_TIFF;
            } else if (length >= 4 && matchMagic(magic, 'G', 'I', 'F', '8')) {
                format = FMT_GIF;
            } else if (length >= 3 && magic[0] == (byte)'P' && isSpace(magic[2])
                    && (byte)'1' <= magic[1] && magic[1] <= (byte)'6') {
                /* "P1" space = ascii PBM (portable bitmap)
                 * "P2" space = ascii PGM (portable graymap)
                 * "P3" space = ascii PPM (portable pixmap)
                 * "P4" space = raw PBM
                 * "P5" space = raw PGM
                 * "P6" space = raw PPM
                 */
                format = FMT_PNM;
            } else if (length >= 3
                    && matchMagic(magic, 'S', 'I', 'M', 'P', 'L', 'E', ' ', ' ', '=')) {
                format = FMT_FITS;
            }
        } catch (IOException ex) {
            return FMT_ERROR;
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
    public static ShapedArray load(String fileName, int colorModel, String description) {
        ShapedArray arr = null;
        int format = DataFormat.guessFormat(fileName);
        try {
            if (format == DataFormat.FMT_MDA) {
                arr = MdaFormat.load(fileName);
            } else {
                arr = ArrayUtils.imageAsDouble(ImageIO.read(new File(fileName)), colorModel);
            }
        } catch (Exception e) {
            fatal("Error while reading " + description + "(" + e.getMessage() +").");
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
        save(arr, fileName, new ScalingOptions());
    }

    /**
     * Save data to file with given options.
     * <p>
     * The file format is guessed from the extension of the file name and default
     * options are used to encode the file.
     * </p>
     * @param arr - The data to save.
     * @param fileName - The destination file.
     * @param opts - Options for encoding the data.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void save(ShapedArray img, String fileName,
            ScalingOptions opts) throws FileNotFoundException, IOException {
        int format = DataFormat.guessFormat(fileName);
        String formatName = null;
        switch (format) {
        //case DataFormat.FMT_PNM:
        case DataFormat.FMT_JPEG:
        case DataFormat.FMT_PNG:
        case DataFormat.FMT_GIF:
        case DataFormat.FMT_BMP:
        case DataFormat.FMT_WBMP:
            //case DataFormat.FMT_TIFF:
            //case DataFormat.FMT_FITS:
            formatName = DataFormat.getFormatName(format);
            break;
        case DataFormat.FMT_MDA:
            MdaFormat.save(img, fileName);
            return;
        default:
            formatName = null;
        }
        if (formatName == null) {
            fatal("Unknown/unsupported format name.");
        }
        int depth, width, height, rank = img.getRank();
        if (rank == 2) {
            depth = 1;
            width = img.getDimension(0);
            height = img.getDimension(1);
        } else if (rank == 3) {
            depth = img.getDimension(0);
            width = img.getDimension(1);
            height = img.getDimension(2);
        } else {
            depth = 0;
            width = 0;
            height = 0;
            fatal("Expecting 2D array as image.");
        }
        double[] data = img.toDouble().flatten();
        BufferedImage buf = ArrayUtils.doubleAsBuffered(data, depth, width, height, opts);
        ImageIO.write(buf, formatName, new File(fileName));
    }

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
