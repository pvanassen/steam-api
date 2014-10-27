package nl.pvanassen.steam.store.buyorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class BuyOrderStatusHandleTest {

    @Test
    public void test() throws IOException {
        BuyOrderStatusHandle handle = new BuyOrderStatusHandle(new ObjectMapper());
        handle.handle(getClass().getResourceAsStream("/buyorder-status.json"));
        BuyOrderStatus status = handle.getBuyOrderStatus();
        assertNotNull(status);
        assertTrue(status.isActive());
        assertTrue(status.isSuccess());
        assertEquals(0, status.getPurchased());
        assertEquals(3, status.getQuantity());
        assertEquals(3, status.getQuantityRemaining());
    }

}
