package nl.pvanassen.steam.store.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.slf4j.LoggerFactory;

import nl.pvanassen.steam.error.SteamException;

/**
 * URL name helper to return a uniform url name
 *
 * @author Paul van Assen
 */
public final class UrlNameHelper {
    /**
     * Make sure urlName is uniform
     * 
     * @param urlName Url name, encoded or not.
     * @return Uniform url
     */
    public static String getUrlName(String urlName) {
        try {
            String decodedUrlName = URLDecoder.decode(urlName, "UTF-8");
            return URLEncoder.encode(decodedUrlName, "UTF-8").replace("+", "%20");
        }
        catch (UnsupportedEncodingException e) {
            throw new SteamException("Encoding not present", e);
        }
        catch (IllegalArgumentException e) {
            LoggerFactory.getLogger(UrlNameHelper.class).error("Error getting url name for '" + urlName + "'", e);
            throw new SteamException("Error getting url name for '" + urlName + "'", e);
        }
    }

    private UrlNameHelper() {
        super();
    }
}
