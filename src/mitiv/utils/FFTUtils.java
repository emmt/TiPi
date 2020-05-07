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

package mitiv.utils;

import java.util.Arrays;

import mitiv.array.ArrayFactory;
import mitiv.array.Double1D;
import mitiv.array.Double2D;
import mitiv.array.Double3D;
import mitiv.array.Double4D;
import mitiv.array.Double5D;
import mitiv.array.Double6D;
import mitiv.array.Double7D;
import mitiv.array.Double8D;
import mitiv.array.Double9D;
import mitiv.array.DoubleArray;
import mitiv.array.Int1D;
import mitiv.array.Int3D;
import mitiv.array.Int4D;
import mitiv.array.Int5D;
import mitiv.array.Int6D;
import mitiv.array.Int7D;
import mitiv.array.Int8D;
import mitiv.array.Int9D;
import mitiv.array.IntArray;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.base.mapping.DoubleFunction;

public class FFTUtils {
    /**
     * This class is not instantiable, it only provides static methods.
     */
    protected FFTUtils() {}

    /**
     * Compute the best dimension for the FFT.
     * @param dim - The minimal length.
     * @return The smallest integer which is greater or equal {@code len}
     * and which is a multiple of powers of 2.
     */
    public static int bestPowerOfTwo(int dim) {
        final int maxDim = (1 << 30);
        if (dim > maxDim) {
            throw new IllegalArgumentException("Integer overflow");
        }
        int best = 1;
        while (best < dim) {
            best *= 2;
        }
        return best;
    }

    /**
     * Compute the best dimension for the FFT.
     * @param dim - The minimal length.
     * @return The smallest integer which is greater or equal {@code len}
     * and which is a multiple of powers of 2, 3 and/or 5.
     */
    public static int bestDimension(int dim) {
        int best = 2*dim;
        for (int n5 = 1; n5 < best; n5 *= 5) {
            for (int n3 = n5; n3 < best; n3 *= 3) {
                /* innermost loop (power of 2) is exited as soon as N2 >= LEN */
                int n2 = n3;
                while (n2 < dim) {
                    n2 *= 2;
                }
                if (n2 == dim) {
                    return dim;
                }
                if (best > n2) {
                    best = n2;
                }
            }
        }
        return best;
    }

    /**
     * Generate discrete Fourier transform frequencies.
     * @param dim - The number of discrete frequencies.
     * @return An array of {@code dim} integers: {0,1,2,...,-2,-1}
     */
    public static Int1D generateFrequels(int dim) {
        int[] freq = new int[dim];
        int cut = dim/2;
        for (int i = 0; i <= cut; ++i) {
            freq[i] = i;
        }
        for (int i = cut + 1; i < dim; ++i) {
            freq[i] = i - dim;
        }
        return ArrayFactory.wrap(freq,dim);
    }


    /**
     * Generate discrete Fourier transform frequencies.
     * @param dim - The number of discrete frequencies.
     * @return An array of {@code dim} integers: {0,1,2,...,-2,-1}
     */
    public static Double1D generateFrequels(int dim,boolean sc) {
        double[] freq = new double[dim];
        int cut = dim/2;
        double factor=1;
        if(sc) {
            factor = 1./dim;
        }
        for (int i = 0; i <= cut; ++i) {
            freq[i] = factor*i;
        }
        for (int i = cut + 1; i < dim; ++i) {
            freq[i] = factor*(i - dim);
        }
        return ArrayFactory.wrap(freq,dim);
    }

