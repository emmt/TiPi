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

package mitiv.microscopy;

import mitiv.array.ArrayFactory;
import mitiv.array.DoubleArray;
import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.deconv.WeightedConvolutionCost;
import mitiv.invpb.ReconstructionJob;
import mitiv.invpb.ReconstructionSynchronizer;
import mitiv.invpb.ReconstructionViewer;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;
import mitiv.optim.BoundProjector;
import mitiv.optim.LineSearch;
import mitiv.optim.MoreThuenteLineSearch;
import mitiv.optim.OptimTask;
import mitiv.optim.ReverseCommunicationOptimizer;
import mitiv.optim.VMLMB;
import mitiv.utils.MathUtils;

public class PSF_Estimation implements ReconstructionJob {

    private double mu = 10.0;
    private double epsilon = 1.0;
    private double gatol = 0.0;
    private double grtol = 1e-3;
    private int limitedMemorySize = 5;
    private double lowerBound = Double.NEGATIVE_INFINITY;
    private double upperBound = Double.POSITIVE_INFINITY;
    private boolean debug = false;
    private int maxiter = 20;
    private int maxeval = 20;
    private DoubleArray data = null;
    private ShapedArray obj = null;
    private DoubleArray result = null;
    private ShapedArray psf = null;
    private double fcost = 0.0;
    private DoubleShapedVector gcost = null;
    private WideFieldModel pupil = null;
    private ReverseCommunicationOptimizer minimizer = null;
    private ReconstructionViewer viewer = null;
    private ReconstructionSynchronizer synchronizer = null;
    private double[] synchronizedParameters = {0.0, 0.0};
    private boolean[] change = {false, false};
    private DoubleArray weights = null;

    public static final int DEFOCUS = 1;
    public static final int ALPHA = 2;
    public static final int BETA = 3;
    public void createSynchronizer() {
        if (synchronizer == null) {
            synchronizedParameters[0] = mu;
            synchronizedParameters[1] = epsilon;
            synchronizer = new ReconstructionSynchronizer(synchronizedParameters);
        }
    }
    public void deleteSynchronizer() {
        synchronizer = null;
    }

    public void enablePositivity(Boolean positivity) {
        setLowerBound(positivity ? 0.0 : Double.NEGATIVE_INFINITY);
    }
    // FIXME: names should be part of the synchronizer...
    // public String getSynchronizedParameterName(int i) {
    //     return synchronizedParameterNames[i];
    // }

    private boolean run = true;

    public PSF_Estimation() {

    }
    public PSF_Estimation(WideFieldModel pupil) {
        if (pupil!=null){
            this.pupil = pupil;
        }else{
            fatal("pupil not specified");
        }
    }
    private static void fatal(String reason) {
        throw new IllegalArgumentException(reason);
    }


