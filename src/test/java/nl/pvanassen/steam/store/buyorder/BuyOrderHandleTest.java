package nl.pvanassen.steam.store.buyorder;

import static org.junit.Assert.*;

import java.io.IOException;

import nl.pvanassen.steam.store.StreamHelper;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class BuyOrderHandleTest {

    @Test
    public void testCancelError() throws IOException {
        BuyOrderHandle handle = new BuyOrderHandle(new ObjectMapper());
        handle.handleError(StreamHelper.getStream("/buyorder-cancel-error.json"));
        assertTrue(handle.isError());
        assertEquals("Unknown", handle.getMessage());
        assertNull(handle.getBuyOrderId());
    }

    @Test
    public void testCancelOk() throws IOException {
        BuyOrderHandle handle = new BuyOrderHandle(new ObjectMapper());
        handle.handle(StreamHelper.getStream("/buyorder-cancel-ok.json"));
        assertFalse(handle.isError());
        assertNull(handle.getBuyOrderId());
    }

    @Test
    public void testCreateError() throws IOException {
        BuyOrderHandle handle = new BuyOrderHandle(new ObjectMapper());
        handle.handleError(StreamHelper.getStream("/buyorder-create-error.json"));
        assertTrue(handle.isError());
        assertEquals("Error handling", handle.getMessage());
        assertNull(handle.getBuyOrderId());
    }

    @Test
    public void testCreateOk() throws IOException {
        BuyOrderHandle handle = new BuyOrderHandle(new ObjectMapper());
        handle.handle(StreamHelper.getStream("/buyorder-create-ok.json"));
        assertFalse(handle.isError());
        assertEquals("198984", handle.getBuyOrderId());
    }

}
