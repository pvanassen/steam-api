package nl.pvanassen.steam.store;

import java.util.Set;

import nl.pvanassen.steam.store.buy.BuyService;
import nl.pvanassen.steam.store.buyorder.BuyOrderService;
import nl.pvanassen.steam.store.history.HistoryService;
import nl.pvanassen.steam.store.inventory.InventoryService;
import nl.pvanassen.steam.store.item.ItemService;
import nl.pvanassen.steam.store.listing.ListingService;
import nl.pvanassen.steam.store.login.LoginService;
import nl.pvanassen.steam.store.marketpage.MarketPageService;
import nl.pvanassen.steam.store.remove.RemoveService;
import nl.pvanassen.steam.store.sell.SellService;
import nl.pvanassen.steam.store.tradeoffer.TradeofferService;

/**
 * Interface defining a connecion to the steam store
 *
 * @author Paul van Assen
 */
public interface StoreService {
    Set<Integer> getAppIds();

    BuyService getBuyService();

    BuyOrderService getBuyOrderService();

    HistoryService getHistoryService();

    InventoryService getInventoryService();

    ListingService getListingService();

    ItemService getItemService();

    LoginService getLoginService();

    MarketPageService getMarketPageService();

    SellService getSellService();

    TradeofferService getTradeofferService();

    RemoveService getRemoveService();

    /**
     * Retrieve the current cookies from the HTTP session
     * 
     * @return The current cookies from the http session
     */
    String getCookies();
}
