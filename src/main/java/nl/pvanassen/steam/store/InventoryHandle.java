package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import nl.pvanassen.steam.http.DefaultHandle;
import nl.pvanassen.steam.store.helper.UrlNameHelper;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

class InventoryHandle extends DefaultHandle {
    private final ObjectMapper objectMapper;
    private final List<InventoryItem> inventoryItemList;
    private final int contextId;

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
            // Fix for steam sending crap
            if (!item.has("market_hash_name")) {
                continue;
            }
            String urlName = UrlNameHelper.getUrlName(item.get("market_hash_name").asText());
            descriptionMap.put(item.get("classid").asText() + "-" + item.get("instanceid").asText(), new Description(item.get("appid").asInt(), urlName, item.get("marketable").asBoolean()));
        }

        JsonNode inventory = node.get("rgInventory");
        for (JsonNode item : inventory) {
            Description descroption = descriptionMap.get(item.get("classid").asText() + "-" + item.get("instanceid").asText());
            if (descroption == null) {
                continue;
            }
            inventoryItemList.add(new InventoryItem(item.get("id").asText(), contextId, item.get("instanceid").asText(), descroption.appId, descroption.urlName, descroption.marketable));
        }
    }

    private static final class Description {
        private final int appId;
        private final String urlName;
        private final boolean marketable;

        Description(int appId, String urlName, boolean marketable) {
            super();
            this.appId = appId;
            this.urlName = urlName;
            this.marketable = marketable;
        }

    }
}
