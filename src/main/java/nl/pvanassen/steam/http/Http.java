package nl.pvanassen.steam.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.AbstractHttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http connection helper
 * 
 * @author Paul van Assen
 */
public class Http {
    private static final int FIVE_MINUTES = 5 * 60 * 1000;
    private final Map<AbstractExecutionAwareRequest, Long> connectionsToWatch = new HashMap<>();
    
    /**
     * @param cookies Cookies to use for the request. This is just a simple string send out to the
     *        server in the most
     *        unsafe way possible
     * @param username Username for the referer
     * @return Returns an instance of the helper
     */
    public static Http getInstance(String cookies, String username) {
        return new Http(cookies, username);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RequestConfig globalConfig;
    private final HttpClientContext context;
    private final String cookies;
    private final String username;

    private Http(String cookies, String username) {
        this.cookies = cookies;
        globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH).setSocketTimeout(10000)
                .build();
        context = HttpClientContext.create();
        this.username = username;
        init();
        WatchDog watchDog = new WatchDog(connectionsToWatch);
        Thread watchDogThread = new Thread(watchDog, "watchDog-Thread-" + username);
        watchDogThread.setPriority(Thread.MIN_PRIORITY);
        watchDogThread.setDaemon(true);
        watchDogThread.start();
    }

    /**
     * Reset cookies
     */
    public void reset() {
        logger.warn("Resetting http");
        init();
    }

    private final void init() {
        CookieStore cookieStore = new BasicCookieStore();
        context.setCookieStore(cookieStore);
        if (!"".equals(cookies)) {
            for (String cookie : cookies.split("; ")) {
                int split = cookie.indexOf('=');
                String parts[] = new String[] { cookie.substring(0, split), cookie.substring(split + 1) };
                if ("Steam_Language".equals(parts[0])) {
                    continue;
                }
                cookieStore.addCookie(getCookie(parts[0], parts[1]));
            }
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
        addHeaders(httpget);

        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        CloseableHttpResponse response = null;
        try {
            connectionsToWatch.put(httpget, System.currentTimeMillis() + FIVE_MINUTES);
            response = httpclient.execute(httpget, context);
            connectionsToWatch.remove(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    // Forbidden, 404, invalid request. Stop
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

    private void addHeaders(AbstractHttpMessage httpMessage) {
        httpMessage.addHeader("Accept", "*/*");
        httpMessage.addHeader("Accept-Language", "en-US,en;q=0.5");
        httpMessage.addHeader("Cache-Control", "no-cache");
        httpMessage.addHeader("Accept-Encoding", "gzip, deflate");
        httpMessage.addHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:28.0) Gecko/20100101 Firefox/28.0");
        httpMessage.addHeader("Referer", "http://steamcommunity.com/id/" + username + "/inventory/");
        httpMessage.addHeader("Origin", "http://steamcommunity.com");
        // httpMessage.addHeader("X-Prototype-Version", "1.7");
        // httpMessage.addHeader("X-Requested-With", "XMLHttpRequest");
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
        HttpPost httpPost = new HttpPost(url);
        addHeaders(httpPost);
        String sessionid = "";
        StringBuilder cookieStr = new StringBuilder();
        for (Cookie cookie : context.getCookieStore().getCookies()) {
            if (cookie.getName().equals("sessionid")) {
                sessionid = cookie.getValue();
            }
            cookieStr.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
        }
        cookieStr.setLength(cookieStr.length() - 2);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
        }
        sb.append("sessionid").append("=").append(sessionid);
        if (sessionid.isEmpty()) {
            logger.error("Error, sessionid empty");
            return;
        }
        logger.info("Posting to url: " + url);
        logger.info("Sending data " + sb.toString());
        logger.info("Sending cookie " + cookieStr.toString());

        httpPost.setEntity(new StringEntity(sb.toString(), ContentType
                .create("application/x-www-form-urlencoded", "UTF-8")));
        httpPost.setHeader("Cookie", cookieStr.toString());
        CloseableHttpClient httpclient = HttpClients.custom().build();
        CloseableHttpResponse response = null;
        try {
            connectionsToWatch.put(httpPost, System.currentTimeMillis() + FIVE_MINUTES);
            response = httpclient.execute(httpPost);
            connectionsToWatch.remove(httpPost);
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
    
    /**
     * @return The current used cookies
     */
    public String getCookies() {
        StringBuilder cookies = new StringBuilder();
        for (Cookie cookie : context.getCookieStore().getCookies()) {
            cookies.append(cookie.getName()).append('=').append(cookie.getValue()).append(';');
        }
        return cookies.toString();
    }
    
    private static class WatchDog implements Runnable {
        private final Logger logger = LoggerFactory.getLogger("watchdog");
        private final Map<AbstractExecutionAwareRequest, Long> connectionsToWatch;
        WatchDog(Map<AbstractExecutionAwareRequest, Long> connectionsToWatch) {
            this.connectionsToWatch = connectionsToWatch;
        }
        
        @Override
        public void run() {
            logger.info("Starting watchdog thread");
            while (true) {
                try {
                    Thread.sleep(60000);
                }
                catch (InterruptedException e) {
                    Thread.interrupted();
                    return;
                }
                long now = System.currentTimeMillis();
                logger.info(connectionsToWatch.size() + " open connections");
                for (Map.Entry<AbstractExecutionAwareRequest, Long> entry : connectionsToWatch.entrySet()) {
                    logger.info("Now: " + now + ", timeout connection: " + entry.getValue());
                    if (entry.getValue() < now) {
                        logger.warn("Killing " + entry.getValue());
                        entry.getKey().abort();
                    }
                }
            }
            
        }
    }
}
