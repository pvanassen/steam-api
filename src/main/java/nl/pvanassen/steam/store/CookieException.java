package nl.pvanassen.steam.store;

/**
 * Exception thrown when steam is bitching about cookies
 * @author Paul van Assen
 *
 */
public class CookieException extends RuntimeException {
    public CookieException() {
        super("Error posting data, got cookie error back from Steam");
    }
}
