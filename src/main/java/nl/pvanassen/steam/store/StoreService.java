package nl.pvanassen.steam.store;

import java.util.Set;

import nl.pvanassen.steam.community.friends.FriendService;
import nl.pvanassen.steam.store.buy.BuyService;
import nl.pvanassen.steam.store.buyorder.BuyOrderService;
import nl.pvanassen.steam.store.history.HistoryService;
import nl.pvanassen.steam.store.inventory.InventoryService;
import nl.pvanassen.steam.store.item.ItemService;
import nl.pvanassen.steam.store.listing.ListingService;
import nl.pvanassen.steam.store.login.LoginService;
import nl.pvanassen.steam.store.marketpage.MarketPageService;
import nl.pvanassen.steam.store.tradeoffer.TradeOfferService;

/**
 * Interface defining a connecion to the steam store
 *
 * @author Paul van Assen
 */
public interface StoreService {
    /**
     * @return The app ids in the market
     */
    Set<Integer> getAppIds();

    /**
     * @return Buy service
     */
    BuyService getBuyService();

    /**
     * @return Buy order handler
     */
    BuyOrderService getBuyOrderService();

    /**
     * @return History service
     */
    HistoryService getHistoryService();
    
    /**
     * @return Inventory service for querying inventories
     */
    InventoryService getInventoryService();

    /**
     * @return Listing service for getting new listings
     */
    ListingService getListingService();

    
    /**
     * @return Service for getting information about items
     */
    ItemService getItemService();

    /**
     * @return Service for logging in to Steam
     */
    LoginService getLoginService();

    /**
     * @return All information about the market page
     */
    MarketPageService getMarketPageService();

    /**
     * @return Handles trade offers
     */
    TradeOfferService getTradeofferService();
    
    /**
     * @return Service for handling friends
     */
    FriendService getFriendService();

    /**
     * Retrieve the current cookies from the HTTP session
     * 
     * @return The current cookies from the http session
     */
    String getCookies();
}
