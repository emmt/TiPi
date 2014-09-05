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

package mitiv.array;

import mitiv.random.MersenneTwisterGenerator;
import mitiv.random.NormalDistribution;

public class TestArray {
    public static void main(String[] args) {
        final int n1 = 401;
        final int n2 = 302;
        final int n3 = 203;
        final int number = n1*n2*n3;
        final int repeat = 20;
        //final int n1n2 = n1*n2;
        float[] arr = new float[n1*n2*n3];
        //float[][] rect = new float[n3][n2*n1];
        float[][][] cube = new float[n3][n2][n1];
        Float3D a = Float3D.wrap(arr, n1, n2, n3);
        //FloatArray3D b = FloatArray3D.wrap(rect, n1, n2);
        Float3D c = Float3D.wrap(cube);
        NormalDistribution generator = new NormalDistribution(new MersenneTwisterGenerator(7864), -1.0, 1.0);
        long t0, t1;
        generator.nextFloat(); // initialize internal buffers

        System.out.println("dimensions: " + n1 + "x" + n2 + "x" + n3);
        System.out.println("number of elements: " + number);

        for (int loop = 1; loop <= repeat; ++loop) {

            System.out.println();
            System.out.println("loop #" + loop);

            t0 = System.currentTimeMillis();
            for (int i = 0; i < number; ++i) {
                arr[i] = i;
            }
            t1 = System.currentTimeMillis();
            System.out.println("direct filling of flat array: " + (t1 - t0) + " ms");

            /*
            t0 = System.currentTimeMillis();
            for (int i3 = 0; i3 < n3; ++i3) {
                for (int k = 0; k < n1n2; ++k) {
                    rect[i3][k] = -1.0F;
                }
            }
            t1 = System.currentTimeMillis();
            System.out.println("direct filling of rect array: " + (t1 - t0) + " ms");
             */
            t0 = System.currentTimeMillis();
            for (int i3 = 0; i3 < n3; ++i3) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    for (int i1 = 0; i1 < n1; ++i1) {
                        cube[i3][i2][i1] = i1 + n1*(i2 + n2*i3);
                    }
                }
            }
            t1 = System.currentTimeMillis();
            System.out.println("direct filling of cube array: " + (t1 - t0) + " ms");
            System.out.println("some values: " + cube[3][2][1] + " / " + c.get(1, 2, 3));
            System.out.println("some values: " + cube[5][4][3] + " / " + c.get(3, 4, 5));

            t0 = System.currentTimeMillis();
            for (int i1 = 0; i1 < n1; ++i1) {
                for (int i2 = 0; i2 < n2; ++i2) {
                    for (int i3 = 0; i3 < n3; ++i3) {
                        cube[i3][i2][i1] = i1 + n1*(i2 + n2*i3);
                    }
                }
            }
            t1 = System.currentTimeMillis();
            System.out.println("direct filling of cube array in wrong order: " + (t1 - t0) + " ms");

            t0 = System.currentTimeMillis();
            a.fill(1.0F);
            t1 = System.currentTimeMillis();
            System.out.println("filling of flat array: " + (t1 - t0) + " ms");

            /*
            t0 = System.currentTimeMillis();
            b.set(1.0F);
            t1 = System.currentTimeMillis();
            System.out.println("filling of rect array: " + (t1 - t0) + " ms");
             */

            t0 = System.currentTimeMillis();
            c.fill(1.0F);
            t1 = System.currentTimeMillis();
            System.out.println("filling of cube array: " + (t1 - t0) + " ms");
            System.out.println("some values: " + c.get(1, 2, 3) + ", " + c.get(3, 4, 5));

            t0 = System.currentTimeMillis();
            a.fill(generator);
            t1 = System.currentTimeMillis();
            System.out.println("random fill of flat array: " + (t1 - t0) + " ms");

            t0 = System.currentTimeMillis();
            c.fill(generator);
            t1 = System.currentTimeMillis();
            System.out.println("random fill of cube array: " + (t1 - t0) + " ms");
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
