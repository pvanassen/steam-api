package nl.pvanassen.steam.store.inventory;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;

import nl.pvanassen.steam.store.common.InventoryItem;

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

        assertEquals(28, items.size());
        InventoryItem item = items.get(14);
        assertNotNull(item);
        assertNotNull(item.getBlockedUntil());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2015);
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DATE, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 34);
        calendar.set(Calendar.SECOND, 31);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(calendar.getTime(), item.getBlockedUntil());
    }
}
