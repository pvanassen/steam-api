package nl.pvanassen.steam.store.tradeoffer;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import nl.pvanassen.steam.http.DefaultHandle;

class TradeOfferHandle extends DefaultHandle {
    private final ObjectMapper om;
    private int tradeOfferId;

    TradeOfferHandle(ObjectMapper om) {
        this.om = om;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        JsonNode jsonNode = om.readTree(stream);
        tradeOfferId = jsonNode.get("tradeofferid").asInt();
    }

    int getTradeOfferId() {
        return tradeOfferId;
    }
}
