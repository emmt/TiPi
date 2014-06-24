package plugins.mitiv.microscopy;

import icy.sequence.Sequence;

import java.awt.EventQueue;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mitiv.utils.CommonUtils;
import mitiv.utils.MathUtils;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzStoppable;
import plugins.adufour.ezplug.EzVarBoolean;
import plugins.adufour.ezplug.EzVarDouble;

/**
 * MicroscopyModel can generate PSF thanks to zernike polynoms
 * 
 * @author Leger Jonathan
 *
 */
public class MicroscopyModel extends EzPlug implements EzStoppable
{
    //Mydata
    EzVarDouble na;
    EzVarDouble lambda;
    EzVarDouble ni;
    EzVarDouble dxy;
    EzVarDouble dz;
    EzVarDouble nx;
    EzVarDouble ny;
    EzVarDouble nz;
    EzVarDouble j;
    EzVarDouble deltax;
    EzVarDouble deltay;
    EzVarDouble alpha;
    EzVarDouble beta;

    EzVarBoolean rho;
    EzVarBoolean phi;
    EzVarBoolean psi;

    EzVarBoolean xz;

    JSlider slider;
    JLabel label;
    //Zernike part

    double [][][] psf;
    double PSFXZ[][][];    
    MicroscopyModelPSF2D2 pupil2;
    double[] args;
    boolean[] rpp;
    //icy part
    Sequence myseq;
    ZernikeWindowSlim zernSlim;
    @Override
    protected void initialize()
    {
        na = new EzVarDouble("Numerical Aperture");
        lambda = new EzVarDouble("Wavelength (nm)");
        ni = new EzVarDouble("Immersion Index");
        dxy = new EzVarDouble("Lateral pixel size (nm)");
        dz = new EzVarDouble("Axial pixel size (um)");
        nx = new EzVarDouble("Number of samples along lateral X-dimension");
        ny = new EzVarDouble("Number of samples along lateral Y-dimension");
        nz = new EzVarDouble("Number of samples along lateral Z-dimension");
        j = new EzVarDouble("Number of zernike modes");
        deltax = new EzVarDouble("Defocus along lateral X-dimension");
        deltay = new EzVarDouble("Defocus along lateral Y-dimension");
        alpha = new EzVarDouble("Zernike coefficients: Alpha");
        beta = new EzVarDouble("Zernike coefficients: Beta");

        rho = new EzVarBoolean("Show rho", false);
        phi = new EzVarBoolean("Show phi", false);
        psi = new EzVarBoolean("Show psi", false);

        xz = new EzVarBoolean("PSF XZ", false);

        na.setValue(1.4);
        lambda.setValue(542.0);
        ni.setValue(1.518);
        dxy.setValue(64.5);
        dz.setValue(0.16);
        nx.setValue(256.0);
        ny.setValue(256.0);
        nz.setValue(64.0);
        j.setValue(10.0);
        deltax.setValue(0.0);
        deltay.setValue(0.0);
        alpha.setValue(1.0);
        beta.setValue(1.0);

        slider = new JSlider(0,100,0);
        slider.setEnabled(false);
        label = new JLabel("               ");

        super.addEzComponent(na);
        super.addEzComponent(lambda);
        super.addEzComponent(ni);
        super.addEzComponent(dxy);
        super.addEzComponent(dz);
        super.addEzComponent(nx);
        super.addEzComponent(ny);
        super.addEzComponent(nz);
        super.addEzComponent(j);
        super.addEzComponent(deltax);
        super.addEzComponent(deltay);
        super.addEzComponent(alpha);
        super.addEzComponent(beta);

        super.addEzComponent(rho);
        super.addEzComponent(phi);
        super.addEzComponent(psi);

        super.addEzComponent(xz);

        super.addComponent(slider);
        super.addComponent(label);
    }

