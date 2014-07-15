package nl.pvanassen.steam.store.buyorder;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import nl.pvanassen.steam.http.DefaultHandle;

class BuyOrderHandle extends DefaultHandle {
    private final ObjectMapper objectMapper;
    private boolean error;
    private String message;
    private String buyOrderId;
    
    BuyOrderHandle(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public void handle(InputStream stream) throws IOException {
        JsonNode node = objectMapper.readTree(stream);
        if (!node.get("success").asBoolean()) {
            error = true;
            message = "Unknown";
        }
        else {
            buyOrderId = node.get("buy_orderid").asText();
        }
    }
    
    @Override
    public void handleError(InputStream stream) throws IOException {
        error = true;
        JsonNode node = objectMapper.readTree(stream);
        if (node == null) {
            message = "No result";
        }
        if (node.get("message") != null) {
            message = node.get("message").asText();
        }
        else {
            message = node.toString();
        }
    }
    
    boolean isError() {
        return error;
    }
    String getMessage() {
        return message;
    }
    String getBuyOrderId() {
        return buyOrderId;
    }
}
