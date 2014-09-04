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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Class for efficient writing of binary data with any byte ordering.
 * 
 * @see {@link #BufferedInputData}, {@link #InternalBuffer}.
 *
 * @author Éric Thiébaut
 */
public class BufferedOutputDataStream extends InternalBuffer {
    private final WritableByteChannel channel;
    private boolean writable;

    /**
     * Create a BufferedOutputDataStream with given buffer size.
     * <p>
     * Do not forget to call the {@link #close()} method when no longer in
     * use or you will have leakage.
     * 
     * @param file     - The output stream.
     * @param capacity - The size of the internal buffer (in bytes).  In any
     *                   case, the buffer size will be at least {@link #BUFSIZ}.
     */
    BufferedOutputDataStream(FileOutputStream file, int capacity) {
        /* Allocate resources and make sure buffer is empty and in write mode.. */
        super(capacity);
        buffer.clear();
        channel = file.getChannel();
        writable = true;
    }

    /**
     * Create a BufferedOutputDataStream with minimal buffer size.
     * <p>
     * Do not forget to call the {@link #close()} method when no longer in
     * use or you will have leakage.
     * 
     * @param file - The output stream.
     */
    BufferedOutputDataStream(FileOutputStream file) {
        this(file, 0);
    }

    /**
     * Close the channel.
     * <p>
     * Close the output channel of the stream after flushing any
     * remaining unwritten data.
     * @throws IOException
     */
    public void close() throws IOException {
        flush();
        channel.close();
        writable = false;
    }

    /**
     * Write as much data as possible.
     * @return The number of bytes not yet written.
     * @throws IOException
     */
    public void flush() throws IOException {
        if (writable && buffer.position() > 0) {
            /* Switch the internal buffer to read mode. */
            buffer.flip();
            try {
                /* Write as many bytes as possible. (FIXME: assume blocking mode). */
                do {
                    channel.write(buffer);
                } while (buffer.hasRemaining());
            } catch (Exception ex) {
                /* Make sure to switch the internal buffer back to read mode before
                 * re-throwing the exception. */
                buffer.compact();
                throw ex;
            }

            /* Switch the internal buffer back to read mode. */
            buffer.compact();
        }
    }

    /**
     * Write a single byte value.
     * @throws IOException
     */
    public void writeByte(byte value) throws IOException {
        if (buffer.remaining() < 1) {
            flush();
        }
        buffer.put(value);
    }

    /**
     * Write a single short value.
     * @throws IOException
     */
    public void writeShort(short value) throws IOException {
        if (buffer.remaining() < 2) {
            flush();
        }
        buffer.putShort(value);
    }

    /**
     * Write a single int value.
     * @throws IOException
     */
    public void writeInt(int value) throws IOException {
        if (buffer.remaining() < 4) {
            flush();
        }
        buffer.putInt(value);
    }

    /**
     * Write a single long value.
     * @throws IOException
     */
    public void writeLong(long value) throws IOException {
        if (buffer.remaining() < 8) {
            flush();
        }
        buffer.putLong(value);
    }

    /**
     * Write a single float value.
     * @throws IOException
     */
    public void writeFloat(float value) throws IOException {
        if (buffer.remaining() < 4) {
            flush();
        }
        buffer.putFloat(value);
    }

    /**
     * Write a single double value.
     * @throws IOException
     */
    public void writeDouble(double value) throws IOException {
        if (buffer.remaining() < 8) {
            flush();
        }
        buffer.putDouble(value);
    }

    /**
     * Write some bytes to the data stream.
     * <p>
     * Note that the stream being buffered, the written data may be
     * partially in the buffer.  To force writing all remaining unwritten
     * data, call the {@link #flush()} method.
     * 
     * @param arr     - The source array.
     * @param offset  - The first index to consider in the source array.
     * @param number  - The number of elements to write.
     * @return The number of elements actually written.
     * @throws IOException
     */
    public int write(byte[] arr, int offset, int number) throws IOException {
        if (offset < 0 || number <= 0) {
            return 0;
        }
        if (buffer.remaining() < 1) {
            flush();
        }
        int start = offset;
        while (true) {
            int chunk = Math.min(number, buffer.remaining());
            buffer.put(arr, offset, chunk);
            offset += chunk;
            number -= chunk;
            if (number <= 0) {
                break;
            }
            flush();
        }
        /* Return the number of transferred values. */
        return (offset - start);
    }

    /**
     * Write some short's to the data stream.
     * <p>
     * Byte swapping if performed according to the current byte order of
     * the data stream.
     * <p>
     * Note that the stream being buffered, the written data may be
     * partially in the buffer.  To force writing all remaining unwritten
     * data, call the {@link #flush()} method.
     *
     * @param arr     - The source array.
     * @param offset  - The first index to consider in the source array.
     * @param number  - The number of elements to write.
     * @return The number of elements actually written.
     * @throws IOException
     */
    public int write(short[] arr, int offset, int number) throws IOException {
        if (offset < 0 || number <= 0) {
            return 0;
        }
        final int elemSize = Short.SIZE/8;
        if (buffer.remaining() < elemSize) {
            flush();
        }
        int start = offset;
        while (true) {
            ShortBuffer buf = buffer.asShortBuffer();
            int chunk = Math.min(number, buf.remaining());
            buf.put(arr, offset, chunk);
            buffer.position(buffer.position() + elemSize*chunk);
            offset += chunk;
            number -= chunk;
            if (number <= 0) {
                break;
            }
            flush();
        }
        /* Return the number of transferred values. */
        return (offset - start);
    }

    /**
     * Write some int's to the data stream.
     * <p>
     * Byte swapping if performed according to the current byte order of
     * the data stream.
     * <p>
     * Note that the stream being buffered, the written data may be
     * partially in the buffer.  To force writing all remaining unwritten
     * data, call the {@link #flush()} method.
     * 
     * @param arr     - The source array.
     * @param offset  - The first index to consider in the source array.
     * @param number  - The number of elements to write.
     * @return The number of elements actually written.
     * @throws IOException
     */
    public int write(int[] arr, int offset, int number) throws IOException {
        if (offset < 0 || number <= 0) {
            return 0;
        }
        final int elemSize = Integer.SIZE/8;
        if (buffer.remaining() < elemSize) {
            flush();
        }
        int start = offset;
        while (true) {
            IntBuffer buf = buffer.asIntBuffer();
            int chunk = Math.min(number, buf.remaining());
            buf.put(arr, offset, chunk);
            buffer.position(buffer.position() + elemSize*chunk);
            offset += chunk;
            number -= chunk;
            if (number <= 0) {
                break;
            }
            flush();
        }
        /* Return the number of transferred values. */
        return (offset - start);
    }

    /**
     * Write some long's to the data stream.
     * <p>
     * Byte swapping if performed according to the current byte order of
     * the data stream.
     * <p>
     * Note that the stream being buffered, the written data may be
     * partially in the buffer.  To force writing all remaining unwritten
     * data, call the {@link #flush()} method.
     * 
     * @param arr     - The source array.
     * @param offset  - The first index to consider in the source array.
     * @param number  - The number of elements to write.
     * @return The number of elements actually written.
     * @throws IOException
     */
    public int write(long[] arr, int offset, int number) throws IOException {
        if (offset < 0 || number <= 0) {
            return 0;
        }
        final int elemSize = Long.SIZE/8;
        if (buffer.remaining() < elemSize) {
            flush();
        }
        int start = offset;
        while (true) {
            LongBuffer buf = buffer.asLongBuffer();
            int chunk = Math.min(number, buf.remaining());
            buf.put(arr, offset, chunk);
            buffer.position(buffer.position() + elemSize*chunk);
            offset += chunk;
            number -= chunk;
            if (number <= 0) {
                break;
            }
            flush();
        }
        /* Return the number of transferred values. */
        return (offset - start);
    }

    /**
     * Write some float's to the data stream.
     * <p>
     * Byte swapping if performed according to the current byte order of
     * the data stream.
     * <p>
     * Note that the stream being buffered, the written data may be
     * partially in the buffer.  To force writing all remaining unwritten
     * data, call the {@link #flush()} method.
     * 
     * @param arr     - The source array.
     * @param offset  - The first index to consider in the source array.
     * @param number  - The number of elements to write.
     * @return The number of elements actually written.
     * @throws IOException
     */
    public int write(float[] arr, int offset, int number) throws IOException {
        if (offset < 0 || number <= 0) {
            return 0;
        }
        final int elemSize = Float.SIZE/8;
        if (buffer.remaining() < elemSize) {
            flush();
        }
        int start = offset;
        while (true) {
            FloatBuffer buf = buffer.asFloatBuffer();
            int chunk = Math.min(number, buf.remaining());
            buf.put(arr, offset, chunk);
            buffer.position(buffer.position() + elemSize*chunk);
            offset += chunk;
            number -= chunk;
            if (number <= 0) {
                break;
            }
            flush();
        }
        /* Return the number of transferred values. */
        return (offset - start);
    }

    /**
     * Write some double's to the data stream.
     * <p>
     * Byte swapping if performed according to the current byte order of
     * the data stream.
     * <p>
     * Note that the stream being buffered, the written data may be
     * partially in the buffer.  To force writing all remaining unwritten
     * data, call the {@link #flush()} method.
     * 
     * @param arr     - The source array.
     * @param offset  - The first index to consider in the source array.
     * @param number  - The number of elements to write.
     * @return The number of elements actually written.
     * @throws IOException
     */
    public int write(double[] arr, int offset, int number) throws IOException {
        if (offset < 0 || number <= 0) {
            return 0;
        }
        final int elemSize = Double.SIZE/8;
        if (buffer.remaining() < elemSize) {
            flush();
        }
        int start = offset;
        while (true) {
            DoubleBuffer buf = buffer.asDoubleBuffer();
            int chunk = Math.min(number, buf.remaining());
            buf.put(arr, offset, chunk);
            buffer.position(buffer.position() + elemSize*chunk);
            offset += chunk;
            number -= chunk;
            if (number <= 0) {
                break;
            }
            flush();
        }
        /* Return the number of transferred values. */
        return (offset - start);
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
