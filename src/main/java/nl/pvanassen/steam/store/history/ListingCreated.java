package nl.pvanassen.steam.store.history;

import java.util.Date;

class ListingCreated extends HistoryRow {
    private final String steamId1;

    ListingCreated(String rowName, Date listed, Date acted, int price) {
        super(rowName, listed, acted, price);
        steamId1 = rowName.substring("history_row_".length());
    }

    String getSteamId1() {
        return steamId1;
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
        result = prime * result + ((steamId1 == null) ? 0 : steamId1.hashCode());
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
        if (!(obj instanceof ListingCreated)) {
            return false;
        }
        ListingCreated other = (ListingCreated) obj;
        if (steamId1 == null) {
            if (other.steamId1 != null) {
                return false;
            }
        }
        else if (!steamId1.equals(other.steamId1)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ListingCreated [steamId1=" + steamId1 + "]";
    }

}
