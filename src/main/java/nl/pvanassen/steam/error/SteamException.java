package nl.pvanassen.steam.error;

/**
 * Steam exception
 *
 * @author Paul van Assen
 */
public class SteamException extends RuntimeException {
    /**
     * Constructor with message
     *
     * @param message
     *            The error message
     */
    public SteamException(String message) {
        super(message);
    }

    /**
     * Constructor with message and exception
     *
     * @param message
     *            The error message
     * @param throwable
     *            Underlying exception
     */
    public SteamException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
