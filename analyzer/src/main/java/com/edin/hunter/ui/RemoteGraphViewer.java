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
    private DirectedGraph graph;
    private HttpServer server;
    public RemoteGraphViewer(DirectedGraph graph){
        this.graph = graph;

    }
    public void startServer(){
        try {
            if(server != null)
                stopServer();
            server = HttpServer.create(new InetSocketAddress(5566), 0);
            server.createContext("/graph.json",new GraphJSONHandler(graph));
            server.start();
//            showPageInBrowser();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void showPageInBrowser(){
        List<String> command = new ArrayList<>();
        command.add("clang");


        ProcessBuilder pb = new ProcessBuilder(command);
        System.out.println(pb.command());
        String astString = null;
        try {

            Process p = pb.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            astString = builder.toString();
            p.waitFor();
            System.out.println(p.exitValue());
        } catch (IOException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {
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
        public GraphJSONHandler(DirectedGraph graph){
            this.graph = graph;
        }
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = graph.toJSON();
            Headers headers = httpExchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Headers", "Cache-Control");
            headers.add("Content-Type", "application/json");
            headers.add("Cache-Control", "max-age=1209600");
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
