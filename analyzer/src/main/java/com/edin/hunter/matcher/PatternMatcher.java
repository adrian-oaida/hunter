package com.edin.hunter.matcher;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.matcher.constrictor.AgglomerationConstrictor;
import com.edin.hunter.matcher.detector.StructuralDetector;
import com.edin.hunter.matcher.exceptions.MalformedGraphException;
import com.edin.hunter.runner.BaseRunner;

import java.util.NoSuchElementException;

public class PatternMatcher {
    //class architecture easy enough to be extended to other structural matchers
    //
    private DirectedGraph dataFlowGraph;

    private StructuralDetector detector;
    private AgglomerationConstrictor constrictor;


    public PatternMatcher(){
        this.detector = detector;
        this.constrictor = constrictor;

        //need to configure the lattice matching heuristic
        //need to configure the aglomeration tackling heursitcs heuristic

    }

    public void match(StructuralDetector detector, AgglomerationConstrictor constrictor){

        this.dataFlowGraph = constrictor.getDataFlowGraph();

        try {
            //node 0 is the root node
            if(dataFlowGraph.getNode(0) != null && dataFlowGraph.getNode(0).getInDegree() == 0){
                dataFlowGraph.removeNode(0);
            }
        }catch (NoSuchElementException e){
            throw new MalformedGraphException("The graph contains to root node");
        }

        //
//        if(!detector.containsStructure()){
            constrictor.eliminateAgglomeration();
            detector.containsStructure();

            //this would be the final decision point
//        }

    }
}
