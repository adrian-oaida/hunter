package com.edin.hunter.matcher.detector;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;
import com.edin.hunter.runner.BaseRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.edin.hunter.graph.DirectedGraph.ATTR_COLOR;

public class SLMDetector extends LatticeDetector implements StructuralDetector {


    public SLMDetector(BaseRunner runner) {
        super(runner);
    }

    @Override
    public boolean containsStructure() {
        List<Node> startNodes = new ArrayList<>();
        List<Node> stopNodes = new ArrayList<>();

        List<Node> cornerNodes = new ArrayList<>();
        List<Node> centerNodes = new ArrayList<>();
        List<Node> otherTypes = new ArrayList<>();

        for(Node node : dataFlowGraph){

            if(node.getOutDegree() == node.getInDegree()){
                if(node.getOutDegree() == 1){
                    cornerNodes.add(node);
                }else{
                    centerNodes.add(node);
                }
            }else{
                if(node.getOutDegree() == 0){
                    stopNodes.add(node);
                }else if(node.getInDegree() == 0){
                    startNodes.add(node);
                }else{
                    otherTypes.add(node);
                }
            }
        }


        List<List<Node>> finalLattice = null;
        if(cornerNodes.size() > (startNodes.size() + stopNodes.size()))
            return false;
        for(Node n : cornerNodes){

            for(Node terminatorNode : stopNodes){
//                System.out.printf("path from %d to %d \n", terminatorNode.getId(), n.getId());
                //because this graph is already a tree, we do a inverse dfs to get the paths from start to the corners
                //then we can say that we have identified a stage in the pipeline
                List<Node> stage = shortestPath(n, terminatorNode);

                List<List<Node>> newLattice = matchLattice(stage);
                if(finalLattice == null)
                    finalLattice = newLattice;
                if(newLattice != null && newLattice.get(0).size() > finalLattice.get(0).size()){
                    finalLattice = newLattice;
                }
            }

        }
        if(finalLattice != null){
            int latticeNodeSize = 0;
            for(List<Node> stage : finalLattice){
                for(Node node : stage){
                    latticeNodeSize++;
                    node.setAttribute(ATTR_COLOR, "red");
                }
            }
//            int otherSubGraphsSize = 0;
//            for(Node node : dataFlowGraph){
//                if(node.getAttribute(ATTR_COLOR) != "red"){
//                    otherSubGraphsSize++;
//                }
//            }
            if(latticeNodeSize == dataFlowGraph.getNodeCount()){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    private List<Node> shortestPath(Node x, Node y){
        boolean[] visited = new boolean[dataFlowGraph.getMaxNodeId() + 1];
        List<Edge> path = new ArrayList<>();
        outgoingDFS(x, visited, y, path);
        Collections.reverse(path);
        List<Node> pathNodes = new ArrayList<>();
        for(Edge e : path){
            pathNodes.add(e.getSource());
        }
        pathNodes.add(y);

        return pathNodes;
    }
}
