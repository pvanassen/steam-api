package nl.pvanassen.steam.store.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import nl.pvanassen.steam.error.SteamException;

/**
 * URL name helper to return a uniform url name
 * 
 * @author Paul van Assen
 */
public final class UrlNameHelper {
    private UrlNameHelper() {
        super();
    }

    /**
     * Make sure urlName is uniform
     * 
     * @param urlName Url name, encoded or not. 
     * @return Uniform url
     */
    public static String getUrlName(String urlName) {
        try {
            String decoded = URLDecoder.decode(urlName, "UTF-8");
            return URLEncoder.encode(decoded, "UTF-8").replace("+", "%20");
        }
        catch (UnsupportedEncodingException e) {
            throw new SteamException("Encoding not present", e);
        }
    }
}
