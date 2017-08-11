package com.edin.hunter.matcher.constrictor;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.runner.BaseRunner;

public class IterativeStructuralConstrictor extends AgglomerationConstrictor{


    public IterativeStructuralConstrictor(BaseRunner runner) {
        super(runner);
    }

    @Override
    public boolean eliminateAgglomeration() {
        return false;
    }
}
