package nl.pvanassen.steam.store.marketpage;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import nl.pvanassen.steam.store.helper.AmountHelper;
import nl.pvanassen.steam.store.helper.UrlNameHelper;
import nl.pvanassen.steam.store.xpath.XPathHelper;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class BuyOrder {
    private static final XPathExpression SECTION = XPathHelper.getXpathExpression("//DIV[@class='my_listing_section market_content_block market_home_listing_table']");
    private static final XPathExpression BUYORDER = XPathHelper.getXpathExpression(".//DIV[@class='market_listing_row market_recent_listing_row']");
    private static final XPathExpression LINK = XPathHelper.getXpathExpression(".//A[@class='market_listing_item_name_link']");
    private static final XPathExpression PRICE = XPathHelper.getXpathExpression(".//SPAN[@class='market_listing_price']");

    private BuyOrder() {
        
    }
    
    static List<MarketPageBuyOrder> getBuyOrders(Document document) {
        try {
            Node node = (Node)SECTION.evaluate(document, XPathConstants.NODE);
            NodeList nodeList = (NodeList)BUYORDER.evaluate(node, XPathConstants.NODESET);
            List<MarketPageBuyOrder> buyOrders = new ArrayList<>(nodeList.getLength());
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                String steamId = item.getAttributes().getNamedItem("id").getTextContent().substring(11);
                Node link = (Node)LINK.evaluate(item, XPathConstants.NODE);
                String href = link.getAttributes().getNamedItem("href").getTextContent();
                int appIdStart = href.indexOf('/', href.indexOf("listings")) + 1;
                int appIdEnd = href.indexOf('/', appIdStart);
                int appId = Integer.valueOf(href.substring(appIdStart, appIdEnd));
                String urlName = UrlNameHelper.getUrlName(href.substring(appIdEnd + 1));
                NodeList priceQuantity = (NodeList)PRICE.evaluate(node, XPathConstants.NODESET);
                Node priceNode = priceQuantity.item(0);
                Node quantityNode  = priceQuantity.item(1);
                int price = AmountHelper.getAmount(priceNode.getTextContent().trim());
                int quantity = Integer.valueOf(quantityNode.getTextContent().trim());
                buyOrders.add(new MarketPageBuyOrder(appId, urlName, steamId, quantity, price));
            }
            return buyOrders;
        }
        catch (XPathExpressionException e) {
            LoggerFactory.getLogger(BuyOrder.class).error("Error getting buy orders", e);
        }
        return null;
    }
}
