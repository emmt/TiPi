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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import mitiv.array.ArrayFactory;
import mitiv.array.ByteArray;
import mitiv.array.DoubleArray;
import mitiv.array.Float3D;
import mitiv.array.FloatArray;
import mitiv.array.IntegerArray;
import mitiv.array.LongArray;
import mitiv.array.ShapedArray;
import mitiv.array.ShortArray;
import mitiv.base.Traits;
import mitiv.exception.DataFormatException;
import mitiv.exception.RecoverableFormatException;

/**
 * Implement MDA data format.
 * <p>
 * The MDA (for Multi-Dimensional Array) data format provides a minimal encapsulation
 * to store or to transfer multi-dimensional arrays, optionally with byte swapping.
 * 
 * <h2>Java Usage</h2>
 * In Java, all integers are signed.  This is not really appropriate for image data
 * with 8-bit pixel values since these values are in general unsigned 8-bit integers
 * (in the range 0-255).  For now, the code assumes that 8-bit integers are saved as
 * if they where 8-bit unsigned integers stored as Java {@code byte}'s.  Therefore
 * the integer value is:
 * <pre>
 *    int intValue = (byteValue & 0xFF);
 * </pre>
 * where {@code byteValue} is the value written in the MDA stream.
 * 
 * <h2>Format</h2>
 * The binary format consists in a header followed by the bytes of the binary
 * array.  All binary values are encoded in little endian (least significant
 * bytes first) or big endian (most significant bytes first) byte order,
 * floating point values are encoded according to IEEE 754 standard.  Byte
 * order is indicated by the identifier in the 4 first bytes of the header.
 *
 * The header is given by the following table.  The variables <b>type</b> and
 * <b>rank</b> are integers in the range 0-15 with the type of the array elements
 * and the number of dimensions.
 * <pre>
 * |   Size  Type   Description
 * | ----------------------------------------------------------
 * |      4  int8   Identifier:
 * |                 - for big endian:     'M' 'D' 'A'  x
 * |                 - for little endian:   x  'A' 'D' 'M'
 * |                with 4th byte: x = (type << 4) | rank
 * | rank*4  int32  The dimensions of the array, the first one
 * |                corresponding to the faster varying index.
 * </pre>
 *
 * The table below gives the correspondence of the <b>type</b> integer code with
 * the binary type of the elements of the array.  For instance, a 2-D array of
 * signed 16-bit integers has the 4th byte of its header equal to 50 (0x32 in
 * hexadecimal, 062 in octal).
 * <pre>
 * | Code    Type    Description
 * | ----------------------------------------------
 * |    1   int8     signed 8-bit integer
 * |    2   uint8    unsigned 8-bit integer
 * |    3   int16    signed 16-bit integer
 * |    4   uint16   unsigned 16-bit integer
 * |    5   int32    signed 32-bit integer
 * |    6   uint32   unsigned 32-bit integer
 * |    7   int64    signed 64-bit integer
 * |    8   uint64   unsigned 64-bit integer
 * |    9   float    single precision floating-point
 * |   10   double   double precision floating-point
 * </pre>
 *
 * @author Éric Thiébaut
 */
public class MdaFormat {

    public final static int MDA_NONE    =  0;
    public final static int MDA_INT8    =  1;
    public final static int MDA_UINT8   =  2;
    public final static int MDA_INT16   =  3;
    public final static int MDA_UINT16  =  4;
    public final static int MDA_INT32   =  5;
    public final static int MDA_UINT32  =  6;
    public final static int MDA_INT64   =  7;
    public final static int MDA_UINT64  =  8;
    public final static int MDA_FLOAT   =  9;
    public final static int MDA_DOUBLE  = 10;

    /* Equivalence between Java primitive types and MDA types.  All Java
     * integers are signed but we will pretend that Java's bytes are unsigned.
     * This means that the byte values must be properly filtered,
     * e.g. intValue = (bytValue & 0xFF).
     */
    public final static int MDA_BYTE    = MDA_UINT8;
    public final static int MDA_SHORT   = MDA_INT16;
    public final static int MDA_INT     = MDA_INT32;
    public final static int MDA_LONG    = MDA_INT64;

