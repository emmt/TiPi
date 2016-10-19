/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014-2016 the MiTiV project, http://mitiv.univ-lyon1.fr/
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

import static nom.tam.fits.BasicHDU.BITPIX_BYTE;
import static nom.tam.fits.BasicHDU.BITPIX_DOUBLE;
import static nom.tam.fits.BasicHDU.BITPIX_FLOAT;
import static nom.tam.fits.BasicHDU.BITPIX_INT;
import static nom.tam.fits.BasicHDU.BITPIX_LONG;
import static nom.tam.fits.BasicHDU.BITPIX_SHORT;
import static nom.tam.fits.header.Standard.BITPIX;
import static nom.tam.fits.header.Standard.BLANK;
import static nom.tam.fits.header.Standard.BSCALE;
import static nom.tam.fits.header.Standard.BZERO;
import static nom.tam.fits.header.Standard.COMMENT;
import static nom.tam.fits.header.Standard.HISTORY;
import static nom.tam.fits.header.Standard.NAXIS;
import static nom.tam.fits.header.Standard.NAXISn;
import static nom.tam.fits.header.Standard.SIMPLE;
import static nom.tam.fits.header.Standard.XTENSION;
import static nom.tam.fits.header.Standard.XTENSION_IMAGE;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Locale;

import mitiv.array.ArrayFactory;
import mitiv.array.ByteArray;
import mitiv.array.DoubleArray;
import mitiv.array.FloatArray;
import mitiv.array.IntArray;
import mitiv.array.LongArray;
import mitiv.array.ShapedArray;
import mitiv.array.ShortArray;
import mitiv.base.Traits;
import mitiv.exception.DataFormatException;
import mitiv.exception.IllegalTypeException;
import mitiv.exception.RecoverableFormatException;
import mitiv.linalg.shaped.ShapedVector;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.FitsUtil;
import nom.tam.fits.Header;
import nom.tam.fits.ImageData;
import nom.tam.fits.ImageHDU;
import nom.tam.fits.header.IFitsHeader;
import nom.tam.util.ArrayDataInput;
import nom.tam.util.BufferedFile;

/**
 * Implement reading/writing of FITS files.
 *
 * @author Éric
 *
 * @see {@link https://archive.stsci.edu/fits/users_guide/node21.html} for an
 *      explanation of the standard FITS keywords.
 */
public class FitsFormat {

    private final static int BUFFER_SIZE = 1024*1024;
    private final static int SUCCESS = 0;
    private final static int FAILURE = -1;

    /* FITS value types. */
    private final static int NONE = 0;
    private final static int LOGICAL = 1;
    private final static int INTEGER = 2;
    private final static int REAL = 3;
    private final static int STRING = 4;

    private final static Hashtable<String, IFitsHeader> keywords = createKeywords();

    private static Hashtable<String, IFitsHeader> createKeywords() {
        Hashtable<String, IFitsHeader> keywords = new Hashtable<String, IFitsHeader>();
        keywords.put("SIMPLE", SIMPLE);
        keywords.put("BITPIX", BITPIX);
        keywords.put("NAXIS", NAXIS);
        keywords.put("NAXISn", NAXISn);
        keywords.put("BLANK", BLANK);
        keywords.put("COMMENT", COMMENT);
        keywords.put("HISTORY", HISTORY);
        return keywords;
    }

    /**
     * Read the primary FITS "image" as a shaped array.
     *
     * <p> The "image" in the primary HDU of the FITS file is read and converted
     * into a shaped array with floating point values. The conversion takes into
     * account the keywords "BLANK" (for bad data), "BSCALE" and "BZERO" (for
     * value correction). </p>
     *
     * @param filename   The name of the FITS (can also be an URL).
     *
     * @return A shaped array.
     *
     * @throws IOException
     * @throws DataFormatException
     * @throws RecoverableFormatException
     */
    public static ShapedArray load(String filename)
            throws IOException, DataFormatException, RecoverableFormatException {
        return load(filename, 0, false);
    }

