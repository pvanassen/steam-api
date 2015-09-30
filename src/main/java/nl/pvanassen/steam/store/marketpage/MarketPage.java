package nl.pvanassen.steam.store.marketpage;

import com.google.common.collect.ImmutableList;

import java.util.List;

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
    private final List<MarketPageBuyOrder> marketPageBuyOrders;

    MarketPage(int wallet, int items, int amount, List<OutstandingItem> itemList, List<MarketPageBuyOrder> marketPageBuyOrders) {
        this.wallet = wallet;
        this.items = items;
        this.amount = amount;
        this.itemList = ImmutableList.copyOf(itemList);
        if (marketPageBuyOrders == null) {
            this.marketPageBuyOrders = ImmutableList.of();
        }
        else {
            this.marketPageBuyOrders = ImmutableList.copyOf(marketPageBuyOrders);
        }
    }

    /**
     * @return Amount in euros of outstanding items
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @return Item list
     */
    public List<OutstandingItem> getItemList() {
        return itemList;
    }

    /**
     * @return Number of items
     */
    public int getItems() {
        return items;
    }

    /**
     * @return Amount of money in the wallet
     */
    public int getWallet() {
        return wallet;
    }
    
    /**
     * @return Status of the buy orders on the market page
     */
    public List<MarketPageBuyOrder> getMarketPageBuyOrders() {
        return marketPageBuyOrders;
    }
}
