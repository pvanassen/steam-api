package nl.pvanassen.steam.store.common;

/**
 * Don't handle
 * @author Paul van Assen
 *
 * @param <T> Type
 */
public class DontHandle<T> implements GenericHandle<T> {
    @Override
    public void handle(T item) {}
    
    public static <T> DontHandle<T> create() {
        return new DontHandle<T>();
    }
}
