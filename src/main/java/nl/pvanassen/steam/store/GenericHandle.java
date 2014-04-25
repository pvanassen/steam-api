package nl.pvanassen.steam.store;

/**
 * Handle an object
 * 
 * @author Paul van Assen
 * @param <T> Type of object to handle
 */
public interface GenericHandle<T> {
    /**
     * @param item Item to handle
     */
    void handle(T item);
}
