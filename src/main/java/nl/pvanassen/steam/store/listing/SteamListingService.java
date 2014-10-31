/**
 *
 */
package nl.pvanassen.steam.store.listing;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.store.common.Listing;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul van Assen
 */
public class SteamListingService implements ListingService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Http http;

    /**
     * @param http For mocking
     */
    public SteamListingService(Http http) {
        this.http = http;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.listing.ListingService#getAsyncNewlyListed(int,
     *      java.lang.String, nl.pvanassen.steam.store.listing.ListingDeque)
     */
    @Override
    public void getAsyncNewlyListed(int currency, String country, ListingDeque queue) {
        try {
            ListingHandle handle = new ListingHandle(objectMapper, queue, country);
            http.get("http://steamcommunity.com/market/recent?currency=" + currency + "&country=" + country + "&language=english&" + System.currentTimeMillis(), handle);
        }
        catch (IOException e) {
            logger.error("Error getting inventory", e);
        }

    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.listing.ListingService#getNewlyListed(int,
     *      java.lang.String)
     */
    @Override
    public List<Listing> getNewlyListed(int currency, String country) {
        try {
            ListingDeque listing = new ListingDeque(60000);
            ListingHandle handle = new ListingHandle(objectMapper, listing, country);
            http.get("http://steamcommunity.com/market/recent?currency=" + currency + "&country=" + country + "&language=english&" + System.currentTimeMillis(), handle);
            return listing.getDeque();
        }
        catch (IOException e) {
            logger.error("Error getting inventory", e);
        }
        return Collections.emptyList();
    }
}
