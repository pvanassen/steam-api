package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

class InventoryHandle extends DefaultHandle {
    private final ObjectMapper objectMapper;
    private final List<InventoryItem> inventoryItemList;
    private final int contextId;

    InventoryHandle( ObjectMapper objectMapper, int contextId, List<InventoryItem> inventoryItemList ) {
        this.objectMapper = objectMapper;
        this.contextId = contextId;
        this.inventoryItemList = inventoryItemList;
    }

    @Override
    public void handle( InputStream stream ) throws IOException {
        JsonNode node = objectMapper.readTree( stream );

        Map<String, String[]> descriptionMap = new HashMap<>();
        JsonNode descriptions = node.get( "rgDescriptions" );
        for ( JsonNode item : descriptions ) {
            String urlName = URLEncoder.encode(item.get( "market_hash_name" ).asText(), "UTF-8").replace("+", "%20");
            descriptionMap.put(item.get( "classid" ).asText() + "-" + item.get( "instanceid" ).asText(), new String[]{item.get( "appid" ).asText(), urlName});
        }

        JsonNode inventory = node.get( "rgInventory" );
        for ( JsonNode item : inventory ) {
            String[] obj = descriptionMap.get( item.get( "classid" ).asText() + "-" + item.get( "instanceid" ).asText());
            inventoryItemList.add(new InventoryItem(item.get( "id" ).asText(), contextId, item.get( "instanceid" ).asText(), Integer.parseInt(obj[0]), obj[1]));
        }
    }
}
