package nl.pvanassen.steam.store;

/**
 * Representation of an item in the inventory
 * 
 * @author Paul van Assen
 */
public class InventoryItem {

    private final String assetId;
    private final int contextId;
    private final String instanceId;
    private final int appId;
    private final String urlName;

    InventoryItem( String assetId, int contextId, String instanceId, int appId, String urlName ) {
        super();
        this.assetId = assetId;
        this.contextId = contextId;
        this.instanceId = instanceId;
        this.appId = appId;
        this.urlName = urlName;
    }

    /**
     * @return the appId
     */
    public int getAppId() {
        return appId;
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
     * @return the urlName
     */
    public String getUrlName() {
        return urlName;
    }

    /**
     * Steam instance id of an item
     * @return Instance id
     */
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public String toString() {
        return "InventoryItem [assetId=" + assetId + ", contextId=" + contextId + ", instanceId=" + instanceId
                + ", appId=" + appId + ", urlName=" + urlName + "]";
    }
}
