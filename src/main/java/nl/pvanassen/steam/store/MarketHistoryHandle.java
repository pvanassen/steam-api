package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


class MarketHistoryHandle extends DefaultHandle {
	private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<MarketHistory> marketHistory = new LinkedList<>();
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
    private static final XPath XPATH = XPATH_FACTORY.newXPath();
    private static final XPathExpression HISTORY_ROW_XPATH;
    private static final XPathExpression GAIN_LOSS_XPATH;
    private static final XPathExpression DATE_XPATH;
    private static final XPathExpression PRICE_XPATH;
    private static final XPathExpression BUYER_XPATH;

    static {
    	XPathExpression historyRowXpath = null;
    	XPathExpression gainLossXpath = null;
    	XPathExpression dateXpath = null;
    	XPathExpression priceXpath = null;
    	XPathExpression buyerXpath = null;
    	try {
    		historyRowXpath = XPATH.compile("//DIV[@class='market_listing_row market_recent_listing_row']");
    		gainLossXpath = XPATH.compile("//DIV[@class='market_listing_left_cell market_listing_gainorloss']");
    		dateXpath = XPATH.compile("//DIV[@class='market_listing_right_cell market_listing_listed_date']");
    		priceXpath = XPATH.compile("//SPAN[@class='market_listing_price']");
    		buyerXpath = XPATH.compile("//DIV[@class='market_listing_whoactedwith_name_block']");
		} catch (XPathExpressionException e) {
			LoggerFactory.getLogger(MarketHistory.class).error("Error instantiating XPATH", e);
		}
    	HISTORY_ROW_XPATH = historyRowXpath;
    	GAIN_LOSS_XPATH = gainLossXpath;
    	DATE_XPATH = dateXpath;
    	PRICE_XPATH = priceXpath;
    	BUYER_XPATH = buyerXpath;
    }

    MarketHistoryHandle() {
        super();
    }
    
    private final class HistoryInfo {
    	private final String listedStr;
    	private final String actedStr;
    	private final String price;
    	private final String buyer;
		HistoryInfo(String listedStr, String actedStr, String price, String buyer) {
			super();
			this.listedStr = listedStr;
			this.actedStr = actedStr;
			this.price = price;
			this.buyer = buyer;
		}
    	
    }
    
    @Override
    public void handle(InputStream stream) throws IOException {
        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree( stream );
        String resultHtml = node.get("results_html").asText();
        Map<String,HistoryInfo> htmlMap = new HashMap<>();

        DOMFragmentParser parser = new DOMFragmentParser();
        HTMLDocument document = new HTMLDocumentImpl();
        try {
        	DocumentFragment fragment = document.createDocumentFragment();
        	parser.parse(new InputSource( new StringReader( resultHtml ) ), fragment );
        	
        	
        	NodeList nodeSet = (NodeList) HISTORY_ROW_XPATH.evaluate(fragment, XPathConstants.NODESET);
        	for(int i=0; i<nodeSet.getLength() ; i++) {
        		Node historyRow = nodeSet.item(i);
        		String rowName = historyRow.getAttributes().getNamedItem("id").getTextContent();
        		Node gainLoss = (Node)GAIN_LOSS_XPATH.evaluate(historyRow, XPathConstants.NODE);
        		if (!"-".equals(gainLoss.getTextContent())) {
        			// No sale
        			continue;
        		}
        		NodeList dates = (NodeList)DATE_XPATH.evaluate(historyRow, XPathConstants.NODESET);
        		String listedStr = dates.item(0).getTextContent();
        		String actedStr = dates.item(0).getTextContent();
        		String price = ((Node)PRICE_XPATH.evaluate(historyRow, XPathConstants.NODE)).getTextContent();
        		String buyer = ((Node)BUYER_XPATH.evaluate(historyRow, XPathConstants.NODE)).getTextContent();
        		htmlMap.put(rowName, new HistoryInfo(listedStr, actedStr, price, buyer));
        	}
        }
        catch (SAXException | XPathExpressionException e) {
        	logger.error("Error parsing html", e);
		}
        Map<String,String> hoverMap = new HashMap<>();
        String hovers = node.get("hovers").asText();
        for (String hoverStr : hovers.split(";")) {
        	String hover = hoverStr.trim();
        	if (hover.startsWith("CreateItemHoverFromContainer")) {
        		// CreateItemHoverFromContainer( g_rgAssets, 'history_row_2853334817499652196_2853334817499652205_name', 753, '6', '619156810', 0 );
        		String[] items = hover.replaceAll("'", "").split(",");
        		hoverMap.put(items[4], items[1]);
        	}
        }
        SimpleDateFormat formatter = new SimpleDateFormat("d M", Locale.US);
        JsonNode assets = node.get("assets");
        for (JsonNode appId : assets) {
            for (JsonNode contextId : appId) {
                for (JsonNode item : contextId) {
                	String historyRowId = hoverMap.get(item.get("id"));
                	HistoryInfo historyInfo = htmlMap.get(historyRowId);
                	if (historyInfo == null) {
                		// Not a sale
                		continue;
                	}
                    String urlName = URLEncoder.encode( item.get( "market_hash_name" ).asText(), "UTF-8" ).replace( "+", "%20" );
                    // 17 Mar
                    try {
	                    Date acted = formatter.parse(historyInfo.actedStr);
	                    Date listed = formatter.parse(historyInfo.listedStr);
	                    // 0,10&#8364;
	                    int price = Integer.parseInt(historyInfo.price.replace(",", "").replace("&#8364;", "").trim());
	                    marketHistory.add(new MarketHistory(appId.asInt(), contextId.asInt(), urlName, listed, acted, price, historyInfo.buyer));
                    }
                    catch (ParseException e) {
                    	logger.error("Error parsing date", e);
                    }
                }
            }
        }
    }
    
    List<MarketHistory> getMarketHistory() {
        return marketHistory;
    }
}
