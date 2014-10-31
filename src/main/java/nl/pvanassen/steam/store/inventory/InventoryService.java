/**
 *
 */
package nl.pvanassen.steam.store.inventory;

import java.util.List;

/**
 * @author Paul van Assen
 */
public interface InventoryService {
    /**
     * Retrieve all that is in the inventory of the user
     *
     * @return List of items in the inventory
     */
    List<InventoryItem> getInventory();

    /**
     * Retrieve the inventory of a user for only one app id
     *
     * @param appId Retrieve only one app id
     * @return List of items in the inventory
     */
    List<InventoryItem> getInventory(int appId);

    /**
     * Retrieve all that is in the inventory of a user
     *
     * @param username The username to get the inventory from
     * @return List of items in the inventory
     */
    List<InventoryItem> getInventory(String username);

    /**
     * Retrieve all that is in the inventory of a user and an app id
     *
     * @param username The username to get the inventory from
     * @param appId Retrieve only one app id
     * @return List of items in the inventory
     */
    List<InventoryItem> getInventory(String username, int appId);

}
