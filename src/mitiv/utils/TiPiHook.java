/**
 *
 */
package mitiv.utils;

/**
 * Define a hook that can be called during computation.
 * It is usefull to show information / image during iteration
 * @author ferreol
 *
 */
public abstract interface TiPiHook {

    /**
     * Run specific code
     * @param caller   calling object
     * @param iter     number of current iteration
     */
    public abstract void run(Object caller, int iter);
}
