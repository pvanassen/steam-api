package nl.pvanassen.steam.store;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class MarketHistoryHandleTest {

    @Test
    public void testHandle() throws IOException {
        MarketHistoryHandle handle = new MarketHistoryHandle( );
        handle.handle(getClass().getResourceAsStream("/markethistory.json"));
    }

}
