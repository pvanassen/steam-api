/**
 *
 */
package nl.pvanassen.steam.store.tradeoffer;

import java.io.IOException;
import java.util.*;

import nl.pvanassen.steam.error.SteamException;
import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.http.NullHandle;
import nl.pvanassen.steam.store.common.InventoryItem;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.*;
import org.slf4j.*;

import com.google.common.base.Optional;

/**
 * @author Paul van Assen
 */
public class SteamTradeOfferService implements TradeOfferService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Http http;

    /**
     * @param http For mocking
     */
    public SteamTradeOfferService(Http http) {
        this.http = http;
    }

    @Override
    public void acceptTradeOffer(TradeOffer tradeoffer) {
        String id = tradeoffer.getOfferId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("partner", tradeoffer.getPartnerId());
            params.put("tradeofferid", id);
            params.put("serverid", "1");
            TradeOfferHandle handle = new TradeOfferHandle(objectMapper);
            http.post("https://steamcommunity.com/tradeoffer/" + id + "/accept", params, handle, "http://steamcommunity.com/tradeoffer/" + id, true, false, false);
            if (handle.isError()) {
                throw new SteamException(handle.getMessage());
            }
        } catch (IOException e) {
            logger.error("Error getting trade offers", e);
        }
    }

    private void fillTradeNode(List<InventoryItem> me, ObjectNode meNode) {
        ArrayNode assetsNode = meNode.putArray("assets");
        for (InventoryItem item : me) {
            ObjectNode itemNode = assetsNode.addObject();
            itemNode.put("appid", item.getAppId());
            itemNode.put("contextid", Integer.toString(item.getContextId()));
            itemNode.put("amount", 1);
            itemNode.put("assetid", item.getAssetId());
        }
        meNode.putArray("currency");
        meNode.put("ready", false);
    }

    @Override
    public List<TradeOffer> getTradeOffers() {
        ListTradeoffersHandle handle = new ListTradeoffersHandle();
        http.get("https://steamcommunity.com/id/mantorch/tradeoffers/", handle, false, false);
        return handle.getTradeoffers();
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.tradeoffer.TradeOfferService#makeTradeOffer(long, java.util.List, java.util.List, com.google.common.base.Optional)
     */
    @Override
    public int makeTradeOffer(long steamId, List<InventoryItem> me, List<InventoryItem> them, Optional<String> message) {
        http.get("https://steamcommunity.com/tradeoffer/new/?partner=" + Long.toString(steamId & 0xFFFFFFFFL), new NullHandle(), false, false);
        ObjectNode tradeOffer = objectMapper.createObjectNode();
        tradeOffer.put("newversion", true);
        tradeOffer.put("version", me.size() + them.size() + 1);
        ObjectNode meNode = tradeOffer.putObject("me");
        fillTradeNode(me, meNode);
        ObjectNode themNode = tradeOffer.putObject("them");
        fillTradeNode(them, themNode);
        Map<String, String> params = new HashMap<>();
        params.put("json_tradeoffer", tradeOffer.toString());
        params.put("partner", Long.toString(steamId));
        params.put("trade_offer_create_params", "{}");
        params.put("tradeoffermessage", message.or(""));
        params.put("captcha", "");
        params.put("serverid", "1");
        logger.info("Sending: " + params.toString());
        try {
            TradeOfferHandle handle = new TradeOfferHandle(objectMapper);
            http.post("https://steamcommunity.com/tradeoffer/new/send", params, handle,
                    "http://steamcommunity.com/tradeoffer/new/?partner=" + Long.toString(steamId & 0xFFFFFFFFL), true, false, false);
            if (handle.isError()) {
                throw new SteamException(handle.getMessage());
            }
            return handle.getTradeOfferId();
        } catch (IOException e) {
            logger.error("Error making trade offer", e);
            throw new SteamException("Error making trade offer", e);
        }
    }

}
