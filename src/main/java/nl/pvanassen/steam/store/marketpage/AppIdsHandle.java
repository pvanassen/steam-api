package nl.pvanassen.steam.store.marketpage;

import com.google.common.collect.ImmutableSet;
import nl.pvanassen.steam.http.DefaultHandle;
import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

class AppIdsHandle extends DefaultHandle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
    private static final XPath XPATH = XPATH_FACTORY.newXPath();
    private static final XPathExpression APPIDS_XPATH;
    private final Set<Integer> appIds = new HashSet<>();

    static {
        XPathExpression appIdsXpath = null;
        try {
            appIdsXpath = XPATH.compile("//A[@class='game_button']");
        }
        catch (XPathExpressionException e) {
            LoggerFactory.getLogger(AppIdsHandle.class).error("Error instantiating XPATH", e);
        }
        APPIDS_XPATH = appIdsXpath;
    }

    Set<Integer> getAppIds() {
        return ImmutableSet.copyOf(appIds);
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        DOMParser parser = new DOMParser();
        try {
            parser.parse(new InputSource(stream));
            Document document = parser.getDocument();
            NodeList appIdsNodes = (NodeList) APPIDS_XPATH.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i != appIdsNodes.getLength(); i++) {
                Node appIdNode = appIdsNodes.item(i);
                String appIdStr = appIdNode.getAttributes().getNamedItem("href").getTextContent().split("=")[1];
                appIds.add(Integer.parseInt(appIdStr));
            }
        }
        catch (SAXException | XPathExpressionException | RuntimeException e) {
            logger.error("Error getting app ids items", e);
        }
    }
}