    public void fitPSF(DoubleShapedVector x, int flag) {
        // FIXME set a best X
        double best_cost = Double.POSITIVE_INFINITY;
        // Check input data and get dimensions.
        if (data == null) {
            fatal("Input data not specified.");
        }
        Shape dataShape = data.getShape();
        Shape xShape = x.getShape();
        int rank = data.getRank();
        DoubleShapedVectorSpace dataSpace = new DoubleShapedVectorSpace(dataShape);
        DoubleShapedVector best_x = x.clone();
        // Check the PSF.
        if (obj == null) {
            fatal("Object not specified.");
        }
        if (obj.getRank() != rank) {
            fatal("Obj must have same rank as data.");
        }
        for (int k = 0; k < rank; ++k) {
            if (obj.getDimension(k) != dataShape.dimension(k)) {
                fatal("The dimensions of the PSF must match those of the input image.");
            }
        }

        DoubleShapedVectorSpace objSpace = new DoubleShapedVectorSpace(dataShape);

        if (result != null) {
            /* We try to keep the previous result, at least its dimensions
             * must match. */
            for (int k = 0; k < rank; ++k) {
                if (result.getDimension(k) != data.getDimension(k)) {
                    result = null;
                    break;
                }
            }
        }

        // Initialize a vector space and populate it with workspace vectors.

        DoubleShapedVectorSpace variableSpace = x.getSpace();
        result = ArrayFactory.wrap(x.getData(), xShape);
        int[] off ={0,0, 0};
        // Build convolution operator.
        WeightedConvolutionCost fdata = WeightedConvolutionCost.build(objSpace, dataSpace);
        fdata.setPSF(obj,off);
        fdata.setWeightsAndData(weights, data);

        if (debug) {
            System.out.println("Vector space initialization complete.");
        }

        // Build the cost functions
        //   fcost = 0.0;
        gcost = objSpace.create();
        fcost = fdata.computeCostAndGradient(1.0, objSpace.wrap(pupil.getPSF()), gcost, true);
        best_cost = fcost;
        best_x = x.clone();

        if (debug) {
            System.out.println("Cost function initialization complete.");
        }

        // Initialize the non linear conjugate gradient
        LineSearch lineSearch = null;
        VMLMB vmlmb = null;
        BoundProjector projector = null;
        int bounded = 0;
        limitedMemorySize = 0;

        if (lowerBound != Double.NEGATIVE_INFINITY) {
            bounded |= 1;
        }
        if (upperBound != Double.POSITIVE_INFINITY) {
            bounded |= 2;
        }


        if (debug) {
            System.out.println("bounded");
            System.out.println(bounded);
        }

        /* No bounds have been specified. */
        lineSearch = new MoreThuenteLineSearch(0.05, 0.1, 1E-17);

        int m = (limitedMemorySize > 1 ? limitedMemorySize : 5);
        vmlmb = new VMLMB(variableSpace, projector, m, lineSearch);
        vmlmb.setAbsoluteTolerance(gatol);
        vmlmb.setRelativeTolerance(grtol);
        minimizer = vmlmb;
        //

        if (debug) {
            System.out.println("Optimization method initialization complete.");
        }

        DoubleShapedVector gX = variableSpace.create();
        // Launch the non linear conjugate gradient
        OptimTask task = minimizer.start();
        while (run) {
            if (task == OptimTask.COMPUTE_FG) {
                if(flag == DEFOCUS)
                {
                    if (debug) {
                        System.out.println("--------------");
                        System.out.println("defocus");
                        MathUtils.printArray(x.getData());
                    }
                    pupil.setDefocus(x.getData());
                }
                else if (flag == ALPHA)
                {
                    if (debug) {
                        System.out.println("--------------");
                        System.out.println("alpha");
                        MathUtils.printArray(x.getData());
                    }
                    pupil.setPhi(x.getData());
                }
                else if(flag == BETA)
                {
                    if (debug) {
                        System.out.println("--------------");
                        System.out.println("beta");
                        MathUtils.printArray(x.getData());
                    }
                    pupil.setRho(x.getData());
                }
                pupil.computePSF();

                fcost = fdata.computeCostAndGradient(1.0, objSpace.wrap(pupil.getPSF()), gcost, true);

                if(fcost<best_cost){
                    best_cost = fcost;
                    best_x = x.clone();
                    System.out.println("Cost: " + best_cost);
                }
                if(flag == DEFOCUS)
                {
                    gX = variableSpace.wrap(pupil.apply_J_defocus(gcost.getData()));
                    if (debug) {
                        System.out.println("grdx");
                        MathUtils.stat(gcost.getData());
                        System.out.println("grd");
                        MathUtils.printArray(gX.getData());
                    }
                }
                else if (flag == ALPHA)
                {
                    gX = variableSpace.wrap(pupil.apply_J_phi(gcost.getData()));
                    if (debug) {
                        System.out.println("grd");
                        MathUtils.printArray(gX.getData());
                    }
                }
                else if(flag == BETA)
                {
                    gX = variableSpace.wrap(pupil.apply_J_rho(gcost.getData()));
                    if (debug) {
                        System.out.println("grdx");
                        MathUtils.stat(gcost.getData());
                        System.out.println("grd");
                        MathUtils.printArray(gX.getData());
                    }
                }
            } else if (task == OptimTask.NEW_X || task == OptimTask.FINAL_X) {
                if (viewer != null) {
                    viewer.display(this);
                }
                boolean stop = (task == OptimTask.FINAL_X);
                if (! stop && maxiter >= 0 && minimizer.getIterations() >= maxiter) {
                    if (debug){
                        System.out.format("Warning: too many iterations (%d).\n", maxiter);
                    }
                    stop = true;
                }
                if (stop) {
                    break;
                }
            } else {
                if (debug){
                    System.out.println("TiPi: PSF_Estimation, "+task+" : "+minimizer.getReason());
                }
                break;
            }
            if (synchronizer != null) {
                /* FIXME: check the values, suspend/resume, restart the algorithm, etc. */
                if (synchronizer.getTask() == ReconstructionSynchronizer.STOP) {
                    break;
                }
                synchronizedParameters[0] = mu;
                synchronizedParameters[1] = epsilon;
                if (synchronizer.updateParameters(synchronizedParameters, change)) {
                    if (change[0]) {
                        mu = synchronizedParameters[0];
                    }
                    if (change[1]) {
                        epsilon = synchronizedParameters[1];
                    }
                    // FIXME: restart!!!
                }
            }
            if (debug) {
                System.out.println("Evaluations");
                System.out.println(minimizer.getEvaluations());
                System.out.println("Iterations");
                System.out.println(minimizer.getIterations());
            }

            if (minimizer.getEvaluations() >= maxeval) {
                System.err.format("Warning: too many evaluation (%d).\n", maxeval);
                break;
            }
            task = minimizer.iterate(x, fcost, gX);
        }

        if(flag == DEFOCUS)
        {
            if (debug) {
                System.out.println("--------------");
                System.out.println("defocus");
                MathUtils.printArray(best_x.getData());
            }
            pupil.setDefocus(best_x.getData());
        }
        else if (flag == ALPHA)
        {
            if (debug) {
                System.out.println("--------------");
                System.out.println("alpha");
                MathUtils.printArray(best_x.getData());
            }
            pupil.setPhi(best_x.getData());
        }
        else if(flag == BETA)
        {
            if (debug) {
                System.out.println("--------------");
                System.out.println("beta");
                MathUtils.printArray(best_x.getData());
            }
            pupil.setRho(best_x.getData());
        }
    }

