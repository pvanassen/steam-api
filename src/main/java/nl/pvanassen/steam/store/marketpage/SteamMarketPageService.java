/**
 *
 */
package nl.pvanassen.steam.store.marketpage;

import nl.pvanassen.steam.error.SteamException;
import nl.pvanassen.steam.http.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author Paul van Assen
 */
public class SteamMarketPageService implements MarketPageService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Http http;
    private final String username;

    /**
     * @param http For mocking
     * @param username The username of the owner of this service. This is used
     *            to calculate the referer
     */
    public SteamMarketPageService(Http http, String username) {
        this.http = http;
        this.username = username;
    }

    @Override
    public Set<Integer> getAppIds() {
        logger.info("Getting market page for app ids");
        AppIdsHandle handle = new AppIdsHandle();
        http.get("http://steamcommunity.com/market/", handle, false);
        return handle.getAppIds();
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.marketpage.MarketPageService#getMarketPage()
     */
    @Override
    public MarketPage getMarketPage() {
        logger.info("Getting market page for " + username);
        MarketPageHandle handle = new MarketPageHandle();
        http.get("http://steamcommunity.com/market/", handle, false);
        if (handle.isError()) {
            throw new SteamException("Error getting the market page, unknown error");
        }
        return handle.getOutstandings();
    }


}
