package nl.pvanassen.steam.store;

import java.util.Set;

import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.store.buy.BuyService;
import nl.pvanassen.steam.store.buy.SteamBuyService;
import nl.pvanassen.steam.store.buyorder.BuyOrderService;
import nl.pvanassen.steam.store.buyorder.SteamBuyOrderService;
import nl.pvanassen.steam.store.history.HistoryService;
import nl.pvanassen.steam.store.history.SteamHistoryService;
import nl.pvanassen.steam.store.inventory.InventoryService;
import nl.pvanassen.steam.store.inventory.SteamInventoryService;
import nl.pvanassen.steam.store.item.ItemService;
import nl.pvanassen.steam.store.item.SteamItemService;
import nl.pvanassen.steam.store.listing.ListingService;
import nl.pvanassen.steam.store.listing.SteamListingService;
import nl.pvanassen.steam.store.login.LoginService;
import nl.pvanassen.steam.store.login.SteamLoginService;
import nl.pvanassen.steam.store.marketpage.AppIds;
import nl.pvanassen.steam.store.marketpage.MarketPageService;
import nl.pvanassen.steam.store.marketpage.SteamMarketPageService;
import nl.pvanassen.steam.store.sell.SellService;
import nl.pvanassen.steam.store.sell.SteamSellService;
import nl.pvanassen.steam.store.tradeoffer.SteamTradeofferService;
import nl.pvanassen.steam.store.tradeoffer.TradeofferService;

import com.google.common.collect.ImmutableSet;

/**
 * Interface to the steam store
 *
 * @author Paul van Assen
 */
class SteamService implements StoreService {
    private final Http http;
    private final Set<Integer> appIds;
    private final BuyService buyService;
    private final BuyOrderService buyOrderService;
    private final HistoryService historyService;
    private final InventoryService inventoryService;
    private final ListingService listingService;
    private final ItemService itemService;
    private final LoginService loginService;
    private final MarketPageService marketPageService;
    private final SellService sellService;
    private final TradeofferService tradeofferService;

    /**
     * @param http
     *            For mocking
     */
    SteamService(Http http, String username) {
        appIds = ImmutableSet.copyOf(AppIds.getAppids());
        this.http = http;
        loginService = new SteamLoginService(http);
        marketPageService = new SteamMarketPageService(http, username);
        buyService = new SteamBuyService(http, username);
        buyOrderService = new SteamBuyOrderService(http, username);
        historyService = new SteamHistoryService(http);
        listingService = new SteamListingService(http);
        itemService = new SteamItemService(http);
        sellService = new SteamSellService(http, username);
        tradeofferService = new SteamTradeofferService(http);
        inventoryService = new SteamInventoryService(http, username, appIds);
    }

    SteamService(String cookies, String username) {
        this(Http.getInstance(cookies, username), username);
    }

    public Set<Integer> getAppIds() {
        return appIds;
    }

    public BuyService getBuyService() {
        return buyService;
    }

    public BuyOrderService getBuyOrderService() {
        return buyOrderService;
    }

    public HistoryService getHistoryService() {
        return historyService;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public ListingService getListingService() {
        return listingService;
    }

    public ItemService getItemService() {
        return itemService;
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public MarketPageService getMarketPageService() {
        return marketPageService;
    }

    public SellService getSellService() {
        return sellService;
    }

    public TradeofferService getTradeofferService() {
        return tradeofferService;
    }
    
    @Override
    public String getCookies() {
        return http.getCookies();
    }
}
