package nl.pvanassen.steam.store.history;

import java.util.Date;

/**
 * Class for tracking lising created/removed
 *
 * @author Paul van Assen
 */
public class HistoryPart {
    private final String steamId;
    private final String name;
    private final String gameName;
    private final Date listed;
    private final Date acted;
    private final int price;
    private final HistoryStatus status;

    HistoryPart(String steamId, String name, String gameName, Date listed, Date acted, int price, HistoryStatus status) {
        super();
        this.steamId = steamId;
        this.name = name;
        this.gameName = gameName;
        this.listed = listed;
        this.acted = acted;
        this.price = price;
        this.status = status;
    }

    /**
     * @return the acted
     */
    public Date getActed() {
        return acted;
    }

    /**
     * @return the gameName
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * @return the listed
     */
    public Date getListed() {
        return listed;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the price
     */
    public int getPrice() {
        return price;
    }

    /**
     * @return the status
     */
    public HistoryStatus getStatus() {
        return status;
    }

    /**
     * @return the steamId
     */
    public String getSteamId() {
        return steamId;
    }

}
