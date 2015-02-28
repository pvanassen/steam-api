package nl.pvanassen.steam.store.listing;

import java.io.*;
import java.nio.charset.Charset;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

class ListingMutationHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean error = false;
    private String message;
    private final String mutationType;
    
    ListingMutationHandle(String mutationType) {
        this.mutationType = mutationType;
    }

    String getMessage() {
        return message;
    }

    @Override
    public void handleError(InputStream stream) throws IOException {
        error = true;
        ObjectMapper om = new ObjectMapper();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteStreams.copy(stream, baos);
        String streamString = new String(baos.toByteArray(), Charset.forName("UTF-8")); 
        JsonNode node;
        try {
            node = om.readTree(streamString);
        }
        catch (JsonProcessingException e) {
            logger.error("Error parsing answer from Steam: '" + streamString + "'", e);
            message = "unknown error";
            return;
        }
        if ((node == null) || (node.get("message") == null)) {
            logger.error("Error could not " + mutationType + " item: unknown error");
            message = "unknown error";
            return;
        }
        message = node.get("message").asText();
        logger.error("Error could not " + mutationType + " item: " + message);
    }

    boolean isError() {
        return error;
    }
}
