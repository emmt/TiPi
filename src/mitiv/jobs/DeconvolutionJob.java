/**
 *
 */
package mitiv.jobs;

import mitiv.array.ShapedArray;
import mitiv.conv.WeightedConvolutionCost;
import mitiv.cost.DifferentiableCostFunction;
import mitiv.invpb.Deconvolution;
import mitiv.optim.OptimTask;
import mitiv.utils.TiPiHook;

/**
 * @author ferreol
 *
 */
public class DeconvolutionJob {
    protected Deconvolution solver;
    protected TiPiHook iterHook,finalHook;
    protected boolean run=true;

    public DeconvolutionJob(DifferentiableCostFunction fdata, double mu,
            DifferentiableCostFunction fprior, boolean positivity,
            int nbIterDeconv, TiPiHook iterHook, TiPiHook finalHook){

        solver = new  Deconvolution();

        solver.setLikelihood(fdata);
        solver.setRegularization(fprior);
        solver.setRegularizationLevel(mu);

        solver.setSaveBest(true);
        solver.setLowerBound(positivity ? 0.0 : Double.NEGATIVE_INFINITY);
        solver.setUpperBound(Double.POSITIVE_INFINITY);
        solver.setMaximumIterations(nbIterDeconv);
        solver.setMaximumEvaluations(2*nbIterDeconv);

        this.iterHook = iterHook;
        this.finalHook = finalHook;
    }

    /**
     * Perform deconvolution using objArray as initial guess
     * @param objArray
     * @return deconvolved array
     */
    public ShapedArray deconv(ShapedArray objArray){
        int iter=0;
        run = true;
        solver.setInitialSolution(objArray);

        OptimTask task = solver.start();

        while (run) {
            task = solver.getTask();
            if (task == OptimTask.ERROR) {
                System.err.format("Error: %s\n", solver.getReason());
                break;
            }
            if (task == OptimTask.NEW_X || task == OptimTask.FINAL_X) {
                if(iterHook!=null){
                    iterHook.run(solver,iter++);
                }
                if (task == OptimTask.FINAL_X) {
                    break;
                }
            }
            if (task == OptimTask.WARNING) {
                break;
            }
            solver.iterate();
        }
        objArray = solver.getBestSolution().asShapedArray();
        finalHook.run(solver,iter);
        return objArray;
    }

    /**
     * Emergency stop
     */
    public void abort(){
        run = false;
    }

    /**
     * @return running state
     */
    public boolean isRunning() {
        return run;
    }

    /**
     * @param psfArray
     */
    public void updatePsf(ShapedArray psfArray) {
        WeightedConvolutionCost fdata = (WeightedConvolutionCost) solver.getLikelihood();
        fdata.setPSF(psfArray);
    }

    /**
     * @param wgtArray
     */
    public void updateWeight(ShapedArray wgtArray) {
        WeightedConvolutionCost fdata = (WeightedConvolutionCost) solver.getLikelihood();
        fdata.setWeights(wgtArray,true);
    }

    public ShapedArray getModel() {
        return solver.getModel();
    }

    public void setInitialSolution(ShapedArray objArray){
        solver.setInitialSolution(objArray);
    }

}
