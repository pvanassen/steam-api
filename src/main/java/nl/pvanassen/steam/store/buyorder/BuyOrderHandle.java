package nl.pvanassen.steam.store.buyorder;

import java.io.IOException;
import java.io.InputStream;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BuyOrderHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper;
    private boolean error;
    private String message;
    private String buyOrderId;

    BuyOrderHandle(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    String getBuyOrderId() {
        return buyOrderId;
    }

    String getMessage() {
        return message;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.http.DefaultHandle#handle(java.io.InputStream)
     */
    @Override
    public void handle(InputStream stream) throws IOException {
        JsonNode node = objectMapper.readTree(stream);
        logger.info(node.toString());
        if (node.get("error") != null && !node.get("error").asText().equals("null")) {
            error = true;
            message = "Unknown: " + node.toString();
        }
        else if (node.get("buy_orderid") != null) {
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
        else if (node.get("message") != null) {
            message = node.get("message").asText();
        }
        else {
            message = "Unknown";
        }
    }

    boolean isError() {
        return error;
    }
}
