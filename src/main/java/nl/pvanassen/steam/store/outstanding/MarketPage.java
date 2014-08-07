package nl.pvanassen.steam.store.outstanding;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Outstanding items
 * 
 * @author Paul van Assen
 */
public class MarketPage {
    private final int wallet;
    private final int items;
    private final int amount;
    private final List<OutstandingItem> itemList;
    private final Set<Integer> appIds;

    MarketPage(int wallet, int items, int amount, List<OutstandingItem> itemList, Set<Integer> appIds) {
        this.wallet = wallet;
        this.items = items;
        this.amount = amount;
        this.itemList = ImmutableList.copyOf(itemList);
        this.appIds = ImmutableSet.copyOf(appIds);
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

    /**
     * @return App ids
     */
    public Set<Integer> getAppIds() {
        return appIds;
    }
}
