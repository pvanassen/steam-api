package nl.pvanassen.steam.store;

/**
 * Representation of an item in the inventory
 * 
 * @author Paul van Assen
 */
public class InventoryItem extends Item {

    private final String assetId;
    private final int contextId;
    private final String instanceId;
    private final boolean marketable;

    InventoryItem(String assetId, int contextId, String instanceId, int appId, String urlName, boolean marketable) {
        super(appId, urlName);
        this.assetId = assetId;
        this.contextId = contextId;
        this.instanceId = instanceId;
        this.marketable = marketable;
    }

    /**
     * @return the assetId
     */
    public String getAssetId() {
        return assetId;
    }

    /**
     * @return Context id to use
     */
    public int getContextId() {
        return contextId;
    }

    /**
     * Steam instance id of an item
     * 
     * @return Instance id
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * @return Is marketable
     */
    public boolean isMarketable() {
        return marketable;
    }

    @Override
    public String toString() {
        return "InventoryItem [assetId=" + assetId + ", contextId=" + contextId + ", instanceId=" + instanceId +
               ", appId=" + getAppId() + ", urlName=" + getUrlName() + "]";
    }
}
