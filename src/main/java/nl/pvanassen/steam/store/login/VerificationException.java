package nl.pvanassen.steam.store.login;

import nl.pvanassen.steam.error.SteamException;

/**
 * Verification error
 *
 * @author Paul van Assen
 */
public class VerificationException extends SteamException {

    /**
     * Error logging in
     * 
     * @param message Message why login failed
     */
    public VerificationException(String message) {
        super(message);
    }

    /**
     * Error logging in
     * 
     * @param message Message why login failed
     * @param cause Exception causing login error
     */
    public VerificationException(String message, Throwable cause) {
        super(message, cause);
    }

}
