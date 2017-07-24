package com.edin.hunter.matcher;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Node;
import com.edin.hunter.ui.GraphViewer;

/**
 * Created by dude on 7/9/17.
 */
public class LatticeGenerator {
    private int workers;
    private int stages;
    private DirectedGraph graph;
    public LatticeGenerator(int workers, int stages){
        this.workers = workers;
        this.stages = stages;
        this.graph = new DirectedGraph("Lattice " + workers + " X " + stages + "");

    }
    public void generateLattice(){
        int nodeCounter = 1;
        Node[][] latticeStructure = new Node[workers][stages];

        for(int i = 0; i < workers ; i++){

            for(int j = 0; j < stages ; j++){
                latticeStructure[i][j] = graph.getOrAddNode(nodeCounter++);
                if(i > 0){
                    latticeStructure[i - 1][j].addEdgeTo(latticeStructure[i][j]);
                }
                if(j > 0){
                    latticeStructure[i][j - 1].addEdgeTo(latticeStructure[i][j]);
                }
            }
        }


    }
    public void displayLattice(){
        BaseMatcher matcher = new StageMatcher(graph);
        matcher.detect();

        GraphViewer viewer = new GraphViewer(graph);
        viewer.displayGraph();
    }
}
