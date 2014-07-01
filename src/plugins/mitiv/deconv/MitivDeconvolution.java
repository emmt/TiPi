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

package plugins.mitiv.deconv;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import mitiv.deconv.DeconvUtils;
import mitiv.deconv.Deconvolution;
import mitiv.utils.CommonUtils;
import plugins.adufour.ezplug.*;

/**
 * EzPlug interface to get the choices of the user
 * 
 * Full CODE see EzPlugTutorial
 * 
 * @author Leger Jonathan
 *
 */
public class MitivDeconvolution extends EzPlug implements EzStoppable,SequenceListener
{
    //Mydata
    EzVarText options;
    EzVarText correction;
    //EzVarBoolean	varBoolean;
    EzVarFile varFilePSF;
    EzVarFile varFileIMAGE;
    EzVarSequence sequencePSF;
    EzVarSequence sequenceImage;
    JSlider slider;

    String wiener = "Wiener";
    String quad = "Quadratic";
    String cg = "CG";

    String normal = "Normal";
    String corrected = "Corrected";
    String colormap = "Colormap";
    String correctColormap = "Corrected+Colormap";

    Sequence myseq;
    JLabel label;

    int job;
    int correct;
    Deconvolution deconvolution;

    ThreadCG thread;

    private final static double muMin = 1e-12;
    private final static double muMax = 1e1;
    private final static double muAlpha = Math.log(muMin);
    private final static double muBeta = Math.log(muMax/muMin)/1e2;

    private void updateLabel(double val){
        DecimalFormat df = new DecimalFormat("#.####");
        label.setText( "Actual Value : "+df.format(val));
    }

    public void updateProgressBarMessage(String msg){
        getUI().setProgressBarMessage(msg);
    }

    public static double sliderToRegularizationWeight(int slidervalue) {
        return Math.exp(muAlpha + muBeta*slidervalue);
    }

    /*
     * Yes I'm using == to compare 2 strings and yes this is what I want,
     * and yes it's working because getValue() return a string object that I compare
     * with himself
     */
    private int chooseCorrection(){
        if (correction.getValue() == normal) {
            return CommonUtils.SCALE;
        } else if(correction.getValue() == corrected){
            return CommonUtils.SCALE_CORRECTED;
        } else if (correction.getValue() == colormap) {
            return CommonUtils.SCALE_COLORMAP;
        } else if (correction.getValue() == correctColormap){
            return CommonUtils.SCALE_CORRECTED_COLORMAP;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private int chooseJob(){
        if (options.getValue().equals(wiener)) {
            return DeconvUtils.JOB_WIENER;
        } else if (options.getValue().equals(quad)) {
            return DeconvUtils.JOB_QUAD;
        } else if (options.getValue().equals(cg)) {
            return DeconvUtils.JOB_CG;
        }else{
            throw new IllegalArgumentException("Invalid Job");
        }
    }

    private BufferedImage firstJob(int job){
        thread = new ThreadCG(this);
        thread.start();
        switch (job) {
        //First value correspond to next job with alpha = 0, not all are equal to 1
        case DeconvUtils.JOB_WIENER: 
            return (deconvolution.FirstDeconvolution(muMin));
        case DeconvUtils.JOB_QUAD:
            return (deconvolution.FirstDeconvolutionQuad1D(muMin));
        case DeconvUtils.JOB_CG:
            return (deconvolution.FirstDeconvolutionCGNormal(muMin));
        default:
            throw new IllegalArgumentException("Invalid Job");
        }
    }

    public BufferedImage nextJob(int slidervalue, int job){
        double mu = sliderToRegularizationWeight(slidervalue);
        updateLabel(mu);
        double mult = 1E9; //HACK While the data uniformization is not done... TODO
        switch (job) {
        case DeconvUtils.JOB_WIENER:
            return (deconvolution.NextDeconvolution(mu));

        case DeconvUtils.JOB_QUAD:
            return (deconvolution.NextDeconvolutionQuad1D(mu*mult));

        case DeconvUtils.JOB_CG:
            return (deconvolution.NextDeconvolutionCGNormal(mu*mult));

        default:
            throw new IllegalArgumentException("Invalid Job");
        }
    }

    @Override
    protected void initialize()
    {
        sequencePSF = new EzVarSequence("PSF");
        sequenceImage = new EzVarSequence("Image");
        options = new EzVarText("Regularization", new String[] { wiener,quad,cg}, 0, false);
        correction = new EzVarText("Output", new String[] { normal,corrected,colormap,correctColormap}, 0, false);
        slider = new JSlider(0, 100, 0);
        slider.setEnabled(false);  
        label = new JLabel("                     ");
        super.addEzComponent(sequencePSF);
        super.addEzComponent(sequenceImage);
        super.addEzComponent(options);
        super.addEzComponent(correction);
        super.addComponent(slider);
        super.addComponent(label);
    }

    public void updateImage(BufferedImage buffered, int value){
        myseq.setName(options.getValue()+" "+correction.getValue()+" "+value);
        myseq.setImage(0, 0, buffered); 
    }

    @Override
    protected void execute()
    {
        correct = chooseCorrection();
        job = chooseJob();
        if (myseq != null) {
            myseq.close();
        }

        if(sequenceImage.getValue() == null || sequencePSF.getValue() == null){
            throw new IllegalArgumentException("We need a PSF and/or an image");
        }
        deconvolution = new Deconvolution(sequenceImage.getValue().getFirstNonNullImage(),
                sequencePSF.getValue().getFirstNonNullImage(),correct);

        myseq = new Sequence();
        myseq.addImage(0,firstJob(job));
        myseq.addListener(this); 
        addSequence(myseq);
        myseq.setName("");
        slider.setEnabled(true);

        slider.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent event){
                //getUI().setProgressBarMessage("Computation in progress");
                int sliderValue =(((JSlider)event.getSource()).getValue());
                updateProgressBarMessage("Computing");
                thread.prepareNextJob(sliderValue, job);
                //OMEXMLMetadataImpl metaData = new OMEXMLMetadataImpl();
                //myseq.setMetaData(metaData);
                //updateImage(buffered, tmp);
            }
        });  
        //Beware, need to be called at the END
        slider.setValue(0);
    }

    @Override
    public void clean()
    {
        if(myseq != null){
            myseq.close();
        }
        if(thread != null){
            thread.cancel();
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Erreur fin Thread "+e);
            }
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
        slider.setEnabled(false);   
    }

    public int getOutputValue(){
        return deconvolution.getOuputValue();
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