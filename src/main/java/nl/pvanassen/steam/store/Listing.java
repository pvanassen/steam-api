package nl.pvanassen.steam.store;

import java.util.Date;

/**
 * Listing item 
 * 
 * @author Paul van Assen
 */
public class Listing extends Item {

    private final String listingId;

    private final String steamIdLister;

    private final int subTotal;

    private final int fee;

    private final int steamFee;

    private final int publisherFee;

    private final int publisherFeeApp;

    private final double publisherFeePercent;

    private final Date createdDate = new Date();

    Listing(int appId, String urlName, String listingId, String steamIdLister, int subTotal, int fee,
            int steamFee, int publisherFee, int publisherFeeApp, double publisherFeePercent) {
        super(appId, urlName);
        this.listingId = listingId;
        this.steamIdLister = steamIdLister;
        this.subTotal = subTotal;
        this.fee = fee;
        this.steamFee = steamFee;
        this.publisherFee = publisherFee;
        this.publisherFeeApp = publisherFeeApp;
        this.publisherFeePercent = publisherFeePercent;
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
     * @return the steamIdLister
     */
    public String getSteamIdLister() {
        return steamIdLister;
    }

    /**
     * @return the subTotal
     */
    public int getSubTotal() {
        return subTotal;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Listing [appId=" + getAppId() + ", urlName=" + getUrlName() + ", listingId=" + listingId +
               ", steamIdLister=" + steamIdLister + ", subTotal=" + subTotal + ", fee=" + fee + ", steamFee=" +
               steamFee + ", publisherFee=" + publisherFee + ", publisherFeeApp=" + publisherFeeApp +
               ", publisherFeePercent=" + publisherFeePercent + "]";
    }

    /**
     * @return Date when this item was created
     */
    public Date getCreatedDate() {
        return createdDate;
    }
}
