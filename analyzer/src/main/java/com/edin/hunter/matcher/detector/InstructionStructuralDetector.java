package com.edin.hunter.matcher.detector;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;
import com.edin.hunter.runner.BaseRunner;

import java.util.ArrayList;
import java.util.List;

import static com.edin.hunter.graph.DirectedGraph.ATTR_COLOR;

public class InstructionStructuralDetector extends LatticeDetector {
    private DirectedGraph staticCallGraph;
    private DirectedGraph dynamicCallGraph;

    public InstructionStructuralDetector(BaseRunner runner) {
        super(runner);
    }

    private void matchOnStructure(){
        int color = 2;
        List<Node> startRegion = new ArrayList<>();
        List<Node> endRegion = new ArrayList<>();

        for(Node node : dynamicCallGraph){
            if(node.instructionType() == Node.InstructionType.ITERATIVE){
                for(Edge edge : node.getOutgoingEdges()){
                    if(dataFlowGraph.getNode(edge.getTarget().getId()) != null){
                        Node dataNode = dataFlowGraph.getNode(edge.getTarget().getId());
                        dataNode.setAttribute("regionId", color + "");

//                          dataNode.setAttribute(ATTR_COLOR, colorArray[color]);
                        if(dataNode.getOutDegree() == 0){
                            endRegion.add(dataNode);
                        }
                        if(dataNode.getInDegree() == 0){
                            startRegion.add(dataNode);
                        }
                    }
                }
                //this is a region, need to split it into trees
                //then need to check for a dependency between trees
                //then need to find the region that contains the stop node
                //then work our way up until we find the stop node

                color+=1;
            }

        }
        if(endRegion.size() > 0){
            Node tmp = endRegion.get(0);
            boolean hasNext = true;
            while(hasNext){
                hasNext = false;
                for(Edge edge: tmp.getIncomingEdges()){
                    if(edge.getSource().getAttribute("regionId").equals(tmp.getAttribute("regionId"))){
                        endRegion.add(edge.getSource());
                        tmp = edge.getSource();
                        hasNext = true;
                    }
                }
            }
            List<List<Node>> pipeline = matchLattice(endRegion);
            if(pipeline != null){
                for(List<Node> stage : pipeline){
                    for(Node node : stage){
                        node.setAttribute(ATTR_COLOR, "red");
                    }
                }

            }
        }

    }
}
