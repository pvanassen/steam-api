package nl.pvanassen.steam.store.marketpage;

import nl.pvanassen.steam.store.common.Item;

/**
 * Buy orders on the market place
 * @author Paul van Assen
 *
 */
public class MarketPageBuyOrder extends Item{
    private final String steamId;
    private final int quantity;
    private final int price;
    
    MarketPageBuyOrder(int appId, String urlName, String steamId, int quantity, int price) {
        super(appId, urlName);
        this.steamId = steamId;
        this.quantity = quantity;
        this.price = price;
    }
    
    /**
     * @return ID used by steam for this buy order
     */
    public String getSteamId() {
        return steamId;
    }

    /**
     * @return Quantity left to buy
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return Price paying
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
        result = prime * result + price;
        result = prime * result + quantity;
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
        if (!(obj instanceof MarketPageBuyOrder)) {
            return false;
        }
        MarketPageBuyOrder other = (MarketPageBuyOrder) obj;
        if (price != other.price) {
            return false;
        }
        if (quantity != other.quantity) {
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
        return "MarketPageBuyOrder [steamId=" + steamId + ", quantity=" + quantity + ", price=" + price + "]";
    }
    
    
}
