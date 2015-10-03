package nl.pvanassen.steam.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.AbstractHttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Http connection helper
 *
 * @author Paul van Assen
 */
public class Http {
    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();

    static {
        CONNECTION_MANAGER.setDefaultMaxPerRoute(2);
        CONNECTION_MANAGER.setMaxTotal(4);
    }

    private final CloseableHttpClient httpclient;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final HttpClientContext context;
    private final String cookies;
    private final String username;

    private Http(String cookies, String username) {
        this.cookies = cookies;
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).setSocketTimeout(1000).setConnectionRequestTimeout(2000).setConnectTimeout(2000).build();
        context = HttpClientContext.create();
        this.username = username;
        this.httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).setConnectionManager(CONNECTION_MANAGER).build();
        // IOReactorConfig config = IOReactorConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).setSoReuseAddress(true).build();
        init();
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

    private Cookie getCookie(String name, String value) {
        Calendar expiresCalendar = Calendar.getInstance();
        expiresCalendar.add(Calendar.YEAR, 1000);
        Date expires = expiresCalendar.getTime();
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain("steamcommunity.com");
        cookie.setExpiryDate(expires);
        cookie.setPath("/");
        return cookie;
    }

    private void handleConnection(HttpRequestBase httpget, Handle handle) {
        handleConnection(httpget, handle, 0);
    }

    private void handleConnection(HttpRequestBase httpget, Handle handle, int attempt) {
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
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                // No sleep, shutdown
            }
            if (attempt == 5) {
                throw new RuntimeException(e);
            }
            handleConnection(httpget, handle, attempt + 1);
        } catch (IOException e) {
            logger.error("Error in protocol", e);
            handle.handleException(e);
        }
    }

    private void init() {
        CookieStore cookieStore = new BasicCookieStore();
        context.setCookieStore(cookieStore);
        if (!"".equals(cookies)) {
            for (String cookie : cookies.split("; ")) {
                int split = cookie.indexOf('=');
                if (split == -1) {
                    continue;
                }
                String parts[] = new String[] { cookie.substring(0, split), cookie.substring(split + 1) };
                if ("Steam_Language".equals(parts[0])) {
                    continue;
                }
                cookieStore.addCookie(getCookie(parts[0], parts[1]));
            }
        }
        cookieStore.addCookie(getCookie("Steam_Language", "english"));
    }
    
    private String encode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            // Ok, we're screwed. Just stop
            throw new RuntimeException("Use an OS that does support UTF-8", e);
        }
    }
    
    private String decode(String text) {
        try {
            return URLDecoder.decode(text, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            // Ok, we're screwed. Just stop
            throw new RuntimeException("Use an OS that does support UTF-8", e);
        }
    }

    /**
     * Make a get call to the url using the provided handle
     *
     * @param url The url to call
     * @param handle The handle to use
     * @param ajax Is this an ajax call
     * @param high USe the high prio buffer
     */
    public void get(String url, Handle handle, boolean ajax, boolean high) {
        HttpGet httpget = new HttpGet(url);
        addHeaders(httpget, "http://steamcommunity.com/id/" + username + "/inventory/", ajax);
        handleConnection(httpget, handle, high);
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
                sb.append(entry.getKey()).append("=").append(encode(entry.getValue())).append("&");
            } else {
                sb.append(entry.getKey()).append("=").append(decode(entry.getValue()).replaceAll(" ", "+")).append("&");
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
}
