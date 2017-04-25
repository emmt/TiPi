/**
 *
 */
package mitiv.utils;

/**
 * @author ferreol
 *
 */
public abstract interface TiPiHook {

    /**
     * Run specific code
     * @param caller
     * @param iter
     */
    public abstract void run(Object caller, int iter);
}
