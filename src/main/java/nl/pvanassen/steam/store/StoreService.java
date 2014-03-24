package nl.pvanassen.steam.store;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Interface defining a connecion to the steam store
 * 
 * @author Paul van Assen
 */
public interface StoreService {
    /**
     * Buy a listed item
     * 
     * @param listingId Listing id
     * @param fee Fee to pay
     * @param subTotal Subtotal
     * @return The results of a purchase
     */
    BuyResult buy( Listing listing );

    /**
     * @param executorService Executor service for multi-threading fetch
     * @param handle Handle overview item
     */
    void getAllItems( ExecutorService executorService, GenericHandle<OverviewItem> handle );

    /**
     * Retrieve all that is in the inventory
     * 
     * @return List of items in the inventory
     */
    List<InventoryItem> getInventory();

    /**
     * The datapoints will always be called first. Once they are done the listings handle is called
     * 
     * @param appId Appid of the item to get
     * @param urlName url name of the item to get
     * @param dataPointHandle If a datapoint is found this handle is called
     * @param listingHandle If all datapoints have been processed, the listings are handled through this call
     */
    void getItem( int appId, String urlName, GenericHandle<StatDataPoint> dataPointHandle, GenericHandle<Listing> listingHandle );

    /**
     * Retrieve the newly listed
     * 
     * @return Listings
     */
    List<Listing> getNewlyListed();

    /**
     * @return Returns the amount in cents currently in the wallet
     */
    int getWallet();

    /**
     * Sell an item in the inventory
     * 
     * @param assetId
     * @param appId
     * @param contextId
     * @param price
     * @return True if successful, false if not
     */
    boolean sell( String assetId, int appId, String urlName, int contextId, int price );

    /**
     * Get sold items currently offered
     * @return a list of market history items
     */
    List<MarketHistory> getSoldItemsFromHistory();
}
