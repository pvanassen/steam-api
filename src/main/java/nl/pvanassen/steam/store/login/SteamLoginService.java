/**
 *
 */
package nl.pvanassen.steam.store.login;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nl.pvanassen.steam.http.Http;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul van Assen
 */
public class SteamLoginService implements LoginService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Http http;

    /**
     * @param http For mocking
     */
    public SteamLoginService(Http http) {
        this.http = http;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.login.LoginService#login(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void login(String user, String password) throws VerificationException, SteamGuardException {
        login(user, password, "", "", "", "", "");
    }

    @Override
    public void login(String user, String password, String capchaGid, String capchaAnswer, String emailSteamId, String friendlyName, String code) throws VerificationException,
            SteamGuardException {
        Map<String, String> params = new HashMap<>();
        params.put("username", user);
        GetRSAHandle rsaHandle = new GetRSAHandle(objectMapper);
        DoLoginHandle doLoginHandle = new DoLoginHandle(objectMapper);
        try {
            http.post("https://store.steampowered.com/login/getrsakey/", params, rsaHandle, "http://steamcommunity.com/id/" + user + "/inventory/", false, true);
            if (!rsaHandle.isSuccess()) {
                throw new VerificationException("Invalid username");
            }
            RSA crypto = new RSA(rsaHandle.getPublicKeyMod(), rsaHandle.getPublicKeyExp());

            String encryptedPasswordBase64 = crypto.encrypt(password);

            params.put("captcha_text", capchaAnswer);
            params.put("captchagid", capchaGid);
            params.put("emailauth", code);
            params.put("emailsteamid", emailSteamId);
            params.put("loginfriendlyname", friendlyName);
            params.put("password", encryptedPasswordBase64);
            params.put("remember_login", "true");
            params.put("rsatimestamp", Long.toString(rsaHandle.getTimestamp()));

            http.post("https://steamcommunity.com/login/dologin/", params, doLoginHandle, "http://steamcommunity.com/id/" + user + "/inventory/", false, true);
            if (doLoginHandle.isSuccess()) {
                // logged in
                return;
            }
            if (doLoginHandle.getMessage().contains("SteamGuard")) {
                throw new SteamGuardException(doLoginHandle.getEmailSteamId());
            }
            if (doLoginHandle.isCaptchaNeeded() || doLoginHandle.getMessage().contains("Error verifying humanity")) {
                String capchaG = doLoginHandle.getCaptchaGid();
                throw new CapchaException("https://steamcommunity.com/public/captcha.php?gid=" + capchaG, capchaG);
            }
            throw new VerificationException(doLoginHandle.getMessage());
        }
        catch (IOException e) {
            logger.error("Error logging in", e);
            throw new VerificationException("Error logging in", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.login.LoginService#loginCapcha(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void loginCapcha(String user, String password, String capchaGid, String capchaAnswer) throws VerificationException, SteamGuardException {
        login(user, password, capchaGid, capchaAnswer, "", "", "");
    }

    @Override
    public void loginSteamGuard(String user, String password, String emailSteamId, String friendlyName, String code) throws VerificationException, SteamGuardException,
            CapchaException {
        login(user, password, "", "", emailSteamId, friendlyName, code);
    }

}
