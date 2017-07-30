package com.edin.hunter.matcher;

import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Node;

import java.util.List;

/**
 * Created by dude on 7/12/17.
 */
public class MatrixMatcher extends BaseMatcher {

    public MatrixMatcher(DirectedGraph graph){
        super(graph);
    }

    @Override
    public DirectedGraph detect() {

        removeSelfEdges(dataFlowGraph);
        removeDuplicatedEdges(dataFlowGraph);
        markUpGraph(dataFlowGraph);

        detectLatice();

        return dataFlowGraph;

    }

    private boolean detectLatice(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Node[][] matrix = new Node[dataFlowGraph.getNodeCount()][dataFlowGraph.getNodeCount()];

        boolean[] visited = new boolean[dataFlowGraph.getMaxNodeId() + 1];

        matrix[0][0] = startNodes.get(0);
        if(populateMatrix(matrix, startNodes.get(0), 0, 0)){
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
        if(currentNode == finishNodes.get(0)){
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
}
