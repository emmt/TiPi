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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Class for data input/output buffer.
 *
 * <p> An InternalBuffer is a small {@link ByteBuffer} designed for efficient
 * input/output of binary data. </p>
 *
 * @see ByteBuffer
 * @see BufferedInputDataStream
 * @see BufferedOutputDataStream
 *
 * @author Éric Thiébaut
 */
public class InternalBuffer {
    /* For an usage of ByteBuffers, see:
     *  - http://tutorials.jenkov.com/java-nio/buffers.html
     *  - http://www.javamex.com/tutorials/io/nio_buffer_performance.shtml
     *  - http://www.ntu.edu.sg/home/ehchua/programming/java/J5b_IO_advanced.html
     */
    protected final ByteBuffer buffer;

    /** Default and minimum size (16 kb) of the buffer used to transfer data. */
    public static final int BUFSIZ = 16*1024;

    /**
     * Create a buffer with given size.
     *
     * @param capacity
     *        The size of the internal buffer (in bytes).  In any case, the
     *        buffer size will be at least {@link #BUFSIZ}.
     */
    InternalBuffer(int capacity) {
        buffer = ByteBuffer.allocateDirect(Math.max(capacity,  BUFSIZ));
    }

    /**
     * Create a buffer with minimal size.
     */
    InternalBuffer() {
        this(0);
    }

    /**
     * Get byte order of the internal buffer.
     *
     * @return The current byte order of the internal buffer.
     */
    public final ByteOrder getByteOrder() {
        return buffer.order();
    }

    /**
     * Set the byte order of the internal buffer.
     *
     * @param order
     *        The chosen byte order.
     */
    public final void setByteOrder(ByteOrder order) {
        buffer.order(order);
    }

    /**
     * Query the number of bytes left in the internal buffer.
     *
     * @return The number of remaining bytes.
     */
    public final int remaining() {
        return buffer.remaining();
    }

    /**
     * Sets the mark of the internal buffer at its current position.
     *
     * <p> Note that the buffer capacity sets the maximum number of bytes that
     * can be preserved in the data stream between a mark() and a reset().
     * Make sure to fill()/flush() the stream to have the maximum
     * capacity. </p>
     */
    public final void mark() {
        buffer.mark();
    }

    /**
     * Resets the position of the internal buffer to the previously-marked
     * position.
     */
    public final void reset() {
        buffer.reset();
    }

    /*
    private static void debug(String name, Buffer buf) {
        System.err.println(name + "{ capacity = " + buf.capacity() +
                ", position = " + buf.position() + ", limit = " + buf.limit() + "}");
    }
     */

}
