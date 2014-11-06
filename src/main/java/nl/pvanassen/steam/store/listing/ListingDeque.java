package nl.pvanassen.steam.store.listing;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import nl.pvanassen.steam.store.common.Listing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fifo buffer for listings that will filter out any duplicates.
 * 
 * @author Paul van Assen
 */
public final class ListingDeque implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BlockingDeque<Listing> deque = new LinkedBlockingDeque<>();
    private final Map<String, Long> processedMap = new HashMap<>();
    private final int keepTime;

    /**
     * Constructor with a time to keep items in a map to prevent duplicates
     * 
     * @param keepTime Time in miliseconds
     */
    public ListingDeque(int keepTime) {
        Thread thread = new Thread(this, "ListingDeque-cleanup");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(true);
        thread.start();
        this.keepTime = keepTime;
    }

    List<Listing> getDeque() {
        return new LinkedList<>(deque);
    }

    void offerFirst(Listing listing) {
        logger.info("Adding " + listing.getListingId());
        if (processedMap.containsKey(listing.getListingId())) {
            logger.debug("Listing already known. " + listing.getListingId());
            return;
        }
        deque.offerFirst(listing);
        processedMap.put(listing.getListingId(), System.currentTimeMillis());
        logger.debug("Added " + listing.getListingId());
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(60000);
            }
            catch (InterruptedException e) {
                // System shutdown
                return;
            }
            long cutoff = System.currentTimeMillis() - keepTime;
            Set<String> toRemove = new HashSet<>();
            for (Map.Entry<String, Long> entry : processedMap.entrySet()) {
                if (entry.getValue().longValue() < cutoff) {
                    logger.info("Removing " + entry.getKey());
                    toRemove.add(entry.getKey());
                }
            }
            processedMap.keySet().removeAll(toRemove);
        }

    }

    /**
     * Takes first item
     * 
     * @return The first element in the fifo deque.
     * @throws InterruptedException If during the wait the thread is interupted.
     */
    public Listing takeFirst() throws InterruptedException {
        Listing listing = deque.takeFirst();
        return listing;
    }
}
