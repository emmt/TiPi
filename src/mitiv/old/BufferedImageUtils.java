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

package mitiv.old;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import mitiv.array.Double2D;
import mitiv.array.Double3D;
import mitiv.array.Float2D;
import mitiv.array.Float3D;
import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.base.Traits;
@Deprecated
public class BufferedImageUtils {

    public static ShapedArray imageToArray(BufferedImage image) {
        return imageToArray(image, false);
    }
    /**
     * Convert a image to an 1D array of double.
     *
     * @param image the image
     * @param single
     * @return the double[]
     */
    public static ShapedArray imageToArray(BufferedImage image, boolean single) {
        int height = image.getHeight();
        int width = image.getWidth();
        WritableRaster raster = image.getRaster();
        if (single) {
            float []out;
            int size = width*height;
            out = new float[size];
            int[] tmp = new int[raster.getNumBands()];
            for(int j=0;j<height;j++){
                for(int i=0;i<width;i++){
                    raster.getPixel(i, j, tmp);
                    if (tmp.length == 1 || tmp.length == 2) {
                        out[(i+j*width)] = CommonUtils.colorToGrey(tmp[0], tmp[0], tmp[0]);
                    } else {
                        out[(i+j*width)] = CommonUtils.colorToGrey(tmp[0], tmp[1], tmp[2]);
                    }
                }
            }
            return Float2D.wrap(out, width, height);
        } else {
            double []out;
            int size = width*height;
            out = new double[size];
            int[] tmp = new int[raster.getNumBands()];
            for(int j=0;j<height;j++){
                for(int i=0;i<width;i++){
                    raster.getPixel(i, j, tmp);
                    if (tmp.length == 1 || tmp.length == 2) {
                        out[(i+j*width)] = CommonUtils.colorToGrey(tmp[0], tmp[0], tmp[0]);
                    } else {
                        out[(i+j*width)] = CommonUtils.colorToGrey(tmp[0], tmp[1], tmp[2]);
                    }
                }
            }
            return Double2D.wrap(out, width, height);
        }
    }

    /**
     * By default we use double precision
     *
     * @param listImage
     * @return a shaped array
     */
    public static ShapedArray imageToArray(ArrayList<BufferedImage> listImage) {
        return imageToArray(listImage, false);
    }

    public static ShapedArray imageToArray(ArrayList<BufferedImage> listImage, boolean single) {
        int width = listImage.get(0).getWidth();
        int height = listImage.get(0).getHeight();
        int sizeZ = listImage.size();   //FIXME if 2D (size == 1) what happen ? Throw exception ?
        if (single) {
            float[] out = new float[sizeZ*width*height];
            for (int k = 0; k < sizeZ; k++) {
                BufferedImage current = listImage.get(k);
                WritableRaster raster = current.getRaster();
                int[] tmp = new int[raster.getNumBands()];
                for(int j = 0; j < height; j++){
                    for(int i = 0; i < width; i++){
                        raster.getPixel(i, j, tmp);
                        if (tmp.length == 1 || tmp.length == 2) {
                            out[i+j*width+k*width*height] = CommonUtils.colorToGrey(tmp[0], tmp[0], tmp[0]);
                        } else {
                            out[i+j*width+k*width*height] = CommonUtils.colorToGrey(tmp[0], tmp[1], tmp[2]);
                        }
                    }
                }
            }
            return Float3D.wrap(out, width, height, sizeZ);
        } else {
            double[] out = new double[sizeZ*width*height];
            for (int k = 0; k < sizeZ; k++) {
                BufferedImage current = listImage.get(k);
                WritableRaster raster = current.getRaster();
                int[] tmp = new int[raster.getNumBands()];
                for(int j = 0; j < height; j++){
                    for(int i = 0; i < width; i++){
                        raster.getPixel(i, j, tmp);
                        if (tmp.length == 1 || tmp.length == 2) {
                            out[i+j*width+k*width*height] = CommonUtils.colorToGrey(tmp[0], tmp[0], tmp[0]);
                        } else {
                            out[i+j*width+k*width*height] = CommonUtils.colorToGrey(tmp[0], tmp[1], tmp[2]);
                        }
                    }
                }
            }
            return Double3D.wrap(out, width, height, sizeZ);
        }
    }

