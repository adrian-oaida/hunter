package com.edin.hunter.matcher;



import com.edin.hunter.runner.Edge;
import com.edin.hunter.runner.Graph;
import com.edin.hunter.runner.Node;

import java.util.*;

/**
 * Created by dude on 6/25/17.
 */
public class Matcher {
    private Graph graph;

    /*
    * @param graph a dataflow graph
    *
    * */
    public Matcher(Graph graph){
        this.graph = graph;
        markUpGraph();
    }
    public Graph getGraph(){


        return graph;
    }
    /*
    *This returns a new graph based on the existent graph but with pipeline stage and latices highlighted
    *
    *
    * @return
    * */
    public Graph detect(){
        removeSelfEdges();
        removeDuplicatedEdges();
        markUpGraph();

        detectLatice();
        detectPipelineStages();

        return graph;
    }
    //TODO lattice highlighter
    //latice definition
    private boolean detectLatice(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Node[][] matrix = new Node[graph.getNodeCount()][graph.getNodeCount()];

        boolean[] visited = new boolean[graph.getMaxNodeId() + 1];

        matrix[0][0] = start;
        if(populateMatrix(matrix, start, 0, 0)){
            System.out.println("Match");
            showMatrix(matrix);
        }else{
            System.out.println("no match");
        }

        return false;
    }

    private void showMatrix(Node[][] matrix){
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                if(matrix[i][j] != null)
                    System.out.printf(" %4d ", matrix[i][j].getId());
                else
                    System.out.print(" null ");
            }
            System.out.println();
        }

    }
    private boolean testMatrix(Node[][] matrix){
        int lineMax = 0;
        int columnMax = 0;
        for(int i = 0; i < matrix.length; i++){
            int currentLineMax = 0;
            for(int j = 0; j < matrix[i].length; j++){
                if(matrix[i][j] != null){
                    currentLineMax++;

                }else{
                    break;
                }
            }
            if(lineMax == 0){
                lineMax = currentLineMax;
            }else{

            }
            int currentColumnMax = 0;
            for(int j = 0; j < matrix[i].length; j++){
                if(matrix[j][i] != null){
                    currentColumnMax++;

                }else{
                    break;
                }
            }
            if(columnMax == 0){
                columnMax = currentColumnMax;
            }else{

            }
        }
        for(int i = 0; i < columnMax; i++){
            for(int j = 0; j < lineMax; j++){
                if(matrix[i][j] == null)
                    return false;
            }
        }
        return true;
    }

    private boolean populateMatrix(Node[][] matrix, Node currentNode, int i, int j){
        showMatrix(matrix);
        if(currentNode == finish){
            return testMatrix(matrix);
        }
        List<Edge> outgoingEdges = currentNode.getOutgoingEdges();
        if(outgoingEdges.size() == 2){
            matrix[i+1][j] = outgoingEdges.get(0).getTarget();
            matrix[i][j + 1] = outgoingEdges.get(1).getTarget();
            if(!populateMatrix(matrix, outgoingEdges.get(0).getTarget(), i + 1, j) && !populateMatrix(matrix, outgoingEdges.get(1).getTarget(), i, j + 1)){
                matrix[i+1][j] = outgoingEdges.get(1).getTarget();
                matrix[i][j + 1] = outgoingEdges.get(0).getTarget();
                if(!populateMatrix(matrix, outgoingEdges.get(1).getTarget(), i + 1, j) && !populateMatrix(matrix, outgoingEdges.get(0).getTarget(), i, j + 1)){
                    matrix[i + 1][j] = null;
                    matrix[i][j + 1] = null;
                    return false;
                }
            }
        }else if(outgoingEdges.size() == 1){
            matrix[i+1][j] = outgoingEdges.get(0).getTarget();
            if(!populateMatrix(matrix, outgoingEdges.get(0).getTarget(), i + 1, j))
            {
                matrix[i + 1][j] = null;
                matrix[i][j + 1] = outgoingEdges.get(0).getTarget();
                if(!populateMatrix(matrix, outgoingEdges.get(0).getTarget(), i, j + 1)){
                    return false;
                }
            }

        }

        return true;
    }
    private boolean detectPipelineStages(){
        //look for the maximum number of subgraphs that have the same structure and link to each other
        //


        return false;
    }

    private Node start;
    private Node finish;

    /*
    * This method modified the graph by removing edges that point to their originating node (source = target)
    * */
    private void removeSelfEdges(){
        for(Node node : graph){
            List<Edge> selfEdges = node.getEdgesTowards(node);
            for(Edge edge : selfEdges){
                node.removeEdge(edge);
            }
        }
    }
    /*
    * <p>
    *     This method modifies the graph by removing duplicated edges leaving.
    * */
    private void removeDuplicatedEdges(){
        for(Node node : graph){
            for(Node neighNode : node.getNeighbouringNodes()){
                List<Edge> edges = node.getEdgesTowards(neighNode);
                if(edges.size() > 1){
                    for(int i = 1 ; i < edges.size(); i++){
                        node.removeEdge(edges.get(i));
                    }
                }
            }
        }

    }
    private void markUpGraph(){
        for(Node n : graph){
            n.setAttribute("color", "blue");
            if(n.getInDegree() == 0){
                start =  n;
                System.out.println("start " + start);
                n.setAttribute("shape", "box");
                n.setAttribute("color", "red");
            }
            if(n.getOutDegree() == 0){
                finish = n;
                System.out.println("finish " + finish);
                n.setAttribute("shape", "box");
                n.setAttribute("color", "black");
            }
            if(! n.getEdgesTowards(n).isEmpty()){
                if(n.getInDegree() - 1 == 0){
                    n.setAttribute("color", "red");
                }
                if(n.getOutDegree() - 1 == 0){
                    n.setAttribute("color", "black");
                }
            }
        }
    }
}
