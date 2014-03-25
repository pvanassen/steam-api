package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

class SellHandle extends DefaultHandle {
    private boolean error = false;
    private String message;
    
    @Override
    public void handle( InputStream stream ) throws IOException {
        return;
    }

    @Override
    public void handleError( InputStream stream ) throws IOException {
        error = true;
        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree( stream );
        message = node.get( "message" ).asText();
    }

    boolean isError() {
        return error;
    }
    
    String getMessage() {
        return message;
    }
}
