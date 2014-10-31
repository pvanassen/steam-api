package nl.pvanassen.steam.store.history;

/**
 * History service for retrieving purchase and selling history
 *
 * @author Paul van Assen
 */
public interface HistoryService {
    /**
     * Get trading history
     * 
     * @param lastSteamId The ID of the last row processed in previous runs.
     *            Leave null if this is the first run
     * @return The trading history for this account
     */
    History getHistory(String lastSteamId);
}
