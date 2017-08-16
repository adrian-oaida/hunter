package com.edin.hunter.matcher.constrictor;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;
import com.edin.hunter.runner.BaseRunner;

import java.util.*;

import static com.edin.hunter.graph.DirectedGraph.ATTR_COLOR;
import static com.edin.hunter.graph.DirectedGraph.ATTR_INSTRUCTION;
import static com.edin.hunter.graph.DirectedGraph.colorArray;

public class DINConstrictor extends AgglomerationConstrictor {
    public DINConstrictor(BaseRunner runner) {
        super(runner);

    }
    private List<Node> getDeepestDynamicNodes(){
        Deque<Node> queue = new ArrayDeque<>();
        queue.add(dynamicCallGraph.getNode(0));
        int[] depthArray = new int[dynamicCallGraph.getMaxNodeId() + 1];
        depthArray[0] = 0;
        while(!queue.isEmpty()){
            Node currentNode = queue.poll();
            for(Edge edge : currentNode.getOutgoingEdges()){
                queue.add(edge.getTarget());
                depthArray[edge.getTarget().getId()] = depthArray[currentNode.getId()] + 1;
            }
        }
        int max = -1;

        for(int i = 0; i < depthArray.length; i++){
            if(depthArray[i] > max){
                    max = depthArray[i];
            }
        }
        List<Node> result = new ArrayList<>();
        for(int i = 0; i < depthArray.length; i++){
            if(depthArray[i] == max){
                result.add(dynamicCallGraph.getNode(i));
            }
        }
        return result;
    }
    @Override
    public int eliminateAgglomeration() {
        //how about the bigest number of same level instructions


        int colour = 1;
        int numberOfNodes = dataFlowGraph.getNodeCount();
        List<Node> toEliminate = new ArrayList<>();
        Set<Integer> number = new TreeSet<>();
        for(Node node : getDeepestDynamicNodes()){
            number.add(node.getIncomingEdges().get(0).getSource().getId());
            colour++;

            Node parentDynamicNode = node.getIncomingEdges().get(0).getSource();
//                    mergeForest(parentDynamicNode);

            Node parentDataFlowNode = dataFlowGraph.getOrAddNode(parentDynamicNode.getId());
            parentDataFlowNode.copyAttributesFrom(parentDynamicNode);


            for(Edge outgoingEdge: parentDynamicNode.getOutgoingEdges()){
                Node dataNode = dataFlowGraph.getNode(outgoingEdge.getTarget().getId());

                if(dataNode != null){
                    for(Edge outward : dataNode.getOutgoingEdges()){
                        parentDataFlowNode.addEdgeTo(outward.getTarget());
                    }
                    for(Edge inward : dataNode.getIncomingEdges()){
                        inward.getSource().addEdgeTo(parentDataFlowNode);
                    }
                    dataFlowGraph.removeNode(dataNode);
                    toEliminate.add(outgoingEdge.getTarget());
                }
            }
        }

        for(Node node:toEliminate){
            dynamicCallGraph.removeNode(node);
        }
        removeDuplicatedEdges(dataFlowGraph);
        removeSelfEdges(dataFlowGraph);

        return numberOfNodes - dataFlowGraph.getNodeCount();
    }
}