    /**
     * Generate discrete Fourier transform squared frequencies.
     * @param dim - The number of discrete frequencies.
     * @return An array of {@code dim} integers: {0,1^2,2^2,...,-2^2,-1^2}
     */
    public static Int1D generateFrequels2(int dim) {
        int[] freq = new int[dim];
        int cut = dim/2;
        for (int i = 0; i <= cut; ++i) {
            freq[i] = i*i;
        }
        for (int i = cut + 1; i < dim; ++i) {
            freq[i] = (i - dim)*( i - dim);
        }
        return ArrayFactory.wrap(freq,dim);
    }
    /**
     * Generate discrete Fourier transform squared frequencies.
     * @param dim - The number of discrete frequencies.
     * @return An array of {@code dim} integers: {0,1^2,2^2,...,-2^2,-1^2}
     */
    public static Double1D generateFrequels2(int dim, boolean sc) {
        double[] freq = new double[dim];
        int cut = dim/2;
        double factor=1;
        if(sc) {
            factor = 1./dim/dim;
        }

        for (int i = 0; i <= cut; ++i) {
            freq[i] = factor*i*i;
        }
        for (int i = cut + 1; i < dim; ++i) {
            freq[i] = factor*(i - dim)*( i - dim);
        }
        return ArrayFactory.wrap(freq,dim);
    }

    /**
     * Compute squared length of FFT frequencies/coordinates.
     *
     * @param shp shape of the array
     * @return the squared distance of FFT frequencies/coordinates.
     */
    public static DoubleArray fftDist2(Shape shp ) {
        return fftDist2( shp, null);
    }

