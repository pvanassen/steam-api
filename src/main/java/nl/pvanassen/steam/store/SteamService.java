package nl.pvanassen.steam.store;

import java.util.List;
import java.util.Set;

import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.store.buy.BuyResult;
import nl.pvanassen.steam.store.buy.BuyService;
import nl.pvanassen.steam.store.buy.SteamBuyService;
import nl.pvanassen.steam.store.buyorder.BuyOrderService;
import nl.pvanassen.steam.store.buyorder.BuyOrderStatus;
import nl.pvanassen.steam.store.buyorder.SteamBuyOrderService;
import nl.pvanassen.steam.store.common.BuyOrder;
import nl.pvanassen.steam.store.common.Item;
import nl.pvanassen.steam.store.common.Listing;
import nl.pvanassen.steam.store.history.History;
import nl.pvanassen.steam.store.history.HistoryService;
import nl.pvanassen.steam.store.history.SteamHistoryService;
import nl.pvanassen.steam.store.inventory.InventoryItem;
import nl.pvanassen.steam.store.inventory.InventoryService;
import nl.pvanassen.steam.store.inventory.SteamInventoryService;
import nl.pvanassen.steam.store.item.ItemService;
import nl.pvanassen.steam.store.item.OverviewItem;
import nl.pvanassen.steam.store.item.StatDataPoint;
import nl.pvanassen.steam.store.item.SteamItemService;
import nl.pvanassen.steam.store.listing.ListingDeque;
import nl.pvanassen.steam.store.listing.ListingService;
import nl.pvanassen.steam.store.listing.SteamListingService;
import nl.pvanassen.steam.store.login.LoginService;
import nl.pvanassen.steam.store.login.SteamGuardException;
import nl.pvanassen.steam.store.login.SteamLoginService;
import nl.pvanassen.steam.store.login.VerificationException;
import nl.pvanassen.steam.store.outstanding.MarketPage;
import nl.pvanassen.steam.store.outstanding.OutstandingService;
import nl.pvanassen.steam.store.outstanding.SteamOutstandingService;
import nl.pvanassen.steam.store.remove.RemoveService;
import nl.pvanassen.steam.store.remove.SteamRemoveService;
import nl.pvanassen.steam.store.sell.SellService;
import nl.pvanassen.steam.store.sell.SteamSellService;
import nl.pvanassen.steam.store.tradeoffer.SteamTradeofferService;
import nl.pvanassen.steam.store.tradeoffer.TradeofferService;

import com.google.common.base.Optional;

/**
 * Interface to the steam store
 * 
 * @author Paul van Assen
 */
class SteamService implements StoreService {
//    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Http http;
    private final Set<Integer> appIds;
    private final BuyService buyService;
    private final BuyOrderService buyOrderService;
    private final HistoryService historyService;
    private final InventoryService inventoryService;
    private final ListingService listingService;
    private final ItemService itemService;
    private final LoginService loginService;
    private final OutstandingService outstandingService;
    private final SellService sellService;
    private final TradeofferService tradeofferService;
    private final RemoveService removeService;
    
    SteamService(String cookies, String username) {
        this(Http.getInstance(cookies, username), username);
    }

    /**
     * @param http For mocking
     */
    SteamService(Http http, String username) {
        this.http = http;
        loginService = new SteamLoginService(http);
        outstandingService = new SteamOutstandingService(http);
        buyService = new SteamBuyService(http, username);
        buyOrderService = new SteamBuyOrderService(http, username);
        historyService = new SteamHistoryService(http);
        listingService = new SteamListingService(http);
        itemService = new SteamItemService(http);
        sellService = new SteamSellService(http, username);
        tradeofferService = new SteamTradeofferService(http);
        removeService = new SteamRemoveService(http, username);
        appIds = getOutstandings().getAppIds();
        inventoryService = new SteamInventoryService(http, username, appIds);
    }

    @Override
    public BuyResult buy(String listingId, int fee, int subTotal) {
    	return buyService.buy(new BuyOrder(0, "", listingId, subTotal, fee));
    }

    /**
     * {@inheritDoc}
     * 
     * @see nl.pvanassen.steam.store.StoreService#getAllItems(nl.pvanassen.steam.store.GenericHandle)
     */
    @Override
    public void getAllItems(GenericHandle<OverviewItem> genericHandle) {
        itemService.getAllItems(genericHandle);
    }
    
    @Override
    public List<InventoryItem> getInventory() {
        return inventoryService.getInventory();
    }

    @Override
    public List<InventoryItem> getInventory(String username) {
        return inventoryService.getInventory(username);
    }
    
    @Override
    public List<InventoryItem> getInventory(int appId) {
        return inventoryService.getInventory(appId);
    }
    
    @Override
    public List<InventoryItem> getInventory(String username, int appId) {
        return inventoryService.getInventory(username, appId);
    }

    @Override
    public void getItem(int appId, String urlName, GenericHandle<StatDataPoint> dataPointHandle,
            GenericHandle<Listing> listingHandle) {
    	itemService.getItem(appId, urlName, dataPointHandle, listingHandle);
    }
    
    @Override
    public List<Listing> getNewlyListed(int currency, String country) {
    	return listingService.getNewlyListed(currency, country);
    }

    @Override
    public void getAsyncNewlyListed(int currency, String country, ListingDeque queue) {
    	listingService.getAsyncNewlyListed(currency, country, queue);
    }

    @Override
    public boolean sell(String assetId, int appId, String urlName, int contextId, int price) {
    	return sellService.sell(assetId, appId, urlName, contextId, price);
    }

    @Override
    public boolean removeListing(String listingId) {
    	return removeService.removeListing(listingId);
    }
    
    @Override
    public List<History> getSoldItemsFromHistory() {
    	return historyService.getSoldItemsFromHistory();
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getOutstandings()
     */
    @Override
    public MarketPage getOutstandings() {
    	return outstandingService.getOutstandings();
    }

    @Override
    public void login(String user, String password) throws VerificationException, SteamGuardException {
    	loginService.login(user, password);
    }

    @Override
    public void verification(String code) {
    	loginService.verification(code);
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
    
    @Override
    public Set<Integer> getAppIds() {
        return getOutstandings().getAppIds();
    }
    
    @Override
    public int makeTradeOffer(long partner, List<InventoryItem> me, List<InventoryItem> them, Optional<String> message) {
        return tradeofferService.makeTradeOffer(partner, me, them, message);
    }

    @Override
    public void cancelBuyOrder(String id) {
    	buyOrderService.cancelBuyOrder(id);
    }
    
    @Override
    public String createBuyOrder(Item item, int currencyId, int priceTotal, int quantity) {
    	return buyOrderService.createBuyOrder(item, currencyId, priceTotal, quantity);
    }
    
    @Override
    public BuyOrderStatus getBuyOrderStatus(String buyOrderId) {
    	return buyOrderService.getBuyOrderStatus(buyOrderId);
    }
}
