package nl.pvanassen.steam.store;


/**
 * Object containing all information for a line on the market page
 * 
 * @author Paul van Assen
 */
public class OverviewItem {

    private final int appId;
    private final String itemName;
    private final String urlName;
    private final int currentOffers;
    private final int currentPrice;
    private final String gameName;
    private final String steamId;

    OverviewItem( int appId, String itemName, String urlName, int currentOffers, int currentPrice, String gameName, String steamId ) {
        super();
        this.appId = appId;
        this.itemName = itemName;
        this.urlName = urlName;
        this.currentOffers = currentOffers;
        this.currentPrice = currentPrice;
        this.gameName = gameName;
        this.steamId = steamId;
    }

    /**
     * @return the appId
     */
    public int getAppId() {
        return appId;
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
     * @return A url for debugging
     */
    public String getUrl() {
        return "http://steamcommunity.com/market/listings/" + appId + "/" + urlName;
    }

    /**
     * @return the urlName
     */
    public String getUrlName() {
        return urlName;
    }


}
