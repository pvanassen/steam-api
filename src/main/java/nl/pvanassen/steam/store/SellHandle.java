package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SellHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean error = false;
    private String message;
    
    @Override
    public void handle( InputStream stream ) throws IOException {
        super.handle(stream);
        return;
    }

    @Override
    public void handleError( InputStream stream ) throws IOException {
        error = true;
        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree( stream );
        message = node.get( "message" ).asText();
        logger.error("Error could not sell item: " + message);
    }

    boolean isError() {
        return error;
    }
    
    String getMessage() {
        return message;
    }
}
