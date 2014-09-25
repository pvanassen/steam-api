package nl.pvanassen.steam.store.item;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class ListingPageScriptHandleTest {

	@Test
	public void testManco() throws IOException {
		ListingPageScriptHandle handle = new ListingPageScriptHandle(new ObjectMapper());
		try (InputStream stream = getClass().getResourceAsStream("/manco.html")) {
			handle.handle(stream);
		}
	}
}
