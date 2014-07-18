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

    OverviewItem(int appId, String itemName, String urlName, int currentOffers, int currentPrice, String gameName,
            String steamId) {
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
}
