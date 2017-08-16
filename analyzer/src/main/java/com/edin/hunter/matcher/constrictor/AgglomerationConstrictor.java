package com.edin.hunter.matcher.constrictor;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;
import com.edin.hunter.runner.BaseRunner;

import java.util.*;

public abstract class AgglomerationConstrictor {
    public DirectedGraph getDataFlowGraph() {
        return dataFlowGraph;
    }

    public DirectedGraph getStaticCallGraph() {
        return staticCallGraph;
    }

    public DirectedGraph getDynamicCallGraph() {
        return dynamicCallGraph;
    }

    protected DirectedGraph dataFlowGraph;
    protected DirectedGraph staticCallGraph;
    protected DirectedGraph dynamicCallGraph;


    public AgglomerationConstrictor(BaseRunner runner){
        dataFlowGraph = runner.getDataFlowGraph();
        staticCallGraph = runner.getStaticCallGraph();
        dynamicCallGraph = runner.getDynamicCallGraph();

    }
    public abstract int eliminateAgglomeration();

    /*
    * This method modified the graph by removing edges that point to their originating node (source = target)
    * */
    protected void removeSelfEdges(DirectedGraph graph){
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
    protected void removeDuplicatedEdges(DirectedGraph graph){
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
    protected List<Node> getSubGraph(Node root){
        List<Node> result = new ArrayList<>();

        Deque<Node> queue = new ArrayDeque<>();
        queue.add(root);
        while(!queue.isEmpty()){
            Node node = queue.poll();
            for(Edge edge : node.getOutgoingEdges()){
                result.add(edge.getTarget());
                queue.add(edge.getTarget());
            }
        }

        return result;
    }

}
