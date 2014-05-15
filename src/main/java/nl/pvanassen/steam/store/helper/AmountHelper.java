package nl.pvanassen.steam.store.helper;

/**
 * Amount helper
 * 
 * @author Paul van Assen
 */
public final class AmountHelper {
    private AmountHelper() {
        super();
    }

    /**
     * Convert the html amount string to the amount in cents
     * 
     * @param html Html text
     * @return Amount in cents
     */
    public static int getAmount(String html) {
        return Integer.parseInt(html.replace("&#8364;", "").replace(",", "").replace("-", "0").trim());
    }
}
