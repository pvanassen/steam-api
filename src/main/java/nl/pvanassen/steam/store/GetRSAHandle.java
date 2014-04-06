package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import nl.pvanassen.steam.http.DefaultHandle;


class GetRSAHandle extends DefaultHandle {
    private final ObjectMapper objectMapper;
    
    private boolean success;
    private String publicKeyMod;
    private String publicKeyExp;
    private long timestamp;
    GetRSAHandle(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    
    @Override
    public void handle(InputStream stream) throws IOException {
        JsonNode node = objectMapper.readTree(stream);
        success = node.get("success").asBoolean();
        publicKeyMod = node.get("publickey_mod").asText();
        publicKeyExp = node.get("publickey_exp").asText();
        timestamp = node.get("timestamp").asLong();
    }
    
    String getPublicKeyExp() {
        return publicKeyExp;
    }
    
    String getPublicKeyMod() {
        return publicKeyMod;
    }
    
    long getTimestamp() {
        return timestamp;
    }
    
    boolean isSuccess() {
        return success;
    }
}
