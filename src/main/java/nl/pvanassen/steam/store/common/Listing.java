package nl.pvanassen.steam.store.common;

import java.util.Date;

/**
 * Listing item 
 *
 * @author Paul van Assen
 */
public class Listing extends Item {

    private final String listingId;

    private final int subTotal;

    private final int fee;

    private final int steamFee;

    private final int publisherFee;

    private final int publisherFeeApp;

    private final double publisherFeePercent;

    private final String country;

    private final Date createdDate = new Date();

    /**
     * Listing constructor
     * @param appId App id
     * @param urlName Url name
     * @param listingId Listing id (or steam id)
     * @param subTotal Sub total of the item
     * @param fee Total fee to pay
     * @param steamFee Fee for steam
     * @param publisherFee Fee for the publisher
     * @param publisherFeeApp Who is the publisher
     * @param publisherFeePercent Publisher fee percentage
     * @param country Country of the listing, if known
     */
    public Listing(int appId, String urlName, String listingId, int subTotal, int fee, int steamFee, int publisherFee, int publisherFeeApp, double publisherFeePercent,
            String country) {
        super(appId, urlName);
        this.listingId = listingId;
        this.subTotal = subTotal;
        this.fee = fee;
        this.steamFee = steamFee;
        this.publisherFee = publisherFee;
        this.publisherFeeApp = publisherFeeApp;
        this.publisherFeePercent = publisherFeePercent;
        this.country = country;
    }

    /**
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
        if (!(obj instanceof Listing)) {
            return false;
        }
        Listing other = (Listing) obj;
        if (listingId == null) {
            if (other.listingId != null) {
                return false;
            }
        }
        else if (!listingId.equals(other.listingId)) {
            return false;
        }
        return true;
    }

    /**
     * @return Country string for this listing
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return Date when this item was created
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @return the fee
     */
    public int getFee() {
        return fee;
    }

    /**
     * @return the listingId
     */
    public String getListingId() {
        return listingId;
    }

    /**
     * @return the publisherFee
     */
    public int getPublisherFee() {
        return publisherFee;
    }

    /**
     * @return the publisherFeeApp
     */
    public int getPublisherFeeApp() {
        return publisherFeeApp;
    }

    /**
     * @return the publisherFeePercent
     */
    public double getPublisherFeePercent() {
        return publisherFeePercent;
    }

    /**
     * @return the steamFee
     */
    public int getSteamFee() {
        return steamFee;
    }

    /**
     * @return the subTotal
     */
    public int getSubTotal() {
        return subTotal;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((listingId == null) ? 0 : listingId.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Listing [appId=" + getAppId() + ", urlName=" + getUrlName() + ", listingId=" + listingId + ", subTotal=" + subTotal + ", fee=" + fee + ", steamFee=" + steamFee
                + ", publisherFee=" + publisherFee + ", publisherFeeApp=" + publisherFeeApp + ", publisherFeePercent=" + publisherFeePercent + ", country=" + country + "]";
    }
}
