package nl.pvanassen.steam.store.history;

import java.util.Date;

import nl.pvanassen.steam.store.common.Item;

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

    public String getSteamId1() {
        return steamId1;
    }

    public String getSteamId2() {
        return steamId2;
    }
}
