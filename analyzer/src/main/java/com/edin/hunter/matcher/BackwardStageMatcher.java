package com.edin.hunter.matcher;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;

import java.util.*;

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
        mergeNeighbourCodeBlocks();
//        mergeLinearSESE();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        matchStage();

        return null;
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
            if(node.getAttribute("staticNodeId") != null){
                Node staticNode = staticCallGraph.getNode(node.getAttribute("staticNodeId"));
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

        int i = 4;
        if(maxNode.getInDegree() == 1){
            //has only 1 parent
            Node parentNode = maxNode.getIncomingEdges().get(0).getSource();
            for(Node dynamicNode : parentNode.getAssociatedNodes()){
                List<Node> nodesToMerge = new ArrayList<>();

                for(Edge outgoingEdge : dynamicNode.getOutgoingEdges()){
                        nodesToMerge.add(dataFlowGraph.getNode(outgoingEdge.getTarget().getId())) ;
                }

                for(Node nodeM : nodesToMerge){
                    nodeM.setAttribute("color", colorArray[i]);
                }
                i++;
            }
        }
        System.out.printf("found %d regions \n", i);

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
                node.setAttribute("color", colorArray[color]);
            }

            if(parent != null && child != null){
                for(Node node: region){
                    dataFlowGraph.removeNode(node);
                }
                Edge edge = parent.addEdgeTo(child);
                edge.setAttribute("color", colorArray[color]);
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
//
//        List<List<Node>> finalLattice = null;
//        for(Node n : cornerNodes){
//
//            for(Node terminatorNode : finishNodes){
//                System.out.printf("path from %d to %d \n", terminatorNode.getId(), n.getId());
//                //because this graph is already a tree, we do a inverse dfs to get the paths from start to the corners
//                //then we can say that we have identified a stage in the pipeline
//                List<Node> stage = shortestPath(terminatorNode, n);
//
//                List<List<Node>> newLattice = matchPipeline(stage);
//                if(finalLattice == null)
//                    finalLattice = newLattice;
//                if(newLattice != null && newLattice.get(0).size() > finalLattice.get(0).size()){
//                    finalLattice = newLattice;
//                }
//            }
//
//        }
//
//        for(List<Node> stage : finalLattice){
//            for(Node node : stage){
//                node.setAttribute("color", "red");
//
//            }
//        }
        //                        edge.setAttribute("color", "red");
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }


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
