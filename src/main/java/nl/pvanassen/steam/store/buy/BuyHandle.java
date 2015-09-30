package nl.pvanassen.steam.store.buy;

import nl.pvanassen.steam.http.DefaultHandle;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

class BuyHandle extends DefaultHandle {
    private final ObjectMapper om;
    private int wallet;
    private boolean error = false;
    private String message;

    BuyHandle(ObjectMapper objectMapper) {
        om = objectMapper;
    }

    String getMessage() {
        return message;
    }

    int getWallet() {
        return wallet;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        error = false;
        JsonNode node = om.readTree(stream);
        JsonNode walletInfo = node.get("wallet_info");
        if ((walletInfo != null) && (walletInfo.get("wallet_balance") != null)) {
            wallet = walletInfo.get("wallet_balance").asInt();
        }
    }

    @Override
    public void handleError(InputStream stream) throws IOException {
        error = true;
        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree(stream);
        if (node == null) {
            message = "No result";
        }
        else if (node.get("message") != null) {
            message = node.get("message").asText();
        }
        else {
            message = node.toString();
        }
    }

    boolean isError() {
        return error;
    }
}
