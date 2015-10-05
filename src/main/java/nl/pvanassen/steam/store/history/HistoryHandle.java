package nl.pvanassen.steam.store.history;

import com.google.common.collect.ImmutableList;
import nl.pvanassen.steam.http.DefaultHandle;
import nl.pvanassen.steam.store.helper.AmountHelper;
import nl.pvanassen.steam.store.helper.UrlNameHelper;
import nl.pvanassen.steam.store.xpath.XPathHelper;
import org.apache.html.dom.HTMLDocumentImpl;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class HistoryHandle extends DefaultHandle {
    private boolean exceptionThrown = false;

    private static class Asset {
        private final int appId;
        private final int contextId;
        private final String urlName;

        Asset(int appId, int contextId, String urlName) {
            this.appId = appId;
            this.contextId = contextId;
            this.urlName = urlName;
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Set<Purchase> purchases = new HashSet<>();
    private final Set<Sale> sales = new HashSet<>();
    private final Set<ListingCreated> listingsCreated = new LinkedHashSet<>();
    private final Set<ListingRemoved> listingsRemoved = new LinkedHashSet<>();
    private final ObjectMapper om;
    private static final XPathExpression HISTORY_ROW_XPATH = XPathHelper.getXpathExpression("//DIV[@class='market_listing_row market_recent_listing_row']");
    private static final XPathExpression GAIN_LOSS_XPATH = XPathHelper.getXpathExpression("./DIV[@class='market_listing_left_cell market_listing_gainorloss']");
    private static final XPathExpression DATE_XPATH = XPathHelper.getXpathExpression("./DIV[@class='market_listing_right_cell market_listing_listed_date can_combine']");
    private static final XPathExpression PRICE_XPATH = XPathHelper.getXpathExpression("./DIV/SPAN/SPAN[@class='market_listing_price']");
    private static final XPathExpression BUYER_XPATH = XPathHelper.getXpathExpression("./DIV/DIV[@class='market_listing_whoactedwith_name_block']");
    private static final XPathExpression ACTED_XPATH = XPathHelper.getXpathExpression("./DIV[@class='market_listing_right_cell market_listing_whoactedwith']");

    private final String lastRowId;
    private boolean error = false;
    private boolean foundRowId;
    private String latestRowId;
    private boolean savedFirstRowId = false;
    private int totalCount;

    HistoryHandle(String lastRowId, ObjectMapper om) {
        super();
        if (lastRowId == null) {
            this.lastRowId = "";
        } else {
            this.lastRowId = lastRowId;
        }
        this.om = om;
    }

    private Map<String, Asset> getAssetMap(JsonNode node) throws UnsupportedEncodingException {
        Map<String, Asset> assetMap = new HashMap<>();
        JsonNode assets = node.get("assets");
        for (JsonNode appId : assets) {
            for (JsonNode contextId : appId) {
                for (JsonNode item : contextId) {
                    String urlName = UrlNameHelper.getUrlName(item.get("market_hash_name").asText());
                    assetMap.put(item.get("id").asText(), new Asset(item.get("appid").asInt(), item.get("contextid").asInt(), urlName));
                }
            }
        }
        return assetMap;
    }

    History getHistory() {
        return new History(ImmutableList.copyOf(purchases), ImmutableList.copyOf(sales), ImmutableList.copyOf(listingsCreated), ImmutableList.copyOf(listingsRemoved), latestRowId);
    }

    private Map<String, String> getHovers(JsonNode node) {
        Map<String, String> hoverMap = new HashMap<>();
        String hovers = node.get("hovers").asText();
        for (String hoverStr : hovers.split(";")) {
            String hover = hoverStr.trim();
            if (hover.startsWith("CreateItemHoverFromContainer")) {
                String[] items = hover.replaceAll("'", "").split(",");
                String id = items[4].trim();
                String rowName = items[1].trim();
                hoverMap.put(rowName, id);
                int cutoffIdx = rowName.indexOf('_', 40);
                hoverMap.put(rowName.substring(0, cutoffIdx), id);
            }
        }
        return hoverMap;
    }

    int getTotalCount() {
        return totalCount;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        error = false;
        logger.info("Handling stream");
        JsonNode node = om.readTree(stream);
        totalCount = node.get("total_count").asInt();
        String resultHtml = node.get("results_html").asText();
        if (resultHtml.contains("market_listing_table_message")) {
            logger.error("There was an error: " + resultHtml.trim());
            error = true;
            return;
        }
        DOMFragmentParser parser = new DOMFragmentParser();
        HTMLDocument document = new HTMLDocumentImpl();
        Calendar now = Calendar.getInstance();
        Calendar actedCal = Calendar.getInstance();
        Calendar listedCal = Calendar.getInstance();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("d MMM", Locale.US);
            Map<String, Asset> assetMap = getAssetMap(node);
            Map<String, String> hoverMap = getHovers(node);

            DocumentFragment fragment = document.createDocumentFragment();
            parser.parse(new InputSource(new StringReader(resultHtml)), fragment);

            NodeList nodeSet = (NodeList) HISTORY_ROW_XPATH.evaluate(fragment, XPathConstants.NODESET);
            for (int i = 0; i < nodeSet.getLength(); i++) {
                Node historyRow = nodeSet.item(i);
                historyRow.getParentNode().removeChild(historyRow);
                String rowName = historyRow.getAttributes().getNamedItem("id").getTextContent();
                if (!savedFirstRowId) {
                    latestRowId = rowName;
                    savedFirstRowId = true;
                }
                if (rowName.toLowerCase().equals(lastRowId.toLowerCase())) {
                    logger.info("Found last row, stopping!");
                    foundRowId = true;
                    return;
                }
                Node gainLoss = (Node) GAIN_LOSS_XPATH.evaluate(historyRow, XPathConstants.NODE);
                String gainLossText = gainLoss.getTextContent().trim();
                HistoryStatus status = null;
                if ("-".equals(gainLossText)) {
                    status = HistoryStatus.SOLD;
                } else if ("+".equals(gainLossText)) {
                    status = HistoryStatus.BOUGHT;
                } else {
                    Node actedNode = (Node) ACTED_XPATH.evaluate(historyRow, XPathConstants.NODE);
                    String acted = actedNode.getTextContent().trim();
                    if ("Listing created".equals(acted)) {
                        status = HistoryStatus.CREATED;
                    } else if ("Listing canceled".equals(acted)) {
                        status = HistoryStatus.REMOVED;
                    }
                }
                boolean full = (status == HistoryStatus.BOUGHT) || (status == HistoryStatus.SOLD);

                String priceStr = ((Node) PRICE_XPATH.evaluate(historyRow, XPathConstants.NODE)).getTextContent().trim();
                String buyer = "";
                if (full) {
                    buyer = ((Node) BUYER_XPATH.evaluate(historyRow, XPathConstants.NODE)).getTextContent().replace("Buyer:", "").replace("Seller:", "").trim();
                }
                int eventIdx = rowName.indexOf("_event");
                if (eventIdx > -1) {
                    rowName = rowName.substring(0, eventIdx);
                }
                Asset asset = assetMap.get(hoverMap.get(rowName));
                try {
                    Date acted = null;
                    Date listed = null;
                    if (full) {
                        NodeList dates = (NodeList) DATE_XPATH.evaluate(historyRow, XPathConstants.NODESET);
                        String listedStr = dates.item(0).getTextContent().trim();
                        String actedStr = dates.item(1).getTextContent().trim();
                        acted = formatter.parse(actedStr);
                        listed = formatter.parse(listedStr);
                        actedCal.setTime(acted);
                        listedCal.setTime(listed);
                        setYear(now, actedCal);
                        setYear(now, listedCal);
                        acted = actedCal.getTime();
                        listed = listedCal.getTime();
                    }
                    int price = 0;
                    if (!"".equals(priceStr)) {
                        price = AmountHelper.getAmount(priceStr);
                    }
                    if (status == null) {
                        continue;
                    }
                    switch (status) {
                        case BOUGHT:
                            purchases.add(new Purchase(rowName, asset.appId, asset.urlName, asset.contextId, listed, acted, price, buyer));
                            break;
                        case CREATED:
                            listingsCreated.add(new ListingCreated(rowName, listed, acted, price));
                            break;
                        case REMOVED:
                            listingsRemoved.add(new ListingRemoved(rowName, listed, acted, price));
                            break;
                        case SOLD:
                            sales.add(new Sale(rowName, asset.appId, asset.urlName, asset.contextId, listed, acted, price, buyer));
                            break;
                    }
                } catch (ParseException e) {
                    logger.error("Error parsing date", e);
                }
            }
        } catch (SAXException | XPathExpressionException e) {
            logger.error("Error parsing html", e);
        }
    }

    boolean isError() {
        return error;
    }

    boolean isFoundRowId() {
        return foundRowId;
    }

    private void setYear(Calendar now, Calendar dateLess) {
        if (dateLess.get(Calendar.MONTH) > now.get(Calendar.MONTH)) {
            dateLess.set(Calendar.YEAR, now.get(Calendar.YEAR) - 1);
        } else {
            dateLess.set(Calendar.YEAR, now.get(Calendar.YEAR));
        }
    }

    @Override
    public void handleException(Exception exception) {
        super.handleException(exception);
        exceptionThrown = true;
    }

    public boolean isExceptionThrown() {
        return exceptionThrown;
    }
}
