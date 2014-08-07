/**
 * 
 */
package nl.pvanassen.steam.store.login;

import nl.pvanassen.steam.error.SteamGuardException;
import nl.pvanassen.steam.error.VerificationException;

/**
 * @author Paul van Assen
 *
 */
public interface LoginService {

    /**
     * Do an login attempt. If a code is requested a VerificationError is thrown
     * 
     * @param user Username
     * @param password Password
     * @throws VerificationException In case login failed
     * @throws SteamGuardException In case a code is requested
     */
    void login(String user, String password) throws VerificationException, SteamGuardException;

    /**
     * Verification based on a code
     * 
     * @param code The requested code
     */
    void verification(String code);

}
