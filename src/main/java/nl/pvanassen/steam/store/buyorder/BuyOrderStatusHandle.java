package nl.pvanassen.steam.store.buyorder;

import java.io.IOException;
import java.io.InputStream;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

class BuyOrderStatusHandle extends DefaultHandle {
    private final ObjectMapper objectMapper;
    private BuyOrderStatus buyOrderStatus;

    BuyOrderStatusHandle(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    BuyOrderStatus getBuyOrderStatus() {
        return buyOrderStatus;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        // {"success":1,"active":1,"purchased":0,"quantity":"3","quantity_remaining":"3","purchases":[]}
        JsonNode node = objectMapper.readTree(stream);
        boolean success = node.get("success").asBoolean();
        boolean active = node.get("active").asBoolean();
        int purchased = node.get("purchased").asInt();
        int quantity = node.get("quantity").asInt();
        int quantityRemaining = node.get("quantity_remaining").asInt();
        buyOrderStatus = new BuyOrderStatus(success, active, purchased, quantity, quantityRemaining);
    }
}
