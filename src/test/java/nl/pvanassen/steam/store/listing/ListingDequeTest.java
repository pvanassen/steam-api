package nl.pvanassen.steam.store.listing;

import static org.junit.Assert.*;
import nl.pvanassen.steam.store.common.Listing;
import nl.pvanassen.steam.store.listing.ListingDeque;

import org.junit.Test;

public class ListingDequeTest {

	@Test
	public void test() throws InterruptedException {
		ListingDeque deque = new ListingDeque(5000);
		Listing listing1 = new Listing(1, "test", "test-123", "123", 1, 1, 1, 1, 123, 10.0d, "--");
		Listing listing2 = new Listing(12, "test2", "test-123", "123", 12, 12, 12, 12, 1234, 10.0d, "--");
		deque.offerFirst(listing1);
		deque.offerFirst(listing2);
		assertEquals(1, deque.getDeque().size());
		assertEquals(listing1, deque.takeFirst());
		assertEquals(0, deque.getDeque().size());
	}

}
