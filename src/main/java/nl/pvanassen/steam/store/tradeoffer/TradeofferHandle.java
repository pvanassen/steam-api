package nl.pvanassen.steam.store.tradeoffer;

import java.io.IOException;
import java.io.InputStream;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

class TradeofferHandle extends DefaultHandle {
    private final ObjectMapper om;
    private int tradeOfferId;

    TradeofferHandle(ObjectMapper om) {
        this.om = om;
    }

    int getTradeOfferId() {
        return tradeOfferId;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        JsonNode jsonNode = om.readTree(stream);
        tradeOfferId = jsonNode.get("tradeofferid").asInt();
    }
}
