/**
 *
 */
package mitiv.utils;

import java.util.stream.IntStream;

import mitiv.array.ArrayFactory;
import mitiv.array.Double1D;
import mitiv.array.DoubleArray;
import mitiv.array.FloatArray;
import mitiv.array.Int1D;
import mitiv.array.ShapedArray;
import mitiv.base.Traits;
import mitiv.base.indexing.Range;
import mitiv.base.mapping.DoubleFunction;
import mitiv.base.mapping.FloatFunction;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.ShapedVector;

/**
 * @author ferreol
 *
 */
public class HistoMap {
    private double alpha;

    private double beta;

    private boolean computationRequired = true;

    /** Number of finite values. */
    protected Int1D count = null;

    /** Sum of all finite data values. */
    protected Int1D histo = null;

    /** Number of finite values. */
    protected Double1D mean = null;

    /** Number of NaN values. */
    protected int nans = 0;
    /** Number of bin. */
    protected int nbin = 1;
    /** Number of negative infinite values. */
    protected int neginfs = 0;

    /** Number of positive infinite values. */
    protected int posinfs = 0;

    protected Double1D var = null;

    /** Maximum finite value or NaN. */
    protected double vmax = Double.NaN;

    /** Minimum finite value or NaN. */
    protected double vmin = Double.NaN;



    /**
     * Create an histogram from the values of a shaped array.
     */
    public HistoMap(DoubleArray truth, DoubleArray data) {
        update(truth, data,null);
    }

    /**
     * Create an histogram from the values of a shaped vector.
     *
     * @param vec
     *        A shaped vector.
     */
    public HistoMap(DoubleShapedVector truth, DoubleShapedVector data) {
        update(truth, data,null);
    }


    /**
     * Create an histogram from the values of a shaped array.
     */
    public HistoMap(ShapedArray truth, ShapedArray data, ShapedArray badpix) {
        update(truth.toDouble(), data.toDouble(),badpix);
    }

    /**
     * Create an histogram from the values of a shaped vector.
     *
     * @param vec
     *        A shaped vector.
     */
    public HistoMap(ShapedVector truth, ShapedVector data, ShapedArray badpix) {
        update(truth, data,badpix);
    }


    /**
     * Get maximum finite value.
     *
     * @return The maximum finite value, may be NaN if there are no finite
     *         values.
     */
    public double getMaximumValue() {
        return vmax;
    }

    /**
     * Get minimum finite value.
     *
     * @return The minimum finite value, may be NaN if there are no finite
     *         values.
     */
    public double getMinimumValue() {
        return vmin;
    }

    /**
     * Get number of negative infinite values.
     *
     * @return The number of negative infinite values.
     */
    public int getNumberOfBins() {
        return nbin;
    }

    /**
     * Get number of NaN values.
     *
     * @return The number of NaN values.
     */
    public int getNumberOfNaNs() {
        return nans;
    }

    /**
     * Get number of positive infinite values.
     *
     * @return The number of positive infinite values.
     */
    public int getNumberOfPositiveInfinites() {
        return posinfs;
    }



    /**
     * @return the alpha
     */
    public double getAlpha() {
        if (computationRequired ) {
            compute();
            computationRequired = false;
        }
        return alpha;
    }

    /**
     * @return the beta
     */
    public double getBeta() {
        if (computationRequired ) {
            compute();
            computationRequired = false;
        }
        return beta;
    }
    public ShapedArray computeWeightMap(ShapedArray model) {
        if (computationRequired ) {
            compute();
            computationRequired = false;
        }
        ShapedArray wgt = model.copy();
        if (wgt.getType() == Traits.FLOAT) {
            ((FloatArray) wgt).map(new FloatPrecisionlaw());
        }else {
            ((DoubleArray) wgt).map(new DoublePrecisionlaw());
        }
        return wgt;
    }

