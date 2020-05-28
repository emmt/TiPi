/**
 *
 */
package mitiv.psf;

import java.util.Arrays;

import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.DoubleFFT_2D;
import org.jtransforms.fft.FloatFFT_1D;
import org.jtransforms.fft.FloatFFT_2D;

import mitiv.array.ArrayFactory;
import mitiv.array.Double2D;
import mitiv.array.Double3D;
import mitiv.array.DoubleArray;
import mitiv.array.Float2D;
import mitiv.array.Float3D;
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
public class MoffatPsf extends PsfModel {

    double[] scale=null;
    double alpha, beta;
    int rank;
    Object fft;
    /**
     * @param psfShape
     * @param single
     */
    public MoffatPsf(Shape psfShape, double alpha, double beta,double[] scale, boolean single) {
        super(psfShape, single);
        this.alpha = alpha;
        this.beta=beta;
        rank = psfShape.rank();
        if (scale==null) {
            scale = new double[rank];
            Arrays.fill(scale, 1.0);
        }else if(rank != scale.length) {
            throw new IllegalArgumentException("Scale and shape must have the same rank");
        }
        this.scale =  Arrays.copyOf(scale, rank);
        if(single) {
            switch(rank) {
                case 1:
                    fft = new FloatFFT_1D(psfShape.dimension(0));
                    break;
                case 2:
                    fft = new FloatFFT_2D(psfShape.dimension(1), psfShape.dimension(0));
                    break;
                default:
                    throw new IllegalArgumentException("Rank >2 unsupported");
            }
        }else {
            switch(rank) {
                case 1:
                    fft = new DoubleFFT_1D(psfShape.dimension(0));
                    break;
                case 2:
                    fft = new DoubleFFT_2D(psfShape.dimension(1), psfShape.dimension(0));
                    break;
                default:
                    throw new IllegalArgumentException("Rank >2 unsupported");
            }
        }
    }

    /**
     *
     */
    private void computePsf() {
        psf = FFTUtils.fftDist(psfShape,scale);
        if (isSingle()) {
            psf = psf.toFloat();
            ((FloatArray) psf).map(new FloatMoffat(alpha,beta));
            ((FloatArray) psf).scale(1.0F/ ((FloatArray) psf).sum());
        }else {
            psf=psf.toDouble();
            ((DoubleArray) psf).map(new DoubleMoffat(alpha,beta));
            ((DoubleArray) psf).scale(1.0/ ((DoubleArray) psf).sum());
        }
    }

    @Override
    public ShapedArray getPsf() {

        if (psf==null) {
            computePsf();
        }
        return psf;
    }

    @Override
    public ShapedArray getMtf() {
        if (psf==null) {
            computePsf();
        }
        ShapedArray mtf;
        int[] mtfdims = new int[rank+1];
        System.arraycopy(psfShape.copyDimensions(), 0, mtfdims, 1, rank);
        mtfdims[0] = 2;
        Shape mtfShape = new Shape(mtfdims);
        if(isSingle()) {
            mtf = ArrayFactory.create(Traits.FLOAT, mtfShape);
            switch(rank) {
                case 1:
                    ((Float2D) mtf).slice(0, 0).assign(psf);
                    ((FloatFFT_1D) fft).realForwardFull(((Float2D) mtf).getData());
                    break;
                case 2:
                    ((Float3D) mtf).slice(0, 0).assign(psf);
                    ((FloatFFT_2D) fft).realForwardFull(((Float3D) mtf).getData());
                    break;
                default:
                    throw new IllegalArgumentException("Rank >2 unsupported");
            }

        }else {
            mtf = ArrayFactory.create(Traits.DOUBLE, mtfShape);
            switch(rank) {
                case 1:
                    ((Double2D) mtf).slice(0, 0).assign(psf);
                    ((DoubleFFT_1D) fft).realForwardFull(((Double2D) mtf).getData());
                    break;
                case 2:
                    ((Double3D) mtf).slice(0, 0).assign(psf);
                    ((DoubleFFT_2D) fft).realForwardFull(((Double3D) mtf).getData());
                    break;
                default:
                    throw new IllegalArgumentException("Rank >2 unsupported");
            }
        }
        return mtf;
    }

    @Override
    public void setParam(DoubleShapedVector param) {
        setParam(  param.getData());
    }

    @Override
    public void setParam(double[] x) {

        switch (x.length) {
            case 4:
                scale[1]=x[3] ;
            case 3:
                scale[0]=x[2] ;
            case 2:
                beta = Math.abs(x[1]);
            case 1:
                alpha = Math.abs(x[0]);
        }
        psf=null;
    }

    private class  DoubleMoffat implements DoubleFunction
    {
        double alpha2=1.0; // Math.sqrt(2. * Math.PI);
        double beta=1.0;
        double factor=1.0;

        public DoubleMoffat(double alph, double bet) {
            alpha2 = alph*alph;
            beta = bet;
            factor= (beta-1)/alpha2;
        }
        @Override
        public double apply(double arg) {
            return factor* Math.pow(1+ arg /alpha2,-beta);
        }

    }

    private class  FloatMoffat implements FloatFunction
    {
        double alpha2=1.0; // Math.sqrt(2. * Math.PI);
        double beta=1.0;
        double factor=1.0;

        public FloatMoffat(double alph, double bet) {
            alpha2 = alph*alph;
            beta = bet;
            factor= (beta-1)/alpha2;
        }
        @Override
        public float apply(float arg) {
            return (float) (factor* Math.pow(1+ arg /alpha2,-beta));
        }

    }

}
