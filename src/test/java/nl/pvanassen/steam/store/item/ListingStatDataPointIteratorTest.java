package nl.pvanassen.steam.store.item;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class ListingStatDataPointIteratorTest {

	@Test
	public void testBrokenListing() throws IOException {
		ListingPageScriptHandle handle = new ListingPageScriptHandle(new ObjectMapper());
		try (InputStream stream = getClass().getResourceAsStream("/broken-listing.html")) {
			handle.handle(stream);
		}
        JsonNode priceHistoryInfo = handle.getPriceHistoryInfo();
        for (StatDataPoint point : new ListingStatDataPointIterator(priceHistoryInfo)) {
            assertNotNull(point);
        }

	}

}
