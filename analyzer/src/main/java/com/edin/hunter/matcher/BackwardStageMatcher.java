package com.edin.hunter.matcher;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;

import java.util.*;

import static com.edin.hunter.graph.DirectedGraph.ATTR_COLOR;
import static com.edin.hunter.graph.DirectedGraph.ATTR_STATIC_ID;

/**
 * Created by dude on 7/12/17.
 */
public class BackwardStageMatcher extends BaseMatcher {
    private DirectedGraph staticCallGraph;
    private DirectedGraph dynamicCallGraph;
    public BackwardStageMatcher(DirectedGraph staticCallGraph, DirectedGraph dynamicCallGraph, DirectedGraph dataFlowGraph) {
        super(dataFlowGraph);
        this.staticCallGraph = staticCallGraph;
        this.dynamicCallGraph = dynamicCallGraph;

    }

    public DirectedGraph getDynamicCallGraph() {
        return dynamicCallGraph;
    }

    public DirectedGraph getStaticCallGraph() {
        return staticCallGraph;
    }

    @Override
    public DirectedGraph detect() {
        markUpGraph(this.dataFlowGraph);
        removeSelfEdges(dataFlowGraph);
        removeDuplicatedEdges(dataFlowGraph);
//        mergeLinearSESE();
//        buildForestOfStages();
        mergeNeighbourCodeBlocks();
        removeDuplicatedEdges(dataFlowGraph);
        removeSelfEdges(dataFlowGraph);
//        mergeLinearSESE();
        markUpGraph(this.dataFlowGraph);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        matchStage();

        return null;
    }

    /*
    * This method computes the maximum number of sub graphs that are identical in structure
    * this will fail for a data flow graph that has two different types of stages
    * */
    private void buildForestOfStages(){

        int[] forest = new int[dataFlowGraph.getMaxNodeId() + 1];
        //min forest size
        int forestMaxSize = 3;
        int[] forestSize = new int[dataFlowGraph.getMaxNodeId() + 1];
        Deque<Node> queue = new ArrayDeque<>();

        for(Node node : dataFlowGraph){
            if(forest[node.getId()] == 0){
                forestSize[node.getId()] = 1;
                forest[node.getId()] = node.getId();
                for(Node neigh : node.getNeighbouringNodes()){
                    if(forest[neigh.getId()] == 0 && forestSize[node.getId()] < forestMaxSize){
                        forest[neigh.getId()] = node.getId();
                        forestSize[node.getId()]++;
                    }
                }
            }
        }
        int colourCount = 0;
        for(int i = 0 ; i < forestSize.length; i++){
            if(forestSize[i] != 0){
                dataFlowGraph.getNode(i).setAttribute(ATTR_COLOR, colorArray[colourCount]);
                for(Node neigh : dataFlowGraph.getNode(i).getNeighbouringNodes()){
                    if(forest[neigh.getId()] == i){
                        neigh.setAttribute(ATTR_COLOR, colorArray[colourCount]);
                    }
                }
                colourCount++;
            }
        }

    }

    /*
    * This aproach works only if the stage data flow paths are inside another block
    * */
    private void mergeNeighbourCodeBlocks(){
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
        System.out.printf("found %d regions \n", forestId);

    }
    private void mergeLinearSESE() {
        boolean[] visited = new boolean[this.dataFlowGraph.getMaxNodeId() + 1];
        List<Node> sisoNodes = new ArrayList<>();
        for(Node node : dataFlowGraph){
            if(node.getOutDegree() == 1 && node.getInDegree() == 1){
                //we have a posible siso
                sisoNodes.add(node);
            }
        }
        List<Set<Node>> regionsList = new ArrayList<>();
        for(Node node : sisoNodes){
            if(!visited[node.getId()]){
                Set<Node> region = new HashSet<>();
                Node tmp = node;
                while(tmp.getOutDegree() == 1 && tmp.getInDegree() == 1 && !visited[tmp.getId()]){
                    region.add(tmp);
                    visited[tmp.getId()] = true;
                    tmp = tmp.getOutgoingEdges().get(0).getTarget();

                }
                tmp = node;
                while(tmp.getOutDegree() == 1 && tmp.getInDegree() == 1 && !visited[tmp.getId()]){
                    region.add(tmp);
                    visited[tmp.getId()] = true;
                    tmp = tmp.getIncomingEdges().get(0).getSource();
                }
                regionsList.add(region);
                //run to forward nodes
                //run to backward nodes
            }
        }
        //in this stage we have to order the region
        //but first we will just colour it
        int color = 0;
        System.out.printf("got %d regions \n", regionsList.size());
        //get the first and last node, and create an edge between the parent and child of the region

        for(Set<Node> region : regionsList){
            Node parent = null, child = null;
            for(Node node : region){
                if(!region.contains(node.getIncomingEdges().get(0).getSource())){
                    parent = node.getIncomingEdges().get(0).getSource();
                }
                if(!region.contains(node.getOutgoingEdges().get(0).getTarget())){
                    child = node.getOutgoingEdges().get(0).getTarget();
                }
//                node.setAttribute("color", colorArray[color]);
            }

            if(parent != null && child != null){
                for(Node node: region){
                    dataFlowGraph.removeNode(node);
                }
                Edge edge = parent.addEdgeTo(child);
//                edge.setAttribute("color", colorArray[color]);
            }
            color++;
        }
    }

