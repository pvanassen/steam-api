package nl.pvanassen.steam.store.marketpage;

import java.util.Set;

import nl.pvanassen.steam.http.Http;

import com.google.common.collect.ImmutableSet;

public final class AppIds {
	private final Set<Integer> ids;
	private static final AppIds APP_IDS = new AppIds();
	
	private AppIds() {
		ids = ImmutableSet.copyOf(new SteamMarketPageService(Http.getInstance("", ""), "").getAppIds());
	}
	
	public static Set<Integer> getAppids() {
		return APP_IDS.ids;
	}
}
