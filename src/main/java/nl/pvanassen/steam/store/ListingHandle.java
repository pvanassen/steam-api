package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;

class ListingHandle extends DefaultHandle {
	// private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ObjectMapper objectMapper;
	private final List<Listing> listings = new LinkedList<Listing>();

	ListingHandle(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	List<Listing> getListings() {
		return listings;
	}

	@Override
	public void handle(InputStream stream) throws IOException {
		Map<String, IncompleteListing> incompleteListingsMap = null;
		Map<String, Asset> assetsMap = null;
		JsonParser jParser = objectMapper.getJsonFactory().createJsonParser(
				stream);
		String fieldname = jParser.getCurrentName();
		jParser.nextToken();
		while (jParser.nextToken() != JsonToken.END_OBJECT) {
			fieldname = jParser.getCurrentName();
			if (fieldname == null) {
				break;
			}
			jParser.nextToken();
			if ("listinginfo".equals(fieldname)) {
				incompleteListingsMap = getListings(jParser);
			} else if ("assets".equals(fieldname)) {
				assetsMap = getAssets(jParser);
			}
			else if (incompleteListingsMap != null && assetsMap != null) {
				break;
			}
			else if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
				walkArray(jParser);
			} else if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
				walkObject(jParser);
			}
		}
		for (Map.Entry<String, IncompleteListing> entry : incompleteListingsMap
				.entrySet()) {
			IncompleteListing incompleteListing = entry.getValue();
			Asset asset = assetsMap.get(entry.getKey());
			if (asset == null) {
				continue;
			}
			Listing listing = incompleteListing.getListing(asset);
			if (listing.getFee() == 0) {
				continue;
			}
			listings.add(listing);
		}
		/*
		 * JsonNode node = objectMapper.readTree( stream ); JsonNode assets =
		 * node.get( "assets" ); for ( JsonNode listing : node.get(
		 * "listinginfo" ) ) { int appId = listing.get( "asset" ).get( "appid"
		 * ).asInt(); String contextId = listing.get( "asset" ).get( "contextid"
		 * ).asText(); String id = listing.get( "asset" ).get( "id" ).asText();
		 * String urlName = URLEncoder.encode( assets.get( Integer.toString(
		 * appId ) ).get( contextId ).get( id ).get( "market_hash_name"
		 * ).asText(), "UTF-8" ).replace( "+", "%20" ); try { listings.add( new
		 * Listing( appId, urlName, listing.get( "listingid" ).asText(),
		 * listing.get( "steamid_lister" ).asText(), listing.get(
		 * "converted_price" ).asInt(), listing.get( "converted_fee" ).asInt(),
		 * listing.get( "converted_steam_fee" ).asInt(), listing.get(
		 * "converted_publisher_fee" ).asInt(), listing.get( "publisher_fee_app"
		 * ).asInt(), listing.get( "publisher_fee_percent" ).asDouble() ) ); }
		 * catch (NullPointerException e) { logger.error("Error fetching " +
		 * listing); } }
		 */
	}
	
	private void walkArray(JsonParser jParser) throws JsonParseException, IOException {
		while (jParser.nextToken() != JsonToken.END_ARRAY) {
			jParser.nextToken();
			if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
				walkObject(jParser);
			}
			else if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
				walkArray(jParser);
			}
		}
	}

	private void walkObject(JsonParser jParser) throws JsonParseException, IOException {
		while (jParser.nextToken() != JsonToken.END_OBJECT) {
			jParser.nextToken();
			if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
				walkObject(jParser);
			}
			else if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
				walkArray(jParser);
			}
		}
	}

	private Map<String, Asset> getAssets(JsonParser jParser)
			throws JsonParseException, IOException {
		Map<String, Asset> map = new HashMap<>();
		String fieldname;
		while (jParser.nextToken() != JsonToken.END_OBJECT) {
			jParser.nextToken();
			while (jParser.nextToken() != JsonToken.END_OBJECT) {
				jParser.nextToken();
				while (jParser.nextToken() != JsonToken.END_OBJECT) {
					jParser.nextToken();
					Asset asset = new Asset();
					while (jParser.nextToken() != JsonToken.END_OBJECT) {
						fieldname = jParser.getCurrentName();
						jParser.nextToken();
						if ("id".equals(fieldname)) {
							asset.id = jParser.getText();
						} else if ("tradable".equals(fieldname)) {
							asset.tradable = jParser.getIntValue() == 1;
						} else if ("market_hash_name".equals(fieldname)) {
							asset.urlName = URLEncoder.encode(
									jParser.getText(), "UTF-8").replace("+",
									"%20");
						} else if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
							while (jParser.nextToken() != JsonToken.END_ARRAY) {
								jParser.nextToken();
								// Loop
							}
						}
					}
					map.put(asset.id, asset);
				}
			}
		}
		return map;
	}

	private Map<String, IncompleteListing> getListings(JsonParser jParser)
			throws JsonParseException, IOException {
		Map<String, IncompleteListing> map = new LinkedHashMap<>();
		String fieldname;
		while (jParser.nextToken() != JsonToken.END_OBJECT) {
			jParser.nextToken();
			IncompleteListing listing = new IncompleteListing();
			while (jParser.nextToken() != JsonToken.END_OBJECT) {
				fieldname = jParser.getCurrentName();
				jParser.nextToken();
				if ("asset".equals(fieldname)) {
					while (jParser.nextToken() != JsonToken.END_OBJECT) {
						fieldname = jParser.getCurrentName();
						jParser.nextToken();
						if ("id".equals(fieldname)) {
							listing.id = jParser.getText();
						} else if ("appid".equals(fieldname)) {
							listing.appId = jParser.getIntValue();
						} else if ("contextid".equals(fieldname)) {
							listing.contextId = Integer.valueOf(jParser
									.getText());
						} else if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
							while (jParser.nextToken() != JsonToken.END_ARRAY) {
								jParser.nextToken();
							}
						} else if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
							while (jParser.nextToken() != JsonToken.END_OBJECT) {
								jParser.nextToken();
							}
						}
					}
				} else if ("listingid".equals(fieldname)) {
					listing.listingId = jParser.getText();
				} else if ("steamid_lister".equals(fieldname)) {
					listing.steamIdLister = jParser.getText();
				} else if ("converted_price".equals(fieldname)) {
					listing.subTotal = jParser.getIntValue();
				} else if ("converted_fee".equals(fieldname)) {
					listing.fee = jParser.getIntValue();
				} else if ("converted_steam_fee".equals(fieldname)) {
					listing.steamFee = jParser.getIntValue();
				} else if ("converted_publisher_fee".equals(fieldname)) {
					listing.publisherFee = jParser.getIntValue();
				} else if ("publisher_fee_app".equals(fieldname)) {
					listing.publisherFeeApp = Integer
							.valueOf(jParser.getText());
				} else if ("publisher_fee_percent".equals(fieldname)) {
					listing.publisherFeePercent = Double.valueOf(jParser
							.getText());
				} else if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
					while (jParser.nextToken() != JsonToken.END_ARRAY) {
						jParser.nextToken();
					}
				} else if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
					while (jParser.nextToken() != JsonToken.END_OBJECT) {
						jParser.nextToken();
					}
				}
			}
			map.put(listing.id, listing);
		}
		return map;
	}

	private static class IncompleteListing {
		private int appId;

		private int contextId;

		private String id;

		private String listingId;

		private String steamIdLister;

		private int subTotal;

		private int fee;

		private int steamFee;

		private int publisherFee;

		private int publisherFeeApp;

		private double publisherFeePercent;

		Listing getListing(Asset asset) {
			return new Listing(appId, asset.urlName, listingId, steamIdLister,
					subTotal, fee, steamFee, publisherFee, publisherFeeApp,
					publisherFeePercent);
		}
	}

	private static class Asset {
		private String id;

		private String urlName;

		private boolean tradable;
	}
}
