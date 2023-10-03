package mitiv.jobs;

import mitiv.array.ShapedArray;
import mitiv.cost.DifferentiableCostFunction;
import mitiv.cost.HomogeneousFunction;
import mitiv.array.DoubleArray;
import mitiv.array.FloatArray;
import mitiv.linalg.shaped.ShapedVector;
import mitiv.weights.WeightUpdater;


/**
 * @author ferreol
 *
 */
public class AmorsJob {
	
    private int totalNbOfBlindDecLoop;
    private DeconvolutionJob Objdeconvolver;
    private DeconvolutionJob PSFdeconvolver;
    private WeightUpdater wghtUpdt =null;

    protected boolean run=true;    
	private boolean debug=false;
	private double alpha=1.0;
	private double atol=1.0;
	private boolean single = false;
	private boolean succes = true;

    public AmorsJob(int totalNbOfBlindDecLoop, 
					DeconvolutionJob Objdeconvolver, DeconvolutionJob PSFdeconvolver,
					WeightUpdater wghtUpdt, boolean debug ){
		this.totalNbOfBlindDecLoop = totalNbOfBlindDecLoop;
		this.Objdeconvolver = Objdeconvolver;
		this.PSFdeconvolver = PSFdeconvolver;
		this.debug = debug;
		this.wghtUpdt =wghtUpdt;

    }
	
    public boolean blindDeconv(ShapedArray objArray,ShapedArray psfArray){
        run =true;
		Objdeconvolver.setInitialSolution(objArray);
		PSFdeconvolver.setInitialSolution(psfArray);
		
		
		Objdeconvolver.updatePsf(psfArray);	
		Objdeconvolver.solver.iterate();	// one iteration to set best solution
		
		PSFdeconvolver.updatePsf(objArray);	
		PSFdeconvolver.solver.iterate();	// one iteration to set best solution
		for(int iter = 0; iter < totalNbOfBlindDecLoop; iter++) {
			do {

				alpha = best_factor(Objdeconvolver,PSFdeconvolver);
				if (alpha != 1.0){
					scale(objArray, alpha);
					scale(psfArray, 1./alpha);
				}
            	//Emergency stop
            	if (!run) {
                	return succes;
        	    }
				Objdeconvolver.updatePsf(psfArray);	
				objArray = Objdeconvolver.deconv(objArray);	

				alpha = best_factor(Objdeconvolver,PSFdeconvolver);

				if (alpha != 1.0){
					scale(objArray, alpha);
					scale(psfArray, 1./alpha);
				}

			}while (iter < 1 && Math.abs(alpha - 1.0) > atol);
			
			Objdeconvolver.updateWeight(wghtUpdt.update(PSFdeconvolver));

            //Emergency stop
            if (!run) {
                return succes;
            }


			PSFdeconvolver.updatePsf(objArray);	
			psfArray = PSFdeconvolver.deconv(psfArray);	
			Objdeconvolver.updateWeight(wghtUpdt.update(PSFdeconvolver));

            //Emergency stop
            if (!run) {
                return succes;
            }

		}
        run = false;
        return succes;
    }


	private void scale(ShapedArray objArray, double alpha) {
		if(single){
			((FloatArray) objArray).scale((float)alpha);
		}
		else{
			((DoubleArray) objArray).scale(alpha) ;
		}
	}

	/**
     * Check whether the blind deconvolution is running
     * @return run
     */
    public boolean isRunning() {
        return run;
    }

    /**
     * Emergency stop
     */
    public void abort(){
        System.out.println("abort");
        run  = false;
        Objdeconvolver.abort();
        PSFdeconvolver.abort();
    }


    /**
     * Return the convolved object (model)
     * @return model
     */
    public ShapedArray getModel() {
        return Objdeconvolver.getModel();
    }

    private double best_factor(DeconvolutionJob objdeconvolver, DeconvolutionJob PSFdeconvolver) {
		ShapedVector solution = objdeconvolver.solver.getBestSolution();
		double lambda = objdeconvolver.solver.getRegularizationLevel();
		DifferentiableCostFunction objregul = objdeconvolver.solver.getRegularization();
		double lambdaJx  = objregul.evaluate(lambda,solution);
		double q = ((HomogeneousFunction) objregul).getHomogeneousDegree();

		double mu = PSFdeconvolver.solver.getRegularizationLevel();
		DifferentiableCostFunction PSFregul = PSFdeconvolver.solver.getRegularization();
		double muKy  = PSFregul.evaluate(mu,solution);
		double r = ((HomogeneousFunction) PSFregul).getHomogeneousDegree();

		return best_factor( lambdaJx,  q,  muKy,  r);
	}

	public double best_factor(double λJx, double q, double µKy, double r){ 
   		return Math.pow((r*µKy)/(q*λJx),(1.0/(q + r)));
	}
}
