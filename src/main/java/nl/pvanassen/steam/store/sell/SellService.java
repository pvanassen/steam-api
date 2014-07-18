/**
 * 
 */
package nl.pvanassen.steam.store.sell;

/**
 * @author Paul van Assen
 *
 */
public interface SellService {
    /**
     * Sell an item in the inventory
     * 
     * @param assetId Asset id to sell
     * @param appId App id to sell
     * @param urlName Url name to sell
     * @param contextId Context id to sell
     * @param price Price to use
     * @return True if successful, false if not
     */
    boolean sell(String assetId, int appId, String urlName, int contextId, int price);
}
