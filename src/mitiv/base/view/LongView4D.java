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

package mitiv.base.view;

import mitiv.base.mapping.LongFunction;
import mitiv.base.mapping.LongScanner;
import mitiv.random.LongGenerator;

/**
 * This class implements 4D views of arrays of long's.
 * 
 * @see {@link View} for a general introduction to the concepts of <i>views</i>.
 * 
 * @author Éric Thiébaut.
 *
 */
public class LongView4D extends View4D implements LongView {
    private final long[] data;

    /**
     * Create a 4D view of an array of long's with zero offset, contiguous
     * elements and {@link #COLUMN_MAJOR} order.
     * @param data - The array to wrap in the view.
     * @param n1   - The 1st dimension of the view.
     * @param n2   - The 2nd dimension of the view.
     * @param n3   - The 3rd dimension of the view.
     * @param n4   - The 4th dimension of the view.
     */
    public LongView4D(long[] data, int n1, int n2, int n3, int n4) {
        super(data.length, n1, n2, n3, n4, 0, 1, n1, n1*n2, n1*n2*n3);
        this.data = data;
    }

    /**
     * Create a 4D view of an array of long's.
     * @param data - The array to wrap in the view.
     * @param n1   - The 1st dimension of the view.
     * @param n2   - The 2nd dimension of the view.
     * @param n3   - The 3rd dimension of the view.
     * @param n4   - The 4th dimension of the view.
     * @param s0   - The offset of element (0,0,0).
     * @param s1   - The stride along the 1st dimension.
     * @param s2   - The stride along the 2nd dimension.
     * @param s3   - The stride along the 3rd dimension.
     * @param s4   - The stride along the 4th dimension.
     */
    public LongView4D(long[] data, int n1, int n2, int n3, int n4,
            int s0, int s1, int s2, int s3, int s4) {
        super(data.length, n1, n2, n3, n4, s0, s1, s2, s3, s4);
        this.data = data;
    }

    /**
     * Query the array wrapped by the view.
     * @return The array wrapped by the view.
     */
    public final long[] getData() {
        return data;
    }

    /**
     * Query the value stored at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @return The value stored at position {@code (i1,i2,i3,i4)} of the view.
     */
    public final long get(int i1, int i2, int i3, int i4) {
        return data[index(i1, i2, i3, i4)];
    }

    /**
     * Set the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param value - The value to store at position {@code (i1,i2,i3,i4)} of the view.
     */
    public final void set(int i1, int i2, int i3, int i4, long value) {
        data[index(i1, i2, i3, i4)] = value;
    }

    /**
     * Set all the values of the view.
     * @param value - The value to set.
     */
    @Override
    public final void set(long value) {
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    for (int i3 = 0; i3 < n3; ++i3) {
                        for (int i4 = 0; i4 < n4; ++i4) {
                            data[index(i1, i2, i3, i4)] = value;
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < n4; ++i4) {
                for (int i3 = 0; i3 < n3; ++i3) {
                    for (int i2 = 0; i2 < n2; ++i2) {
                        for (int i1 = 0; i1 < n1; ++i1) {
                            data[index(i1, i2, i3, i4)] = value;
                        }
                    }
                }
            }
        }
    }

    /**
     * Set the values of the view with a generator.
     * @param generator - The generator to use.
     */
    @Override
    public final void set(LongGenerator generator) {
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    for (int i3 = 0; i3 < n3; ++i3) {
                        for (int i4 = 0; i4 < n4; ++i4) {
                            data[index(i1, i2, i3, i4)] = generator.nextLong();
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < n4; ++i4) {
                for (int i3 = 0; i3 < n3; ++i3) {
                    for (int i2 = 0; i2 < n2; ++i2) {
                        for (int i1 = 0; i1 < n1; ++i1) {
                            data[index(i1, i2, i3, i4)] = generator.nextLong();
                        }
                    }
                }
            }
        }
    }

    /**
     * Increment the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param value - The value to add to the value stored at position
     *                {@code (i1,i2,i3,i4)} of the view.
     */
    public final void incr(int i1, int i2, int i3, int i4, long value) {
        data[index(i1, i2, i3, i4)] += value;
    }

