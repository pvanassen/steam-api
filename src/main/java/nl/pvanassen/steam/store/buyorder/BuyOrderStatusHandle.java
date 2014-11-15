package nl.pvanassen.steam.store.buyorder;

import java.io.IOException;
import java.io.InputStream;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BuyOrderStatusHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
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
        try {
            if (node.get("success").asInt() != 1) {
                buyOrderStatus = new BuyOrderStatus(false, false, 0, 0, 0);
                return;
            }
            boolean success = true;
            boolean active = node.get("active").asBoolean();
            int purchased = node.get("purchased").asInt();
            int quantity = node.get("quantity").asInt();
            int quantityRemaining = node.get("quantity_remaining").asInt();
            buyOrderStatus = new BuyOrderStatus(success, active, purchased, quantity, quantityRemaining);
        }
        catch (RuntimeException e) {
            logger.error("Error parsing json: " + node, e);
        }
    }
}
