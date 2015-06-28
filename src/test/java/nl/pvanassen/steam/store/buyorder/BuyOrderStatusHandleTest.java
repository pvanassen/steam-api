package nl.pvanassen.steam.store.buyorder;

import static org.junit.Assert.*;

import java.io.IOException;

import nl.pvanassen.steam.store.StreamHelper;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class BuyOrderStatusHandleTest {

    @Test
    public void test() throws IOException {
        BuyOrderStatusHandle handle = new BuyOrderStatusHandle(new ObjectMapper());
        handle.handle(StreamHelper.getStream("/buyorder-status.json"));
        BuyOrderStatus status = handle.getBuyOrderStatus();
        assertNotNull(status);
        assertTrue(status.isActive());
        assertTrue(status.isSuccess());
        assertEquals(0, status.getPurchased());
        assertEquals(3, status.getQuantity());
        assertEquals(3, status.getQuantityRemaining());
    }

}
