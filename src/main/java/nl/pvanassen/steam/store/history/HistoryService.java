package nl.pvanassen.steam.store.history;

import java.util.List;

/**
 * History service for retrieving purchase and selling history
 * 
 * @author Paul van Assen
 *
 */
public interface HistoryService {
	/**
	 * Get sold items currently offered
	 * 
	 * @return a list of market history items
	 */
	List<History> getSoldItemsFromHistory();
}
