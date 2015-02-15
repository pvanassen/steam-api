package nl.pvanassen.steam.store.inventory;

import java.io.*;
import java.text.*;
import java.util.*;

import nl.pvanassen.steam.http.DefaultHandle;
import nl.pvanassen.steam.store.common.InventoryItem;
import nl.pvanassen.steam.store.helper.UrlNameHelper;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.*;

class InventoryHandle extends DefaultHandle {
    private static final String TRADING_BLOCKED = "This item can be traded after ";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final class Description {
        private final int appId;
        private final String urlName;
        private final boolean marketable;
        private final boolean tradable;
        private final Date blockedUntil;

        Description(int appId, String urlName, boolean marketable, boolean tradable, Date blockedUntil) {
            super();
            this.appId = appId;
            this.urlName = urlName;
            this.marketable = marketable;
            this.tradable = tradable;
            this.blockedUntil = blockedUntil;
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
            Date blockedUntil = new Date();
            JsonNode ownerDescriptions = item.get("owner_descriptions");
            if (ownerDescriptions != null && ownerDescriptions.isArray()) {
                for (JsonNode ownerDescription : ownerDescriptions) {
                    String text = ownerDescription.get("value").asText();
                    if (text.startsWith(TRADING_BLOCKED)) {
                        String date = text.substring(TRADING_BLOCKED.length());
                        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                        try {
                            blockedUntil = format.parse(date);
                        } catch (ParseException e) {
                            logger.error("Error parsing blocked text: " + date, e);
                        }
                    }
                }
            }
            Description description = new Description(item.get("appid").asInt(), urlName, item.get("marketable").asBoolean(), item.get("tradable").asBoolean(), blockedUntil);
            descriptionMap.put(item.get("classid").asText() + "-" + item.get("instanceid").asText(), description);
        }

        JsonNode inventory = node.get("rgInventory");
        for (JsonNode item : inventory) {
            Description description = descriptionMap.get(item.get("classid").asText() + "-" + item.get("instanceid").asText());
            if (description == null) {
                continue;
            }
            inventoryItemList.add(new InventoryItem(item.get("id").asText(), contextId, item.get("instanceid").asText(), description.appId, description.urlName,
                    description.marketable, description.tradable, description.blockedUntil));
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
