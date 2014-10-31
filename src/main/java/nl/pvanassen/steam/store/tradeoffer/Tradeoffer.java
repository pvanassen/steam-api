package nl.pvanassen.steam.store.tradeoffer;

/**
 * Trade offer class representing a trade offer
 * 
 * @author Paul van Assen
 */
public class Tradeoffer {
    private final String partnerId;
    private final String offerId;
    private final String comment;

    Tradeoffer(String partnerId, String offerId, String comment) {
        super();
        this.partnerId = partnerId;
        this.offerId = offerId;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String getOfferId() {
        return offerId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    @Override
    public String toString() {
        return "Tradeoffer [partnerId=" + partnerId + ", offerId=" + offerId + ", comment=" + comment + "]";
    }
}
