package nl.pvanassen.steam.store.item;

import nl.pvanassen.steam.store.common.Item;

/**
 * Object containing all information for a line on the market page
 *
 * @author Paul van Assen
 */
public class OverviewItem extends Item {

    private final String itemName;
    private final int currentOffers;
    private final int currentPrice;
    private final String gameName;
    private final String steamId;
    
    OverviewItem(int appId, String itemName, String urlName, int currentOffers, int currentPrice, String gameName, String steamId) {
        super(appId, urlName);
        this.itemName = itemName;
        this.currentOffers = currentOffers;
        this.currentPrice = currentPrice;
        this.gameName = gameName;
        this.steamId = steamId;
    }

    /**
     * @return the currentOffers
     */
    public int getCurrentOffers() {
        return currentOffers;
    }

    /**
     * @return the currentPrice
     */
    public int getCurrentPrice() {
        return currentPrice;
    }

    /**
     * @return the gameName
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @return the steamId
     */
    public String getSteamId() {
        return steamId;
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
        result = prime * result + currentOffers;
        result = prime * result + currentPrice;
        result = prime * result + ((gameName == null) ? 0 : gameName.hashCode());
        result = prime * result + ((itemName == null) ? 0 : itemName.hashCode());
        result = prime * result + ((steamId == null) ? 0 : steamId.hashCode());
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
        if (!(obj instanceof OverviewItem)) {
            return false;
        }
        OverviewItem other = (OverviewItem) obj;
        if (currentOffers != other.currentOffers) {
            return false;
        }
        if (currentPrice != other.currentPrice) {
            return false;
        }
        if (gameName == null) {
            if (other.gameName != null) {
                return false;
            }
        }
        else if (!gameName.equals(other.gameName)) {
            return false;
        }
        if (itemName == null) {
            if (other.itemName != null) {
                return false;
            }
        }
        else if (!itemName.equals(other.itemName)) {
            return false;
        }
        if (steamId == null) {
            if (other.steamId != null) {
                return false;
            }
        }
        else if (!steamId.equals(other.steamId)) {
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
        return "OverviewItem [itemName=" + itemName + ", currentOffers=" + currentOffers + ", currentPrice=" + currentPrice + ", gameName=" + gameName + ", steamId=" + steamId
                + "]";
    }
}
