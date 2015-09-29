package nl.pvanassen.steam.store.listing;

import java.io.*;
import java.nio.charset.Charset;

import nl.pvanassen.steam.http.DefaultHandle;
import nl.pvanassen.steam.store.CommandResult;
import nl.pvanassen.steam.store.common.GenericHandle;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

class ListingMutationHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final GenericHandle<CommandResult> handle;
    private final String mutationType;
    
    ListingMutationHandle(String mutationType, GenericHandle<CommandResult> handle) {
        this.mutationType = mutationType;
        this.handle = handle;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        super.handle(stream);
        handle.handle(CommandResult.success());
    }

    @Override
    public void handleError(InputStream stream) throws IOException {
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
            handle.handle(CommandResult.error(e, "unknown erorr"));
            return;
        }
        if ((node == null) || (node.get("message") == null)) {
            logger.error("Error could not " + mutationType + " item: unknown error");
            handle.handle(CommandResult.error("unknown erorr"));
            return;
        }
        String message = node.get("message").asText();
        handle.handle(CommandResult.error(message));
        logger.error("Error could not " + mutationType + " item: " + message);
    }
    
    @Override
    public void handleException(Exception exception) {
        handle.handle(CommandResult.error(exception));
    }
}
