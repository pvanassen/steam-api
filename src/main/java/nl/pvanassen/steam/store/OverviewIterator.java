package nl.pvanassen.steam.store;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import javax.xml.xpath.*;

import nl.pvanassen.steam.http.DefaultHandle;
import nl.pvanassen.steam.http.Http;

import org.apache.commons.io.output.NullOutputStream;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.w3c.tidy.Tidy;

class OverviewIterator implements Iterator<OverviewItem>, Iterable<OverviewItem> {

    private class GetCurrentItemsTask implements Callable<String> {

        private final Logger logger = LoggerFactory.getLogger( getClass() );
        private final int start;
        private final ObjectMapper om;
        private final Http http;

        GetCurrentItemsTask( Http http, int start, ObjectMapper om ) {
            this.http = http;
            this.start = start;
            this.om = om;
        }
        
        @Override
        public String call() throws Exception {
            JsonHandle handle = new JsonHandle( om );
            try {
                http.get( "http://steamcommunity.com/market/search/render/?query=&search_descriptions=0&start=" + start + "&count=" + 100, handle );
            }
            catch ( IOException e ) {
                logger.error( "Error getting data", e );
            }
            return handle.getHtml();
        }
    }

    static class JsonHandle extends DefaultHandle {

        private final ObjectMapper om;

        private String html;

        private int totalCount;

        JsonHandle( ObjectMapper om ) {
            this.om = om;
        }

        String getHtml() {
            return html;
        }

        int getTotalCount() {
            return totalCount;
        }

        @Override
        public void handle( InputStream stream ) throws IOException {
            JsonNode node = om.readTree( stream );
            html = node.get( "results_html" ).asText();
            totalCount = node.get( "total_count" ).asInt();
        }
    }

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    static final int MAX_PAGE_SIZE = 100;

    private final Tidy tidy = TidyHelper.getTidy();

    private final ObjectMapper om = new ObjectMapper();

    private OverviewItem nextItem;

    private int start = 0;

    private int totalCount = 0;

    private final XPathExpression rowXpath;

    private final LinkedList<OverviewItem> items = new LinkedList<>();

    private final Queue<Future<String>> futurePages;

    OverviewIterator(Http http, ExecutorService executorService) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        try {
            rowXpath = xpath.compile( "//a[@class='market_listing_row_link']" );
        }
        catch ( XPathExpressionException e ) {
            throw new RuntimeException( e );
        }
        JsonHandle handle = new JsonHandle( om );
        // Get first page to find max of pages
        try {
            http.get( "http://steamcommunity.com/market/search/render/?query=&search_descriptions=0&start=" + start + "&count=" + MAX_PAGE_SIZE, handle );
        }
        catch ( IOException e ) {
            logger.error( "Error getting first page", e );
        }
        if ( totalCount == 0 ) {
            totalCount = handle.getTotalCount();
        }
        // Create pool and start fetching
        futurePages = new LinkedBlockingQueue<Future<String>>();
        for ( int start = 0; start < totalCount; start += MAX_PAGE_SIZE ) {
            GetCurrentItemsTask task = new GetCurrentItemsTask( http, start, om );
            futurePages.offer( executorService.submit( task ) );
        }
    }

    private synchronized OverviewItem getNextItem() {
        if ( !items.isEmpty() ) {
            return items.removeLast();
        }
        // Safe guard
        if ( start > 90000 ) {
            return null;
        }
        String html;
        try {
            logger.info( "Fetching page " + start );
            if ( futurePages.isEmpty()) {
                // Last page
                return null;
            }
            html = futurePages.poll().get();
            if ( html.contains( "market_listing_table_message" ) ) {
                logger.info( "Last page hit" );
                // Done
                return null;
            }
            Document document = tidy.parseDOM( new ByteArrayInputStream( html.getBytes() ), new NullOutputStream() );
            NodeList nodeList = ( NodeList ) rowXpath.evaluate( document, XPathConstants.NODESET );
            for ( int i = 0; i < nodeList.getLength(); i++ ) {
                Node node = nodeList.item( i );
                String link = node.getAttributes().getNamedItem( "href" ).getNodeValue();
                int startName = link.lastIndexOf( '/' );
                int startAppId = link.lastIndexOf( '/', startName - 1 );
                String urlName = link.substring( startName + 1 );
                String steamId = "unknown";
                int idx = urlName.indexOf( '-' );
                if ( idx > -1 ) {
                    steamId = urlName.substring( 0, idx );
                }
                int appId = Integer.valueOf( link.substring( startAppId + 1, startName ) );
                logger.info( "Found: " + urlName + ", appid: " + appId );

                Node spanNode = node.getNextSibling().getChildNodes().item( 2 ).getFirstChild();
                int currentPrice = Integer.valueOf( spanNode.getLastChild().getNodeValue().trim().substring( 1 ).replace( "€", "" ).replace( ",", "" ).replace( "--", "00" ) );
                int currentOffers = Integer.valueOf( spanNode.getChildNodes().item( 1 ).getFirstChild().getNodeValue().replace( ",", "" ) );

                Node nameNode = node.getNextSibling().getChildNodes().item( 3 );
                String name = nameNode.getFirstChild().getFirstChild().getNodeValue().trim();
                String gameName = nameNode.getLastChild().getFirstChild().getNodeValue().trim();

                items.add( new OverviewItem( appId, name, urlName, currentOffers, currentPrice, gameName, steamId ) );
            }
            start += MAX_PAGE_SIZE;
        }
        catch ( XPathExpressionException | ExecutionException
                | InterruptedException e ) {
            logger.error( "Error parsing steam", e );
            return null;
        }
        if ( !items.isEmpty() ) {
            return items.removeLast();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        if ( nextItem != null ) {
            return true;
        }
        nextItem = getNextItem();
        return nextItem != null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<OverviewItem> iterator() {
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#next()
     */
    @Override
    public OverviewItem next() {
        OverviewItem item;
        if ( hasNext() ) {
            item = nextItem;
            nextItem = null;
            return item;
        }
        throw new NoSuchElementException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException( "Cannot remove" );
    }
}