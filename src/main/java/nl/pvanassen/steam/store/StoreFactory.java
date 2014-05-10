package nl.pvanassen.steam.store;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Store factory for getting an instance
 * 
 * @author Paul van Assen
 */
public class StoreFactory {
    private static final Map<WeakReference<String>, WeakReference<SteamService>> CACHE_MAP = new HashMap<>();

    /**
     * Factory method for getting a store instance
     * 
     * @param cookies Cookies to use
     * @param username Username for the referral
     * @return Instance of the store service
     */
    public static StoreService getSteamStore(String cookies, String username) {
        synchronized (CACHE_MAP) {
            WeakReference<String> key = new WeakReference<>(cookies);
            WeakReference<SteamService> service = CACHE_MAP.get(key);
            if ((service != null) && (service.get() != null)) {
                return service.get();
            }
            service = new WeakReference<SteamService>(new SteamService(cookies, username));
            CACHE_MAP.put(key, service);
            return service.get();
        }
    }
}
