package nl.pvanassen.steam.store.history;

import java.util.Date;

class ListingRemoved extends HistoryRow {
	private final String steamId1;

	ListingRemoved(String rowName, Date listed, Date acted, int price) {
		super(rowName, listed, acted, price);
		steamId1 = rowName.substring("history_row_".length());
	}
	
	String getSteamId1() {
		return steamId1;
	}
}