    /* Bytes used in the MDA identifier. */
    public final static byte MDA_HDR0 = (byte)0x4D; // ascii 'M'
    public final static byte MDA_HDR1 = (byte)0x44; // ascii 'D'
    public final static byte MDA_HDR2 = (byte)0x41; // ascii 'A'
    public final static int  MDA_HDR  = ((MDA_HDR0 << 24) | (MDA_HDR1 << 16) | (MDA_HDR2 << 8)); // 0x4D444100
    public final static int  MDA_MAX_RANK = 15;

    final public static int getPrimitiveType(int type) {
        switch (type) {
        case MDA_INT8:
        case MDA_UINT8:
            return Traits.BYTE;
        case MDA_INT16:
        case MDA_UINT16:
            return Traits.SHORT;
        case MDA_INT32:
        case MDA_UINT32:
            return Traits.INT;
        case MDA_INT64:
        case MDA_UINT64:
            return Traits.LONG;
        case MDA_FLOAT:
            return Traits.FLOAT;
        case MDA_DOUBLE:
            return Traits.DOUBLE;
        default:
            return Traits.VOID;
        }
    }

    final public static boolean isUnsignedInteger(int type) {
        return (MDA_UINT8 <= type && type <= MDA_UINT64 && type%2 == 0);
    }

    final public static boolean isInteger(int type) {
        return (0 < type && type < MDA_FLOAT);
    }

    final public static boolean isFloatingPoint(int type) {
        return (type == MDA_DOUBLE || type == MDA_FLOAT);
    }

    /**
     * Read a multi-dimensional array in MDA format.
     * @param dataStream - The input data stream.
     * @return A {@link #ShapedArray} which can be safely casted according to its
     *         type and/or rank. For instance, if its type is
     *         {@link Traits#FLOAT} and its rank is equal to 3, it can
     *         be casted into a {@link FloatArray}, a {@link Array3D},
     *         or a {@link Float3D}.
     * @throws IOException, DataFormatException,
     * @throws RecoverableFormatException in case of an error but the data stream
     *         is not corrupted (no data consumption).
     */
    public static ShapedArray load(BufferedInputDataStream dataStream)
            throws IOException, DataFormatException, RecoverableFormatException {
        /* Make sure we can read the header without impacting the stream in case of failure. */
        final int minHeaderSize = 4;
        final int maxHeaderSize = minHeaderSize + 4*MDA_MAX_RANK;
        int preserved = dataStream.insure(maxHeaderSize);
        if (preserved < minHeaderSize) {
            throw new DataFormatException("insufficient data for getting 4-byte MDA identifier");
        }
        dataStream.mark();

        /* Parse header (restore the data stream in case of error). */
        int info, type, rank, number, dimensionsRead = 0;
        int[] shape;
        try {
            byte[] hdr = new byte[4];
            if (dataStream.read(hdr, 0, 4) != 4) {
                throw new RecoverableFormatException("failed to read 4-byte MDA identifier");
            }
            if (hdr[0] == MDA_HDR0 && hdr[1] == MDA_HDR1 && hdr[2] == MDA_HDR2) {
                dataStream.setByteOrder(ByteOrder.BIG_ENDIAN);
                info = hdr[3] & 0xFF;
            } else if (hdr[3] == MDA_HDR0 && hdr[2] == MDA_HDR1 && hdr[1] == MDA_HDR2) {
                dataStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                info = hdr[0] & 0xFF;
            } else {
                throw new RecoverableFormatException("unrecognized MDA header {" + (hdr[0]&0xFF) + ","
                        + (hdr[1]&0xFF) + ","+ (hdr[2]&0xFF) + ","+ (hdr[3]&0xFF) + "}");
            }
            type = getPrimitiveType((info >> 4) & 0xF);
            if (type == Traits.VOID) {
                throw new RecoverableFormatException("bad type in MDA header");
            }
            rank = (info & 0xF);
            shape = new int[rank];
            dimensionsRead = dataStream.read(shape, 0, rank);
            if (dimensionsRead != rank) {
                throw new RecoverableFormatException("short MDA stream (missing some dimensions)");
            }
            long bigNumber = 1L;
            for (int k = 0; k < rank; ++k) {
                int length = shape[k];
                if (length <= 0) {
                    throw new RecoverableFormatException("bad dimension in MDA header (dim" + (k+1) + " = " + length + ")");
                }
                bigNumber *= length;
            }
            if (bigNumber > Integer.MAX_VALUE) {
                throw new RecoverableFormatException("number of elements too large in MDA data (number = " + bigNumber + ")");
            }
            number = (int)bigNumber;
        } catch (RecoverableFormatException ex) {
            if (minHeaderSize + dimensionsRead*4 <= preserved) {
                dataStream.reset();
                throw ex;
            } else {
                throw new DataFormatException(ex.getMessage());
            }
        } catch (IOException ex) {
            if (minHeaderSize + dimensionsRead*4 <= preserved) {
                dataStream.reset();
            }
            throw ex;
        }

        /* Read the data part. */
        if (type == Traits.BYTE) {
            byte[] arr = new byte[number];
            if (dataStream.read(arr, 0, number) == number) {
                return ArrayFactory.wrap(arr, shape);
            }
        } else if (type == Traits.SHORT) {
            short[] arr = new short[number];
            if (dataStream.read(arr, 0, number) == number) {
                return ArrayFactory.wrap(arr, shape);
            }
        } else if (type == Traits.INT) {
            int[] arr = new int[number];
            if (dataStream.read(arr, 0, number) == number) {
                return ArrayFactory.wrap(arr, shape);
            }
        } else if (type == Traits.LONG) {
            long[] arr = new long[number];
            if (dataStream.read(arr, 0, number) == number) {
                return ArrayFactory.wrap(arr, shape);
            }
        } else if (type == Traits.FLOAT) {
            float[] arr = new float[number];
            if (dataStream.read(arr, 0, number) == number) {
                return ArrayFactory.wrap(arr, shape);
            }
        } else /*  type == Traits.DOUBLE */ {
            double[] arr = new double[number];
            if (dataStream.read(arr, 0, number) == number) {
                return ArrayFactory.wrap(arr, shape);
            }
        }
        throw new DataFormatException("short MDA data (some elements cannot be read)");
    }

