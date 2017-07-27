package com.edin.hunter.matcher;



import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;

import java.util.*;

/**
 * Created by dude on 6/25/17.
 */
public abstract class BaseMatcher {
    protected DirectedGraph graph;
    protected List<Node> startNodes;
    protected List<Node> finishNodes;
    private String[] colorArray = new String[]{"blue", "blueviolet", "brown", "burlywood", "cadetblue", "Chartreuse", "Chocolate", "Coral", "CornflowerBlue", "Crimson", "Cyan", "DarkBlue", "DarkCyan", "DarkGoldenRod", "DarkGreen", "DarkMagenta", "DarkSalmon", "DarkSlateBlue","DarkTurquoise"};
    /*
    * @param graph a dataflow graph
    *
    * */
    public BaseMatcher(DirectedGraph graph){
        this.graph = graph;
        markUpGraph();
    }
    public DirectedGraph getGraph(){


        return graph;
    }
    /*
    *This returns a new graph based on the existent graph but with pipeline stage and latices highlighted
    *
    *
    * @return
    * */
    public abstract DirectedGraph detect();
    //TODO lattice highlighter
    //latice definition


    /*
    * This method modified the graph by removing edges that point to their originating node (source = target)
    * */
    protected void removeSelfEdges(){
        for(Node node : graph){
            List<Edge> selfEdges = node.getEdgesTowards(node);
            for(Edge edge : selfEdges){
                node.removeEdge(edge);
            }
        }
    }
    /*
    * <p>
    *     This method modifies the graph by removing duplicated edges leaving.
    * */
    protected void removeDuplicatedEdges(){
        for(Node node : graph){
            for(Node neighNode : node.getNeighbouringNodes()){
                List<Edge> edges = node.getEdgesTowards(neighNode);
                if(edges.size() > 1){
                    for(int i = 1 ; i < edges.size(); i++){
                        node.removeEdge(edges.get(i));
                    }
                }
            }
        }

    }
    protected void markUpGraph(){
        startNodes = new ArrayList<>();
        finishNodes = new ArrayList<>();
        for(Node n : graph) {
            n.setAttribute("color", colorArray[Integer.parseInt(n.getAttribute("staticId"))]);
            if (n.getInDegree() == 0) {
                startNodes.add(n);
                System.out.println("start " + n);
                n.setAttribute("shape", "box");
                n.setAttribute("color", "red");
            }
            if (n.getOutDegree() == 0) {
                finishNodes.add(n);
                System.out.println("finish " + n);
                n.setAttribute("shape", "box");
                n.setAttribute("color", "black");
            }
//            if (!n.getEdgesTowards(n).isEmpty()) {
//                if (n.getInDegree() - 1 == 0) {
//                    startNodes.add(n);
//                    n.setAttribute("color", "red");
//                }
//                if(n.getOutDegree() - 1 == 0) {
//                    finishNodes.add(n);
//                    n.setAttribute("color", "black");
//                }
//            }
        }
    }
        /*
    * This method performs a depth first search on incoming edges until it research the node y and returns a reversed path to that node
    * */

    protected boolean incomingDFS(Node node, boolean[] visited, Node y, List<Edge> path){

        visited[node.getId()] = true;


        if(node == y){
            return true;
        }
        for(Edge e : node.getIncomingEdges()){
            if(!visited[e.getSource().getId()]){
                if(incomingDFS(e.getSource(), visited, y, path)){
                    path.add(e);
                    return true;
                }
            }
        }
        return false;
    }
    /*
    * This method performs a depth first search on outgoing edges until it research the node y and returns a reversed path to that node
    * */
    protected boolean outgoingDFS(Node node, boolean[] visited, Node y, List<Edge> path){

        visited[node.getId()] = true;

        if(node == y){
            return true;
        }
        for(Edge e : node.getOutgoingEdges()){
            if(!visited[e.getTarget().getId()]){
                if(outgoingDFS(e.getTarget(), visited, y, path)){
                    path.add(e);
                    return true;
                }
            }
        }
        return false;
    }
}
