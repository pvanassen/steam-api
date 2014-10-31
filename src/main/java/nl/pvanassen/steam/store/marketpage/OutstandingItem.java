package nl.pvanassen.steam.store.marketpage;

import java.util.Date;

import nl.pvanassen.steam.store.common.Item;

/**
 * Item listed in the market
 * 
 * @author Paul van Assen
 */
public class OutstandingItem extends Item {

    private final String listingId;
    private final String itemId;
    private final int contextId;
    private final int price;
    private final Date listedSince;

    OutstandingItem(int appId, String urlName, String listingId, String itemId, int contextId, int price, Date listedSince) {
        super(appId, urlName);
        this.listingId = listingId;
        this.itemId = itemId;
        this.contextId = contextId;
        this.price = price;
        this.listedSince = listedSince;
    }

    /**
     * @return Context id
     */
    public int getContextId() {
        return contextId;
    }

    /**
     * @return The item id
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @return Listed since date
     */
    public Date getListedSince() {
        return listedSince;
    }

    /**
     * @return The listing ID
     */
    public String getListingId() {
        return listingId;
    }

    /**
     * @return Total price in cents
     */
    public int getPrice() {
        return price;
    }
}
