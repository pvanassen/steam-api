package nl.pvanassen.steam.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Handle for (http) streams
 * 
 * @author Paul van Assen
 */
public interface Handle {
    /**
     * Success handle
     * 
     * @param stream Stream to handle
     * @throws IOException IOException is allowed while handling stream
     */
    void handle(InputStream stream) throws IOException;

    /**
     * Error handle
     * 
     * @param stream Stream to handle
     * @throws IOException IOException is allowed while handling stream
     */
    void handleError(InputStream stream) throws IOException;
}
