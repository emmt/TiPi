/**
 *
 */
package mitiv.auto;

import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.DoubleFFT_2D;
import org.jtransforms.fft.DoubleFFT_3D;
import org.jtransforms.fft.FloatFFT_1D;
import org.jtransforms.fft.FloatFFT_2D;
import org.jtransforms.fft.FloatFFT_3D;

import mitiv.array.Array1D;
import mitiv.array.Array2D;
import mitiv.array.Array3D;
import mitiv.array.Array4D;
import mitiv.array.ArrayFactory;
import mitiv.array.Double1D;
import mitiv.array.Double2D;
import mitiv.array.Double3D;
import mitiv.array.Double4D;
import mitiv.array.Float1D;
import mitiv.array.Float2D;
import mitiv.array.Float3D;
import mitiv.array.Float4D;
import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.base.Traits;
import mitiv.psf.PsfModel;

/**
 * @author ferreol
 *
 */
public class GML extends estimationMetric {

    private PsfModel psf=null;
    private ShapedArray priorDSP;
    private ShapedArray dataDSP;
    private ShapedArray psfDSP = null;
    private int rank;
    private Shape shape;
    private boolean single;
    private long numel;
    private double hyperparam;

    /**
     *
     */
    public GML(Object psf, ShapedArray data) {
        if(psf instanceof PsfModel) {
            this.psf= (PsfModel) psf;
            single = this.psf.isSingle();
            shape =this.psf.getShape();

        }else if(psf instanceof ShapedArray) {
            single = (((ShapedArray) psf).getType()  !=Traits.DOUBLE);
            psfDSP = computeDSP( (ShapedArray)psf,single);
            shape = ((ShapedArray) psf).getShape();
        }else {
            throw new IllegalArgumentException("bad type for PSF model");
        }
        rank = shape.rank();
        if (rank != data.getRank()) {
            throw new IllegalArgumentException("PSF and data does not have the same rank");
        }
        priorDSP = QuadraticSmoothDSP(shape,single);
        dataDSP = computeDSP( data,single);
    }

    /**
     * @param data
     * @param single
     * @return
     */
    private ShapedArray computeDSP(ShapedArray data, boolean single) {

        ShapedArray dataFT;
        ShapedArray dataDSP;
        Shape dataShape = data.getShape();
        rank = dataShape.rank();
        int[] dataFTdims = new int[rank+1];
        System.arraycopy(dataShape.copyDimensions(), 0, dataFTdims, 1, rank);
        dataFTdims[0] = 2;
        Shape dataFTShape = new Shape(dataFTdims);

        if(single) {
            dataDSP = ArrayFactory.create(Traits.FLOAT, dataShape);
            dataFT = ArrayFactory.create(Traits.FLOAT, dataFTShape);
            switch(rank) {
                case 1:{
                    ((Float2D) dataFT).slice(0, 0).assign(data);
                    FloatFFT_1D fft = new FloatFFT_1D(dataFTShape.dimension(0));
                    fft.realForwardFull(((Float2D) dataFT).getData());
                    break;
                }
                case 2:{
                    ((Float3D) dataFT).slice(0, 0).assign(data);
                    FloatFFT_2D fft = new FloatFFT_2D(dataFTShape.dimension(1),dataFTShape.dimension(0));
                    fft.realForwardFull(((Float3D) dataFT).getData());
                    break;
                }
                case 3:{
                    ((Float4D) dataFT).slice(0, 0).assign(data);
                    FloatFFT_3D fft = new FloatFFT_3D(dataFTShape.dimension(2),dataFTShape.dimension(1),dataFTShape.dimension(0));
                    fft.realForwardFull(((Float4D) dataFT).getData());
                    break;
                }
                default:
                    throw new IllegalArgumentException("Rank >3 unsupported");
            }

            for (int i = 0; i < dataShape.number(); i++) {
                double a = ((Float1D) dataFT.as1D()).get(i);
                double b = ((Float1D) dataFT.as1D()).get(i+1);
                ((Float1D) dataDSP.as1D()).set(i, (float) (a*a+b*b));
            }

        }else {
            dataDSP = ArrayFactory.create(Traits.DOUBLE, dataShape);
            dataFT = ArrayFactory.create(Traits.DOUBLE, dataFTShape);
            switch(rank) {
                case 1:{
                    ((Double2D) dataFT).slice(0, 0).assign(data);
                    DoubleFFT_1D fft = new DoubleFFT_1D(dataFTShape.dimension(0));
                    fft.realForwardFull(((Double2D) dataFT).getData());
                    break;
                }
                case 2:{
                    ((Double3D) dataFT).slice(0, 0).assign(data);
                    DoubleFFT_2D fft = new DoubleFFT_2D(dataFTShape.dimension(1),dataFTShape.dimension(0));
                    fft.realForwardFull(((Double3D) dataFT).getData());
                    break;
                }
                case 3:{
                    ((Double4D) dataFT).slice(0, 0).assign(data);
                    DoubleFFT_3D fft = new DoubleFFT_3D(dataFTShape.dimension(2),dataFTShape.dimension(1),dataFTShape.dimension(0));
                    fft.realForwardFull(((Double4D) dataFT).getData());
                    break;
                }
                default:
                    throw new IllegalArgumentException("Rank >3 unsupported");
            }
            for (int i = 0; i < dataShape.number(); i++) {
                double a = ((Double1D) dataFT.as1D()).get(i);
                double b = ((Double1D) dataFT.as1D()).get(i+1);
                ((Double1D) dataDSP.as1D()).set(i, a*a+b*b);
            }

        }
        return dataDSP;
    }


