package nl.pvanassen.steam.community.friends;

import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.http.NullHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Friends service to handle friend requests    
 * 
 * @author Paul van Assen
 */
public class SteamFriendService implements FriendService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Http http;
    private final String username;

    /**
     * @param http For mocking
     * @param username The username
     */
    public SteamFriendService(Http http, String username) {
        this.http = http;
        this.username = username;
    }

    @Override
    public void ignoreAllFriendRequests() {
        logger.info("Ignoring all friends");
        http.get("http://steamcommunity.com/id/" + username + "/home_process?action=ignoreAll&type=friends&sessionID=" + http.getSessionId(), new NullHandle(), false);
    }
}
