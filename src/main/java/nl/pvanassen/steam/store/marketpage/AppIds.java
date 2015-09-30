package nl.pvanassen.steam.store.marketpage;

import com.google.common.collect.ImmutableSet;
import nl.pvanassen.steam.http.Http;

import java.util.Set;

/**
 * Holder for known app ids
 * 
 * @author Paul van Assen
 */
public final class AppIds {
    /**
     * Getter for the known app ids
     * 
     * @return An unsorted set of known app ids
     */
    public static Set<Integer> getAppids() {
        return APP_IDS.ids;
    }

    private final Set<Integer> ids;

    private static final AppIds APP_IDS = new AppIds();

    private AppIds() {
        ids = ImmutableSet.copyOf(new SteamMarketPageService(Http.getInstance("", ""), "").getAppIds());
    }
}
