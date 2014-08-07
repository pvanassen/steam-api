package nl.pvanassen.steam.store.listing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import nl.pvanassen.steam.store.common.Listing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fifo buffer for listings that will filter out any duplicates. 
 * @author Paul van Assen
 *
 */
public class ListingDeque implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final BlockingDeque<Listing> deque = new LinkedBlockingDeque<>();
	private final Map<String,Long> processedMap = new HashMap<>();
	private final int keepTime;
	
	/**
	 * Constructor with a time to keep items in a map to prevent duplicates
	 * @param keepTime Time in miliseconds
	 */
	public ListingDeque(int keepTime) {
		Thread thread = new Thread(this, "ListingDeque-cleanup");
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.setDaemon(true);
		thread.start();
		this.keepTime = keepTime;
	}
	
	void offerFirst(Listing listing) {
		logger.info("Adding " + listing.getListingId());
		if (processedMap.containsKey(listing.getListingId())) {
			logger.info("Listing already known. " + listing.getListingId());
			return;
		}
		processedMap.put(listing.getListingId(), System.currentTimeMillis());
		deque.offerFirst(listing);
		logger.info("Added " + listing.getListingId());
	}
	
	/**
	 * Takes first item
	 * @return The first element in the fifo deque. 
	 * @throws InterruptedException If during the wait the thread is interupted. 
	 */
	public Listing takeFirst() throws InterruptedException {
		Listing listing = deque.takeFirst();
		return listing;
	}
	
	List<Listing> getDeque() {
		return new LinkedList<>(deque);
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
			for(Map.Entry<String,Long> entry : processedMap.entrySet()) {
				if (entry.getValue().longValue() < cutoff) {
					logger.info("Removing " + entry.getKey());
					toRemove.add(entry.getKey());
				}
			}
			processedMap.keySet().removeAll(toRemove);
		}
		
	}
}
