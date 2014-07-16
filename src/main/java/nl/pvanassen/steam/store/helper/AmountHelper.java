package nl.pvanassen.steam.store.helper;

import com.google.common.cache.*;

/**
 * Amount helper
 * 
 * @author Paul van Assen
 */
public final class AmountHelper {
    private static final LoadingCache<String,Integer> fastAmountCache = CacheBuilder.newBuilder().concurrencyLevel(32).maximumSize(4096).build(new CacheLoader<String,Integer>() {
       @Override
        public Integer load(String html) throws Exception {
    	   String amount = html;
    	   int idx = html.indexOf('(');
    	   if (idx != -1) {
    		   amount = html.substring(idx+1, html.indexOf(')')-1);
    	   }
           return Integer.parseInt(amount.replace("&#8364;", "").replace("€", "").replace(",", "").replace("-", "0").trim());
        } 
    });
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
        return fastAmountCache.getUnchecked(html);
    }
}
