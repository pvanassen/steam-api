/**
 * 
 */
package nl.pvanassen.steam.store.login;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

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
        login(user, password, "", "");
    }
    

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.login.LoginService#login(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void login(String user, String password, String capchaGid, String capchaAnswer) throws VerificationException, SteamGuardException {
        Map<String, String> params = new HashMap<>();
        params.put("username", user);
        GetRSAHandle rsaHandle = new GetRSAHandle(objectMapper);
        DoLoginHandle doLoginHandle = new DoLoginHandle(objectMapper);
        try {
            http.post("https://store.steampowered.com/login/getrsakey/", params, rsaHandle, "http://steamcommunity.com/id/" + user + "/inventory/", false);
            if (!rsaHandle.isSuccess()) {
                throw new VerificationException("Invalid username");
            }
            RSA crypto = new RSA(rsaHandle.getPublicKeyMod(), rsaHandle.getPublicKeyExp());

            String encryptedPasswordBase64 = crypto.encrypt(password);

            params.put("captcha_text", capchaAnswer);
            params.put("captchagid", capchaGid);
            params.put("emailauth", "");
            params.put("emailsteamid", "");
            params.put("loginfriendlyname", "");

            params.put("password", encryptedPasswordBase64);
            params.put("remember_login", "true");
            params.put("rsatimestamp", Long.toString(rsaHandle.getTimestamp()));
            http.post("https://steamcommunity.com/login/dologin/", params, doLoginHandle, "http://steamcommunity.com/id/" + user + "/inventory/", false);
            if (doLoginHandle.isSuccess()) {
                // logged in
                return;
            }
            if (doLoginHandle.getMessage().contains("SteamGuard")) {
                throw new SteamGuardException();
            }
            if (doLoginHandle.isCapchaNeeded()) {
            	String capchaG = doLoginHandle.getCapchaGid();
            	throw new CapchaException("https://steamcommunity.com/public/captcha.php?gid=" + capchaG, capchaG);
            }
            throw new VerificationException(doLoginHandle.getMessage());
        }
        catch (IOException e) {
            logger.error("Error logging in", e);
            throw new VerificationException("Error logging in", e);
        }
    }
    
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
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
