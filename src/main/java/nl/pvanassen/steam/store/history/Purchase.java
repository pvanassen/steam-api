package nl.pvanassen.steam.store.history;

import java.util.Date;

import nl.pvanassen.steam.store.common.Item;

public class Purchase extends HistoryRow {
    private final Item item;
    private final int contextId;
    private final String seller;
    private final String steamId1;
    private final String steamId2;

    Purchase(String rowName, int appId, String urlName, int contextId, Date listed, Date acted, int price, String seller) {
        super(rowName, listed, acted, price);
        item = new HistoryItem(appId, urlName);
        this.contextId = contextId;
        this.seller = seller;
        String idString = rowName.substring("history_row_".length());
        String[] ids = idString.split("_");
        steamId1 = ids[0];
        steamId2 = ids[1];
    }

    /*
     * (non-Javadoc)
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
        if (!(obj instanceof Purchase)) {
            return false;
        }
        Purchase other = (Purchase) obj;
        if (contextId != other.contextId) {
            return false;
        }
        if (item == null) {
            if (other.item != null) {
                return false;
            }
        }
        else if (!item.equals(other.item)) {
            return false;
        }
        if (seller == null) {
            if (other.seller != null) {
                return false;
            }
        }
        else if (!seller.equals(other.seller)) {
            return false;
        }
        return true;
    }

    /**
     * @return the contextId
     */
    public int getContextId() {
        return contextId;
    }

    /**
     * @return the item
     */
    public Item getItem() {
        return item;
    }

    /**
     * @return the seller
     */
    public String getSeller() {
        return seller;
    }

    public String getSteamId1() {
        return steamId1;
    }

    public String getSteamId2() {
        return steamId2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + contextId;
        result = (prime * result) + ((item == null) ? 0 : item.hashCode());
        result = (prime * result) + ((seller == null) ? 0 : seller.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Purchase [item=" + item + ", contextId=" + contextId + ", seller=" + seller + ", getRowName()=" + getRowName() + ", getListed()=" + getListed() + ", getActed()="
                + getActed() + ", getPrice()=" + getPrice() + "]";
    }

}
