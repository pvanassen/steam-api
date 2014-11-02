package nl.pvanassen.steam.store.common;

/**
 * Buy order class
 * 
 * @author Paul van Assen
 */
public class BuyOrder extends Item {
    private final String listingId;
    private final int subTotal;
    private final int fee;

    /**
     * Construct a buy order
     * 
     * @param appId The appId to purchase
     * @param urlName The url name of the item to purchase
     * @param listingId Listing to purchase
     * @param subTotal Price without fees
     * @param fee Fees that need to be paid.
     */
    public BuyOrder(int appId, String urlName, String listingId, int subTotal, int fee) {
        super(appId, urlName);
        this.listingId = listingId;
        this.subTotal = subTotal;
        this.fee = fee;
    }

    /**
     * @return The fees to pay
     */
    public int getFee() {
        return fee;
    }

    /**
     * @return The listing id
     */
    public String getListingId() {
        return listingId;
    }

    /**
     * @return The price without fee
     */
    public int getSubTotal() {
        return subTotal;
    }
}
