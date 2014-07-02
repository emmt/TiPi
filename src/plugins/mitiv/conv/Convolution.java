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

package plugins.mitiv.conv;


import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mitiv.utils.CommonUtils;
import mitiv.utils.MathUtils;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import icy.image.IcyBufferedImage;
import icy.type.DataType;
import plugins.adufour.ezplug.*;

/**
 * EzPlug interface to get the choices of the user
 * 
 * Full CODE see EzPlugTutorial
 * 
 * @author Ludovic Beaubras
 *
 */
public class Convolution extends EzPlug implements EzStoppable,SequenceListener, EzVarListener<String>
{
    //Mydata
    EzVarText	varText;
    EzVarText  options;
    EzVarText  kernel;
    EzVarText  noise;
    //EzVarBoolean	varBoolean;
    EzVarFile	varFilePSF;
    EzVarFile	varFileIMAGE;
    EzVarSequence sequencePSF;
    EzVarSequence sequenceImage;
    JSlider slider;
    int sliderValue = 3;

    String[] filters = {"no kernel", "average", "disk", "sobel", "prewitt", "kirsh"};
    String noNoise = "no noise";
    String gaussian = "gaussian";

    Sequence myseq;
    Sequence myseq2;
    JLabel label;


    public void updateProgressBarMessage(String msg){
        getUI().setProgressBarMessage(msg);
    }

    public static final int GAUSSIAN = 2;
    public static final int AVERAGE = 3;
    public static final int PREWITT = 4;
    public static final int SOBEL = 5;
    public static final int KIRSH = 6;
    public static final int DISK = 7;
    
    private int getType(String type)
    {
        int out = 0;
        if (type.compareTo("average") == 0)
        {
            out = AVERAGE;
        }
        else if (type.compareTo("disk") == 0)
        {
            out = DISK;
        }
        else if (type.compareTo("sobel") == 0)
        {
            out = SOBEL;
        }
        else if (type.compareTo("prewitt") == 0)
        {
            out = PREWITT;
        }
        else if (type.compareTo("kirsh") == 0)
        {
            out = KIRSH;
        }
        return out;
    }
   
    private BufferedImage createPSF()
    {
        double[][] h;
        if (kernel.getValue().compareTo("average") == 0)
        {
            h = MathUtils.fspecial(AVERAGE, new int[]{sliderValue, sliderValue}, 0);
        }
        else if (kernel.getValue().compareTo("disk") == 0)
        {
            h = MathUtils.fspecial(DISK, sliderValue);
        }
        else
        {
            h = MathUtils.fspecial(getType(kernel.getValue()));
        }
        double[][] h_pad = CommonUtils.imgPad(h, 100,100, 1);
        return CommonUtils.array2BuffI(h_pad);
    }

    private IcyBufferedImage createPSF2()
    {
        IcyBufferedImage image = new IcyBufferedImage(256, 256, 1, DataType.DOUBLE);
        double[] dataBuffer = image.getDataXYAsDouble(0);
        double[][] h = MathUtils.fspecial(getType(kernel.getValue()));
        double[][] h_pad = CommonUtils.imgPad(h, 256,256, 1);
        for (int x = 0; x < 256; x++)
            for (int y = 0; y < 256; y++)
                dataBuffer[x + y * 256] = h_pad[x][y];
        return image;
    }

    private double[][] computePSF(String name)
    {
        double[][] h;
        if (kernel.getValue().compareTo("average") == 0)
        {
            h = MathUtils.fspecial(getType(kernel.getValue()), new int[]{sliderValue, sliderValue}, 0);
        }
        else if (kernel.getValue().compareTo("disk") == 0)
        {
            h = MathUtils.fspecial(getType(kernel.getValue()), sliderValue);
        }
        else
        {
            h = MathUtils.fspecial(getType(kernel.getValue()));
        }
        return h;
    }

    private double[][] convolve(double[][] I, double[][] PSF)
    {
        double[][] Iout = MathUtils.fftConv(I, PSF);
        return Iout;
    }

