/**
 *
 */
package nl.pvanassen.steam.store.listing;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.store.GenericHandle;
import nl.pvanassen.steam.store.common.Item;
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
    private final Random random = new Random();

    /**
     * @param http For mocking
     */
    public SteamListingService(Http http) {
        this.http = http;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.listing.ListingService#getAsyncNewlyListed(java.lang.String, int, java.lang.String, nl.pvanassen.steam.store.listing.ListingDeque)
     */
    @Override
    public void getAsyncNewlyListed(String host, int currency, String country, ListingDeque queue) {
        try {
            ListingHandle handle = new ListingHandle(objectMapper, queue, country);
            http.get("http://" + host + "/market/recent?currency=" + random.nextInt(32) + "&currency=" + currency + "&country=" + country + "&language=english", handle, true, true);
        }
        catch (IOException e) {
            logger.error("Error getting newly listed", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.listing.ListingService#getAsyncNewlyListed(int,
     *      java.lang.String, nl.pvanassen.steam.store.listing.ListingDeque)
     */
    @Override
    public void getAsyncNewlyListed(int currency, String country, ListingDeque queue) {
        getAsyncNewlyListed("steamcommunity.com", currency, country, queue);
    }
    
    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.listing.ListingService#getAsyncListed(nl.pvanassen.steam.store.common.Item, int, java.lang.String, int, nl.pvanassen.steam.store.GenericHandle)
     */
    @Override
    public void getAsyncListed(Item item, int currency, String country, int start, GenericHandle<Listing> listingHandle) {
        getAsyncListed("steamcommunity.com", item, currency, country, start, listingHandle);
    }
    
    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.listing.ListingService#getAsyncListed(java.lang.String, nl.pvanassen.steam.store.common.Item, int, java.lang.String, int, nl.pvanassen.steam.store.GenericHandle)
     */
    @Override
    public void getAsyncListed(String host, Item item, int currency, String country, int start, GenericHandle<Listing> listingHandle) {
        try {
            ListingHandle handle = new ListingHandle(objectMapper, listingHandle, country);
            String url = "http://" + host + "/market/listings/" + item.getAppId() + "/" + item.getUrlName() + "/render/?query=&start=" + start + "&count=10&currency=" + currency + "&country=" + country + "&language=english";
            logger.info("Sending get request to " + url);
            http.get(url, handle, true, true);
        }
        catch (IOException e) {
            logger.error("Error getting listed items", e);
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
            http.get("http://steamcommunity.com/market/recent?currency=" + currency + "&country=" + country + "&language=english", handle, true, true);
            return listing.getDeque();
        }
        catch (IOException e) {
            logger.error("Error getting newly listed", e);
        }
        return Collections.emptyList();
    }
}
