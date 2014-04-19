package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

class BuyHandle extends DefaultHandle {

    private int wallet;
    private boolean error = false;
    private String message;

    int getWallet() {
        return wallet;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree(stream);
        JsonNode walletInfo = node.get("wallet_info");
        wallet = walletInfo.get("wallet_balance").asInt();
    }

    @Override
    public void handleError(InputStream stream) throws IOException {
        error = true;
        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree(stream);
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
}
