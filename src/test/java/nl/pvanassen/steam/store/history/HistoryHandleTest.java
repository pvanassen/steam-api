package nl.pvanassen.steam.store.history;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.pvanassen.steam.store.common.Item;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test the history handler
 * 
 * @author Paul van Assen
 *
 */
public class HistoryHandleTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    static class Action {
        private final String action;
        private final int price;
        private final Date date;

        Action(String action, int price, Date date) {
            super();
            this.action = action;
            this.price = price;
            this.date = date;
        }

        String getAction() {
            return action;
        }

        Date getDate() {
            return date;
        }

        int getPrice() {
            return price;
        }
    }
    
    /**
     * Test the handle
     * @throws IOException
     */
    @Test
    public void testHandle() throws IOException {
        HistoryHandle handle = new HistoryHandle(null, new ObjectMapper());
        handle.handle(getClass().getResourceAsStream("/markethistory.json"));
        History history = handle.getHistory();
        List<Purchase> purchases = history.getPurchases();
        List<ListingCreated> listingsCreated = history.getListingsCreated();
        List<ListingRemoved> listingsRemoved = history.getListingsRemoved();
        List<Sale> sales = history.getSales();
        assertEquals(330, purchases.size());
        assertEquals(326, listingsCreated.size());
        assertEquals(176, listingsRemoved.size());
        assertEquals(168, sales.size());
        assertEquals(1000, purchases.size() + listingsCreated.size() + listingsRemoved.size() + sales.size());
        assertEquals("history_row_2846580424738068612_event_1", history.getLatestRowId());
        // 1. Map listing created to listing removed
        // 2. Map listing created to sold
        // 3. Map listing created + sold to purchased
        // 4. Map listing created + removed to purchased
        Map<String, Item> itemMap = new HashMap<>();
        for (Sale sale : sales) {
            itemMap.put(sale.getSteamId1(), sale.getItem());
        }

        for (ListingCreated row : listingsCreated) {
            for (Sale sale : sales) {
                if (row.getSteamId1().equals(sale.getSteamId1())) {
                    logger.info("Sale match id1");
                }
                if (row.getSteamId1().equals(sale.getSteamId2())) {
                    logger.info("Sale match id2");
                }
            }
            for (ListingRemoved listingRemoved : listingsRemoved) {
                if (row.getSteamId1().equals(listingRemoved.getSteamId1())) {
                    logger.info("ListingRemoved match id1");
                }
            }
            for (Purchase purchase : purchases) {
                if (row.getSteamId1().equals(purchase.getSteamId1())) {
                    logger.info("Purchase match id1");
                }
                if (row.getSteamId1().equals(purchase.getSteamId2())) {
                    logger.info("Purchase match id2");
                }
            }
        }
        /**
         * Map<Item,HistoryRow> createdItemØMapping =
         * Maps.uniqueIndex(listingsCreated, new Function<HistoryRow, Item>() {
         * 
         * @Override public Item apply(HistoryRow row) { return row. } });
         * 
         *           Map<String,HistoryRow> createdMapping =
         *           Maps.uniqueIndex(listingsCreated, new Function<HistoryRow,
         *           String>() {
         * @Override public String apply(HistoryRow row) { return
         *           row.getSteamId(); } }); Map<String,HistoryRow>
         *           removedMapping = Maps.uniqueIndex(listingsCreated, new
         *           Function<HistoryRow, String>() {
         * @Override public String apply(HistoryRow row) { return
         *           row.getSteamId(); } }); Map<String,Sale> soldMapping =
         *           Maps.uniqueIndex(sales, new Function<Sale, String>() {
         * @Override public String apply(Sale row) { return row.getSteamId(); }
         *           });
         * 
         *           Map<Purchase,List<Action>> actionMap = new HashMap<>(); for
         *           (Purchase purchase : purchases) { // Find first listing
         *           created based on the same item }
         */
    }
}
