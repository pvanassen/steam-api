/**
 * 
 */
package nl.pvanassen.steam.store.marketpage;

import java.io.IOException;
import java.util.Set;

import nl.pvanassen.steam.http.Http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul van Assen
 *
 */
public class SteamMarketPageService implements MarketPageService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Http http;
    private final String username;
    
    public SteamMarketPageService(String cookies, String username) {
        this(Http.getInstance(cookies, username), username);
    }

    /**
     * @param http For mocking
     */
    public SteamMarketPageService(Http http, String username) {
        this.http = http;
        this.username = username;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getOutstandings()
     */
    @Override
    public MarketPage getOutstandings() {
    	logger.info("Getting market page for " + username);
        MarketPageHandle handle = new MarketPageHandle();
        try {
            http.get("http://steamcommunity.com/market/", handle);
        }
        catch (IOException e) {
            logger.error("Error getting outstanding listings", e);
        }
        return handle.getOutstandings();
    }
    
    @Override
    public Set<Integer> getAppIds() {
    	logger.info("Getting market page for app ids");
        AppIdsHandle handle = new AppIdsHandle();
        try {
            http.get("http://steamcommunity.com/market/", handle);
        }
        catch (IOException e) {
            logger.error("Error getting outstanding listings", e);
        }
        return handle.getAppIds();
    }
}
