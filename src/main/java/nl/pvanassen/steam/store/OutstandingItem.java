package nl.pvanassen.steam.store;

public class OutstandingItem extends Item {

    private final String listingId;
    private final String itemId;
    private final int contextId;
    private final int price;

    OutstandingItem(int appId, String urlName, String listingId, String itemId, int contextId, int price) {
        super(appId, urlName);
        this.listingId = listingId;
        this.itemId = itemId;
        this.contextId = contextId;
        this.price = price;
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

}
