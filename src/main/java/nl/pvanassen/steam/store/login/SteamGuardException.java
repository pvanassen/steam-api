package nl.pvanassen.steam.store.login;

import nl.pvanassen.steam.error.SteamException;


/**
 * Steam guard authentication is needed
 * 
 * @author Paul van Assen
 */
public class SteamGuardException extends SteamException {

    /**
     * Steam guard login
     */
    public SteamGuardException() {
        super("Steam guard authentication needed");
    }
}
