package nl.pvanassen.steam.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class OverviewHandleTest implements GenericHandle<OverviewItem>{
	private List<OverviewItem> itemList = new LinkedList<>();
	@Test
	public void testError() throws IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		OverviewHandle handle = new OverviewHandle(this, objectMapper);
		handle.handle(getClass().getResourceAsStream("/overview-error.json"));
		assertTrue("Expected to find an error", handle.isError());
		assertFalse("Expected not last page", handle.isLastPage());
		assertTrue("Expected empty list", itemList.isEmpty());
	}

	@Test
	public void testLastPage() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		OverviewHandle handle = new OverviewHandle(this, objectMapper);
		handle.handle(getClass().getResourceAsStream("/overview-lastpage.json"));
		assertFalse("Expecting no error", handle.isError());
		assertTrue("Expecting last page", handle.isLastPage());
		assertTrue("Expected empty list", itemList.isEmpty());
	}
	
	@Test
	public void testMormalPage() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		OverviewHandle handle = new OverviewHandle(this, objectMapper);
		handle.handle(getClass().getResourceAsStream("/overview-ok.json"));
		assertFalse("Expecting no error", handle.isError());
		assertFalse("Expecting last page", handle.isLastPage());
		assertFalse("Expected empty list", itemList.isEmpty());
		assertEquals("Expected 100 items", 100, itemList.size());
	}

	@Override
	public void handle(OverviewItem item) {
		itemList.add(item);
	}
}
