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
    private final String assetId;
    private final int contextId;
    private final int price;
    private final Date listedSince;

    OutstandingItem(int appId, String urlName, String listingId, String assetId, int contextId, int price, Date listedSince) {
        super(appId, urlName);
        this.listingId = listingId;
        this.assetId = assetId;
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
     * @return The asset id
     */
    public String getAssetId() {
        return assetId;
    }

    /**
     * @return Listed since date
     */
    public Date getListedSince() {
        return new Date(listedSince.getTime());
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

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + contextId;
        result = prime * result + ((assetId == null) ? 0 : assetId.hashCode());
        result = prime * result + ((listedSince == null) ? 0 : listedSince.hashCode());
        result = prime * result + ((listingId == null) ? 0 : listingId.hashCode());
        result = prime * result + price;
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
        if (!(obj instanceof OutstandingItem)) {
            return false;
        }
        OutstandingItem other = (OutstandingItem) obj;
        if (contextId != other.contextId) {
            return false;
        }
        if (assetId == null) {
            if (other.assetId != null) {
                return false;
            }
        }
        else if (!assetId.equals(other.assetId)) {
            return false;
        }
        if (listedSince == null) {
            if (other.listedSince != null) {
                return false;
            }
        }
        else if (!listedSince.equals(other.listedSince)) {
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
        if (price != other.price) {
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
        return "OutstandingItem [listingId=" + listingId + ", itemId=" + assetId + ", contextId=" + contextId + ", price=" + price + ", listedSince=" + listedSince + "]";
    }
}
