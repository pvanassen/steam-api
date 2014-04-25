package nl.pvanassen.steam.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default stream handle that outputs the stream to the log
 * 
 * @author Paul van Assen
 */
public class DefaultHandle implements Handle {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(InputStream stream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer);
        logger.info(writer.toString());
    }

    @Override
    public void handleError(InputStream stream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer);
        logger.error(writer.toString());
    }

}
