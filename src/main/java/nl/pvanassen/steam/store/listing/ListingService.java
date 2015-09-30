/**
 *
 */
package nl.pvanassen.steam.store.listing;

import nl.pvanassen.steam.store.CommandResult;
import nl.pvanassen.steam.store.common.GenericHandle;
import nl.pvanassen.steam.store.common.Item;
import nl.pvanassen.steam.store.common.Listing;

/**
 * @author Paul van Assen
 */
public interface ListingService {
    /**
     * Retrieve all newly listed
     * 
     * @param currency Currency to retrieve
     * @param host Host to connect
     * @param country Country to get listings for
     * @param listingHandle Triggered when item is received
     */
    void getAsyncNewlyListed(String host, int currency, String country, GenericHandle<Listing> listingHandle);

    /**
     * Retrieve all newly listed
     * 
     * @param item Item to get the listings for
     * @param currency Currency to retrieve
     * @param country Country to get listings for
     * @param start Start of the first item
     * @param listingHandle Queue to add the items to
     */
    void getAsyncListed(Item item, int currency, String country, int start, GenericHandle<Listing> listingHandle);

    /**
     * Retrieve all newly listed
     * 
     * @param item Item to get the listings for
     * @param currency Currency to retrieve
     * @param host Host to connect
     * @param country Country to get listings for
     * @param start Start of the first item
     * @param listingHandle Queue to add the items to
     */
    void getAsyncListed(String host, Item item, int currency, String country, int start, GenericHandle<Listing> listingHandle);

    /**
     * Remove an active listing
     *
     * @param listingId Listing to remove
     * @param result Handle for processing the result
     */
    void removeListing(String listingId, GenericHandle<CommandResult> result);

    /**
     * Create a listing from an item in the inventory
     *
     * @param assetId Asset id to sell
     * @param appId App id to sell
     * @param urlName Url name to sell
     * @param contextId Context id to sell
     * @param price Price to use
     * @param result Handle for processing the result
     */
    void createListing(String assetId, int appId, String urlName, int contextId, int price, GenericHandle<CommandResult> result);

}
