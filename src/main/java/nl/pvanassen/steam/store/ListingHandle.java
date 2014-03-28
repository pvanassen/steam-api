package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ListingHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper;
    private final List<Listing> listings = new LinkedList<Listing>();

    ListingHandle( ObjectMapper objectMapper ) {
        this.objectMapper = objectMapper;
    }

    List<Listing> getListings() {
        return listings;
    }

    @Override
    public void handle( InputStream stream ) throws IOException {
        Map<String, IncompleteListing> incompleteListingsMap = new HashMap<>();
        Map<String, Asset> assetsMap = new HashMap<>();
        JsonParser jParser = objectMapper.getJsonFactory().createJsonParser( stream );
        while (jParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jParser.getCurrentName();
            if ("listinginfo".equals(fieldname)) {
                incompleteListingsMap = getListings(jParser);
            }
            else if ("assets".equals(fieldname)) {
                assetsMap = getAssets(jParser);
            }
        }
        for (Map.Entry<String,IncompleteListing> entry : incompleteListingsMap.entrySet()) {
            IncompleteListing incompleteListing = entry.getValue();
            Asset asset = assetsMap.get( entry.getKey() );
            if (asset == null) {
                continue;
            }
            listings.add( incompleteListing.getListing( asset ) );
        }
/*        JsonNode node = objectMapper.readTree( stream );
        JsonNode assets = node.get( "assets" );
        for ( JsonNode listing : node.get( "listinginfo" ) ) {
            int appId = listing.get( "asset" ).get( "appid" ).asInt();
            String contextId = listing.get( "asset" ).get( "contextid" ).asText();
            String id = listing.get( "asset" ).get( "id" ).asText();
            String urlName = URLEncoder.encode( assets.get( Integer.toString( appId ) ).get( contextId ).get( id ).get( "market_hash_name" ).asText(), "UTF-8" ).replace( "+", "%20" );
            try {
                listings.add( new Listing( appId, urlName, listing.get( "listingid" ).asText(), listing.get( "steamid_lister" ).asText(), listing.get( "converted_price" ).asInt(), listing.get( "converted_fee" ).asInt(), listing.get( "converted_steam_fee" ).asInt(), listing.get( "converted_publisher_fee" ).asInt(), listing.get( "publisher_fee_app" ).asInt(), listing.get( "publisher_fee_percent" ).asDouble() ) );
            }
            catch (NullPointerException e) {
                logger.error("Error fetching " + listing);
            }
        }*/
    }
    
    private Map<String, Asset> getAssets( JsonParser jParser ) throws JsonParseException, IOException {
        Map<String, Asset> map = new HashMap<>();
        while (jParser.nextToken() != JsonToken.END_ARRAY) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                jParser.nextToken();
                while (jParser.nextToken() != JsonToken.END_OBJECT) {
                    fieldname = jParser.getCurrentName();
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_OBJECT) {
                        Asset asset = new Asset();
                        fieldname = jParser.getCurrentName();
                        jParser.nextToken();
                        if ("id".equals( fieldname )) {
                            asset.id = jParser.getText();
                        }
                        else if ("tradable".equals( fieldname )) {
                            asset.tradable = jParser.getBooleanValue();
                        }
                        else if ("market_hash_name".equals( fieldname )) {
                            asset.urlName = jParser.getText();
                        }
                    }
                }
            }
        }
        return map;
    }

    private Map<String, IncompleteListing> getListings( JsonParser jParser ) throws JsonParseException, IOException {
        Map<String, IncompleteListing> map = new HashMap<>();
        while (jParser.nextToken() != JsonToken.END_ARRAY) {
            IncompleteListing listing = new IncompleteListing();
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                jParser.nextToken();
                if ("asset".equals(fieldname)) {
                    while (jParser.nextToken() != JsonToken.END_OBJECT) {
                        fieldname = jParser.getCurrentName();
                        jParser.nextToken();
                        if ("id".equals( fieldname )) {
                            listing.id = jParser.getText();
                        }
                        else if ("appid".equals( fieldname )) {
                            listing.appId = jParser.getIntValue();
                        }
                        else if ("contextid".equals( fieldname )) {
                            listing.contextId = jParser.getIntValue();
                        }
                    }
                }
                if ("listingid".equals(fieldname)) {
                    listing.listingId = jParser.getText();
                }
                else if ("steamid_lister".equals( fieldname )) {
                    listing.steamIdLister = jParser.getText(); 
                }
                else if ("converted_price".equals( fieldname )) {
                    listing.subTotal = jParser.getIntValue(); 
                }
                else if ("converted_fee".equals( fieldname )) {
                    listing.fee = jParser.getIntValue(); 
                }
                else if ("converted_steam_fee".equals( fieldname )) {
                    listing.steamFee = jParser.getIntValue(); 
                }
                else if ("converted_publisher_fee".equals( fieldname )) {
                    listing.publisherFee = jParser.getIntValue(); 
                }
                else if ("publisher_fee_app".equals( fieldname )) {
                    listing.publisherFeeApp = jParser.getIntValue(); 
                }
                else if ("publisher_fee_percent".equals( fieldname )) {
                    listing.publisherFeePercent = jParser.getDoubleValue(); 
                }
                map.put( listing.id, listing );
            }
            
        }
        return map;
    }

    


    private static class IncompleteListing {
        private int appId;
        
        private int contextId;
        
        private String id;
        
        private String listingId;

        private String steamIdLister;

        private int subTotal;

        private int fee;

        private int steamFee;

        private int publisherFee;

        private int publisherFeeApp;

        private double publisherFeePercent;
        
        Listing getListing(Asset asset) {
            return new Listing( appId, asset.urlName, listingId, steamIdLister, subTotal, fee, steamFee, publisherFee, publisherFeeApp, publisherFeePercent );
        }
    }
    
    private static class Asset {
        private String id;

        private String urlName;
        
        private boolean tradable;
    }
}
