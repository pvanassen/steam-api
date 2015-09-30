package nl.pvanassen.steam.store.tradeoffer;

import nl.pvanassen.steam.store.StreamHelper;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ListTradeoffersHandleTest {

    @Test
    public void testHandle() throws IOException {
        ListTradeoffersHandle handle = new ListTradeoffersHandle();
        InputStream stream = StreamHelper.getStream("/tradeoffers.html");
        handle.handle(stream);
        List<TradeOffer> tradeoffers = handle.getTradeoffers();
        assertNotNull(tradeoffers);
        assertEquals(1, tradeoffers.size());
        // &nbsp; or character code 160 (translates to -62 signed)
        assertEquals(4, tradeoffers.get(0).getComment().length());
    }

}
