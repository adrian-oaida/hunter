package com.edin.hunter.matcher.detector;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Node;
import com.edin.hunter.runner.BaseRunner;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.edin.hunter.graph.DirectedGraph.ATTR_COLOR;
import static com.edin.hunter.graph.DirectedGraph.colorArray;

public class ForestStructuralDetector extends LatticeDetector{

    public ForestStructuralDetector(BaseRunner runner) {
        super(runner);
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

}