    /**
     * Create a buffered image, simply copy array to buffered image.
     *
     * @param array the array
     * @param shape
     * @return the buffered image
     */
    public static ShapedArray arrayToImage(double[] array, Shape shape) {
        if (shape.rank() == 2) {
            return arrayToImage(array, shape.dimension(0), shape.dimension(1));
        } else if (shape.rank() == 3) {
            return arrayToImage(array, shape.dimension(0), shape.dimension(1), shape.dimension(2));
        }else{
            throw new IllegalArgumentException("Only Bi and tri dimensionnal images are accepted now");
        }
    }

    /**
     * Create a buffered image, simply copy array to buffered image.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @return the buffered image
     */
    public static ShapedArray arrayToImage(double[] array, int width, int height){
        return Double2D.wrap(array, width, height);
    }

    /**
     * Create a buffered image, simply copy array to buffered image.
     *
     * @param array the array
     * @param width
     * @param height
     * @param depth
     * @return the buffered image
     */
    public static ShapedArray arrayToImage(double[] array, int width, int height, int depth) {
        return Double3D.wrap(array, width, height, depth);
    }

    /**
     * Create a buffered image, simply copy array to buffered image.
     *
     * @param array the array
     * @param shape
     * @return the buffered image
     */
    public static ShapedArray arrayToImage(float[] array, Shape shape){
        if (shape.rank() == 2) {
            return arrayToImage(array, shape.dimension(0), shape.dimension(1));
        } else if (shape.rank() == 3) {
            return arrayToImage(array, shape.dimension(0), shape.dimension(1), shape.dimension(2));
        }else{
            throw new IllegalArgumentException("Only Bi and tri dimensionnal images are accepted now");
        }
    }

    /**
     * Create a buffered image, simply copy array to buffered image.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @return the buffered image
     */
    public static ShapedArray arrayToImage(float[] array, int width, int height){
        return Float2D.wrap(array, width, height);
    }

    /**
     * Create a buffered image, simply copy array to buffered image.
     *
     * @param array the array
     * @param width the width
     * @param height the height
     * @param sizeZ
     * @return the buffered image
     */
    public static ShapedArray arrayToImage(float[] array, int width, int height, int sizeZ){
        return Float3D.wrap(array, width, height, sizeZ);
    }


    public static ArrayList<BufferedImage> arrayToImage(ShapedArray array) {
        Shape shape = array.getShape();
        int width = shape.dimension(0);
        int height = shape.dimension(1);
        if (array.getType() == Traits.DOUBLE) {
            if (shape.rank() == 2) {
                BufferedImage tmp = CommonUtils.arrayToImage1D(((Double2D)array).flatten(), width, height, false);
                return fill(tmp);
            } else if (shape.rank() == 3) {
                double[] data = ((Double3D)array).flatten();
                int sizeZ = shape.dimension(2);
                ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();
                for (int j = 0; j < sizeZ; j++) {
                    double[] tmp = new double[width*height];
                    for (int i = 0; i < width*height; i++) {
                        tmp[i] = data[i+j*width*height];
                    }
                    BufferedImage tmpImg = CommonUtils.arrayToImage1D(((Double2D)array).flatten(), width, height, false);
                    list.add(tmpImg);
                }
                return list;
            } else {
                throw new IllegalArgumentException("Rank of the Shaped Array can only be 2 or 3");
            }
        } else {
            if (shape.rank() == 2) {
                BufferedImage tmp = createNewBufferedImage(width, height);
                return fill(tmp); //Whatever the type other than float, we use float
            } else if (shape.rank() == 3) {
                float[] data = ((Float3D)array).flatten();
                int sizeZ = shape.dimension(2);
                ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();
                for (int j = 0; j < sizeZ; j++) {
                    float[] tmp = new float[width*height];
                    for (int i = 0; i < width*height; i++) {
                        tmp[i] = data[i+j*width*height];
                    }
                    BufferedImage tmpImg = CommonUtils.arrayToImage1D(((Float2D)array).flatten(), width, height, false);
                    list.add(tmpImg);
                }
                return list;
            } else {
                throw new IllegalArgumentException("Rank of the Shaped Array can only be 2 or 3");
            }
        }
    }

