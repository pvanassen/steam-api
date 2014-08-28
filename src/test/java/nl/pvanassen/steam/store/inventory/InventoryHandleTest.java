package nl.pvanassen.steam.store.inventory;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class InventoryHandleTest {

	@Test
    public void testHandle() throws IOException {
		List<InventoryItem> items = new LinkedList<>();
        InventoryHandle handle = new InventoryHandle(new ObjectMapper(), 1, items);
        handle.handle(getClass().getResourceAsStream("/inventory.json"));
        
        assertEquals(2, items.size());
    }

}
