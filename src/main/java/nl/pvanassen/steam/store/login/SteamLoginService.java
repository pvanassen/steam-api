/**
 * 
 */
package nl.pvanassen.steam.store.login;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import nl.pvanassen.steam.error.SteamGuardException;
import nl.pvanassen.steam.error.VerificationException;
import nl.pvanassen.steam.http.Http;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul van Assen
 *
 */
public class SteamLoginService implements LoginService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Http http;
    
    public SteamLoginService(String cookies, String username) {
        this(Http.getInstance(cookies, username));
    }

    /**
     * @param http For mocking
     */
    public SteamLoginService(Http http) {
        this.http = http;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.login.LoginService#login(java.lang.String, java.lang.String)
     */
    @Override
    public void login(String user, String password) throws VerificationException, SteamGuardException {
        Map<String, String> params = new HashMap<>();
        params.put("username", user);
        GetRSAHandle rsaHandle = new GetRSAHandle(objectMapper);
        DoLoginHandle doLoginHandle = new DoLoginHandle(objectMapper);
        try {
            http.post("https://store.steampowered.com/login/getrsakey/", params, rsaHandle, "http://steamcommunity.com/id/" + user + "/inventory/");
            if (!rsaHandle.isSuccess()) {
                throw new VerificationException("Invalid username");
            }
            BigInteger pubKeyMod = new BigInteger(rsaHandle.getPublicKeyMod(), 16);
            BigInteger pubKeyExp = new BigInteger(rsaHandle.getPublicKeyExp(), 10);
            RSACrypto crypto = new RSACrypto(pubKeyMod, pubKeyExp, false);

            byte[] encrypted = crypto.encrypt(password.getBytes());
            String encryptedPasswordBase64 = Base64.encodeBase64String(encrypted);

            params.put("captcha_text", "");
            params.put("captchagid", "");
            params.put("emailauth", "");
            params.put("emailsteamid", "");
            params.put("loginfriendlyname", "");

            params.put("password", encryptedPasswordBase64);
            params.put("remember_login", "true");
            params.put("rsatimestamp", Long.toString(rsaHandle.getTimestamp()));
            http.post("https://steamcommunity.com/login/dologin/", params, doLoginHandle, "http://steamcommunity.com/id/" + user + "/inventory/");
            if (doLoginHandle.isSuccess()) {
                // logged in
                return;
            }
            if (doLoginHandle.getMessage().contains("SteamGuard")) {
                throw new SteamGuardException();
            }
        }
        catch (IOException e) {
            logger.error("Error logging in", e);
            throw new VerificationException("Error logging in", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.login.LoginService#verification(java.lang.String)
     */
    @Override
    public void verification(String code) {
    	
        // TODO Auto-generated method stub
    }
}
