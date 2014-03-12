package nl.pvanassen.steam.store;

import java.io.IOException;
import java.io.InputStream;

import nl.pvanassen.steam.http.DefaultHandle;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

class BuyHandle extends DefaultHandle {

    private int wallet;
    private boolean error = false;

    int getWallet() {
        return wallet;
    }

    @Override
    public void handle( InputStream stream ) throws IOException {
        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree( stream );
        JsonNode walletInfo = node.get( "wallet_info" );
        wallet = walletInfo.get( "wallet_balance" ).asInt();
    }

    @Override
    public void handleError( InputStream stream ) throws IOException {
        error = true;
        super.handle( stream );
    }

    boolean isError() {
        return error;
    }
}
