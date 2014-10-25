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
	    long startTime = System.currentTimeMillis();
	    OptimumStepSize optimumStepSize = new OptimumStepSize();
		logger.info("Getting history, up to " + lastSteamId);
		HistoryHandle handle = new HistoryHandle(lastSteamId, objectMapper);
		try {
			logger.info("Getting some data");
			try {
				http.get("http://steamcommunity.com/market/myhistory/render/?query=&search_descriptions=0&start=0&count=1", handle);
			}
			catch (IOException e) {
			    logger.error("IO error", e);
				return getHistory(lastSteamId);
			}
			int stepSize = 10;
			if (handle.isError()) {
			    logger.error("Error in handle");
				return getHistory(lastSteamId);
			}
			// Added extra margin
			int totalCount = handle.getTotalCount() + (stepSize / 2);
			logger.info("Need to get a total of " + totalCount);
			boolean error;
			for (int start = 0; start <= totalCount; start += stepSize) {
				do {
					if (start < 0) {
						start = 0;
					}
					stepSize = optimumStepSize.getStepSize();
					error = false;
					logger.info("Getting from " + start + ", with stepsize " + stepSize);
					try {
						http.get(
								"http://steamcommunity.com/market/myhistory/render/?query=&search_descriptions=0&count="
										+ stepSize + "&start=" + start, handle);
						if (handle.isFoundRowId()) {
							return handle.getHistory();
						}
						if (handle.isError()) {
	                        optimumStepSize.error();
						}
						else {
						    optimumStepSize.success();
						}
					}
					catch (IOException e) {
					    optimumStepSize.error();
					    logger.error("IO Exception. Retrying");
					    Thread.sleep(500);
						error = true;
					}
					long timePast = System.currentTimeMillis() - startTime;
					double itemsPerTime = (totalCount - start) / (double)timePast;
					logger.info("Doing " + itemsPerTime + " per " + timePast + ", expected " + (itemsPerTime * start));
				} while (handle.isError() || error);
			}
		} catch (RuntimeException | InterruptedException e) {
			logger.error("Error getting data", e);
		}
		return handle.getHistory();
	}
}
