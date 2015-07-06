package nl.pvanassen.steam.http;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.nio.reactor.IOReactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http connection helper
 *
 * @author Paul van Assen
 */
public class Http {
    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();
    private final CloseableHttpClient httpclient;
    private final CloseableHttpAsyncClient httpAsyncClient;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RequestConfig globalConfig;
    private final HttpClientContext context;
    private final String cookies;
    private final String username;

    static {
        CONNECTION_MANAGER.setDefaultMaxPerRoute(2);
        CONNECTION_MANAGER.setMaxTotal(4);
    }

    /**
     * @param cookies Cookies to use for the request. This is just a simple
     *            string send out to the server in the most unsafe way possible
     * @param username Username for the referer
     * @return Returns an instance of the helper
     */
    public static Http getInstance(String cookies, String username) {
        return new Http(cookies, username);
    }

    private Http(String cookies, String username) {
        this.cookies = cookies;
        globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).setSocketTimeout(10000).build();
        context = HttpClientContext.create();
        this.username = username;
        this.httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).setConnectionManager(CONNECTION_MANAGER).build();
        IOReactorConfig config = IOReactorConfig.custom().setIoThreadCount(2).setSoKeepAlive(true).setTcpNoDelay(true).setSoReuseAddress(true).build();
        this.httpAsyncClient = HttpAsyncClients.custom().setDefaultRequestConfig(globalConfig).setDefaultIOReactorConfig(config).build();
        this.httpAsyncClient.start();
        init();
    }

    private void addHeaders(AbstractHttpMessage httpMessage, String referer, boolean ajax) {
        httpMessage.addHeader("Accept", "*/*");
        httpMessage.addHeader("Accept-Encoding", "gzip, deflate");
        httpMessage.addHeader("Accept-Language", "en-US,en;q=0.5");
        httpMessage.addHeader("Cache-Control", "no-cache");
        httpMessage.addHeader("Connection", "keep-alive");
        httpMessage.addHeader("Host", "steamcommunity.com");
        httpMessage.addHeader("Origin", "http://steamcommunity.com");
        httpMessage.addHeader("Pragma", "no-cache");
        httpMessage.addHeader("Referer", referer);
        httpMessage.addHeader("If-Modified-Since", "Wed, 1 Jan 2014 12:00:00 GMT");
        httpMessage.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:24.0) Gecko/20100101 Firefox/24.0");
        if (ajax) {
            httpMessage.addHeader("X-Prototype-Version", "1.7");
            httpMessage.addHeader("X-Requested-With", "XMLHttpRequest");
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
    private void handleConnectionASync(HttpRequestBase httpget, Handle handle, boolean highPrio) throws IOException {
        
        httpAsyncClient.execute(httpget, new FutureCallback<HttpResponse>() {
            
            @Override
            public void failed(Exception ex) {
                logger.error("Error from async http", ex);
            }
            
            @Override
            public void completed(HttpResponse result) {
                HttpEntity entity = result.getEntity();
                if (entity == null) {
                    return;
                }
                if ("gzip".equals(entity.getContentEncoding().getValue())) {
                    entity = new GzipDecompressingEntity(entity);
                }
                try (InputStream instream = entity.getContent()) {
                    // Forbidden, 404, invalid request. Stop
                    if (result.getStatusLine().getStatusCode() >= 400) {
                        handle.handleError(instream);
                    } else {
                        handle.handle(instream);
                    }
                }
                catch (IOException e) {
                    logger.error("Error from async http", e);
                }
            }
            
            @Override
            public void cancelled() {
                
            }
        });
    }
    
    private void handleConnection(HttpRequestBase httpget, Handle handle, boolean highPrio) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing request with cookies: " + getCookies());
        }
        try (CloseableHttpResponse response = httpclient.execute(httpget, context)) {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return;
            }
            try (InputStream instream = entity.getContent()) {
                // Forbidden, 404, invalid request. Stop
                if (response.getStatusLine().getStatusCode() >= 400) {
                    handle.handleError(instream);
                } else {
                    handle.handle(instream);
                }
            }
        } catch (HttpHostConnectException | InterruptedIOException e) {
            logger.warn("Steam doesn't like me. Slowing down and sleeping a bit");
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e1) {
                // No sleep, shutdown
                return;
            }
        } catch (ClientProtocolException e) {
            logger.error("Error in protocol", e);
            throw e;
        }
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
     * @param ajax Is this an ajax call
     * @param high USe the high prio buffer
     * @throws IOException In case of an error
     */
    public void get(String url, Handle handle, boolean ajax, boolean high) throws IOException {
        HttpGet httpget = new HttpGet(url);
        addHeaders(httpget, "http://steamcommunity.com/id/" + username + "/inventory/", ajax);
        handleConnection(httpget, handle, high);
    }

    /**
     * Make a get call to the url using the provided handle
     *
     * @param url The url to call
     * @param handle The handle to use
     * @param ajax Is this an ajax call
     * @param high USe the high prio buffer
     * @throws IOException In case of an error
     */
    public void getAsync(String url, Handle handle, boolean ajax, boolean high) throws IOException {
        HttpGet httpget = new HttpGet(url);
        addHeaders(httpget, "http://steamcommunity.com/id/" + username + "/inventory/", ajax);
        handleConnectionASync(httpget, handle, high);
    }

    /**
     * @return The current used cookies
     */
    public String getCookies() {
        StringBuilder cookies = new StringBuilder();
        for (Cookie cookie : context.getCookieStore().getCookies()) {
            cookies.append(cookie.getName()).append('=').append(cookie.getValue()).append("; ");
        }
        return cookies.toString();
    }

    /**
     * @return Returns the current session id
     */
    public String getSessionId() {
        for (Cookie cookie : context.getCookieStore().getCookies()) {
            if (cookie.getName().equals("sessionid")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * @param url Url to call
     * @param params Parameters to send with the request
     * @param handle Handle to use
     * @param referer Referer to pass to the server
     * @param sessionRequired Does this request require a session? If not, like
     *            in the case of login, don't fail on it not being present
     * @param reencode Re-encode the parameter
     * @param high Use high prio buffer
     * @throws IOException if a network error occurs
     */
    public void post(String url, Map<String, String> params, Handle handle, String referer, boolean sessionRequired, boolean reencode, boolean high) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        addHeaders(httpPost, referer, true);
        String sessionid = getSessionId();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (reencode) {
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
            } else {
                sb.append(entry.getKey()).append("=").append(URLDecoder.decode(entry.getValue(), "UTF-8").replaceAll(" ", "+")).append("&");
            }
        }
        if (sessionRequired) {
            sb.append("sessionid").append("=").append(sessionid);
            if (sessionid.isEmpty()) {
                logger.error("Error, sessionid empty");
                return;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Sending POST to " + url + " with parameters " + sb.toString());
        }
        httpPost.setEntity(new StringEntity(sb.toString(), ContentType.create("application/x-www-form-urlencoded", "UTF-8")));
        handleConnection(httpPost, handle, high);
    }
}
