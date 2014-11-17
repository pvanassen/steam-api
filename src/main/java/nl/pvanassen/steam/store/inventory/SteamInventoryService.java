/**
 *
 */
package nl.pvanassen.steam.store.inventory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nl.pvanassen.steam.error.SteamException;
import nl.pvanassen.steam.http.Http;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * @author Paul van Assen
 */
public class SteamInventoryService implements InventoryService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Set<Integer> appIds;
    private final Http http;
    private final String username;

    /**
     * @param http For mocking
     * @param username Username for referals
     * @param appIds The known app ids
     */
    public SteamInventoryService(Http http, String username, Set<Integer> appIds) {
        this.http = http;
        this.username = username;
        this.appIds = appIds;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.store.inventory.InventoryService#getInventory()
     */
    @Override
    public List<InventoryItem> getInventory() {
        return getInventory(username);
    }

    @Override
    public List<InventoryItem> getInventory(int appId) {
        return getInventory(username, appId);
    }

    @Override
    public List<InventoryItem> getInventory(String username) {
        List<InventoryItem> inventoryItems = new LinkedList<>();
        for (int appId : appIds) {
            inventoryItems.addAll(getInventory(username, appId));
        }
        return ImmutableList.copyOf(inventoryItems);
    }

    @Override
    public List<InventoryItem> getInventory(String username, int appId) {
        List<InventoryItem> inventoryItems = new LinkedList<>();
        int contextId = 2;
        if (appId == 753) {
            contextId = 6;
        }
        logger.info("Getting inventory for app id " + appId);
        InventoryHandle handle = new InventoryHandle(objectMapper, contextId, inventoryItems);
        try {
            http.get("http://steamcommunity.com/id/" + username + "/inventory/json/" + appId + "/" + contextId + "/", handle);
        }
        catch (IOException e) {
            logger.error("Error fetching inventory data", e);
            throw new SteamException("Error fetching inventory data", e);
        }
        if (handle.isError()) {
            logger.error("Error fetching inventory data");
            throw new SteamException("Error fetching inventory data");
        }
        return ImmutableList.copyOf(inventoryItems);
    }
}
