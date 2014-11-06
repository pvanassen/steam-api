package nl.pvanassen.steam.store.listing;

import java.io.IOException;
import java.io.InputStream;

import nl.pvanassen.steam.http.DefaultHandle;
import nl.pvanassen.steam.store.common.Listing;
import nl.pvanassen.steam.store.helper.UrlNameHelper;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ListingHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper;
    private final ListingDeque listings;
    private final String country;

    ListingHandle(ObjectMapper objectMapper, ListingDeque listings, String country) {
        this.objectMapper = objectMapper;
        this.listings = listings;
        this.country = country;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        JsonNode node = objectMapper.readTree(stream);
        JsonNode assets = node.get("assets");
        for (JsonNode listing : node.get("listinginfo")) {
            int appId = listing.get("asset").get("appid").asInt();
            String contextId = listing.get("asset").get("contextid").asText();
            String id = listing.get("asset").get("id").asText();
            String urlName = UrlNameHelper.getUrlName(assets.get(Integer.toString(appId)).get(contextId).get(id).get("market_hash_name").asText());
            try {
                if (listing.get("price").asInt() == 0) {
                    logger.warn("Sometimes Steam send 0 price. Skipping");
                    continue;
                }
                listings.offerFirst(new Listing(appId, urlName, listing.get("listingid").asText(), listing.get("converted_price").asInt(), listing.get("converted_fee").asInt(),
                        listing.get("converted_steam_fee").asInt(), listing.get("converted_publisher_fee").asInt(), listing.get("publisher_fee_app").asInt(), listing.get(
                                "publisher_fee_percent").asDouble(), country));
            }
            catch (NullPointerException e) {
                logger.error("Error fetching " + listing + ", " + e.getMessage());
            }
        }
    }

    @Override
    public void handleError(InputStream stream) throws IOException {
        // Ignore errors
        return;
    }
}
