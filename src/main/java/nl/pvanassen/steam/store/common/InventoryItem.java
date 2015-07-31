package nl.pvanassen.steam.store.common;

import java.util.Date;

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
    private final boolean tradable;
    private final Date blockedUntil;

    /**
     * An item from the inventory
     * @param assetId Asset id
     * @param contextId Context id
     * @param instanceId Instance id
     * @param appId App id
     * @param urlName Url name
     * @param marketable Is this item marketable?
     * @param tradable Is this item tradable?
     * @param blockedUntil Blocked until when?
     */
    public InventoryItem(String assetId, int contextId, String instanceId, int appId, String urlName, boolean marketable, boolean tradable, Date blockedUntil) {
        super(appId, urlName);
        this.assetId = assetId;
        this.contextId = contextId;
        this.instanceId = instanceId;
        this.marketable = marketable;
        this.tradable = tradable;
        this.blockedUntil = blockedUntil;
    }

    /**
     * Protected copy constructor for extending the object
     * 
     * @param inventoryItem Item to copy
     */
    protected InventoryItem(InventoryItem inventoryItem) {
        super(inventoryItem.getAppId(), inventoryItem.getUrlName());
        this.assetId = inventoryItem.assetId;
        this.contextId = inventoryItem.contextId;
        this.instanceId = inventoryItem.instanceId;
        this.marketable = inventoryItem.marketable;
        this.tradable = inventoryItem.tradable;
        this.blockedUntil = inventoryItem.blockedUntil;
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

    /**
     * @return Can this item be traded
     */
    public boolean isTradable() {
        return tradable;
    }

    /**
     * @return Item can be traded or marketed from this day onward
     */
    public Date getBlockedUntil() {
        return blockedUntil;
    }

    @Override
    public String toString() {
        return "InventoryItem [assetId=" + assetId + ", contextId=" + contextId + ", instanceId=" + instanceId + ", appId=" + getAppId() + ", urlName=" + getUrlName() + "]";
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((assetId == null) ? 0 : assetId.hashCode());
        result = prime * result + contextId;
        result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
        result = prime * result + (marketable ? 1231 : 1237);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof InventoryItem)) {
            return false;
        }
        InventoryItem other = (InventoryItem) obj;
        if (assetId == null) {
            if (other.assetId != null) {
                return false;
            }
        }
        else if (!assetId.equals(other.assetId)) {
            return false;
        }
        if (contextId != other.contextId) {
            return false;
        }
        if (instanceId == null) {
            if (other.instanceId != null) {
                return false;
            }
        }
        else if (!instanceId.equals(other.instanceId)) {
            return false;
        }
        if (marketable != other.marketable) {
            return false;
        }
        return true;
    }
}
