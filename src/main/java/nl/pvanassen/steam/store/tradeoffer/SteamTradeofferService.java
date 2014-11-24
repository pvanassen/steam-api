/**
 *
 */
package nl.pvanassen.steam.store.tradeoffer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.pvanassen.steam.error.SteamException;
import nl.pvanassen.steam.http.DefaultHandle;
import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.store.inventory.InventoryItem;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * @author Paul van Assen
 */
public class SteamTradeofferService implements TradeofferService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Http http;

    /**
     * @param http For mocking
     */
    public SteamTradeofferService(Http http) {
        this.http = http;
    }

    @Override
    public void acceptTradeOffer(Tradeoffer tradeoffer) {
        String id = tradeoffer.getOfferId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("partner", tradeoffer.getPartnerId());
            params.put("tradeofferid", id);
            http.post("https://steamcommunity.com/tradeoffer/" + id + "/accept", params, new DefaultHandle(), "http://steamcommunity.com/tradeoffer/" + id);
        }
        catch (IOException e) {
            logger.error("Error getting trade offers", e);
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
    public List<Tradeoffer> getTradeOffers() {
        ListTradeoffersHandle handle = new ListTradeoffersHandle();
        try {
            http.get("https://steamcommunity.com/id/mantorch/tradeoffers/", handle);
        }
        catch (IOException e) {
            logger.error("Error getting trade offers", e);
        }
        return handle.getTradeoffers();
    }

    @Override
    public int makeTradeOffer(long steamId, List<InventoryItem> me, List<InventoryItem> them, Optional<String> message) {
        ObjectNode tradeOffer = objectMapper.createObjectNode();
        tradeOffer.put("newversion", true);
        tradeOffer.put("version", 2);
        ObjectNode meNode = tradeOffer.putObject("me");
        fillTradeNode(me, meNode);
        ObjectNode themNode = tradeOffer.putObject("them");
        fillTradeNode(them, themNode);
        Map<String, String> params = new HashMap<>();
        params.put("json_tradeoffer", tradeOffer.toString());
        params.put("partner", Long.toString(steamId));
        params.put("trade_offer_create_params", "{}");
        params.put("tradeoffermessage", message.or(""));
        params.put("serverid", "1");
        logger.info("Sending: " + params.toString());
        try {
            TradeofferHandle handle = new TradeofferHandle(objectMapper);
            http.post("https://steamcommunity.com/tradeoffer/new/send", params, handle, "http://steamcommunity.com/tradeoffer/new/?partner=" + Long.toString(steamId & 0xFFFFFFFFL));
            return handle.getTradeOfferId();
        }
        catch (IOException e) {
            logger.error("Error making trade offer", e);
            throw new SteamException("Error making trade offer", e);
        }
    }

}