    /**
     * Read a multi-dimensional array in MDA format.
     * @param fileName - The name of the input file.
     * @return A {@link #ShapedArray} which can be safely casted according to its
     *         type and/or rank.  See {@link #load(BufferedInputDataStream)} for
     *         more details.
     * @throws FileNotFoundException, IOException, DataFormatException
     * @throws RecoverableFormatException in case of an error but the data stream
     *         is not corrupted (no data consumption).
     */
    public static ShapedArray load(String fileName)
            throws FileNotFoundException, IOException,
            DataFormatException, RecoverableFormatException {
        FileInputStream fileStream = new FileInputStream(fileName);
        BufferedInputDataStream dataStream = new BufferedInputDataStream(fileStream);
        ShapedArray obj;
        try {
            obj = load(dataStream);
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } catch (DataFormatException ex) {
            throw ex;
        } catch (RecoverableFormatException ex) {
            throw ex;
        } finally{
            dataStream.close();
            fileStream.close();
        }
        return obj;
    }

    public static void save(ShapedArray obj, String fileName)
            throws FileNotFoundException, IOException {
        save(obj, fileName, ByteOrder.nativeOrder());
    }

    public static void save(ShapedArray obj, String fileName, ByteOrder order)
            throws FileNotFoundException, IOException  {
        FileOutputStream fileStream = new FileOutputStream(fileName);
        BufferedOutputDataStream dataStream = new BufferedOutputDataStream(fileStream);
        dataStream.setByteOrder(order);
        try {
            save(obj, dataStream);
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            dataStream.close();
            fileStream.close();
        }
    }

