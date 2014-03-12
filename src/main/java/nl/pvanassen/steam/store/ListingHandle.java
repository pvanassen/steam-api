package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonNode;
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
        JsonNode node = objectMapper.readTree( stream );
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
        }
    }
}
