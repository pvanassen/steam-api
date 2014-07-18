/**
 * 
 */
package nl.pvanassen.steam.store.remove;

import java.io.IOException;
import java.util.HashMap;

import nl.pvanassen.steam.http.Http;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul van Assen
 *
 */
public class SteamRemoveService implements RemoveService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Http http;
    private final String username;
    
    public SteamRemoveService(String cookies, String username) {
        this(Http.getInstance(cookies, username), username);
    }

    /**
     * @param http For mocking
     */
    public SteamRemoveService(Http http, String username) {
        this.http = http;
        this.username = username;
    }
    @Override
    public boolean removeListing(String listingId) {
    	try {
    		RemoveHandle removeHandle = new RemoveHandle();
    		http.post("http://steamcommunity.com/market/removelisting/" + listingId, new HashMap<String,String>(), removeHandle, "http://steamcommunity.com/id/" + username + "/inventory/");
            return !removeHandle.isError();
    	}
    	catch (IOException | RuntimeException e) {
            logger.error("Error posting data", e);
            return false;
    	}
    }
}
