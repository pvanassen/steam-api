package nl.pvanassen.steam.store;

import java.io.*;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Handler to parse the listing page
 * 
 * @author Paul van Assen
 */
public class ListingPageScriptHandle extends DefaultHandle {

    private final ObjectMapper om;
    private JsonNode listingInfo;
    private JsonNode priceHistoryInfo;

    ListingPageScriptHandle( ObjectMapper om ) {
        this.om = om;
    }

    JsonNode getListingInfo() {
        return listingInfo;
    }

    JsonNode getPriceHistoryInfo() {
        return priceHistoryInfo;
    }

    @Override
    public void handle( InputStream stream ) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( stream ) );
        boolean listingFound = false;
        boolean salesFound = false;
        String line;
        while ( ( line = reader.readLine() ) != null ) {
            if ( !listingFound ) {
                int listingStart = line.indexOf( "g_rgListingInfo" );
                if ( listingStart > -1 ) {
                    int objectStart = line.indexOf( '{', listingStart );
                    if ( objectStart > -1 ) {
                        listingInfo = om.readTree( line.substring( objectStart, line.length() - 1 ) );
                        listingFound = true;
                    }
                }
            }
            if ( !salesFound ) {
                int salesStart = line.indexOf( "line1" );
                if ( salesStart > -1 ) {
                    int objectStart = line.indexOf( '[', salesStart );
                    if (objectStart <= -1) {
                    	continue;
                    }
                    priceHistoryInfo = om.readTree( line.substring( objectStart, line.length() - 1 ) );
                    salesFound = true;
                }
            }
        }
    }

}
