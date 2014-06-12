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

import icy.gui.frame.IcyFrame;
//import icy.main.Icy;
//import icy.sequence.Sequence;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/**
 * ZernikeWindows is created once the user have given all the parameters to 
 * microscopyModel
 * 
 * @author Leger Jonathan
 *
 */
public class ZernikeWindow implements  Runnable {

    private JLabel label;   
    private JLabel image;
    private double alpha;
    
    double [][][] psf;
    double PSFXZ[][][];
    double[] args;
    boolean[] rpp;
    
    IcyFrame test;
    MicroscopyModelPSF2D2 pupil2;
    /**
     * Take a psf and a image, and give the method used to compute the solution
     * 
     * @param pathPsf   Can be a string, icyimage, bufferedImage
     * @param pathImage Can be a string, icyimage, bufferedImage
     * @param quadratic Boolean to enble the use quadatric computation method
     */
    public ZernikeWindow(double[] args, boolean[] rpp){
        this.args = args;
        this.rpp = rpp;
        psf = new double[(int)args[7]][(int)args[6]][(int)args[5]];
        pupil2 = new MicroscopyModelPSF2D2(args[0], args[1], args[2], args[3], args[4], (int)args[5], (int)args[6], (int)args[7], (int)args[8], (int)(args[5]*args[6]));
        pupil2.computePSF(psf, new double[]{args[11]}, new double[]{args[12]}, args[9], args[10]);
        PSFXZ = Utils.XY2XZ(psf);
    }

    @Override
    public void run() {
        test = new IcyFrame();
        //JFrame test = new JFrame();
        label = new JLabel("Actual Value : 0");
        image = new JLabel();
        //if we show XZ
        BufferedImage tmp;
        if (rpp[3]) {
            tmp = Utils.Array2BufferedImageColor(PSFXZ[0]);
        }else{
            tmp = Utils.Array2BufferedImageColor(psf[0]);
        }
        //Icy.getMainInterface().addSequence(new Sequence(tmp));
        image.setIcon(new ImageIcon(tmp));
        //slider
        JSlider slide = new JSlider();
        if (rpp[3]) {
            slide.setMaximum((int)args[6]-1);
        }else{
            slide.setMaximum((int)args[7]-1);
        }
        
        slide.setMinimum(0);
        slide.setValue((int)alpha);
        slide.setPaintTicks(true);
        slide.setPaintLabels(true);
        slide.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent event){
                int tmp =(((JSlider)event.getSource()).getValue());
                BufferedImage imageZern;
                if (rpp[3]) {
                    imageZern = Utils.Array2BufferedImageColor(PSFXZ[tmp]);
                }else{
                    imageZern = Utils.Array2BufferedImageColor(psf[tmp]);
                }
                //Icy.getMainInterface().addSequence(new Sequence(imageZern));
                image.setIcon(new ImageIcon(imageZern));
                label.setText( "Actual value : "+tmp);
            }
        });
        
        JPanel middle = new JPanel();
        middle.setLayout(new FlowLayout());
        if (rpp[0]) {
            JLabel rholabel = new JLabel();
            rholabel.setIcon(new ImageIcon(Utils.Array2BufferedImageColor(pupil2.rho)));
            middle.add("label",new JLabel("RHO"));
            middle.add("image",rholabel);
        }
        if (rpp[1]) {
            JLabel philabel = new JLabel();
            philabel.setIcon(new ImageIcon(Utils.Array2BufferedImageColor(pupil2.phi)));
            middle.add("label",new JLabel("PHI"));
            middle.add("image",philabel);
        }
        if (rpp[2]) {
            JLabel psylabel = new JLabel();
            psylabel.setIcon(new ImageIcon(Utils.Array2BufferedImageColor(pupil2.psi)));
            middle.add("label",new JLabel("PSI"));
            middle.add("image",psylabel);
            
        }        
        JPanel bottom = new JPanel();
        bottom.setLayout(new BorderLayout());
        bottom.add(slide, BorderLayout.NORTH);
        bottom.add(label, BorderLayout.SOUTH);
        
        image.setVerticalAlignment(JLabel.CENTER);
        image.setHorizontalAlignment(JLabel.CENTER);
        
        test.setLayout(new BorderLayout());
        test.add(image, BorderLayout.NORTH);
        test.add(middle, BorderLayout.CENTER);
        test.add(bottom, BorderLayout.SOUTH);
        //test.add(slide, BorderLayout.SOUTH);
        
        //fenetre
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
    public void stop(){
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
