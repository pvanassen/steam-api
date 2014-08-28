/**
 * 
 */
package nl.pvanassen.steam.store.login;


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
     * @throws CapchaException In case a capcha needs to be solved
     */
    void login(String user, String password) throws VerificationException, SteamGuardException, CapchaException;

    /**
     * Do an login attempt. If a code is requested a VerificationError is thrown
     * 
     * @param user Username
     * @param password Password
     * @param capchaGid The CAPCHA gid to answer
     * @param capchaAnswer The CAPCHA answer
     * @throws VerificationException In case login failed
     * @throws SteamGuardException In case a code is requested
     * @throws CapchaException In case a capcha needs to be solved
     */
    void login(String user, String password, String capchaGid, String capchaAnswer) throws VerificationException, SteamGuardException, CapchaException;
    
    /**
     * Verification based on a code
     * 
     * @param code The requested code
     */
    void verification(String code);

}
