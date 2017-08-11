package com.edin.hunter.matcher.detector;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;
import com.edin.hunter.runner.BaseRunner;

import java.util.ArrayList;
import java.util.List;

public abstract class LatticeDetector{
    protected DirectedGraph dataFlowGraph;
    protected DirectedGraph staticCallGraph;
    protected DirectedGraph dynamicCallGraph;
    public LatticeDetector(BaseRunner runner){
        this.dataFlowGraph = runner.getDataFlowGraph();
        this.staticCallGraph = runner.getStaticCallGraph();
        this.dynamicCallGraph = runner.getDynamicCallGraph();

    }

    public void printLattice(List<List<Node>> lattice){
        for(int i = 0; i < lattice.size(); i++){
            for(int j = 0; j < lattice.get(i).size(); j++)
                System.out.printf(" %3d ", lattice.get(i).get(j).getId());
            System.out.println();
        }
    }

    protected List<List<Node>> matchLattice(List<Node> seedStage){
        List<List<Node>> pipeline = new ArrayList<>();

        boolean visited[] = new boolean[dataFlowGraph.getMaxNodeId() + 1];
        for(Node node : seedStage){
            visited[node.getId()] = true;
        }
        pipeline.add(seedStage);
        List<Node> currentStage = seedStage;
        List<Node> newStage = null;
        do{
            newStage = new ArrayList<>();
            for(Node node: currentStage){
                for(Edge edge : node.getIncomingEdges()){
                    if(!visited[edge.getSource().getId()]){
                        visited[edge.getSource().getId()] = true;
                        newStage.add(edge.getSource());
                    }
                }
            }
            currentStage = newStage;
            pipeline.add(currentStage);
        }while(newStage.size() != 0);

        int maxRowSize = 0;
        //validate lattice structure
        for(int i = 0; i < pipeline.size(); i++){
            if(maxRowSize == 0){
                maxRowSize = pipeline.get(i).size();
            }else{
                if(pipeline.get(i).size() > maxRowSize){
                    return null;
                }
            }

        }
        return pipeline;
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
