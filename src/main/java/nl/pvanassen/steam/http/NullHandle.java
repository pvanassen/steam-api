package nl.pvanassen.steam.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Do nothing at all
 *
 * @author Paul van Assen
 */
public class NullHandle implements Handle {
    @Override
    public void handle(InputStream stream) throws IOException {
    }

    @Override
    public void handleError(InputStream stream) throws IOException {
    }
    
    @Override
    public void handleException(Exception exception) {}
}
