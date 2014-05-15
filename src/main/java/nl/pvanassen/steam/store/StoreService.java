package nl.pvanassen.steam.store;

import java.util.Deque;
import java.util.List;

/**
 * Interface defining a connecion to the steam store
 * 
 * @author Paul van Assen
 */
public interface StoreService {
    /**
     * Buy a listed item
     * 
     * @param listingId Listing to buy
     * @param fee Fee to pay
     * @param subTotal Sub total, total excluding fee
     * @return The results of a purchase
     */
    BuyResult buy(String listingId, int fee, int subTotal);

    /**
     * @param handle Handle overview item
     */
    void getAllItems(GenericHandle<OverviewItem> handle);

    /**
     * Retrieve all that is in the inventory
     * 
     * @param username The username to use
     * @return List of items in the inventory
     */
    List<InventoryItem> getInventory(String username);

    /**
     * Retrieve all newly listed
     * 
     * @param queue Queue to add the items to
     */
    void getAsyncNewlyListed(Deque<Listing> queue);

    /**
     * The datapoints will always be called first. Once they are done the listings handle is called
     * 
     * @param appId Appid of the item to get
     * @param urlName url name of the item to get
     * @param dataPointHandle If a datapoint is found this handle is called
     * @param listingHandle If all datapoints have been processed, the listings are handled through
     *        this call
     */
    void getItem(int appId, String urlName, GenericHandle<StatDataPoint> dataPointHandle,
            GenericHandle<Listing> listingHandle);

    /**
     * Retrieve the newly listed
     * 
     * @return Listings
     */
    List<Listing> getNewlyListed();

    /**
     * @return Returns the amount in cents currently in the wallet
     * @deprecated Please use getOutstandings
     */
    @Deprecated
    int getWallet();

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

    /**
     * Get sold items currently offered
     * 
     * @return a list of market history items
     */
    List<MarketHistory> getSoldItemsFromHistory();

    /**
     * Get outstanding items
     * 
     * @return Outstanding items
     */
    Outstandings getOutstandings();
    
    /**
     * Remove an active listing
     * 
     * @param listingId Listing to remove
     * @return True if succesful removed
     */
    boolean removeListing(String listingId);

    /**
     * Do an login attempt. If a code is requested a VerificationError is thrown
     * 
     * @param user Username
     * @param password Password
     * @throws VerificationException In case login failed
     * @throws SteamGuardException In case a code is requested
     */
    void login(String user, String password) throws VerificationException, SteamGuardException;

    /**
     * Verification based on a code
     * 
     * @param code The requested code
     */
    void verification(String code);
    
    /**
     * Get the current cookies
     * @return Current set of cookies in a String
     */
    String getCookies();
}
