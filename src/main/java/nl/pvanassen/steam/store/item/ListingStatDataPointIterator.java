package nl.pvanassen.steam.store.item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ListingStatDataPointIterator implements Iterator<StatDataPoint>, Iterable<StatDataPoint> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
    private StatDataPoint nextItem;
    private JsonNode priceHistory;
    private int nodePos = 0;

    ListingStatDataPointIterator(JsonNode priceHistory) {
        this.priceHistory = priceHistory;
    }

    private synchronized StatDataPoint getNextItem() {
        if (priceHistory == null) {
            return null;
        }
        if (nodePos >= priceHistory.size()) {
            return null;
        }
        JsonNode item = priceHistory.get(nodePos++);
        String dateStr = item.get(0).asText();
        double average = item.get(1).asDouble() * 100;
        String soldHour = item.get(2).asText();
        int sales = Integer.valueOf(soldHour.substring(0, soldHour.length() - 5));
        Date date;
        try {
            date = dateFormat.parse(dateStr);
        }
        catch (ParseException e) {
            logger.error("Error parsing steam", e);
            return getNextItem();
        }
        return new StatDataPoint(date, sales, average);
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
    public Iterator<StatDataPoint> iterator() {
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#next()
     */
    @Override
    public StatDataPoint next() {
        StatDataPoint item;
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
