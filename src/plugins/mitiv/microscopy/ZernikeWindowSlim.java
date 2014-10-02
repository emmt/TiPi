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

package plugins.mitiv.microscopy;
import mitiv.utils.*;
import mitiv.microscopy.*;

import icy.gui.frame.IcyFrame;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * ZernikeWindows is created once the user have given all the parameters to 
 * microscopyModel
 * 
 * @author Leger Jonathan
 *
 */
public class ZernikeWindowSlim implements  Runnable {
    
    double[] psf;
    double PSFXZ[][][];
    boolean[] rpp;
    int Nx;
    int Ny;

    IcyFrame test;
    MicroscopyModelPSF_1D pupil;
    
    /**
     * Take a psf and a image, and give the method used to compute the solution
     * 
     * @param pathPsf   Can be a string, icyimage, bufferedImage
     * @param pathImage Can be a string, icyimage, bufferedImage
     * @param quadratic Boolean to enble the use quadatric computation method
     */
    public ZernikeWindowSlim(MicroscopyModelPSF_1D pupil, int Nx, int Ny, boolean[] rpp)
    {
        this.pupil = pupil;
        this.rpp = rpp;
        this.Nx = Nx;
        this.Ny = Ny;
    }

    @Override
    public void run() {
        test = new IcyFrame();
 
        JPanel middle = new JPanel();
        middle.setLayout(new FlowLayout());
        if (rpp[0]) {
            JLabel rholabel = new JLabel();
            rholabel.setIcon(new ImageIcon(CommonUtils.arrayToImage1D(pupil.getRho(), Nx, Ny, false)));
            middle.add("label",new JLabel("RHO"));
            middle.add("image",rholabel);
        }
        if (rpp[1]) {
            JLabel philabel = new JLabel();
            philabel.setIcon(new ImageIcon(CommonUtils.arrayToImage1D(pupil.getPhi(), Nx, Ny, false)));
            middle.add("label",new JLabel("PHI"));
            middle.add("image",philabel);
        }
        if (rpp[2]) {
            JLabel psylabel = new JLabel();
            psylabel.setIcon(new ImageIcon(CommonUtils.arrayToImage1D(pupil.getPsi(), Nx, Ny, false)));
            middle.add("label",new JLabel("PSI"));
            middle.add("image",psylabel);
            
        }        

        test.setLayout(new BorderLayout());
        test.add(middle, BorderLayout.CENTER);
        
        //Title of the windows
        if (rpp[3]) {
            test.setTitle("PSF along XZ axis");
        }else{
            test.setTitle("PSF along XY axis");
        }
        test.pack();
        //ICY
        test.addToMainDesktopPane();
        test.setResizable(true);
        test.setAlwaysOnTop(true);
        test.setVisible(true);  
    }
    
    /**
     * This function is here in case we create a new windows and so we don't need 
     * the old one
     * 
     */
    public void close(){
        test.close();
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
