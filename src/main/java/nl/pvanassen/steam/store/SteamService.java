package nl.pvanassen.steam.store;

import java.util.Set;

import nl.pvanassen.steam.community.friends.*;
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
import nl.pvanassen.steam.store.tradeoffer.SteamTradeOfferService;
import nl.pvanassen.steam.store.tradeoffer.TradeOfferService;

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
    private final TradeOfferService tradeofferService;
    private final FriendService friendService;
    /**
     * @param http For mocking
     */
    private SteamService(Http http, String username) {
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
        tradeofferService = new SteamTradeOfferService(http);
        inventoryService = new SteamInventoryService(http, username, appIds);
        friendService = new SteamFriendService(http, username);
    }

    SteamService(String cookies, String username) {
        this(Http.getInstance(cookies, username), username);
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getAppIds()
     */
    @Override
    public Set<Integer> getAppIds() {
        return appIds;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getBuyService()
     */
    @Override
    public BuyService getBuyService() {
        return buyService;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getBuyOrderService()
     */
    @Override
    public BuyOrderService getBuyOrderService() {
        return buyOrderService;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getHistoryService()
     */
    @Override
    public HistoryService getHistoryService() {
        return historyService;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getInventoryService()
     */
    @Override
    public InventoryService getInventoryService() {
        return inventoryService;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getListingService()
     */
    @Override
    public ListingService getListingService() {
        return listingService;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getItemService()
     */
    @Override
    public ItemService getItemService() {
        return itemService;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getLoginService()
     */
    @Override
    public LoginService getLoginService() {
        return loginService;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getMarketPageService()
     */
    @Override
    public MarketPageService getMarketPageService() {
        return marketPageService;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getSellService()
     */
    @Override
    public SellService getSellService() {
        return sellService;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getTradeofferService()
     */
    @Override
    public TradeOfferService getTradeofferService() {
        return tradeofferService;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getFriendService()
     */
    @Override
    public FriendService getFriendService() {
        return friendService;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getCookies()
     */
    @Override
    public String getCookies() {
        return http.getCookies();
    }
}
