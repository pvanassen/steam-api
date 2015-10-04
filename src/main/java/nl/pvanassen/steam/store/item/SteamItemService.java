/**
 *
 */
package nl.pvanassen.steam.store.item;

import nl.pvanassen.steam.error.SteamException;
import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.store.common.GenericHandle;
import nl.pvanassen.steam.store.common.Listing;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul van Assen
 */
public class SteamItemService implements ItemService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Http http;

    /**
     * @param http For mocking
     */
    public SteamItemService(Http http) {
        this.http = http;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.item.ItemService#getAllItems(nl.pvanassen.steam.store.common.GenericHandle)
     */
    @Override
    public void getAllItems(GenericHandle<OverviewItem> genericHandle) {
        OverviewHandle handle = new OverviewHandle(genericHandle, objectMapper);
        // Initial high, will be corrected on first run
        int totalCount = 5000;
        for (int start = 0; start < totalCount; start += 100) {
            do {
                http.get("http://steamcommunity.com/market/search/render/?query=&search_descriptions=0&start=" + start + "&count=100", handle, false);
                totalCount = handle.getTotalCount();
                // Stop on overrun
                if (handle.isLastPage()) {
                    return;
                }
            } while (handle.isError());
        }
    }

    @Override
    public void getItem(String host, int appId, String urlName, GenericHandle<StatDataPoint> dataPointHandle, GenericHandle<Listing> listingHandle,
            GenericHandle<Boolean> buyOrders, GenericHandle<Boolean> immediateSale) {
        String url = "http://" + host + "/market/listings/" + appId + "/" + urlName;
        ListingPageScriptHandle handle = new ListingPageScriptHandle(objectMapper);
        http.get(url, handle, false);
        if (handle.isError()) {
            throw new SteamException("Error getting data for url: " + url + " error code was not 200");
        }
        if (handle.isNoListingForThisItem()) {
            throw new NoListingFoundException();
        }
        if (handle.isNoPricingHistoryForThisItem()) {
            throw new NoPricingHistoryFoundException();
        }
        if (handle.isNoLongerSold()) {
            throw new NoLongerSoldException();
        }
        buyOrders.handle(handle.isBuyOrders());
        immediateSale.handle(handle.isImmediateSale());
        JsonNode priceHistoryInfo = handle.getPriceHistoryInfo();
        for (StatDataPoint point : new ListingStatDataPointIterator(priceHistoryInfo)) {
            dataPointHandle.handle(point);
        }
        if (listingHandle == null) {
            return;
        }
        JsonNode listingInfo = handle.getListingInfo();
        for (Listing item : new ListingItemIterator(appId, urlName, listingInfo)) {
            listingHandle.handle(item);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.item.ItemService#getItem(int, java.lang.String, nl.pvanassen.steam.store.common.GenericHandle, nl.pvanassen.steam.store.common.GenericHandle,
     *      nl.pvanassen.steam.store.common.GenericHandle, nl.pvanassen.steam.store.common.GenericHandle)
     */
    @Override
    public void getItem(int appId, String urlName, GenericHandle<StatDataPoint> dataPointHandle, GenericHandle<Listing> listingHandle, GenericHandle<Boolean> buyOrders,
            GenericHandle<Boolean> immediateSale) {
        getItem("steamcommunity.com", appId, urlName, dataPointHandle, listingHandle, buyOrders, immediateSale);
    }
}