    public Int1D getAxis() {
        int j=-1;
        for (int i = 0; i < nbin; i++) {
            if (( count.get(i))>1) {
                ++j;
            }
        }
        if(j>1) {
            return histo.view(new Range(0, j-1));
        }else {
            return null;
        }
    }

    public Int1D getCount() {

        Int1D realcount = count.create();
        int j=-1;
        for (int i = 0; i < nbin; i++) {
            int c;
            if (( c=count.get(i))>1) {
                realcount.set(++j,c);
            }
        }
        if(j>1) {
            return realcount.view(new Range(0, j-1));
        }else {
            return null;
        }
    }

    public Double1D getMean() {
        Double1D realmean = mean.create();
        int j=-1;
        for (int i = 0; i < nbin; i++) {
            double c;
            if (( c=count.get(i))>1) {
                realmean.set(++j, mean.get(i)/c);
            }

        }
        if(j>1) {
            return realmean.view(new Range(0, j-1));
        }else {
            return null;
        }

    }

    public Double1D getVar() {

        Double1D realvar = var.create();
        int j=-1;
        for (int i = 0; i < nbin; i++) {
            double c;
            if (( c=count.get(i))>1) {
                //  double correction = 1 - 1./(4*c) - 7./32./(c*c);
                realvar.set(++j, (var.get(i)/(c-1)));
            }

        }
        if(j>1) {
            return realvar.view(new Range(0, j-1));
        }else {
            return null;
        }
    }



    /**
     * Reset a summary of values.
     * @return
     *
     */
    public HistoMap reset() {
        vmin = Double.NaN;
        vmax = Double.NaN;
        histo = null;
        nans = 0;
        posinfs = 0;
        neginfs = 0;
        nbin =1;
        count = null;
        mean=null;
        var=null;
        computationRequired = true;
        return this;
    }



    public void Show(int n) {
        System.out.format(" Histogram :\n");
        Int1D axis = getAxis();
        Double1D variance = getVar();
        Double1D avg = getMean();
        Int1D cnt = getCount();
        if (count !=null) {
            for (int j = 0; j < Math.min(cnt.getNumber(),n); j++) {
                System.out.format("  %d \t %d \t %e \t %e \n", axis.get(j), cnt.get(j), avg.get(j),variance.get(j));
            }
        }
    }


    /**
     * Update the summary with the values of a shaped array.
     * @param truth
     * @param data
     *
     * @return The object itself after the updating.
     */
    public HistoMap update(DoubleArray truth, DoubleArray data, ShapedArray badpix) {
        if ( (truth != null)||(data != null)) {
            if (!truth.getShape().equals(data.getShape()))
                throw new IllegalArgumentException("truth does not have the same shape as data");

            double tmpmin, tmpmax;
            double[] mm = truth.getMinAndMax();
            tmpmin = mm[0];
            tmpmax = mm[1];

            updateSize(tmpmin,tmpmax);


            if(badpix != null){
                if (!badpix.getShape().equals(data.getShape()))
                    throw new IllegalArgumentException("bad pixels map does not have the same shape as data");
                for (int i = 0; i < truth.getNumber(); i++) {
                    double val = Math.floor(((Double1D) truth.as1D().toDouble()).get(i));
                    double mapval =  ((Double1D) data.as1D().toDouble()).get(i);
                    if (((Int1D) badpix.toInt().as1D()).get(i)!=0){
                        if (Double.isNaN(mapval)) {
                            ++nans;
                        } else if (Double.isInfinite(mapval)) {
                            if (mapval > 0) {
                                ++posinfs;
                            } else {
                                ++neginfs;
                            }
                        } else {
                            int idx = (int)Math.floor(val) - histo.get(0);
                            mapval -= histo.get(idx) ;
                            count.set(idx,  count.get(idx)+1);
                            mean.set(idx,  mean.get(idx)+ mapval);
                            var.set(idx,  var.get(idx)+ mapval*mapval);
                        }
                    }
                }
            }else {
                for (int i = 0; i < truth.getNumber(); i++) {
                    double val = Math.floor(((Double1D) truth.as1D().toDouble()).get(i));
                    double mapval =  ((Double1D) data.as1D().toDouble()).get(i);
                    if (Double.isNaN(mapval)) {
                        ++nans;
                    } else if (Double.isInfinite(mapval)) {
                        if (mapval > 0) {
                            ++posinfs;
                        } else {
                            ++neginfs;
                        }
                    } else {
                        int idx = (int)Math.floor(val) - histo.get(0);
                        mapval -= histo.get(idx) ;
                        count.set(idx,  count.get(idx)+1);
                        mean.set(idx,  mean.get(idx)+ mapval);
                        var.set(idx,  var.get(idx)+ mapval*mapval);
                    }

                }

            }
        }


        computationRequired = true;

        return this;
    }


