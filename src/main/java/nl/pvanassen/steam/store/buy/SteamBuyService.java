package nl.pvanassen.steam.store.buy;

import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.store.CookieException;
import nl.pvanassen.steam.store.common.BuyOrder;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for buying items on steam
 * 
 * @author Paul van Assen
 */
public class SteamBuyService implements BuyService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Http http;
    private final String username;

    /**
     * @param http For mocking
     * @param username The username
     */
    public SteamBuyService(Http http, String username) {
        this.http = http;
        this.username = username;
    }

    @Override
    public BuyResult buy(BuyOrder buyOrder) {
        int fee = buyOrder.getFee();
        int subTotal = buyOrder.getSubTotal();
        String listingId = buyOrder.getListingId();
        Map<String, String> params = new HashMap<>();
        params.put("currency", "3");
        params.put("fee", Integer.toString(fee));
        params.put("subtotal", Integer.toString(subTotal));
        params.put("total", Integer.toString(fee + subTotal));
        params.put("quantity", "1");
        try {
            BuyHandle handle = new BuyHandle(objectMapper);
            http.post("https://steamcommunity.com/market/buylisting/" + listingId, params, handle, "http://steamcommunity.com/market/listing/" + buyOrder.getAppId() + "/" + buyOrder.getUrlName(), true, false);
            if ((handle.getMessage() != null) && handle.getMessage().contains("temporary")) {
                return buy(buyOrder);
            }
            if ((handle.getMessage() != null) && handle.getMessage().contains("Cookies")) {
                logger.error("Cookie issue.");
                throw new CookieException();
            }
            return new BuyResult(!handle.isError(), handle.getWallet(), handle.getMessage());
        }
        catch (IOException e) {
            logger.error("Error posting data", e);
            return new BuyResult(false, 0, "");
        }
    }
}
