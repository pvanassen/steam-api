package nl.pvanassen.steam.store.buyorder;

import static org.junit.Assert.*;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class BuyOrderHandleTest {

	@Test
	public void testCreateOk() throws IOException {
		BuyOrderHandle handle = new BuyOrderHandle(new ObjectMapper());
		handle.handle(getClass().getResourceAsStream("/buyorder-create-ok.json"));
		assertFalse(handle.isError());
		assertEquals("198984", handle.getBuyOrderId());
	}
	
	@Test
	public void testCreateError() throws IOException {
		BuyOrderHandle handle = new BuyOrderHandle(new ObjectMapper());
		handle.handle(getClass().getResourceAsStream("/buyorder-create-error.json"));
		assertTrue(handle.isError());
		assertEquals("Error handling", handle.getMessage());
		assertNull(handle.getBuyOrderId());
	}
	@Test
	public void testCancelOk() throws IOException {
		BuyOrderHandle handle = new BuyOrderHandle(new ObjectMapper());
		handle.handle(getClass().getResourceAsStream("/buyorder-cancel-ok.json"));
		assertFalse(handle.isError());
		assertNull(handle.getBuyOrderId());
	}
	@Test
	public void testCancelError() throws IOException {
		BuyOrderHandle handle = new BuyOrderHandle(new ObjectMapper());
		handle.handle(getClass().getResourceAsStream("/buyorder-cancel-error.json"));
		assertTrue(handle.isError());
		assertEquals("Error handling", handle.getMessage());
		assertNull(handle.getBuyOrderId());
	}

}
