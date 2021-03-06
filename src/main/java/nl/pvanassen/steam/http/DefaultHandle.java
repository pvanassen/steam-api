package nl.pvanassen.steam.http;

import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Default stream handle that outputs the stream to the log
 *
 * @author Paul van Assen
 */
public class DefaultHandle implements Handle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Charset charset = Charset.forName("UTF-8");

    @Override
    public void handle(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteStreams.copy(stream, baos);
        logger.info(new String(baos.toByteArray(), charset));
    }

    @Override
    public void handleError(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteStreams.copy(stream, baos);
        logger.error(new String(baos.toByteArray(), charset), new Exception());
    }

    @Override
    public void handleException(Exception exception) {
        logger.error("Exception caught", exception);
    }
}
