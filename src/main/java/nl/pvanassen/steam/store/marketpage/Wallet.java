package nl.pvanassen.steam.store.marketpage;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import nl.pvanassen.steam.store.helper.AmountHelper;
import nl.pvanassen.steam.store.xpath.XPathHelper;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

class Wallet {
    private static final XPathExpression WALLET_XPATH = XPathHelper.getXpathExpression("//SPAN[@id='marketWalletBalanceAmount']");

    private Wallet() {

    }

    static int getWallet(Document document) throws XPathExpressionException {
        int wallet = 0;
        Node walletNode = (Node) WALLET_XPATH.evaluate(document, XPathConstants.NODE);
        if (walletNode != null) {
            wallet = AmountHelper.getAmount(walletNode.getTextContent().trim());
        }
        else {
            LoggerFactory.getLogger(Wallet.class).error("Wallet node is null!");
        }
        if (wallet == 0) {
            LoggerFactory.getLogger(Wallet.class).error("No cash in this account, that's weird?");
        }
        return wallet;
    }

}
