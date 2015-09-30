package nl.pvanassen.steam.store.inventory;

import nl.pvanassen.steam.store.StreamHelper;
import nl.pvanassen.steam.store.common.InventoryItem;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class InventoryHandleTest {

    @Test
    public void testHandle() throws IOException {
        List<InventoryItem> items = new LinkedList<>();
        InventoryHandle handle = new InventoryHandle(new ObjectMapper(), 1, items);
        handle.handle(StreamHelper.getStream("/inventory.json"));

        assertEquals(2, items.size());
    }

    @Test
    public void testHandleWithBlock() throws IOException {
        List<InventoryItem> items = new LinkedList<>();
        InventoryHandle handle = new InventoryHandle(new ObjectMapper(), 1, items);
        handle.handle(StreamHelper.getStream("/inventory-with-block.json"));

        assertEquals(28, items.size());
        InventoryItem item = items.get(14);
        assertNotNull(item);
        assertFalse(item.isMarketable());
        assertFalse(item.isTradable());
        assertNotNull(item.getBlockedUntil());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2015);
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DATE, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 34);
        calendar.set(Calendar.SECOND, 31);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        assertEquals(calendar.getTime(), item.getBlockedUntil());
    }
    
    @Test
    public void testHandleWithBlock570() throws IOException {
        List<InventoryItem> items = new LinkedList<>();
        InventoryHandle handle = new InventoryHandle(new ObjectMapper(), 1, items);
        handle.handle(StreamHelper.getStream("/inventory-570.json"));

        assertEquals(48, items.size());
        InventoryItem item = items.get(1);
        assertNotNull(item);
        assertNotNull(item.getBlockedUntil());
        assertFalse(item.isMarketable());
        assertFalse(item.isTradable());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2015);
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DATE, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 23);
        calendar.set(Calendar.SECOND, 35);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(calendar.getTime(), item.getBlockedUntil());
    }
    
    @Test
    public void testHandleWithBlock730() throws IOException {
        List<InventoryItem> items = new LinkedList<>();
        InventoryHandle handle = new InventoryHandle(new ObjectMapper(), 1, items);
        handle.handle(StreamHelper.getStream("/inventory-730.json"));

        assertEquals(10, items.size());
        InventoryItem item = items.get(0);
        assertNotNull(item);
        assertNotNull(item.getBlockedUntil());
        assertTrue(item.isMarketable());
        assertFalse(item.isTradable());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2015);
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DATE, 7);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        assertEquals(calendar.getTime(), item.getBlockedUntil());
    }
}
