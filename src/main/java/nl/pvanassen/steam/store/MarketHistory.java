package nl.pvanassen.steam.store;

import java.util.Date;

/**
 * Items in the market
 * 
 * @author Paul van Assen
 * 
 */
public class MarketHistory {
	private final int appId;
	private final int contextId;
	private final String urlName;
	private final Date listed;
	private final Date acted;
	private final int price;
	private final String buyer;

	MarketHistory(int appId, int contextId, String urlName, Date listed,
			Date acted, int price, String buyer) {
		super();
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

}
