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

import icy.image.IcyBufferedImage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import mitiv.array.Double2D;
import mitiv.array.Double3D;
import mitiv.array.ShapedArray;

public class IcyBufferedImageUtils {

    public static ShapedArray imageToArray(ArrayList<IcyBufferedImage> listImage) {
        int width = listImage.get(0).getWidth();
        int height = listImage.get(0).getHeight();
        int sizeZ = listImage.size();
        int[] shape = new int[]{width, height, sizeZ};
        return imageToArray(listImage,shape[0], shape[1], shape[2]);
    }
    
    public static ShapedArray imageToArray(ArrayList<IcyBufferedImage> listImage, int[] shape) {
        if (shape.length != 3) {
            throw new IllegalArgumentException("Shape should be of size 3, because input is three dimensionnal data");
        }
        return imageToArray(listImage,shape[0], shape[1], shape[2]);
    }
    
    public static ShapedArray imageToArray(ArrayList<IcyBufferedImage> listImage, int width, int height, int sizeZ) {
        //First we try if we can convert the data directly by using Icy Capacities
        int[] shape = new int[]{width, height, sizeZ};
        try {
            double[] out;
            out = new double[sizeZ*width*height];
            for (int j = 0; j < sizeZ; j++) {
                double[] tmp = listImage.get(j).getDataCopyCXYAsDouble();
                for (int i = 0; i < tmp.length; i++) {
                    out[i+j*tmp.length] = tmp[i];
                }
            }
            return Double3D.wrap(out, shape);
        } catch (Exception e) {
            //System.err.println("Could not directly convert ICY sequence");
            ArrayList<BufferedImage> list = new ArrayList<BufferedImage>(listImage);
            return BufferedImageUtils.imageToArray(list);
        }
    }
    
    public static ShapedArray imageToArray(IcyBufferedImage image) {
        int[] shape = new int[]{image.getWidth(), image.getHeight()};
        return imageToArray(image, shape[0], shape[1]);
    }

    public static ShapedArray imageToArray(IcyBufferedImage image, int[] shape) {
        if (shape.length != 2) {
            throw new IllegalArgumentException("Shape should be of size 2, because input is two dimensionnal data");
        }
        return imageToArray(image, shape[0], shape[1]);
    }

    public static ShapedArray imageToArray(IcyBufferedImage image, int width,int height) {
        //First we try if we can convert the data directly by using Icy Capacities
        int[] shape = new int[]{width, height};
        try {
            return Double2D.wrap(image.getDataCopyCXYAsDouble(), shape);
        } catch (Exception e) {
            return BufferedImageUtils.imageToArray(image);
        }
    }
    
    public static ShapedArray sequenceToArray(IcyBufferedImage image, int width,int height) {
        //First we try if we can convert the data directly by using Icy Capacities
        int[] shape = new int[]{width, height};
        try {
            return Double2D.wrap(image.getDataCopyCXYAsDouble(), shape);
        } catch (Exception e) {
            return BufferedImageUtils.imageToArray(image);
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