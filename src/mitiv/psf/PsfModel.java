/**
 *
 */
package mitiv.psf;

import mitiv.array.ShapedArray;
import mitiv.base.Shape;
import mitiv.linalg.shaped.DoubleShapedVector;

/**
 * @author ferreol
 *
 */
public abstract class PsfModel {

    private boolean single = false;
    protected Shape psfShape;
    protected ShapedArray psf=null; //3D point spread function

    public PsfModel(Shape psfShape,boolean single) {
        this.psfShape = psfShape;
        this.setSingle(single);
    }


    /**
     * @return the PSF
     */
    abstract public ShapedArray getPsf();

    /**
     * @return the MTF
     */
    abstract public ShapedArray getMtf();

    /**
     * Setter for PSF parameters. The parameter type is given by the parameter space of @param
     * @param param PSF parameters
     */
    abstract public void setParam(DoubleShapedVector param);


    /**
     * Setter for PSF parameters. The parameter type is given by the parameter space of @param
     * @param param PSF parameters
     */
    abstract public void setParam(double[] param);


    /**
     * Free some memory
     */
    public void freeMem() {
        psf=null;
    }

    /**
     * Setter for the single precision flag
     * @param single
     */
    public void setSingle(boolean single){
        this.single = single;
    }


    /**
     * @return the single precision flag
     */
    public boolean isSingle() {
        return single;
    }


    /**
     * @return shape
     */
    public Shape getShape() {
        return psfShape;
    }

}
