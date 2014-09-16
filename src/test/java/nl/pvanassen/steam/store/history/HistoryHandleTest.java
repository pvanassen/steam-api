package nl.pvanassen.steam.store.history;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class HistoryHandleTest {
	@Test
    public void testHandle() throws IOException {
        HistoryHandle handle = new HistoryHandle(null, new ObjectMapper());
        handle.handle(getClass().getResourceAsStream("/markethistory.json"));
        List<Purchase> purchases = handle.getPurchases();
        List<HistoryRow> listingsCreated = handle.getListingsCreated();
        List<HistoryRow> listingsRemoved = handle.getListingsRemoved();
        List<Sale> sales = handle.getSales();
        assertEquals(330, purchases.size());
    }
}