    @Override
    protected void execute()
    {
        args = new double[13];
        args[0] = na.getValue();
        args[1] = lambda.getValue()*1e-9;
        args[2] = ni.getValue();
        args[3] = dxy.getValue()*1e-9;
        args[4] = dz.getValue()*1e-6;
        args[5] = nx.getValue();
        args[6] = ny.getValue();
        args[7] = nz.getValue();
        args[8] = j.getValue();
        args[9] = deltax.getValue();
        args[10] = deltay.getValue();
        args[11] = alpha.getValue();
        args[12] = beta.getValue();

        rpp = new boolean[4];
        rpp[0] = rho.getValue();
        rpp[1] = phi.getValue();
        rpp[2] = psi.getValue();
        rpp[3] = xz.getValue();
        /*if (prev != null) {
            //prev.stop();
        }
        ZernikeWindow zern = new ZernikeWindow(args,rpp);
        EventQueue.invokeLater(zern);
        prev = zern;*/

        psf = new double[(int)args[7]][(int)args[6]][(int)args[5]];
        pupil2 = new MicroscopyModelPSF2D2(args[0], args[1], args[2], args[3], args[4], (int)args[5], (int)args[6], (int)args[7], (int)args[8], (int)(args[5]*args[6]));
        pupil2.computePSF(psf, new double[]{args[11]}, new double[]{args[12]}, args[9], args[10]);
        PSFXZ = MathUtils.XY2XZ(psf);

        if (myseq != null) {
            myseq.close();
        }
        myseq = new Sequence();
        //If we show the xz visualization
        if (rpp[3]) {
            for (int i = 0; i < (int)args[6]-1; i++) {
                myseq.setImage(i,0,CommonUtils.array2BufferedImageColor(PSFXZ[i]));
            }
            
        }else{
            for (int i = 0; i < (int)args[7]-1; i++) {
                myseq.setImage(i,0,CommonUtils.array2BufferedImageColor(psf[i]));
            }
            
        }
        addSequence(myseq);

        slider.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent event){
                int tmp =(((JSlider)event.getSource()).getValue());

                if (rpp[3]) {
                    myseq.setImage(0,0,CommonUtils.array2BufferedImageColor(PSFXZ[tmp]));
                }else{
                    myseq.setImage(0,0,CommonUtils.array2BufferedImageColor(psf[tmp]));
                }

                label.setText( "Actual value : "+tmp);
            }
        });
        //some slider adjustement
        if (rpp[3]) {
            slider.setMaximum((int)args[6]-1);
        }else{
            slider.setMaximum((int)args[7]-1);
        }
        //Show rho phi psi
        if (zernSlim != null) {
            zernSlim.close();
        }
        if (rpp[0] || rpp[1] || rpp[2]) {
            zernSlim = new ZernikeWindowSlim(pupil2, rpp);
            EventQueue.invokeLater(zernSlim); 
        }
        slider.setEnabled(true);
        slider.setValue(0);
    }

    @Override
    public void clean()
    {
        if (zernSlim != null) {
            zernSlim.close();
        }
        if (myseq != null) {
            myseq.close();
        }
    }

    @Override
    public void stopExecution()
    {

    }

    /**
     * 
     */
    public double getNa() {
        return na.getValue();
    }

    /**
     * 
     */
    public double getLambda() {
        return lambda.getValue()*1e-9;
    }

    /**
     * 
     */
    public double getNi() {
        return ni.getValue();
    }

    /**
     * 
     */
    public double getDxy() {
        return dxy.getValue()*1e-9;
    }

    /**
     * 
     */
    public double getDz() {
        return dz.getValue()*1e-6;
    }

    /**
     * 
     */
    public double getNx() {
        return nx.getValue();
    }

    /**
     * 
     */
    public double getNy() {
        return ny.getValue();
    }

    /**
     * 
     */
    public double getNz() {
        return nz.getValue();
    }

    /**
     * 
     */
    public double getJ() {
        return j.getValue();
    }

    /**
     * 
     */
    public double getDeltax() {
        return deltax.getValue();
    }

    /**
     * 
     */
    public double getDeltay() {
        return deltay.getValue();
    }

    /**
     * 
     */
    public double getAlpha() {
        return alpha.getValue();
    }

    /**
     * 
     */
    public double getBeta() {
        return beta.getValue();
    }

    /**
     * 
     */
    public boolean getRho() {
        return rho.getValue();
    }

    /**
     * 
     */
    public boolean getPhi() {
        return phi.getValue();
    }

    /**
     * 
     */
    public boolean getPsi() {
        return psi.getValue();
    }

    /**
     * *
     */
    public boolean getXz() {
        return xz.getValue();
    }

}