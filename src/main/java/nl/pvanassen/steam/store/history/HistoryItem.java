package nl.pvanassen.steam.store.history;

import nl.pvanassen.steam.store.common.Item;

/**
 * An item of history
 * 
 * @author Paul van Assen
 */
public class HistoryItem extends Item {
    /**
     * Base class for history items
     * @param appId App id
     * @param urlName Url name
     */
    protected HistoryItem(int appId, String urlName) {
        super(appId, urlName);
    }
}
