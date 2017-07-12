package com.edin.hunter.matcher;



import com.edin.hunter.runner.Edge;
import com.edin.hunter.runner.Graph;
import com.edin.hunter.runner.Node;

import java.util.*;

/**
 * Created by dude on 6/25/17.
 */
public abstract class BasicMatcher {
    protected Graph graph;
    protected Node start;
    protected Node finish;

    /*
    * @param graph a dataflow graph
    *
    * */
    public BasicMatcher(Graph graph){
        this.graph = graph;
        markUpGraph();
    }
    public Graph getGraph(){


        return graph;
    }
    /*
    *This returns a new graph based on the existent graph but with pipeline stage and latices highlighted
    *
    *
    * @return
    * */
    public abstract Graph detect();
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
        for(Node n : graph){
            n.setAttribute("color", "blue");
            if(n.getInDegree() == 0){
                start =  n;
                System.out.println("start " + start);
                n.setAttribute("shape", "box");
                n.setAttribute("color", "red");
            }
            if(n.getOutDegree() == 0){
                finish = n;
                System.out.println("finish " + finish);
                n.setAttribute("shape", "box");
                n.setAttribute("color", "black");
            }
            if(! n.getEdgesTowards(n).isEmpty()){
                if(n.getInDegree() - 1 == 0){
                    n.setAttribute("color", "red");
                }
                if(n.getOutDegree() - 1 == 0){
                    n.setAttribute("color", "black");
                }
            }
        }
    }
}
