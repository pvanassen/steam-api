package nl.pvanassen.steam.store.history;

/**
 * Market history status per item
 *
 * @author Paul van Assen
 */
enum HistoryStatus {
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
    SOLD
}
