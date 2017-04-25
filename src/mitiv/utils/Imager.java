/**
 *
 */
package mitiv.utils;

import mitiv.array.ShapedArray;
import mitiv.linalg.shaped.ShapedVector;

/**
 * An abstract class for plot/save inspired by DeconvolutionLab2 Imager class
 * @author ferréol ferreol.soulez@epfl.ch
 *
 */
public abstract interface Imager {
    /**
     * plot the array
     * @param arr
     */
    public abstract void show(final ShapedArray arr);
    /**
     * plot the vector
     * @param vec
     */
    public abstract void show(final ShapedVector vec);
    /**
     * plot the array
     * @param arr
     * @param title
     *            title of the plot
     */
    public abstract void show(final ShapedArray arr,final String title);
    /**
     * plot the vector
     * @param vec
     * @param title
     *            title of the plot
     */
    public abstract void show(final ShapedVector vec,final String title);


    /**
     * Save an array in the file pointed by path
     * @param arr
     * @param path
     *            path of the file
     */
    public abstract void save(final ShapedArray arr,final String path);

    /**
     * Save a vector in the file pointed by path
     * @param arr
     * @param path
     *            path of the file
     */
    public abstract void save(final ShapedVector arr,final String path);
}
