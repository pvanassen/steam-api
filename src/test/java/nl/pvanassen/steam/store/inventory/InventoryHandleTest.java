package nl.pvanassen.steam.store.inventory;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;

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

    @Test
    public void testHandleWithBlock() throws IOException {
        List<InventoryItem> items = new LinkedList<>();
        InventoryHandle handle = new InventoryHandle(new ObjectMapper(), 1, items);
        handle.handle(getClass().getResourceAsStream("/inventory-with-block.json"));

        assertEquals(1, items.size());
        InventoryItem item = items.get(0);
        assertNotNull(item);
        assertNotNull(item.getBlockedUntil());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2014);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DATE, 27);
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 22);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(calendar.getTime(), item.getBlockedUntil());
    }
}
