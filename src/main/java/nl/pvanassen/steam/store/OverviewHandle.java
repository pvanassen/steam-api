package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.pvanassen.steam.http.DefaultHandle;

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

class OverviewHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
    private static final XPath XPATH = XPATH_FACTORY.newXPath();
    private static final XPathExpression ROW_XPATH;
    private static final XPathExpression PRICE_XPATH;
    private static final XPathExpression OFFERINGS_XPATH;
    private static final XPathExpression NAME_XPATH;
    private static final XPathExpression GAMENAME_XPATH;

    static {
        XPathExpression rowXpath = null;
        XPathExpression priceXpath = null;
        XPathExpression offeringsXpath = null;
        XPathExpression nameXpath = null;
        XPathExpression gameNameXpath = null;
        try {
            rowXpath = XPATH.compile("//A[@class='market_listing_row_link']");
            priceXpath = XPATH
                    .compile(".//DIV[@class='market_listing_right_cell market_listing_their_price']/SPAN/SPAN");
            offeringsXpath = XPATH
                    .compile(".//DIV[@class='market_listing_right_cell market_listing_num_listings']/SPAN/SPAN");
            nameXpath = XPATH
                    .compile(".//DIV[@class='market_listing_item_name_block']/SPAN[@class='market_listing_item_name']");
            gameNameXpath = XPATH
                    .compile(".//DIV[@class='market_listing_item_name_block']/SPAN[@class='market_listing_game_name']");
        }
        catch (XPathExpressionException e) {
            LoggerFactory.getLogger(MarketHistory.class).error("Error instantiating XPATH", e);
        }
        ROW_XPATH = rowXpath;
        PRICE_XPATH = priceXpath;
        OFFERINGS_XPATH = offeringsXpath;
        NAME_XPATH = nameXpath;
        GAMENAME_XPATH = gameNameXpath;
    }

    private final GenericHandle<OverviewItem> genericHandle;

    private final ObjectMapper om;

    private int totalCount;

    private boolean error = false;

    private boolean lastPage = false;

    public OverviewHandle(GenericHandle<OverviewItem> genericHandle, ObjectMapper om) {
        this.genericHandle = genericHandle;
        this.om = om;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        error = false;
        lastPage = false;
        JsonNode jsonNode = om.readTree(stream);
        String html = jsonNode.get("results_html").asText();
        totalCount = jsonNode.get("total_count").asInt();
        if (html.contains("Please try again later")) {
            error = true;
            return;
        }
        if (html.contains("There were no items matching your search. Try again with different keywords")) {
            logger.info("Last page hit");
            lastPage = true;
            return;
        }
        DOMFragmentParser parser = new DOMFragmentParser();
        HTMLDocument document = new HTMLDocumentImpl();
        DocumentFragment fragment = document.createDocumentFragment();
        try {
            parser.parse(new InputSource(new StringReader(html)), fragment);
            NodeList nodeSet = (NodeList) ROW_XPATH.evaluate(fragment, XPathConstants.NODESET);
            for (int i = 0; i < nodeSet.getLength(); i++) {
                Node node = nodeSet.item(i);
                String href = node.getAttributes().getNamedItem("href").getTextContent();
                int startName = href.lastIndexOf('/');
                int startAppId = href.lastIndexOf('/', startName - 1);
                String urlName = href.substring(startName + 1);
                String steamId = "unknown";
                int idx = urlName.indexOf('-');
                if (idx > -1) {
                    steamId = urlName.substring(0, idx);
                }
                int appId = Integer.valueOf(href.substring(startAppId + 1, startName));
                logger.info("Found: " + urlName + ", appid: " + appId);
                Node priceSpan = (Node) PRICE_XPATH.evaluate(node, XPathConstants.NODE);
                int currentPrice = Integer.valueOf(priceSpan.getTextContent().trim().substring(1).replace("€", "")
                        .replace(",", "").replace("--", "00"));
                Node offersSpan = (Node) OFFERINGS_XPATH.evaluate(node, XPathConstants.NODE);
                int currentOffers = Integer.valueOf(offersSpan.getTextContent().replace(",", ""));

                Node nameSpan = (Node) NAME_XPATH.evaluate(node, XPathConstants.NODE);
                String name = nameSpan.getTextContent();
                Node gameNameSpan = (Node) GAMENAME_XPATH.evaluate(node, XPathConstants.NODE);
                String gameName = gameNameSpan.getTextContent();
                genericHandle.handle(new OverviewItem(appId, name, urlName, currentOffers, currentPrice, gameName,
                        steamId));
            }

        }
        catch (SAXException | XPathExpressionException e) {
            logger.error("Error parsing XML", e);
        }
    }

    @Override
    public void handleError(InputStream stream) throws IOException {
        error = true;
    }

    boolean isError() {
        return error;
    }

    boolean isLastPage() {
        return lastPage;
    }

    int getTotalCount() {
        return totalCount;
    }
}
