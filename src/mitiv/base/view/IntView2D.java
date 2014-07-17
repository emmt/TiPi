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

import mitiv.base.mapping.IntFunction;
import mitiv.base.mapping.IntScanner;
import mitiv.random.IntGenerator;

/**
 * This class implements 2D views of arrays of int's.
 * 
 * @see {@link View} for a general introduction to the concepts of <i>views</i>.
 * 
 * @author Éric Thiébaut.
 *
 */
public class IntView2D extends View2D implements IntView {
    private final int[] data;

    /**
     * Create a 2D view of an array of int's with zero offset, contiguous
     * elements and {@link #COLUMN_MAJOR} order.
     * @param data - The array to wrap in the view.
     * @param n1   - The 1st dimension of the view.
     * @param n2   - The 2nd dimension of the view.
     */
    public IntView2D(int[] data, int n1, int n2) {
        super(data.length, n1, n2, 0, 1, n1);
        this.data = data;
    }

    /**
     * Create a 2D view of an array of int's.
     * @param data - The array to wrap in the view.
     * @param n1   - The 1st dimension of the view.
     * @param n2   - The 2nd dimension of the view.
     * @param s0   - The offset of element (0,0,0).
     * @param s1   - The stride along the 1st dimension.
     * @param s2   - The stride along the 2nd dimension.
     */
    public IntView2D(int[] data, int n1, int n2, int s0, int s1, int s2) {
        super(data.length, n1, n2, s0, s1, s2);
        this.data = data;
    }

    /**
     * Query the array wrapped by the view.
     * @return The array wrapped by the view.
     */
    public final int[] getData() {
        return data;
    }

    /**
     * Query the value stored at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @return The value stored at position {@code (i1,i2)} of the view.
     */
    public final int get(int i1, int i2) {
        return data[index(i1, i2)];
    }

    /**
     * Set the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param value - The value to store at position {@code (i1,i2,i3)} of the view.
     */
    public final void set(int i1, int i2, int value) {
        data[index(i1, i2)] = value;
    }

    /**
     * Set all the values of the view.
     * @param value - The value to set.
     */
    @Override
    public final void set(int value) {
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    data[index(i1, i2)] = value;
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < n2; ++i2) {
                for (int i1 = 0; i1 < n1; ++i1) {
                    data[index(i1, i2)] = value;
                }
            }
        }
    }

    /**
     * Set the values of the view with a generator.
     * @param generator - The generator to use.
     */
    @Override
    public final void set(IntGenerator generator) {
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    data[index(i1, i2)] = generator.nextInt();
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < n2; ++i2) {
                for (int i1 = 0; i1 < n1; ++i1) {
                    data[index(i1, i2)] = generator.nextInt();
                }
            }
        }
    }

    /**
     * Increment the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param value - The value to add to the value stored at position
     *                {@code (i1,i2)} of the view.
     */
    public final void incr(int i1, int i2, int value) {
        data[index(i1, i2)] += value;
    }

    /**
     * Increment all the values of the view.
     * @param value - The increment.
     */
    @Override
    public final void incr(int value) {
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    data[index(i1, i2)] += value;
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < n2; ++i2) {
                for (int i1 = 0; i1 < n1; ++i1) {
                    data[index(i1, i2)] += value;
                }
            }
        }
    }

    /**
     * Decrement the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param value - The value to subtract to the value stored at position
     *                {@code (i1,i2)} of the view.
     */
    public final void decr(int i1, int i2, int value) {
        data[index(i1, i2)] -= value;
    }

    /**
     * Decrement all the values of the view.
     * @param value - The decrement.
     */
    @Override
    public final void decr(int value) {
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    data[index(i1, i2)] -= value;
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < n2; ++i2) {
                for (int i1 = 0; i1 < n1; ++i1) {
                    data[index(i1, i2)] -= value;
                }
            }
        }
    }

    /**
     * Multiply the value at a given position of the view.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param value - The value by which to scale the value at position
     *                {@code (i1,i2)} of the view.
     */
    public final void mult(int i1, int i2, int value) {
        data[index(i1, i2)] *= value;
    }

    /**
     * Multiply all the values of the view.
     * @param value - The multiplier.
     */
    @Override
    public final void mult(int value) {
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    data[index(i1, i2)] *= value;
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < n2; ++i2) {
                for (int i1 = 0; i1 < n1; ++i1) {
                    data[index(i1, i2)] *= value;
                }
            }
        }
    }

    /**
     * Map the value at a given position of the view by a function.
     * @param i1 - The index along the 1st dimension.
     * @param i2 - The index along the 2nd dimension.
     * @param func - The function to apply.
     */
    public final void map(int i1, int i2, IntFunction func) {
        int k = index(i1, i2);
        data[k] = func.apply(data[k]);
    }

    /**
     * Map all the values of the view by a function.
     * @param func - The function to apply.
     */
    @Override
    public final void map(IntFunction func) {
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    int k = index(i1, i2);
                    data[k] = func.apply(data[k]);
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < n2; ++i2) {
                for (int i1 = 0; i1 < n1; ++i1) {
                    int k = index(i1, i2);
                    data[k] = func.apply(data[k]);
                }
            }
        }
    }

    /**
     * Scan the values of the view.
     * @param scanner - The scanner to use.
     */
    @Override
    public final void scan(IntScanner scanner) {
        scanner.initialize(get(0, 0));
        if (order == ROW_MAJOR) {
            /* Scan elements in row-major order. */
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = (i1 == 0 ? 1 : 0); i2 < n2; ++i2) {
                    scanner.update(get(i1, i2));
                }
            }
        } else {
            /* Assume column-major order. */
            for (int i2 = 0; i2 < n2; ++i2) {
                for (int i1 = (i2 == 0 ? 1 : 0); i1 < n1; ++i1) {
                    scanner.update(get(i1, i2));
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
