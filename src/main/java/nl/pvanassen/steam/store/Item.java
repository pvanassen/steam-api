package nl.pvanassen.steam.store;

/**
 * Base class for any item related stuff
 * 
 * @author Paul van Assen
 */
public abstract class Item {
    private final int appId;
    private final String urlName;

    /**
     * Constructor
     * 
     * @param appId App id of the item
     * @param urlName Url name of the item
     */
    protected Item(int appId, String urlName) {
        this.appId = appId;
        this.urlName = urlName;
    }

    /**
     * @return Steam application id
     */
    public int getAppId() {
        return appId;
    }

	/**
     * @return Steam URL name
     */
    public String getUrlName() {
        return urlName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + appId;
        result = (prime * result) + ((urlName == null) ? 0 : urlName.hashCode());
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
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Item)) {
            return false;
        }
        Item other = (Item) obj;
        if (appId != other.appId) {
            return false;
        }
        if (urlName == null) {
            if (other.urlName != null) {
                return false;
            }
        }
        else if (!urlName.equals(other.urlName)) {
            return false;
        }
        return true;
    }

    @Override
	public String toString() {
		return "Item [appId=" + appId + ", urlName=" + urlName + "]";
	}

    /**
     * @return A url for debugging
     */
    public String getUrl() {
        return "http://steamcommunity.com/market/listings/" + appId + "/" + urlName;
    }
}
