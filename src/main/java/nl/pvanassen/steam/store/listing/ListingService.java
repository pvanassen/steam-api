/**
 *
 */
package nl.pvanassen.steam.store.listing;

import java.util.List;

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
     * Retrieve the newly listed
     *
     * @param currency Currency to retrieve
     * @param country Country to get listings for
     * @return Listings
     */
    List<Listing> getNewlyListed(int currency, String country);

}
