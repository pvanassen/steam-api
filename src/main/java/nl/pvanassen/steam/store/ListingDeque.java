package nl.pvanassen.steam.store;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ListingDeque implements Runnable {
	private final BlockingDeque<Listing> deque = new LinkedBlockingDeque<>();
	private final Map<Long,String> processedMap = new HashMap<>();
	
	public ListingDeque() {
		Thread thread = new Thread(this, "ListingDeque-cleanup");
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.setDaemon(true);
		thread.start();
	}
	
	void offerFirst(Listing listing) {
		if (processedMap.values().contains(listing.getListingId())) {
			return;
		}
		processedMap.put(System.currentTimeMillis(), listing.getListingId());
		deque.offerFirst(listing);
	}
	
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
			long cutoff = System.currentTimeMillis() - 60000;
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
