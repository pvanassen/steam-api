package nl.pvanassen.steam.store;

import java.io.IOException;
import java.util.List;

import static  org.junit.Assert.*;
import org.junit.Test;

public class MarketHistoryHandleTest {

	@Test
    public void testHandle() throws IOException {
        MarketHistoryHandle handle = new MarketHistoryHandle( );
        handle.handle(getClass().getResourceAsStream("/myhistory.json"));
        List<MarketHistory> historyList = handle.getMarketHistory();
        assertEquals(500, historyList.size());
        for (MarketHistory history : historyList) {
        	System.out.printf("%s,%s\n", history.getSteamId(), history.getStatus());
        }
    }

}
