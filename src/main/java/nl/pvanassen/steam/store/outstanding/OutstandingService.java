/**
 * 
 */
package nl.pvanassen.steam.store.outstanding;


/**
 * @author Paul van Assen
 *
 */
public interface OutstandingService {

    /**
     * Get outstanding items
     * 
     * @return Outstanding items
     */
    MarketPage getOutstandings();

}
