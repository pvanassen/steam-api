package nl.pvanassen.steam.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http connection helper
 * 
 * @author Paul van Assen
 */
public class Http {

    /**
     * @param cookies Cookies to use for the request. This is just a simple string send out to the server in the most
     *            unsafe way possible
     * @return Returns an instance of the helper
     */
    public static Http getInstance(String cookies) {
        return new Http(cookies);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RequestConfig globalConfig;

    private final HttpClientContext context;
    
    private final String cookies;

    private Http(String cookies) {
        this.cookies = cookies;
        globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH).build();
        context = HttpClientContext.create();
        init();
    }
    
    /**
     * Reset cookies
     */
    public void reset() {
        logger.warn( "Resetting http" );
        init();
    }
    
    private final void init() {
        CookieStore cookieStore = new BasicCookieStore();
        context.setCookieStore(cookieStore);
        for (String cookie : cookies.split("; ")) {
            String parts[] = cookie.split("=");
            if ("Steam_Language".equals(parts[0])) {
                continue;
            }
            cookieStore.addCookie(getCookie(parts[0], parts[1]));
        }
        cookieStore.addCookie(getCookie("Steam_Language", "english"));
    }

    /**
     * Make a get call to the url using the provided handle
     * 
     * @param url The url to call
     * @param handle The handle to use
     * @throws IOException In case of an error
     */
    public void get(String url, Handle handle) throws IOException {
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpget.addHeader("Accept-Language", "nl,en-us;q=0.7,en;q=0.3");
        httpget.addHeader("Accept-Encoding", "gzip, deflate");
        httpget.addHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:25.0) Gecko/20100101 Firefox/25.0");
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpget, context);
            // Forbidden, 404, invalid request. Stop
            if (response.getStatusLine().getStatusCode() >= 400) {
                throw new RuntimeException("Error getting data " + response.getStatusLine() + " for url " + url);
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    handle.handle(instream);
                }
                finally {
                    IOUtils.closeQuietly(instream);
                }
            }
        }
        catch (HttpHostConnectException e) {
            logger.warn("Steam doesn't like me. Slowing down and sleeping a bit");
            try {
                Thread.sleep(30000);
            }
            catch (InterruptedException e1) {
                // No sleep, shutdown
                return;
            }
            get(url, handle);
        }
        catch (ClientProtocolException e) {
            logger.error("Error in protocol", e);
            throw e;
        }
        finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpclient);
        }
    }

    private final Cookie getCookie(String name, String value) {
        Calendar expiresCalendar = Calendar.getInstance();
        expiresCalendar.add(Calendar.YEAR, 1000);
        Date expires = expiresCalendar.getTime();
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain("steamcommunity.com");
        cookie.setExpiryDate(expires);
        cookie.setPath("/");
        return cookie;
    }

    /**
     * @param url Url to call
     * @param params Parameters to send with the request
     * @param handle Handle to use
     * @throws IOException if a network error occurs
     */
    public void post(String url, Map<String, String> params, Handle handle) throws IOException {
        HttpPost httpget = new HttpPost(url);
        httpget.addHeader("Accept", "*/*");
        httpget.addHeader("Accept-Language", "nl,en-us;q=0.7,en;q=0.3");
        httpget.addHeader("Accept-Encoding", "gzip, deflate");
        httpget.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpget.addHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:25.0) Gecko/20100101 Firefox/25.0");
        httpget.addHeader("Referer", "  http://steamcommunity.com/id/mantorch/inventory/");
        String sessionid = "";
        for (Cookie cookie : context.getCookieStore().getCookies()) {
            if (cookie.getName().equals("sessionid")) {
                sessionid = cookie.getValue();
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sb.append("sessionid").append("=").append(sessionid);

        httpget.setEntity(new StringEntity(sb.toString()));
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpget, context);
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            // Forbidden, 404, invalid request. Stop
            try {
                if (response.getStatusLine().getStatusCode() >= 400) {
                    handle.handleError(instream);
                }
                else {
                    handle.handle(instream);
                }
            }
            finally {
                IOUtils.closeQuietly(instream);
            }

        }
        catch (ClientProtocolException e) {
            logger.error("Error in protocol", e);
            throw e;
        }
        finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpclient);
        }
    }

}
