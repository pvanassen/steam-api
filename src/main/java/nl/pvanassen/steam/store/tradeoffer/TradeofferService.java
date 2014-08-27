/**
 * 
 */
package nl.pvanassen.steam.store.tradeoffer;

import java.util.List;

import nl.pvanassen.steam.store.inventory.InventoryItem;

import com.google.common.base.Optional;

/**
 * @author Paul van Assen
 *
 */
public interface TradeofferService {
    /**
     * Make a trade offer of items to a user
     * @param partner Trading partner ID
     * @param me What do I offer
     * @param them What do they offer
     * @param message A message for the trade
     * @return The trade offer id
     */
    int makeTradeOffer(long partner, List<InventoryItem> me, List<InventoryItem> them, Optional<String> message);
    
    /**
     * @return A list of trade offers
     */
    List<Tradeoffer> getTradeOffers();
    
    void acceptTradeOffer(Tradeoffer tradeoffer);
}
