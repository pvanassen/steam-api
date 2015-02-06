package nl.pvanassen.steam.store.item;

import static org.junit.Assert.*;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

/**
 * Test for no info
 * 
 * @author Paul van Assen
 */
public class ListingPageScriptHandleTest {

    /**
     * Test manco key
     * 
     * @throws IOException
     */
    @Test
    public void testManco() throws IOException {
        ListingPageScriptHandle handle = new ListingPageScriptHandle(new ObjectMapper());
        try (InputStream stream = getClass().getResourceAsStream("/manco.html")) {
            handle.handle(stream);
        }
    }

    /**
     * Test no listing found
     * 
     * @throws IOException
     */
    @Test
    public void testNoListing() throws IOException {
        ListingPageScriptHandle handle = new ListingPageScriptHandle(new ObjectMapper());
        try (InputStream stream = getClass().getResourceAsStream("/listing-page-no-listing.html")) {
            handle.handle(stream);
            assertTrue("Expected no listings", handle.isNoListingForThisItem());
        }
    }

    /**
     * No pricing found exception test
     * 
     * @throws IOException
     */
    @Test
    public void testNoHistoryFound() throws IOException {
        ListingPageScriptHandle handle = new ListingPageScriptHandle(new ObjectMapper());
        try (InputStream stream = getClass().getResourceAsStream("/listing-page-no-price-history.html")) {
            handle.handle(stream);
            assertTrue("Expected no pricing history", handle.isNoPricingHistoryForThisItem());
        }
    }
}