    public static void save(ShapedArray obj, BufferedOutputDataStream dataStream)
            throws IOException {
        int rank = obj.getRank();
        if (rank <= 0 || rank > MDA_MAX_RANK) {
            throw new IllegalArgumentException("illegal rank for MDA data");
        }
        int type = obj.getType();
        int number = obj.getNumber();
        int mdaType;
        switch (type) {
        case Traits.BYTE:
            mdaType = MDA_BYTE;
            break;
        case Traits.SHORT:
            mdaType = MDA_SHORT;
            break;
        case Traits.INT:
            mdaType = MDA_INT;
            break;
        case Traits.LONG:
            mdaType = MDA_LONG;
            break;
        case Traits.FLOAT:
            mdaType = MDA_FLOAT;
            break;
        case Traits.DOUBLE:
            mdaType = MDA_DOUBLE;
            break;
        default:
            throw new IllegalArgumentException("unsupported data type");
        }

        /* Write the header data as a single array of int's. */
        int headerCount = 1 + rank;
        int[] headerData = new int[headerCount];
        headerData[0] = MDA_HDR | ((mdaType << 4) | rank);
        for (int k = 0; k < rank; ++k) {
            headerData[k + 1] = obj.getDimension(k);
        }
        if (dataStream.write(headerData, 0, headerCount) != headerCount) {
            throw new IOException("failed to write MDA header part");
        }

        /* Write the data part. */
        int transfered = 0;
        if (type == Traits.BYTE) {
            byte[] arr = ((ByteArray)obj).flatten();
            transfered = dataStream.write(arr, 0, number);
        } else if (type == Traits.SHORT) {
            short[] arr = ((ShortArray)obj).flatten();
            transfered = dataStream.write(arr, 0, number);
        } else if (type == Traits.INT) {
            int[] arr = ((IntegerArray)obj).flatten();
            transfered = dataStream.write(arr, 0, number);
        } else if (type == Traits.LONG) {
            long[] arr = ((LongArray)obj).flatten();
            transfered = dataStream.write(arr, 0, number);
        } else if (type == Traits.FLOAT) {
            float[] arr = ((FloatArray)obj).flatten();
            transfered = dataStream.write(arr, 0, number);
        } else if (type == Traits.DOUBLE) {
            double[] arr = ((DoubleArray)obj).flatten();
            transfered = dataStream.write(arr, 0, number);
        }
        if (transfered != number) {
            throw new IOException("failed to write MDA data part");
        }
    }

    /*========================================================================*/

    public static void main(String[] args) {
        /*
        if (MDA_HDR == 0x4D444100) {
            System.out.println("value of MDA_HDR is correct");
        } else {
            System.err.println("value of MDA_HDR is incorrect");
        }
         */
        int dim1 = 3;
        int dim2 = 4;
        int dim3 = 5;
        float[] arr = new float[dim1*dim2*dim3];
        FloatArray obj = Float3D.wrap(arr, dim1, dim2, dim3);
        Float3D orig = (Float3D)obj;

        float value = 1e38F;
        float scale = (float)(1.0/3.0);
        for (int j = 0; j < arr.length; ++j) {
            arr[j] = value;
            value *= scale;
        }

        try {
            String[] names = {"/tmp/testdata0.mda", "/tmp/testdata1.mda", "/tmp/testdata2.mda"};
            for (int j = 0; j < names.length; ++j) {
                String name = names[j];
                String order;
                if (j == 1) {
                    MdaFormat.save(obj, name, ByteOrder.BIG_ENDIAN);
                    order = "big endian";
                } else if (j == 2) {
                    MdaFormat.save(obj, name, ByteOrder.LITTLE_ENDIAN);
                    order = "little endian";
                } else {
                    MdaFormat.save(obj, name);
                    order = "native";
                }
                System.out.println("test file \"" + name + "\" written in " + order + " byte order");
                ShapedArray tmp = MdaFormat.load(name);
                System.out.println("test file \"" + name + "\" successfully read");
                Float3D cpy = (Float3D)tmp;
                int errors = 0;
                for (int i3 = 0; i3 < dim3; ++i3) {
                    for (int i2 = 0; i2 < dim2; ++i2) {
                        for (int i1 = 0; i1 < dim1; ++i1) {
                            if (cpy.get(i1, i2, i3) != orig.get(i1,i2,i3)) {
                                ++errors;
                                System.err.println("copy.get("+i1+","+i2+","+i3+") = "
                                        + cpy.get(i1, i2, i3) + " != " + orig.get(i1,i2,i3));
                            }
                        }
                    }
                }
                if (errors == 0) {
                    System.out.println("identical contents for test file \"" + name + "\"");
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
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
