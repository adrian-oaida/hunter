package com.edin.hunter.matcher.constrictor;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;
import com.edin.hunter.runner.BaseRunner;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DIINConstrictor extends AgglomerationConstrictor{


    public DIINConstrictor(BaseRunner runner) {
        super(runner);
    }

    private Node getDeepestIterativeStaticNode(){
        Deque<Node> queue = new ArrayDeque<>();
        queue.add(staticCallGraph.getNode(0));
        int[] depthArray = new int[staticCallGraph.getMaxNodeId() + 1];
        depthArray[0] = 0;
        while(!queue.isEmpty()){
            Node currentNode = queue.poll();
            for(Edge edge : currentNode.getOutgoingEdges()){
                queue.add(edge.getTarget());
                depthArray[edge.getTarget().getId()] = depthArray[currentNode.getId()] + 1;
            }
        }
        int max = -1;
        Node maxNode = null;
        //node 0 is at the base, and it has a depth of 0
        for(int i = 1; i < depthArray.length; i++){
            if(depthArray[i] > 0 && depthArray[i] > max){
                try {
                    if (staticCallGraph.getNode(i).getInstructionType() == Node.InstructionType.ITERATIVE) {
                        max = depthArray[i];
                        maxNode = staticCallGraph.getNode(i);
                    }
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
        return maxNode;
    }

    @Override
    public int eliminateAgglomeration() {

        //TODO merge and then report
        // merge all of the nodes under that node
        removeSelfEdges(staticCallGraph);
        removeDuplicatedEdges(staticCallGraph);

        removeDuplicatedEdges(dynamicCallGraph);
        removeSelfEdges(dynamicCallGraph);

        int currentDFNodeCount = dataFlowGraph.getNodeCount();

        Node staticNode = getDeepestIterativeStaticNode();
        if(staticNode != null){
            for(Node dynamicNode : staticNode.getAssociatedNodes()){
                //all of the dynamic nodes must be merge individually
                List<Node> subGraph = getSubGraph(dynamicNode);
                //now we get the underlying data flow nodes from this and merge them
                Node parentDataNode = dataFlowGraph.getOrAddNode(dynamicNode.getId());

                parentDataNode.copyAttributesFrom(dynamicNode);

                for(Node subGraphNode : subGraph){
                    Node subGraphDataNode = dataFlowGraph.getNode(subGraphNode.getId());
                    if(subGraphDataNode != null){
                        for(Edge edge : subGraphDataNode.getOutgoingEdges()){
                            parentDataNode.addEdgeTo(edge.getTarget());
                        }
                        for(Edge edge : subGraphDataNode.getIncomingEdges()){
                            edge.getSource().addEdgeTo(parentDataNode);
                        }
                        dataFlowGraph.removeNode(subGraphDataNode);
                        dynamicCallGraph.removeNode(subGraphNode);
                    }
                }
                if(parentDataNode.getNeighbouringNodes().size() == 0){
                    dataFlowGraph.removeNode(parentDataNode);
                }
                dynamicCallGraph.removeNode(dynamicNode);

            }
            staticCallGraph.removeNode(staticNode);

            removeSelfEdges(dataFlowGraph);
            removeSelfEdges(dynamicCallGraph);
            removeSelfEdges(staticCallGraph);

            removeDuplicatedEdges(dataFlowGraph);
            removeDuplicatedEdges(dynamicCallGraph);
            removeDuplicatedEdges(staticCallGraph);
        }


        return currentDFNodeCount - dataFlowGraph.getNodeCount();
    }
}
