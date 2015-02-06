package nl.pvanassen.steam.store.item;

import nl.pvanassen.steam.error.SteamException;

/**
 * No longer sold exception
 * 
 * @author Paul van Assen
 */
public class NoLongerSoldException extends SteamException {
    NoLongerSoldException() {
        super("Item no longer sold");
    }
}
