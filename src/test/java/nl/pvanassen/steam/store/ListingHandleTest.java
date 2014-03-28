package nl.pvanassen.steam.store;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

public class ListingHandleTest {
    private ListingHandle handle;
    private final Queue<Listing> listingQueue = new LinkedBlockingQueue<>();
    
    @Before
    public void setUp() {
        handle = new ListingHandle( new ObjectMapper(), listingQueue );
    }

    @Test
    public void testPerformance() throws IOException {
        for ( int i = 0; i != 5000; i++ ) {
            handle.handle( getClass().getResourceAsStream( "/listing.json" ) );
        }
        long start = System.currentTimeMillis();
        for ( int i = 0; i != 10000; i++ ) {
            handle.handle( getClass().getResourceAsStream( "/listing.json" ) );
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("Took: " + time);
    }

    @Test
    public void testHandle() throws IOException {
        listingQueue.clear();
        handle.handle( getClass().getResourceAsStream( "/listing.json" ) );
        List<Listing> listings = new ArrayList<>(listingQueue);
        assertEquals( 10, listings.size() );
        assertEquals( 730, listings.get( 0 ).getAppId() );
        assertEquals( "eSports%20Case", listings.get( 0 ).getUrlName() );
        assertEquals( 2, listings.get( 0 ).getFee() );
        assertEquals( 1, listings.get( 0 ).getPublisherFee() );
        assertEquals( 730, listings.get( 0 ).getPublisherFeeApp() );
        assertEquals( "2854460461479367720", listings.get( 0 ).getListingId() );
        assertEquals( 1, listings.get( 0 ).getSteamFee() );
        assertEquals( "76561197971150314", listings.get( 0 ).getSteamIdLister() );
        assertEquals( 16, listings.get( 0 ).getSubTotal() );
    }

}