    /**
     * Update a summary with the values of a shaped vector.
     *
     * @param vec - A shaped array.
     * @param vec2
     *
     * @return The object itself after the updating.
     */
    public HistoMap update(ShapedVector vec, ShapedVector vec2, ShapedArray badpix) {
        return update(ArrayFactory.wrap(vec).toDouble(),ArrayFactory.wrap(vec2).toDouble(), badpix);
    }

    private void compute() {
        double sw=0, swrg=0, swg=0,  swg2=0;
        for (int i = 0; i < nbin; i++) {
            int c;
            if (( c=count.get(i))>1) {
                double g;
                sw   += (c-1);
                swrg += (g=histo.get(i))*var.get(i)*(c-1);
                swg  += g*(c-1);
                swg2 += g*g*(c-1);
            }
        }
        alpha = Math.max(swrg *sw / ( sw * swg2 - swg*swg ),0.);
        beta = Math.max(alpha * swg /sw, Double.MIN_VALUE);
    }

    /**
     * @param tmpmin
     * @param tmpmax
     */
    private void updateSize(double tmpmin, double tmpmax) {
        boolean updte=false;
        Int1D oldcount;
        Double1D oldmean, oldvar;
        if (Double.isNaN(vmin) || vmin > tmpmin ) {
            vmin = tmpmin;
            updte=true;
        }

        if (Double.isNaN(vmax) || vmax < tmpmax ) {
            vmax = tmpmax;
            updte=true;
        }

        if(updte) {
            nbin = (int) (Math.ceil(vmax) - Math.floor(vmin))+1;
            if (count==null || histo==null) {
                count =  Int1D.create(nbin);
                mean =  Double1D.create(nbin);
                var =  Double1D.create(nbin);
                histo =  Int1D.wrap(IntStream.rangeClosed((int)Math.floor(vmin), (int)Math.ceil(vmax)).toArray(), nbin);
            }else {
                oldcount = count.copy();
                oldmean = mean.copy();
                oldvar = var.copy();
                int first = (int)Math.floor(vmin) - histo.get(0) ;

                count =  Int1D.create(nbin);
                mean =  Double1D.create(nbin);
                var =  Double1D.create(nbin);
                histo =  Int1D.wrap(IntStream.rangeClosed((int)Math.floor(vmin), (int)Math.ceil(vmax)).toArray(), nbin);
                Range rng1 =  new Range(first, first+oldcount.getNumber()-1);
                count.view(rng1).assign(oldcount);
                mean.view(rng1).assign(oldmean);
                var.view(rng1).assign(oldvar);
            }

        }

    }

    private class DoublePrecisionlaw implements DoubleFunction {
        @Override
        public double apply(double arg) {
            // TODO Auto-generated method stub
            return 1./( alpha*arg+beta);
        }
    }

    private class FloatPrecisionlaw implements FloatFunction {
        @Override
        public float apply(float arg) {
            // TODO Auto-generated method stub
            return (float) (1./( alpha*arg+beta));
        }
    }
}
