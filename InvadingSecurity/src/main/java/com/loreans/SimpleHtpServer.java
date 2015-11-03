package com.loreans;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class SimpleHtpServer {

    static HttpServer server;

    public static void main(String[] args) throws Exception {
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {

        public void handle(HttpExchange t) throws IOException {
            String query = t.getRequestURI().getQuery();
            String rawQuery = t.getRequestURI().getRawQuery();
            Map map = null;
            try {
                 map = queryToMap(query);
            }catch (Exception e){
                String errorMessage = "something went terribly wrong!";
                t.sendResponseHeaders(400,errorMessage.length());
                t.getResponseBody().write(errorMessage.getBytes());
                t.getResponseBody().close();
            }
            String response = "This is the response " + query;
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            if (map.get("close").equals("true"))
                server.stop(0);
        }


    }

    private static Map queryToMap(String query) {
        Map<String,String> map = new HashMap<>();
        if(query == null){
            return map;
        }

        String[] strings = query.split("&");
        System.out.println(strings.length);
        for(String string : strings){
            String[] split = string.split("=");
            map.put(split[0], split.length==1?null:split[1]);
        }
        return map;
    }
}