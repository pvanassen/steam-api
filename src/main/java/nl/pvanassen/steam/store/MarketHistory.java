package nl.pvanassen.steam.store;

/**
 * Items in the market
 * @author Paul van Assen
 *
 */
public class MarketHistory {
    private final int appId;
    private final int contextId;
    private final String id;
    private final String classId;
    private final String instanceid;
    private final String urlName;
    private final int amount;
    private final int status;

    MarketHistory( int appId, int contextId, String id, String classId, String instanceid, String urlName, int amount, int status ) {
        super();
        this.appId = appId;
        this.contextId = contextId;
        this.id = id;
        this.classId = classId;
        this.instanceid = instanceid;
        this.urlName = urlName;
        this.amount = amount;
        this.status = status;
    }

    /**
     * @return the appId
     */
    public int getAppId() {
        return appId;
    }

    /**
     * @return the contextId
     */
    public int getContextId() {
        return contextId;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the classId
     */
    public String getClassId() {
        return classId;
    }

    /**
     * @return the instanceid
     */
    public String getInstanceid() {
        return instanceid;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return URL name
     */
    public String getUrlName() {
        return urlName;
    }
}