    private static ArrayList<BufferedImage> fill(BufferedImage img){
        ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();
        list.add(img);
        return list;
    }

    /**********************************************************/
    /**                     PADDING                          **/
    /**********************************************************/

    /**
     *
     * @param input
     * @param coef
     * @return a shaped array
     */
    public static ShapedArray imagePad(ShapedArray input, double coef) {
        Shape shape = input.getShape();
        if (input.getRank() == 2) {
            return imagePad(input, shape.dimension(0), shape.dimension(1), 1, coef, coef);
        } else {
            return imagePad(input, shape.dimension(0), shape.dimension(1), shape.dimension(2), coef, coef);
        }

    }

    /**
     *
     * @param input
     * @param width
     * @param height
     * @param sizeZ
     * @param coef
     * @return a shaped array
     */
    public static ShapedArray imagePad(ShapedArray input, int width, int height, int sizeZ, double coef) {
        return imagePad(input, width, height, sizeZ, coef, coef);
    }

    /**
     * Pad the input Shaped Array with the coefficient given,
     * <br>
     * A coefficient equal to one mean that we are not doing anything.
     * A coefficient equal to two mean that the padding will be of same size as the input image.
     * i.e: The image will be 8 time bigger weight*2, height*2, sizeZ*2
     *
     * @param input
     * @param width
     * @param height
     * @param sizeZ
     * @param coefWH
     * @param coefZ
     * @return a shaped array
     */
    public static ShapedArray imagePad(ShapedArray input, int width, int height, int sizeZ, double coefWH, double coefZ) {
        int sizePadW = (int)(width*coefWH-width);
        int sizePadH = (int)(height*coefWH-height);
        int sizePadZ = (int)(sizeZ*coefZ-sizeZ);

        int halfSizePadW = sizePadW/2;
        int halfSizePadH = sizePadH/2;
        int halfSizePadZ = sizePadZ/2;

        Shape shape = new Shape(width+sizePadW, height+sizePadH, sizeZ+sizePadZ);
        //If we are in double else we will work in FLOAt
        if (input.getType() == Traits.DOUBLE) {
            double[] output = new double[(width+sizePadW)*(height+sizePadH)*(sizeZ+sizePadZ)];
            double[] inputDbl = input.toDouble().flatten();
            for (int i = 0; i < output.length; i++) {
                output[i] = 0;
            }
            double temp;
            for (int k = 0; k < sizeZ; k++) {
                for (int j = 0; j < height; j++) {
                    for (int i = 0; i < width; i++) {
                        temp = inputDbl[i+j*width+k*width*height];
                        output[
                               (i+halfSizePadW)+
                               (j+halfSizePadH)*(width+sizePadW)+
                               (k+halfSizePadZ)*(width+sizePadW)*(height+sizePadH)] = temp;
                    }
                }
            }
            return Double3D.wrap(output, shape);
        } else {
            float[] output = new float[(width+sizePadW)*(height+sizePadH)*(sizeZ+sizePadZ)];
            float[] inputFlt = input.toFloat().flatten();
            for (int i = 0; i < output.length; i++) {
                output[i] = 0;
            }
            float temp;
            for (int k = 0; k < sizeZ; k++) {
                for (int j = 0; j < height; j++) {
                    for (int i = 0; i < width; i++) {
                        temp = inputFlt[i+j*width+k*width*height];
                        output[
                               (i+halfSizePadW)+
                               (j+halfSizePadH)*(width+sizePadW)+
                               (k+halfSizePadZ)*(width+sizePadW)*(height+sizePadH)] = temp;
                    }
                }
            }
            return Float3D.wrap(output, shape);
        }
    }



