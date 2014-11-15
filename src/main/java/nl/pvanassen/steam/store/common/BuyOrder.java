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

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + fee;
        result = prime * result + ((listingId == null) ? 0 : listingId.hashCode());
        result = prime * result + subTotal;
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof BuyOrder)) {
            return false;
        }
        BuyOrder other = (BuyOrder) obj;
        if (fee != other.fee) {
            return false;
        }
        if (listingId == null) {
            if (other.listingId != null) {
                return false;
            }
        }
        else if (!listingId.equals(other.listingId)) {
            return false;
        }
        if (subTotal != other.subTotal) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BuyOrder [listingId=" + listingId + ", subTotal=" + subTotal + ", fee=" + fee + "]";
    }
}
