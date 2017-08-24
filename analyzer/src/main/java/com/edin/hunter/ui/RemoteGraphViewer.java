package com.edin.hunter.ui;

import com.edin.hunter.graph.DirectedGraph;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class RemoteGraphViewer {
    private DirectedGraph originalGraph;
    private DirectedGraph processedGraph;
    private HttpServer server;
    public RemoteGraphViewer(DirectedGraph originalGraph, DirectedGraph processedGraph){
        this.originalGraph = originalGraph;
        this.processedGraph = processedGraph;

    }
    public void startServer(){
        try {
            if(server != null)
                stopServer();
            server = HttpServer.create(new InetSocketAddress(5566), 0);
            server.createContext("/originalGraph.json",new GraphJSONHandler(originalGraph));
            server.createContext("/processedGraph.json",new GraphJSONHandler(processedGraph));

            server.start();
//            showPageInBrowser();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopServer(){
        if(server != null){
            server.stop(0);
            server = null;
        }
    }
    static class GraphJSONHandler implements HttpHandler {
        private DirectedGraph graph;
        private String payload;
        public GraphJSONHandler(DirectedGraph graph){
            this.graph = graph;
            this.payload = graph.toJSON();
        }
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            Headers headers = httpExchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Headers", "Cache-Control");
            headers.add("Content-Type", "application/json");
            headers.add("Cache-Control", "max-age=1209600");
            httpExchange.sendResponseHeaders(200, payload.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(payload.getBytes());
            os.close();
        }
    }
}
