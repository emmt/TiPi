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

/**
 * 
 * @author Leger Jonathan
 *
 */
package mitiv.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import mitiv.deconv.Deconvolution;

/**
 * mitivCLI is a command line interface that let the user call the functions contains 
 * in the library easily and with precise control of the parameters.
 * 
 * usage: mitivCLI PSF IMAGE
 * options  -h help menu
 *          -r regularization choice: wiener(default), quadratic, cg
 *          -p post treatment: none(default), corrected, colormap, correted_colormap
 *          -a alpha value 
 */
public class mitivCLI {

    static String regularization = "wiener";
    static String[] regularizationChoice = new String[]{"wiener","quadratic","cg"};
    static String postTreatment = "none";
    static String[] postTreatmentChoice = new String[]{"none","corrected", "colormap", "correted_colormap"};
    static String alpha = "1.0";
    static String PSF = "";
    static String Image = "";
    static String OutputImage = "DeconvoluatedImage.png";

    private static boolean isIn(String element, String[] tab){
        boolean ispresent = false;
        for (int i = 0; i < tab.length; i++) {
            if (tab[i].compareTo(element) == 0) {
                ispresent = true;
            }
        }
        return ispresent;
    }

    private static void checkArgs(){
        if (PSF.compareTo("") == 0 || Image.compareTo("") == 0) {
            System.out.println("We need at least PSF and a image");
            System.exit(1);
        }
        if (!isIn(regularization, regularizationChoice)) {
            System.out.println("The regularization chosen does not exist: "+regularization);
            System.exit(1);
        }
        if (!isIn(postTreatment, postTreatmentChoice)) {
            System.out.println("The post treatment chosen does not exist: "+postTreatment);
            System.exit(1);
        }
        try {
            Double.parseDouble(alpha);
        } catch (Exception e) {
            System.out.println("The alpha chosen is not good: "+alpha);
            System.exit(1);
        }

    }

    private static BufferedImage chooseReg(String reg,Deconvolution deconv, double alpha){
        if (reg.compareTo(regularizationChoice[0]) == 0) {
            return deconv.FirstDeconvolution(alpha);
        }
        else if (reg.compareTo(regularizationChoice[1]) == 0) {
            return deconv.FirstDeconvolutionQuad(alpha);
        }else if (reg.compareTo(regularizationChoice[2]) == 0) {
            return deconv.FirstDeconvolutionCG(alpha);
        }else{
            throw new IllegalArgumentException("Invalid Job");
        }
    }
    
    private static int choosePost(String post){
        if (post.compareTo(postTreatmentChoice[0]) == 0) {
            return DeconvUtils.SCALE;
        }else if (post.compareTo(postTreatmentChoice[1]) == 0) {
            return DeconvUtils.SCALE_CORRECTED;
        }else if (post.compareTo(postTreatmentChoice[2]) == 0) {
            return DeconvUtils.SCALE_COLORMAP;
        }else if(post.compareTo(postTreatmentChoice[3]) == 0){
            return DeconvUtils.SCALE_CORRECTED_COLORMAP;
        }else{
            throw new IllegalArgumentException("Invalid Job");
        }
    }

    private static void printHelp(){
        System.out.println("Usage: mitivCLI psf image");
        System.out.println("      options: -o output image");
        System.out.println("      options: -r kind of regularization");
        System.out.println("                  regularization: "+Arrays.toString(regularizationChoice));
        System.out.println("      options: -p post treatment");
        System.out.println("                  treatment: "+Arrays.toString(postTreatmentChoice));
        System.out.println("      options: -a alpha value");
    }

    public static void main(String[] args) {
        boolean psfFound = false;
        for (int i = 0; i < args.length; i++) {
            String tmp = args[i];
            if (tmp.charAt(0) == '-' ) {
                String next = args[i+1];
                switch (tmp.charAt(1)) {
                case 'h':
                    printHelp();
                    System.exit(0);
                case 'o':
                    OutputImage = next;
                    break;
                case 'r':
                    regularization = next;
                    break;
                case 'p':
                    postTreatment = next;
                    break;
                case 'a':
                    alpha = next;
                    break;
                default:
                    break;
                }
                ++i;
            }else{
                if (!psfFound) {
                    PSF = tmp;
                    psfFound = true;
                }else{
                    Image = tmp; 
                }
            }
        }
        checkArgs();
        System.out.format("Regularization: %s, PostTreatment: %s, alpha: %s, Output: %s\n",regularization,postTreatment,alpha,OutputImage);

        Deconvolution deconvolution = new Deconvolution(Image,PSF,choosePost(postTreatment));
        BufferedImage img = chooseReg(regularization, deconvolution, Double.parseDouble(alpha));
        try {
            File outputfile = new File(OutputImage);
            ImageIO.write(img, "png", outputfile);
            System.out.println("Done.");
        } catch (IOException e) {
            System.err.println("Bad output path");
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

