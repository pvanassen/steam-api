package nl.pvanassen.steam.store.item;

import nl.pvanassen.steam.error.SteamException;

/**
 * No pricing found
 * @author Paul van Assen
 *
 */
public class NoPricingHistoryFoundException extends SteamException {
    NoPricingHistoryFoundException() {
        super("No pricing history found");
    }
}
