package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.pvanassen.steam.http.DefaultHandle;
import nl.pvanassen.steam.store.helper.AmountHelper;

import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class OutstandingsHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<OutstandingItem> items = new LinkedList<>();
    private Outstandings outstandings;
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
    private static final XPath XPATH = XPATH_FACTORY.newXPath();
    private static final XPathExpression ITEMS_DIV_XPATH;
    private static final XPathExpression PRICE_XPATH;
    private static final XPathExpression DATE_XPATH;
    private static final XPathExpression REMOVE_XPATH;
    private static final XPathExpression LINK_XPATH;
    private static final XPathExpression WALLET_XPATH;

    static {
        XPathExpression itemsDivXpath = null;
        XPathExpression priceXpath = null;
        XPathExpression dateXpath = null;
        XPathExpression removeXpath = null;
        XPathExpression linkXpath = null;
        XPathExpression walletXpath = null;
        try {
            itemsDivXpath = XPATH.compile("//DIV[@class='market_content_block my_listing_section market_home_listing_table']");
            priceXpath = XPATH.compile(".//SPAN[@class='market_listing_price']");
            dateXpath = XPATH.compile(".//DIV[@class='market_listing_right_cell market_listing_listed_date']");
            removeXpath = XPATH.compile(".//A[@class='item_market_action_button item_market_action_button_edit']");
            linkXpath = XPATH.compile(".//A[@class='market_listing_item_name_link']");
            walletXpath = XPATH.compile("//SPAN[@id='marketWalletBalanceAmount']");
        }
        catch (XPathExpressionException e) {
            LoggerFactory.getLogger(MarketHistory.class).error("Error instantiating XPATH", e);
        }
        ITEMS_DIV_XPATH = itemsDivXpath;
        PRICE_XPATH = priceXpath;
        DATE_XPATH = dateXpath;
        REMOVE_XPATH = removeXpath;
        LINK_XPATH = linkXpath;
        WALLET_XPATH = walletXpath;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        int wallet = 0;
        int amount = 0;
        int items = 0;
        DOMParser parser = new DOMParser();
        try {
        	SimpleDateFormat formatter = new SimpleDateFormat("d MMM", Locale.US);
            parser.parse(new InputSource(stream));
            Document document = parser.getDocument();
            String walletStr = ((Node) WALLET_XPATH.evaluate(document, XPathConstants.NODE)).getTextContent().trim();
            wallet = AmountHelper.getAmount(walletStr);
            Node node = (Node) ITEMS_DIV_XPATH.evaluate(document, XPathConstants.NODE);
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node outstandingRow = node.getChildNodes().item(i);
                if (outstandingRow.getAttributes() == null) {
                    continue;
                }
                String clz = outstandingRow.getAttributes().getNamedItem("class").getTextContent();
                if (!clz.contains("market_recent_listing_row")) {
                    continue;
                }
                String priceStr = ((Node) PRICE_XPATH.evaluate(outstandingRow, XPathConstants.NODE))
                        .getTextContent().trim();
                // Skip sold items
                if (priceStr.contains("Sold")) {
                	continue;
                }
                int price = AmountHelper.getAmount(priceStr); 
                String removeScript = ((Node) REMOVE_XPATH.evaluate(outstandingRow, XPathConstants.NODE))
                        .getAttributes().getNamedItem("href").getTextContent();
                String[] scriptParts = removeScript.replace("'", "").split(",");
                String listingId = scriptParts[1].trim();
                int appId = Integer.parseInt(scriptParts[2].trim());
                int contextId = Integer.parseInt(scriptParts[3].trim());
                String link = ((Node) LINK_XPATH.evaluate(outstandingRow, XPathConstants.NODE)).getAttributes()
                        .getNamedItem("href").getTextContent();
                String urlName = link.substring(link.lastIndexOf('/') + 1).trim();
                String date = ((Node)DATE_XPATH.evaluate(outstandingRow, XPathConstants.NODE)).getTextContent().trim();
                
                this.items.add(new OutstandingItem(appId, urlName, listingId, scriptParts[4].trim(), contextId,
                        price, formatter.parse(date)));
                amount += price;
                items++;
            }
            outstandings = new Outstandings(wallet, items, amount, this.items);
        }
        catch (ParseException | RuntimeException e) {
            logger.error("Error getting outstanding items", e);
        }
        catch (SAXException | XPathExpressionException e) {
            logger.error("Error getting outstanding items", e);
		}
    }

    public Outstandings getOutstandings() {
        return outstandings;
    }

    public List<OutstandingItem> getItems() {
        return items;
    }
}
