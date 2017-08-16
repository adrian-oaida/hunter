package com.edin.hunter.matcher.constrictor;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;
import com.edin.hunter.matcher.constrictor.AgglomerationConstrictor;
import com.edin.hunter.runner.BaseRunner;

import java.util.*;

import static com.edin.hunter.graph.DirectedGraph.ATTR_STATIC_ID;

public class MCINConstrictor extends AgglomerationConstrictor {


    public MCINConstrictor(BaseRunner runner) {
        super(runner);
    }

    @Override
    public int eliminateAgglomeration() {
        return mergeNeighbourCodeBlocks();
    }


    /*
    * This aproach works only if the stage data flow paths are inside another block
    * */
    protected int mergeNeighbourCodeBlocks(){
        //first we make a ranking of the most popular static id's
        //then we take the parent of the most popular and merge all children of the dynamic call graph in the dataflow graph
        removeDuplicatedEdges(dynamicCallGraph);
        removeDuplicatedEdges(staticCallGraph);
        removeDuplicatedEdges(dataFlowGraph);
        removeSelfEdges(dataFlowGraph);


        HashMap<Node, Integer> popularSCGNodes = new HashMap<>();
        int numberOfNodes = dataFlowGraph.getNodeCount();
        //max node from static call graph

        for(Node node : dataFlowGraph){
            if(node.getAttribute(ATTR_STATIC_ID) != null){
                Node staticNode = staticCallGraph.getNode(node.getAttribute(ATTR_STATIC_ID));
                if(popularSCGNodes.containsKey(staticNode)){
                    popularSCGNodes.put(staticNode, popularSCGNodes.get(staticNode) + 1);
                }else{
                    popularSCGNodes.put(staticNode, 1);
                }
            }
        }
        Map.Entry<Node, Integer> maxEntry = null;
        for(Map.Entry<Node, Integer> entry : popularSCGNodes.entrySet()){
            if(entry.getKey().getInDegree() == 1){//why is this?
                if(maxEntry == null){
                    maxEntry = entry;
                }else if(maxEntry.getValue() < entry.getValue()){
                    maxEntry = entry;
                }
            }
        }

        //this contains the static node with the highest number of usages in the data flow graph
        if(maxEntry != null ){
            //has only 1 parent
            Node scgParentNode = maxEntry.getKey().getIncomingEdges().get(0).getSource();
//            mergeForest(scgParentNode.getAssociatedNodes());
                mergeForest(scgParentNode);



        }

        return numberOfNodes - dataFlowGraph.getNodeCount();
    }
    protected void mergeForest(Node scgParentNode) {
        //
        int forestId = 1;
        int[] forest = new int[dataFlowGraph.getMaxNodeId() + 1];

        for(Node dynamicNodeParent : scgParentNode.getAssociatedNodes()) {

            List<Node> nodesToMerge = new ArrayList<>();
            //we take all of the child nodes of the parent node
            //aka we extract the subgraph
            for(Node subGraphNode : getSubGraph(dynamicNodeParent)){
                if(dataFlowGraph.getNode(subGraphNode.getId()) != null){
                    nodesToMerge.add(dataFlowGraph.getNode(subGraphNode.getId()));
                    forest[subGraphNode.getId()] = forestId;
                }
            }

            List<Node> exitNodes = new ArrayList<>();
            //split the region into regions based on exit nodes

            for (Node nodeM : nodesToMerge) {
                boolean posibleExitNode = true;
                if (nodeM != null) {
                    for (Edge outgoingEdge : nodeM.getOutgoingEdges()) {
                        if (forest[outgoingEdge.getTarget().getId()] == forestId) {
                            posibleExitNode = false;
                            break;
                        }
                    }
                    if (posibleExitNode) {
                        exitNodes.add(nodeM);
                    }
                }
            }

            Map<Integer, Deque<Node>> forestDeque = new HashMap<>();
            for (int i = 0; i < exitNodes.size(); i++) {
                Deque<Node> deque = new ArrayDeque<>();
                deque.add(exitNodes.get(i));
                forest[exitNodes.get(i).getId()] = forestId + i + 1;
                forestDeque.put(forestId + i + 1, deque);
            }
            while (forestDeque.size() != 0) {
                Iterator<Map.Entry<Integer, Deque<Node>>> it = forestDeque.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, Deque<Node>> entry = it.next();
                    Node node = entry.getValue().poll();
                    for (Edge edge : node.getIncomingEdges()) {
                        //it wasn't assigned yet to a subforest
                        if (forest[edge.getSource().getId()] == forestId) {
                            forest[edge.getSource().getId()] = forest[node.getId()];
                            entry.getValue().add(edge.getSource());
                        }
                    }
                    if (entry.getValue().isEmpty()) {
                        it.remove();
//                            forestDeque.remove(entry.getKey());
                    }
                }
            }
            for (Node node : exitNodes) {
                Deque<Node> exitDeque = new ArrayDeque<>();
                exitDeque.add(node);
                List<Node> treeNodes = new ArrayList<>();
                treeNodes.add(node);
                while (!exitDeque.isEmpty()) {
                    Node currentNode = exitDeque.poll();

                    for (Edge incomingEdge : currentNode.getIncomingEdges()) {
                        //part of the same sub tree
                        if (forest[node.getId()] == forest[incomingEdge.getSource().getId()]) {
                            treeNodes.add(incomingEdge.getSource());
                            exitDeque.add(incomingEdge.getSource());
                        } else {
                            //add edge from incoming
//                                incomingEdge.getSource().addEdgeTo(node);
                            if (currentNode != node) {
//                                    incomingEdge.getTarget().addEdgeTo(node);
//                                    treeNodes.remove(incomingEdge.getTarget());
                            }
                        }
                    }

                }
                treeNodes.remove(node);
                node.setAttribute("instruction", dynamicNodeParent.getAssociatedNodes().get(0).getAttribute("instruction"));

                for (Node twig : treeNodes) {
                    //iterate through outgoing and incoming
                    for (Edge incomingEdge : twig.getIncomingEdges()) {
                        incomingEdge.getSource().addEdgeTo(node);
                    }
                    for (Edge outgoingEdge : twig.getOutgoingEdges()) {
                        node.addEdgeTo(outgoingEdge.getTarget());
                    }
                }
                for (Node twig : treeNodes) {
                    dataFlowGraph.removeNode(twig);
                }
            }
            forestId += exitNodes.size() + 1;
//    }
            removeDuplicatedEdges(dataFlowGraph);
            removeSelfEdges(dataFlowGraph);
        }

    }
}
