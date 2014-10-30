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
    private final List<Tradeoffer> tradeoffers = new LinkedList<>();
    private final Map<String, Item> imageToItemMapping = new HashMap<>();
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
    private static final XPath XPATH = XPATH_FACTORY.newXPath();
    private static final XPathExpression TRADE_OFFERS_XPATH;
    private static final XPathExpression PARTNERID_XPATH;
    private static final XPathExpression OFFER_XPATH;
    private static final XPathExpression QUOTE_XPATH;

    static {
        XPathExpression tradeOffers = null;
        XPathExpression partnerId = null;
        XPathExpression offerId = null;
        XPathExpression quote = null;

        try {
            tradeOffers = XPATH.compile("//DIV[@class='tradeoffer']");
            partnerId = XPATH.compile("//DIV[@class='tradeoffer_partner']/DIV");
            offerId = XPATH.compile("//DIV[@class='link_overlay']");
            quote = XPATH.compile("//DIV[@class='quote']");
        }
        catch (XPathExpressionException e) {
            LoggerFactory.getLogger(ListTradeoffersHandle.class).error("Error instantiating XPATH", e);
        }
        TRADE_OFFERS_XPATH = tradeOffers;
        PARTNERID_XPATH = partnerId;
        OFFER_XPATH = offerId;
        QUOTE_XPATH = quote;
    }

    ListTradeoffersHandle() {
        super();
    }

    public Map<String, Item> getImageToItemMapping() {
        return imageToItemMapping;
    }

    List<Tradeoffer> getTradeoffers() {
        return tradeoffers;
    }

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
                Node linkOverlay = (Node) OFFER_XPATH.evaluate(tradeofferNode, XPathConstants.NODE);
                String onClick = linkOverlay.getAttributes().getNamedItem("onclick").getTextContent();
                int quoteStart = onClick.indexOf('\'') + 1;
                int quoteEnd = onClick.indexOf('\'', quoteStart);
                String offerId = onClick.substring(quoteStart, quoteEnd);
                String quote = ((Node) QUOTE_XPATH.evaluate(tradeofferNode, XPathConstants.NODE)).getFirstChild().getTextContent().trim();
                tradeoffers.add(new Tradeoffer(partnerId, offerId, quote));
            }
        }
        catch (SAXException | XPathExpressionException e) {
            logger.error("Error getting items", e);
        }
    }
}
