package nl.pvanassen.steam.store;

import java.util.Date;

/**
 * Items in the market
 * 
 * @author Paul van Assen
 */
public class MarketHistory {
    private final String steamId;
    private final int appId;
    private final int contextId;
    private final String urlName;
    private final Date listed;
    private final Date acted;
    private final int price;
    private final String buyer;

    MarketHistory( String steamId, int appId, int contextId, String urlName, Date listed, Date acted, int price, String buyer ) {
        super();
        this.steamId = steamId;
        this.appId = appId;
        this.contextId = contextId;
        this.urlName = urlName;
        this.listed = listed;
        this.acted = acted;
        this.price = price;
        this.buyer = buyer;
    }

    public int getAppId() {
        return appId;
    }

    public int getContextId() {
        return contextId;
    }

    public String getUrlName() {
        return urlName;
    }

    public Date getListed() {
        return listed;
    }

    public Date getActed() {
        return acted;
    }

    public int getPrice() {
        return price;
    }

    public String getBuyer() {
        return buyer;
    }
    
    public String getSteamId() {
        return steamId;
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
        result = prime * result + ( ( steamId == null ) ? 0 : steamId.hashCode() );
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( !( obj instanceof MarketHistory ) ) {
            return false;
        }
        MarketHistory other = ( MarketHistory ) obj;
        if ( steamId == null ) {
            if ( other.steamId != null ) {
                return false;
            }
        }
        else if ( !steamId.equals( other.steamId ) ) {
            return false;
        }
        return true;
    }

}
