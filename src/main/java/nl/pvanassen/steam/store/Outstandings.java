package nl.pvanassen.steam.store;

/**
 * Outstanding items
 * 
 * @author Paul van Assen
 *
 */
public class Outstandings {
    private final int items;
    private final int amount;
    
    Outstandings(int items, int amount) {
        this.items = items;
        this.amount = amount;
    }
   
    /**
     * @return Amount in euros of outstanding items
     */
    public int getAmount() {
        return amount;
    }
    
    /**
     * @return Number of items
     */
    public int getItems() {
        return items;
    }
}
