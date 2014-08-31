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
     * Get outstanding items
     * 
     * @return Outstanding items
     */
    MarketPage getOutstandings();

    /**
     * Get the known app ids
     * @return Set of app ids
     */
    Set<Integer> getAppIds();
}
