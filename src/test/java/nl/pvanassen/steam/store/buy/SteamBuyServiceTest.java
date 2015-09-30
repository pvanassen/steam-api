package nl.pvanassen.steam.store.buy;

import nl.pvanassen.steam.http.Handle;
import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.store.CookieException;
import nl.pvanassen.steam.store.StreamHelper;
import nl.pvanassen.steam.store.common.BuyOrder;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;

public class SteamBuyServiceTest {

    @SuppressWarnings("unchecked")
    @Test(expected = CookieException.class)
    public void testCookieError() throws IOException {
        Http http = mock(Http.class);
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                BuyHandle handle = (BuyHandle) args[2];
                handle.handleError(StreamHelper.getStream("/buyhandle-cookie-error.json"));
                return null;
            }
        })
                .when(http)
                .post(eq("https://steamcommunity.com/market/buylisting/12345"), (Map<String, String>) anyMap(), (Handle) anyObject(),
                        eq("http://steamcommunity.com/id/testuser/inventory/"), eq(true), eq(false), eq(true));
        SteamBuyService steamBuyService = new SteamBuyService(http, "testuser");
        steamBuyService.buy(new BuyOrder(1, "test", "12345", 10, 4));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSuccess() throws IOException {
        Http http = mock(Http.class);
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                BuyHandle handle = (BuyHandle) args[2];
                handle.handle(StreamHelper.getStream("/buyhandle-success.json"));
                return null;
            }
        })
                .when(http)
                .post(eq("https://steamcommunity.com/market/buylisting/12345"), (Map<String, String>) anyMap(), (Handle) anyObject(),
                        eq("http://steamcommunity.com/id/testuser/inventory/"), eq(true), eq(false), eq(true));
        SteamBuyService steamBuyService = new SteamBuyService(http, "testuser");
        BuyResult result = steamBuyService.buy(new BuyOrder(1, "test", "12345", 10, 4));
        assertNotNull(result);
        assertEquals(27485, result.getWallet());
    }
}
