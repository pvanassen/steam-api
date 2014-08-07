package nl.pvanassen.steam.store.buy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class BuyHandleTest {

	@Test
	public void testSuccess() throws IOException {
		BuyHandle handle = new BuyHandle(new ObjectMapper());
		handle.handle(getClass().getResourceAsStream("/buyhandle-success.json"));
		assertFalse(handle.isError());
		assertNull(handle.getMessage());
		assertEquals(27485, handle.getWallet());
	}

	@Test
	public void testError() throws IOException {
		BuyHandle handle = new BuyHandle(new ObjectMapper());
		handle.handleError(getClass().getResourceAsStream("/buyhandle-error.json"));
		assertTrue(handle.isError());
		assertEquals("Testmessage", handle.getMessage());
	}
}
