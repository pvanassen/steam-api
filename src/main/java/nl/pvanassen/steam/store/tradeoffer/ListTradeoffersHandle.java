package nl.pvanassen.steam.store.tradeoffer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.*;

import nl.pvanassen.steam.http.DefaultHandle;
import nl.pvanassen.steam.store.common.Item;
import nl.pvanassen.steam.store.xpath.XPathHelper;

import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class ListTradeoffersHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<TradeOffer> tradeoffers = new LinkedList<>();
    private final Map<String, Item> imageToItemMapping = new HashMap<>();
    private static final XPathExpression TRADE_OFFERS_XPATH = XPathHelper.getXpathExpression("//DIV[@class='tradeoffer']");
    private static final XPathExpression PARTNERID_XPATH = XPathHelper.getXpathExpression("//DIV[@class='tradeoffer_partner']/DIV");
    private static final XPathExpression OFFER_XPATH = XPathHelper.getXpathExpression("//DIV[@class='link_overlay']");
    private static final XPathExpression QUOTE_XPATH = XPathHelper.getXpathExpression("//DIV[@class='quote']");

    ListTradeoffersHandle() {
        super();
    }

    Map<String, Item> getImageToItemMapping() {
        return imageToItemMapping;
    }

    List<TradeOffer> getTradeoffers() {
        return tradeoffers;
    }

    /**
     * {@inheritDoc}
     *
     * @see nl.pvanassen.steam.http.DefaultHandle#handle(java.io.InputStream)
     */
    @Override
    public void handle(InputStream stream) throws IOException {
        DOMParser parser = new DOMParser();
        try {
            parser.parse(new InputSource(stream));
            Document document = parser.getDocument();
            NodeList tradeoffersNode = (NodeList) TRADE_OFFERS_XPATH.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i != tradeoffersNode.getLength(); i++) {
                Node tradeofferNode = tradeoffersNode.item(i);
                Node partnerNode = (Node) PARTNERID_XPATH.evaluate(tradeofferNode, XPathConstants.NODE);
                String partnerId = partnerNode.getAttributes().getNamedItem("data-miniprofile").getNodeValue();
                String offerId = tradeofferNode.getAttributes().getNamedItem("id").getTextContent().replace("tradeofferid_", "");
                Node linkOverlay = (Node) OFFER_XPATH.evaluate(tradeofferNode, XPathConstants.NODE);
                String onClick = linkOverlay.getAttributes().getNamedItem("onclick").getTextContent();
                int quoteStart = onClick.indexOf('\'') + 1;
                int quoteEnd = onClick.indexOf('\'', quoteStart);
                String quote = ((Node) QUOTE_XPATH.evaluate(tradeofferNode, XPathConstants.NODE)).getFirstChild().getTextContent().trim();
                tradeoffers.add(new TradeOffer(partnerId, offerId, quote));
            }
        }
        catch (SAXException | XPathExpressionException e) {
            logger.error("Error getting items", e);
        }
    }
}
