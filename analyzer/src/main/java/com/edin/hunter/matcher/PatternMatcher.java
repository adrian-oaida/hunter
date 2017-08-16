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
    private BaseRunner run;

    public PatternMatcher(BaseRunner run){
        this.detector = detector;
        this.constrictor = constrictor;
        this.run = run;

        //need to configure the lattice matching heuristic
        //need to configure the aglomeration tackling heursitcs heuristic

    }

    public boolean match(StructuralDetector detector, AgglomerationConstrictor constrictor){

        this.dataFlowGraph = constrictor.getDataFlowGraph();

        try {
            //node 0 is the root node
            if(dataFlowGraph.getNode(0) != null && dataFlowGraph.getNode(0).getInDegree() == 0){
                dataFlowGraph.removeNode(0);
            }
        }catch (NoSuchElementException e){
            throw new MalformedGraphException("The graph contains to root node");
        }

//        constrictor.eliminateAgglomeration();
////        constrictor.eliminateAgglomeration();
////        constrictor.eliminateAgglomeration();
//        detector.containsStructure();

        while(!detector.containsStructure()){
            int oldSCGSize = constrictor.getStaticCallGraph().getNodeCount();
            int oldDCGSize = constrictor.getDynamicCallGraph().getNodeCount();
            int oldDFGSize = constrictor.getDataFlowGraph().getNodeCount();

            constrictor.eliminateAgglomeration();
            if(oldSCGSize == constrictor.getStaticCallGraph().getNodeCount() &&
                    oldDCGSize == constrictor.getDynamicCallGraph().getNodeCount() &&
                    oldDFGSize == constrictor.getDataFlowGraph().getNodeCount()){
                return false;
            }
        }
        return true;

    }
}
