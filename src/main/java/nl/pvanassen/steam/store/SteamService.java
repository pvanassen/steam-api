package nl.pvanassen.steam.store;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;

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
    public void getAllItems( ExecutorService executorService, GenericHandle<OverviewItem> handle ) {
        for ( OverviewItem overviewItem : new OverviewIterator(http, executorService) ) {
            try {
                handle.handle( overviewItem );
            }
            catch ( RuntimeException e ) {
                logger.error( "Error handling item", e );
            }
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
            ListingHandle handle = new ListingHandle( objectMapper );
            http.get( "http://steamcommunity.com/market/recent", handle );
            return handle.getListings();
        }
        catch ( IOException e ) {
            logger.error( "Error posting data", e );
        }
        return Collections.emptyList();
    }

    @Override
    public int getWallet() {
        WalletHandle handle = new WalletHandle();
        try {
            http.get( "http://steamcommunity.com/market/", handle );
        }
        catch ( IOException e ) {
            logger.error( "Error getting wallet", e );
        }
        // If the wallet is empty, re-init all cookies
        if (handle.getWallet() == 0) {
            http.reset();
        }
        return handle.getWallet();
    }

    @Override
    public boolean sell( String assetId, int appId, String urlName, int contextId, int price ) {
        try {
//        	Http http = Http.getInstance(cookies);
//        	http.get("http://steamcommunity.com/market/pricehistory/?appid=" + appId + "&market_hash_name=" + urlName, new DefaultHandle());
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
            http.get("http://steamcommunity.com/market/myhistory/render/?query=&search_descriptions=0&start=10&count=1000", handle);
        }
        catch ( IOException | RuntimeException e ) {
            logger.error( "Error getting data", e );
        }
        return handle.getMarketHistory();
    }
}
