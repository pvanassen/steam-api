package nl.pvanassen.steam.store.inventory;

import nl.pvanassen.steam.http.DefaultHandle;
import nl.pvanassen.steam.store.common.InventoryItem;
import nl.pvanassen.steam.store.helper.UrlNameHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class InventoryHandle extends DefaultHandle {
    private static final String TRADING_BLOCKED = "Tradable After: ";
    private static final String TRADING_BLOCKED_570 = "Tradable & Marketable After: ";
    private static final String TRADING_BLOCKED_730 = "Tradable After ";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final class Description {
        private final int appId;
        private final String urlName;
        private final boolean marketable;
        private final boolean tradable;
        private final Date blockedUntil;
        private final Map<String,String> properties;

        Description(int appId, String urlName, boolean marketable, boolean tradable, Date blockedUntil, Map<String,String> properties) {
            super();
            this.appId = appId;
            this.urlName = urlName;
            this.marketable = marketable;
            this.tradable = tradable;
            this.blockedUntil = blockedUntil;
            this.properties = properties;
        }

    }

    private final ObjectMapper objectMapper;
    private final List<InventoryItem> inventoryItemList;
    private final int contextId;
    private boolean error;

    InventoryHandle(ObjectMapper objectMapper, int contextId, List<InventoryItem> inventoryItemList) {
        this.objectMapper = objectMapper;
        this.contextId = contextId;
        this.inventoryItemList = inventoryItemList;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        JsonNode node = objectMapper.readTree(stream);

        Map<String, Description> descriptionMap = new HashMap<>();
        JsonNode descriptions = node.get("rgDescriptions");
        // Avoid NPE's on empty inventories
        if (descriptions == null) {
            return;
        }
        for (JsonNode item : descriptions) {
            String urlName;
            // Fix for steam sending crap
            if (!item.has("market_hash_name")) {
                // Unprefered fallback
                urlName = UrlNameHelper.getUrlName(item.get("name").asText());
            }
            else {
                urlName = UrlNameHelper.getUrlName(item.get("market_hash_name").asText());
            }
            int appId = item.get("appid").asInt();
            Date blockedUntil = new Date(0);
            JsonNode ownerDescriptions;
            if (appId == 730) {
                ownerDescriptions = item.get("owner_descriptions");
            }
            else {
                ownerDescriptions = item.get("descriptions");
            }
            if (ownerDescriptions != null && ownerDescriptions.isArray()) {
                for (JsonNode ownerDescription : ownerDescriptions) {
                    String text = ownerDescription.get("value").asText().trim();
                    if (appId == 570 && text.startsWith(TRADING_BLOCKED_570)) {
                        String date = text.substring(TRADING_BLOCKED_570.length());
                        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy (HH:mm:ss)", Locale.ENGLISH);
                        try {
                            blockedUntil = format.parse(date);
                        } catch (ParseException e) {
                            logger.error("Error parsing blocked text: " + date, e);
                        }
                    }
                    else if (appId == 730 && text.startsWith(TRADING_BLOCKED_730)) {
                        String date = text.substring(TRADING_BLOCKED_730.length());
                        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy (HH:mm:ss) z", Locale.ENGLISH);
                        try {
                            blockedUntil = format.parse(date);
                        } catch (ParseException e) {
                            logger.error("Error parsing blocked text: " + date, e);
                        }
                    }
                    else if (text.startsWith(TRADING_BLOCKED)) {
                        String date = text.substring(TRADING_BLOCKED.length());
                        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy (HH:mm:ss) z", Locale.ENGLISH);
                        try {
                            blockedUntil = format.parse(date);
                        } catch (ParseException e) {
                            logger.error("Error parsing blocked text: " + date, e);
                        }
                    }
                }
            }
            boolean marketable = false;
            if (item.get("marketable") != null) {
                marketable = item.get("marketable").asBoolean();
            }
            boolean tradable = false;
            if (item.get("tradable") != null) {
                tradable = item.get("tradable").asBoolean();
            }
            Map<String,String> properties = new HashMap<>();
            Iterator<Map.Entry<String,JsonNode>> itr = item.getFields();
            while (itr.hasNext()) {
                Map.Entry<String,JsonNode> entry = itr.next();
                properties.put(entry.getKey(), entry.getValue().asText());
            }
            Description description = new Description(appId, urlName, marketable, tradable, blockedUntil, properties);
            descriptionMap.put(item.get("classid").asText() + "-" + item.get("instanceid").asText(), description);
        }

        JsonNode inventory = node.get("rgInventory");
        for (JsonNode item : inventory) {
            Description description = descriptionMap.get(item.get("classid").asText() + "-" + item.get("instanceid").asText());
            if (description == null) {
                continue;
            }

            inventoryItemList.add(new InventoryItem(item.get("id").asText(), contextId, item.get("instanceid").asText(), description.appId, description.urlName,
                    description.marketable, description.tradable, description.blockedUntil, description.properties));
        }
        error = false;
    }
    
    @Override
    public void handleError(InputStream stream) throws IOException {
        error = true;
    }
    
    boolean isError() {
        return error;
    }
}
