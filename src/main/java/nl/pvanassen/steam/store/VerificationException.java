package nl.pvanassen.steam.store;

/**
 * Verification error
 * 
 * @author Paul van Assen
 *
 */
public class VerificationException extends Exception {
    
    VerificationException(String message) {
        super(message);
    }

    VerificationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