    /**
     * Compute squared length of FFT frequencies/coordinates.
     *
     * @param shp shape of the array
     * @param scale the scale of each dimension
     * @return the squared distance of FFT frequencies/coordinates.
     */
    public static DoubleArray fftDist2(Shape shp, double[] scale)
    {
        DoubleArray res = (DoubleArray) ArrayFactory.create(Traits.DOUBLE, shp);
        int rank = shp.rank();
        if (scale==null) {
            scale = new double[rank];
            Arrays.fill(scale, 1.0);
        }else if(rank != scale.length) {
            throw new IllegalArgumentException("Scale must have the same rank");
        }
        if (rank == 1) {
            res = generateFrequels2(shp.dimension(0)).toDouble();
            res.scale(scale[0]*scale[0]);
            return res;
        }

        Double1D[]  x = new Double1D[rank];
        for( int j = 0; j < rank; j++){
            x[j] = generateFrequels2(shp.dimension(j)).toDouble();
            x[j].scale(scale[j]*scale[j]);
        }

        switch (rank) {
            case 2:
                for(int n1=0; n1< shp.dimension(1);++n1) {
                    for(int n0=0; n0< shp.dimension(0);++n0) {
                        ((Double2D) res).set(n0,n1,
                                x[0].get(n0)+x[1].get(n1));
                    }
                }
                break;
            case 3:
                for(int n2=0; n2< shp.dimension(2);++n2) {
                    for(int n1=0; n1< shp.dimension(1);++n1) {
                        for(int n0=0; n0< shp.dimension(0);++n0) {
                            ((Double3D) res).set(n0,n1,n2,
                                    x[0].get(n0)+x[1].get(n1)
                                    +x[2].get(n2));
                        }
                    }
                }
                break;
            case 4:
                for(int n3=0; n3< shp.dimension(3);++n3) {
                    for(int n2=0; n2< shp.dimension(2);++n2) {
                        for(int n1=0; n1< shp.dimension(1);++n1) {
                            for(int n0=0; n0< shp.dimension(0);++n0) {
                                ((Double4D) res).set(n0,n1,n2,n3,
                                        x[0].get(n0)+x[1].get(n1)
                                        +x[2].get(n2)+x[3].get(n3));
                            }
                        }
                    }
                }
                break;
            case 5:
                for(int n4=0; n4< shp.dimension(4);++n4) {
                    for(int n3=0; n3< shp.dimension(3);++n3) {
                        for(int n2=0; n2< shp.dimension(2);++n2) {
                            for(int n1=0; n1< shp.dimension(1);++n1) {
                                for(int n0=0; n0< shp.dimension(0);++n0) {
                                    ((Double5D) res).set(n0,n1,n2,n3,n4,
                                            x[0].get(n0)+x[1].get(n1)
                                            +x[2].get(n2)+x[3].get(n3)
                                            +x[4].get(n4));
                                }
                            }
                        }
                    }
                }
                break;
            case 6:
                for(int n5=0; n5< shp.dimension(5);++n5) {
                    for(int n4=0; n4< shp.dimension(4);++n4) {
                        for(int n3=0; n3< shp.dimension(3);++n3) {
                            for(int n2=0; n2< shp.dimension(2);++n2) {
                                for(int n1=0; n1< shp.dimension(1);++n1) {
                                    for(int n0=0; n0< shp.dimension(0);++n0) {
                                        ((Double6D) res).set(n0,n1,n2,n3,n4,n5,
                                                x[0].get(n0)+x[1].get(n1)
                                                +x[2].get(n2)+x[3].get(n3)
                                                +x[4].get(n4)+x[5].get(n5));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case 7:
                for(int n6=0; n6< shp.dimension(6);++n6) {
                    for(int n5=0; n5< shp.dimension(5);++n5) {
                        for(int n4=0; n4< shp.dimension(4);++n4) {
                            for(int n3=0; n3< shp.dimension(3);++n3) {
                                for(int n2=0; n2< shp.dimension(2);++n2) {
                                    for(int n1=0; n1< shp.dimension(1);++n1) {
                                        for(int n0=0; n0< shp.dimension(0);++n0) {
                                            ((Double7D) res).set(n0,n1,n2,n3,n4,n5,n6,
                                                    x[0].get(n0)+x[1].get(n1)
                                                    +x[2].get(n2)+x[3].get(n3)
                                                    +x[4].get(n4)+x[5].get(n5)
                                                    +x[6].get(n6));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case 8:
                for(int n7=0; n7< shp.dimension(7);++n7) {
                    for(int n6=0; n6< shp.dimension(6);++n6) {
                        for(int n5=0; n5< shp.dimension(5);++n5) {
                            for(int n4=0; n4< shp.dimension(4);++n4) {
                                for(int n3=0; n3< shp.dimension(3);++n3) {
                                    for(int n2=0; n2< shp.dimension(2);++n2) {
                                        for(int n1=0; n1< shp.dimension(1);++n1) {
                                            for(int n0=0; n0< shp.dimension(0);++n0) {
                                                ((Double8D) res).set(n0,n1,n2,n3,n4,n5,n6,n7,
                                                        x[0].get(n0)+x[1].get(n1)
                                                        +x[2].get(n2)+x[3].get(n3)
                                                        +x[4].get(n4)+x[5].get(n5)
                                                        +x[6].get(n6)+x[7].get(n7));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case 9:
                for(int n8=0; n8< shp.dimension(8);++n8) {
                    for(int n7=0; n7< shp.dimension(7);++n7) {
                        for(int n6=0; n6< shp.dimension(6);++n6) {
                            for(int n5=0; n5< shp.dimension(5);++n5) {
                                for(int n4=0; n4< shp.dimension(4);++n4) {
                                    for(int n3=0; n3< shp.dimension(3);++n3) {
                                        for(int n2=0; n2< shp.dimension(2);++n2) {
                                            for(int n1=0; n1< shp.dimension(1);++n1) {
                                                for(int n0=0; n0< shp.dimension(0);++n0) {
                                                    ((Double9D) res).set(n0,n1,n2,n3,n4,n5,n6,n7,n8,
                                                            x[0].get(n0)+x[1].get(n1)
                                                            +x[2].get(n2)+x[3].get(n3)
                                                            +x[4].get(n4)+x[5].get(n5)
                                                            +x[6].get(n6)+x[7].get(n7)
                                                            +x[8].get(n8));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported rank");
        }
        return res;
    }

    /**
     * Compute  length of FFT frequencies/coordinates.
     *
     * @param shp shape of the array
     * @param scale the scale of each dimension
     * @return the  distance of FFT frequencies/coordinates.
     */
    public static DoubleArray fftDist(Shape shp,double[] scale)
    {
        DoubleArray res = fftDist2(shp,scale);
        res.map(new DoubleFunction() {

            @Override
            public double apply(double arg) {
                return Math.sqrt(arg);
            }
        });
        return res;
    }
    /**
     * Compute  length of FFT frequencies/coordinates.
     *
     * @param shp shape of the array
     *
     * @return the  distance of FFT frequencies/coordinates.
     */
    public static DoubleArray fftDist(Shape shp)
    {
        DoubleArray res = fftDist2(shp,null);
        res.map(new DoubleFunction() {

            @Override
            public double apply(double arg) {
                return Math.sqrt(arg);
            }
        });
        return res;
    }


    /**
     * Compute squared length of FFT frequencies/coordinates.
     *
     * @param shp shape of the array
     * @param scale the scale of each dimension
     * @return the squared distance of FFT frequencies/coordinates.
     */
    public static IntArray Mesh(Shape shp)
    {
        int rank = shp.rank();
        if (rank == 1)
            return generateFrequels(shp.dimension(0));

        int[] meshdims = new int[rank+1];
        System.arraycopy(shp.copyDimensions(), 0, meshdims, 0, rank);
        meshdims[rank] = rank;
        Shape meshShape = new Shape(meshdims);
        IntArray res = (IntArray) ArrayFactory.create(Traits.INT, meshShape);

        Int1D[]  x = new Int1D[rank];
        for( int j = 0; j < rank; j++){
            x[j] = generateFrequels(shp.dimension(j));
        }

        switch (rank) {
            case 2:
                for(int n1=0; n1< shp.dimension(1);++n1) {
                    for(int n0=0; n0< shp.dimension(0);++n0) {
                        ((Int3D) res).set(n0,n1,0, x[0].get(n0));
                        ((Int3D) res).set(n0,n1,1, x[1].get(n1));
                    }
                }
                break;
            case 3:
                for(int n2=0; n2< shp.dimension(2);++n2) {
                    for(int n1=0; n1< shp.dimension(1);++n1) {
                        for(int n0=0; n0< shp.dimension(0);++n0) {
                            ((Int4D) res).set(n0,n1,n2,0, x[0].get(n0));
                            ((Int4D) res).set(n0,n1,n2,1, x[1].get(n1));
                            ((Int4D) res).set(n0,n1,n2,2, x[2].get(n2));
                        }
                    }
                }
                break;
            case 4:
                for(int n3=0; n3< shp.dimension(3);++n3) {
                    for(int n2=0; n2< shp.dimension(2);++n2) {
                        for(int n1=0; n1< shp.dimension(1);++n1) {
                            for(int n0=0; n0< shp.dimension(0);++n0) {
                                ((Int5D) res).set(n0,n1,n2,n3,0, x[0].get(n0));
                                ((Int5D) res).set(n0,n1,n2,n3,1, x[1].get(n1));
                                ((Int5D) res).set(n0,n1,n2,n3,3, x[2].get(n2));
                                ((Int5D) res).set(n0,n1,n2,n3,4, x[3].get(n3));
                            }
                        }
                    }
                }
                break;
            case 5:
                for(int n4=0; n4< shp.dimension(4);++n4) {
                    for(int n3=0; n3< shp.dimension(3);++n3) {
                        for(int n2=0; n2< shp.dimension(2);++n2) {
                            for(int n1=0; n1< shp.dimension(1);++n1) {
                                for(int n0=0; n0< shp.dimension(0);++n0) {
                                    ((Int6D) res).set(n0,n1,n2,n3,n4,0, x[0].get(n0));
                                    ((Int6D) res).set(n0,n1,n2,n3,n4,1, x[1].get(n1));
                                    ((Int6D) res).set(n0,n1,n2,n3,n4,3, x[2].get(n2));
                                    ((Int6D) res).set(n0,n1,n2,n3,n4,4, x[3].get(n3));
                                    ((Int6D) res).set(n0,n1,n2,n3,n4,5, x[4].get(n4));
                                }
                            }
                        }
                    }
                }
                break;
            case 6:
                for(int n5=0; n5< shp.dimension(5);++n5) {
                    for(int n4=0; n4< shp.dimension(4);++n4) {
                        for(int n3=0; n3< shp.dimension(3);++n3) {
                            for(int n2=0; n2< shp.dimension(2);++n2) {
                                for(int n1=0; n1< shp.dimension(1);++n1) {
                                    for(int n0=0; n0< shp.dimension(0);++n0) {
                                        ((Int7D) res).set(n0,n1,n2,n3,n4,n5,0, x[0].get(n0));
                                        ((Int7D) res).set(n0,n1,n2,n3,n4,n5,1, x[1].get(n1));
                                        ((Int7D) res).set(n0,n1,n2,n3,n4,n5,3, x[2].get(n2));
                                        ((Int7D) res).set(n0,n1,n2,n3,n4,n5,4, x[3].get(n3));
                                        ((Int7D) res).set(n0,n1,n2,n3,n4,n5,5, x[4].get(n4));
                                        ((Int7D) res).set(n0,n1,n2,n3,n4,n5,6, x[5].get(n5));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case 7:
                for(int n6=0; n6< shp.dimension(6);++n6) {
                    for(int n5=0; n5< shp.dimension(5);++n5) {
                        for(int n4=0; n4< shp.dimension(4);++n4) {
                            for(int n3=0; n3< shp.dimension(3);++n3) {
                                for(int n2=0; n2< shp.dimension(2);++n2) {
                                    for(int n1=0; n1< shp.dimension(1);++n1) {
                                        for(int n0=0; n0< shp.dimension(0);++n0) {
                                            ((Int8D) res).set(n0,n1,n2,n3,n4,n5,n6,0, x[0].get(n0));
                                            ((Int8D) res).set(n0,n1,n2,n3,n4,n5,n6,1, x[1].get(n1));
                                            ((Int8D) res).set(n0,n1,n2,n3,n4,n5,n6,3, x[2].get(n2));
                                            ((Int8D) res).set(n0,n1,n2,n3,n4,n5,n6,4, x[3].get(n3));
                                            ((Int8D) res).set(n0,n1,n2,n3,n4,n5,n6,5, x[4].get(n4));
                                            ((Int8D) res).set(n0,n1,n2,n3,n4,n5,n6,6, x[5].get(n5));
                                            ((Int8D) res).set(n0,n1,n2,n3,n4,n5,n6,7, x[6].get(n6));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case 8:
                for(int n7=0; n7< shp.dimension(7);++n7) {
                    for(int n6=0; n6< shp.dimension(6);++n6) {
                        for(int n5=0; n5< shp.dimension(5);++n5) {
                            for(int n4=0; n4< shp.dimension(4);++n4) {
                                for(int n3=0; n3< shp.dimension(3);++n3) {
                                    for(int n2=0; n2< shp.dimension(2);++n2) {
                                        for(int n1=0; n1< shp.dimension(1);++n1) {
                                            for(int n0=0; n0< shp.dimension(0);++n0) {
                                                ((Int9D) res).set(n0,n1,n2,n3,n4,n5,n6,n7,0, x[0].get(n0));
                                                ((Int9D) res).set(n0,n1,n2,n3,n4,n5,n6,n7,1, x[1].get(n1));
                                                ((Int9D) res).set(n0,n1,n2,n3,n4,n5,n6,n7,3, x[2].get(n2));
                                                ((Int9D) res).set(n0,n1,n2,n3,n4,n5,n6,n7,4, x[3].get(n3));
                                                ((Int9D) res).set(n0,n1,n2,n3,n4,n5,n6,n7,5, x[4].get(n4));
                                                ((Int9D) res).set(n0,n1,n2,n3,n4,n5,n6,n7,6, x[5].get(n5));
                                                ((Int9D) res).set(n0,n1,n2,n3,n4,n5,n6,n7,7, x[6].get(n6));
                                                ((Int9D) res).set(n0,n1,n2,n3,n4,n5,n6,n7,7, x[7].get(n7));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported rank");
        }
        return res;
    }

}

