package nl.pvanassen.steam.store;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * A fifo buffer for listings that will filter out any duplicates. 
 * @author Paul van Assen
 *
 */
public class ListingDeque implements Runnable {
	private final BlockingDeque<Listing> deque = new LinkedBlockingDeque<>();
	private final Map<Long,String> processedMap = new HashMap<>();
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
		if (processedMap.values().contains(listing.getListingId())) {
			return;
		}
		processedMap.put(System.currentTimeMillis(), listing.getListingId());
		deque.offerFirst(listing);
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
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				// System shutdown
				return;
			}
			long cutoff = System.currentTimeMillis() - keepTime;
			Iterator<Long> itr = processedMap.keySet().iterator();
			while (itr.hasNext()) {
				Long val = itr.next();
				if (val.longValue() < cutoff) {
					itr.remove();
				}
			}
		}
		
	}
}
