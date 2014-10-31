/**
 *
 */
package nl.pvanassen.steam.store.sell;

/**
 * @author Paul van Assen
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
     * @throws SellException In case of an error
     */
    void sell(String assetId, int appId, String urlName, int contextId, int price) throws SellException;
}
