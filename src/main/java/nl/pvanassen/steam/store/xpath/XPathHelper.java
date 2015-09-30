package nl.pvanassen.steam.store.xpath;

import org.slf4j.LoggerFactory;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Get xpath expression
 * @author Paul van Assen
 *
 */
public class XPathHelper {
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
    private static final XPath XPATH = XPATH_FACTORY.newXPath();
    
    private XPathHelper() {
        super();
    }
    
    /**
     * Generate xpath expression from string
     * @param xpath Xpath
     * @return Parsed expression
     */
    public static XPathExpression getXpathExpression(String xpath) {
        try {
            return XPATH.compile(xpath);
        }
        catch (XPathExpressionException e) {
            LoggerFactory.getLogger(XPathHelper.class).error("Error instantiating XPATH", e);
        }
        return null;
    }
}
