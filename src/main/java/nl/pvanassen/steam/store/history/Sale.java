package nl.pvanassen.steam.store.history;

import nl.pvanassen.steam.store.common.Item;

import java.util.Date;

/**
 * Represents a sales row in the steam history
 * 
 * @author Paul van Assen
 */
public class Sale extends HistoryRow {
    private final Item item;
    private final int contextId;
    private final String buyer;
    private final String steamId1;
    private final String steamId2;

    Sale(String rowName, int appId, String urlName, int contextId, Date listed, Date acted, int price, String buyer) {
        super(rowName, listed, acted, price);
        item = new HistoryItem(appId, urlName);
        this.contextId = contextId;
        this.buyer = buyer;
        String idString = rowName.substring("history_row_".length());
        String[] ids = idString.split("_");
        steamId1 = ids[0];
        steamId2 = ids[1];
    }

    /**
     * @return the seller
     */
    public String getBuyer() {
        return buyer;
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
     * @return First part of the ID
     */
    public String getSteamId1() {
        return steamId1;
    }

    /**
     * @return Second part of the ID
     */
    public String getSteamId2() {
        return steamId2;
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
        result = prime * result + ((buyer == null) ? 0 : buyer.hashCode());
        result = prime * result + contextId;
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        result = prime * result + ((steamId1 == null) ? 0 : steamId1.hashCode());
        result = prime * result + ((steamId2 == null) ? 0 : steamId2.hashCode());
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
        if (!(obj instanceof Sale)) {
            return false;
        }
        Sale other = (Sale) obj;
        if (buyer == null) {
            if (other.buyer != null) {
                return false;
            }
        }
        else if (!buyer.equals(other.buyer)) {
            return false;
        }
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
        if (steamId1 == null) {
            if (other.steamId1 != null) {
                return false;
            }
        }
        else if (!steamId1.equals(other.steamId1)) {
            return false;
        }
        if (steamId2 == null) {
            if (other.steamId2 != null) {
                return false;
            }
        }
        else if (!steamId2.equals(other.steamId2)) {
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
        return "Sale [item=" + item + ", contextId=" + contextId + ", buyer=" + buyer + ", steamId1=" + steamId1 + ", steamId2=" + steamId2 + "]";
    }
}
