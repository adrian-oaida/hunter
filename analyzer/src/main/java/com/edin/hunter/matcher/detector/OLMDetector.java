package com.edin.hunter.matcher.detector;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;
import com.edin.hunter.runner.BaseRunner;

import java.util.ArrayList;
import java.util.List;

import static com.edin.hunter.graph.DirectedGraph.ATTR_COLOR;

public class OLMDetector extends LatticeDetector implements StructuralDetector{

    public OLMDetector(BaseRunner runner) {
        super(runner);
        this.staticCallGraph = staticCallGraph.clone();
        this.dynamicCallGraph = dynamicCallGraph.clone();
    }

    public DirectedGraph getDynamicCallGraph(){
        return dynamicCallGraph;
    }
    @Override
    public boolean containsStructure() {
        int color = 2;
        List<Node> startRegion = new ArrayList<>();
        List<Node> endRegion = new ArrayList<>();
        for(Node node : dynamicCallGraph){
            if(node.getInstructionType() == Node.InstructionType.ITERATIVE){
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

                color+=1;
            }

        }
//        for(Node staticNode : staticCallGraph){
//            if(staticNode.getInstructionType() == Node.InstructionType.ITERATIVE){
//                for(Node dynamicNode : staticNode.getAssociatedNodes()){
//                    for(Edge outgoingEdge : dynamicNode.getOutgoingEdges()){
//
//                    }
//                }
//            }
//        }
        if(endRegion.size() > 0){
            Node tmp = endRegion.get(0);
            boolean hasNext = true;
            while(hasNext){
                hasNext = false;
                for(Edge edge: tmp.getIncomingEdges()){
                    if(edge.getSource().getAttribute("regionId") != null && edge.getSource().getAttribute("regionId").equals(tmp.getAttribute("regionId"))){
                        endRegion.add(edge.getSource());
                        tmp = edge.getSource();
                        hasNext = true;
                    }
                }
            }
            List<List<Node>> pipeline = matchLattice(endRegion);
            if(pipeline != null){
                int latticeSize = 0;
                for(List<Node> stage : pipeline){
                    for(Node node : stage){
                        latticeSize++;
                        node.setAttribute(ATTR_COLOR, "red");
                    }
                }
                if(latticeSize == dataFlowGraph.getNodeCount()){
                    return true;
                }

            }
        }
        return false;
    }
}
