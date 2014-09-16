package nl.pvanassen.steam.store.history;

import java.util.Date;

import nl.pvanassen.steam.store.common.Item;

public class Sale extends HistoryRow {
	private final Item item;
	private final int contextId;
	private final String buyer;

	Sale(String steamId, int appId, String urlName, int contextId, Date listed,
			Date acted, int price, String buyer) {
		super(steamId, listed, acted, price);
		this.item = new HistoryItem(appId, urlName);
		this.contextId = contextId;
		this.buyer = buyer;
	}

	/**
	 * @return the contextId
	 */
	public int getContextId() {
		return contextId;
	}

	/**
	 * @return the seller
	 */
	public String getBuyer() {
		return buyer;
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}
}
