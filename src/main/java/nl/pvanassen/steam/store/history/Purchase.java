package nl.pvanassen.steam.store.history;

import java.util.Date;

import nl.pvanassen.steam.store.common.Item;

public class Purchase extends HistoryRow {
	private final Item item;
	private final int contextId;
	private final String seller;

	Purchase(String steamId, int appId, String urlName, int contextId,
			Date listed, Date acted, int price, String seller) {
		super(steamId, listed, acted, price);
		this.item = new HistoryItem(appId, urlName);
		this.contextId = contextId;
		this.seller = seller;
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
	public String getSeller() {
		return seller;
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

}
