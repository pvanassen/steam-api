package nl.pvanassen.steam.store.item;

import nl.pvanassen.steam.error.SteamException;

/**
 * Exception if there is no data
 * @author Paul van Assen
 *
 */
public class NoListingFoundException extends SteamException {
    NoListingFoundException() {
        super("No listing found");
    }
}