    /**
     * Read a FITS "image" as a shaped array.
     *
     * <p> The specified HDU of the FITS file is read and converted into a
     * shaped array with floating point values. The conversion takes into
     * account the keywords "BLANK" (for bad data), "BSCALE" and "BZERO" (for
     * value correction). The HDU must be an "image" extension. </p>
     *
     * @param filename   The name of the FITS (can also be an URL).
     *
     * @param index      The HDU index (starting at 0).
     *
     * @return A shaped array.
     *
     * @throws IOException
     * @throws DataFormatException
     * @throws RecoverableFormatException
     */
    public static ShapedArray load(String filename, int index)
            throws IOException, DataFormatException, RecoverableFormatException {
        return load(filename, index, false);
    }

    /**
     * Read a FITS "image" as a shaped array.
     *
     * <p> The specified HDU of the FITS file is read and convert into a
     * shaped array.  The HDU must be an "image" extension.  Unless argument
     * {@code raw} is set true, the returned array has floating point values
     * and the keywords "BLANK" (for bad data), "BSCALE" and "BZERO" (for
     * value correction) are taken into account. </p>
     *
     * @param filename   The name of the FITS (can also be an URL).
     *
     * @param index      The HDU index (starting at 0).
     *
     * @param raw        True to avoid value correction and data conversion.
     *
     * @return A shaped array.
     *
     * @throws IOException
     * @throws DataFormatException
     * @throws RecoverableFormatException
     */
    public static ShapedArray load(String filename, int index, boolean raw)
            throws IOException, DataFormatException, RecoverableFormatException {
        setup();
        Fits fits = null;
        ShapedArray arr = null;
        try {
            /* Open the file and check whether this is an image extension. */
            fits = new Fits(filename);
            BasicHDU<?> hdu = fits.getHDU(index);
            Header header = hdu.getHeader();
            if (index != 0) {
                String xtension = header.getStringValue(XTENSION);
                xtension = (xtension == null ? "" : xtension.trim().toUpperCase(Locale.US));
                if (! xtension.equals(XTENSION_IMAGE) && ! xtension.equals("IUEIMAGE")) {
                    throw new DataFormatException(String.format("HDU %d of FITS file '%s' is not an IMAGE",
                            index, filename));
                }
            }

            /* Determine the dimensions of the shaped array. */
            int[] dims = getDimensions(header);
            /* Determine the element type of the shaped array. */
            final int bitpix = header.getIntValue(BITPIX, -1);
            if (bitpix == -1) {
                throw new DataFormatException(String.format("Missing BITPIX keyword in HDU %d of FITS file '%s'",
                        index, filename));
            }
            int type; // shaped array type
            switch (bitpix) {
            case BITPIX_BYTE:
                type = (raw ? Traits.BYTE : Traits.FLOAT);
                break;
            case BITPIX_SHORT:
                type = (raw ? Traits.SHORT : Traits.FLOAT);
                break;
            case BITPIX_INT:
                type = (raw ? Traits.INT : Traits.DOUBLE);
                break;
            case BITPIX_LONG:
                type = (raw ? Traits.LONG : Traits.DOUBLE);
                break;
            case BITPIX_FLOAT:
                type = Traits.FLOAT;
                break;
            case BITPIX_DOUBLE:
                type = Traits.DOUBLE;
                break;
            default:
                throw new IllegalTypeException(String.format("Unsupported BITPIX = %d in HDU %d of FITS file '%s'",
                        bitpix, index, filename));
            }

            /* Create the shaped array. */
            arr = ArrayFactory.create(type, dims);
            int number = arr.getNumber();

            /* Read the data efficiently. */
            double bscale = (raw ? 1.0 : header.getDoubleValue(BSCALE, 1.0));
            double bzero = (raw ? 0.0 : header.getDoubleValue(BZERO, 0.0));
            boolean haveBlank = (! raw && (bitpix == BITPIX_BYTE ||
                    bitpix == BITPIX_SHORT || bitpix == BITPIX_INT ||
                    bitpix == BITPIX_LONG) && header.containsKey(BLANK.key()));
            long blankValue = (haveBlank ? header.getLongValue(BLANK) : 0);
            header.getLongValue(BLANK);
            if (! hdu.getData().reset()) {
                throw new DataFormatException(String.format("Unable to reset data stream for HDU %d of FITS file '%s'",
                        index, filename));
            }
            ArrayDataInput inp = fits.getStream();
            int status = FAILURE;
            switch (type) {
            case Traits.BYTE:
                status = (inp.read(((ByteArray)arr).getData(), 0, number) == number*1 ? SUCCESS : FAILURE);
                break;
            case Traits.SHORT:
                status = (inp.read(((ShortArray)arr).getData(), 0, number) == number*2 ? SUCCESS : FAILURE);
                break;
            case Traits.INT:
                status = (inp.read(((IntArray)arr).getData(), 0, number) == number*4 ? SUCCESS : FAILURE);
                break;
            case Traits.LONG:
                status = (inp.read(((LongArray)arr).getData(), 0, number) == number*8 ? SUCCESS : FAILURE);
                break;
            case Traits.FLOAT:
                status = readArray(((FloatArray)arr).getData(), inp, bitpix, (float)bscale, (float)bzero, haveBlank, blankValue);
                break;
            case Traits.DOUBLE:
                status = readArray(((DoubleArray)arr).getData(), inp, bitpix, bscale, bzero, haveBlank, blankValue);
                break;
            }
            if (status == FAILURE) {
                throw new DataFormatException(String.format("Truncated data for HDU %d of FITS file '%s'",
                        index, filename));
            }
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DataFormatException(ex.getMessage());
        } finally {
            if (fits != null) {
                fits.close();
            }
        }
        return arr;
    }

