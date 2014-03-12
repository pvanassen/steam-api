package nl.pvanassen.steam.store;

/**
 * Result of a purchase attempt
 * 
 * @author Paul van Assen
 */
public class BuyResult {

    private final boolean success;
    private final int wallet;

    BuyResult( boolean success, int wallet ) {
        super();
        this.success = success;
        this.wallet = wallet;
    }

    /**
     * @return the wallet
     */
    public int getWallet() {
        return wallet;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BuyResult [success=" + success + ", wallet=" + wallet + "]";
    }

}
