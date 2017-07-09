package com.edin.hunter.matcher;



import com.edin.hunter.runner.Edge;
import com.edin.hunter.runner.Graph;
import com.edin.hunter.runner.Node;

import java.util.*;

/**
 * Created by dude on 6/25/17.
 */
public class Matcher {
    private Graph graph;

    /*
    * @param graph a dataflow graph
    *
    * */
    public Matcher(Graph graph){
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
    public Graph detect(){
        removeDuplicatedEdges();
        detectLatice();
        detectPipelineStages();

        return graph;
    }
    //TODO lattice highlighter
    //latice definition
    private boolean detectLatice(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        return false;
    }
    class NodePair{
        private Node source;
        private Node target;
        public NodePair(Node source, Node target){
            this.source = source;
            this.target = target;
        }
        public Node getSource(){
            return source;
        }
        public Node getTarget(){
            return target;
        }
        @Override
        public boolean equals(Object obj){
            if(this == obj)
                return true;
            if(obj instanceof NodePair){
                NodePair pair = (NodePair)obj;
                if(pair.source == this.source && pair.target == this.target)
                    return true;
            }
            return false;
        }
        @Override
        public int hashCode(){
            int hash = 1;
            hash = hash * 31 + source.getId();
            hash = hash * 33 + target.getId();
            return hash;
        }
    }
    private boolean detectPipelineStages(){
        //look for the maximum number of subgraphs that have the same structure and link to each other
        //
        removeDuplicatedEdges();

        return false;
    }

    private Node start;
    private Node finish;

    private void removeDuplicatedEdges(){
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
    private void markUpGraph(){
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
