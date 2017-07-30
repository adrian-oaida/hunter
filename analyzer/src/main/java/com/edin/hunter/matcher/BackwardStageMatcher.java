package com.edin.hunter.matcher;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dude on 7/12/17.
 */
public class BackwardStageMatcher extends BaseMatcher {

    public BackwardStageMatcher(DirectedGraph graph) {
        super(graph);

    }

    @Override
    public DirectedGraph detect() {
//        removeSelfEdges();
//        removeDuplicatedEdges();
        mergeSISO();
        markUpGraph();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        matchStage();

        return null;
    }

    private void mergeSISO() {

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
