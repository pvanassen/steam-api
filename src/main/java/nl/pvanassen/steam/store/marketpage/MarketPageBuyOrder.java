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

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }
    
    
}
