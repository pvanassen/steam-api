/**
 *
 */
package nl.pvanassen.steam.store.item;

import nl.pvanassen.steam.store.GenericHandle;
import nl.pvanassen.steam.store.common.Listing;

/**
 * @author Paul van Assen
 */
public interface ItemService {

    /**
     * @param handle Handle overview item
     */
    void getAllItems(GenericHandle<OverviewItem> handle);

    /**
     * The datapoints will always be called first. Once they are done the
     * listings handle is called
     *
     * @param appId Appid of the item to get
     * @param urlName url name of the item to get
     * @param dataPointHandle If a datapoint is found this handle is called
     * @param listingHandle If all datapoints have been processed, the listings
     *            are handled through this call
     */
    void getItem(int appId, String urlName, GenericHandle<StatDataPoint> dataPointHandle, GenericHandle<Listing> listingHandle, GenericHandle<Boolean> buyOrders);

}
