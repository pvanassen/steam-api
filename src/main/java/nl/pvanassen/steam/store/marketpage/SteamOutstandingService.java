/**
 * 
 */
package nl.pvanassen.steam.store.marketpage;

import java.io.IOException;

import nl.pvanassen.steam.http.Http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul van Assen
 *
 */
public class SteamOutstandingService implements OutstandingService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Http http;
    
    public SteamOutstandingService(String cookies, String username) {
        this(Http.getInstance(cookies, username));
    }

    /**
     * @param http For mocking
     */
    public SteamOutstandingService(Http http) {
        this.http = http;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getOutstandings()
     */
    @Override
    public MarketPage getOutstandings() {
    	logger.info("Getting market page");
        MarketPageHandle handle = new MarketPageHandle();
        try {
            http.get("http://steamcommunity.com/market/", handle);
        }
        catch (IOException e) {
            logger.error("Error getting outstanding listings", e);
        }
        return handle.getOutstandings();
    }
}
