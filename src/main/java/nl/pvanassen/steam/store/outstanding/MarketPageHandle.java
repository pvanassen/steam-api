package nl.pvanassen.steam.store.outstanding;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.xpath.*;

import nl.pvanassen.steam.http.DefaultHandle;
import nl.pvanassen.steam.store.helper.AmountHelper;
import nl.pvanassen.steam.store.history.History;

import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class MarketPageHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<OutstandingItem> items = new LinkedList<>();
    private MarketPage outstandings;
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
    private static final XPath XPATH = XPATH_FACTORY.newXPath();
    private static final XPathExpression ITEMS_DIV_XPATH;
    private static final XPathExpression PRICE_XPATH;
    private static final XPathExpression DATE_XPATH;
    private static final XPathExpression REMOVE_XPATH;
    private static final XPathExpression LINK_XPATH;
    private static final XPathExpression WALLET_XPATH;
    private static final XPathExpression APPIDS_XPATH;

    static {
        XPathExpression itemsDivXpath = null;
        XPathExpression priceXpath = null;
        XPathExpression dateXpath = null;
        XPathExpression removeXpath = null;
        XPathExpression linkXpath = null;
        XPathExpression walletXpath = null;
        XPathExpression appIdsXpath = null;
        
        try {
            itemsDivXpath = XPATH.compile("//DIV[@class='market_content_block my_listing_section market_home_listing_table']");
            priceXpath = XPATH.compile(".//SPAN[@class='market_listing_price']");
            dateXpath = XPATH.compile(".//DIV[@class='market_listing_right_cell market_listing_listed_date']");
            removeXpath = XPATH.compile(".//A[@class='item_market_action_button item_market_action_button_edit nodisable']");
            linkXpath = XPATH.compile(".//A[@class='market_listing_item_name_link']");
            walletXpath = XPATH.compile("//SPAN[@id='marketWalletBalanceAmount']");
            appIdsXpath = XPATH.compile("//A[@class='game_button']");
        }
        catch (XPathExpressionException e) {
            LoggerFactory.getLogger(History.class).error("Error instantiating XPATH", e);
        }
        ITEMS_DIV_XPATH = itemsDivXpath;
        PRICE_XPATH = priceXpath;
        DATE_XPATH = dateXpath;
        REMOVE_XPATH = removeXpath;
        LINK_XPATH = linkXpath;
        WALLET_XPATH = walletXpath;
        APPIDS_XPATH = appIdsXpath;
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
            Node walletNode = (Node) WALLET_XPATH.evaluate(document, XPathConstants.NODE);
            if (walletNode != null) {
            	wallet = AmountHelper.getAmount(walletNode.getTextContent().trim());
            }
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
            Set<Integer> appIds = new HashSet<>();
            NodeList appIdsNodes = (NodeList)APPIDS_XPATH.evaluate(document, XPathConstants.NODESET);
            for (int i=0;i!=appIdsNodes.getLength();i++) {
                Node appIdNode = appIdsNodes.item(i);
                String appIdStr = appIdNode.getAttributes().getNamedItem("href").getTextContent().split("=")[1];
                appIds.add(Integer.parseInt(appIdStr));
            }
            outstandings = new MarketPage(wallet, items, amount, this.items, appIds);
        }
        catch (ParseException | RuntimeException e) {
            logger.error("Error getting outstanding items", e);
        }
        catch (SAXException | XPathExpressionException e) {
            logger.error("Error getting outstanding items", e);
		}
    }

    public MarketPage getOutstandings() {
        return outstandings;
    }

    public List<OutstandingItem> getItems() {
        return items;
    }
}