    public static void save(ShapedVector vec, String filename, Object... meta)
            throws IOException {
        save(vec.asShapedArray(), filename, meta);
    }

    public static void save(ShapedArray arr, String filename, Object... meta)
            throws IOException {
        BufferedFile out = null;
        ImageHDU hdu = null;
        Header header = new Header();
        int bitpix = bitpixFromType(arr.getType());
        int rank = arr.getRank();
        try {
            /* Build the initial header. */
            header.setSimple(true);
            header.setBitpix(bitpix);
            header.setNaxes(rank);
            for (int k = 0; k < rank; ++k) {
                header.setNaxis(k + 1, arr.getDimension(k));
            }

            /* Add supplementary cards (FIXME: use an iterator). */
            int i = 0;
            while (i < meta.length) {
                IFitsHeader key = null;
                Object val = null;
                String comment = null;
                String keyword = null;
                int ival = -1, icom = -1;
                if (meta[i] instanceof IFitsHeader) {
                    key = (IFitsHeader)meta[i];
                    keyword = key.key();
                    if (i + 1 >= meta.length) {
                        throw new DataFormatException(String.format("Missing value for FITS keyword '%s'", keyword));
                    }
                    ival = i + 1;
                } else if (meta[i] instanceof String) {
                    keyword = (String)meta[i];
                    if (keyword == null) {
                        keyword = "";
                    } else {
                        keyword = keyword.trim().toUpperCase(Locale.US);
                    }
                    key = keywords.get(keyword);
                    if (key != null && key.valueType() == IFitsHeader.VALUE.NONE) {
                        icom = i + 1;
                    } else {
                        ival = i + 1;
                        icom = i + 2;
                    }
                }
                if (ival >= 0) {
                    if (ival >= meta.length) {
                        throw new DataFormatException(String.format("Missing value for FITS keyword '%s'", keyword));
                    }
                    val = meta[ival];
                } else {
                    val = null;
                }
                if (icom >= 0) {
                    if (icom >= meta.length) {
                        comment = "";
                    } else {
                        if (meta[icom] == null) {
                            comment = "";
                        } else if (meta[icom] instanceof String) {
                            comment = (String)meta[icom];
                        } else {
                            throw new DataFormatException(String.format("Comment for FITS keyword '%s' must be a string", keyword));
                        }
                    }
                }
                i = Math.max(ival,  icom) + 1;

                /* Fix type of value. */
                int valueType;
                if (val == null) {
                    valueType = NONE;
                } else if (val instanceof String) {
                    valueType = STRING;
                } else if (val instanceof Boolean) {
                    valueType = LOGICAL;
                } else if (val instanceof Integer) {
                    valueType = INTEGER;
                    val = new Long(((Integer)val).intValue());
                } else if (val instanceof Long) {
                    valueType = INTEGER;
                } else if (val instanceof Double) {
                    valueType = REAL;
                } else if (val instanceof Byte) {
                    valueType = INTEGER;
                    val = new Long(((Byte)val).byteValue() & 0xFF);
                } else if (val instanceof Short) {
                    valueType = INTEGER;
                    val = new Long(((Short)val).shortValue());
                } else if (val instanceof Float) {
                    valueType = REAL;
                    val = new Double(((Float)val).floatValue());
                } else {
                    throw new IllegalTypeException(String.format("Unsupported value type for FITS keyword '%s'", keyword));
                }

                if (valueType == LOGICAL) {
                    boolean value = (Boolean)val;
                    if (key != null) {
                        header.addValue(key, value);
                    } else {
                        header.addValue(keyword, value, comment);
                    }
                } else if (valueType == INTEGER) {
                    long value = (Long)val;
                    if (key != null) {
                        header.addValue(key, value);
                    } else {
                        header.addValue(keyword, value, comment);
                    }
                } else if (valueType == REAL) {
                    long value = (Long)val;
                    if (key != null) {
                        header.addValue(key, value);
                    } else {
                        header.addValue(keyword, value, comment);
                    }
                } else if (valueType == STRING) {
                    String value = (String)val;
                    if (key != null) {
                        header.addValue(key, value);
                    } else {
                        header.addValue(keyword, value, comment);
                    }
                } else {
                    if (key == COMMENT) {
                        header.insertComment(comment);
                    } else if (key == HISTORY) {
                        header.insertHistory(comment);
                    } else {
                        throw new DataFormatException(String.format("Unsupported commentary FITS keyword '%s'", keyword));
                    }
                }
            }

            /*
             * Create the HDU from the header, open the file, write the header,
             * write the data and zero pad the file.
             */
            hdu = (ImageHDU)FitsFactory.hduFactory(header, new ImageData(header));
            out = new BufferedFile(filename, "rw");
            hdu.getHeader().write(out);
            int elsize = 0;
            switch (arr.getType()) {
            case Traits.BYTE:
                out.write(((ByteArray)arr).flatten());
                elsize = 1;
                break;
            case Traits.SHORT:
                out.write(((ShortArray)arr).flatten());
                elsize = 2;
                break;
            case Traits.INT:
                out.write(((IntArray)arr).flatten());
                elsize = 4;
                break;
            case Traits.LONG:
                out.write(((LongArray)arr).flatten());
                elsize = 8;
                break;
            case Traits.FLOAT:
                out.write(((FloatArray)arr).flatten());
                elsize = 4;
                break;
            case Traits.DOUBLE:
                out.write(((DoubleArray)arr).flatten());
                elsize = 8;
                break;
            }
            FitsUtil.pad(out,  elsize*(long)arr.getNumber());
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DataFormatException(ex.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private static int readArray(float[] dst, ArrayDataInput inp, int bitpix,
            float alpha, float beta, boolean haveBlank, long blankValue)
                    throws IOException {
        int pending = dst.length;
        int offset = 0;
        if (bitpix == BITPIX_BYTE) {
            final int elsize = 1;
            final int blank = (int)blankValue;
            byte[] buf = new byte[Math.min(BUFFER_SIZE/elsize, pending)];
            while (pending > 0) {
                int count = Math.min(pending, buf.length);
                if (inp.read(buf, 0, count) != count*elsize) {
                    return FAILURE;
                }
                if (haveBlank) {
                    for (int i = 0; i < count; ++i) {
                        int val = (buf[i] & 0xff);
                        if (val == blank) {
                            dst[offset + i] = Float.NaN;
                        } else {
                            dst[offset + i] = alpha*val + beta;
                        }
                    }
                } else {
                    for (int i = 0; i < count; ++i) {
                        dst[offset + i] = alpha*(buf[i] & 0xff) + beta;
                    }
                }
                pending -= count;
                offset += count;
            }
        } else if (bitpix == BITPIX_SHORT) {
            final int elsize = 2;
            final short blank = (short)blankValue;
            short[] buf = new short[Math.min(BUFFER_SIZE/elsize, pending)];
            while (pending > 0) {
                int count = Math.min(pending, buf.length);
                if (inp.read(buf, 0, count) != count*elsize) {
                    return FAILURE;
                }
                if (haveBlank) {
                    for (int i = 0; i < count; ++i) {
                        short val = buf[i];
                        if (val == blank) {
                            dst[offset + i] = Float.NaN;
                        } else {
                            dst[offset + i] = alpha*val + beta;
                        }
                    }
                } else {
                    for (int i = 0; i < count; ++i) {
                        dst[offset + i] = alpha*buf[i] + beta;
                    }
                }
                pending -= count;
                offset += count;
            }
        } else if (bitpix == BITPIX_INT) {
            final int elsize = 4;
            final int blank = (int)blankValue;
            int[] buf = new int[Math.min(BUFFER_SIZE/elsize, pending)];
            while (pending > 0) {
                int count = Math.min(pending, buf.length);
                if (inp.read(buf, 0, count) != count*elsize) {
                    return FAILURE;
                }
                if (haveBlank) {
                    for (int i = 0; i < count; ++i) {
                        int val = buf[i];
                        if (val == blank) {
                            dst[offset + i] = Float.NaN;
                        } else {
                            dst[offset + i] = alpha*val + beta;
                        }
                    }
                } else {
                    for (int i = 0; i < count; ++i) {
                        dst[offset + i] = alpha*buf[i] + beta;
                    }
                }
                pending -= count;
                offset += count;
            }
        } else if (bitpix ==  BITPIX_LONG) {
            final int elsize = 8;
            final long blank = blankValue;
            long[] buf = new long[Math.min(BUFFER_SIZE/elsize, pending)];
            while (pending > 0) {
                int count = Math.min(pending, buf.length);
                if (inp.read(buf, 0, count) != count*elsize) {
                    return FAILURE;
                }
                if (haveBlank) {
                    for (int i = 0; i < count; ++i) {
                        long val = buf[i];
                        if (val == blank) {
                            dst[offset + i] = Float.NaN;
                        } else {
                            dst[offset + i] = alpha*val + beta;
                        }
                    }
                } else {
                    for (int i = 0; i < count; ++i) {
                        dst[offset + i] = alpha*buf[i] + beta;
                    }
                }
                pending -= count;
                offset += count;
            }
        } else if (bitpix ==  BITPIX_FLOAT) {
            if (inp.read(dst, 0, pending) != pending*4) {
                return FAILURE;
            }
            if (alpha != 1 || beta != 0) {
                for (int i = 0; i < dst.length; ++i) {
                    dst[i] = alpha*dst[i] + beta;
                }
            }
        } else {
            throw new DataFormatException("This is not supposed to happen!");
        }
        return SUCCESS;
    }

    private static int readArray(double[] dst, ArrayDataInput inp, int bitpix,
            double alpha, double beta, boolean haveBlank, long blankValue)
                    throws IOException {
        int pending = dst.length;
        int offset = 0;
        if (bitpix == BITPIX_BYTE) {
            final int elsize = 1;
            final int blank = (int)blankValue;
            byte[] buf = new byte[Math.min(BUFFER_SIZE/elsize, pending)];
            while (pending > 0) {
                int count = Math.min(pending, buf.length);
                if (inp.read(buf, 0, count) != count*elsize) {
                    return FAILURE;
                }
                if (haveBlank) {
                    for (int i = 0; i < count; ++i) {
                        int val = (buf[i] & 0xff);
                        if (val == blank) {
                            dst[offset + i] = Double.NaN;
                        } else {
                            dst[offset + i] = alpha*val + beta;
                        }
                    }
                } else {
                    for (int i = 0; i < count; ++i) {
                        dst[offset + i] = alpha*(buf[i] & 0xff) + beta;
                    }
                }
                pending -= count;
                offset += count;
            }
        } else if (bitpix == BITPIX_SHORT) {
            final int elsize = 2;
            final short blank = (short)blankValue;
            short[] buf = new short[Math.min(BUFFER_SIZE/elsize, pending)];
            while (pending > 0) {
                int count = Math.min(pending, buf.length);
                if (inp.read(buf, 0, count) != count*elsize) {
                    return FAILURE;
                }
                if (haveBlank) {
                    for (int i = 0; i < count; ++i) {
                        short val = buf[i];
                        if (val == blank) {
                            dst[offset + i] = Double.NaN;
                        } else {
                            dst[offset + i] = alpha*val + beta;
                        }
                    }
                } else {
                    for (int i = 0; i < count; ++i) {
                        dst[offset + i] = alpha*buf[i] + beta;
                    }
                }
                pending -= count;
                offset += count;
            }
        } else if (bitpix == BITPIX_INT) {
            final int elsize = 4;
            final int blank = (int)blankValue;
            int[] buf = new int[Math.min(BUFFER_SIZE/elsize, pending)];
            while (pending > 0) {
                int count = Math.min(pending, buf.length);
                if (inp.read(buf, 0, count) != count*elsize) {
                    return FAILURE;
                }
                if (haveBlank) {
                    for (int i = 0; i < count; ++i) {
                        int val = buf[i];
                        if (val == blank) {
                            dst[offset + i] = Double.NaN;
                        } else {
                            dst[offset + i] = alpha*val + beta;
                        }
                    }
                } else {
                    for (int i = 0; i < count; ++i) {
                        dst[offset + i] = alpha*buf[i] + beta;
                    }
                }
                pending -= count;
                offset += count;
            }
        } else if (bitpix ==  BITPIX_LONG) {
            final int elsize = 8;
            final long blank = blankValue;
            long[] buf = new long[Math.min(BUFFER_SIZE/elsize, pending)];
            while (pending > 0) {
                int count = Math.min(pending, buf.length);
                if (inp.read(buf, 0, count) != count*elsize) {
                    return FAILURE;
                }
                if (haveBlank) {
                    for (int i = 0; i < count; ++i) {
                        long val = buf[i];
                        if (val == blank) {
                            dst[offset + i] = Double.NaN;
                        } else {
                            dst[offset + i] = alpha*val + beta;
                        }
                    }
                } else {
                    for (int i = 0; i < count; ++i) {
                        dst[offset + i] = alpha*buf[i] + beta;
                    }
                }
                pending -= count;
                offset += count;
            }
        } else if (bitpix == BITPIX_DOUBLE) {
            if (inp.read(dst, 0, pending) != pending*8) {
                return FAILURE;
            }
            if (alpha != 1 || beta != 0) {
                for (int i = 0; i < dst.length; ++i) {
                    dst[i] = alpha*dst[i] + beta;
                }
            }
        } else {
            throw new DataFormatException("This is not supposed to happen!");
        }
        return SUCCESS;
    }

    private static void setup() {
        // Switch to "US" locale to avoid problems with number formats.
        //Locale.setDefault(Locale.US);
        FitsFactory.setUseHierarch(true);
        FitsFactory.setLongStringsEnabled(true);
    }

    public static int[] getDimensions(Header header) throws FitsException {
        int rank = header.getIntValue(NAXIS, 0);
        if (rank < 0) {
            throw new FitsException("Negative NAXIS value " + rank);
        }
        if (rank > 9) {
            throw new FitsException("NAXIS value " + rank + " too large");
        }

        if (rank == 0) {
            return null;
        }

        int[] dims = new int[rank];
        for (int i = 0; i < rank; ++i) {
            dims[i] = header.getIntValue(NAXISn.n(i + 1), 0);
        }

        return dims;
    }

    public static int bitpixFromType(int type) {
        switch (type) {
        case Traits.BYTE:
            return BITPIX_BYTE;
        case Traits.SHORT:
            return BITPIX_SHORT;
        case Traits.INT:
            return BITPIX_INT;
        case Traits.LONG:
            return BITPIX_LONG;
        case Traits.FLOAT:
            return BITPIX_FLOAT;
        case Traits.DOUBLE:
            return BITPIX_DOUBLE;
        default:
            throw new IllegalTypeException("No BITPIX for that data type");
        }
    }

    public static int typeFromBitpix(int bitpix) {
        switch (bitpix) {
        case BITPIX_BYTE:
            return Traits.BYTE;
        case BITPIX_SHORT:
            return Traits.SHORT;
        case BITPIX_INT:
            return Traits.INT;
        case BITPIX_LONG:
            return Traits.LONG;
        case BITPIX_FLOAT:
            return Traits.FLOAT;
        case BITPIX_DOUBLE:
            return Traits.DOUBLE;
        default:
            throw new IllegalTypeException("Unsupported BITPIX value");
        }
    }

    public static void main(String[] args) {
        // Switch to "US" locale to avoid problems with number formats.
        //Locale.setDefault(Locale.US);
        FitsFactory.setUseHierarch(true);
        FitsFactory.setLongStringsEnabled(true);
        try {
            ShapedArray arr = load("/home/eric/work/data/stsdas-testdata/data/saturn/saturn.fits");
            MdaFormat.save(arr, "/tmp/saturn.mda");
        } catch (Exception e) {
            System.err.format("ERROR: %s\n", e.getMessage());
        }
    }
}

