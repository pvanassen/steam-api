/**
 *
 */
package nl.pvanassen.steam.store.listing;

import java.util.List;

import nl.pvanassen.steam.store.common.*;

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
     * @param queue Queue to add the items to
     */
    void getAsyncNewlyListed(String host, int currency, String country, ListingDeque queue);

    /**
     * Retrieve all newly listed
     * 
     * @param currency Currency to retrieve
     * @param country Country to get listings for
     * @param queue Queue to add the items to
     */
    void getAsyncNewlyListed(int currency, String country, ListingDeque queue);

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
     * Retrieve the newly listed
     *
     * @param currency Currency to retrieve
     * @param country Country to get listings for
     * @return Listings
     */
    List<Listing> getNewlyListed(int currency, String country);


    /**
     * Remove an active listing
     *
     * @param listingId Listing to remove
     * @return True if succesful removed
     */
    boolean removeListing(String listingId);
    
    /**
     * Create a listing from an item in the inventory
     *
     * @param assetId Asset id to sell
     * @param appId App id to sell
     * @param urlName Url name to sell
     * @param contextId Context id to sell
     * @param price Price to use
     * @throws SellException In case of an error
     */
    void createListing(String assetId, int appId, String urlName, int contextId, int price) throws SellException;

}
