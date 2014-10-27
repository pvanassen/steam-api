/**
 *
 */
package nl.pvanassen.steam.store.remove;

/**
 * @author Paul van Assen
 *
 */
public interface RemoveService {

    /**
     * Remove an active listing
     *
     * @param listingId
     *            Listing to remove
     * @return True if succesful removed
     */
    boolean removeListing(String listingId);

}
