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
     * @param user
     *            Username
     * @param password
     *            Password
     * @throws VerificationException
     *             In case login failed
     * @throws SteamGuardException
     *             In case a code is requested
     * @throws CapchaException
     *             In case a capcha needs to be solved
     */
    void login(String user, String password) throws VerificationException, SteamGuardException, CapchaException;

    /**
     * Do an login attempt. If a code is requested a VerificationError is thrown
     *
     * @param user
     *            Username
     * @param password
     *            Password
     * @param emailSteamId
     *            The email steam id
     * @param friendlyName
     *            Friendly name for this session
     * @param capchaGid
     *            The CAPCHA gid to answer
     * @param capchaAnswer
     *            The CAPCHA answer
     * @param code
     *            Code send by email
     * @throws VerificationException
     *             In case login failed
     * @throws SteamGuardException
     *             In case a code is requested
     * @throws CapchaException
     *             In case a capcha needs to be solved
     */
    void login(String user, String password, String capchaGid, String capchaAnswer, String emailSteamId, String friendlyName, String code) throws VerificationException,
            SteamGuardException;

    /**
     * Do an login attempt. If a code is requested a VerificationError is thrown
     *
     * @param user
     *            Username
     * @param password
     *            Password
     * @param capchaGid
     *            The CAPCHA gid to answer
     * @param capchaAnswer
     *            The CAPCHA answer
     * @throws VerificationException
     *             In case login failed
     * @throws SteamGuardException
     *             In case a code is requested
     * @throws CapchaException
     *             In case a capcha needs to be solved
     */
    void loginCapcha(String user, String password, String capchaGid, String capchaAnswer) throws VerificationException, SteamGuardException, CapchaException;

    /**
     * Do an login attempt. If a code is requested a VerificationError is thrown
     *
     * @param user
     *            Username
     * @param password
     *            Password
     * @param emailSteamId
     *            The email steam id
     * @param friendlyName
     *            Friendly name for this session
     * @param code
     *            Code send by email
     * @throws VerificationException
     *             In case login failed
     * @throws SteamGuardException
     *             In case a code is requested
     * @throws CapchaException
     *             In case a capcha needs to be solved
     */
    void loginSteamGuard(String user, String password, String emailSteamId, String friendlyName, String code) throws VerificationException, SteamGuardException, CapchaException;
}
