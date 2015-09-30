package nl.pvanassen.steam.store.buy;

import nl.pvanassen.steam.store.StreamHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class BuyHandleTest {

    @Test
    public void testError() throws IOException {
        BuyHandle handle = new BuyHandle(new ObjectMapper());
        handle.handleError(StreamHelper.getStream("/buyhandle-error.json"));
        assertTrue(handle.isError());
        assertEquals("Testmessage", handle.getMessage());
    }

    @Test
    public void testSuccess() throws IOException {
        BuyHandle handle = new BuyHandle(new ObjectMapper());
        handle.handle(StreamHelper.getStream("/buyhandle-success.json"));
        assertFalse(handle.isError());
        assertNull(handle.getMessage());
        assertEquals(27485, handle.getWallet());
    }
}
