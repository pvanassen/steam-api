package nl.pvanassen.steam.store.item;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nl.pvanassen.steam.store.StreamHelper;
import nl.pvanassen.steam.store.common.GenericHandle;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class OverviewHandleTest implements GenericHandle<OverviewItem> {
    private List<OverviewItem> itemList = new LinkedList<>();

    @Override
    public void handle(OverviewItem item) {
        itemList.add(item);
    }

    @Test
    public void testError() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        OverviewHandle handle = new OverviewHandle(this, objectMapper);
        handle.handle(StreamHelper.getStream("/overview-error.json"));
        assertTrue("Expected to find an error", handle.isError());
        assertFalse("Expected not last page", handle.isLastPage());
        assertTrue("Expected empty list", itemList.isEmpty());
    }

    @Test
    public void testLastPage() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        OverviewHandle handle = new OverviewHandle(this, objectMapper);
        handle.handle(StreamHelper.getStream("/overview-lastpage.json"));
        assertFalse("Expecting no error", handle.isError());
        assertTrue("Expecting last page", handle.isLastPage());
        assertTrue("Expected empty list", itemList.isEmpty());
    }

    @Test
    public void testNormalPage() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        OverviewHandle handle = new OverviewHandle(this, objectMapper);
        handle.handle(StreamHelper.getStream("/overview-ok.json"));
        assertFalse("Expecting no error", handle.isError());
        assertFalse("Expecting last page", handle.isLastPage());
        assertFalse("Expected empty list", itemList.isEmpty());
        assertEquals("Expected 100 items", 100, itemList.size());
    }
}
