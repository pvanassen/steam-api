package nl.pvanassen.steam.store;

/**
 * Market history status per item
 * @author Paul van Assen
 *
 */
public enum MarketHistoryStatus {
    /**
     * Purchase of an item
     */
    BOUGHT, 
    /**
     * Listing created
     */
    CREATED, 
    /**
     * Listing removed
     */
    REMOVED, 
    /**
     * Item sold
     */
    SOLD;
}
