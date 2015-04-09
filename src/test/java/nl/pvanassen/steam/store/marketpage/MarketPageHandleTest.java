package nl.pvanassen.steam.store.marketpage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

public class MarketPageHandleTest {

    @Test
    public void testEmptyHandle() throws IOException {
        MarketPageHandle handle = new MarketPageHandle();
        handle.handle(getClass().getResourceAsStream("/empty-marketpage.html"));
        assertNotNull("Expected object", handle.getOutstandings());
        assertEquals("Expected 0", 0, handle.getOutstandings().getAmount());
        assertEquals("Expected 0", 0, handle.getOutstandings().getItems());
        assertNotNull(handle.getOutstandings().getItemList());
    }

    @Test
    public void testLoadedHandle() throws IOException {
        MarketPageHandle handle = new MarketPageHandle();
        handle.handle(getClass().getResourceAsStream("/loaded-marketpage.html"));
        assertNotNull("Expected object", handle.getOutstandings());
        assertEquals("Expected 155", 155, handle.getOutstandings().getAmount());
        assertEquals("Expected 1", 1, handle.getOutstandings().getItems());
        assertNotNull(handle.getOutstandings().getItemList());
        assertEquals(1, handle.getOutstandings().getItemList().size());
        assertFalse("Last char should not be a )", handle.getOutstandings().getItemList().get(0).getAssetId().endsWith(")"));
    }

    @Test
    public void testBuyOrders() throws IOException {
        MarketPageHandle handle = new MarketPageHandle();
        handle.handle(getClass().getResourceAsStream("/market-page-with-buy-orders.html"));
        assertNotNull("Expected object", handle.getOutstandings().getMarketPageBuyOrders());
        assertEquals("Expected 198", 198, handle.getOutstandings().getMarketPageBuyOrders().size());
    }
}
