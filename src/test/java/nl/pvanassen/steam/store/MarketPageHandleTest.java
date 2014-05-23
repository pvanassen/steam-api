package nl.pvanassen.steam.store;

import static org.junit.Assert.*;

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
		assertNotNull(handle.getItems());
        assertNotNull(handle.getOutstandings().getAppIds());
	}


	@Test
	public void testLoadedHandle() throws IOException {
		MarketPageHandle handle = new MarketPageHandle();
		handle.handle(getClass().getResourceAsStream("/loaded-marketpage.html"));
		assertNotNull("Expected object", handle.getOutstandings());
		assertEquals("Expected 155", 155, handle.getOutstandings().getAmount());
		assertEquals("Expected 1", 1, handle.getOutstandings().getItems());
        assertNotNull(handle.getItems());
        assertEquals(1, handle.getItems().size());
        assertNotNull(handle.getOutstandings().getAppIds());
        assertTrue(handle.getOutstandings().getAppIds().contains(238460));
        assertTrue(handle.getOutstandings().getAppIds().contains(730));
        assertTrue(handle.getOutstandings().getAppIds().contains(570));
        assertTrue(handle.getOutstandings().getAppIds().contains(238960));
        assertTrue(handle.getOutstandings().getAppIds().contains(753));
        assertTrue(handle.getOutstandings().getAppIds().contains(440));
        assertTrue(handle.getOutstandings().getAppIds().contains(230410));
	}

}