    /**
     * remove the pad of an image with the size given:<br>
     * WidthInput = WidthOutput + sizePSF<br>
     * HeightInput = HeightOutput +sizePSF.
     *
     * @param image the image
     * @param sizePSF the size psf
     * @return the buffered image
     */
    public static BufferedImage imageUnPad(BufferedImage image, int sizePSF) {
        int hlf = sizePSF/2;
        return image.getSubimage(hlf, hlf, image.getWidth()-sizePSF, image.getHeight()-sizePSF);
    }

    public static ArrayList<BufferedImage> imageUnPad(ArrayList<BufferedImage> image, int sizePSF) {
        ArrayList<BufferedImage> out = new ArrayList<BufferedImage>();
        for (int i = image.size()/4; i < (image.size()*3)/4; i++) {
            out.add(imageUnPad(image.get(i), sizePSF));
        }

        /*for (int i = (image.size()*3)/4; i < image.size(); i++) {
            out.add(imageUnPad(image.get(i), sizePSF));
        }
        for (int i = 0; i < image.size()/4; i++) {
            out.add(imageUnPad(image.get(i), sizePSF));
        }*/
        return out;
    }

    public static ShapedArray shiftPsf(ShapedArray psf){
        Shape shape = psf.getShape();
        int width = shape.dimension(0);
        int height = shape.dimension(1);
        double[] data;
        if (shape.rank() == 2) {
            data = psf.toDouble().flatten();
            double[] out = new double[data.length];
            CommonUtils.psfPadding1D(out, width, height, data, width, height, false);
            return Double2D.wrap(out, shape);
        } else if (shape.rank() == 3) {
            data = psf.toDouble().flatten();
            double[] out = new double[data.length];
            //CommonUtils.fftShift3D(data, out, width, height, shape[2]);
            CommonUtils.psf3DPadding1D(data, out, width, height, shape.dimension(2));
            return Double3D.wrap(out, shape);
        } else {
            throw new IllegalArgumentException("Input should be bi or tri dimensionnal");
        }
    }

    /**********************************************************/
    /**                  Simple Utils                        **/
    /**********************************************************/

    /**
     * Create a buffered image from another buffered image
     * @param originalImage the original image
     * @return BufferedImage the buffered image with same characteristics as originalImage
     */
    public static BufferedImage createNewBufferedImage(BufferedImage originalImage){
        BufferedImage imageout;
        //In certain cases we need a specific type
        if(originalImage.getType() == 0){
            imageout = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        }else{
            imageout = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        }
        return imageout;
    }

    /**
     * Create a BufferedImage from a size (WxH).
     *
     * @param width the width
     * @param height the height
     * @return the buffered image
     */
    public static BufferedImage createNewBufferedImage(int width, int height){
        return new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    }


    /**
     * Show buffered image.
     *
     * @param I the i
     */
    public static void showBufferedImage(BufferedImage I)
    {
        JFrame frame = new JFrame();
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(I));
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Save buffered image.
     *
     * @param I the i
     * @param name the name
     */
    public static void saveBufferedImage(BufferedImage I, String name)
    {
        try
        {
            ImageIO.write(I, "PNG", new File(name));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open as buffered image.
     *
     * @param path the path
     * @return the buffered image
     */
    public static BufferedImage openAsBufferedImage(String path) {
        BufferedImage I = null;
        try {
            I = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return I;
    }

    /**
     * Save array to image.
     *
     * @param A - The shaped array.
     * @param name - The file name.
     */
    public static void saveArrayToImage(ShapedArray A, String name)
    {
        if (A.getShape().rank() != 2) {
            throw new IllegalArgumentException("The shapped array should be bi-dimensionnal");
        }
        BufferedImage I = arrayToImage(A).get(0);
        saveBufferedImage(I, name);
    }
}
