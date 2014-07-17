/**
 * 
 */
package nl.pvanassen.steam.store.item;

import java.io.IOException;

import nl.pvanassen.steam.error.SteamException;
import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.store.GenericHandle;
import nl.pvanassen.steam.store.common.Listing;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul van Assen
 *
 */
public class SteamItemService implements ItemService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Http http;

    public SteamItemService(String cookies, String username) {
        this(Http.getInstance(cookies, username));
    }

    /**
     * @param http For mocking
     */
    public SteamItemService(Http http) {
        this.http = http;
    }

    /**
     * 
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.listing.ListingService#getItem(int, java.lang.String, nl.pvanassen.steam.store.GenericHandle, nl.pvanassen.steam.store.GenericHandle)
     */
    @Override
    public void getItem(int appId, String urlName, GenericHandle<StatDataPoint> dataPointHandle,
            GenericHandle<Listing> listingHandle) {
    	String url = "http://steamcommunity.com/market/listings/" + appId + "/" + urlName;
        ListingPageScriptHandle handle = new ListingPageScriptHandle(objectMapper);
        try {
            http.get(url, handle);
        }
        catch (IOException e) {
            logger.error("Error fetching listing page data", e);
        	throw new SteamException("Error getting data for url: " + url, e);
        }
        if (handle.isError()) {
        	throw new SteamException("Error getting data for url: " + url);
        }
        JsonNode priceHistoryInfo = handle.getPriceHistoryInfo();
        for (StatDataPoint point : new ListingStatDataPointIterator(priceHistoryInfo)) {
            dataPointHandle.handle(point);
        }
        if (listingHandle == null) {
            return;
        }
        JsonNode listingInfo = handle.getListingInfo();
        for (Listing item : new ListingItemIterator(appId, urlName, listingInfo)) {
            listingHandle.handle(item);
        }
    }
}
