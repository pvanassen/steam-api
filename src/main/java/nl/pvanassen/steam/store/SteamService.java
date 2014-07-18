package nl.pvanassen.steam.store;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.pvanassen.steam.error.SteamException;
import nl.pvanassen.steam.error.SteamGuardException;
import nl.pvanassen.steam.error.VerificationException;
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
import nl.pvanassen.steam.store.item.StatDataPoint;
import nl.pvanassen.steam.store.item.SteamItemService;
import nl.pvanassen.steam.store.listing.ListingDeque;
import nl.pvanassen.steam.store.listing.ListingService;
import nl.pvanassen.steam.store.listing.SteamListingService;
import nl.pvanassen.steam.store.login.LoginService;
import nl.pvanassen.steam.store.login.SteamLoginService;
import nl.pvanassen.steam.store.outstanding.MarketPage;
import nl.pvanassen.steam.store.outstanding.OutstandingService;
import nl.pvanassen.steam.store.outstanding.SteamOutstandingService;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * Interface to the steam store
 * 
 * @author Paul van Assen
 */
class SteamService implements StoreService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Http http;
    private final String username;
    private final Set<Integer> appIds;
    private final BuyService buyService;
    private final BuyOrderService buyOrderService;
    private final HistoryService historyService;
    private final InventoryService inventoryService;
    private final ListingService listingService;
    private final ItemService itemService;
    private final LoginService loginService;
    private final OutstandingService outstandingService;
    
    SteamService(String cookies, String username) {
        this(Http.getInstance(cookies, username), username);
    }

    /**
     * @param http For mocking
     */
    SteamService(Http http, String username) {
        this.http = http;
        this.username = username;
        appIds = getOutstandings().getAppIds();
        buyService = new SteamBuyService(http, username);
        buyOrderService = new SteamBuyOrderService(http, username);
        historyService = new SteamHistoryService(http);
        inventoryService = new SteamInventoryService(http, username, appIds);
        listingService = new SteamListingService(http);
        itemService = new SteamItemService(http);
        loginService = new SteamLoginService(http);
        outstandingService = new SteamOutstandingService(http);
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
        try {
            OverviewHandle handle = new OverviewHandle(genericHandle, objectMapper);
            // Initial high, will be corrected on first run
            int totalCount = 5000;
            for (int start = 0; start < totalCount; start += 100) {
                do {
                    http.get("http://steamcommunity.com/market/search/render/?query=&search_descriptions=0&start=" +
                             start + "&count=100", handle);
                    totalCount = handle.getTotalCount();
                    // Stop on overrun
                    if (handle.isLastPage()) {
                        return;
                    }
                }
                while (handle.isError());
            }
        }
        catch (IOException e) {
            logger.error("Error handling item", e);
        }
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
        try {
            Map<String, String> params = new HashMap<>();
            params.put("amount", "1");
            params.put("appid", Integer.toString(appId));
            params.put("assetid", assetId);
            params.put("contextid", Integer.toString(contextId));
            params.put("price", Integer.toString(price));
            logger.info(params.toString());
            SellHandle sellHandle = new SellHandle();
            http.post("https://steamcommunity.com/market/sellitem/", params, sellHandle, "http://steamcommunity.com/id/" + username + "/inventory/");
            return !sellHandle.isError();
        }
        catch (IOException | RuntimeException e) {
            logger.error("Error posting data", e);
            return false;
        }
    }

    @Override
    public boolean removeListing(String listingId) {
    	try {
    		RemoveHandle removeHandle = new RemoveHandle();
    		http.post("http://steamcommunity.com/market/removelisting/" + listingId, new HashMap<String,String>(), removeHandle, "http://steamcommunity.com/id/" + username + "/inventory/");
            return !removeHandle.isError();
    	}
    	catch (IOException | RuntimeException e) {
            logger.error("Error posting data", e);
            return false;
    	}
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
    public int makeTradeOffer(long steamId, List<InventoryItem> me, List<InventoryItem> them, Optional<String> message) {
        ObjectNode tradeOffer = objectMapper.createObjectNode();
        tradeOffer.put("newversion", true);
        tradeOffer.put("version", 3);
        ObjectNode meNode = tradeOffer.putObject("me");
        fillTradeNode(me, meNode);
        ObjectNode themNode = tradeOffer.putObject("them");
        fillTradeNode(them, themNode);
        Map<String,String> params = new HashMap<>();
        params.put("json_tradeoffer", tradeOffer.toString());
        params.put("partner", Long.toString(steamId));
        params.put("trade_offer_create_params", "{}");
        params.put("tradeoffermessage", message.or(""));
        logger.info("Sending: " + params.toString());
        try {
            TradeOfferHandle handle = new TradeOfferHandle(objectMapper);
            http.post("https://steamcommunity.com/tradeoffer/new/send", params, handle, "http://steamcommunity.com/tradeoffer/new/?partner=" + Long.toString(steamId & 0xFFFFFFFFL));
            return handle.getTradeOfferId();
        }
        catch (IOException e) {
            logger.error("Error making trade offer", e);
            throw new SteamException("Error making trade offer", e);
        }
    }

    private void fillTradeNode(List<InventoryItem> me, ObjectNode meNode) {
        ArrayNode assetsNode = meNode.putArray("assets");
        for (InventoryItem item : me) {
            ObjectNode itemNode = assetsNode.addObject();
            itemNode.put("appid", item.getAppId());
            itemNode.put("contextid", item.getContextId());
            itemNode.put("amount", 1);
            itemNode.put("assetid", item.getAssetId());
        }
        meNode.putArray("currency");
        meNode.put("ready", false);
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
