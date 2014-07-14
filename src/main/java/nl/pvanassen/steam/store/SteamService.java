package nl.pvanassen.steam.store;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
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
import nl.pvanassen.steam.store.common.BuyOrder;
import nl.pvanassen.steam.store.common.Item;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

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
    
    SteamService(String cookies, String username) {
        http = Http.getInstance(cookies, username);
        this.username = username;
        appIds = getOutstandings().getAppIds();
        buyService = new SteamBuyService(http, username);
    }

    /**
     * @param http For mocking
     */
    SteamService(Http http, String username) {
        this.http = http;
        this.username = username;
        appIds = getOutstandings().getAppIds();
        buyService = new SteamBuyService(http, username);
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
        return getInventory(username);
    }

    @Override
    public List<InventoryItem> getInventory(String username) {
        List<InventoryItem> inventoryItems = new LinkedList<>();
        for (int appId : appIds) {
        	inventoryItems.addAll(getInventory(username, appId));
        }
        return ImmutableList.copyOf(inventoryItems);
    }
    
    @Override
    public List<InventoryItem> getInventory(int appId) {
        return getInventory(username, appId);
    }
    
    @Override
    public List<InventoryItem> getInventory(String username, int appId) {
        List<InventoryItem> inventoryItems = new LinkedList<>();
        int contextId = 2;
        if (appId == 753) {
            contextId = 6;
        }
        logger.info("Getting inventory for app id " + appId);
        InventoryHandle handle = new InventoryHandle(objectMapper, contextId, inventoryItems);
        try {
            http.get("http://steamcommunity.com/id/" + username + "/inventory/json/" + appId + "/" +
                     contextId + "/", handle);
        }
        catch (IOException e) {
            logger.error("Error fetching inventory data", e);

        }
        return ImmutableList.copyOf(inventoryItems);
    }

    @Override
    public void getItem(int appId, String urlName, GenericHandle<StatDataPoint> dataPointHandle,
            GenericHandle<Listing> listingHandle) {
    	String url = "http://steamcommunity.com/market/listings/" + appId + "/" + urlName;
        ListingPageScriptHandle handle = new ListingPageScriptHandle(objectMapper);
        try {
            http.get(url, handle);
        }
        catch (IOException e) {
            logger.error("Error fetching listing page data", e);
        	throw new SteamException("Error getting data for url: " + url, e);
        }
        if (handle.isError()) {
        	throw new SteamException("Error getting data for url: " + url);
        }
        JsonNode priceHistoryInfo = handle.getPriceHistoryInfo();
        for (StatDataPoint point : new ListingStatDataPointIterator(priceHistoryInfo)) {
            dataPointHandle.handle(point);
        }
        if (listingHandle == null) {
            return;
        }
        JsonNode listingInfo = handle.getListingInfo();
        for (Listing item : new ListingItemIterator(appId, urlName, listingInfo)) {
            listingHandle.handle(item);
        }
    }
    
    @Override
    public List<Listing> getNewlyListed(int currency, String country) {
        try {
        	ListingDeque listing = new ListingDeque(60000);
            ListingHandle handle = new ListingHandle(objectMapper, listing, country);
            http.get("http://steamcommunity.com/market/recent?currency=" + currency + "&country=" + country + "&" + System.currentTimeMillis(), handle);
            return listing.getDeque();
        }
        catch (IOException e) {
            logger.error("Error getting inventory", e);
        }
        return Collections.emptyList();
    }

    @Override
    public void getAsyncNewlyListed(int currency, String country, ListingDeque queue) {
        try {
            ListingHandle handle = new ListingHandle(objectMapper, queue, country);
            http.get("http://steamcommunity.com/market/recent?currency=" + currency + "&country=" + country + "&" + System.currentTimeMillis(), handle);
        }
        catch (IOException e) {
            logger.error("Error getting inventory", e);
        }

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
    public List<MarketHistory> getSoldItemsFromHistory() {
        MarketHistoryHandle handle = new MarketHistoryHandle();
        try {
            int stepSize = 100;
            http.get("http://steamcommunity.com/market/myhistory/render/?query=&search_descriptions=0&start=0&count=" +
                     stepSize, handle);
            if (handle.isError()) {
                return getSoldItemsFromHistory();
            }
            int totalCount = handle.getTotalCount();
            for (int start = stepSize; start < totalCount; start += stepSize) {
                do {
                    Thread.sleep(500);
                    http.get("http://steamcommunity.com/market/myhistory/render/?query=&search_descriptions=0&count=" +
                             stepSize + "&start=" + start, handle);
                }
                while (handle.isError());
            }
        }
        catch (IOException | RuntimeException | InterruptedException e) {
            logger.error("Error getting data", e);
        }
        return handle.getMarketHistory();
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getOutstandings()
     */
    @Override
    public MarketPage getOutstandings() {
        MarketPageHandle handle = new MarketPageHandle();
        try {
            http.get("http://steamcommunity.com/market/", handle);
        }
        catch (IOException e) {
            logger.error("Error getting outstanding listings", e);
        }
        return handle.getOutstandings();
    }

    @Override
    public void login(String user, String password) throws VerificationException, SteamGuardException {
        Map<String, String> params = new HashMap<>();
        params.put("username", user);
        GetRSAHandle rsaHandle = new GetRSAHandle(objectMapper);
        DoLoginHandle doLoginHandle = new DoLoginHandle(objectMapper);
        try {
            http.post("https://store.steampowered.com/login/getrsakey/", params, rsaHandle, "http://steamcommunity.com/id/" + username + "/inventory/");
            if (!rsaHandle.isSuccess()) {
                throw new VerificationException("Invalid username");
            }
            BigInteger pubKeyMod = new BigInteger(rsaHandle.getPublicKeyMod(), 16);
            BigInteger pubKeyExp = new BigInteger(rsaHandle.getPublicKeyExp(), 10);
            RSACrypto crypto = new RSACrypto(pubKeyMod, pubKeyExp, false);

            byte[] encrypted = crypto.encrypt(password.getBytes());
            String encryptedPasswordBase64 = Base64.encodeBase64String(encrypted);

            params.put("captcha_text", "");
            params.put("captchagid", "");
            params.put("emailauth", "");
            params.put("emailsteamid", "");
            params.put("loginfriendlyname", "");

            params.put("password", encryptedPasswordBase64);
            params.put("remember_login", "true");
            params.put("rsatimestamp", Long.toString(rsaHandle.getTimestamp()));
            http.post("https://steamcommunity.com/login/dologin/", params, doLoginHandle, "http://steamcommunity.com/id/" + username + "/inventory/");
            if (doLoginHandle.isSuccess()) {
                // logged in
                return;
            }
            if (doLoginHandle.getMessage().contains("SteamGuard")) {
                throw new SteamGuardException();
            }
        }
        catch (IOException e) {
            logger.error("Error logging in", e);
            throw new VerificationException("Error logging in", e);
        }
    }

    @Override
    public void verification(String code) {
        // TODO Auto-generated method stub
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
        // curl 'http://steamcommunity.com/market/cancelbuyorder/' -H 'Accept: text/javascript, text/html, application/xml, text/xml, */*' -H 'Accept-Encoding: gzip, deflate' -H 'Accept-Language: nl,en-US;q=0.7,en;q=0.3' -H 'Cache-Control: no-cache' -H 'Connection: keep-alive' -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -H 'Cookie: timezoneOffset=7200,0; Steam_Language=english; __utma=268881843.198519209.1391434770.1403166922.1403242194.84; __utmz=268881843.1402998924.74.7.utmcsr=store.steampowered.com|utmccn=(referral)|utmcmd=referral|utmcct=/; strInventoryLastContext=238460_2; __utmc=268881843; sessionid=MzkxNTEzOTk2; steamRememberLogin=76561198036110246%7C%7C22ce98f2d69b7e260f39303330324df7; webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22sales_this_year%22%3A16057%2C%22max_sales_per_year%22%3A-1%2C%22forms_requested%22%3A0%2C%22new_device_cooldown_days%22%3A7%7D; steamLogin=76561198036110246%7C%7CB9B45A5B3185AF74F3E3523317E591D9D4905146; steamCC_84_24_113_147=NL; __utmb=268881843.0.10.1403242194' -H 'Host: steamcommunity.com' -H 'Pragma: no-cache' -H 'Referer: http://steamcommunity.com/market/listings/753/303700-Summer%20Adventure%20Card%201' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:30.0) Gecko/20100101 Firefox/30.0' -H 'X-Prototype-Version: 1.7' -H 'X-Requested-With: XMLHttpRequest' 
        // --data 'sessionid=MzkxNTEzOTk2&buy_orderid=198984'
        // {"success":1}
        Map<String,String> params = new HashMap<>();
        params.put("buy_orderid", id);
        BuyOrderHandle handle = new BuyOrderHandle(objectMapper);
        try {
            http.post("http://steamcommunity.com/market/cancelbuyorder/", params, handle, "http://steamcommunity.com/id/" + username + "/inventory/");
        }
        catch (IOException e) {
            logger.error("Error canceling buy order", e);
            throw new SteamException("Error canceling buy order", e);
        }
        if (handle.isError()) {
            throw new SteamException(handle.getMessage());
        }
    }
    
    @Override
    public String createBuyOrder(Item item, int currencyId, int priceTotal, int quantity) { 
        // curl 'https://steamcommunity.com/market/createbuyorder/' -H 'Accept: */*' -H 'Accept-Encoding: gzip, deflate' -H 'Accept-Language: nl,en-US;q=0.7,en;q=0.3' -H 'Cache-Control: no-cache' -H 'Connection: keep-alive' -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -H 'Cookie: timezoneOffset=7200,0; steamMachineAuth76561197997047916=957FE57BD7190A29EC64566E4F59F53D266DD9AA; Steam_Language=english; __utma=268881843.198519209.1391434770.1403166922.1403242194.84; __utmz=268881843.1402998924.74.7.utmcsr=store.steampowered.com|utmccn=(referral)|utmcmd=referral|utmcct=/; strInventoryLastContext=238460_2; steamMachineAuth76561198036110246=0541BE57DFC72B046C381F32CF05BC61C44A4751; __utmc=268881843; sessionid=MzkxNTEzOTk2; steamRememberLogin=76561198036110246%7C%7C22ce98f2d69b7e260f39303330324df7; webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22sales_this_year%22%3A16057%2C%22max_sales_per_year%22%3A-1%2C%22forms_requested%22%3A0%2C%22new_device_cooldown_days%22%3A7%7D; steamLogin=76561198036110246%7C%7CB9B45A5B3185AF74F3E3523317E591D9D4905146; steamCC_84_24_113_147=NL; __utmb=268881843.0.10.1403242194' -H 'Host: steamcommunity.com' -H 'Origin: http://steamcommunity.com' -H 'Pragma: no-cache' -H 'Referer: http://steamcommunity.com/market/listings/753/303700-Summer%20Adventure%20Card%201' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:30.0) Gecko/20100101 Firefox/30.0'
        // --data 'sessionid=MzkxNTEzOTk2&currency=3&appid=753&market_hash_name=303700-Summer+Adventure+Card+1&price_total=45&quantity=3'
        // {"success":1,"buy_orderid":"198984"}
        Map<String,String> params = new HashMap<>();
        params.put("currency", Integer.toString(currencyId));
        params.put("appid", Integer.toString(item.getAppId()));
        params.put("market_hash_name", item.getUrlName());
        params.put("price_total", Integer.toString(priceTotal));
        params.put("quantity", Integer.toString(quantity));
        BuyOrderHandle handle = new BuyOrderHandle(objectMapper);
        try {
            http.post("https://steamcommunity.com/market/createbuyorder/", params, handle, item.getUrl());
        }
        catch (IOException e) {
            logger.error("Error creating a buy order", e);
            throw new SteamException("Error creating a buy order", e);
        }
        if (handle.isError()) {
            throw new SteamException(handle.getMessage());
        }
        return handle.getBuyOrderId();
    }
    
    
    @Override
    public BuyOrderStatus getBuyOrderStatus(String buyOrderId) {
        // curl 'http://steamcommunity.com/market/getbuyorderstatus/?sessionid=MzkxNTEzOTk2&buy_orderid=198984' -H 'Accept: */*' -H 'Accept-Encoding: gzip, deflate' -H 'Accept-Language: nl,en-US;q=0.7,en;q=0.3' -H 'Connection: keep-alive' -H 'Cookie: timezoneOffset=7200,0; Steam_Language=english; __utma=268881843.198519209.1391434770.1403166922.1403242194.84; __utmz=268881843.1402998924.74.7.utmcsr=store.steampowered.com|utmccn=(referral)|utmcmd=referral|utmcct=/; strInventoryLastContext=238460_2; __utmc=268881843; sessionid=MzkxNTEzOTk2; steamRememberLogin=76561198036110246%7C%7C22ce98f2d69b7e260f39303330324df7; webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22sales_this_year%22%3A16057%2C%22max_sales_per_year%22%3A-1%2C%22forms_requested%22%3A0%2C%22new_device_cooldown_days%22%3A7%7D; steamLogin=76561198036110246%7C%7CB9B45A5B3185AF74F3E3523317E591D9D4905146; steamCC_84_24_113_147=NL; __utmb=268881843.0.10.1403242194' -H 'Host: steamcommunity.com' -H 'Referer: http://steamcommunity.com/market/listings/753/303700-Summer%20Adventure%20Card%201' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:30.0) Gecko/20100101 Firefox/30.0' -H 'X-Requested-With: XMLHttpRequest'
        // {"success":1,"active":1,"purchased":0,"quantity":"3","quantity_remaining":"3","purchases":[]}
        String sessionId = http.getSessionId();
        BuyOrderStatusHandle handle = new BuyOrderStatusHandle(objectMapper);
        try {
            http.get("http://steamcommunity.com/market/getbuyorderstatus/?sessionid=" + sessionId + "&buy_orderid=" + buyOrderId, handle);
        }
        catch (IOException e) {
            logger.error("Error getting status of a buy order", e);
            throw new SteamException("Error getting status of a buy order", e);
        }
        return handle.getBuyOrderStatus();
    }
}
