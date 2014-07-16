package nl.pvanassen.steam.store.history;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class HistoryHandleTest {
	@Test
    public void testHandle() throws IOException {
        HistoryHandle handle = new HistoryHandle(new ObjectMapper());
        handle.handle(getClass().getResourceAsStream("/markethistory.json"));
        List<History> history = handle.getMarketHistory();
        assertEquals(1000, history.size());
    }
}
