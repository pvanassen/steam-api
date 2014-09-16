package nl.pvanassen.steam.store.history;

import java.io.IOException;

import nl.pvanassen.steam.http.Http;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Steam implementation
 * 
 * @author Paul van Assen
 *
 */
public class SteamHistoryService implements HistoryService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Http http;

	public SteamHistoryService(String cookies, String username) {
		this(Http.getInstance(cookies, username));
	}

	/**
	 * @param http
	 *            For mocking
	 */
	public SteamHistoryService(Http http) {
		this.http = http;
	}
	
	@Override
	public History getHistory(String lastSteamId) {
		HistoryHandle handle = new HistoryHandle(lastSteamId, objectMapper);
		try {
			int stepSize = 100;
			http.get(
					"http://steamcommunity.com/market/myhistory/render/?query=&search_descriptions=0&start=0&count="
							+ stepSize, handle);
			if (handle.isError()) {
				return getHistory(lastSteamId);
			}
			int totalCount = handle.getTotalCount();
			for (int start = stepSize; start < totalCount; start += stepSize) {
				do {
					Thread.sleep(2000);
					http.get(
							"http://steamcommunity.com/market/myhistory/render/?query=&search_descriptions=0&count="
									+ stepSize + "&start=" + start, handle);
				} while (handle.isError());
			}
		} catch (IOException | RuntimeException | InterruptedException e) {
			logger.error("Error getting data", e);
		}
		return new History(handle.getPurchases(), handle.getSales(), handle.getListingsCreated(), handle.getListingsRemoved());
	}
}
