package nl.pvanassen.steam.store;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Outstanding items
 * 
 * @author Paul van Assen
 */
public class Outstandings {
    private final int items;
    private final int amount;
    private final List<OutstandingItem> itemList;

    Outstandings(int items, int amount, List<OutstandingItem> itemList) {
        this.items = items;
        this.amount = amount;
        this.itemList = ImmutableList.copyOf(itemList);
    }

    /**
     * @return Amount in euros of outstanding items
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @return Number of items
     */
    public int getItems() {
        return items;
    }

    /**
     * @return Item list
     */
    public List<OutstandingItem> getItemList() {
        return itemList;
    }

}
