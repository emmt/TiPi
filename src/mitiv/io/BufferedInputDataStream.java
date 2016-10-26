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

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * Class for efficient reading of binary data with any byte ordering.
 *
 * @see BufferedOutputDataStream
 * @see InternalBuffer
 *
 * @author Éric Thiébaut
 */
public class BufferedInputDataStream extends InternalBuffer implements Closeable {
    private final ReadableByteChannel channel;
    private boolean readable;

    /**
     * Create a BufferedInputDataStream with given buffer size.
     *
     * <p> Do not forget to call the {@link #close()} method when no longer in
     * use or you will have leakage. </p>
     *
     * @param file
     *        The input stream.
     *
     * @param capacity
     *        The size of the internal buffer (in bytes).  In any case, the
     *        buffer size will be at least {@link #BUFSIZ}.
     */
    BufferedInputDataStream(FileInputStream file, int capacity) {
        /* Allocate resources and make sure the internal buffer is empty and in read mode. */
        super(capacity);
        buffer.clear().flip();
        channel = file.getChannel();
        readable = true;
    }

    /**
     * Create a BufferedInputDataStream with minimal buffer size.
     *
     * <p> Do not forget to call the {@link #close()} method when no longer in
     * use or you will have leakage. </p>
     *
     * @param file
     *        The input stream.
     */
    BufferedInputDataStream(FileInputStream file) {
        this(file, 0);
    }

    /** Close the channel.
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        channel.close();
        readable = false;
    }

    /**
     * Make sure a minimum number of bytes are available in the internal buffer.
     *
     * @param size
     *        The required number of bytes.
     *
     * @return The number of remaining bytes which cannot exceed the capacity of
     *         the internal buffer, nor the remaining data from the stream.
     *
     * @throws IOException
     */
    public int insure(int size) throws IOException {
        int remaining = buffer.remaining();
        if (remaining >= size) {
            return remaining;
        } else {
            return fill();
        }
    }

    /**
     * Read as much data as possible.
     *
     * @return The number of available bytes for reading.
     *
     * @throws IOException
     */
    public int fill() throws IOException {
        if (readable) {
            try {
                /* Switch the internal buffer to write mode, preserving unread bytes if any. */
                buffer.compact();
                if (buffer.remaining() > 0) {
                    /* Read as many bytes as possible. (FIXME: blocking mode is assumed.) */
                    channel.read(buffer);
                }
            } catch (IOException ex) {
                throw ex;
            } finally {
                /* Switch the internal buffer back to read mode. */
                buffer.flip();
            }
        }
        /* Return the number of bytes available for reading. */
        return buffer.remaining();
    }

    /**
     * Read a single byte value.
     *
     * @return The next byte value from the stream.
     *
     * @throws IOException not enough data available.
     */
    public byte readByte() throws IOException {
        if (insure(1) < 1) {
            throw new IOException("end of stream");
        }
        return buffer.get();
    }

    /**
     * Read a single short value.
     *
     * @return The next short value from the stream.
     *
     * @throws IOException not enough data available.
     */
    public short readShort() throws IOException {
        if (insure(2) < 2) {
            throw new IOException("end of stream");
        }
        return buffer.getShort();
    }

    /**
     * Read a single int value.
     *
     * @return The next int value from the stream.
     *
     * @throws IOException not enough data available.
     */
    public int readInt() throws IOException {
        if (insure(4) < 4) {
            throw new IOException("end of stream");
        }
        return buffer.getInt();
    }

    /**
     * Read a single long value.
     *
     * @return The next long value from the stream.
     *
     * @throws IOException not enough data available.
     */
    public long readLong() throws IOException {
        if (insure(8) < 8) {
            throw new IOException("end of stream");
        }
        return buffer.getLong();
    }

    /**
     * Read a single float value.
     *
     * @return The next float value from the stream.
     *
     * @throws IOException not enough data available.
     */
    public float readFloat() throws IOException {
        if (insure(4) < 4) {
            throw new IOException("end of stream");
        }
        return buffer.getFloat();
    }

    /**
     * Read a single double value.
     *
     * @return The next double value from the stream.
     *
     * @throws IOException not enough data available.
     */
    public double readDouble() throws IOException {
        if (insure(8) < 8) {
            throw new IOException("end of stream");
        }
        return buffer.getDouble();
    }

    /**
     * Read some bytes from the data stream.
     *
     * @param arr
     *        The destination array.
     *
     * @param offset
     *        The first index to store data in the destination array.
     *
     * @param number
     *        The number of elements to read.
     *
     * @return The number of elements actually read.
     *
     * @throws IOException
     */
    public int read(byte[] arr, int offset, int number) throws IOException {
        if (offset < 0 || number <= 0 || insure(1) < 1) {
            return 0;
        }
        int start = offset;
        do {
            int chunk = Math.min(number, buffer.remaining());
            buffer.get(arr, offset, chunk);
            offset += chunk;
            number -= chunk;
        } while (number > 0 && fill() >= 1);
        return offset - start;
    }

