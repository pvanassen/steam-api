/**
 *
 */
package nl.pvanassen.steam.store.marketpage;

import java.util.Set;

/**
 * @author Paul van Assen
 *
 */
public interface MarketPageService {

    /**
     * Get the known app ids
     * 
     * @return Set of app ids
     */
    Set<Integer> getAppIds();

    /**
     * Get outstanding items
     *
     * @return Outstanding items
     */
    MarketPage getOutstandings();
    
    /**
     * Remove an active listing
     *
     * @param listingId
     *            Listing to remove
     * @return True if succesful removed
     */
    boolean removeListing(String listingId);

}
