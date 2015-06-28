package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class StreamHelper {
    public static InputStream getStream(String file) throws IOException {
        return new GZIPInputStream(StreamHelper.class.getResourceAsStream(file + ".gz"));
    }
}