    private BufferedImage convolve()
    {
        /* Convert buffered image to array */
        double[][] I = CommonUtils.buffI2array(sequenceImage.getValue().getFirstNonNullImage());
        /* Create the kernel filter and FFT padding */

        /* If no noise and no kernel --> return I */
        if (kernel.getValue().compareTo(filters[0]) == 0 & noise.getValue().compareTo(noNoise) == 0)
        {
            return CommonUtils.array2BuffI(I);
        }
        /* If kernel and no noise --> return I_filtered_noNoise */
        else if (kernel.getValue().compareTo(filters[0]) != 0 & noise.getValue().compareTo(noNoise) == 0)
        {
            double[][] h;
            if (kernel.getValue().compareTo("average") == 0)
            {
                h = MathUtils.fspecial(getType(kernel.getValue()), new int[]{sliderValue, sliderValue}, 0);
            }
            else if (kernel.getValue().compareTo("disk") == 0)
            {
                h = MathUtils.fspecial(getType(kernel.getValue()), sliderValue);
            }
            else
            {
                h = MathUtils.fspecial(getType(kernel.getValue()));
            }
            double[][] h_pad = CommonUtils.imgPad(h, I, -1);
            double[][] I_filtered_noNoise = MathUtils.fftConv(I, h_pad);
            return CommonUtils.array2BuffI(I_filtered_noNoise);
        }
        /* If no kernel but noise --> return I */
        else if(kernel.getValue().compareTo(filters[0]) == 0 & noise.getValue().compareTo(noNoise) != 0)
        {
            double noiseMean = 0;
            double noiseVar = 0.01;
            double[][] I_unfiltered_noise = MathUtils.imnoise(I, getType(noise.getValue()), noiseVar, noiseMean);
            return CommonUtils.array2BuffI(I_unfiltered_noise);
        }else
            /* Filtered with noise --> return I_filtered_noise */
        {
            double[][] h;
            if (kernel.getValue().compareTo("average") == 0)
            {
                h = MathUtils.fspecial(getType(kernel.getValue()), new int[]{sliderValue, sliderValue}, 0);
            }
            else if (kernel.getValue().compareTo("disk") == 0)
            {
                h = MathUtils.fspecial(getType(kernel.getValue()), sliderValue);
            }
            else
            {
                h = MathUtils.fspecial(getType(kernel.getValue()));
            }
            double[][] h_pad = CommonUtils.imgPad(h, I, -1);
            /* Convolve using FFT */
            double[][] I_filtered = MathUtils.fftConv(I, h_pad);
            double noiseMean = 0;
            double noiseVar = 0.01;
            double[][] I_filtered_noise = MathUtils.imnoise(I_filtered, getType(noise.getValue()), noiseVar, noiseMean);
            return CommonUtils.array2BuffI(I_filtered_noise);
        }
    }



    @Override
    protected void initialize()
    {
        sequenceImage = new EzVarSequence("Image");
        kernel = new EzVarText("Kernel", filters, 0, false);
        kernel.addVarChangeListener(this);
        noise = new EzVarText("Noise", new String[] {noNoise, gaussian}, 0, false);
        slider = new JSlider(0, 100, sliderValue);
        slider.setEnabled(false);  
        label = new JLabel("Value : " + sliderValue);
        slider.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent event){
                sliderValue =(((JSlider)event.getSource()).getValue());
                label.setText("Value : " + sliderValue);
            }
        });

        super.addEzComponent(sequenceImage);
        super.addEzComponent(kernel);
        super.addEzComponent(noise);
        super.addComponent(slider);
        super.addComponent(label);
    }

    @Override
    protected void execute()
    {
        if (myseq != null) {
            myseq.close();
        }

        if(sequenceImage.getValue() == null){
            throw new IllegalArgumentException("We need an image");
        }

        myseq = new Sequence();
        myseq.addImage(0, convolve());
        myseq.addListener(this); 
        addSequence(myseq);
        myseq.setName("Blurred I");

        if (myseq2 != null) {
            myseq2.close();
        }

        myseq2 = new Sequence();
        // myseq2.addImage(0, createPSF());
        myseq2.addImage(0, createPSF());
        myseq2.addListener(this); 
        addSequence(myseq2);
        myseq2.setName("PSF");
    }

    @Override
    public void clean()
    {
        if(myseq != null){
            myseq.close();
        }
    }

    @Override
    public void stopExecution()
    {

    }

    @Override
    public void sequenceChanged(SequenceEvent sequenceEvent) {

    }

    @Override
    public void sequenceClosed(Sequence sequence) {
        //slider.setEnabled(false);   
    }
    @Override
    public void variableChanged(EzVar<String> source, String newValue) {
        boolean chosen = newValue.compareTo("average") == 0 || newValue.compareTo("disk") == 0;
        slider.setEnabled(chosen);
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