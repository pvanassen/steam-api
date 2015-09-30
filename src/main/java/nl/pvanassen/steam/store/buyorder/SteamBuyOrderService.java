package nl.pvanassen.steam.store.buyorder;

import nl.pvanassen.steam.error.SteamException;
import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.store.common.Item;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for setting up and managing build orders
 * 
 * @author Paul van Assen
 */
public class SteamBuyOrderService implements BuyOrderService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Http http;
    private final String username;

    /**
     * @param http For mocking
     * @param username The username to use for referals
     */
    public SteamBuyOrderService(Http http, String username) {
        this.http = http;
        this.username = username;
    }

    @Override
    public void cancelBuyOrder(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("buy_orderid", id);
        BuyOrderHandle handle = new BuyOrderHandle(objectMapper);
        try {
            http.post("http://steamcommunity.com/market/cancelbuyorder/", params, handle, "http://steamcommunity.com/id/" + username + "/inventory/", true, false, false);
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
        Map<String, String> params = new HashMap<>();
        params.put("currency", Integer.toString(currencyId));
        params.put("appid", Integer.toString(item.getAppId()));
        params.put("market_hash_name", item.getUrlName());
        params.put("price_total", Integer.toString(priceTotal));
        params.put("quantity", Integer.toString(quantity));
        BuyOrderHandle handle = new BuyOrderHandle(objectMapper);
        try {
            http.post("https://steamcommunity.com/market/createbuyorder/", params, handle, item.getUrl(), true, false, false);
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
        String sessionId = http.getSessionId();
        BuyOrderStatusHandle handle = new BuyOrderStatusHandle(objectMapper);
        http.get("http://steamcommunity.com/market/getbuyorderstatus/?sessionid=" + sessionId + "&buy_orderid=" + buyOrderId, handle, false, false);
        BuyOrderStatus status = handle.getBuyOrderStatus();
        if (!status.isSuccess()) {
            logger.error("Error getting status of a buy order " + buyOrderId);
        }
        return status;
    }
}
