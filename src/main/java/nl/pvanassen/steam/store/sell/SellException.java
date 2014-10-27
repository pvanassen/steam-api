package nl.pvanassen.steam.store.sell;

import nl.pvanassen.steam.error.SteamException;

/**
 * Exception while trying to sell the item
 * 
 * @author Paul van Assen
 *
 */
public class SellException extends SteamException {
    private final boolean itemNotInInventory;

    SellException(String error) {
        super(error);
        itemNotInInventory = ((error != null) && error.contains("found in inventory"));
    }

    SellException(String error, Throwable cause) {
        super(error, cause);
        itemNotInInventory = ((error != null) && error.contains("found in inventory"));
    }

    public boolean isItemNotInInventory() {
        return itemNotInInventory;
    }
}
