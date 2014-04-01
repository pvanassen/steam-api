package nl.pvanassen.steam.store;

import java.io.IOException;
import java.util.List;

import static  org.junit.Assert.*;
import org.junit.Test;

public class MarketHistoryHandleTest {

    @Test
    public void testHandle() throws IOException {
        MarketHistoryHandle handle = new MarketHistoryHandle( );
        handle.handle(getClass().getResourceAsStream("/markethistory.json"));
        List<MarketHistory> history = handle.getMarketHistory();
        assertEquals(1000, history.size());
    }

}
