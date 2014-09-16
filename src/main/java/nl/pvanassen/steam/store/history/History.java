package nl.pvanassen.steam.store.history;

import java.util.List;

public class History {
	private final List<Purchase> purchases;
	private final List<Sale> sales;
	private final List<HistoryRow> listingsCreated;
	private final List<HistoryRow> listingsRemoved;

	History(List<Purchase> purchases, List<Sale> sales,
			List<HistoryRow> listingsCreated, List<HistoryRow> listingsRemoved) {
		super();
		this.purchases = purchases;
		this.sales = sales;
		this.listingsCreated = listingsCreated;
		this.listingsRemoved = listingsRemoved;
	}

	/**
	 * @return the purchases
	 */
	public List<Purchase> getPurchases() {
		return purchases;
	}

	/**
	 * @return the sales
	 */
	public List<Sale> getSales() {
		return sales;
	}

	/**
	 * @return the listingsCreated
	 */
	public List<HistoryRow> getListingsCreated() {
		return listingsCreated;
	}

	/**
	 * @return the listingsRemoved
	 */
	public List<HistoryRow> getListingsRemoved() {
		return listingsRemoved;
	}
}
