package com.edin.hunter.matcher;

import com.edin.hunter.runner.Edge;
import com.edin.hunter.runner.Graph;
import com.edin.hunter.runner.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dude on 7/12/17.
 */
public class StageMatcher extends BasicMatcher{

    public StageMatcher(Graph graph) {
        super(graph);
    }

    private void matchStage(){
        List<Node> cornerNodes = new ArrayList<>();
        for(Node node : graph){

            if(node.getOutDegree() == node.getInDegree()){
                if(node.getOutDegree() == 1){
                    cornerNodes.add(node);
                }else{
                    //System.out.println("center " + node.getId());
                }
            }else{
               // System.out.println("other types " + node.getId());
            }
        }
        for(Node n : cornerNodes){
            System.out.printf("path from %d to %d \n", start.getId(), n.getId());
            //because this graph is already a tree, we do a dfs to get the paths from start to the corners
            //then we can say that we have identified a stage in the pipeline

            List<Node> stage = shortestPath(start, n);
            if(matchPipeline(stage)){
                System.out.println("we got a lattice");
            }

        }
    }
    //then we find the next stage as an identical path from which all of the nodes in the original path have at least one to one vertex
    //then we replace the path and go around again until we find no more
    //then we test the resulting structure, and if it has the shape of a lattice we return true
    private boolean matchPipeline(List<Node> stage){
        boolean visited[] = new boolean[graph.getMaxNodeId() + 1];
        for(Node node : stage){
            visited[node.getId()] = true;
        }
        List<List<Node>> lattice = new ArrayList<>();
        lattice.add(stage);
        List<Node> newStage = null;
        do{
            newStage = new ArrayList<>();
            for(Node node: stage){
                for(Edge edge : node.getOutgoingEdges()){
                    if(!visited[edge.getTarget().getId()]){
                        visited[edge.getTarget().getId()] = true;
                        edge.setAttribute("color", "red");
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        newStage.add(edge.getTarget());
                    }
                }
            }
            stage = newStage;
            lattice.add(stage);
        }while(newStage.size() != 0);

        for(int i = 0; i < lattice.size(); i++){
            for(int j = 0; j < lattice.get(i).size(); j++)
                System.out.printf(" %3d ", lattice.get(i).get(j).getId());
            System.out.println();
        }
        int maxRowSize = 0;
        for(int i = 0; i < lattice.size(); i++){
            if(maxRowSize == 0){
                maxRowSize = lattice.get(i).size();
            }else{
                if(lattice.get(i).size() > maxRowSize){
                    return false;
                }
            }

        }
        return true;
    }
    private boolean dfsUtil(Node node, boolean[] visited, Node y, List<Edge> path){

        visited[node.getId()] = true;


        if(node == y){
            return true;
        }
        for(Edge e : node.getOutgoingEdges()){
            if(!visited[e.getTarget().getId()]){
                if(dfsUtil(e.getTarget(), visited, y, path)){
                    path.add(e);
                    return true;
                }
            }
        }
        return false;
    }
    private List<Node> shortestPath(Node x, Node y){
        List<Edge> path = new ArrayList<>();
        boolean [] visited = new boolean[graph.getMaxNodeId() + 1];

        dfsUtil(x, visited, y, path);
        Collections.reverse(path);
        List<Node> pathNodes = new ArrayList<>();
        pathNodes.add(x);
        for(Edge edge : path){
            pathNodes.add(edge.getTarget());
            edge.setAttribute("color", "red");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(edge);
        }
        return pathNodes;
    }

    @Override
    public Graph detect() {
        removeSelfEdges();
        removeDuplicatedEdges();
        markUpGraph();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        matchStage();
        return null;
    }

}