    /**
     * Read some short's from the data stream.
     *
     * <p> Byte swapping is performed according to the current byte order of
     * the data stream. </p>
     *
     * @param arr
     *        The destination array.
     *
     * @param offset
     *        The first index to store data in the destination array.
     *
     * @param number
     *        The number of elements to read.
     *
     * @return The number of elements actually read.
     *
     * @throws IOException
     */
    public int read(short[] arr, int offset, int number) throws IOException {
        final int elemSize = Short.SIZE/8;
        if (offset < 0 || number <= 0 || insure(elemSize) < elemSize) {
            return 0;
        }
        int start = offset;
        do {
            ShortBuffer buf = buffer.asShortBuffer();
            int chunk = Math.min(number, buf.remaining());
            buf.get(arr, offset, chunk);
            buffer.position(buffer.position() + elemSize*chunk);
            offset += chunk;
            number -= chunk;
        } while (number > 0 && fill() >= elemSize);
        return offset - start;
    }

    /**
     * Read some int's from the data stream.
     *
     * <p> Byte swapping is performed according to the current byte order of
     * the data stream. </p>
     *
     * @param arr
     *        The destination array.
     *
     * @param offset
     *        The first index to store data in the destination array.
     *
     * @param number
     *        The number of elements to read.
     *
     * @return The number of elements actually read.
     *
     * @throws IOException
     */
    public int read(int[] arr, int offset, int number) throws IOException {
        final int elemSize = Integer.SIZE/8;
        if (offset < 0 || number <= 0 || insure(elemSize) < elemSize) {
            return 0;
        }
        int start = offset;
        do {
            IntBuffer buf = buffer.asIntBuffer();
            int chunk = Math.min(number, buf.remaining());
            buf.get(arr, offset, chunk);
            buffer.position(buffer.position() + elemSize*chunk);
            offset += chunk;
            number -= chunk;
        } while (number > 0 && fill() >= elemSize);
        return offset - start;
    }

    /**
     * Read some long's from the data stream.
     *
     * <p> Byte swapping is performed according to the current byte order of
     * the data stream. </p>
     *
     * @param arr
     *        The destination array.
     *
     * @param offset
     *        The first index to store data in the destination array.
     *
     * @param number
     *        The number of elements to read.
     *
     * @return The number of elements actually read.
     *
     * @throws IOException
     */
    public int read(long[] arr, int offset, int number) throws IOException {
        final int elemSize = Long.SIZE/8;
        if (offset < 0 || number <= 0 || insure(elemSize) < elemSize) {
            return 0;
        }
        int start = offset;
        do {
            LongBuffer buf = buffer.asLongBuffer();
            int chunk = Math.min(number, buf.remaining());
            buf.get(arr, offset, chunk);
            buffer.position(buffer.position() + elemSize*chunk);
            offset += chunk;
            number -= chunk;
        } while (number > 0 && fill() >= elemSize);
        return offset - start;
    }

    /**
     * Read some float's from the data stream.
     *
     * <p> Byte swapping is performed according to the current byte order of
     * the data stream. </p>
     *
     * @param arr
     *        The destination array.
     *
     * @param offset
     *        The first index to store data in the destination array.
     *
     * @param number
     *        The number of elements to read.
     *
     * @return The number of elements actually read.
     *
     * @throws IOException
     */
    public int read(float[] arr, int offset, int number) throws IOException {
        final int elemSize = Float.SIZE/8;
        if (offset < 0 || number <= 0 || insure(elemSize) < elemSize) {
            return 0;
        }
        int start = offset;
        do {
            FloatBuffer buf = buffer.asFloatBuffer();
            int chunk = Math.min(number, buf.remaining());
            buf.get(arr, offset, chunk);
            buffer.position(buffer.position() + elemSize*chunk);
            offset += chunk;
            number -= chunk;
        } while (number > 0 && fill() >= elemSize);
        return offset - start;
    }

    /**
     * Read some double's from the data stream.
     *
     * <p>
     * Byte swapping if performed according to the current byte order of
     * the data stream.
     *
     * @param arr
     *        The destination array.
     *
     * @param offset
     *        The first index to store data in the destination array.
     *
     * @param number
     *        The number of elements to read.
     *
     * @return The number of elements actually read.
     *
     * @throws IOException
     */
    public int read(double[] arr, int offset, int number) throws IOException {
        final int elemSize = Double.SIZE/8;
        if (offset < 0 || number <= 0 || insure(elemSize) < elemSize) {
            return 0;
        }
        int start = offset;
        do {
            DoubleBuffer buf = buffer.asDoubleBuffer();
            int chunk = Math.min(number, buf.remaining());
            buf.get(arr, offset, chunk);
            buffer.position(buffer.position() + elemSize*chunk);
            offset += chunk;
            number -= chunk;
        } while (number > 0 && fill() >= elemSize);
        return offset - start;
    }

    /*
    private static void debug(String name, Buffer buf) {
        System.err.println(name + "{ capacity = " + buf.capacity() +
                ", position = " + buf.position() + ", limit = " + buf.limit() + "}");
    }
     */
}
