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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.crypto.Data;

import mitiv.array.Double2D;
import mitiv.array.Double3D;
import mitiv.array.DoubleArray;
import mitiv.exception.DataFormatException;
import mitiv.exception.RecoverableFormatException;
import mitiv.io.MdaFormat;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.microscopy.MicroscopyModelPSF1D;
import mitiv.utils.CommonUtils;
import mitiv.utils.MathUtils;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import icy.image.IcyBufferedImage;
import icy.type.DataType;
import icy.type.collection.array.Array1DUtil;
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
    EzVarText   varText;
    EzVarText  options;
    EzVarText  kernel;
    EzVarText  noise;
    //EzVarBoolean  varBoolean;
    EzVarFile   varFilePSF;
    EzVarFile   varFileIMAGE;
    EzVarSequence EzVarSequencePSF;
    EzVarSequence EzVarSequenceImage;
    EzVarSequence EzVarSequenceSave;
    JSlider slider;
    int sliderValue = 3;
    int w;
    int h;
    int d;
    double[] x;
    double[] psf;
    DoubleShapedVector y;

    String[] filters = {"no kernel", "average", "disk", "sobel", "prewitt", "kirsh"};
    String noNoise = "no noise";
    String gaussian = "gaussian";

    Sequence myseq;
    Sequence myseqData;
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


    @Override
    protected void initialize()
    {
        EzVarSequenceImage = new EzVarSequence("Image");
        EzVarSequencePSF = new EzVarSequence("Load PSF");

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

        super.addEzComponent(EzVarSequenceImage);
        super.addEzComponent(EzVarSequencePSF);
        super.addEzComponent(noise);
        /* Objet deconvolution button */
        EzButton saveMDAButton = new EzButton("Save to mda", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                saveMDA();
            }
        });

        EzButton loadSomething = new EzButton("Load data", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                loaData();
            }
        });

        addEzComponent(saveMDAButton);
        addEzComponent(loadSomething);
        super.addComponent(slider);
        super.addComponent(label);
    }

    @Override
    protected void execute()
    {
        if (myseq != null) {
            myseq.close();
        }

        Sequence seqImg = EzVarSequenceImage.getValue();
        Sequence seqPSF = EzVarSequencePSF.getValue();


        this.w = seqImg.getSizeX();
        this.h = seqImg.getSizeY();
        this.d = seqImg.getSizeZ();

        this.x = new double[w*h*d];
        this.psf = new double[w*h*d];
        for(int k = 0; k < d; k++)
        {
            Array1DUtil.arrayToDoubleArray(seqImg.getDataXY(0, k, 0), 0, x, k*w*h, w*h, seqImg.isSignedDataType());
            Array1DUtil.arrayToDoubleArray(seqPSF.getDataXY(0, k, 0), 0, psf, k*w*h, w*h, seqImg.isSignedDataType());
        }

        double[] psf_shift= MathUtils.fftShift3D(psf, w, h, d);
        y = MathUtils.convolution(x, psf_shift, w, h, d);

        Sequence seqY = new Sequence();
        seqY.setName("Y");
        for(int k = 0; k < d; k++)
        {
            seqY.addImage(new IcyBufferedImage(w, h, MathUtils.getArray(y.getData(), w, h, k)));
        }
        addSequence(seqY);

    }

    private void saveMDA()
    {
        System.out.println(w);
        MathUtils.stat(x);
        DoubleArray xArray =  Double3D.wrap(x, w, h, d);
        DoubleArray psfArray =  Double3D.wrap(psf, w, h, d);
        DoubleArray yArray = Double3D.wrap(y.getData(), w, h, d);
        try {
            //MdaFormat.save(xArray, "/home/ludo/Images/TestShrinkBars/barShrink_mda");
            MdaFormat.save(psfArray, "/home/ludo/Images/TestShrinkBars/psfGL_mda");
            MdaFormat.save(yArray, "/home/ludo/Images/TestShrinkBars/barShrinkBlurredGL_mda");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void loaData()
    {

            DoubleArray data;
            try {
                data = MdaFormat.load("/home/ludo/Images/TestShrinkBars/barShrink_mda").toDouble();
                Sequence seqD = new Sequence();
                seqD.setName("Y");
                for(int k = 0; k < 64; k++)
                {
                    seqD.addImage(new IcyBufferedImage(128, 128, MathUtils.getArray(data.flatten(), 128, 128, k)));
                }
                addSequence(seqD);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (DataFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (RecoverableFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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