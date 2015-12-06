package nl.pvanassen.steam.store.history;

import nl.pvanassen.steam.http.Http;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Steam implementation
 *
 * @author Paul van Assen
 */
public class SteamHistoryService implements HistoryService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Http http;

    /**
     * @param http For mocking
     */
    public SteamHistoryService(Http http) {
        this.http = http;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.history.HistoryService#getHistory(java.lang.String)
     */
    @Override
    public History getHistory(String lastSteamId) {
        long startTime = System.currentTimeMillis();
        OptimumStepSize optimumStepSize = new OptimumStepSize();
        logger.info("Getting history, up to " + lastSteamId);
        HistoryHandle handle = new HistoryHandle(lastSteamId, objectMapper);
        try {
            logger.info("Getting some data");
            http.get("http://steamcommunity.com/market/myhistory/render/?query=&search_descriptions=0&start=0&count=1", handle, false);
            if (handle.isExceptionThrown()) {
                logger.error("IO error");
                return getHistory(lastSteamId);
            }
            int stepSize = optimumStepSize.getStepSize();
            if (handle.isError()) {
                logger.error("Error in handle");
                return getHistory(lastSteamId);
            }
            // Added extra margin
            int totalCount = handle.getTotalCount() + (stepSize / 2);
            logger.info("Need to get a total of " + totalCount);
            boolean error;
            for (int start = 0; start <= totalCount; start += stepSize) {
                if (start < 0) {
                    start = 0;
                }
                do {
                    stepSize = optimumStepSize.getStepSize();
                    error = false;
                    logger.info("Getting from " + start + ", with stepsize " + stepSize);
                    http.get("http://steamcommunity.com/market/myhistory/render/?query=&search_descriptions=0&count=" + stepSize + "&start=" + start, handle, false);
                    if (handle.isExceptionThrown()) {
                        optimumStepSize.error();
                        logger.error("IO Exception. Retrying");
                        error = true;
                    } else if (handle.isFoundRowId()) {
                        return handle.getHistory();
                    } else if (handle.isError()) {
                        optimumStepSize.error();
                    } else {
                        optimumStepSize.success();
                    }
                    long timePast = System.currentTimeMillis() - startTime;
                    double itemsPerTime = (totalCount - start) / (double) timePast;
                    logger.info("Doing " + itemsPerTime + " per " + timePast + ", expected " + (itemsPerTime * start));
                } while (handle.isError() || error);
            }
        } catch (RuntimeException e) {
            logger.error("Error getting data", e);
        }
        return handle.getHistory();
    }
}
