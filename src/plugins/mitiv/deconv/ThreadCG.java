package plugins.mitiv.deconv;

import java.awt.image.BufferedImage;

public class ThreadCG extends Thread {
    
    MitivDeconvolution deconv;
    
    boolean stop = false;
    boolean hasjob = false;
    
    int nextJobValue;
    int nextJobJob;
    
    public ThreadCG(MitivDeconvolution deconv){
        this.deconv = deconv;
    }
    
    public void prepareNextJob(int tmp, int job){
        this.nextJobValue = tmp;
        this.nextJobJob = job;
        hasjob = true;
    }
    
    public void run() {
        while (!stop) {
            if (hasjob) {
            	hasjob = false; //first because if while computing a new job appear, we will not miss it
                BufferedImage buffered = deconv.nextJob(nextJobValue, nextJobJob);
                deconv.updateImage(buffered, nextJobValue);
                deconv.updateProgressBarMessage("Done");
            }
            try {
                sleep(100);
            } catch (Exception e) {
                System.err.println("Bad sleep thread");
            }
        }
    }
    
    public void cancel(){
        stop = true;
    }
  }
