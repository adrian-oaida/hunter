package com.edin.hunter.matcher.constrictor;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;
import com.edin.hunter.matcher.constrictor.AgglomerationConstrictor;
import com.edin.hunter.runner.BaseRunner;

import java.util.*;

import static com.edin.hunter.graph.DirectedGraph.ATTR_STATIC_ID;

public class PopularNodesConstrictor extends AgglomerationConstrictor {


    public PopularNodesConstrictor(BaseRunner runner) {
        super(runner);
    }

    @Override
    public boolean eliminateAgglomeration() {
        mergeNeighbourCodeBlocks();
        return false;
    }

    /*
    * This aproach works only if the stage data flow paths are inside another block
    * */
    protected void mergeNeighbourCodeBlocks(){
        //first we make a ranking of the most popular static id's
        //then we take the parent of the most popular and merge all children of the dynamic call graph in the dataflow graph
        removeDuplicatedEdges(dynamicCallGraph);
        removeDuplicatedEdges(staticCallGraph);

        HashMap<Node, Integer> popularNodes = new HashMap<>();

        for(Node node : dataFlowGraph){
            if(node.getAttribute(ATTR_STATIC_ID) != null){
                Node staticNode = staticCallGraph.getNode(node.getAttribute(ATTR_STATIC_ID));
                if(popularNodes.containsKey(staticNode)){
                    popularNodes.put(staticNode, popularNodes.get(staticNode) + 1);
                }else{
                    popularNodes.put(staticNode, 1);
                }
            }
        }
        Node maxNode = null;
        int maxValue = 0;
        for(Map.Entry<Node, Integer> entry : popularNodes.entrySet()){
            if(entry.getValue() > maxValue){
                maxNode = entry.getKey();
                maxValue = entry.getValue();
            }
        }
        popularNodes.remove(maxNode);

        int forestId = 1;
        int[] forest = new int[dataFlowGraph.getMaxNodeId() + 1];

        if(maxNode.getInDegree() == 1){
            //has only 1 parent
            Node parentNode = maxNode.getIncomingEdges().get(0).getSource();
            for(Node dynamicNode : parentNode.getAssociatedNodes()){
                List<Node> nodesToMerge = new ArrayList<>();

                for(Edge outgoingEdge : dynamicNode.getOutgoingEdges()){
                    nodesToMerge.add(dataFlowGraph.getNode(outgoingEdge.getTarget().getId())) ;
                    forest[outgoingEdge.getTarget().getId()] = forestId;
                }
                List<Node> entryNodes = new ArrayList<>();
                List<Node> exitNodes = new ArrayList<>();
                //split the region into regions based on exit nodes

                for(Node nodeM : nodesToMerge){
                    boolean posibleExitNode = true;
                    if(nodeM != null){
                        for(Edge outgoingEdge : nodeM.getOutgoingEdges()){
                            if(forest[outgoingEdge.getTarget().getId()] == forestId){
                                posibleExitNode = false; break;
                            }
                        }
                        if(posibleExitNode){
                            exitNodes.add(nodeM);
                        }

                    }
                }


                Map<Integer, Deque<Node>> forestDeque = new HashMap<>();
                for(int i = 0; i < exitNodes.size(); i++){
                    Deque<Node> deque = new ArrayDeque<>();
                    deque.add(exitNodes.get(i));
                    forest[exitNodes.get(i).getId()] = forestId + i + 1;
                    forestDeque.put(forestId + i + 1, deque);
                }
                while(forestDeque.size() != 0){
                    Iterator<Map.Entry<Integer, Deque<Node>>> it = forestDeque.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry<Integer, Deque<Node>> entry = it.next();
                        Node node = entry.getValue().poll();
                        for(Edge edge : node.getIncomingEdges()){
                            //it wasn't assigned yet to a subforest
                            if(forest[edge.getSource().getId()] == forestId){
                                forest[edge.getSource().getId()] = forest[node.getId()];
                                entry.getValue().add(edge.getSource());
                            }
                        }
                        if(entry.getValue().isEmpty()){
                            it.remove();
//                            forestDeque.remove(entry.getKey());
                        }
                    }
                }
                for(Node node : exitNodes){
                    Deque<Node> exitDeque = new ArrayDeque<>();
                    exitDeque.add(node);
                    List<Node> treeNodes = new ArrayList<>();
                    treeNodes.add(node);
                    while(!exitDeque.isEmpty()){
                        Node currentNode = exitDeque.poll();

                        for(Edge incomingEdge : currentNode.getIncomingEdges()){
                            //part of the same sub tree
                            if(forest[node.getId()] == forest[incomingEdge.getSource().getId()]){
                                treeNodes.add(incomingEdge.getSource());
                                exitDeque.add(incomingEdge.getSource());
                            }else{
                                //add edge from incoming
//                                incomingEdge.getSource().addEdgeTo(node);
                                if(currentNode != node){
//                                    incomingEdge.getTarget().addEdgeTo(node);
//                                    treeNodes.remove(incomingEdge.getTarget());
                                }
                            }
                        }

                    }
                    treeNodes.remove(node);
                    node.setAttribute("instruction", parentNode.getAttribute("instruction"));
                    for(Node twig : treeNodes){
                        //iterate through outgoing and incoming
                        for(Edge incomingEdge : twig.getIncomingEdges()){
                            incomingEdge.getSource().addEdgeTo(node);
                        }
                        for(Edge outgoingEdge : twig.getOutgoingEdges()){
                            node.addEdgeTo(outgoingEdge.getTarget());
                        }
                    }
                    for(Node twig : treeNodes){
                        dataFlowGraph.removeNode(twig);
                    }
                }
                forestId+=exitNodes.size() + 1;
            }
        }
        //need to return the number of compresed nodes and the number of eliminated nodes
        //
//        for(int i = 0; i < forest.length; i++){
//            if(forest[i] != 0){
//                try{
//                    dataFlowGraph.getNode(i).setAttribute("forestId", "" + forest[i]);
//                    dataFlowGraph.getNode(i).setAttribute(ATTR_COLOR, colorArray[forest[i]]);
//                }catch(NullPointerException e){
//                    System.err.println("error on " + i + " node");
//                    e.printStackTrace();
//                }
//            }
//        }
        removeDuplicatedEdges(dataFlowGraph);
        removeSelfEdges(dataFlowGraph);
        System.out.printf("found %d regions \n", forestId);

    }
}
