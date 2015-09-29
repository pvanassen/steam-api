/**
 *
 */
package nl.pvanassen.steam.store.listing;

import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.store.CommandResult;
import nl.pvanassen.steam.store.common.GenericHandle;
import nl.pvanassen.steam.store.common.Item;
import nl.pvanassen.steam.store.common.Listing;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Paul van Assen
 */
public class SteamListingService implements ListingService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Http http;
    private final String username;

    /**
     * @param http For mocking
     * @param username For creating new listings
     */
    public SteamListingService(Http http, String username) {
        this.http = http;
        this.username = username;
    }

    @Override
    public void getAsyncNewlyListed(String host, int currency, String country, GenericHandle<Listing> listingHandle) {
        ListingHandle handle = new ListingHandle(objectMapper, listingHandle, country);
        http.get("http://" + host + "/market/recent?country=" + country + "&language=english&currency=" + currency, handle, true, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.listing.ListingService#getAsyncListed(nl.pvanassen.steam.store.common.Item, int, java.lang.String, int,
     *      nl.pvanassen.steam.store.common.GenericHandle)
     */
    @Override
    public void getAsyncListed(Item item, int currency, String country, int start, GenericHandle<Listing> listingHandle) {
        getAsyncListed("steamcommunity.com", item, currency, country, start, listingHandle);
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.listing.ListingService#getAsyncListed(java.lang.String, nl.pvanassen.steam.store.common.Item, int, java.lang.String, int,
     *      nl.pvanassen.steam.store.common.GenericHandle)
     */
    @Override
    public void getAsyncListed(String host, Item item, int currency, String country, int start, GenericHandle<Listing> listingHandle) {
        ListingHandle handle = new ListingHandle(objectMapper, listingHandle, country);
        String url = "http://" + host + "/market/listings/" + item.getAppId() + "/" + item.getUrlName() + "/render/?query=&start=" + start + "&count=10&currency=" + currency
                + "&country=" + country + "&language=english";
        logger.info("Sending get request to " + url);
        http.get(url, handle, true, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.listing.ListingService#removeListing(java.lang.String, nl.pvanassen.steam.store.common.GenericHandle)
     */
    @Override
    public void removeListing(String listingId, GenericHandle<CommandResult> handle) {
        ListingMutationHandle removeHandle = new ListingMutationHandle("remove", handle);
        try {
            http.post("http://steamcommunity.com/market/removelisting/" + listingId, new HashMap<String, String>(), removeHandle, "http://steamcommunity.com/id/" + username
                    + "/inventory/", true, false, false);
        }
        catch (IOException e) {
            logger.error("Error in removing listing", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.listing.ListingService#createListing(java.lang.String, int, java.lang.String, int, int, nl.pvanassen.steam.store.common.GenericHandle)
     */
    @Override
    public void createListing(String assetId, int appId, String urlName, int contextId, int price, GenericHandle<CommandResult> handle) {
        if (price < 1) {
            handle.handle(CommandResult.error("Error, price is too low: " + price));
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("amount", "1");
        params.put("appid", Integer.toString(appId));
        params.put("assetid", assetId);
        params.put("contextid", Integer.toString(contextId));
        params.put("price", Integer.toString(price));
        logger.info(params.toString());
        ListingMutationHandle sellHandle = new ListingMutationHandle("create", handle);
        try {
            http.post("https://steamcommunity.com/market/sellitem/", params, sellHandle, "http://steamcommunity.com/id/" + username + "/inventory/", true, false, false);
        }
        catch (IOException e) {
            logger.error("Error in creating listing", e);
        }
    }
}