    /* Below are all methods required for a ReconstructionJob. */

    public void setDebugMode(boolean value) {
        debug = value;
    }
    public void setMaximumIterations(int value) {
        maxiter = value;
        maxeval = 2* value; // 2 or 3 times more evaluations than iterations seems reasonable
    }
    public void setLimitedMemorySize(int value) {
        limitedMemorySize = value;
    }
    public void setAbsoluteTolerance(double value) {
        gatol = value;
    }
    public void setRelativeTolerance(double value) {
        grtol = value;
    }
    @Override
    public double getRelativeTolerance() {
        return grtol;
    }
    //the same effect with preMade value is enablePositivity()
    public void setLowerBound(double value) {
        lowerBound = value;
    }
    public void setUpperBound(double value) {
        upperBound = value;
    }
    public void stop(){
        run = false;
    }
    public void start(){
        run = true;
    }
    public void setWeight(DoubleArray W){
        this.weights = W;
    }
    public ReconstructionViewer getViewer() {
        return viewer;
    }
    public void setViewer(ReconstructionViewer rv) {
        viewer = rv;
    }
    public ReconstructionSynchronizer getSynchronizer() {
        return synchronizer;
    }
    public void setPupil(WideFieldModel pupil) {
        this.pupil = pupil;
    }
    public WideFieldModel getPupil() {
        return pupil;
    }
    public DoubleArray getData() {
        return data;
    }

    public void setData(DoubleArray data) {
        this.data = data;
    }

    public ShapedArray getPsf() {
        return psf;
    }
    public void setPsf(DoubleArray psf) {
        this.psf = psf;
    }

    @Override
    public DoubleArray getResult() {
        /* Nothing else to do because the actual result is in a vector
         * which shares the contents of the ShapedArray.  Otherwise,
         * some kind of synchronization is needed. */
        return result;
    }

    public void setResult(DoubleArray result) {
        this.result = result;
    }

    @Override
    public int getIterations() {
        return (minimizer == null ? 0 : minimizer.getIterations());
    }

    @Override
    public int getEvaluations() {
        return (minimizer == null ? 0 : minimizer.getEvaluations());
    }

    @Override
    public double getCost() {
        return fcost;
    }

    @Override
    public double getGradientNorm2() {
        return (gcost == null ? 0.0 : gcost.norm2());
    }

    @Override
    public double getGradientNorm1() {
        return (gcost == null ? 0.0 : gcost.norm1());
    }

    @Override
    public double getGradientNormInf() {
        return (gcost == null ? 0.0 : gcost.normInf());
    }
    public void setObj(ShapedArray obj) {
        // TODO Auto-generated method stub
        this.obj = obj;

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