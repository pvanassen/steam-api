package nl.pvanassen.steam.store;

import java.util.List;
import java.util.Set;

import nl.pvanassen.steam.store.buy.BuyResult;
import nl.pvanassen.steam.store.buyorder.BuyOrderStatus;
import nl.pvanassen.steam.store.common.Item;
import nl.pvanassen.steam.store.common.Listing;
import nl.pvanassen.steam.store.history.History;
import nl.pvanassen.steam.store.inventory.InventoryItem;
import nl.pvanassen.steam.store.item.OverviewItem;
import nl.pvanassen.steam.store.item.StatDataPoint;
import nl.pvanassen.steam.store.listing.ListingDeque;
import nl.pvanassen.steam.store.login.LoginService;
import nl.pvanassen.steam.store.marketpage.MarketPage;
import nl.pvanassen.steam.store.tradeoffer.Tradeoffer;

import com.google.common.base.Optional;

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
     * Retrieve all that is in the inventory of the user
     * 
     * @return List of items in the inventory
     */
    List<InventoryItem> getInventory();

    /**
     * Retrieve the inventory of a user for only one app id
     * 
     * @param appId Retrieve only one app id
     * @return List of items in the inventory
     */
    List<InventoryItem> getInventory(int appId);
    
    /**
     * Retrieve all that is in the inventory of a user
     * 
     * @param username The username to get the inventory from
     * @return List of items in the inventory
     */
    List<InventoryItem> getInventory(String username);

    /**
     * Retrieve all that is in the inventory of a user and an app id
     * 
     * @param username The username to get the inventory from
     * @param appId Retrieve only one app id
     * @return List of items in the inventory
     */
    List<InventoryItem> getInventory(String username, int appId);

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
     * @param currency Currency to retrieve
     * @param country Country to get listings for
     * @return Listings
     */
    List<Listing> getNewlyListed(int currency, String country);

    /**
     * Retrieve all newly listed
     * @param currency Currency to retrieve
     * @param country Country to get listings for
     * 
     * @param queue Queue to add the items to
     */
    void getAsyncNewlyListed(int currency, String country, ListingDeque queue);

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
    History getHistory(String lastSteamId);

    /**
     * Get outstanding items
     * 
     * @return Outstanding items
     */
    MarketPage getOutstandings();
    
    /**
     * Remove an active listing
     * 
     * @param listingId Listing to remove
     * @return True if succesful removed
     */
    boolean removeListing(String listingId);

    LoginService getLoginService();
    
    /**
     * Get the current cookies
     * @return Current set of cookies in a String
     */
    String getCookies();
    
    /**
     * Get all known app ids
     * @return Returns a set containing all known tradable app ids
     */
    Set<Integer> getAppIds();
    
    /**
     * Make a trade offer of items to a user
     * @param partner Trading partner ID
     * @param me What do I offer
     * @param them What do they offer
     * @param message A message for the trade
     * @return The trade offer id
     */
    int makeTradeOffer(long partner, List<InventoryItem> me, List<InventoryItem> them, Optional<String> message);
    
    /**
     * @return A list of trade offers
     */
    List<Tradeoffer> getTradeOffers();
    
    void acceptTradeOffer(Tradeoffer tradeoffer);

    
    String createBuyOrder(Item item, int currencyId, int priceTotal, int quantity);
    
    BuyOrderStatus getBuyOrderStatus(String id);
    
    void cancelBuyOrder(String id);
}
