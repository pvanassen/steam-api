package nl.pvanassen.steam.store.login;

import nl.pvanassen.steam.error.SteamException;

/**
 * Steam guard authentication is needed
 *
 * @author Paul van Assen
 */
public class SteamGuardException extends SteamException {
    private final String emailSteamId;

    /**
     * Steam guard login
     */
    SteamGuardException(String emailSteamId) {
        super("Steam guard authentication needed");
        this.emailSteamId = emailSteamId;
    }

    /**
     * @return ID send by steam to identify this mail
     */
    public String getEmailSteamId() {
        return emailSteamId;
    }
}
