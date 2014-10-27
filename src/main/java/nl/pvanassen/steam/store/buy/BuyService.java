package nl.pvanassen.steam.store.buy;

import nl.pvanassen.steam.store.common.BuyOrder;

/**
 * Buy service handing purchasing of items
 *
 * @author Paul van Assen
 */
public interface BuyService {
    /**
     * Buy a listed item
     *
     * @param buyOrder
     *            The order to buy, containing listing id and price
     * @return The results of a purchase
     */
    BuyResult buy(BuyOrder buyOrder);
}
