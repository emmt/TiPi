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
    
    double [][][] psf;
    double PSFXZ[][][];
    boolean[] rpp;
    
    IcyFrame test;
    MicroscopyModelPSF pupil;
    /**
     * Take a psf and a image, and give the method used to compute the solution
     * 
     * @param pathPsf   Can be a string, icyimage, bufferedImage
     * @param pathImage Can be a string, icyimage, bufferedImage
     * @param quadratic Boolean to enble the use quadatric computation method
     */
    public ZernikeWindowSlim(MicroscopyModelPSF pupil,  boolean[] rpp){
        this.pupil = pupil;
        this.rpp = rpp;
    }

    @Override
    public void run() {
        test = new IcyFrame();
 
        JPanel middle = new JPanel();
        middle.setLayout(new FlowLayout());
        if (rpp[0]) {
            JLabel rholabel = new JLabel();
            rholabel.setIcon(new ImageIcon(CommonUtils.array2BuffI(pupil.getRho())));
            middle.add("label",new JLabel("RHO"));
            middle.add("image",rholabel);
        }
        if (rpp[1]) {
            JLabel philabel = new JLabel();
            philabel.setIcon(new ImageIcon(CommonUtils.array2BuffI(pupil.getPhi())));
            middle.add("label",new JLabel("PHI"));
            middle.add("image",philabel);
        }
        if (rpp[2]) {
            JLabel psylabel = new JLabel();
            psylabel.setIcon(new ImageIcon(CommonUtils.array2BuffI(pupil.getPsi())));
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