    /**
     * Increment all the values of the view.
     * @param value - The increment.
     */
    @Override
    public final void incr(long value) {
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    for (int i3 = 0; i3 < n3; ++i3) {
                        for (int i4 = 0; i4 < n4; ++i4) {
                            data[index(i1, i2, i3, i4)] += value;
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < n4; ++i4) {
                for (int i3 = 0; i3 < n3; ++i3) {
                    for (int i2 = 0; i2 < n2; ++i2) {
                        for (int i1 = 0; i1 < n1; ++i1) {
                            data[index(i1, i2, i3, i4)] += value;
                        }
                    }
                }
            }
        }
    }

    /**
     * Decrement the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param value - The value to subtract to the value stored at position
     *                {@code (i1,i2,i3,i4)} of the view.
     */
    public final void decr(int i1, int i2, int i3, int i4, long value) {
        data[index(i1, i2, i3, i4)] -= value;
    }

    /**
     * Decrement all the values of the view.
     * @param value - The decrement.
     */
    @Override
    public final void decr(long value) {
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    for (int i3 = 0; i3 < n3; ++i3) {
                        for (int i4 = 0; i4 < n4; ++i4) {
                            data[index(i1, i2, i3, i4)] -= value;
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < n4; ++i4) {
                for (int i3 = 0; i3 < n3; ++i3) {
                    for (int i2 = 0; i2 < n2; ++i2) {
                        for (int i1 = 0; i1 < n1; ++i1) {
                            data[index(i1, i2, i3, i4)] -= value;
                        }
                    }
                }
            }
        }
    }

    /**
     * Multiply the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param value - The value by which to scale the value at position
     *                {@code (i1,i2,i3,i4)} of the view.
     */
    public final void mult(int i1, int i2, int i3, int i4, long value) {
        data[index(i1, i2, i3, i4)] *= value;
    }

    /**
     * Multiply all the values of the view.
     * @param value - The multiplier.
     */
    @Override
    public final void mult(long value) {
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    for (int i3 = 0; i3 < n3; ++i3) {
                        for (int i4 = 0; i4 < n4; ++i4) {
                            data[index(i1, i2, i3, i4)] *= value;
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < n4; ++i4) {
                for (int i3 = 0; i3 < n3; ++i3) {
                    for (int i2 = 0; i2 < n2; ++i2) {
                        for (int i1 = 0; i1 < n1; ++i1) {
                            data[index(i1, i2, i3, i4)] *= value;
                        }
                    }
                }
            }
        }
    }

    /**
     * Map the value at a given position of the view by a function.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param i3 - The index along the 3rd dimension.
     * @param i4 - The index along the 4th dimension.
     * @param func - The function to apply.
     */
    public final void map(int i1, int i2, int i3, int i4, LongFunction func) {
        int k = index(i1, i2, i3, i4);
        data[k] = func.apply(data[k]);
    }

    /**
     * Map all the values of the view by a function.
     * @param func - The function to apply.
     */
    @Override
    public final void map(LongFunction func) {
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    for (int i3 = 0; i3 < n3; ++i3) {
                        for (int i4 = 0; i4 < n4; ++i4) {
                            int k = index(i1, i2, i3, i4);
                            data[k] = func.apply(data[k]);
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < n4; ++i4) {
                for (int i3 = 0; i3 < n3; ++i3) {
                    for (int i2 = 0; i2 < n2; ++i2) {
                        for (int i1 = 0; i1 < n1; ++i1) {
                            int k = index(i1, i2, i3, i4);
                            data[k] = func.apply(data[k]);
                        }
                    }
                }
            }
        }
    }

    /**
     * Scan the values of the view.
     * @param scanner - The scanner to use.
     */
    @Override
    public final void scan(LongScanner scanner) {
        boolean skip = true;
        scanner.initialize(get(0, 0, 0, 0));
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    for (int i3 = 0; i3 < n3; ++i3) {
                        for (int i4 = 0; i4 < n4; ++i4) {
                            if (skip) {
                                skip = false;
                            } else {
                                scanner.update(get(i1, i2, i3, i4));
                            }
                        }
                    }
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i4 = 0; i4 < n4; ++i4) {
                for (int i3 = 0; i3 < n3; ++i3) {
                    for (int i2 = 0; i2 < n2; ++i2) {
                        for (int i1 = 0; i1 < n1; ++i1) {
                            if (skip) {
                                skip = false;
                            } else {
                                scanner.update(get(i1, i2, i3, i4));
                            }
                        }
                    }
                }
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
