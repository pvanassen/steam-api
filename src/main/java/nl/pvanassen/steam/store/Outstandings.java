package nl.pvanassen.steam.store;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Outstanding items
 * 
 * @author Paul van Assen
 */
public class Outstandings {
    private final int wallet;
    private final int items;
    private final int amount;
    private final List<OutstandingItem> itemList;

    Outstandings(int wallet, int items, int amount, List<OutstandingItem> itemList) {
        this.wallet = wallet;
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
    
    /**
     * @return Amount of money in the wallet
     */
    public int getWallet() {
        return wallet;
    }

}
