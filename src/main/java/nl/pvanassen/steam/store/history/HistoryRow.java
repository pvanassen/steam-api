package nl.pvanassen.steam.store.history;

import java.util.Date;

public class HistoryRow {
	private final String steamId;
	private final Date listed;
	private final Date acted;
	private final int price;
	
	HistoryRow(String steamId, Date listed, Date acted, int price) {
		super();
		this.steamId = steamId;
		this.listed = listed;
		this.acted = acted;
		this.price = price;
	}
	/**
	 * @return the steamId
	 */
	public final String getSteamId() {
		return steamId;
	}
	/**
	 * @return the listed
	 */
	public final Date getListed() {
		return listed;
	}
	/**
	 * @return the acted
	 */
	public final Date getActed() {
		return acted;
	}
	/**
	 * @return the price
	 */
	public final int getPrice() {
		return price;
	}

}