    /**
     * @param shape
     * @param single2
     * @return
     */
    private ShapedArray QuadraticSmoothDSP(Shape shape, boolean single2) {
        int rank = shape.rank();
        ShapedArray DSP;
        if (single) {
            DSP = ArrayFactory.create(Traits.FLOAT ,shape);
        }else {
            DSP = ArrayFactory.create(Traits.DOUBLE ,shape);
        }
        numel = shape.number();
        switch(rank) {
            case 1:{
                int nx =shape.dimension(0);
                for (int ix = 0; ix < nx; ix++) {
                    double ax = Math.sin(Math.PI * ix/nx);
                    if(single) {
                        ((Float1D) DSP).set(ix, (float) (4*ax*ax));
                    }else {
                        ((Double1D) DSP).set(ix, (4*ax*ax));
                    }
                }
                break;
            }
            case 2:{
                int nx =shape.dimension(0);
                int ny =shape.dimension(1);
                for (int iy = 0; iy <ny; iy++) {
                    double ay = Math.sin(Math.PI * iy/ny);
                    for (int ix = 0; ix < nx; ix++) {
                        double ax = Math.sin(Math.PI * ix/nx);
                        if(single) {
                            ((Float2D) DSP).set(ix,iy ,(float) (4*ax*ax+4*ay*ay));
                        }else {
                            ((Double2D) DSP).set(ix, iy,(4*ax*ax+4*ay*ay));
                        }
                    }
                }
                break;
            }
            case 3:{
                int nx =shape.dimension(0);
                int ny =shape.dimension(1);
                int nz =shape.dimension(2);
                for (int iz = 0; iz < nz; iz++) {
                    double az = Math.sin(Math.PI * iz/nz);
                    for (int iy = 0; iy < ny; iy++) {
                        double ay = Math.sin(Math.PI * iy/ny);
                        for (int ix = 0; ix < nz; ix++) {
                            double ax = Math.sin(Math.PI * ix/nx);
                            if(single) {
                                ((Float3D) DSP).set(ix,iy ,iz,(float) (4*ax*ax+4*ay*ay+4*az*az));
                            }else {
                                ((Double3D) DSP).set(ix, iy,iz,(4*ax*ax+4*ay*ay+4*az*az));
                            }
                        }
                    }
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Rank >3 unsupported");
        }
        return DSP;
    }

    @Override
    public double value(double[] arg0) {
        if(arg0.length>1)// single parameter = known psf
        {
            double[] param = new double[arg0.length-1];
            System.arraycopy(arg0, 1, param, 0, arg0.length-1);
            psf.setParam(param);
        }
        hyperparam = Math.pow(10,arg0[0]);

        return     computeGML();
    }


    private double computeGML() {
        Array1D mtfR = null, mtfI = null;
        ShapedArray mtf;
        double num = 0, denom=0;
        long sz = 0;
        double result;

        if(psf!=null) {
            mtf = psf.getMtf();
            switch(rank) {
                case 1:
                    mtfR =  ((Array2D) mtf).slice(0,0);
                    mtfI =  ((Array2D) mtf).slice(1,0);
                    break;
                case 2:
                    mtfR =  ((Array3D) mtf).slice(0,0).as1D();
                    mtfI =  ((Array3D) mtf).slice(1,0).as1D();
                    break;
                case 3:
                    mtfR =  ((Array4D) mtf).slice(0,0).as1D();
                    mtfI =  ((Array4D) mtf).slice(1,0).as1D();
                    break;
                default:
                    throw new IllegalArgumentException("Rank >3 unsupported");
            }
        }
        for (int i = 0; i < numel; i++) {
            double a=0,b=0,q=0,d=0,h2=0;

            if(psf!=null) {
                if(single) {
                    a = ((Float1D) mtfR).get(i);
                    b = ((Float1D) mtfI).get(i);
                    q = hyperparam*((Float1D) priorDSP.as1D()).get(i);
                    d = ((Float1D) dataDSP.as1D()).get(i);
                }else {
                    a = ((Double1D) mtfR).get(i);
                    b = ((Double1D) mtfI).get(i);
                    q = hyperparam*((Double1D) priorDSP.as1D()).get(i);
                    d = ((Double1D) dataDSP.as1D()).get(i);
                }
                h2 = a*a+b*b;
            }else {
                if(single) {
                    h2 = ((Float1D) psfDSP.as1D()).get(i);
                    q = hyperparam*((Float1D) priorDSP.as1D()).get(i);
                    d = ((Float1D) dataDSP.as1D()).get(i);
                }else {
                    h2 = ((Double1D) psfDSP.as1D()).get(i);
                    q = hyperparam*((Double1D) priorDSP.as1D()).get(i);
                    d = ((Double1D) dataDSP.as1D()).get(i);
                }

            }
            double w = q / ( h2 + q);
            if(w>0) {
                num  += w * d;
                denom += Math.log(w);
                sz ++;
            }
        }
        if (sz ==0)
        {
            result =Double.POSITIVE_INFINITY;
        }else {
            result =  num/Math.exp(denom/sz);
        }
        return result;
    }
}