    private void matchStage(){
//        for(Node finish:finishNodes){
//            for(Node start:startNodes){
//                List<Edge> path = new ArrayList<>();
//                boolean[] visited = new boolean[graph.getMaxNodeId() + 1];
//                if(incomingDFS(finish, visited, start, path)){
//                    System.out.println("path from " + finish + " to " + start);
//                    for(Edge e : path){
//                        System.out.print(e + "\t");
//                    }
//                    System.out.println();
//                }
//            }
//        }
        List<Node> cornerNodes = new ArrayList<>();
        List<Node> centerNodes = new ArrayList<>();
        List<Node> otherTypes = new ArrayList<>();

        for(Node node : dataFlowGraph){

            if(node.getOutDegree() == node.getInDegree()){
                if(node.getOutDegree() == 1){
                    cornerNodes.add(node);
                }else{
                    centerNodes.add(node);
                    System.out.println("center " + node.getId());
                }
            }else{
                otherTypes.add(node);
                 System.out.println("other types " + node.getId());
            }
        }
        System.out.printf("start %d\n", startNodes.size());
        System.out.printf("finish %d\n", finishNodes.size());
        System.out.printf("corner %d\n", cornerNodes.size());
        System.out.printf("center %d\n", centerNodes.size());
        System.out.printf("other %d\n", otherTypes.size());

        List<List<Node>> finalLattice = null;
        for(Node n : cornerNodes){

            for(Node terminatorNode : finishNodes){
                System.out.printf("path from %d to %d \n", terminatorNode.getId(), n.getId());
                //because this graph is already a tree, we do a inverse dfs to get the paths from start to the corners
                //then we can say that we have identified a stage in the pipeline
                List<Node> stage = shortestPath(terminatorNode, n);

                List<List<Node>> newLattice = matchPipeline(stage);
                if(finalLattice == null)
                    finalLattice = newLattice;
                if(newLattice != null && newLattice.get(0).size() > finalLattice.get(0).size()){
                    finalLattice = newLattice;
                }
            }

        }

        for(List<Node> stage : finalLattice){
            for(Node node : stage){
                node.setAttribute(ATTR_COLOR, "red");

            }
        }

    }
    public List<List<Node>> matchPipeline(List<Node> stage){
        List<List<Node>> pipeline = new ArrayList<>();
        boolean visited[] = new boolean[dataFlowGraph.getMaxNodeId() + 1];
        for(Node node : stage){
            visited[node.getId()] = true;
        }

        pipeline.add(stage);
        List<Node> newStage = null;
        do{
            newStage = new ArrayList<>();
            for(Node node: stage){
                for(Edge edge : node.getIncomingEdges()){
                    if(!visited[edge.getSource().getId()]){
                        visited[edge.getSource().getId()] = true;
                        newStage.add(edge.getSource());
                    }
                }
            }
            stage = newStage;
            pipeline.add(stage);
        }while(newStage.size() != 0);

        for(int i = 0; i < pipeline.size(); i++){
            for(int j = 0; j < pipeline.get(i).size(); j++)
                System.out.printf(" %3d ", pipeline.get(i).get(j).getId());
            System.out.println();
        }
        int maxRowSize = 0;
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
    private List<Node> shortestPath(Node x, Node y){
        boolean[] visited = new boolean[dataFlowGraph.getMaxNodeId() + 1];
        List<Edge> path = new ArrayList<>();
        incomingDFS(x, visited, y, path);
        Collections.reverse(path);
        List<Node> pathNodes = new ArrayList<>();
        pathNodes.add(x);
        for(Edge e : path){
            pathNodes.add(e.getSource());
        }
        return pathNodes;
    }

}
