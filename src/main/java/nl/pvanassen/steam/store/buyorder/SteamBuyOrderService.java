package nl.pvanassen.steam.store.buyorder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nl.pvanassen.steam.error.SteamException;
import nl.pvanassen.steam.http.Http;
import nl.pvanassen.steam.store.common.Item;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SteamBuyOrderService implements BuyOrderService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Http http;
    private final String username;

    /**
     * @param http
     *            For mocking
     */
    public SteamBuyOrderService(Http http, String username) {
        this.http = http;
        this.username = username;
    }

    public SteamBuyOrderService(String cookies, String username) {
        http = Http.getInstance(cookies, username);
        this.username = username;
    }

    @Override
    public void cancelBuyOrder(String id) {
        // curl 'http://steamcommunity.com/market/cancelbuyorder/' -H 'Accept:
        // text/javascript, text/html, application/xml, text/xml, */*' -H
        // 'Accept-Encoding: gzip, deflate' -H 'Accept-Language:
        // nl,en-US;q=0.7,en;q=0.3' -H 'Cache-Control: no-cache' -H 'Connection:
        // keep-alive' -H 'Content-Type: application/x-www-form-urlencoded;
        // charset=UTF-8' -H 'Cookie: timezoneOffset=7200,0;
        // Steam_Language=english;
        // __utma=268881843.198519209.1391434770.1403166922.1403242194.84;
        // __utmz=268881843.1402998924.74.7.utmcsr=store.steampowered.com|utmccn=(referral)|utmcmd=referral|utmcct=/;
        // strInventoryLastContext=238460_2; __utmc=268881843;
        // sessionid=MzkxNTEzOTk2;
        // steamRememberLogin=76561198036110246%7C%7C22ce98f2d69b7e260f39303330324df7;
        // webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22sales_this_year%22%3A16057%2C%22max_sales_per_year%22%3A-1%2C%22forms_requested%22%3A0%2C%22new_device_cooldown_days%22%3A7%7D;
        // steamLogin=76561198036110246%7C%7CB9B45A5B3185AF74F3E3523317E591D9D4905146;
        // steamCC_84_24_113_147=NL; __utmb=268881843.0.10.1403242194' -H 'Host:
        // steamcommunity.com' -H 'Pragma: no-cache' -H 'Referer:
        // http://steamcommunity.com/market/listings/753/303700-Summer%20Adventure%20Card%201'
        // -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:30.0)
        // Gecko/20100101 Firefox/30.0' -H 'X-Prototype-Version: 1.7' -H
        // 'X-Requested-With: XMLHttpRequest'
        // --data 'sessionid=MzkxNTEzOTk2&buy_orderid=198984'
        // {"success":1}
        Map<String, String> params = new HashMap<>();
        params.put("buy_orderid", id);
        BuyOrderHandle handle = new BuyOrderHandle(objectMapper);
        try {
            http.post("http://steamcommunity.com/market/cancelbuyorder/", params, handle, "http://steamcommunity.com/id/" + username + "/inventory/");
        }
        catch (IOException e) {
            logger.error("Error canceling buy order", e);
            throw new SteamException("Error canceling buy order", e);
        }
        if (handle.isError()) {
            throw new SteamException(handle.getMessage());
        }
    }

    @Override
    public String createBuyOrder(Item item, int currencyId, int priceTotal, int quantity) {
        // curl 'https://steamcommunity.com/market/createbuyorder/' -H 'Accept:
        // */*' -H 'Accept-Encoding: gzip, deflate' -H 'Accept-Language:
        // nl,en-US;q=0.7,en;q=0.3' -H 'Cache-Control: no-cache' -H 'Connection:
        // keep-alive' -H 'Content-Type: application/x-www-form-urlencoded;
        // charset=UTF-8' -H 'Cookie: timezoneOffset=7200,0;
        // steamMachineAuth76561197997047916=957FE57BD7190A29EC64566E4F59F53D266DD9AA;
        // Steam_Language=english;
        // __utma=268881843.198519209.1391434770.1403166922.1403242194.84;
        // __utmz=268881843.1402998924.74.7.utmcsr=store.steampowered.com|utmccn=(referral)|utmcmd=referral|utmcct=/;
        // strInventoryLastContext=238460_2;
        // steamMachineAuth76561198036110246=0541BE57DFC72B046C381F32CF05BC61C44A4751;
        // __utmc=268881843; sessionid=MzkxNTEzOTk2;
        // steamRememberLogin=76561198036110246%7C%7C22ce98f2d69b7e260f39303330324df7;
        // webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22sales_this_year%22%3A16057%2C%22max_sales_per_year%22%3A-1%2C%22forms_requested%22%3A0%2C%22new_device_cooldown_days%22%3A7%7D;
        // steamLogin=76561198036110246%7C%7CB9B45A5B3185AF74F3E3523317E591D9D4905146;
        // steamCC_84_24_113_147=NL; __utmb=268881843.0.10.1403242194' -H 'Host:
        // steamcommunity.com' -H 'Origin: http://steamcommunity.com' -H
        // 'Pragma: no-cache' -H 'Referer:
        // http://steamcommunity.com/market/listings/753/303700-Summer%20Adventure%20Card%201'
        // -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:30.0)
        // Gecko/20100101 Firefox/30.0'
        // --data
        // 'sessionid=MzkxNTEzOTk2&currency=3&appid=753&market_hash_name=303700-Summer+Adventure+Card+1&price_total=45&quantity=3'
        // {"success":1,"buy_orderid":"198984"}
        Map<String, String> params = new HashMap<>();
        params.put("currency", Integer.toString(currencyId));
        params.put("appid", Integer.toString(item.getAppId()));
        params.put("market_hash_name", item.getUrlName());
        params.put("price_total", Integer.toString(priceTotal));
        params.put("quantity", Integer.toString(quantity));
        BuyOrderHandle handle = new BuyOrderHandle(objectMapper);
        try {
            http.post("https://steamcommunity.com/market/createbuyorder/", params, handle, item.getUrl());
        }
        catch (IOException e) {
            logger.error("Error creating a buy order", e);
            throw new SteamException("Error creating a buy order", e);
        }
        if (handle.isError()) {
            throw new SteamException(handle.getMessage());
        }
        return handle.getBuyOrderId();
    }

    @Override
    public BuyOrderStatus getBuyOrderStatus(String buyOrderId) {
        // curl
        // 'http://steamcommunity.com/market/getbuyorderstatus/?sessionid=MzkxNTEzOTk2&buy_orderid=198984'
        // -H 'Accept: */*' -H 'Accept-Encoding: gzip, deflate' -H
        // 'Accept-Language: nl,en-US;q=0.7,en;q=0.3' -H 'Connection:
        // keep-alive' -H 'Cookie: timezoneOffset=7200,0;
        // Steam_Language=english;
        // __utma=268881843.198519209.1391434770.1403166922.1403242194.84;
        // __utmz=268881843.1402998924.74.7.utmcsr=store.steampowered.com|utmccn=(referral)|utmcmd=referral|utmcct=/;
        // strInventoryLastContext=238460_2; __utmc=268881843;
        // sessionid=MzkxNTEzOTk2;
        // steamRememberLogin=76561198036110246%7C%7C22ce98f2d69b7e260f39303330324df7;
        // webTradeEligibility=%7B%22allowed%22%3A1%2C%22allowed_at_time%22%3A0%2C%22steamguard_required_days%22%3A15%2C%22sales_this_year%22%3A16057%2C%22max_sales_per_year%22%3A-1%2C%22forms_requested%22%3A0%2C%22new_device_cooldown_days%22%3A7%7D;
        // steamLogin=76561198036110246%7C%7CB9B45A5B3185AF74F3E3523317E591D9D4905146;
        // steamCC_84_24_113_147=NL; __utmb=268881843.0.10.1403242194' -H 'Host:
        // steamcommunity.com' -H 'Referer:
        // http://steamcommunity.com/market/listings/753/303700-Summer%20Adventure%20Card%201'
        // -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:30.0)
        // Gecko/20100101 Firefox/30.0' -H 'X-Requested-With: XMLHttpRequest'
        // {"success":1,"active":1,"purchased":0,"quantity":"3","quantity_remaining":"3","purchases":[]}
        String sessionId = http.getSessionId();
        BuyOrderStatusHandle handle = new BuyOrderStatusHandle(objectMapper);
        try {
            http.get("http://steamcommunity.com/market/getbuyorderstatus/?sessionid=" + sessionId + "&buy_orderid=" + buyOrderId, handle);
        }
        catch (IOException e) {
            logger.error("Error getting status of a buy order", e);
            throw new SteamException("Error getting status of a buy order", e);
        }
        return handle.getBuyOrderStatus();
    }
}
