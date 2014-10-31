package nl.pvanassen.steam.store.history;

import nl.pvanassen.steam.store.common.Item;

/**
 * An item of history
 * 
 * @author Paul van Assen
 */
public class HistoryItem extends Item {
    HistoryItem(int appId, String urlName) {
        super(appId, urlName);
    }
}
