package nl.pvanassen.steam.store.tradeoffer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

public class ListTradeoffersHandleTest {

    @Test
    public void testHandle() throws IOException {
        ListTradeoffersHandle handle = new ListTradeoffersHandle();
        InputStream stream = getClass().getResourceAsStream("/tradeoffers.html");
        handle.handle(stream);
        List<TradeOffer> tradeoffers = handle.getTradeoffers();
        assertNotNull(tradeoffers);
        assertEquals(1, tradeoffers.size());
        // &nbsp; or character code 160 (translates to -62 signed)
        assertEquals(4, tradeoffers.get(0).getComment().length());
    }

}
