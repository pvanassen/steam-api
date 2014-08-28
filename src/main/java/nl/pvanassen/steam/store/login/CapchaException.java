package nl.pvanassen.steam.store.login;

import nl.pvanassen.steam.error.SteamException;

/**
 * You are not human. Go do capcha
 * @author Paul van Assen
 *
 */
public class CapchaException extends SteamException {
    private final String capchaUrl;
    private final String capchaGid;
    
    CapchaException(String capchaUrl, String capchaGid) {
        super("Please verify humanity: " + capchaUrl);
        this.capchaUrl = capchaUrl;
        this.capchaGid = capchaGid;
    }
    
    /**
     * 
     * @return The capcha url to go to
     */
    public String getCapchaUrl() {
        return capchaUrl;
    }
    
    /**
     * 
     * @return The capcha GID needed for ansering the question
     */
    public String getCapchaGid() {
        return capchaGid;
    }
}
