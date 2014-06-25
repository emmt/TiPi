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

package mitiv.microscopy;
import mitiv.utils.MathUtils;

public class MicroscopyMain {
    public static void main(String[] args) {
        double NA = 1.4;
        double ni = 1.518;
        double ns = 0;
        double zdepth = 0;
        double lambda = 542e-9;
        double dxy = 64.5e-9;
        double dz = 1.6e-7;
        int nx = 256;
        int ny = 256;
        int nz = 64;
        int nzern = 10;
        //double psf[] = new double[nx*ny];
        double psf[][][] = new double[nz][ny][nx];
        //double[] alpha = {0.000001};
        //double[] alpha = {0, 0, 0, 10, -3, 2, 1};
        double[] alpha = {0.,0.,1.};
        double[] beta = {0.,0.,0.,0., 1.,1.};
        double deltaX = 0;
        double deltaY = 0;

        MicroscopyModelPSF pupil = new MicroscopyModelPSF(NA, lambda, ni, ns, zdepth, dxy, dz, nx, ny, nz, nzern);
        pupil.computePSF(psf, alpha, beta, deltaX, deltaY, zdepth);

/*
        Utils.fft_pli2(pupil.getMaskPupil());
        Utils.fft_pli2(pupil.getPsi());
        Utils.fft_pli2(pupil.getRho());
        Utils.fft_pli2(pupil.getPhi());
        
  */      System.out.println("Pupil");
        MathUtils.stat(pupil.getMaskPupil());
        System.out.println("Psi");
        MathUtils.stat(pupil.getPsi());
        System.out.println("Rho");
        MathUtils.stat(pupil.getRho());
        System.out.println("Phi");
        MathUtils.stat(pupil.getPhi());
        
        MathUtils.printArray(MathUtils.indgen(7));
        MathUtils.printArray(MathUtils.indgen(4, 7));
        MathUtils.printArray(MathUtils.span1(2, 8, 2));
        //DoubleVectorSpace tmp = new DoubleVectorSpace(4);
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