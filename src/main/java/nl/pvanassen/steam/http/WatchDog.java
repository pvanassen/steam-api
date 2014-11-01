package nl.pvanassen.steam.http;

import java.util.Map;

import org.apache.http.client.methods.AbstractExecutionAwareRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WatchDog implements Runnable {
    private final Logger logger = LoggerFactory.getLogger("watchdog");
    private final Map<AbstractExecutionAwareRequest, Long> connectionsToWatch;

    WatchDog(Map<AbstractExecutionAwareRequest, Long> connectionsToWatch) {
        this.connectionsToWatch = connectionsToWatch;
    }

    @Override
    public void run() {
        logger.info("Starting watchdog thread");
        while (true) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                Thread.interrupted();
                return;
            }
            long now = System.currentTimeMillis();
            logger.info(connectionsToWatch.size() + " open connections");
            for (Map.Entry<AbstractExecutionAwareRequest, Long> entry : connectionsToWatch.entrySet()) {
                logger.info("Now: " + now + ", timeout connection: " + entry.getValue() + ", waiting another " + (entry.getValue() - now));
                if (entry.getValue() < now) {
                    logger.warn("Killing " + entry.getValue());
                    entry.getKey().abort();
                }
            }
        }

    }
}