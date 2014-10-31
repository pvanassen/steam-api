package nl.pvanassen.steam.store.buyorder;

/**
 * Status of a previously made buy order
 * 
 * @author Paul van Assen
 */
public class BuyOrderStatus {
    private final boolean success;
    private final boolean active;
    private final int purchased;
    private final int quantity;
    private final int quantityRemaining;

    BuyOrderStatus(boolean success, boolean active, int purchased, int quantity, int quantityRemaining) {
        super();
        this.success = success;
        this.active = active;
        this.purchased = purchased;
        this.quantity = quantity;
        this.quantityRemaining = quantityRemaining;
    }

    /**
     * @return the purchased
     */
    public int getPurchased() {
        return purchased;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return the quantityRemaining
     */
    public int getQuantityRemaining() {
        return quantityRemaining;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

}
