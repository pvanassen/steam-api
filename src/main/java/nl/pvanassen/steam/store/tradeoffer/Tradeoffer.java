package nl.pvanassen.steam.store.tradeoffer;

import java.util.List;

import nl.pvanassen.steam.store.common.Item;

public class Tradeoffer {
	private final String partnerId;
	private final String offerId;
	private final String comment;
	private final List<Item> items;

	Tradeoffer(String partnerId, String offerId, String comment,
			List<Item> items) {
		super();
		this.partnerId = partnerId;
		this.offerId = offerId;
		this.comment = comment;
		this.items = items;
	}

	public String getComment() {
		return comment;
	}

	public List<Item> getItems() {
		return items;
	}

	public String getOfferId() {
		return offerId;
	}

	public String getPartnerId() {
		return partnerId;
	}

    @Override
    public String toString() {
        return "Tradeoffer [partnerId=" + partnerId + ", offerId=" + offerId + ", comment=" + comment + ", items=" + items + "]";
    }
}
