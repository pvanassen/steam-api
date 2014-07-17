package nl.pvanassen.steam.store.item;

import java.util.Iterator;
import java.util.NoSuchElementException;

import nl.pvanassen.steam.store.common.Listing;

import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Iterator to get all listings
 * 
 * @author Paul van Assen
 */
public class ListingItemIterator implements Iterator<Listing>, Iterable<Listing> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final int appId;
    private final String urlName;
    private final JsonNode listingNode;
    private Listing nextItem;
    private final Iterator<String> fields;

    ListingItemIterator(int appId, String urlName, JsonNode listingNode) {
        this.appId = appId;
        this.urlName = urlName;
        this.listingNode = listingNode;
        if (listingNode == null) {
            fields = null;
        }
        else {
            fields = listingNode.getFieldNames();
        }
    }

    private synchronized Listing getNextItem() {
        if ((listingNode == null) || (fields == null)) {
            return null;
        }
        if (!fields.hasNext()) {
            return null;
        }
        JsonNode item = listingNode.get(fields.next());
        try {
            if ((item.get("price").asInt() == 0) || (item.get("converted_fee") == null)) {
                return getNextItem();
            }
            String listingId = item.get("listingid").asText();
            String steamIdListing = item.get("steamid_lister").asText();
            int convertedPrice = item.get("converted_price").asInt();
            int convertedFee = item.get("converted_fee").asInt();
            int convertedSteamFee = item.get("converted_steam_fee").asInt();
            int convertedPublisherFee = item.get("converted_publisher_fee").asInt();
            int publisherFeeApp = item.get("publisher_fee_app").asInt();
            double publisherFeePercent = item.get("publisher_fee_percent").asDouble();
            return new Listing(appId, urlName, listingId, steamIdListing, convertedPrice, convertedFee,
                    convertedSteamFee, convertedPublisherFee, publisherFeeApp, publisherFeePercent, "--");
        }
        catch (RuntimeException e) {
            logger.info("Error getting item " + item, e);
            return getNextItem();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        if (nextItem != null) {
            return true;
        }
        nextItem = getNextItem();
        return nextItem != null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<Listing> iterator() {
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#next()
     */
    @Override
    public Listing next() {
        Listing item;
        if (hasNext()) {
            item = nextItem;
            nextItem = null;
            return item;
        }
        throw new NoSuchElementException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove");
    }

}
