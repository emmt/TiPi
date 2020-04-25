/**
 *
 */
package mitiv.utils;

import mitiv.array.ShapedArray;

/**
 * @author ferreol
 *
 */
public abstract interface WeightUpdater {
    /**
     * Run specific code
     * @param caller   calling object
     */
    public abstract void update(Object caller);

    public abstract ShapedArray getWeights();
}

