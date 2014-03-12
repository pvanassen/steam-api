package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import nl.pvanassen.steam.http.DefaultHandle;


class MarketHistoryHandle extends DefaultHandle {
    private final List<MarketHistory> marketHistory = new LinkedList<>();
    private final boolean sold;
    
    
    MarketHistoryHandle(boolean sold) {
        this.sold = sold;
    }
    
    @Override
    public void handle(InputStream stream) throws IOException {
        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree( stream );
        JsonNode assets = node.get("assets");
        for (JsonNode appId : assets) {
            for (JsonNode contextId : appId) {
                for (JsonNode item : contextId) {
                    String urlName = URLEncoder.encode( item.get( "market_hash_name" ).asText(), "UTF-8" ).replace( "+", "%20" );
                    if ("0".equals(item.get("instanceid").asText())) {
                        // Sold
                        if (sold) {
                            marketHistory.add(new MarketHistory(item.get("appid").asInt(), item.get("contextid").asInt(), item.get("id").asText(), item.get("classid").asText(), item.get("instanceid").asText(), urlName, item.get("amount").asInt(), item.get("status").asInt())); 
                        }
                    }
                    else {
                        // bought
                        if (!sold) {
                            marketHistory.add(new MarketHistory(item.get("appid").asInt(), item.get("contextid").asInt(), item.get("id").asText(), item.get("classid").asText(), item.get("instanceid").asText(), urlName, item.get("amount").asInt(), item.get("status").asInt())); 
                        }
                    }
                }
            }
        }
    }
    
    List<MarketHistory> getMarketHistory() {
        return marketHistory;
    }
}
