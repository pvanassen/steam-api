package nl.pvanassen.steam.store;

import java.io.IOException;
import java.util.*;

import nl.pvanassen.steam.http.Http;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * Interface to the steam store
 * 
 * @author Paul van Assen
 */
class SteamService implements StoreService {

    private final Logger logger = LoggerFactory.getLogger( getClass() );
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int[] APP_IDS = new int[] { 440, 570, 730, 753, 238960, 230410 };
    
    private final Http http;

    SteamService(String cookies) {
        http = Http.getInstance(cookies);
    }

    @Override
    public BuyResult buy( Listing listing ) {
    	String listingId = listing.getListingId();
    	int fee = listing.getFee();
    	int subTotal = listing.getSubTotal();
        Map<String, String> params = new HashMap<>();
        params.put( "currency", "3" );
        params.put( "fee", Integer.toString( fee ) );
        params.put( "subtotal", Integer.toString( subTotal ) );
        params.put( "total", Integer.toString( fee + subTotal ) );
        try {
            BuyHandle handle = new BuyHandle();
            http.post( "https://steamcommunity.com/market/buylisting/" + listingId, params, handle );
            if (handle.getMessage() != null && handle.getMessage().contains("temporary")) {
            	return buy(listing);
            }
            if (handle.getMessage() != null && handle.getMessage().contains("Cookies")) {
                http.reset();
                return buy(listing);
            }
            return new BuyResult( !handle.isError(), handle.getWallet(), handle.getMessage() );
        }
        catch ( IOException e ) {
            logger.error( "Error posting data", e );
            return new BuyResult( false, 0, "" );
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getAllItems(java.util.concurrent.ExecutorService, nl.pvanassen.steam.store.GenericHandle)
     */
    @Override
    public void getAllItems( GenericHandle<OverviewItem> genericHandle ) {
    	try {
    		OverviewHandle handle = new OverviewHandle(genericHandle, objectMapper);
    		// Initial high, will be corrected on first run
    		int totalCount = 5000;
    		for ( int start = 0; start < totalCount; start += 100 ) {
    		    do {
        			http.get( "http://steamcommunity.com/market/search/render/?query=&search_descriptions=0&start=" + start + "&count=100", handle );
        			totalCount = handle.getTotalCount();
        			// Stop on overrun
        			if (handle.isLastPage()) {
        				return;
        			}
        		} while (handle.isError());
    		}
    	}
    	catch (IOException e) {
            logger.error( "Error handling item", e );
    	}
    }

    @Override
    public List<InventoryItem> getInventory() {
        List<InventoryItem> inventoryItems = new LinkedList<>();
        for ( int appId : APP_IDS ) {
            int contextId = 2;
            if ( appId == 753 ) {
                contextId = 6;
            }
            InventoryHandle handle = new InventoryHandle( objectMapper, contextId, inventoryItems );
            try {
                http.get( "http://steamcommunity.com/id/mantorch/inventory/json/" + appId + "/" + contextId + "/", handle );
            }
            catch ( IOException e ) {
                logger.error( "Error fetching inventory data", e );

            }
        }
        return ImmutableList.copyOf( inventoryItems );
    }

    @Override
    public void getItem( int appId, String urlName, GenericHandle<StatDataPoint> dataPointHandle, GenericHandle<Listing> listingHandle ) {

        ListingPageScriptHandle handle = new ListingPageScriptHandle( objectMapper );
        try {
            http.get( "http://steamcommunity.com/market/listings/" + appId + "/" + urlName, handle );
        }
        catch ( IOException e ) {
            logger.error( "Error fetching listing page data", e );
        }
        JsonNode priceHistoryInfo = handle.getPriceHistoryInfo();
        for ( StatDataPoint point : new ListingStatDataPointIterator( priceHistoryInfo ) ) {
            dataPointHandle.handle( point );
        }
        if ( listingHandle == null ) {
            return;
        }
        JsonNode listingInfo = handle.getListingInfo();
        for ( Listing item : new ListingItemIterator( appId, urlName, listingInfo ) ) {
            listingHandle.handle( item );
        }
    }

    @Override
    public List<Listing> getNewlyListed() {
        try {
            LinkedList<Listing> listing = new LinkedList<>();
            ListingHandle handle = new ListingHandle( objectMapper, listing );
            http.get( "http://steamcommunity.com/market/recent", handle );
            return listing;
        }
        catch ( IOException e ) {
            logger.error( "Error getting inventory", e );
        }
        return Collections.emptyList();
    }
    
    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.StoreService#getAsyncInventory(java.util.Deque)
     */
    @Override
    public void getAsyncInventory( Deque<Listing> queue ) {
        try {
            ListingHandle handle = new ListingHandle( objectMapper, queue );
            http.get( "http://steamcommunity.com/market/recent", handle );
        }
        catch ( IOException e ) {
            logger.error( "Error getting inventory", e );
        }
        
    }

    @Override
    public int getWallet() {
        WalletHandle handle = new WalletHandle();
        try {
            http.get( "http://steamcommunity.com/market/", handle );
            if (handle.getWallet() == 0) {
                http.reset();
                return getWallet();
            }
        }
        catch ( IOException e ) {
            logger.error( "Error getting wallet", e );
        }
        return handle.getWallet();
    }

    @Override
    public boolean sell( String assetId, int appId, String urlName, int contextId, int price ) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put( "amount", "1" );
            params.put( "appid", Integer.toString( appId ) );
            params.put( "assetid", assetId );
            params.put( "contextid", Integer.toString( contextId ) );
            params.put( "price", Integer.toString( price ) );
            logger.info( params.toString() );
            SellHandle sellHandle = new SellHandle();
            http.post( "https://steamcommunity.com/market/sellitem/", params, sellHandle );
            return !sellHandle.isError();
        }
        catch ( IOException | RuntimeException e ) {
            logger.error( "Error posting data", e );
            return false;
        }
    }

    @Override
    public List<MarketHistory> getSoldItemsFromHistory() {
        MarketHistoryHandle handle = new MarketHistoryHandle();
        try {
            http.get("http://steamcommunity.com/market/myhistory/render/?query=&search_descriptions=0&start=0&count=1000", handle);
            if (handle.isError()) {
                return getSoldItemsFromHistory();
            }
            int totalCount = handle.getTotalCount();
            for (int start = 1000; start < totalCount; start += 1000) {
                do {
                    Thread.sleep(500);
                    http.get("http://steamcommunity.com/market/myhistory/render/?query=&search_descriptions=0&count=1000&start=" + start, handle);
                }
                while (handle.isError());
            }
        }
        catch ( IOException | RuntimeException | InterruptedException e ) {
            logger.error( "Error getting data", e );
        }
        return handle.getMarketHistory();
    }
    
    @Override
    public Outstandings getOutstandings() {
        OutstandingsHandle handle = new OutstandingsHandle();
        try {
            http.get( "http://steamcommunity.com/market/", handle );
        }
        catch ( IOException e ) {
            logger.error( "Error getting wallet", e );
        }
        return handle.getOutstandings();
    }
    
    @Override
    public String login( String user, String password ) throws VerificationError {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String verification( String code ) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
