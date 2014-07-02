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

package mitiv.deconv;

import icy.image.IcyBufferedImage;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import mitiv.linalg.DoubleVector;
import mitiv.linalg.DoubleVectorSpaceWithRank;
import mitiv.linalg.DoubleVectorWithRank;
import mitiv.linalg.FloatVector;
import mitiv.linalg.FloatVectorSpaceWithRank;
import mitiv.linalg.FloatVectorWithRank;
import mitiv.linalg.Vector;
import mitiv.linalg.VectorSpace;
import mitiv.utils.CommonUtils;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;

public class DeconvUtilsVector {

    private Vector image;
    private Vector image_psf;
    private VectorSpace imageSpace;
    private VectorSpace imageSpaceComplex;
    private boolean single = true;
    boolean isComplex;

    DoubleFFT_1D fft1D;
    FloatFFT_1D fft1DFloat;

    public void ReadImage(String pathImage, String pathPSF, boolean singlePrecision) {
        try {
            ReadImage(ImageIO.read(new File(pathImage)), ImageIO.read(new File(pathPSF)), singlePrecision);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Wrong path given");
        }
    }

    public void ReadImage(IcyBufferedImage image, IcyBufferedImage PSF, boolean singlePrecision) {
        System.out.println("icy");
        ReadImage(IcyBufferedImage.createFrom(image), IcyBufferedImage.createFrom(PSF), singlePrecision); //
    }

    public void ReadImage(BufferedImage image, BufferedImage PSF, boolean singlePrecision) {
        ReadImage(image, PSF, false, singlePrecision,true);
    }

    public void ReadImage(BufferedImage image, BufferedImage PSF, Boolean padding, boolean singlePrecision, boolean isComplex) {
        //For now, no padding option TODO add padding option
        if (singlePrecision) {
            imageSpace = new FloatVectorSpaceWithRank(image.getHeight(), image.getWidth());
            FloatVectorSpaceWithRank psfSpace = new FloatVectorSpaceWithRank(PSF.getWidth(), PSF.getHeight());
            imageSpaceComplex = new FloatVectorSpaceWithRank(image.getHeight()*2, image.getWidth());

            this.image = CommonUtils.imageToVector(imageSpace, image, singlePrecision, isComplex);
            this.image_psf = CommonUtils.imageToVector(psfSpace, PSF, singlePrecision, isComplex);
        } else {
            imageSpace = new DoubleVectorSpaceWithRank(image.getHeight(), image.getWidth());
            DoubleVectorSpaceWithRank psfSpace = new DoubleVectorSpaceWithRank(PSF.getWidth(), PSF.getHeight());
            imageSpaceComplex = new DoubleVectorSpaceWithRank(image.getHeight()*2, image.getWidth());

            this.image = CommonUtils.imageToVector(imageSpaceComplex, image, singlePrecision , isComplex);
            this.image_psf = CommonUtils.imageToVector(psfSpace, PSF, singlePrecision, false);
        }
        single = singlePrecision;
        this.isComplex = isComplex;
    }

    public Vector psfPadding(){
        return CommonUtils.psfPadding1D(imageSpaceComplex, image, image_psf, single, isComplex);
    }

    public void FFT1D(Vector vector) {
        if (single) {
            FloatVector vectorFloat = (FloatVector)vector;
            float[] array = vectorFloat.getData();
            int size = ((FloatVectorSpaceWithRank)imageSpace).getSize();
            if(fft1DFloat == null){
                fft1DFloat = new FloatFFT_1D(size);
            }
            fft1DFloat.realForwardFull(array);
        } else {
            DoubleVector vectorDouble = (DoubleVector)vector;
            double[] array = vectorDouble.getData();
            int size = ((DoubleVectorSpaceWithRank)imageSpace).getSize();
            if(fft1D == null){
                fft1D = new DoubleFFT_1D(size);
            }
            fft1D.realForwardFull(array);
        }
    }

    public void IFFT1D(Vector vector) {
        if (single) {
            FloatVector vectorFloat = (FloatVector)vector;
            float[] array = vectorFloat.getData();
            fft1DFloat.complexInverse(array, true);
        } else {
            DoubleVector vectorDouble = (DoubleVector)vector;
            double[] array = vectorDouble.getData();
            fft1D.complexInverse(array, true);
        }
    }

    public Vector getImage(){
        return image;
    }

    public Vector getPsfPad(){
        return psfPadding();
    }

    public BufferedImage ArrayToImage(Vector vector, int correction,boolean isComplex){
        return CommonUtils.vectorToImage(imageSpace, vector, correction ,single, isComplex);
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