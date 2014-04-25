package nl.pvanassen.steam.store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import nl.pvanassen.steam.http.DefaultHandle;

class WalletHandle extends DefaultHandle {

    private int wallet;

    int getWallet() {
        return wallet;
    }

    @Override
    public void handle(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            int mstart = line.indexOf("marketWalletBalanceAmount");
            if (mstart >= 0) {
                int start = line.indexOf('>', mstart);
                int end = line.indexOf('<', start);
                String amount = line.substring(start + 1, end);
                wallet = Integer.parseInt(amount.replace("&#8364;", "").replace(",", "").replace("-", "0").trim());
                return;
            }
        }
    }
}
