/**
 *
 */
package mitiv.psf;

import java.util.Arrays;

import mitiv.array.Array1D;
import mitiv.array.ArrayFactory;
import mitiv.array.ArrayUtils;
import mitiv.array.Double1D;
import mitiv.array.Double2D;
import mitiv.array.DoubleArray;
import mitiv.array.Float1D;
import mitiv.array.Float2D;
import mitiv.array.FloatArray;
import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.base.mapping.DoubleFunction;
import mitiv.base.mapping.FloatFunction;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.utils.FFTUtils;

/**
 * @author ferreol
 *
 */
public class GaussianPsf extends PsfModel {
    double[] scale=null;
    int rank;
    /**
     *
     */

    public GaussianPsf(Shape psfShape, double[] scale, boolean single) {
        super(psfShape, single);
        rank = psfShape.rank();
        if (scale==null) {
            scale = new double[rank];
            Arrays.fill(scale, 1.0);
        }else if(rank != scale.length) {
            throw new IllegalArgumentException("Scale and shape must have the same rank");
        }
        this.scale =  Arrays.copyOf(scale, rank);
    }

    @Override
    public ShapedArray getPsf() {
        if (psf==null) {
            if (isSingle()) {
                psf = FFTUtils.generateFrequels2(psfShape.dimension(0)).toFloat();
                ((Float1D) psf).map(new FloatGaussian((float) Math.sqrt(0.5)*scale[0]));
            }else {
                psf = FFTUtils.generateFrequels2(psfShape.dimension(0)).toDouble();
                ((Double1D) psf).map(new DoubleGaussian(Math.sqrt(0.5)*scale[0]));
            }

            for( int j = 1; j < rank; j++){
                Array1D nextdim;
                if (isSingle()) {
                    nextdim = FFTUtils.generateFrequels2(psfShape.dimension(j)).toFloat();
                    ((Float1D) nextdim).map(new FloatGaussian((float) Math.sqrt(0.5)*scale[j]));
                }else {
                    nextdim = FFTUtils.generateFrequels2(psfShape.dimension(j)).toDouble();
                    ((Double1D) nextdim).map(new DoubleGaussian(Math.sqrt(0.5)*scale[j]));
                }
                psf = ArrayUtils.outer(psf, nextdim);
            }

            if (isSingle()) {
                ((FloatArray) psf).scale(1.0F/ ((FloatArray) psf).sum());
            }else {
                ((DoubleArray) psf).scale(1.0/ ((DoubleArray) psf).sum());
            }
        }
        return psf;
    }

    @Override
    public ShapedArray getMtf() {
        ShapedArray mtf;
        int[] firstdims=  new int[2];
        firstdims[0]=2;
        firstdims[1]=psfShape.dimension(0);

        if (isSingle()) {
            mtf = ArrayFactory.create(Traits.FLOAT,firstdims );
            Array1D nextdim= FFTUtils.generateFrequels2(psfShape.dimension(0),true).toFloat();
            ((Float1D) nextdim).map(new FloatGaussian((float)  Math.PI/scale[0]));
            ((Float2D) mtf).slice(0,0).assign(nextdim);
        }else {
            mtf = ArrayFactory.create(Traits.DOUBLE,firstdims );
            Array1D nextdim= FFTUtils.generateFrequels2(psfShape.dimension(0),true).toDouble();
            ((Double1D) nextdim).map(new DoubleGaussian(Math.sqrt(2)*Math.PI/scale[0]));
            ((Double2D) mtf).slice(0,0).assign(nextdim);
        }

        for( int j = 1; j < rank; j++){
            Array1D nextdim;
            if (isSingle()) {
                nextdim = FFTUtils.generateFrequels2(psfShape.dimension(j)).toFloat();
                ((Float1D) nextdim).map(new FloatGaussian((float) Math.sqrt(2)*Math.PI/scale[j]));
            }else {
                nextdim = FFTUtils.generateFrequels2(psfShape.dimension(j),true).toDouble();
                ((Double1D) nextdim).map(new DoubleGaussian( Math.sqrt(2)*Math.PI/scale[j]));
            }
            mtf = ArrayUtils.outer(mtf, nextdim);
        }
        return mtf;
    }
    @Override
    public void setParam(DoubleShapedVector param) {
        setParam(  param.getData());
    }


    @Override
    public void setParam(double[] param) {

        if(param.length==1)
        {
            Arrays.fill(scale, param[0]);
        } else if(param.length==rank) {
            System.arraycopy(param, 0, scale, 0, rank);
        }else if(param.length==2) {
            switch (rank) {
                case 3:
                    scale[0]= param[0];
                    scale[1]= param[0];
                    scale[2]= param[1];
                    break;
                default:
                    throw new IllegalArgumentException(" psf dimension>3 not implemented");

            }
        }
        psf=null;
    }

    private class  DoubleGaussian implements DoubleFunction
    {
        double factor=1.0; // Math.sqrt(2. * Math.PI);
        double scale2;

        public DoubleGaussian(double sc) {
            factor=1 ;// Math.sqrt(2. * Math.PI);
            scale2=-sc*sc;
        }
        @Override
        public double apply(double arg) {
            return factor*Math.exp(scale2* arg);
        }

    }


    private class  FloatGaussian implements FloatFunction
    {
        double factor=1.0 / Math.sqrt(2. * Math.PI);
        double scale2;

        public FloatGaussian(double sc) {
            factor= 1; // Math.sqrt(2. * Math.PI);
            scale2=-sc*sc;
        }
        @Override
        public float apply(float arg) {
            return (float) (factor* Math.exp(scale2* arg));
        }

    }



}
