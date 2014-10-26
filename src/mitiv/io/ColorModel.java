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

import java.awt.image.BufferedImage;

import mitiv.array.Array2D;
import mitiv.array.Array3D;
import mitiv.array.ShapedArray;
import mitiv.base.Traits;

/**
 * Different color models used when interpreting shaped arrays as images or
 * when selecting different color channels or applying color conversion.
 * 
 * <h2>Conventions</h2>
 * <p>
 * A simple heuristic is applied to determine the color model of a shaped
 * array when it is interpreted as an image.  The rules are:
 * <ol>
 * <li>a 2D shaped array of any type is a gray-scale image with color
 *     model {@link #GRAY};</li>
 * <li>a 3D shaped array of any type with first dimension equals to 3 is
 *     a RGB image with color model {@link #RGB};</li>
 * <li>a 3D shaped array of type {@code byte} and with first dimension
 *     equals to 4 and is a RGBA image with color model {@link #RGBA};</li>
 * <li>anything else corresponds to the the color model {@link #NONE}.</li>
 * </ol>
 * </p><p>
 * These conventions are assumed by the various conversion routines, <i>e.g.</i>
 * {@link DataFormat#makeBufferedImage} to convert a buffered image to a
 * shaped array or {@link DataFormat#imageToShapedArray} to perform the
 * opposite conversion.
 * </p>
 * 
 * <h2>Gray-scale images</h2>
 * <p>
 * A gray-scale image is stored into a shaped array as a 2D array
 * with the first dimension equals to
 * the width of the image and the second dimension equals to the height of
 * the image.  The pixels of a gray-scale image have a single value: their intensity
 * or their gray level.  The gray level of a shaped array interpreted as a gray-scale
 * image can be accessed as follows:
 * <pre>
 *      gray = img.get(x,y);
 * </pre>
 * where <b>img</b> is an {@link Array2D} object and (<b>x</b>,<b>y</b>)
 * are the pixel coordinates.
 * </p>
 * 
 * <h2>RGB images</h2>
 * <p>
 * A RGB image is stored into a shaped array as a 3D array
 * with the first dimension equals to 3, the second dimension equals to
 * the width of the image and the third dimension equals to the height of
 * the image.  The pixels of a RGB image have three colors: red (R), blue (B) and
 * green (G).  The colors of a shaped array interpreted as a RGB image
 * can be accessed as follows:
 * <pre>
 *      red   = img.get(0,x,y);
 *      green = img.get(1,x,y);
 *      blue  = img.get(2,x,y);
 * </pre>
 * where <b>img</b> is an {@link Array3D} object and (<b>x</b>,<b>y</b>)
 * are the pixel coordinates.
 * </p>
 * 
 * <h2>RGBA images</h2>
 * <p>
 * A RGBA image is stored into a shaped array as a 3D
 * array with the first dimension equals to 4, the second dimension equals
 * to the width of the image and the third dimension equals to the height
 * of the image.
 * The pixels of a RGBA image have four values: red (R), blue (B), green
 * (G) and alpha (A).   The values of a shaped array interpreted as a RGBA image
 * can be accessed as follows:
 * <pre>
 *      red   = img.get(0,x,y);
 *      green = img.get(1,x,y);
 *      blue  = img.get(2,x,y);
 *      alpha = img.get(3,x,y);
 * </pre>
 * where <b>img</b> is an {@link Array3D} object and (<b>x</b>,<b>y</b>)
 * are the pixel coordinates.
 * </p><p>
 * Currently, to avoid ambiguities about the interpretation of the alpha
 * channel, we only support RGBA images with {@code byte} values.  These
 * values are understood as being unsigned.  A value of zero for alpha
 * corresponds to a transparent pixel, the highest possible value of
 * alpha, <i>i.e.</i> {@code 0xFF}, corresponds to an opaque pixel.  It is
 * assumed by the various conversion routines, that the color levels are
 * not pre-multiplied by
 * the alpha value.  When converting a buffered image of type
 * {@link BufferedImage#TYPE_INT_ARGB_PRE} or
 * {@link BufferedImage#TYPE_4BYTE_ABGR}, the pre-multiplication is
 * reversed to follow this convention.
 * </p>
 * 
 * @author Éric Thiébaut.
 *
 */
public enum ColorModel {
    /** The red channel of an RGB or RGBA image. */
    RED(1, "red channel"),

    /** The green channel of an RGB or RGBA image. */
    GREEN(1, "green channel"),

    /** The blue channel of an RGB or RGBA image. */
    BLUE(1, "blue channel"),

    /** The alpha channel of an RGBA image. */
    ALPHA(1, "alpha channel"),

    /** A gray-scale image. */
    GRAY(1, "intensity or gray-scale"),

    /**
     * A RGB image.
     * @see {@link guessColorModel} for a discussion about the conventions.
     */
    RGB(3, "red-green-blue"),

    /**
     * A RGBA image.
     * @see {@link guessColorModel} for a discussion about the conventions.
     */
    RGBA(4, "red-green-blue-alpha"),

    /** Any non-image array. */
    NONE(-1, "non-image array");

    private final int bands;
    private final String description;
    private ColorModel(int bands, String descr) {
        this.bands = bands;
        this.description = descr;
    }

    /** Get the number of bands of the color model. */
    public int bands() {
        return bands;
    }

    /** Get the description of the color model. */
    public String description() {
        return description;
    }

    /** Get a string representation of the color model. */
    @Override
    public String toString() {
        return description;
    }

    /**
     * Guess the color model of a shaped array.
     * <p>
     * This method applies a simple heuristic (see {@link ColorModel}) to
     * determine the color model of a shaped array when it is interpreted
     * as an image.
     * </p>
     * @param arr - The shaped array.
     *
     * @return One of the following color models: {@link #GRAY},
     *          {@link #RGB}, {@link #RGBA} or {@link #NONE}.
     *
     * @see {@link ColorModel}, {@link #GRAY}, {@link #RGB}, {@link #RGBA} or {@link #NONE}.
     */
    static public ColorModel guessColorModel(ShapedArray arr) {
        int rank = arr.getRank();
        if (rank == 2) {
            return GRAY;
        }
        if (rank == 3) {
            int bands = arr.getDimension(0);
            if (bands == 3) {
                return RGB;
            }
            if (bands == 4 && arr.getType() == Traits.BYTE) {
                return RGBA;
            }
        }
        return NONE;
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
