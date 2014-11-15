package nl.pvanassen.steam.store.marketpage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import nl.pvanassen.steam.store.helper.AmountHelper;
import nl.pvanassen.steam.store.xpath.XPathHelper;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

final class Outstandings {
    private static final XPathExpression ITEMS_DIV_XPATH = XPathHelper.getXpathExpression("//DIV[@class='market_content_block my_listing_section market_home_listing_table']");
    private static final XPathExpression PRICE_XPATH = XPathHelper.getXpathExpression(".//SPAN[@class='market_listing_price']");
    private static final XPathExpression DATE_XPATH = XPathHelper.getXpathExpression(".//DIV[@class='market_listing_right_cell market_listing_listed_date']");
    private static final XPathExpression REMOVE_XPATH = XPathHelper.getXpathExpression(".//A[@class='item_market_action_button item_market_action_button_edit nodisable']");
    private static final XPathExpression LINK_XPATH = XPathHelper.getXpathExpression(".//A[@class='market_listing_item_name_link']");
    private Outstandings() {
        
    }
    static List<OutstandingItem> getOutstandingItems(Document document) throws DOMException, XPathExpressionException, ParseException {
        Node node = (Node) ITEMS_DIV_XPATH.evaluate(document, XPathConstants.NODE);
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM", Locale.US);
        List<OutstandingItem> items = new ArrayList<>(node.getChildNodes().getLength());
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node outstandingRow = node.getChildNodes().item(i);
            if (outstandingRow.getAttributes() == null) {
                continue;
            }
            String clz = outstandingRow.getAttributes().getNamedItem("class").getTextContent();
            if (!clz.contains("market_recent_listing_row")) {
                continue;
            }
            String priceStr = ((Node) PRICE_XPATH.evaluate(outstandingRow, XPathConstants.NODE)).getTextContent().trim();
            // Skip sold items
            if (priceStr.contains("Sold")) {
                continue;
            }
            int price = AmountHelper.getAmount(priceStr);
            String removeScript = ((Node) REMOVE_XPATH.evaluate(outstandingRow, XPathConstants.NODE)).getAttributes().getNamedItem("href").getTextContent();
            String[] scriptParts = removeScript.replace("'", "").split(",");
            String listingId = scriptParts[1].trim();
            int appId = Integer.parseInt(scriptParts[2].trim());
            int contextId = Integer.parseInt(scriptParts[3].trim());
            String link = ((Node) LINK_XPATH.evaluate(outstandingRow, XPathConstants.NODE)).getAttributes().getNamedItem("href").getTextContent();
            String urlName = link.substring(link.lastIndexOf('/') + 1).trim();
            String date = ((Node) DATE_XPATH.evaluate(outstandingRow, XPathConstants.NODE)).getTextContent().trim();

            items.add(new OutstandingItem(appId, urlName, listingId, scriptParts[4].trim(), contextId, price, formatter.parse(date)));
        }
        return items;
    }
}
