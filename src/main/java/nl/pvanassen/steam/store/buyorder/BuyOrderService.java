package nl.pvanassen.steam.store.buyorder;

import nl.pvanassen.steam.store.common.Item;

/**
 * Buy order service
 * @author paul
 *
 */
public interface BuyOrderService {
	/**
	 * Creates a buy order
	 * @param item Item to purchase
	 * @param currencyId Currency to apply to the prices
	 * @param priceTotal Price in total you want to pay max. 
	 * @param quantity Number of items you wish to buy
	 * @return A buy order id
	 */
    String createBuyOrder(Item item, int currencyId, int priceTotal, int quantity);
    
    /**
     * Returns the status of a buy order for a certain buy order id
     * @param id Buy order id, received from createBuyOrder
     * @return A status object
     */
    BuyOrderStatus getBuyOrderStatus(String id);
    
    /**
     * Cancels the buy order with the given id
     * @param id Buy order id, received from createBuyOrder
     */
    void cancelBuyOrder(String id);

}
