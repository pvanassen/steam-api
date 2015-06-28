package nl.pvanassen.steam.store.item;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import nl.pvanassen.steam.store.StreamHelper;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

/**
 * Test retrieval of listing points
 * @author Paul van Assen
 *
 */
public class ListingStatDataPointIteratorTest {

    /**
     * Test broken list
     * @throws IOException
     */
    @Test
    public void testBrokenListing() throws IOException {
        ListingPageScriptHandle handle = new ListingPageScriptHandle(new ObjectMapper());
        try (InputStream stream = StreamHelper.getStream("/broken-listing.html")) {
            handle.handle(stream);
        }
        JsonNode priceHistoryInfo = handle.getPriceHistoryInfo();
        for (StatDataPoint point : new ListingStatDataPointIterator(priceHistoryInfo)) {
            assertNotNull(point);
        }

    }

}
