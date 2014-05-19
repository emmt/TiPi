package plugins.mitiv.deconv;

import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import mitiv.deconv.Deconvolution;
import mitiv.utils.DeconvUtils;
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

    /**
     * Job to compute with the wiener filter
     */
    public static final int JOB_WIENER = 1;
    /**
     * Job to compute using quadratic and circulant approximation
     */
    public static final int JOB_QUAD = 2;
    /**
     * Job to compute with Conjugate gradients
     */
    public static final int JOB_CG = 3;

    //Mydata
    EzVarText	varText;
    EzVarText  options;
    EzVarText  correction;
    //EzVarBoolean	varBoolean;
    EzVarFile	varFilePSF;
    EzVarFile	varFileIMAGE;
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
    
    private final static double muMin = 1e-12;
    private final static double muMax = 1e1;
    private final static double muAlpha = Math.log(muMin);
    private final static double muBeta = Math.log(muMax/muMin)/1e2;

    private int chooseCorrection(){
        if (correction.getValue() == normal) {
            return DeconvUtils.SCALE;
        } else if(correction.getValue() == corrected){
            return DeconvUtils.SCALE_CORRECTED;
        } else if (correction.getValue() == colormap) {
            return DeconvUtils.SCALE_COLORMAP;
        } else if (correction.getValue() == correctColormap){
            return DeconvUtils.SCALE_CORRECTED_COLORMAP;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private int chooseJob(){
        if (options.getValue().equals(wiener)) {
            return JOB_WIENER;
        } else if (options.getValue().equals(quad)) {
            return JOB_QUAD;
        } else if (options.getValue().equals(cg)) {
            return JOB_CG;
        }else{
            throw new IllegalArgumentException("Invalid Job");
        }
    }

    private BufferedImage firstJob(int job, Deconvolution deconvolution){
        switch (job) {
        //First value correspond to next job with alpha = 0, not all are equal to 1
        case JOB_WIENER: 
            return (deconvolution.FirstDeconvolution(muMin));
        case JOB_QUAD:
            return (deconvolution.FirstDeconvolutionQuad(muMin));
        case JOB_CG:
            return (deconvolution.FirstDeconvolutionCG(muMin));
        default:
            throw new IllegalArgumentException("Invalid Job");
        }
    }

    private void updateLabel(double val){
        label.setText( "Actual Value : "+val);
    }

    private static double sliderToRegularizationWeight(int slidervalue) {
        return Math.exp(muAlpha + muBeta*slidervalue);
    }
    private BufferedImage nextJob(int slidervalue, int job, Deconvolution deconvolution){
        double mu = sliderToRegularizationWeight(slidervalue);
        updateLabel(mu);
        double mult = 1E9; //HACK While the data uniformization is not done...
        switch (job) {
        case JOB_WIENER:
            return (deconvolution.NextDeconvolution(mu));

        case JOB_QUAD:
            return (deconvolution.NextDeconvolutionQuad(mu*mult));

        case JOB_CG:
            return (deconvolution.NextDeconvolutionCG(mu*mult));
            
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
        myseq.addImage(0,firstJob(job, deconvolution));
        myseq.addListener(this); 
        addSequence(myseq);
        myseq.setName("");
        slider.setEnabled(true);

        slider.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent event){
                //getUI().setProgressBarMessage("Computation in progress");
                int tmp =(((JSlider)event.getSource()).getValue());
                BufferedImage buffered = nextJob(tmp, job, deconvolution);
                getUI().setProgressBarMessage("Done");
                //OMEXMLMetadataImpl metaData = new OMEXMLMetadataImpl();
                //myseq.setMetaData(metaData);
                myseq.setName(options.getValue()+" "+correction.getValue()+" "+tmp);
                myseq.setImage(0, 0, buffered);           
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
}
