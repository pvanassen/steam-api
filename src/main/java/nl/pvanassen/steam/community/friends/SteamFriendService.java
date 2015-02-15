package nl.pvanassen.steam.community.friends;

import java.io.IOException;

import nl.pvanassen.steam.error.SteamException;
import nl.pvanassen.steam.http.*;

import org.slf4j.*;

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
        try {
            logger.info("Ignoring all friends");
            http.get("http://steamcommunity.com/id/" + username + "/home_process?action=ignoreAll&type=friends&sessionID=" + http.getSessionId(), new NullHandle());
        } catch (IOException e) {
            logger.info("Error ignoring friends", e);
            throw new SteamException("IO Error ignoring friend requests", e);
        }
    }
}
