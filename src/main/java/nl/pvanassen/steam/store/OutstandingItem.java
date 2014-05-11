package nl.pvanassen.steam.store;

import java.util.Date;

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

    public String getListingId() {
        return listingId;
    }

    public String getItemId() {
        return itemId;
    }

    public int getContextId() {
        return contextId;
    }

    public int getPrice() {
        return price;
    }

    public Date getListedSince() {
		return listedSince;
	}
}
