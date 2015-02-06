package nl.pvanassen.steam.store.item;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Random;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler to parse the listing page
 *
 * @author Paul van Assen
 */
public class ListingPageScriptHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Charset charset = Charset.forName("UTF-8");
    private final ObjectMapper om;
    private JsonNode listingInfo;
    private JsonNode priceHistoryInfo;
    private boolean error;
    private boolean buyOrders;
    private boolean immediateSale;
    private boolean noListingForThisItem;
    private boolean noPricingHistoryForThisItem;
    private boolean noLongerSold;
    
    ListingPageScriptHandle(ObjectMapper om) {
        this.om = om;
        error = false;
        buyOrders = false;
        immediateSale = true;
    }

    JsonNode getListingInfo() {
        return listingInfo;
    }

    JsonNode getPriceHistoryInfo() {
        return priceHistoryInfo;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
        boolean listingFound = false;
        boolean salesFound = false;
        boolean buyOrdersFound = false;
        boolean immediateSaleFound = false;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (logger.isDebugEnabled()) {
                stringBuilder.append(line).append('\n');
            }
            if (!buyOrdersFound) {
                if (line.indexOf("market_commodity_order_block") > -1) {
                    buyOrders = true;
                    buyOrdersFound = true;
                }
            }
            if (line.indexOf("There are no listings for this item") > -1) {
                noListingForThisItem = true;
                return;
            }
            if (line.indexOf("There is no price history available") > -1) {
                noPricingHistoryForThisItem = true;
                return;
            }
            if (line.indexOf("This item may no longer be bought or sold on the market") > -1) {
                noLongerSold = true;
                return;
            }
            if (!immediateSaleFound) {
                if (line.indexOf("either in-game or on the Steam Community Market") > -1) {
                    immediateSaleFound = true;
                    immediateSale = line.indexOf("Note: Items purchased for") == -1;
                }
            }
            if (!listingFound) {
                int listingStart = line.indexOf("g_rgListingInfo");
                if (listingStart > -1) {
                    int objectStart = line.indexOf('{', listingStart);
                    if (objectStart > -1) {
                        listingInfo = om.readTree(line.substring(objectStart, line.length() - 1));
                        listingFound = true;
                    }
                }
            }
            if (!salesFound) {
                int salesStart = line.indexOf("line1");
                if (salesStart > -1) {
                    int objectStart = line.indexOf('[', salesStart);
                    if (objectStart <= -1) {
                        continue;
                    }
                    priceHistoryInfo = om.readTree(line.substring(objectStart, line.length() - 1));
                    salesFound = true;
                }
            }
        }
        if (!salesFound) {
            logger.warn("No sales info found!");
        }
        if (!listingFound) {
            logger.warn("No listing info found!");
        }
        if (!(salesFound && listingFound) && logger.isDebugEnabled()) {
            logger.debug("Content: " + stringBuilder.toString());
            try (PrintWriter printWriter = new PrintWriter(new File("listing-page-" + new Random().nextLong() + ".html"))) {
                printWriter.print(stringBuilder.toString());
                printWriter.flush();
            }
        }
    }

    @Override
    public void handleError(InputStream stream) throws IOException {
        error = true;
    }

    boolean isError() {
        return error;
    }
    
    /**
     * @return Does this item support buy orders?
     */
    boolean isBuyOrders() {
        return buyOrders;
    }
    
    boolean isImmediateSale() {
        return immediateSale;
    }
    
    boolean isNoListingForThisItem() {
        return noListingForThisItem;
    }
    
    boolean isNoPricingHistoryForThisItem() {
        return noPricingHistoryForThisItem;
    }

    boolean isNoLongerSold() {
        return noLongerSold;
    }
}
