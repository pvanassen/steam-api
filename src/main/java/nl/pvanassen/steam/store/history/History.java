package nl.pvanassen.steam.store.history;

import java.util.List;

public class History {
    private final List<Purchase> purchases;
    private final List<Sale> sales;
    private final List<ListingCreated> listingsCreated;
    private final List<ListingRemoved> listingsRemoved;
    private final String latestRowId;

    History(List<Purchase> purchases, List<Sale> sales, List<ListingCreated> listingsCreated, List<ListingRemoved> listingsRemoved, String latestRowId) {
        super();
        this.purchases = purchases;
        this.sales = sales;
        this.listingsCreated = listingsCreated;
        this.listingsRemoved = listingsRemoved;
        this.latestRowId = latestRowId;
    }

    /**
     * @return The most recent row id parsed
     */
    public String getLatestRowId() {
        return latestRowId;
    }

    /**
     * @return the listingsCreated
     */
    public List<ListingCreated> getListingsCreated() {
        return listingsCreated;
    }

    /**
     * @return the listingsRemoved
     */
    public List<ListingRemoved> getListingsRemoved() {
        return listingsRemoved;
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
}
