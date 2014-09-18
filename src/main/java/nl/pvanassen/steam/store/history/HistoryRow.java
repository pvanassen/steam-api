package nl.pvanassen.steam.store.history;

import java.util.Date;

public class HistoryRow {
	private final String rowName;
	private final Date listed;
	private final Date acted;
	private final int price;
	
	HistoryRow(String rowName, Date listed, Date acted, int price) {
		super();
		this.rowName = rowName;
		this.listed = listed;
		this.acted = acted;
		this.price = price;
	}
	/**
	 * @return the steamId
	 */
	public final String getRowName() {
		return rowName;
	}
	/**
	 * @return the listed
	 */
	public final Date getListed() {
		return listed;
	}
	/**
	 * @return the acted
	 */
	public final Date getActed() {
		return acted;
	}
	/**
	 * @return the price
	 */
	public final int getPrice() {
		return price;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acted == null) ? 0 : acted.hashCode());
		result = prime * result + ((listed == null) ? 0 : listed.hashCode());
		result = prime * result + price;
		result = prime * result + ((rowName == null) ? 0 : rowName.hashCode());
		return result;
	}
	/**
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
		if (!(obj instanceof HistoryRow)) {
			return false;
		}
		HistoryRow other = (HistoryRow) obj;
		if (acted == null) {
			if (other.acted != null) {
				return false;
			}
		} else if (!acted.equals(other.acted)) {
			return false;
		}
		if (listed == null) {
			if (other.listed != null) {
				return false;
			}
		} else if (!listed.equals(other.listed)) {
			return false;
		}
		if (price != other.price) {
			return false;
		}
		if (rowName == null) {
			if (other.rowName != null) {
				return false;
			}
		} else if (!rowName.equals(other.rowName)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HistoryRow [steamId=" + rowName + ", listed=" + listed
				+ ", acted=" + acted + ", price=" + price + "]";
	}

}
